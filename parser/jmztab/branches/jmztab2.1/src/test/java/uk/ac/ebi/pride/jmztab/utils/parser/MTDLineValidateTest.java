package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Section;
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
    private MTDLineParser parser;
    private MZTabErrorList errorList;

    @Before
    public void setUp() throws Exception {
        parser = new MTDLineParser();
        parser.getMetadata().setDescription("test...");
        errorList = new MZTabErrorList();
    }

    @Test
    public void testMZTabDescription() throws Exception {
        try {
            parser.parse(1, "MTD\tmzTab-ver\t1.0 rc5", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MTDDefineLabel);
            System.out.println(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tmzTab-mode\tUnknow", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MZTabMode);
            System.out.println(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tmzTab-type\tUnknow", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MZTabType);
            System.out.println(e.getMessage());
        }        
    }

    @Test
    public void testIndexElement() throws Exception {
        try {
            // param name can not set empty.
            parser.parse(1, "MTD\tsample_processing[1]\t[, SEP:00173, ,]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ParamList);
            System.out.println(e.getMessage());
        }

        try {
            // second param name can not set empty.
            parser.parse(1, "MTD\tsample_processing[1]\t[SEP, SEP:00142, enzyme digestion, ]|[MS, MS:1001251, , ]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ParamList);
            System.out.println(e.getMessage());
        }

        try {
            // split char error.
            parser.parse(1, "MTD\tsample_processing[1]\t[SEP, SEP:00142, enzyme digestion, ]/[MS, MS:1001251, Trypsin, ]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ParamList);
            System.out.println(e.getMessage());
        }

        try {
            // split char error.
            parser.parse(1, "MTD\tsample_processing[x]\t[SEP, SEP:00142, enzyme digestion, ]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == LogicalErrorType.IdNumber);
            System.out.println(e.getMessage());
        }

        try {
            // param error.
            parser.parse(1, "MTD\tinstrument[1]-analyzer\t[MS, MS:1000291, ,]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Param);
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testPublication() throws Exception {
        try {
            // split char error.
            parser.parse(1, "MTD\tpublication[1]\tpubmed:21063943/doi:10.1007/978-1-60761-987-1_6", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Publication);
            System.out.println(e.getMessage());
        }

        try {
            // error publication item
            parser.parse(1, "MTD\tpublication[1]\tpub:21063943", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Publication);
            System.out.println(e.getMessage());
        }

        try {
            // split char error.
            parser.parse(1, "MTD\tpublication[1]\tdoi:21063943/pubmed:21063943", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Publication);
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testColUnit1() throws Exception {
        try {
            parser.parse(1, "MTD\tcolunit-unknown\tretention_time=[UO,UO:0000031, minute,]", errorList);
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MTDDefineLabel);
            System.out.println(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tcolunit-protein\tretention_time=[UO,UO:0000031, minute,]", errorList);
            parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Protein_Header));
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ColUnit);
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
    }
}
