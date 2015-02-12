package uk.ac.ebi.pride.jmztab.model;

/**
 * Fixed Modification used to identify peptides and proteins of the mzTab file (e.g. carbamidomethylation,
 * oxidation, labels/tags).
 *
 * @author qingwei
 * @since 14/10/13
 */
public class FixedMod extends Mod {
    /**
     * Define a fixed modification.
     * @param id non-negative integer.
     */
    public FixedMod(int id) {
        super(MetadataElement.FIXED_MOD, id);
    }
}
