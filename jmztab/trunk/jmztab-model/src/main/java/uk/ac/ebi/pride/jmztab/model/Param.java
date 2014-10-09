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
 * @author qingwei
 * @author jgriss
 * @since 30/01/13
 */
public class Param {
    private final static String PARAM = "Param";

    protected String cvLabel;
    protected String accession;
    protected String name;
    protected String value;

    /**
     * Create a {@link CVParam} object. Notice: name item never set null!
     */
    protected Param(String cvLabel, String accession, String name, String value) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(PARAM + "'s name can not set empty!");
        }

        this.cvLabel = cvLabel == null ? null : cvLabel.trim();
        this.accession = accession == null ? null : accession.trim();
        this.name = MZTabUtils.removeDoubleQuotes(name);
        this.value = value == null ? null : MZTabUtils.removeDoubleQuotes(value);

    }

    /**
     * Create a {@link UserParam} object. Notice: name item never set null!
     */
    protected Param(String name, String value) {
        this(null, null, name, value);
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
     * Judge the parameter equal with another. If the name and value are not taken into account, the UserParam will be
     * always equals because the accession is always null.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Param)) return false;

        Param param = (Param) o;

        if (accession != null ? !accession.equalsIgnoreCase(param.accession) : param.accession != null) return false;
        if (cvLabel != null ? !cvLabel.equalsIgnoreCase(param.cvLabel) : param.cvLabel != null) return false;
        if (name != null ? !name.equalsIgnoreCase(param.name) : param.name != null) return false;
        if (value != null ? !value.equalsIgnoreCase(param.value) : param.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = accession != null ? accession.hashCode() : 0;
        result = 31 * result + (cvLabel != null ? cvLabel.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    /**
     * In case, the name of the param contains commas, quotes MUST be added to avoid problems with the parsing:
     * [label, accession, "first part of the param name , second part of the name", value].
     *
     * For example: [MOD, MOD:00648, "N,O-diacetylated L-serine",]
     */
    private void printReserveString(String name, StringBuilder sb) {
        List<String> charList = new ArrayList<String>();

        charList.add(",");

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
     * If there exists reserved characters in value, remove them all.
     */
    private String removeReservedChars(String value) {
        if (value != null) {
            value = value.trim();

            // define a reserved character list.
            List<String> reserveCharList = new ArrayList<String>();

            reserveCharList.add(",");

            for (String c : reserveCharList) {
                value = value.replaceAll(c, "");
            }
        }

        return value;
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
