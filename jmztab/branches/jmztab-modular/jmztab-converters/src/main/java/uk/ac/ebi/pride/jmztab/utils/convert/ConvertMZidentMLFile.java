package uk.ac.ebi.pride.jmztab.utils.convert;

import org.apache.log4j.Logger;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.model.mzidml.UserParam;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.model.Modification;
import uk.ac.ebi.pride.jmztab.model.Param;
import uk.ac.ebi.pride.jmztab.utils.convert.utils.MZIdentMLUtils;
import uk.ac.ebi.pride.jmztab.utils.convert.utils.MzIdentMLUnmarshallerAdaptor;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabConversionException;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Class that convert a mzIdentML to a mzTab file using the mzTab library. This Class extends the @ConvertProvider<File, Void>  class
 * and add new methods to handle specifications in the mzidentml files.
 *
 * Created by yperez on 13/06/2014.
 */

public class ConvertMZidentMLFile extends ConvertProvider<File, Void> {

    private static Logger logger = Logger.getLogger(ConvertMZidentMLFile.class);

    private MzIdentMLUnmarshallerAdaptor reader;

    private Metadata metadata;

    private MZTabColumnFactory proteinColumnFactory;
    private MZTabColumnFactory psmColumnFactory;

    private Map<Comparable, Integer> spectraToRun;

    private Map<String, Integer> proteinScoreToScoreIndex;

    private Map<String, Integer> psmScoreToScoreIndex;

    private final static Integer THRESHOLD_LOOP_FOR_SCORE = 100;

    private Map<Param, Set<String>> variableModifications;

    private Set<Comparable> proteinIds;


    public ConvertMZidentMLFile(File source) {
        super(source, null);
    }

    /**
     * Load PRIDE XML file.
     */
    @Override
    protected void init() {
        try {
            this.reader = new MzIdentMLUnmarshallerAdaptor(source,true);
        } catch (ConfigurationException e) {
            throw new MZTabConversionException("Error opening the mzidentml file", e);
        }
    }

    /**
     * Generate {@link uk.ac.ebi.pride.jmztab.model.Metadata}
     */
    @Override
    protected Metadata convertMetadata() {

        this.metadata = new Metadata();

        /**
         * It is a good practice to take the name of the file as the Ids because the writers never use proper IDs
         * in the mzidentml files.
         */
        String mzTabId = getFileNameWithoutExtension(source.getName());
        metadata.setMZTabID(mzTabId);

        /**
         *  Is really common that name of the mzidentml is not provided then we will use Ids if the name is not provided.
         */
        String title = reader.getMzIdentMLName() != null?reader.getMzIdentMLName():reader.getMzIdentMLId();
        metadata.setTitle(title);

        //Description
        loadExperimentParams();

        /**
         * Get sample processing steps or CvTerms associated with it.
         */
        // Todo: We need to think about how to handler the sample processing
        //loadSampleProcessing(reader.getSampleList());

        // process the software
        loadSoftware(reader.getSoftwares(), reader.getProteinDetectionProtocol(), reader.getSpectrumIdentificationProtocol());

        // Get the information of the search engine for metadata from all different objects. The information is in every
        // identification, we assume that is going to be the same per
        // protein and psm (constant number of scores in all the proteins and constant number of score per psm)
        // They can not be added while processing proteins and psm identification because the initialization of the protein/psms
        // need to know in advance all the columns for the factory, they can not grow dynamically inside (the values are
        // not properly shifted)
        loadSearchEngineScores();

        //load modifications
        //loadModifications(reader.getModificationIds());

        // process the references
        loadReferences(reader.getReferences());

        // process the contacts
        loadContacts(reader.getPersonContacts());

        // set Ms Run
        loadMsRun(reader.getSpectraData());

        // process samples
        loadSamples(reader.getSampleList());

        /**
         * MZIdentML always will be an Identification File.
         */
        metadata.setMZTabType(MZTabDescription.Type.Identification);
        metadata.setMZTabMode(MZTabDescription.Mode.Complete);

        //Fragmentation methods
        //Sample processing (protocol steps)
        //MODS
        loadURI(reader.getMzIdentMLId());

        //QUANT METADATA and units
        //CV Info
        //Study vars

        //The description should be added in loadExperiment()
        if (metadata.getDescription() == null || metadata.getDescription().isEmpty()) {
            metadata.setDescription("Descripion not available");
        }

        metadata.addCustom(new uk.ac.ebi.pride.jmztab.model.UserParam("Date of export", new Date().toString()));
        metadata.addCustom(new uk.ac.ebi.pride.jmztab.model.UserParam("Original converted file", source.getAbsolutePath()));

        return metadata;
    }

