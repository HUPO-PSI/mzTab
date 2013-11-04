package uk.ac.ebi.pride.jmztab.model;

/**
 * User: qingwei
 * Date: 14/10/13
 */
public enum MetadataSubElement {
    ASSAY_QUANTIFICATION_MOD                           (MetadataElement.ASSAY,           "quantification_mod");

    private MetadataElement element;
    private String subName;

    private MetadataSubElement(MetadataElement element, String subName) {
        this.element = element;
        this.subName = subName;
    }

    public String getName() {
        return element.getName() + "_" + subName;
    }

    public String getSubName() {
        return subName;
    }

    @Override
    public String toString() {
        return getSubName();
    }

    public MetadataElement getElement() {
        return element;
    }

    /**
     * subElementName should include elementName.
     * For example: assay_quantification_mod
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
