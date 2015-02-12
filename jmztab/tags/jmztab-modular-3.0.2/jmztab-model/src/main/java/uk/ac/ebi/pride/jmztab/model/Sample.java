package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * A biological material that has been analysed, to which descriptors of species, cell/tissue type etc. can be attached.
 * In all of types of mzTab file, these MAY be reported in the metadata section as sample[1-n]-description.
 * Samples are NOT MANDATORY in mzTab, since many software packages cannot determine what type of sample was analysed
 * (e.g. whether biological or technical replication was performed).
 *
 * @author qingwei
 * @since 23/05/13
 */
public class Sample extends IndexedElement {
    private List<Param> speciesList = new ArrayList<Param>();
    private List<Param> tissueList = new ArrayList<Param>();
    private List<Param> cellTypeList = new ArrayList<Param>();
    private List<Param> diseaseList = new ArrayList<Param>();
    private String description;
    private List<Param> customList = new ArrayList<Param>();

    /**
     * A biological material that has been analysed, to which descriptors of species, cell/tissue type etc. can be attached.
     * In all of types of mzTab file, these MAY be reported in the metadata section as sample[1-n]-description.
     * Samples are NOT MANDATORY in mzTab, since many software packages cannot determine what type of sample was analysed
     * (e.g. whether biological or technical replication was performed).
     *
     * @param id SHOULD be positive integer
     */
    public Sample(int id) {
        super(MetadataElement.SAMPLE, id);
    }

    /**
     * @return The respective species of the samples analysed.
     */
    public List<Param> getSpeciesList() {
        return speciesList;
    }

    /**
     * @return The respective tissue(s) of the sample.
     */
    public List<Param> getTissueList() {
        return tissueList;
    }

    /**
     * @return The respective cell type(s) of the sample.
     */
    public List<Param> getCellTypeList() {
        return cellTypeList;
    }

    /**
     * @return The respective disease(s) of the sample.
     */
    public List<Param> getDiseaseList() {
        return diseaseList;
    }

    /**
     * @return Parameters describing the sample's additional properties
     */
    public List<Param> getCustomList() {
        return customList;
    }

    /**
     * Add a species for sample.
     * @param param SHOULD NOT set null.
     */
    public void addSpecies(Param param) {
        speciesList.add(param);
    }

    /**
     * Add a tissue for sample.
     * @param param SHOULD NOT set null.
     */
    public void addTissue(Param param) {
        tissueList.add(param);
    }

    /**
     * Add a cell type for sample.
     * @param param SHOULD NOT set null.
     */
    public void addCellType(Param param) {
        cellTypeList.add(param);
    }

    /**
     * Add a disease for sample.
     * @param param SHOULD NOT set null.
     */
    public void addDisease(Param param) {
        diseaseList.add(param);
    }

    /**
     * @return A human readable description of the sample.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set a human readable description of the sample.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Add a custom parameter for sample.
     * @param custom SHOULD NOT set null.
     */
    public void addCustom(Param custom) {
        this.customList.add(custom);
    }

    /**
     * Print a sample in metadata to string. The structure like following:
     * <ul>
     *     <li>MTD	sample[1]-species[1]	[NEWT, 9606, Homo sapiens (Human), ]</li>
     *     <li>MTD	sample[1]-tissue[1]	[BTO, BTO:0000759, liver, ]</li>
     *     <li>MTD	sample[1]-cell_type[1]	[CL, CL:0000182, hepatocyte, ]</li>
     *     <li>MTD	sample[1]-disease[1]	[DOID, DOID:684, hepatocellular carcinoma, ]</li>
     *     <li>MTD	sample[1]-description	Hepatocellular carcinoma samples.</li>
     *     <li>MTD	sample[1]-custom[1]	[, , Extraction date, 2011-12-21]</li>
     * </ul>
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        printList(speciesList, SAMPLE_SPECIES, sb);
        printList(tissueList, SAMPLE_TISSUE, sb);
        printList(cellTypeList, SAMPLE_CELL_TYPE, sb);
        printList(diseaseList, SAMPLE_DISEASE, sb);

        if (description != null) {
            sb.append(printProperty(SAMPLE_DESCRIPTION, description)).append(NEW_LINE);
        }

        printList(customList, SAMPLE_CUSTOM, sb);

        return sb.toString();
    }
}
