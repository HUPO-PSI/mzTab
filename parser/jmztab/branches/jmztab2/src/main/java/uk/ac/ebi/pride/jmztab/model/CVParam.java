package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 04/02/13
 */
public class CVParam extends Param {
    public CVParam(String cvLabel, String accession, String name, String value) {
        super(cvLabel, accession, name, value);
    }

    public String getCvLabel() {
        return cvLabel;
    }

    public String getAccession() {
        return accession;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
