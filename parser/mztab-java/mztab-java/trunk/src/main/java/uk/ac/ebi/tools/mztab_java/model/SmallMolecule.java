package uk.ac.ebi.tools.mztab_java.model;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.tools.mztab_java.MzTabParsingException;

/**
 * A SmallMolecule in a mzTab file.
 * @author jg
 *
 */
public class SmallMolecule extends TableObject {
	/**
	 * The 0-based index of the small molecule
	 * in the small molecule table.
	 */
	private int index = -1;
	
	/**
	 * Fields
	 */
	private List<String>	identifier;
	private String			unitId;
	private String			description;
	private Double			massToCharge;
	private Double			charge;
	private List<Double>	retentionTime;
	private Integer			taxid;
	private String			species;
	private String			database;
	private String			databaseVersion;
	private Integer			reliability;
	private URI				uri;
	private ParamList		searchEngine;
	private ParamList		searchEngineScore;
	private List<Modification> modifications;
	private Map<Integer, Double> abundance = new HashMap<Integer, Double>();
	private Map<Integer, Double> abundanceStd = new HashMap<Integer, Double>();
	private Map<Integer, Double> abundanceError = new HashMap<Integer, Double>();
	private Map<String, String> custom = new HashMap<String, String>();
	
	public SmallMolecule() {
		
	}
	
	/**
	 * Creates a new SmallMolecule based on the 
	 * parseTableLine.
	 * @param parsedTableLine
	 * @throws MzTabParsingException
	 */
	public SmallMolecule(Map<String, String> parsedTableLine) throws MzTabParsingException {
		// TODO: for "simple" fields there is currently no way to distinguish between NA and MISSING
		
		try {
			for (String fieldName : parsedTableLine.keySet()) {
				SmallMoleculeTableField field = SmallMoleculeTableField.getField(fieldName);
				String value 			= parsedTableLine.get(fieldName).trim();
				
				switch (field) {
					case IDENTIFIER:
						identifier = parseStringArray(value, "\\|");
						break;
					case UNIT_ID:
						unitId = value;
						break;
					case DESCRIPTION:
						description = value;
						break;
					case MASS_TO_CHARGE:
						massToCharge = parseDoubleField(value);
						break;
					case CHARGE:
						charge = parseDoubleField(value);
						break;
					case RETENTION_TIME:
						retentionTime = parseDoubleArray(value, ",");
						break;
					case TAXID:
						taxid = parseIntegerField(value);
						break;
					case SPECIES:
						species = value;
						break;
					case DATABASE:
						database = value;
						break;
					case DATABASE_VERSION:
						databaseVersion = value;
						break;
					case RELIABILITY:
						reliability = parseIntegerField(value);
						break;
					case URI:
						uri = parseUriField(value);
						break;
					case SEARCH_ENGINE:
						searchEngine = parseParamListField(value);
						break;
					case SEARCH_ENGINE_SCORE:
						searchEngineScore = parseParamListField(value);
						break;
					case MODIFICATIONS:
						modifications = parseModifications(value);
						break;
					case ABUNDANCE:
					case ABUNDANCE_STD:
					case ABUNDANCE_STD_ERROR:
						parseAbundanceField(field, fieldName, value);
						break;
					case CUSTOM:
						custom.put(fieldName, value);
						break;
				}
			}
		}
		catch (Exception e) {
			throw new MzTabParsingException("Failed to parse small molecule.", e);
		}
	}
	
	/**
	 * Creates a new SmallMolecule based on the 
	 * parseTableLine.
	 * @param parsedTableLine
	 * @param index The 0-based index of the SmallMolecule in the small molecule table.
	 * @throws MzTabParsingException
	 */
	public SmallMolecule(Map<String, String> parsedTableLine, int index) throws MzTabParsingException {
		this(parsedTableLine);
		
		this.index = index;
	}
	
	/**
	 * Parses a protein abundance field (abundance, stdev, stderror) and
	 * sets the appropriate variable.
	 * @param field
	 * @param fieldName
	 * @param value
	 */
	private void parseAbundanceField(SmallMoleculeTableField field, String fieldName, String value) {
		// get the subsample index
		Integer subsampleIndex = Integer.parseInt(fieldName.substring(fieldName.lastIndexOf('[') + 1, fieldName.lastIndexOf(']')));
		// parse the value
		Double doubleValue = parseDoubleField(value);
		
		// set the appropriate value
		switch (field) {
			case ABUNDANCE:
				abundance.put(subsampleIndex, doubleValue);
				break;
			case ABUNDANCE_STD:
				abundanceStd.put(subsampleIndex, doubleValue);
				break;
			case ABUNDANCE_STD_ERROR:
				abundanceError.put(subsampleIndex, doubleValue);
				break;
		}
	}
	
	/*
	 * Getters and Setters
	 */
	
	public List<String> getIdentifier() {
		return identifier;
	}

	public String getUnitId() {
		return unitId;
	}

	public String getDescription() {
		return description;
	}

	public Double getMassToCharge() {
		return massToCharge;
	}

	public Double getCharge() {
		return charge;
	}

	public List<Double> getRetentionTime() {
		return retentionTime;
	}

	public Integer getTaxid() {
		return taxid;
	}

	public String getSpecies() {
		return species;
	}

	public String getDatabase() {
		return database;
	}

	public String getDatabaseVersion() {
		return databaseVersion;
	}

	public Integer getReliability() {
		return reliability;
	}

	public URI getUri() {
		return uri;
	}

	public ParamList getSearchEngine() {
		return searchEngine;
	}

