package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.net.URL;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.model.MetadataElement.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;
import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.*;

/**
 * using {UNIT_ID} to identifier a unit.
 *
 * User: Qingwei
 * Date: 30/01/13
 */
public class Unit {
    private String unitId;
    private String title;
    private String description;
    private Map<Integer, SplitList<Param>> sampleProcessingMap = new TreeMap<Integer, SplitList<Param>>();
    private Map<Integer, Instrument> instrumentMap = new TreeMap<Integer, Instrument>();
    private Map<Integer, Software> softwareMap = new TreeMap<Integer, Software>();
    private SplitList<Param> falseDiscoveryRate = new SplitList<Param>(BAR);
    private List<Publication> publicationList = new ArrayList<Publication>();
    private Map<Integer, Contact> contactMap = new TreeMap<Integer, Contact>();
    private List<URI> uriList = new ArrayList<java.net.URI>();
    private SplitList<Param> mod = new SplitList<Param>(BAR);
    private Param quantificationMethod;
    private Param proteinQuantificationUnit;
    private Param peptideQuantificationUnit;
    private Map<Integer, MsFile> msFileMap = new TreeMap<Integer, MsFile>();
    private List<Param> customList = new ArrayList<Param>();
    private List<ProteinColUnit> proteinColUnitList = new ArrayList<ProteinColUnit>();
    private List<PeptideColUnit> peptideColUnitList = new ArrayList<PeptideColUnit>();
    private List<SmallMoleculeColUnit> smallMoleculeColUnitList = new ArrayList<SmallMoleculeColUnit>();


    public Unit(String unitId) {
        if (unitId == null) {
            throw new NullPointerException("Unit_IDs can not empty.");
        }

        this.unitId = unitId;
    }

    /**
     * MTD  {unit_id}-
     */
    protected StringBuilder printPrefix(StringBuilder sb) {
        sb.append(Section.Metadata.getPrefix()).append(TAB);
        sb.append(getIdentifier()).append(MINUS);

        return sb;
    }

    /**
     * identifier is unique id in Unit class and sub-class (such as SubUnit, ReplicateUnit).
     * @return {unit_id}
     */
    public String getIdentifier() {
        return unitId;
    }

