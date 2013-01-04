package uk.ac.ebi.pride.jmztab.model;

import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.util.TsvTableParser;

public class PeptideTest extends TestCase {
	public void testMarshallingUnmarshalling() {
		String line1 = "PEP\tNILN[14]ELFQR\tgi|10181184\tPRIDE_1234\t1\tNCBI NR\t21.06.2011\t[MS,MS:1001207,Mascot,]\t[MS,MS:1001171,Mascot:score,35]\t3\t1[0.8]-UNIMOD:35\t20.8\t2\t500\tnull\tms_file[1]:index=4\t1.0\tnull\tnull\t4491.221363\tnull\tnull\t4491.221363\tnull\tnull\t4491.221363\tnull\tnull\t\t\t\t";
		String line2 = "PEP\tQG(my mod)IFQVVISR\tgi|10181184\tPRIDE_1234\t0\tNCBI NR\t21.06.2011\t[MS,MS:1001207,Mascot,]\t[MS,MS:1001171,Mascot:score,40]\t1\t6[0.4]-UNIMOD:21\t10.2\t3\t200\tnull\tms_file[2]:index=5\t1.0\tnull\tnull\t16500.75069\tnull\tnull\t16500.75069\tnull\tnull\t16500.75069\tnull\tnull\t\t\t\t";

		TsvTableParser parser = new TsvTableParser("PEH\tsequence\taccession\tunit_id\tunique\tdatabase\tdatabase_version\tsearch_engine\tsearch_engine_score\treliability\tmodifications\tretention_time\tcharge\tmass_to_charge\turi\tspectra_ref\tpeptide_abundance_sub[1]\tpeptide_abundance_stdev_sub[1]\tpeptide_abundance_std_error_sub[1]\tpeptide_abundance_sub[2]\tpeptide_abundance_stdev_sub[2]\tpeptide_abundance_std_error_sub[2]\tpeptide_abundance_sub[3]\tpeptide_abundance_stdev_sub[3]\tpeptide_abundance_std_error_sub[3]\tpeptide_abundance_sub[4]\tpeptide_abundance_stdev_sub[4]\tpeptide_abundance_std_error_sub[4]\t\t\t\t");
		Map<String, String> parsedLine1 = parser.parseTableLine(line1);
		Map<String, String> parsedLine2 = parser.parseTableLine(line2);

		try {
			Peptide p1 = new Peptide(parsedLine1);
			Peptide p2 = new Peptide(parsedLine2);

			assertNotNull(p1);
			assertNotNull(p2);

			assertEquals("NILN[14]ELFQR", p1.getSequence());
			assertEquals("NILNELFQR", p1.getCleanSequence());

			assertEquals("QG(my mod)IFQVVISR", p2.getSequence());
			assertEquals("QGIFQVVISR", p2.getCleanSequence());

			assertEquals(line1.trim() + TableObject.EOL, p1.toMzTab(4, Collections.EMPTY_LIST));
			assertEquals(line2.trim() + TableObject.EOL, p2.toMzTab(4, Collections.EMPTY_LIST));
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
