package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.SmallMolecule;
import uk.ac.ebi.pride.jmztab.model.TableObject;
import uk.ac.ebi.pride.jmztab.util.TsvTableParser;

public class SmallMoleculeTest extends TestCase {
	public void testMarshallingUnmarshalling() {
		String line1 = "SML\tThe molecule\tPRIDE_1234\tH2O\tNA\tNA\tA molecule that just rocks.\t143.45\t2\t10.0,28.8\t9606\tHuman (Homo sapiens)\tNA\tNA\t2\tNA\tms_file[4]:1\t[,,SpectraST,]\tNA\tNA\tThis col is cool!";
		
		TsvTableParser parser = new TsvTableParser("SMH\tidentifier\tunit_id\tchemical_formula\tsmiles\tinchi_key\tdescription\tmass_to_charge\tcharge\tretention_time\ttaxid\tspecies\tdatabase\tdatabase_version\treliability\turi\tspectra_ref\tsearch_engine\tsearch_engine_score\tmodifications\topt_my_column");
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
