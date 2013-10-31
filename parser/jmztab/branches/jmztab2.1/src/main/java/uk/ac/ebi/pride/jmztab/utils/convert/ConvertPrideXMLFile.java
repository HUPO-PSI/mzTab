package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.iongen.model.impl.DefaultPeptideIon;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.model.Param;
import uk.ac.ebi.pride.jmztab.model.UserParam;
import uk.ac.ebi.pride.mol.PTModification;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.Utils;
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
 * User: Qingwei
 * Date: 07/06/13
 */
public class ConvertPrideXMLFile extends ConvertFile {
    private PrideXmlReader reader;
    private MsRun msRun;

    public ConvertPrideXMLFile(File inFile) {
        super(inFile, PRIDE);
        this.reader = new PrideXmlReader(inFile);

        msRun = new MsRun(1);
        try {
            msRun.setLocation(inFile.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        createArchitecture();
        fillData();
    }

    @Override
    protected Metadata convertMetadata() {
        this.metadata = new Metadata();

        metadata.setMZTabID(reader.getExpAccession());
        metadata.setTitle(reader.getExpTitle());

        loadSoftware(reader.getDataProcessing().getSoftware());

        // process the references
        loadReferences(reader.getReferences());
        // process the contacts
        loadContacts(reader.getAdmin().getContact());
        // process the experiment params
        loadExperimentParams(reader.getAdditionalParams());
        // process the instrument information
        loadInstrument(reader.getInstrument());
        // set the accession as URI if there's one
        loadURI(reader.getExpAccession());
        // set Ms Run
        loadMsRun(reader.getExpAccession());

        loadSubSamples(reader.getAdmin().getSampleDescription());

        if (isIdentification()) {
            metadata.setMZTabType(MZTabDescription.Type.Identification);
            metadata.setMZTabMode(MZTabDescription.Mode.Complete);
        } else {
            metadata.setMZTabType(MZTabDescription.Type.Quantification);
            metadata.setMZTabMode(MZTabDescription.Mode.Summary);
        }

        metadata.addCustom(new UserParam("date of export", new Date().toString()));
        return metadata;
    }

    private void loadSoftware(uk.ac.ebi.pride.jaxb.model.Software software) {
        StringBuilder sb = new StringBuilder();

        if (! isEmpty(software.getName())) {
            sb.append(software.getName());
        }
        if (! isEmpty(software.getVersion())) {
            sb.append(" v").append(software.getVersion());
        }

        if (sb.length() > 0) {
            metadata.addSoftwareParam(1, new CVParam("MS", "MS:1001456", "analysis software", sb.toString().replaceAll("," , "")));
        }
    }

    /**
     * Converts the experiment's references into the reference string (DOIs and PubMed ids)
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
            if (! isEmpty(doi)) {
                items.add(new PublicationItem(PublicationItem.Type.DOI, doi));
            }

            // check if there's a pubmed id
            String pubmed = getPublicationAccession(param, DAOCvParams.REFERENCE_PUBMED.getName());
            if (! isEmpty(pubmed)) {
                items.add(new PublicationItem(PublicationItem.Type.PUBMED, pubmed));
            }

            metadata.addPublicationItems(1, items);
        }
    }

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
    private void loadContacts(List<uk.ac.ebi.pride.jaxb.model.Contact> contactList)  {
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
     * Processes the experiment additional params
     * (f.e. quant method, description...).
     */
    private void loadExperimentParams(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return;
        }

        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.setDescription(p.getValue());
            } else if (QuantitationCvParams.isQuantificationMethod(p.getAccession())) {
                // check if it's a quantification method
                metadata.setQuantificationMethod(convertParam(p));
            } else if (DAOCvParams.GEL_BASED_EXPERIMENT.getAccession().equals(p.getAccession())) {
                metadata.addCustom(convertParam(p));
            }
        }
    }

    private enum SearchEngineParameter {
        MASCOT("MS", "MS:1001207", "Mascot"),
        OMSSA("MS", "MS:1001475", "OMSSA"),
        SEQUEST("MS", "MS:1001208", "Sequest"),
        SPECTRUMMILL( "MS", "MS:1000687", "Spectrum Mill for MassHunter Workstation"),
        SPECTRAST("MS", "MS:1001477", "SpectaST"),
        XTANDEM_1("MS", "MS:1001476", "X!Tandem"),
        XTANDEM_2("MS", "MS:1001476", "X!Tandem");

        private String cvLabel;
        private String accession;
        private String name;

        private SearchEngineParameter(String cvLabel, String accession, String name) {
            this.cvLabel = cvLabel;
            this.accession = accession;
            this.name = name;
        }
    }

    private enum SearchEngineScoreParameter {
        MASCOT("PRIDE", "PRIDE:0000069", "Mascot score", null),
        OMSSA_E("PRIDE", "PRIDE:0000185", "OMSSA E-value", null),
        OMSSA_P("PRIDE", "PRIDE:0000186", "OMSSA P-value", null),
        SEQUEST("PRIDE", "PRIDE:0000053", "Sequest score", null),
        SPECTRUMMILL( "PRIDE", "PRIDE:0000177", "Spectrum Mill peptide score", null),
        XTANDEM("PRIDE", "PRIDE:0000176", "X!Tandem Hyperscore", null),
        SLOMO("PRIDE", "PRIDE:0000275", "SloMo score", null),
        DISCRIMINANT("PRIDE", "PRIDE:0000138", "Discriminant score", null);

        private String cvLabel;
        private String accession;
        private String name;
        private Double value;

        private SearchEngineScoreParameter(String cvLabel, String accession, String name, Double value) {
            this.cvLabel = cvLabel;
            this.accession = accession;
            this.name = name;
            this.value = value;
        }
    }

    /**
     * Tries to guess which search engine is described by the passed name. In case no matching parameter
     * is found, null is returned.
     */
    private CVParam findSearchEngineParam(String searchEngineName) {
        if (searchEngineName == null) {
            return null;
        }

        searchEngineName = searchEngineName.toLowerCase();

        SearchEngineParameter param = null;
        if (searchEngineName.contains("mascot")) {
            param = SearchEngineParameter.MASCOT;
        } else if (searchEngineName.contains("omssa")) {
            param = SearchEngineParameter.OMSSA;
        } else if (searchEngineName.contains("sequest")) {
            param = SearchEngineParameter.SEQUEST;
        } else if (searchEngineName.contains("spectrummill")) {
            param = SearchEngineParameter.SPECTRUMMILL;
        } else if (searchEngineName.contains("spectrast")) {
            param = SearchEngineParameter.SPECTRAST;
        } else if (searchEngineName.contains("xtandem")) {
            param = SearchEngineParameter.XTANDEM_1;
        } else if (searchEngineName.contains("x!tandem")) {
            param = SearchEngineParameter.XTANDEM_2;
        }

        if (param == null) {
            return null;
        } else {
            return new CVParam(param.cvLabel, param.accession, param.name, null);
        }
    }

