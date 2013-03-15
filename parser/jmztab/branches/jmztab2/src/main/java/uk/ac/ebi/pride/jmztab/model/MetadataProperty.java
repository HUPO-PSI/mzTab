package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 09/02/13
 */
public enum MetadataProperty {
    INSTRUMENT_NAME                (MetadataElement.INSTRUMENT,     "name"),
    INSTRUMENT_SOURCE              (MetadataElement.INSTRUMENT,     "source"),
    INSTRUMENT_ANALYZER            (MetadataElement.INSTRUMENT,     "analyzer"),
    INSTRUMENT_DETECTOR            (MetadataElement.INSTRUMENT,     "detector"),

    SOFTWARE_SETTING               (MetadataElement.SOFTWARE,       "setting"),

    CONTACT_NAME                   (MetadataElement.CONTACT,        "name"),
    CONTACT_AFFILIATION            (MetadataElement.CONTACT,        "affiliation"),
    CONTACT_EMAIL                  (MetadataElement.CONTACT,        "email"),

    PROTEIN_QUANTIFICATION_UNIT    (MetadataElement.PROTEIN,        "protein_quantification_unit"),
    PEPTIDE_QUANTIFICATION_UNIT    (MetadataElement.PEPTIDE,        "peptide_quantification_unit"),

    MS_FILE_FORMAT                 (MetadataElement.MS_FILE,        "format"),
    MS_FILE_LOCATION               (MetadataElement.MS_FILE,        "location"),
    MS_FILE_ID_FORMAT              (MetadataElement.MS_FILE,        "id_format"),

    COLUNIT_PROTEIN                (MetadataElement.COLUNIT,        "protein"),
    COLUNIT_PEPTIDE                (MetadataElement.COLUNIT,        "peptide"),
    COLUNIT_SMALL_MOLECULE         (MetadataElement.COLUNIT,        "small_molecule");

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

    public static MetadataProperty findProperty(String elementName, String propertyName) {
        if (elementName == null || propertyName == null) {
            return null;
        }

        MetadataProperty property;
        try {
            property = MetadataProperty.valueOf((elementName + "_" + propertyName).trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            property = null;
        }

        return property;
    }
}
