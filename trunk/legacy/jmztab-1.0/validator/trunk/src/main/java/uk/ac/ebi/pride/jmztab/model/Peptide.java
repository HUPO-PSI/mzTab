package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;

public class Peptide extends TableObject {

    private final Logger logger = Logger.getLogger(Peptide.class);
    /**
     * The 0-based index of this peptide in the peptide table.
     */
    private int index = -1;
    /**
     * General attributes
     */
    private String sequence;
    private String accession;
    private String unitId;
    private Boolean unique;
    private String database;
    private String databaseVersion;
    private Param searchEngine;
    private ParamList searchEngineScore;
    private Integer reliability;
    private List<Modification> modification;
    private List<Double> retentionTime;
    private Integer charge;
    private Double massToCharge;
    private URI uri;
    private List<SpecRef> specRef;
    // TODO: add support for is_decoy when parameter is defined

    public Peptide() {
    }

    /**
     * Creates a new Peptide object from a parsedTableLine.
     *
     * @param parsedTableLine The parsed taclazzble line representing this
     * peptide.
     * @throws MzTabParsingException
     */
    public Peptide(Map<String, String> parsedTableLine) throws MzTabParsingException {
	try {
	    for (String fieldName : parsedTableLine.keySet()) {
		PeptideTableField field = PeptideTableField.getField(fieldName);
		String value = parsedTableLine.get(fieldName).trim();

		if (field == null) {
		    logger.warn("Unknown field <" + fieldName + "> encountered in peptide table.");
		    continue;
		}

		switch (field) {
		    case SEQUENCE:
			sequence = value;
			break;
		    case ACCESSION:
			accession = value;
			break;
		    case UNIT_ID:
			checkUnitId(value);
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
			// make sure the reliability is between 1-3
			if (reliability != null && (reliability < 1 || reliability > 3)) {
			    throw new MzTabParsingException("Invalid reliability " + reliability + ". Reliability must only be 1 (good), 2 (medium), and 3 (bad).");
			}
			break;
		    case MODIFICATIONS:
			modification = parseModifications(value);
			break;
		    case RETENTION_TIME:
			retentionTime = parseDoubleArray(value, "\\|");
			break;
		    case CHARGE:
			charge = parseIntegerField(value);
			break;
		    case MASS_TO_CHARGE:
			massToCharge = parseDoubleField(value);
			break;
		    case URI:
			uri = parseUriField(value);
			break;
		    case SPEC_REF:
			specRef = parseSpecRefArray(value);
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
	} catch (Exception e) {
	    throw new MzTabParsingException("Failed to parse peptide: " + e.getMessage(), e);
	}
    }

    /**
     * Creates a new Peptide object from a parsedTableLine.
     *
     * @param parsedTableLine The parsed table line representing this peptide.
     * @param index The 0-based index of the line in the peptide table.
     * @throws MzTabParsingException
     */
    public Peptide(Map<String, String> parsedTableLine, int index) throws MzTabParsingException {
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

    /**
     * Returns the peptide's sequence without any potential modification
     * definitions.
     *
     * @return The peptides sequence without any additional modification
     * specifications.
     */
    public String getCleanSequence() {
	String cleanSequence = sequence.replaceAll("\\([^)]*\\)", "");
	cleanSequence = cleanSequence.replaceAll("\\[[^]]*\\]", "");

	return cleanSequence;
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

    public Integer getCharge() {
	return charge;
    }

    public Double getMassToCharge() {
	return massToCharge;
    }

    public URI getUri() {
	return uri;
    }

    public List<SpecRef> getSpecRefs() {
	return specRef;
    }

    /**
     * Returns the 0-based index of the peptide in the peptide table. Returns -1
     * if the index wasn't set.
     *
     * @return The 0-based index of the peptide in the peptide table. -1 in case
     * the index wasn't set.
     */
    public int getIndex() {
	return index;
    }

    /**
     * Sets the 0-based index of the peptide in the peptide table.
     *
     * @param index The 0-based index of the peptide in the peptide table.
     */
    public void setIndex(int index) {
	this.index = index;
    }

    public void setSequence(String sequence) throws MzTabParsingException {
	checkStringValue(sequence);
	this.sequence = sequence;
    }

    public void setAccession(String accession) throws MzTabParsingException {
	checkStringValue(accession);
	this.accession = accession;
    }

    public void setUnitId(String unitId) throws MzTabParsingException {
	checkUnitId(unitId);
	this.unitId = unitId;
    }

    public void setUnique(Boolean unique) {
	this.unique = unique;
    }

    public void setDatabase(String database) throws MzTabParsingException {
	checkStringValue(database);
	this.database = database;
    }

    public void setDatabaseVersion(String databaseVersion) throws MzTabParsingException {
	checkStringValue(databaseVersion);
	this.databaseVersion = databaseVersion;
    }

    public void setSearchEngine(Param searchEngine) {
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

    public void setModification(List<Modification> modification) {
	this.modification = modification;
    }

    public void setRetentionTime(List<Double> retentionTime) {
	this.retentionTime = retentionTime;
    }

    public void setCharge(Integer charge) {
	this.charge = charge;
    }

    public void setMassToCharge(Double massToCharge) {
	this.massToCharge = massToCharge;
    }

    public void setUri(URI uri) {
	this.uri = uri;
    }

    /**
     * Add a spectrum reference to the peptide object.
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

    /**
     * Converts the peptide object to an mzTab formatted String. The fields are
     * written in the order defined in the PeptideTableFields ENUM. The header
     * is not written.
     *
     * @param nSubsamples Defines the number of subsamples written. In case this
     * peptide was not quantified for that many subsamples, MISSING is written
     * instead.
     * @param optionalColumns A list of optionalColumn headers. In case this
     * peptide does not have a value for a given optional column, MISSING is
     * written.
     * @return The mzTab formatted string representing this peptide.
     */
    @Override
    public String toMzTab(int nSubsamples, List<String> optionalColumns) {
	StringBuilder mzTabString = new StringBuilder();
	List<PeptideTableField> fields = PeptideTableField.getOrderedFieldList();

	for (PeptideTableField field : fields) {
	    switch (field) {
		case ROW_PREFIX:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(PeptideTableField.ROW_PREFIX);
		    break;
		case SEQUENCE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(sequence));
		    break;
		case ACCESSION:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(accession));
		    break;
		case UNIT_ID:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(unitId));
		    break;
		case UNIQUE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(unique));
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
		case MODIFICATIONS:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(modification, ","));
		    break;
		case RETENTION_TIME:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(retentionTime, "|"));
		    break;
		case CHARGE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(charge));
		    break;
		case MASS_TO_CHARGE:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(massToCharge));
		    break;
		case URI:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(toField(uri));
		    break;
		case SPEC_REF:
		    mzTabString.append(mzTabString.length() > 1 ? SEPARATOR : "").append(arrayToField(specRef, "|"));
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
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((abundance == null) ? 0 : abundance.hashCode());
	result = prime * result
		+ ((abundanceError == null) ? 0 : abundanceError.hashCode());
	result = prime * result
		+ ((abundanceStd == null) ? 0 : abundanceStd.hashCode());
	result = prime * result
		+ ((accession == null) ? 0 : accession.hashCode());
	result = prime * result + ((charge == null) ? 0 : charge.hashCode());
	result = prime * result + ((custom == null) ? 0 : custom.hashCode());
	result = prime * result
		+ ((database == null) ? 0 : database.hashCode());
	result = prime * result
		+ ((databaseVersion == null) ? 0 : databaseVersion.hashCode());
	result = prime * result
		+ ((massToCharge == null) ? 0 : massToCharge.hashCode());
	result = prime * result
		+ ((modification == null) ? 0 : modification.hashCode());
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
	result = prime * result
		+ ((sequence == null) ? 0 : sequence.hashCode());
	result = prime * result + ((unique == null) ? 0 : unique.hashCode());
	result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
	result = prime * result + ((uri == null) ? 0 : uri.hashCode());
	result = prime * result + ((uri == null) ? 0 : specRef.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	Peptide other = (Peptide) obj;
	if (abundance == null) {
	    if (other.abundance != null) {
		return false;
	    }
	} else if (!abundance.equals(other.abundance)) {
	    return false;
	}
	if (abundanceError == null) {
	    if (other.abundanceError != null) {
		return false;
	    }
	} else if (!abundanceError.equals(other.abundanceError)) {
	    return false;
	}
	if (abundanceStd == null) {
	    if (other.abundanceStd != null) {
		return false;
	    }
	} else if (!abundanceStd.equals(other.abundanceStd)) {
	    return false;
	}
	if (accession == null) {
	    if (other.accession != null) {
		return false;
	    }
	} else if (!accession.equals(other.accession)) {
	    return false;
	}
	if (charge == null) {
	    if (other.charge != null) {
		return false;
	    }
	} else if (!charge.equals(other.charge)) {
	    return false;
	}
	if (custom == null) {
	    if (other.custom != null) {
		return false;
	    }
	} else if (!custom.equals(other.custom)) {
	    return false;
	}
	if (database == null) {
	    if (other.database != null) {
		return false;
	    }
	} else if (!database.equals(other.database)) {
	    return false;
	}
	if (databaseVersion == null) {
	    if (other.databaseVersion != null) {
		return false;
	    }
	} else if (!databaseVersion.equals(other.databaseVersion)) {
	    return false;
	}
	if (massToCharge == null) {
	    if (other.massToCharge != null) {
		return false;
	    }
	} else if (!massToCharge.equals(other.massToCharge)) {
	    return false;
	}
	if (modification == null) {
	    if (other.modification != null) {
		return false;
	    }
	} else if (!modification.equals(other.modification)) {
	    return false;
	}
	if (reliability == null) {
	    if (other.reliability != null) {
		return false;
	    }
	} else if (!reliability.equals(other.reliability)) {
	    return false;
	}
	if (retentionTime == null) {
	    if (other.retentionTime != null) {
		return false;
	    }
	} else if (!retentionTime.equals(other.retentionTime)) {
	    return false;
	}
	if (searchEngine == null) {
	    if (other.searchEngine != null) {
		return false;
	    }
	} else if (!searchEngine.equals(other.searchEngine)) {
	    return false;
	}
	if (searchEngineScore == null) {
	    if (other.searchEngineScore != null) {
		return false;
	    }
	} else if (!searchEngineScore.equals(other.searchEngineScore)) {
	    return false;
	}
	if (sequence == null) {
	    if (other.sequence != null) {
		return false;
	    }
	} else if (!sequence.equals(other.sequence)) {
	    return false;
	}
	if (unique == null) {
	    if (other.unique != null) {
		return false;
	    }
	} else if (!unique.equals(other.unique)) {
	    return false;
	}
	if (unitId == null) {
	    if (other.unitId != null) {
		return false;
	    }
	} else if (!unitId.equals(other.unitId)) {
	    return false;
	}
	if (uri == null) {
	    if (other.uri != null) {
		return false;
	    }
	} else if (!uri.equals(other.uri)) {
	    return false;
	}
	if (specRef == null) {
	    if (other.specRef != null) {
		return false;
	    }
	} else if (!specRef.equals(other.specRef)) {
	    return false;
	}
	return true;
    }
}
