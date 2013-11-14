package uk.ac.ebi.pride.jmztab.model;

import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;

import java.net.URI;
import java.net.URL;

/**
 * User: qingwei
 * Date: 14/11/13
 */
public class SmallMoleculeTest {
    private Metadata getMetadata() throws Exception {
        MZTabDescription tabDescription = new MZTabDescription(MZTabDescription.Mode.Summary, MZTabDescription.Type.Identification);
        tabDescription.setId("PRIDE_1234");
        Metadata mtd = new Metadata(tabDescription);

        mtd.setTitle("My first test experiment");
        mtd.setDescription("An experiment investigating the effects of Il-6.");

        mtd.addSampleProcessingParam(1, new CVParam("SEP", "SEP:00173", "SDS PAGE", null));
        mtd.addSampleProcessingParam(2, new CVParam("SEP", "SEP:00142", "enzyme digestion", null));
        mtd.addSampleProcessingParam(2, new CVParam("MS", "MS:1001251", "Trypsin", null));

        mtd.addInstrumentName(1, new CVParam("MS", "MS:100049", "LTQ Orbitrap", null));
        mtd.addInstrumentName(2, new CVParam("MS", "MS:1000031", "Instrument model", "name of the instrument not included in the CV"));
        mtd.addInstrumentSource(1, new CVParam("MS", "MS:1000073", "ESI", null));
        mtd.addInstrumentSource(2, new CVParam("MS", "MS:1000598", "ETD", null));
        mtd.addInstrumentAnalyzer(1, new CVParam("MS", "MS:1000291", "linear ion trap", null));
        mtd.addInstrumentAnalyzer(2, new CVParam("MS", "MS:1000484", "orbitrap", null));
        mtd.addInstrumentDetector(1, new CVParam("MS", "MS:1000253", "electron multiplier", null));
        mtd.addInstrumentDetector(2, new CVParam("MS", "MS:1000348", "focal plane collector", null));

        mtd.addSoftwareParam(1, new CVParam("MS", "MS:1001207", "Mascot", "2.3"));
        mtd.addSoftwareParam(2, new CVParam("MS", "MS:1001561", "Scaffold", "1.0"));
        mtd.addSoftwareSetting(1, "Fragment tolerance = 0.1Da");
        mtd.addSoftwareSetting(1, "Parent tolerance = 0.5Da");

        mtd.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001364", "pep:global FDR", "0.01"));
        mtd.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001214", "pep:global FDR", "0.08"));

        mtd.addPublicationItem(1, PublicationItem.Type.PUBMED, "21063943");
        mtd.addPublicationItem(1, PublicationItem.Type.DOI, "10.1007/978-1-60761-987-1_6");
        mtd.addPublicationItem(2, PublicationItem.Type.PUBMED, "20615486");
        mtd.addPublicationItem(2, PublicationItem.Type.DOI, "10.1016/j.jprot.2010.06.008");

        mtd.addContactName(1, "James D. Watson");
        mtd.addContactName(2, "Francis Crick");
        mtd.addContactAffiliation(1, "Cambridge University, UK");
        mtd.addContactAffiliation(2, "Cambridge University, UK");
        mtd.addContactEmail(1, "watson@cam.ac.uk");
        mtd.addContactEmail(2, "crick@cam.ac.uk");

        mtd.addUri(new URI("http://www.ebi.ac.uk/pride/url/to/experiment"));
        mtd.addUri(new URI("http://proteomecentral.proteomexchange.org/cgi/GetDataset"));

        mtd.addFixedModParam(1, new CVParam("UNIMOD", "UNIMOD:4", "Carbamidomethyl", null));
        mtd.addFixedModSite(1, "M");
        mtd.addFixedModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));
        mtd.addFixedModSite(2, "N-term");
        mtd.addFixedModParam(3, new CVParam("UNIMOD", "UNIMOD:1", "Acetyl", null));
        mtd.addFixedModPosition(3, "Protein C-term");

        mtd.addVariableModParam(1, new CVParam("UNIMOD", "UNIMOD:21", "Phospho", null));
        mtd.addVariableModSite(1, "M");
        mtd.addVariableModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));
        mtd.addVariableModSite(2, "N-term");
        mtd.addVariableModParam(3, new CVParam("UNIMOD", "UNIMOD:1", "Acetyl", null));
        mtd.addVariableModPosition(3, "Protein C-term");

        mtd.setQuantificationMethod(new CVParam("MS", "MS:1001837", "iTRAQ quantitation analysis", null));
        mtd.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        mtd.setPeptideQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        mtd.setSmallMoleculeQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));

        mtd.addMsRunFormat(1, new CVParam("MS", "MS:1000584", "mzML file", null));
        mtd.addMsRunFormat(2, new CVParam("MS", "MS:1001062", "Mascot MGF file", null));
        mtd.addMsRunLocation(1, new URL("file://C:\\path\\to\\my\\file"));
        mtd.addMsRunLocation(2, new URL("ftp://ftp.ebi.ac.uk/path/to/file"));
        mtd.addMsRunIdFormat(1, new CVParam("MS", "MS:1001530", "mzML unique identifier", null));
        mtd.addMsRunFragmentationMethod(1, new CVParam("MS", "MS:1000133", "CID", null));
        mtd.addMsRunFragmentationMethod(2, new CVParam("MS", "MS:1000422", "HCD", null));

        mtd.addCustom(new UserParam("MS operator", "Florian"));

        mtd.addSampleSpecies(1, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        mtd.addSampleSpecies(1, new CVParam("NEWT", "573824", "Human rhinovirus 1", null));
        mtd.addSampleSpecies(2, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        mtd.addSampleSpecies(2, new CVParam("NEWT", "12130", "Human rhinovirus 2", null));
        mtd.addSampleTissue(1, new CVParam("BTO", "BTO:0000759", "liver", null));
        mtd.addSampleCellType(1, new CVParam("CL", "CL:0000182", "hepatocyte", null));
        mtd.addSampleDisease(1, new CVParam("DOID", "DOID:684", "hepatocellular carcinoma", null));
        mtd.addSampleDisease(1, new CVParam("DOID", "DOID:9451", "alcoholic fatty liver", null));
        mtd.addSampleDescription(1, "Hepatocellular carcinoma samples.");
        mtd.addSampleDescription(2, "Healthy control samples.");
        mtd.addSampleCustom(1, new UserParam("Extraction date", "2011-12-21"));
        mtd.addSampleCustom(1, new UserParam("Extraction reason", "liver biopsy"));

        Sample sample1 = mtd.getSampleMap().get(1);
        Sample sample2 = mtd.getSampleMap().get(2);
        mtd.addAssayQuantificationReagent(1, new CVParam("PRIDE", "PRIDE:0000114", "iTRAQ reagent", "114"));
        mtd.addAssayQuantificationReagent(2, new CVParam("PRIDE", "PRIDE:0000115", "iTRAQ reagent", "115"));
        mtd.addAssayQuantificationReagent(1, new CVParam("PRIDE", "MS:1002038", "unlabeled sample", null));
        mtd.addAssaySample(1, sample1);
        mtd.addAssaySample(2, sample2);

        mtd.addAssayQuantificationModParam(2, 1, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        mtd.addAssayQuantificationModParam(2, 2, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        mtd.addAssayQuantificationModSite(2, 1, "R");
        mtd.addAssayQuantificationModSite(2, 2, "K");
        mtd.addAssayQuantificationModPosition(2, 1, "Anywhere");
        mtd.addAssayQuantificationModPosition(2, 2, "Anywhere");

        MsRun msRun1 = mtd.getMsRunMap().get(1);
        mtd.addAssayMsRun(1, msRun1);

        Assay assay1 = mtd.getAssayMap().get(1);
        Assay assay2 = mtd.getAssayMap().get(2);
        mtd.addStudyVariableAssay(1, assay1);
        mtd.addStudyVariableAssay(1, assay2);

        mtd.addStudyVariableSample(1, sample1);
        mtd.addStudyVariableDescription(1, "description Group B (spike-in 0.74 fmol/uL)");

        mtd.addCVLabel(1, "MS");
        mtd.addCVFullName(1, "MS");
        mtd.addCVVersion(1, "3.54.0");
        mtd.addCVURL(1, "http://psidev.cvs.sourceforge.net/viewvc/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo");

        mtd.addProteinColUnit(ProteinColumn.RELIABILITY, new CVParam("MS", "MS:00001231", "PeptideProphet:Score", null));

        MZTabColumnFactory peptideFactory = MZTabColumnFactory.getInstance(Section.Peptide);
        PeptideColumn peptideColumn = (PeptideColumn) peptideFactory.findColumnByHeader("retention_time");
        mtd.addPeptideColUnit(peptideColumn, new CVParam("UO", "UO:0000031", "minute", null));

        mtd.addPSMColUnit(PSMColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));
        mtd.addSmallMoleculeColUnit(SmallMoleculeColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));

        return mtd;
    }

    private MZTabColumnFactory getSMH(Metadata metadata) {
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Small_Molecule);

        // add optional columns which have stable order.
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();

        // add optional columns which have stable order.
        factory.addOptionalColumn(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, metadata.getMsRunMap().get(1));

        // add abundance columns which locate the end of table.
        factory.addAbundanceOptionalColumn(metadata.getAssayMap().get(1));
        factory.addAbundanceOptionalColumn(metadata.getStudyVariableMap().get(1));
        factory.addAbundanceOptionalColumn(metadata.getAssayMap().get(2));

        // add user defined optional columns
        factory.addOptionalColumn(metadata.getMsRunMap().get(1), "my_value", String.class);
        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);

        return factory;
    }

    private SmallMolecule getRecord(Metadata metadata, MZTabColumnFactory factory) {
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

//        reference factory.addOptionalColumn(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, metadata.getMsRunMap().get(1));
        sm.setSearchEngineScore(metadata.getMsRunMap().get(1), "[MS,MS:1001171,Mascot score,50]|[MS,MS:1001155,Sequest:xcorr,2]");

//      reference factory.addAbundanceOptionalColumn(metadata.getAssayMap().get(1));
        sm.setAbundanceColumn(metadata.getAssayMap().get(1), "12.3");


//      reference factory.addOptionalColumn(metadata.getMsRunMap().get(1), "my_value", String.class);
        sm.setOptionColumn(metadata.getMsRunMap().get(1), "my_value", "Tom");

        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
//      reference factory.addOptionalColumn(param, String.class);
        sm.setOptionColumn(param, "111");

        return sm;
    }

    @Test
    public void testTabFile() throws Exception {
        Metadata metadata = getMetadata();
        MZTabColumnFactory factory = getSMH(metadata);
        SmallMolecule record = getRecord(metadata, factory);

        MZTabFile tabFile = new MZTabFile(metadata);
        tabFile.setSmallMoleculeColumnFactory(factory);
        tabFile.addSmallMolecule(record);

        tabFile.printMZTab(System.out);
    }
}
