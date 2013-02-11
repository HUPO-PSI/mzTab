package uk.ac.ebi.pride.jmztab.model;

import java.math.BigDecimal;

/**
 * The peptide section is table based. The peptide section must always come after the metadata section
 * and or protein section if these are present in the file. All table columns MUST be Tab separated.
 * There MUST NOT be any empty cells. The columns in the peptide section MUST be in the order they are
 * presented in this document. All columns, unless specified otherwise, are mandatory.
 *
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public enum PeptideColumn implements MZTabColumn {
    SEQUENCE(                   "sequence",                String.class,            1),
    ACCESSION(                  "accession",               String.class,            2),
    UNIT_ID(                    "unit_id",                 String.class,            3),
    UNIQUE(                     "unique",                  MZBoolean.class,         4),
    DATABASE(                   "database",                String.class,            5),
    DATABASE_VERSION(           "database_version",        String.class,            6),
    SEARCH_ENGINE(              "search_engine",           SplitList.class,         7),
    SEARCH_ENGINE_SCORE(        "search_engine_score",     SplitList.class,         8),
    RELIABILITY(                "reliability",             Reliability.class,       9),
    MODIFICATIONS(              "modifications",           SplitList.class,        10),
    RETENTION_TIME(             "retention_time",          SplitList.class,        11),
    CHARGE(                     "charge",                  Integer.class,          12),
    MASS_TO_CHARGE(             "mass_to_charge",          BigDecimal.class,       13),
    URI(                        "uri",                     java.net.URI.class,     14),
    SPEC_REF(                   "spectra_ref",             SplitList.class,        15);

    private String name;
    private Class columnType;
    private int position;

    private PeptideColumn(String name, Class columnType, int position) {
        this.name = name;
        this.columnType = columnType;
        this.position = position;
    }

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public Class getColumnType() {
        return columnType;
    }
}