    /**
     * Multi-lines output. One line like following:
     * {Unit_ID}-item[{map.key}]    {map.value}
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
            if (value instanceof SplitList) {
                sb.append(NEW_LINE);
            }
        }

        return sb;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

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

        for (Publication publication : publicationList) {
            printPrefix(sb).append(PUBLICATION).append(TAB).append(publication).append(NEW_LINE);
        }

        sb = printMap(contactMap, CONTACT.toString(), sb);

        for (URI uri : uriList) {
            printPrefix(sb).append(URI).append(TAB).append(uri).append(NEW_LINE);
        }

        if (! mod.isEmpty()) {
            printPrefix(sb).append(MOD).append(TAB).append(mod).append(NEW_LINE);
        }

        if (quantificationMethod != null) {
            printPrefix(sb).append(QUANTIFICATION_METHOD).append(TAB).append(quantificationMethod).append(NEW_LINE);
        }
        if (proteinQuantificationUnit != null) {
            printPrefix(sb).append(PROTEIN_QUANTIFICATION_UNIT).append(TAB).append(proteinQuantificationUnit).append(NEW_LINE);
        }
        if (peptideQuantificationUnit != null) {
            printPrefix(sb).append(PEPTIDE_QUANTIFICATION_UNIT).append(TAB).append(peptideQuantificationUnit).append(NEW_LINE);
        }

        sb = printMap(msFileMap, MS_FILE.toString(), sb);

        for (ProteinColUnit colUnit : proteinColUnitList) {
            printPrefix(sb).append(COLUNIT_PROTEIN).append(TAB).append(colUnit).append(NEW_LINE);
        }
        for (PeptideColUnit colUnit : peptideColUnitList) {
            printPrefix(sb).append(COLUNIT_PEPTIDE).append(TAB).append(colUnit).append(NEW_LINE);
        }
        for (SmallMoleculeColUnit colUnit : smallMoleculeColUnitList) {
            printPrefix(sb).append(COLUNIT_SMALL_MOLECULE).append(TAB).append(colUnit).append(NEW_LINE);
        }
        for (Param custom : customList) {
            printPrefix(sb).append(CUSTOM).append(TAB).append(custom).append(NEW_LINE);
        }

        return sb.toString();
    }

    public String getUnitId() {
        return unitId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Map<Integer, SplitList<Param>> getSampleProcessingMap() {
        return sampleProcessingMap;
    }

    public Map<Integer, Instrument> getInstrumentMap() {
        return instrumentMap;
    }

    public Map<Integer, Software> getSoftwareMap() {
        return softwareMap;
    }

    public SplitList<Param> getFalseDiscoveryRate() {
        return falseDiscoveryRate;
    }

    public List<Publication> getPublicationList() {
        return publicationList;
    }

    public Map<Integer, Contact> getContactMap() {
        return contactMap;
    }

    public List<URI> getUriList() {
        return uriList;
    }

    public SplitList<Param> getMod() {
        return mod;
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

    public Map<Integer, MsFile> getMsFileMap() {
        return msFileMap;
    }

    public List<Param> getCustomList() {
        return customList;
    }

    public List<ProteinColUnit> getProteinColUnitList() {
        return proteinColUnitList;
    }

    public List<PeptideColUnit> getPeptideColUnitList() {
        return peptideColUnitList;
    }

    public List<SmallMoleculeColUnit> getSmallMoleculeColUnitList() {
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

    public boolean addInstrumentName(Integer id, Param name) {
        Instrument instrument = instrumentMap.get(id);
        if (instrument == null) {
            instrument = new Instrument(id, this);
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
            instrument = new Instrument(id, this);
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
            instrument = new Instrument(id, this);
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
            instrument = new Instrument(id, this);
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
            software = new Software(id, this);
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
            software = new Software(id, this);
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

    public void addPublication(Publication publication) {
        this.publicationList.add(publication);
    }

    public boolean addContactName(Integer id, String name) {
        Contact contact = contactMap.get(id);
        if (contact == null) {
            contact = new Contact(id, this);
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
            contact = new Contact(id, this);
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
            contact = new Contact(id, this);
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

    public void addModParam(Param param) {
        this.mod.add(param);
    }

    public void setMod(SplitList<Param> mod) {
        this.mod = mod;
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

    public boolean addMsFileFormat(Integer id, CVParam format) {
        MsFile msFile = msFileMap.get(id);
        if (msFile == null) {
            msFile = new MsFile(id, this);
            msFile.setFormat(format);
            msFileMap.put(id, msFile);
            return true;
        } else if (msFile.getFormat() != null) {
            return false;
        } else {
            msFile.setFormat(format);
            return true;
        }
    }

    public boolean addMsFileLocation(Integer id, URL location) {
        MsFile msFile = msFileMap.get(id);
        if (msFile == null) {
            msFile = new MsFile(id, this);
            msFile.setLocation(location);
            msFileMap.put(id, msFile);
            return true;
        } else if (msFile.getLocation() != null) {
            return false;
        } else {
            msFile.setLocation(location);
            return true;
        }
    }

    public boolean addMsFileIdFormat(Integer id, CVParam idFormat) {
        MsFile msFile = msFileMap.get(id);
        if (msFile == null) {
            msFile = new MsFile(id, this);
            msFile.setIdFormat(idFormat);
            msFileMap.put(id, msFile);
            return true;
        } else if (msFile.getIdFormat() != null) {
            return false;
        } else {
            msFile.setIdFormat(idFormat);
            return true;
        }
    }

    public void addProteinColUnit(MZTabColumn column, Param param) {
        this.proteinColUnitList.add(new ProteinColUnit(column, param));
    }

    public void addPeptideColUnit(MZTabColumn column, Param param) {
        this.peptideColUnitList.add(new PeptideColUnit(column, param));
    }

    public void addSmallMoleculeColUnit(MZTabColumn column, Param param) {
        this.smallMoleculeColUnitList.add(new SmallMoleculeColUnit(column, param));
    }

    public void addCustom(Param custom) {
        this.customList.add(custom);
    }
}
