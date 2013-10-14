package uk.ac.ebi.pride.jmztab.model;

/**
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

    private MetadataProperty(MetadataElement element, String name) {
        this.element = element;
        this.subElement = null;
        this.name = name;
    }

    private MetadataProperty(MetadataSubElement subElement, String name) {
        this.element = subElement.getElement();
        this.subElement = subElement;
        this.name = name;
    }

    public MetadataElement getElement() {
        return element;
    }

    public MetadataSubElement getSubElement() {
        return subElement;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * element.getName_propertyName.
     * @see uk.ac.ebi.pride.jmztab.model.MetadataElement#getName()
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
     * subElement.getName_propertyName
     * @see uk.ac.ebi.pride.jmztab.model.MetadataSubElement#getName()
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
