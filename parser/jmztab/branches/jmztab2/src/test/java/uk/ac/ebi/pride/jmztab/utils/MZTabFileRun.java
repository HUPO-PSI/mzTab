package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.utils.MZTabFile;

import java.io.File;

/**
 * User: qingwei
 * Date: 01/03/13
 */
public class MZTabFileRun {
    public static void main(String[] args) throws Exception {
        MZTabFile file = new MZTabFile(new File("example/mztab_itraq_example.txt"), true);

        file.print(System.out);
    }
}
