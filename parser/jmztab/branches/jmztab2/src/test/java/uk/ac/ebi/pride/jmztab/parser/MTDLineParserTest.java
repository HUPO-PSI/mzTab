package uk.ac.ebi.pride.jmztab.parser;

import org.apache.log4j.Logger;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabException;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
* User: Qingwei
* Date: 11/02/13
*/
public class MTDLineParserTest {
    private static Logger logger = Logger.getLogger(MTDLineParserTest.class);

    @Test
    public void testDefineLabel() throws Exception {
        MTDLineParser parser;

        parser = new MTDLineParser("MTD\tPRIDE_1234-title\tmzTab iTRAQ test");
        assertTrue(parser.getUnit().getUnitId().equals("PRIDE_1234"));
        assertTrue(parser.getElement().getName().equals("title"));
        assertTrue(parser.getProperty() == null);
        assertTrue(parser.getId() == null);

        parser = new MTDLineParser("MTD\tPRIDE_1234-contact[1]-name\tJohannes");
        assertTrue(parser.getUnit().getUnitId().equals("PRIDE_1234"));
        assertTrue(parser.getElement().getName().equals("contact"));
        assertTrue(parser.getProperty().getName().equals("name"));
        assertTrue(parser.getId() == 1);

        parser = new MTDLineParser(" MTD \t PRIDE_1234-sub[1]-description \t  Healthy human liver tissue  ");
        assertTrue(parser.getUnit().getIdentifier().equals("PRIDE_1234-sub[1]"));
        assertTrue(parser.getElement().getName().equals("description"));
        assertTrue(parser.getProperty() == null);
        assertTrue(parser.getId() == null);

        parser = new MTDLineParser("MTD\tPRIDE_1234-rep[1]\tReplicate 1 of experiment 1.");
        assertTrue(parser.getUnit().getIdentifier().equals("PRIDE_1234-rep[1]"));
        assertTrue(parser.getElement() == null);
        assertTrue(parser.getProperty() == null);
        assertTrue(parser.getId() == null);

        parser = new MTDLineParser("MTD\tEXP_1-rep[1]-instrument[1]-name\t[MS, MS:100049, LTQ Orbitrap, ]");
        assertTrue(parser.getUnit().getIdentifier().equals("EXP_1-rep[1]"));
        assertTrue(parser.getElement().getName().equals("instrument"));
        assertTrue(parser.getProperty().getName().equals("name"));
        assertTrue(parser.getId() == 1);

        parser = new MTDLineParser("MTD\tPRIDE_1234-sub[2]-species[1]\t[PRIDE, PRIDE:0000115, iTRAQ reagent, 115]");
        assertTrue(parser.getUnit().getIdentifier().equals("PRIDE_1234-sub[2]"));
        assertTrue(parser.getElement().getName().equals("species"));
        assertTrue(parser.getProperty() == null);
        assertTrue(parser.getId() == 1);
    }

//    @Test
//    public void testDefineLabelException() throws Exception {
//        MTDLineParser parser;
//
//        try {
//            parser = new MTDLineParser("MTD\t1234-title\tmzTab iTRAQ test");
//            fail();
//        } catch (MZTabException e) {
//            assertTrue(e.getMessage().contains(FormatErrorType.UnitID.getCode().toString()));
//            logger.debug(e.getMessage());
//        }
//    }
}
