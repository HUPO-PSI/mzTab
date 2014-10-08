package uk.ac.ebi.pride.jmztab.model;

import java.util.List;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * Index-organized {@link MetadataElement}, index value is positive integer.
 *
 * @author qingwei
 * @since 23/05/13
 */
public class IndexedElement {
    private MetadataElement element;
    private Integer id;

    /**
     * Create a index-organized {@link MetadataElement}, index value is non-negative integer.
     * @param element SHOULD NOT be null.
     * @param id SHOULD be non-negative integer.
     */
    public IndexedElement(MetadataElement element, int id) {
        if (element == null) {
            throw new NullPointerException("MetadataElement can not set null!");
        }
        this.element = element;

        setId(id);
    }

    /**
     * @return indexed element.
     */
    public MetadataElement getElement() {
        return element;
    }

    /**
     * @return indexed number.
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id value should great than 0!");
        }

        this.id = id;
    }

    /**
     * @return element[id]
     */
    public String getReference() {
        StringBuilder sb = new StringBuilder();

        sb.append(element).append("[").append(id).append("]");

        return sb.toString();
    }

    /**
     * Print metadata indexed element line prefix String, structure like:
     * MTD  {...}
     */
    protected StringBuilder printPrefix(StringBuilder sb) {
        return sb.append(Section.Metadata.getPrefix()).append(TAB);
    }

    /**
     * Print indexed element with value to a String, structure like:
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
     * Print indexed element with property and value to a String, structure like:
     *
     * MTD  {element}[id]-{property}    {value.toString}
     */
    protected String printProperty(MetadataProperty property, Object value) {
        StringBuilder sb = new StringBuilder();

        printPrefix(sb).append(getReference()).append(MINUS).append(property);

        if (value != null) {
            sb.append(TAB).append(value);
        }
        else {
            sb.append(TAB).append(NULL);
        }

        return sb.toString();
    }

    /**
     * Print indexed element with property and value to a String, the property has sub index, the structure like:
     *
     * MTD  {element}[id]-{property}[sub-id]    {value.toString}
     */
    protected String printProperty(MetadataProperty property, int subId, Object value) {
        StringBuilder sb = new StringBuilder();

        printPrefix(sb).append(getReference()).append(MINUS).append(property).append("[").append(subId).append("]");

        if (value != null) {
            sb.append(TAB).append(value).append(NEW_LINE);
        }

        return sb.toString();
    }

    /**
     * Print a list of metadata line. The sub index start from 1.
     * MTD  {element}[id]-{property}[1]    {value.toString}
     * MTD  {element}[id]-{property}[2]    {value.toString}
     * MTD  {element}[id]-{property}[3]    {value.toString}
     * ....
     */
    protected StringBuilder printList(List<?> list, MetadataProperty property, StringBuilder sb) {
        Object param;
        for (int i = 0; i < list.size(); i++) {
            param = list.get(i);
            sb.append(printProperty(property, i + 1, param));
        }

        return sb;
    }
}
