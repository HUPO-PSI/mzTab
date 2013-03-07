package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 04/02/13
 */
public class MZTabColumnRun {
    public static void main(String[] args) throws Exception {
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Peptide);
        SubUnit subUnit = new SubUnit("PRIDE_1234", 1);
        factory.addAbundanceColumns(subUnit);
        CVParam param = new CVParam("MS", "MS:1001208", "TOM", null);
        factory.addOptionColumn("my_value", String.class);
        factory.addCVParamOptionColumn(param);

        System.out.println(factory.toString());
    }
}
