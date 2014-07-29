package uk.ac.ebi.pride.jmztab.utils.convert;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileConverter;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * User: qingwei
 * Date: 17/09/13
 */
public class ConvertPrideXMLFileRun {
    private static Logger logger = Logger.getLogger(ConvertPrideXMLFile.class);

    private void convert(File inFile, File outFile) throws Exception {
        logger.debug("Input file name is: " + inFile.getAbsoluteFile());
        MZTabFileConverter converter = new MZTabFileConverter(inFile, MassSpecFileFormat.PRIDE);
        MZTabErrorList errorList = converter.getErrorList();

        OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
        if (errorList.isEmpty()) {
            MZTabFile tabFile = converter.getMZTabFile();
            if (tabFile.isEmpty()) {
                logger.debug("Not data included in mztab file.");
            } else {
                logger.debug("Finish convert, no error in it. Output file name is " + outFile.getAbsoluteFile());
                tabFile.printMZTab(out);
            }
        } else {
            logger.debug("There exists errors in convert files.");
            logger.debug(errorList);
        }
        out.close();
    }

    public static void main(String[] args) throws Exception {
        ConvertPrideXMLFileRun run = new ConvertPrideXMLFileRun();
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_16649.xml"), new File("temp/PRIDE_Exp_Complete_Ac_16649.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_17910.xml"), new File("temp/PRIDE_Exp_Complete_Ac_17910.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_1643.xml"), new File("temp/PRIDE_Exp_Complete_Ac_1643.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_2030.xml"), new File("temp/PRIDE_Exp_Complete_Ac_2030.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_31257.xml"), new File("temp/PRIDE_Exp_Complete_Ac_31257.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_108.xml"), new File("temp/PRIDE_Exp_Complete_Ac_108.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_1650.xml"), new File("temp/PRIDE_Exp_Complete_Ac_1650.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_1889.xml"), new File("temp/PRIDE_Exp_Complete_Ac_1889.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_2375.xml"), new File("temp/PRIDE_Exp_Complete_Ac_2375.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_3217.xml"), new File("temp/PRIDE_Exp_Complete_Ac_3217.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_3483.xml"), new File("temp/PRIDE_Exp_Complete_Ac_3483.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_11797.xml"), new File("temp/PRIDE_Exp_Complete_Ac_11797.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_8696.xml"), new File("temp/PRIDE_Exp_Complete_Ac_8696.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_20601.xml"), new File("temp/PRIDE_Exp_Complete_Ac_20601.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_16912.xml"), new File("temp/PRIDE_Exp_Complete_Ac_16912.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_27061.xml"), new File("temp/PRIDE_Exp_Complete_Ac_27061.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_27555.xml"), new File("temp/PRIDE_Exp_Complete_Ac_27555.mztab"));
//        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_30351.xml"), new File("temp/PRIDE_Exp_Complete_Ac_30351.mztab"));
        run.convert(new File("testset/PRIDE_Exp_Complete_Ac_28622.xml"), new File("temp/PRIDE_Exp_Complete_Ac_28622.mztab"));
    }
}
