package uk.ac.ebi.pride.jmztab.parser;

import org.junit.Test;

import java.io.File;

/**
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabFileParserTest {
    private String fileName;

    private void parse(String fileName) throws Exception {
        System.out.println("check " + fileName);
        File tabFile = new File("example/" + fileName);
        MZTabFileParser.parse(tabFile, System.out);
        System.out.println();
        System.out.println();
    }

    @Test
    public void testParserFile1() throws Exception {
        fileName = "mztab_SILAC_example.txt";
        parse(fileName);
    }

    @Test
    public void testParserFile2() throws Exception {
        fileName = "CPTAC_Progenesis_label_free_mzq.txt";
        parse(fileName);
    }

    @Test
    public void testParserFile3() throws Exception {
        fileName = "mztab_itraq_example.txt";
        parse(fileName);
    }

    @Test
    public void testParserFile4() throws Exception {
        fileName = "mztab_lipidomics_example.txt";
        parse(fileName);
    }

    @Test
    public void testParserFile5() throws Exception {
        fileName = "mztab_merged_example.txt";
        parse(fileName);
    }

    @Test
    public void testParserFile6() throws Exception {
        fileName = "OpenMS_PQ_example.mzTab";
        parse(fileName);
    }

    @Test
    public void testParserFile7() throws Exception {
        fileName = "PRIDE_Exp_Complete_Ac_16649.xml-mztab.txt";
        parse(fileName);
    }

    @Test
    public void testParserFile8() throws Exception {
        fileName = "PXD000002_mztab.txt.gz";
        parse(fileName);
    }
}
