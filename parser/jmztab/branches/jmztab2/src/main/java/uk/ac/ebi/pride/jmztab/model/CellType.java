package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * The respective cell type. If there were multiple subsamples analyzed in the respective unit
 * these species should be given using the additional “–{SUB_ID}” part.
 * Subsample specific parameters describing one sample should all contain the same number between the brackets.
 *
 * User: Qingwei, Johannes Griss
 * Date: 04/02/13
 */
public class CellType extends SubUnitElement {
    private Param param;

    public CellType(int id, SubUnit subUnit, Param param) {
        super(id, subUnit);
        this.param = param;
    }

    public Param getParam() {
        return param;
    }

    /**
     * MTD  {Unit_ID}-sub[id]-{element}[id]-{property}    {value.toString}
     */
    public String toString() {
        return printElement(MetadataElement.CELL_TYPE, null, param) + MZTabConstants.NEW_LINE;
    }
}
