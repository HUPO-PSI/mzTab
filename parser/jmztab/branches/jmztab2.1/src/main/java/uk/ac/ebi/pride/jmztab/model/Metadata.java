package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.net.URL;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.*;
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
    private SplitList<Param> fixedMod = new SplitList<Param>(BAR);
    private SplitList<Param> variableMod = new SplitList<Param>(BAR);
    private Param quantificationMethod;
    private Param proteinQuantificationUnit;
    private Param peptideQuantificationUnit;
    private Param smallMoleculeQuantificationUnit;
    private SortedMap<Integer, MsRun> msRunMap = new TreeMap<Integer, MsRun>();
    private List<Param> customList = new ArrayList<Param>();
    private SortedMap<Integer, Sample> sampleMap = new TreeMap<Integer, Sample>();
    private SortedMap<Integer, Assay> assayMap = new TreeMap<Integer, Assay>();
    private SortedMap<Integer, StudyVariable> studyVariableMap = new TreeMap<Integer, StudyVariable>();
    private List<ColUnit> proteinColUnitList = new ArrayList<ColUnit>();
    private List<ColUnit> peptideColUnitList = new ArrayList<ColUnit>();
    private List<ColUnit> psmColUnitList = new ArrayList<ColUnit>();
    private List<ColUnit> smallMoleculeColUnitList = new ArrayList<ColUnit>();

    public Metadata() {
        this(new MZTabDescription(MZTabDescription.Mode.Summary, MZTabDescription.Type.Identification));
    }

    public Metadata(MZTabDescription tabDescription) {
        if (tabDescription == null) {
            throw new NullPointerException("Should define mz-tab description first.");
        }

        this.tabDescription = tabDescription;
    }

    private StringBuilder printPrefix(StringBuilder sb) {
        sb.append(Section.Metadata.getPrefix()).append(TAB);

        return sb;
    }

    /**
     * Multi-lines output. One line like following:
     * item[{map.key}]    {map.value}
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

        for (URI uri : uriList) {
            printPrefix(sb).append(MetadataElement.URI).append(TAB).append(uri).append(NEW_LINE);
        }

        if (! fixedMod.isEmpty()) {
            printPrefix(sb).append(FIXED_MOD).append(TAB).append(fixedMod).append(NEW_LINE);
        }

        if (! variableMod.isEmpty()) {
            printPrefix(sb).append(VARIABLE_MOD).append(TAB).append(variableMod).append(NEW_LINE);
        }

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

        for (Param custom : customList) {
            printPrefix(sb).append(CUSTOM).append(TAB).append(custom).append(NEW_LINE);
        }

        sb = printMap(sampleMap, SAMPLE.toString(), sb);
        sb = printMap(assayMap, ASSAY.toString(), sb);
        sb = printMap(studyVariableMap, STUDY_VARIABLE.toString(), sb);

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

        return sb.toString();
    }

    public MZTabDescription getTabDescription() {
        return tabDescription;
    }

    public void setMZTabVersion(String version) {
        this.tabDescription.setVersion(version);
    }

    public void setMZTabID(String id) {
        this.tabDescription.setId(id);
    }

    public void setMZTabMode(MZTabDescription.Mode mode) {
        this.tabDescription.setMode(mode);
    }

    public void setMZTabType(MZTabDescription.Type type) {
        this.tabDescription.setType(type);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SortedMap<Integer, SplitList<Param>> getSampleProcessingMap() {
        return sampleProcessingMap;
    }

    public SortedMap<Integer, Instrument> getInstrumentMap() {
        return instrumentMap;
    }

    public SortedMap<Integer, Software> getSoftwareMap() {
        return softwareMap;
    }

    public SplitList<Param> getFalseDiscoveryRate() {
        return falseDiscoveryRate;
    }

    public SortedMap<Integer, Publication> getPublicationMap() {
        return publicationMap;
    }

    public SortedMap<Integer, Contact> getContactMap() {
        return contactMap;
    }

    public List<URI> getUriList() {
        return uriList;
    }

    public SplitList<Param> getFixedMod() {
        return fixedMod;
    }

    public SplitList<Param> getVariableMod() {
        return variableMod;
    }

    public Param getQuantificationMethod() {
        return quantificationMethod;
    }

    public Param getProteinQuantificationUnit() {
        return proteinQuantificationUnit;
    }

    public Param getPeptideQuantificationUnit() {
        return peptideQuantificationUnit;
    }

    public Param getSmallMoleculeQuantificationUnit() {
        return smallMoleculeQuantificationUnit;
    }

    public SortedMap<Integer, MsRun> getMsRunMap() {
        return msRunMap;
    }

    public List<Param> getCustomList() {
        return customList;
    }

    public SortedMap<Integer, Sample> getSampleMap() {
        return sampleMap;
    }

    public SortedMap<Integer, Assay> getAssayMap() {
        return assayMap;
    }

    public SortedMap<Integer, StudyVariable> getStudyVariableMap() {
        return studyVariableMap;
    }

    public List<ColUnit> getProteinColUnitList() {
        return proteinColUnitList;
    }

    public List<ColUnit> getPeptideColUnitList() {
        return peptideColUnitList;
    }

    public List<ColUnit> getPsmColUnitList() {
        return psmColUnitList;
    }

    public List<ColUnit> getSmallMoleculeColUnitList() {
        return smallMoleculeColUnitList;
    }

    public boolean setTitle(String title) {
        if (this.title != null) {
            return false;
        } else {
            this.title = title;
            return true;
        }
    }

    public boolean setDescription(String description) {
        if (this.description != null) {
            return false;
        } else {
            this.description = description;
            return true;
        }
    }

    public boolean addSampleProcessing(Integer id, SplitList<Param> sampleProcessing) {
        if (sampleProcessingMap.containsKey(id)) {
            return false;
        } else {
            sampleProcessing.setSplitChar(BAR);
            this.sampleProcessingMap.put(id, sampleProcessing);
            return true;
        }
    }

    public boolean addSampleProcessingParam(Integer id, Param param) {
        SplitList<Param> sampleProcessing = sampleProcessingMap.get(id);
        if (sampleProcessing == null) {
            sampleProcessing = new SplitList<Param>(BAR);
            sampleProcessing.add(param);
            sampleProcessingMap.put(id, sampleProcessing);
        } else {
            sampleProcessing.add(param);
        }

        return true;
    }

    public boolean addInstrumentName(Integer id, Param name) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setName(name);
            instrumentMap.put(id, instrument);
            return true;
        } else if (instrument.getName() != null) {
            return false;
        } else {
            instrument.setName(name);
            return true;
        }
    }

    public boolean addInstrumentSource(Integer id, Param source) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setSource(source);
            instrumentMap.put(id, instrument);
            return true;
        } else if (instrument.getSource() != null) {
            return false;
        } else {
            instrument.setSource(source);
            return true;
        }
    }

    public boolean addInstrumentAnalyzer(Integer id, Param analyzer) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setAnalyzer(analyzer);
            instrumentMap.put(id, instrument);
            return true;
        } else if (instrument.getAnalyzer() != null) {
            return false;
        } else {
            instrument.setAnalyzer(analyzer);
            return true;
        }
    }

    public boolean addInstrumentDetector(Integer id, Param detector) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setDetector(detector);
            instrumentMap.put(id, instrument);
            return true;
        } else if (instrument.getDetector() != null) {
            return false;
        } else {
            instrument.setDetector(detector);
            return true;
        }
    }

    public boolean addSoftwareParam(Integer id, Param param) {
        Software software = softwareMap.get(id);
        if (software == null) {
            software = new Software(id);
            software.setParam(param);
            softwareMap.put(id, software);
            return true;
        } else if (software.getParam() != null) {
            return false;
        } else {
            software.setParam(param);
            return true;
        }
    }

    public void addSoftwareSetting(Integer id, String setting) {
        Software software = softwareMap.get(id);
        if (software == null) {
            software = new Software(id);
            software.addSetting(setting);
            softwareMap.put(id, software);
        } else  {
            software.addSetting(setting);
        }
    }

    public void addFalseDiscoveryRateParam(Param param) {
        this.falseDiscoveryRate.add(param);
    }

    public void setFalseDiscoveryRate(SplitList<Param> paramList) {
        this.falseDiscoveryRate = paramList;
    }

    public void addPublicationItem(Integer id, PublicationItem.Type type, String accession) {
        Publication publication = publicationMap.get(id);
        if (publication == null) {
            publication = new Publication(id);
            publication.addPublicationItem(new PublicationItem(type, accession));
            publicationMap.put(id, publication);
        } else {
            publication.addPublicationItem(new PublicationItem(type, accession));
        }
    }

    public void addPublicationItems(Integer id, Collection<PublicationItem> items) {
        Publication publication = publicationMap.get(id);
        if (publication == null) {
            publication = new Publication(id);
            publication.addPublicationItems(items);
            publicationMap.put(id, publication);
        } else {
            publication.addPublicationItems(items);
        }
    }

    public boolean addContactName(Integer id, String name) {
        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setName(name);
            contactMap.put(id, contact);
            return true;
        } else if (contact.getName() != null) {
            return false;
        } else {
            contact.setName(name);
            return true;
        }
    }

    public boolean addContactAffiliation(Integer id, String affiliation) {
        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setAffiliation(affiliation);
            contactMap.put(id, contact);
            return true;
        } else if (contact.getAffiliation() != null) {
            return false;
        } else {
            contact.setAffiliation(affiliation);
            return true;
        }
    }

    public boolean addContactEmail(Integer id, String email) {
        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setEmail(email);
            contactMap.put(id, contact);
            return true;
        } else if (contact.getEmail() != null) {
            return false;
        } else {
            contact.setEmail(email);
            return true;
        }
    }

    public void addUri(URI uri) {
        this.uriList.add(uri);
    }

    public void addFixedModParam(Param param) {
        this.fixedMod.add(param);
    }

    public void addVariableModParam(Param param) {
        this.variableMod.add(param);
    }

    public void setFixedMod(SplitList<Param> fixedMod) {
        this.fixedMod = fixedMod;
    }

    public void setVariableMod(SplitList<Param> variableMod) {
        this.variableMod = variableMod;
    }

    public void setQuantificationMethod(Param quantificationMethod) {
        this.quantificationMethod = quantificationMethod;
    }

    public void setProteinQuantificationUnit(Param proteinQuantificationUnit) {
        this.proteinQuantificationUnit = proteinQuantificationUnit;
    }

    public void setPeptideQuantificationUnit(Param peptideQuantificationUnit) {
        this.peptideQuantificationUnit = peptideQuantificationUnit;
    }

    public void setSmallMoleculeQuantificationUnit(Param smallMoleculeQuantificationUnit) {
        this.smallMoleculeQuantificationUnit = smallMoleculeQuantificationUnit;
    }

    public void addMsRun(MsRun msRun) {
        msRunMap.put(msRun.getId(), msRun);
    }

    public boolean addMsRunFormat(Integer id, Param format) {
        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setFormat(format);
            msRunMap.put(id, msRun);
            return true;
        } else if (msRun.getFormat() != null) {
            return false;
        } else {
            msRun.setFormat(format);
            return true;
        }
    }

    public boolean addMsRunLocation(Integer id, URL location) {
        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setLocation(location);
            msRunMap.put(id, msRun);
            return true;
        } else if (msRun.getLocation() != null) {
            return false;
        } else {
            msRun.setLocation(location);
            return true;
        }
    }

    public boolean addMsRunIdFormat(Integer id, Param idFormat) {
        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setIdFormat(idFormat);
            msRunMap.put(id, msRun);
            return true;
        } else if (msRun.getIdFormat() != null) {
            return false;
        } else {
            msRun.setIdFormat(idFormat);
            return true;
        }
    }

    public boolean addMsRunFragmentationMethod(Integer id, Param fragmentationMethod) {
        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setFragmentationMethod(fragmentationMethod);
            msRunMap.put(id, msRun);
            return true;
        } else if (msRun.getFragmentationMethod() != null) {
            return false;
        } else {
            msRun.setFragmentationMethod(fragmentationMethod);
            return true;
        }
    }

    public void addCustom(Param custom) {
        this.customList.add(custom);
    }

    public void addSample(Sample sample) {
        sampleMap.put(sample.getId(), sample);
    }

    public boolean addSampleSpecies(Integer id, Param species) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addSpecies(species);
            sampleMap.put(id, sample);
            return true;
        } else {
            sample.addSpecies(species);
            return true;
        }
    }

    public boolean addSampleTissue(Integer id, Param tissue) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addTissue(tissue);
            sampleMap.put(id, sample);
            return true;
        } else {
            sample.addTissue(tissue);
            return true;
        }
    }

    public boolean addSampleCellType(Integer id, Param cellType) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addCellType(cellType);
            sampleMap.put(id, sample);
            return true;
        } else {
            sample.addCellType(cellType);
            return true;
        }
    }

    public boolean addSampleDisease(Integer id, Param disease) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addDisease(disease);
            sampleMap.put(id, sample);
            return true;
        } else {
            sample.addDisease(disease);
            return true;
        }
    }

    public boolean addSampleDescription(Integer id, String description) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.setDescription(description);
            sampleMap.put(id, sample);
            return true;
        } else {
            sample.setDescription(description);
            return true;
        }
    }

    public boolean addSampleCustom(Integer id, Param custom) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addCustom(custom);
            sampleMap.put(id, sample);
            return true;
        } else {
            sample.addCustom(custom);
            return true;
        }
    }

    public void addAssay(Assay assay) {
        assayMap.put(assay.getId(), assay);
    }

    public boolean addAssayQuantificationReagent(Integer id, Param quantificationReagent) {
        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setQuantificationReagent(quantificationReagent);
            assayMap.put(id, assay);
            return true;
        } else if (assay.getQuantificationReagent() != null) {
            return false;
        } else {
            assay.setQuantificationReagent(quantificationReagent);
            return true;
        }
    }

    public boolean addAssaySample(Integer id, Sample sample) {
        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setSample(sample);
            assayMap.put(id, assay);
            return true;
        } else {
            assay.setSample(sample);
            return true;
        }
    }

    public boolean addAssayMsRun(Integer id, MsRun msRun) {
        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setMsRun(msRun);
            assayMap.put(id, assay);
            return true;
        } else {
            assay.setMsRun(msRun);
            return true;
        }
    }

    public void addStudyVariable(StudyVariable studyVariable) {
        studyVariableMap.put(studyVariable.getId(), studyVariable);
    }

    public boolean addStudyVariableAssay(Integer id, Assay assay) {
        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
            studyVariable.addAssay(assay.getId(), assay);
            studyVariableMap.put(id, studyVariable);
            return true;
        } else {
            studyVariable.addAssay(assay.getId(), assay);
            return true;
        }
    }

    public boolean addStudyVariableSample(Integer id, Sample sample) {
        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
            studyVariable.addSample(sample.getId(), sample);
            studyVariableMap.put(id, studyVariable);
            return true;
        } else {
            studyVariable.addSample(sample.getId(), sample);
            return true;
        }
    }

    public boolean addStudyVariableDescription(Integer id, String description) {
        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
        }

        studyVariable.setDescription(description);
        studyVariableMap.put(id, studyVariable);
        return true;
    }

    public void addProteinColUnit(MZTabColumn column, Param param) {
        this.proteinColUnitList.add(new ColUnit(column, param));
    }

    public void addPeptideColUnit(MZTabColumn column, Param param) {
        this.peptideColUnitList.add(new ColUnit(column, param));
    }

    public void addPSMColUnit(MZTabColumn column, Param param) {
        this.psmColUnitList.add(new ColUnit(column, param));
    }

    public void addSmallMoleculeColUnit(MZTabColumn column, Param param) {
        this.smallMoleculeColUnitList.add(new ColUnit(column, param));
    }
}
