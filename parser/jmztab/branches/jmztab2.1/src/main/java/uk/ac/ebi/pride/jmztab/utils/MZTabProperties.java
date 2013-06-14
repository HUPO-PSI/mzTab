package uk.ac.ebi.pride.jmztab.utils;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * User: Qingwei
 * Date: 29/01/13
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
            FileReader reader = new FileReader(mzTabProperties);
            properties.load(reader);
            reader.close();

            reader = new FileReader(formatProperties);
            properties.load(reader);
            reader.close();

            reader = new FileReader(logicalProperties);
            properties.load(reader);
            reader.close();

            reader = new FileReader(crosscheckProperties);
            properties.load(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
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

//    public final static String Version = getProperty("mztab.version");
    public final static String ENCODE = getProperty("mztab.encode");
    public final static int MAX_ERROR_COUNT = Integer.parseInt(getProperty("mztab.max_error_count"));
    public final static LogicalErrorType.Level LEVEL = LogicalErrorType.findLevel(getProperty("mztab.level"));
    public final static boolean CVPARAM_CHECK = Boolean.parseBoolean(getProperty("mztab.cvparam_webservice"));

//    public final static boolean BUFFERED = Boolean.parseBoolean(getProperty("mztab.buffered"));

}
