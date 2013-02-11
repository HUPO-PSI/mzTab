package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.MINUS;
import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.TAB;

/**
 * MTD  {Unit_ID}-{element}[id]-
 *
 * User: Qingwei
 * Date: 30/01/13
 */
public class UnitElement {
    private int id;
    private Unit unit;

    public UnitElement(int id, Unit unit) {
        if (id < 1) {
            throw new IllegalArgumentException("Unit's id should be great than 0");
        }
        if (unit == null) {
            throw new NullPointerException("Unit can not set null!");
        }

        this.id = id;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public Unit getUnit() {
        return unit;
    }

    /**
     * MTD  {Unit_ID}-{element}[id]    {value.toString}
     */
    protected String printElement(MetadataElement element, Object value) {
        StringBuilder sb = new StringBuilder();

        sb = unit.printPrefix(sb);

        sb.append(element).append("[").append(id).append("]");

        if (value != null) {
            sb.append(TAB).append(value);
        }

        return sb.toString();
    }

    /**
     * MTD  {Unit_ID}-{element}[id]-{property}    {value.toString}
     */
    protected String printProperty(MetadataProperty property, Object value) {
        StringBuilder sb = new StringBuilder();

        sb = unit.printPrefix(sb);

        sb.append(property.getElement()).append("[").append(id).append("]");

        if (property != null) {
            sb.append(MINUS).append(property);
        }

        if (value != null) {
            sb.append(TAB).append(value);
        }

        return sb.toString();
    }
}
