package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.convert.ConvertProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Create a SILAC experiment mzTab file, with quantification on 2 study variables (control/treatment),
 * 3+3 assays (replicates) reported, identifications reported.
 *
 * mzTab file coming from : http://mztab.googlecode.com/svn/examples/SILAC_CQI.mzTab
 *
 * @author qingwei
 * @since 13/12/13
 */
public class ConvertSilacExperiment extends ConvertProvider<Void, Void> {
    private Metadata mtd;

    private MZTabColumnFactory prh;   // Protein Header Column Factory
    private MZTabColumnFactory psh;   // PSM Header Column Factory.

    public ConvertSilacExperiment() {
        // Generate mzTab by manual, no convert source and parameters setting.
        super(null, null);
    }

    /**
     * Generate metadata section by manual.
     */
    @Override
    protected Metadata convertMetadata(){
        this.mtd = new Metadata();

        // setting mzTab- description.
        mtd.setMZTabMode(MZTabDescription.Mode.Complete);
        mtd.setMZTabType(MZTabDescription.Type.Quantification);
        mtd.setDescription("mzTab example file for reporting a summary report of quantification data quantified on the protein level");

        // create ms_run[1-6]-location
        try {
            mtd.addMsRunLocation(1, new URL("file://path/to/my/file1.mzML"));
            mtd.addMsRunLocation(2, new URL("file://path/to/my/file2.mzML"));
            mtd.addMsRunLocation(3, new URL("file://path/to/my/file3.mzML"));
            mtd.addMsRunLocation(4, new URL("file://path/to/my/file4.mzML"));
            mtd.addMsRunLocation(5, new URL("file://path/to/my/file5.mzML"));
            mtd.addMsRunLocation(6, new URL("file://path/to/my/file6.mzML"));
        } catch (MalformedURLException e) {
//            e.printStackTrace();
        }

        mtd.addProteinSearchEngineScoreParam(1, new CVParam("MS", "MS:1001171", "Mascot:score", null));

        // set protein-quantification_unit
        mtd.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000393", "Relative quantification unit", null));

        // set software[1] parameter
        mtd.addSoftwareParam(1, new CVParam("MS", "MS:1001583", "MaxQuant", null));

        // set fixed_mod[1], fixed_mod[2] and variable_mod[2]
        mtd.addFixedModParam(1, new CVParam("UNIMOD", "UNIMOD:4", "Carbamidomethyl", null));
        mtd.addFixedModParam(2, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        mtd.addVariableModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));

        // set quantification_method
        mtd.setQuantificationMethod(new CVParam("MS", "MS:1001835", "SILAC", null));

        // set assay[1-6]-quantification_reagent
        mtd.addAssayQuantificationReagent(1, new CVParam("PRIDE", "PRIDE:0000326", "SILAC light", null));
        mtd.addAssayQuantificationReagent(2, new CVParam("PRIDE", "PRIDE:0000325", "SILAC heavy", null));
        mtd.addAssayQuantificationReagent(3, new CVParam("PRIDE", "PRIDE:0000326", "SILAC light", null));
        mtd.addAssayQuantificationReagent(4, new CVParam("PRIDE", "PRIDE:0000325", "SILAC heavy", null));
        mtd.addAssayQuantificationReagent(5, new CVParam("PRIDE", "PRIDE:0000326", "SILAC light", null));
        mtd.addAssayQuantificationReagent(6, new CVParam("PRIDE", "PRIDE:0000325", "SILAC heavy", null));

        // set assay[1-6]-ms_run_ref
        mtd.addAssayMsRun(1, mtd.getMsRunMap().get(1));
        mtd.addAssayMsRun(2, mtd.getMsRunMap().get(1));
        mtd.addAssayMsRun(3, mtd.getMsRunMap().get(2));
        mtd.addAssayMsRun(4, mtd.getMsRunMap().get(2));
        mtd.addAssayMsRun(5, mtd.getMsRunMap().get(3));
        mtd.addAssayMsRun(6, mtd.getMsRunMap().get(3));

        // set study_variable[1]-assay_refs 's value is assay[1],assay[3],assay[5]
        mtd.addStudyVariableAssay(1, mtd.getAssayMap().get(1));
        mtd.addStudyVariableAssay(1, mtd.getAssayMap().get(3));
        mtd.addStudyVariableAssay(1, mtd.getAssayMap().get(5));
        // set study_variable[1]-description
        mtd.addStudyVariableDescription(1, "heat shock response of control");

