package uk.ac.ebi.pride.jmztab.parser;

import org.junit.Test;
import uk.ac.ebi.pride.jmztab.model.*;

import java.math.BigDecimal;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static uk.ac.ebi.pride.jmztab.parser.MZTabParserUtils.*;

/**
 * User: Qingwei
 * Date: 11/02/13
 */
public class MZTabParserUtilsTest {
    @Test
    public void testEmail() throws Exception {
        assertTrue(parseEmail("watson@cam.ac.uk").equals("watson@cam.ac.uk"));
        assertTrue(parseEmail("crick@cam.ac.uk").equals("crick@cam.ac.uk"));

        assertTrue(parseEmail("11@11") == null);
        assertTrue(parseEmail("null") == null);
    }

    @Test
    public void testParam() throws Exception {
        assertTrue(parseParam("[PRIDE,PRIDE:0000114,iTRAQ reagent 114,]") instanceof CVParam);
        assertTrue(parseParam("[, ,tolerance,0.5]") instanceof UserParam);
        assertTrue(parseParam("[, ,,0.5]") == null);
        assertTrue(parseParam("null") == null);
    }

    @Test
    public void testParamList() throws Exception {
        SplitList<Param> paramList;

        StringBuilder sb = new StringBuilder();

        paramList = parseParamList(sb.toString());
        assertTrue(paramList.size() == 0);

        sb.append("[MS,MS:1001207,Mascot,]");
        paramList = parseParamList(sb.toString());
        assertTrue(paramList.size() == 1);
        assertTrue(paramList.get(0) instanceof CVParam);

        sb.append("|[, ,name,0.5]");
        paramList = parseParamList(sb.toString());
        assertTrue(paramList.size() == 2);
        assertTrue(paramList.get(1) instanceof UserParam);

        sb.append("|[MS,MS:1001171,Mascot:score,30]");
        paramList = parseParamList(sb.toString());
        assertTrue(paramList.size() == 3);
        assertTrue(paramList.get(2) instanceof CVParam);

        sb.append("|[, ,,0.5]");
        paramList = parseParamList(sb.toString());
        assertTrue(paramList.size() == 0);

        sb = new StringBuilder();
        sb.append("null");
        paramList = parseParamList(sb.toString());
        assertTrue(paramList.size() == 0);
    }

    @Test
    public void testGOTermList() throws Exception {
        SplitList<String> goList;

        StringBuilder sb = new StringBuilder();
        goList = parseGOTermList(sb.toString());
        assertTrue(goList.size() == 0);

        sb.append("GO:0005515");
        goList = parseGOTermList(sb.toString());
        assertTrue(goList.size() == 1);

        sb.append(",GO:0008270, GO:0043167");
        goList = parseGOTermList(sb.toString());
        assertTrue(goList.size() == 3);

        sb.append(",GO:000, 8270, GO:0043167");
        goList = parseGOTermList(sb.toString());
        assertTrue(goList.size() == 0);
    }

    @Test
    public void testBigDecimalList() throws Exception {
        SplitList<BigDecimal> decimalList;

        StringBuilder sb = new StringBuilder();
        decimalList = parseBigDecimalList(sb.toString());
        assertTrue(decimalList.size() == 0);

        sb.append("2.3");
        decimalList = parseBigDecimalList(sb.toString());
        assertTrue(decimalList.size() == 1);

        sb.append("| 10.2|11.5");
        decimalList = parseBigDecimalList(sb.toString());
        assertTrue(decimalList.size() == 3);

        sb.append("|1|2|3");
        decimalList = parseBigDecimalList(sb.toString());
        assertTrue(decimalList.size() == 6);

        sb.append("1000,100");
        decimalList = parseBigDecimalList(sb.toString());
        assertTrue(decimalList.size() == 0);
    }

    @Test
    public void testPublication() throws Exception {
        Publication publication;

        StringBuilder sb = new StringBuilder();
        publication = parsePublication(sb.toString());
        assertTrue(publication.size() == 0);

        sb.append("pubmed:21063943|doi:10.1007/978-1-60761-987-1_6");
        publication = parsePublication(sb.toString());
        assertTrue(publication.toString().contains("pubmed:21063943|doi:10.1007/978-1-60761-987-1_6"));

        sb.append("| doi:1231-60761-987-1_6");
        publication = parsePublication(sb.toString());
        assertTrue(publication.size() == 3);

        sb.append("|cnki:1231-60761-987-1_6");
        publication = parsePublication(sb.toString());
        assertTrue(publication.size() == 0);
    }

//    @Test
//    public void testOptColumnName() throws Exception {
//        assertTrue(checkOptColumnName("opt_my_value"));
//        assertFalse(checkOptColumnName("op_my_value"));
//        assertFalse(checkOptColumnName("opt_my value"));
//    }
//
//    @Test
//    public void testCVParamOptColumnName() throws Exception {
//        CVParam param;
//
//        param = parseCVParamOptColumnName("opt_cv_MS:1001208_TOM");
//        assertTrue(param.getAccession().equals("MS:1001208"));
//        assertTrue(param.getName().equals("TOM"));
//
//        param = parseCVParamOptColumnName("opt_cv_TOM_MS:1001208_DD");
//        assertTrue(param.getAccession().equals("TOM"));
//        assertTrue(param.getName().equals("MS:1001208_DD"));
//    }

    @Test
    public void testModification() throws Exception {
        Modification modification;

        modification = parseModification(Section.Protein, "3-MOD:00412");
        assertTrue(modification.getPositionMap().containsKey(3));
        assertTrue(modification.getType().name().equals("MOD"));
        assertTrue(modification.getAccession().equals("00412"));

        modification = parseModification(Section.Protein, "3|4-UNIMOD:00412");
        assertTrue(modification.getPositionMap().containsKey(3));
        assertTrue(modification.getPositionMap().containsKey(4));
        assertTrue(modification.getType().name().equals("UNIMOD"));
        assertTrue(modification.getAccession().equals("00412"));

        modification = parseModification(Section.Protein, "CHEMMOD:+159.93");
        assertTrue(modification.getPositionMap().isEmpty());
        assertTrue(modification.getType().name().equals("CHEMMOD"));
        assertTrue(modification.getAccession().equals("+159.93"));

        modification = parseModification(Section.Protein, "3-SUBST:R");
        assertTrue(modification.getPositionMap().containsKey(3));
        assertTrue(modification.getType().name().equals("SUBST"));
        assertTrue(modification.getAccession().equals("R"));

        modification = parseModification(Section.Protein, "3[MS, MS:100xxxx, Probability Score Y, 0.8]|4[MS, MS:100xxxx, Probability Score Y, 0.2]-MOD:00412|[MS, MS:1001524, fragment neutral loss, 63.998285]");
        assertTrue(modification.getPositionMap().containsKey(3));
        assertTrue(modification.getPositionMap().get(3).getValue().contains("0.8"));
        assertTrue(modification.getPositionMap().containsKey(4));
        assertTrue(modification.getPositionMap().get(4).getValue().contains("0.2"));
        assertTrue(modification.getType().name().equals("MOD"));
        assertTrue(modification.getAccession().equals("00412"));
        assertTrue(modification.getNeutralLoss().getName().contains("fragment neutral loss"));

        List<Modification> modList = parseModificationList(Section.Protein, "3|4|8-MOD:00412, 3|4|8-MOD:00412");
        assertTrue(modList.size() == 2);
    }
}
