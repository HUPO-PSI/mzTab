package uk.ac.ebi.tools.mztab_java.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to parse (tab) separated tables.
 * @author jg
 *
 */
public class TsvTableParser {
	/**
	 * The used separator for table fields
	 */
	private final static String SEPARATOR = "\t";
	/**
	 * The table's fields (name as key) and their
	 * index in the table as value.
	 */
	private HashMap<String, Integer> fieldMap = new HashMap<String, Integer>();
	
	/**
	 * Creates a new table parser object by passing
	 * the table's header line.
	 * @param tableHeaderLine The table's header line.
	 */
	public TsvTableParser(String tableHeaderLine) {
		// split the string into the fields
		String[] fields = tableHeaderLine.split(SEPARATOR);
		
		// process the fields
		for (int index = 0; index < fields.length; index++)
			fieldMap.put(fields[index].trim(), index);
	}
	
	/**
	 * Checks whether the given field is defined in the table
	 * header.
	 * @param fieldname The fieldname to check.
	 * @return A boolean indicating whether the field exists.
	 */
	public boolean hasField(String fieldname) {
		return fieldMap.containsKey(fieldname);
	}
	
	/**
	 * Parses the given line and returns the result as a Map
	 * with the column name as key and the respective field's
	 * value as value. 
	 * @param line The table line to parse.
	 * @return A Map representing the table line's contents.
	 * @throws IllegalStateException Thrown in case the line contains a different number of fields than defined in the header.
	 */
	public Map<String, String> parseTableLine(String line) throws IllegalStateException {
		// split the line into the fields
		String[] fields = line.split(SEPARATOR);
		
		// make sure the number of fields is as expected
		if (fields.length != fieldMap.size())
			throw new IllegalStateException("Invalid table line passed to parseTableLine. The line contains " + fields.length + " fields while the table header defined " + fieldMap.size() + " columns: " + line);
		
		// initialize the return value
		HashMap<String, String> mappedFields = new HashMap<String, String>(fields.length);
		
		for (String fieldName : fieldMap.keySet()) {
			// get the field value
			String value = fields[ fieldMap.get(fieldName) ].trim();
			
			// remove possible '"' around the field
			if (value.startsWith("\"") && value.endsWith("\""))
				value = value.substring(1, value.length() - 1);
			
			// save the value
			mappedFields.put(fieldName, value);
		}
		
		return mappedFields;
	}
}
