package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertFile;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertMzIndentMLFile;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertPrideXMLFile;

import java.io.File;

/**
 * User: qingwei
 * Date: 27/02/13
 */
public class MZTabFileConverter {
    public final static String PRIDE = "PRIDE";
    public final static String mzIdentML = "mzIndenML";

    private ConvertFile convertFile;

    public MZTabFileConverter(File inFile, ConvertFile.Format format) {
        if (format == null) {
            throw new NullPointerException("Source file format is null");
        }

        switch (format) {
            case PRIDE:
                convertFile = new ConvertPrideXMLFile(inFile);
                break;
            case mzIdentML:
                convertFile = new ConvertMzIndentMLFile(inFile);
                break;
            default:
                convertFile = null;
        }
    }

    public MZTabFile getMZTabFile() {
        return convertFile.getMZTabFile();
    }

    public static ConvertFile.Format findFormat(String formatLabel) {
        if (formatLabel == null) {
            return null;
        }

        if (formatLabel.equals(PRIDE)) {
            return ConvertFile.Format.PRIDE;
        } else if (formatLabel.equals(mzIdentML)) {
            return ConvertFile.Format.mzIdentML;
        } else {
            return null;
        }
    }
}
