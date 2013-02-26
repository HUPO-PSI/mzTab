package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class ProteinRecord extends AbstractMZTabRecord {
    public ProteinRecord() {
        super(MZTabColumnFactory.getInstance(Section.Protein));
    }

    public ProteinRecord(MZTabColumnFactory factory) {
        super(factory);
    }

    /**
     * PRT  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Protein.getPrefix() + MZTabConstants.TAB + super.toString();
    }
}
