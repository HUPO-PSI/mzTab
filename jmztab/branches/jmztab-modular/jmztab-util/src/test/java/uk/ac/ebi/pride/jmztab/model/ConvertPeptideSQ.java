package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.convert.ConvertProvider;

/**
 * Report of a minimal "Summary Quantification report" experiment, quantification on 2 study variables
 * (control/treatment), no assays (replicates) reported, no identifications reported
 *
 * User: qingwei
 * Date: 16/12/13
 */
public class ConvertPeptideSQ extends ConvertProvider<Void, Void> {
    private Metadata mtd;
    private MZTabColumnFactory peh;

    private StudyVariable sv1;
    private StudyVariable sv2;

    public ConvertPeptideSQ() {
        // Generate mzTab by manual, no convert source and parameters setting.
        super(null, null);
    }

    /**
     * Generate metadata section by manual.
     */
    @Override
    protected Metadata convertMetadata() {
        mtd = new Metadata();

        // setting mzTab- description.
        mtd.setMZTabMode(MZTabDescription.Mode.Summary);
        mtd.setMZTabType(MZTabDescription.Type.Quantification);
        mtd.setMZTabID("Peptide_SQ");
        mtd.setDescription("mzTab example file for reporting a summary report of quantification data quantified on the peptide level");

        // create ms_run[1-6]-location
        mtd.addMsRunLocation(1, MZTabUtils.parseURL("file://C:\\path\\to\\my\\file1.mzML"));
        mtd.addMsRunLocation(2, MZTabUtils.parseURL("file://C:\\path\\to\\my\\file2.mzML"));
        mtd.addMsRunLocation(3, MZTabUtils.parseURL("file://C:\\path\\to\\my\\file3.mzML"));
        mtd.addMsRunLocation(4, MZTabUtils.parseURL("file://C:\\path\\to\\my\\file4.mzML"));
        mtd.addMsRunLocation(5, MZTabUtils.parseURL("file://C:\\path\\to\\my\\file5.mzML"));
        mtd.addMsRunLocation(6, MZTabUtils.parseURL("file://C:\\path\\to\\my\\file6.mzML"));

        //set search_engine_score[1]
        mtd.addSearchEngineScoreParam(1, new CVParam("MS", "MS:1001171", "Mascot:score", null));

        // set fixed_mod[1] and variable_mod[1]
        mtd.addFixedModParam(1, MZTabUtils.parseParam("[UNIMOD, UNIMOD:4, Carbamidomethyl, ]"));
        mtd.addVariableModParam(1, MZTabUtils.parseParam("[UNIMOD, UNIMOD:35, Oxidation, ]"));

        // set peptide-quantification_unit
        mtd.setPeptideQuantificationUnit(MZTabUtils.parseParam("[PRIDE, PRIDE:0000393, Relative quantification unit,]"));

        // define study_variable[1]-description
        sv1 = new StudyVariable(1);
        sv1.setDescription("heat shock response of control");
        mtd.addStudyVariable(sv1);

        // define study_variable[2]-description
        sv2 = new StudyVariable(2);
        sv2.setDescription("heat shock response of treatment");
        mtd.addStudyVariable(sv2);

        return mtd;
    }

    /**
     * Generate peptide header line by manual.
     */
    @Override
    protected MZTabColumnFactory convertPeptideColumnFactory() {
        peh = MZTabColumnFactory.getInstance(Section.Peptide_Header);

        // add best_search_engine_score column
        peh.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 1);

        // abundance optional columns: peptide_abundance_study_variable[1-2], peptide_abundance_stdev_study_variable[1-2] and peptide_abundance_std_error_study_variable[1-2]
        for (StudyVariable studyVariable : mtd.getStudyVariableMap().values()) {
            peh.addAbundanceOptionalColumn(studyVariable);
        }

        return peh;
    }

    @Override
    protected void fillData() {
        Peptide peptide = new Peptide(peh, mtd);

        peptide.setSequence("KLVILEGELER");
        peptide.setAccession("IPI00010779");
        peptide.setUnique("0");
        peptide.setDatabase("UniProtKB");
        peptide.setDatabaseVersion("2013_08");
        peptide.setSearchEngine("[MS,MS:1001207,Mascot,]");
        peptide.setBestSearchEngineScore(1, "46");
        peptide.setRetentionTime("5498.3");
        peptide.setCharge("2");
        peptide.setMassToCharge("649.8875");

        // set value for peptide_abundance_study_variable[1-2], peptide_abundance_stdev_study_variable[1-2] and peptide_abundance_std_error_study_variable[1-2]
        // NOTICE: study_variable[1-2] SHOULD be defined in the metadata, otherwise throw exception.
        // NOTICE: in this demo, peptide_abundance_stdev_study_variable[1] and peptide_abundance_std_error_study_variable[1] value are "null"
        peptide.setAbundanceColumnValue(sv1, "1");
        peptide.setAbundanceColumnValue(sv2, "17.3");
        peptide.setAbundanceStdevColumnValue(sv2, "2.3");
        peptide.setAbundanceStdErrorColumnValue(sv2, "1.327905619");

        // NOTICE: should be add peptide into peptides Container, which defined in the ConvertProvider class.
        peptides.add(peptide);
    }

    public static void main(String[] args) throws Exception {
        ConvertPeptideSQ convert = new ConvertPeptideSQ();
        MZTabFile tabFile = convert.getMZTabFile();

        // print mzTab model into console.
        tabFile.printMZTab(System.out);
    }
}
