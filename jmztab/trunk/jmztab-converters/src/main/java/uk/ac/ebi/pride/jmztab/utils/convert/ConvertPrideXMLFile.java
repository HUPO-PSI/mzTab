package uk.ac.ebi.pride.jmztab.utils.convert;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPeptideIon;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.model.Param;
import uk.ac.ebi.pride.jmztab.model.UserParam;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabConversionException;
import uk.ac.ebi.pride.mol.PTModification;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;

/**
 * Convert PRIDE XML v2.1 file to mzTab.
 * <p/>
 * @author qingwei
 * @author ntoro
 * @since 07/06/13
 */
public class ConvertPrideXMLFile extends ConvertProvider<File, Void> {

    private static final String DUP_PROTEINS_SEARCH_ENGINES = "duplicated_proteins_search_engines";
    private static final String DUP_PROTEINS_SEARCH_ENGINES_SCORES = "duplicated_proteins_search_engines_scores";
    private static final String DUP_PROTEINS_BEST_SEARCH_ENGINES_SCORE = "duplicated_proteins_best_search_engines_score";
    private static final String DUP_PROTEINS_HAD_QUANT = "duplicated_proteins_had_quantification";
    private static final String NUM_MERGE_PROTEINS = "num_merge_proteins";


    private static Logger logger = Logger.getLogger(ConvertPrideXMLFile.class);

    private PrideXmlReader reader;

    private int alternativeId = 0;
    private boolean gelExperiment = false;

    private Metadata metadata;
    private MZTabColumnFactory proteinColumnFactory;
    private MZTabColumnFactory psmColumnFactory;

    private SortedMap<String, List<Protein>> accessionProteinMap = new TreeMap<String, List<Protein>>();



    public ConvertPrideXMLFile(File source) {
        super(source, null);
    }

    /**
     * Load PRIDE XML file.
     */
    @Override
    protected void init() {
        this.reader = new PrideXmlReader(source);
    }

    /**
     * Generate {@link Metadata}
     */
    @Override
    protected Metadata convertMetadata() {
        this.metadata = new Metadata();

        String mzTabId = getFileNameWithoutExtension(source.getName());
        metadata.setMZTabID(mzTabId);
        metadata.setTitle(reader.getExpTitle());

        // process the software
        loadSoftware(reader.getDataProcessing().getSoftware());
        // process the references
        loadReferences(reader.getReferences());
        // process the contacts
        loadContacts(reader.getAdmin().getContact());
        // process the experiment params
        loadExperimentParams(reader.getAdditionalParams());
        //process the sample processing information
        loadSampleProcessing(reader.getProtocol());
        // process the instrument information
        loadInstrument(reader.getInstrument());

        // process search engines. The information is in every identification, we assume that is going to be the same per
        // protein and psm (constant number of scores in all the proteins and constant number of score per psm)
        // They can not be added while processing proteins and psm identification because the initialization of the protein/psms
        // need to know in advance all the columns for the factory, they can not grow dynamically inside (the values are
        // not properly shifted)
        loadSearchEngineScores(reader.getIdentIds());

        // set Ms Run
        loadMsRun();

        // process samples
        loadSamples(reader.getAdmin().getSampleDescription(), reader.getAdmin().getSampleName());

        // set mzTab- description
        if (isIdentification()) {
            metadata.setMZTabType(MZTabDescription.Type.Identification);
            metadata.setMZTabMode(MZTabDescription.Mode.Complete);
        } else {
            metadata.setMZTabType(MZTabDescription.Type.Quantification);
            metadata.setMZTabMode(MZTabDescription.Mode.Summary);
            logger.debug("Converting quantification file from PRIDE XML.");
        }

        loadURI(reader.getExpAccession());

        //The description should be added in loadExperiment()
        //TODO: Move to the right place, it is a default checking (ConverterProvider)
        if (metadata.getDescription() == null || metadata.getDescription().isEmpty()) {
            metadata.setDescription("Description not available");
        }

        //This will generate the columns for the gels in the protein columns factory
        if(reader.getTwoDimIdentIds()!= null && reader.getTwoDimIdentIds().size() > 0){
            gelExperiment = true;
        }

        metadata.addCustom(new UserParam("Date of export", new Date().toString()));
        metadata.addCustom(new UserParam("Original converted file", source.toURI().toString()));

        return metadata;
    }

