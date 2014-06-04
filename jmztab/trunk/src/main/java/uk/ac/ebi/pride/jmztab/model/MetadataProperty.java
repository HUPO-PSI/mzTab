package uk.ac.ebi.pride.jmztab.model;

/**
 * Define a property in metadata, which depend on the {@link MetadataElement} or {@link MetadataSubElement}
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public enum MetadataProperty {
    MZTAB_VERSION                         (MetadataElement.MZTAB,                               "version"),
    MZTAB_MODE                            (MetadataElement.MZTAB,                               "mode"),
    MZTAB_TYPE                            (MetadataElement.MZTAB,                               "type"),
    MZTAB_ID                              (MetadataElement.MZTAB,                               "ID"),

    INSTRUMENT_NAME                       (MetadataElement.INSTRUMENT,                          "name"),
    INSTRUMENT_SOURCE                     (MetadataElement.INSTRUMENT,                          "source"),
    INSTRUMENT_ANALYZER                   (MetadataElement.INSTRUMENT,                          "analyzer"),
    INSTRUMENT_DETECTOR                   (MetadataElement.INSTRUMENT,                          "detector"),

    SOFTWARE_SETTING                      (MetadataElement.SOFTWARE,                            "setting"),

    CONTACT_NAME                          (MetadataElement.CONTACT,                             "name"),
    CONTACT_AFFILIATION                   (MetadataElement.CONTACT,                             "affiliation"),
    CONTACT_EMAIL                         (MetadataElement.CONTACT,                             "email"),

    FIXED_MOD_SITE                        (MetadataElement.FIXED_MOD,                           "site"),
    FIXED_MOD_POSITION                    (MetadataElement.FIXED_MOD,                           "position"),

    VARIABLE_MOD_SITE                     (MetadataElement.VARIABLE_MOD,                         "site"),
    VARIABLE_MOD_POSITION                 (MetadataElement.VARIABLE_MOD,                         "position"),

    PROTEIN_QUANTIFICATION_UNIT           (MetadataElement.PROTEIN,                             "quantification_unit"),
    PEPTIDE_QUANTIFICATION_UNIT           (MetadataElement.PEPTIDE,                             "quantification_unit"),
    SMALL_MOLECULE_QUANTIFICATION_UNIT    (MetadataElement.SMALL_MOLECULE,                      "quantification_unit"),

    MS_RUN_FORMAT                         (MetadataElement.MS_RUN,                              "format"),
    MS_RUN_LOCATION                       (MetadataElement.MS_RUN,                              "location"),
    MS_RUN_ID_FORMAT                      (MetadataElement.MS_RUN,                              "id_format"),
    MS_RUN_FRAGMENTATION_METHOD           (MetadataElement.MS_RUN,                              "fragmentation_method"),
    MS_RUN_HASH                           (MetadataElement.MS_RUN,                              "hash"),
    MS_RUN_HASH_METHOD                    (MetadataElement.MS_RUN,                              "hash_method"),

    SAMPLE_SPECIES                        (MetadataElement.SAMPLE,                              "species"),
    SAMPLE_TISSUE                         (MetadataElement.SAMPLE,                              "tissue"),
    SAMPLE_CELL_TYPE                      (MetadataElement.SAMPLE,                              "cell_type"),
    SAMPLE_DISEASE                        (MetadataElement.SAMPLE,                              "disease"),
    SAMPLE_DESCRIPTION                    (MetadataElement.SAMPLE,                              "description"),
    SAMPLE_CUSTOM                         (MetadataElement.SAMPLE,                              "custom"),

    ASSAY_QUANTIFICATION_REAGENT          (MetadataElement.ASSAY,                               "quantification_reagent"),
    ASSAY_SAMPLE_REF                      (MetadataElement.ASSAY,                               "sample_ref"),
    ASSAY_MS_RUN_REF                      (MetadataElement.ASSAY,                               "ms_run_ref"),

    ASSAY_QUANTIFICATION_MOD_SITE         (MetadataSubElement.ASSAY_QUANTIFICATION_MOD,         "site"),
    ASSAY_QUANTIFICATION_MOD_POSITION     (MetadataSubElement.ASSAY_QUANTIFICATION_MOD,         "position"),

    STUDY_VARIABLE_ASSAY_REFS             (MetadataElement.STUDY_VARIABLE,                      "assay_refs"),
    STUDY_VARIABLE_SAMPLE_REFS            (MetadataElement.STUDY_VARIABLE,                      "sample_refs"),
    STUDY_VARIABLE_DESCRIPTION            (MetadataElement.STUDY_VARIABLE,                      "description"),

    CV_LABEL                              (MetadataElement.CV,                                  "label"),
    CV_FULL_NAME                          (MetadataElement.CV,                                  "full_name"),
    CV_VERSION                            (MetadataElement.CV,                                  "version"),
    CV_URL                                (MetadataElement.CV,                                  "url"),

    COLUNIT_PROTEIN                       (MetadataElement.COLUNIT,                             "protein"),
    COLUNIT_PEPTIDE                       (MetadataElement.COLUNIT,                             "peptide"),
    COLUNIT_PSM                           (MetadataElement.COLUNIT,                             "psm"),
    COLUNIT_SMALL_MOLECULE                (MetadataElement.COLUNIT,                             "small_molecule");

    private String name;
    private MetadataElement element;
    private MetadataSubElement subElement;

    /**
     * Define a property depend on {@link MetadataElement}
     * For example: assay[1-n]-sample_ref, assay[1-n] is {@link MetadataElement#ASSAY},
     * sample_ref is {@link MetadataProperty#ASSAY_SAMPLE_REF}
     */
    private MetadataProperty(MetadataElement element, String name) {
        this.element = element;
        this.subElement = null;
        this.name = name;
    }

    /**
     * Define a property depend on {@link MetadataSubElement}
     * For example: assay[1-n]-quantification_mod[1-n]-position, assay[1-n] is {@link MetadataElement#ASSAY},
     * quantification_mod[1-n] is {@link MetadataSubElement#ASSAY_QUANTIFICATION_MOD} and position is {@link #name}
     */
    private MetadataProperty(MetadataSubElement subElement, String name) {
        this.element = subElement.getElement();
        this.subElement = subElement;
        this.name = name;
    }

    /**
     * @return dependent {@link MetadataElement}
     */
    public MetadataElement getElement() {
        return element;
    }

    /**
     * @return dependent {@link MetadataSubElement}
     */
    public MetadataSubElement getSubElement() {
        return subElement;
    }

    /**
     * @return property name
     */
    public String getName() {
        return name;
    }

    /**
     * @return property name
     *
     * @see #getName()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Find property by {@link MetadataElement} and property name with case-insensitive. If not find, return null.
     */
    public static MetadataProperty findProperty(MetadataElement element, String propertyName) {
        if (element == null || propertyName == null) {
            return null;
        }

        MetadataProperty property;
        try {
            property = MetadataProperty.valueOf((element.getName() + "_" + propertyName).toUpperCase());
        } catch (IllegalArgumentException e) {
            property = null;
        }

        return property;
    }

    /**
     * Find property by {@link MetadataElement}_{@link MetadataSubElement} and property name with case-insensitive.
     * If not find, return null.
     */
    public static MetadataProperty findProperty(MetadataSubElement subElement, String propertyName) {
        if (subElement == null || propertyName == null) {
            return null;
        }

        MetadataProperty property;
        try {
            property = MetadataProperty.valueOf((subElement.getName() + "_" + propertyName).toUpperCase());
        } catch (IllegalArgumentException e) {
            property = null;
        }

        return property;
    }
}
