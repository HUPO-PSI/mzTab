package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 09/02/13
 */
public enum MetadataProperty {
    INSTRUMENT_NAME(MetadataElement.INSTRUMENT, "name"),
    INSTRUMENT_SOURCE(MetadataElement.INSTRUMENT, "source"),
    INSTRUMENT_ANALYZER(MetadataElement.INSTRUMENT, "analyzer"),
    INSTRUMENT_DETECTOR(MetadataElement.INSTRUMENT, "detector"),

    SOFTWARE_SETTING(MetadataElement.SOFTWARE, "setting"),

    CONTACT_NAME(MetadataElement.CONTACT, "name"),
    CONTACT_AFFILIATION(MetadataElement.CONTACT, "affiliation"),
    CONTACT_EMAIL(MetadataElement.CONTACT, "email"),

    MSFILE_FORMAT(MetadataElement.MS_FILE, "format"),
    MSFILE_LOCATION(MetadataElement.MS_FILE, "location"),
    MSFILE_ID_FORMAT(MetadataElement.MS_FILE, "id_format");

    private String name;
    private MetadataElement element;

    private MetadataProperty(MetadataElement element, String name) {
        this.element = element;
        this.name = name;
    }

    public MetadataElement getElement() {
        return element;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
