package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import static junit.framework.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 20/02/13
 */
public class MZTabDataLineParserTest {
    private Metadata metadata;

    @Before
    public void setUp() throws Exception {
        MTDLineParserTest test = new MTDLineParserTest();
        metadata = test.parseMetadata("testset/mtdFile.txt");
    }

    @Test
    public void testCheckProteinData() throws Exception {
        MZTabErrorList errorList = new MZTabErrorList();

        String header = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
                "search_engine\tbest_search_engine_score\tsearch_engine_score_ms_file[1]\treliability\t" +
                "num_psms_ms_file[1]\tnum_peptides_distinct_ms_file[1]\tnum_peptides_unique_ms_file[1]\t" +
                "ambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage\tprotein_abundance_assay[1]\t" +
                "protein_abundance_assay[2]\tprotein_abundance_study_variable[1]\t" +
                "protein_abundance_stdev_study_variable[1]\tprotein_abundance_std_error_study_variable[1]\t" +
                "opt_assay[1]_my_value\topt_global_cv_MS:1001208_TOM";

        // check stable columns
        PRHLineParser headerParser = new PRHLineParser(metadata);
        headerParser.parse(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        PRTLineParser dataParser = new PRTLineParser(factory, metadata, errorList);

        String data = "PRT\tP12345\tAspartate aminotransferase, mitochondrial\t10116\tnull\tnull\tnull\t" +
                "[MS, MS:1001207, Mascot, ]|[MS, MS:1001208, Sequest, ]\tnull\tnull\t1\tnull\tnull\tnull\t" +
                "null\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull";
        dataParser.parse(1, data);
    }

    @Test
    public void testLoadProteinData() throws Exception {
        MZTabErrorList errorList = new MZTabErrorList();

        String header = "PRH\taccession\tdescription\ttaxid\tspecies\tdatabase\tdatabase_version\t" +
                "search_engine\tbest_search_engine_score\tsearch_engine_score_ms_file[1]\treliability\t" +
                "num_psms_ms_file[1]\tnum_peptides_distinct_ms_file[1]\tnum_peptides_unique_ms_file[1]\t" +
                "ambiguity_members\tmodifications\turi\tgo_terms\tprotein_coverage\tprotein_abundance_assay[1]\t" +
                "protein_abundance_assay[2]\tprotein_abundance_study_variable[1]\t" +
                "protein_abundance_stdev_study_variable[1]\tprotein_abundance_std_error_study_variable[1]\t" +
                "opt_assay[1]_my_value\topt_global_cv_MS:1001208_TOM";

        // check stable columns
        PRHLineParser headerParser = new PRHLineParser(metadata);
        headerParser.parse(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        PRTLineParser dataParser = new PRTLineParser(factory, metadata, errorList);

        String data;
        Protein record;
        String[] items;

        // check stable columns' data.
        data = "PRT\tP12345\tAspartate aminotransferase, mitochondrial\t10116\tnull\tnull\tnull\t" +
                "[MS, MS:1001207, Mascot, ]|[MS, MS:1001208, Sequest, ]\tnull\tnull\t1\tnull\tnull\tnull\t" +
                "null\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull";
        record = dataParser.getRecord(data);
        items = data.split("\t");

        assertTrue(record.getAccession().equals(items[1]));
        assertTrue(record.getDescription().equals(items[2]));
        assertTrue(record.getTaxid().equals(new Integer(items[3])));
        assertTrue(record.getSearchEngine().equals(MZTabUtils.parseParamList(items[7])));
        assertTrue(record.getReliability().equals(Reliability.findReliability(items[10])));

        // check optional columns which have stable order
        data = "PRT\tP12345\tAspartate aminotransferase, mitochondrial\t10116\tnull\tnull\tnull\t" +
                "[MS, MS:1001207, Mascot, ]|[MS, MS:1001208, Sequest, ]\tnull\t" +
                "[MS, MS:1001171, Mascot score, 50]|[MS, MS:1001155, Sequest:xcorr, 2]\t1\t4\t3\t2\tnull\tnull\t" +
                "null\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull";
        record = dataParser.getRecord(data);
        items = data.split("\t");
        assertTrue(record.getSearchEngineScore(metadata.getMsRunMap().get(1)).equals(MZTabUtils.parseParamList(items[9])));
        assertTrue(record.getNumPSMs(metadata.getMsRunMap().get(1)).equals(new Integer(items[11])));
        assertTrue(record.getNumPeptidesDistinct(metadata.getMsRunMap().get(1)).equals(new Integer(items[12])));
        assertTrue(record.getNumPeptidesUnique(metadata.getMsRunMap().get(1)).equals(new Integer(items[13])));

        // check abundance optional columns
        data = "PRT\tP12345\tAspartate aminotransferase, mitochondrial\t10116\tnull\tnull\tnull\t" +
                "[MS, MS:1001207, Mascot, ]|[MS, MS:1001208, Sequest, ]\tnull\t" +
                "[MS, MS:1001171, Mascot score, 50]|[MS, MS:1001155, Sequest:xcorr, 2]\t1\t4\t3\t2\tnull\tnull\t" +
                "null\tnull\tnull\t0.4\t0.2\t0.4\t0.3\t0.2\tnull\tnull";
        record = dataParser.getRecord(data);
        items = data.split("\t");
        // protein_abundance_assay[1]
        assertTrue(record.getAbundanceColumn(metadata.getAssayMap().get(1)).toString().equals(items[19]));
        // protein_abundance_assay[2]
        assertTrue(record.getAbundanceColumn(metadata.getAssayMap().get(2)).toString().equals(items[20]));
        // protein_abundance_study_variable[1]
        assertTrue(record.getAbundanceColumn(metadata.getStudyVariableMap().get(1)).toString().equals(items[21]));
        // protein_abundance_stdev_study_variable[1]
        assertTrue(record.getAbundanceStdevColumn(metadata.getStudyVariableMap().get(1)).toString().equals(items[22]));
        // protein_abundance_std_error_study_variable[1]
        assertTrue(record.getAbundanceStdErrorColumn(metadata.getStudyVariableMap().get(1)).toString().equals(items[23]));

        // check user defined optional columns
        data = "PRT\tP12345\tAspartate aminotransferase, mitochondrial\t10116\tnull\tnull\tnull\t" +
                "[MS, MS:1001207, Mascot, ]|[MS, MS:1001208, Sequest, ]\tnull\t" +
                "[MS, MS:1001171, Mascot score, 50]|[MS, MS:1001155, Sequest:xcorr, 2]\t1\t4\t3\t2\tnull\tnull\t" +
                "null\tnull\tnull\t0.4\t0.2\t0.4\t0.3\t0.2\tMy value about assay[1]\tTOM value";
        record = dataParser.getRecord(data);
        items = data.split("\t");
        // opt_assay[1]_my_value
        assertTrue(record.getOptionColumn(metadata.getAssayMap().get(1), "my value").equals(items[24]));
        // opt_global_cv_MS:1001208_TOM
        CVParam param = new CVParam("MS", "MS:1001208", "TOM", null);
        assertTrue(record.getOptionColumn(param).equals(items[25]));
    }

    @Test
    public void testLoadPSMData() throws Exception {
        MZTabErrorList errorList = new MZTabErrorList();

        String header = "PSH\tsequence\tPSM_ID\taccession\tunique\tdatabase\tdatabase_version\t" +
                "search_engine\tsearch_engine_score\treliability\tmodifications\tretention_time\t" +
                "charge\texp_mass_to_charge\tcalc_mass_to_charge\turi\tspectra_ref\tpre\tpost\tstart\tend\t" +
                "opt_global_cv_PRIDE:0000446_PRIDE_peptide_score\topt_global_cv_PRIDE:0000445_PRIDE-Cluster_score\t" +
                "opt_global_cv_PRIDE:0000444_PRIDE_experiment_accession\topt_global_cv_MS:1000879_PubMed_identifier\t" +
                "opt_global_cv_MS:1001919_ProteomeXchange_accession_number";

        // check stable columns
        PSHLineParser headerParser = new PSHLineParser(metadata);
        headerParser.parse(1, header);
        MZTabColumnFactory factory = headerParser.getFactory();
        PSMLineParser dataParser = new PSMLineParser(factory, metadata, errorList);

        String data;
        String[] items;
        PSM psm;

        data = "PSM\tTDTVLILCR\tnull\tnull\tnull\tnull\tnull\t" +
                "[MS, MS:1001207, Mascot, ]\t[MS, MS:1001171, Mascot:score, 62.93]\t3\t8-MOD:00397\tnull\t" +
                "2\t545.79\tnull\tnull\tnull\tnull\tnull\tnull\tnull\t74.15\t1\t10018\tpubmed:20432482\tnull";
        psm = dataParser.getRecord(data);
        items = data.split("\t");
        assertTrue(psm.getSequence().equals(items[1]));
        assertTrue(psm.getSearchEngine().get(0).getAccession().equals("MS:1001207"));
        assertTrue(psm.getSearchEngineScore().get(0).getAccession().equals("MS:1001171"));
        assertTrue(psm.getModifications().get(0).getType() == Modification.Type.MOD);
        assertTrue(psm.getModifications().get(0).getAccession().equals("00397"));
        assertTrue(psm.getCharge().equals(new Integer(items[12])));
        assertTrue(psm.getExpMassToCharge().toString().equals(items[13]));

        // opt_global_cv_MS:1000879_PubMed_identifier
        CVParam param = new CVParam("MS", "MS:1000879", "PubMed identifier", null);
        assertTrue(psm.getOptionColumn(param).equals("pubmed:20432482"));
    }

//
//    @Test
//    public void testCheckSmallMoleculeData() throws Exception {
//        MZTabErrorList errorList = new MZTabErrorList();
//
//        String header = "SMH\tidentifier\tunit_id\tchemical_formula\tsmiles\tinchi_key\tdescription\tmass_to_charge" +
//                "\tcharge\tretention_time\ttaxid\tspecies\tdatabase\tdatabase_version\treliability\turi\tspectra_ref" +
//                "\tsearch_engine\tsearch_engine_score\tmodifications" +
//                "\tsmallmolecule_abundance_sub[1]\tsmallmolecule_abundance_stdev_sub[1]\tsmallmolecule_abundance_std_error_sub[1]" +
//                "\tsmallmolecule_abundance_sub[2]\tsmallmolecule_abundance_stdev_sub[2]\tsmallmolecule_abundance_std_error_sub[2]";
//
//        // check stable columns
//        SMHLineParser headerParser = new SMHLineParser(metadata);
//        headerParser.check(1, header);
//        MZTabColumnFactory factory = headerParser.getFactory();
//        SMLLineParser dataParser = new SMLLineParser(factory, metadata, errorList);
//
//        String data = "SML\tID_1\tPRIDE_1234\tnull\tnull\tnull\tnull\t254.4\t2\t20.7\t9606\tHomo sapiens (Human)\tnull" +
//                "\tnull\t2\tnull\tnull\t[,,SpectraSt,]\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull";
//        dataParser.check(1, data);
//    }
//
//    @Test
//    public void testLoadSmallMoleculeData() throws Exception {
//        MZTabErrorList errorList = new MZTabErrorList();
//
//        String header = "SMH\tidentifier\tunit_id\tchemical_formula\tsmiles\tinchi_key\tdescription\tmass_to_charge" +
//                "\tcharge\tretention_time\ttaxid\tspecies\tdatabase\tdatabase_version\treliability\turi\tspectra_ref" +
//                "\tsearch_engine\tsearch_engine_score\tmodifications" +
//                "\tsmallmolecule_abundance_sub[1]\tsmallmolecule_abundance_stdev_sub[1]\tsmallmolecule_abundance_std_error_sub[1]" +
//                "\tsmallmolecule_abundance_sub[2]\tsmallmolecule_abundance_stdev_sub[2]\tsmallmolecule_abundance_std_error_sub[2]";
//
//        // check stable columns
//        SMHLineParser headerParser = new SMHLineParser(metadata);
//        headerParser.check(1, header);
//        MZTabColumnFactory factory = headerParser.getFactory();
//        SMLLineParser dataParser = new SMLLineParser(factory, metadata, errorList);
//
//        String data = "SML\tID_1\tPRIDE_1234\tnull\tnull\tnull\tnull\t254.4\t2\t20.7\t9606\tHomo sapiens (Human)\tnull" +
//                "\tnull\t2\tnull\tnull\t[,,SpectraSt,]\tnull\tnull\tnull\tnull\tnull\tnull\tnull\tnull";
//
//        SmallMolecule record = dataParser.getRecord(data);
//
//        System.out.println(header);
//        System.out.println(record);
//    }
}
