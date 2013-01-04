package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;

/**
 * A protein in a mzTab file.
 *
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
    // TODO: add support for is_decoy

    /**
     * Creates a new empty protein object
     */
    public Protein() {
    }

    /**
     * Creates a new empty protein object based on a parsed protein table line.
     * This line must be a map with the column name as key and the respective
     * value as value.
     *
     * @param parsedTableLine A Map representing a parsed protein table line.
     */
    public Protein(Map<String, String> parsedTableLine) throws MzTabParsingException {
	try {
	    for (String fieldName : parsedTableLine.keySet()) {
		ProteinTableField field = ProteinTableField.getField(fieldName);
		String value = parsedTableLine.get(fieldName).trim();

		if (field == null) {
		    logger.warn("Unknown field <" + fieldName + "> in protein table.");
		    continue;
		}

		switch (field) {
		    case ACCESSION:
			accession = value;
			break;
		    case UNIT_ID:
			checkUnitId(value);
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
			// make sure the reliability is between 1-3
			if (reliability != null && (reliability < 1 || reliability > 3)) {
			    throw new MzTabParsingException("Invalid reliability " + reliability + ". Reliability must only be 1 (good), 2 (medium), and 3 (bad).");
			}
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
			goTerms = parseStringArray(value, "\\|");
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
	} catch (Exception e) {
	    throw new MzTabParsingException("Failed to parse protein: " + e.getMessage(), e);
	}
    }

    /**
     * Parses a protein abundance field (abundance, stdev, stderror) and sets
     * the appropriate variable.
     *
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
		abundance.put(subsampleIndex, doubleValue);
		break;
	    case PROTEIN_ABUNDANCE_STD:
		abundanceStd.put(subsampleIndex, doubleValue);
		break;
	    case PROTEIN_ABUNDANCE_STD_ERROR:
		abundanceError.put(subsampleIndex, doubleValue);
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

    public void setAccession(String accession) throws MzTabParsingException {
	checkStringValue(accession);
	this.accession = accession;
    }

    public void setUnitId(String unitId) throws MzTabParsingException {
	checkUnitId(unitId);
	this.unitId = unitId;
    }

    public void setDescription(String description) throws MzTabParsingException {
	checkStringValue(description);
	this.description = description;
    }

    public void setTaxid(String taxid) throws MzTabParsingException {
	checkStringValue(taxid);
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

    public void setSearchEngine(ParamList searchEngine) {
	this.searchEngine = searchEngine;
    }

    public void setSearchEngineScore(ParamList searchEngineScore) {
	this.searchEngineScore = searchEngineScore;
    }

    public void setReliability(Integer reliability) throws MzTabParsingException {
	// make sure the reliability is between 1-3
	if (reliability < 1 || reliability > 3) {
	    throw new MzTabParsingException("Invalid reliability " + reliability + ". Reliability must only be 1 (good), 2 (medium), and 3 (bad).");
	}
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

    public void setAmbiguityMembers(List<String> ambiguityMembers) throws MzTabParsingException {
	for (String string : ambiguityMembers) {
	    checkStringValue(string);
	}
	this.ambiguityMembers = ambiguityMembers;
    }

    public void setModifications(List<Modification> modifications) {
	this.modifications = modifications;
    }

    public void setUri(URI uri) {
	this.uri = uri;
    }

    public void setGoTerms(List<String> goTerms) throws MzTabParsingException {
	for (String value : goTerms) {
	    checkStringValue(value);
	}
	this.goTerms = goTerms;
    }

    public void setProteinCoverage(Double proteinCoverage) {
	this.proteinCoverage = proteinCoverage;
    }

    /**
     * Converts the protein object to an mzTab formatted String. The fields are
     * written in the order defined in the ProteinTableFields ENUM. The header
     * is not written.
     *
     * @param nSubsamples Defines the number of subsamples written. In case this
     * protein was not quantified for that many subsamples, MISSING is written
     * instead.
     * @param optionalColumns A list of optionalColumn headers. In case this
     * protein does not have a value for a given optional column, MISSING is
     * written.
     * @return The mzTab formatted string representing this protein.
     */
    @Override
    public String toMzTab(int nSubsamples, List<String> optionalColumns) {
	StringBuilder mzTabString = new StringBuilder();
	List<ProteinTableField> fields = ProteinTableField.getOrderedFieldList();

	for (ProteinTableField field : fields) {
	    switch (field) {
		case ROW_PREFIX:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(ProteinTableField.ROW_PREFIX);
		    break;
		case ACCESSION:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(accession));
		    break;
		case UNIT_ID:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(unitId));
		    break;
		case DESCRIPTION:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(description));
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
		case SEARCH_ENGINE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(searchEngine));
		    break;
		case SEARCH_ENGINE_SCORE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(searchEngineScore));
		    break;
		case RELIABILITY:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(reliability));
		    break;
		case NUM_PEPTIDES:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(numPeptides));
		    break;
		case NUM_PEPTIDES_DISTINCT:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(numPeptidesDistinct));
		    break;
		case NUM_PEPTIDES_UNAMBIGUOUS:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(numPeptidesUnambiguous));
		    break;
		case AMBIGUITY_MEMGERS:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(ambiguityMembers, ","));
		    break;
		case MODIFICATIONS:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(modifications, ","));
		    break;
		case URI:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(uri));
		    break;
		case GO_TERMS:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(goTerms, "|"));
		    break;
		case PROTEIN_COVERAGE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(proteinCoverage));
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
	int hash = 5;
	hash = 19 * hash + (this.accession != null ? this.accession.hashCode() : 0);
	hash = 19 * hash + (this.unitId != null ? this.unitId.hashCode() : 0);
	hash = 19 * hash + (this.description != null ? this.description.hashCode() : 0);
	hash = 19 * hash + (this.taxid != null ? this.taxid.hashCode() : 0);
	hash = 19 * hash + (this.species != null ? this.species.hashCode() : 0);
	hash = 19 * hash + (this.database != null ? this.database.hashCode() : 0);
	hash = 19 * hash + (this.databaseVersion != null ? this.databaseVersion.hashCode() : 0);
	hash = 19 * hash + (this.searchEngine != null ? this.searchEngine.hashCode() : 0);
	hash = 19 * hash + (this.searchEngineScore != null ? this.searchEngineScore.hashCode() : 0);
	hash = 19 * hash + (this.reliability != null ? this.reliability.hashCode() : 0);
	hash = 19 * hash + (this.numPeptides != null ? this.numPeptides.hashCode() : 0);
	hash = 19 * hash + (this.numPeptidesDistinct != null ? this.numPeptidesDistinct.hashCode() : 0);
	hash = 19 * hash + (this.numPeptidesUnambiguous != null ? this.numPeptidesUnambiguous.hashCode() : 0);
	hash = 19 * hash + (this.ambiguityMembers != null ? this.ambiguityMembers.hashCode() : 0);
	hash = 19 * hash + (this.modifications != null ? this.modifications.hashCode() : 0);
	hash = 19 * hash + (this.uri != null ? this.uri.hashCode() : 0);
	hash = 19 * hash + (this.goTerms != null ? this.goTerms.hashCode() : 0);
	hash = 19 * hash + (this.proteinCoverage != null ? this.proteinCoverage.hashCode() : 0);
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
	final Protein other = (Protein) obj;
	if ((this.accession == null) ? (other.accession != null) : !this.accession.equals(other.accession)) {
	    return false;
	}
	if ((this.unitId == null) ? (other.unitId != null) : !this.unitId.equals(other.unitId)) {
	    return false;
	}
	if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
	    return false;
	}
	if ((this.taxid == null) ? (other.taxid != null) : !this.taxid.equals(other.taxid)) {
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
	if (this.searchEngine != other.searchEngine && (this.searchEngine == null || !this.searchEngine.equals(other.searchEngine))) {
	    return false;
	}
	if (this.searchEngineScore != other.searchEngineScore && (this.searchEngineScore == null || !this.searchEngineScore.equals(other.searchEngineScore))) {
	    return false;
	}
	if (this.reliability != other.reliability && (this.reliability == null || !this.reliability.equals(other.reliability))) {
	    return false;
	}
	if (this.numPeptides != other.numPeptides && (this.numPeptides == null || !this.numPeptides.equals(other.numPeptides))) {
	    return false;
	}
	if (this.numPeptidesDistinct != other.numPeptidesDistinct && (this.numPeptidesDistinct == null || !this.numPeptidesDistinct.equals(other.numPeptidesDistinct))) {
	    return false;
	}
	if (this.numPeptidesUnambiguous != other.numPeptidesUnambiguous && (this.numPeptidesUnambiguous == null || !this.numPeptidesUnambiguous.equals(other.numPeptidesUnambiguous))) {
	    return false;
	}
	if (this.ambiguityMembers != other.ambiguityMembers && (this.ambiguityMembers == null || !this.ambiguityMembers.equals(other.ambiguityMembers))) {
	    return false;
	}
	if (this.modifications != other.modifications && (this.modifications == null || !this.modifications.equals(other.modifications))) {
	    return false;
	}
	if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri))) {
	    return false;
	}
	if (this.goTerms != other.goTerms && (this.goTerms == null || !this.goTerms.equals(other.goTerms))) {
	    return false;
	}
	if (this.proteinCoverage != other.proteinCoverage && (this.proteinCoverage == null || !this.proteinCoverage.equals(other.proteinCoverage))) {
	    return false;
	}
	return true;
    }
}
