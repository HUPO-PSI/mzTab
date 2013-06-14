package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class Sample extends IndexedElement {
    private SortedMap<Integer, Param> speciesMap = new TreeMap<Integer, Param>();
    private SortedMap<Integer, Param> tissueMap = new TreeMap<Integer, Param>();
    private SortedMap<Integer, Param> cellTypeMap = new TreeMap<Integer, Param>();
    private SortedMap<Integer, Param> diseaseMap = new TreeMap<Integer, Param>();
    private String description;
    private List<Param> customList = new ArrayList<Param>();

    public Sample(int id) {
        super(MetadataElement.SAMPLE, id);
    }

    public SortedMap<Integer, Param> getSpeciesMap() {
        return speciesMap;
    }

    public SortedMap<Integer, Param> getTissueMap() {
        return tissueMap;
    }

    public SortedMap<Integer, Param> getCellTypeMap() {
        return cellTypeMap;
    }

    public SortedMap<Integer, Param> getDiseaseMap() {
        return diseaseMap;
    }

    public List<Param> getCustomList() {
        return customList;
    }

    public void addSpecies(Integer pid, Param param) {
        speciesMap.put(pid, param);
    }

    public void addTissue(Integer pid, Param param) {
        tissueMap.put(pid, param);
    }

    public void addCellType(Integer pid, Param param) {
        cellTypeMap.put(pid, param);
    }

    public void addDisease(Integer pid, Param param) {
        diseaseMap.put(pid, param);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addCustom(Param custom) {
        this.customList.add(custom);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb = printMap(speciesMap, SAMPLE_SPECIES, sb);
        sb = printMap(tissueMap, SAMPLE_TISSUE, sb);
        sb = printMap(cellTypeMap, SAMPLE_CELL_TYPE, sb);
        sb = printMap(diseaseMap, SAMPLE_DISEASE, sb);

        if (description != null) {
            sb.append(printProperty(SAMPLE_DESCRIPTION, description)).append(NEW_LINE);
        }

        for (Param custom : customList) {
            sb.append(printProperty(SAMPLE_CUSTOM, custom)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
