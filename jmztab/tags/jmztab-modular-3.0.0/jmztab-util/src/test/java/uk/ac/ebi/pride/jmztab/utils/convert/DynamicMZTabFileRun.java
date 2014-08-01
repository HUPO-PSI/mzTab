package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;

import java.io.File;

/**
 * @author qingwei
 * @since 02/12/13
 */
public class DynamicMZTabFileRun {

    public static void main(String[] args) throws Exception {
        MZTabFileParser parser = new MZTabFileParser(new File("testset/merge/PRIDE_Exp_Complete_Ac_1.mztab"), System.out);
        MZTabFile tabFile = parser.getMZTabFile();

        DynamicMZTabFile modifyTabFile = new DynamicMZTabFile(tabFile);
        DynamicMetadata dynamicMetadata = modifyTabFile.getDynamicMetadata();

        dynamicMetadata.modifyMsRunId(1, 9);
        System.out.println(modifyTabFile.getTabFile().getProteinColumnFactory());
    }
}