        // set study_variable[2]-assay_refs 's value is assay[2],assay[4],assay[6]
        mtd.addStudyVariableAssay(2, mtd.getAssayMap().get(2));
        mtd.addStudyVariableAssay(2, mtd.getAssayMap().get(4));
        mtd.addStudyVariableAssay(2, mtd.getAssayMap().get(6));
        // set study_variable[2]-description
        mtd.addStudyVariableDescription(2, "heat shock response of treatment");

        return mtd;
    }

    /**
     * Generate protein header line by manual.
     */
    @Override
    protected MZTabColumnFactory convertProteinColumnFactory() {
        prh = MZTabColumnFactory.getInstance(Section.Protein_Header);

        prh.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 1);

        // optional columns: search_engine_score_ms_run[1-6], num_psms_ms_run[1-6], num_peptides_distinct_ms_run[1-6] and num_peptides_unique_ms_run[1-6]
        for (MsRun msRun : mtd.getMsRunMap().values()) {
            prh.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun);
            prh.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun);
            prh.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun);
            prh.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun);
        }

        // abundance optional columns: protein_abundance_assay[1-6]
        for (Assay assay : mtd.getAssayMap().values()) {
            prh.addAbundanceOptionalColumn(assay);
        }

        // abundance optional columns: protein_abundance_study_variable[1-2], protein_abundance_stdev_study_variable[1-2] and protein_abundance_std_error_study_variable[1-2]
        for (StudyVariable studyVariable : mtd.getStudyVariableMap().values()) {
            prh.addAbundanceOptionalColumn(studyVariable);
        }

        return prh;
    }

    /**
     * Generate standard psm header line.
     */
    @Override
    protected MZTabColumnFactory convertPSMColumnFactory() {
        psh = MZTabColumnFactory.getInstance(Section.PSM_Header);

        psh.addSearchEngineScoreOptionalColumn(PSMColumn.SEARCH_ENGINE_SCORE, 1, null);

        return psh;
    }

    /**
     * Generate and fill on protein record into mzTab file.
     */
    private void fillProteinRecord() {
        Protein protein = new Protein(prh);

        protein.setAccession("P63017");
        protein.setDescription("Heat shock cognate 71 kDa protein");
        protein.setTaxid("10090");
        protein.setSpecies("Mus musculus");
        protein.setDatabase("UniProtKB");
        protein.setDatabaseVersion("2013_08");
        protein.setSearchEngine("[MS,MS:1001207,Mascot,]");
        protein.setBestSearchEngineScore(1, "46");

        // add parameter value for search_engine_score_ms_run[1-6]
        // NOTICE: ms_run[1-6] and search_engine_score[1] SHOULD be defined in the metadata, otherwise throw exception.
        protein.setSearchEngineScore(1, mtd.getMsRunMap().get(1), "46");
        protein.setSearchEngineScore(1, mtd.getMsRunMap().get(2), "26");
        protein.setSearchEngineScore(1, mtd.getMsRunMap().get(3), "36");
        protein.setSearchEngineScore(1, mtd.getMsRunMap().get(4), "126");
        protein.setSearchEngineScore(1, mtd.getMsRunMap().get(5), "63");
        protein.setSearchEngineScore(1, mtd.getMsRunMap().get(6), "null");

        // add parameter value for num_psms_ms_run[1-6]
        protein.setNumPSMs(mtd.getMsRunMap().get(1), "1");
        protein.setNumPSMs(mtd.getMsRunMap().get(2), "1");
        protein.setNumPSMs(mtd.getMsRunMap().get(3), "1");
        protein.setNumPSMs(mtd.getMsRunMap().get(4), "1");
        protein.setNumPSMs(mtd.getMsRunMap().get(5), "1");
        protein.setNumPSMs(mtd.getMsRunMap().get(6), "0");

        // add parameter value for num_peptides_distinct_ms_run[1-6]
        protein.setNumPeptidesDistinct(mtd.getMsRunMap().get(1), "1");
        protein.setNumPeptidesDistinct(mtd.getMsRunMap().get(2), "1");
        protein.setNumPeptidesDistinct(mtd.getMsRunMap().get(3), "1");
        protein.setNumPeptidesDistinct(mtd.getMsRunMap().get(4), "1");
        protein.setNumPeptidesDistinct(mtd.getMsRunMap().get(5), "1");
        protein.setNumPeptidesDistinct(mtd.getMsRunMap().get(6), "0");

        // add parameter value for num_peptides_unique_ms_run[1-6]
        protein.setNumPeptidesUnique(mtd.getMsRunMap().get(1), "1");
        protein.setNumPeptidesUnique(mtd.getMsRunMap().get(2), "1");
        protein.setNumPeptidesUnique(mtd.getMsRunMap().get(3), "1");
        protein.setNumPeptidesUnique(mtd.getMsRunMap().get(4), "1");
        protein.setNumPeptidesUnique(mtd.getMsRunMap().get(5), "1");
        protein.setNumPeptidesUnique(mtd.getMsRunMap().get(6), "0");

        protein.setModifications("0");
        protein.setProteinConverage("0.34");

        // set value for  protein_abundance_assay[1-6]
        // NOTICE: assay[1-6] SHOULD be defined in the metadata, otherwise throw exception.
        protein.setAbundanceColumnValue(mtd.getAssayMap().get(1), "1");
        protein.setAbundanceColumnValue(mtd.getAssayMap().get(2), "17.3");
        protein.setAbundanceColumnValue(mtd.getAssayMap().get(3), "1");
        protein.setAbundanceColumnValue(mtd.getAssayMap().get(4), "26.7");
        protein.setAbundanceColumnValue(mtd.getAssayMap().get(5), "1");
        protein.setAbundanceColumnValue(mtd.getAssayMap().get(6), "12.3");

        // set value for protein_abundance_study_variable[1-2], protein_abundance_stdev_study_variable[1-2] and protein_abundance_std_error_study_variable[1-2]
        // NOTICE: study_variable[1-2] SHOULD be defined in the metadata, otherwise throw exception.
        // NOTICE: in this demo, protein_abundance_stdev_study_variable[1] and protein_abundance_std_error_study_variable[1] value are "null"
        protein.setAbundanceColumnValue(mtd.getStudyVariableMap().get(1), "1");                        // protein_abundance_study_variable[1]
        protein.setAbundanceColumnValue(mtd.getStudyVariableMap().get(2), "18.76666667");              // protein_abundance_study_variable[2]
        protein.setAbundanceStdevColumnValue(mtd.getStudyVariableMap().get(2), "7.311178656");         // protein_abundance_stdev_study_variable[2]
        protein.setAbundanceStdErrorColumnValue(mtd.getStudyVariableMap().get(2), "4.221110965");      // protein_abundance_std_error_study_variable[2]

        // NOTICE: should be add protein into proteins Container, which defined in the ConvertProvider class.
        proteins.add(protein);
    }

    /**
     * Generate and fill on PSM record into mzTab file.
     */
    private void fillPSMRecord() {
        PSM psm = new PSM(psh, mtd);

        psm.setSequence("QTQTFTTYSDNQPGVL");
        psm.setPSM_ID("1");
        psm.setAccession("P63017");
        psm.setDatabase("UniProtKB");
        psm.setDatabaseVersion("2013_08");
        psm.setSearchEngine("[MS,MS:1001207,Mascot,]");
        psm.setSearchEngineScore(1, "46");
        psm.setModifications("17-UNIMOD:188");
        psm.setSpectraRef("ms_run[1]:scan=1296");
        psm.setRetentionTime("1336.62");
        psm.setCharge("3");
        psm.setExpMassToCharge("600.6360419");
        psm.setCalcMassToCharge("600.6197");
        psm.setPre("K");
        psm.setPost("I");
        psm.setStart("424");
        psm.setEnd("429");

        // NOTICE: should be add psm into psms Container, which defined in the ConvertProvider class.
        psms.add(psm);
    }

    @Override
    protected void fillData() {
        fillProteinRecord();
        fillPSMRecord();
    }

    public static void main(String[] args) throws Exception {
        ConvertSilacExperiment run = new ConvertSilacExperiment();
        MZTabFile tabFile = run.getMZTabFile();

        // print mzTab model into console.
        tabFile.printMZTab(System.out);
    }
}
