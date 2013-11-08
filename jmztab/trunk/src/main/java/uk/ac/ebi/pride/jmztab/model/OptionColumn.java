package uk.ac.ebi.pride.jmztab.model;

/**
 * Additional columns can be added to the end of the protein table.
 * These column headers MUST start with the prefix "opt_".
 * Column names MUST only contain the following characters:
 * 'A’-'Z’, 'a’-'z’, '0’-'9’, '_’, '-’, '[’, ']’, and ':’.
 *
 * User: Qingwei
 * Date: 28/05/13
 */
public class OptionColumn extends MZTabColumn {
    public static final String OPT = "opt";
    public static final String GLOBAL = "global";

    public static String getHeader(IndexedElement element, String value) {
        StringBuilder sb = new StringBuilder();

        sb.append(OPT).append("_").append(element == null ? GLOBAL : element.getReference());
        sb.append("_").append(value.replaceAll(" ", "_"));

        return sb.toString();
    }

    public OptionColumn(IndexedElement element, String value, Class columnType, int offset) {
        super(getHeader(element, value), columnType, true, offset + 1 + "");
    }
}