//    private CVParam findSearchEngineScoreParam(Identification identification) {
//        String searchEngineName = identification.getSearchEngine();
//        if (searchEngineName == null) {
//            return null;
//        }
//
//        searchEngineName = searchEngineName.toLowerCase();
//
//        SearchEngineScoreParameter param = null;
//        if (searchEngineName.contains("mascot")) {
//            param = SearchEngineScoreParameter.MASCOT;
//            param.value = identification.getScore();
//        } else if (searchEngineName.contains("omssa")) {
//            param = SearchEngineScoreParameter.OMSSA_E;
//            param.value = identification.getScore();
//        } else if (searchEngineName.contains("sequest")) {
//            param = SearchEngineScoreParameter.SEQUEST;
//            param.value = identification.getScore();
//        } else if (searchEngineName.contains("spectrummill")) {
//            param = SearchEngineScoreParameter.SPECTRUMMILL;
//            param.value = identification.getScore();
//        } else if (searchEngineName.contains("xtandem")) {
//            param = SearchEngineScoreParameter.XTANDEM_1;
//            param.value = identification.getScore();
//        } else if (searchEngineName.contains("x!tandem")) {
//            param = SearchEngineScoreParameter.XTANDEM_2;
//            param.value = identification.getScore();
//        }
//
//        if (param == null) {
//            return null;
//        } else {
//            return new CVParam(param.cvLabel, param.accession, param.name, (param.value == null ? "" : param.value.toString()));
//        }
//    }

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

    /**
     * Checks whether the passed identification object is a decoy hit. This function only checks for
     * the presence of specific cv / user Params.
     *
     * @param identification A PRIDE JAXB Identification object.
     * @return Boolean indicating whether the passed identification is a decoy hit.
     */
    private boolean isDecoyHit(Identification identification) {
        for (uk.ac.ebi.pride.jaxb.model.CvParam param : identification.getAdditional().getCvParam()) {
            if (param.getAccession().equals(DAOCvParams.DECOY_HIT.getAccession()))
                return true;
        }

        for (uk.ac.ebi.pride.jaxb.model.UserParam param : identification.getAdditional().getUserParam()) {
            if ("Decoy Hit".equals(param.getName()))
                return true;
        }

        return false;
    }

    private void loadInstrument(uk.ac.ebi.pride.jaxb.model.Instrument instrument) {
        if (instrument == null) {
            return;
        }

        if (! isEmpty(instrument.getInstrumentName())) {
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
            uk.ac.ebi.pride.jaxb.model.Param analyzerParam = instrument.getAnalyzerList().getAnalyzer().iterator().next();
            param = getFirstCvParam(analyzerParam);
            if (param != null) {
                metadata.addInstrumentAnalyzer(1, convertParam(param));
            }
        }

    }

    private void loadURI(String expAccession) {
        if (isEmpty(expAccession)) {
            return;
        }

        try {
            URI uri = new URI("http://www.ebi.ac.uk/pride/experiment.do?experimentAccessionNumber=" + expAccession);
            metadata.addUri(uri);
        } catch (URISyntaxException e) {
            // do nothing
        }
    }

    private void loadMsRun(String expAccession) {
        if (!inFile.isFile()) {
            return;
        }

        metadata.addMsRunFormat(1, new CVParam("MS", "MS:1000564", "PSI mzData file", null));
        metadata.addMsRunIdFormat(1, new CVParam("MS", "MS:1000777", "spectrum identifier nativeID format", null));
        try {
            metadata.addMsRunLocation(1, new URL("ftp://ftp.ebi.ac.uk/pub/databases/pride/PRIDE_Exp_Complete_Ac_" + expAccession + ".xml"));
        } catch (MalformedURLException e) {
            // do nothing
        }
    }

    /**
     * Adds the sample parameters (species, tissue, cell type, disease) to the unit and the various subsamples.
     */
    private void loadSubSamples(SampleDescription sampleDescription) {
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
            if (! isEmpty(p.getValue()) && p.getValue().startsWith("subsample")) {
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
                if (isEmpty(p.getValue())) {
                    if ("NEWT".equals(p.getCvLabel())) {
                        metadata.addSampleSpecies(1, convertParam(p));
                    }
                    else if ("BTO".equals(p.getCvLabel())) {
                        metadata.addSampleTissue(1, convertParam(p));
                    }
                    else if ("CL".equals(p.getCvLabel())) {
                        metadata.addSampleCellType(1, convertParam(p));
                    }
                    else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                        //DOID: Human Disease Ontology
                        //IDO: Infectious Disease Ontology
                        metadata.addSampleDisease(1, convertParam(p));
                    }
                    else {
                        metadata.addSampleCustom(1, convertParam(p));
                    }
                }
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
            metadata.addAssayMsRun(1, msRun);
        } else {
            for (Assay assay : metadata.getAssayMap().values()) {
                assay.setSample(metadata.getSampleMap().get(assay.getId()));
                assay.setMsRun(msRun);
            }
        }
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

    private Collection<Modification> getProteinModifications(List<PeptideItem> items) {
        HashMap<String, Modification> modifications = new HashMap<String, Modification>();

        Modification mod;
        for (PeptideItem item : items) {
            for (ModificationItem ptm : item.getModificationItem()) {
                // ignore modifications that can't be processed correctly
                if (item.getStart() == null || ptm.getModAccession() == null || ptm.getModLocation() == null) {
                    continue;
                }
                mod = modifications.get(ptm.getModAccession());
                if (mod == null) {
                    mod = MZTabUtils.parseModification(Section.Protein, ptm.getModAccession());
                    modifications.put(ptm.getModAccession(), mod);
                }

                Integer position = item.getStart().intValue() + ptm.getModLocation().intValue() - 1;
                mod.addPosition(position, null);
            }
        }

        return modifications.values();
    }

    /**
     * Only one sample means this file is Identification and Complete. Otherwise, the file is Quantification and Summary.
     */
    private boolean isIdentification() {
        return metadata.getSampleMap().size() == 1;
    }

    @Override
    protected MZTabColumnFactory convertProteinColumnFactory() {
        MZTabColumnFactory proteinColumnFactory = MZTabColumnFactory.getInstance(Section.Protein);

        // If not provide protein_quantification_unit in metadata, default value is Ratio
        if (metadata.getProteinQuantificationUnit() == null) {
            metadata.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        }

        // ms_run[1] optional columns
        proteinColumnFactory.addOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, msRun);
        proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun);
        proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun);
        // for quantification file, need provide all optional columns for each ms_run.
        if (! isIdentification()) {
            proteinColumnFactory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun);
        }

        return proteinColumnFactory;
    }

    @Override
    protected MZTabColumnFactory convertPeptideColumnFactory() {
        return null;
    }

    @Override
    protected MZTabColumnFactory convertPSMColumnFactory() {
        return MZTabColumnFactory.getInstance(Section.PSM);
    }

    @Override
    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        return null;
    }

    @Override
    protected void fillData() {
        // Get a list of Identification ids
        List<String> ids = reader.getIdentIds();

        // Iterate over each identification
        for(String id : ids) {
            Identification identification = reader.getIdentById(id);

            // ignore any decoy hits
            if (isDecoyHit(identification)) {
                continue;
            }

            Protein protein = loadProtein(identification);
            proteins.add(protein);

            // convert psm
            List<PSM> psmList = loadPSMs(identification);
            psms.addAll(psmList);
        }
    }

    private void addOptionalColumnValue(MZTabRecord record, MZTabColumnFactory columnFactory, String name, String value) {
        String logicalPosition = columnFactory.addOptionalColumn(name, String.class);
        record.setValue(logicalPosition, value);
    }

    private void loadSearchEngineScore(Protein protein, Identification identification) {
        if (identification.getScore() != null) {
            CVParam score = null;
            for (PeptideItem peptideItem : identification.getPeptideItem()) {
                score = getSearchEngineScores(peptideItem.getAdditional().getCvParam());
                if (score != null) {
                    score = new CVParam(score.getCvLabel(), score.getAccession(), score.getName(), identification.getScore().toString());
                    protein.addSearchEngineScoreParam(msRun, score);
                    break;
                }
            }
            if (score == null) {
                // not find score term in search engine score
                if (identification.getSearchEngine().toLowerCase().contains("spectrast")) {
                    score = new CVParam(null, null, "SpectraST score", identification.getScore().toString());
                    protein.addSearchEngineScoreParam(msRun, score);
                }
            }

            // in PRIDE XML, best_search_engine_score and search_engine_score_ms_run[1] are same.
            if (score != null) {
                protein.addBestSearchEngineScoreParam(score);
            }
        }
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

        uk.ac.ebi.pride.jmztab.model.Param searchEngineParam = findSearchEngineParam(identification.getSearchEngine());
        if (searchEngineParam != null) {
            protein.addSearchEngineParam(searchEngineParam);
        }
        loadSearchEngineScore(protein, identification);

        // set the description if available
        String description = getCvParamValue(identification.getAdditional(), DAOCvParams.PROTEIN_NAME.getAccession());
        protein.setDescription(description);

        // set protein species and taxid.
        Sample sample = metadata.getSampleMap().get(1);
        Param speciesParam = sample.getSpeciesList().get(0);
        protein.setSpecies(speciesParam.getName());
        protein.setTaxid(speciesParam.getAccession());

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
        protein.setNumPSMs(msRun, allPeptideList.size());
        protein.setNumPeptidesDistinct(msRun, distinctPeptideList.size());

        // add the indistinguishable accessions to the ambiguity members
        List<String> ambiguityMembers = getAmbiguityMembers(identification.getAdditional(), DAOCvParams.INDISTINGUISHABLE_ACCESSION.getAccession());
        for (String member : ambiguityMembers) {
            protein.addAmbiguityMembers(member);
        }

        // set the modifications
        for (Modification modification : getProteinModifications(items)) {
            protein.addModification(modification);
        }

        // protein coverage
        protein.setProteinConverage(identification.getSequenceCoverage());

        // process the additional params
        for (CvParam p : identification.getAdditional().getCvParam()) {
            // check if there's a quant unit set
            if (QuantitationCvParams.UNIT_RATIO.getAccession().equals(p.getAccession()) || QuantitationCvParams.UNIT_COPIES_PER_CELL.getAccession().equals(p.getAccession())) {
                CVParam param = convertParam(p);
                if (param != null && metadata.getProteinQuantificationUnit() == null) {
                    metadata.setProteinQuantificationUnit(param);
                }
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

    /**
     * Extracts all search engine score related parameters from a peptide object and returns them in a list of
     * mzTab Params.
     */
    private CVParam getSearchEngineScores(List<CvParam> cvParamList) {
        for (CvParam param : cvParamList) {
            if (Utils.PEPTIDE_SCORE_PARAM.isScoreAccession(param.getAccession())) {
                return convertParam(param);
            } else {
                for (SearchEngineScoreParameter scoreParameter : SearchEngineScoreParameter.values()) {
                    if (scoreParameter.accession.equalsIgnoreCase(param.getAccession())) {
                        return convertParam(param);
                    }
                }
            }
        }

        return null;
    }

    private Collection<Modification> getPeptideModifications(PeptideItem item) {
        HashMap<String, Modification> modifications = new HashMap<String, Modification>();

        Modification mod;
        for (ModificationItem ptm : item.getModificationItem()) {
            // ignore modifications that can't be processed correctly
            if (item.getStart() == null || ptm.getModAccession() == null || ptm.getModLocation() == null) {
                continue;
            }
            mod = modifications.get(ptm.getModAccession());
            if (mod == null) {
                mod = MZTabUtils.parseModification(Section.Peptide, ptm.getModAccession());
                modifications.put(ptm.getModAccession(), mod);
            }

            Integer position = ptm.getModLocation().intValue();
            mod.addPosition(position, null);
        }

        return modifications.values();
    }

    private List<PSM> loadPSMs(Identification identification) {
        List<PSM> psmList = new ArrayList<PSM>();

        for (PeptideItem peptideItem : identification.getPeptideItem()) {
            // create the peptide object
            PSM psm = new PSM(psmColumnFactory, metadata);

            psm.setSequence(peptideItem.getSequence());
            psm.setPSM_ID(peptideItem.getSpectrum().getId());
            psm.setAccession(identification.getAccession());
            psm.setDatabase(identification.getDatabase());
            psm.setDatabaseVersion(identification.getDatabaseVersion());

            // set the search engine - is possible
            uk.ac.ebi.pride.jmztab.model.Param searchEngineParam = findSearchEngineParam(identification.getSearchEngine());
            if (searchEngineParam != null) {
                psm.addSearchEngineParam(searchEngineParam);
                psm.addSearchEngineScoreParam(getSearchEngineScores(peptideItem.getAdditional().getCvParam()));
            }

            // set the modifications
            for (Modification modification : getPeptideModifications(peptideItem)) {
                psm.addModification(modification);
            }

            // set exp m/z
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

            // set the peptide spectrum reference
            String spectrumReference = peptideItem.getSpectrum() == null ? "null" : Integer.toString(peptideItem.getSpectrum().getId());
            psm.addSpectraRef(new SpectraRef(msRun, "spectrum=" + spectrumReference));

            if (peptideItem.getStart() != null) {
                psm.setStart(peptideItem.getStart().toString());
            }
            if (peptideItem.getEnd() != null) {
                psm.setEnd(peptideItem.getEnd().toString());
            }


            // process the additional params -- mainly check for quantity units
            if (peptideItem.getAdditional() != null) {
                for (CvParam p : peptideItem.getAdditional().getCvParam()) {
                    // check if there's a quant unit set
                    if (QuantitationCvParams.UNIT_RATIO.getAccession().equals(p.getAccession()) || QuantitationCvParams.UNIT_COPIES_PER_CELL.getAccession().equals(p.getAccession())) {
                        CVParam param = convertParam(p);
                        if (param != null) {
                            metadata.setPeptideQuantificationUnit(param);
                        }
                    }
                    else if (DAOCvParams.CHARGE_STATE.getAccession().equalsIgnoreCase(p.getAccession())) {
                        psm.setCharge(p.getValue());
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
                if (! success) {
                    uk.ac.ebi.pride.mol.Peptide peptide = new uk.ac.ebi.pride.mol.Peptide(peptideItem.getSequence());
                    for (ModificationItem modificationItem : peptideItem.getModificationItem()) {
                        int location = modificationItem.getModLocation().intValue();
                        double mass = Double.parseDouble(modificationItem.getModMonoDelta().get(0));
                        List<Double> monoMassList = new ArrayList<Double>();
                        monoMassList.add(mass);
                        peptide.addModification(location, new PTModification("","","", monoMassList, null));
                    }
                    DefaultPeptideIon peptideIon = new DefaultPeptideIon(peptide, psm.getCharge());
                    psm.setCalcMassToCharge(peptideIon.getMassOverCharge());
                }
            }

            psmList.add(psm);
        }

        return psmList;
    }
}
