package uk.ac.ebi.pride.jmztab.utils.parser;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

import static junit.framework.Assert.assertTrue;

/**
 * Test all parser error.
 *
 * User: qingwei
 * Date: 09/09/13
 */
public class MTDLineValidateTest {
    private static Logger logger = Logger.getLogger(MTDLineValidateTest.class);

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
        parser.parse(1, "MTD\tinstrument[1]-analyzer\t[MS, MS:1000291, ,]", errorList);
        assertTrue(errorList.size() == 4);
        assertTrue(errorList.getError(3).getType() ==  FormatErrorType.Param);
    }

    @Test
    public void testPublication() throws Exception {
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
    public void testColUnit1() throws Exception {
        try {
            parser.parse(1, "MTD\tcolunit-unknown\tretention_time=[UO,UO:0000031, minute,]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MTDDefineLabel);
            logger.debug(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tcolunit-protein\tretention_time=[UO,UO:0000031, minute,]", errorList);
            parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Protein_Header));
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ColUnit);
            logger.debug(e.getMessage());
        }

    }

    @Test
    public void testColUnit2() throws Exception {
        try {
            parser.parse(1, "MTD\tcolunit-peptide\tretention_time=[UO,UO:0000031, ,]", errorList);
            parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Peptide_Header));
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Param);
            logger.debug(e.getMessage());
        }
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
