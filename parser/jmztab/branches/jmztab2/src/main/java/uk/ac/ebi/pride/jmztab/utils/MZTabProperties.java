package uk.ac.ebi.pride.jmztab.utils;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

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

}
