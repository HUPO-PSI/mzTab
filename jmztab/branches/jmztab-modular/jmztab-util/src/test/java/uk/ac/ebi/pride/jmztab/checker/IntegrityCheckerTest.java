package uk.ac.ebi.pride.jmztab.checker;

import org.junit.Before;
import uk.ac.ebi.pride.jmztab.model.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author ntoro
 * @since 03/08/2014 14:06
 */
public class IntegrityCheckerTest {

    Metadata metadataSI;
    Metadata metadataCI;
    Metadata metadataInvalidCQ;

    @Before
    public void setUp() throws Exception {
        metadataSI = createSummaryIdentificationMetadata();
        metadataCI = createCompleteIdentificationMetadata();
        metadataInvalidCQ = createInvalidCompleteQuantificationMetadata();
    }

    private Metadata createSummaryIdentificationMetadata() throws URISyntaxException, MalformedURLException {

        Metadata metadata = new Metadata();

        metadata.setMZTabID("PRIDE_1234");
        metadata.setMZTabType(MZTabDescription.Type.Identification);
        metadata.setMZTabMode(MZTabDescription.Mode.Summary);

        metadata.setTitle("My first test experiment");
        metadata.setDescription("An experiment investigating the effects of Il-6.");

        metadata.addSampleProcessingParam(1, new CVParam("SEP", "SEP:00173", "SDS PAGE", null));
        metadata.addSampleProcessingParam(2, new CVParam("SEP", "SEP:00142", "enzyme digestion", null));
        metadata.addSampleProcessingParam(2, new CVParam("MS", "MS:1001251", "Trypsin", null));

        metadata.addInstrumentName(1, new CVParam("MS", "MS:100049", "LTQ Orbitrap", null));
        metadata.addInstrumentName(2, new CVParam("MS", "MS:1000031", "Instrument model", "name of the instrument not included in the CV"));
        metadata.addInstrumentSource(1, new CVParam("MS", "MS:1000073", "ESI", null));
        metadata.addInstrumentSource(2, new CVParam("MS", "MS:1000598", "ETD", null));
        metadata.addInstrumentAnalyzer(1, new CVParam("MS", "MS:1000291", "linear ion trap", null));
        metadata.addInstrumentAnalyzer(2, new CVParam("MS", "MS:1000484", "orbitrap", null));
        metadata.addInstrumentDetector(1, new CVParam("MS", "MS:1000253", "electron multiplier", null));
        metadata.addInstrumentDetector(2, new CVParam("MS", "MS:1000348", "focal plane collector", null));

        metadata.addSoftwareParam(1, new CVParam("MS", "MS:1001207", "Mascot", "2.3"));
        metadata.addSoftwareParam(2, new CVParam("MS", "MS:1001561", "Scaffold", "1.0"));
        metadata.addSoftwareSetting(1, "Fragment tolerance = 0.1Da");
        metadata.addSoftwareSetting(1, "Parent tolerance = 0.5Da");

        metadata.addProteinSearchEngineScoreParam(1, new CVParam("MS", "MS:1001171", "Mascot:score", null));
        metadata.addPeptideSearchEngineScoreParam(1, new CVParam("MS", "MS:1001153", "search engine specific score", null));
        metadata.addSmallMoleculeSearchEngineScoreParam(1, new CVParam("MS", "MS:1001420", "SpectraST:delta", null));

        metadata.addPsmSearchEngineScoreParam(1, new CVParam("MS", "MS:1001330", "X!Tandem:expect", null));
        metadata.addPsmSearchEngineScoreParam(2, new CVParam("MS", "MS:1001331", "X!Tandem:hyperscore", null));

        metadata.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001364", "pep:global FDR", "0.01"));
        metadata.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001214", "pep:global FDR", "0.08"));

        metadata.addPublicationItem(1, PublicationItem.Type.PUBMED, "21063943");
        metadata.addPublicationItem(1, PublicationItem.Type.DOI, "10.1007/978-1-60761-987-1_6");
        metadata.addPublicationItem(2, PublicationItem.Type.PUBMED, "20615486");
        metadata.addPublicationItem(2, PublicationItem.Type.DOI, "10.1016/j.jprot.2010.06.008");

        metadata.addContactName(1, "James D. Watson");
        metadata.addContactName(2, "Francis Crick");
        metadata.addContactAffiliation(1, "Cambridge University, UK");
        metadata.addContactAffiliation(2, "Cambridge University, UK");
        metadata.addContactEmail(1, "watson@cam.ac.uk");
        metadata.addContactEmail(2, "crick@cam.ac.uk");

        metadata.addUri(new URI("http://www.ebi.ac.uk/pride/url/to/experiment"));
        metadata.addUri(new URI("http://proteomecentral.proteomexchange.org/cgi/GetDataset"));

        metadata.addFixedModParam(1, new CVParam("UNIMOD", "UNIMOD:4", "Carbamidomethyl", null));
        metadata.addFixedModSite(1, "M");
        metadata.addFixedModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));
        metadata.addFixedModSite(2, "N-term");
        metadata.addFixedModParam(3, new CVParam("UNIMOD", "UNIMOD:1", "Acetyl", null));
        metadata.addFixedModPosition(3, "Protein C-term");

        metadata.addVariableModParam(1, new CVParam("UNIMOD", "UNIMOD:21", "Phospho", null));
        metadata.addVariableModSite(1, "M");
        metadata.addVariableModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));
        metadata.addVariableModSite(2, "N-term");
        metadata.addVariableModParam(3, new CVParam("UNIMOD", "UNIMOD:1", "Acetyl", null));
        metadata.addVariableModPosition(3, "Protein C-term");

        metadata.setQuantificationMethod(new CVParam("MS", "MS:1001837", "iTRAQ quantitation analysis", null));
        metadata.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        metadata.setPeptideQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        metadata.setSmallMoleculeQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));

        metadata.addMsRunFormat(1, new CVParam("MS", "MS:1000584", "mzML file", null));
        metadata.addMsRunFormat(2, new CVParam("MS", "MS:1001062", "Mascot MGF file", null));
        metadata.addMsRunLocation(1, new URL("file://ftp.ebi.ac.uk/path/to/file"));
        metadata.addMsRunLocation(2, new URL("ftp://ftp.ebi.ac.uk/path/to/file"));
        metadata.addMsRunIdFormat(1, new CVParam("MS", "MS:1001530", "mzML unique identifier", null));
        metadata.addMsRunFragmentationMethod(1, new CVParam("MS", "MS:1000133", "CID", null));
        metadata.addMsRunHash(2, "de9f2c7fd25e1b3afad3e85a0bd17d9b100db4b3");
        metadata.addMsRunHashMethod(2, new CVParam("MS", "MS:1000569", "SHA-1", null));
