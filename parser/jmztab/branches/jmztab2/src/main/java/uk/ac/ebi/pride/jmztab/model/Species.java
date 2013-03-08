package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * The respective species. Multiple species can be supplied. If there were multiple subsamples
 * analyzed in the respective unit these species should be given using the additional “–{SUB_ID}” part.
 * Subsample specific parameters describing one sample should all contain the same number between the brackets.
 *
 * User: Qingwei, Johannes Griss
 * Date: 04/02/13
 */
public class Species extends SubUnitElement {
    private Param param;

    public Species(int id, SubUnit subUnit, Param param) {
        super(id, subUnit);

        if (param == null) {
            throw new NullPointerException("Species param value can not set null!");
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
        return printElement(MetadataElement.SPECIES, null, param) + NEW_LINE;
    }
}
