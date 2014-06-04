package uk.ac.ebi.pride.jmztab.utils.parser;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
* User: Qingwei
* Date: 18/02/13
*/
public class MZTabHeaderLineParserTest {
    private static Logger logger = Logger.getLogger(MZTabHeaderLineParserTest.class);

    private Metadata metadata;
    private MZTabErrorList errorList;

    @Before
    public void setUp() throws Exception {
        MTDLineParserTest test = new MTDLineParserTest();
        metadata = test.parseMetadata("testset/mtdFile.txt");
        errorList = new MZTabErrorList();
    }

    @Test
    public void testProteinHeader() throws Exception {
        MZTabColumn column;
        PRHLineParser parser = new PRHLineParser(metadata);

        // check stable columns with stable order.
        String headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
            "search_engine\tambiguity_members\tmodifications\tprotein_coverage";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getStableColumnMapping().size());

        // check best_search_engine_score[1] and best_search_engine_score[2]
        headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
                "search_engine\tbest_search_engine_score[1]\tbest_search_engine_score[2]\tambiguity_members\tmodifications\t" +
                "protein_coverage";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == 2);


        // check reliability, go_terms and uri, optional columns have stable order.
        headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
            "search_engine\tbest_search_engine_score[1]\tbest_search_engine_score[2]\treliability\tambiguity_members\tmodifications\t" +
            "uri\tgo_terms\tprotein_coverage";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == 5);

        // check flexible optional columns with stable order.
        headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\tsearch_engine\t" +
            "best_search_engine_score[1]\tbest_search_engine_score[2]\tsearch_engine_score[1]_ms_run[1]\tsearch_engine_score[2]_ms_run[1]\t" +
            "reliability\tnum_psms_ms_run[1]\tnum_psms_ms_run[2]\tnum_peptides_distinct_ms_run[1]\tnum_peptides_distinct_ms_run[2]\t" +
            "num_peptides_unique_ms_run[1]\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage";
        parser.parse(1, headerLine, errorList);
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == 12);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());

        // check Abundance Columns
        headerLine += "\tprotein_abundance_assay[1]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("protein_abundance_assay[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tprotein_abundance_study_variable[1]\tprotein_abundance_stdev_study_variable[1]\tprotein_abundance_std_error_study_variable[1]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("protein_abundance_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumnByHeader("protein_abundance_stdev_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumnByHeader("protein_abundance_std_error_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tprotein_abundance_assay[2]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("protein_abundance_assay[2]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        // check Optional Column which start with opt_
        headerLine += "\topt_ms_run[1]_my_value";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_ms_run[1]_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        headerLine += "\topt_global_my_value";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_global_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        // check Optional CVParam Column
        headerLine += "\topt_assay[1]_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_assay[1]_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        headerLine += "\topt_global_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_global_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        logger.debug(parser.getFactory().toString());
    }

    @Test
    public void testPeptideHeader() throws Exception {
        PEHLineParser parser = new PEHLineParser(metadata);
        MZTabColumn column;
        int count;

        // check stable columns
        String headerLine = "PEH\tsequence\taccession\tunique\tdatabase\tdatabase_version\tsearch_engine\t" +
            "modifications\tretention_time\tretention_time_window\tcharge\tmass_to_charge\tspectra_ref";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getStableColumnMapping().size());

        headerLine += "\tbest_search_engine_score[1]\tbest_search_engine_score[2]";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        count = 2;
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == count);

        // check reliability and uri optional columns
        headerLine += "\treliability\turi";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        count += 2;
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == count);

        // test search_engine_score[1]_ms_run[1]	search_engine_score[2]_ms_run[1]
        headerLine += "\tsearch_engine_score[1]_ms_run[1]\tsearch_engine_score[2]_ms_run[1]";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        count += 2;
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == count);
        column = parser.getFactory().findColumnByHeader("search_engine_score[1]_ms_run[1]");
        assertNotNull(column);

        // check Abundance Columns
        headerLine += "\tpeptide_abundance_assay[1]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("peptide_abundance_assay[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tpeptide_abundance_study_variable[1]\tpeptide_abundance_stdev_study_variable[1]\tpeptide_abundance_std_error_study_variable[1]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("peptide_abundance_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumnByHeader("peptide_abundance_stdev_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumnByHeader("peptide_abundance_std_error_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tpeptide_abundance_assay[2]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("peptide_abundance_assay[2]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        // check Optional Column which start with opt_
        headerLine += "\topt_ms_run[1]_my_value";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_ms_run[1]_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        headerLine += "\topt_global_my_value";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_global_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        // check Optional CVParam Column
        headerLine += "\topt_assay[1]_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_assay[1]_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        headerLine += "\topt_global_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_global_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        logger.debug(parser.getFactory().toString());
    }

    @Test
    public void testPSMHeader() throws Exception {
        PSHLineParser parser = new PSHLineParser(metadata);
        MZTabColumn column;
        int count;

        // check stable columns
        String headerLine = "PSH\tsequence\tPSM_ID\taccession\tunique\tdatabase\tdatabase_version\t" +
            "search_engine\tmodifications\tretention_time\tcharge\t" +
            "exp_mass_to_charge\tcalc_mass_to_charge\tspectra_ref\tpre\tpost\tstart\tend";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getStableColumnMapping().size());

        headerLine += "\tsearch_engine_score[1]\tsearch_engine_score[2]";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        count = 2;
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == count);

        // check optional columns with stable order.
        headerLine += "\treliability\turi";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        count += 2;
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == count);

        // check Optional Column which start with opt_
        headerLine += "\topt_ms_run[1]_my_value";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_ms_run[1]_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        headerLine += "\topt_global_my_value";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_global_my_value");
        assertNotNull(column);
        assertTrue(column instanceof OptionColumn);

        // check Optional CVParam Column
        headerLine += "\topt_assay[1]_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_assay[1]_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        headerLine += "\topt_global_cv_MS:1002217_decoy_peptide";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("opt_global_cv_MS:1002217_decoy_peptide");
        assertNotNull(column);
        assertTrue(column instanceof CVParamOptionColumn);

        logger.debug(parser.getFactory().toString());
    }

    @Test
    public void testSmallMoleculeHeader() throws Exception {
        SMHLineParser parser = new SMHLineParser(metadata);
        MZTabColumn column;
        int count;

        // check stable columns
        String headerLine = "SMH\tidentifier\tchemical_formula\tsmiles\tinchi_key\tdescription\t" +
            "exp_mass_to_charge\tcalc_mass_to_charge\tcharge\tretention_time\ttaxid\tspecies\tdatabase\t" +
            "database_version\tspectra_ref\tsearch_engine\tmodifications";

        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getStableColumnMapping().size());

        headerLine += "\tbest_search_engine_score[1]\tbest_search_engine_score[2]";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        count = 2;
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == count);

        // check optional columns with stable order.
        headerLine += "\treliability\turi";
        parser.parse(1, headerLine, errorList);
        assertTrue(headerLine.split("\t").length - 1 == parser.getFactory().getColumnMapping().size());
        count += 2;
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == count);

        // check flexible optional columns with stable order.
        headerLine += "\tsearch_engine_score[1]_ms_run[1]\tsearch_engine_score[1]_ms_run[2]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("search_engine_score[1]_ms_run[2]");
        assertNotNull(column);
        count += 2;
        assertTrue(parser.getFactory().getOptionalColumnMapping().size() == count);

        // check Abundance Columns
        headerLine += "\tsmallmolecule_abundance_assay[1]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("smallmolecule_abundance_assay[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tsmallmolecule_abundance_study_variable[1]\tsmallmolecule_abundance_stdev_study_variable[1]\tsmallmolecule_abundance_std_error_study_variable[1]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("smallmolecule_abundance_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumnByHeader("smallmolecule_abundance_stdev_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);
        column = parser.getFactory().findColumnByHeader("smallmolecule_abundance_std_error_study_variable[1]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        headerLine += "\tsmallmolecule_abundance_assay[2]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("smallmolecule_abundance_assay[2]");
        assertNotNull(column);
        assertTrue(column instanceof AbundanceColumn);

        logger.debug(parser.getFactory().toString());
    }

    @Test
    public void testPositionMapping() throws Exception {
        MZTabColumnFactory factory;
        PRHLineParser parser = new PRHLineParser(metadata);

        // check stable columns with stable order.
        String headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\tsearch_engine\t" +
            "best_search_engine_score\tsearch_engine_score_ms_run[1]\treliability\tnum_psms_ms_run[1]\t" +
            "num_psms_ms_run[2]\tnum_peptides_distinct_ms_run[1]\tnum_peptides_distinct_ms_run[2]\t" +
            "num_peptides_unique_ms_run[1]\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage";
        factory = parser.getFactory();
        parser.parse(1, headerLine, errorList);
        PositionMapping positionMapping1 = new PositionMapping(factory, headerLine);
        assertTrue(positionMapping1.size() == factory.getColumnMapping().size());

        // change physical position of taxid
        headerLine = "PRH\taccession\tdescription\tspecies\tdatabase\tdatabase_version\tsearch_engine\ttaxid\t" +
            "best_search_engine_score\tsearch_engine_score_ms_run[1]\treliability\tnum_psms_ms_run[1]\t" +
            "num_psms_ms_run[2]\tnum_peptides_distinct_ms_run[1]\tnum_peptides_distinct_ms_run[2]\t" +
            "num_peptides_unique_ms_run[1]\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage";
        factory = parser.getFactory();
        parser.parse(1, headerLine, errorList);
        PositionMapping positionMapping2 = new PositionMapping(factory, headerLine);
        assertTrue(positionMapping2.size() == factory.getColumnMapping().size());
        assertTrue(positionMapping2.get(7).equals(positionMapping1.get(3)));

        Set<String> mapping1LogicalPosition = positionMapping1.exchange().keySet();
        Set<String> mapping2LogicalPosition = positionMapping2.exchange().keySet();
        assertTrue(mapping1LogicalPosition.equals(mapping2LogicalPosition));
    }
}
