package uk.ac.ebi.pride.mztab_java.model;


/**
 * The various line types that can
 * be present in a mzTab file.
 * @author jg
 *
 */
public enum LineType {
	METADATA(				"MTD"),
	PROTEIN_TABLE_HEADER(	"PRH"),
	PROTEIN(				"PRT"),
	PEPTIDE_TABLE_HEADER(	"PEH"),
	PEPTIDE(				"PEP"),
	SMALL_MOLECULE_TABLE_HEADER("SMH"),
	SMALL_MOLECULE(			"SML"),
	COMMENT(				"COM");
	/**
	 * The line's prefix
	 */
	private String prefix;
	/**
	 * Creates a new LineType with
	 * the given prefix.
	 * @param prefix The line's expected prefix.
	 */
	private LineType(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * Returns the lines prefix.
	 * @return The lines prefix.
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * Returns the line type identifying the given
	 * line. Returns null in case the line could
	 * not be identified.
	 * @param line The line to get the type of.
	 * @return The line's type or null in case it is unknown.
	 */
	public static LineType getLineType(String line) {		
		// check if any of the line types fit
		for (LineType type : values()) {
			if (line.startsWith(type.getPrefix()))
				return type;
		}
		
		return null;
	}
}