    /**
     * Generate {@link MZTabColumnFactory} which maintain a couple of {@link ProteinColumn}
     */
    @Override
    protected MZTabColumnFactory convertProteinColumnFactory() {
        this.proteinColumnFactory = MZTabColumnFactory.getInstance(Section.Protein);

        // If not provide protein_quantification_unit in metadata, default value is Ratio
        if (!isIdentification() && metadata.getProteinQuantificationUnit() == null) {
            metadata.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        }

        // ms_run[1] optional columns
        proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PSMS, metadata.getMsRunMap().get(1));
        proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, metadata.getMsRunMap().get(1));
        proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, metadata.getMsRunMap().get(1));
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
            proteinColumnFactory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, id, metadata.getMsRunMap().get(1));
        }

        if(gelExperiment) {
            proteinColumnFactory.addOptionalColumn("gel_spotidentifier", String.class);
            logger.debug("Optional column gel_spotidentifier added;");

            proteinColumnFactory.addOptionalColumn("gel_identifier", String.class);
            logger.debug("Optional column gel_identifier added;");
        }

        return proteinColumnFactory;
    }

    /**
     * Generate {@link MZTabColumnFactory} which maintain a couple of {@link PSMColumn}
     */
    @Override
    protected MZTabColumnFactory convertPSMColumnFactory() {
        this.psmColumnFactory = MZTabColumnFactory.getInstance(Section.PSM);

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
        List<String> ids = reader.getIdentIds();

        if (!ids.isEmpty()) {
            // Iterate over each identification
            for (String id : ids) {
                Identification identification = reader.getIdentById(id);

                Protein protein = loadProtein(identification);
                if (protein != null) {
                    // we create a check for duplicated proteins ids.
                    // If the protein is not null, it represents that have been
                    // already added and we need to merge the information with the original
                    List<Protein> proteinList = accessionProteinMap.get(protein.getAccession());
                    if (proteinList == null) {
                        proteinList = new ArrayList<Protein>();
                    }
                    proteinList.add(protein);
                    accessionProteinMap.put(protein.getAccession(), proteinList);

                }
                // convert psm
                // they don't have problems with duplications
                List<PSM> psmList = loadPSMs(identification);
                psms.addAll(psmList);
            }

            if (!accessionProteinMap.isEmpty()) {
                for (List<Protein> proteinList : accessionProteinMap.values()) {
                        Protein protein = merge(proteinList);
                        proteins.add(protein);
                }
            }

        } else {
            logger.warn("There is not identification information in the file.");
        }

        //This check can not be move to the metadata section
        //TODO: Move to the right place, it is a default checking (ConverterProvider)
        if (metadata.getFixedModMap().isEmpty()) {
            metadata.addFixedModParam(1, ConverterCVParam.MS_FIXED_MOD.getParam());
            Comment comment = new Comment("Only variable modifications can be reported when the original source is a PRIDE XML file");
            getMZTabFile().addComment(getMZTabFile().getComments().size() + 1, comment);
        }

        //TODO: Move to the right place, it is a default checking (ConverterProvider)
        if (metadata.getVariableModMap().isEmpty()) {
            metadata.addVariableModParam(1, ConverterCVParam.MS_VAR_MOD.getParam());
        }
    }

    /**
     * Function to merge proteins that have the same accession. The search engine score will be written in an
     * optional column. The quantification information of the inference is lost after the merge.
     * @param proteinList list of candidate proteins to be merged
     * @return only one protein with merge information
     */
    private Protein merge(List<Protein> proteinList) {


        int numProteinSearchEngineScore = metadata.getProteinSearchEngineScoreMap().size();
        int numDupProteins = proteinList.size();
        MZBoolean hadQuant = MZBoolean.False;

        StringBuilder dupProteinsSearchEngine;
        StringBuilder dupProteinsSearchEngineScore;
        StringBuilder dupProteinsBestSearchEngineScore;

        dupProteinsSearchEngine = new StringBuilder();
        dupProteinsSearchEngineScore = new StringBuilder();
        dupProteinsBestSearchEngineScore = new StringBuilder();

        checkMergeColumnsDefinition();

        //We fill/initialize the information with the first one and we put in optional columns or merge the rest of information
        Protein protein = new Protein(proteinColumnFactory);

        //More than one protein. Merge and record the problem
        int i = 0;

        for (Protein duplicated : proteinList) {
            if (i == 0) {

                protein = proteinList.get(0);

                if (proteinList.size() == 1) {
                    //We don't need to merge proteins
                    dupProteinsSearchEngine.append("null");
                    dupProteinsSearchEngineScore.append("null");
                    dupProteinsBestSearchEngineScore.append("null");
                }

            } else {
                //Information mergeable
                if (duplicated.getModifications() != null) {
                    for (Modification modification : duplicated.getModifications()) {
                        if (protein.getModifications() != null) {
                            if (!protein.getModifications().contains(modification)) {
                                protein.addModification(modification);
                            }
                        } else {
                            //initialize the splitList and add the modification
                            protein.addModification(modification);
                        }
                    }
                }
                // ambiguityMembers are not support in PRIDEXML
//                if (duplicated.getAmbiguityMembers() != null) {
//                    for (String ambiguityMember : duplicated.getAmbiguityMembers()) {
//                        if (protein.getAmbiguityMembers() != null) {
//                            if (!protein.getAmbiguityMembers().contains(ambiguityMember)) {
//                                protein.addAmbiguityMembers(ambiguityMember);
//                            }
//                        } else {
//                            protein.addAmbiguityMembers(ambiguityMember);
//                        }
//                    }
//                }

                //Counts
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    Integer numPeptidesDistinct = duplicated.getNumPeptidesDistinct(msRun);
                    Integer numPeptidesUnique = duplicated.getNumPeptidesUnique(msRun);
                    Integer numPSMs = duplicated.getNumPSMs(msRun);

                    if (numPeptidesDistinct != null) {
                        protein.setNumPeptidesDistinct(msRun, protein.getNumPeptidesDistinct(msRun) + numPeptidesDistinct);
                    }
                    if (numPeptidesUnique != null) {
                        protein.setNumPeptidesUnique(msRun, protein.getNumPeptidesUnique(msRun) + numPeptidesUnique);
                    }
                    if (numPSMs != null) {
                        protein.setNumPSMs(msRun, protein.getNumPSMs(msRun) + numPSMs);
                    }
                }

                //Not mergeable  search_engine
                //Same columns and appended by "|"
                SplitList<Param> searchEngine = duplicated.getSearchEngine();
                if (searchEngine != null) {
                    dupProteinsSearchEngine.append(searchEngine.toString());
                } else {
                    dupProteinsSearchEngine.append((String) null);
                }

                if (i < numDupProteins - 1) {
                    dupProteinsSearchEngine.append(";");
                }

                //Not mergeable  search_engine_score per ms_run
                for (ProteinSearchEngineScore proteinSearchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
                    Param ro_param = proteinSearchEngineScore.getParam();   //not setValue allow in Param
                    for (MsRun msRun : metadata.getMsRunMap().values()) {

                        Double searchEngineScore = duplicated.getSearchEngineScore(proteinSearchEngineScore.getId(), msRun);
                        CVParam cvParam;
                        if (searchEngineScore != null) {
                            cvParam = new CVParam(ro_param.getCvLabel(), ro_param.getAccession(), ro_param.getName(), searchEngineScore.toString());
                        } else {
                            cvParam = new CVParam(ro_param.getCvLabel(), ro_param.getAccession(), ro_param.getName(), null);
                        }
                        dupProteinsSearchEngineScore.append(cvParam.toString());
                        if (proteinSearchEngineScore.getId() < numProteinSearchEngineScore) {
                            dupProteinsBestSearchEngineScore.append("|");
                        }
                    }
                }

                if (i < numDupProteins - 1) {
                    dupProteinsSearchEngineScore.append(";");
                }

                //Not mergeable  best_search_engine_score
                for (ProteinSearchEngineScore proteinSearchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
                    Param ro_param = proteinSearchEngineScore.getParam();   //not setValue allow

                    Double bestSearchEngineScore = duplicated.getBestSearchEngineScore(proteinSearchEngineScore.getId());
                    CVParam cvParam;
                    if (bestSearchEngineScore != null) {
                        cvParam = new CVParam(ro_param.getCvLabel(), ro_param.getAccession(), ro_param.getName(), bestSearchEngineScore.toString());
                    } else {
                        cvParam = new CVParam(ro_param.getCvLabel(), ro_param.getAccession(), ro_param.getName(), null);
                    }

                    dupProteinsBestSearchEngineScore.append(cvParam.toString());
                    if (proteinSearchEngineScore.getId() < numProteinSearchEngineScore) {
                        dupProteinsBestSearchEngineScore.append("|");
                    }
                }

                if (i < numDupProteins - 1) {
                    dupProteinsBestSearchEngineScore.append(";");
                }
            }
            i++;
        }



        if(numDupProteins > 1){
            logger.warn(numDupProteins + " duplicated proteins with accession " + protein.getAccession() + " have been merge");
        }

        protein.setOptionColumnValue(NUM_MERGE_PROTEINS, numDupProteins);
        protein.setOptionColumnValue(DUP_PROTEINS_SEARCH_ENGINES, dupProteinsSearchEngine.toString());
        protein.setOptionColumnValue(DUP_PROTEINS_BEST_SEARCH_ENGINES_SCORE, dupProteinsBestSearchEngineScore.toString());
        protein.setOptionColumnValue(DUP_PROTEINS_SEARCH_ENGINES_SCORES, dupProteinsSearchEngineScore.toString());

        //Quant
        if(metadata.getProteinQuantificationUnit() != null){
            hadQuant = MZBoolean.True;
        }

        protein.setOptionColumnValue(DUP_PROTEINS_HAD_QUANT, hadQuant);

        if(hadQuant == MZBoolean.True){
            logger.warn(numDupProteins + " duplicated proteins with accession " + protein.getAccession() + " contained quantification information");
        }

        return protein;
    }

    private void checkMergeColumnsDefinition() {

        String header;
        MZTabColumn column;

        //Optional columns definition

        header = OptionColumn.getHeader(null, NUM_MERGE_PROTEINS);
        column = proteinColumnFactory.findColumnByHeader(header);
        if (column == null) {
            proteinColumnFactory.addOptionalColumn(NUM_MERGE_PROTEINS, Integer.class);
        }

        header = OptionColumn.getHeader(null, DUP_PROTEINS_SEARCH_ENGINES);
        column = proteinColumnFactory.findColumnByHeader(header);
        if (column == null) {
            proteinColumnFactory.addOptionalColumn(DUP_PROTEINS_SEARCH_ENGINES, String.class);
        }

        header = OptionColumn.getHeader(null, DUP_PROTEINS_BEST_SEARCH_ENGINES_SCORE);
        column = proteinColumnFactory.findColumnByHeader(header);
        if (column == null) {
            proteinColumnFactory.addOptionalColumn(DUP_PROTEINS_BEST_SEARCH_ENGINES_SCORE, String.class);
        }

        header = OptionColumn.getHeader(null, DUP_PROTEINS_SEARCH_ENGINES_SCORES);
        column = proteinColumnFactory.findColumnByHeader(header);
        if (column == null) {
            proteinColumnFactory.addOptionalColumn(DUP_PROTEINS_SEARCH_ENGINES_SCORES, String.class);
        }

        header = OptionColumn.getHeader(null, DUP_PROTEINS_HAD_QUANT);
        column = proteinColumnFactory.findColumnByHeader(header);
        if (column == null) {
            proteinColumnFactory.addOptionalColumn(DUP_PROTEINS_HAD_QUANT, MZBoolean.class);
        }
    }


    /* Metadata */
    private void loadSearchEngineScores(List<String> identIds) {

        //Future optimization, avoid loop all the identifications, only the n-first
        //TODO sorting the search engines if we need

        Identification identification;
        Double score;
        String searchEngineName;

        SearchEngineScoreParam searchEngineScoreParam;
        SearchEngineScoreParam psm_searchEngineScoreParam;

        List<PeptideItem> peptideItems;
        CVParam psm_scoreParam;
        Integer id;
        Integer psm_id;

        // Iterate over each identification
        for (String identId : identIds) {
            identification = reader.getIdentById(identId);

            score = identification.getScore();
            searchEngineName = identification.getSearchEngine();

            //protein scores
            if (score != null) {
                //Todo: when more than one score is added at protein level the information is missing
                searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByName(searchEngineName);
                id = -1;

                if (searchEngineScoreParam != null) {
                    //CVParam for the metadata section
                    CVParam scoreParam = searchEngineScoreParam.getParam(null);

                    for (Map.Entry<Integer, ProteinSearchEngineScore> entry : metadata.getProteinSearchEngineScoreMap().entrySet()) {
                        if (entry.getValue().getParam().equals(scoreParam)) {
                            id = entry.getKey();
                            break;
                        }
                    }

                    //Add the search engine score in the metadata section. This allow to create properly and extend the column factories with the new columns.
                    if (id <= 0) {
                        id = metadata.getProteinSearchEngineScoreMap().size() + 1;
                        metadata.addProteinSearchEngineScoreParam(id, scoreParam);
                    }
                } else {
                    logger.debug("Protein search engine score param was not found for search engine name " + searchEngineName + ". It can not be added to the metadata section");
                }
            }

            //psms scores
            // We look in the psm to be sure that we have all the possible search engines scores before we write
            // proteins and psms.
            peptideItems = identification.getPeptideItem();

            for (PeptideItem peptideItem : peptideItems) {
                for (uk.ac.ebi.pride.jaxb.model.CvParam cvParam : peptideItem.getAdditional().getCvParam()) {
                    psm_searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByAccession(cvParam.getAccession());
                    psm_id = -1;

                    if (psm_searchEngineScoreParam != null) {
                        psm_scoreParam = psm_searchEngineScoreParam.getParam(null);

                        for (Map.Entry<Integer, PSMSearchEngineScore> entry : metadata.getPsmSearchEngineScoreMap().entrySet()) {
                            if (entry.getValue().getParam().equals(psm_scoreParam)) {
                                psm_id = entry.getKey();
                                break;
                            }
                        }

                        //Add the search engine score in the metadata section and extend the column factories with the new columns.
                        if (psm_id <= 0) {
                            psm_id = metadata.getPsmSearchEngineScoreMap().size() + 1;
                            metadata.addPsmSearchEngineScoreParam(psm_id, psm_scoreParam);
                        }
                    }
                    else {
                        logger.debug("Psm search engine score param was not found for name \"" + cvParam.getName() + "\". It can not be added to the metadata section");
                    }
                }
            }
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
     * Load software. We use a common parameter [MS, MS:1001456, analysis software, ...] to express software[n].
     */
    private void loadSoftware(uk.ac.ebi.pride.jaxb.model.Software software) {
        StringBuilder sb = new StringBuilder();

        if (!isEmpty(software.getName())) {
            sb.append(software.getName());
        }
        if (!isEmpty(software.getVersion())) {
            sb.append(" v").append(software.getVersion());
        }

        if (sb.length() > 0) {
            metadata.addSoftwareParam(1, new CVParam("MS", "MS:1001456", "analysis software", sb.toString().replaceAll(",", "")));
        }
    }

    /**
     * Converts the experiment's references into a couple of {@link PublicationItem} (including DOIs and PubMed ids)
     */
    private void loadReferences(List<Reference> references) {
        if (references == null || references.size() == 0) {
            return;
        }

        int i = 1;
        for (Reference ref : references) {
            uk.ac.ebi.pride.jaxb.model.Param param = ref.getAdditional();
            if (param == null) {
                continue;
            }

            List<PublicationItem> items = new ArrayList<PublicationItem>();

            // check if there's a DOI
            String doi = getPublicationAccession(param, ConverterCVParam.PRIDE_REFERENCE_DOI.getName());
            if (!isEmpty(doi)) {
                items.add(new PublicationItem(PublicationItem.Type.DOI, doi));
            }

            // check if there's a pubmed id
            String pubmed = getPublicationAccession(param, ConverterCVParam.PRIDE_REFERENCE_PUBMED.getName());
            if (!isEmpty(pubmed)) {
                items.add(new PublicationItem(PublicationItem.Type.PUBMED, pubmed));
            }

            if(!items.isEmpty())
                metadata.addPublicationItems(i++, items);
        }
    }

    /**
     * Extract publication accession number from CVParam.
     */
    private String getPublicationAccession(uk.ac.ebi.pride.jaxb.model.Param param, String name) {
        if (param == null || isEmpty(name)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (name.equalsIgnoreCase(p.getName())) {
                return p.getValue();
            }
        }

        return null;
    }

    /**
     * Converts a list of PRIDE JAXB Contacts into an ArrayList of mzTab Contacts.
     */
    private void loadContacts(List<uk.ac.ebi.pride.jaxb.model.Contact> contactList) {
        // make sure there are contacts to be processed
        if (contactList == null || contactList.size() == 0) {
            return;
        }

        // initialize the return variable
        int id = 1;
        for (uk.ac.ebi.pride.jaxb.model.Contact c : contactList) {
            metadata.addContactName(id, c.getName());
            String originalContact = c.getContactInfo();

            //Some files are annotated using more than one email, we will use the first one
            if(originalContact != null && originalContact.contains(";")){
                originalContact=originalContact.split(";")[0];
            }

            metadata.addContactAffiliation(id, c.getInstitution());
            if (originalContact != null && originalContact.contains("@")) {
                metadata.addContactEmail(id, originalContact);
            }
            id++;
        }
    }

    /**
     * Processes the experiment additional params: (f.e. quant method, description...).
     */
    private void loadExperimentParams(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return;
        }

        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (ConverterCVParam.PRIDE_EXPERIMENT_DESCRIPTION.getAccession().equals(p.getAccession()) && !isEmpty(p.getValue())) {
                metadata.setDescription(p.getValue());
            } else if (QuantitationCVParam.isQuantificationMethod(p.getAccession())) {
                // check if it's a quantification method
                metadata.setQuantificationMethod(convertParam(p));
            } else if (ConverterCVParam.PRIDE_GEL_BASED_EXPERIMENT.getAccession().equals(p.getAccession())) {
                //If it a gel we add the optional columns for gel
                gelExperiment = true;
                metadata.addCustom(convertParam(p));
            }
        }
    }

    /**
     * Processes the sample processing information contained in protocol
     */
    private void loadSampleProcessing(Protocol protocol) {
        if (protocol == null) {
            return;
        }

        int i = 1;
        if (protocol.getProtocolSteps() != null) {
            for (uk.ac.ebi.pride.jaxb.model.Param param : protocol.getProtocolSteps().getStepDescription()) {
                if(getFirstCvParam(param) != null)
                    metadata.addSampleProcessingParam(i++, convertParam(getFirstCvParam(param)));
                else if(getFirstUserParam(param) != null)
                    metadata.addSampleProcessingParam(i++, convertUserParam(getFirstUserParam(param)));

            }
            //If the protocol was reported as a UserParam, it can not be converted for now

        }
    }

    private void loadInstrument(uk.ac.ebi.pride.jaxb.model.Instrument instrument) {
        if (instrument == null) {
            return;
        }

        if (!isEmpty(instrument.getInstrumentName())) {
            metadata.addInstrumentName(1, new CVParam("MS", "MS:1000031", "Instrument model", instrument.getInstrumentName()));
        }

        // handle the source information
        uk.ac.ebi.pride.jaxb.model.Param sourceParam = instrument.getSource();
        uk.ac.ebi.pride.jaxb.model.CvParam param = getFirstCvParam(sourceParam);
        if (param != null) {
            metadata.addInstrumentSource(1, convertParam(param));
        }

        uk.ac.ebi.pride.jaxb.model.Param detectorParam = instrument.getDetector();
        param = getFirstCvParam(detectorParam);
        if (param != null) {
            metadata.addInstrumentDetector(1, convertParam(param));
        }

        // handle the analyzer information
        if (instrument.getAnalyzerList().getCount() > 0) {
            for (uk.ac.ebi.pride.jaxb.model.Param analyzerParam : instrument.getAnalyzerList().getAnalyzer()) {
                param = getFirstCvParam(analyzerParam);  //The PRIDE XML file should have only one cvParam here
                if (param != null) {
                    metadata.addInstrumentAnalyzer(1, convertParam(param));
                }
            }
            //If the analyzer was reported as a UserParam, it can not be converted for now
        }

    }

    private void loadURI(String expAccession) {
        if (isEmpty(expAccession)) {
            return;
        }

        try {
            URI uri = new URI("http://www.ebi.ac.uk/pride/archive/assays/" + expAccession);
            metadata.addUri(uri);
        } catch (URISyntaxException e) {
            throw new MZTabConversionException("Error while building URI at the metadata section", e);
        }
    }

    private void loadMsRun() {
        metadata.addMsRunFormat(1, ConverterCVParam.MS_PSI_MZDATA_FILE.getParam());
        metadata.addMsRunIdFormat(1, ConverterCVParam.MS_SPEC_NATIVE_ID_FORMAT.getParam());

        try {
            metadata.addMsRunLocation(1, new URL("file:/" + source.getName()));
        } catch (MalformedURLException e) {
            throw new MZTabConversionException("Error while adding ms run location", e);
        }
    }

    /**
     * Adds the sample parameters (species, tissue, cell type, disease) to the unit and the various subsamples.
     */
    private void loadSamples(SampleDescription sampleDescription, String sampleName) {
        if (sampleDescription == null) {
            return;
        }

        // Quantification
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : sampleDescription.getCvParam()) {
            // check for subsample descriptions
            if (QuantitationCVParam.SUBSAMPLE1_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(1, p.getValue());
                continue;
            }
            if (QuantitationCVParam.SUBSAMPLE2_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(2, p.getValue());
                continue;
            }
            if (QuantitationCVParam.SUBSAMPLE3_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(3, p.getValue());
                continue;
            }
            if (QuantitationCVParam.SUBSAMPLE4_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(4, p.getValue());
                continue;
            }
            if (QuantitationCVParam.SUBSAMPLE5_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(5, p.getValue());
                continue;
            }
            if (QuantitationCVParam.SUBSAMPLE6_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(6, p.getValue());
                continue;
            }
            if (QuantitationCVParam.SUBSAMPLE7_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(7, p.getValue());
                continue;
            }
            if (QuantitationCVParam.SUBSAMPLE8_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(8, p.getValue());
                continue;
            }

            // check if it belongs to a sample
            if (!isEmpty(p.getValue()) && p.getValue().startsWith("subsample")) {
                // get the subsample number
                Pattern subsampleNumberPattern = Pattern.compile("subsample(\\d+)");
                Matcher matcher = subsampleNumberPattern.matcher(p.getValue());

                if (matcher.find()) {
                    Integer id = Integer.parseInt(matcher.group(1));

                    // add the param depending on the type
                    if ("NEWT".equals(p.getCvLabel())) {
                        metadata.addSampleSpecies(id, convertParam(p));
                    } else if ("BRENDA".equals(p.getCvLabel())) {
                        metadata.addSampleTissue(id, convertParam(p));
                    } else if ("CL".equals(p.getCvLabel())) {
                        metadata.addSampleCellType(id, convertParam(p));
                    } else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                        metadata.addSampleDisease(id, convertParam(p));
                    } else if (QuantitationCVParam.isQuantificationReagent(p.getAccession())) {
                        metadata.addAssayQuantificationReagent(id, convertParam(p));
                    } else {
                        metadata.addSampleCustom(id, convertParam(p));
                    }
                }
            }
        }

        // Identification
        if (metadata.getSampleMap().isEmpty()) {
            for (uk.ac.ebi.pride.jaxb.model.CvParam p : sampleDescription.getCvParam()) {
                if (!isEmpty(p.getCvLabel())) {
                    if ("NEWT".equals(p.getCvLabel())) {
                        metadata.addSampleSpecies(1, convertParam(p));
                    } else if ("BTO".equals(p.getCvLabel())) {
                        metadata.addSampleTissue(1, convertParam(p));
                    } else if ("CL".equals(p.getCvLabel())) {
                        metadata.addSampleCellType(1, convertParam(p));
                    } else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                        //DOID: Human Disease Ontology
                        //IDO: Infectious Disease Ontology
                        metadata.addSampleDisease(1, convertParam(p));
                    } else if (!isEmpty(p.getName())) {
                        metadata.addSampleCustom(1, convertParam(p));
                    }
                }
            }
            if (sampleName != null && !sampleName.isEmpty()) {
                metadata.addSampleDescription(1, sampleName);
            }
        }

        // setting custom parameter for identification.
        if (metadata.getSampleMap().size() <= 1) {
            for (uk.ac.ebi.pride.jaxb.model.UserParam userParam : sampleDescription.getUserParam()) {
                metadata.addSampleCustom(1, new UserParam(userParam.getName(), userParam.getValue()));
            }
        }

        // create relationships between ms_run, samples, and assays
        if (metadata.getSampleMap().size() == 1) {
            // Identification
            metadata.addAssaySample(1, metadata.getSampleMap().get(1));
            metadata.addAssayMsRun(1, metadata.getMsRunMap().get(1));
        } else {
            for (Assay assay : metadata.getAssayMap().values()) {
                assay.setSample(metadata.getSampleMap().get(assay.getId()));
                assay.setMsRun(metadata.getMsRunMap().get(1));
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
     * Checks whether the passed identification object is a decoy hit. This function only checks for
     * the presence of specific cv / user Params.
     *
     * @param identification A PRIDE JAXB Identification object.
     * @return Boolean indicating whether the passed identification is a decoy hit.
     */
    private boolean isDecoyHit(Identification identification) {
        if (identification.getAdditional() != null) {
            for (uk.ac.ebi.pride.jaxb.model.CvParam param : identification.getAdditional().getCvParam()) {
                if (param.getAccession().equals(ConverterCVParam.PRIDE_DECOY_HIT.getAccession())) {
                    return true;
                }
            }

            for (uk.ac.ebi.pride.jaxb.model.UserParam param : identification.getAdditional().getUserParam()) {
                if ("Decoy Hit".equals(param.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Converts the passed Identification object into an MzTab protein.
     */
    private Protein loadProtein(Identification identification) {


        // create the protein object
        Protein protein = new Protein(proteinColumnFactory);

        protein.setAccession(identification.getAccession());
        protein.setDatabase(identification.getDatabase());
        String version = (identification.getDatabaseVersion() != null && ! identification.getDatabaseVersion().isEmpty())?identification.getDatabaseVersion():null;
        protein.setDatabaseVersion(version);

        // We mark the decoy hits
        // The optional column was added previously
        CVParam decoyProtein = ConverterCVParam.PRIDE_DECOY_HIT.getParam();

        if (isDecoyHit(identification)) {
            String header = CVParamOptionColumn.getHeader(null, decoyProtein);
            MZTabColumn column = proteinColumnFactory.findColumnByHeader(header);
            if (column != null) {
                protein.setOptionColumnValue(decoyProtein, MZBoolean.True);
            } else {
                proteinColumnFactory.addOptionalColumn(decoyProtein, MZBoolean.class);
                protein.setOptionColumnValue(decoyProtein, MZBoolean.True);
                logger.debug("The protein decoy column has been added.");
            }
        } else {
            String header = CVParamOptionColumn.getHeader(null, decoyProtein);
            MZTabColumn column = proteinColumnFactory.findColumnByHeader(header);
            if (column != null) {
                protein.setOptionColumnValue(decoyProtein, MZBoolean.False);
            } else {
                proteinColumnFactory.addOptionalColumn(decoyProtein, MZBoolean.class);
                protein.setOptionColumnValue(decoyProtein, MZBoolean.False);
                logger.debug("The protein decoy column has been added.");
            }
        }

        if (identification.getSearchEngine() != null) {
            loadSearchEngineScore(protein, identification);
        }

        //TODO protein, species and taxid
        // set protein species and taxid. We are not sure about the origin of the protein. So we keep this value as
        // null to avoid discrepancies, we don't copy from metadata
//        if (!metadata.getSampleMap().isEmpty()) {
//            Sample sample = metadata.getSampleMap().get(1);
//            if (!sample.getSpeciesList().isEmpty()) {
//                Param speciesParam = sample.getSpeciesList().get(0);
//                protein.setSpecies(speciesParam.getName());
//                protein.setTaxid(speciesParam.getAccession());
//            }
//        }

        // get the number of psms and distinct peptides
        List<PeptideItem> items = identification.getPeptideItem();
        List<String> allPeptideList = new ArrayList<String>();
        // sequence + modifications + charge
        HashSet<String> distinctPeptideList = new HashSet<String>();
        for (PeptideItem item : items) {
            List<ModificationItem> modList = item.getModificationItem();
            StringBuilder sb = new StringBuilder();
            for (ModificationItem mod : modList) {
                sb.append(mod.getModAccession()).append(mod.getModLocation());
            }
            sb.append(item.getSequence());
            for (uk.ac.ebi.pride.jaxb.model.CvParam cvParam : item.getAdditional().getCvParam()) {
                if (ConverterCVParam.MS_CHARGE_STATE.getAccession().equalsIgnoreCase(cvParam.getAccession())) {
                    sb.append(cvParam.getValue());
                    break;
                }
            }

            distinctPeptideList.add(sb.toString());
            allPeptideList.add(item.getSequence());
        }
        protein.setNumPSMs(metadata.getMsRunMap().get(1), allPeptideList.size());
        protein.setNumPeptidesDistinct(metadata.getMsRunMap().get(1), distinctPeptideList.size());

        //TODO num_peptides_unique_ms_run
//        protein.setNumPeptidesUnique();

        // set the modifications
        // is not necessary check by ambiguous modifications because are not supported in PRIDEXML
        // the actualization of the metadata with fixed and variable modifications is done in the peptide section
        loadModifications(protein, items);

        // protein coverage
        protein.setProteinConverage(identification.getSequenceCoverage());

        // process the additional params
        if (identification.getAdditional() != null) {
            for (uk.ac.ebi.pride.jaxb.model.CvParam p : identification.getAdditional().getCvParam()) {
                // check if there's a quant unit set
                if (!isIdentification() && (QuantitationCVParam.UNIT_RATIO.getAccession().equals(p.getAccession()) ||
                        QuantitationCVParam.UNIT_COPIES_PER_CELL.getAccession().equals(p.getAccession()))) {
                    CVParam param = convertParam(p);
                    if (param != null && metadata.getProteinQuantificationUnit() == null) {
                        metadata.setProteinQuantificationUnit(param);
                    }
                }
                // Quantification values
                else if (QuantitationCVParam.INTENSITY_SUBSAMPLE1.getAccession().equalsIgnoreCase(p.getAccession())) {
                    protein.setAbundanceColumnValue(metadata.getAssayMap().get(1), p.getValue());
                } else if (QuantitationCVParam.INTENSITY_SUBSAMPLE2.getAccession().equalsIgnoreCase(p.getAccession())) {
                    protein.setAbundanceColumnValue(metadata.getAssayMap().get(2), p.getValue());
                } else if (QuantitationCVParam.INTENSITY_SUBSAMPLE3.getAccession().equalsIgnoreCase(p.getAccession())) {
                    protein.setAbundanceColumnValue(metadata.getAssayMap().get(3), p.getValue());
                } else if (QuantitationCVParam.INTENSITY_SUBSAMPLE4.getAccession().equalsIgnoreCase(p.getAccession())) {
                    protein.setAbundanceColumnValue(metadata.getAssayMap().get(4), p.getValue());
                } else if (QuantitationCVParam.INTENSITY_SUBSAMPLE5.getAccession().equalsIgnoreCase(p.getAccession())) {
                    protein.setAbundanceColumnValue(metadata.getAssayMap().get(5), p.getValue());
                } else if (QuantitationCVParam.INTENSITY_SUBSAMPLE6.getAccession().equalsIgnoreCase(p.getAccession())) {
                    protein.setAbundanceColumnValue(metadata.getAssayMap().get(6), p.getValue());
                } else if (QuantitationCVParam.INTENSITY_SUBSAMPLE7.getAccession().equalsIgnoreCase(p.getAccession())) {
                    protein.setAbundanceColumnValue(metadata.getAssayMap().get(7), p.getValue());
                } else if (QuantitationCVParam.INTENSITY_SUBSAMPLE8.getAccession().equalsIgnoreCase(p.getAccession())) {
                    protein.setAbundanceColumnValue(metadata.getAssayMap().get(8), p.getValue());
                } else {
                    // check optional column.
                    if (QuantitationCVParam.EMPAI_VALUE.getAccession().equals(p.getAccession())) {
                        addOptionalColumnValue(protein, proteinColumnFactory, "empai", p.getValue());
                    } else if (ConverterCVParam.PRIDE_GEL_SPOT_IDENTIFIER.getAccession().equals(p.getAccession())) {
                        // check if there's gel spot identifier
                        addOptionalColumnValue(protein, proteinColumnFactory, "gel_spotidentifier", p.getValue());
                    } else if (ConverterCVParam.PRIDE_GEL_IDENTIFIER.getAccession().equals(p.getAccession())) {
                        // check if there's gel identifier
                        addOptionalColumnValue(protein, proteinColumnFactory, "gel_identifier", p.getValue());
                    }
                }
            }

            // set the description if available
            String description = getCvParamValue(identification.getAdditional(), ConverterCVParam.PRIDE_PROTEIN_NAME.getAccession());
            if(description!= null && !description.isEmpty()) {
                protein.setDescription(description.trim());
            }

            // add the indistinguishable accessions to the ambiguity members
            // for now we are not going to look for these cases because for some of them they are annotated as synonyms instead of ambiguity members of the inference problem,
            // PRIDEXML conversion doesn't support ambiguity members
//            List<String> ambiguityMembers = getAmbiguityMembers(identification.getAdditional(), ConverterCVParam.PRIDE_INDISTINGUISHABLE_ACCESSION.getAccession());
//            for (String member : ambiguityMembers) {
//                protein.addAmbiguityMembers(member);
//            }

        }

        return protein;
    }

    private void loadSearchEngineScore(Protein protein, Identification identification) {

        //Search Engine score value to store
        //PRIDEXML only contain one score in this level, this implies only one search engine and search score
        SearchEngineScoreParam searchEngineScoreParam;
        SearchEngineScoreParam psm_searchEngineScoreParam;
        Double score = identification.getScore();
        String searchEngineName = identification.getSearchEngine();
        Integer id;

        if (score != null) {
            //CVParam for the metadata section
            searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByName(searchEngineName);
            id = -1;

            if (searchEngineScoreParam != null) {
                CVParam scoreParam = searchEngineScoreParam.getParam(null);

                //Look for the search engine score in the metadata section if it has been stored.
                for (Map.Entry<Integer, ProteinSearchEngineScore> entry : metadata.getProteinSearchEngineScoreMap().entrySet()) {
                    if (entry.getValue().getParam().equals(scoreParam)) {
                        id = entry.getKey();
                        break;
                    }
                }

                if (id <= 0) { //if the search engine score is not in the metadata we have a problem
                    logger.warn("The search engine score value can not be converted because the search engine score is not defined in the metadata section.");
                    return;
                }

                //We assume the search engine scores has been detected previously
                protein.setSearchEngineScore(id, metadata.getMsRunMap().get(1), score);
                protein.setBestSearchEngineScore(id, score);
                protein.addSearchEngineParam(searchEngineScoreParam.getSearchEngineParam().getParam());
            }

        } else { //It tries to find the search engine at least
            CVParam searchEngineParam = SearchEngineParam.findParamByName(searchEngineName).getParam();

            if (searchEngineParam != null) {
                protein.addSearchEngineParam(searchEngineParam);
            }
        }
    }

    private List<String> getAmbiguityMembers(uk.ac.ebi.pride.jaxb.model.Param param, String accession) {
        if (param == null || isEmpty(accession)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        List<String> ambiguityMembers = new ArrayList<String>();
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (accession.equals(p.getAccession())) {
                ambiguityMembers.add(p.getValue());
                logger.debug("Ambiguity member added from PRIDE XML file");
            }
        }

        return ambiguityMembers;
    }

    private void loadModifications(Protein protein, List<PeptideItem> items) {

        Set<Modification> modifications = new TreeSet<Modification>(new Comparator<Modification>() {
            @Override
            public int compare(Modification o1, Modification o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });

        for (PeptideItem item : items) {
            for (ModificationItem ptm : item.getModificationItem()) {
                // ignore modifications that can't be processed correctly (can not be mapped to the protein)
                if (ptm.getModAccession() == null) {
                    continue;
                }

                // mod without position
                Modification mod = MZTabUtils.parseModification(Section.Protein, ptm.getModAccession());

                if (mod != null) {

                    // only biological significant modifications are propagated to the protein
                    if (ModParam.isBiological(ptm.getModAccession())) {
                        // if we can calculate the position, we add it to the modification
                        if (item.getStart() != null && ptm.getModLocation() != null) {
                            Integer position = item.getStart().intValue() + ptm.getModLocation().intValue();
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

    /**
     * Converts the passed Identification object into an MzTab PSM.
     */
    private List<PSM> loadPSMs(Identification identification) {

        List<PSM> psmList = new ArrayList<PSM>();

        CVParam decoyPSM = ConverterCVParam.MS_DECOY_PEPTIDE.getParam();
        String header = CVParamOptionColumn.getHeader(null, decoyPSM);

        for (PeptideItem peptideItem : identification.getPeptideItem()) {

            // create the peptide object
            PSM psm = new PSM(psmColumnFactory, metadata);

            psm.setSequence(peptideItem.getSequence());

            String spectrumReference;

            if (peptideItem.getSpectrum() != null) {
                psm.setPSM_ID(peptideItem.getSpectrum().getId());
                spectrumReference = "spectrum=" + Integer.toString(peptideItem.getSpectrum().getId());
                // set the peptide spectrum reference
                psm.addSpectraRef(new SpectraRef(metadata.getMsRunMap().get(1),  spectrumReference));

            }
            else{
                psm.setPSM_ID(alternativeId++);
                logger.debug("There is no spectrum available, using an alternative id as PSM id.");
            }

            psm.setAccession(identification.getAccession());
            psm.setDatabase(identification.getDatabase());
            String version = (identification.getDatabaseVersion() != null && ! identification.getDatabaseVersion().isEmpty())?identification.getDatabaseVersion():null;
            psm.setDatabaseVersion(version);


            if (isDecoyHit(identification)) {
                MZTabColumn column = psmColumnFactory.findColumnByHeader(header);
                if (column != null) {
                    psm.setOptionColumnValue(decoyPSM, MZBoolean.True);
                } else {
                    psmColumnFactory.addOptionalColumn(decoyPSM, MZBoolean.class);
                    psm.setOptionColumnValue(decoyPSM, MZBoolean.True);
                    logger.debug("The psm decoy column has been added.");
                }
            } else {
                MZTabColumn column = psmColumnFactory.findColumnByHeader(header);
                if (column != null) {
                    psm.setOptionColumnValue(decoyPSM, MZBoolean.False);
                } else {
                    psmColumnFactory.addOptionalColumn(decoyPSM, MZBoolean.class);
                    psm.setOptionColumnValue(decoyPSM, MZBoolean.False);
                    logger.debug("The psm decoy column has been added.");
                }
            }


            // set the search engine - if possible
            loadSearchEngineScore(psm, peptideItem);

            // set the modifications
            // is not necessary check by ambiguous modifications because are not supported in PRIDEXML
            // updates the metadata with fixed and variable modifications
            loadModifications(psm, peptideItem);

            // set exp m/z
            try {
                List<Precursor> precursorList = peptideItem.getSpectrum().getSpectrumDesc().getPrecursorList().getPrecursor();
                for (Precursor precursor : precursorList) {
                    if (precursor.getMsLevel() != 1) {
                        continue;
                    }

                    for (uk.ac.ebi.pride.jaxb.model.CvParam c : precursor.getIonSelection().getCvParam()) {
                        if (ConverterCVParam.MS_PRECURSOR_MZ.getAccession().equalsIgnoreCase(c.getAccession())) {
                            psm.setExpMassToCharge(c.getValue());
                            break;
                        }
                    }
                }
            } catch (NullPointerException e) {
                // ignore no precursor ion.
            }

            if (peptideItem.getStart() != null) {
                psm.setStart(peptideItem.getStart().toString());
            }
            if (peptideItem.getEnd() != null) {
                psm.setEnd(peptideItem.getEnd().toString());
            }

            // process the additional params -- mainly check for quantity units
            if (peptideItem.getAdditional() != null) {
                for (uk.ac.ebi.pride.jaxb.model.CvParam p : peptideItem.getAdditional().getCvParam()) {
                    if (ConverterCVParam.MS_CHARGE_STATE.getAccession().equalsIgnoreCase(p.getAccession())) {
                        psm.setCharge(p.getValue());
                    }
                    if (ConverterCVParam.PRIDE_UPSTREAM_FLANKING_SEQUENCE.getAccession().equalsIgnoreCase(p.getAccession())) {
                        psm.setPre(p.getValue());
                    }
                    if (ConverterCVParam.PRIDE_DOWNSTREAM_FLANKING_SEQUENCE.getAccession().equalsIgnoreCase(p.getAccession())) {
                        psm.setPost(p.getValue());
                    }
                }
            }

            // calculate m/z based on sequence.
            if (psm.getCharge() != null) {
                boolean success = false;
                // Step 1: additional cv param list contains theoretical mass
                for (uk.ac.ebi.pride.jaxb.model.CvParam c : peptideItem.getAdditional().getCvParam()) {
                    if (c.getName().trim().equalsIgnoreCase("Theoretical Mass")) {
                        Double mass = Double.parseDouble(c.getValue());
                        psm.setCalcMassToCharge(mass / psm.getCharge());
                        success = true;
                        break;
                    }
                }
                // Step 2: calculate based on sequence + modification + charge.
                if (!success) {
                    try {
                        uk.ac.ebi.pride.mol.Peptide peptide = new uk.ac.ebi.pride.mol.Peptide(peptideItem.getSequence());
                        for (ModificationItem modificationItem : peptideItem.getModificationItem()) {
                            int location = modificationItem.getModLocation().intValue();
                            double mass = Double.parseDouble(modificationItem.getModMonoDelta().get(0));
                            List<Double> monoMassList = new ArrayList<Double>();
                            monoMassList.add(mass);
                            peptide.addModification(location, new PTModification("", "", "", monoMassList, null));
                        }
                        DefaultPeptideIon peptideIon = new DefaultPeptideIon(peptide, psm.getCharge());
                        psm.setCalcMassToCharge(peptideIon.getMassOverCharge());
                    } catch (IllegalArgumentException e) {
                        // ignore unrecognized amino acid Peptide sequence.
                        // need solved in the future.
                    }

                }
            }

            psmList.add(psm);
        }

        return psmList;
    }

    private void loadSearchEngineScore(PSM psm, PeptideItem peptideItem) {

        SearchEngineScoreParam searchEngineScoreParam;
        CVParam scoreParam;
        String score;
        Integer id;

        for (uk.ac.ebi.pride.jaxb.model.CvParam cvParam : peptideItem.getAdditional().getCvParam()) {
            searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByAccession(cvParam.getAccession());
            id = -1;

            if (searchEngineScoreParam != null) {
                scoreParam = searchEngineScoreParam.getParam(null);
                score = cvParam.getValue();

                //Add the search engine score in the metadata section if it has been stored.
                for (Map.Entry<Integer, PSMSearchEngineScore> entry : metadata.getPsmSearchEngineScoreMap().entrySet()) {
                    if (entry.getValue().getParam().equals(scoreParam)) {
                        id = entry.getKey();
                        break;
                    }
                }

                if (id <= 0) { //if the search engine score is not in the metadata we have a problem
                    logger.warn("The search engine score value can not be converted because the search engine score is not defined in the metadata section.");
                    return;
                }

                psm.setSearchEngineScore(id, score);
                psm.addSearchEngineParam(searchEngineScoreParam.getSearchEngineParam().getParam());

            }
        }
    }

    private void loadModifications(PSM psm, PeptideItem item) {

        Modification mod;
        for (ModificationItem ptm : item.getModificationItem()) {
            // ignore modifications that can't be processed correctly
            if (ptm.getModAccession() == null) {
                continue;
            }

            mod = MZTabUtils.parseModification(Section.Peptide, ptm.getModAccession());

            if (mod != null) {

                if (ptm.getModLocation() != null) {
                    Integer position = ptm.getModLocation().intValue();
                    mod.addPosition(position, null);
                }
                psm.addModification(mod);

                // an additional param should exist with the cv name.
                // if not, we can not convert the modification to the header because we don't store all the mod cv terms
                // in this moment
                if (ptm.getAdditional() != null) {
                    uk.ac.ebi.pride.jaxb.model.CvParam additional = ptm.getAdditional().getCvParamByAcc(ptm.getModAccession());
                    if (additional != null) {
                        Param metadataParam = convertParam(additional);

                        // propagate the modification to the metadata section
                        boolean found = false;

                        //For PRIDEXML converter all the modifications are considered variables because
                        // we don't have the information form the original experiment
                        for (VariableMod variableMod : metadata.getVariableModMap().values()) {
                            if (variableMod.getParam().getAccession().equals(ptm.getModAccession())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            //TODO Add the modification site in the future
                            metadata.addVariableModParam(metadata.getVariableModMap().size() + 1, metadataParam);
                        }
                    } else {
                        logger.warn("A CvParam with the modification information is not provided. The modification can not be propagated to the metadata section.");
                    }
                } else {
                    logger.warn("A CvParam with the modification information is not provided. The modification can not be propagated to the metadata section.");
                }
            }
        }
    }


    /* Utils */

    private CVParam convertParam(uk.ac.ebi.pride.jaxb.model.CvParam param) {
        return new CVParam(param.getCvLabel(), param.getAccession(), param.getName(), param.getValue());
    }

    private CVParam convertUserParam(uk.ac.ebi.pride.jaxb.model.UserParam param) {
        return new CVParam(null, null, param.getName(), param.getValue());
    }

    private uk.ac.ebi.pride.jaxb.model.CvParam getFirstCvParam(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return null;
        }

        if (param.getCvParam().iterator().hasNext()) {
            return param.getCvParam().iterator().next();
        }

        return null;
    }

    private uk.ac.ebi.pride.jaxb.model.UserParam getFirstUserParam(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return null;
        }

        if (param.getUserParam().iterator().hasNext()) {
            return param.getUserParam().iterator().next();
        }

        return null;
    }

    private String getCvParamValue(uk.ac.ebi.pride.jaxb.model.Param param, String accession) {
        if (param == null || isEmpty(accession)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (accession.equals(p.getAccession())) {
                return p.getValue();
            }
        }

        return null;
    }

    private void addOptionalColumnValue(MZTabRecord record, MZTabColumnFactory columnFactory, String name, String value) {

        String header = OptionColumn.getHeader(null, name);
        MZTabColumn column = columnFactory.findColumnByHeader(header);

        String logicalPosition;

        if (column == null) {
            logicalPosition =  columnFactory.addOptionalColumn(name, String.class);
        }
        else {
            logicalPosition = column.getLogicPosition();
        }

        record.setValue(logicalPosition, value);
    }


}
