package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 11/02/13
 */
public class MTDLineParserTest {
    private MTDLineParser parser;
    private Metadata metadata;
    private MZTabErrorList errorList;

    @Before
    public void setUp() throws Exception {
        parser = new MTDLineParser();
        metadata = parser.getMetadata();
        errorList = new MZTabErrorList();
    }

    @Test
    public void testUnitParser() throws Exception {
        parser.parse(1, "MTD\tmzTab-version\t1.0 rc5", errorList);
        assertTrue(metadata.getTabDescription().getVersion().equals("1.0 rc5"));

        parser.parse(1, "MTD\tmzTab-mode\tComplete", errorList);
        assertTrue(metadata.getTabDescription().getMode() == MZTabDescription.Mode.Complete);

        parser.parse(1, "MTD\tmzTab-mode\tSummary", errorList);
        assertTrue(metadata.getTabDescription().getMode() == MZTabDescription.Mode.Summary);

        parser.parse(1, "MTD\tmzTab-type\tQuantification", errorList);
        assertTrue(metadata.getTabDescription().getType() == MZTabDescription.Type.Quantification);

        parser.parse(1, "MTD\tmzTab-type\tIdentification", errorList);
        assertTrue(metadata.getTabDescription().getType() == MZTabDescription.Type.Identification);

        parser.parse(1, "MTD\tmzTab-ID\tPRIDE_1234", errorList);
        assertTrue(metadata.getTabDescription().getId().equals("PRIDE_1234"));

        parser.parse(1, "MTD\ttitle\tmzTab iTRAQ test", errorList);
        assertTrue(metadata.getTitle().contains("mzTab iTRAQ test"));

        parser.parse(1, "MTD\tdescription\tAn experiment investigating the effects of Il-6.", errorList);
        assertTrue(metadata.getDescription().contains("An experiment investigating the effects of Il-6."));

        parser.parse(1, "MTD\tsample_processing[1]\t[SEP, SEP:00173, SDS PAGE, ]", errorList);
        Param param = metadata.getSampleProcessingMap().get(1).get(0);
        assertTrue(param instanceof CVParam);
        CVParam cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("SDS PAGE"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));