//        mtd.addMsRunFragmentationMethod(2, new CVParam("MS", "MS:1000422", "HCD", null));

        metadata.addCustom(new UserParam("MS operator", "Florian"));

        metadata.addSampleSpecies(1, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        metadata.addSampleSpecies(1, new CVParam("NEWT", "573824", "Human rhinovirus 1", null));
        metadata.addSampleSpecies(2, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        metadata.addSampleSpecies(2, new CVParam("NEWT", "12130", "Human rhinovirus 2", null));
        metadata.addSampleTissue(1, new CVParam("BTO", "BTO:0000759", "liver", null));
        metadata.addSampleCellType(1, new CVParam("CL", "CL:0000182", "hepatocyte", null));
        metadata.addSampleDisease(1, new CVParam("DOID", "DOID:684", "hepatocellular carcinoma", null));
        metadata.addSampleDisease(1, new CVParam("DOID", "DOID:9451", "alcoholic fatty liver", null));
        metadata.addSampleDescription(1, "Hepatocellular carcinoma samples.");
        metadata.addSampleDescription(2, "Healthy control samples.");
        metadata.addSampleCustom(1, new UserParam("Extraction date", "2011-12-21"));
        metadata.addSampleCustom(1, new UserParam("Extraction reason", "liver biopsy"));

        Sample sample1 = metadata.getSampleMap().get(1);
        Sample sample2 = metadata.getSampleMap().get(2);
        metadata.addAssayQuantificationReagent(1, new CVParam("PRIDE", "PRIDE:0000114", "iTRAQ reagent", "114"));
        metadata.addAssayQuantificationReagent(2, new CVParam("PRIDE", "PRIDE:0000115", "iTRAQ reagent", "115"));
        metadata.addAssayQuantificationReagent(1, new CVParam("PRIDE", "MS:1002038", "unlabeled sample", null));
        metadata.addAssaySample(1, sample1);
        metadata.addAssaySample(2, sample2);

        metadata.addAssayQuantificationModParam(2, 1, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        metadata.addAssayQuantificationModParam(2, 2, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        metadata.addAssayQuantificationModSite(2, 1, "R");
        metadata.addAssayQuantificationModSite(2, 2, "K");
        metadata.addAssayQuantificationModPosition(2, 1, "Anywhere");
        metadata.addAssayQuantificationModPosition(2, 2, "Anywhere");

        MsRun msRun1 = metadata.getMsRunMap().get(1);
        metadata.addAssayMsRun(1, msRun1);

        Assay assay1 = metadata.getAssayMap().get(1);
        Assay assay2 = metadata.getAssayMap().get(2);

        metadata.addStudyVariableAssay(1, assay1);
        metadata.addStudyVariableAssay(1, assay2);
        metadata.addStudyVariableSample(1, sample1);
        metadata.addStudyVariableDescription(1, "description Group B (spike-in 0.74 fmol/uL)");
        metadata.addStudyVariableAssay(2, assay1);
        metadata.addStudyVariableAssay(2, assay2);
        metadata.addStudyVariableSample(2, sample1);
        metadata.addStudyVariableDescription(2, "description Group B (spike-in 0.74 fmol/uL)");

        metadata.addCVLabel(1, "MS");
        metadata.addCVFullName(1, "MS");
        metadata.addCVVersion(1, "3.54.0");
        metadata.addCVURL(1, "http://psidev.cvs.sourceforge.net/viewvc/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo");

        metadata.addProteinColUnit(ProteinColumn.RELIABILITY, new CVParam("MS", "MS:00001231", "PeptideProphet:Score", null));

        MZTabColumnFactory peptideFactory = MZTabColumnFactory.getInstance(Section.Peptide);
        PeptideColumn peptideColumn = (PeptideColumn) peptideFactory.findColumnByHeader("retention_time");
        metadata.addPeptideColUnit(peptideColumn, new CVParam("UO", "UO:0000031", "minute", null));

        metadata.addPSMColUnit(PSMColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));
        metadata.addSmallMoleculeColUnit(SmallMoleculeColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));

        return metadata;

    }

    private Metadata createCompleteIdentificationMetadata() throws URISyntaxException, MalformedURLException {

        Metadata metadata = new Metadata();

        metadata.setMZTabID("PRIDE_1234");
        metadata.setMZTabType(MZTabDescription.Type.Identification);
        metadata.setMZTabMode(MZTabDescription.Mode.Complete);

        metadata.setTitle("My first test experiment");
        metadata.setDescription("An experiment investigating the effects of Il-6.");

        metadata.addSampleProcessingParam(1, new CVParam("SEP", "SEP:00173", "SDS PAGE", null));
        metadata.addSampleProcessingParam(2, new CVParam("SEP", "SEP:00142", "enzyme digestion", null));
        metadata.addSampleProcessingParam(2, new CVParam("MS", "MS:1001251", "Trypsin", null));

        metadata.addInstrumentName(1, new CVParam("MS", "MS:100049", "LTQ Orbitrap", null));
        metadata.addInstrumentName(2, new CVParam("MS", "MS:1000031", "Instrument model", "name of the instrument not included in the CV"));
        metadata.addInstrumentSource(1, new CVParam("MS", "MS:1000073", "ESI", null));
        metadata.addInstrumentSource(2, new CVParam("MS", "MS:1000598", "ETD", null));
        metadata.addInstrumentAnalyzer(1, new CVParam("MS", "MS:1000291", "linear ion trap", null));
        metadata.addInstrumentAnalyzer(2, new CVParam("MS", "MS:1000484", "orbitrap", null));
        metadata.addInstrumentDetector(1, new CVParam("MS", "MS:1000253", "electron multiplier", null));
        metadata.addInstrumentDetector(2, new CVParam("MS", "MS:1000348", "focal plane collector", null));

        metadata.addSoftwareParam(1, new CVParam("MS", "MS:1001207", "Mascot", "2.3"));
        metadata.addSoftwareParam(2, new CVParam("MS", "MS:1001561", "Scaffold", "1.0"));
        metadata.addSoftwareSetting(1, "Fragment tolerance = 0.1Da");
        metadata.addSoftwareSetting(1, "Parent tolerance = 0.5Da");

        metadata.addProteinSearchEngineScoreParam(1, new CVParam("MS", "MS:1001171", "Mascot:score", null));
        metadata.addPeptideSearchEngineScoreParam(1, new CVParam("MS", "MS:1001153", "search engine specific score", null));
        metadata.addSmallMoleculeSearchEngineScoreParam(1, new CVParam("MS", "MS:1001420", "SpectraST:delta", null));

        metadata.addPsmSearchEngineScoreParam(1, new CVParam("MS", "MS:1001330", "X!Tandem:expect", null));
        metadata.addPsmSearchEngineScoreParam(2, new CVParam("MS", "MS:1001331", "X!Tandem:hyperscore", null));

        metadata.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001364", "pep:global FDR", "0.01"));
        metadata.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001214", "pep:global FDR", "0.08"));

        metadata.addPublicationItem(1, PublicationItem.Type.PUBMED, "21063943");
        metadata.addPublicationItem(1, PublicationItem.Type.DOI, "10.1007/978-1-60761-987-1_6");
        metadata.addPublicationItem(2, PublicationItem.Type.PUBMED, "20615486");
        metadata.addPublicationItem(2, PublicationItem.Type.DOI, "10.1016/j.jprot.2010.06.008");

        metadata.addContactName(1, "James D. Watson");
        metadata.addContactName(2, "Francis Crick");
        metadata.addContactAffiliation(1, "Cambridge University, UK");
        metadata.addContactAffiliation(2, "Cambridge University, UK");
        metadata.addContactEmail(1, "watson@cam.ac.uk");
        metadata.addContactEmail(2, "crick@cam.ac.uk");

        metadata.addUri(new URI("http://www.ebi.ac.uk/pride/url/to/experiment"));
        metadata.addUri(new URI("http://proteomecentral.proteomexchange.org/cgi/GetDataset"));

        metadata.addFixedModParam(1, new CVParam("UNIMOD", "UNIMOD:4", "Carbamidomethyl", null));
        metadata.addFixedModSite(1, "M");
        metadata.addFixedModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));
        metadata.addFixedModSite(2, "N-term");
        metadata.addFixedModParam(3, new CVParam("UNIMOD", "UNIMOD:1", "Acetyl", null));
        metadata.addFixedModPosition(3, "Protein C-term");

        metadata.addVariableModParam(1, new CVParam("UNIMOD", "UNIMOD:21", "Phospho", null));
        metadata.addVariableModSite(1, "M");
        metadata.addVariableModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));
        metadata.addVariableModSite(2, "N-term");
        metadata.addVariableModParam(3, new CVParam("UNIMOD", "UNIMOD:1", "Acetyl", null));
        metadata.addVariableModPosition(3, "Protein C-term");

        metadata.setQuantificationMethod(new CVParam("MS", "MS:1001837", "iTRAQ quantitation analysis", null));
        metadata.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        metadata.setPeptideQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        metadata.setSmallMoleculeQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));

        metadata.addMsRunFormat(1, new CVParam("MS", "MS:1000584", "mzML file", null));
        metadata.addMsRunFormat(2, new CVParam("MS", "MS:1001062", "Mascot MGF file", null));
        metadata.addMsRunLocation(1, new URL("file://ftp.ebi.ac.uk/path/to/file"));
        metadata.addMsRunLocation(2, new URL("ftp://ftp.ebi.ac.uk/path/to/file"));
        metadata.addMsRunIdFormat(1, new CVParam("MS", "MS:1001530", "mzML unique identifier", null));
        metadata.addMsRunFragmentationMethod(1, new CVParam("MS", "MS:1000133", "CID", null));
        metadata.addMsRunHash(2, "de9f2c7fd25e1b3afad3e85a0bd17d9b100db4b3");
        metadata.addMsRunHashMethod(2, new CVParam("MS", "MS:1000569", "SHA-1", null));
