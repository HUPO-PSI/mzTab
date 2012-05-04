package uk.ac.ebi.pride.mztab_java.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum SmallMoleculeTableField {
	// FIELD					FIELD NAME						COLUMN POSITION (-1 = not written, 0-based)
	HEADER_PREFIX(				"SMH", 							-1),
	ROW_PREFIX(					"SML", 							0),
	IDENTIFIER(					"identifier",					1),
	UNIT_ID(					"unit_id",						2),
	CHEMICAL_FORMULA(			"chemical_formula",				3),
	SMILES(						"smiles",						4),
	INCHI_KEY(					"inchi_key",					5),
	DESCRIPTION(				"description",					6),
	MASS_TO_CHARGE(				"mass_to_charge",				7),
	CHARGE(						"charge",						8),
	RETENTION_TIME(				"retention_time", 				9),
	TAXID(						"taxid",						10),
	SPECIES(					"species", 						11),
	DATABASE(					"database", 					12),
	DATABASE_VERSION(			"database_version",				13),
	RELIABILITY(				"reliability", 					14),
	URI(						"uri",							15),
	SPEC_REF(					"spectra_ref",					16),
	SEARCH_ENGINE(				"search_engine",				17),
	SEARCH_ENGINE_SCORE(		"search_engine_score",			18),
	MODIFICATIONS(				"modifications",				19),
	// WARNING: the position information of the quantitative fields as well 
	// as the custom field is not evaluated. To change the order these fields
	// are marshalled, the respective functions in Protein.java need to be
	// changed.
	ABUNDANCE(					"smallmolecule_abundance_sub",	20),
	ABUNDANCE_STD(				"smallmolecule_abundance_stdev_sub", 21),
	ABUNDANCE_STD_ERROR(		"smallmolecule_abundance_std_error_sub", 22),
	CUSTOM(						"opt_",							23);
	
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


