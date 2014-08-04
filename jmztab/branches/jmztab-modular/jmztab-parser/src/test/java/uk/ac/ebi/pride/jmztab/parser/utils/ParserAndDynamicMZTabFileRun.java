package uk.ac.ebi.pride.jmztab.parser.utils;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.parser.MZTabFileParser;
import uk.ac.ebi.pride.jmztab.utils.DynamicMZTabFile;
import uk.ac.ebi.pride.jmztab.utils.DynamicMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * User: qingwei
 * Date: 02/12/13
 */
public class ParserAndDynamicMZTabFileRun {

    public static void main(String[] args) throws Exception {

        String fileName = "testset/mzTabs/PRIDE_Example.mztab";

        URL input = ParserAndDynamicMZTabFileRun.class.getClassLoader().getResource(fileName);

        if(input != null) {
            MZTabFileParser parser = new MZTabFileParser(new File(input.getFile()), System.out);
            MZTabFile tabFile = parser.getMZTabFile();

            DynamicMZTabFile modifyTabFile = new DynamicMZTabFile(tabFile);
            DynamicMetadata dynamicMetadata = modifyTabFile.getDynamicMetadata();

            dynamicMetadata.modifyMsRunId(1, 9);
            System.out.println(modifyTabFile.getTabFile().getProteinColumnFactory());
        } else {
            throw new FileNotFoundException(fileName);
        }

    }
}
