package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 11/02/13
 */
public class MTDLineParserTest {
    @Test
    public void testUnitParser() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.parse(1, "MTD\tmzTab-version\t1.0 rc4");
        assertTrue(metadata.getTabDescription().getVersion().equals("1.0 rc4"));

        parser.parse(1, "MTD\tmzTab-ID\tPRIDE_1234");
        assertTrue(metadata.getTabDescription().getId().equals("PRIDE_1234"));

        parser.parse(1, "MTD\tmzTab-mode\tComplete");
        assertTrue(metadata.getTabDescription().getMode() == MZTabDescription.Mode.Complete);

        parser.parse(1, "MTD\ttitle\tmzTab iTRAQ test");
        assertTrue(metadata.getTitle().contains("mzTab iTRAQ test"));

        parser.parse(1, "MTD\tdescription\tThis is a PRIDE test.");
        assertTrue(metadata.getDescription().contains("This is a PRIDE test."));

        parser.parse(1, "MTD\tsample_processing[1]\t[SEP, SEP:00173, SDS PAGE, ]");
        Param param = metadata.getSampleProcessingMap().get(1).get(0);
        assertTrue(param instanceof CVParam);
        CVParam cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("SDS PAGE"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));

        parser.parse(1, "MTD\tsample_processing[2]\t[SEP, SEP:00142, enzyme digestion, ]|[MS, MS:1001251, Trypsin, ]");
        assertTrue(metadata.getSampleProcessingMap().size() == 2);
        param = metadata.getSampleProcessingMap().get(2).get(0);
        assertTrue(param instanceof CVParam);
        cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("enzyme digestion"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));
        param = metadata.getSampleProcessingMap().get(2).get(1);
        assertTrue(param instanceof CVParam);
        cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("Trypsin"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));

        parser.parse(1, "MTD\tinstrument[1]-name\t[MS, MS:100049, LTQ Orbitrap, ]");
        parser.parse(1, "MTD\tinstrument[1]-analyzer\t[MS, MS:1000291, linear ion trap, ]");
        parser.parse(1, "MTD\tinstrument[2]-source\t[MS, MS:1000598, ETD, ]");
        parser.parse(1, "MTD\tinstrument[3]-detector\t[MS, MS:1000253, electron multiplier, ]");
        param = metadata.getInstrumentMap().get(1).getName();
        assertTrue(param.toString().contains("LTQ Orbitrap"));
        param = metadata.getInstrumentMap().get(1).getAnalyzer();
        assertTrue(param.toString().contains("linear ion trap"));
        param = metadata.getInstrumentMap().get(2).getSource();
        assertTrue(param.toString().contains("ETD"));
        param = metadata.getInstrumentMap().get(3).getDetector();
        assertTrue(param.toString().contains("electron multiplier"));

        parser.parse(1, "MTD\tsoftware[1]\t[MS, MS:1001207, Mascot, 2.3]");
        parser.parse(1, "MTD\tsoftware[2]-setting\tFragment tolerance = 0.1Da");
        parser.parse(1, "MTD\tsoftware[2]-setting\tParent tolerance = 0.5Da");
        param = metadata.getSoftwareMap().get(1).getParam();
        assertTrue(param.toString().contains("Mascot"));
        List<String> settingList = metadata.getSoftwareMap().get(2).getSettingList();
        assertTrue(settingList.size() == 2);

        parser.parse(1, "MTD\tfalse_discovery_rate\t[MS, MS:1234, pep-fdr, 0.5]|[MS, MS:1001364, pep:global FDR, 0.01]|[MS, MS:1001214, pep:global FDR, 0.08]");
        assertTrue(metadata.getFalseDiscoveryRate().size() == 3);

        parser.parse(1, "MTD\tpublication[1]\tpubmed:21063943|doi:10.1007/978-1-60761-987-1_6");
        parser.parse(1, "MTD\tpublication[2]\tpubmed:20615486|doi:10.1016/j.jprot.2010.06.008");
        assertTrue(metadata.getPublicationMap().size() == 2);

        parser.parse(1, "MTD\tcontact[1]-name\tJames D. Watson");
        parser.parse(1, "MTD\tcontact[1]-affiliation\tCambridge University, UK");
        parser.parse(1, "MTD\tcontact[1]-email\twatson@cam.ac.uk");
        parser.parse(1, "MTD\tcontact[2]-affiliation\tCambridge University, UK");
        parser.parse(1, "MTD\tcontact[2]-email\tcrick@cam.ac.uk");
        assertTrue(metadata.getContactMap().size() == 2);

        parser.parse(1, "MTD\turi\thttp://www.ebi.ac.uk/pride/url/to/experiment");
        parser.parse(1, "MTD\turi\thttp://proteomecentral.proteomexchange.org/cgi/GetDataset");
        assertTrue(metadata.getUriList().size() == 2);

        parser.parse(1, "MTD\tmod\t[MOD, MOD:00397, iodoacetamide derivatized residue, ]|[MOD, MOD:00675, oxidized residue, ]");
        parser.parse(1, "MTD\tquantification_method\t[MS, MS:1001837, iTraq, ]");
        assertTrue(metadata.getMod().size() == 2);
        assertTrue(metadata.getQuantificationMethod() != null);

        parser.parse(1, "MTD\tprotein-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]");
        parser.parse(1, "MTD\tpeptide-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]");
        parser.parse(1, "MTD\tsmall_molecule-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]");
        assertTrue(metadata.getProteinQuantificationUnit() != null);
        assertTrue(metadata.getPeptideQuantificationUnit() != null);
        assertTrue(metadata.getSmallMoleculeQuantificationUnit() != null);

        parser.parse(1, "MTD\tms_file[1]-format\t[MS, MS:1000584, mzML file, ]");
        parser.parse(1, "MTD\tms_file[2]-location\tfile://C:/path/to/my/file");
        parser.parse(1, "MTD\tms_file[2]-id_format\t[MS, MS:1000774, multiple peak list, nativeID format]");
        parser.parse(1, "MTD\tms_file[3]-location\tftp://ftp.ebi.ac.uk/path/to/file");
        assertTrue(metadata.getMsFileMap().size() == 3);

        parser.parse(1, "MTD\tcustom\t[, , MS operator, Florian]");
        assertTrue(metadata.getCustomList().size() == 1);
    }

    @Test
    public void testColUnit() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.parse(1, "MTD\tcolunit-peptide \t retention_time=[UO, UO:0000031, minute, ]");
        parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Peptide_Header));
        assertTrue(metadata.getPeptideColUnitList().size() == 1);

        parser.parse(1, "MTD\tcolunit-small_molecule \t retention_time=[UO, UO:0000031, minute, ]");
        parser.refineColUnit(MZTabColumnFactory.getInstance(Section.Small_Molecule_Header));
        assertTrue(metadata.getSmallMoleculeColUnitList().size() == 1);

        MZTabColumnFactory proteinFactory = MZTabColumnFactory.getInstance(Section.Protein_Header);
        MsFile msFile1 = new MsFile(1);
        proteinFactory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msFile1);
        parser.parse(1, "MTD\tcolunit-protein\tnum_peptides_unique_ms_file[1] = [EFO, EFO:0004374, milligram per deciliter, ]");
        parser.refineColUnit(proteinFactory);
        assertTrue(metadata.getProteinColUnitList().size() == 1);
    }

        @Test
    public void testIndexedElementParser() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.parse(1, " MTD\tsample[1]-species[1]\t[NEWT, 9606, Homo sapien (Human), ]");
        parser.parse(1, " MTD\tsample[1]-species[2]\t[NEWT, 573824, Human rhinovirus 1, ]");
        Sample sample1 = metadata.getSampleMap().get(1);
        assertTrue(sample1.getSpeciesMap().size() == 2);

        parser.parse(1, "MTD\tsample[1]-tissue[1]\t[BTO, BTO:0000759, liver, ]");
        assertTrue(sample1.getTissueMap().size() == 1);

        parser.parse(1, " MTD\tsample[1]-cell_type[1]\t[CL, CL:0000182, hepatocyte, ]");
        assertTrue(sample1.getCellTypeMap().size() == 1);

        parser.parse(1, " MTD\tsample[1]-disease[1]\t[DOID, DOID:684, hepatocellular carcinoma, ]");
        parser.parse(1, " MTD\tsample[1]-disease[2]\t[DOID, DOID:9451, alcoholic fatty liver, ]");
        assertTrue(sample1.getDiseaseMap().size() == 2);

        parser.parse(1, " MTD \t sample[1]-description \t  Hepatocellular carcinoma samples.");
        parser.parse(1, " MTD \t sample[2]-description \t  Healthy control samples.");
        assertTrue(sample1.getDescription().contains("Hepatocellular carcinoma samples."));
        Sample sample2 = metadata.getSampleMap().get(2);
        assertTrue(sample2.getDescription().contains("Healthy control samples."));

        parser.parse(1, "MTD\tsample[1]-custom\t[,,Extraction date, 2011-12-21]");
        parser.parse(1, "MTD\tsample[1]-custom\t[,,Extraction reason, liver biopsy]");
        assertTrue(sample1.getCustomList().size() == 2);
    }

    @Test
    public void testAssay() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.parse(1, "MTD\tassay[1]-quantification_reagent\t[PRIDE,PRIDE:0000114,iTRAQ reagent,114]");
        parser.parse(1, "MTD\tassay[2]-quantification_reagent\t[PRIDE,PRIDE:0000115,iTRAQ reagent,115]");
        assertTrue(metadata.getAssayMap().size() == 2);
        assertTrue(metadata.getAssayMap().get(1).getQuantificationReagent().getAccession().equals("PRIDE:0000114"));

        Sample sample1 = new Sample(1);
        Sample sample2 = new Sample(2);
        metadata.addSample(sample1);
        metadata.addSample(sample2);
        parser.parse(1, "MTD\tassay[1]-sample_ref\tsample[1]");
        parser.parse(1, "MTD\tassay[2]-sample_ref\tsample[2]");
        assertTrue(metadata.getAssayMap().get(1).getSample().equals(sample1));
        assertTrue(metadata.getAssayMap().get(2).getSample().equals(sample2));

        MsFile msFile1 = new MsFile(1);
        MsFile msFile2 = new MsFile(2);
        metadata.addMsFile(msFile1);
        metadata.addMsFile(msFile2);
        parser.parse(1, "MTD\tassay[1]-ms_file_ref\tms_file[1]");
        parser.parse(1, "MTD\tassay[2]-ms_file_ref\tms_file[2]");
        assertTrue(metadata.getAssayMap().get(1).getMsFile().equals(msFile1));
        assertTrue(metadata.getAssayMap().get(2).getMsFile().equals(msFile2));
    }

    @Test
    public void testStudyVariable() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.parse(1, "MTD\tstudy_variable[1]-description\tGroup B (spike-in 0,74 fmol/uL)");
        assertTrue(metadata.getStudyVariableMap().size() == 1);
        assertTrue(metadata.getStudyVariableMap().get(1).getDescription().equals("Group B (spike-in 0,74 fmol/uL)"));

        Sample sample1 = new Sample(1);
        Sample sample2 = new Sample(2);
        metadata.addSample(sample1);
        metadata.addSample(sample2);
        parser.parse(1, "MTD\tstudy_variable[1]-sample_refs\tsample[1],sample[2]");
        assertTrue(metadata.getStudyVariableMap().get(1).getSampleMap().size() == 2);
        assertTrue(metadata.getStudyVariableMap().get(1).getSampleMap().get(2) == sample2);

        Assay assay1 = new Assay(1);
        Assay assay2 = new Assay(2);
        metadata.addAssay(assay1);
        metadata.addAssay(assay2);
        parser.parse(1, "MTD\tstudy_variable[2]-assay_refs\tassay[1], assay[2]");
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

            parser.parse(lineNumber, line);
        }

        reader.close();

        return parser.getMetadata();
    }

    @Test
    public void testCreateMetadata() throws Exception {
        parseMetadata("testset/mtdFile.txt");
    }
}
