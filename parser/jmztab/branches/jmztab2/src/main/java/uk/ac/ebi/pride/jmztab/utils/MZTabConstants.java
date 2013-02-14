package uk.ac.ebi.pride.jmztab.utils;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class MZTabConstants {
    public final static String NEW_LINE = System.getProperty("line.separator");
    public final static String NULL = "null";
    public final static char TAB = '\u0009';
    public final static char MINUS = '-';
    public final static char BAR = '\u007c';
    public final static char COLON = ':';
    public final static char COMMA = ',';

    public final static int MAX_ERROR_COUNT = Integer.parseInt(MZTabProperties.getProperty("mztab.max_error_count"));
    public final static int BUFFERED_LINE_COUNT = Integer.parseInt(MZTabProperties.getProperty("mztab.buffered_line_count"));
    public final static String FIELD_ENCODING = MZTabProperties.getProperty("mztab.default_encode");

}
