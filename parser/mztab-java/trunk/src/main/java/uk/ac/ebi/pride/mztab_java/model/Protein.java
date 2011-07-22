package uk.ac.ebi.pride.mztab_java.model;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.mztab_java.MzTabParsingException;

/**
 * A protein in a mzTab file.
 * @author jg
 *
 */
public class Protein extends TableObject {
	private static final Logger logger = Logger.getLogger(Protein.class);
	
	/**
	 * Available fields
	 */
	private String accession;
	private String unitId;
	private String description;
	private String taxid;
	private String species;
	private String database;
	private String databaseVersion;
	private ParamList searchEngine;
	private ParamList searchEngineScore;
	private Integer reliability;
	private Integer numPeptides;
	private Integer numPeptidesDistinct;
	private Integer numPeptidesUnambiguous;
	private List<String> ambiguityMembers;
	private List<Modification> modifications;
	private URI uri;
	private List<String> goTerms;
	private Double proteinCoverage;
	private Map<Integer, Double> proteinAbundance = new HashMap<Integer, Double>();
	private Map<Integer, Double> proteinAbundanceStd = new HashMap<Integer, Double>();
	private Map<Integer, Double> proteinAbundanceError = new HashMap<Integer, Double>();
	private Map<String, String> custom = new HashMap<String, String>();
	
	/**
	 * Creates a new empty protein object
	 */
	public Protein() {
		
	}
	
	/**
	 * Creates a new empty protein object based on a parsed
	 * protein table line. This line must be a map with the
	 * column name as key and the respective value as value.
	 * @param parsedTableLine A Map representing a parsed protein table line.
	 */
	public Protein(Map<String, String> parsedTableLine) throws MzTabParsingException {
		// TODO: for "simple" fields there is currently no way to distinguish between NA and MISSING
		
		try {
			for (String fieldName : parsedTableLine.keySet()) {
				ProteinTableField field = ProteinTableField.getField(fieldName);
				String value 			= parsedTableLine.get(fieldName).trim();
				
				if (field == null) {
					logger.warn("Unknown field <" + fieldName + "> in protein table.");
					continue;
				}
				
				switch (field) {
					case ACCESSION:
						accession = value;
						break;
					case UNIT_ID:
						unitId = value;
						break;
					case DESCRIPTION:
						description = value;
						break;
					case TAXID:
						taxid = value;
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
					case SEARCH_ENGINE:
						searchEngine = parseParamListField(value);
						break;
					case SEARCH_ENGINE_SCORE:
						searchEngineScore = parseParamListField(value);
						break;
					case RELIABILITY:
						reliability = parseIntegerField(value);
						break;
					case NUM_PEPTIDES:
						numPeptides = parseIntegerField(value);
						break;
					case NUM_PEPTIDES_DISTINCT:
						numPeptidesDistinct = parseIntegerField(value);
						break;
					case NUM_PEPTIDES_UNAMBIGUOUS:
						numPeptidesUnambiguous = parseIntegerField(value);
						break;
					case AMBIGUITY_MEMGERS:
						ambiguityMembers = parseStringArray(value, ",");
						break;
					case MODIFICATIONS:
						modifications = parseModifications(value);
						break;
					case URI:
						uri = parseUriField(value);
						break;
					case GO_TERMS:
						goTerms = parseStringArray(value, ",");
						break;
					case PROTEIN_COVERAGE:
						proteinCoverage = parseDoubleField(value);
						break;
					case CUSTOM:
						custom.put(fieldName, value);
						break;
					case PROTEIN_ABUNDANCE:
					case PROTEIN_ABUNDANCE_STD:
					case PROTEIN_ABUNDANCE_STD_ERROR:
						parseAbundanceField(field, fieldName, value);
						break;
				}
			}
		}
		catch (Exception e) {
			throw new MzTabParsingException("Failed to parse protein.", e);
		}
	}

	/**
	 * Parses a protein abundance field (abundance, stdev, stderror) and
	 * sets the appropriate variable.
	 * @param field
	 * @param fieldName
	 * @param value
	 */
	private void parseAbundanceField(ProteinTableField field, String fieldName, String value) {
		// get the subsample index
		Integer subsampleIndex = Integer.parseInt(fieldName.substring(fieldName.lastIndexOf('[') + 1, fieldName.lastIndexOf(']')));
		// parse the value
		Double doubleValue = parseDoubleField(value);
		
		// set the appropriate value
		switch (field) {
			case PROTEIN_ABUNDANCE:
				proteinAbundance.put(subsampleIndex, doubleValue);
				break;
			case PROTEIN_ABUNDANCE_STD:
				proteinAbundanceStd.put(subsampleIndex, doubleValue);
				break;
			case PROTEIN_ABUNDANCE_STD_ERROR:
				proteinAbundanceError.put(subsampleIndex, doubleValue);
				break;
		}
	}

	public String getAccession() {
		return accession;
	}

	public String getUnitId() {
		return unitId;
	}

	public String getDescription() {
		return description;
	}

