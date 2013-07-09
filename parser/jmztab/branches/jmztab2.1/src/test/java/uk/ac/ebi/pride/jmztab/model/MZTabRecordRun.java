package uk.ac.ebi.pride.jmztab.model;

import java.net.URL;

/**
 * User: Qingwei
 * Date: 29/05/13
 */
public class MZTabRecordRun {
    private void addProteinValue() {
        MsRun msRun1 = new MsRun(1);
        MsRun msRun2 = new MsRun(2);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein_Header);
        factory.addOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun2);

        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);

        factory.addOptionalColumn(assay1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);

        System.out.println(factory);
        Protein protein = new Protein(factory);

        // set stable columns data.
        protein.setAccession("P12345");
        protein.setDescription("Aspartate aminotransferase, mitochondrial");
        protein.setTaxid("10116");
        protein.setSpecies("Rattus norvegicus (Rat)");
        protein.setDatabase("UniProtKB");
        protein.setDatabaseVersion("2011_11");
        protein.setSearchEngine("[MS,MS:1001207,Mascot,]");
        protein.addSearchEngineParam("[MS,MS:1001208,Sequest,]");
        protein.setBestSearchEngineScore("[MS,MS:1001171,Mascot score,50]|[MS,MS:1001155,Sequest:xcorr,2]");
        protein.setReliability(Reliability.High);
        protein.setAmbiguityMembers("P12347,P12348");
        protein.setModifications("3|4|8-MOD:00412, 3|4|8-MOD:00412");
        protein.setURI("http://www.ebi.ac.uk/pride/url/to/P12345");
        protein.setGOTerms("GO:0006457|GO:0005759|GO:0005886|GO:0004069");
        protein.setProteinConverage("0.4");
        System.out.println(protein);

        // set optional columns which have stable order.
        protein.setSearchEngineScore(msRun1, "[MS,MS:1001171,Mascot score,50]|[MS,MS:1001155,Sequest:xcorr,2]");
        protein.setNumPSMs(msRun1, 4);
        protein.setNumPSMs(msRun2, 2);
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
        protein.setOptionColumn(assay1, "my_value", "My value about assay[1]");
        protein.setOptionColumn(param, "TOM value");

        System.out.println(protein);
    }

    private void addPeptideValue() throws Exception {
        MsRun msRun1 = new MsRun(1);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Peptide_Header);
        factory.addOptionalColumn(PeptideColumn.SEARCH_ENGINE_SCORE, msRun1);
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        factory.addOptionalColumn(msRun1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);

        Metadata metadata = new Metadata();
        metadata.addMsRunLocation(2, new URL("file://C:\\path\\to\\my\\file"));

        System.out.println(factory);
        Peptide peptide = new Peptide(factory, metadata);

        peptide.setSequence("KVPQVSTPTLVEVSR");
        peptide.setAccession("P02768");
        peptide.setUnique("0");
        peptide.setDatabase("UniProtKB");
        peptide.setDatabaseVersion("2011_11");
        peptide.setSearchEngine("[MS,MS:1001207,Mascot,]|[MS,MS:1001208,Sequest,]");
        peptide.setBestSearchEngineScore("[MS,MS:1001155,Sequest:xcorr,2]");
        peptide.setReliability("3");
        peptide.setModifications("3[MS,MS:1001876, modification probability, 0.8]|4[MS,MS:1001876, modification probability, 0.2]-MOD:00412,8[MS,MS:1001876, modification probability, 0.3]-MOD:00412");
        peptide.setRetentionTime("10.2");
        peptide.setCharge("2");
        peptide.setMassToCharge("1234.4");
        peptide.setURI("http://www.ebi.ac.uk/pride/link/to/peptide");
        peptide.setSpectraRef("ms_run[2]:index=7|ms_run[2]:index=9");
        System.out.println(peptide);
    }

    private void addPSMValue() throws Exception {
        Assay assay1 = new Assay(1);

        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.PSM_Header);
        factory.addOptionalColumn(assay1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);

        Metadata metadata = new Metadata();
        metadata.addMsRunLocation(2, new URL("file://C:\\path\\to\\my\\file"));

        System.out.println(factory);
        PSM psm = new PSM(factory, metadata);

        psm.setSequence("KVPQVSTPTLVEVSR");
        psm.setPSM_ID("1");
        psm.setAccession("P02768");
        psm.setUnique(MZBoolean.False);
        psm.setDatabase("UniProtKB");
        psm.setDatabaseVersion("2011_11");
        psm.setSearchEngine("[MS,MS:1001207,Mascot,]|[MS,MS:1001208,Sequest,]");
        psm.setSearchEngineScore("[MS,MS:1001155,Sequest:xcorr,2]");
        psm.setReliability("3");
        psm.setModifications("CHEMMOD:+159.93");
        psm.setRetentionTime("10.2");
        psm.setCharge("2");
        psm.setExpMassToCharge("1234.4");
        psm.setCalcMassToCharge("123.4");
        psm.setURI("http://www.ebi.ac.uk/pride/link/to/peptide");
        psm.setSpectraRef("ms_run[2]:index=7|ms_run[2]:index=9");
        psm.setPre("K");
        psm.setPost("D");
        psm.setStart("45");
        psm.setEnd("57");
        System.out.println(psm);
    }

    private void addSmallMoleculeValue() throws Exception {
        MsRun msRun1 = new MsRun(1);
        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        StudyVariable studyVariable1 = new StudyVariable(1);

        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Small_Molecule);
        factory.addAbundanceOptionalColumn(assay1);
        factory.addAbundanceOptionalColumn(studyVariable1);
        factory.addAbundanceOptionalColumn(assay2);
        factory.addOptionalColumn(msRun1, "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);

        Metadata metadata = new Metadata();
        metadata.addMsRunLocation(2, new URL("file://C:\\path\\to\\my\\file"));

        System.out.println(factory);
        SmallMolecule sm = new SmallMolecule(factory, metadata);
        sm.setIdentifier("CID:00027395");
        sm.setChemicalFormula("C17H20N4O2");
        sm.setSmiles("C1=CC=C(C=C1)CCNC(=O)CCNNC(=O)C2=CC=NC=C2");
        sm.setInchiKey("QXBMEGUKVLFJAM-UHFFFAOYSA-N");
        sm.setDescription("N-(2-phenylethyl)-3-[2-(pyridine-4-carbonyl)hydrazinyl]propanamide");
        sm.setExpMassToCharge("1234.4");
        sm.setCalcMassToCharge("1234.5");
        sm.setCharge("2");
        sm.setRetentionTime("10.2|11.5");
        sm.setTaxid("10116");
        sm.setSpecies("Rattus norvegicus (Rat)");
        sm.setDatabase("UniProtKB");
        sm.setDatabaseVersion("2011_11");
        sm.setReliability("2");
        sm.setURI("http://www.ebi.ac.uk/pride/link/to/identification");
        sm.setSpectraRef("ms_run[2]:index=7|ms_run[2]:index=9");
        sm.setSearchEngine("[MS, MS:1001477, SpectraST,]");
        sm.setBestSearchEngineScore("[MS, MS:1001419, SpectraST:discriminant score F, 0.7]");
        sm.setModifications("CHEMMOD:+Na-H");

        System.out.println(sm);
    }

    public static void main(String[] args) throws Exception {
        MZTabRecordRun run = new MZTabRecordRun();

        System.out.println("Fill protein record.");
        run.addProteinValue();
        System.out.println("\n\n");

        System.out.println("Fill peptide record.");
        run.addPeptideValue();
        System.out.println("\n\n");

        System.out.println("Fill PSM record.");
        run.addPSMValue();
        System.out.println("\n\n");

        System.out.println("Fill small molecule record.");
        run.addSmallMoleculeValue();
        System.out.println("\n\n");
    }
}
