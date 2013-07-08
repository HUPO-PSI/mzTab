package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 24/05/13
 */
public class MZTabColumnFactoryRun {
    public static void main(String[] args) {
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
        factory.addOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);

        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun2);
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

    }
}
