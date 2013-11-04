package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertFile;

import java.io.File;

/**
 * User: qingwei
 * Date: 01/05/13
 */
public class ConvertMzIdentMLFileRun {
    public static void main(String[] args) throws Exception {
        File inFile = new File("testset/Mascot_MSMS_example.mzid");
        MZTabFileConverter converter = new MZTabFileConverter(inFile, ConvertFile.mzIdentML);

        MZTabFile tabFile = converter.getMZTabFile();
        tabFile.printMZTab(System.out);
    }
}
