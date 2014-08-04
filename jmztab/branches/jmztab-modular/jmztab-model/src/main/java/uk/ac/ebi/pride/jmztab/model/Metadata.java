package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.net.URL;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.CV;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * The metadata section can provide additional information about the dataset(s) reported in the mzTab file.
 *
 * @author qingwei
 * @since 23/05/13
 */
public class Metadata {
    private MZTabDescription tabDescription;
    private String title;
    private String description;
    private SortedMap<Integer, SplitList<Param>> sampleProcessingMap = new TreeMap<Integer, SplitList<Param>>();
    private SortedMap<Integer, Instrument> instrumentMap = new TreeMap<Integer, Instrument>();
    private SortedMap<Integer, Software> softwareMap = new TreeMap<Integer, Software>();
    private SortedMap<Integer, ProteinSearchEngineScore> proteinSearchEngineScoreMap = new TreeMap<Integer, ProteinSearchEngineScore>();
    private SortedMap<Integer, PeptideSearchEngineScore> peptideSearchEngineScoreMap = new TreeMap<Integer, PeptideSearchEngineScore>();
    private SortedMap<Integer, PSMSearchEngineScore> psmSearchEngineScoreMap = new TreeMap<Integer, PSMSearchEngineScore>();
    private SortedMap<Integer, SmallMoleculeSearchEngineScore> smallMoleculeSearchEngineScoreMap = new TreeMap<Integer, SmallMoleculeSearchEngineScore>();

    private SplitList<Param> falseDiscoveryRate = new SplitList<Param>(BAR);
    private SortedMap<Integer, Publication> publicationMap = new TreeMap<Integer, Publication>();
    private SortedMap<Integer, Contact> contactMap = new TreeMap<Integer, Contact>();
    private List<URI> uriList = new ArrayList<URI>();
    private SortedMap<Integer, FixedMod> fixedModMap = new TreeMap<Integer, FixedMod>();
    private SortedMap<Integer, VariableMod> variableModMap = new TreeMap<Integer, VariableMod>();
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
    private Map<String, String> colUnitMap = new HashMap<String, String>();

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
        sb = printMap(proteinSearchEngineScoreMap, PROTEIN_SEARCH_ENGINE_SCORE.toString(), sb);
        sb = printMap(peptideSearchEngineScoreMap, PEPTIDE_SEARCH_ENGINE_SCORE.toString(), sb);
        sb = printMap(psmSearchEngineScoreMap, PSM_SEARCH_ENGINE_SCORE.toString(), sb);
        sb = printMap(smallMoleculeSearchEngineScoreMap, SMALLMOLECULE_SEARCH_ENGINE_SCORE.toString(), sb);


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
     * Get {@link MZTabDescription} object, a couple of fields which start with "mzTab-" in metadata section.
     */
    public MZTabDescription getTabDescription() {
        return tabDescription;
    }

    /**
     * Get the mzTab-mode. The results included in an mzTab file can be reported in 2 ways:
     * 'Complete' (when results for each assay/replicate are included) and 'Summary',
     * when only the most representative results are reported.
     */
    public MZTabDescription.Mode getMZTabMode() {
        return tabDescription.getMode();
    }

    /**
     * Get the version of the mzTab file.
     */
    public String getMZTabVersion() {
        return tabDescription.getVersion();
    }

    /**
     * Get mzTab-type value. The results included in an mzTab file MUST be flagged as
     * 'Identification' or 'Quantification'  - the latter encompassing approaches
     * that are quantification only or quantification and identification.
     */
    public MZTabDescription.Type getMZTabType() {
        return tabDescription.getType();
    }

    /**
     * Get the mzTab-ID of the mzTab file
     */
    public String getMZTabID() {
        return tabDescription.getId();
    }

    /**
     * Set the mzTab-ID of the mzTab file.
     *
     * @param id SHOULD NOT set empty
     */
    public void setMZTabID(String id) {
        tabDescription.setId(id);
    }

    /**
     * Set the version of the mzTab file.
     * @param version SHOULD NOT be empty.
     */
    public void setMZTabVersion(String version) {
        tabDescription.setVersion(version);
    }

    /**
     * Set mzTab-mode. Set the mzTab-mode. The results included in an mzTab file can be reported in 2 ways:
     * 'Complete' (when results for each assay/replicate are included) and 'Summary',
     * when only the most representative results are reported.
     *
     * @param mode SHOULD NOT be null.
     */
    public void setMZTabMode(MZTabDescription.Mode mode) {
        tabDescription.setMode(mode);
    }

    /**
     * Set mzTab-type value. The results included in an mzTab file MUST be flagged as
     * 'Identification' or 'Quantification'  - the latter encompassing approaches
     * that are quantification only or quantification and identification.
     *
     * @param type SHOULD NOT be null.
     */
    public void setMZTabType(MZTabDescription.Type type) {
        tabDescription.setType(type);
    }

    /**
     * Set {@link MZTabDescription} object, which start with "mzTab-" in metadata section.
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
     * Get the file's human readable title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the file's human readable title.
     *
     * @param title SHOULD NOT set empty.
     */
    public void setTitle(String title) {
        if (isEmpty(title)) {
            throw new IllegalArgumentException("title should not set empty!");
        }

        this.title = title;
    }

    /**
     * The file's human readable description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The file's human readable description.
     *
     * @param description SHOULD NOT set empty.
     */
    public void setDescription(String description) {
        if (isEmpty(description)) {
            throw new IllegalArgumentException("description should not set empty!");
        }

        this.description = description;
    }

