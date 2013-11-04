package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 09/02/13
 */
public enum MetadataElement {
    TITLE                             ("title"),
    DESCRIPTION                       ("description"),
    SAMPLE_PROCESSING                 ("sample_processing"),
    INSTRUMENT                        ("instrument"),
    SOFTWARE                          ("software"),
    FALSE_DISCOVERY_RATE              ("false_discovery_rate"),
    PUBLICATION                       ("publication"),
    CONTACT                           ("contact"),
    URI                               ("uri"),
    MOD                               ("mod"),
    QUANTIFICATION_METHOD             ("quantification_method"),
    PROTEIN                           ("protein"),
    PEPTIDE                           ("peptide"),
    MS_FILE                           ("ms_file"),
    CUSTOM                            ("custom"),
    SPECIES                           ("species"),
    TISSUE                            ("tissue"),
    CELL_TYPE                         ("cell_type"),
    DISEASE                           ("disease"),
    QUANTIFICATION_REAGENT            ("quantification_reagent"),
    COLUNIT                           ("colunit");

    private String name;

    private MetadataElement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MetadataElement findElement(String name) {
        if (name == null) {
            return null;
        }

        MetadataElement element;
        try {
            element = MetadataElement.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            element = null;
        }

        return element;
    }
}
