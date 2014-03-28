package uk.ac.ebi.pride.jmztab.model;

/**
 * Defines the unit for the data reported in a column of the protein, peptide, psm and small molecule section.
 * The format of the value has to be {column name}={Parameter defining the unit}.
 * This field MUST NOT be used to define a unit for quantification columns. The unit
 * used for protein quantification values MUST be set in protein-quantification_unit.
 *
 * User: Qingwei
 * Date: 04/06/13
 */
public class ColUnit {
    private MZTabColumn column;
    private Param value;

    /**
     * Defines the used unit for a column in the peptide/protein/PSM/small_molecule section.
     * The format of the value has to be {column name}={Parameter defining the unit}
     *
     * @param column should not set null, and SHOULD not be a instance of {@link AbundanceColumn}.
     * @param value SHOULD be a {@link Param}
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

    /**
     * Get colunit {@link Param} value.
     */
    public Param getValue() {
        return value;
    }

    /**
     * Set {@link Param} value for colunit.
     *
     * @param value should not set null!
     */
    public void setValue(Param value) {
        if (value == null) {
            throw new NullPointerException("colunit parameter value should not set null!");
        }

        this.value = value;
    }

    /**
     * Output like: {column header name}={Parameter defining the unit}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(column.getHeader()).append("=").append(value.toString());

        return sb.toString();
    }
}
