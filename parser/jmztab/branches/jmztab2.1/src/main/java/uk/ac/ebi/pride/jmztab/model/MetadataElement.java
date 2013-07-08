package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public enum MetadataElement {
    MZTAB                             ("mzTab"),
    TITLE                             ("title"),
    DESCRIPTION                       ("description"),
    SAMPLE_PROCESSING                 ("sample_processing"),
    INSTRUMENT                        ("instrument"),
    SOFTWARE                          ("software"),
    FALSE_DISCOVERY_RATE              ("false_discovery_rate"),
    PUBLICATION                       ("publication"),
    CONTACT                           ("contact"),
    URI                               ("uri"),
    FIXED_MOD                         ("fixed_mod"),
    VARIABLE_MOD                      ("variable_mod"),
    QUANTIFICATION_METHOD             ("quantification_method"),
    PROTEIN                           ("protein"),
    PEPTIDE                           ("peptide"),
    SMALL_MOLECULE                    ("small_molecule"),
    MS_RUN                            ("ms_run"),
    CUSTOM                            ("custom"),
    SAMPLE                            ("sample"),
    ASSAY                             ("assay"),
    STUDY_VARIABLE                    ("study_variable"),
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
