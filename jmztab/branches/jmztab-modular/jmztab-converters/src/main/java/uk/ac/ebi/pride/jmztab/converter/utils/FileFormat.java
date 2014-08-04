package uk.ac.ebi.pride.jmztab.converter.utils;

import uk.ac.ebi.pride.jmztab.model.MZTabUtils;

import java.io.File;

/**
 * @author ntoro
 * @since 24/07/2014 13:59
 */
public enum FileFormat {
    MZIDENTML,
    PRIDE,
    UNKNOWN;

    public static FileFormat detectFileType(File inFile) {
        if (inFile.getAbsolutePath().contains(".xml"))
            return FileFormat.PRIDE;
        else if (inFile.getAbsolutePath().contains(".mzid"))
            return FileFormat.MZIDENTML;

        return null;
    }

    public static FileFormat getFormat(String format) {
        if (MZTabUtils.isEmpty(format)) {
            return null;
        }

        if (format.equalsIgnoreCase(FileFormat.PRIDE.name())) {
            return FileFormat.PRIDE;
        } else if (format.equalsIgnoreCase(FileFormat.MZIDENTML.name())) {
            return FileFormat.MZIDENTML;
        } else {
            return FileFormat.UNKNOWN;
        }
    }

}
