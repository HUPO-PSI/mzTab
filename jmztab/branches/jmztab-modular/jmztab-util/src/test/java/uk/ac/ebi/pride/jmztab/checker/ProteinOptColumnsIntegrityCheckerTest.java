package uk.ac.ebi.pride.jmztab.checker;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.checker.section.ProteinOptColumnsIntegrityChecker;
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
public class ProteinOptColumnsIntegrityCheckerTest extends IntegrityCheckerTest  {

    private Logger logger = LoggerFactory.getLogger(ProteinOptColumnsIntegrityCheckerTest.class);

    @Test
    public void testProteinOK() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        ProteinOptColumnsIntegrityChecker prtChecker;

        factory = createValidColumnFactory();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        prtChecker = new ProteinOptColumnsIntegrityChecker(metadataSI, factory, errorList);

        errorList.clear();
        prtChecker.check();
        assertTrue(errorList.isEmpty());
    }



    @Test
    public void testProteinValidatorNoProteinAbundance() throws Exception {

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        ProteinOptColumnsIntegrityChecker prtChecker;

        factory = createInvalidColumnFactory();

        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        prtChecker = new ProteinOptColumnsIntegrityChecker(metadataInvalidCQ, factory, errorList);
        prtChecker.check();
        assertTrue(!errorList.isEmpty());
        assertEquals(4, errorList.size());
        logger.debug(errorList.toString());
    }

    @Test
    public void testProteinCheckSearchEngineScore() throws Exception{

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        ProteinOptColumnsIntegrityChecker prtChecker;

        factory = createInvalidColumnFactoryWithoutSearchEngineScore();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        prtChecker = new ProteinOptColumnsIntegrityChecker(metadataSI, factory, errorList);
        prtChecker.check();
        assertTrue(!errorList.isEmpty());
        assertEquals(1, errorList.size());
        logger.debug(errorList.toString());

    }

    @Test
    public void testAllProteinColumnForCompleteAndIdentification() throws Exception {

        // In Complete and Identification document, user should provide:
        // search_engine_score_ms_run[1-n], num_psms_ms_run[1-n], num_peptides_distinct_ms_run[1-n]
        // num_peptide_unique_ms_run[1-n]

        MZTabErrorList errorList;
        MZTabColumnFactory factory;
        ProteinOptColumnsIntegrityChecker prtChecker;

        factory = createInvalidColumnFactoryWithoutSearchEngineScore();
        errorList = new MZTabErrorList(MZTabErrorType.Level.Warn);
        prtChecker = new ProteinOptColumnsIntegrityChecker(metadataCI, factory, errorList);
        prtChecker.check();
        assertTrue(!errorList.isEmpty());
        assertEquals(9, errorList.size());
        logger.debug(errorList.toString());


    }

    private MZTabColumnFactory createValidColumnFactory() {

        MsRun msRun1 = new MsRun(1);
        MsRun msRun2 = new MsRun(2);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);
        StudyVariable studyVariable2 = new StudyVariable(2);


        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein_Header);

        // add optional columns which have stable order.
        factory.addGoTermsOptionalColumn();
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun2);

        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 2);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun1);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 2, msRun1);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun2);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 2, msRun2);

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        factory.addAbundanceOptionalColumn(studyVariable2);


        // add user defined optional columns
        factory.addOptionalColumn(assay1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);

        return factory;
    }

    private MZTabColumnFactory createInvalidColumnFactory() {

        MsRun msRun1 = new MsRun(1);
        MsRun msRun2 = new MsRun(2);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);
        StudyVariable studyVariable2 = new StudyVariable(2);


        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein_Header);

        // add optional columns which have stable order.
        factory.addGoTermsOptionalColumn();
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun2);

        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 2);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun1);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 2, msRun1);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun2);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 2, msRun2);

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
//        factory.addAbundanceOptionalColumn(studyVariable2);


        // add user defined optional columns
        factory.addOptionalColumn(assay1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);

        System.out.println(factory);

        return factory;
    }

    private MZTabColumnFactory createInvalidColumnFactoryWithoutSearchEngineScore() {

        MsRun msRun1 = new MsRun(1);
        MsRun msRun2 = new MsRun(2);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);
        StudyVariable studyVariable2 = new StudyVariable(2);


        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein_Header);

        // add optional columns which have stable order.
        factory.addGoTermsOptionalColumn();
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

//        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun1);
//        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun1);
//        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);
//        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun2);
//        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun2);
//        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun2);

//        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 1);
//        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 2);
//        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun1);
//        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 2, msRun1);
//        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun2);
//        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 2, msRun2);

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        factory.addAbundanceOptionalColumn(studyVariable2);


        // add user defined optional columns
        factory.addOptionalColumn(assay1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);

        System.out.println(factory);

        return factory;
    }
}
