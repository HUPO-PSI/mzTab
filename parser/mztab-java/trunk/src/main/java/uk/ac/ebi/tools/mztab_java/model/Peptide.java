package uk.ac.ebi.tools.mztab_java.model;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.tools.mztab_java.MzTabParsingException;

public class Peptide extends TableObject {
	/**
	 * The 0-based index of this peptide in the peptide
	 * table.
	 */
	private int index = -1;
	/**
	 * General attributes
	 */
	private String		sequence;
	private String		accession;
	private String		unitId;
	private Boolean		unique;
	private String		database;
	private String		databaseVersion;
	private Param		searchEngine;
	private ParamList	searchEngineScore;
	private Integer		reliability;
	private List<Modification> modification;
	private List<Double> retentionTime;
	private Double		charge;
	private Double		massToCharge;
	private URI			uri;
	private Map<Integer, Double> abundance = new HashMap<Integer, Double>();
	private Map<Integer, Double> abundanceStd = new HashMap<Integer, Double>();
	private Map<Integer, Double> abundanceError = new HashMap<Integer, Double>();
	private Map<String, String> custom = new HashMap<String, String>();
	
	public Peptide() {
		
	}
	
	/**
	 * Creates a new Peptide object from a parsedTableLine.
	 * @param parsedTableLine The parsed table line representing this peptide.
	 * @throws MzTabParsingException
	 */
	public Peptide(Map<String, String> parsedTableLine) throws MzTabParsingException {
		// TODO: for "simple" fields there is currently no way to distinguish between NA and MISSING
		
		try {
			for (String fieldName : parsedTableLine.keySet()) {
				PeptideTableField field = PeptideTableField.getField(fieldName);
				String value 			= parsedTableLine.get(fieldName).trim();
				
				switch (field) {
					case SEQUENCE:
						sequence = value;
						break;
					case ACCESSION:
						accession = value;
						break;
					case UNIT_ID:
						unitId = value;
						break;
					case UNIQUE:
						unique = parseBooleanField(value);
						break;
					case DATABASE:
						database = value;
						break;
					case DATABASE_VERSION:
						databaseVersion = value;
						break;
					case SEARCH_ENGINE:
						searchEngine = parseParamField(value);
						break;
					case SEARCH_ENGINE_SCORE:
						searchEngineScore = parseParamListField(value);
						break;
					case RELIABILITY:
						reliability = parseIntegerField(value);
						break;
					case MODIFICATIONS:
						modification = parseModifications(value);
						break;
					case RETENTION_TIME:
						retentionTime = parseDoubleArray(value, ",");
						break;
					case CHARGE:
						charge = parseDoubleField(value);
						break;
					case MASS_TO_CHARGE:
						massToCharge = parseDoubleField(value);
						break;
					case URI:
						uri = parseUriField(value);
						break;
					case PEPTIDE_ABUNDANCE:
					case PEPTIDE_ABUNDANCE_STD:
					case PEPTIDE_ABUNDANCE_STD_ERROR:
						parseAbundanceField(field, fieldName, value);
						break;
					case CUSTOM:
						custom.put(fieldName, value);
						break;
				}
			}
		}
		catch (Exception e) {
			throw new MzTabParsingException("Failed to parse peptide.", e);
		}
	}
	
