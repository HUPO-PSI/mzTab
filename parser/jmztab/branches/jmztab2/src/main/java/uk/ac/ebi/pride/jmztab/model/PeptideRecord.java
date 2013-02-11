package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class PeptideRecord extends AbstractMZTabRecord {
    public PeptideRecord() {
        super(MZTabColumnFactory.getInstance(Section.Peptide));
    }

    /**
     * PEP  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Peptide.getPrefix() + MZTabConstants.TAB + super.toString();
    }
}
