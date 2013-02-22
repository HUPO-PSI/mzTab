package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class MZTabConstants {
    public final static String NEW_LINE = System.getProperty("line.separator");
    public final static String NULL = "null";
    public final static String INFINITY = "INF";
    public final static String CALCULATE_ERROR = "NaN";

    public final static char TAB = '\u0009';
    public final static char MINUS = '-';
    public final static char BAR = '\u007c';
    public final static char COLON = ':';
    public final static char COMMA = ',';

    public final static int MAX_ERROR_COUNT = Integer.parseInt(MZTabProperties.getProperty("mztab.max_error_count"));
    public final static String ENCODE = MZTabProperties.getProperty("encode");
    public final static LogicalErrorType.Level LEVEL = LogicalErrorType.findLevel(MZTabProperties.getProperty("mztab.level"));
    public final static boolean CVPARAM_CHECK = new Boolean(MZTabProperties.getProperty("mztab.cvparam_webservice"));

}