    /**
     * A list of parameters describing a sample processing step. The order of the data_processing
     * items should reflect the order these processing steps were performed in. If multiple parameters
     * are given for a step these MUST be separated by a "|".
     */
    public SortedMap<Integer, SplitList<Param>> getSampleProcessingMap() {
        return sampleProcessingMap;
    }

    /**
     * A list of parameters describing a sample processing step. The order of the data_processing
     * items should reflect the order these processing steps were performed in. If multiple parameters
     * are given for a step these MUST be separated by a "|".
     */
    public void setSampleProcessingMap(SortedMap<Integer, SplitList<Param>> sampleProcessingMap) {
        this.sampleProcessingMap = sampleProcessingMap;
    }

    /**
     * Get a sorted instrument map used in the experiment, which order by indexed
     */
    public SortedMap<Integer, Instrument> getInstrumentMap() {
        return instrumentMap;
    }

    /**
     * Get a sorted instrument map used in the experiment, which order by indexed
     */
    public void setInstrumentMap(SortedMap<Integer, Instrument> instrumentMap) {
        this.instrumentMap = instrumentMap;
    }

    /**
     * Get a sorted software map used in the analyze the data and obtain the reported results, which order by indexed
     */
    public SortedMap<Integer, Software> getSoftwareMap() {
        return softwareMap;
    }

    /**
     * Set a sorted software map used in the analyze the data and obtain the reported results, which order by indexed
     */
    public void setSoftwareMap(SortedMap<Integer, Software> softwareMap) {
        this.softwareMap = softwareMap;
    }

    /**
     * Get a sorted protein search engine score map, which order by indexed
     */
    public SortedMap<Integer, ProteinSearchEngineScore> getProteinSearchEngineScoreMap() {
        return proteinSearchEngineScoreMap;
    }

    /**
     * Set a sorted protein search engine score map, which order by indexed
     */
    public void setProteinSearchEngineScoreMap(SortedMap<Integer, ProteinSearchEngineScore> proteinSearchEngineScoreMap) {
        this.proteinSearchEngineScoreMap = proteinSearchEngineScoreMap;
    }

    /**
     * Get a sorted peptide search engine score map, which order by indexed
     */
    public SortedMap<Integer, PeptideSearchEngineScore> getPeptideSearchEngineScoreMap() {
        return peptideSearchEngineScoreMap;
    }

    /**
     * Set a sorted peptide search engine score map, which order by indexed
     */
    public void setPeptideSearchEngineScoreMap(SortedMap<Integer, PeptideSearchEngineScore> peptideSearchEngineScoreMap) {
        this.peptideSearchEngineScoreMap = peptideSearchEngineScoreMap;
    }

    /**
     * Get a sorted psm search engine score map, which order by indexed
     */
    public SortedMap<Integer, PSMSearchEngineScore> getPsmSearchEngineScoreMap() {
        return psmSearchEngineScoreMap;
    }

    /**
     * Set a sorted psm search engine score map, which order by indexed
     */
    public void setPsmSearchEngineScoreMap(SortedMap<Integer, PSMSearchEngineScore> psmSearchEngineScoreMap) {
        this.psmSearchEngineScoreMap = psmSearchEngineScoreMap;
    }

    /**
     * Get a sorted small molecule search engine score map, which order by indexed
     */
    public SortedMap<Integer, SmallMoleculeSearchEngineScore> getSmallMoleculeSearchEngineScoreMap() {
        return smallMoleculeSearchEngineScoreMap;
    }

    /**
     * Set a sorted small molecule search engine score map, which order by indexed
     */
    public void setSmallMoleculeSearchEngineScoreMap(SortedMap<Integer, SmallMoleculeSearchEngineScore> smallMoleculeSearchEngineScoreMap) {
        this.smallMoleculeSearchEngineScoreMap = smallMoleculeSearchEngineScoreMap;
    }

    /**
     * Get the file's false discovery rate(s) reported at the PSM, peptide, and/or protein level.
     * False Localization Rate (FLD) for the reporting of modifications can also be reported here.
     * Multiple parameters MUST be separated by "|".
     */
    public SplitList<Param> getFalseDiscoveryRate() {
        return falseDiscoveryRate;
    }

    /**
     * Set the file's false discovery rate(s) reported at the PSM, peptide, and/or protein level.
     * False Localization Rate (FLD) for the reporting of modifications can also be reported here.
     * Multiple parameters MUST be separated by "|".
     */
    public void setFalseDiscoveryRate(SplitList<Param> falseDiscoveryRate) {
        if (falseDiscoveryRate == null) {
            falseDiscoveryRate = new SplitList<Param>(BAR);
        }

        this.falseDiscoveryRate = falseDiscoveryRate;
    }

    /**
     * Get publication associated with this file. Several publications can be given by indicating the
     * number in the square brackets after "publication". PubMed ids must be prefixed by "pubmed:",
     * DOIs by "doi:". Multiple identifiers MUST be separated by "|".
     */
    public SortedMap<Integer, Publication> getPublicationMap() {
        return publicationMap;
    }

    /**
     * Set publication associated with this file. Several publications can be given by indicating the
     * number in the square brackets after "publication". PubMed ids must be prefixed by "pubmed:",
     * DOIs by "doi:". Multiple identifiers MUST be separated by "|".
     */
    public void setPublicationMap(SortedMap<Integer, Publication> publicationMap) {
        this.publicationMap = publicationMap;
    }

    /**
     * Get contact list associated with this file.
     */
    public SortedMap<Integer, Contact> getContactMap() {
        return contactMap;
    }

    /**
     * Set contact list associated with this file.
     */
    public void setContactMap(SortedMap<Integer, Contact> contactMap) {
        this.contactMap = contactMap;
    }

    /**
     * Get a couple of URIs pointing to the file's source data.
     */
    public List<URI> getUriList() {
        return uriList;
    }

