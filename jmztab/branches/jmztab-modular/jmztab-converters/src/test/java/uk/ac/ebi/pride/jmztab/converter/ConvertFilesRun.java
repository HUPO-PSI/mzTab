package uk.ac.ebi.pride.jmztab.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.converter.utils.FileFormat;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;

import java.io.*;
import java.net.URL;

import static uk.ac.ebi.pride.jmztab.converter.utils.FileFormat.detectFileType;

/**
 * @author qingwei
 * @since 17/09/13
 */
public class ConvertFilesRun {

    private static Logger logger = LoggerFactory.getLogger(ConvertFilesRun.class);

    private void convert(File inFile, File outFile) throws Exception {
        logger.debug("Input file name is: " + inFile.getAbsoluteFile());
        FileFormat spectrumTyp = detectFileType(inFile);
        if(spectrumTyp != null){
            MZTabFileConverter converter = new MZTabFileConverter(inFile, spectrumTyp, false);
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
                logger.debug("There exists errors in converted files.");
                logger.debug(errorList.toString());
            }
            out.close();
        } else {
            logger.debug("The file types supported in current mztab converter are PRIDE XML (.xml) and MZIdentML (.mzid)");
        }

    }

    public static void main(String[] args) throws Exception {
        ConvertFilesRun run = new ConvertFilesRun();

        String dirName = "testset";

        URL input = ConvertFilesRun.class.getClassLoader().getResource(dirName);

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


}
