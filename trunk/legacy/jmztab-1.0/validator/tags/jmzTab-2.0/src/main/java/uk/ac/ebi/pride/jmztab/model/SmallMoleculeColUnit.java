package uk.ac.ebi.pride.jmztab.model;

/**
 * {UNIT_ID}-colunit-small_molecule
 *
 * Defines the used unit for a column in the small molecule section.
 * The format of the value has to be {column name}={Parameter defining the unit}
 *
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public class SmallMoleculeColUnit {
    private MZTabColumn column;
    private Param value;

    public SmallMoleculeColUnit(MZTabColumn column, Param value) {
        if (column == null) {
            throw new NullPointerException("Small Molecule Column can not set null");
        }

        if (value == null) {
            throw new NullPointerException("Param can not set null");
        }

        this.column = column;
        this.value = value;
    }

    /**
     * {column name}={Parameter defining the unit}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(column.getHeader()).append("=").append(value.toString());

        return sb.toString();
    }
}
