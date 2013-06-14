package uk.ac.ebi.pride.jmztab.model;

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
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(CV_PARAM + "'s name can not set empty!");
        }

        this.cvLabel = cvLabel == null ? null : cvLabel.trim();
        this.accession = accession == null ? null : accession.trim();
        this.name = name.trim();
        this.value = value == null ? null : value.trim();
    }

    protected Param(String name, String value) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(USER_PARAM + "'s name can not set empty!");
        }

        this.name = name;
        this.value = value;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Param param = (Param) o;

        if (accession != null ? !accession.equals(param.accession) : param.accession != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return accession != null ? accession.hashCode() : 0;
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
