package uk.ac.ebi.pride.jmztab.checker;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.checker.section.SmallMoleculeOptColumnsIntegrityChecker;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorType;
import uk.ac.ebi.pride.jmztab.model.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: ntoro
 * Date: 28/07/2014
 * Time: 15:18
 */
public class SmallMoleculeOptColumnsIntegrityCheckerTest extends IntegrityCheckerTest {

    private Logger logger = LoggerFactory.getLogger(SmallMoleculeOptColumnsIntegrityCheckerTest.class);

    @Test
    public void testSmallMoleculeOK() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        SmallMoleculeOptColumnsIntegrityChecker smlChecker;

        factory = createValidColumnFactory();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        smlChecker = new SmallMoleculeOptColumnsIntegrityChecker(metadataSI, factory, errorList);
        smlChecker.check();
        assertTrue(errorList.isEmpty());

    }

    @Test
    public void testSmallMoleculeValidatorSearchEngineScore() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        SmallMoleculeOptColumnsIntegrityChecker smlChecker;

        factory = createInvalidColumnFactoryNoSearchEngineScore();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        smlChecker = new SmallMoleculeOptColumnsIntegrityChecker(metadataCI, factory, errorList);
        smlChecker.check();
        assertTrue(!errorList.isEmpty());
        assertEquals(1, errorList.size());
        logger.debug(errorList.toString());

    }


    @Test
    public void testSmallMoleculeValidatorNoPeptideAbundance() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        SmallMoleculeOptColumnsIntegrityChecker smlChecker;

        factory = createInvalidColumnFactoryNoSearchEngineScore();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        smlChecker = new SmallMoleculeOptColumnsIntegrityChecker(metadataInvalidCQ, factory, errorList);
        smlChecker.check();
        assertTrue(!errorList.isEmpty());
        assertEquals(6, errorList.size());
        logger.debug(errorList.toString());

    }

    private MZTabColumnFactory createValidColumnFactory(){

        MsRun msRun1 = new MsRun(1);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);
        StudyVariable studyVariable2 = new StudyVariable(2);

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Small_Molecule);

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        // add optional columns which have stable order.
        factory.addBestSearchEngineScoreOptionalColumn(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addSearchEngineScoreOptionalColumn(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, 1, msRun1);

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        factory.addAbundanceOptionalColumn(studyVariable2);

        // add user defined optional columns
        factory.addOptionalColumn(msRun1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);
        System.out.println();

        return factory;

    }

    private MZTabColumnFactory createInvalidColumnFactoryNoSearchEngineScore() {

        MsRun msRun1 = new MsRun(1);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Small_Molecule);

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        // add optional columns which have stable order.
//        factory.addBestSearchEngineScoreOptionalColumn(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addSearchEngineScoreOptionalColumn(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, 1, msRun1);

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
//        factory.addAbundanceOptionalColumn(assay2);

        // add user defined optional columns
        factory.addOptionalColumn(msRun1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);

        return factory;
    }
}
