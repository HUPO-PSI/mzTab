package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Set;

import static org.junit.Assert.*;


/**
* @author qingwei
* @since 18/02/13
*/
public class MZTabHeaderLineParserTest {
    private static Logger logger = LoggerFactory.getLogger(MZTabHeaderLineParserTest.class);

    private Metadata metadata;
    private MZTabErrorList errorList;

    @Before
    public void setUp() throws Exception {
        MTDLineParserTest test = new MTDLineParserTest();
        String fileName = "testset/mtdFile.txt";

        URL uri = MZTabHeaderLineParserTest.class.getClassLoader().getResource(fileName);
        if(uri!=null) {
            metadata = test.parseMetadata(uri.getFile());
        } else {
            throw new FileNotFoundException(fileName);
        }
        errorList = new MZTabErrorList();
    }

    @Test
    public void testProteinHeader() throws Exception {
        MZTabColumn column;
        PRHLineParser parser = new PRHLineParser(metadata);

        // check stable columns with stable order.
        // check best_search_engine_score[1]
        String headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
                "search_engine\tbest_search_engine_score[1]\tambiguity_members\tmodifications\t" +
                "protein_coverage";
        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());
        //protein coverage is not stable. It is only mandatory for Identification Complete
        assertEquals(2, parser.getFactory().getOptionalColumnMapping().size());
//        assertEquals(headerLine.split("\t").length - parser.getFactory().getOptionalColumnMapping().size() - 1, parser.getFactory().getStableColumnMapping().size());

        parser = new PRHLineParser(metadata);
        // check reliability, go_terms and uri, optional columns have stable order.
        headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
            "search_engine\tbest_search_engine_score[1]\treliability\tambiguity_members\tmodifications\t" +
            "uri\tgo_terms\tprotein_coverage";
        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());
        assertEquals(5, parser.getFactory().getOptionalColumnMapping().size());

        parser = new PRHLineParser(metadata);
        // check flexible optional columns with stable order.
        headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\tsearch_engine\t" +
            "best_search_engine_score[1]\t" +
            "search_engine_score[1]_ms_run[1]\t" +
            "search_engine_score[1]_ms_run[2]\t" +
            "reliability\t" +
            "num_psms_ms_run[1]\t" +
            "num_psms_ms_run[2]\t" +
            "num_peptides_distinct_ms_run[1]\t" +
            "num_peptides_distinct_ms_run[2]\t" +
            "num_peptides_unique_ms_run[1]\t" +
            "num_peptides_unique_ms_run[2]\t" +
            "ambiguity_members\t" +
            "modifications\t" +
            "uri\t" +
            "go_terms\t" +
            "protein_coverage";
        parser.parse(1, headerLine, errorList);
        assertEquals(13, parser.getFactory().getOptionalColumnMapping().size());
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());

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
        assertEquals(headerLine.split("\t").length - 1 , parser.getFactory().getColumnMapping().size());
//        assertEquals(headerLine.split("\t").length - 1 , parser.getFactory().getStableColumnMapping().size());

        headerLine += "\tbest_search_engine_score[1]";
        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1 , parser.getFactory().getColumnMapping().size());
        count = 1;
        assertEquals(count, parser.getFactory().getOptionalColumnMapping().size());

        // check reliability and uri optional columns
        headerLine += "\treliability\turi";
        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());
        count += 2;
        assertEquals(count, parser.getFactory().getOptionalColumnMapping().size());

        // test search_engine_score[1]_ms_run[1]	search_engine_score[1]_ms_run[2]
        headerLine += "\tsearch_engine_score[1]_ms_run[1]\tsearch_engine_score[1]_ms_run[2]";
        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());
        count += 2;
        assertEquals(count, parser.getFactory().getOptionalColumnMapping().size());
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

        // check columns
        String headerLine = "PSH\tsequence\tPSM_ID\taccession\tunique\tdatabase\tdatabase_version\t" +
            "search_engine\t" +
            "search_engine_score[1]\t" +
            "search_engine_score[2]\t" +
            "modifications\tretention_time\tcharge\t" +
            "exp_mass_to_charge\tcalc_mass_to_charge\tspectra_ref\tpre\tpost\tstart\tend";

        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());
        count = 2;
        assertEquals(count, parser.getFactory().getOptionalColumnMapping().size());
