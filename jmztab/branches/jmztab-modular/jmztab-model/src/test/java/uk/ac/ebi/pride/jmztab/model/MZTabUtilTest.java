package uk.ac.ebi.pride.jmztab.model;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * User: Qingwei
 * Date: 09/07/13
 */
public class MZTabUtilTest {
    private static Logger logger = Logger.getLogger(MZTabUtilTest.class);

    @Test
    public void testParam() throws Exception {
        logger.debug(new UserParam("Source", "Sigma-Aldrich, catalog #H4522, lot #043K0502"));
        logger.debug(new UserParam("Source", "Sigma-Aldrich [catalog #H4522, lot #043K0502]"));
        logger.debug(new UserParam("Source", "Sigma-Aldrich, \"catalog #H4522, lot #043K0502\""));

        assertTrue(parseParam("[PRIDE,PRIDE:0000114,iTRAQ reagent 114,]") instanceof CVParam);
        assertTrue(parseParam("[, ,tolerance,0.5]") instanceof UserParam);
        assertTrue(parseParam("[, ,,0.5]") == null);
        assertTrue(parseParam("null") == null);

        assertTrue(parseParam("[PRIDE,PRIDE:0000114,\"N,O-diacetylated L-serine\",]").getName().contains("N,O-diacetylated L-serine"));
        logger.debug(parseParam("[PRIDE,PRIDE:0000114,\"N[12],O-diacetylated L-serine\",]"));
    }

    @Test
    public void testDouble() throws Exception {
        Double value;
        value = parseDouble("NaN");
        assertTrue(value.equals(Double.NaN));
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
    public void testDoubleList() throws Exception {
        SplitList<Double> valueList;

        StringBuilder sb = new StringBuilder();
        valueList = parseDoubleList(sb.toString());
        assertTrue(valueList.size() == 0);

        sb.append("2.3");
        valueList = parseDoubleList(sb.toString());
        assertTrue(valueList.size() == 1);

        sb.append("| 10.2|11.5");
        valueList = parseDoubleList(sb.toString());
        assertTrue(valueList.size() == 3);

        sb.append("|1|2|3");
        valueList = parseDoubleList(sb.toString());
        assertTrue(valueList.size() == 6);

        sb.append("1000,100");
        valueList = parseDoubleList(sb.toString());
        assertTrue(valueList.size() == 0);
    }

    @Test
    public void testPublication() throws Exception {
        SplitList<PublicationItem> items;

        StringBuilder sb = new StringBuilder();
        items = parsePublicationItems(sb.toString());
        assertTrue(items.size() == 0);

        sb.append("pubmed:21063943|doi:10.1007/978-1-60761-987-1_6");
        items = parsePublicationItems(sb.toString());
        assertTrue(items.toString().contains("pubmed:21063943|doi:10.1007/978-1-60761-987-1_6"));

        sb.append("| doi:1231-60761-987-1_6");
        items = parsePublicationItems(sb.toString());
        assertTrue(items.size() == 3);

        sb.append("|cnki:1231-60761-987-1_6");
        items = parsePublicationItems(sb.toString());
        assertTrue(items.size() == 0);
    }

    @Test
    public void testModification() throws Exception {
        List<Modification> modList;
        Modification modification;

        StringBuilder sb = new StringBuilder();

        sb.append("3-MOD:00412");
        modList = parseModificationList(Section.Protein, sb.toString());
        assertTrue(modList.size() == 1);
        modification = modList.get(0);
        assertTrue(modification.getPositionMap().containsKey(3));
        assertTrue(modification.getType().name().equals("MOD"));
        assertTrue(modification.getAccession().equals("00412"));

        sb.append(", 3|4-UNIMOD:00412");
        modList = parseModificationList(Section.Protein, sb.toString());
        assertTrue(modList.size() == 2);
        modification = modList.get(1);
        assertTrue(modification.getPositionMap().containsKey(3));
        assertTrue(modification.getPositionMap().containsKey(4));
        assertTrue(modification.getType().name().equals("UNIMOD"));
        assertTrue(modification.getAccession().equals("00412"));

        sb.append(", 3[MS, MS:100xxxx, Probability Score Y, 0.8]|4[MS, MS:100xxxx, Probability Score Y, 0.2]-MOD:00412|[MS, MS:1001524, fragment neutral loss, 63.998285]");
        modList = parseModificationList(Section.Protein, sb.toString());
        assertTrue(modList.size() == 3);
        modification = modList.get(2);
        assertTrue(modification.getPositionMap().containsKey(3));
        assertTrue(modification.getPositionMap().get(3).getValue().contains("0.8"));
        assertTrue(modification.getPositionMap().containsKey(4));
        assertTrue(modification.getPositionMap().get(4).getValue().contains("0.2"));
        assertTrue(modification.getType().name().equals("MOD"));
        assertTrue(modification.getAccession().equals("00412"));
        assertTrue(modification.getNeutralLoss().getName().contains("fragment neutral loss"));

        sb.append(", CHEMMOD:+159.93");
        modList = parseModificationList(Section.Protein, sb.toString());
        assertTrue(modList.size() == 4);
        modification = modList.get(3);
        assertTrue(modification.getPositionMap().isEmpty());
        assertTrue(modification.getType().name().equals("CHEMMOD"));
        assertTrue(modification.getAccession().equals("+159.93"));

        sb.append(", 3-SUBST:R");
        modList = parseModificationList(Section.Protein, sb.toString());
        assertTrue(modList.size() == 5);
        modification = modList.get(4);
        assertTrue(modification.getPositionMap().containsKey(3));
        assertTrue(modification.getType().name().equals("SUBST"));
        assertTrue(modification.getAccession().equals("R"));

        sb.append(", 3-MOD:00412");
        modList = parseModificationList(Section.Protein, sb.toString());
        assertTrue(modList.size() == 6);

        sb.append(", 3|4-UNIMOD:00412");
        modList = parseModificationList(Section.Protein, sb.toString());
        assertTrue(modList.size() == 7);

        sb.append(", CHEMMOD:+NH4-H");
        modList = parseModificationList(Section.Protein, sb.toString());
        assertTrue(modList.size() == 8);
        modification = modList.get(7);
        assertTrue(modification.getPositionMap().isEmpty());
        assertTrue(modification.getType().name().equals("CHEMMOD"));
        assertTrue(modification.getAccession().equals("+NH4-H"));

        // test no modification.
        Modification mod = parseModification(Section.Protein, "0");
        assertTrue(mod.toString().equals("0"));
        modList = parseModificationList(Section.Protein, "0");
        assertTrue(modList.size() == 1);
    }
}
