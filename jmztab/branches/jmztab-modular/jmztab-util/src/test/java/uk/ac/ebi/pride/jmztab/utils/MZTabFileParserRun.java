package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorType;

import java.io.File;

/**
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabFileParserRun {
    public void check(File tabFile) throws Exception {
        System.out.println("checking " + tabFile.getName() + " with Error level");
        new MZTabFileParser(tabFile, System.out, MZTabErrorType.Level.Error);
        System.out.println("Finish!");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        MZTabFileParserRun run = new MZTabFileParserRun();

        File inDir = new File("temp");
        for (File tabFile : inDir.listFiles()) {
            if(tabFile.isFile() && !tabFile.isHidden())
                run.check(tabFile);
        }
    }
}
