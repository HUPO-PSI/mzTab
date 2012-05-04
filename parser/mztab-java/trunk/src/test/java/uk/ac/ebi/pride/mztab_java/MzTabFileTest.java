package uk.ac.ebi.pride.mztab_java;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jmzTab.MzTabFile;
import uk.ac.ebi.pride.jmzTab.MzTabParsingException;
import uk.ac.ebi.pride.jmzTab.model.MsFile;
import uk.ac.ebi.pride.jmzTab.model.Peptide;
import uk.ac.ebi.pride.jmzTab.model.Protein;
import uk.ac.ebi.pride.jmzTab.model.SmallMolecule;
import uk.ac.ebi.pride.jmzTab.model.Unit;
import uk.ac.ebi.pride.jmzTab.util.TsvTableParser;

public class MzTabFileTest extends TestCase {
	MzTabFile mzTabFile;
	File sourceFile;
	
	public void setUp() throws Exception {
		// create the mascot dao
	    try {
	        URL testFile = getClass().getClassLoader().getResource("mztab_merged_example.txt");
	        assertNotNull("Error loading mzTab test file", testFile);
	        sourceFile = new File(testFile.toURI());
	        mzTabFile = new MzTabFile(sourceFile);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	        fail(ex.getMessage());
	    }
	}

