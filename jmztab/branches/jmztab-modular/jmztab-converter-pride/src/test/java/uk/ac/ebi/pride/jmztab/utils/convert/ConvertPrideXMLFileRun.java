package uk.ac.ebi.pride.jmztab.utils.convert;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileConverter;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.io.*;
import java.net.URL;

/**
 * User: qingwei
 * Date: 17/09/13
 */
public class ConvertPrideXMLFileRun {
    private static Logger logger = Logger.getLogger(ConvertPrideXMLFile.class);

    private void convert(File inFile, File outFile) throws Exception {
        logger.debug("Input file name is: " + inFile.getAbsoluteFile());
        MassSpecFileFormat spectrumTyp = detectFileType(inFile);
        if(spectrumTyp != null){
            MZTabFileConverter converter = new MZTabFileConverter(inFile, spectrumTyp);
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
        }else{
            logger.debug("The file types supported in current mztab converter are PRIDE XML (.xml) and MZIdentML (.mzid)");
        }

    }

    public static void main(String[] args) throws Exception {
        ConvertPrideXMLFileRun run = new ConvertPrideXMLFileRun();

        String dirName = "testset";

        URL input = ConvertPrideXMLFileRun.class.getClassLoader().getResource(dirName);

        if(input != null) {
            File inputDir = new File(input.getFile());

            for (File tabFile : inputDir.listFiles()) {
                if(tabFile.isFile() && !tabFile.isHidden()){
                    String tabFileName = (tabFile.getName().contains(".xml"))?tabFile.getName().replace(".xml", ".mztab"):tabFile.getName();
                    tabFileName        = (tabFileName.contains(".mzid"))?tabFile.getName().replace(".mzid", ".mztab"):tabFileName;
                    run.convert(tabFile, new File("temp", tabFileName));
                }
            }
        } else {
            throw new FileNotFoundException(dirName);
        }
    }

    private static MassSpecFileFormat detectFileType(File inFile){
        if(inFile.getAbsolutePath().contains(".xml"))
            return MassSpecFileFormat.PRIDE;
        else if (inFile.getAbsolutePath().contains(".mzid"))
            return MassSpecFileFormat.MZIDENTML;

        return null;
    }
}
