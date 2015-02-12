package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.io.FileNotFoundException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * @author qingwei
 * @since 16/09/13
 */
public class PeptideValidateTest {
    private Logger logger = LoggerFactory.getLogger(PeptideValidateTest.class);

    private MZTabErrorList errorList;
    private PEPLineParser pepParser;
    private MZTabColumnFactory pehFactory;

    public PeptideValidateTest() throws Exception {
        MTDLineParserTest test = new MTDLineParserTest();
        String fileName = "testset/mtdFile.txt";
        Metadata metadata;

        URL uri = PSMValidateTest.class.getClassLoader().getResource(fileName);
        if(uri != null) {
            metadata = test.parseMetadata(uri.getFile());
        } else {
            throw new FileNotFoundException(fileName);
        }
        errorList = new MZTabErrorList();

        PEHLineParser pehParser = new PEHLineParser(metadata);
        String headerLine = "PEH\t" +
            "sequence\t" +
            "accession\t" +
            "unique\t" +
            "database\t" +
            "database_version\t" +
            "search_engine\t" +
            "best_search_engine_score[1]\t" +
            "search_engine_score[1]_ms_run[1]\t" +
            "reliability\t" +
            "modifications\t" +
            "retention_time\t" +
            "retention_time_window\t" +
            "charge\t" +
            "mass_to_charge\t" +
            "uri\t" +
            "spectra_ref";
        pehParser.parse(1, headerLine, errorList);
        pehFactory = pehParser.getFactory();
        pepParser = new PEPLineParser(pehFactory, new PositionMapping(pehFactory, headerLine), metadata, errorList);
    }

    @After
    public void tearDown() throws Exception {
        for (MZTabError mzTabError : errorList.getErrorList()) {
            logger.debug(mzTabError.toString());
        }
        errorList.clear();
    }

    private void assertError(MZTabErrorType errorType) {
        assertTrue(errorList.getError(errorList.size() - 1).getType() == errorType);
    }

    @Test
    public void testUnique() throws Exception {
        pepParser.checkUnique(pehFactory.findColumnByHeader("unique"), "x");
        assertError(FormatErrorType.MZBoolean);
    }

    @Test
    public void testModificationPosition() throws Exception {
        // in proteins and peptides MUST be reported with the position set to 0 (N-terminal)
        // or the amino acid length +1 (C-terminal) respectively.
        String dataLine = "PEP\t" +
            "EIEIL\t" +
            "P02768\t" +
            "0\t" +
            "UniProtKB\t" +
            "2011_11\t" +
            "[MS,MS:1001207,Mascot,]|[MS,MS:1001208,Sequest,]\t" +
            "2\t" +
            "2\t" +
            "3\t" +
            "8-MOD:00397\t" +                                    // position overflow
            "10.2\t" +
            "1123.2|1145.3\t" +
            "2\t" +
            "1234.4\t" +
            "http://www.ebi.ac.uk/pride/link/to/peptide\t" +
            "ms_run[1]:index=5";
        pepParser.parse(1, dataLine, errorList);
        assertError(LogicalErrorType.ModificationPosition);
    }
}
