package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;

/**
 * A SmallMolecule in a mzTab file.
 *
 * @author jg
 *
 */
public class SmallMolecule extends TableObject {

    private static final Logger logger = Logger.getLogger(SmallMolecule.class);
    /**
     * The 0-based index of the small molecule in the small molecule table.
     */
    private int index = -1;
    /**
     * Fields
     */
    private List<String> identifier;
    private String unitId;
    private String chemicalFormula;
    private String smiles;
    private String inchiKey;
    private String description;
    private Double massToCharge;
    private Integer charge;
    private List<Double> retentionTime;
    private Integer taxid;
    private String species;
    private String database;
    private String databaseVersion;
    private Integer reliability;
    private URI uri;
    private List<SpecRef> specRef;
    private ParamList searchEngine;
    private ParamList searchEngineScore;
    private List<Modification> modifications;
    // TODO: add support for is_decoy

    public SmallMolecule() {
    }

    /**
     * Creates a new SmallMolecule based on the parseTableLine.
     *
     * @param parsedTableLine
     * @throws MzTabParsingException
     */
    public SmallMolecule(Map<String, String> parsedTableLine) throws MzTabParsingException {
	try {
	    for (String fieldName : parsedTableLine.keySet()) {
		SmallMoleculeTableField field = SmallMoleculeTableField.getField(fieldName);
		String value = parsedTableLine.get(fieldName).trim();

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
			charge = parseIntegerField(value);
			break;
		    case RETENTION_TIME:
			retentionTime = parseDoubleArray(value, "\\|");
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
			if (reliability != null && (reliability < 1 || reliability > 3)) {
			    throw new MzTabParsingException("Invalid reliability " + reliability + ". Reliability must only be 1 (good), 2 (medium), and 3 (bad).");
			}
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
	} catch (Exception e) {
	    throw new MzTabParsingException("Failed to parse small molecule: " + e.getMessage(), e);
	}
    }

    /**
     * Creates a new SmallMolecule based on the parseTableLine.
     *
     * @param parsedTableLine
     * @param index The 0-based index of the SmallMolecule in the small molecule
     * table.
     * @throws MzTabParsingException
     */
    public SmallMolecule(Map<String, String> parsedTableLine, int index) throws MzTabParsingException {
	this(parsedTableLine);

	this.index = index;
    }

    /**
     * Parses a protein abundance field (abundance, stdev, stderror) and sets
     * the appropriate variable.
     *
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

    public Integer getCharge() {
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
     * Returns the 0-based index of the small molecule in the small molecule
     * table. Returns -1 in case the index wasn't set.
     *
     * @return The 0-based index of the small molecule in the small molecule
     * table. -1 in case it wasn't set.
     */
    public int getIndex() {
	return index;
    }

    /**
     * Set the 0-based index of the small molecule in the small molecule table.
     *
     * @param index The 0-based index of the small molecule in the small
     * molecule table.
     */
    public void setIndex(int index) {
	this.index = index;
    }

    public void setIdentifier(List<String> identifier) throws MzTabParsingException {
	for (String ident : identifier) {
	    checkStringValue(ident);
	}
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

    public void setCharge(Integer charge) {
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
	if (reliability < 1 || reliability > 3) {
	    throw new MzTabParsingException("Invalid reliability " + reliability + ". Reliability must only be 1 (good), 2 (medium), and 3 (bad).");
	}
	this.reliability = reliability;
    }

    public void setUri(URI uri) {
	this.uri = uri;
    }

    /**
     * Add a spectrum reference to the small molecule object.
     *
     * @param specRef A SpecRef object.
     */
    public void addSpecRef(SpecRef specRef) {
	if (this.specRef == null) {
	    this.specRef = new ArrayList<SpecRef>(1);
	}

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
     * Converts the small molecule object to an mzTab formatted String. The
     * fields are written in the order defined in the SmallMoleculeTableFields
     * ENUM. The header is not written.
     *
     * @param nSubsamples Defines the number of subsamples written. In case this
     * small molecule was not quantified for that many subsamples, MISSING is
     * written instead.
     * @param optionalColumns A list of optionalColumn headers. In case this
     * small molecule does not have a value for a given optional column, MISSING
     * is written.
     * @return The mzTab formatted string representing this peptide.
     */
    @Override
    public String toMzTab(int nSubsamples, List<String> optionalColumns) {
	StringBuilder mzTabString = new StringBuilder();
	List<SmallMoleculeTableField> fields = SmallMoleculeTableField.getOrderedFieldList();

	for (SmallMoleculeTableField field : fields) {
	    switch (field) {
		case ROW_PREFIX:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(SmallMoleculeTableField.ROW_PREFIX);
		    break;
		case IDENTIFIER:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(identifier, "|"));
		    break;
		case UNIT_ID:
		    StringBuilder append = mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(unitId));
		    break;
		case CHEMICAL_FORMULA:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(chemicalFormula));
		    break;
		case DESCRIPTION:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(description));
		    break;
		case SMILES:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(smiles));
		    break;
		case INCHI_KEY:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(inchiKey));
		    break;
		case MASS_TO_CHARGE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(massToCharge));
		    break;
		case CHARGE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(charge));
		    break;
		case RETENTION_TIME:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(retentionTime, "|"));
		    break;
		case TAXID:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(taxid));
		    break;
		case SPECIES:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(species));
		    break;
		case DATABASE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(database));
		    break;
		case DATABASE_VERSION:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(databaseVersion));
		    break;
		case RELIABILITY:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(reliability));
		    break;
		case URI:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(uri));
		    break;
		case SPEC_REF:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(specRef, "|"));
		    break;
		case SEARCH_ENGINE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(searchEngine));
		    break;
		case SEARCH_ENGINE_SCORE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(searchEngineScore));
		    break;
		case MODIFICATIONS:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(modifications, ","));
		    break;
	    }
	}

	// process the abundance data
	mzTabString.append(quantDataToMztab(nSubsamples));

	// add the optional columns
	for (String optionalColumn : optionalColumns) {
	    mzTabString.append(SEPARATOR).append(custom.containsKey(optionalColumn) ? custom.get(optionalColumn) : MISSING);
	}

	// add the line terminator
	mzTabString.append(EOL);

	return mzTabString.toString();
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 61 * hash + this.index;
	hash = 61 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
	hash = 61 * hash + (this.unitId != null ? this.unitId.hashCode() : 0);
	hash = 61 * hash + (this.chemicalFormula != null ? this.chemicalFormula.hashCode() : 0);
	hash = 61 * hash + (this.smiles != null ? this.smiles.hashCode() : 0);
	hash = 61 * hash + (this.inchiKey != null ? this.inchiKey.hashCode() : 0);
	hash = 61 * hash + (this.description != null ? this.description.hashCode() : 0);
	hash = 61 * hash + (this.massToCharge != null ? this.massToCharge.hashCode() : 0);
	hash = 61 * hash + (this.charge != null ? this.charge.hashCode() : 0);
	hash = 61 * hash + (this.retentionTime != null ? this.retentionTime.hashCode() : 0);
	hash = 61 * hash + (this.taxid != null ? this.taxid.hashCode() : 0);
	hash = 61 * hash + (this.species != null ? this.species.hashCode() : 0);
	hash = 61 * hash + (this.database != null ? this.database.hashCode() : 0);
	hash = 61 * hash + (this.databaseVersion != null ? this.databaseVersion.hashCode() : 0);
	hash = 61 * hash + (this.reliability != null ? this.reliability.hashCode() : 0);
	hash = 61 * hash + (this.uri != null ? this.uri.hashCode() : 0);
	hash = 61 * hash + (this.specRef != null ? this.specRef.hashCode() : 0);
	hash = 61 * hash + (this.searchEngine != null ? this.searchEngine.hashCode() : 0);
	hash = 61 * hash + (this.searchEngineScore != null ? this.searchEngineScore.hashCode() : 0);
	hash = 61 * hash + (this.modifications != null ? this.modifications.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SmallMolecule other = (SmallMolecule) obj;
	if (this.index != other.index) {
	    return false;
	}
	if (this.identifier != other.identifier && (this.identifier == null || !this.identifier.equals(other.identifier))) {
	    return false;
	}
	if ((this.unitId == null) ? (other.unitId != null) : !this.unitId.equals(other.unitId)) {
	    return false;
	}
	if ((this.chemicalFormula == null) ? (other.chemicalFormula != null) : !this.chemicalFormula.equals(other.chemicalFormula)) {
	    return false;
	}
	if ((this.smiles == null) ? (other.smiles != null) : !this.smiles.equals(other.smiles)) {
	    return false;
	}
	if ((this.inchiKey == null) ? (other.inchiKey != null) : !this.inchiKey.equals(other.inchiKey)) {
	    return false;
	}
	if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
	    return false;
	}
	if (this.massToCharge != other.massToCharge && (this.massToCharge == null || !this.massToCharge.equals(other.massToCharge))) {
	    return false;
	}
	if (this.charge != other.charge && (this.charge == null || !this.charge.equals(other.charge))) {
	    return false;
	}
	if (this.retentionTime != other.retentionTime && (this.retentionTime == null || !this.retentionTime.equals(other.retentionTime))) {
	    return false;
	}
	if (this.taxid != other.taxid && (this.taxid == null || !this.taxid.equals(other.taxid))) {
	    return false;
	}
	if ((this.species == null) ? (other.species != null) : !this.species.equals(other.species)) {
	    return false;
	}
	if ((this.database == null) ? (other.database != null) : !this.database.equals(other.database)) {
	    return false;
	}
	if ((this.databaseVersion == null) ? (other.databaseVersion != null) : !this.databaseVersion.equals(other.databaseVersion)) {
	    return false;
	}
	if (this.reliability != other.reliability && (this.reliability == null || !this.reliability.equals(other.reliability))) {
	    return false;
	}
	if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri))) {
	    return false;
	}
	if (this.specRef != other.specRef && (this.specRef == null || !this.specRef.equals(other.specRef))) {
	    return false;
	}
	if (this.searchEngine != other.searchEngine && (this.searchEngine == null || !this.searchEngine.equals(other.searchEngine))) {
	    return false;
	}
	if (this.searchEngineScore != other.searchEngineScore && (this.searchEngineScore == null || !this.searchEngineScore.equals(other.searchEngineScore))) {
	    return false;
	}
	if (this.modifications != other.modifications && (this.modifications == null || !this.modifications.equals(other.modifications))) {
	    return false;
	}
	return true;
    }
}
