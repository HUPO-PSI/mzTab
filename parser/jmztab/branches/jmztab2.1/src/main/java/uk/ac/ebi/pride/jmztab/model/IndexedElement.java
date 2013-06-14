package uk.ac.ebi.pride.jmztab.model;

import java.util.SortedMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.MINUS;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class IndexedElement {
    private MetadataElement element;
    private int id;

    public IndexedElement(MetadataElement element, int id) {
        if (element == null) {
            throw new NullPointerException("MetadataElement can not set null!");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("id value should great than 0!");
        }

        this.element = element;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getReference() {
        StringBuilder sb = new StringBuilder();

        sb.append(element).append("[").append(id).append("]");

        return sb.toString();
    }

    /**
     * MTD  {...}
     */
    protected StringBuilder printPrefix(StringBuilder sb) {
        return sb.append(Section.Metadata.getPrefix()).append(TAB);
    }

    /**
     * MTD  {element}[id]    {value.toString}
     */
    protected String printElement(Object value) {
        StringBuilder sb = new StringBuilder();

        printPrefix(sb).append(getReference());

        if (value != null) {
            sb.append(TAB).append(value);
        }

        return sb.toString();
    }

    /**
     * MTD  {element}[id]-{property}    {value.toString}
     */
    protected String printProperty(MetadataProperty property, Object value) {
        return printProperty(property, null, value);
    }

    /**
     * MTD  {element}[id]-{property}[pid]    {value.toString}
     */
    protected String printProperty(MetadataProperty property, Integer pid, Object value) {
        StringBuilder sb = new StringBuilder();

        printPrefix(sb).append(getReference()).append(MINUS).append(property);
        if (pid != null) {
            sb.append("[").append(pid).append("]");
        }

        if (value != null) {
            sb.append(TAB).append(value);
        }

        return sb.toString();
    }

    /**
     * MTD  {element}[id]-{property}[pid]   value
     */
    protected StringBuilder printMap(SortedMap<Integer, Param> map, MetadataProperty property, StringBuilder sb) {
        Param param;
        for (Integer pid : map.keySet()) {
            param = map.get(pid);
            sb.append(printProperty(property, pid, param)).append(NEW_LINE);
        }

        return sb;
    }
}
