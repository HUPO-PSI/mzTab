package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;

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
        MZTabColumn column;

        String headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
                "search_engine\tbest_search_engine_score\treliability\tambiguity_members\tmodifications\t" +
                "uri\tgo_terms\tprotein_coverage";

        PRHLineParser parser = new PRHLineParser(metadata);

        // check stable columns with stable order.
        parser.parse(1, headerLine);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getStableColumnMapping().size());

        // check optional columns with stable order.
        headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\tsearch_engine\t" +
                "best_search_engine_score\tsearch_engine_score_ms_file[1]\treliability\tnum_psms_ms_file[1]\t" +
                "num_psms_ms_file[2]\tnum_peptides_distinct_ms_file[1]\tnum_peptides_distinct_ms_file[2]\t" +
                "num_peptides_unique_ms_file[1]\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage";
        parser.parse(1, headerLine);
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == 6);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());

        // check Abundance Columns
        headerLine += "\tprotein_abundance_assay[1]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("protein_abundance_assay[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tprotein_abundance_study_variable[1]\tprotein_abundance_stdev_study_variable[1]\tprotein_abundance_std_error_study_variable[1]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("protein_abundance_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumn("protein_abundance_stdev_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumn("protein_abundance_std_error_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tprotein_abundance_assay[2]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("protein_abundance_assay[2]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        // check Optional Column which start with opt_
        headerLine += "\topt_ms_file[1]_my_value";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_ms_file[1]_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        headerLine += "\topt_global_my_value";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_global_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        // check Optional CVParam Column
        headerLine += "\topt_assay[1]_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_assay[1]_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        headerLine += "\topt_global_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_global_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

//        System.out.println(parser.getFactory().toString());
    }

    @Test
    public void testPeptideHeader() throws Exception {
        String headerLine = "PEH\tsequence\taccession\tunique\tdatabase\tdatabase_version\tsearch_engine\t" +
                "best_search_engine_score\treliability\tmodifications\tretention_time\tcharge\tmass_to_charge\t" +
                "uri\tspectra_ref";

        PEHLineParser parser = new PEHLineParser(metadata);
        MZTabColumn column;

        // check stable columns
        parser.parse(1, headerLine);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getStableColumnMapping().size());

        // check optional columns with stable order.
        headerLine = "PEH\tsequence\taccession\tunique\tdatabase\tdatabase_version\tsearch_engine\t" +
                "best_search_engine_score\tsearch_engine_score_ms_file[1]\tsearch_engine_score_ms_file[2]\t" +
                "reliability\tmodifications\tretention_time\tcharge\tmass_to_charge\turi\tspectra_ref";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("search_engine_score_ms_file[1]");
        assertNotNull(column);
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == 2);

        // check Abundance Columns
        headerLine += "\tpeptide_abundance_assay[1]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("peptide_abundance_assay[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tpeptide_abundance_study_variable[1]\tpeptide_abundance_stdev_study_variable[1]\tpeptide_abundance_std_error_study_variable[1]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("peptide_abundance_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumn("peptide_abundance_stdev_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumn("peptide_abundance_std_error_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tpeptide_abundance_assay[2]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("peptide_abundance_assay[2]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        // check Optional Column which start with opt_
        headerLine += "\topt_ms_file[1]_my_value";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_ms_file[1]_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        headerLine += "\topt_global_my_value";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_global_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        // check Optional CVParam Column
        headerLine += "\topt_assay[1]_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_assay[1]_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        headerLine += "\topt_global_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_global_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

//        System.out.println(parser.getFactory().toString());
    }

    @Test
    public void testPSMHeader() throws Exception {
        String headerLine = "PSH\tsequence\tPSM_ID\taccession\tunique\tdatabase\tdatabase_version\tsearch_engine\t" +
                "search_engine_score\treliability\tmodifications\tretention_time\tcharge\texp_mass_to_charge\t" +
                "calc_mass_to_charge\turi\tspectra_ref\tpre\tpost\tstart\tend";

        PSHLineParser parser = new PSHLineParser(metadata);
        MZTabColumn column;

        // check stable columns
        parser.parse(1, headerLine);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getStableColumnMapping().size());

        // check Optional Column which start with opt_
        headerLine += "\topt_ms_file[1]_my_value";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_ms_file[1]_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        headerLine += "\topt_global_my_value";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_global_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        // check Optional CVParam Column
        headerLine += "\topt_assay[1]_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_assay[1]_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        headerLine += "\topt_global_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("opt_global_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

//        System.out.println(parser.getFactory().toString());
    }

    @Test
    public void testSmallMoleculeHeader() throws Exception {
        String headerLine = "SMH\tidentifier\tchemical_formula\tsmiles\tinchi_key\tdescription\tmass_to_charge" +
                "\tcharge\tretention_time\ttaxid\tspecies\tdatabase\tdatabase_version\treliability\turi\tspectra_ref" +
                "\tsearch_engine\tbest_search_engine_score\tmodifications";

        SMHLineParser parser = new SMHLineParser(metadata);
        MZTabColumn column;

        // check stable columns
        parser.parse(1, headerLine);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getStableColumnMapping().size());

        // check optional columns with stable order.
        headerLine = "SMH\tidentifier\tchemical_formula\tsmiles\tinchi_key\tdescription\tmass_to_charge" +
                "\tcharge\tretention_time\ttaxid\tspecies\tdatabase\tdatabase_version\treliability\turi\tspectra_ref" +
                "\tsearch_engine\tbest_search_engine_score\tsearch_engine_score_ms_file[1]\tsearch_engine_score_ms_file[2]" +
                "\tmodifications";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("search_engine_score_ms_file[1]");
        assertNotNull(column);
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == 2);

        // check Abundance Columns
        headerLine += "\tsmallmolecule_abundance_assay[1]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("smallmolecule_abundance_assay[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tsmallmolecule_abundance_study_variable[1]\tsmallmolecule_abundance_stdev_study_variable[1]\tsmallmolecule_abundance_std_error_study_variable[1]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("smallmolecule_abundance_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumn("smallmolecule_abundance_stdev_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumn("smallmolecule_abundance_std_error_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tsmallmolecule_abundance_assay[2]";
        parser.parse(1, headerLine);
        column = parser.getFactory().findColumn("smallmolecule_abundance_assay[2]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

//        System.out.println(parser.getFactory().toString());
    }
}
