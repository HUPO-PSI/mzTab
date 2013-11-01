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

    public Metadata() {
        this(new MZTabDescription(MZTabDescription.Mode.Summary, MZTabDescription.Type.Identification));
    }

    public Metadata(MZTabDescription tabDescription) {
        if (tabDescription == null) {
            throw new IllegalArgumentException("Should define mz-tab description first.");
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

    /**
     * item[list.id+1]    {list.value}
     */
    protected StringBuilder printList(List<?> list, String item, StringBuilder sb) {
        for (int i = 0; i < list.size(); i++) {
            printPrefix(sb).append(item).append("[").append(i + 1).append("]").append(TAB).append(list.get(i)).append(NEW_LINE);
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

    public MZTabDescription getTabDescription() {
        return tabDescription;
    }

    public MZTabDescription.Mode getMZTabMode() {
        return tabDescription.getMode();
    }

    public String getMZTabVersion() {
        return tabDescription.getVersion();
    }

    public MZTabDescription.Type getMZTabType() {
        return tabDescription.getType();
    }

    public String getMZTabID() {
        return tabDescription.getId();
    }

    public void setMZTabID(String id) {
        tabDescription.setId(id);
    }

    public void setMZTabVersion(String version) {
        tabDescription.setVersion(version);
    }

    public void setMZTabMode(MZTabDescription.Mode mode) {
        tabDescription.setMode(mode);
    }

    public void setMZTabType(MZTabDescription.Type type) {
        tabDescription.setType(type);
    }

    public void setTabDescription(MZTabDescription tabDescription) {
        if (tabDescription == null) {
            throw new IllegalArgumentException("MZTabDescription should not set null!");
        }

        this.tabDescription = tabDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SortedMap<Integer, SplitList<Param>> getSampleProcessingMap() {
        return sampleProcessingMap;
    }

    public void setSampleProcessingMap(SortedMap<Integer, SplitList<Param>> sampleProcessingMap) {
        if (sampleProcessingMap == null) {
            sampleProcessingMap = new TreeMap<Integer, SplitList<Param>>();
        }

        this.sampleProcessingMap = sampleProcessingMap;
    }

    public SortedMap<Integer, Instrument> getInstrumentMap() {
        return instrumentMap;
    }

    public void setInstrumentMap(SortedMap<Integer, Instrument> instrumentMap) {
        if (instrumentMap == null) {
            instrumentMap = new TreeMap<Integer, Instrument>();
        }

        this.instrumentMap = instrumentMap;
    }

    public SortedMap<Integer, Software> getSoftwareMap() {
        return softwareMap;
    }

    public void setSoftwareMap(SortedMap<Integer, Software> softwareMap) {
        if (softwareMap == null) {
            softwareMap = new TreeMap<Integer, Software>();
        }

        this.softwareMap = softwareMap;
    }

    public SplitList<Param> getFalseDiscoveryRate() {
        return falseDiscoveryRate;
    }

    public void setFalseDiscoveryRate(SplitList<Param> falseDiscoveryRate) {
        if (falseDiscoveryRate == null) {
            falseDiscoveryRate = new SplitList<Param>(BAR);
        }

        this.falseDiscoveryRate = falseDiscoveryRate;
    }

    public SortedMap<Integer, Publication> getPublicationMap() {
        return publicationMap;
    }

    public void setPublicationMap(SortedMap<Integer, Publication> publicationMap) {
        if (publicationMap == null) {
            publicationMap = new TreeMap<Integer, Publication>();
        }

        this.publicationMap = publicationMap;
    }

    public SortedMap<Integer, Contact> getContactMap() {
        return contactMap;
    }

    public void setContactMap(SortedMap<Integer, Contact> contactMap) {
        if (contactMap == null) {
            contactMap = new TreeMap<Integer, Contact>();
        }

        this.contactMap = contactMap;
    }

    public List<URI> getUriList() {
        return uriList;
    }

    public void setUriList(List<URI> uriList) {
        if (uriList == null) {
            uriList = new ArrayList<java.net.URI>();
        }

        this.uriList = uriList;
    }

    public SortedMap<Integer, FixedMod> getFixedModMap() {
        return fixedModMap;
    }

    public void setFixedModMap(SortedMap<Integer, FixedMod> fixedModMap) {
        if (fixedModMap == null) {
            fixedModMap = new TreeMap<Integer, FixedMod>();
        }

        this.fixedModMap = fixedModMap;
    }

    public SortedMap<Integer, VariableMod> getVariableModMap() {
        return variableModMap;
    }

    public void setVariableModMap(SortedMap<Integer, VariableMod> variableModMap) {
        if (variableModMap == null) {
            variableModMap = new TreeMap<Integer, VariableMod>();
        }

        this.variableModMap = variableModMap;
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

    public SortedMap<Integer, MsRun> getMsRunMap() {
        return msRunMap;
    }

    public void setMsRunMap(SortedMap<Integer, MsRun> msRunMap) {
        if (msRunMap == null) {
            msRunMap = new TreeMap<Integer, MsRun>();
        }

        this.msRunMap = msRunMap;
    }

    public List<Param> getCustomList() {
        return customList;
    }

    public void setCustomList(List<Param> customList) {
        if (customList == null) {
            customList = new ArrayList<Param>();
        }

        this.customList = customList;
    }

    public SortedMap<Integer, Sample> getSampleMap() {
        return sampleMap;
    }

    public void setSampleMap(SortedMap<Integer, Sample> sampleMap) {
        if (sampleMap == null) {
            sampleMap = new TreeMap<Integer, Sample>();
        }

        this.sampleMap = sampleMap;
    }

    public SortedMap<Integer, Assay> getAssayMap() {
        return assayMap;
    }

    public void setAssayMap(SortedMap<Integer, Assay> assayMap) {
        if (assayMap == null) {
            assayMap = new TreeMap<Integer, Assay>();
        }

        this.assayMap = assayMap;
    }

    public SortedMap<Integer, StudyVariable> getStudyVariableMap() {
        return studyVariableMap;
    }

    public void setStudyVariableMap(SortedMap<Integer, StudyVariable> studyVariableMap) {
        if (studyVariableMap == null) {
            studyVariableMap = new TreeMap<Integer, StudyVariable>();
        }

        this.studyVariableMap = studyVariableMap;
    }

    public SortedMap<Integer, CV> getCvMap() {
        return cvMap;
    }

    public void setCvMap(SortedMap<Integer, CV> cvMap) {
        if (cvMap == null) {
            cvMap = new TreeMap<Integer, CV>();
        }

        this.cvMap = cvMap;
    }

    public List<ColUnit> getProteinColUnitList() {
        return proteinColUnitList;
    }

    public void setProteinColUnitList(List<ColUnit> proteinColUnitList) {
        if (proteinColUnitList == null) {
            proteinColUnitList = new ArrayList<ColUnit>();
        }

        this.proteinColUnitList = proteinColUnitList;
    }

    public List<ColUnit> getPeptideColUnitList() {
        return peptideColUnitList;
    }

    public void setPeptideColUnitList(List<ColUnit> peptideColUnitList) {
        if (peptideColUnitList == null) {
            peptideColUnitList = new ArrayList<ColUnit>();
        }

        this.peptideColUnitList = peptideColUnitList;
    }

    public List<ColUnit> getPsmColUnitList() {
        return psmColUnitList;
    }

    public void setPsmColUnitList(List<ColUnit> psmColUnitList) {
        if (psmColUnitList == null) {
            psmColUnitList = new ArrayList<ColUnit>();
        }

        this.psmColUnitList = psmColUnitList;
    }

    public List<ColUnit> getSmallMoleculeColUnitList() {
        return smallMoleculeColUnitList;
    }

    public void setSmallMoleculeColUnitList(List<ColUnit> smallMoleculeColUnitList) {
        if (smallMoleculeColUnitList == null) {
            smallMoleculeColUnitList = new ArrayList<ColUnit>();
        }

        this.smallMoleculeColUnitList = smallMoleculeColUnitList;
    }

    public void addSample(Sample sample) {
        if (sample == null) {
            throw new IllegalArgumentException("Sample should not be null");
        }

        this.sampleMap.put(sample.getId(), sample);
    }

    public void addSampleSpecies(Integer id, Param species) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addSpecies(species);
            sampleMap.put(id, sample);
        } else {
            sample.addSpecies(species);
        }
    }

    public void addSampleTissue(Integer id, Param tissue) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addTissue(tissue);
            sampleMap.put(id, sample);
        } else {
            sample.addTissue(tissue);
        }
    }

    public void addSampleCellType(Integer id, Param cellType) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addCellType(cellType);
            sampleMap.put(id, sample);
        } else {
            sample.addCellType(cellType);
        }
    }

    public void addSampleDisease(Integer id, Param disease) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addDisease(disease);
            sampleMap.put(id, sample);
        } else {
            sample.addDisease(disease);
        }
    }

    public void addSampleDescription(Integer id, String description) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.setDescription(description);
            sampleMap.put(id, sample);
        } else {
            sample.setDescription(description);
        }
    }

    public void addSampleCustom(Integer id, Param custom) {
        Sample sample = sampleMap.get(id);
        if (sample == null) {
            sample = new Sample(id);
            sample.addCustom(custom);
            sampleMap.put(id, sample);
        } else {
            sample.addCustom(custom);
        }
    }

    public void addSampleProcessing(Integer id, SplitList<Param> sampleProcessing) {
        sampleProcessing.setSplitChar(BAR);
        this.sampleProcessingMap.put(id, sampleProcessing);
    }

    public void addSampleProcessingParam(Integer id, Param param) {
        SplitList<Param> sampleProcessing = sampleProcessingMap.get(id);
        if (sampleProcessing == null) {
            sampleProcessing = new SplitList<Param>(BAR);
            sampleProcessing.add(param);
            sampleProcessingMap.put(id, sampleProcessing);
        } else {
            sampleProcessing.add(param);
        }
    }

    public void addInstrument(Instrument instrument) {
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument should not be null");
        }

        instrumentMap.put(instrument.getId(), instrument);
    }

    public void addInstrumentName(Integer id, Param name) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setName(name);
            instrumentMap.put(id, instrument);
        } else {
            instrument.setName(name);
        }
    }

    public void addInstrumentSource(Integer id, Param source) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setSource(source);
            instrumentMap.put(id, instrument);
        } else {
            instrument.setSource(source);
        }
    }

    public void addInstrumentAnalyzer(Integer id, Param analyzer) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setAnalyzer(analyzer);
            instrumentMap.put(id, instrument);
        } else {
            instrument.setAnalyzer(analyzer);
        }
    }

    public void addInstrumentDetector(Integer id, Param detector) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id);
            instrument.setDetector(detector);
            instrumentMap.put(id, instrument);
        } else {
            instrument.setDetector(detector);
        }
    }

    public void addSoftware(Software software) {
        if (software == null) {
            throw new IllegalArgumentException("Software should not be null");
        }

        this.softwareMap.put(software.getId(), software);
    }

    public void addSoftwareParam(Integer id, Param param) {
        Software software = softwareMap.get(id);
        if (software == null) {
            software = new Software(id);
            software.setParam(param);
            softwareMap.put(id, software);
        } else {
            software.setParam(param);
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

    public void addPublication(Publication publication) {
        if (publication == null) {
            throw new IllegalArgumentException("Publication should not be null");
        }

        this.publicationMap.put(publication.getId(), publication);
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

    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact should not be null");
        }

        this.contactMap.put(contact.getId(), contact);
    }

    public void addContactName(Integer id, String name) {
        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setName(name);
            contactMap.put(id, contact);
        } else {
            contact.setName(name);
        }
    }

    public void addContactAffiliation(Integer id, String affiliation) {
        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setAffiliation(affiliation);
            contactMap.put(id, contact);
        } else {
            contact.setAffiliation(affiliation);
        }
    }

    public void addContactEmail(Integer id, String email) {
        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id);
            contact.setEmail(email);
            contactMap.put(id, contact);
        } else {
            contact.setEmail(email);
        }
    }

    public void addUri(URI uri) {
        this.uriList.add(uri);
    }

    public void addFixedMod(FixedMod mod) {
        if (mod == null) {
            throw new IllegalArgumentException("FixedMod should not be null");
        }

        this.fixedModMap.put(mod.getId(), mod);
    }

    public void addFixedModParam(Integer id, Param param) {
        FixedMod mod = fixedModMap.get(id);
        if (mod == null) {
            mod = new FixedMod(id);
            mod.setParam(param);
            fixedModMap.put(id, mod);
        } else {
            mod.setParam(param);
        }
    }

    public void addFixedModSite(Integer id, String site) {
        FixedMod mod = fixedModMap.get(id);
        if (mod == null) {
            mod = new FixedMod(id);
            mod.setSite(site);
            fixedModMap.put(id, mod);
        } else  {
            mod.setSite(site);
        }
    }

    public void addFixedModPosition(Integer id, String position) {
        FixedMod mod = fixedModMap.get(id);
        if (mod == null) {
            mod = new FixedMod(id);
            mod.setPosition(position);
            fixedModMap.put(id, mod);
        } else  {
            mod.setPosition(position);
        }
    }

    public void addVariableMod(VariableMod mod) {
        if (mod == null) {
            throw new IllegalArgumentException("VariableMod should not be null");
        }

        this.variableModMap.put(mod.getId(), mod);
    }

    public void addVariableModParam(Integer id, Param param) {
        VariableMod mod = variableModMap.get(id);
        if (mod == null) {
            mod = new VariableMod(id);
            mod.setParam(param);
            variableModMap.put(id, mod);
        } else {
            mod.setParam(param);
        }
    }

    public void addVariableModSite(Integer id, String site) {
        VariableMod mod = variableModMap.get(id);
        if (mod == null) {
            mod = new VariableMod(id);
            mod.setSite(site);
            variableModMap.put(id, mod);
        } else  {
            mod.setSite(site);
        }
    }

    public void addVariableModPosition(Integer id, String position) {
        VariableMod mod = variableModMap.get(id);
        if (mod == null) {
            mod = new VariableMod(id);
            mod.setPosition(position);
            variableModMap.put(id, mod);
        } else  {
            mod.setPosition(position);
        }
    }

    public void addMsRun(MsRun msRun) {
        if (msRun == null) {
            throw new IllegalArgumentException("MsRun should not be null");
        }

        msRunMap.put(msRun.getId(), msRun);
    }

    public void addMsRunFormat(Integer id, Param format) {
        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setFormat(format);
            msRunMap.put(id, msRun);
        } else {
            msRun.setFormat(format);
        }
    }

    public void addMsRunLocation(Integer id, URL location) {
        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setLocation(location);
            msRunMap.put(id, msRun);
        } else {
            msRun.setLocation(location);
        }
    }

    public void addMsRunIdFormat(Integer id, Param idFormat) {
        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setIdFormat(idFormat);
            msRunMap.put(id, msRun);
        } else {
            msRun.setIdFormat(idFormat);
        }
    }

    public void addMsRunFragmentationMethod(Integer id, Param fragmentationMethod) {
        MsRun msRun = msRunMap.get(id);
        if (msRun == null) {
            msRun = new MsRun(id);
            msRun.setFragmentationMethod(fragmentationMethod);
            msRunMap.put(id, msRun);
        } else {
            msRun.setFragmentationMethod(fragmentationMethod);
        }
    }

    public void addCustom(Param custom) {
        this.customList.add(custom);
    }

    public void addAssay(Assay assay) {
        if (assay == null) {
            throw new IllegalArgumentException("Assay should not be null");
        }

        assayMap.put(assay.getId(), assay);
    }

    public void addAssayQuantificationReagent(Integer id, Param quantificationReagent) {
        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setQuantificationReagent(quantificationReagent);
            assayMap.put(id, assay);
        } else {
            assay.setQuantificationReagent(quantificationReagent);
        }
    }

    public void addAssaySample(Integer id, Sample sample) {
        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setSample(sample);
            assayMap.put(id, assay);
        } else {
            assay.setSample(sample);
        }
    }

    public void addAssayMsRun(Integer id, MsRun msRun) {
        Assay assay = assayMap.get(id);
        if (assay == null) {
            assay = new Assay(id);
            assay.setMsRun(msRun);
            assayMap.put(id, assay);
        } else {
            assay.setMsRun(msRun);
        }
    }

    public void addAssayQuantificationMod(Integer assayId, AssayQuantificationMod mod) {
        Assay assay = assayMap.get(assayId);
        if (assay == null) {
            assay = new Assay(assayId);
            assay.addQuantificationMod(mod);
            assayMap.put(assayId, assay);
        } else {
            assay.addQuantificationMod(mod);
        }
    }

    public void addAssayQuantificationModParam(Integer assayId, Integer quanModId, Param param) {
        Assay assay = assayMap.get(assayId);
        if (assay == null) {
            assay = new Assay(assayId);
            assay.addQuantificationModParam(quanModId, param);
            assayMap.put(assayId, assay);
        } else {
            assay.addQuantificationModParam(quanModId, param);
        }
    }

    public void addAssayQuantificationModSite(Integer assayId, Integer quanModId, String site) {
        Assay assay = assayMap.get(assayId);
        if (assay == null) {
            assay = new Assay(assayId);
            assay.addQuantificationModSite(quanModId, site);
            assayMap.put(assayId, assay);
        } else {
            assay.addQuantificationModSite(quanModId, site);
        }
    }

    public void addAssayQuantificationModPosition(Integer assayId, Integer quanModId, String position) {
        Assay assay = assayMap.get(assayId);
        if (assay == null) {
            assay = new Assay(assayId);
            assay.addQuantificationModPosition(quanModId, position);
            assayMap.put(assayId, assay);
        } else {
            assay.addQuantificationModPosition(quanModId, position);
        }
    }

    public void addStudyVariable(StudyVariable studyVariable) {
        if (studyVariable == null) {
            throw new IllegalArgumentException("StudyVariable should not be null");
        }

        studyVariableMap.put(studyVariable.getId(), studyVariable);
    }

    public void addStudyVariableAssay(Integer id, Assay assay) {
        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
            studyVariable.addAssay(assay.getId(), assay);
            studyVariableMap.put(id, studyVariable);
        } else {
            studyVariable.addAssay(assay.getId(), assay);
        }
    }

    public void addStudyVariableSample(Integer id, Sample sample) {
        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
            studyVariable.addSample(sample.getId(), sample);
            studyVariableMap.put(id, studyVariable);
        } else {
            studyVariable.addSample(sample.getId(), sample);
        }
    }

    public void addStudyVariableDescription(Integer id, String description) {
        StudyVariable studyVariable = studyVariableMap.get(id);
        if (studyVariable == null) {
            studyVariable = new StudyVariable(id);
        }

        studyVariable.setDescription(description);
        studyVariableMap.put(id, studyVariable);
    }

    public void addCV(CV cv) {
        cvMap.put(cv.getId(), cv);
    }

    public void addCVLabel(Integer id, String label) {
        CV cv = cvMap.get(id);
        if (cv == null) {
            cv = new CV(id);
        }

        cv.setLabel(label);
        cvMap.put(id, cv);
    }

    public void addCVFullName(Integer id, String fullName) {
        CV cv = cvMap.get(id);
        if (cv == null) {
            cv = new CV(id);
        }

        cv.setFullName(fullName);
        cvMap.put(id, cv);
    }

    public void addCVVersion(Integer id, String version) {
        CV cv = cvMap.get(id);
        if (cv == null) {
            cv = new CV(id);
        }

        cv.setVersion(version);
        cvMap.put(id, cv);
    }

    public void addCVURL(Integer id, String url) {
        CV cv = cvMap.get(id);
        if (cv == null) {
            cv = new CV(id);
        }

        cv.setUrl(url);
        cvMap.put(id, cv);
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
