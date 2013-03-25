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

    private MZTabFile mzTabFile;

    public MZTabFileConverter(File inFile, ConvertFile.Format format) {
        if (format == null) {
            throw new NullPointerException("Source file format is null");
        }

        ConvertFile convertFile;
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

        mzTabFile = convertFile.getMZTabFile();
    }

    public MZTabFile getMZTabFile() {
        return mzTabFile;
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
