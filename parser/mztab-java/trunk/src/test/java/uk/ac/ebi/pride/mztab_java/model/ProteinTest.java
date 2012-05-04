package uk.ac.ebi.pride.mztab_java.model;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jmzTab.MzTabParsingException;
import uk.ac.ebi.pride.jmzTab.model.Protein;
import uk.ac.ebi.pride.jmzTab.model.TableObject;
import uk.ac.ebi.pride.jmzTab.util.TsvTableParser;

public class ProteinTest extends TestCase {

	public void testMarshallingUnmarshalling() {
		String line1 = "PRT	gi|10181184	PRIDE_1234	Albumin	9606	Homo sapiens(Human)	NCBI NR	21.06.2011	[MS,MS:1001207,Mascot,]	[MS,MS:1001171,Mascot:score,140]	3	4	2	1		13[0.8]-UNIMOD:35,29[0.2]|35[0.4]-UNIMOD:21	http://ebi.ac.uk/pride/experiment/1234/protein/193ttp://ebi.ac.uk/pride/experiment/1234/protein/193	GO:0008299,GO:0016740	0.2	1.0	-	-	7265.31988	6346.894427	-	7265.31988	6346.894427	-	7265.31988	6346.894427	-";
		String line2 = "PRT	gi|1050551	PRIDE_1234	Thyroxin	9606	Homo sapiens(Human)	NCBI NR	21.06.2011	[MS,MS:1001207,Mascot,]	[MS,MS:1001171,Mascot:score,70]	2	2	1	1	gi|1049108	NA	http://ebi.ac.uk/pride/experiment/1234/protein/193ttp://ebi.ac.uk/pride/experiment/1234/protein/191		0.1	1.0	-	-	3447.020481	1962.7157	-	3447.020481	1962.7157	-	3447.020481	1962.7157	-";
		
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
