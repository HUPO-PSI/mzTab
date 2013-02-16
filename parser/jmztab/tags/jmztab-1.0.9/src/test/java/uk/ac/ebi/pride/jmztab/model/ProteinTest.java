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
	String line1 = "PRT\tgi|10181184\tPRIDE_1234\tAlbumin\t9606\tHomo sapiens(Human)\tNCBI NR\t21.06.2011\t[MS,MS:1001207,Mascot,]\t[MS,MS:1001171,Mascot:score,140]\t3\t4\t2\t1\tnull\t13[,,my score,0.8]-UNIMOD:35,29[,,another score,0.2]|35[,,my score,0.4]-UNIMOD:21\thttp://ebi.ac.uk/pride/experiment/1234/protein/193ttp://ebi.ac.uk/pride/experiment/1234/protein/193\tGO:0008299|GO:0016740\t0.2\t1.0\tnull\tnull\t7265.31988\t6346.894427\tnull\t7265.31988\t6346.894427\tnull\t7265.31988\t6346.894427\tnull";
	String line2 = "PRT\tgi|1050551\tPRIDE_1234\tThyroxin\t9606\tHomo sapiens(Human)\tNCBI NR\t21.06.2011\t[MS,MS:1001207,Mascot,]\t[MS,MS:1001171,Mascot:score,70]\t2\t2\t1\t1\tgi|1049108\tnull\thttp://ebi.ac.uk/pride/experiment/1234/protein/193ttp://ebi.ac.uk/pride/experiment/1234/protein/191\tnull\t0.1\t1.0\tnull\tnull\t3447.020481\t1962.7157\tnull\t3447.020481\t1962.7157\tnull\t3447.020481\t1962.7157\tnull";

	TsvTableParser parser = new TsvTableParser("PRH	accession\tunit_id\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\tsearch_engine\tsearch_engine_score\treliability\tnum_peptides\tnum_peptides_distinct\tnum_peptides_unambiguous\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage\tprotein_abundance_sub[1]\tprotein_abundance_stdev_sub[1]\tprotein_abundance_std_error_sub[1]\tprotein_abundance_sub[2]\tprotein_abundance_stdev_sub[2]\tprotein_abundance_std_error_sub[2]\tprotein_abundance_sub[3]\tprotein_abundance_stdev_sub[3]\tprotein_abundance_std_error_sub[3]\tprotein_abundance_sub[4]\tprotein_abundance_stdev_sub[4]\tprotein_abundance_std_error_sub[4]");
	Map<String, String> parsedLine1 = parser.parseTableLine(line1);
	Map<String, String> parsedLine2 = parser.parseTableLine(line2);

	try {
	    Protein p1 = new Protein(parsedLine1);
	    Protein p2 = new Protein(parsedLine2);

	    assertNotNull(p1);
	    assertNotNull(p2);

	    assertEquals(2, p1.getGoTerms().size());

	    assertEquals(line1 + TableObject.EOL, p1.toMzTab(4, Collections.EMPTY_LIST));
	    assertEquals(line2 + TableObject.EOL, p2.toMzTab(4, Collections.EMPTY_LIST));
	} catch (MzTabParsingException e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }
}
