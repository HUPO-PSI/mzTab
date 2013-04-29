package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.Metadata;

/**
 * User: Qingwei
 * Date: 18/02/13
 */
public class MTDHeaderLineParserTest {
    private Metadata metadata;

    @Before
    public void setUp() throws Exception {
        MTDLineParserTest test = new MTDLineParserTest();
        metadata = test.parseMetadata("testset/mtdFile.txt");
    }

    @Test
    public void testProteinHeader() throws Exception {
        String header = "PRH\taccession\tunit_id\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
                "search_engine\tsearch_engine_score\treliability\tnum_peptides\tnum_peptides_distinct\t" +
                "num_peptides_unambiguous\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage";

        PRHLineParser parser = new PRHLineParser(metadata);

        // check stable columns
        parser.check(1, header);

        // check abundance columns
        header += "\tprotein_abundance_sub[1]\tprotein_abundance_stdev_sub[1]\tprotein_abundance_std_error_sub[1]" +
                "\tprotein_abundance_sub[2]\tprotein_abundance_stdev_sub[2]\tprotein_abundance_std_error_sub[2]";
        parser.check(1, header);

        // check Optional Column
        header += "\topt_my_value";
        parser.check(1, header);

        // check Optional CVParam Column
        header += "\topt_cv_MS:1001905_emPAI-value";
        parser.check(1, header);

//        System.out.println(parser.getFactory().toString());
    }

    @Test
    public void testPeptideHeader() throws Exception {
        String header = "PEH\tsequence\taccession\tunit_id\tunique\tdatabase\tdatabase_version\tsearch_engine\t" +
                "search_engine_score\treliability\tmodifications\tretention_time\tcharge\tmass_to_charge\turi\tspectra_ref";

        PEHLineParser parser = new PEHLineParser(metadata);

        // check stable columns
        parser.check(1, header);

        // check abundance columns
        header += "\tpeptide_abundance_sub[1]\tpeptide_abundance_stdev_sub[1]\tpeptide_abundance_std_error_sub[1]" +
                "\tpeptide_abundance_sub[2]\tpeptide_abundance_stdev_sub[2]\tpeptide_abundance_std_error_sub[2]";
        parser.check(1, header);

        // check Optional Column
        header += "\topt_my_value";
        parser.check(1, header);

        // check Optional CVParam Column
        header += "\topt_cv_MS:1001905_emPAI-value";
        parser.check(1, header);

//        System.out.println(parser.getFactory().toString());
    }

    @Test
    public void testSmallMoleculeHeader() throws Exception {
        String header = "SMH\tidentifier\tunit_id\tchemical_formula\tsmiles\tinchi_key\tdescription\tmass_to_charge" +
                "\tcharge\tretention_time\ttaxid\tspecies\tdatabase\tdatabase_version\treliability\turi\tspectra_ref" +
                "\tsearch_engine\tsearch_engine_score\tmodifications";

        SMHLineParser parser = new SMHLineParser(metadata);

        // check stable columns
        parser.check(1, header);

        // check abundance columns
        header += "\tsmallmolecule_abundance_sub[1]\tsmallmolecule_abundance_stdev_sub[1]\tsmallmolecule_abundance_std_error_sub[1]" +
                  "\tsmallmolecule_abundance_sub[2]\tsmallmolecule_abundance_stdev_sub[2]\tsmallmolecule_abundance_std_error_sub[2]";
        parser.check(1, header);

        // check Optional Column
        header += "\topt_my_value";
        parser.check(1, header);

        // check Optional CVParam Column
        header += "\topt_cv_MS:1001905_emPAI-value";
        parser.check(1, header);

//        System.out.println(parser.getFactory().toString());
    }
}