	public ParamList getSearchEngineScore() {
		return searchEngineScore;
	}

	public List<Modification> getModifications() {
		return modifications;
	}

	public Map<String, String> getCustom() {
		return custom;
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
	
	public Collection<Integer> getSubsampleIds() {
		return abundance.keySet();
	}
	
	/**
	 * Returns the 0-based index of the small
	 * molecule in the small molecule table.
	 * Returns -1 in case the index wasn't set.
	 * @return The 0-based index of the small molecule in the small molecule table. -1 in case it wasn't set.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Set the 0-based index of the small molecule
	 * in the small molecule table.
	 * @param index The 0-based index of the small molecule in the small molecule table.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	public void setIdentifier(List<String> identifier) {
		this.identifier = identifier;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMassToCharge(Double massToCharge) {
		this.massToCharge = massToCharge;
	}

	public void setCharge(Double charge) {
		this.charge = charge;
	}

	public void setRetentionTime(List<Double> retentionTime) {
		this.retentionTime = retentionTime;
	}

	public void setTaxid(Integer taxid) {
		this.taxid = taxid;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setDatabaseVersion(String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public void setReliability(Integer reliability) {
		this.reliability = reliability;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setSearchEngine(ParamList searchEngine) {
		this.searchEngine = searchEngine;
	}

	public void setSearchEngineScore(ParamList searchEngineScore) {
		this.searchEngineScore = searchEngineScore;
	}

	public void setModifications(List<Modification> modifications) {
		this.modifications = modifications;
	}

	/**
	 * A HashMap of custom columns to be set. The column name
	 * must be given as key and the value as value.
	 * @param custom
	 */
	public void setCustom(Map<String, String> custom) {
		this.custom = custom;
	}
	
	/**
	 * Sets the abundance of the given small molecule for the specified
	 * subsample.
	 * @param nSubsample 1-based number of the subsample.
	 * @param abundance The small molecule's abundance.
	 * @param standardDeviation The standard deviation. Set to NULL if missing.
	 * @param standardError The standard error. Set to NULL if missing.
	 */
	public void setSmallMoleculeAbundance(int nSubsample, Double abundance, Double standardDeviation, Double standardError) {
		this.abundance.put(nSubsample, abundance);
		this.abundanceStd.put(nSubsample, standardDeviation);
		this.abundanceError.put(nSubsample, standardError);
	}

	/**
	 * Converts the small molecule object to an mzTab formatted
	 * String. The fields are written in the order defined
	 * in the SmallMoleculeTableFields ENUM. The header is not
	 * written.
	 * @param nSubsamples Defines the number of subsamples written. In case this small molecule was not quantified for that many subsamples, MISSING is written instead.
	 * @param optionalColumns A list of optionalColumn headers. In case this small molecule does not have a value for a given optional column, MISSING is written.
	 * @return The mzTab formatted string representing this peptide.
	 */
	@Override
	public String toMzTab(int nSubsamples, List<String> optionalColumns) {
		// TODO: make sure that the header is written beforehand
		String mzTabString = "";
		List<SmallMoleculeTableField> fields = SmallMoleculeTableField.getOrderedFieldList();
		
		for (SmallMoleculeTableField field : fields) {			
			switch (field) {
				case ROW_PREFIX:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + SmallMoleculeTableField.ROW_PREFIX;
					break;
				case IDENTIFIER:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(identifier, "|");
					break;
				case UNIT_ID:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(unitId);
					break;
				case DESCRIPTION:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(description);
					break;
				case MASS_TO_CHARGE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(massToCharge);
					break;
				case CHARGE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(charge);
					break;
				case RETENTION_TIME:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(retentionTime, ",");
					break;
				case TAXID:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(taxid);
					break;
				case SPECIES:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(species);
					break;
				case DATABASE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(database);
					break;
				case DATABASE_VERSION:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(databaseVersion);
					break;
				case RELIABILITY:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(reliability);
					break;
				case URI:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(uri);
					break;
				case SEARCH_ENGINE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(searchEngine);
					break;
				case SEARCH_ENGINE_SCORE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(searchEngineScore);
					break;
				case MODIFICATIONS:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(modifications, ",");
					break;
			}
		}
		
		// process the abundance data
		mzTabString += quantDataToMztab(nSubsamples);
		
		// add the optional columns
		for (String optionalColumn : optionalColumns) {
			mzTabString += SEPARATOR + (custom.containsKey(optionalColumn) ? custom.get(optionalColumn) : MISSING);
		}
		
		// add the line terminator
		mzTabString += EOL;
		
		return mzTabString;
	}
	
	/**
	 * Converts the protein's quantitative data into a
	 * mzTab formatted string. The quantitative data is
	 * written in the order "abundance" - "stddev" - "stderror"
	 * @param nSubsamples
	 * @return The formatted mzTab string.
	 */
	private String quantDataToMztab(int nSubsamples) {
		String mzTabString = "";
		
		for (Integer subsample = 1; subsample <= nSubsamples; subsample++) {
			Double abundance	= this.abundance.get(subsample);
			Double stddev 		= this.abundanceStd.get(subsample);
			Double stderr		= this.abundanceError.get(subsample);
			
			mzTabString += SEPARATOR +
							// abundance
						   (abundance != null ? abundance : MISSING) + SEPARATOR +
						   // stdandard dev
						   (stddev != null ? stddev : MISSING) + SEPARATOR +
						   // standard error
						   (stderr != null ? stderr : MISSING);
		}
		
		return mzTabString;
	}
	
}
