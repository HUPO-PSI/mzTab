package uk.ac.ebi.pride.mztab_java.model;

import uk.ac.ebi.pride.mztab_java.MzTabFile;
import uk.ac.ebi.pride.mztab_java.MzTabParsingException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TableObject {
    public static final String NA = "NA";
    public static final String MISSING = "--";
    public static final String SEPARATOR = MzTabFile.SEPARATOR;
    public static final String EOL = MzTabFile.EOL;
    
    protected Map<Integer, Double> abundance = new HashMap<Integer, Double>();
    protected Map<Integer, Double> abundanceStd = new HashMap<Integer, Double>();
    protected Map<Integer, Double> abundanceError = new HashMap<Integer, Double>();
    
    protected Map<String, String> custom = new HashMap<String, String>();

    /**
     * Converts the given object to a mzTab formatted string (including
     * the newline character).
     *
     * @param nSubsamples     Defines the number of subsamples written. In case this protein was not quantified for that many subsamples, MISSING is written instead.
     * @param optionalColumns A list of optionalColumn headers. In case this protein does not have a value for a given optional column, MISSING is written.
     * @return The mzTab formatted string (including the newline character) representing this object.
     */
    abstract public String toMzTab(int nSubsamples, List<String> optionalColumns);

    /**
     * Parses a param list field. In case the field is set to "--"
     * null will be returned. In case the field is set to "NA" an
     * empty ParamList is returned.
     *
     * @param field The field's value to be parsed.
     * @return The ParamList representing the field
     */
    protected ParamList parseParamListField(String field) {
        field = field.trim();

        // check if the field is available
        if (MISSING.equals(field))
            return null;
        if (NA.equals(field))
            return new ParamList();

        // return the param list
        return new ParamList(field);
    }

    /**
     * Parses an integer field. Returns null in case the field
     * is set to missing or na.
     *
     * @param field The field's value to be parsed.
     * @return The Integer representing the field
     */
    protected Integer parseIntegerField(String field) {
        field = field.trim();

        // check if the field is available
        if (NA.equals(field) || MISSING.equals(field))
            return null;

        // return the Integer
        return Integer.parseInt(field);
    }

    /**
     * Parses an double field. Returns null in case the field
     * is set to missing or na.
     *
     * @param field The field's value to be parsed.
     * @return The Double representing the field
     */
    protected Double parseDoubleField(String field) {
        field = field.trim();

        // check if the field is available
        if (NA.equals(field) || MISSING.equals(field))
            return null;

        // return the Integer
        return Double.parseDouble(field);
    }

    /**
     * Parses a Param field. Returns null in case the field
     * is set to missing or na.
     *
     * @param field The field's value to be parsed.
     * @return The Param representing the field
     */
    protected Param parseParamField(String field) {
        field = field.trim();

        // check if the field is available
        if (NA.equals(field) || MISSING.equals(field))
            return null;

        // return the param
        return new Param(field);
    }

    /**
     * Parses a URI field. Returns null in case the field
     * is set to missing or na.
     *
     * @param field The field's value to be parsed.
     * @return The URI representing the field
     */
    protected URI parseUriField(String field) throws URISyntaxException {
        field = field.trim();

        // check if the field is available
        if (NA.equals(field) || MISSING.equals(field))
            return null;

        // return the param
        return new URI(field);
    }

    /**
     * Parses an boolean field. Returns null in case the field
     * is set to missing or na.
     *
     * @param field The field's value to be parsed.
     * @return The Boolean representing the field
     */
    protected Boolean parseBooleanField(String field) {
        field = field.trim();

        // check if the field is available
        if (NA.equals(field) || MISSING.equals(field))
            return null;

        // take numbers into consideration
        if ("1".equals(field))
            return true;
        if ("0".equals(field))
            return false;

        // return the Integer
        return Boolean.parseBoolean(field);
    }

    /**
     * Parses an array of strings separated by the passed
     * deliminator. In case the field is set to missing
     * or not available, null is returned.
     *
     * @param field       The field's value.
     * @param deliminator The deliminator to be used. In case the deliminator is a special characted in regex expression, it must be escaped accordingly.
     * @return
     */
    protected List<String> parseStringArray(String field, String deliminator) {
        field = field.trim();

        // check if the field is available
        if (MISSING.equals(field))
            return null;
        if (NA.equals(field))
            return Collections.emptyList();

        // return the Integer
        String[] values = field.split(deliminator);

        List<String> parsedValues = new ArrayList<String>(values.length);

        for (String value : values)
            parsedValues.add(value.trim());

        return parsedValues;
    }

    /**
     * Parses an array of douobles separated by the passed
     * deliminator. In case the field is set to missing
     * or not available, null is returned.
     *
     * @param field       The field's value.
     * @param deliminator The deliminator to be used. In case the deliminator is a special characted in regex expression, it must be escaped accordingly.
     * @return
     */
    protected List<Double> parseDoubleArray(String field, String deliminator) {
        field = field.trim();

        // check if the field is available
        if (MISSING.equals(field))
            return null;
        if (NA.equals(field))
            return Collections.emptyList();

        // return the Integer
        String[] values = field.split(deliminator);

        List<Double> parsedValues = new ArrayList<Double>(values.length);

        for (String value : values)
            parsedValues.add(Double.parseDouble(value));

        return parsedValues;
    }
    
    /**
     * Parses a list of spec_refs and returns them
     * as a List of SpecRefS objects.
     * 
     * @param field
     * @return
     * @throws MzTabParsingException
     */
    protected List<SpecRef> parseSpecRefArray(String field) throws MzTabParsingException {
    	field = field.trim();
    	
    	// check if the field is available
        if (MISSING.equals(field))
            return null;
        if (NA.equals(field))
            return Collections.emptyList();
        
        // split the field into the refs
        String refs[] = field.split("\\|");
        
        List<SpecRef> specRefs = new ArrayList<SpecRef>(refs.length);
        
        for (String ref : refs) {
        	specRefs.add(new SpecRef(ref));
        }
        
        return specRefs;
    }

    /**
     * Parses a modification string and returns a List of ModificationS.
     *
     * @param string
     * @return
     * @throws MzTabParsingException
     */
    protected List<Modification> parseModifications(String string) throws MzTabParsingException {
        // return if nothing is set
        if (MISSING.equals(string))
            return null;
        if (NA.equals(string))
            return Collections.emptyList();

        // split the modification string
        String[] modificationStrings = string.split(",");
        ArrayList<Modification> modifications = new ArrayList<Modification>(modificationStrings.length);

        for (String modString : modificationStrings)
            modifications.add(new Modification(modString));

        return modifications;
    }

    /**
     * Converts the given object to a field value.
     *
     * @param value
     * @return
     */
    protected String toField(Object value) {
        if (value == null)
            return MISSING;

        String string = value.toString();

        if ("".equals(string))
            return NA;

        // if it's a double remove a possible .0
        if (value instanceof Double)
            string = string.replaceAll("\\.0$", "");

        return string;
    }

    /**
     * Converts the given List into
     * a field value.
     *
     * @param array
     * @param separator
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected String arrayToField(List array, String separator) {
        if (array == null)
            return MISSING;

        if (array.size() < 1)
            return NA;

        String string = "";

        for (Object value : array)
            string += (string.length() > 1 ? separator : "") + value.toString();

        return string;
    }
    
    /**
	 * Converts the quantitative data into a
	 * mzTab formatted string. The quantitative data is
	 * written in the order "abundance" - "stddev" - "stderror"
	 * @param nSubsamples
	 * @return The formatted mzTab string.
	 */
	protected String quantDataToMztab(int nSubsamples) {
		StringBuffer mzTabString = new StringBuffer();
		
		for (Integer subsample = 1; subsample <= nSubsamples; subsample++) {
			Double abundance	= this.abundance.get(subsample);
			Double stddev 		= this.abundanceStd.get(subsample);
			Double stderr		= this.abundanceError.get(subsample);
			
			mzTabString.append(SEPARATOR +
							// abundance
						   (abundance != null ? abundance : MISSING) + SEPARATOR +
						   // stdandard dev
						   (stddev != null ? stddev : MISSING) + SEPARATOR +
						   // standard error
						   (stderr != null ? stderr : MISSING));
		}
		
		return mzTabString.toString();
	}
	
	/**
	 * Sets the abundance of the given object for the specified
	 * subsample.
	 * @param nSubsample 1-based number of the subsample.
	 * @param abundance The peptide's abundance.
	 * @param standardDeviation The standard deviation. Set to NULL if missing.
	 * @param standardError The standard error. Set to NULL if missing.
	 */
	public void setAbundance(int nSubsample, Double abundance, Double standardDeviation, Double standardError) {
		this.abundance.put(nSubsample, abundance);
		this.abundanceStd.put(nSubsample, standardDeviation);
		this.abundanceError.put(nSubsample, standardError);
	}
	
	public Double getAbundance(int subsampleIndex) {
		return abundance.get(subsampleIndex);
	}
	
	public Double getAbundanceStdDev(int subsampleIndex) {
		return abundanceStd.get(subsampleIndex);
	}
	
	public Double getAbundanceStdErr(int subsampleIndex) {
		return abundanceError.get(subsampleIndex);
	}
	
	public Collection<Integer> getSubsampleIndexes() {
		return abundance.keySet();
	}
	
	public Map<String, String> getCustom() {
		return custom;
	}
	
	/**
	 * Sets a specific custom's column value. The column
	 * name must start with "opt_" as defined in the
	 * mzTab specification document.
	 * @param columnName The column's name to set the value for. Must start with "opt_".
	 * @param value The new value.
	 * @throws MzTabParsingException Thrown if an invalid column name is passed.
	 */
	public void setCustomColumn(String columnName, String value) throws MzTabParsingException {
		if (!columnName.startsWith("opt_"))
			throw new MzTabParsingException("Invalid custom column name. Custom column names must start with \"opt_\".");
		
		custom.put(columnName, value);
	}
}
