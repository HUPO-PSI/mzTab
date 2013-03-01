package uk.ac.ebi.pride.jmztab.parser;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;

/**
 * User: Qingwei
 * Date: 20/02/13
 */
public class MTDDataLineParserTest {
    private Metadata metadata;

    @Before
    public void setUp() throws Exception {
        MTDLineParserTest test = new MTDLineParserTest();
        metadata = test.parseMetadata("testset/mtdFile.txt");
    }

    @Test
    public void testCheckProteinData() throws Exception {
        String header = "PRH\taccession\tunit_id\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
                "search_engine\tsearch_engine_score\treliability\tnum_peptides\tnum_peptides_distinct\t" +
                "num_peptides_unambiguous\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage";

        // check stable columns
        PRHLineParser headerParser = new PRHLineParser(metadata);
        headerParser.check(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        PRTLineParser dataParser = new PRTLineParser(factory, metadata);

        String data = "PRT\tIPI00004942\tPRIDE_1234\tName4\t9606\tHomo sapiens\tIPI\t3.11\t[MS,MS:1001207,Mascot,]" +
                "\t[MS,MS:1001171,Mascot:score,30]\tnull\t1\t1\tnull\tnull\tnull\tnull" +
                "\tGO:0005488,GO:0005515,GO:0008270,GO:0043167,GO:0043169,GO:0046872,GO:0046914\t0.3";
        dataParser.check(1, data);


        // check abundance columns
        header += "\tprotein_abundance_sub[1]\tprotein_abundance_stdev_sub[1]\tprotein_abundance_std_error_sub[1]" +
                "\tprotein_abundance_sub[2]\tprotein_abundance_stdev_sub[2]\tprotein_abundance_std_error_sub[2]";
        headerParser.check(1, header);
        factory = headerParser.getFactory();
        dataParser = new PRTLineParser(factory, metadata);
        data = "PRT\tIPI00004943\tPRIDE_1234\tName4\t9606\tHomo sapiens\tIPI\t3.11\t[MS,MS:1001207,Mascot,]" +
               "\t[MS,MS:1001171,Mascot:score,30]\tnull\t1\t1\tnull\tnull\tnull\tnull" +
               "\tGO:0005488,GO:0005515,GO:0008270,GO:0043167,GO:0043169,GO:0046872,GO:0046914\t0.3" +
               "\t1.0\tnull\tnull\t5.6789\t1.2\tnull";
        dataParser.check(2, data);
    }

    @Test
    public void testLoadProteinData() throws Exception {
        String header = "PRH\taccession\tunit_id\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
                "search_engine\tsearch_engine_score\treliability\tnum_peptides\tnum_peptides_distinct\t" +
                "num_peptides_unambiguous\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage" +
                "\tprotein_abundance_sub[1]\tprotein_abundance_stdev_sub[1]\tprotein_abundance_std_error_sub[1]" +
                "\tprotein_abundance_sub[2]\tprotein_abundance_stdev_sub[2]\tprotein_abundance_std_error_sub[2]";

        // check stable columns
        PRHLineParser headerParser = new PRHLineParser(metadata);
        headerParser.check(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        PRTLineParser dataParser = new PRTLineParser(factory, metadata);

        String data = "PRT\tIPI00004943\tPRIDE_1234\tName4\t9606\tHomo sapiens\tIPI\t3.11\t[MS,MS:1001207,Mascot,]" +
                    "\t[MS,MS:1001171,Mascot:score,30]\tnull\t1\t1\tnull\tnull\tnull\tnull" +
                    "\tGO:0005488,GO:0005515,GO:0008270,GO:0043167,GO:0043169,GO:0046872,GO:0046914\t0.3" +
                    "\t1.0\tnull\tnull\t5.6789\t1.2\tnull";
        Protein record = dataParser.getRecord(data);

        System.out.println(header);
        System.out.println(record);
    }

    @Test
    public void testCheckPeptideData() throws Exception {
        String header = "PEH\tsequence\taccession\tunit_id\tunique\tdatabase\tdatabase_version\tsearch_engine" +
                "\tsearch_engine_score\treliability\tmodifications\tretention_time\tcharge\tmass_to_charge\turi\tspectra_ref" +
                "\tpeptide_abundance_sub[1]\tpeptide_abundance_stdev_sub[1]\tpeptide_abundance_std_error_sub[1]" +
                "\tpeptide_abundance_sub[2]\tpeptide_abundance_stdev_sub[2]\tpeptide_abundance_std_error_sub[2]";

        // check stable columns
        PEHLineParser headerParser = new PEHLineParser(metadata);
        headerParser.check(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        PEPLineParser dataParser = new PEPLineParser(factory, metadata);

        String data = "PEP\tIQLVEEELDR\tIPI00218319\tPRIDE_1234\tfalse\tIPI\t3,11\t[MS,MS:1001207,Mascot,]" +
                "\t[MS,MS:1001171,Mascot:score,30]\tnull\tnull\tnull\tnull\tnull\tnull\tnull" +
                "\t1.0\tnull\tnull\t1.2345\t0.123\tnull";
        dataParser.check(1, data);
    }

    @Test
    public void testLoadPeptideData() throws Exception {
        String header = "PEH\tsequence\taccession\tunit_id\tunique\tdatabase\tdatabase_version\tsearch_engine" +
                "\tsearch_engine_score\treliability\tmodifications\tretention_time\tcharge\tmass_to_charge\turi\tspectra_ref" +
                "\tpeptide_abundance_sub[1]\tpeptide_abundance_stdev_sub[1]\tpeptide_abundance_std_error_sub[1]" +
                "\tpeptide_abundance_sub[2]\tpeptide_abundance_stdev_sub[2]\tpeptide_abundance_std_error_sub[2]";

        // check stable columns
        PEHLineParser headerParser = new PEHLineParser(metadata);
        headerParser.check(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        PEPLineParser dataParser = new PEPLineParser(factory, metadata);

        String data = "PEP\tIQLVEEELDR\tIPI00218319\tPRIDE_1234\tfalse\tIPI\t3,11\t[MS,MS:1001207,Mascot,]" +
                "\t[MS,MS:1001171,Mascot:score,30]\tnull\tnull\tnull\tnull\tnull\tnull\tnull" +
                "\t1.0\tnull\tnull\t1.2345\t0.123\tnull";
        Peptide record = dataParser.getRecord(data);

        System.out.println(header);
        System.out.println(record);
    }

    @Test
    public void testCheckSmallMoleculeData() throws Exception {
        String header = "SMH\tidentifier\tunit_id\tchemical_formula\tsmiles\tinchi_key\tdescription\tmass_to_charge" +
                "\tcharge\tretention_time\ttaxid\tspecies\tdatabase\tdatabase_version\treliability\turi\tspectra_ref" +
                "\tsearch_engine\tsearch_engine_score\tmodifications" +
                "\tsmallmolecule_abundance_sub[1]\tsmallmolecule_abundance_stdev_sub[1]\tsmallmolecule_abundance_std_error_sub[1]" +
                "\tsmallmolecule_abundance_sub[2]\tsmallmolecule_abundance_stdev_sub[2]\tsmallmolecule_abundance_std_error_sub[2]";

        // check stable columns
        SMHLineParser headerParser = new SMHLineParser(metadata);
        headerParser.check(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        SMLLineParser dataParser = new SMLLineParser(factory, metadata);

        String data = "SML\tID_1\tPRIDE_1234\tnull\tnull\tnull\tnull\t254.4\t2\t20.7\t9606\tHomo sapiens (Human)\tnull" +
                "\tnull\t2\tnull\tnull\t[,,SpectraSt,]\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull";
        dataParser.check(1, data);
    }

    @Test
    public void testLoadSmallMoleculeData() throws Exception {
        String header = "SMH\tidentifier\tunit_id\tchemical_formula\tsmiles\tinchi_key\tdescription\tmass_to_charge" +
                "\tcharge\tretention_time\ttaxid\tspecies\tdatabase\tdatabase_version\treliability\turi\tspectra_ref" +
                "\tsearch_engine\tsearch_engine_score\tmodifications" +
                "\tsmallmolecule_abundance_sub[1]\tsmallmolecule_abundance_stdev_sub[1]\tsmallmolecule_abundance_std_error_sub[1]" +
                "\tsmallmolecule_abundance_sub[2]\tsmallmolecule_abundance_stdev_sub[2]\tsmallmolecule_abundance_std_error_sub[2]";

        // check stable columns
        SMHLineParser headerParser = new SMHLineParser(metadata);
        headerParser.check(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        SMLLineParser dataParser = new SMLLineParser(factory, metadata);

        String data = "SML\tID_1\tPRIDE_1234\tnull\tnull\tnull\tnull\t254.4\t2\t20.7\t9606\tHomo sapiens (Human)\tnull" +
                "\tnull\t2\tnull\tnull\t[,,SpectraSt,]\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull";

        SmallMolecule record = dataParser.getRecord(data);

        System.out.println(header);
        System.out.println(record);
    }
}
