package uk.ac.ebi.pride.jmztab.utils;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;

import java.io.IOException;
import java.util.Properties;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * This Class will load the properties used by the mzTab library
 *
 * @author qingwei
 * @since 29/01/13
 */
public class MZTabProperties {
    private static Logger logger = Logger.getLogger(MZTabProperties.class);

    private static Properties properties;
    static {
        String mzTabProperties = "conf/mztab.properties";
        String formatProperties = "conf/mztab_format_error.properties";
        String logicalProperties = "conf/mztab_logical_error.properties";
        String crosscheckProperties = "conf/mztab_crosscheck_error.properties";
        try {
            properties = new Properties();

            properties.load(MZTabProperties.class.getClassLoader().getResourceAsStream(mzTabProperties));
            properties.load(MZTabProperties.class.getClassLoader().getResourceAsStream(formatProperties));
            properties.load(MZTabProperties.class.getClassLoader().getResourceAsStream(logicalProperties));
            properties.load(MZTabProperties.class.getClassLoader().getResourceAsStream(crosscheckProperties));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public final static String MZTabExceptionMessage = "There exists errors in the metadata section or " +
            "protein/peptide/small_molecule header section! Validation will stop, and ignore data table check!" + NEW_LINE;
    public final static String MZTabErrorOverflowExceptionMessage = "System error queue overflow!" + NEW_LINE;

    public final static String ENCODE = getProperty("mztab.encode");
    public final static int MAX_ERROR_COUNT = Integer.parseInt(getProperty("mztab.max_error_count"));
    public final static LogicalErrorType.Level LEVEL = LogicalErrorType.findLevel(getProperty("mztab.level"));
    public final static boolean CVPARAM_CHECK = Boolean.parseBoolean(getProperty("mztab.cvparam_webservice"));

}
