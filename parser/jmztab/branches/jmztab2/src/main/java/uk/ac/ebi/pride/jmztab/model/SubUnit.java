package uk.ac.ebi.pride.jmztab.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.*;

/**
 * If there were multiple subsamples analyzed in the respective unit these species should be
 * given using the additional “–{SUB_ID}” part. Subsample specific parameters describing one sample
 * should all contain the same number between the brackets.
 *
 * {UNIT_ID}-(SUB_ID)
 *
 * User: Qingwei, Johannes Griss
 * Date: 04/02/13
 */
public class SubUnit extends Unit {
    public final static String SUB = "sub";

    private Integer subId;

    private Map<Integer, Species> speciesMap = new TreeMap<Integer, Species>();
    private Map<Integer, Tissue> tissueMap = new TreeMap<Integer, Tissue>();
    private Map<Integer, CellType> cellTypeMap = new TreeMap<Integer, CellType>();
    private Map<Integer, Disease> diseaseMap = new TreeMap<Integer, Disease>();

    private String description;
    private Param quantificationReagent;
    private List<Param> customList = new ArrayList<Param>();

    /**
     * If subId is null, SubUnit same with Unit.
     */
    public SubUnit(String unitId, Integer subId) {
        super(unitId);

        if (subId != null && subId < 1) {
            throw new IllegalArgumentException("sub_id should be great than 0");
        }

        this.subId = subId;
    }

    public Integer getSubId() {
        return subId;
    }

    public void setSubId(Integer subId) {
        Integer oldSubId = this.subId;
        this.subId = subId;
        firePropertyChange(OperationCenter.SUB_UNIT_ID, oldSubId, subId);
    }

    @Override
    public String getIdentifier() {
        StringBuilder sb = new StringBuilder();

        sb.append(getUnitId()).append(MINUS).append(SUB);
        if (subId != null) {
            sb.append("[").append(subId).append("]");
        }

        return sb.toString();
    }

    /**
     * temporary function.
     * if identifier like {Unit_id}-sub, remove -sub
     * if identifier like {Unit_id}-sub[id], do nothing.
     */
    protected StringBuilder printPrefix(StringBuilder sb) {
        sb.append(Section.Metadata.getPrefix()).append(TAB);

        String identifier = getIdentifier();
        if (! identifier.contains("[")) {
            identifier = identifier.substring(0, identifier.indexOf("-"));
        }

        sb.append(identifier).append(MINUS);

        return sb;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb = printMap(speciesMap, SPECIES.toString(), sb);
        sb = printMap(tissueMap, TISSUE.toString(), sb);
        sb = printMap(cellTypeMap, CELL_TYPE.toString(), sb);
        sb = printMap(diseaseMap, DISEASE.toString(), sb);

        if (description != null) {
            printPrefix(sb).append(DESCRIPTION).append(TAB).append(description).append(NEW_LINE);
        }

        if (quantificationReagent != null) {
            printPrefix(sb).append(QUANTIFICATION_REAGENT).append(TAB).append(quantificationReagent).append(NEW_LINE);
        }

        for (Param custom : customList) {
            printPrefix(sb).append(CUSTOM).append(TAB).append(custom).append(NEW_LINE);
        }

        return sb.toString();
    }

    public Map<Integer, Species> getSpeciesMap() {
        return speciesMap;
    }

    public Map<Integer, Tissue> getTissueMap() {
        return tissueMap;
    }

    public Map<Integer, CellType> getCellTypeMap() {
        return cellTypeMap;
    }

    public Map<Integer, Disease> getDiseaseMap() {
        return diseaseMap;
    }

    public List<Param> getCustomList() {
        return customList;
    }

    public boolean addSpecies(Integer id, Param param) {
        Species species;
        if (speciesMap.containsKey(id)) {
            return false;
        } else {
            species = new Species(id, this, param);
            speciesMap.put(id, species);
            return true;
        }
    }

    public boolean addTissue(Integer id, Param param) {
        Tissue tissue;
        if (tissueMap.containsKey(id)) {
            return false;
        } else {
            tissue = new Tissue(id, this, param);
            tissueMap.put(id, tissue);
            return true;
        }
    }

    public boolean addCellType(Integer id, Param param) {
        CellType cellType;
        if (cellTypeMap.containsKey(id)) {
            return false;
        } else {
            cellType = new CellType(id, this, param);
            cellTypeMap.put(id, cellType);
            return true;
        }
    }

    public boolean addDisease(Integer id, Param param) {
        Disease disease;
        if (diseaseMap.containsKey(id)) {
            return false;
        } else {
            disease = new Disease(id, this, param);
            diseaseMap.put(id, disease);
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

    public void setQuantificationReagent(Param quantificationReagent) {
        this.quantificationReagent = quantificationReagent;
    }

    public void addCustom(Param custom) {
        this.customList.add(custom);
    }

    public String getDescription() {
        return description;
    }

    public Param getQuantificationReagent() {
        return quantificationReagent;
    }

    @Deprecated
    public boolean setTitle(String title) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addSampleProcessing(Integer id, SplitList<Param> sampleProcessing) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addInstrumentName(Integer id, Param name) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addInstrumentSource(Integer id, Param source) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addInstrumentAnalyzer(Integer id, Param analyzer) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addInstrumentDetector(Integer id, Param detector) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addSoftwareParam(Integer id, Param param) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public void addSoftwareSetting(Integer id, String setting) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public void addFalseDiscoveryRateParam(Param param) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public void addPublication(Publication publication) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addContactAffiliation(Integer id, String affiliation) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addContactEmail(Integer id, String email) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public void addUri(java.net.URI uri) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public void addModParam(Param param) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public void setQuantificationMethod(Param quantificationMethod) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public void setProteinQuantificationUnit(Param proteinQuantificationUnit) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public void setPeptideQuantificationUnit(Param peptideQuantificationUnit) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addMsFileFormat(Integer id, CVParam format) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addMsFileLocation(Integer id, URL location) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }

    @Deprecated
    public boolean addMsFileIdFormat(Integer id, CVParam idFormat) {
        throw new UnsupportedOperationException("This operation not support in SubUnit");
    }
}
