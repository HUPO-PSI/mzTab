package uk.ac.ebi.pride.jmztab.parser.utils;

import uk.ac.ebi.pride.jmztab.errors.MZTabErrorType;
import uk.ac.ebi.pride.jmztab.parser.MZTabFileParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @author qingwei
 * @since 21/02/13
 */
public class MZTabFileParserRun {

    public void check(File tabFile) throws Exception {
        System.out.println("checking " + tabFile.getName() + " with Error level");
        MZTabFileParser mzTabFileParser = new MZTabFileParser(tabFile, System.out, MZTabErrorType.Level.Error);
        mzTabFileParser.getMZTabFile();

        System.out.println("Finish!");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        MZTabFileParserRun run = new MZTabFileParserRun();

        String dirName = "testset/mzTabs";

        URL input = MZTabFileParserRun .class.getClassLoader().getResource(dirName);

        if(input != null) {
            File inputDir = new File(input.getFile());

            for (File tabFile : inputDir.listFiles()) {
                if(tabFile.isFile() && !tabFile.isHidden()){
                    run.check(tabFile);
                }
            }
        } else {
            throw new FileNotFoundException(dirName);
        }
    }

}
