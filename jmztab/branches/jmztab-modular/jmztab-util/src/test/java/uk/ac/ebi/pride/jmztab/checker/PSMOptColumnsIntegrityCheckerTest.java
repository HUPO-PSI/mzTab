package uk.ac.ebi.pride.jmztab.checker;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.checker.section.PsmOptColumnsIntegrityChecker;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorType;
import uk.ac.ebi.pride.jmztab.model.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: ntoro
 * Date: 28/07/2014
 * Time: 13:18
 */
public class PSMOptColumnsIntegrityCheckerTest extends IntegrityCheckerTest {

    private Logger logger = LoggerFactory.getLogger(PSMOptColumnsIntegrityCheckerTest.class);

    @Test
    public void testPSMOK() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        PsmOptColumnsIntegrityChecker psmChecker;

        factory = createValidColumnFactory();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        psmChecker = new PsmOptColumnsIntegrityChecker(metadataSI, factory, errorList);
        psmChecker.check();
        assertTrue(errorList.isEmpty());

    }

    @Test
    public void testPSMValidatorSearchEngineScore() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        PsmOptColumnsIntegrityChecker psmChecker;

        factory = createInvalidColumnFactoryNoSearchEngineScore();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        psmChecker = new PsmOptColumnsIntegrityChecker(metadataCI, factory, errorList);
        psmChecker.check();
        assertTrue(!errorList.isEmpty());
        assertEquals(2, errorList.size());
        logger.debug(errorList.toString());

    }

    private MZTabColumnFactory createValidColumnFactory() {

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.PSM_Header);

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        factory.addSearchEngineScoreOptionalColumn(PSMColumn.SEARCH_ENGINE_SCORE, 1, null);
        factory.addSearchEngineScoreOptionalColumn(PSMColumn.SEARCH_ENGINE_SCORE, 2, null);

        // add user defined optional columns
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);

        return factory;
    }

    private MZTabColumnFactory createInvalidColumnFactoryNoSearchEngineScore() {

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.PSM_Header);

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        // add user defined optional columns
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);

        return factory;
    }
}
