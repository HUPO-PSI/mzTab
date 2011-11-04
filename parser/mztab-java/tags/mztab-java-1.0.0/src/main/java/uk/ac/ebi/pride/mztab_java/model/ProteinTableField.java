package uk.ac.ebi.pride.mztab_java.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The available protein table fields.
 * @author jg
 *
 */
public enum ProteinTableField {
	// FIELD					FIELD NAME						COLUMN POSITION (-1 = not written, 0-based)
	HEADER_PREFIX(				"PRH", 							-1),
	ROW_PREFIX(					"PRT", 							0),
	ACCESSION(					"accession", 					1),
	UNIT_ID(					"unit_id", 						2),
	DESCRIPTION(				"description",					3),
	TAXID(						"taxid",						4),
	SPECIES(					"species",						5),
	DATABASE(					"database",						6),
	DATABASE_VERSION(			"database_version",				7),
	SEARCH_ENGINE(				"search_engine",				8),
	SEARCH_ENGINE_SCORE(		"search_engine_score",			9),
	RELIABILITY(				"reliability",					10),
	NUM_PEPTIDES(				"num_peptides",					11),
	NUM_PEPTIDES_DISTINCT(		"num_peptides_distinct",		12),
	NUM_PEPTIDES_UNAMBIGUOUS(	"num_peptides_unambiguous",		13),
	AMBIGUITY_MEMGERS(			"ambiguity_members",			14),
	MODIFICATIONS(				"modifications",				15),
	URI(						"uri",							16),
	GO_TERMS(					"go_terms",						17),
	PROTEIN_COVERAGE(			"protein_coverage",				18),
	// WARNING: the position information of the quantitative fields as well 
	// as the custom field is not evaluated. To change the order these fields
	// are marshalled, the respective functions in Protein.java need to be
	// changed.
	PROTEIN_ABUNDANCE(			"protein_abundance_sub",		19),
	PROTEIN_ABUNDANCE_STD(		"protein_abundance_stdev_sub",	20),
	PROTEIN_ABUNDANCE_STD_ERROR("protein_abundance_std_error_sub",21),
	CUSTOM(						"opt_",							22);
	
	private static HashMap<String, ProteinTableField> fieldMappings = new HashMap<String, ProteinTableField>();
	private static ArrayList<ProteinTableField> orderedTableFields = new ArrayList<ProteinTableField>();
	
	static {
		HashMap<Integer, ProteinTableField> fieldsByIndex = new HashMap<Integer, ProteinTableField>();
		
		for (ProteinTableField field : values()) {
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
	
	private ProteinTableField(String fieldName, int fieldPosition) {
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
	public static ProteinTableField getField(String fieldName) {
		// check if it's defined in the hashmap
		if (fieldMappings.containsKey(fieldName))
			return fieldMappings.get(fieldName);
		
		// check the "special" fields
		if (fieldName.startsWith(CUSTOM.toString()))
			return CUSTOM;
		
		if (fieldName.startsWith(PROTEIN_ABUNDANCE.toString()))
			return PROTEIN_ABUNDANCE;
		
		if (fieldName.startsWith(PROTEIN_ABUNDANCE_STD.toString()))
			return PROTEIN_ABUNDANCE_STD;
		
		if (fieldName.startsWith(PROTEIN_ABUNDANCE_STD_ERROR.toString()))
			return PROTEIN_ABUNDANCE_STD_ERROR;
		
		return null;
	}
	
	public static List<ProteinTableField> getOrderedFieldList() {
		return orderedTableFields;
	}
}
