package uk.ac.ebi.tools.mztab_java.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents both, cv as well as userParams
 * @author jg
 *
 */
public class Param {
	/**
	 * Possible param types.
	 * @author jg
	 *
	 */
	public enum ParamType {CV_PARAM, USER_PARAM};
	/**
	 * The param's type.
	 */
	private ParamType type;
	/**
	 * Param properties
	 */
	String cvLabel;
	String accession;
	String name;
	String value;
	/**
	 * Pattern used to parse a Param object from
	 * a mzTab String.
	 */
	private static Pattern mzTabParamPattern = Pattern.compile("\\[([^,]+,)?([^,]+,)?([^,]+),([^,]*)\\]");	
	/**
	 * Creates a new cvParam.
	 * @param cvLabel
	 * @param accession
	 * @param name
	 * @param value
	 */
	public Param(String cvLabel, String accession, String name, String value) {
		super();
		this.cvLabel = cvLabel;
		this.accession = accession;
		this.name = name;
		this.value = value;
		this.type = ParamType.CV_PARAM;
	}

	/**
	 * Creates a new userParam.
	 * @param name
	 * @param value
	 */
	public Param(String name, String value) {
		super();
		this.name = name;
		this.value = value;
		this.type = ParamType.USER_PARAM;
	}
	
	/**
	 * Creates a new param object from a mzTab string.
	 * @param mzTabString
	 */
	public Param(String mzTabString) {
		// parse the mzTabString
		Matcher matcher = mzTabParamPattern.matcher(mzTabString);
		
		if (!matcher.find())
			throw new RuntimeException("Invalid mzTabString passed to Param constructor: \"" + mzTabString + "\"");
		
		// check if it's a user or a cvParam by checking whether a cvLabel is set
		if (matcher.group(1) == null) {
			this.type 	= ParamType.USER_PARAM;
			this.name 	= matcher.group(3).trim();
			this.value 	= matcher.group(4).trim();
		}
		else {
			this.type 		= ParamType.CV_PARAM;
			this.cvLabel 	= matcher.group(1).substring(0, matcher.group(1).length() - 1).trim(); // remove the trailing ","
			this.accession 	= matcher.group(2).substring(0, matcher.group(2).length() - 1).trim(); // remove the trailing ","
			this.name 		= matcher.group(3).trim();
			this.value 		= matcher.group(4).trim();
		}
	}

	public ParamType getType() {
		return type;
	}

	public String getCvLabel() {
		return cvLabel;
	}

	public String getAccession() {
		return accession;
	}
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (type == ParamType.USER_PARAM)
			return "[" + name + "," + value + "]";
		if (type == ParamType.CV_PARAM)
			return "[" + cvLabel + "," + accession + "," + name + "," + value + "]";
		
		return "";
	}
}
