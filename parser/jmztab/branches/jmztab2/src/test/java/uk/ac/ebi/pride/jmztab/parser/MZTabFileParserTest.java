package uk.ac.ebi.pride.jmztab.parser;

import org.junit.Test;

import java.io.File;

/**
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabFileParserTest {
    @Test
    public void testParserFile() throws Exception {
        File silacFile = new File("example/mztab_SILAC_example.txt");
        MZTabFileParser.parse(silacFile, System.out);
    }
}
