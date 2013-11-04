package uk.ac.ebi.pride.jmztab.utils;

import java.io.File;

/**
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabFileParserRun {
    public void check(String fileName) throws Exception {
        System.out.println("check " + fileName);
        File tabFile = new File("testset/" + fileName);
        new MZTabFileParser(tabFile, System.out);
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        MZTabFileParserRun run = new MZTabFileParserRun();

//        run.check("mztab_SILAC_example.txt");
//        run.check("CPTAC_Progenesis_label_free_mzq.txt");
        run.check("mztab_itraq_example.txt");
//        run.check("mztab_lipidomics_example.txt");
//        run.check("mztab_merged_example.txt");
//        run.check("OpenMS_PQ_example.mzTab");
//        run.check("PRIDE_Exp_Complete_Ac_16649.xml-mztab.txt");
//        run.check("PXD000002_mztab.txt.gz");
    }
}