//        mtd.addMsRunFragmentationMethod(2, new CVParam("MS", "MS:1000422", "HCD", null));

        metadata.addCustom(new UserParam("MS operator", "Florian"));

        metadata.addSampleSpecies(1, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        metadata.addSampleSpecies(1, new CVParam("NEWT", "573824", "Human rhinovirus 1", null));
        metadata.addSampleSpecies(2, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        metadata.addSampleSpecies(2, new CVParam("NEWT", "12130", "Human rhinovirus 2", null));
        metadata.addSampleTissue(1, new CVParam("BTO", "BTO:0000759", "liver", null));
        metadata.addSampleCellType(1, new CVParam("CL", "CL:0000182", "hepatocyte", null));
        metadata.addSampleDisease(1, new CVParam("DOID", "DOID:684", "hepatocellular carcinoma", null));
        metadata.addSampleDisease(1, new CVParam("DOID", "DOID:9451", "alcoholic fatty liver", null));
        metadata.addSampleDescription(1, "Hepatocellular carcinoma samples.");
        metadata.addSampleDescription(2, "Healthy control samples.");
        metadata.addSampleCustom(1, new UserParam("Extraction date", "2011-12-21"));
        metadata.addSampleCustom(1, new UserParam("Extraction reason", "liver biopsy"));

        Sample sample1 = metadata.getSampleMap().get(1);
        Sample sample2 = metadata.getSampleMap().get(2);
        metadata.addAssayQuantificationReagent(1, new CVParam("PRIDE", "PRIDE:0000114", "iTRAQ reagent", "114"));
        metadata.addAssayQuantificationReagent(2, new CVParam("PRIDE", "PRIDE:0000115", "iTRAQ reagent", "115"));
        metadata.addAssayQuantificationReagent(1, new CVParam("PRIDE", "MS:1002038", "unlabeled sample", null));
        metadata.addAssaySample(1, sample1);
        metadata.addAssaySample(2, sample2);

        metadata.addAssayQuantificationModParam(2, 1, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        metadata.addAssayQuantificationModParam(2, 2, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        metadata.addAssayQuantificationModSite(2, 1, "R");
        metadata.addAssayQuantificationModSite(2, 2, "K");
        metadata.addAssayQuantificationModPosition(2, 1, "Anywhere");
        metadata.addAssayQuantificationModPosition(2, 2, "Anywhere");

        MsRun msRun1 = metadata.getMsRunMap().get(1);
        metadata.addAssayMsRun(1, msRun1);

        Assay assay1 = metadata.getAssayMap().get(1);
        Assay assay2 = metadata.getAssayMap().get(2);

        metadata.addStudyVariableAssay(1, assay1);
        metadata.addStudyVariableAssay(1, assay2);
        metadata.addStudyVariableSample(1, sample1);
        metadata.addStudyVariableDescription(1, "description Group B (spike-in 0.74 fmol/uL)");
        metadata.addStudyVariableAssay(2, assay1);
        metadata.addStudyVariableAssay(2, assay2);
        metadata.addStudyVariableSample(2, sample1);
        metadata.addStudyVariableDescription(2, "description Group B (spike-in 0.74 fmol/uL)");

        metadata.addCVLabel(1, "MS");
        metadata.addCVFullName(1, "MS");
        metadata.addCVVersion(1, "3.54.0");
        metadata.addCVURL(1, "http://psidev.cvs.sourceforge.net/viewvc/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo");

        metadata.addProteinColUnit(ProteinColumn.RELIABILITY, new CVParam("MS", "MS:00001231", "PeptideProphet:Score", null));

        MZTabColumnFactory peptideFactory = MZTabColumnFactory.getInstance(Section.Peptide);
        PeptideColumn peptideColumn = (PeptideColumn) peptideFactory.findColumnByHeader("retention_time");
        metadata.addPeptideColUnit(peptideColumn, new CVParam("UO", "UO:0000031", "minute", null));

        metadata.addPSMColUnit(PSMColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));
        metadata.addSmallMoleculeColUnit(SmallMoleculeColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));

        return metadata;

    }

    private Metadata createInvalidCompleteQuantificationMetadata() throws URISyntaxException, MalformedURLException {
        //Invalid metadata
        Metadata metadata = new Metadata();

        metadata.setMZTabID("PRIDE_1234");
        metadata.setMZTabMode(MZTabDescription.Mode.Complete);
        metadata.setMZTabType(MZTabDescription.Type.Quantification);

        metadata.setTitle("My first test experiment");
        metadata.setDescription("An experiment investigating the effects of Il-6.");

        metadata.addSampleProcessingParam(1, new CVParam("SEP", "SEP:00173", "SDS PAGE", null));
        metadata.addSampleProcessingParam(2, new CVParam("SEP", "SEP:00142", "enzyme digestion", null));
        metadata.addSampleProcessingParam(2, new CVParam("MS", "MS:1001251", "Trypsin", null));

        metadata.addInstrumentName(1, new CVParam("MS", "MS:100049", "LTQ Orbitrap", null));
        metadata.addInstrumentName(2, new CVParam("MS", "MS:1000031", "Instrument model", "name of the instrument not included in the CV"));
        metadata.addInstrumentSource(1, new CVParam("MS", "MS:1000073", "ESI", null));
        metadata.addInstrumentSource(2, new CVParam("MS", "MS:1000598", "ETD", null));
        metadata.addInstrumentAnalyzer(1, new CVParam("MS", "MS:1000291", "linear ion trap", null));
        metadata.addInstrumentAnalyzer(2, new CVParam("MS", "MS:1000484", "orbitrap", null));
        metadata.addInstrumentDetector(1, new CVParam("MS", "MS:1000253", "electron multiplier", null));
        metadata.addInstrumentDetector(2, new CVParam("MS", "MS:1000348", "focal plane collector", null));

//        metadata.addSoftwareParam(1, new CVParam("MS", "MS:1001207", "Mascot", "2.3"));
//        metadata.addSoftwareParam(2, new CVParam("MS", "MS:1001561", "Scaffold", "1.0"));
//        metadata.addSoftwareSetting(1, "Fragment tolerance = 0.1Da");
//        metadata.addSoftwareSetting(1, "Parent tolerance = 0.5Da");

        metadata.addProteinSearchEngineScoreParam(1, new CVParam("MS", "MS:1001171", "Mascot:score", null));
        metadata.addPeptideSearchEngineScoreParam(1, new CVParam("MS", "MS:1001153", "search engine specific score", null));
        metadata.addSmallMoleculeSearchEngineScoreParam(1, new CVParam("MS", "MS:1001420", "SpectraST:delta", null));

        metadata.addPsmSearchEngineScoreParam(1, new CVParam("MS", "MS:1001330", "X!Tandem:expect", null));
        metadata.addPsmSearchEngineScoreParam(2, new CVParam("MS", "MS:1001331", "X!Tandem:hyperscore", null));

        metadata.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001364", "pep:global FDR", "0.01"));
        metadata.addFalseDiscoveryRateParam(new CVParam("MS", "MS:1001214", "pep:global FDR", "0.08"));

        metadata.addPublicationItem(1, PublicationItem.Type.PUBMED, "21063943");
        metadata.addPublicationItem(1, PublicationItem.Type.DOI, "10.1007/978-1-60761-987-1_6");
        metadata.addPublicationItem(2, PublicationItem.Type.PUBMED, "20615486");
        metadata.addPublicationItem(2, PublicationItem.Type.DOI, "10.1016/j.jprot.2010.06.008");

        metadata.addContactName(1, "James D. Watson");
        metadata.addContactName(2, "Francis Crick");
        metadata.addContactAffiliation(1, "Cambridge University, UK");
        metadata.addContactAffiliation(2, "Cambridge University, UK");
        metadata.addContactEmail(1, "watson@cam.ac.uk");
        metadata.addContactEmail(2, "crick@cam.ac.uk");

        metadata.addUri(new URI("http://www.ebi.ac.uk/pride/url/to/experiment"));
        metadata.addUri(new URI("http://proteomecentral.proteomexchange.org/cgi/GetDataset"));

//        metadata.addFixedModParam(1, new CVParam("UNIMOD", "UNIMOD:4", "Carbamidomethyl", null));
//        metadata.addFixedModSite(1, "M");
//        metadata.addFixedModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));
//        metadata.addFixedModSite(2, "N-term");
//        metadata.addFixedModParam(3, new CVParam("UNIMOD", "UNIMOD:1", "Acetyl", null));
//        metadata.addFixedModPosition(3, "Protein C-term");
//
//        metadata.addVariableModParam(1, new CVParam("UNIMOD", "UNIMOD:21", "Phospho", null));
//        metadata.addVariableModSite(1, "M");
//        metadata.addVariableModParam(2, new CVParam("UNIMOD", "UNIMOD:35", "Oxidation", null));
//        metadata.addVariableModSite(2, "N-term");
//        metadata.addVariableModParam(3, new CVParam("UNIMOD", "UNIMOD:1", "Acetyl", null));
//        metadata.addVariableModPosition(3, "Protein C-term");

//        metadata.setQuantificationMethod(new CVParam("MS", "MS:1001837", "iTRAQ quantitation analysis", null));
//        metadata.setProteinQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        metadata.setPeptideQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));
        metadata.setSmallMoleculeQuantificationUnit(new CVParam("PRIDE", "PRIDE:0000395", "Ratio", null));

        metadata.addMsRunFormat(1, new CVParam("MS", "MS:1000584", "mzML file", null));
        metadata.addMsRunFormat(2, new CVParam("MS", "MS:1001062", "Mascot MGF file", null));
        metadata.addMsRunLocation(1, new URL("file://ftp.ebi.ac.uk/path/to/file"));
        metadata.addMsRunLocation(2, new URL("ftp://ftp.ebi.ac.uk/path/to/file"));
        metadata.addMsRunIdFormat(1, new CVParam("MS", "MS:1001530", "mzML unique identifier", null));
        metadata.addMsRunFragmentationMethod(1, new CVParam("MS", "MS:1000133", "CID", null));
        metadata.addMsRunHash(2, "de9f2c7fd25e1b3afad3e85a0bd17d9b100db4b3");
        metadata.addMsRunHashMethod(2, new CVParam("MS", "MS:1000569", "SHA-1", null));
//        mtd.addMsRunFragmentationMethod(2, new CVParam("MS", "MS:1000422", "HCD", null));

        metadata.addCustom(new UserParam("MS operator", "Florian"));

        metadata.addSampleSpecies(1, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        metadata.addSampleSpecies(1, new CVParam("NEWT", "573824", "Human rhinovirus 1", null));
        metadata.addSampleSpecies(2, new CVParam("NEWT", "9606", "Homo sapiens (Human)", null));
        metadata.addSampleSpecies(2, new CVParam("NEWT", "12130", "Human rhinovirus 2", null));
        metadata.addSampleTissue(1, new CVParam("BTO", "BTO:0000759", "liver", null));
        metadata.addSampleCellType(1, new CVParam("CL", "CL:0000182", "hepatocyte", null));
        metadata.addSampleDisease(1, new CVParam("DOID", "DOID:684", "hepatocellular carcinoma", null));
        metadata.addSampleDisease(1, new CVParam("DOID", "DOID:9451", "alcoholic fatty liver", null));
        metadata.addSampleDescription(1, "Hepatocellular carcinoma samples.");
        metadata.addSampleDescription(2, "Healthy control samples.");
        metadata.addSampleCustom(1, new UserParam("Extraction date", "2011-12-21"));
        metadata.addSampleCustom(1, new UserParam("Extraction reason", "liver biopsy"));

        Sample sample1 = metadata.getSampleMap().get(1);
        Sample sample2 = metadata.getSampleMap().get(2);
        metadata.addAssayQuantificationReagent(1, new CVParam("PRIDE", "PRIDE:0000114", "iTRAQ reagent", "114"));
        metadata.addAssayQuantificationReagent(2, new CVParam("PRIDE", "PRIDE:0000115", "iTRAQ reagent", "115"));
        metadata.addAssayQuantificationReagent(1, new CVParam("PRIDE", "MS:1002038", "unlabeled sample", null));
        metadata.addAssaySample(1, sample1);
        metadata.addAssaySample(2, sample2);

        metadata.addAssayQuantificationModParam(2, 1, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        metadata.addAssayQuantificationModParam(2, 2, new CVParam("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        metadata.addAssayQuantificationModSite(2, 1, "R");
        metadata.addAssayQuantificationModSite(2, 2, "K");
        metadata.addAssayQuantificationModPosition(2, 1, "Anywhere");
        metadata.addAssayQuantificationModPosition(2, 2, "Anywhere");

        MsRun msRun1 = metadata.getMsRunMap().get(1);
        metadata.addAssayMsRun(1, msRun1);

        Assay assay1 = metadata.getAssayMap().get(1);
        Assay assay2 = metadata.getAssayMap().get(2);

        metadata.addStudyVariableAssay(1, assay1);
        metadata.addStudyVariableAssay(1, assay2);
        metadata.addStudyVariableSample(1, sample1);
        metadata.addStudyVariableDescription(1, "description Group B (spike-in 0.74 fmol/uL)");
        metadata.addStudyVariableAssay(2, assay1);
        metadata.addStudyVariableAssay(2, assay2);
        metadata.addStudyVariableSample(2, sample1);
        metadata.addStudyVariableDescription(2, "description Group B (spike-in 0.74 fmol/uL)");

        metadata.addCVLabel(1, "MS");
        metadata.addCVFullName(1, "MS");
        metadata.addCVVersion(1, "3.54.0");
        metadata.addCVURL(1, "http://psidev.cvs.sourceforge.net/viewvc/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo");

        metadata.addProteinColUnit(ProteinColumn.RELIABILITY, new CVParam("MS", "MS:00001231", "PeptideProphet:Score", null));

        MZTabColumnFactory peptideFactory = MZTabColumnFactory.getInstance(Section.Peptide);
        PeptideColumn peptideColumn = (PeptideColumn) peptideFactory.findColumnByHeader("retention_time");
        metadata.addPeptideColUnit(peptideColumn, new CVParam("UO", "UO:0000031", "minute", null));

        metadata.addPSMColUnit(PSMColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));
        metadata.addSmallMoleculeColUnit(SmallMoleculeColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));

        return metadata;
    }

}
