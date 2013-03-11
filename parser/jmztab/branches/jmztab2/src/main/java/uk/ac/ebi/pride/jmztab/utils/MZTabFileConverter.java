package uk.ac.ebi.pride.jmztab.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: qingwei
 * Date: 27/02/13
 */
public class MZTabFileConverter {
    public enum Format {
        PRIDE_210("PRIDE-XML", "2.1"),            // Pride XML
        mzIdentML_110("mzIdentML", "1.1.0"),
        mzTab_100("mzTab", "1.0");

        private String type;
        private String verstion;

        private Format(String type, String verstion) {
            this.type = type;
            this.verstion = verstion;
        }
    }

    public static void convert(File inFile, Format format, OutputStream out) throws IOException {
        if (format == null) {
            format = Format.PRIDE_210;
        }
    }

    public static Format findFormat(String type) {
        if (type == null) {
            return null;
        }

        for (Format format : Format.values()) {
            if (format.type.equals(type)) {
                return format;
            }
        }

        return null;
    }
}
