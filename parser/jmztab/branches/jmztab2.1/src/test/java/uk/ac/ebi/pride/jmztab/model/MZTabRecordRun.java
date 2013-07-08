package uk.ac.ebi.pride.jmztab.model;

import java.net.URL;

/**
 * User: Qingwei
 * Date: 29/05/13
 */
public class MZTabRecordRun {
    private void addProteinValue() {
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein);
        MsRun msRun1 = new MsRun(1);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        factory.addOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);

        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(assay2);
        factory.addAbundanceOptionalColumn(studyVariable1);

        factory.addOptionalColumn(assay1, "my value", String.class);
        CVParam param = new CVParam("MS", "MS:1001208", "TOM", null);
        factory.addOptionalColumn(param, String.class);

        System.out.println(factory);

        Protein protein = new Protein(factory);

        // set stable columns data.
        protein.setAccession("P12345");
        protein.setDescription("Aspartate aminotransferase, mitochondrial");
        protein.setTaxid("10116");
        SplitList<Param> searchEngine = new SplitList<Param>(MZTabConstants.BAR);
        searchEngine.add(new CVParam("MS", "MS:1001207", "Mascot", null));
        searchEngine.add(new CVParam("MS", "MS:1001208", "Sequest", null));
        protein.setSearchEngine(searchEngine);
        protein.setReliability(Reliability.High);
        System.out.println(protein);

        // set optional columns which have stable order.
        protein.setSearchEngineScore(msRun1, "[MS,MS:1001171,Mascot score,50]|[MS,MS:1001155,Sequest:xcorr,2]");
        protein.setNumPSMs(msRun1, 4);
        protein.setNumPeptidesDistinct(msRun1, 3);
        protein.setNumPeptidesUnique(msRun1, 2);
        System.out.println(protein);

        // set abundance columns
        protein.setAbundanceColumn(assay1, "0.4");
        protein.setAbundanceColumn(assay2, "0.2");

        protein.setAbundanceColumn(studyVariable1, "0.4");
        protein.setAbundanceStdevColumn(studyVariable1, "0.3");
        protein.setAbundanceStdErrorColumn(studyVariable1, "0.2");
        System.out.println(protein);

        // set user defined optional columns
        protein.setOptionColumn(assay1, "my value", "My value about assay[1]");
        protein.setOptionColumn(param, "TOM value");
        System.out.println(protein);
    }

    private void addPeptideValue() throws Exception {
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Peptide);
        Metadata metadata = new Metadata();
        metadata.addMsRunLocation(2, new URL("file://C:\\path\\to\\my\\file"));

        System.out.println(factory);
        Peptide peptide = new Peptide(factory, metadata);
        peptide.setSpectraRef("ms_file[2]:index=7|ms_file[2]:index=9");
        System.out.println(peptide);
    }

    public static void main(String[] args) throws Exception {
        MZTabRecordRun run = new MZTabRecordRun();

        run.addProteinValue();
//        run.addPeptideValue();
    }
}
