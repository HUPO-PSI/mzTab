package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class Sample extends IndexedElement {
    private List<Param> speciesList = new ArrayList<Param>();
    private List<Param> tissueList = new ArrayList<Param>();
    private List<Param> cellTypeList = new ArrayList<Param>();
    private List<Param> diseaseList = new ArrayList<Param>();
    private String description;
    private List<Param> customList = new ArrayList<Param>();

    public Sample(int id) {
        super(MetadataElement.SAMPLE, id);
    }

    public List<Param> getSpeciesList() {
        return speciesList;
    }

    public List<Param> getTissueList() {
        return tissueList;
    }

    public List<Param> getCellTypeList() {
        return cellTypeList;
    }

    public List<Param> getDiseaseList() {
        return diseaseList;
    }

    public List<Param> getCustomList() {
        return customList;
    }

    public void addSpecies(Param param) {
        speciesList.add(param);
    }

    public void addTissue(Param param) {
        tissueList.add(param);
    }

    public void addCellType(Param param) {
        cellTypeList.add(param);
    }

    public void addDisease(Param param) {
        diseaseList.add(param);
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

        sb = printList(speciesList, SAMPLE_SPECIES, sb);
        sb = printList(tissueList, SAMPLE_TISSUE, sb);
        sb = printList(cellTypeList, SAMPLE_CELL_TYPE, sb);
        sb = printList(diseaseList, SAMPLE_DISEASE, sb);

        if (description != null) {
            sb.append(printProperty(SAMPLE_DESCRIPTION, description)).append(NEW_LINE);
        }

        for (Param custom : customList) {
            sb.append(printProperty(SAMPLE_CUSTOM, custom)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