//        assertEquals(headerLine.split("\t").length - 1 - parser.getFactory().getOptionalColumnMapping().size(), parser.getFactory().getStableColumnMapping().size());

        // check optional columns with stable order.
        headerLine += "\treliability\turi";
        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());
        count += 2;
        assertEquals(count, parser.getFactory().getOptionalColumnMapping().size());

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
            "database_version\tspectra_ref\tsearch_engine\tbest_search_engine_score[1]\tmodifications";

        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());
        count = 1;
        assertEquals(count, parser.getFactory().getOptionalColumnMapping().size());
//        assertEquals(headerLine.split("\t").length - 1 - count, parser.getFactory().getStableColumnMapping().size());

        // check optional columns with stable order.
        headerLine += "\treliability\turi";
        parser.parse(1, headerLine, errorList);
        assertEquals(headerLine.split("\t").length - 1, parser.getFactory().getColumnMapping().size());
        count += 2;
        assertEquals(count, parser.getFactory().getOptionalColumnMapping().size());

        // check flexible optional columns with stable order.
        headerLine += "\tsearch_engine_score[1]_ms_run[1]\tsearch_engine_score[1]_ms_run[2]";
        parser.parse(1, headerLine, errorList);
        column = parser.getFactory().findColumnByHeader("search_engine_score[1]_ms_run[2]");
        assertNotNull(column);
        count += 2;
        assertEquals(count, parser.getFactory().getOptionalColumnMapping().size());

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
        PRHLineParser parser1 = new PRHLineParser(metadata);

        // check stable columns with stable order.
        String headerLine = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\tsearch_engine\t" +
            "best_search_engine_score[1]\t" +
            "search_engine_score[1]_ms_run[1]\t" +
            "reliability\tnum_psms_ms_run[1]\t" +
            "num_psms_ms_run[2]\tnum_peptides_distinct_ms_run[1]\tnum_peptides_distinct_ms_run[2]\t" +
            "num_peptides_unique_ms_run[1]\tnum_peptides_unique_ms_run[2]\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage";
        factory = parser1.getFactory();
        parser1.parse(1, headerLine, errorList);
        PositionMapping positionMapping1 = new PositionMapping(factory, headerLine);
        assertEquals(positionMapping1.size(), factory.getColumnMapping().size());
        assertEquals("110001",positionMapping1.get(11));
        assertEquals("110002",positionMapping1.get(12));
        assertEquals("120001",positionMapping1.get(13));
        assertEquals("120002",positionMapping1.get(14));
        assertEquals("130001",positionMapping1.get(15));
        assertEquals("130002",positionMapping1.get(16));

        PRHLineParser parser2 = new PRHLineParser(metadata);
        // change physical position of taxid
        headerLine = "PRH\taccession\tdescription\tspecies\tdatabase\tdatabase_version\tsearch_engine\ttaxid\t" +
            "best_search_engine_score[1]\t" +
            "search_engine_score[1]_ms_run[1]\t" +
            "reliability\tnum_psms_ms_run[1]\t" +
            "num_psms_ms_run[2]\tnum_peptides_distinct_ms_run[1]\tnum_peptides_distinct_ms_run[2]\t" +
            "num_peptides_unique_ms_run[1]\tnum_peptides_unique_ms_run[2]\tambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage";
        factory = parser2.getFactory();
        parser2.parse(1, headerLine, errorList);
        PositionMapping positionMapping2 = new PositionMapping(factory, headerLine);
        assertEquals(positionMapping2.size(), factory.getColumnMapping().size());
        assertEquals("110001",positionMapping1.get(11));
        assertEquals("110002",positionMapping1.get(12));
        assertEquals("120001",positionMapping1.get(13));
        assertEquals("120002",positionMapping1.get(14));
        assertEquals("130001",positionMapping1.get(15));
        assertEquals("130002", positionMapping1.get(16));

        Set<String> mapping1LogicalPosition = positionMapping1.reverse().keySet();
        Set<String> mapping2LogicalPosition = positionMapping2.reverse().keySet();
        assertTrue(mapping1LogicalPosition.equals(mapping2LogicalPosition));
    }
}
