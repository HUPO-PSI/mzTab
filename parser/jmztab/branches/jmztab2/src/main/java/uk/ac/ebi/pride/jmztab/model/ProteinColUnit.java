package uk.ac.ebi.pride.jmztab.model;

/**
 * {UNIT_ID}-colunit-protein
 *
 *  Defines the used unit for a column in the protein section.
 *  The format of the value has to be {column name}={Parameter defining the unit}
 *
 * User: Qingwei
 * Date: 31/01/13
 */
public class ProteinColUnit {
    private ProteinColumn column;
    private Param value;

    public ProteinColUnit(ProteinColumn column, Param value) {
        if (column == null) {
            throw new NullPointerException("Protein Column can not set null");
        }

        if (value == null) {
            throw new NullPointerException("Param can not set null");
        }

        this.column = column;
        this.value = value;
    }

   /**
    * MTD  {UNIT_ID}-colunit-protein   {column name}={Parameter defining the unit}
    */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(column).append("=").append(value.toString());

        return sb.toString();
    }
}
