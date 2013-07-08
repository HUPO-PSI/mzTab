package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.net.URL;

/**
 * User: Qingwei
 * Date: 22/05/13
 */
public class MetadataRun {
    public static void main(String[] args) throws Exception {
        Metadata mtd = new Metadata();

        mtd.getTabDescription().setId("PRIDE_1234");

        mtd.setTitle("mzTab iTRAQ test");
        mtd.setDescription("This is a PRIDE test.");

        mtd.addSampleProcessingParam(1, new CVParam("SEP", "SEP:00173", "SDS PAGE", null));
        mtd.addSampleProcessingParam(2, new CVParam("SEP", "SEP:00142", "enzyme digestion", null));
        mtd.addSampleProcessingParam(2, new CVParam("MS", "MS:1001251", "Trypsin", null));

        mtd.addInstrumentName(1, new CVParam("MS", "MS:100049", "LTQ Orbitrap", null));
        mtd.addInstrumentSource(2, new CVParam("MS", "MS:1000598", "ETD", null));
        mtd.addInstrumentAnalyzer(1, new CVParam("MS", "MS:1000291", "linear ion trap", null));
        mtd.addInstrumentDetector(3, new CVParam("MS", "MS:1000253", "electron multiplier", null));

        mtd.addSoftwareParam(1, new CVParam("MS", "MS:1001207", "Mascot", "2.3"));
        mtd.addSoftwareSetting(2, "Fragment tolerance = 0.1Da");
        mtd.addSoftwareSetting(2, "Parent tolerance = 0.5Da");

        mtd.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1234", "pep-fdr", "0.5"));
        mtd.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001364", "pep:global FDR", "0.01"));
        mtd.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001214", "pep:global FDR", "0.08"));

        mtd.addPublicationItem(1, PublicationItem.Type.PUBMED, "21063943");
        mtd.addPublicationItem(1, PublicationItem.Type.DOI, "10.1007/978-1-60761-987-1_6");
        mtd.addPublicationItem(2, PublicationItem.Type.PUBMED, "20615486");
        mtd.addPublicationItem(2, PublicationItem.Type.DOI, "10.1016/j.jprot.2010.06.008");

        mtd.addContactName(1, "James D. Watson");
        mtd.addContactAffiliation(1, "Cambridge University, UK");
        mtd.addContactAffiliation(2, "Cambridge University, UK");
        mtd.addContactEmail(1, "watson@cam.ac.uk");
        mtd.addContactEmail(2, "crick@cam.ac.uk");

        mtd.addUri(new URI("http://www.ebi.ac.uk/pride/url/to/experiment"));
        mtd.addUri(new URI("http://proteomecentral.proteomexchange.org/cgi/GetDataset"));

        mtd.addModParam(new CVParam("MOD", "MOD:00397", "iodoacetamide derivatized residue", null));
        mtd.addModParam(new CVParam("MOD", "MOD:00675", "oxidized residue", null));

        mtd.setQuantificationMethod(new CVParam("MS", "MS:1001837", "iTraq", null));
        mtd.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        mtd.setPeptideQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        mtd.setSmallMoleculeQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));

        mtd.addMsFileFormat(1, new CVParam("MS", "MS:1000584", "mzML file", null));
        mtd.addMsFileLocation(2, new URL("file://C:\\path\\to\\my\\file"));
        mtd.addMsFileLocation(3, new URL("ftp://ftp.ebi.ac.uk/path/to/file"));
        mtd.addMsFileIdFormat(2, new CVParam("MS", "MS:1000774", "multiple peak list", "nativeID format"));

        mtd.addCustom(new UserParam("MS operator", "Florian"));

        mtd.addSampleSpecies(1, 1, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        mtd.addSampleSpecies(1, 2, new CVParam("NEWT", "573824", "Human rhinovirus 1", null));
        mtd.addSampleSpecies(2, 1, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        mtd.addSampleSpecies(2, 2, new CVParam("NEWT", "12130", "Human rhinovirus 2", null));

        Sample sample1 = mtd.getSampleMap().get(1);
        Sample sample2 = mtd.getSampleMap().get(2);
        mtd.addAssayQuantificationReagent(1, new CVParam("PRIDE", "PRIDE:0000114", "iTRAQ reagent", "114"));
        mtd.addAssayQuantificationReagent(2, new CVParam("PRIDE", "MS:1002038", "unlabeled sample", null));
        mtd.addAssaySample(1, sample1);
        mtd.addAssaySample(2, sample2);

        MsFile msFile1 = mtd.getMsFileMap().get(1);
        mtd.addAssayMsFile(1, msFile1);

        Assay assay1 = mtd.getAssayMap().get(1);
        Assay assay2 = mtd.getAssayMap().get(2);
        mtd.addStudyVariableAssay(1, assay1);
        mtd.addStudyVariableAssay(1, assay2);

        mtd.addStudyVariableSample(1, sample1);
        mtd.addStudyVariableDescription(1, "description Group B (spike-in 0.74 fmol/uL)");


        mtd.addProteinColUnit(ProteinColumn.RELIABILITY, new CVParam("MS", "MS:00001231", "PeptideProphet:Score", null));

        MZTabColumnFactory peptideFactory = MZTabColumnFactory.getInstance(Section.Peptide);
        PeptideColumn peptideColumn = (PeptideColumn) peptideFactory.findColumn("retention_time");
        mtd.addPeptideColUnit(peptideColumn, new CVParam("UO", "UO:0000031", "minute", null));

        mtd.addSmallMoleculeColUnit(SmallMoleculeColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));

        System.out.println(mtd);
    }
}
