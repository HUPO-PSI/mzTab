package uk.ac.ebi.pride.jmztab.model;

/**
 * The respective disease. If there were multiple subsamples analyzed in the respective unit
 * these species should be given using the additional “–{SUB_ID}” part.
 * Subsample specific parameters describing one sample should all contain the same number between the brackets.
 *
 * User: Qingwei
 * Date: 04/02/13
 */
public class Disease extends SubUnitElement {
    private Param param;

    public Disease(int id, SubUnit subUnit, Param param) {
        super(id, subUnit);

        if (param == null) {
            throw new NullPointerException("Disease param value can not set null!");
        }

        this.param = param;
    }

    public Param getParam() {
        return param;
    }

    /**
     * MTD  {Unit_ID}-sub[id]-{element}[id]-{property}    {value.toString}
     */
    public String toString() {
        return printElement(MetadataElement.DISEASE, null, param) + MZTabConstants.NEW_LINE;
    }
}