    /**
     * Set a couple of URIs pointing to the file's source data.
     */
    public void setUriList(List<URI> uriList) {
        this.uriList = uriList;
    }

    /**
     * Get a couple of fixed modifications searched for.
     * Fixed Modification used to identify peptides and proteins of the mzTab file (e.g. carbamidomethylation,
     * oxidation, labels/tags).
     */
    public SortedMap<Integer, FixedMod> getFixedModMap() {
        return fixedModMap;
    }

    /**
     * Set a couple of fixed modifications searched for.
     * Fixed Modification used to identify peptides and proteins of the mzTab file (e.g. carbamidomethylation,
     * oxidation, labels/tags).
     */
    public void setFixedModMap(SortedMap<Integer, FixedMod> fixedModMap) {
        this.fixedModMap = fixedModMap;
    }

    /**
     * Get a couple of variable modifications searched for.
     *
     * Variable Modification used to identify peptides and proteins of the mzTab file (e.g. carbamidomethylation,
     * oxidation, labels/tags).
     */
    public SortedMap<Integer, VariableMod> getVariableModMap() {
        return variableModMap;
    }

    /**
     * Set a couple of variable modifications searched for.
     *
     * Variable Modification used to identify peptides and proteins of the mzTab file (e.g. carbamidomethylation,
     * oxidation, labels/tags).
     */
    public void setVariableModMap(SortedMap<Integer, VariableMod> variableModMap) {
        this.variableModMap = variableModMap;
    }

    /**
     * Get the quantification method parameter used in the experiment reported in the file.
     */
    public Param getQuantificationMethod() {
        return quantificationMethod;
    }

    /**
     * Get the type of units is reported in the protein quantification fields.
     */
    public Param getProteinQuantificationUnit() {
        return proteinQuantificationUnit;
    }

    /**
     * Get the type of units is reported in the peptide quantification fields.
     */
    public Param getPeptideQuantificationUnit() {
        return peptideQuantificationUnit;
    }

    /**
     * Get the type of units is reported in the small molecule quantification fields.
     */
    public Param getSmallMoleculeQuantificationUnit() {
        return smallMoleculeQuantificationUnit;
    }

    /**
     * Set the quantification method used in the experiment reported in the file.
     *
     * @param quantificationMethod if null, system will not print quantification_method in mzTab file.
     */
    public void setQuantificationMethod(Param quantificationMethod) {
        this.quantificationMethod = quantificationMethod;
    }

    /**
     * Set the type of units is reported in the protein quantification fields.
     *
     * @param proteinQuantificationUnit if null, system will not print protein-quantification_unit in mzTab file.
     */
    public void setProteinQuantificationUnit(Param proteinQuantificationUnit) {
        this.proteinQuantificationUnit = proteinQuantificationUnit;
    }

    /**
     * Set the type of units is reported in the peptide quantification fields.
     *
     * @param peptideQuantificationUnit if null, system will not print peptide-quantification_unit in mzTab file.
     */
    public void setPeptideQuantificationUnit(Param peptideQuantificationUnit) {
        this.peptideQuantificationUnit = peptideQuantificationUnit;
    }

    /**
     * Set the type of units is reported in the small molecule quantification fields.
     *
     * @param smallMoleculeQuantificationUnit  if null, system will not print smallmolecule-quantification_unit in mzTab file.
     */
    public void setSmallMoleculeQuantificationUnit(Param smallMoleculeQuantificationUnit) {
        this.smallMoleculeQuantificationUnit = smallMoleculeQuantificationUnit;
    }

    /**
     * Get the external MS data files. An MS run is effectively one run (or set of runs on pre-fractionated samples)
     * on an MS instrument, and is referenced from assay in different contexts.
     */
    public SortedMap<Integer, MsRun> getMsRunMap() {
        return msRunMap;
    }

    /**
     * Set the external MS data files. An MS run is effectively one run (or set of runs on pre-fractionated samples)
     * on an MS instrument, and is referenced from assay in different contexts.
     */
    public void setMsRunMap(SortedMap<Integer, MsRun> msRunMap) {
        this.msRunMap = msRunMap;
    }

    /**
     * Get additional parameters describing the analysis reported.
     */
    public List<Param> getCustomList() {
        return customList;
    }

    /**
     * Set additional parameters describing the analysis reported.
     */
    public void setCustomList(List<Param> customList) {
        this.customList = customList;
    }

    /**
     * Get a couple of biological material that has been analysed, to which descriptors of species, cell/tissue type etc.
     * can be attached. Samples are NOT MANDATORY in mzTab, since many software packages cannot determine what type of sample
     * was analysed (e.g. whether biological or technical replication was performed).
     */
    public SortedMap<Integer, Sample> getSampleMap() {
        return sampleMap;
    }

    /**
     * Set a couple of biological material that has been analysed, to which descriptors of species, cell/tissue type etc.
     * can be attached. Samples are NOT MANDATORY in mzTab, since many software packages cannot determine what type of sample
     * was analysed (e.g. whether biological or technical replication was performed).
     */
    public void setSampleMap(SortedMap<Integer, Sample> sampleMap) {
        this.sampleMap = sampleMap;
    }

    /**
     * Get a couple of assay, which ordered by index number.  One assay is typically mapped to one MS run in the case of
     * label-free MS analysis or multiple assays are mapped to one MS run for multiplexed techniques, along with a
     * description of the label or tag applied.
     */
    public SortedMap<Integer, Assay> getAssayMap() {
        return assayMap;
    }

