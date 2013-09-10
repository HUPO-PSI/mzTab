package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Section;
import uk.ac.ebi.pride.jmztab.utils.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

import static junit.framework.Assert.assertTrue;

/**
 * Test all parser error.
 *
 * User: qingwei
 * Date: 09/09/13
 */
public class MTDLineValidateTest {
    @Test
    public void testMZTabDescription() throws Exception {
        MTDLineParser parser = new MTDLineParser();

        try {
            parser.parse(1, "MTD\tmzTab-ver\t1.0 rc5");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MTDDefineLabel);
            System.out.println(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tmzTab-mode\tUnknow");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MZTabMode);
            System.out.println(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tmzTab-type\tUnknow");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MZTabType);
            System.out.println(e.getMessage());
        }        
    }

    @Test
    public void testIndexElement() throws Exception {
        MTDLineParser parser = new MTDLineParser();

        try {
            // param name can not set empty.
            parser.parse(1, "MTD\tsample_processing[1]\t[, SEP:00173, ,]");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ParamList);
            System.out.println(e.getMessage());
        }

        try {
            // second param name can not set empty.
            parser.parse(1, "MTD\tsample_processing[1]\t[SEP, SEP:00142, enzyme digestion, ]|[MS, MS:1001251, , ]");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ParamList);
            System.out.println(e.getMessage());
        }

        try {
            // split char error.
            parser.parse(1, "MTD\tsample_processing[1]\t[SEP, SEP:00142, enzyme digestion, ]/[MS, MS:1001251, Trypsin, ]");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ParamList);
            System.out.println(e.getMessage());
        }

        try {
            // split char error.
            parser.parse(1, "MTD\tsample_processing[x]\t[SEP, SEP:00142, enzyme digestion, ]");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MTDDefineLabel);
            System.out.println(e.getMessage());
        }

        try {
            // param error.
            parser.parse(1, "MTD\tinstrument[1]-analyzer\t[MS, MS:1000291, ,]");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Param);
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testPublication() throws Exception {
        MTDLineParser parser = new MTDLineParser();

        try {
            // split char error.
            parser.parse(1, "MTD\tpublication[1]\tpubmed:21063943/doi:10.1007/978-1-60761-987-1_6");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Publication);
            System.out.println(e.getMessage());
        }

        try {
            // error publication item
            parser.parse(1, "MTD\tpublication[1]\tpub:21063943");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Publication);
            System.out.println(e.getMessage());
        }

        try {
            // split char error.
            parser.parse(1, "MTD\tpublication[1]\tdoi:21063943/pubmed:21063943");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Publication);
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testColUnit() throws Exception {
        MTDLineParser parser = new MTDLineParser();

        try {
            parser.parse(1, "MTD\tcolunit-unknown\tretention_time=[UO,UO:0000031, minute,]");
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.MTDDefineLabel);
            System.out.println(e.getMessage());
        }

        try {
            parser.parse(1, "MTD\tcolunit-protein\tretention_time=[UO,UO:0000031, minute,]");
            parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Protein_Header));
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.ColUnit);
            System.out.println(e.getMessage());
        }

        // clear colunit map.
        parser = new MTDLineParser();
        try {
            parser.parse(1, "MTD\tcolunit-peptide\tretention_time=[UO,UO:0000031, ,]");
            parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Peptide_Header));
            assertTrue(false);
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == FormatErrorType.Param);
            System.out.println(e.getMessage());
        }
    }
}
