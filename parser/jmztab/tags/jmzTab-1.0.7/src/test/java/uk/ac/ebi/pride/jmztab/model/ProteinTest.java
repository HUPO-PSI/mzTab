package uk.ac.ebi.pride.jmztab.model;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.Protein;
import uk.ac.ebi.pride.jmztab.model.TableObject;
import uk.ac.ebi.pride.jmztab.util.TsvTableParser;

public class ProteinTest extends TestCase {

	public void testMarshallingUnmarshalling() {
		String line1 = "PRT\tgi|10181184\tPRIDE_1234\tAlbumin\t9606\tHomo sapiens(Human)\tNCBI NR\t21.06.2011\t[MS,MS:1001207,Mascot,]\t[MS,MS:1001171,Mascot:score,140]\t3\t4\t2\t1\tNA\t13[0.8]-UNIMOD:35,29[0.2]|35[0.4]-UNIMOD:21\thttp://ebi.ac.uk/pride/experiment/1234/protein/193ttp://ebi.ac.uk/pride/experiment/1234/protein/193\tGO:0008299,GO:0016740\t0.2\t1.0\tNA\tNA\t7265.31988\t6346.894427\tNA\t7265.31988\t6346.894427\tNA\t7265.31988\t6346.894427\tNA";
		String line2 = "PRT\tgi|1050551\tPRIDE_1234\tThyroxin\t9606\tHomo sapiens(Human)\tNCBI NR\t21.06.2011\t[MS,MS:1001207,Mascot,]\t[MS,MS:1001171,Mascot:score,70]\t2\t2\t1\t1\tgi|1049108\tNA\thttp://ebi.ac.uk/pride/experiment/1234/protein/193ttp://ebi.ac.uk/pride/experiment/1234/protein/191\tNA\t0.1\t1.0\tNA\tNA\t3447.020481\t1962.7157\tNA\t3447.020481\t1962.7157\tNA\t3447.020481\t1962.7157\tNA";
		
		TsvTableParser parser = new TsvTableParser("PRH	accession	unit_id	description	taxid	species	database	database_version	search_engine	search_engine_score	reliability	num_peptides	num_peptides_distinct	num_peptides_unambiguous	ambiguity_members	modifications	uri	go_terms	protein_coverage	protein_abundance_sub[1]	protein_abundance_stdev_sub[1]	protein_abundance_std_error_sub[1]	protein_abundance_sub[2]	protein_abundance_stdev_sub[2]	protein_abundance_std_error_sub[2]	protein_abundance_sub[3]	protein_abundance_stdev_sub[3]	protein_abundance_std_error_sub[3]	protein_abundance_sub[4]	protein_abundance_stdev_sub[4]	protein_abundance_std_error_sub[4]");
		Map<String, String> parsedLine1 = parser.parseTableLine(line1);
		Map<String, String> parsedLine2 = parser.parseTableLine(line2);
		
		try {
			Protein p1 = new Protein(parsedLine1);
			Protein p2 = new Protein(parsedLine2);
			
			assertNotNull(p1);
			assertNotNull(p2);
			
			assertEquals(line1 + TableObject.EOL, p1.toMzTab(4, Collections.EMPTY_LIST));
			assertEquals(line2 + TableObject.EOL, p2.toMzTab(4, Collections.EMPTY_LIST));
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
