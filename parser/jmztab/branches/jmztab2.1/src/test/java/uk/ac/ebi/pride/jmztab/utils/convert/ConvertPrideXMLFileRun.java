package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * User: qingwei
 * Date: 17/09/13
 */
public class ConvertPrideXMLFileRun {
    public static void main(String[] args) throws Exception {
        File inFile = new File("testset/PRIDE_Exp_Complete_Ac_16649.xml");
        ConvertPrideXMLFile convert = new ConvertPrideXMLFile(inFile);

        File outFile = new File("temp/Pride_16649.mztab");
        MZTabFile tabFile = convert.getMZTabFile();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
        tabFile.printMZTab(out);
        out.close();
    }
}
