package uk.ac.ebi.pride.jmztab.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 05/02/13
 */
public class MZTabRecordRun {
    public static void main(String[] args) {
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein);
        SubUnit subUnit = new SubUnit("PRIDE_1234", 1);
        factory.addAbundanceColumns(subUnit);
        CVParam param = new CVParam("MS", "MS:1001208", "TOM", null);
        factory.addOptionalColumn("my_value", String.class);
        factory.addCVParamOptionalColumn(param);

        Protein protein = new Protein(factory);

        protein.setAccession("P12345");
        protein.setUnitId("PRIDE_1234");
        protein.setDescription("Aspartate aminotransferase, mitochondrial");

        protein.setTaxid("10116");

        SplitList<Param> searchEngine = new SplitList<Param>(MZTabConstants.BAR);
        searchEngine.add(new CVParam("MS", "MS:1001207", "Mascot", null));
        searchEngine.add(new CVParam("MS", "MS:1001208", "Sequest", null));
        protein.setSearchEngine(searchEngine);

        protein.setReliability(Reliability.High);

        factory.getAbundanceColumnMapping();
        factory.getOptionalColumnMapping();

        factory.getAbundanceColumnMapping();
    }
}