        parser.parse(1, "MTD\tsample_processing[12]\t[SEP, SEP:00142, enzyme digestion, ]|[MS, MS:1001251, Trypsin, ]", errorList);
        assertTrue(metadata.getSampleProcessingMap().size() == 2);
        param = metadata.getSampleProcessingMap().get(12).get(0);
        assertTrue(param instanceof CVParam);
        cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("enzyme digestion"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));
        param = metadata.getSampleProcessingMap().get(12).get(1);
        assertTrue(param instanceof CVParam);
        cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("Trypsin"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));

        parser.parse(1, "MTD\tinstrument[1]-name\t[MS, MS:100049, LTQ Orbitrap, ]", errorList);
        parser.parse(1, "MTD\tinstrument[1]-analyzer\t[MS, MS:1000291, linear ion trap, ]", errorList);
        parser.parse(1, "MTD\tinstrument[2]-source\t[MS, MS:1000598, ETD, ]", errorList);
        parser.parse(1, "MTD\tinstrument[13]-detector\t[MS, MS:1000253, electron multiplier, ]", errorList);
        param = metadata.getInstrumentMap().get(1).getName();
        assertTrue(param.toString().contains("LTQ Orbitrap"));
        param = metadata.getInstrumentMap().get(1).getAnalyzer();
        assertTrue(param.toString().contains("linear ion trap"));
        param = metadata.getInstrumentMap().get(2).getSource();
        assertTrue(param.toString().contains("ETD"));
        param = metadata.getInstrumentMap().get(13).getDetector();
        assertTrue(param.toString().contains("electron multiplier"));

        parser.parse(1, "MTD\tsoftware[11]\t[MS, MS:1001207, Mascot, 2.3]", errorList);
        parser.parse(1, "MTD\tsoftware[2]-setting[1]\tFragment tolerance = 0.1Da", errorList);
        parser.parse(1, "MTD\tsoftware[2]-setting[2]\tParent tolerance = 0.5Da", errorList);
        param = metadata.getSoftwareMap().get(11).getParam();
        assertTrue(param.toString().contains("Mascot"));
        List<String> settingList = metadata.getSoftwareMap().get(2).getSettingList();
        assertTrue(settingList.size() == 2);

        parser.parse(1, "MTD\tsearch_engine_score[1]\t[MS, MS:1001171, Mascot:score,]", errorList);
        parser.parse(1, "MTD\tsearch_engine_score[2]\t[MS, MS:1001330, X!Tandem:expect,]", errorList);
        parser.parse(1, "MTD\tsearch_engine_score[3]\t[MS, MS:1001331, X!Tandem:hyperscore,]", errorList);
        assertTrue(metadata.getSearchEngineScoreMap().size() == 3);
        param = metadata.getSearchEngineScoreMap().get(2).getParam();
        assertTrue(param.toString().contains("expect"));

        parser.parse(1, "MTD\tfalse_discovery_rate\t[MS, MS:1234, pep-fdr, 0.5]|[MS, MS:1001364, pep:global FDR, 0.01]|[MS, MS:1001214, pep:global FDR, 0.08]", errorList);
        assertTrue(metadata.getFalseDiscoveryRate().size() == 3);

        parser.parse(1, "MTD\tpublication[1]\tpubmed:21063943|doi:10.1007/978-1-60761-987-1_6", errorList);
        parser.parse(1, "MTD\tpublication[12]\tpubmed:20615486|doi:10.1016/j.jprot.2010.06.008", errorList);
        assertTrue(metadata.getPublicationMap().size() == 2);

        parser.parse(1, "MTD\tcontact[11]-name\tJames D. Watson", errorList);
        parser.parse(1, "MTD\tcontact[11]-affiliation\tCambridge University, UK", errorList);
        parser.parse(1, "MTD\tcontact[11]-email\twatson@cam.ac.uk", errorList);
        parser.parse(1, "MTD\tcontact[2]-affiliation\tCambridge University, UK", errorList);
        parser.parse(1, "MTD\tcontact[2]-email\tcrick@cam.ac.uk", errorList);
        assertTrue(metadata.getContactMap().size() == 2);

        parser.parse(1, "MTD\turi\thttp://www.ebi.ac.uk/pride/url/to/experiment", errorList);
        parser.parse(1, "MTD\turi\thttp://proteomecentral.proteomexchange.org/cgi/GetDataset", errorList);
        assertTrue(metadata.getUriList().size() == 2);

        parser.parse(1, "MTD\tfixed_mod[11]\t[UNIMOD, UNIMOD:4, Carbamidomethyl, ]", errorList);
        parser.parse(1, "MTD\tfixed_mod[11]-site\tM", errorList);
        parser.parse(1, "MTD\tfixed_mod[2]\t[UNIMOD, UNIMOD:35, Oxidation, ]", errorList);
        parser.parse(1, "MTD\tfixed_mod[2]-site\tN-term", errorList);
        parser.parse(1, "MTD\tfixed_mod[3]\t[UNIMOD, UNIMOD:1, Acetyl, ]", errorList);
        parser.parse(1, "MTD\tfixed_mod[3]-position\tProtein C-term", errorList);
        assertTrue(metadata.getFixedModMap().size() == 3);
        assertTrue(metadata.getFixedModMap().get(11).getSite().equals("M"));
        assertTrue(metadata.getVariableModMap().size() == 0);

        parser.parse(1, "MTD\tquantification_method\t[MS, MS:1001837, iTraq, ]", errorList);
        assertTrue(metadata.getQuantificationMethod() != null);

        parser.parse(1, "MTD\tprotein-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]", errorList);
        parser.parse(1, "MTD\tpeptide-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]", errorList);
        parser.parse(1, "MTD\tsmall_molecule-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]", errorList);
        assertTrue(metadata.getProteinQuantificationUnit() != null);
        assertTrue(metadata.getPeptideQuantificationUnit() != null);
        assertTrue(metadata.getSmallMoleculeQuantificationUnit() != null);

        parser.parse(1, "MTD\tcustom\t[, , MS operator, Florian]", errorList);
        assertTrue(metadata.getCustomList().size() == 1);

        parser.parse(1, "MTD\tcv[1]-label\tMS", errorList);
        parser.parse(1, "MTD\tcv[12]-full_name\tMS", errorList);
        parser.parse(1, "MTD\tcv[1]-version\t3.54.0", errorList);
        parser.parse(1, "MTD\tcv[12]-url\thttp://psidev.cvs.sourceforge.net/viewvc/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo", errorList);
        assertTrue(metadata.getCvMap().size() == 2);
        assertTrue(metadata.getCvMap().get(1).getVersion().equals("3.54.0"));
        assertTrue(metadata.getCvMap().get(12).getUrl().equals("http://psidev.cvs.sourceforge.net/viewvc/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
    }

    @Test
    public void testColUnit() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.parse(1, "MTD\tcolunit-peptide\tretention_time=[UO, UO:0000031, minute, ]", errorList);
        parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Peptide_Header));
        assertTrue(metadata.getPeptideColUnitList().size() == 1);

        parser.parse(1, "MTD\tcolunit-psm\tretention_time=[UO, UO:0000031, minute, ]", errorList);
        parser.refineColUnit(MZTabColumnFactory.getInstance(Section.PSM_Header));
        assertTrue(metadata.getPsmColUnitList().size() == 1);

        parser.parse(1, "MTD\tcolunit-small_molecule\tretention_time=[UO, UO:0000031, minute, ]", errorList);
        parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Small_Molecule_Header));
        assertTrue(metadata.getSmallMoleculeColUnitList().size() == 1);

        MZTabColumnFactory proteinFactory = MZTabColumnFactory.getInstance(Section.Protein_Header);
        MsRun msRun1 = new MsRun(1);
        proteinFactory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);
        parser.parse(1, "MTD\tcolunit-protein\tnum_peptides_unique_ms_run[1] = [EFO, EFO:0004374, milligram per deciliter, ]", errorList);
        parser.refineColUnit(proteinFactory);
        assertTrue(metadata.getProteinColUnitList().size() == 1);
    }

    @Test
    public void testMsRun() throws Exception {
        parser.parse(1, "MTD\tms_run[1]-format\t[MS, MS:1000584, mzML file, ]", errorList);
        parser.parse(1, "MTD\tms_run[2]-location\tfile://C:/path/to/my/file", errorList);
        parser.parse(1, "MTD\tms_run[2]-id_format\t[MS, MS:1000774, multiple peak list, nativeID format]", errorList);
        parser.parse(1, "MTD\tms_run[2]-fragmentation_method\t[MS, MS:1000133, CID, ]", errorList);
        parser.parse(1, "MTD\tms_run[3]-location\tftp://ftp.ebi.ac.uk/path/to/file", errorList);
        assertTrue(metadata.getMsRunMap().size() == 3);
        MsRun msRun2 = metadata.getMsRunMap().get(2);
        assertTrue(msRun2.getLocation().toString().equals("file://C:/path/to/my/file"));
        assertTrue(msRun2.getFragmentationMethod().getAccession().equals("MS:1000133"));

        parser.parse(1, "MTD\tms_run[2]-hash\tde9f2c7fd25e1b3afad3e85a0bd17d9b100db4b3", errorList);
        parser.parse(1, "MTD\tms_run[2]-hash_method\t[MS, MS: MS:1000569, SHA-1, ]", errorList);
        assertTrue(msRun2.getHash().equals("de9f2c7fd25e1b3afad3e85a0bd17d9b100db4b3"));
        assertTrue(msRun2.getHashMethod().getName().equals("SHA-1"));
    }

    @Test
    public void testSample() throws Exception {
        parser.parse(1, " MTD\tsample[1]-species[1]\t[NEWT, 9606, Homo sapien (Human), ]", errorList);
        parser.parse(1, " MTD\tsample[1]-species[2]\t[NEWT, 573824, Human rhinovirus 1, ]", errorList);
        Sample sample1 = metadata.getSampleMap().get(1);
        assertTrue(sample1.getSpeciesList().size() == 2);

        parser.parse(1, "MTD\tsample[1]-tissue[1]\t[BTO, BTO:0000759, liver, ]", errorList);
        assertTrue(sample1.getTissueList().size() == 1);

        parser.parse(1, " MTD\tsample[1]-cell_type[1]\t[CL, CL:0000182, hepatocyte, ]", errorList);
        assertTrue(sample1.getCellTypeList().size() == 1);

        parser.parse(1, " MTD\tsample[1]-disease[1]\t[DOID, DOID:684, hepatocellular carcinoma, ]", errorList);
        parser.parse(1, " MTD\tsample[1]-disease[2]\t[DOID, DOID:9451, alcoholic fatty liver, ]", errorList);
        assertTrue(sample1.getDiseaseList().size() == 2);

        parser.parse(1, " MTD \t sample[1]-description \t  Hepatocellular carcinoma samples.", errorList);
        parser.parse(1, " MTD \t sample[2]-description \t  Healthy control samples.", errorList);
        assertTrue(sample1.getDescription().contains("Hepatocellular carcinoma samples."));
        Sample sample2 = metadata.getSampleMap().get(2);
        assertTrue(sample2.getDescription().contains("Healthy control samples."));

        parser.parse(1, "MTD\tsample[1]-custom[1]\t[,,Extraction date, 2011-12-21]", errorList);
        parser.parse(1, "MTD\tsample[1]-custom[2]\t[,,Extraction reason, liver biopsy]", errorList);
        assertTrue(sample1.getCustomList().size() == 2);
    }

    @Test
    public void testAssay() throws Exception {
        parser.parse(1, "MTD\tassay[1]-quantification_reagent\t[PRIDE,PRIDE:0000114,iTRAQ reagent,114]", errorList);
        parser.parse(1, "MTD\tassay[2]-quantification_reagent\t[PRIDE,PRIDE:0000115,iTRAQ reagent,115]", errorList);
        assertTrue(metadata.getAssayMap().size() == 2);
        assertTrue(metadata.getAssayMap().get(1).getQuantificationReagent().getAccession().equals("PRIDE:0000114"));

        Sample sample1 = new Sample(1);
        Sample sample2 = new Sample(2);
        metadata.addSample(sample1);
        metadata.addSample(sample2);
        parser.parse(1, "MTD\tassay[1]-sample_ref\tsample[1]", errorList);
        parser.parse(1, "MTD\tassay[2]-sample_ref\tsample[2]", errorList);
        assertTrue(metadata.getAssayMap().get(1).getSample().equals(sample1));
        assertTrue(metadata.getAssayMap().get(2).getSample().equals(sample2));

        MsRun msRun1 = new MsRun(1);
        MsRun msRun2 = new MsRun(2);
        metadata.addMsRun(msRun1);
        metadata.addMsRun(msRun2);
        parser.parse(1, "MTD\tassay[1]-ms_run_ref\tms_run[1]", errorList);
        parser.parse(1, "MTD\tassay[2]-ms_run_ref\tms_run[2]", errorList);
        assertTrue(metadata.getAssayMap().get(1).getMsRun().equals(msRun1));
        assertTrue(metadata.getAssayMap().get(2).getMsRun().equals(msRun2));

        parser.parse(1, "MTD\tassay[2]-quantification_mod[1]\t[UNIMOD, UNIMOD:188, Label:13C(6), ]", errorList);
        parser.parse(1, "MTD\tassay[2]-quantification_mod[1]-site\tR", errorList);
        parser.parse(1, "MTD\tassay[2]-quantification_mod[1]-position\tAnywhere", errorList);
        parser.parse(1, "MTD\tassay[2]-quantification_mod[2]\t[UNIMOD, UNIMOD:188, Label:13C(6), ]", errorList);
        parser.parse(1, "MTD\tassay[2]-quantification_mod[2]-site\tK", errorList);
        parser.parse(1, "MTD\tassay[2]-quantification_mod[2]-position\tAnywhere", errorList);
        assertTrue(metadata.getAssayMap().get(2).getQuantificationModMap().size() == 2);
        AssayQuantificationMod quantificationMod = metadata.getAssayMap().get(2).getQuantificationModMap().get(1);
        assertTrue(quantificationMod.getSite().equals("R"));
        quantificationMod = metadata.getAssayMap().get(2).getQuantificationModMap().get(2);
        assertTrue(quantificationMod.getPosition().equals("Anywhere"));
    }

    @Test
    public void testStudyVariable() throws Exception {
        parser.parse(1, "MTD\tstudy_variable[1]-description\tGroup B (spike-in 0,74 fmol/uL)", errorList);
        assertTrue(metadata.getStudyVariableMap().size() == 1);
        assertTrue(metadata.getStudyVariableMap().get(1).getDescription().equals("Group B (spike-in 0,74 fmol/uL)"));

        Sample sample1 = new Sample(1);
        Sample sample2 = new Sample(2);
        metadata.addSample(sample1);
        metadata.addSample(sample2);
        parser.parse(1, "MTD\tstudy_variable[1]-sample_refs\tsample[1],sample[2]", errorList);
        assertTrue(metadata.getStudyVariableMap().get(1).getSampleMap().size() == 2);
        assertTrue(metadata.getStudyVariableMap().get(1).getSampleMap().get(2) == sample2);

        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        metadata.addAssay(assay1);
        metadata.addAssay(assay2);
        parser.parse(1, "MTD\tstudy_variable[2]-assay_refs\tassay[1], assay[2]", errorList);
        assertTrue(metadata.getStudyVariableMap().get(2).getAssayMap().size() == 2);
        assertTrue(metadata.getStudyVariableMap().get(2).getAssayMap().get(1) == assay1);
    }


    public Metadata parseMetadata(String mtdFile) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(mtdFile));
        MTDLineParser parser = new MTDLineParser();

        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.trim().length() == 0) {
                continue;
            }

            parser.parse(lineNumber, line, errorList);
        }

        reader.close();

        parser.refineNormalMetadata();
        return parser.getMetadata();
    }

    @Test
    public void testCreateMetadata() throws Exception {
        parseMetadata("testset/mtdFile.txt");
    }
}
