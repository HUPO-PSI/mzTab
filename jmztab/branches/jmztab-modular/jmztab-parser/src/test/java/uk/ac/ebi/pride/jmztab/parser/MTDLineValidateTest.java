package uk.ac.ebi.pride.jmztab.parser;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.Assay;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.Sample;

import static junit.framework.Assert.assertTrue;

/**
 * Test all parser error.
 *
 * User: qingwei
 * Date: 09/09/13
 */
public class MTDLineValidateTest {
    private static Logger logger = LoggerFactory.getLogger(MTDLineValidateTest.class);

    private Metadata metadata;
    private MTDLineParser parser;
    private MZTabErrorList errorList;

    @Before
    public void setUp() throws Exception {
        parser = new MTDLineParser();
        metadata = parser.getMetadata();
        metadata.setDescription("test...");
        errorList = new MZTabErrorList();
    }

    @Test
    public void testMZTabDescription() throws Exception {
        try {
            parser.parse(1, "MTD\tmzTab-ver\t1.0 rc5", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MTDDefineLabel);
            logger.debug(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tmzTab-mode\tUnknow", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MZTabMode);
            logger.debug(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tmzTab-type\tUnknow", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MZTabType);
            logger.debug(e.getMessage());
        }
    }

    @Test
    public void testIndexElement() throws Exception {
        // param name can not set empty.
        parser.parse(1, "MTD\tsample_processing[1]\t[, SEP:00173, ,]", errorList);
        assertTrue(errorList.size() == 1);
        assertTrue(errorList.getError(0).getType() ==  FormatErrorType.ParamList);

        // second param name can not set empty.
        parser.parse(1, "MTD\tsample_processing[1]\t[SEP, SEP:00142, enzyme digestion, ]|[MS, MS:1001251, , ]", errorList);
        assertTrue(errorList.size() == 2);
        assertTrue(errorList.getError(1).getType() ==  FormatErrorType.ParamList);

        // split char error.
        parser.parse(1, "MTD\tsample_processing[1]\t[SEP, SEP:00142, enzyme digestion, ]/[MS, MS:1001251, Trypsin, ]", errorList);
        assertTrue(errorList.size() == 3);
        assertTrue(errorList.getError(2).getType() ==  FormatErrorType.ParamList);

        try {
            // split char error.
            parser.parse(1, "MTD\tsample_processing[x]\t[SEP, SEP:00142, enzyme digestion, ]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == LogicalErrorType.IdNumber);
            logger.debug(e.getMessage());
        }

        // param error.
        parser.parse(1, "MTD\tinstrument[1]-analyzer[1]\t[MS, MS:1000291, ,]", errorList);
        assertTrue(errorList.size() == 4);
        assertTrue(errorList.getError(3).getType() ==  FormatErrorType.Param);
    }

    @Test
    public void testPublication() throws Exception {

        // no error.
        parser.parse(1, "MTD\tpublication[1]\tpubmed:21063943|doi:10.1007/978-1-60761-987-1_6", errorList);
        assertTrue(errorList.size() == 0);

        // split char error.
        parser.parse(1, "MTD\tpublication[1]\tpubmed:21063943/doi:10.1007/978-1-60761-987-1_6", errorList);
        assertTrue(errorList.size() == 1);
        assertTrue(errorList.getError(0).getType() == FormatErrorType.Publication);

        // error publication item
        parser.parse(1, "MTD\tpublication[1]\tpub:21063943", errorList);
        assertTrue(errorList.size() == 2);
        assertTrue(errorList.getError(1).getType() == FormatErrorType.Publication);

        // split char error.
        parser.parse(1, "MTD\tpublication[1]\tdoi:21063943/pubmed:21063943", errorList);
        assertTrue(errorList.size() == 3);
        assertTrue(errorList.getError(2).getType() == FormatErrorType.Publication);
    }

    @Test
    public void testInvalidColUnitLabel() throws Exception {
        parser.parse(1, "MTD\tcolunit-unknown\tretention_time=[UO,UO:0000031, minute,]", errorList);
        assertTrue(errorList.getError(0).getType() == FormatErrorType.MTDDefineLabel);
        logger.debug(errorList.getError(0).getMessage());
    }

    @Test
    public void testNoColumnForColUnit() throws Exception {
        parser.parse(1, "MTD\tprotein_search_engine_score[1]\t[MS, MS:1001171, Mascot:score,]", errorList);
        parser.parse(1, "MTD\tcolunit-protein\tretention_time=[UO,UO:0000031, minute,]", errorList);

        PRHLineParser prtParser = new PRHLineParser(metadata);
        String headerLine = "PRH\t" +
                "accession\t" +
                "description\t" +
                "taxid\t" +
                "species\t" +
                "database\t" +
                "database_version\t" +
                "search_engine\t" +
                "best_search_engine_score[1]\t" +
                "ambiguity_members\t" +
                "modifications\t" +
                "uri\t" +
                "go_terms\t" +
                "protein_coverage";

        prtParser.parse(1, headerLine, errorList);
        assertTrue(errorList.getError(0).getType() == FormatErrorType.ColUnit);
        logger.debug(errorList.getError(0).getMessage());
    }

    @Test
    public void testColUnitParamParseError() throws Exception {
        parser.parse(1, "MTD\tpeptide_search_engine_score[1]\t[MS, MS:1001171, Mascot:score,]", errorList);
        parser.parse(1, "MTD\tcolunit-peptide\tretention_time=[UO,UO:0000031, ,]", errorList);
        PEHLineParser pehParser = new PEHLineParser(metadata);
        String headerLine = "PEH\t" +
                "sequence\t" +
                "accession\t" +
                "unique\t" +
                "database\t" +
                "database_version\t" +
                "search_engine\t" +
                "best_search_engine_score[1]\t" +
                "reliability\t" +
                "modifications\t" +
                "retention_time\t" +
                "retention_time_window\t" +
                "charge\t" +
                "mass_to_charge\t" +
                "uri\t" +
                "spectra_ref";
        pehParser.parse(1, headerLine, errorList);
        assertTrue(errorList.getError(0).getType() == FormatErrorType.Param);
        logger.debug(errorList.getError(0).getMessage());
    }

    @Test
    public void testDuplicationDefine() throws Exception {
        parser.parse(1, "MTD\tprotein-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]", errorList);
        assertTrue(true);

        try {
            // not allow duplicate twice.
            parser.parse(1, "MTD\tprotein-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == LogicalErrorType.DuplicationDefine);
            logger.debug(e.getMessage());
        }
    }

    @Test
    public void testDuplicationID() throws Exception {
        parser.parse(1, "MTD\tstudy_variable[1]-description\tdescription Group B (spike-in 0.74 fmol/uL)", errorList);
        assertTrue(errorList.isEmpty());

        Sample sample1 = new Sample(1);
        Sample sample2 = new Sample(2);
        metadata.addSample(sample1);
        metadata.addSample(sample2);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        metadata.addAssay(assay1);
        metadata.addAssay(assay2);

        parser.parse(1, "MTD\tstudy_variable[1]-assay_refs\tassay[1], assay[2]", errorList);
        assertTrue(errorList.isEmpty());

        parser.parse(1, "MTD\tstudy_variable[1]-assay_refs\tassay[1], assay[1]", errorList);
        assertTrue(errorList.getError(0).getType() == LogicalErrorType.DuplicationID);

        parser.parse(1, "MTD\tstudy_variable[1]-sample_refs\tsample[1], sample[1]", errorList);
        assertTrue(errorList.getError(1).getType() == LogicalErrorType.DuplicationID);
    }
}