    /**
     * Generate {@link uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory} which maintain a couple of {@link uk.ac.ebi.pride.jmztab.model.ProteinColumn}
     */
    @Override
    protected MZTabColumnFactory convertProteinColumnFactory() {
        this.proteinColumnFactory = MZTabColumnFactory.getInstance(Section.Protein);

        // ms_run[1] optional columns
        for(MsRun msRun: metadata.getMsRunMap().values()){
            proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun);
            proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun);
            proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun);
        }

        // for quantification file, need provide all optional columns for each ms_run.
        if (!isIdentification()) {
            for (Assay assay : metadata.getAssayMap().values()) {
                proteinColumnFactory.addAbundanceOptionalColumn(assay);
            }
        }

        //TODO check identification and summary
        for (Integer id : metadata.getProteinSearchEngineScoreMap().keySet()) {
            //To be compliance with the specification you need the columns in the psms too
            proteinColumnFactory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, id);
            //proteinColumnFactory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, id, metadata.getMsRunMap().get(1));
        }

        for(MsRun msRun: metadata.getMsRunMap().values())
           for(Integer idScore: metadata.getProteinSearchEngineScoreMap().keySet())
               proteinColumnFactory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, idScore, msRun);

        return proteinColumnFactory;
    }

    /**
     * Generate {@link uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory} which maintain a couple of {@link uk.ac.ebi.pride.jmztab.model.PSMColumn}
     */
    @Override
    protected MZTabColumnFactory convertPSMColumnFactory() {
        this.psmColumnFactory = MZTabColumnFactory.getInstance(Section.PSM);
        psmColumnFactory.addOptionalColumn(MZIdentMLUtils.OPTIONAL_ID_COLUMN,String.class);
        psmColumnFactory.addOptionalColumn(MZIdentMLUtils.OPTIONAL_DECOY_COLUMN, Integer.class);

        //Search engine score information (mandatory for all)
        for (Integer id : metadata.getPsmSearchEngineScoreMap().keySet()) {
            psmColumnFactory.addSearchEngineScoreOptionalColumn(PSMColumn.SEARCH_ENGINE_SCORE, id, null);
        }

        return this.psmColumnFactory;
    }

    /**
     * Fill records into model. This method will be called in {@link #getMZTabFile()} method.
     */
    @Override
    protected void fillData() {
        // Get a list of Identification ids
        proteinIds = new HashSet<Comparable>();
        try{
            if(!reader.hasProteinGroup()){
                List<Comparable> proteinIds = reader.getProteinIds();
                //Iterate over proteins
                for (Comparable id : proteinIds) {
                    Protein identification = getProteinById(id);
                    proteins.add(identification);
                }
            }else{
                List<Comparable> proteinGroupIds = reader.getProteinGroupIds();
                for(Comparable proteinGroupId: proteinGroupIds){
                    Protein identification = getProteinGroupById(proteinGroupId);
                    proteins.add(identification);
                }
            }
            // convert psm
            List<PSM> psmList = loadPSMs(reader.getAllSpectrumIdentificationItem());
            psms.addAll(psmList);

        }catch(ConfigurationException e){
            throw new MZTabConversionException("Error try to retrieve the information for onw Protein");
        }catch(JAXBException e){
            throw new MZTabConversionException("Error try to retrieve the information for onw Protein");

        }


        if(metadata.getFixedModMap().isEmpty()){
            Comment comment = new Comment("Only variable modifications can be reported when the original source is a MZIdentML XML file");
            getMZTabFile().addComment(1, comment);
            metadata.addFixedModParam(1, new CVParam("MS", "MS:1002453", "No fixed modifications searched", null));
        }
        if(metadata.getVariableModMap().isEmpty()){
            metadata.addVariableModParam(1, new CVParam("MS", "MS:1002454", "No variable modifications searched", null));
        }
    }

    private Protein getProteinGroupById(Comparable proteinGroupId) throws JAXBException {
        ProteinAmbiguityGroup proteinAmbiguityGroup = reader.getProteinAmbiguityGroup(proteinGroupId);
        //Todo: We will annotated only the first protein, the core protein.
        ProteinDetectionHypothesis firstProteinDetectionHypothesis = proteinAmbiguityGroup.getProteinDetectionHypothesis().get(0);
        if (proteinIds.contains(firstProteinDetectionHypothesis.getDBSequence().getAccession()))
            throw new MZTabConversionException(MZTabConversionException.ERROR_AMBIGUITY);
        else
            proteinIds.add(firstProteinDetectionHypothesis.getDBSequence().getAccession());

        List<SpectrumIdentificationItem> spectra = getScannedSpectrumIdentificationItems(firstProteinDetectionHypothesis);
        Protein protein = loadProtein(firstProteinDetectionHypothesis.getDBSequence(), spectra);
        String membersString = "";
        for(int i=1; i < proteinAmbiguityGroup.getProteinDetectionHypothesis().size();i++)
            membersString = proteinAmbiguityGroup.getProteinDetectionHypothesis().get(i).getDBSequence().getAccession() + ",";
        membersString = (membersString.isEmpty())?membersString:membersString.substring(0,membersString.length()-1);
        protein.addAmbiguityMembers(membersString);

        //Loop for spectrum to get all the ms_run to repeat the score at protein level
        Set<MsRun> msRuns = new HashSet<MsRun>();
        for(SpectrumIdentificationItem spec: spectra){
            String[] spectumMap = reader.getIdentSpectrumMap().get(spec.getId());
            MsRun msRun = metadata.getMsRunMap().get(spectraToRun.get(spectumMap[1]));
            msRuns.add(msRun);
        }
        // See which protein scores are supported

        for(CvParam cvPAram: firstProteinDetectionHypothesis.getCvParam()){
            if(proteinScoreToScoreIndex.containsKey(cvPAram.getAccession())){
               CVParam param = convertParam(cvPAram);
               int idCount = proteinScoreToScoreIndex.get(cvPAram.getAccession());
               for (MsRun msRun: metadata.getMsRunMap().values()){
                    String value = null;
                    if(msRuns.contains(msRun))
                        value = param.getValue();
                    protein.setSearchEngineScore(idCount,msRun, value);
               }
            }
        }


        return protein;
    }


    /* Metadata */
    private void loadSearchEngineScores(){

        Map<SearchEngineScoreParam, Integer> psmScores = new HashMap<SearchEngineScoreParam, Integer>();
        Map<SearchEngineScoreParam, Integer> proteinScores = new HashMap<SearchEngineScoreParam, Integer>();
        proteinScoreToScoreIndex = new HashMap<String, Integer>();
        psmScoreToScoreIndex = new HashMap<String, Integer>();

       /**
         * Look for all scores are protein level, PSM, and ProteinHypothesis, PeptideHypothesis. We should
         * implement a way to keep track the order of Score in the mzTab related with the rank
         */

        try{
            Set<String> psmIds = reader.getAllSpectrumIdentificationItem();
            Iterator<String> id = psmIds.iterator();
            int i =1;

            //Todo: Define the way to capture the order related with rank

            while(id.hasNext() && i < THRESHOLD_LOOP_FOR_SCORE) {
                String psmId = id.next();
                SpectrumIdentificationItem psm = reader.getSpectrumIdentificationItem(psmId);
                List<SearchEngineScoreParam> psmParams = MZIdentMLUtils.getSearchEngineScoreTerm(psm.getCvParam());
                for(SearchEngineScoreParam scoreCv: psmParams){
                    psmScores.put(scoreCv,i);
                }
                i++;
            }


            Set<String> proteinHypothesisIds = reader.getProteinHypothesisIds();

            id = proteinHypothesisIds.iterator();
            i =0;
            while(id.hasNext() && i < THRESHOLD_LOOP_FOR_SCORE) {
                String proteinId = id.next();
                ProteinDetectionHypothesis protein = reader.getIdentificationById(proteinId);
                List<SearchEngineScoreParam> proteinParams = MZIdentMLUtils.getSearchEngineScoreTerm(protein.getCvParam());
                for(SearchEngineScoreParam scoreCv: proteinParams){
                    proteinScores.put(scoreCv,i);
                }
                i++;
            }


            for(SearchEngineScoreParam param: psmScores.keySet()){
                int idCount = metadata.getPsmSearchEngineScoreMap().size() + 1;
                metadata.addPsmSearchEngineScoreParam(idCount,param.getParam(null));
                psmScoreToScoreIndex.put(param.getParam(null).getAccession(),idCount);
            }
            for(SearchEngineScoreParam param: proteinScores.keySet()){
                int idCount = metadata.getProteinSearchEngineScoreMap().size() + 1;
                metadata.addProteinSearchEngineScoreParam(idCount, param.getParam(null));
                proteinScoreToScoreIndex.put(param.getParam(null).getAccession(),idCount);
            }
        }catch(ConfigurationException ex){
            ex.printStackTrace();
        }catch (JAXBException ex){
            ex.printStackTrace();
        }

        if (metadata.getProteinSearchEngineScoreMap().isEmpty()) {
            metadata.addProteinSearchEngineScoreParam(1, SearchEngineScoreParam.MS_SEARCH_ENGINE_SPECIFIC_SCORE.getParam(null));
        }
        if (metadata.getPsmSearchEngineScoreMap().isEmpty()) {
            metadata.addPsmSearchEngineScoreParam(1, SearchEngineScoreParam.MS_SEARCH_ENGINE_SPECIFIC_SCORE.getParam(null));
        }

    }

    private String getFileNameWithoutExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        return fileName.substring(0, lastIndexOfDot);
    }

    /**
     * Load software and settings for each software. We annotated the software information with the same CvParams from the original file.
     */
    private void loadSoftware(List<AnalysisSoftware> softwareList, ProteinDetectionProtocol proteinDetectionProtocol,
                              List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList) {


        if(!softwareList.isEmpty()){

            for(int i = 0; i < softwareList.size(); i++){
                CvParam nameCVparam = softwareList.get(i).getSoftwareName().getCvParam();
                if(nameCVparam!=null){

                    String version = (softwareList.get(i).getVersion() != null && !softwareList.get(i).getVersion().isEmpty())? softwareList.get(i).getVersion():"";
                    CVParam nameCV = new CVParam(nameCVparam.getCvRef(),nameCVparam.getAccession(),nameCVparam.getName(),version);
                     metadata.addSoftwareParam(i+1, nameCV);
                     if(proteinDetectionProtocol != null && proteinDetectionProtocol.getAnalysisSoftware() != null &&
                             proteinDetectionProtocol.getAnalysisSoftware().getId().equals(softwareList.get(i).getId())){
                         if(proteinDetectionProtocol.getThreshold() != null){
                            loadCvParamSettings(i+1, proteinDetectionProtocol.getThreshold());

                            //Add FDR at Protein level if is annotated
                            for(CvParam cvParam: proteinDetectionProtocol.getThreshold().getCvParam())
                              if(Arrays.asList(MZIdentMLUtils.CVTERMS_FDR_PROTEIN).contains(cvParam.getAccession()))
                                  metadata.addFalseDiscoveryRateParam(convertParam(cvParam));
                         }
                         if(proteinDetectionProtocol.getAnalysisParams() != null){
                             loadCvParamSettings(i+1, proteinDetectionProtocol.getAnalysisParams());
                         }
                    }

                    for(SpectrumIdentificationProtocol spectrumIdentificationProtocol: spectrumIdentificationProtocolList){
                        if(spectrumIdentificationProtocol.getAnalysisSoftware().getId().equals(softwareList.get(i).getId())){
                            if(spectrumIdentificationProtocol.getThreshold() != null){
                                loadCvParamSettings(i+1, spectrumIdentificationProtocol.getThreshold());
                                //Add FDR at PSM level if is annotated
                                for(CvParam cvParam: spectrumIdentificationProtocol.getThreshold().getCvParam())
                                    if(Arrays.asList(MZIdentMLUtils.CVTERMS_FDR_PSM).contains(cvParam.getAccession()))
                                        metadata.addFalseDiscoveryRateParam(convertParam(cvParam));
                            }
                            if(spectrumIdentificationProtocol.getAdditionalSearchParams() != null){
                                loadCvParamSettings(i+1, spectrumIdentificationProtocol.getAdditionalSearchParams());
                            }
                            if(spectrumIdentificationProtocol.getFragmentTolerance() != null){
                                loadCvParamListSettings(i + 1, spectrumIdentificationProtocol.getFragmentTolerance().getCvParam());
                            }
                            if(spectrumIdentificationProtocol.getParentTolerance() != null){
                                loadCvParamListSettings(i+1, spectrumIdentificationProtocol.getParentTolerance().getCvParam());
                            }
                            //Todo: See if we need to capture other objects from fragmentation table, etc.
                        }
                    }

                }
            }

        }
    }

    /**
     * Load in metadata all possible settings.
     * @param order order of the CvParam
     * @param paramList Param List
     */
    private void loadCvParamSettings(int order, ParamList paramList){

        loadCvParamListSettings(order, paramList.getCvParam());

        for(UserParam userParam: paramList.getUserParam()){
            metadata.addSoftwareSetting(order, userParam.getName() + " = " + userParam.getValue());
        }
    }

    /**
     * Isert in metadata only the CvTerm List Settings in an specific order
     * @param order order of the Param
     * @param paramList Param List
     */
    private void loadCvParamListSettings(int order, List<CvParam> paramList){
        for (CvParam cvParam: paramList){
            metadata.addSoftwareSetting(order, cvParam.getName() + " = " + cvParam.getValue());
        }
    }



    /**
     * Converts the experiment's references into a couple of {@link uk.ac.ebi.pride.jmztab.model.PublicationItem} (including DOIs and PubMed ids)
     */
    private void loadReferences(Iterator<BibliographicReference> reference) {
        List<PublicationItem> items = new ArrayList<PublicationItem>();
        int i = 1;
        while(reference.hasNext()){
            BibliographicReference ref = reference.next();
            String doi = ref.getDoi();
            if(doi != null && !doi.isEmpty()){
                items.add(new PublicationItem(PublicationItem.Type.DOI, doi));
                metadata.addPublicationItems(i, items);
                i++;
            }
        }
    }

    /**
     * Converts a list of PRIDE JAXB Contacts into an ArrayList of mzTab Contacts.
     */
    private void loadContacts(List<Person> contactList) {
        // make sure there are contacts to be processed
        if (contactList == null || contactList.size() == 0) {
            return;
        }

        // initialize the return variable
        int id = 1;
        for (Person c : contactList) {
            String name = (c.getName()!=null)?c.getName():c.getId();
            name = (c.getLastName() != null)? name + " " + c.getLastName():name;
            if(!name.isEmpty()){
                metadata.addContactName(id, name);
                String affiliation = "";
                if(c.getAffiliation().get(0) != null && c.getAffiliation().get(0).getOrganization() != null){
                    if(c.getAffiliation().get(0).getOrganization().getName() != null)
                        affiliation = c.getAffiliation().get(0).getOrganization().getName();
                    else
                        affiliation = c.getAffiliation().get(0).getOrganization().getId();
                }
                metadata.addContactAffiliation(id, affiliation);
                String mail  = getMailFromCvParam(c);
                if (!mail.isEmpty()) {
                    metadata.addContactEmail(id, mail);
                }
                id++;
            }
        }
    }

    private String getMailFromCvParam(Person person){
        String mail = "";
        for(CvParam cv: person.getCvParam()){
            if(cv.getAccession().equals(MZIdentMLUtils.CVTERM_MAIL) || cv.getValue().contains("@") ){
                mail = cv.getValue();
            }
        }
        if(mail.isEmpty()){
            for(UserParam cv: person.getUserParam()){
                if(cv.getUnitAccession().equals(MZIdentMLUtils.CVTERM_MAIL) || cv.getValue().contains("@") ){
                    mail = cv.getValue();
                }
            }
        }
        return mail;
    }

    /**
     * Processes the experiment additional params: (f.e. quant method, description...).
     */
    private void loadExperimentParams() {
        String description = "";
        description = description + ("Spectrum Identification Protocol: ");
        List<SpectrumIdentificationProtocol> psmProtocols = reader.getSpectrumIdentificationProtocol();
        for(SpectrumIdentificationProtocol protocol: psmProtocols){
            Enzymes enzymes = protocol.getEnzymes();
            if(enzymes!= null && !enzymes.getEnzyme().isEmpty()){
                description = description + ("Enzymes - ");
                for(Enzyme enzyme: enzymes.getEnzyme()){
                    String name = "";
                    if(enzyme.getEnzymeName() != null && enzyme.getEnzymeName().getCvParam().size() != 0){
                        name = enzyme.getEnzymeName().getCvParam().get(0).getName();
                    }else if(enzyme.getEnzymeName() != null && enzyme.getEnzymeName().getUserParam().size() != 0){
                        name = (enzyme.getEnzymeName().getUserParam().get(0).getValue() != null)?enzyme.getEnzymeName().getUserParam().get(0).getValue():enzyme.getEnzymeName().getUserParam().get(0).getName();
                    }
                    //String name = (enzyme.getEnzymeName() != null && enzyme.getEnzymeName().getCvParam() != null && !enzyme.getEnzymeName().getCvParam().isEmpty())?enzyme.getEnzymeName().getCvParam().get(0).toString():"";
                    description = (!name.isEmpty())?description + name + " ":description;
                }
                description = description.substring(0,description.length()-1);
            }
            if(protocol.getDatabaseFilters() != null){
                description = description + ("; Database Filters - ");
                List<Filter> filters = protocol.getDatabaseFilters().getFilter();
                for(Filter filter: filters){
                    String name = (filter.getFilterType().getCvParam() != null)?filter.getFilterType().getCvParam().getName():"";
                    description = (!name.isEmpty())?description + name + " ":description;
                }
                description = description.substring(0,description.length()-1);
            }
        }
        metadata.setDescription(description);

    }



    private void loadURI(String expAccession) {
        if (expAccession == null || expAccession.isEmpty()) {
            return;
        }
        expAccession = expAccession.replaceAll("\\s+","-");
        try {
            URI uri = new URI("http://www.ebi.ac.uk/pride/archive/assays/" + expAccession);
            metadata.addUri(uri);
        } catch (URISyntaxException e) {
            throw new MZTabConversionException("Error while building URI at the metadata section", e);
        }
    }

    private void loadMsRun(List<SpectraData> spectraDataList) {
        spectraToRun = new HashMap<Comparable, Integer>(spectraDataList.size());
        if(spectraDataList != null && !spectraDataList.isEmpty()){
            int idRun = 1;
            for(SpectraData spectradata: spectraDataList){
                if(spectradata.getFileFormat() != null && spectradata.getFileFormat().getCvParam() != null)
                   metadata.addMsRunFormat(idRun, convertParam(spectradata.getFileFormat().getCvParam()));
                if(spectradata.getSpectrumIDFormat() != null && spectradata.getSpectrumIDFormat().getCvParam() != null)
                   metadata.addMsRunIdFormat(idRun, convertParam(spectradata.getSpectrumIDFormat().getCvParam()));

                String location = (spectradata.getLocation() != null && !spectradata.getLocation().isEmpty())?spectradata.getLocation():spectradata.getName();
                if(location != null && !location.isEmpty() && !location.contains("file:")) location = "file:"+location;
                if(location == null) location="";
                try{
                    metadata.addMsRunLocation(idRun, new URL(location));
                }catch (MalformedURLException e){
                    throw new MZTabConversionException("Error while adding ms run location", e);
                }
                spectraToRun.put(spectradata.getId(), idRun);
                idRun++;
            }

        }
    }

    /**
     * Adds the sample parameters (species, tissue, cell type, disease) to the unit and the various sub-samples.
     */
    private void loadSamples(List<uk.ac.ebi.jmzidml.model.mzidml.Sample> sampleList) {

        if(sampleList != null && !sampleList.isEmpty()){
            // Identification
            int idSample = 1;

            for(uk.ac.ebi.jmzidml.model.mzidml.Sample sample: sampleList){
                int specieId = 1;
                int tissueId = 1;
                int cellTypeId = 1;
                int diseaseId = 1;
                for (CvParam cv: sample.getCvParam()){

                    if ("NEWT".equals(cv.getCvRef())) {
                        metadata.addSampleSpecies(specieId, convertParam(cv));
                        specieId++;
                    } else if ("BTO".equals(cv.getCvRef())) {
                        metadata.addSampleTissue(tissueId, convertParam(cv));
                        tissueId++;
                    } else if ("CL".equals(cv.getCvRef())) {
                        metadata.addSampleCellType(cellTypeId, convertParam(cv));
                        cellTypeId++;
                    } else if ("DOID".equals(cv.getCvRef()) || "IDO".equals(cv.getCvRef())) {
                        metadata.addSampleDisease(diseaseId, convertParam(cv));
                        diseaseId++;
                    }
                }
                idSample++;

                //metadata.addSampleDescription(idSample, sample.getName());
            }
        }
    }

    /**
     * Only one sample means this file is Identification and Complete. Otherwise, the file is Quantification and Summary.
     */
    private boolean isIdentification() {
        return metadata.getSampleMap().size() == 1;
    }



    /**
     * Converts the passed Identification object into an MzTab PSM.
     */
    private List<PSM> loadPSMs(Set<String> oldpsmList) throws JAXBException {

        Map<Comparable, Integer> indexSpectrumID = new HashMap<Comparable, Integer>();
        List<PSM> psmList = new ArrayList<PSM>();
        variableModifications = new HashMap<Param, Set<String>>();

        for (String oldPsmId : oldpsmList) {
            SpectrumIdentificationItem oldPSM = reader.getSpectrumIdentificationItem(oldPsmId);
            for(PeptideEvidenceRef peptideEvidenceRef: oldPSM.getPeptideEvidenceRef()){
                PSM psm = new PSM(psmColumnFactory, metadata);
                psm.setSequence(oldPSM.getPeptide().getPeptideSequence());
                psm.setPSM_ID(oldPSM.getId());
                psm.setAccession(peptideEvidenceRef.getPeptideEvidence().getDBSequence().getAccession());
                psm.setDatabase(getDatabaseName(peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getDatabaseName().getCvParam(),peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getDatabaseName().getUserParam()));
                String version = (peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getVersion() != null && !peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getVersion().isEmpty())?peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getVersion():null;
                psm.setDatabaseVersion(version);

                psm.setStart(peptideEvidenceRef.getPeptideEvidence().getStart());
                psm.setEnd(peptideEvidenceRef.getPeptideEvidence().getEnd());
                psm.setPre(peptideEvidenceRef.getPeptideEvidence().getPre());
                psm.setPost(peptideEvidenceRef.getPeptideEvidence().getPost());


                List<Modification> mods = new ArrayList<Modification>();
                for(uk.ac.ebi.jmzidml.model.mzidml.Modification oldMod: oldPSM.getPeptide().getModification()){
                    Modification mod = MZTabUtils.parseModification(Section.PSM, oldMod.getCvParam().get(0).getAccession());
                    if(mod != null){
                        mod.addPosition(oldMod.getLocation(), null);
                        mods.add(mod);
                        String site = null;
                        if(oldMod.getLocation()-1 < 0)
                            site = "N-Term";
                        else if(peptideEvidenceRef.getPeptideEvidence().getPeptide().getPeptideSequence().length() <= oldMod.getLocation() -1)
                            site = "C-Term";
                        else
                          site = String.valueOf(peptideEvidenceRef.getPeptideEvidence().getPeptide().getPeptideSequence().charAt(oldMod.getLocation()-1));
                        Param param = convertParam(oldMod.getCvParam().get(0));
                        if(!variableModifications.containsKey(param) || !variableModifications.get(param).contains(site)){
                            Set<String> sites = new HashSet<String>();
                            sites = (variableModifications.containsKey(param.getAccession()))?variableModifications.get(param.getAccession()):sites;
                            sites.add(site);
                            variableModifications.put(param, sites);
                        }
                    }else{
                        logger.warn("Your mzidentml contains an UNKNOWN modification which is not supported by mzTab format");
                    }
                    for(CvParam param: oldMod.getCvParam()) {
                        if(param.getAccession().equalsIgnoreCase(MZIdentMLUtils.CVTERM_NEUTRAL_LOST)){
                            CVParam lost = convertParam(param);
                            Modification modNeutral = new Modification(Section.PSM,Modification.Type.NEUTRAL_LOSS, lost.getAccession());
                            modNeutral.setNeutralLoss(lost);
                            modNeutral.addPosition(oldMod.getLocation(), null);
                            mods.add(modNeutral);
                            //mod.setNeutralLoss(lost);
                        }
                    }
                }

                for(Modification mod: mods) psm.addModification(mod);

                psm.setExpMassToCharge(oldPSM.getExperimentalMassToCharge());
                psm.setCharge(oldPSM.getChargeState());
                psm.setCalcMassToCharge(oldPSM.getCalculatedMassToCharge());

                String[] spectumMap = reader.getIdentSpectrumMap().get(oldPsmId);

                String spectrumReference = spectumMap[0];
                if(spectumMap[1] != null && spectrumReference != null)
                    psm.addSpectraRef(new SpectraRef(metadata.getMsRunMap().get(spectraToRun.get(spectumMap[1])), spectrumReference));
                psm.setStart(peptideEvidenceRef.getPeptideEvidence().getStart());
                psm.setEnd(peptideEvidenceRef.getPeptideEvidence().getEnd());

                // See which psm scores are supported
                for(CvParam cvPAram: oldPSM.getCvParam()){
                    if(psmScoreToScoreIndex.containsKey(cvPAram.getAccession())){
                        CVParam param = convertParam(cvPAram);
                        int idCount = psmScoreToScoreIndex.get(cvPAram.getAccession());
                        psm.setSearchEngineScore(idCount, param.getValue());
                    }
                }
                //loadModifications(psm,peptideEvidenceRef.getPeptideEvidence());
                if(!indexSpectrumID.containsKey(oldPSM.getId())){
                    int index = indexSpectrumID.size()+1;
                    indexSpectrumID.put(oldPSM.getId(), index);
                }

                //Set Search Engine
                Set<SearchEngineParam> searchEngines = new HashSet<SearchEngineParam>();
                List<SearchEngineParam> searchEngineParams = MZIdentMLUtils.getSearchEngineTypes(oldPSM.getCvParam());
                searchEngines.addAll(searchEngineParams);

                for(SearchEngineParam searchEngineParam: searchEngines)
                    psm.addSearchEngineParam(searchEngineParam.getParam());

                //Set optional parameter

                psm.setPSM_ID(indexSpectrumID.get(oldPSM.getId()));
                psm.setOptionColumnValue(MZIdentMLUtils.OPTIONAL_ID_COLUMN, oldPSM.getId());
                Boolean decoy = peptideEvidenceRef.getPeptideEvidence().isIsDecoy();
                psm.setOptionColumnValue(MZIdentMLUtils.OPTIONAL_DECOY_COLUMN, (!decoy)?0:1);
                psmList.add(psm);
            }
        }

        //Load the modifications in case some of modifications are not reported in the SpectrumIdentificationProtocol
        int varId = 1;
        for(Param param: variableModifications.keySet()){
            String siteString = "";
            for(String site: variableModifications.get(param)){
                siteString=siteString+" "+ site;
            }
            siteString = siteString.trim();
            metadata.addVariableModParam(varId, param);
            metadata.addVariableModSite(varId, siteString);
            varId++;
        }
        return psmList;
    }


    /* Utils */

    private CVParam convertParam(CvParam param) {
        return new CVParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue());
    }

    private Protein getProteinById(Comparable proteinId) throws JAXBException {
        DBSequence dbSequence = reader.getDBSequenceById(proteinId);
        List<SpectrumIdentificationItem> spectrumIdentificationItems = getScannedSpectrumIdentificationItems(proteinId);
        return loadProtein(dbSequence, spectrumIdentificationItems);
    }

    private Protein loadProtein(DBSequence sequence, List<SpectrumIdentificationItem> spectrumItems){
        // create the protein object
        Protein protein = new Protein(proteinColumnFactory);
        protein.setAccession(sequence.getAccession());
        protein.setDatabase(getDatabaseName(sequence.getSearchDatabase().getDatabaseName().getCvParam(), sequence.getSearchDatabase().getDatabaseName().getUserParam()));
        String version = (sequence.getSearchDatabase().getVersion() != null && !sequence.getSearchDatabase().getVersion().isEmpty())?sequence.getSearchDatabase().getVersion():null;
        protein.setDatabaseVersion(version);

        // set the description if available
        String description = (getDescriptionFromCVParams(sequence.getCvParam()) != null && !getDescriptionFromCVParams(sequence.getCvParam()).isEmpty())?getDescriptionFromCVParams(sequence.getCvParam()):null;
        protein.setDescription(description);



        //Todo: MzIdentml the samples are completely disconnected from proteins and peptides.
        // set protein species and taxid. We are not sure about the origin of the protein. So we keep this value as
        // null to avoid discrepancies

        Map<Integer, Integer> totalPSM = new HashMap<Integer, Integer>();
        Set<Integer> msRunforProtein = new HashSet<Integer>();

        for(SpectrumIdentificationItem specItem: spectrumItems){
            String ref = reader.getIdentSpectrumMap().get(specItem.getId())[1];
            if(spectraToRun.containsKey(ref)){
                Integer value = 1;
                if(totalPSM.containsKey(spectraToRun.get(ref))){
                     value = totalPSM.get(spectraToRun.get(ref)) + 1;
                }
                msRunforProtein.add(spectraToRun.get(ref));
                totalPSM.put(spectraToRun.get(ref), value);
            }
        }

        //Scores for Proteins

        for(Integer msRunId: totalPSM.keySet())
            protein.setNumPSMs(metadata.getMsRunMap().get(msRunId), totalPSM.get(msRunId));

        //Set Search Engine
        Set<SearchEngineParam> searchEngines = new HashSet<SearchEngineParam>();
        for(int i=0; i < THRESHOLD_LOOP_FOR_SCORE && i < spectrumItems.size(); i++){
            List<SearchEngineParam> searchEngineParams = MZIdentMLUtils.getSearchEngineTypes(spectrumItems.get(i).getCvParam());
            searchEngines.addAll(searchEngineParams);
        }
        for(SearchEngineParam searchEngineParam: searchEngines)
                protein.addSearchEngineParam(searchEngineParam.getParam());

        // set the modifications
        // is not necessary check by ambiguous modifications because are not supported in PRIDEXML
        // the actualization of the metadata with fixed and variable modifications is done in the peptide section
        loadModifications(protein, spectrumItems);

        if(sequence.getSeq() != null && !sequence.getSeq().isEmpty())
            protein.setOptionColumnValue(MZIdentMLUtils.OPTIONAL_SEQUENCE_COLUMN, sequence.getSeq());


        return protein;

    }

    private void loadModifications(PSM psm, PeptideEvidence peptideEvidence) {

        //TODO simplify
        Set<Modification> modifications = new TreeSet<Modification>(new Comparator<Modification>() {
            @Override
            public int compare(Modification o1, Modification o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });

        for (uk.ac.ebi.jmzidml.model.mzidml.Modification ptm : peptideEvidence.getPeptide().getModification()) {
                // ignore modifications that can't be processed correctly (can not be mapped to the protein)

            if (ptm.getCvParam().get(0).getAccession() == null) {
                continue;
            }

                // mod without position
            Modification mod = MZTabUtils.parseModification(Section.PSM, ptm.getCvParam().get(0).getAccession());

            if (mod != null) {
            // only biological significant modifications are propagated to the protein
                   if (peptideEvidence.getStart() != null && ptm.getLocation() != null) {
                       Integer position = peptideEvidence.getStart() + ptm.getLocation();
                       mod.addPosition(position, null);
                   }
                      //if position is not set null is reported

                modifications.add(mod);
            }
        }

        //We add to the protein not duplicated modifications
        for (Modification modification : modifications) {
            psm.addModification(modification);
        }
    }

    private void loadModifications(Protein protein, List<SpectrumIdentificationItem> items) {

        //TODO simplify
        Set<Modification> modifications = new TreeSet<Modification>(new Comparator<Modification>() {
            @Override
            public int compare(Modification o1, Modification o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });

        for (SpectrumIdentificationItem item : items) {
            PeptideEvidence peptideEvidence = null;
            for(PeptideEvidenceRef peptideEvidenceRef: item.getPeptideEvidenceRef()){
                if(peptideEvidenceRef.getPeptideEvidence().getDBSequence().getAccession().equalsIgnoreCase(protein.getAccession())){
                    peptideEvidence = peptideEvidenceRef.getPeptideEvidence();
                    break;
                }
            }

            for (uk.ac.ebi.jmzidml.model.mzidml.Modification ptm : item.getPeptide().getModification()) {
                // ignore modifications that can't be processed correctly (can not be mapped to the protein)
                if (ptm.getCvParam().get(0).getAccession() == null) {
                    continue;
                }

                // mod without position
                Modification mod = MZTabUtils.parseModification(Section.Protein, ptm.getCvParam().get(0).getAccession());

                if (mod != null) {

                    // only biological significant modifications are propagated to the protein
                    if (ModParam.isBiological(ptm.getCvParam().get(0).getAccession())) {
                        // if we can calculate the position, we add it to the modification
                        if (peptideEvidence != null && peptideEvidence.getStart() != null && ptm.getLocation() != null) {
                            Integer position = peptideEvidence.getStart() + ptm.getLocation();
                            mod.addPosition(position, null);

                        }
                        //if position is not set null is reported
                        modifications.add(mod);

                        // the metadata is updated in the PSM section because the protein modifications are a subset of
                        // the psm modifications
                    }
                }
            }
        }
        //We add to the protein not duplicated modifications
        for (Modification modification : modifications) {
            protein.addModification(modification);
        }
    }

    private String getDescriptionFromCVParams(List<CvParam> cvParams) {
        for(CvParam cvparam: cvParams){
            if(cvparam.getAccession().equalsIgnoreCase(MZIdentMLUtils.CVTERM_PROTEIN_DESCRIPTION)){
                return cvparam.getValue();
            }
        }
        return null;
    }

    private String getDatabaseName(CvParam databaseName, UserParam userParam){
        if(databaseName != null )
          return (databaseName.getValue()!=null)?databaseName.getValue():databaseName.getName();
        else if(userParam != null){
          return (userParam.getValue()!=null)?userParam.getValue():userParam.getName();
        }
        return null;
    }



   private List<SpectrumIdentificationItem> getScannedSpectrumIdentificationItems(Comparable proteinId) throws JAXBException {
        List<Comparable> spectrumIdentIds = reader.getIdentProteinsMap().get(proteinId);

        return reader.getSpectrumIdentificationsByIds(spectrumIdentIds);
   }

    private List<SpectrumIdentificationItem> getScannedSpectrumIdentificationItems(ProteinDetectionHypothesis proteinDetectionHypothesis){
        List<SpectrumIdentificationItem> spectrumIdentIds = new ArrayList<SpectrumIdentificationItem>();
        List<PeptideHypothesis> peptideHypothesises = proteinDetectionHypothesis.getPeptideHypothesis();
        for(PeptideHypothesis peptideHypothesis: peptideHypothesises){
          List<SpectrumIdentificationItemRef> specRefs = peptideHypothesis.getSpectrumIdentificationItemRef();
          for(SpectrumIdentificationItemRef spectrumIdentificationItemRef: specRefs)
              spectrumIdentIds.add(spectrumIdentificationItemRef.getSpectrumIdentificationItem());
        }
        return spectrumIdentIds;
    }

}
