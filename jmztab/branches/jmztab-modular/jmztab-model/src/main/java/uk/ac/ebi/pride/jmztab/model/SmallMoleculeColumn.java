package uk.ac.ebi.pride.jmztab.model;

/**
 * Define the stable columns and optional columns which have stable order in small molecule header line.
 *
 * @author qingwei
 * @since 23/05/13
 */
public class SmallMoleculeColumn extends MZTabColumn {
    SmallMoleculeColumn(String name, Class columnType, boolean optional, String order) {
        super(name, columnType, optional, order);
    }

    SmallMoleculeColumn(String name, Class columnType, boolean optional, String order, Integer id) {
        super(name, columnType, optional, order, id);
    }

    public static SmallMoleculeColumn IDENTIFIER = new SmallMoleculeColumn("identifier", SplitList.class, false, "01");
    public static SmallMoleculeColumn CHEMICAL_FORMULA = new SmallMoleculeColumn("chemical_formula", String.class, false, "02");
    public static SmallMoleculeColumn SMILES = new SmallMoleculeColumn("smiles", SplitList.class, false, "03");
    public static SmallMoleculeColumn INCHI_KEY = new SmallMoleculeColumn("inchi_key", SplitList.class, false, "04");
    public static SmallMoleculeColumn DESCRIPTION = new SmallMoleculeColumn("description", String.class, false, "05");
    public static SmallMoleculeColumn EXP_MASS_TO_CHARGE = new SmallMoleculeColumn("exp_mass_to_charge", Double.class, false, "06");
    public static SmallMoleculeColumn CALC_MASS_TO_CHARGE = new SmallMoleculeColumn("calc_mass_to_charge", Double.class, false, "07");
    public static SmallMoleculeColumn CHARGE = new SmallMoleculeColumn("charge", Integer.class, false, "08");
    public static SmallMoleculeColumn RETENTION_TIME = new SmallMoleculeColumn("retention_time", SplitList.class, false, "09");
    public static SmallMoleculeColumn TAXID = new SmallMoleculeColumn("taxid", Integer.class, false, "10");
    public static SmallMoleculeColumn SPECIES = new SmallMoleculeColumn("species", String.class, false, "11");
    public static SmallMoleculeColumn DATABASE = new SmallMoleculeColumn("database", String.class, false, "12");
    public static SmallMoleculeColumn DATABASE_VERSION = new SmallMoleculeColumn("database_version", String.class, false, "13");
    public static SmallMoleculeColumn RELIABILITY = new SmallMoleculeColumn("reliability", Reliability.class, true, "14");
    public static SmallMoleculeColumn URI = new SmallMoleculeColumn("uri", java.net.URI.class, true, "15");
    public static SmallMoleculeColumn SPECTRA_REF = new SmallMoleculeColumn("spectra_ref", SplitList.class, false, "16");
    public static SmallMoleculeColumn SEARCH_ENGINE = new SmallMoleculeColumn("search_engine", SplitList.class, false, "17");
    public static SmallMoleculeColumn BEST_SEARCH_ENGINE_SCORE = new SmallMoleculeColumn("best_search_engine_score", Double.class, true, "18");
    public static SmallMoleculeColumn SEARCH_ENGINE_SCORE = new SmallMoleculeColumn("search_engine_score", Double.class, true, "19");
    public static SmallMoleculeColumn MODIFICATIONS = new SmallMoleculeColumn("modifications", SplitList.class, false, "20");
}
