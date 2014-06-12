package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.iongen.model.impl.DefaultPeptideIon;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.model.Param;
import uk.ac.ebi.pride.jmztab.model.UserParam;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabConversionException;
import uk.ac.ebi.pride.mol.PTModification;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;

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
 * User: Qingwei
 * Date: 07/06/13
 */
public class ConvertPrideXMLFile extends ConvertProvider<File, Void> {

    private PrideXmlReader reader;

    private Metadata metadata;
    private MZTabColumnFactory proteinColumnFactory;
    private MZTabColumnFactory psmColumnFactory;

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
        loadSamples(reader.getAdmin().getSampleDescription(),reader.getAdmin().getSampleName());

        // set mzTab- description
        if (isIdentification()) {
            metadata.setMZTabType(MZTabDescription.Type.Identification);
            metadata.setMZTabMode(MZTabDescription.Mode.Complete);
        } else {
            metadata.setMZTabType(MZTabDescription.Type.Quantification);
            metadata.setMZTabMode(MZTabDescription.Mode.Summary);
        }

        //Pending of convert
        //Fragmentation methods
        //Sample processing (protocol steps)
        //MODS
        loadURI(reader.getExpAccession());

        //QUANT METADATA and units
        //CV Info
        //Study vars

        //The description should be added in loadExperiment()
        if (metadata.getDescription() == null || metadata.getDescription().isEmpty()) {
            metadata.setDescription("date of export: " + new Date());
        }

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
        for (Integer id : metadata.getSearchEngineScoreMap().keySet()) {
            //To be compliance with the specification you need the columns in the psms too
            proteinColumnFactory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, id);
            proteinColumnFactory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, id, metadata.getMsRunMap().get(1));
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
        for (Integer id : metadata.getSearchEngineScoreMap().keySet()) {
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

        // Iterate over each identification
        for (String id : ids) {
            Identification identification = reader.getIdentById(id);

            // ignore any decoy hits
            //TODO decoy
            if (isDecoyHit(identification)) {
                continue;
            }

            Protein protein = loadProtein(identification);
            //create a check for duplicated proteins ids (hash instead of array list)
            proteins.add(protein);

            // convert psm
            List<PSM> psmList = loadPSMs(identification);
            psms.addAll(psmList);
        }

        Comment comment = new Comment("Only variable modifications can be reported when the original source is a PRIDE XML file");
        getMZTabFile().addComment(1, comment);

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

            // ignore any decoy hits for search engines values
            //TODO decoy
            if (isDecoyHit(identification)) {
                continue;
            }

            score = identification.getScore();
            searchEngineName = identification.getSearchEngine();

            //protein scores
            if (score != null) {
                searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByName(searchEngineName);
                id = -1;

                if (searchEngineScoreParam != null) {
                    //CVParam for the metadata section
                    CVParam scoreParam = searchEngineScoreParam.toCVParam(null);

                    for (Map.Entry<Integer, SearchEngineScore> entry : metadata.getSearchEngineScoreMap().entrySet()) {
                        if (entry.getValue().getParam().equals(scoreParam)) {
                            id = entry.getKey();
                            break;
                        }
                    }

                    //Add the search engine score in the metadata section and extend the column factories with the new columns.
                    if (id <= 0) {
                        id = metadata.getSearchEngineScoreMap().size() + 1;
                        metadata.addSearchEngineScoreParam(id, scoreParam);
                    }

                    // We look in the psm to be sure that we have all the possible search engines scores before we write
                    // proteins and psms.
                    peptideItems = identification.getPeptideItem();

                    for (PeptideItem peptideItem : peptideItems) {
                        for (CvParam cvParam : peptideItem.getAdditional().getCvParam()) {
                            psm_searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByAccession(cvParam.getAccession());
                            psm_id = -1;

                            if (psm_searchEngineScoreParam != null) {
                                psm_scoreParam = psm_searchEngineScoreParam.toCVParam(null);

                                for (Map.Entry<Integer, SearchEngineScore> entry : metadata.getSearchEngineScoreMap().entrySet()) {
                                    if (entry.getValue().getParam().equals(psm_scoreParam)) {
                                        psm_id = entry.getKey();
                                        break;
                                    }
                                }

                                //Add the search engine score in the metadata section and extend the column factories with the new columns.
                                if (psm_id <= 0) {
                                    psm_id = metadata.getSearchEngineScoreMap().size() + 1;
                                    metadata.addSearchEngineScoreParam(psm_id, psm_scoreParam);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (metadata.getSearchEngineScoreMap().isEmpty()) {
            metadata.addSearchEngineScoreParam(1, SearchEngineScoreParam.SEARCH_ENGINE_SPECIFIC_SCORE.toCVParam(null));
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

        for (Reference ref : references) {
            uk.ac.ebi.pride.jaxb.model.Param param = ref.getAdditional();
            if (param == null) {
                continue;
            }

            List<PublicationItem> items = new ArrayList<PublicationItem>();

            // check if there's a DOI
            String doi = getPublicationAccession(param, DAOCvParams.REFERENCE_DOI.getName());
            if (!isEmpty(doi)) {
                items.add(new PublicationItem(PublicationItem.Type.DOI, doi));
            }

            // check if there's a pubmed id
            String pubmed = getPublicationAccession(param, DAOCvParams.REFERENCE_PUBMED.getName());
            if (!isEmpty(pubmed)) {
                items.add(new PublicationItem(PublicationItem.Type.PUBMED, pubmed));
            }

            metadata.addPublicationItems(1, items);
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
            if (name.equals(p.getCvLabel())) {
                return p.getAccession();
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
            metadata.addContactAffiliation(id, c.getInstitution());
            if (c.getContactInfo() != null && c.getContactInfo().contains("@")) {
                metadata.addContactEmail(id, c.getContactInfo());
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
            if (DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession().equals(p.getAccession()) && !isEmpty(p.getValue())) {
                metadata.setDescription(p.getValue());
            } else if (QuantitationCvParams.isQuantificationMethod(p.getAccession())) {
                // check if it's a quantification method
                metadata.setQuantificationMethod(convertParam(p));
            } else if (DAOCvParams.GEL_BASED_EXPERIMENT.getAccession().equals(p.getAccession())) {
                metadata.addCustom(convertParam(p));
            }
        }
    }

    /**
     * Processes the sample processing information contained in protocol
     */
    private void loadSampleProcessing(Protocol protocol) {
        if(protocol == null){
            return;
        }

        int i=1;
        if(protocol.getProtocolSteps() != null){
            for (uk.ac.ebi.pride.jaxb.model.Param param : protocol.getProtocolSteps().getStepDescription()) {
                metadata.addSampleProcessingParam(i++, convertParam(getFirstCvParam(param)));
            }
            //If the protocol was reported as a UserParam, it can not be converted for now

        }
    }

    private void loadInstrument(uk.ac.ebi.pride.jaxb.model.Instrument instrument) {
        if (instrument == null) {
            return;
        }

        if (!isEmpty(instrument.getInstrumentName())) {
            metadata.addInstrumentName(1, new CVParam("PRIDE", "PRIDE:0000131", "Instrument model", instrument.getInstrumentName()));
        }

        // handle the source information
        uk.ac.ebi.pride.jaxb.model.Param sourceParam = instrument.getSource();
        CvParam param = getFirstCvParam(sourceParam);
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
        metadata.addMsRunFormat(1, new CVParam("MS", "MS:1000564", "PSI mzData file", null));
        metadata.addMsRunIdFormat(1, new CVParam("MS", "MS:1000777", "spectrum identifier nativeID format", null));

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
        for (CvParam p : sampleDescription.getCvParam()) {
            // check for subsample descriptions
            if (QuantitationCvParams.SUBSAMPLE1_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(1, p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE2_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(2, p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE3_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(3, p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE4_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(4, p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE5_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(5, p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE6_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(6, p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE7_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.addSampleDescription(7, p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE8_DESCRIPTION.getAccession().equals(p.getAccession())) {
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
                    } else if (QuantitationCvParams.isQuantificationReagent(p.getAccession())) {
                        metadata.addAssayQuantificationReagent(id, convertParam(p));
                    } else {
                        metadata.addSampleCustom(id, convertParam(p));
                    }
                }
            }
        }

        // Identification
        if (metadata.getSampleMap().isEmpty()) {
            for (CvParam p : sampleDescription.getCvParam()) {
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
            if(sampleName!= null && !sampleName.isEmpty()) {
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
        for (uk.ac.ebi.pride.jaxb.model.CvParam param : identification.getAdditional().getCvParam()) {
            if (param.getAccession().equals(DAOCvParams.DECOY_HIT.getAccession())) {
                return true;
            }
        }

        for (uk.ac.ebi.pride.jaxb.model.UserParam param : identification.getAdditional().getUserParam()) {
            if ("Decoy Hit".equals(param.getName())) {
                return true;
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
        protein.setDatabaseVersion(identification.getDatabaseVersion());

        if (identification.getSearchEngine() != null) {
            loadSearchEngineScore(protein, identification);
        }

        // set the description if available
        String description = getCvParamValue(identification.getAdditional(), DAOCvParams.PROTEIN_NAME.getAccession());
        protein.setDescription(description);

        // set protein species and taxid.
        if (!metadata.getSampleMap().isEmpty()) {
            Sample sample = metadata.getSampleMap().get(1);
            if (!sample.getSpeciesList().isEmpty()) {
                Param speciesParam = sample.getSpeciesList().get(0);
                protein.setSpecies(speciesParam.getName());
                protein.setTaxid(speciesParam.getAccession());
            }
        }

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
            for (CvParam cvParam : item.getAdditional().getCvParam()) {
                if (DAOCvParams.CHARGE_STATE.getAccession().equalsIgnoreCase(cvParam.getAccession())) {
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

        // add the indistinguishable accessions to the ambiguity members
        List<String> ambiguityMembers = getAmbiguityMembers(identification.getAdditional(), DAOCvParams.INDISTINGUISHABLE_ACCESSION.getAccession());
        for (String member : ambiguityMembers) {
            protein.addAmbiguityMembers(member);
        }

        // set the modifications
        // is not necessary check by ambiguous modifications because are not supported in PRIDEXML
        // the actualization of the metadata with fixed and variable modifications is done in the peptide section
        loadModifications(protein, items);

        // protein coverage
        protein.setProteinConverage(identification.getSequenceCoverage());

        // process the additional params
        for (CvParam p : identification.getAdditional().getCvParam()) {
            // check if there's a quant unit set
            if (!isIdentification() && (QuantitationCvParams.UNIT_RATIO.getAccession().equals(p.getAccession()) || QuantitationCvParams.UNIT_COPIES_PER_CELL.getAccession().equals(p.getAccession()))) {
                CVParam param = convertParam(p);
                if (param != null && metadata.getProteinQuantificationUnit() == null) {
                    metadata.setProteinQuantificationUnit(param);
                }
            }
            // Quantification values
            else if (QuantitationCvParams.INTENSITY_SUBSAMPLE1.getAccession().equalsIgnoreCase(p.getAccession())) {
                protein.setAbundanceColumnValue(metadata.getAssayMap().get(1), p.getValue());
            } else if (QuantitationCvParams.INTENSITY_SUBSAMPLE2.getAccession().equalsIgnoreCase(p.getAccession())) {
                protein.setAbundanceColumnValue(metadata.getAssayMap().get(2), p.getValue());
            } else if (QuantitationCvParams.INTENSITY_SUBSAMPLE3.getAccession().equalsIgnoreCase(p.getAccession())) {
                protein.setAbundanceColumnValue(metadata.getAssayMap().get(3), p.getValue());
            } else if (QuantitationCvParams.INTENSITY_SUBSAMPLE4.getAccession().equalsIgnoreCase(p.getAccession())) {
                protein.setAbundanceColumnValue(metadata.getAssayMap().get(4), p.getValue());
            } else if (QuantitationCvParams.INTENSITY_SUBSAMPLE5.getAccession().equalsIgnoreCase(p.getAccession())) {
                protein.setAbundanceColumnValue(metadata.getAssayMap().get(5), p.getValue());
            } else if (QuantitationCvParams.INTENSITY_SUBSAMPLE6.getAccession().equalsIgnoreCase(p.getAccession())) {
                protein.setAbundanceColumnValue(metadata.getAssayMap().get(6), p.getValue());
            } else if (QuantitationCvParams.INTENSITY_SUBSAMPLE7.getAccession().equalsIgnoreCase(p.getAccession())) {
                protein.setAbundanceColumnValue(metadata.getAssayMap().get(7), p.getValue());
            } else if (QuantitationCvParams.INTENSITY_SUBSAMPLE8.getAccession().equalsIgnoreCase(p.getAccession())) {
                protein.setAbundanceColumnValue(metadata.getAssayMap().get(8), p.getValue());
            } else {
                // check optional column.
                if (QuantitationCvParams.EMPAI_VALUE.getAccession().equals(p.getAccession())) {
                    addOptionalColumnValue(protein, proteinColumnFactory, "empai", p.getValue());
                } else if (DAOCvParams.GEL_SPOT_IDENTIFIER.getAccession().equals(p.getAccession())) {
                    // check if there's gel spot identifier
                    addOptionalColumnValue(protein, proteinColumnFactory, "gel_spotidentifier", p.getValue());
                } else if (DAOCvParams.GEL_IDENTIFIER.getAccession().equals(p.getAccession())) {
                    // check if there's gel identifier
                    addOptionalColumnValue(protein, proteinColumnFactory, "gel_identifier", p.getValue());
                }
            }
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
        List<PeptideItem> peptideItems = identification.getPeptideItem();
        CVParam psm_scoreParam;
        Integer id;
        Integer psm_id;

        if (score != null) {
            //CVParam for the metadata section
            searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByName(searchEngineName);
            id = -1;

            if (searchEngineScoreParam != null) {
                CVParam scoreParam = searchEngineScoreParam.toCVParam(null);

                //Add the search engine score in the metadata section if it has been stored.
                for (Map.Entry<Integer, SearchEngineScore> entry : metadata.getSearchEngineScoreMap().entrySet()) {
                    if (entry.getValue().getParam().equals(scoreParam)) {
                        id = entry.getKey();
                        break;
                    }
                }

                if (id <= 0) { //if the search engine score is not in the metadata we have a problem
                    //TODO report problem
                    return;
                }

                //We assume the search engine scores has been detected previously
                protein.setSearchEngineScore(id, metadata.getMsRunMap().get(1), score);
                protein.setBestSearchEngineScore(id, score);
                protein.addSearchEngineParam(searchEngineScoreParam.getSearchEngineParam().toCVParam());
            }

        } else { //It tries to find the search engine at least
            CVParam searchEngineParam = SearchEngineParam.findParamByName(searchEngineName).toCVParam();

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
            }
        }

        return ambiguityMembers;
    }

    private void loadModifications(Protein protein, List<PeptideItem> items) {

        //TODO simplify
        Set<Modification> modifications = new TreeSet<Modification>(new Comparator<Modification>() {
            @Override
            public int compare(Modification o1, Modification o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        }) ;

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

        for (PeptideItem peptideItem : identification.getPeptideItem()) {
            if (peptideItem.getSpectrum() == null) {
                continue;
            }

            // create the peptide object
            PSM psm = new PSM(psmColumnFactory, metadata);

            psm.setSequence(peptideItem.getSequence());
            psm.setPSM_ID(peptideItem.getSpectrum().getId());
            psm.setAccession(identification.getAccession());
            psm.setDatabase(identification.getDatabase());
            psm.setDatabaseVersion(identification.getDatabaseVersion());

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

                    for (CvParam c : precursor.getIonSelection().getCvParam()) {
                        if (DAOCvParams.PRECURSOR_MZ.getAccession().equalsIgnoreCase(c.getAccession())) {
                            psm.setExpMassToCharge(c.getValue());
                            break;
                        }
                    }
                }
            } catch (NullPointerException e) {
                // ignore no precursor ion.
            }

            // set the peptide spectrum reference
            String spectrumReference = peptideItem.getSpectrum() == null ? "null" : Integer.toString(peptideItem.getSpectrum().getId());
            psm.addSpectraRef(new SpectraRef(metadata.getMsRunMap().get(1), "spectrum=" + spectrumReference));

            if (peptideItem.getStart() != null) {
                psm.setStart(peptideItem.getStart().toString());
            }
            if (peptideItem.getEnd() != null) {
                psm.setEnd(peptideItem.getEnd().toString());
            }

            // process the additional params -- mainly check for quantity units
            if (peptideItem.getAdditional() != null) {
                for (CvParam p : peptideItem.getAdditional().getCvParam()) {
                    if (DAOCvParams.CHARGE_STATE.getAccession().equalsIgnoreCase(p.getAccession())) {
                        psm.setCharge(p.getValue());
                    }
                    if(DAOCvParams.UPSTREAM_FLANKING_SEQUENCE.getAccession().equalsIgnoreCase(p.getAccession())){
                        psm.setPre(p.getValue());
                    }
                    if(DAOCvParams.DOWNSTREAM_FLANKING_SEQUENCE.getAccession().equalsIgnoreCase(p.getAccession())){
                        psm.setPost(p.getValue());
                    }
                }
            }

            // calculate m/z based on sequence.
            if (psm.getCharge() != null) {
                boolean success = false;
                // Step 1: additional cv param list contains theoretical mass
                for (CvParam c : peptideItem.getAdditional().getCvParam()) {
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

        for (CvParam cvParam : peptideItem.getAdditional().getCvParam()) {
            searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByAccession(cvParam.getAccession());
            id = -1;

            if (searchEngineScoreParam != null) {
                scoreParam = searchEngineScoreParam.toCVParam(null);
                score = cvParam.getValue();

                //Add the search engine score in the metadata section if it has been stored.
                for (Map.Entry<Integer, SearchEngineScore> entry : metadata.getSearchEngineScoreMap().entrySet()) {
                    if (entry.getValue().getParam().equals(scoreParam)) {
                        id = entry.getKey();
                        break;
                    }
                }

                if (id <= 0) { //if the search engine score is not in the metadata we have a problem
                    //TODO report problem
                    return;
                }

                psm.setSearchEngineScore(id, score);
                psm.addSearchEngineParam(searchEngineScoreParam.getSearchEngineParam().toCVParam());

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

                if(ptm.getModLocation() != null){
                    Integer position = ptm.getModLocation() .intValue();
                    mod.addPosition(position, null);
                }
                psm.addModification(mod);

                // an additional param should exist with the cv name.
                // if not, we can not convert the modification to the header because we don't store all the mod cv terms
                // in this moment
                CvParam additional = ptm.getAdditional().getCvParamByAcc(ptm.getModAccession());
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
                    if (!found)
                        metadata.addVariableModParam(metadata.getVariableModMap().size() + 1, metadataParam);

                }
                else {
                    //TODO report a warning and/or enrich with database information
                }
            }
        }
    }


    /* Utils */

    private CVParam convertParam(uk.ac.ebi.pride.jaxb.model.CvParam param) {
        return new CVParam(param.getCvLabel(), param.getAccession(), param.getName(), param.getValue());
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
        String logicalPosition = columnFactory.addOptionalColumn(name, String.class);
        record.setValue(logicalPosition, value);
    }


}
