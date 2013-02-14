package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * User: Qingwei
 * Date: 13/02/13
 */
public class ModificationList {
    /**
     * CHEMMODs MUST NOT be used for protein/peptide modifications if the respective entry is present
     * in either the PSI-MOD or the UNIMOD ontology.
     */
    private boolean existsMod = false;

    private SplitList<Modification> modList = new SplitList<Modification>(MZTabConstants.COMMA);

    public ModificationList() {

    }

    public boolean add(Modification modification) {
        if (modification.getType() == Modification.Type.MOD || modification.getType() == Modification.Type.UNIMOD) {
            existsMod = true;
        }

        if (modification.getType() == Modification.Type.CHEMMOD && existsMod) {
            throw new IllegalArgumentException("CHEMMODs MUST NOT be used for protein/peptide modifications " +
                    "if the respective entry is present in either the PSI-MOD or the UNIMOD ontology.");
        }

        return this.modList.add(modification);
    }

    @Override
    public String toString() {
        return modList.toString();
    }
}
