package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.MzTabParsingException;

/**
 * Represents the reference to an external
 * MS file as defined in a Unit's metadata
 * section.
 * @author jg
 *
 */
public class MsFile {
	/**
	 * A cvParam describing the used
	 * file format.
	 */
	private Param format;
	/**
	 * A cvParam describing the id
	 * format used by the file.
	 */
	private Param idFormat;
	/**
	 * The external file's location
	 */
	private String location;
	
	/**
	 * Creates a new MsFile object.
	 */
	public MsFile() {
		
	}
	
	/**
	 * Creates a new MsFile object.
	 * @param format cvParam identifying the used file format.
	 * @param idFormat cvParam describing the id format used by the file format.
	 * @param location The file's location.
	 * @throws MzTabParsingException 
	 */
	public MsFile(Param format, Param idFormat, String location) throws MzTabParsingException {
		TableObject.checkStringValue(location);
		
		this.format = format;
		this.idFormat = idFormat;
		this.location = location;
	}

	public Param getFormat() {
		return format;
	}

	public void setFormat(Param format) {
		this.format = format;
	}

	public Param getIdFormat() {
		return idFormat;
	}

	public void setIdFormat(Param idFormat) {
		this.idFormat = idFormat;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) throws MzTabParsingException {
		TableObject.checkStringValue(location);
		this.location = location;
	}
}
