package uk.ac.ebi.pride.jmztab.checker;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.checker.section.PeptideOptColumnsIntegrityChecker;
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
public class PeptideOptColumnsIntegrityCheckerTest extends IntegrityCheckerTest  {

    private Logger logger = LoggerFactory.getLogger(PeptideOptColumnsIntegrityCheckerTest.class);

    @Test
    public void testPeptideOK() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        PeptideOptColumnsIntegrityChecker pepChecker;

        factory = createValidColumnFactory();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        pepChecker = new PeptideOptColumnsIntegrityChecker(metadataSI, factory, errorList);
        pepChecker.check();
        assertTrue(errorList.isEmpty());

    }


    @Test
    public void testPeptideNoPeptideAbundance() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        PeptideOptColumnsIntegrityChecker pepChecker;

        factory = createValidColumnFactory();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        pepChecker = new PeptideOptColumnsIntegrityChecker(metadataInvalidCQ, factory, errorList);
        pepChecker.check();
        assertTrue(!errorList.isEmpty());
        assertEquals(3, errorList.size());
        logger.debug(errorList.toString());

    }

    private MZTabColumnFactory createValidColumnFactory() {

        MsRun msRun1 = new MsRun(1);
        MsRun msRun2 = new MsRun(2);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Peptide_Header);

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        factory.addBestSearchEngineScoreOptionalColumn(PeptideColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addSearchEngineScoreOptionalColumn(PeptideColumn.SEARCH_ENGINE_SCORE, 1, msRun1);
        factory.addSearchEngineScoreOptionalColumn(PeptideColumn.SEARCH_ENGINE_SCORE, 1, msRun2);

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        factory.addAbundanceOptionalColumn(studyVariable1);


        // add user defined optional columns
        factory.addOptionalColumn(msRun1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);

        return factory;
    }

}
