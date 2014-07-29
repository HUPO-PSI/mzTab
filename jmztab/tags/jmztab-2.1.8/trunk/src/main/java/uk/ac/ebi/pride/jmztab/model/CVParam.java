package uk.ac.ebi.pride.jmztab.model;

/**
 * The PSI-MS controlled vocabulary is intended to provide terms for annotation of mzML, mzIdentML, and mzQuantML files.
 * The CV has been generated with a collection of terms from software vendors and academic groups working in the area of
 * mass spectrometry and proteome informatics. Some terms describe attributes that must be coupled with a numerical value
 * attribute in the CvParam element (e.g. MS:1001191 "p-value") and optionally a unit for that value (e.g. MS:1001117,
 * "theoretical mass", units = "dalton"). The terms that require a value are denoted by having a "datatype" key-value
 * pair in the CV itself: MS:1001172 "mascot:expectation value" value-type:xsd:double. Terms that need to be qualified with
 * units are denoted with a "has_units" key in the CV itself (relationship: has_units: UO:0000221 ! dalton).
 *
 * User: Qingwei
 * Date: 04/02/13
 */
public class CVParam extends Param {
    /**
     * Define a PSI-MS controlled vocabulary parameter.

     * @param name SHOULD NOT be empty.
     */
    public CVParam(String cvLabel, String accession, String name, String value) {
        super(cvLabel, accession, name, value);
    }

    /**
     * @return cv label string.
     */
    public String getCvLabel() {
        return cvLabel;
    }

    /**
     * @return cv accession number.
     */
    public String getAccession() {
        return accession;
    }

    /**
     * @return cv name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return cv value.
     */
    public String getValue() {
        return value;
    }
}
