package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * {Unit_ID}-sub[id]-{element}[id]-
 *
 * User: Qingwei
 * Date: 31/01/13
 */
public class SubUnitElement {
    private SubUnit subUnit;
    private int id;

    public SubUnitElement(int id, SubUnit subUnit) {
        if (subUnit == null) {
            throw new NullPointerException("SubUnit should create first.");
        }

        if (id < 1) {
            throw new IllegalArgumentException("sub[id] :" + id + " should great than 0");
        }

        this.subUnit = subUnit;
        this.id = id;
    }

    public SubUnit getSubUnit() {
        return subUnit;
    }

    public void setSubUnit(SubUnit subUnit) {
        this.subUnit = subUnit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * MTD  {Unit_ID}-sub[id]-{element}[id]-{property}    {value.toString}
     */
    protected String printElement(MetadataElement element, String property, Object value) {
        StringBuilder sb = new StringBuilder();

        sb = subUnit.printPrefix(sb);
        sb.append(element).append("[").append(id).append("]");


        if (property != null) {
            sb.append(MINUS).append(property);
        }

        if (value != null) {
            sb.append(TAB).append(value);
        }

        return sb.toString();
    }
}
