package uk.ac.ebi.pride.jmztab.model;

/**
 * A special element which depend on a {@link MetadataElement}, the structure like:
 *
 * {element name}[id]-{subElement subName}
 *
 * User: qingwei
 * Date: 14/10/13
 */
public enum MetadataSubElement {
    ASSAY_QUANTIFICATION_MOD                           (MetadataElement.ASSAY,           "quantification_mod");

    private MetadataElement element;
    private String subName;

    /**
     * A special element which depend on a {@link MetadataElement}, the structure like:
     * {element name}[id]-{subElement subName}
     *
     * @param element SHOULD NOT set null.
     * @param subName SHOULD NOT empty.
     */
    private MetadataSubElement(MetadataElement element, String subName) {
        if (element == null) {
            throw new NullPointerException("Metadata element should not be null!");
        }
        if (MZTabUtils.isEmpty(subName)) {
            throw new IllegalArgumentException("sub element's name should not be empty.");
        }

        this.element = element;
        this.subName = subName;
    }

    /**
     * Used to get a unique name, which used to unique identifier the {@link MetadataProperty}
     * Notice: we use '_' the concatenate element and sub element name.
     *
     * @see MetadataProperty#findProperty(MetadataSubElement, String)
     *
     * @return {element name}_{subElement subName}
     */
    public String getName() {
        return element.getName() + "_" + subName;
    }

    /**
     * @return sub element name.
     */
    public String getSubName() {
        return subName;
    }

    /**
     * @return sub element name.
     */
    @Override
    public String toString() {
        return getSubName();
    }

    /**
     * @return dependent {@link MetadataElement}.
     */
    public MetadataElement getElement() {
        return element;
    }

    /**
     * Find sub element by name with case-insensitive.
     * Notice: we use '_' the concatenate character, for example, assay_quantification_mod.
     *
     * @see #getName()
     */
    public static MetadataSubElement findSubElement(MetadataElement element, String subElementName) {
        if (element == null || subElementName == null) {
            return null;
        }

        MetadataSubElement subElement;
        try {
            subElement = MetadataSubElement.valueOf((element.getName() + "_" + subElementName).trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            subElement = null;
        }

        return subElement;
    }
}
