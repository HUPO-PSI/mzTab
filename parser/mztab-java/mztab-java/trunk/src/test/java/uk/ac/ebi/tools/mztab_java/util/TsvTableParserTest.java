package uk.ac.ebi.tools.mztab_java.util;

import java.util.Map;

import junit.framework.TestCase;

public class TsvTableParserTest extends TestCase {
	private TsvTableParser parser;

	public void testHasField() {
		parser = new TsvTableParser("PEH	sequence	accession	unit_id	unique	database	database_version	search_engine	search_engine_score	reliability	modifications	retention_time	charge	mass_to_charge	uri	peptide_abundance_sub[1]	peptide_abundance_stdev_sub[1]	peptide_abundance_std_error_sub[1]	peptide_abundance_sub[2]	peptide_abundance_stdev_sub[2]	peptide_abundance_std_error_sub[2]	peptide_abundance_sub[3]	peptide_abundance_stdev_sub[3]	peptide_abundance_std_error_sub[3]	peptide_abundance_sub[4]	peptide_abundance_stdev_sub[4]");
		
		assertTrue(parser.hasField("sequence"));
		assertTrue(parser.hasField("unit_id"));
		assertFalse(parser.hasField("peptide_sequence"));
	}

	public void testParseTableLine() {
		parser = new TsvTableParser("PEH	sequence	accession	unit_id	unique	database	database_version	search_engine	search_engine_score	reliability	modifications	retention_time	charge	mass_to_charge	uri	peptide_abundance_sub[1]	peptide_abundance_stdev_sub[1]	peptide_abundance_std_error_sub[1]	peptide_abundance_sub[2]	peptide_abundance_stdev_sub[2]	peptide_abundance_std_error_sub[2]	peptide_abundance_sub[3]	peptide_abundance_stdev_sub[3]	peptide_abundance_std_error_sub[3]	peptide_abundance_sub[4]	peptide_abundance_stdev_sub[4]	peptide_abundance_std_error_sub[4]");
		
		String line = "PEP	NILNELFQR	gi|10181184	PRIDE_1234	1	NCBI NR	21.06.2011	[MS,MS:1001207,Mascot,]	[MS,MS:1001171,Mascot:score,35]	3	1[0.8]-UNIMOD:35	20.8	2	500	--	1	--	--	4491.221363	--	--	4491.221363	--	--	4491.221363	--	--";
		
		Map<String, String> parsedLine = parser.parseTableLine(line.trim());
		
		assertEquals("NILNELFQR", parsedLine.get("sequence"));
		assertEquals("[MS,MS:1001207,Mascot,]", parsedLine.get("search_engine"));
	}

}
