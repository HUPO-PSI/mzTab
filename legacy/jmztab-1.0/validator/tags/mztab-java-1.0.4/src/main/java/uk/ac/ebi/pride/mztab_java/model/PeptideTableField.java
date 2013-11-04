package uk.ac.ebi.pride.mztab_java.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The available protein table fields.
 * @author jg
 *
 */
public enum PeptideTableField {
	// FIELD					FIELD NAME						COLUMN POSITION (-1 = not written, 0-based)
	HEADER_PREFIX(				"PEH", 							-1),
	ROW_PREFIX(					"PEP", 							0),
	SEQUENCE(					"sequence",						1),
	ACCESSION(					"accession",					2),
	UNIT_ID(					"unit_id",						3),
	UNIQUE(						"unique",						4),
	DATABASE(					"database",						5),
	DATABASE_VERSION(			"database_version",				6),
	SEARCH_ENGINE(				"search_engine",				7),
	SEARCH_ENGINE_SCORE(		"search_engine_score",			8),
	RELIABILITY(				"reliability",					9),
	MODIFICATIONS(				"modifications",				10),
	RETENTION_TIME(				"retention_time",				11),
	CHARGE(						"charge",						12),
	MASS_TO_CHARGE(				"mass_to_charge",				13),
	URI(						"uri",							14),
	SPEC_REF(					"spectra_ref",					15),
	// WARNING: the position information of the quantitative fields as well 
	// as the custom field is not evaluated. To change the order these fields
	// are marshalled, the respective functions in Protein.java need to be
	// changed.
	PEPTIDE_ABUNDANCE(			"peptide_abundance_sub",		16),
	PEPTIDE_ABUNDANCE_STD(		"peptide_abundance_stdev_sub",	17),
	PEPTIDE_ABUNDANCE_STD_ERROR("peptide_abundance_std_error_sub",18),
	CUSTOM(						"opt_",							19);
	
	private static HashMap<String, PeptideTableField> fieldMappings = new HashMap<String, PeptideTableField>();
	private static ArrayList<PeptideTableField> orderedTableFields = new ArrayList<PeptideTableField>();
	
	static {
		HashMap<Integer, PeptideTableField> fieldsByIndex = new HashMap<Integer, PeptideTableField>();
		
		for (PeptideTableField field : values()) {
			fieldMappings.put(field.toString(), field);
			fieldsByIndex.put(field.getFieldPosition(), field);
		}
		
		for (int i = 0; i < fieldsByIndex.size(); i++) {
			if (fieldsByIndex.containsKey(i))
				orderedTableFields.add(fieldsByIndex.get(i));
		}
			
	}
	
	private String fieldName;
	private int fieldPosition;
	
	private PeptideTableField(String fieldName, int fieldPosition) {
		this.fieldName = fieldName;
		this.fieldPosition = fieldPosition;
	}

	@Override
	public String toString() {
		return fieldName;
	}
	
	public int getFieldPosition() {
		return fieldPosition;
	}
	
	/**
	 * Determines the protein table field name based on the passed
	 * fieldName. Returns null in case the fieldName is not known.
	 * @param fieldName The field's name.
	 * @return The ProteinTableField representing this field or null in case it is unknown.
	 */
	public static PeptideTableField getField(String fieldName) {
		// check if it's defined in the hashmap
		if (fieldMappings.containsKey(fieldName))
			return fieldMappings.get(fieldName);
		
		// check the "special" fields
		if (fieldName.startsWith(CUSTOM.toString()))
			return CUSTOM;
		
		if (fieldName.startsWith(PEPTIDE_ABUNDANCE.toString()))
			return PEPTIDE_ABUNDANCE;
		
		if (fieldName.startsWith(PEPTIDE_ABUNDANCE_STD.toString()))
			return PEPTIDE_ABUNDANCE_STD;
		
		if (fieldName.startsWith(PEPTIDE_ABUNDANCE_STD_ERROR.toString()))
			return PEPTIDE_ABUNDANCE_STD_ERROR;
		
		return null;
	}
	
	public static List<PeptideTableField> getOrderedFieldList() {
		return orderedTableFields;
	}
}

