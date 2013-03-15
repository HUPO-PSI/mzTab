package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * User: Qingwei
 * Date: 13/03/13
 */
public class ConverterPrideXMLFileRun {
    public static void main(String[] args) throws Exception {
        File inFile = new File("testset/PRIDE_Exp_Complete_Ac_16649.xml");
//        File inFile = new File("testset/PRIDE_Exp_Complete_Ac_8500.xml");

        MZTabFileConverter converter = new MZTabFileConverter(inFile, ConvertFile.Format.PRIDE);
        MZTabFile mzTabFile = converter.getMZTabFile();

        File outFile = new File("testset/PRIDE_Exp_Complete_Ac_16649.xml-mztab.txt");
        mzTabFile.printMZTab(new BufferedOutputStream(new FileOutputStream(outFile)));
    }
}
