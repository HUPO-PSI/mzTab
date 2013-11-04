package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.Test;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
* User: Qingwei
* Date: 11/02/13
*/
public class MTDLineParserTest {
    @Test
    public void testUnitParser() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.check(1, "MTD\tPRIDE_1234-title\tmzTab iTRAQ test");
        Unit pride1234 = metadata.getUnit("PRIDE_1234");
        assertTrue(pride1234 != null);
        assertTrue(pride1234.getTitle().contains("mzTab iTRAQ test"));

        parser.check(1, "MTD\tPRIDE_1234-description\tThis is a PRIDE test.");
        assertTrue(pride1234.getDescription().contains("This is a PRIDE test."));

        parser.check(1, "MTD\tPRIDE_1234-sample_processing[1]\t[SEP, SEP:00173, SDS PAGE, ]");
        Param param = pride1234.getSampleProcessingMap().get(1).get(0);
        assertTrue(param instanceof CVParam);
        CVParam cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("SDS PAGE"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));

        parser.check(1, "MTD\tPRIDE_1234-sample_processing[2]\t[SEP, SEP:00142, enzyme digestion, ]|[MS, MS:1001251, Trypsin, ]");
        assertTrue(pride1234.getSampleProcessingMap().size() == 2);
        param = pride1234.getSampleProcessingMap().get(2).get(0);
        assertTrue(param instanceof CVParam);
        cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("enzyme digestion"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));
        param = pride1234.getSampleProcessingMap().get(2).get(1);
        assertTrue(param instanceof CVParam);
        cvParam = (CVParam) param;
        assertTrue(cvParam.getName().contains("Trypsin"));
        assertTrue(MZTabUtils.isEmpty(cvParam.getValue()));

        parser.check(1, "MTD\tPRIDE_1234-instrument[1]-name\t[MS, MS:100049, LTQ Orbitrap, ]");
        parser.check(1, "MTD\tPRIDE_1234-instrument[1]-analyzer\t[MS, MS:1000291, linear ion trap, ]");
        parser.check(1, "MTD\tPRIDE_1234-instrument[2]-source\t[MS, MS:1000598, ETD, ]");
        parser.check(1, "MTD\tPRIDE_1234-instrument[3]-detector\t[MS, MS:1000253, electron multiplier, ]");
        param = pride1234.getInstrumentMap().get(1).getName();
        assertTrue(param.toString().contains("LTQ Orbitrap"));
        param = pride1234.getInstrumentMap().get(1).getAnalyzer();
        assertTrue(param.toString().contains("linear ion trap"));
        param = pride1234.getInstrumentMap().get(2).getSource();
        assertTrue(param.toString().contains("ETD"));
        param = pride1234.getInstrumentMap().get(3).getDetector();
        assertTrue(param.toString().contains("electron multiplier"));

        parser.check(1, "MTD\tPRIDE_1234-software[1]\t[MS, MS:1001207, Mascot, 2.3]");
        parser.check(1, "MTD\tPRIDE_1234-software[2]-setting\tFragment tolerance = 0.1Da");
        parser.check(1, "MTD\tPRIDE_1234-software[2]-setting\tParent tolerance = 0.5Da");
        param = pride1234.getSoftwareMap().get(1).getParam();
        assertTrue(param.toString().contains("Mascot"));
        List<String> settingList = pride1234.getSoftwareMap().get(2).getSettingList();
        assertTrue(settingList.size() == 2);

        parser.check(1, "MTD\tPRIDE_1234-false_discovery_rate\t[MS, MS:1234, pep-fdr, 0.5]|[MS, MS:1001364, pep:global FDR, 0.01]|[MS, MS:1001214, pep:global FDR, 0.08]");
        assertTrue(pride1234.getFalseDiscoveryRate().size() == 3);

        parser.check(1, "MTD\tPRIDE_1234-publication[1]\tpubmed:21063943|doi:10.1007/978-1-60761-987-1_6");
        parser.check(1, "MTD\tPRIDE_1234-publication[2]\tpubmed:20615486|doi:10.1016/j.jprot.2010.06.008");
        assertTrue(pride1234.getPublicationMap().size() == 2);

        parser.check(1, "MTD\tPRIDE_1234-contact[1]-name\tJames D. Watson");
        parser.check(1, "MTD\tPRIDE_1234-contact[1]-affiliation\tCambridge University, UK");
        parser.check(1, "MTD\tPRIDE_1234-contact[1]-email\twatson@cam.ac.uk");
        parser.check(1, "MTD\tPRIDE_1234-contact[2]-affiliation\tCambridge University, UK");
        parser.check(1, "MTD\tPRIDE_1234-contact[2]-email\tcrick@cam.ac.uk");
        assertTrue(pride1234.getContactMap().size() == 2);

        parser.check(1, "MTD\tPRIDE_1234-uri\thttp://www.ebi.ac.uk/pride/url/to/experiment");
        parser.check(1, "MTD\tPRIDE_1234-uri\thttp://proteomecentral.proteomexchange.org/cgi/GetDataset");
        assertTrue(pride1234.getUriList().size() == 2);

        parser.check(1, "MTD\tPRIDE_1234-mod\t[MOD, MOD:00397, iodoacetamide derivatized residue, ]|[MOD, MOD:00675, oxidized residue, ]");
        parser.check(1, "MTD\tPRIDE_1234-quantification_method\t[MS, MS:1001837, iTraq, ]");
        assertTrue(pride1234.getMod().size() == 2);
        assertTrue(pride1234.getQuantificationMethod() != null);

        parser.check(1, "MTD\tPRIDE_1234-protein-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]");
        parser.check(1, "MTD\tPRIDE_1234-peptide-quantification_unit\t[PRIDE, PRIDE:0000395, Ratio, ]");
        assertTrue(pride1234.getProteinQuantificationUnit() != null);
        assertTrue(pride1234.getPeptideQuantificationUnit() != null);

        parser.check(1, "MTD\tPRIDE_1234-ms_file[1]-format\t[MS, MS:1000584, mzML file, ]");
        parser.check(1, "MTD\tPRIDE_1234-ms_file[2]-location\tfile://C:/path/to/my/file");
        parser.check(1, "MTD\tPRIDE_1234-ms_file[2]-id_format\t[MS, MS:1000774, multiple peak list, nativeID format]");
        parser.check(1, "MTD\tPRIDE_1234-ms_file[3]-location\tftp://ftp.ebi.ac.uk/path/to/file");
        assertTrue(pride1234.getMsFileMap().size() == 3);

        parser.check(1, "MTD\tPRIDE_1234-custom\t[, , MS operator, Florian]");
        assertTrue(pride1234.getCustomList().size() == 1);

        parser.check(1, "MTD\tPRIDE_1234-colunit-protein \t reliability=[MS, MS:00001231, PeptideProphet:Score, ]");
        parser.check(1, "MTD\tPRIDE_1234-colunit-peptide \t retention_time=[UO, UO:0000031, minute, ]");
        parser.check(1, "MTD\tPRIDE_1234-colunit-small_molecule \t retention_time=[UO, UO:0000031, minute, ]");
        assertTrue(pride1234.getProteinColUnitList().size() == 1);
        assertTrue(pride1234.getPeptideColUnitList().size() == 1);
        assertTrue(pride1234.getSmallMoleculeColUnitList().size() == 1);
    }

    @Test
    public void testSubUnitParser() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.check(1, " MTD\tPRIDE_1234-sub[1]-species[1]\t[NEWT, 9606, Homo sapien (Human), ]");
        parser.check(1, " MTD\tPRIDE_1234-sub[1]-species[2]\t[NEWT, 573824, Human rhinovirus 1, ]");
        SubUnit sub1 = (SubUnit) metadata.getUnit("PRIDE_1234-sub[1]");
        assertTrue(sub1.getSubId() == 1);
        assertTrue(sub1.getSpeciesMap().size() == 2);

        parser.check(1, "MTD\tPRIDE_1234-sub[1]-tissue[1]\t[BTO, BTO:0000759, liver, ]");
        assertTrue(sub1.getTissueMap().size() == 1);

        parser.check(1, " MTD\tPRIDE_1234-sub[1]-cell_type[1]\t[CL, CL:0000182, hepatocyte, ]");
        assertTrue(sub1.getCellTypeMap().size() == 1);

        parser.check(1, " MTD\tPRIDE_1234-sub[1]-disease[1]\t[DOID, DOID:684, hepatocellular carcinoma, ]");
        parser.check(1, " MTD\tPRIDE_1234-sub[1]-disease[2]\t[DOID, DOID:9451, alcoholic fatty liver, ]");
        assertTrue(sub1.getDiseaseMap().size() == 2);

        parser.check(1, " MTD \t PRIDE_1234-sub[1]-description \t  Hepatocellular carcinoma samples.");
        parser.check(1, " MTD \t PRIDE_1234-sub[2]-description \t  Healthy control samples.");
        assertTrue(sub1.getDescription().contains("Hepatocellular carcinoma samples."));
        SubUnit sub2 = (SubUnit) metadata.getUnit("PRIDE_1234-sub[2]");
        assertTrue(sub2.getDescription().contains("Healthy control samples."));

        parser.check(1, "MTD\tPRIDE_1234-sub[1]-quantification_reagent\t[PRIDE,PRIDE:0000114,iTRAQ reagent,114]");
        parser.check(1, "MTD\tPRIDE_1234-sub[2]-quantification_reagent\t[PRIDE,PRIDE:0000115,iTRAQ reagent,115]");
        assertTrue(sub1.getQuantificationReagent() != null);
        assertTrue(sub2.getQuantificationReagent() != null);

        parser.check(1, "MTD\tPRIDE_1234-sub[1]-custom\t[,,Extraction date, 2011-12-21]");
        parser.check(1, "MTD\tPRIDE_1234-sub[1]-custom\t[,,Extraction reason, liver biopsy]");
        assertTrue(sub1.getCustomList().size() == 2);
    }

    @Test
    public void testSubUnitWithoutSubID() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.check(1, "MTD\tPRIDE_1234-species[1]\t[NEWT, 9606, Homo sapiens (Human), ]");
        parser.check(1, "MTD\tPRIDE_1234-species[2]\t[NEWT, 12059, Rhinovirus, ]");
        SubUnit sub = (SubUnit) metadata.getUnit("PRIDE_1234-sub");
        assertTrue(sub.getSpeciesMap().size() == 2);

        parser.check(1, "MTD\tPRIDE_1234-tissue[1]\t[BTO, BTO:0000759, liver, ]");
        assertTrue(sub.getTissueMap().size() == 1);

        parser.check(1, "MTD\tPRIDE_1234-cell_type[1]\t[CL, CL:0000182, hepatocyte, ]");
        assertTrue(sub.getCellTypeMap().size() == 1);

        parser.check(1, "MTD\tPRIDE_1234-disease[1]\t[DOID, DOID:684, hepatocellular carcinoma, ]");
        parser.check(1, "MTD\tPRIDE_1234-disease[2]\t[DOID, DOID:9451, alcoholic fatty liver, ]");
        assertTrue(sub.getDiseaseMap().size() == 2);

        try {
            parser.check(1, "MTD\tPRIDE_1234-sub[1]-disease[1]\t[DOID, DOID:684, hepatocellular carcinoma, ]");
            fail();
        } catch (MZTabException e) {
            assertTrue(e.getError().getType() == LogicalErrorType.Duplication);
        }
    }

    @Test
    public void testReplicateUnitParser() throws Exception {
        MTDLineParser parser = new MTDLineParser();
        Metadata metadata = parser.getMetadata();

        parser.check(1, "MTD\tPRIDE_1234-rep[1]\tReplicate 1 of experiment 1.");
        ReplicateUnit repUnit = (ReplicateUnit) metadata.getUnit("PRIDE_1234-rep[1]");
        assertTrue(repUnit.getComment().equals("Replicate 1 of experiment 1."));
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

            parser.check(lineNumber, line);
        }

        reader.close();

        return parser.getMetadata();
    }

    @Test
    public void testCreateMetadata() throws Exception {
        parseMetadata("testset/mtdFile.txt");
    }
}
