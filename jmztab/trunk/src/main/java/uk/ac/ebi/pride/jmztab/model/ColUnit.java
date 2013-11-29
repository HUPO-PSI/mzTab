package uk.ac.ebi.pride.jmztab.model;

/**
 * {column header} = {parameter defining the unit}
 *
 * User: Qingwei
 * Date: 04/06/13
 */
public class ColUnit {
    private MZTabColumn column;
    private Param value;

    /**
     * Defines the used unit for a column in the peptide/protein/small_molecule section.
     * The format of the value has to be {column name}={Parameter defining the unit}
     */
    ColUnit(MZTabColumn column, Param value) {
        if (column == null) {
            throw new NullPointerException("MZTabColumn can not set null");
        }

        if (value == null) {
            throw new NullPointerException("Param can not set null");
        }

        if (column instanceof AbundanceColumn) {
            throw new IllegalArgumentException("Colunit MUST NOT be used to define a unit for quantification columns.");
        }

        this.column = column;
        this.value = value;
    }

    public Param getValue() {
        return value;
    }

    public void setValue(Param value) {
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
