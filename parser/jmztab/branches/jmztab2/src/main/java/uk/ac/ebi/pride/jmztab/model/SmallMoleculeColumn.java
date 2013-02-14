package uk.ac.ebi.pride.jmztab.model;

import java.math.BigDecimal;

/**
 * The small molecule section is table-based. The small molecule section MUST always come after
 * the metadata section, peptide section and or protein section if they are present in the file.
 * All table columns MUST be Tab separated. There MUST NOT be any empty cells.
 *
 * The columns in the small molecule section MUST be in the order they are presented in this document.
 * All columns, unless specified otherwise, are mandatory.
 *
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public enum SmallMoleculeColumn implements MZTabColumn {
	IDENTIFIER(					"identifier",					SplitList.class,              1),
	UNIT_ID(					"unit_id",						String.class,                 2),
	CHEMICAL_FORMULA(			"chemical_formula",				String.class,                 3),
	SMILES(						"smiles",						SplitList.class,              4),
	INCHI_KEY(					"inchi_key",					SplitList.class,              5),
	DESCRIPTION(				"description",					String.class,                 6),
	MASS_TO_CHARGE(				"mass_to_charge",               BigDecimal.class,             7),
	CHARGE(						"charge",						Integer.class,                8),
	RETENTION_TIME(				"retention_time", 				SplitList.class,              9),
	TAXID(						"taxid",						Integer.class,               10),
	SPECIES(					"species", 						String.class,                11),
	DATABASE(					"database", 					String.class,                12),
	DATABASE_VERSION(			"database_version",				String.class,                13),
	RELIABILITY(				"reliability", 					Reliability.class,           14),
	URI(						"uri",							java.net.URI.class,          15),
	SPEC_REF(					"spectra_ref",					String.class,                16),
	SEARCH_ENGINE(				"search_engine",				SplitList.class,             17),
	SEARCH_ENGINE_SCORE(		"search_engine_score",			SplitList.class,             18),
	MODIFICATIONS(				"modifications",				SplitList.class,             19);

    private String name;
    private Class columnType;
    private int position;

    private SmallMoleculeColumn(String name, Class columnType, int position) {
        this.name = name;
        this.columnType = columnType;
        this.position = position;
    }

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public Class getColumnType() {
        return columnType;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public static SmallMoleculeColumn findColumn(String name) {
        if (name == null) {
            return null;
        }

        SmallMoleculeColumn column;
        try {
            column = SmallMoleculeColumn.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            column = null;
        }

        return column;
    }
}


