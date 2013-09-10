package uk.ac.ebi.pride.jmztab.model;

import java.util.List;
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
        StringBuilder sb = new StringBuilder();

        printPrefix(sb).append(getReference()).append(MINUS).append(property);

        if (value != null) {
            sb.append(TAB).append(value);
        }

        return sb.toString();
    }

    /**
     * print a list of metadata line.
     */
    protected StringBuilder printList(List<Param> list, MetadataProperty property, StringBuilder sb) {
        for (Param param : list) {
            sb.append(printProperty(property, param)).append(NEW_LINE);
        }

        return sb;
    }
}