	/**
	 * Creates a new Peptide object from a parsedTableLine.
	 * @param parsedTableLine The parsed table line representing this peptide.
	 * @param index The 0-based index of the line in the peptide table.
	 * @throws MzTabParsingException
	 */
	public Peptide(Map<String, String> parsedTableLine, int index) throws MzTabParsingException {
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
	private void parseAbundanceField(PeptideTableField field, String fieldName, String value) {
		// get the subsample index
		Integer subsampleIndex = Integer.parseInt(fieldName.substring(fieldName.lastIndexOf('[') + 1, fieldName.lastIndexOf(']')));
		// parse the value
		Double doubleValue = parseDoubleField(value);
		
		// set the appropriate value
		switch (field) {
			case PEPTIDE_ABUNDANCE:
				abundance.put(subsampleIndex, doubleValue);
				break;
			case PEPTIDE_ABUNDANCE_STD:
				abundanceStd.put(subsampleIndex, doubleValue);
				break;
			case PEPTIDE_ABUNDANCE_STD_ERROR:
				abundanceError.put(subsampleIndex, doubleValue);
				break;
		}
	}
	
	/*
	 * Getters
	 */
	
	public String getSequence() {
		return sequence;
	}

	public String getAccession() {
		return accession;
	}

	public String getUnitId() {
		return unitId;
	}

	public Boolean getUnique() {
		return unique;
	}

	public String getDatabase() {
		return database;
	}

	public String getDatabaseVersion() {
		return databaseVersion;
	}

	public Param getSearchEngine() {
		return searchEngine;
	}

	public ParamList getSearchEngineScore() {
		return searchEngineScore;
	}

	public Integer getReliability() {
		return reliability;
	}

	public List<Modification> getModification() {
		return modification;
	}

	public List<Double> getRetentionTime() {
		return retentionTime;
	}

	public Double getCharge() {
		return charge;
	}

	public Double getMassToCharge() {
		return massToCharge;
	}

	public URI getUri() {
		return uri;
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
	 * Returns the 0-based index of the peptide
	 * in the peptide table. Returns -1 if the 
	 * index wasn't set.
	 * @return The 0-based index of the peptide in the peptide table. -1 in case the index wasn't set.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the 0-based index of the peptide
	 * in the peptide table.
	 * @param index The 0-based index of the peptide in the peptide table.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setDatabaseVersion(String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public void setSearchEngine(Param searchEngine) {
		this.searchEngine = searchEngine;
	}

	public void setSearchEngineScore(ParamList searchEngineScore) {
		this.searchEngineScore = searchEngineScore;
	}

	public void setReliability(Integer reliability) {
		this.reliability = reliability;
	}

	public void setModification(List<Modification> modification) {
		this.modification = modification;
	}

	public void setRetentionTime(List<Double> retentionTime) {
		this.retentionTime = retentionTime;
	}

	public void setCharge(Double charge) {
		this.charge = charge;
	}

	public void setMassToCharge(Double massToCharge) {
		this.massToCharge = massToCharge;
	}

	public void setUri(URI uri) {
		this.uri = uri;
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
	 * Sets the abundance of the given peptide for the specified
	 * subsample.
	 * @param nSubsample 1-based number of the subsample.
	 * @param abundance The peptide's abundance.
	 * @param standardDeviation The standard deviation. Set to NULL if missing.
	 * @param standardError The standard error. Set to NULL if missing.
	 */
	public void setPeptideAbundance(int nSubsample, Double abundance, Double standardDeviation, Double standardError) {
		this.abundance.put(nSubsample, abundance);
		this.abundanceStd.put(nSubsample, standardDeviation);
		this.abundanceError.put(nSubsample, standardError);
	}

	/**
	 * Converts the peptide object to an mzTab formatted
	 * String. The fields are written in the order defined
	 * in the PeptideTableFields ENUM. The header is not
	 * written.
	 * @param nSubsamples Defines the number of subsamples written. In case this peptide was not quantified for that many subsamples, MISSING is written instead.
	 * @param optionalColumns A list of optionalColumn headers. In case this peptide does not have a value for a given optional column, MISSING is written.
	 * @return The mzTab formatted string representing this peptide.
	 */
	@Override
	public String toMzTab(int nSubsamples, List<String> optionalColumns) {
		// TODO: make sure that the header is written beforehand
		String mzTabString = "";
		List<PeptideTableField> fields = PeptideTableField.getOrderedFieldList();
		
		for (PeptideTableField field : fields) {			
			switch (field) {
				case ROW_PREFIX:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + PeptideTableField.ROW_PREFIX;
					break;
				case SEQUENCE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(sequence);
					break;
				case ACCESSION:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(accession);
					break;
				case UNIT_ID:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(unitId);
					break;
				case UNIQUE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(unique);
					break;
				case DATABASE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(database);
					break;
				case DATABASE_VERSION:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(databaseVersion);
					break;
				case SEARCH_ENGINE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(searchEngine);
					break;
				case SEARCH_ENGINE_SCORE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(searchEngineScore);
					break;
				case RELIABILITY:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(reliability);
					break;
				case MODIFICATIONS:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(modification, ",");
					break;
				case RETENTION_TIME:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(retentionTime, ",");
					break;
				case CHARGE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(charge);
					break;
				case MASS_TO_CHARGE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(massToCharge);
					break;
				case URI:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(uri);
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
