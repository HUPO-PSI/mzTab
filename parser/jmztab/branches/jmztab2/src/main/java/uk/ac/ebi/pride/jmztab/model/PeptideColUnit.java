package uk.ac.ebi.pride.jmztab.model;

/**
 * {UNIT_ID}-colunit-{section}
 *
 *  Defines the used unit for a column in the protein/peptide/small_molecule section.
 *  The format of the value has to be {column name}={Parameter defining the unit}
 *
 * User: Qingwei
 * Date: 31/01/13
 */
public class PeptideColUnit {
    private PeptideColumn column;
    private Param value;

    /**
     * Defines the used unit for a column in the peptide section.
     * The format of the value has to be {column name}={Parameter defining the unit}
     */
    public PeptideColUnit(PeptideColumn column, Param value) {
        if (column == null) {
            throw new NullPointerException("Peptide Column can not set null");
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

        sb.append(column).append("=").append(value.toString());

        return sb.toString();
    }
}
