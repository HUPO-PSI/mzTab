package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.StringUtils;

/**
 * mzTab makes use of CV parameters. As mzTab is expected to be used in several experimental environments
 * where parameters might not yet be available for the generated scores etc. all parameters can either
 * report CV parameters or user parameters that only contain a name and a value.
 * Parameters are always reported as [CV label, accession, name, value].
 * Any field that is not available MUST be left empty.
 *
 * User: Qingwei, Johannes Griss
 * Date: 30/01/13
 */
public class Param {
    private final static String CV_PARAM = "CV Param";
    private final static String USER_PARAM = "User Param";

    protected String cvLabel;
    protected String accession;
    protected String name;
    protected String value;

    protected Param(String cvLabel, String accession, String name, String value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(CV_PARAM + "'s name can not set empty!");
        }

        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.value = value;
    }

    protected Param(String name, String value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(USER_PARAM + "'s name can not set empty!");
        }

        this.name = name;
        this.value = value;
    }

    protected String getCvLabel() {
        return cvLabel;
    }

    protected String getAccession() {
        return accession;
    }

    protected String getName() {
        return name;
    }

    protected String getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        if (cvLabel != null) {
            sb.append(cvLabel);
        }
        sb.append(", ");

        if (accession != null) {
            sb.append(accession);
        }
        sb.append(", ");

        sb.append(name);
        sb.append(", ");

        if (value != null) {
            sb.append(value);
        }

        sb.append("]");

        return sb.toString();
    }
}
