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
    private ConvertFile convertFile;

    public MZTabFileConverter(File inFile, String format) {
        if (format == null) {
            throw new NullPointerException("Source file format is null");
        }

        if (format.equalsIgnoreCase(ConvertFile.PRIDE)) {
            convertFile = new ConvertPrideXMLFile(inFile);
        } else if (format.equalsIgnoreCase(ConvertFile.mzIdentML)) {
            convertFile = new ConvertMzIndentMLFile(inFile);
        }
    }

    public MZTabFile getMZTabFile() {
        return convertFile.getMZTabFile();
    }
}
