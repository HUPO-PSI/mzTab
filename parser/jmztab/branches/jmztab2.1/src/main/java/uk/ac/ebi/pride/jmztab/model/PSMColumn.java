package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class PSMColumn extends MZTabColumn {
    private PSMColumn(String name, Class columnType, boolean optional, String order) {
        super(name, columnType, optional, order);
    }

    public static PSMColumn SEQUENCE = new PSMColumn("sequence", String.class, false, "01");
    public static PSMColumn PSM_ID = new PSMColumn("PSM_ID", Integer.class, false, "02");
    public static PSMColumn ACCESSION = new PSMColumn("accession", String.class, false, "03");
    public static PSMColumn UNIQUE = new PSMColumn("unique", MZBoolean.class, false, "04");
    public static PSMColumn DATABASE = new PSMColumn("database", String.class, false, "05");
    public static PSMColumn DATABASE_VERSION = new PSMColumn("database_version", String.class, false, "06");
    public static PSMColumn SEARCH_ENGINE = new PSMColumn("search_engine", SplitList.class, false, "07");
    public static PSMColumn SEARCH_ENGINE_SCORE = new PSMColumn("search_engine_score", SplitList.class, false, "08");
    public static PSMColumn RELIABILITY = new PSMColumn("reliability", Reliability.class, false, "09");
    public static PSMColumn MODIFICATIONS = new PSMColumn("modifications", SplitList.class, false, "10");
    public static PSMColumn RETENTION_TIME = new PSMColumn("retention_time", SplitList.class, false, "11");
    public static PSMColumn CHARGE = new PSMColumn("charge", Integer.class, false, "12");
    public static PSMColumn EXP_MASS_TO_CHARGE = new PSMColumn("exp_mass_to_charge", Double.class, false, "13");
    public static PSMColumn CALC_MASS_TO_CHARGE = new PSMColumn("calc_mass_to_charge", Double.class, false, "14");
    public static PSMColumn URI = new PSMColumn("uri", java.net.URI.class, false, "15");
    public static PSMColumn SPECTRA_REF = new PSMColumn("spectra_ref", SplitList.class, false, "16");
    public static PSMColumn PRE = new PSMColumn("pre", String.class, false, "17");
    public static PSMColumn POST = new PSMColumn("post", String.class, false, "18");
    public static PSMColumn START = new PSMColumn("start", String.class, false, "19");
    public static PSMColumn END = new PSMColumn("end", String.class, false, "20");
}
