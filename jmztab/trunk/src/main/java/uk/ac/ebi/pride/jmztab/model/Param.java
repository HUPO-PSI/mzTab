package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;
import java.util.List;

/**
 * mzTab makes use of CV parameters. As mzTab is expected to be used in several experimental environments
 * where parameters might not yet be available for the generated scores etc. all parameters can either
 * report CV parameters or user parameters that only contain a name and a value.
 * Parameters are always reported as [CV label, accession, name, value].
 * Any field that is not available MUST be left empty.
 *
 * @see UserParam
 * @see CVParam
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

    /**
     * If there exists reserved characters in value, remove them all.
     */
    private void setValue(String value) {
        if (value == null) {
            this.value = value;
        } else {
            value = value.trim();

            // define a reserved character list.
            List<String> reserveCharList = new ArrayList<String>();

            reserveCharList.add("\"");
            reserveCharList.add("\'");
            reserveCharList.add(",");
            reserveCharList.add("\\[");
            reserveCharList.add("\\]");

            for (String c : reserveCharList) {
                value = value.replaceAll(c, "");
            }
            this.value = value;
        }
    }

    /**
     * Create a {@link CVParam} object. Notice: name item never set null!
     */
    protected Param(String cvLabel, String accession, String name, String value) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(CV_PARAM + "'s name can not set empty!");
        }

        this.cvLabel = cvLabel == null ? null : cvLabel.trim();
        this.accession = accession == null ? null : accession.trim();
        this.name = name.trim();
        setValue(value);
    }

    /**
     * Create a {@link UserParam} object. Notice: name item never set null!
     */
    protected Param(String name, String value) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(USER_PARAM + "'s name can not set empty!");
        }

        this.name = name.trim();
        setValue(value);
    }

    /**
     * @return label of parameter.
     */
    public String getCvLabel() {
        return cvLabel;
    }

    /**
     * @return accession of parameter
     */
    public String getAccession() {
        return accession;
    }

    /**
     * @return name of parameter
     */
    public String getName() {
        return name;
    }

    /**
     * @return value of parameter.
     */
    public String getValue() {
        return value;
    }

    /**
     * Judge the parameter equal with another on based on its accession number.
     */
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

    /**
     * In case, the name of the param contains commas, quotes MUST be added to avoid problems with the parsing:
     * [label, accession, "first part of the param name , second part of the name", value].
     *
     * For example: [MOD, MOD:00648, "N,O-diacetylated L-serine",]
     */
    private void printReserveString(String name, StringBuilder sb) {
        List<String> charList = new ArrayList<String>();

        charList.add("\"");
        charList.add(",");
        charList.add("[");
        charList.add("]");

        boolean containReserveChar = false;
        for (String c : charList) {
            if (name.contains(c)) {
                containReserveChar = true;
                break;
            }
        }

        if (containReserveChar) {
            sb.append("\"").append(name).append("\"");
        } else {
            sb.append(name);
        }
    }

    /**
     * In case, the name of the param contains commas, quotes MUST be added to avoid problems with the parsing:
     * [label, accession, "first part of the param name , second part of the name", value].
     *
     * For example: [MOD, MOD:00648, "N,O-diacetylated L-serine",]
     */
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

        printReserveString(name, sb);
        sb.append(", ");

        if (value != null) {
            printReserveString(value, sb);
        }

        sb.append("]");

        return sb.toString();
    }
}
