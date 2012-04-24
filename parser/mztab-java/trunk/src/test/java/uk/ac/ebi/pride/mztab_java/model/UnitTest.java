package uk.ac.ebi.pride.mztab_java.model;

import junit.framework.TestCase;
import uk.ac.ebi.pride.mztab_java.MzTabFile;
import uk.ac.ebi.pride.mztab_java.MzTabParsingException;

public class UnitTest extends TestCase {
    private Unit unit = new Unit();

    private static final String mzTabString = "MTD\tPRIDE_1234-title\tmzTab iTRAQ" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-sample_processing[1]\t[MS,MS:1234,first step,]|[MS,MS:1235,describing first step,]" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-contact[1]-name\tJohannes" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-contact[2]-name\tMathias" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-ms_file[1]-format\t[MS,MS:1000584,mzML file,]" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-ms_file[1]-location\t/tmp/somefile.xml" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-ms_file[1]-id_format\t[MS,MS:1001530,mzML unique identifier,]" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-sub[1]-description\tHealthy human liver" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-sub[1]-quantification_reagent\t[PRIDE,PRIDE:0000114,iTRAQ reagent 114,]" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-sub[2]-description\tHuman hepatocellular carcinoma" + MzTabFile.EOL +
            "MTD\tPRIDE_1234-sub[2]-quantification_reagent\t[PRIDE,PRIDE:0000115,iTRAQ reagent 115,]" + MzTabFile.EOL;

    public void testUnmarshall() {
        try {
            unit.unmarshall(mzTabString);

            assertEquals("PRIDE_1234", unit.getUnitId());
            assertEquals("mzTab iTRAQ", unit.getTitle());
            assertEquals(2, unit.getContact().size());
            assertEquals("Johannes", unit.getContact().get(0).getName());
            assertEquals("Mathias", unit.getContact().get(1).getName());
            assertEquals(2, unit.getSubsamples().size());
            assertEquals("Healthy human liver", unit.getSubsamples().get(0).getDescription());
            assertEquals("Human hepatocellular carcinoma", unit.getSubsamples().get(1).getDescription());
            assertEquals("[PRIDE,PRIDE:0000114,iTRAQ reagent 114,]", unit.getSubsamples().get(0).getQuantificationReagent().toString());
            assertEquals("[PRIDE,PRIDE:0000115,iTRAQ reagent 115,]", unit.getSubsamples().get(1).getQuantificationReagent().toString());

            assertEquals(1, unit.getSampleProcessing().size());
            assertEquals(2, unit.getSampleProcessing().get(0).size());
            
            MsFile msFile = unit.getMsFile(1);
            
            assertNotNull(msFile);
            assertEquals("/tmp/somefile.xml", msFile.getLocation());
            assertEquals("MS:1000584", msFile.getFormat().getAccession());

            assertEquals(mzTabString, unit.toMzTab());

        } catch (MzTabParsingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
