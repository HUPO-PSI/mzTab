package uk.ac.ebi.pride.jmztab.model;

/**
 * Variable Modification used to identify peptides and proteins of the mzTab file (e.g. carbamidomethylation,
 * oxidation, labels/tags).
 *
 * User: qingwei
 * Date: 14/10/13
 */
public class VariableMod extends Mod {
    /**
     * Define a variable modification.
     * @param id non-negative integer.
     */
    public VariableMod(int id) {
        super(MetadataElement.VARIABLE_MOD, id);
    }
}
