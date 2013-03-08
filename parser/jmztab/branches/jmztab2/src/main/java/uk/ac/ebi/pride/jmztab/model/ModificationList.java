package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.COMMA;

/**
 * User: Qingwei
 * Date: 13/02/13
 */
public class ModificationList {
    private SplitList<Modification> modList = new SplitList<Modification>(COMMA);

    public boolean add(Modification modification) {
        return this.modList.add(modification);
    }

    @Override
    public String toString() {
        return modList.toString();
    }
}
