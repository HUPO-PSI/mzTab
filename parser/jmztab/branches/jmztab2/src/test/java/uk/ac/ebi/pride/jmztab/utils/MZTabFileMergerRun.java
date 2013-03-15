package uk.ac.ebi.pride.jmztab.utils;

import java.io.File;

/**
 * User: Qingwei
 * Date: 04/03/13
 */
public class MZTabFileMergerRun {
    public static void main(String[] args) throws Exception {
        File file1 = new File("example/mztab_itraq_example.txt");
        File file2 = new File("example/CPTAC_Progenesis_label_free_mzq.txt");

        MZTabFileMerger merger = new MZTabFileMerger(file1, file2);
        merger.setCombine(true);

        merger.printMZTab(System.out);
    }
}
