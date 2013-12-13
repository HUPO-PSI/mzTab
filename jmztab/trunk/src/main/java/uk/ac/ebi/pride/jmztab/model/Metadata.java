package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.net.URL;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.CV;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class Metadata {
    private MZTabDescription tabDescription;
    private String title;
    private String description;
    private SortedMap<Integer, SplitList<Param>> sampleProcessingMap = new TreeMap<Integer, SplitList<Param>>();
    private SortedMap<Integer, Instrument> instrumentMap = new TreeMap<Integer, Instrument>();
    private SortedMap<Integer, Software> softwareMap = new TreeMap<Integer, Software>();
    private SplitList<Param> falseDiscoveryRate = new SplitList<Param>(BAR);
    private SortedMap<Integer, Publication> publicationMap = new TreeMap<Integer, Publication>();
    private SortedMap<Integer, Contact> contactMap = new TreeMap<Integer, Contact>();
    private List<URI> uriList = new ArrayList<URI>();
    private SortedMap<Integer, FixedMod> fixedModMap = new TreeMap<Integer, FixedMod>();
    private  SortedMap<Integer, VariableMod> variableModMap = new TreeMap<Integer, VariableMod>();
    private Param quantificationMethod;
    private Param proteinQuantificationUnit;
    private Param peptideQuantificationUnit;
    private Param smallMoleculeQuantificationUnit;
    private SortedMap<Integer, MsRun> msRunMap = new TreeMap<Integer, MsRun>();
    private List<Param> customList = new ArrayList<Param>();
    private SortedMap<Integer, Sample> sampleMap = new TreeMap<Integer, Sample>();
    private SortedMap<Integer, Assay> assayMap = new TreeMap<Integer, Assay>();
    private SortedMap<Integer, StudyVariable> studyVariableMap = new TreeMap<Integer, StudyVariable>();
    private SortedMap<Integer, CV> cvMap = new TreeMap<Integer, CV>();
    private List<ColUnit> proteinColUnitList = new ArrayList<ColUnit>();
    private List<ColUnit> peptideColUnitList = new ArrayList<ColUnit>();
    private List<ColUnit> psmColUnitList = new ArrayList<ColUnit>();
    private List<ColUnit> smallMoleculeColUnitList = new ArrayList<ColUnit>();

    /**
     * Create a metadata section with default {@link MZTabDescription}:
     * The mzTab-mode is Summary and mzTab-type is Identification,
     * and the mzTab-version is {@link MZTabDescription#default_version}
     */
    public Metadata() {
        this(new MZTabDescription(MZTabDescription.Mode.Summary, MZTabDescription.Type.Identification));
    }

    /**
     * Create a metadata section with special {@link MZTabDescription}
     *
     * @param tabDescription SHOULD NOT set null.
     */
    public Metadata(MZTabDescription tabDescription) {
        if (tabDescription == null) {
            throw new IllegalArgumentException("Should define mz-tab description first.");
        }

        this.tabDescription = tabDescription;
    }

    /**
     * Print metadata line prefix:
     *
     * MTD  ...
     */
    private StringBuilder printPrefix(StringBuilder sb) {
        sb.append(Section.Metadata.getPrefix()).append(TAB);

        return sb;
    }

    /**
     * Internal method used to output the indexed element map, e.g. {@link #sampleProcessingMap}, {@link #instrumentMap}
     * and so on. The output line structure like:
     * MTD   item[map.key]   map.value
     * For example:
     * MTD  sample_processing[1]	[SEP, SEP:00173, SDS PAGE, ]
     * MTD	sample_processing[2]	[SEP, SEP:00142, enzyme digestion, ]|[MS, MS:1001251, Trypsin, ]
     */
    protected StringBuilder printMap(Map<Integer, ?> map, String item, StringBuilder sb) {
        Object value;
        Iterator<Integer> it = map.keySet().iterator();
        Integer id;
        while (it.hasNext()) {
            id = it.next();
            value = map.get(id);
            if (value instanceof SplitList) {
                printPrefix(sb).append(item).append("[").append(id).append("]").append(TAB);
            }
            sb.append(value);

            // for sample processing, provide a new line for each item.
            if (item.equals(SAMPLE_PROCESSING.getName())) {
                sb.append(NEW_LINE);
            }
        }

        return sb;
    }

    /**
     * Internal method used to output the list object, e.g. {@link #uriList}, {@link #proteinColUnitList} and so on.
     * The output line structure like:
     * MTD   item[list.id + 1]   list.value
     * For example:
     * MTD	uri[1]	http://www.ebi.ac.uk/pride/url/to/experiment
     * MTD	uri[2]	http://proteomecentral.proteomexchange.org/cgi/GetDataset
     */
    protected StringBuilder printList(List<?> list, String item, StringBuilder sb) {
        for (int i = 0; i < list.size(); i++) {
            printPrefix(sb).append(item).append("[").append(i + 1).append("]").append(TAB).append(list.get(i)).append(NEW_LINE);
        }

        return sb;
    }

    /**
     * Print the metadata to a string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(tabDescription.toString());

        if (title != null) {
            printPrefix(sb).append(TITLE).append(TAB).append(title).append(NEW_LINE);
        }

        if (description != null) {
            printPrefix(sb).append(DESCRIPTION).append(TAB).append(description).append(NEW_LINE);
        }

        sb = printMap(sampleProcessingMap, SAMPLE_PROCESSING.toString(), sb);
        sb = printMap(instrumentMap, INSTRUMENT.toString(), sb);
        sb = printMap(softwareMap, SOFTWARE.toString(), sb);

        if (! falseDiscoveryRate.isEmpty()) {
            printPrefix(sb).append(FALSE_DISCOVERY_RATE).append(TAB).append(falseDiscoveryRate).append(NEW_LINE);
        }

        sb = printMap(publicationMap, PUBLICATION.toString(), sb);
        sb = printMap(contactMap, CONTACT.toString(), sb);

        printList(uriList, URI.toString(), sb);

        sb = printMap(fixedModMap, FIXED_MOD.toString(), sb);
        sb = printMap(variableModMap, VARIABLE_MOD.toString(), sb);

        if (quantificationMethod != null) {
            printPrefix(sb).append(QUANTIFICATION_METHOD).append(TAB).append(quantificationMethod).append(NEW_LINE);
        }
        if (proteinQuantificationUnit != null) {
            printPrefix(sb).append(PROTEIN).append(MINUS).append(PROTEIN_QUANTIFICATION_UNIT).append(TAB).append(proteinQuantificationUnit).append(NEW_LINE);
        }
        if (peptideQuantificationUnit != null) {
            printPrefix(sb).append(PEPTIDE).append(MINUS).append(PEPTIDE_QUANTIFICATION_UNIT).append(TAB).append(peptideQuantificationUnit).append(NEW_LINE);
        }
        if (smallMoleculeQuantificationUnit != null) {
            printPrefix(sb).append(SMALL_MOLECULE).append(MINUS).append(SMALL_MOLECULE_QUANTIFICATION_UNIT).append(TAB).append(smallMoleculeQuantificationUnit).append(NEW_LINE);
        }

        sb = printMap(msRunMap, MS_RUN.toString(), sb);
        sb = printMap(sampleMap, SAMPLE.toString(), sb);
        sb = printMap(assayMap, ASSAY.toString(), sb);
        sb = printMap(studyVariableMap, STUDY_VARIABLE.toString(), sb);
        sb = printMap(cvMap, CV.toString(), sb);

        for (ColUnit colUnit : proteinColUnitList) {
            printPrefix(sb).append(COLUNIT).append(MINUS).append(COLUNIT_PROTEIN);
            sb.append(TAB).append(colUnit).append(NEW_LINE);
        }
        for (ColUnit colUnit : peptideColUnitList) {
            printPrefix(sb).append(COLUNIT).append(MINUS).append(COLUNIT_PEPTIDE);
            sb.append(TAB).append(colUnit).append(NEW_LINE);
        }
        for (ColUnit colUnit : psmColUnitList) {
            printPrefix(sb).append(COLUNIT).append(MINUS).append(COLUNIT_PSM);
            sb.append(TAB).append(colUnit).append(NEW_LINE);
        }
        for (ColUnit colUnit : smallMoleculeColUnitList) {
            printPrefix(sb).append(COLUNIT).append(MINUS).append(COLUNIT_SMALL_MOLECULE);
            sb.append(TAB).append(colUnit).append(NEW_LINE);
        }

        printList(customList, CUSTOM.toString(), sb);

        return sb.toString();
    }

    /**
     * Get {@link MZTabDescription} object.
     */
    public MZTabDescription getTabDescription() {
        return tabDescription;
    }

    /**
     * Get mzTab-mode.
     */
    public MZTabDescription.Mode getMZTabMode() {
        return tabDescription.getMode();
    }

    /**
     * Get mzTab-version.
     */
    public String getMZTabVersion() {
        return tabDescription.getVersion();
    }

    /**
     * Get mzTab-type.
     */
    public MZTabDescription.Type getMZTabType() {
        return tabDescription.getType();
    }

    /**
     * Get mzTab-ID.
     */
    public String getMZTabID() {
        return tabDescription.getId();
    }

    /**
     * Set mzTab-ID.
     */
    public void setMZTabID(String id) {
        tabDescription.setId(id);
    }

    /**
     * Set mzTab-version.
     */
    public void setMZTabVersion(String version) {
        tabDescription.setVersion(version);
    }

    /**
     * Set mzTab-mode.
     */
    public void setMZTabMode(MZTabDescription.Mode mode) {
        tabDescription.setMode(mode);
    }

    /**
     * Set mzTab-type.
     */
    public void setMZTabType(MZTabDescription.Type type) {
        tabDescription.setType(type);
    }

    /**
     * Set {@link MZTabDescription} object.
     *
     * @param tabDescription SHOULD NOT set null.
     */
    public void setTabDescription(MZTabDescription tabDescription) {
        if (tabDescription == null) {
            throw new IllegalArgumentException("MZTabDescription should not set null!");
        }

        this.tabDescription = tabDescription;
    }

    /**
     * The file’s human readable title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The file’s human readable title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The file’s human readable description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The file’s human readable description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * A list of parameters describing a sample processing step. The order of the data_processing
     * items should reflect the order these processing steps were performed in. If multiple parameters
     * are given for a step these MUST be separated by a “|”.
     */
    public SortedMap<Integer, SplitList<Param>> getSampleProcessingMap() {
        return sampleProcessingMap;
    }

    /**
     * The instrument used in the experiment
     */
    public SortedMap<Integer, Instrument> getInstrumentMap() {
        return instrumentMap;
    }

    /**
     * Software used to analyze the data and obtain the reported results.
     */
    public SortedMap<Integer, Software> getSoftwareMap() {
        return softwareMap;
    }

    /**
     * The file’s false discovery rate(s) reported at the PSM, peptide, and/or protein level.
     * False Localization Rate (FLD) for the reporting of modifications can also be reported here.
     * Multiple parameters MUST be separated by “|”.
     */
    public SplitList<Param> getFalseDiscoveryRate() {
        return falseDiscoveryRate;
    }

    /**
     * The file’s false discovery rate(s) reported at the PSM, peptide, and/or protein level.
     * False Localization Rate (FLD) for the reporting of modifications can also be reported here.
     * Multiple parameters MUST be separated by “|”.
     */
    public void setFalseDiscoveryRate(SplitList<Param> falseDiscoveryRate) {
        if (falseDiscoveryRate == null) {
            falseDiscoveryRate = new SplitList<Param>(BAR);
        }

        this.falseDiscoveryRate = falseDiscoveryRate;
    }

    /**
     * A publication associated with this file. Several publications can be given by indicating the
     * number in the square brackets after “publication”. PubMed ids must be prefixed by “pubmed:”,
     * DOIs by “doi:”. Multiple identifiers MUST be separated by “|”.
     */
    public SortedMap<Integer, Publication> getPublicationMap() {
        return publicationMap;
    }

    /**
     * A contact list associated with this file.
     */
    public SortedMap<Integer, Contact> getContactMap() {
        return contactMap;
    }

    /**
     * A URI pointing to the file's source data.
     */
    public List<URI> getUriList() {
        return uriList;
    }

    /**
     * A couple of fixed modifications searched for.
     */
    public SortedMap<Integer, FixedMod> getFixedModMap() {
        return fixedModMap;
    }

    /**
     * A couple of variable modifications searched for.
     */
    public SortedMap<Integer, VariableMod> getVariableModMap() {
        return variableModMap;
    }

    /**
     * The quantification method used in the experiment reported in the file.
     */
    public Param getQuantificationMethod() {
        return quantificationMethod;
    }

    /**
     * Defines what type of units is reported in the protein quantification fields.
     */
    public Param getProteinQuantificationUnit() {
        return proteinQuantificationUnit;
    }

    /**
     * Defines what type of units is reported in the peptide quantification fields.
     */
    public Param getPeptideQuantificationUnit() {
        return peptideQuantificationUnit;
    }

    /**
     * Defines what type of units is reported in the small molecule quantification fields.
     */
    public Param getSmallMoleculeQuantificationUnit() {
        return smallMoleculeQuantificationUnit;
    }

    /**
     * Set the quantification method used in the experiment reported in the file.
     */
    public void setQuantificationMethod(Param quantificationMethod) {
        this.quantificationMethod = quantificationMethod;
    }

    /**
     * Defines what type of units is reported in the protein quantification fields.
     */
    public void setProteinQuantificationUnit(Param proteinQuantificationUnit) {
        this.proteinQuantificationUnit = proteinQuantificationUnit;
    }

    /**
     * Defines what type of units is reported in the peptide quantification fields.
     */
    public void setPeptideQuantificationUnit(Param peptideQuantificationUnit) {
        this.peptideQuantificationUnit = peptideQuantificationUnit;
    }

    /**
     * Defines what type of units is reported in the small molecule quantification fields.
     */
    public void setSmallMoleculeQuantificationUnit(Param smallMoleculeQuantificationUnit) {
        this.smallMoleculeQuantificationUnit = smallMoleculeQuantificationUnit;
    }

    /**
     * Get the external MS data files
     */
    public SortedMap<Integer, MsRun> getMsRunMap() {
        return msRunMap;
    }

    /**
     * Any additional parameters describing the analysis reported.
     */
    public List<Param> getCustomList() {
        return customList;
    }

    /**
     * A biological material that has been analysed, to which descriptors of species, cell/tissue type etc. can be attached.
     */
    public SortedMap<Integer, Sample> getSampleMap() {
        return sampleMap;
    }

    /**
     * The application of a measurement about the sample.
     */
    public SortedMap<Integer, Assay> getAssayMap() {
        return assayMap;
    }

    /**
     * The variables about which the final results of a study are reported.
     */
    public SortedMap<Integer, StudyVariable> getStudyVariableMap() {
        return studyVariableMap;
    }

    /**
     * Define the controlled vocabularies/ontologies used in the mzTab file.
     */
    public SortedMap<Integer, CV> getCvMap() {
        return cvMap;
    }

    /**
     * Defines the unit for the data reported in a column of the protein section.
     */
    public List<ColUnit> getProteinColUnitList() {
        return proteinColUnitList;
    }

    /**
     * Defines the unit for the data reported in a column of the peptide section.
     */
    public List<ColUnit> getPeptideColUnitList() {
        return peptideColUnitList;
    }

    /**
     * Defines the unit for the data reported in a column of the PSM section.
     */
    public List<ColUnit> getPsmColUnitList() {
        return psmColUnitList;
    }

    /**
     * Defines the unit for the data reported in a column of the small molecule section.
     */
    public List<ColUnit> getSmallMoleculeColUnitList() {
        return smallMoleculeColUnitList;
    }

    /**
     * Add a sample to metadata.
     *
     * @param sample SHOULD NOT set null.
     */
    public void addSample(Sample sample) {
        if (sample == null) {
            throw new IllegalArgumentException("Sample should not be null");
        }

        this.sampleMap.put(sample.getId(), sample);
    }

    /**
     * Add a sample[id]-species into sample.
     *
     * @param id SHOULD be positive integer.
     * @param species SHOULD NOT set null.
     */
    public void addSampleSpecies(Integer id, Param species) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (species == null) {
            throw new NullPointerException("Sample species should not set null");
        }

        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addSpecies(species);
            sampleMap.put(id, sample);
        } else {
            sample.addSpecies(species);
        }
    }

    /**
     * Add a sample[id]-tissue into sample.
     *
     * @param id SHOULD be positive integer.
     * @param tissue SHOULD NOT set null.
     */
    public void addSampleTissue(Integer id, Param tissue) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (tissue == null) {
            throw new NullPointerException("Sample tissue should not set null");
        }

        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addTissue(tissue);
            sampleMap.put(id, sample);
        } else {
            sample.addTissue(tissue);
        }
    }

    /**
     * Add a sample[id]-cell_type into sample.
     *
     * @param id SHOULD be positive integer.
     * @param cellType SHOULD NOT set null.
     */
    public void addSampleCellType(Integer id, Param cellType) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (cellType == null) {
            throw new NullPointerException("Sample cell type should not set null");
        }

        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addCellType(cellType);
            sampleMap.put(id, sample);
        } else {
            sample.addCellType(cellType);
        }
    }

    /**
     * Add a sample[id]-disease into sample.
     *
     * @param id SHOULD be positive integer.
     * @param disease SHOULD NOT set null.
     */
    public void addSampleDisease(Integer id, Param disease) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (disease == null) {
            throw new NullPointerException("Sample disease should not set null");
        }

        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addDisease(disease);
            sampleMap.put(id, sample);
        } else {
            sample.addDisease(disease);
        }
    }

    /**
     * Add a sample[id]-description into sample.
     *
     * @param id SHOULD be positive integer.
     */
    public void addSampleDescription(Integer id, String description) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }

        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.setDescription(description);
            sampleMap.put(id, sample);
        } else {
            sample.setDescription(description);
        }
    }

    /**
     * Add a sample[id]-custom into sample.
     *
     * @param id SHOULD be positive integer.
     * @param custom SHOULD NOT set null.
     */
    public void addSampleCustom(Integer id, Param custom) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (custom == null) {
            throw new NullPointerException("Sample custom parameter should not set null");
        }

        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addCustom(custom);
            sampleMap.put(id, sample);
        } else {
            sample.addCustom(custom);
        }
    }

    /**
     * Add a sample_processing[id]
     *
     * @param id SHOULD be positive integer.
     * @param sampleProcessing SHOULD NOT set null.
     */
    public void addSampleProcessing(Integer id, SplitList<Param> sampleProcessing) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (sampleProcessing == null) {
            throw new NullPointerException("Sample processing parameters should not set null");
        }

        sampleProcessing.setSplitChar(BAR);
        this.sampleProcessingMap.put(id, sampleProcessing);
    }

    /**
     * Add a processing parameter to sample_processing[id]
     *
     * @param id SHOULD be positive integer.
     * @param param SHOULD NOT set null.
     */
    public void addSampleProcessingParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample processing id should be great than 0!");
        }
        if (param == null) {
            throw new NullPointerException("Sample processing parameter should not set null");
        }

        SplitList<Param> sampleProcessing = sampleProcessingMap.get(id);
        if (sampleProcessing == null) {
            sampleProcessing = new SplitList<Param>(BAR);
            sampleProcessing.add(param);
            sampleProcessingMap.put(id, sampleProcessing);
        } else {
            sampleProcessing.add(param);
        }
    }

    /**
     * Add a instrument[id] to metadata.
     *
     * @param instrument SHOULD NOT set null.
     */
    public void addInstrument(Instrument instrument) {
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument should not be null");
        }

        instrumentMap.put(instrument.getId(), instrument);
    }

    /**
     * Add a parameter for instrument[id]-name
     *
     * @param id SHOULD be positive integer.
     * @param name is null, then not output instrument[id]-name
     */
    public void addInstrumentName(Integer id, Param name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Instrument id should be great than 0!");
        }

        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setName(name);
            instrumentMap.put(id, instrument);
        } else {
            instrument.setName(name);
        }
    }

    /**
     * Add a parameter for instrument[id]-source
     *
     * @param id SHOULD be positive integer.
     * @param source is null, then not output instrument[id]-source
     */
    public void addInstrumentSource(Integer id, Param source) {
        if (id <= 0) {
            throw new IllegalArgumentException("Instrument id should be great than 0!");
        }

        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setSource(source);
            instrumentMap.put(id, instrument);
        } else {
            instrument.setSource(source);
        }
    }

    /**
     * Add a parameter for instrument[id]-analyzer
     *
     * @param id SHOULD be positive integer.
     * @param analyzer is null, then not output instrument[id]-analyzer
     */
    public void addInstrumentAnalyzer(Integer id, Param analyzer) {
        if (id <= 0) {
            throw new IllegalArgumentException("Instrument id should be great than 0!");
        }

        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setAnalyzer(analyzer);
            instrumentMap.put(id, instrument);
        } else {
            instrument.setAnalyzer(analyzer);
        }
    }

    /**
     * Add a parameter for instrument[id]-detector
     *
     * @param id SHOULD be positive integer.
     * @param detector is null, then not output instrument[id]-detector
     */
    public void addInstrumentDetector(Integer id, Param detector) {
        if (id <= 0) {
            throw new IllegalArgumentException("Instrument id should be great than 0!");
        }

        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setDetector(detector);
            instrumentMap.put(id, instrument);
        } else {
            instrument.setDetector(detector);
        }
    }

    /**
     * Add a software to metadata.
     *
     * @param software SHOULD NOT set null
     */
    public void addSoftware(Software software) {
        if (software == null) {
            throw new IllegalArgumentException("Software should not be null");
        }

        this.softwareMap.put(software.getId(), software);
    }

    /**
     * Add a software[id] parameter.
     *
     * @param id SHOULD be positive integer.
     * @param param SHOULD NOT set null.
     */
    public void addSoftwareParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("Software id should be great than 0!");
        }
        if (param == null) {
            throw new NullPointerException("Software parameter should not set null");
        }

        Software software = softwareMap.get(id);
        if (software == null) {
            software = new Software(id);
            software.setParam(param);
            softwareMap.put(id, software);
        } else {
            software.setParam(param);
        }
    }

    /**
     * Add a software[id]-setting.
     *
     * @param id SHOULD be positive integer.
     * @param setting SHOULD NOT empty.
     */
    public void addSoftwareSetting(Integer id, String setting) {
        if (id <= 0) {
            throw new IllegalArgumentException("Software id should be great than 0!");
        }
        if (setting == null) {
            throw new NullPointerException("Software setting should not set null");
        }

        Software software = softwareMap.get(id);
        if (software == null) {
            software = new Software(id);
            software.addSetting(setting);
            softwareMap.put(id, software);
        } else  {
            software.addSetting(setting);
        }
    }

    /**
     * Add a false_discovery_rate parameter to metadata.
     * @param param SHOULD NOT set null.
     */
    public void addFalseDiscoveryRateParam(Param param) {
        if (param == null) {
            throw new NullPointerException("False discovery rate parameter should not set null");
        }

        this.falseDiscoveryRate.add(param);
    }

    /**
     * Add a publiction to metadata.
     * @param publication SHOULD NOT set null.
     */
    public void addPublication(Publication publication) {
        if (publication == null) {
            throw new IllegalArgumentException("Publication should not be null");
        }

        this.publicationMap.put(publication.getId(), publication);
    }

    /**
     * Add a publication item to metadata.
     *
     * @param id SHOULD be positive integer.
     * @param type SHOULD NOT set null.
     * @param accession SHOULD NOT set empty.
     */
    public void addPublicationItem(Integer id, PublicationItem.Type type, String accession) {
        if (id <= 0) {
            throw new IllegalArgumentException("Publication id should be great than 0!");
        }
        if (type == null) {
            throw new NullPointerException("Publication type should not set null");
        }
        if (MZTabUtils.isEmpty(accession)) {
            throw new IllegalArgumentException("Publication accession should not set empty.");
        }

        Publication publication = publicationMap.get(id);
        if (publication == null) {
            publication = new Publication(id);
            publication.addPublicationItem(new PublicationItem(type, accession));
            publicationMap.put(id, publication);
        } else {
            publication.addPublicationItem(new PublicationItem(type, accession));
        }
    }

    /**
     * Add a couple of publication items into publication[id].
     *
     * @param id SHOULD be positive integer.
     * @param items SHOULD NOT set null.
     */
    public void addPublicationItems(Integer id, Collection<PublicationItem> items) {
        if (id <= 0) {
            throw new IllegalArgumentException("Publication id should be great than 0!");
        }
        if (items == null) {
            throw new NullPointerException("Publication items should not set null");
        }

        Publication publication = publicationMap.get(id);
        if (publication == null) {
            publication = new Publication(id);
            publication.addPublicationItems(items);
            publicationMap.put(id, publication);
        } else {
            publication.addPublicationItems(items);
        }
    }

    /**
     * Add a contact into metadata.
     *
     * @param contact SHOULD NOT set null.
     */
    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact should not be null");
        }

        this.contactMap.put(contact.getId(), contact);
    }

    /**
     * Add contact[id]-name
     *
     * @param id SHOULD be positive integer.
     * @param name SHOULD NOT set empty.
     */
    public void addContactName(Integer id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Contact id should be great than 0!");
        }
        if (MZTabUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Contact name should not set empty.");
        }

        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setName(name);
            contactMap.put(id, contact);
        } else {
            contact.setName(name);
        }
    }

    /**
     * Add contact[id]-affiliation
     *
     * @param id SHOULD be positive integer.
     * @param affiliation SHOULD NOT set empty.
     */
    public void addContactAffiliation(Integer id, String affiliation) {
        if (id <= 0) {
            throw new IllegalArgumentException("Contact id should be great than 0!");
        }
        if (MZTabUtils.isEmpty(affiliation)) {
            throw new IllegalArgumentException("Contact affiliation should not set empty.");
        }

        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setAffiliation(affiliation);
            contactMap.put(id, contact);
        } else {
            contact.setAffiliation(affiliation);
        }
    }

    /**
     * Add contact[id]-email
     *
     * @param id SHOULD be positive integer.
     * @param email SHOULD NOT set empty.
     */
    public void addContactEmail(Integer id, String email) {
        if (id <= 0) {
            throw new IllegalArgumentException("Contact id should be great than 0!");
        }
        if (MZTabUtils.isEmpty(email)) {
            throw new IllegalArgumentException("Contact email should not set empty.");
        }

        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setEmail(email);
            contactMap.put(id, contact);
        } else {
            contact.setEmail(email);
        }
    }

    /**
     * Add uri into metadata.
     * @param uri SHOULD NOT set null.
     */
    public void addUri(URI uri) {
        if (uri == null) {
            throw new NullPointerException("url should not set null!");
        }

        this.uriList.add(uri);
    }

    /**
     * Add fixed_mod[id] into metadata.
     *
     * @param mod SHOULD NOT set null.
     */
    public void addFixedMod(FixedMod mod) {
        if (mod == null) {
            throw new IllegalArgumentException("FixedMod should not be null");
        }

        this.fixedModMap.put(mod.getId(), mod);
    }

    /**
     * Add fixed_mod[id] parameter into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param param SHOULD NOT set null.
     */
    public void addFixedModParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("fixed_mod id should be great than 0!");
        }
        if (param == null) {
            throw new NullPointerException("fixed_mod parameter should not set null.");
        }

        FixedMod mod = fixedModMap.get(id);
        if (mod == null) {
            mod = new FixedMod(id);
            mod.setParam(param);
            fixedModMap.put(id, mod);
        } else {
            mod.setParam(param);
        }
    }

    /**
     * Add fixed_mod[id]-site into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param site SHOULD NOT set empty.
     */
    public void addFixedModSite(Integer id, String site) {
        if (id <= 0) {
            throw new IllegalArgumentException("fixed_mod id should be great than 0!");
        }
        if (MZTabUtils.isEmpty(site)) {
            throw new IllegalArgumentException("fixed_mod site should not set empty.");
        }

        FixedMod mod = fixedModMap.get(id);
        if (mod == null) {
            mod = new FixedMod(id);
            mod.setSite(site);
            fixedModMap.put(id, mod);
        } else  {
            mod.setSite(site);
        }
    }

    /**
     * Add fixed_mod[id]-position into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param position SHOULD NOT set empty.
     */
    public void addFixedModPosition(Integer id, String position) {
        if (id <= 0) {
            throw new IllegalArgumentException("fixed_mod id should be great than 0!");
        }
        if (MZTabUtils.isEmpty(position)) {
            throw new IllegalArgumentException("fixed_mod position should not set empty.");
        }

        FixedMod mod = fixedModMap.get(id);
        if (mod == null) {
            mod = new FixedMod(id);
            mod.setPosition(position);
            fixedModMap.put(id, mod);
        } else  {
            mod.setPosition(position);
        }
    }

    /**
     * Add variable_mod[id] into metadata.
     *
     * @param mod SHOULD NOT set null.
     */
    public void addVariableMod(VariableMod mod) {
        if (mod == null) {
            throw new IllegalArgumentException("VariableMod should not be null");
        }

        this.variableModMap.put(mod.getId(), mod);
    }

    /**
     * Add variable_mod[id] parameter into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param param SHOULD NOT set null.
     */
    public void addVariableModParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("variable_mod id should be great than 0!");
        }
        if (param == null) {
            throw new NullPointerException("variable_mod parameter should not set null.");
        }

        VariableMod mod = variableModMap.get(id);
        if (mod == null) {
            mod = new VariableMod(id);
            mod.setParam(param);
            variableModMap.put(id, mod);
        } else {
            mod.setParam(param);
        }
    }

    /**
     * Add variable_mod[id]-site into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param site SHOULD NOT set empty.
     */
    public void addVariableModSite(Integer id, String site) {
        if (id <= 0) {
            throw new IllegalArgumentException("variable_mod id should be great than 0!");
        }
        if (MZTabUtils.isEmpty(site)) {
            throw new IllegalArgumentException("variable_mod site should not set empty.");
        }

        VariableMod mod = variableModMap.get(id);
        if (mod == null) {
            mod = new VariableMod(id);
            mod.setSite(site);
            variableModMap.put(id, mod);
        } else  {
            mod.setSite(site);
        }
    }

    /**
     * Add variable_mod[id]-position into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param position SHOULD NOT set empty.
     */
    public void addVariableModPosition(Integer id, String position) {
        if (id <= 0) {
            throw new IllegalArgumentException("variable_mod id should be great than 0!");
        }
        if (MZTabUtils.isEmpty(position)) {
            throw new IllegalArgumentException("variable_mod position should not set empty.");
        }

        VariableMod mod = variableModMap.get(id);
        if (mod == null) {
            mod = new VariableMod(id);
            mod.setPosition(position);
            variableModMap.put(id, mod);
        } else  {
            mod.setPosition(position);
        }
    }

    /**
     * Add a ms_run[id] into metadata.
     *
     * @param msRun SHOULD NOT set null.
     */
    public void addMsRun(MsRun msRun) {
        if (msRun == null) {
            throw new IllegalArgumentException("MsRun should not be null");
        }

        msRunMap.put(msRun.getId(), msRun);
    }

    /**
     * Add ms_run[id]-format into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param format SHOULD NOT set null.
     */
    public void addMsRunFormat(Integer id, Param format) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (format == null) {
            throw new NullPointerException("ms_run format should not set null.");
        }

        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setFormat(format);
            msRunMap.put(id, msRun);
        } else {
            msRun.setFormat(format);
        }
    }

    /**
     * Add ms_run[id]-location into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param location SHOULD NOT set null.
     */
    public void addMsRunLocation(Integer id, URL location) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (location == null) {
            throw new NullPointerException("ms_run location should not set null.");
        }

        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setLocation(location);
            msRunMap.put(id, msRun);
        } else {
            msRun.setLocation(location);
        }
    }

    /**
     * Add ms_run[id]-id_format into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param idFormat SHOULD NOT set null.
     */
    public void addMsRunIdFormat(Integer id, Param idFormat) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (idFormat == null) {
            throw new NullPointerException("ms_run id_format should not set null.");
        }

        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setIdFormat(idFormat);
            msRunMap.put(id, msRun);
        } else {
            msRun.setIdFormat(idFormat);
        }
    }

    /**
     * Add ms_run[id]-fragmentation_method into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param fragmentationMethod SHOULD NOT set null.
     */
    public void addMsRunFragmentationMethod(Integer id, Param fragmentationMethod) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (fragmentationMethod == null) {
            throw new NullPointerException("ms_run fragmentation_method should not set null.");
        }

        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setFragmentationMethod(fragmentationMethod);
            msRunMap.put(id, msRun);
        } else {
            msRun.setFragmentationMethod(fragmentationMethod);
        }
    }

    /**
     * Add a custom parameter into metadata.
     *
     * @param custom SHOULD NOT set null.
     */
    public void addCustom(Param custom) {
        if (custom == null) {
            throw new NullPointerException("custom parameter should not set null.");
        }

        this.customList.add(custom);
    }

    /**
     * Add a assay into metadata.
     *
     * @param assay SHOULD NOT set null.
     */
    public void addAssay(Assay assay) {
        if (assay == null) {
            throw new IllegalArgumentException("Assay should not be null");
        }

        assayMap.put(assay.getId(), assay);
    }

    /**
     * Add assay[id]-quantification_reagent into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param quantificationReagent SHOULD NOT set null.
     */
    public void addAssayQuantificationReagent(Integer id, Param quantificationReagent) {
        if (id <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (quantificationReagent == null) {
            throw new NullPointerException("assay quantification_reagent should not set null.");
        }

        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setQuantificationReagent(quantificationReagent);
            assayMap.put(id, assay);
        } else {
            assay.setQuantificationReagent(quantificationReagent);
        }
    }

    /**
     * Add assay[id]-sample_ref into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param sample SHOULD NOT set null.
     */
    public void addAssaySample(Integer id, Sample sample) {
        if (id <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (sample == null) {
            throw new NullPointerException("assay sample_ref should not set null.");
        }

        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setSample(sample);
            assayMap.put(id, assay);
        } else {
            assay.setSample(sample);
        }
    }

    /**
     * Add assay[id]-ms_run_ref into metadata.
     *
     * @param id SHOULD be positive integer.
     * @param msRun SHOULD NOT set null.
     */
    public void addAssayMsRun(Integer id, MsRun msRun) {
        if (id <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (msRun == null) {
            throw new NullPointerException("assay ms_run_ref should not set null.");
        }

        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setMsRun(msRun);
            assayMap.put(id, assay);
        } else {
            assay.setMsRun(msRun);
        }
    }

    /**
     * Add assay[assayId]-quantification_mod[1-n] into metadata.
     *
     * @param assayId SHOULD be positive integer.
     * @param mod SHOULD NOT set null.
     */
    public void addAssayQuantificationMod(Integer assayId, AssayQuantificationMod mod) {
        if (assayId <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (mod == null) {
            throw new NullPointerException("assay quantification_mod should not set null.");
        }

        Assay assay = assayMap.get(assayId);
        if (assay == null) {
            assay = new Assay(assayId);
            assay.addQuantificationMod(mod);
            assayMap.put(assayId, assay);
        } else {
            assay.addQuantificationMod(mod);
        }
    }

    /**
     * Add assay[assayId]-quantification_mod[quanModId] into metadata.
     *
     * @param assayId SHOULD be positive integer.
     * @param quanModId SHOULD be positive integer.
     * @param param SHOULD NOT set null.
     */
    public void addAssayQuantificationModParam(Integer assayId, Integer quanModId, Param param) {
        if (assayId <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (quanModId <= 0) {
            throw new IllegalArgumentException("quantification_mod id should be great than 0!");
        }
        if (param == null) {
            throw new NullPointerException("assay quantification_mod parameter should not set null.");
        }

        Assay assay = assayMap.get(assayId);
        if (assay == null) {
            assay = new Assay(assayId);
            assay.addQuantificationModParam(quanModId, param);
            assayMap.put(assayId, assay);
        } else {
            assay.addQuantificationModParam(quanModId, param);
        }
    }

    /**
     * Add assay[assayId]-quantification_mod[quanModId]-site into metadata.
     *
     * @param assayId SHOULD be positive integer.
     * @param quanModId SHOULD be positive integer.
     */
    public void addAssayQuantificationModSite(Integer assayId, Integer quanModId, String site) {
        if (assayId <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (quanModId <= 0) {
            throw new IllegalArgumentException("quantification_mod id should be great than 0!");
        }

        Assay assay = assayMap.get(assayId);
        if (assay == null) {
            assay = new Assay(assayId);
            assay.addQuantificationModSite(quanModId, site);
            assayMap.put(assayId, assay);
        } else {
            assay.addQuantificationModSite(quanModId, site);
        }
    }

    /**
     * Add assay[assayId]-quantification_mod[quanModId]-site into metadata.
     *
     * @param assayId SHOULD be positive integer.
     * @param quanModId SHOULD be positive integer.
     */
    public void addAssayQuantificationModPosition(Integer assayId, Integer quanModId, String position) {
        if (assayId <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (quanModId <= 0) {
            throw new IllegalArgumentException("quantification_mod id should be great than 0!");
        }

        Assay assay = assayMap.get(assayId);
        if (assay == null) {
            assay = new Assay(assayId);
            assay.addQuantificationModPosition(quanModId, position);
            assayMap.put(assayId, assay);
        } else {
            assay.addQuantificationModPosition(quanModId, position);
        }
    }

    /**
     * Add a study variable into metadata.
     *
     * @param studyVariable SHOULD NOT set null.
     */
    public void addStudyVariable(StudyVariable studyVariable) {
        if (studyVariable == null) {
            throw new IllegalArgumentException("StudyVariable should not be null");
        }

        studyVariableMap.put(studyVariable.getId(), studyVariable);
    }

    /**
     * Add a study_variable[id]-assay_ref.
     *
     * @param id SHOULD be positive integer.
     * @param assay SHOULD NOT set null.
     */
    public void addStudyVariableAssay(Integer id, Assay assay) {
        if (id <= 0) {
            throw new IllegalArgumentException("study variable id should be great than 0!");
        }
        if (assay == null) {
            throw new NullPointerException("study_variable[n]-assay_ref should not set null.");
        }

        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
            studyVariable.addAssay(assay);
            studyVariableMap.put(id, studyVariable);
        } else {
            studyVariable.addAssay(assay);
        }
    }

    /**
     * Add a study_variable[id]-sample_ref.
     *
     * @param id SHOULD be positive integer.
     * @param sample SHOULD NOT set null.
     */
    public void addStudyVariableSample(Integer id, Sample sample) {
        if (id <= 0) {
            throw new IllegalArgumentException("study variable id should be great than 0!");
        }
        if (sample == null) {
            throw new NullPointerException("study_variable[n]-sample_ref should not set null.");
        }

        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
            studyVariable.addSample(sample);
            studyVariableMap.put(id, studyVariable);
        } else {
            studyVariable.addSample(sample);
        }
    }

    /**
     * Add a study_variable[id]-sample_ref.
     *
     * @param id SHOULD be positive integer.
     */
    public void addStudyVariableDescription(Integer id, String description) {
        if (id <= 0) {
            throw new IllegalArgumentException("study variable id should be great than 0!");
        }

        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
        }

        studyVariable.setDescription(description);
        studyVariableMap.put(id, studyVariable);
    }

    /**
     * Add a controlled vocabularies/ontologies into metadata.
     *
     * @param cv SHOULD NOT set null.
     */
    public void addCV(CV cv) {
        if (cv == null) {
            throw new NullPointerException("Controlled vocabularies/ontologies can not set null!");
        }

        cvMap.put(cv.getId(), cv);
    }

    /**
     * Add a cv[id]-label.
     *
     * @param id SHOULD be positive integer.
     */
    public void addCVLabel(Integer id, String label) {
        if (id <= 0) {
            throw new IllegalArgumentException("controlled vocabularies id should be great than 0!");
        }

        CV cv = cvMap.get(id);
        if (cv == null) {
            cv = new CV(id);
        }

        cv.setLabel(label);
        cvMap.put(id, cv);
    }

    /**
     * Add a cv[id]-full_name.
     *
     * @param id SHOULD be positive integer.
     */
    public void addCVFullName(Integer id, String fullName) {
        if (id <= 0) {
            throw new IllegalArgumentException("controlled vocabularies id should be great than 0!");
        }

        CV cv = cvMap.get(id);
        if (cv == null) {
            cv = new CV(id);
        }

        cv.setFullName(fullName);
        cvMap.put(id, cv);
    }

    /**
     * Add a cv[id]-version.
     *
     * @param id SHOULD be positive integer.
     */
    public void addCVVersion(Integer id, String version) {
        if (id <= 0) {
            throw new IllegalArgumentException("controlled vocabularies id should be great than 0!");
        }

        CV cv = cvMap.get(id);
        if (cv == null) {
            cv = new CV(id);
        }

        cv.setVersion(version);
        cvMap.put(id, cv);
    }

    /**
     * Add a cv[id]-url.
     *
     * @param id SHOULD be positive integer.
     */
    public void addCVURL(Integer id, String url) {
        if (id <= 0) {
            throw new IllegalArgumentException("controlled vocabularies id should be great than 0!");
        }

        CV cv = cvMap.get(id);
        if (cv == null) {
            cv = new CV(id);
        }

        cv.setUrl(url);
        cvMap.put(id, cv);
    }

    /**
     * Defines the unit for the data reported in a column of the protein section.
     *
     * @param column SHOULD NOT set null
     * @param param SHOULD NOT set null
     */
    public void addProteinColUnit(MZTabColumn column, Param param) {
        this.proteinColUnitList.add(new ColUnit(column, param));
    }

    /**
     * Defines the unit for the data reported in a column of the peptide section.
     *
     * @param column SHOULD NOT set null
     * @param param SHOULD NOT set null
     */
    public void addPeptideColUnit(MZTabColumn column, Param param) {
        this.peptideColUnitList.add(new ColUnit(column, param));
    }

    /**
     * Defines the unit for the data reported in a column of the PSM section.
     *
     * @param column SHOULD NOT set null
     * @param param SHOULD NOT set null
     */
    public void addPSMColUnit(MZTabColumn column, Param param) {
        this.psmColUnitList.add(new ColUnit(column, param));
    }

    /**
     * Defines the unit for the data reported in a column of the small molecule section.
     *
     * @param column SHOULD NOT set null
     * @param param SHOULD NOT set null
     */
    public void addSmallMoleculeColUnit(MZTabColumn column, Param param) {
        this.smallMoleculeColUnitList.add(new ColUnit(column, param));
    }
}