	public void testGetUnitMetadataString() {
		Unit pride = mzTabFile.getUnitMetadata("PRIDE_1234");
		Unit file1 = mzTabFile.getUnitMetadata("file_1");
		
		assertNotNull(pride);
		assertNotNull(file1);
		
		assertEquals("mzTab iTRAQ test", pride.getTitle());
		assertEquals("mzTab SILAC test", file1.getTitle());
		
		assertEquals(4, pride.getSubsamples().size());
		
		assertEquals("Human hepatocellular carcinoma sample.", pride.getSubsamples().get(1).getDescription());
		
		try {
			MsFile msFile = pride.getMsFile(1);
			
			assertNotNull(msFile);
            assertEquals("/some/local/path", msFile.getLocation());
            assertEquals("MS:1000584", msFile.getFormat().getAccession());
			
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testGetUnitMetadata() {
		Collection<Unit> units = mzTabFile.getUnitMetadata();
		
		for (Unit u : units) {
			assertNotNull(u);
		}
	}

	public void testGetUnitIds() {
		Collection<String> ids = mzTabFile.getUnitIds();
		
		assertEquals(2, ids.size());
		assertEquals("[PRIDE_1234, file_1]", ids.toString());
	}

	public void testGetProteinString() {
		Collection<Protein> proteins = mzTabFile.getProtein("IPI00002824");
		
		assertEquals(1, proteins.size());
		Protein p = proteins.iterator().next();
		
		assertEquals("PRT\tIPI00002824\tfile_1\tThyroxin\t9606\tHomo sapiens\tIPI\t3.11\t[MS,MS:12345,Andromeda,]\t[MS,MS:100022,Andromeda protein score,30]\t-\t1\t1\t-\tIPI00793531,IPI00791139\tNA\t-\tGO:0005488,GO:0008270,GO:0043167,GO:0043169,GO:0046872,GO:0046914\t5.1\t1.0\t-\t-\t2.3456\t0.12\t-\t-\t-\t-\t-\t-\t-" + MzTabFile.EOL, p.toMzTab(4, Collections.EMPTY_LIST));
	}

	public void testGetProteinStringString() {
		Protein p = mzTabFile.getProtein("IPI00002824", "file_1");
		
		assertNotNull(p);
		assertEquals("PRT\tIPI00002824\tfile_1\tThyroxin\t9606\tHomo sapiens\tIPI\t3.11\t[MS,MS:12345,Andromeda,]\t[MS,MS:100022,Andromeda protein score,30]\t-\t1\t1\t-\tIPI00793531,IPI00791139\tNA\t-\tGO:0005488,GO:0008270,GO:0043167,GO:0043169,GO:0046872,GO:0046914\t5.1\t1.0\t-\t-\t2.3456\t0.12\t-\t-\t-\t-\t-\t-\t-" + MzTabFile.EOL, p.toMzTab(4, Collections.EMPTY_LIST));
	}

	public void testGetUnitProteins() {
		Collection<Protein> proteins = mzTabFile.getUnitProteins("file_1");
		
		assertEquals(5, proteins.size());
	}

	public void testGetProteins() {
		Collection<Protein> proteins = mzTabFile.getProteins();
		
		assertEquals(7, proteins.size());
	}

	public void testGetProteinPeptidesStringString() {
		Collection<Peptide> peptides = mzTabFile.getProteinPeptides("gi|10181184", "PRIDE_1234");
		
		assertEquals(4, peptides.size());
		
		for (Peptide p : peptides)
			assertEquals("gi|10181184", p.getAccession());
	}

	public void testGetProteinPeptidesString() {
		Collection<Peptide> peptides = mzTabFile.getProteinPeptides("gi|10181184");
		
		assertEquals(4, peptides.size());
		
		for (Peptide p : peptides)
			assertEquals("gi|10181184", p.getAccession());
	}

	public void testGetPeptidesString() {
		Collection<Peptide> peptides = mzTabFile.getPeptides("PRIDE_1234");
		
		assertEquals(6, peptides.size());
		
		for (Peptide p : peptides)
			assertEquals("PRIDE_1234", p.getUnitId());
	}

	public void testGetPeptides() {
		Collection<Peptide> peptides = mzTabFile.getPeptides();
		
		assertEquals(20, peptides.size());
	}

	public void testGetPeptidesForSequence() {
		Collection<Peptide> peptides = mzTabFile.getPeptidesForSequence("EDTTSILPK");
		
		assertEquals(2, peptides.size());
		
		for (Peptide p : peptides)
			assertEquals("EDTTSILPK", p.getSequence());
	}

	public void testGetSmallMolecules() {
		Collection<SmallMolecule> smallMolecules = mzTabFile.getSmallMolecules();
		
		assertEquals(0, smallMolecules.size());
	}

	public void testGetSmallMoleculesString() {
		Collection<SmallMolecule> smallMolecules = mzTabFile.getSmallMolecules("PRIDE_1234");
		
		assertNull(smallMolecules);
	}

	public void testGetSmallMoleculesForIdentifier() {
		Collection<SmallMolecule> smallMolecules = mzTabFile.getSmallMoleculesForIdentifier("CHEM:1234");
		
		assertNull(smallMolecules);
	}

	public void testAddProtein() {
		String line1 = "PRT\tIPI00004942\tNEW_UNIT\tAlbumin\t9606\tHomo sapiens(Human)\tNCBI NR\t21.06.2011\t[MS,MS:1001207,Mascot,]\t[MS,MS:1001171,Mascot:score,140]\t3\t4\t2\t1\t\t13[0.8]-UNIMOD:35,29[0.2]|35[0.4]-UNIMOD:21\thttp://ebi.ac.uk/pride/experiment/1234/protein/193ttp://ebi.ac.uk/pride/experiment/1234/protein/193\tGO:0008299,GO:0016740\t0.2\t1.0\t-\t-\t7265.31988\t6346.894427\t-\t7265.31988\t6346.894427\t-\t7265.31988\t6346.894427\t-";
		
		TsvTableParser parser = new TsvTableParser("PRH\taccession\tunit_id\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\tsearch_engine\tsearch_engine_score\treliability\tnum_peptides\tnum_peptides_distinct\tnum_peptides_unambiguous\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage\tprotein_abundance_sub[1]\tprotein_abundance_stdev_sub[1]\tprotein_abundance_std_error_sub[1]\tprotein_abundance_sub[2]\tprotein_abundance_stdev_sub[2]\tprotein_abundance_std_error_sub[2]\tprotein_abundance_sub[3]\tprotein_abundance_stdev_sub[3]\tprotein_abundance_std_error_sub[3]\tprotein_abundance_sub[4]\tprotein_abundance_stdev_sub[4]\tprotein_abundance_std_error_sub[4]");
		Map<String, String> parsedLine1 = parser.parseTableLine(line1);
		
		try {
			Protein p1 = new Protein(parsedLine1);
			
			assertEquals(2, mzTabFile.getUnitIds().size());
			assertEquals(1, mzTabFile.getProtein("IPI00004942").size());
			
			mzTabFile.addProtein(p1);
			
			Protein p2 = mzTabFile.getProtein("IPI00004942", "NEW_UNIT");
			assertNotNull(p2);
			
			assertEquals(3, mzTabFile.getUnitIds().size());
			assertEquals(2, mzTabFile.getProtein("IPI00004942").size());
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testAddPeptide() {
		String line1 = "PEP\tNILNELFQR\tgi|10181184\tNEW_UNIT\ttrue\tNCBI NR\t21.06.2011\t[MS,MS:1001207,Mascot,]\t[MS,MS:1001171,Mascot:score,35]\t3\t1[0.8]-UNIMOD:35\t20.8\t2\t500\t-\t1.0\t-\t-\t4491.221363\t-\t-\t4491.221363\t-\t-\t4491.221363\t-\t-\t\t\t\t";
		
		TsvTableParser parser = new TsvTableParser("PEH\tsequence\taccession\tunit_id\tunique\tdatabase\tdatabase_version\tsearch_engine\tsearch_engine_score\treliability\tmodifications\tretention_time\tcharge\tmass_to_charge\turi\tpeptide_abundance_sub[1]\tpeptide_abundance_stdev_sub[1]\tpeptide_abundance_std_error_sub[1]\tpeptide_abundance_sub[2]\tpeptide_abundance_stdev_sub[2]\tpeptide_abundance_std_error_sub[2]\tpeptide_abundance_sub[3]\tpeptide_abundance_stdev_sub[3]\tpeptide_abundance_std_error_sub[3]\tpeptide_abundance_sub[4]\tpeptide_abundance_stdev_sub[4]\tpeptide_abundance_std_error_sub[4]\t\t\t\t");
		Map<String, String> parsedLine1 = parser.parseTableLine(line1);
		
		try {
			Peptide p1 = new Peptide(parsedLine1);

			assertEquals(2, mzTabFile.getUnitIds().size());
			assertNull(mzTabFile.getPeptides("NEW_UNIT"));
			assertEquals(2, mzTabFile.getPeptidesForSequence("NILNELFQR").size());
			assertEquals(4, mzTabFile.getProteinPeptides("gi|10181184").size());
			
			mzTabFile.addPeptide(p1);
			
			assertEquals(3, mzTabFile.getUnitIds().size());
			assertEquals(1, mzTabFile.getPeptides("NEW_UNIT").size());
			assertEquals(3, mzTabFile.getPeptidesForSequence("NILNELFQR").size());
			assertEquals(5, mzTabFile.getProteinPeptides("gi|10181184").size());
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
