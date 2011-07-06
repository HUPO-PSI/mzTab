package uk.ac.ebi.tools.mztab_java.model;

import uk.ac.ebi.tools.mztab_java.MzTabParsingException;
import junit.framework.TestCase;

public class UnitTest extends TestCase {
	private Unit unit = new Unit();
	
	public void testUnmarshall() {
		try {
			unit.unmarshall("MTD	PRIDE_1234-title	mzTab iTRAQ");
			unit.unmarshall("MTD	PRIDE_1234-contact[1]-name	Johannes");
			unit.unmarshall("MTD	PRIDE_1234-contact[2]-name	Mathias");
			unit.unmarshall("MTD	PRIDE_1234-sub[1]-description	Healthy human liver");
			unit.unmarshall("MTD	PRIDE_1234-sub[2]-description	Human hepatocellular carcinoma");
			unit.unmarshall("MTD	PRIDE_1234-sub[1]-quantitation_reagent	[PRIDE,PRIDE:0000114,iTRAQ reagent 114,]");
			unit.unmarshall("MTD	PRIDE_1234-sub[2]-quantitation_reagent	[PRIDE,PRIDE:0000115,iTRAQ reagent 115,]");
			unit.unmarshall("MTD	PRIDE_1234-sample_processing[1]	[MS,MS:1234,first step,] | [MS,MS:1235,describing first step,]");
			
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
			
			assertEquals("PRIDE_1234-title	mzTab iTRAQ\nPRIDE_1234-sample_processing[1]	[MS,MS:1234,first step,]|[MS,MS:1235,describing first step,]\nPRIDE_1234-contact[1]-name	Johannes\nPRIDE_1234-contact[2]-name	Mathias\nPRIDE_1234-sub[1]	Healthy human liver\nPRIDE_1234-sub[1]	[PRIDE,PRIDE:0000114,iTRAQ reagent 114,]\nPRIDE_1234-sub[2]	Human hepatocellular carcinoma\nPRIDE_1234-sub[2]	[PRIDE,PRIDE:0000115,iTRAQ reagent 115,]\n", unit.toMzTab());
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
