package uk.ac.ebi.pride.mztab_java.model;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import uk.ac.ebi.pride.mztab_java.MzTabParsingException;
import uk.ac.ebi.pride.mztab_java.model.Peptide;
import uk.ac.ebi.pride.mztab_java.model.TableObject;
import uk.ac.ebi.pride.mztab_java.util.TsvTableParser;

public class PeptideTest extends TestCase {
	public void testMarshallingUnmarshalling() {
		String line1 = "PEP	NILNELFQR	gi|10181184	PRIDE_1234	true	NCBI NR	21.06.2011	[MS,MS:1001207,Mascot,]	[MS,MS:1001171,Mascot:score,35]	3	1[0.8]-UNIMOD:35	20.8	2	500	--	ms_file[1]:index=4	1.0	--	--	4491.221363	--	--	4491.221363	--	--	4491.221363	--	--				";
		String line2 = "PEP	QGIFQVVISR	gi|10181184	PRIDE_1234	false	NCBI NR	21.06.2011	[MS,MS:1001207,Mascot,]	[MS,MS:1001171,Mascot:score,40]	4	6[0.4]-UNIMOD:21	10.2	3	200	--	ms_file[2]:index=5	1.0	--	--	16500.75069	--	--	16500.75069	--	--	16500.75069	--	--				";
		
		TsvTableParser parser = new TsvTableParser("PEH	sequence	accession	unit_id	unique	database	database_version	search_engine	search_engine_score	reliability	modifications	retention_time	charge	mass_to_charge	uri	spec_ref	peptide_abundance_sub[1]	peptide_abundance_stdev_sub[1]	peptide_abundance_std_error_sub[1]	peptide_abundance_sub[2]	peptide_abundance_stdev_sub[2]	peptide_abundance_std_error_sub[2]	peptide_abundance_sub[3]	peptide_abundance_stdev_sub[3]	peptide_abundance_std_error_sub[3]	peptide_abundance_sub[4]	peptide_abundance_stdev_sub[4]	peptide_abundance_std_error_sub[4]				");
		Map<String, String> parsedLine1 = parser.parseTableLine(line1);
		Map<String, String> parsedLine2 = parser.parseTableLine(line2);
		
		try {
			Peptide p1 = new Peptide(parsedLine1);
			Peptide p2 = new Peptide(parsedLine2);
			
			assertNotNull(p1);
			assertNotNull(p2);
			
			assertEquals(line1.trim() + TableObject.EOL, p1.toMzTab(4, Collections.EMPTY_LIST));
			assertEquals(line2.trim() + TableObject.EOL, p2.toMzTab(4, Collections.EMPTY_LIST));
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
