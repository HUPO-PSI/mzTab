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
    public void testLoadProteinData() throws Exception {
        MZTabErrorList errorList = new MZTabErrorList();

        String header = "PRH\t" +
            "accession\t" +
            "description\t" +
            "taxid\t" +
            "species\t" +
            "database\t" +
            "database_version\t" +
            "search_engine\t" +
            "best_search_engine_score\t" +
            "search_engine_score_ms_run[1]\t" +
            "reliability\t" +
            "num_psms_ms_run[1]\t" +
            "num_peptides_distinct_ms_run[1]\t" +
            "num_peptides_unique_ms_run[1]\t" +
            "ambiguity_members\t" +
            "modifications\t" +
            "uri\t" +
            "go_terms\t" +
            "protein_coverage\t" +
            "protein_abundance_assay[1]\t" +
            "protein_abundance_assay[2]\t" +
            "protein_abundance_study_variable[1]\t" +
            "protein_abundance_stdev_study_variable[1]\t" +
            "protein_abundance_std_error_study_variable[1]\t" +
            "opt_assay[1]_my_value\t" +
            "opt_global_cv_MS:1001208_TOM";

        // check stable columns
        PRHLineParser headerParser = new PRHLineParser(metadata);
        headerParser.parse(1, header, new MZTabErrorList());
        MZTabColumnFactory factory = headerParser.getFactory();
        PRTLineParser dataParser = new PRTLineParser(factory, new PositionMapping(factory, header), metadata, errorList);

        String data;
        Protein record;
        String[] items;

        // check stable columns' data.
        data = "PRT\t" +
            "P12345\t" +
            "Aspartate aminotransferase, mitochondrial\t" +
            "10116\t" +
            "Rattus norvegicus (Rat)\t" +
            "UniProtKB\t2011_11\t" +
            "[MS, MS:1001207, Mascot, ]|[MS, MS:1001208, Sequest, ]\t" +
            "[MS,MS:1001171,Mascot score,50]|[MS,MS:1001155,Sequest:xcorr,2]\t" +
            "[MS,MS:1001171,Mascot score,50]|[MS,MS:1001155,Sequest:xcorr,2]\t" +
            "1\t" +
            "4\t" +
            "3\t" +
            "2\t" +
            "P12347,P12348\t" +
            "3-MOD:00412,8-MOD:00412\t" +
            "http://www.ebi.ac.uk/pride/url/to/P12345\t" +
            "GO:0006457|GO:0005759|GO:0005886|GO:0004069\t" +
            "0.4\t" +
            "0.4\t" +
            "0.2\t" +
            "0.1\t" +
            "0.4\t" +
            "0.03\t" +
            "My value about assay[1]\t" +
            "some other value that is across reps";
        record = dataParser.getRecord(data);
        System.out.println(header);
        System.out.println(record.toString());
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
    public void testLoadPeptideData() throws Exception {
        MZTabErrorList errorList = new MZTabErrorList();

        String header = "PEH\t" +
            "sequence\t" +
            "accession\t" +
            "unique\t" +
            "database\t" +
            "database_version\t" +
            "search_engine\t" +
            "best_search_engine_score\t" +
            "search_engine_score_ms_run[1]\t" +
            "reliability\t" +
            "modifications\t" +
            "retention_time\t" +
            "retention_time_window\t" +
            "charge\t" +
            "mass_to_charge\t" +
            "uri\t" +
            "spectra_ref";
        PEHLineParser headerParser = new PEHLineParser(metadata);
        headerParser.parse(1, header, errorList);
        MZTabColumnFactory factory = headerParser.getFactory();
        PEPLineParser dataParser = new PEPLineParser(factory, new PositionMapping(factory, header), metadata, errorList);

        String data;
        Peptide peptide;

        data = "PEP\t" +
            "EIEILACEIR\t" +
            "P02768\t" +
            "0\t" +
            "UniProtKB\t" +
            "2011_11\t" +
            "[MS,MS:1001207,Mascot,]|[MS,MS:1001208,Sequest,]\t" +
            "[MS,MS:1001155,Sequest:xcorr,2]\t" +
            "[MS,MS:1001155,Sequest:xcorr,2]\t" +
            "3\t" +
            "8-MOD:00397\t" +
            "10.2\t" +
            "1123.2|1145.3\t" +
            "2\t" +
            "1234.4\t" +
            "http://www.ebi.ac.uk/pride/link/to/peptide\t" +
            "ms_run[1]:index=5";
        peptide = dataParser.getRecord(data);

        System.out.println(header);
        System.out.println(peptide.toString());
    }

    @Test
    public void testLoadPSMData() throws Exception {
        MZTabErrorList errorList = new MZTabErrorList();

        String header = "PSH\t" +
            "sequence\t" +
            "PSM_ID\t" +
            "accession\t" +
            "unique\t" +
            "database\t" +
            "database_version\t" +
            "search_engine\t" +
            "search_engine_score\t" +
            "reliability\t" +
            "modifications\t" +
            "retention_time\t" +
            "charge\t" +
            "exp_mass_to_charge\t" +
            "calc_mass_to_charge\t" +
            "uri\t" +
            "spectra_ref\t" +
            "pre\t" +
            "post\t" +
            "start\t" +
            "end\t" +
            "opt_global_cv_PRIDE:0000446_PRIDE_peptide_score\t" +
            "opt_global_cv_PRIDE:0000445_PRIDE-Cluster_score\t" +
            "opt_global_cv_PRIDE:0000444_PRIDE_experiment_accession\t" +
            "opt_global_cv_MS:1000879_PubMed_identifier\t" +
            "opt_global_cv_MS:1001919_ProteomeXchange_accession_number";

        // check stable columns
        PSHLineParser headerParser = new PSHLineParser(metadata);
        headerParser.parse(1, header, errorList);
        MZTabColumnFactory factory = headerParser.getFactory();
        PSMLineParser dataParser = new PSMLineParser(factory, new PositionMapping(factory, header), metadata, errorList);

        String data;
        String[] items;
        PSM psm;

        data = "PSM\t" +
            "KVPQVSTPTLVEVSR\t" +
            "1\t" +
            "P02768\t" +
            "0\t" +
            "UniProtKB\t" +
            "2011_11\t" +
            "[MS, MS:1001207, Mascot, ]\t" +
            "[MS, MS:1001171, Mascot:score, 62.93]\t" +
            "3\t" +
            "10[MS,MS:100xxxx,Probability Score Y,0.8]-MOD:00412\t" +
            "10.2\t" +
            "2\t" +
            "545.79\t" +
            "1234.4\t" +
            "http://www.ebi.ac.uk/pride/link/to/peptide\t" +
            "ms_run[1]:index=5\t" +
            "K\t" +
            "D\t" +
            "45\t" +
            "57\t" +
            "74.15\t" +
            "1\t" +
            "10018\t" +
            "pubmed:20432482\t" +
            "null";
        psm = dataParser.getRecord(data);
        items = data.split("\t");
        assertTrue(psm.getSequence().equals(items[1]));
        assertTrue(psm.getSearchEngine().get(0).getAccession().equals("MS:1001207"));
        assertTrue(psm.getSearchEngineScore().get(0).getAccession().equals("MS:1001171"));
        assertTrue(psm.getModifications().get(0).getType() == Modification.Type.MOD);
        assertTrue(psm.getModifications().get(0).getAccession().equals("00412"));
        assertTrue(psm.getCharge().equals(new Integer(items[12])));
        assertTrue(psm.getExpMassToCharge().toString().equals(items[13]));

        // opt_global_cv_MS:1000879_PubMed_identifier
        CVParam param = new CVParam("MS", "MS:1000879", "PubMed identifier", null);
        assertTrue(psm.getOptionColumn(param).equals("pubmed:20432482"));
    }

    @Test
    public void testLoadSmallMoleculeData() throws Exception {
        MZTabErrorList errorList = new MZTabErrorList();

        String header = "SMH\t" +
            "identifier\t" +
            "chemical_formula\t" +
            "smiles\t" +
            "inchi_key\t" +
            "description\t" +
            "exp_mass_to_charge\t" +
            "calc_mass_to_charge\t" +
            "charge\t" +
            "retention_time\t" +
            "taxid\t" +
            "species\t" +
            "database\t" +
            "database_version\t" +
            "reliability\t" +
            "uri\t" +
            "spectra_ref\t" +
            "search_engine\t" +
            "best_search_engine_score\t" +
            "search_engine_score_ms_run[1]\t" +
            "modifications\t" +
            "smallmolecule_abundance_assay[1]\t" +
            "smallmolecule_abundance_study_variable[1]\t" +
            "smallmolecule_abundance_stdev_study_variable[1]\t" +
            "smallmolecule_abundance_std_error_study_variable[1]\t" +
            "smallmolecule_abundance_study_variable[2]\t" +
            "smallmolecule_abundance_stdev_study_variable[2]\t" +
            "smallmolecule_abundance_std_error_study_variable[2]";

        // check stable columns
        SMHLineParser headerParser = new SMHLineParser(metadata);
        headerParser.parse(1, header, new MZTabErrorList());
        MZTabColumnFactory factory = headerParser.getFactory();
        SMLLineParser dataParser = new SMLLineParser(factory, new PositionMapping(factory, header), metadata, errorList);

        String data = "SML\t" +
            "CID:00027395\t" +
            "C17H20N4O2\t" +
            "C1=CC=C(C=C1)CCNC(=O)CCNNC(=O)C2=CC=NC=C2\t" +
            "QXBMEGUKVLFJAM-UHFFFAOYSA-N\t" +
            "N-(2-phenylethyl)-3-[2-(pyridine-4-carbonyl)hydrazinyl]propanamide\t" +
            "1234.4\t" +
            "1234.5\t" +
            "2\t" +
            "10.2|11.5\t" +
            "9606\t" +
            "Homo sapiens (Human)\t" +
            "name of used database\t" +
            "2011-12-22\t" +
            "3\t" +
            "http://www.ebi.ac.uk/pride/link/to/identification\t" +
            "ms_run[1]:index=1002\t" +
            "[MS, MS:1001477, SpectraSt,]\t" +
            "[MS, MS:1001419, SpectraST:discriminant score F, 0.7]\t" +
            "[MS, MS:1001419, SpectraST:discriminant score F, 0.7]\t" +
            "CHEMMOD:-NH4\t" +
            "0.3\t" +
            "0.1\t" +
            "0.2\t" +
            "0.3\t" +
            "0.1\t" +
            "0.2\t" +
            "0.3";

        SmallMolecule record = dataParser.getRecord(data);

        System.out.println(header);
        System.out.println(record);
    }
}
