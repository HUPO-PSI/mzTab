package uk.ac.ebi.pride.jmztab.model.utils;

import uk.ac.ebi.pride.jmztab.model.*;

/**
 * @author qingwei
 * @since 24/05/13
 */
public class MZTabColumnFactoryRun {
    public MZTabColumnFactory printProteinHeader() {
        MsRun msRun1 = new MsRun(1);
        MsRun msRun2 = new MsRun(2);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein_Header);
        System.out.println(factory);
        System.out.println();

        // add optional columns which have stable order.
        factory.addGoTermsOptionalColumn();
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);

        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun2);
        System.out.println(factory);
        System.out.println();

        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 2);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun1);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 2, msRun1);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun2);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 2, msRun2);
        System.out.println(factory);
        System.out.println();

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        System.out.println(factory);
        System.out.println();

        // add user defined optional columns
        factory.addOptionalColumn(msRun1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);
        System.out.println();

        return factory;
    }

    public MZTabColumnFactory printPeptideHeader() {
        MsRun msRun1 = new MsRun(1);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Peptide_Header);
        System.out.println(factory);
        System.out.println();

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        factory.addBestSearchEngineScoreOptionalColumn(PeptideColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addSearchEngineScoreOptionalColumn(PeptideColumn.SEARCH_ENGINE_SCORE, 1, msRun1);
        System.out.println(factory);
        System.out.println();

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        System.out.println(factory);
        System.out.println();

        // add user defined optional columns
        factory.addOptionalColumn(msRun1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);
        System.out.println();

        return factory;
    }

    public MZTabColumnFactory printPSMHeader() {
        Assay assay1 = new Assay(1);

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.PSM_Header);
        System.out.println(factory);
        System.out.println();

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        factory.addSearchEngineScoreOptionalColumn(PSMColumn.SEARCH_ENGINE_SCORE, 1, null);

        // add user defined optional columns
        factory.addOptionalColumn(assay1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);
        System.out.println();

        return factory;
    }

    public MZTabColumnFactory printSmallMoleculeHeader() {
        MsRun msRun1 = new MsRun(1);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        // create stable columns.
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Small_Molecule);
        System.out.println(factory);
        System.out.println();

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        // add optional columns which have stable order.
        factory.addBestSearchEngineScoreOptionalColumn(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addSearchEngineScoreOptionalColumn(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, 1, msRun1);
        System.out.println(factory);
        System.out.println();

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        System.out.println(factory);
        System.out.println();

        // add user defined optional columns
        factory.addOptionalColumn(msRun1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        System.out.println(factory);
        System.out.println();

        return factory;
    }

    public static void main(String[] args) {
        MZTabColumnFactoryRun run = new MZTabColumnFactoryRun();

        System.out.println("\n\n print protein header");
        run.printProteinHeader();

        System.out.println("\n\n print peptide header");
        run.printPeptideHeader();

        System.out.println("\n\n print PSM header");
        run.printPSMHeader();

        System.out.println("\n\n print small molecule header");
        run.printSmallMoleculeHeader();
    }
}