	public String getTaxid() {
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

	public ParamList getSearchEngine() {
		return searchEngine;
	}

	public ParamList getSearchEngineScore() {
		return searchEngineScore;
	}

	public Integer getReliability() {
		return reliability;
	}

	public Integer getNumPeptides() {
		return numPeptides;
	}

	public Integer getNumPeptidesDistinct() {
		return numPeptidesDistinct;
	}

	public Integer getNumPeptidesUnambiguous() {
		return numPeptidesUnambiguous;
	}

	public List<String> getAmbiguityMembers() {
		return ambiguityMembers;
	}

	public List<Modification> getModifications() {
		return modifications;
	}

	public URI getUri() {
		return uri;
	}

	public List<String> getGoTerms() {
		return goTerms;
	}

	public Double getProteinCoverage() {
		return proteinCoverage;
	}

	public Map<String, String> getCustom() {
		return custom;
	}
	
	public Double getAbundance(int subsampleIndex) {
		return proteinAbundance.get(subsampleIndex);
	}
	
	public Double getAbundanceStdDev(int subsampleIndex) {
		return proteinAbundanceStd.get(subsampleIndex);
	}
	
	public Double getAbundanceStdErr(int subsampleIndex) {
		return proteinAbundanceError.get(subsampleIndex);
	}
	
	public Collection<Integer> getSubsampleIndexes() {
		return proteinAbundance.keySet();
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTaxid(String taxid) {
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

	public void setSearchEngine(ParamList searchEngine) {
		this.searchEngine = searchEngine;
	}

	public void setSearchEngineScore(ParamList searchEngineScore) {
		this.searchEngineScore = searchEngineScore;
	}

	public void setReliability(Integer reliability) {
		this.reliability = reliability;
	}

	public void setNumPeptides(Integer numPeptides) {
		this.numPeptides = numPeptides;
	}

	public void setNumPeptidesDistinct(Integer numPeptidesDistinct) {
		this.numPeptidesDistinct = numPeptidesDistinct;
	}

	public void setNumPeptidesUnambiguous(Integer numPeptidesUnambiguous) {
		this.numPeptidesUnambiguous = numPeptidesUnambiguous;
	}

	public void setAmbiguityMembers(List<String> ambiguityMembers) {
		this.ambiguityMembers = ambiguityMembers;
	}

	public void setModifications(List<Modification> modifications) {
		this.modifications = modifications;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setGoTerms(List<String> goTerms) {
		this.goTerms = goTerms;
	}

	public void setProteinCoverage(Double proteinCoverage) {
		this.proteinCoverage = proteinCoverage;
	}
	
	/**
	 * Sets the abundance of the given protein for the specified
	 * subsample.
	 * @param nSubsample 1-based number of the subsample.
	 * @param abundance The protein's abundance.
	 * @param standardDeviation The standard deviation. Set to NULL if missing.
	 * @param standardError The standard error. Set to NULL if missing.
	 */
	public void setProteinAbundance(int nSubsample, Double abundance, Double standardDeviation, Double standardError) {
		proteinAbundance.put(nSubsample, abundance);
		proteinAbundanceStd.put(nSubsample, standardDeviation);
		proteinAbundanceError.put(nSubsample, standardError);
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
	 * Converts the protein object to an mzTab formatted
	 * String. The fields are written in the order defined
	 * in the ProteinTableFields ENUM. The header is not
	 * written.
	 * @param nSubsamples Defines the number of subsamples written. In case this protein was not quantified for that many subsamples, MISSING is written instead.
	 * @param optionalColumns A list of optionalColumn headers. In case this protein does not have a value for a given optional column, MISSING is written.
	 * @return The mzTab formatted string representing this protein.
	 */
	@Override
	public String toMzTab(int nSubsamples, List<String> optionalColumns) {
		// TODO: make sure that the header is written beforehand
		String mzTabString = "";
		List<ProteinTableField> fields = ProteinTableField.getOrderedFieldList();
		
		for (ProteinTableField field : fields) {			
			switch (field) {
				case ROW_PREFIX:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + ProteinTableField.ROW_PREFIX;
					break;
				case ACCESSION:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(accession);
					break;
				case UNIT_ID:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(unitId);
					break;
				case DESCRIPTION:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(description);
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
				case SEARCH_ENGINE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(searchEngine);
					break;
				case SEARCH_ENGINE_SCORE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(searchEngineScore);
					break;
				case RELIABILITY:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(reliability);
					break;
				case NUM_PEPTIDES:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(numPeptides);
					break;
				case NUM_PEPTIDES_DISTINCT:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(numPeptidesDistinct);
					break;
				case NUM_PEPTIDES_UNAMBIGUOUS:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(numPeptidesUnambiguous);
					break;
				case AMBIGUITY_MEMGERS:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(ambiguityMembers, ",");
					break;
				case MODIFICATIONS:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(modifications, ",");
					break;
				case URI:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(uri);
					break;
				case GO_TERMS:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(goTerms, ",");
					break;
				case PROTEIN_COVERAGE:
					mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + toField(proteinCoverage);
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
			Double abundance 	= proteinAbundance.get(subsample);
			Double stddev 		= proteinAbundanceStd.get(subsample);
			Double stderr		= proteinAbundanceError.get(subsample);
			
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
