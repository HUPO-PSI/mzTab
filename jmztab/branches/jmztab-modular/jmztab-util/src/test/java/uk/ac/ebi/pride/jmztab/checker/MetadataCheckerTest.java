package uk.ac.ebi.pride.jmztab.checker;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.checker.section.MetadataIntegrityChecker;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: ntoro
 * Date: 28/07/2014
 * Time: 13:24
 */
public class MetadataCheckerTest extends IntegrityCheckerTest {

    private Logger logger = LoggerFactory.getLogger(MetadataCheckerTest.class);


    @Test
    public void testMetadataOK() throws Exception {

        MZTabErrorList errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        MetadataIntegrityChecker mtdChecker = new MetadataIntegrityChecker(metadataSI, errorList);

        errorList.clear();
        mtdChecker.check();
        assertTrue(errorList.isEmpty());
    }

    @Test
    public void testMetadataInvalidSoftwareAndQuantificationMethod() throws Exception {

        MZTabErrorList errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        MetadataIntegrityChecker mtdChecker = new MetadataIntegrityChecker(metadataInvalidCQ, errorList);
        mtdChecker.check();

        assertTrue(!errorList.isEmpty());
        assertEquals(5, errorList.size());
        logger.debug(errorList.toString());
    }

    @Test
    public void testInvalidFixedAndVariableMod() throws Exception {

        MZTabErrorList errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        MetadataIntegrityChecker mtdChecker = new MetadataIntegrityChecker(metadataCI, errorList);

        metadataCI.getFixedModMap().clear();
        metadataCI.getVariableModMap().clear();

        mtdChecker.check();
        assertTrue(!errorList.isEmpty());
        assertEquals(2, errorList.size());
        logger.debug(errorList.toString());

    }
}