    /**
     * Set a couple of assay, which ordered by index number.  One assay is typically mapped to one MS run in the case of
     * label-free MS analysis or multiple assays are mapped to one MS run for multiplexed techniques, along with a
     * description of the label or tag applied.
     */
    public void setAssayMap(SortedMap<Integer, Assay> assayMap) {
        this.assayMap = assayMap;
    }

    /**
     * The variables about which the final results of a study are reported, which may have been derived following
     * averaging across a group of replicate measurements (assays). In files where assays are reported, study variables
     * have references to assays. The same concept has been defined by others as "experimental factor".
     */
    public SortedMap<Integer, StudyVariable> getStudyVariableMap() {
        return studyVariableMap;
    }

    /**
     * The variables about which the final results of a study are reported, which may have been derived following
     * averaging across a group of replicate measurements (assays). In files where assays are reported, study variables
     * have references to assays. The same concept has been defined by others as "experimental factor".
     */
    public void setStudyVariableMap(SortedMap<Integer, StudyVariable> studyVariableMap) {
        this.studyVariableMap = studyVariableMap;
    }

    /**
     * Get the definitions of controlled vocabularies/ontologies used in the mzTab file.
     */
    public SortedMap<Integer, CV> getCvMap() {
        return cvMap;
    }

    /**
     * Set the definitions of controlled vocabularies/ontologies used in the mzTab file.
     */
    public void setCvMap(SortedMap<Integer, CV> cvMap) {
        this.cvMap = cvMap;
    }

    /**
     * Get the definitions of the unit for the data reported in a column of the protein section.
     */
    public List<ColUnit> getProteinColUnitList() {
        return proteinColUnitList;
    }

    /**
     * Set the definitions of the unit for the data reported in a column of the protein section.
     */
    public void setProteinColUnitList(List<ColUnit> proteinColUnitList) {
        this.proteinColUnitList = proteinColUnitList;
    }

    /**
     *  Get the definitions of the unit for the data reported in a column of the peptide section.
     */
    public List<ColUnit> getPeptideColUnitList() {
        return peptideColUnitList;
    }

    /**
     *  Set the definitions of the unit for the data reported in a column of the peptide section.
     */
    public void setPeptideColUnitList(List<ColUnit> peptideColUnitList) {
        this.peptideColUnitList = peptideColUnitList;
    }

    /**
     *  Get the definitions of the unit for the data reported in a column of the PSM section.
     */
    public List<ColUnit> getPsmColUnitList() {
        return psmColUnitList;
    }

    /**
     *  Set the definitions of the unit for the data reported in a column of the PSM section.
     */
    public void setPsmColUnitList(List<ColUnit> psmColUnitList) {
        this.psmColUnitList = psmColUnitList;
    }

    /**
     *  Get the definitions of the unit for the data reported in a column of the small molecule section.
     */
    public List<ColUnit> getSmallMoleculeColUnitList() {
        return smallMoleculeColUnitList;
    }

    /**
     *  Set the definitions of the unit for the data reported in a column of the small molecule section.
     */
    public void setSmallMoleculeColUnitList(List<ColUnit> smallMoleculeColUnitList) {
        this.smallMoleculeColUnitList = smallMoleculeColUnitList;
    }

