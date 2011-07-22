package uk.ac.ebi.pride.mztab_java.model;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;
import uk.ac.ebi.pride.mztab_java.MzTabParsingException;
import uk.ac.ebi.pride.mztab_java.model.SmallMolecule;
import uk.ac.ebi.pride.mztab_java.model.TableObject;
import uk.ac.ebi.pride.mztab_java.util.TsvTableParser;

public class SmallMoleculeTest extends TestCase {
	public void testMarshallingUnmarshalling() {
		String line1 = "SML	The molecule	PRIDE_1234	A molecule that just rocks.	143.45	2	10.0,28.8	9606	Human (Homo sapiens)	--	--	2	--	[SpectraST,]	--	NA	This col is cool!";
		
		TsvTableParser parser = new TsvTableParser("SMH	identifier	unit_id	description	mass_to_charge	charge	retention_time	taxid	species	database	database_version	reliability	uri	search_engine	search_engine_score	modifications	opt_my_column");
		Map<String, String> parsedLine1 = parser.parseTableLine(line1);
		
		try {
			SmallMolecule m1 = new SmallMolecule(parsedLine1);
			
			assertNotNull(m1);
			ArrayList<String> optCols = new ArrayList<String>(1);
			optCols.add("opt_my_column");
			assertEquals(line1.trim() + TableObject.EOL, m1.toMzTab(0, optCols));
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
