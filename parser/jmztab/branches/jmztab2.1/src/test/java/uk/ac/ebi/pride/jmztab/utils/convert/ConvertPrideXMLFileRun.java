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
    private void convert(File inFile, File outFile) throws Exception {
        ConvertPrideXMLFile convert = new ConvertPrideXMLFile(inFile);
        MZTabFile tabFile = convert.getMZTabFile();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
        tabFile.printMZTab(out);
        out.close();
    }

    public static void main(String[] args) throws Exception {
        ConvertPrideXMLFileRun run = new ConvertPrideXMLFileRun();
        run.convert(new File("temp/PRIDE_Exp_Complete_Ac_16649.xml"), new File("temp/Pride_16649.mztab"));
//        run.convert(new File("temp/PRIDE_Exp_Complete_Ac_17910.xml"), new File("temp/Pride_17910.mztab"));
//        run.convert(new File("temp/PRIDE_Exp_Complete_Ac_1643.xml"), new File("temp/Pride_1643.mztab"));
    }
}
