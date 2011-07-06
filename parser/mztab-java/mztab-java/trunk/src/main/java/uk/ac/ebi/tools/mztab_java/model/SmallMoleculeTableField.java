package uk.ac.ebi.tools.mztab_java.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum SmallMoleculeTableField {
	// FIELD					FIELD NAME						COLUMN POSITION (-1 = not written, 0-based)
	HEADER_PREFIX(				"SMH", 							-1),
	ROW_PREFIX(					"SML", 							0),
	IDENTIFIER(					"identifier",					1),
	UNIT_ID(					"unit_id",						2),
	DESCRIPTION(				"description",					3),
	MASS_TO_CHARGE(				"mass_to_charge",				4),
	CHARGE(						"charge",						5),
	RETENTION_TIME(				"retention_time", 				6),
	TAXID(						"taxid",						7),
	SPECIES(					"species", 						8),
	DATABASE(					"database", 					9),
	DATABASE_VERSION(			"database_version",				10),
	RELIABILITY(				"reliability", 					11),
	URI(						"uri",							12),
	SEARCH_ENGINE(				"search_engine",				13),
	SEARCH_ENGINE_SCORE(		"search_engine_score",			14),
	MODIFICATIONS(				"modifications",				15),
	// WARNING: the position information of the quantitative fields as well 
	// as the custom field is not evaluated. To change the order these fields
	// are marshalled, the respective functions in Protein.java need to be
	// changed.
	ABUNDANCE(					"smallmolecule_abundance_sub",	16),
	ABUNDANCE_STD(				"smallmolecule_abundance_stdev_sub", 17),
	ABUNDANCE_STD_ERROR(		"smallmolecule_abundance_std_error_sub", 18),
	CUSTOM(						"opt_",							19);
	
	private static HashMap<String, SmallMoleculeTableField> fieldMappings = new HashMap<String, SmallMoleculeTableField>();
	private static ArrayList<SmallMoleculeTableField> orderedTableFields = new ArrayList<SmallMoleculeTableField>();
	
	static {
		HashMap<Integer, SmallMoleculeTableField> fieldsByIndex = new HashMap<Integer, SmallMoleculeTableField>();
		
		for (SmallMoleculeTableField field : values()) {
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
	
	private SmallMoleculeTableField(String fieldName, int fieldPosition) {
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
	public static SmallMoleculeTableField getField(String fieldName) {
		// check if it's defined in the hashmap
		if (fieldMappings.containsKey(fieldName))
			return fieldMappings.get(fieldName);
		
		// check the "special" fields
		if (fieldName.startsWith(CUSTOM.toString()))
			return CUSTOM;
		
		if (fieldName.startsWith(ABUNDANCE.toString()))
			return ABUNDANCE;
		
		if (fieldName.startsWith(ABUNDANCE_STD.toString()))
			return ABUNDANCE_STD;
		
		if (fieldName.startsWith(ABUNDANCE_STD_ERROR.toString()))
			return ABUNDANCE_STD_ERROR;
		
		return null;
	}
	
	public static List<SmallMoleculeTableField> getOrderedFieldList() {
		return orderedTableFields;
	}
}


