package uk.ac.ebi.pride.mztab_java.model;

import junit.framework.TestCase;
import uk.ac.ebi.pride.mztab_java.MzTabParsingException;

public class UnitTest extends TestCase {
    private Unit unit = new Unit();

    private static final String mzTabString = "MTD	PRIDE_1234-title	mzTab iTRAQ\n" +
            "MTD	PRIDE_1234-sample_processing[1]	[MS,MS:1234,first step,]|[MS,MS:1235,describing first step,]\n" +
            "MTD	PRIDE_1234-contact[1]-name	Johannes\n" +
            "MTD	PRIDE_1234-contact[2]-name	Mathias\n" +
            "MTD	PRIDE_1234-sub[1]-description	Healthy human liver\n" +
            "MTD	PRIDE_1234-sub[1]-quantitation_reagent	[PRIDE,PRIDE:0000114,iTRAQ reagent 114,]\n" +
            "MTD	PRIDE_1234-sub[2]-description	Human hepatocellular carcinoma\n" +
            "MTD	PRIDE_1234-sub[2]-quantitation_reagent	[PRIDE,PRIDE:0000115,iTRAQ reagent 115,]\n";

    public void testUnmarshall() {
        try {
            unit.unmarshall(mzTabString);

            assertEquals("PRIDE_1234", unit.getUnitId());
            assertEquals("mzTab iTRAQ", unit.getTitle());
            assertEquals(2, unit.getContact().size());
            assertEquals("Johannes", unit.getContact().get(0).getName());
            assertEquals("Mathias", unit.getContact().get(1).getName());
            assertEquals(2, unit.getSubsamples().size());
            assertEquals("Healthy human liver", unit.getSubsamples().get(0).getDescription());
            assertEquals("Human hepatocellular carcinoma", unit.getSubsamples().get(1).getDescription());
            assertEquals("[PRIDE,PRIDE:0000114,iTRAQ reagent 114,]", unit.getSubsamples().get(0).getQuantitationReagent().toString());
            assertEquals("[PRIDE,PRIDE:0000115,iTRAQ reagent 115,]", unit.getSubsamples().get(1).getQuantitationReagent().toString());

            assertEquals(1, unit.getSampleProcessing().size());
            assertEquals(2, unit.getSampleProcessing().get(0).size());

            assertEquals(mzTabString, unit.toMzTab());

        } catch (MzTabParsingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
