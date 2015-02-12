package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorType;

import java.io.File;
import java.net.URL;

/**
 * @author qingwei
 * @since 02/12/13
 */
public class DynamicMZTabFileRun {

    //TODO: Fix it. The header doesn't change, because the name is stored when the column is parsed

    public static void main(String[] args) throws Exception {
        String name = "testset/PRIDE_Example.mztab";

        URL input = DynamicMZTabFileRun.class.getClassLoader().getResource(name);
        MZTabFileParser parser = new MZTabFileParser(new File(input.getFile()), System.out, MZTabErrorType.Level.Error);
        MZTabFile tabFile = parser.getMZTabFile();

        DynamicMZTabFile modifyTabFile = new DynamicMZTabFile(tabFile);
        DynamicMetadata dynamicMetadata = modifyTabFile.getDynamicMetadata();

        dynamicMetadata.modifyMsRunId(1, 9);
        System.out.println(modifyTabFile.getTabFile().getProteinColumnFactory());
    }
}
