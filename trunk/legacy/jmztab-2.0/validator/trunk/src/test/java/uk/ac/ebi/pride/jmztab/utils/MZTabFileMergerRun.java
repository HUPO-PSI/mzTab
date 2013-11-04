package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;

import java.io.File;

/**
 * User: Qingwei
 * Date: 04/03/13
 */
public class MZTabFileMergerRun {
    public static void main(String[] args) throws Exception {
        File file1 = new File("testset/mztab_itraq_example.txt");
        File file2 = new File("testset/mztab_lipidomics_example.txt");
        File file3 = new File("testset/mztab_merged_example.txt");

        MZTabFileMerger merger = new MZTabFileMerger();
        MZTabFileParser parser1 = new MZTabFileParser(file1, System.out);
        MZTabFileParser parser2 = new MZTabFileParser(file2, System.out);
        MZTabFileParser parser3 = new MZTabFileParser(file3, System.out);
        merger.addTabFile(parser1.getMZTabFile());
        merger.addTabFile(parser2.getMZTabFile());
        merger.addTabFile(parser3.getMZTabFile());
        merger.setCombine(false);
        MZTabFile tabFile = merger.merge();
        tabFile.printMZTab(System.out);
    }
}
