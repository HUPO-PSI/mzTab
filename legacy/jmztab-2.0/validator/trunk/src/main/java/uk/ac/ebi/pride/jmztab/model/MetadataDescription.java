package uk.ac.ebi.pride.jmztab.model;

/**
 * User: qingwei
 * Date: 30/04/13
 */
public class MetadataDescription {
    public static final String MZTAB = "mzTab";

    public enum Element {
        VERSION                         ("version");

        private String name;

        private Element(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static Element findElement(String name) {
        if (name == null) {
            return null;
        }

        Element element;
        try {
            element = Element.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            element = null;
        }

        return element;
    }

    private String version;

    public MetadataDescription() {
    }

    public MetadataDescription(String version) {
        if (version == null || version.trim().length() == 0) {
            throw new IllegalArgumentException("Version should not empty!");
        }

        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * MTD  mzTab-{element}  {value}
     */
    private String printElement(Element element, Object value) {
        StringBuilder sb = new StringBuilder();

        sb.append(Section.Metadata.getPrefix()).append(MZTabConstants.TAB);
        sb.append(MZTAB).append(MZTabConstants.MINUS).append(element).append(MZTabConstants.TAB);
        sb.append(value).append(MZTabConstants.NEW_LINE);

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (version != null && version.trim().length() != 0) {
            sb.append(printElement(Element.VERSION, getVersion()));
        }

        return sb.toString();
    }
}
