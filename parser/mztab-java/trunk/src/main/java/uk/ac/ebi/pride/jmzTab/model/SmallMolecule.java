package uk.ac.ebi.pride.jmzTab.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.jmzTab.MzTabParsingException;

/**
 * A SmallMolecule in a mzTab file.
 * @author jg
 *
 */
public class SmallMolecule extends TableObject {
	private static final Logger logger = Logger.getLogger(SmallMolecule.class);
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
	private String			chemicalFormula;
	private String			smiles;
	private String			inchiKey;
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
	private List<SpecRef>	specRef;
	private ParamList		searchEngine;
	private ParamList		searchEngineScore;
	private List<Modification> modifications;
	
	public SmallMolecule() {
		
	}
	
	/**
	 * Creates a new SmallMolecule based on the 
	 * parseTableLine.
	 * @param parsedTableLine
	 * @throws MzTabParsingException
	 */
	public SmallMolecule(Map<String, String> parsedTableLine) throws MzTabParsingException {
		try {
			for (String fieldName : parsedTableLine.keySet()) {
				SmallMoleculeTableField field = SmallMoleculeTableField.getField(fieldName);
				String value 			= parsedTableLine.get(fieldName).trim();
				
				if (field == null) {
					logger.warn("Unknown field <" + fieldName + "> encountered in small molecule table.");
					continue;
				}
					
				
				switch (field) {
					case IDENTIFIER:
						identifier = parseStringArray(value, "\\|");
						break;
					case UNIT_ID:
						checkUnitId(value);
						unitId = value;
						break;
					case CHEMICAL_FORMULA:
						chemicalFormula = value;
						break;
					case SMILES:
						smiles = value;
						break;
					case INCHI_KEY:
						inchiKey = value;
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
						// make sure the reliability is between 1-3
						if (reliability != null && (reliability < 1 || reliability > 3))
							throw new MzTabParsingException("Invalid reliability " + reliability + ". Reliability must only be 1 (good), 2 (medium), and 3 (bad).");
						break;
					case URI:
						uri = parseUriField(value);
						break;
					case SPEC_REF:
						specRef = parseSpecRefArray(value);
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
			throw new MzTabParsingException("Failed to parse small molecule: " + e.getMessage(), e);
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
	
	public String getChemicalFormula() {
		return chemicalFormula;
	}

	public String getSmiles() {
		return smiles;
	}

	public String getInchiKey() {
		return inchiKey;
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
	
	public List<SpecRef> getSpecRef() {
		return specRef;
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

	public void setIdentifier(List<String> identifier) throws MzTabParsingException {
		for (String ident : identifier)
			checkStringValue(ident);
		this.identifier = identifier;
	}

	public void setUnitId(String unitId) throws MzTabParsingException {
		checkUnitId(unitId);
		this.unitId = unitId;
	}
	
	public void setChemicalFormula(String chemicalFormula) throws MzTabParsingException {
		checkStringValue(chemicalFormula);
		this.chemicalFormula = chemicalFormula;
	}

	public void setSmiles(String smiles) throws MzTabParsingException {
		checkStringValue(smiles);
		this.smiles = smiles;
	}

	public void setInchiKey(String inchiKey) throws MzTabParsingException {
		checkStringValue(inchiKey);
		this.inchiKey = inchiKey;
	}

	public void setDescription(String description) throws MzTabParsingException {
		checkStringValue(description);
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

	public void setSpecies(String species) throws MzTabParsingException {
		checkStringValue(species);
		this.species = species;
	}

	public void setDatabase(String database) throws MzTabParsingException {
		checkStringValue(database);
		this.database = database;
	}

	public void setDatabaseVersion(String databaseVersion) throws MzTabParsingException {
		checkStringValue(databaseVersion);
		this.databaseVersion = databaseVersion;
	}

	public void setReliability(Integer reliability) throws MzTabParsingException {
		// make sure the reliability is between 1-3
		if (reliability < 1 || reliability > 3)
			throw new MzTabParsingException("Invalid reliability " + reliability + ". Reliability must only be 1 (good), 2 (medium), and 3 (bad).");
		this.reliability = reliability;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	/**
	 * Add a spectrum reference to the small molecule object.
	 * @param specRef A SpecRef object.
	 */
	public void addSpecRef(SpecRef specRef) {
		if (this.specRef == null)
			this.specRef = new ArrayList<SpecRef>(1);
		
		this.specRef.add(specRef);
	}
	
	public void setSpecRefs(List<SpecRef> specRefs) {
		this.specRef = specRefs;
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
		StringBuffer mzTabString = new StringBuffer();
		List<SmallMoleculeTableField> fields = SmallMoleculeTableField.getOrderedFieldList();
		
		for (SmallMoleculeTableField field : fields) {			
			switch (field) {
				case ROW_PREFIX:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + SmallMoleculeTableField.ROW_PREFIX);
					break;
				case IDENTIFIER:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(identifier, "|"));
					break;
				case UNIT_ID:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(unitId));
					break;
				case CHEMICAL_FORMULA:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(chemicalFormula));
					break;
				case DESCRIPTION:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(description));
					break;
				case SMILES:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(smiles));
					break;
				case INCHI_KEY:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(inchiKey));
					break;
				case MASS_TO_CHARGE:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(massToCharge));
					break;
				case CHARGE:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(charge));
					break;
				case RETENTION_TIME:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(retentionTime, ","));
					break;
				case TAXID:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(taxid));
					break;
				case SPECIES:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(species));
					break;
				case DATABASE:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(database));
					break;
				case DATABASE_VERSION:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(databaseVersion));
					break;
				case RELIABILITY:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(reliability));
					break;
				case URI:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(uri));
					break;
				case SPEC_REF:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(specRef, "|"));
					break;
				case SEARCH_ENGINE:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(searchEngine));
					break;
				case SEARCH_ENGINE_SCORE:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + toField(searchEngineScore));
					break;
				case MODIFICATIONS:
					mzTabString.append((mzTabString.length() > 1 ? SEPARATOR : "") + arrayToField(modifications, ","));
					break;
			}
		}
		
		// process the abundance data
		mzTabString.append(quantDataToMztab(nSubsamples));
		
		// add the optional columns
		for (String optionalColumn : optionalColumns) {
			mzTabString.append(SEPARATOR + (custom.containsKey(optionalColumn) ? custom.get(optionalColumn) : MISSING));
		}
		
		// add the line terminator
		mzTabString.append(EOL);
		
		return mzTabString.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((abundance == null) ? 0 : abundance.hashCode());
		result = prime * result
				+ ((abundanceError == null) ? 0 : abundanceError.hashCode());
		result = prime * result
				+ ((abundanceStd == null) ? 0 : abundanceStd.hashCode());
		result = prime * result + ((charge == null) ? 0 : charge.hashCode());
		result = prime * result + ((custom == null) ? 0 : custom.hashCode());
		result = prime * result
				+ ((database == null) ? 0 : database.hashCode());
		result = prime * result
				+ ((databaseVersion == null) ? 0 : databaseVersion.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result
				+ ((chemicalFormula == null) ? 0 : chemicalFormula.hashCode());
		result = prime * result
				+ ((massToCharge == null) ? 0 : massToCharge.hashCode());
		result = prime * result
				+ ((modifications == null) ? 0 : modifications.hashCode());
		result = prime * result
				+ ((reliability == null) ? 0 : reliability.hashCode());
		result = prime * result
				+ ((retentionTime == null) ? 0 : retentionTime.hashCode());
		result = prime * result
				+ ((searchEngine == null) ? 0 : searchEngine.hashCode());
		result = prime
				* result
				+ ((searchEngineScore == null) ? 0 : searchEngineScore
						.hashCode());
		result = prime * result + ((species == null) ? 0 : species.hashCode());
		result = prime * result + ((taxid == null) ? 0 : taxid.hashCode());
		result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((specRef == null) ? 0 : specRef.hashCode());
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
		SmallMolecule other = (SmallMolecule) obj;
		if (abundance == null) {
			if (other.abundance != null)
				return false;
		} else if (!abundance.equals(other.abundance))
			return false;
		if (abundanceError == null) {
			if (other.abundanceError != null)
				return false;
		} else if (!abundanceError.equals(other.abundanceError))
			return false;
		if (abundanceStd == null) {
			if (other.abundanceStd != null)
				return false;
		} else if (!abundanceStd.equals(other.abundanceStd))
			return false;
		if (charge == null) {
			if (other.charge != null)
				return false;
		} else if (!charge.equals(other.charge))
			return false;
		if (custom == null) {
			if (other.custom != null)
				return false;
		} else if (!custom.equals(other.custom))
			return false;
		if (database == null) {
			if (other.database != null)
				return false;
		} else if (!database.equals(other.database))
			return false;
		if (databaseVersion == null) {
			if (other.databaseVersion != null)
				return false;
		} else if (!databaseVersion.equals(other.databaseVersion))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (chemicalFormula == null) {
			if (other.chemicalFormula != null)
				return false;
		} else if (!chemicalFormula.equals(other.chemicalFormula))
			return false;
		if (massToCharge == null) {
			if (other.massToCharge != null)
				return false;
		} else if (!massToCharge.equals(other.massToCharge))
			return false;
		if (modifications == null) {
			if (other.modifications != null)
				return false;
		} else if (!modifications.equals(other.modifications))
			return false;
		if (reliability == null) {
			if (other.reliability != null)
				return false;
		} else if (!reliability.equals(other.reliability))
			return false;
		if (retentionTime == null) {
			if (other.retentionTime != null)
				return false;
		} else if (!retentionTime.equals(other.retentionTime))
			return false;
		if (searchEngine == null) {
			if (other.searchEngine != null)
				return false;
		} else if (!searchEngine.equals(other.searchEngine))
			return false;
		if (searchEngineScore == null) {
			if (other.searchEngineScore != null)
				return false;
		} else if (!searchEngineScore.equals(other.searchEngineScore))
			return false;
		if (species == null) {
			if (other.species != null)
				return false;
		} else if (!species.equals(other.species))
			return false;
		if (taxid == null) {
			if (other.taxid != null)
				return false;
		} else if (!taxid.equals(other.taxid))
			return false;
		if (unitId == null) {
			if (other.unitId != null)
				return false;
		} else if (!unitId.equals(other.unitId))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (specRef == null) {
			if (other.specRef != null)
				return false;
		} else if (!specRef.equals(other.specRef))
			return false;
		return true;
	}
}
