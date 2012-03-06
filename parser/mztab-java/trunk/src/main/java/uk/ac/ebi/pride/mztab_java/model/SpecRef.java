package uk.ac.ebi.pride.mztab_java.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ebi.pride.mztab_java.MzTabParsingException;

/**
 * Represents a reference to an external
 * spectrum.
 * @author jg
 *
 */
public class SpecRef {
	/**
	 * Pattern to extract the msFile
	 * identifier and check whether
	 * the ms file identifier is formatted 
	 * correctly.
	 */
	private final static Pattern MS_FILE_PATTERN = Pattern.compile("ms_file\\[(\\d+)\\]");
	/**
	 * The msFile identifier
	 */
	private String msFile;
	/**
	 * The msFile's index.
	 */
	private Integer msFileIndex;
	/**
	 * Reference to the spectrum in the
	 * msFile.
	 */
	private String reference;
	
	/**
	 * Creates a new specRef object.
	 */
	public SpecRef() {
		
	}
	
	/**
	 * Creates a new SpecRef object.
	 * 
	 * @param msFileIndex The ms_file's index. Must be >= 1.
	 * @param reference The reference to the spectrum in the MS fiile.
	 * @throws MzTabParsingException Thrown in case an invalid ms_file index is passed.
	 */
	public SpecRef(int msFileIndex, String reference) throws MzTabParsingException {
		if (msFileIndex < 1)
			throw new MzTabParsingException("Invalid ms_file index passed. ms_file indexes must larger or equal to 1.");
		
		TableObject.checkStringValue(reference);
		
		this.msFile = String.format("ms_file[%d]", msFileIndex);
		this.reference = reference;
		this.msFileIndex = msFileIndex;
	}
	
	/**
	 * Creates a new SpecRef object.
	 * @param msFile The ms file (f.e. "ms_file[1]")
	 * @param reference The reference to the spectrum in the MS file.
	 * @throws MzTabParsingException Thrown if an invalid ms_file is passed.
	 */
	public SpecRef(String msFile, String reference) throws MzTabParsingException {
		TableObject.checkStringValue(reference);
		
		this.msFile = msFile;
		this.reference = reference;
		
		updateMsFileIndex();
	}
	
	/**
	 * Creates a new SpecRef object from an mzTab formatted
	 * String.
	 * @param mzTab MzTab formatted String representing a spec_ref
	 * @throws MzTabParsingException 
	 */
	public SpecRef(String mzTab) throws MzTabParsingException {
		TableObject.checkStringValue(mzTab);
		int index = mzTab.indexOf(':');
		
		if (index < 0)
			throw new MzTabParsingException("Invalid mzTab formatted string passed to SpecRef constructor.");
		
		this.msFile = mzTab.substring(0, index);
		this.reference = mzTab.substring(index + 1);
		
		updateMsFileIndex();
	}
	
	/**
	 * Updates the msFileIndex based on
	 * the currently set msFile (identifier).
	 * @throws MzTabParsingException 
	 */
	private void updateMsFileIndex() throws MzTabParsingException {
		Matcher matcher = MS_FILE_PATTERN.matcher(msFile);
		
		if (!matcher.find())
			throw new MzTabParsingException("Invalid formatted ms_file identifier encountered");
		
		msFileIndex = Integer.parseInt(matcher.group(1));
	}

	/**
	 * Returns the msFile identifier (f.e. "ms_file[1]")
	 * 
	 * @return The msFile identifier.
	 */
	public String getMsFile() {
		return msFile;
	}
	
	/**
	 * Sets the ms file id the reference
	 * is pointing to.
	 * @param msFile The ms file id (f.e. ms_file[1]).
	 * @throws MzTabParsingException Thrown in case an invalid ms file id is passed.
	 */
	public void setMsFile(String msFile) throws MzTabParsingException {
		TableObject.checkStringValue(msFile);
		this.msFile = msFile;
		
		updateMsFileIndex();
	}
	public String getReference() {
		return reference;
	}
	
	/**
	 * Set's the spectrum's reference in the ms file.
	 * @param reference The spectrum's reference (f.e. index) in the ms file.
	 * @throws MzTabParsingException 
	 */
	public void setReference(String reference) throws MzTabParsingException {
		TableObject.checkStringValue(reference);
		this.reference = reference;
	}
	
	/**
	 * Returns the index of the ms file.
	 * @return
	 */
	public Integer getMsFileIndex() {
		return this.msFileIndex;
	}
	
	/**
	 * Converts the SpecRef object to its mzTab
	 * formatted representation.
	 * 
	 * @return MzTab formatted SpecRef
	 */
	public String toMzTab() {
		return String.format("%s:%s", this.msFile, this.reference);
	}

	@Override
	public String toString() {
		return toMzTab();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msFile == null) ? 0 : msFile.hashCode());
		result = prime * result
				+ ((msFileIndex == null) ? 0 : msFileIndex.hashCode());
		result = prime * result
				+ ((reference == null) ? 0 : reference.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpecRef other = (SpecRef) obj;
		if (msFile == null) {
			if (other.msFile != null)
				return false;
		} else if (!msFile.equals(other.msFile))
			return false;
		if (msFileIndex == null) {
			if (other.msFileIndex != null)
				return false;
		} else if (!msFileIndex.equals(other.msFileIndex))
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}

	
}
