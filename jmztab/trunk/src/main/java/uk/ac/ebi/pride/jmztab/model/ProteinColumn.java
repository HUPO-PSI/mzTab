package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class ProteinColumn extends MZTabColumn {
    ProteinColumn(String name, Class columnType, boolean optional, String order) {
        super(name, columnType, optional, order);
    }

    public static ProteinColumn ACCESSION = new ProteinColumn("accession", String.class, false, "01");
    public static ProteinColumn DESCRIPTION = new ProteinColumn("description", String.class, false, "02");
    public static ProteinColumn TAXID = new ProteinColumn("taxid", Integer.class, false, "03");
    public static ProteinColumn SPECIES = new ProteinColumn("species", String.class, false, "04");
    public static ProteinColumn DATABASE = new ProteinColumn("database", String.class, false, "05");
    public static ProteinColumn DATABASE_VERSION = new ProteinColumn("database_version", String.class, false, "06");
    public static ProteinColumn SEARCH_ENGINE = new ProteinColumn("search_engine", SplitList.class, false, "07");
    public static ProteinColumn BEST_SEARCH_ENGINE_SCORE = new ProteinColumn("best_search_engine_score", SplitList.class, false,  "08");
    public static ProteinColumn SEARCH_ENGINE_SCORE = new ProteinColumn("search_engine_score", SplitList.class, true, "09");
    public static ProteinColumn RELIABILITY = new ProteinColumn("reliability", Reliability.class, true, "10");
    public static ProteinColumn NUM_PSMS = new ProteinColumn("num_psms", Integer.class, true, "11");
    public static ProteinColumn NUM_PEPTIDES_DISTINCT = new ProteinColumn("num_peptides_distinct", Integer.class, true, "12");
    public static ProteinColumn NUM_PEPTIDES_UNIQUE = new ProteinColumn("num_peptides_unique", Integer.class, true, "13");
    public static ProteinColumn AMBIGUITY_MEMBERS = new ProteinColumn("ambiguity_members", SplitList.class, false,  "14");
    public static ProteinColumn MODIFICATIONS = new ProteinColumn("modifications", SplitList.class, false,  "15");
    public static ProteinColumn URI = new ProteinColumn("uri", java.net.URI.class, true, "16");
    public static ProteinColumn GO_TERMS = new ProteinColumn("go_terms", SplitList.class, true, "17");
    public static ProteinColumn PROTEIN_COVERAGE = new ProteinColumn("protein_coverage", Double.class, false, "18");
}
