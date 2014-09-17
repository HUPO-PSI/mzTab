package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.COMMA;

/**
 * A list of {@link Modification} reported in the {@link Metadata}, which split with comma character.
 *
 * @author qingwei
 * @since 13/02/13
 */
public class ModificationList {
    private SplitList<Modification> modList = new SplitList<Modification>(COMMA);

    /**
     * Add a {@link Modification} to list.
     *
     * @param modification SHOULD NOT be null.
     *
     * @return if not add successful, return false.
     */
    public boolean add(Modification modification) {
        if (modification == null) {
            throw new NullPointerException("Can not add null modification to list.");
        }

        return this.modList.add(modification);
    }

    @Override
    public String toString() {
        return modList.toString();
    }
}
