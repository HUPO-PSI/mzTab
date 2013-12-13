package uk.ac.ebi.pride.jmztab.model;

/**
 * Define the stable columns and optional columns which have stable order in peptide header line.
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class PeptideColumn extends MZTabColumn {
    PeptideColumn(String name, Class columnType, boolean optional, String order) {
        super(name, columnType, optional, order);
    }

    public static PeptideColumn SEQUENCE = new PeptideColumn("sequence", String.class, false, "01");
    public static PeptideColumn ACCESSION = new PeptideColumn("accession", String.class, false, "02");
    public static PeptideColumn UNIQUE = new PeptideColumn("unique", MZBoolean.class, false, "03");
    public static PeptideColumn DATABASE = new PeptideColumn("database", String.class, false, "04");
    public static PeptideColumn DATABASE_VERSION = new PeptideColumn("database_version", String.class, false, "05");
    public static PeptideColumn SEARCH_ENGINE = new PeptideColumn("search_engine", SplitList.class, false, "06");
    public static PeptideColumn BEST_SEARCH_ENGINE_SCORE = new PeptideColumn("best_search_engine_score", SplitList.class, false, "07");
    public static PeptideColumn SEARCH_ENGINE_SCORE = new PeptideColumn("search_engine_score", SplitList.class, true, "08");
    public static PeptideColumn RELIABILITY = new PeptideColumn("reliability", Reliability.class, true, "09");
    public static PeptideColumn MODIFICATIONS = new PeptideColumn("modifications", SplitList.class, false, "10");
    public static PeptideColumn RETENTION_TIME = new PeptideColumn("retention_time", SplitList.class, false, "11");
    public static PeptideColumn RETENTION_TIME_WINDOW = new PeptideColumn("retention_time_window", SplitList.class, false, "12");
    public static PeptideColumn CHARGE = new PeptideColumn("charge", Integer.class, false, "13");
    public static PeptideColumn MASS_TO_CHARGE = new PeptideColumn("mass_to_charge", Double.class, false, "14");
    public static PeptideColumn URI = new PeptideColumn("uri", java.net.URI.class, true, "15");
    public static PeptideColumn SPECTRA_REF = new PeptideColumn("spectra_ref", SplitList.class, false, "16");
}
