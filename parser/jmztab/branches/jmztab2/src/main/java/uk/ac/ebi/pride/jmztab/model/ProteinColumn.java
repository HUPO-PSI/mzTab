package uk.ac.ebi.pride.jmztab.model;

import java.math.BigDecimal;

/**
 * The protein section is table-based. The protein section MUST always come after the metadata section
 * if the metadata section is present in the file.  All table columns MUST be tab-separated.
 * There MUST NOT be any empty cells. Missing values MUST be reported using “null”.
 *
 * The columns in the protein section MUST be in the order they are presented in this document.
 * All columns are mandatory unless specified otherwise.
 *
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public enum ProteinColumn implements MZTabColumn {
	ACCESSION(					"accession", 					String.class,        1),
	UNIT_ID(					"unit_id", 						String.class,        2),
	DESCRIPTION(				"description",					String.class,        3),
	TAXID(						"taxid",						Integer.class,       4),
	SPECIES(					"species",						String.class,        5),
	DATABASE(					"database",						String.class,        6),
	DATABASE_VERSION(			"database_version",				String.class,        7),
	SEARCH_ENGINE(				"search_engine",				SplitList.class,     8),
	SEARCH_ENGINE_SCORE(		"search_engine_score",			SplitList.class,     9),
	RELIABILITY(				"reliability",					Reliability.class,  10),
	NUM_PEPTIDES(				"num_peptides",					Integer.class,      11),
	NUM_PEPTIDES_DISTINCT(		"num_peptides_distinct",		Integer.class,      12),
	NUM_PEPTIDES_UNAMBIGUOUS(	"num_peptides_unambiguous",		Integer.class,      13),
	AMBIGUITY_MEMGERS(			"ambiguity_members",			SplitList.class,    14),
	MODIFICATIONS(				"modifications",				SplitList.class,    15),
	URI(						"uri",							java.net.URI.class, 16),
	GO_TERMS(					"go_terms",						SplitList.class,    17),
	PROTEIN_COVERAGE(			"protein_coverage",             BigDecimal.class,   18);

    private String name;
    private Class columnType;
    private int position;

    ProteinColumn(String name, Class columnType, int position) {
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

    public static ProteinColumn findColumn(String name) {
        if (name == null) {
            return null;
        }

        ProteinColumn column;
        try {
            column = ProteinColumn.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            column = null;
        }

        return column;
    }
}
