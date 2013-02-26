package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * User: Qingwei, Johannes Griss
 * Date: 30/01/13
 */
public class SmallMoleculeRecord extends AbstractMZTabRecord {
    public SmallMoleculeRecord() {
        super(MZTabColumnFactory.getInstance(Section.Small_Molecule));
    }

    public SmallMoleculeRecord(MZTabColumnFactory factory) {
        super(factory);
    }

    /**
     * SML  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Small_Molecule.getPrefix() + MZTabConstants.TAB + super.toString();
    }
}
