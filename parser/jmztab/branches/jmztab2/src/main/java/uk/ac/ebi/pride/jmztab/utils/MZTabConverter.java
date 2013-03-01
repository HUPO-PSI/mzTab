package uk.ac.ebi.pride.jmztab.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: qingwei
 * Date: 27/02/13
 */
public class MZTabConverter {
    public enum Format {
        PRIDE("PRIDE", "2.1"),            // Pride XML
        mzIdentML("mzIdentML", "1.1.0"),
        mzQuantML("mzQuantML", "1.0.0");

        private String type;
        private String verstion;

        private Format(String type, String verstion) {
            this.type = type;
            this.verstion = verstion;
        }


    }

    public static void convert(File inFile, Format format, OutputStream out) throws IOException {
        if (format == null) {
            format = Format.PRIDE;
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