    /**
     * Add a sample to metadata. Samples are NOT MANDATORY in mzTab, since many software packages cannot determine what
     * type of sample was analysed (e.g. whether biological or technical replication was performed).
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
     * @param species if null ignore operation.
     */
    public void addSampleSpecies(Integer id, Param species) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (species == null) {
            return;
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
     * @param tissue if null ignore operation.
     */
    public void addSampleTissue(Integer id, Param tissue) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (tissue == null) {
            return;
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
     * @param cellType if null ignore operation.
     */
    public void addSampleCellType(Integer id, Param cellType) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (cellType == null) {
            return;
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
     * @param disease if null ignore operation.
     */
    public void addSampleDisease(Integer id, Param disease) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (disease == null) {
            return;
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
     * @param description if empty ignore operation.
     */
    public void addSampleDescription(Integer id, String description) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }

        if (isEmpty(description)) {
            return;
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
     * Add a sample[id]-custom into sample. Add a custom parameter for sample.
     *
     * @param id SHOULD be positive integer.
     * @param custom if null ignore operation.
     */
    public void addSampleCustom(Integer id, Param custom) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (custom == null) {
            return;
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
     * Add a sample_processing[id]. A list of parameters describing a sample processing step.
     * The order of the data_processing items should reflect the order these processing steps
     * were performed in. If multiple parameters are given for a step these MUST be separated by a "|".
     *
     * @param id SHOULD be positive integer.
     * @param sampleProcessing if null ignore operation.
     */
    public void addSampleProcessing(Integer id, SplitList<Param> sampleProcessing) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample id should be great than 0!");
        }
        if (sampleProcessing == null) {
            return;
        }

        sampleProcessing.setSplitChar(BAR);
        this.sampleProcessingMap.put(id, sampleProcessing);
    }

    /**
     * Add a processing parameter to sample_processing[id]
     *
     * @param id SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addSampleProcessingParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("Sample processing id should be great than 0!");
        }
        if (param == null) {
            return;
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
     * @param name if null ignore operation.
     */
    public void addInstrumentName(Integer id, Param name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Instrument id should be great than 0!");
        }
        if (name == null) {
            return;
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
     * @param source if null ignore operation.
     */
    public void addInstrumentSource(Integer id, Param source) {
        if (id <= 0) {
            throw new IllegalArgumentException("Instrument id should be great than 0!");
        }
        if (source == null) {
            return;
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
     * Add a parameter for instrument[id]-analyzer[i]
     *
     * @param id SHOULD be positive integer.
     * @param analyzer if null ignore operation.
     */
    public void addInstrumentAnalyzer(Integer id, Param analyzer) {
        if (id <= 0) {
            throw new IllegalArgumentException("Instrument id should be great than 0!");
        }
        if (analyzer == null) {
            return;
        }

        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.addAnalyzer(analyzer);
            instrumentMap.put(id, instrument);
        } else {
            instrument.addAnalyzer(analyzer);
        }
    }

    /**
     * Add a parameter for instrument[id]-detector
     *
     * @param id SHOULD be positive integer.
     * @param detector if null ignore operation.
     */
    public void addInstrumentDetector(Integer id, Param detector) {
        if (id <= 0) {
            throw new IllegalArgumentException("Instrument id should be great than 0!");
        }
        if (detector == null) {
            return;
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
     * Add a software to metadata, which used to analyze the data and obtain the reported results.
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
     * Add a software[id] parameter. The parameter's value SHOULD contain the software's version.
     * The order (numbering) should reflect the order in which the tools were used.
     *
     * @param id SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addSoftwareParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("Software id should be great than 0!");
        }
        if (param == null) {
            return;
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
     * Add a software[id]-setting. This field MAY occur multiple times for a single software.
     * The value of this field is deliberately set as a String, since there currently do not
     * exist cvParams for every possible setting.
     *
     * @param id SHOULD be positive integer.
     * @param setting if empty ignore operation.
     */
    public void addSoftwareSetting(Integer id, String setting) {
        if (id <= 0) {
            throw new IllegalArgumentException("Software id should be great than 0!");
        }
        if (isEmpty(setting)) {
            return;
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
     * Add a protein_search_engine_score[id] parameter. The parameter's value SHOULD contain the engine score cv param.
     * The order (numbering) SHOULD reflect their importance for the identification and be used to determine
     * the identification's rank.
     *
     * @param id SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addProteinSearchEngineScoreParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("Protein search engine score id should be great than 0!");
        }
        if (param == null) {
            return;
        }

        ProteinSearchEngineScore searchEngineScore = proteinSearchEngineScoreMap.get(id);
        if (searchEngineScore == null) {
            searchEngineScore = new ProteinSearchEngineScore(id);
            searchEngineScore.setParam(param);
            proteinSearchEngineScoreMap.put(id, searchEngineScore);
        } else {
            searchEngineScore.setParam(param);
        }
    }

    /**
     * Add a peptide_search_engine_score[id] parameter. The parameter's value SHOULD contain the engine score cv param.
     * The order (numbering) SHOULD reflect their importance for the identification and be used to determine
     * the identification's rank.
     *
     * @param id SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addPeptideSearchEngineScoreParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("Peptide search engine score id should be great than 0!");
        }
        if (param == null) {
            return;
        }

        PeptideSearchEngineScore searchEngineScore = peptideSearchEngineScoreMap.get(id);
        if (searchEngineScore == null) {
            searchEngineScore = new PeptideSearchEngineScore(id);
            searchEngineScore.setParam(param);
            peptideSearchEngineScoreMap.put(id, searchEngineScore);
        } else {
            searchEngineScore.setParam(param);
        }
    }

    /**
     * Add a psm_search_engine_score[id] parameter. The parameter's value SHOULD contain the engine score cv param.
     * The order (numbering) SHOULD reflect their importance for the identification and be used to determine
     * the identification's rank.
     *
     * @param id SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addPsmSearchEngineScoreParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("PSM search engine score id should be great than 0!");
        }
        if (param == null) {
            return;
        }

        PSMSearchEngineScore searchEngineScore = psmSearchEngineScoreMap.get(id);
        if (searchEngineScore == null) {
            searchEngineScore = new PSMSearchEngineScore(id);
            searchEngineScore.setParam(param);
            psmSearchEngineScoreMap.put(id, searchEngineScore);
        } else {
            searchEngineScore.setParam(param);
        }
    }

    /**
     * Add a smallmolecule_search_engine_score[id] parameter. The parameter's value SHOULD contain the engine score cv param.
     * The order (numbering) SHOULD reflect their importance for the identification and be used to determine
     * the identification's rank.
     *
     * @param id SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addSmallMoleculeSearchEngineScoreParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("PSM search engine score id should be great than 0!");
        }
        if (param == null) {
            return;
        }

        SmallMoleculeSearchEngineScore searchEngineScore = smallMoleculeSearchEngineScoreMap.get(id);
        if (searchEngineScore == null) {
            searchEngineScore = new SmallMoleculeSearchEngineScore(id);
            searchEngineScore.setParam(param);
            smallMoleculeSearchEngineScoreMap.put(id, searchEngineScore);
        } else {
            searchEngineScore.setParam(param);
        }
    }

    /**
     * Add a false_discovery_rate parameter to metadata. The file's false discovery rate(s) reported at the PSM,
     * peptide, and/or protein level. False Localization Rate (FLD) for the reporting of modifications can also be
     * reported here. Multiple parameters MUST be separated by "|".
     *
     * @param param SHOULD NOT set null.
     */
    public void addFalseDiscoveryRateParam(Param param) {
        if (param == null) {
            throw new NullPointerException("False discovery rate parameter should not set null");
        }

        this.falseDiscoveryRate.add(param);
    }

    /**
     * Add a publiction to metadata. A publication associated with this file. Several publications can be given by
     * indicating the number in the square brackets after "publication". PubMed ids must be prefixed by "pubmed:",
     * DOIs by "doi:". Multiple identifiers MUST be separated by "|".
     *
     * @param publication SHOULD NOT set null.
     */
    public void addPublication(Publication publication) {
        if (publication == null) {
            throw new IllegalArgumentException("Publication should not be null");
        }

        this.publicationMap.put(publication.getId(), publication);
    }

    /**
     * Add a publication item to metadata. PubMed ids must be prefixed by "pubmed:", DOIs by "doi:".
     * Multiple identifiers MUST be separated by "|".
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
        if (isEmpty(accession)) {
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
     * Add a couple of publication items into publication[id]. Several publications can be given by
     * indicating the number in the square brackets after "publication". PubMed ids must be prefixed by "pubmed:",
     * DOIs by "doi:". Multiple identifiers MUST be separated by "|".
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
     * Add contact[id]-name. Several contacts can be given by indicating the number in the square brackets
     * after "contact". A contact has to be supplied in the format [first name] [initials] [last name] (see example).
     *
     * @param id SHOULD be positive integer.
     * @param name SHOULD NOT set empty.
     */
    public void addContactName(Integer id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Contact id should be great than 0!");
        }
        if (isEmpty(name)) {
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
     * Add contact[id]-affiliation.
     *
     * @param id SHOULD be positive integer.
     * @param affiliation SHOULD NOT set empty.
     */
    public void addContactAffiliation(Integer id, String affiliation) {
        if (id <= 0) {
            throw new IllegalArgumentException("Contact id should be great than 0!");
        }
        if (isEmpty(affiliation)) {
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
        if (isEmpty(email)) {
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
     * Add uri into metadata. The URI pointing to the file's source data (e.g., a PRIDE experiment or a PeptideAtlas build).
     * @param uri if null ignore operation.
     */
    public void addUri(URI uri) {
        if (uri == null) {
            return;
        }

        this.uriList.add(uri);
    }

    /**
     * Add fixed_mod[id] into metadata. A parameter describing a fixed modifications searched for. Multiple
     * fixed modifications are numbered 1..n.
     *
     * @param mod if null ignore operation.
     */
    public void addFixedMod(FixedMod mod) {
        if (mod == null) {
            return;
        }

        this.fixedModMap.put(mod.getId(), mod);
    }

    /**
     * Add fixed_mod[id] parameter into metadata. A parameter describing a fixed modifications searched for.
     *
     * @param id SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addFixedModParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("fixed_mod id should be great than 0!");
        }
        if (param == null) {
            return;
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
     * Add fixed_mod[id]-site into metadata. A string describing a fixed modifications site. Following the unimod
     * convention, modification site is a residue (e.g. "M"), terminus ("N-term" or "C-term") or both (e.g.
     * "N-term Q" or "C-term K").
     *
     * @param id SHOULD be positive integer.
     * @param site SHOULD NOT set empty.
     */
    public void addFixedModSite(Integer id, String site) {
        if (id <= 0) {
            throw new IllegalArgumentException("fixed_mod id should be great than 0!");
        }
        if (isEmpty(site)) {
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
     * Add fixed_mod[id]-position into metadata. A string describing the term specifity of a fixed modification.
     * Following the unimod convention, term specifity is denoted by the strings "Anywhere", "Any N-term",
     * "Any C-term", "Protein N-term", "Protein C-term".
     *
     * @param id SHOULD be positive integer.
     * @param position SHOULD NOT set empty.
     */
    public void addFixedModPosition(Integer id, String position) {
        if (id <= 0) {
            throw new IllegalArgumentException("fixed_mod id should be great than 0!");
        }
        if (isEmpty(position)) {
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
     * @param mod if null ignore operation.
     */
    public void addVariableMod(VariableMod mod) {
        if (mod == null) {
            return;
        }

        this.variableModMap.put(mod.getId(), mod);
    }

    /**
     * Add variable_mod[id] parameter into metadata. A parameter describing a variable modifications searched for.
     * Multiple variable modifications are numbered 1.. n.
     *
     * @param id SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addVariableModParam(Integer id, Param param) {
        if (id <= 0) {
            throw new IllegalArgumentException("variable_mod id should be great than 0!");
        }
        if (param == null) {
            return;
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
     * Add variable_mod[id]-site into metadata. A string describing a variable modifications site.
     * Following the unimod convention, modification site is a residue (e.g. "M"), terminus ("N-term"
     * or "C-term") or both (e.g. "N-term Q" or "C-term K").
     *
     * @param id SHOULD be positive integer.
     * @param site SHOULD NOT set empty.
     */
    public void addVariableModSite(Integer id, String site) {
        if (id <= 0) {
            throw new IllegalArgumentException("variable_mod id should be great than 0!");
        }
        if (isEmpty(site)) {
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
     * Add variable_mod[id]-position into metadata. A string describing the term specifity of a variable modification.
     * Following the unimod convention, term specifity is denoted by the strings "Anywhere", "Any N-term",
     * "Any C-term", "Protein N-term", "Protein C-term".
     *
     * @param id SHOULD be positive integer.
     * @param position SHOULD NOT set empty.
     */
    public void addVariableModPosition(Integer id, String position) {
        if (id <= 0) {
            throw new IllegalArgumentException("variable_mod id should be great than 0!");
        }
        if (isEmpty(position)) {
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
     * Add a ms_run[id] into metadata. An MS run is effectively one run (or set of runs on pre-fractionated samples)
     * on an MS instrument, and is referenced from assay in different contexts.
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
     * Add ms_run[id]-format into metadata. A parameter specifying the data format of the external MS data file.
     *
     * @param id SHOULD be positive integer.
     * @param format if null ignore operation.
     */
    public void addMsRunFormat(Integer id, Param format) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (format == null) {
            return;
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
     * Add ms_run[id]-location into metadata. Location of the external data file. If the actual location
     * of the MS run is unknown, a "null" MUST be used as a place holder value.
     *
     * @param id SHOULD be positive integer.
     * @param location if null ignore operation.
     */
    public void addMsRunLocation(Integer id, URL location) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (location == null) {
            return;
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
     * Add ms_run[id]-id_format into metadata. Parameter specifying the id format used in the external data file.
     *
     * @param id SHOULD be positive integer.
     * @param idFormat if null ignore operation.
     */
    public void addMsRunIdFormat(Integer id, Param idFormat) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (idFormat == null) {
            return;
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
     * Add ms_run[id]-fragmentation_method into metadata. A list of "|" separated parameters describing
     * all the types of fragmentation used in a given ms run.
     *
     * @param id SHOULD be positive integer.
     * @param fragmentationMethod if null ignore operation.
     */
    public void addMsRunFragmentationMethod(Integer id, Param fragmentationMethod) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (fragmentationMethod == null) {
            return;
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

    public void addMsRunHash(Integer id, String hash) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (isEmpty(hash)) {
            throw new IllegalArgumentException("ms_run hash should not set empty.");
        }

        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setHash(hash);
            msRunMap.put(id, msRun);
        } else {
            msRun.setHash(hash);
        }
    }

    public void addMsRunHashMethod(Integer id, Param hashMethod) {
        if (id <= 0) {
            throw new IllegalArgumentException("ms_run id should be great than 0!");
        }
        if (hashMethod == null) {
            return;
        }

        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setHashMethod(hashMethod);
            msRunMap.put(id, msRun);
        } else {
            msRun.setHashMethod(hashMethod);
        }
    }

    /**
     * Add a custom parameter into metadata. Any additional parameters describing the analysis reported.
     *
     * @param custom if null ignore operation.
     */
    public void addCustom(Param custom) {
        if (custom == null) {
            return;
        }

        this.customList.add(custom);
    }

    /**
     * Add a assay into metadata. The application of a measurement about the sample (in this case through MS) -
     * producing values about small molecules, peptides or proteins. One assay is typically mapped to one MS run
     * in the case of label-free MS analysis or multiple assays are mapped to one MS run for multiplexed techniques,
     * along with a description of the label or tag applied.
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
     * Add assay[id]-quantification_reagent into metadata. The reagent used to label the sample in the assay.
     * For label-free analyses the "unlabeled sample" CV term SHOULD be used. For the "light" channel in
     * label-based experiments the appropriate CV term specifying the labelling channel should be used.
     *
     * @param id SHOULD be positive integer.
     * @param quantificationReagent if null ignore operation.
     */
    public void addAssayQuantificationReagent(Integer id, Param quantificationReagent) {
        if (id <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (quantificationReagent == null) {
            return;
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
     * Add assay[id]-sample_ref into metadata. An association from a given assay to the sample analysed.
     *
     * @param id SHOULD be positive integer.
     * @param sample SHOULD NOT set null, and SHOULD be defined in metadata first.
     */
    public void addAssaySample(Integer id, Sample sample) {
        if (id <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (sample == null) {
            throw new NullPointerException("assay sample_ref should not set null.");
        }
        if (! sampleMap.containsValue(sample)) {
            throw new IllegalArgumentException("Sample not defined in metadata.");
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
     * Add assay[id]-ms_run_ref into metadata. An association from a given assay to the source MS run.
     *
     * @param id SHOULD be positive integer.
     * @param msRun SHOULD NOT set null, and SHOULD be defined in metadata first.
     */
    public void addAssayMsRun(Integer id, MsRun msRun) {
        if (id <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (msRun == null) {
            throw new NullPointerException("assay ms_run_ref should not set null.");
        }
        if (! msRunMap.containsValue(msRun)) {
            throw new IllegalArgumentException("ms_run should be defined in metadata first.");
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
     * Add assay[assayId]-quantification_mod[1-n] into metadata. A parameter describing a modification
     * associated with a quantification_reagent. Multiple modifications are numbered 1..n.
     *
     * @param assayId SHOULD be positive integer.
     * @param mod if null ignore operation.
     */
    public void addAssayQuantificationMod(Integer assayId, AssayQuantificationMod mod) {
        if (assayId <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (mod == null) {
            return;
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
     * Add assay[assayId]-quantification_mod[quanModId] into metadata. A parameter describing a modification
     * associated with a quantification_reagent.
     *
     * @param assayId SHOULD be positive integer.
     * @param quanModId SHOULD be positive integer.
     * @param param if null ignore operation.
     */
    public void addAssayQuantificationModParam(Integer assayId, Integer quanModId, Param param) {
        if (assayId <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (quanModId <= 0) {
            throw new IllegalArgumentException("quantification_mod id should be great than 0!");
        }
        if (param == null) {
            return;
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
     * Add assay[assayId]-quantification_mod[quanModId]-site into metadata. A string describing the modifications
     * site. Following the unimod convention, modification site is a residue (e.g. "M"), terminus ("N-term"
     * or "C-term") or both (e.g. "N-term Q" or "C-term K").
     *
     * @param assayId SHOULD be positive integer.
     * @param quanModId SHOULD be positive integer.
     * @param site SHOULD NOT empty.
     */
    public void addAssayQuantificationModSite(Integer assayId, Integer quanModId, String site) {
        if (assayId <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (quanModId <= 0) {
            throw new IllegalArgumentException("quantification_mod id should be great than 0!");
        }
        if (isEmpty(site)) {
            throw new IllegalArgumentException("quantification_mod-site should not empty!");
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
     * Add assay[assayId]-quantification_mod[quanModId]-site into metadata. A string describing the term specifity
     * of the modification. Following the unimod convention, term specifity is denoted by the strings "Anywhere",
     * "Any N-term", "Any C-term", "Protein N-term", "Protein C-term".
     *
     * @param assayId SHOULD be positive integer.
     * @param quanModId SHOULD be positive integer.
     * @param position SHOULD NOT empty.
     */
    public void addAssayQuantificationModPosition(Integer assayId, Integer quanModId, String position) {
        if (assayId <= 0) {
            throw new IllegalArgumentException("assay id should be great than 0!");
        }
        if (quanModId <= 0) {
            throw new IllegalArgumentException("quantification_mod id should be great than 0!");
        }
        if (isEmpty(position)) {
            throw new IllegalArgumentException("quantification_mod position should not empty!");
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
     * Add a study variable into metadata. The variables about which the final results of a study are reported, which
     * may have been derived following averaging across a group of replicate measurements (assays). In files where assays
     * are reported, study variables have references to assays. The same concept has been defined by others as "experimental factor".
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
     * Add a study_variable[id]-assay_refs. Comma-separated references to the IDs of assays grouped in the study variable.
     *
     * @param id SHOULD be positive integer.
     * @param assay SHOULD NOT set null, and should be defined in metadata first.
     */
    public void addStudyVariableAssay(Integer id, Assay assay) {
        if (id <= 0) {
            throw new IllegalArgumentException("study variable id should be great than 0!");
        }
        if (assay == null) {
            throw new NullPointerException("study_variable[n]-assay_ref should not set null.");
        }
        if (! assayMap.containsValue(assay)) {
            throw new IllegalArgumentException("assay should be defined in metadata first");
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
     * Add a study_variable[id]-sample_refs. Comma-separated references to the samples that were analysed in the study variable.
     *
     * @param id SHOULD be positive integer.
     * @param sample SHOULD NOT set null, and should be defined in metadata first.
     */
    public void addStudyVariableSample(Integer id, Sample sample) {
        if (id <= 0) {
            throw new IllegalArgumentException("study variable id should be great than 0!");
        }
        if (sample == null) {
            throw new NullPointerException("study_variable[n]-sample_ref should not set null.");
        }
        if (! sampleMap.containsValue(sample)) {
            throw new IllegalArgumentException("sample should be defined in metadata first");
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
     * Add a study_variable[id]-description. A textual description of the study variable.
     *
     * @param id SHOULD be positive integer.
     * @param description if empty ignore operation.
     */
    public void addStudyVariableDescription(Integer id, String description) {
        if (id <= 0) {
            throw new IllegalArgumentException("study variable id should be great than 0!");
        }
        if (isEmpty(description)) {
            return;
        }

        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
        }

        studyVariable.setDescription(description);
        studyVariableMap.put(id, studyVariable);
    }

    /**
     * Add a controlled vocabularies/ontologies into metadata. Define the controlled vocabularies/ontologies
     * used in the mzTab file.
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
     * Add a cv[id]-label. A string describing the labels of the controlled vocabularies/ontologies used in the mzTab file
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
     * Add a cv[id]-full_name. A string describing the full names of the controlled vocabularies/ontologies used in
     * the mzTab file
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
     * Add a cv[id]-version. A string describing the version of the controlled vocabularies/ontologies used in
     * the mzTab file
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
     * Add a cv[id]-url. A string containing the URLs of the controlled vocabularies/ontologies used in the
     * mzTab file
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
     * Defines the unit for the data reported in a column of the protein section. Defines the unit for the data reported
     * in a column of the protein section. The format of the value has to be {column name}={Parameter defining the unit}
     * This field MUST NOT be used to define a unit for quantification columns. The unit used for protein quantification
     * values MUST be set in protein-quantification_unit.
     *
     * @param column SHOULD NOT set null
     * @param param SHOULD NOT set null
     */
    public void addProteinColUnit(MZTabColumn column, Param param) {
        this.proteinColUnitList.add(new ColUnit(column, param));
    }

    /**
     * Defines the unit for the data reported in a column of the peptide section. Defines the used unit for a column in the
     * peptide section. The format of the value has to be {column name}={Parameter defining the unit}. This field MUST NOT
     * be used to define a unit for quantification columns. The unit used for peptide quantification values MUST be set in
     * peptide-quantification_unit.

     *
     * @param column SHOULD NOT set null
     * @param param SHOULD NOT set null
     */
    public void addPeptideColUnit(MZTabColumn column, Param param) {
        this.peptideColUnitList.add(new ColUnit(column, param));
    }

    /**
     * Defines the unit for the data reported in a column of the PSM section. Defines the used unit for a column in the PSM
     * section. The format of the value has to be {column name}={Parameter defining the unit} This field MUST NOT be used to
     * define a unit for quantification columns. The unit used for peptide quantification values MUST be set in
     * peptide-quantification_unit.

     *
     * @param column SHOULD NOT set null
     * @param param SHOULD NOT set null
     */
    public void addPSMColUnit(MZTabColumn column, Param param) {
        this.psmColUnitList.add(new ColUnit(column, param));
    }

    /**
     * Defines the unit for the data reported in a column of the small molecule section. Defines the used unit for a column
     * in the small molecule section. The format of the value has to be {column name}={Parameter defining the unit}
     * This field MUST NOT be used to define a unit for quantification columns. The unit used for small molecule quantification
     * values MUST be set in small_molecule-quantification_unit.
     *
     * @param column SHOULD NOT set null
     * @param param SHOULD NOT set null
     */
    public void addSmallMoleculeColUnit(MZTabColumn column, Param param) {
        this.smallMoleculeColUnitList.add(new ColUnit(column, param));
    }

    /**
     *  Defines a method to access the colUnit to help in the transformation from columnName String -> to columnName MZTabColumn
     */
    public Map<String, String> getColUnitMap() {
        return colUnitMap;
    }
}
