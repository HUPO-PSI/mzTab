package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.net.URI;
import java.net.URL;

/**
 * User: Qingwei
 * Date: 01/02/13
 */
public class UnitRun {
    private void printPRIDEUnit() throws Exception {
        Unit unit = new Unit("PRIDE_1234");

        unit.setTitle("mzTab iTRAQ test");
        unit.setDescription("This is a PRIDE test.");

        SplitList<Param> sampleProcessing1 = new SplitList<Param>(MZTabConstants.BAR);
        sampleProcessing1.add(new CVParam("SEP", "SEP:00173", "SDS PAGE", null));
        SplitList<Param> sampleProcessing2 = new SplitList<Param>(MZTabConstants.BAR);
        sampleProcessing2.add(new CVParam("SEP", "SEP:00142", "enzyme digestion", null));
        sampleProcessing2.add(new CVParam("MS", "MS:1001251", "Trypsin", null));
        unit.addSampleProcessing(1, sampleProcessing1);
        unit.addSampleProcessing(2, sampleProcessing2);

        unit.addInstrumentName(1, new CVParam("MS", "MS:100049", "LTQ Orbitrap", null));
        unit.addInstrumentSource(2, new CVParam("MS", "MS:1000598", "ETD", null));
        unit.addInstrumentAnalyzer(1, new CVParam("MS", "MS:1000291", "linear ion trap", null));
        unit.addInstrumentDetector(3, new CVParam("MS", "MS:1000253", "electron multiplier", null));

        unit.addSoftwareParam(1, new CVParam("MS", "MS:1001207", "Mascot", "2.3"));
        unit.addSoftwareSetting(2, "Fragment tolerance = 0.1Da");
        unit.addSoftwareSetting(2, "Parent tolerance = 0.5Da");

        unit.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1234", "pep-fdr", "0.5"));
        unit.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001364", "pep:global FDR", "0.01"));
        unit.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001214", "pep:global FDR", "0.08"));

        Publication p1 = new Publication();
        p1.addPublication(Publication.Type.PUBMED, "21063943");
        p1.addPublication(Publication.Type.DOI, "10.1007/978-1-60761-987-1_6");
        Publication p2 = new Publication();
        p2.addPublication(Publication.Type.PUBMED, "20615486");
        p2.addPublication(Publication.Type.DOI, "10.1016/j.jprot.2010.06.008");
        unit.addPublication(p1);
        unit.addPublication(p2);

        unit.addContactName(1, "James D. Watson");
        unit.addContactAffiliation(1, "Cambridge University, UK");
        unit.addContactAffiliation(2, "Cambridge University, UK");
        unit.addContactEmail(1, "watson@cam.ac.uk");
        unit.addContactEmail(2, "crick@cam.ac.uk");

        unit.addUri(new URI("http://www.ebi.ac.uk/pride/url/to/experiment"));
        unit.addUri(new URI("http://proteomecentral.proteomexchange.org/cgi/GetDataset"));

        unit.addModParam(new CVParam("MOD", "MOD:00397", "iodoacetamide derivatized residue", null));
        unit.addModParam(new CVParam("MOD", "MOD:00675", "oxidized residue", null));

        unit.setModProbabilityMethod(new CVParam("MS", "MS:1001837", "iTraq", null));
        unit.setQuantificationMethod(new CVParam("MS", "MS:1001837", "iTraq", null));
        unit.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        unit.setPeptideQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));

        unit.addMsFileFormat(1, new CVParam("MS", "MS:1000584", "mzML file", null));
        unit.addMsFileLocation(2, new URL("file://C:\\path\\to\\my\\file"));
        unit.addMsFileLocation(3, new URL("ftp://ftp.ebi.ac.uk/path/to/file"));
        unit.addMsFileIdFormat(2, new CVParam("MS", "MS:1000774", "multiple peak list", "nativeID format"));

        unit.addCustom(new UserParam("MS operator", "Florian"));

        MZTabColumnFactory peptideFactory = MZTabColumnFactory.getInstance(Section.Peptide);
        PeptideColumn peptideColumn = (PeptideColumn) peptideFactory.getColumn("retention_time");
        unit.addPeptideColUnit(peptideColumn, new CVParam("UO", "UO:0000031", "minute", null));
        unit.addProteinColUnit(ProteinColumn.RELIABILITY, new CVParam("MS", "MS:00001231", "PeptideProphet:Score", null));
        unit.addSmallMoleculeColUnit(SmallMoleculeColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));

        System.out.println(unit);
    }

    private void printPRIDESubUnitWithoutSubId() throws Exception {
        SubUnit subUnit1 = new SubUnit("PRIDE_1234", null);

        subUnit1.addSpecies(1, new CVParam("NEWT", "9606", "Homo sapien (Human)", null));
        subUnit1.addSpecies(2, new CVParam("NEWT", "12059", "Rhinovirus", null));
        subUnit1.addTissue(1, new CVParam("BTO", "BTO:0000759", "liver", null));
        subUnit1.addCellType(1, new CVParam("CL", "CL:0000182", "hepatocyte", null));
        subUnit1.addDisease(1, new CVParam("DOID", "DOID:684", "hepatocellular carcinoma", null));
        subUnit1.addDisease(2, new CVParam("DOID", "DOID:9451", "alcoholic fatty liver", null));
        subUnit1.setDescription("Hepatocellular carcinoma sample.");

        System.out.println(subUnit1);
    }

    private void printPRIDESubUnitWithSubId() throws Exception {
        SubUnit subUnit1 = new SubUnit("PRIDE_1234", 1);
        SubUnit subUnit2 = new SubUnit("PRIDE_1234", 2);

        subUnit1.addSpecies(1, new CVParam("NEWT", "9606", "Homo sapien (Human)", null));
        subUnit1.addSpecies(2, new CVParam("NEWT", "573824", "Human rhinovirus 1", null));
        subUnit2.addSpecies(1, new CVParam("NEWT", "9606", "Homo sapien (Human)", null));
        subUnit2.addSpecies(2, new CVParam("NEWT", "12130", "Human rhinovirus 2", null));

        subUnit1.setDescription("Hepatocellular carcinoma sample.");
        subUnit2.setDescription("Healthy control samples.");

        subUnit1.setQuantificationReagent(new CVParam("PRIDE", "PRIDE:0000114", "iTRAQ reagent", "114"));
        subUnit2.setQuantificationReagent(new CVParam("PRIDE", "PRIDE:0000115", "iTRAQ reagent", "115"));

        subUnit1.addCustom(new UserParam("Extraction date", "2011-12-21"));
        subUnit1.addCustom(new UserParam("Extraction reason", "liver biopsy"));

        System.out.println(subUnit1);
        System.out.println(subUnit2);
    }

    private void printRepUnit() {
        ReplicateUnit repUnit = new ReplicateUnit("EXP_1", 1);
        repUnit.setComment("Replicate 1 of experiment 1");

        repUnit.setDescription("Replicate 1 of experiment 1");
        repUnit.addInstrumentName(1, new CVParam("MS", "MS:100049", "LTQ Orbitrap", null));
        repUnit.addInstrumentSource(2, new CVParam("MS", "MS:1000598", "ETD", null));
        repUnit.addInstrumentAnalyzer(1, new CVParam("MS", "MS:1000291", "linear ion trap", null));
        repUnit.addInstrumentDetector(3, new CVParam("MS", "MS:1000253", "electron multiplier", null));

        System.out.println(repUnit);
    }

    public static void main(String[] args) throws Exception {
        UnitRun run = new UnitRun();

        System.out.println("Testing Unit and its elements");
        System.out.println("============================================");
        run.printPRIDEUnit();
        System.out.println();

        System.out.println("Testing SubUnit without Sub_IDs");
        System.out.println("============================================");
        run.printPRIDESubUnitWithoutSubId();
        System.out.println();

        System.out.println("Testing SubUnit with Sub_IDs");
        System.out.println("============================================");
        run.printPRIDESubUnitWithSubId();
        System.out.println();

        System.out.println("Testing Replicate Unit");
        System.out.println("============================================");
        run.printRepUnit();
        System.out.println();
    }
}
