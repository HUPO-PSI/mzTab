package uk.ac.ebi.pride.jmztab.model;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jmztab.MzTabFile;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unit {

    private Logger logger = Logger.getLogger(Unit.class);
    /**
     * Pattern to parse meta-data lines. It extracts the line type, UNIT_ID,
     * SUB_ID (null if not present), and the field name and value.
     */
    private static final Pattern MZTAB_LINE_PATTERN = Pattern.compile("^(\\w{3})\t([^-]+)-(sub\\[\\d+\\])?-?([^\t]+)\t(.+)");
    /**
     * Unit attributes
     */
    private String unitId;
    private String title;
    private String description;
    private List<ParamList> sampleProcessing;
    private List<Instrument> instrument;
    private List<Param> software;
    private Map<Integer, List<String>> softwareSetting;
    private ParamList falseDiscoveryRate;
    /**
     * DOIs must be prefixed by "doi:", PubMed ids by "pubmed:"
     */
    private List<String> publication;
    private List<Contact> contact;
    private URI uri;
    private ParamList mod;
    private Param quantificationMethod;
    private Param proteinQuantificationUnit;
    private Param peptideQuantificationUnit;
    private List<Param> customParams;
    private List<Param> species;
    private List<Param> tissue;
    private List<Param> cellType;
    private List<Param> disease;
    private List<Subsample> subsamples;
    private Map<Integer, MsFile> msFiles;

    /**
     * Constructur constructing an empty Uni.
     */
    public Unit() {
    }

    /**
     * Parses the given mztab string and sets the various properties
     * accordingly. Properties that are already set and not defined in the
     * passed string are not altered. The only exception to this rule is the
     * unit id: once the unit id was set unmarshalling properties assigned to a
     * different unit causes a parsing exception to be thrown.
     *
     * @param mzTabString
     * @throws MzTabParsingException Thrown on any parsing error.
     */
    public void unmarshall(String mzTabString) throws MzTabParsingException {
	// parse the string line by line
	String[] lines = mzTabString.split("\r?\n");

	for (String line : lines) {
	    // ignore empty and non-metadata lines
	    if (line.trim().length() < 1 || !"MTD".equals(line.substring(0, 3))) {
		continue;
	    }

	    // parse the line
	    Matcher matcher = MZTAB_LINE_PATTERN.matcher(line);

	    // throw a parsing exception if the line couldn't be parsed
	    if (!matcher.find()) {
		throw new MzTabParsingException("Invalid meta-data line encountered: <" + line + ">");
	    }

	    // get the various fields
	    String theUnitId = matcher.group(2).trim();
	    String subId = matcher.group(3);
	    String field = matcher.group(4);
	    String value = matcher.group(5);

	    TableObject.checkUnitId(theUnitId);

	    if (subId != null) {
		subId = subId.trim();
	    }
	    if (field != null) {
		field = field.trim();
	    }
	    if (value != null) {
		value = value.trim();
	    }

	    // check that the unitId didn't change - if it wasn't set yet, set it
	    if (this.unitId == null) {
		this.unitId = theUnitId;
	    } else if (!this.unitId.equals(theUnitId)) {
		throw new MzTabParsingException("Metadata line passed to Unit object (id = " + this.unitId + ") with a different UNIT_ID (" + theUnitId + ")");
	    }

	    // parse the field
	    parseField(subId, field, value);
	}
    }

    /**
     * Checks if the given value is valid for the metadata section.
     *
     * ToDo: should perhaps be a public static utilities method.
     *
     * @param value
     */
    private void checkStringValue(String value) throws MzTabParsingException {
	if (value == null) {
	    return;
	}

	if (value.contains("\n") || value.contains("\r")) {
	    throw new MzTabParsingException("Invalid field value. Field values must not contain new-line characters.");
	}
    }

    private void parseField(String subId, String field, String value) throws MzTabParsingException {
	logger.debug("parsing field: subId = " + subId + ", field = " + field + ", value = " + value);

	try {
	    // simple fields with only one value
	    if ("title".equals(field)) {
		title = value.trim();
	    } else if ("description".equals(field) && subId == null) {
		description = value.trim();
	    } else if ("false_discovery_rate".equals(field)) {
		falseDiscoveryRate = new ParamList(value);
	    } else if ("uri".equals(field)) {
		uri = new URI(value);
	    } else if ("mod".equals(field)) {
		mod = new ParamList(value);
	    } else if ("quantification_method".equals(field)) {
		quantificationMethod = new Param(value);
	    } else if ("protein-quantification_unit".equals(field)) {
		proteinQuantificationUnit = new Param(value);
	    } else if ("peptide-quantification_unit".equals(field)) {
		peptideQuantificationUnit = new Param(value);
	    } /**
	     * Complex fields with multiple values
	     */
	    // sample processing
	    else if (field.startsWith("sample_processing")) {
		int sampleProcessingIndex = Integer.parseInt(field.substring(18, field.length() - 1)); // extract the processing step number
		// create the array if necessary
		if (sampleProcessing == null) {
		    sampleProcessing = new ArrayList<ParamList>();
		}
		// set the param
		sampleProcessing.add(sampleProcessingIndex - 1, new ParamList(value));
	    } // instruments
	    else if (field.startsWith("instrument")) {
		// get the instrument's index
		int instrumentIndex = Integer.parseInt(field.substring(11, field.indexOf(']', 11)));
		// create the instrument array if necessary
		if (instrument == null) {
		    instrument = new ArrayList<Instrument>();
		}
		// create the instrument if necessary
		if (instrument.get(instrumentIndex - 1) == null) {
		    instrument.add(instrumentIndex - 1, new Instrument());
		}
		// check which value is set
		if (field.endsWith("name")) {
		    instrument.get(instrumentIndex - 1).setName(new Param(value));
		} else if (field.endsWith("source")) {
		    instrument.get(instrumentIndex - 1).setSource(new Param(value));
		} else if (field.endsWith("analyzer")) {
		    instrument.get(instrumentIndex - 1).setAnalyzer(new Param(value));
		} else if (field.endsWith("detector")) {
		    instrument.get(instrumentIndex - 1).setDetector(new Param(value));
		}
	    } // software
	    else if (field.startsWith("software")) {
		// get the software's 1-based index
		int softwareIndex = Integer.parseInt(field.substring(9, field.length() - 1));
		// create the software array if necessary
		if (software == null) {
		    software = new ArrayList<Param>();
		}
		// add the software
		software.add(softwareIndex - 1, new Param(value));
	    } // software[1-n]-setting
	    else if (field.startsWith("software") && field.contains("]-setting")) {
		// get the software's 1-based index
		int softwareIndex = Integer.parseInt(field.substring(9, field.length() - 1));
		// create the software map if necessary
		if (softwareSetting == null) {
		    softwareSetting = new HashMap<Integer, List<String>>();
		}

		// create the list for this software
		if (!softwareSetting.containsKey(softwareIndex)) {
		    softwareSetting.put(softwareIndex, new ArrayList<String>());
		}
		// add the setting
		softwareSetting.get(softwareIndex).add(value);
	    } // publication
	    else if (field.equals("publication")) {
		// split the string
		String[] publications = value.trim().split("\\|");
		// create the publications array if necessary
		if (publication == null) {
		    publication = new ArrayList<String>(publications.length);
		}
		// add the publications
		for (String pub : publications) {
		    publication.add(pub);
		}
	    } // contact
	    else if (field.startsWith("contact")) {
		// get the instrument's index
		int contactIndex = Integer.parseInt(field.substring(8, field.indexOf(']', 8)));
		// create the instrument array if necessary
		if (contact == null) {
		    contact = new ArrayList<Contact>();
		}
		// create the instrument if necessary
		if (contact.size() < contactIndex) {
		    contact.add(contactIndex - 1, new Contact());
		}
		// check which value is set
		if (field.endsWith("name")) {
		    contact.get(contactIndex - 1).setName(value.trim());
		} else if (field.endsWith("email")) {
		    contact.get(contactIndex - 1).setEmail(value.trim());
		} else if (field.endsWith("affiliation")) {
		    contact.get(contactIndex - 1).setAffiliation(value.trim());
		}
	    } // ms_file
	    else if (field.startsWith("ms_file")) {
		// get the instrument's index
		int msFileIndex = Integer.parseInt(field.substring(8, field.indexOf(']', 8)));
		// create the instrument array if necessary
		if (msFiles == null) {
		    msFiles = new HashMap<Integer, MsFile>();
		}
		// create the instrument if necessary
		if (msFiles.size() < msFileIndex) {
		    msFiles.put(msFileIndex - 1, new MsFile());
		}
		// check which value is set
		if (field.endsWith("id_format")) {
		    msFiles.get(msFileIndex - 1).setIdFormat(new Param(value));
		} else if (field.endsWith("format")) {
		    msFiles.get(msFileIndex - 1).setFormat(new Param(value));
		} else if (field.endsWith("location")) {
		    msFiles.get(msFileIndex - 1).setLocation(value.trim());
		}
	    } // TODO: define how -custom params are handled and react on that
	    else if (field.equals("custom")) {
		if (customParams == null) {
		    customParams = new ArrayList<Param>();
		}

		customParams.add(new Param(value));
	    } // species, tissue, cell type, disease - on the unit level
	    else if (subId == null && field.startsWith("species")) {
		// get the instrument's index
		int speciesIndex = Integer.parseInt(field.substring(8, field.length() - 1));
		// create the instrument array if necessary
		if (species == null) {
		    species = new ArrayList<Param>();
		}

		species.add(speciesIndex - 1, new Param(value));
	    } else if (subId == null && field.startsWith("tissue")) {
		// get the instrument's index
		int tissueIndex = Integer.parseInt(field.substring(7, field.length() - 1));
		// create the instrument array if necessary
		if (tissue == null) {
		    tissue = new ArrayList<Param>();
		}

		tissue.add(tissueIndex - 1, new Param(value));
	    } else if (subId == null && field.startsWith("cell_type")) {
		// get the instrument's index
		int cellTypeIndex = Integer.parseInt(field.substring(10, field.length() - 1));
		// create the instrument array if necessary
		if (cellType == null) {
		    cellType = new ArrayList<Param>();
		}

		cellType.add(cellTypeIndex - 1, new Param(value));
	    } else if (subId == null && field.startsWith("disease")) {
		// get the instrument's index
		int diseaseIndex = Integer.parseInt(field.substring(8, field.length() - 1));
		// create the instrument array if necessary
		if (disease == null) {
		    disease = new ArrayList<Param>();
		}

		disease.add(diseaseIndex - 1, new Param(value));
	    } /**
	     * Parse subsample specific data
	     */
	    else if (subId != null) {
		// extract the index
		int subIndex = Integer.parseInt(subId.substring(4, subId.length() - 1));
		// make sure the index is greater than 0
		// (it should be a 1 based index, and the code will break if this is not the case)
		if (subIndex < 1) {
		    throw new MzTabParsingException("Found sub sample index smaller than 1!");
		}
		// make sure the subsample array exists
		if (subsamples == null) {
		    subsamples = new ArrayList<Subsample>();
		}
		// make sure this subsample already exists
		if (subsamples.size() < subIndex) {
		    subsamples.add(subIndex - 1, new Subsample(this.unitId, subIndex));
		}

		Subsample subsample = subsamples.get(subIndex - 1);

		// parse the field
		if ("description".equals(field)) {
		    subsample.setDescription(value.trim());
		} else if ("quantification_reagent".equals(field)) {
		    subsample.setQuantificationReagent(new Param(value));
		} else if ("custom".equals(field)) {
		    if (subsample.getCustomParams() == null) {
			subsample.setCustomParams(new ArrayList<Param>(1));
		    }
		    subsample.getCustomParams().add(new Param(value));
		} else if (field.startsWith("species")) {
		    // get the instrument's index
		    int speciesIndex = Integer.parseInt(field.substring(8, field.length() - 1));
		    // create the instrument array if necessary
		    if (subsample.getSpecies() == null) {
			subsample.setSpecies(new ArrayList<Param>());
		    }

		    subsample.getSpecies().add(speciesIndex - 1, new Param(value));
		} else if (field.startsWith("tissue")) {
		    // get the instrument's index
		    int tissueIndex = Integer.parseInt(field.substring(7, field.length() - 1));
		    // create the instrument array if necessary
		    if (subsample.getTissue() == null) {
			subsample.setTissue(new ArrayList<Param>());
		    }

		    subsample.getTissue().add(tissueIndex - 1, new Param(value));
		} else if (field.startsWith("cell_type")) {
		    // get the instrument's index
		    int cellTypeIndex = Integer.parseInt(field.substring(10, field.length() - 1));
		    // create the instrument array if necessary
		    if (subsample.getCellType() == null) {
			subsample.setCellType(new ArrayList<Param>());
		    }

		    subsample.getCellType().add(cellTypeIndex - 1, new Param(value));
		} else if (field.startsWith("disease")) {
		    // get the instrument's index
		    int diseaseIndex = Integer.parseInt(field.substring(8, field.length() - 1));
		    // create the instrument array if necessary
		    if (subsample.getDisease() == null) {
			subsample.setDisease(new ArrayList<Param>());
		    }

		    subsample.getDisease().add(diseaseIndex - 1, new Param(value));
		}

	    } else {
		logger.warn("Unknown unit field encountered: " + field);
	    }
	} catch (Exception e) {
	    throw new MzTabParsingException("Failed to parse mztab metadata field.", e);
	}
    }

    public Logger getLogger() {
	return logger;
    }

    public static Pattern getMztabLinePattern() {
	return MZTAB_LINE_PATTERN;
    }

    public String getUnitId() {
	return unitId;
    }

    public String getTitle() {
	return title;
    }

    public String getDescription() {
	return description;
    }

    public List<ParamList> getSampleProcessing() {
	return sampleProcessing;
    }

    public List<Instrument> getInstrument() {
	return instrument;
    }

    public List<Param> getSoftware() {
	return software;
    }

    public List<String> getSoftwareSettings(int softwareIndex) {
	if (!softwareSetting.containsKey(softwareIndex)) {
	    return Collections.EMPTY_LIST;
	}

	return softwareSetting.get(softwareIndex);
    }

    public ParamList getFalseDiscoveryRate() {
	return falseDiscoveryRate;
    }

    public List<String> getPublication() {
	return publication;
    }

    public List<Contact> getContact() {
	return contact;
    }

    public URI getUri() {
	return uri;
    }

    public ParamList getMod() {
	return mod;
    }

    public Param getQuantificationMethod() {
	return quantificationMethod;
    }

    public Param getProteinQuantificationUnit() {
	return proteinQuantificationUnit;
    }

    public Param getPeptideQuantificationUnit() {
	return peptideQuantificationUnit;
    }

    public List<Param> getCustomParams() {
	return customParams;
    }

    public List<Param> getSpecies() {
	return species;
    }

    public List<Param> getTissue() {
	return tissue;
    }

    public List<Param> getCellType() {
	return cellType;
    }

    public List<Param> getDisease() {
	return disease;
    }

    /**
     * Returns the subsample with the given index. In case the subsample doesn't
     * exist, null is returned.
     *
     * @param subsampleId The subsamples index.
     * @return
     */
    public Subsample getSubsample(Integer subsampleId) {
	if (subsamples == null) {
	    return null;
	}

	for (Subsample s : subsamples) {
	    if (s.getSubsampleIndex() == subsampleId) {
		return s;
	    }
	}

	return null;
    }

    /**
     * Retrieves the MsFile reference with the given index. Returns null in case
     * the reference does not exist.
     *
     * @param index The MsFile's index. Must be >= 1.
     * @return The MsFile object representing the reference or null in case it
     * does not exist.
     * @throws MzTabParsingException Thrown in case an invalid index is passed.
     */
    public MsFile getMsFile(int index) throws MzTabParsingException {
	if (index < 1) {
	    throw new MzTabParsingException("MsFile indexes must be greater or equal to 1.");
	}

	return msFiles.get(index - 1);
    }

    /**
     * Adds the given subsample. In case a subsample with the same index already
     * exists, this subample is replaced.
     *
     * @param s
     */
    public void setSubsample(Subsample s) {
	if (subsamples == null) {
	    subsamples = new ArrayList<Subsample>(1);
	}

	// check if the subsample already exists
	for (int i = 0; i < subsamples.size(); i++) {
	    // if a subsample with the same index already exists, replace it
	    if (subsamples.get(i).getSubsampleIndex() == s.getSubsampleIndex()) {
		subsamples.set(i, s);
		return;
	    }
	}

	// as the subsample wasn't set, add this one
	subsamples.add(s);
    }

    /**
     * Adds the given subsamples to the unit. In case there are multiple
     * subsamples with the same index only one of them is being added.
     *
     * @param subsamples
     */
    public void setSubsamples(Collection<Subsample> subsamples) {
	for (Subsample s : subsamples) {
	    setSubsample(s);
	}
    }

    public List<Subsample> getSubsamples() {
	return subsamples;
    }

    public void setLogger(Logger logger) {
	this.logger = logger;
    }

    public void setUnitId(String unitId) throws MzTabParsingException {
	TableObject.checkUnitId(unitId);
	this.unitId = unitId;
    }

    public void setTitle(String title) throws MzTabParsingException {
	if (title == null) {
	    return;
	}
	checkStringValue(title);
	this.title = title;
    }

    public void setDescription(String description) throws MzTabParsingException {
	if (description == null) {
	    return;
	}
	checkStringValue(description);
	this.description = description;
    }

    public void setSampleProcessing(List<ParamList> sampleProcessing) {
	this.sampleProcessing = sampleProcessing;
    }

    public void setInstrument(List<Instrument> instrument) {
	this.instrument = instrument;
    }

    public void setSoftware(List<Param> software) {
	this.software = software;
    }

    /**
     * Removes all settings for the specified software.
     *
     * @param softwareIndex The software's 1-based index.
     */
    public void removeSoftwareSettings(int softwareIndex) {
	if (softwareIndex < 1) {
	    throw new IllegalArgumentException("Software index must be 1-based.");
	}

	if (softwareSetting.containsKey(softwareIndex)) {
	    softwareSetting.remove(softwareIndex);
	}
    }

    /**
     * Sets the software setting(s) for the specified software item. To remove
     * all settings from a software item, either null or an empty list must be
     * passed as parameter.
     *
     * @param softwareIndex The 1-based index of the software.
     * @param settings List of strings representing the software's settings.
     */
    public void setSoftwareSettings(int softwareIndex, List<String> settings) {
	if (softwareIndex < 1) {
	    throw new IllegalArgumentException("Software index must be 1-based.");
	}

	if (softwareSetting == null) {
	    softwareSetting = new HashMap<Integer, List<String>>();
	}

	softwareSetting.put(softwareIndex, settings);
    }

    /**
     * Adds a setting to the specified software.
     *
     * @param softwareIndex The 1-based index of the software.
     * @param setting The setting to add.
     */
    public void addSoftwareSetting(int softwareIndex, String setting) {
	if (softwareIndex < 1) {
	    throw new IllegalArgumentException("Software index must be 1-based.");
	}

	if (softwareSetting == null) {
	    softwareSetting = new HashMap<Integer, List<String>>();
	}

	if (!softwareSetting.containsKey(softwareIndex)) {
	    softwareSetting.put(softwareIndex, new ArrayList<String>());
	}

	softwareSetting.get(softwareIndex).add(setting);
    }

    public void setFalseDiscoveryRate(ParamList falseDiscoveryRate) {
	this.falseDiscoveryRate = falseDiscoveryRate;
    }

    public void setPublication(List<String> publication) throws MzTabParsingException {
	if (publication == null || publication.isEmpty()) {
	    return;
	}
	List<String> publications = new ArrayList<String>();
	for (String value : publication) {
	    if (value != null) {
		checkStringValue(value);
		if (!value.startsWith("pubmed:") && !value.startsWith("doi:")) {
		    throw new MzTabParsingException("Invalid reference. References must be in the format 'pubmed:[PUBMED ID]' or 'doi:[DOI]'.");
		}
		// only add a publication if it passed the checks
		// e.g. not null, valid format (see checkStringValue method) and starts with pubmed: or doi:
		publications.add(value);
	    }
	}
	this.publication = publications;
    }

    public void setContact(List<Contact> contact) {
	this.contact = contact;
    }

    public void setUri(URI uri) {
	this.uri = uri;
    }

    public void setMod(ParamList mod) {
	this.mod = mod;
    }

    public void setQuantificationMethod(Param quantificationMethod) {
	this.quantificationMethod = quantificationMethod;
    }

    public void setProteinQuantificationUnit(Param proteinQuantificationUnit) {
	this.proteinQuantificationUnit = proteinQuantificationUnit;
    }

    public void setPeptideQuantificationUnit(Param peptideQuantificationUnit) {
	this.peptideQuantificationUnit = peptideQuantificationUnit;
    }

    public void setCustomParams(List<Param> customParams) {
	this.customParams = customParams;
    }

    public void setSpecies(List<Param> species) {
	this.species = species;
    }

    public void setTissue(List<Param> tissue) {
	this.tissue = tissue;
    }

    public void setCellType(List<Param> cellType) {
	this.cellType = cellType;
    }

    public void setDisease(List<Param> disease) {
	this.disease = disease;
    }

    /**
     * Sets a reference to an external MS file. To unset a file reference the
     * MsFile object should be set to null.
     *
     * @param index The msFile's index. Must be >= 1.
     * @param msFile The new ms file object. Null to remove the reference from
     * the file.
     * @throws MzTabParsingException
     */
    public void setMsFile(int index, MsFile msFile) throws MzTabParsingException {
	if (index < 1) {
	    throw new MzTabParsingException("MsFile index must be greater or equal to 1.");
	}

	if (msFiles == null) {
	    msFiles = new HashMap<Integer, MsFile>();
	}

	if (msFile == null) {
	    msFiles.remove(index - 1);
	} else {
	    msFiles.put(index - 1, msFile);
	}
    }

    /**
     * Converts the given meta-data to an mzTab formatted string.
     *
     * @return
     */
    public String toMzTab() {
	StringBuilder mzTab = new StringBuilder();

	if (title != null) {
	    mzTab.append(createField("title", title));
	}
	if (description != null) {
	    mzTab.append(createField("description", description));
	}
	// sample processing
	if (sampleProcessing != null) {
	    for (Integer i = 1; i <= sampleProcessing.size(); i++) {
		mzTab.append(createField(String.format("sample_processing[%d]", i), sampleProcessing.get(i - 1)));
	    }
	}
	// instrument
	if (instrument != null) {
	    for (Integer i = 1; i <= instrument.size(); i++) {
		mzTab.append(createField(String.format("instrument[%d]-source", i), instrument.get(i - 1).getName()));
		mzTab.append(createField(String.format("instrument[%d]-source", i), instrument.get(i - 1).getSource()));
		mzTab.append(createField(String.format("instrument[%d]-analyzer", i), instrument.get(i - 1).getAnalyzer()));
		mzTab.append(createField(String.format("instrument[%d]-detector", i), instrument.get(i - 1).getDetector()));
	    }
	}
	// software + software[1-n]-setting
	if (software != null) {
	    for (Integer i = 1; i <= software.size(); i++) {
		mzTab.append(createField(String.format("software[%d]", i), software.get(i - 1)));

		// write out the settings for the specified software
		if (softwareSetting != null && softwareSetting.get(i) != null) {
		    List<String> settings = softwareSetting.get(i);
		    for (String s : settings) {
			mzTab.append(createField(String.format("software[%d]-setting"), s));
		    }
		}
	    }


	}
	// false discovery rate
	if (falseDiscoveryRate != null) {
	    mzTab.append(createField("false_discovery_rate", falseDiscoveryRate));
	}
	// publication
	if (publication != null && publication.size() > 0) {
	    String string = "";

	    for (String p : publication) {
		string += (string.length() > 1 ? "," : "") + p;
	    }

	    mzTab.append(createField("publication", string));
	}
	// contact
	if (contact != null) {
	    for (int i = 1; i <= contact.size(); i++) {
		mzTab.append(createField(String.format("contact[%d]-name", i), contact.get(i - 1).getName()));
		mzTab.append(createField(String.format("contact[%d]-affiliation", i), contact.get(i - 1).getAffiliation()));
		mzTab.append(createField(String.format("contact[%d]-email", i), contact.get(i - 1).getEmail()));
	    }
	}
	// uri
	if (uri != null) {
	    mzTab.append(createField("uri", uri));
	}
	// mods
	if (mod != null) {
	    mzTab.append(createField("mod", mod));
	}
	// quantification method
	if (quantificationMethod != null) {
	    mzTab.append(createField("quantification_method", quantificationMethod));
	}
	// protein quant unit
	if (proteinQuantificationUnit != null) {
	    mzTab.append(createField("protein-quantification_unit", proteinQuantificationUnit));
	}
	// peptide quant unit
	if (peptideQuantificationUnit != null) {
	    mzTab.append(createField("peptide-quantification_unit", peptideQuantificationUnit));
	}
	// ms files
	if (msFiles != null) {
	    List<Integer> ids = new ArrayList<Integer>(msFiles.keySet());
	    Collections.sort(ids);

	    for (Integer index : ids) {
		mzTab.append(createField(String.format("ms_file[%d]-format", index + 1), msFiles.get(index).getFormat()));
		mzTab.append(createField(String.format("ms_file[%d]-location", index + 1), msFiles.get(index).getLocation()));
		mzTab.append(createField(String.format("ms_file[%d]-id_format", index + 1), msFiles.get(index).getIdFormat()));
	    }
	}
	// custom
	if (customParams != null) {
	    for (Param p : customParams) {
		mzTab.append(createField("custom", p));
	    }
	}
	// species
	if (species != null) {
	    for (int i = 1; i <= species.size(); i++) {
		mzTab.append(createField(String.format("species[%d]", i), species.get(i - 1)));
	    }
	}
	// tissue
	if (tissue != null) {
	    for (int i = 1; i <= tissue.size(); i++) {
		mzTab.append(createField(String.format("tissue[%d]", i), tissue.get(i - 1)));
	    }
	}
	// cell_type
	if (cellType != null) {
	    for (int i = 1; i <= cellType.size(); i++) {
		mzTab.append(createField(String.format("cell_type[%d]", i), cellType.get(i - 1)));
	    }
	}
	// disease
	if (disease != null) {
	    for (int i = 1; i <= disease.size(); i++) {
		mzTab.append(createField(String.format("disease[%d]", i), disease.get(i - 1)));
	    }
	}
	// subsamples
	if (subsamples != null) {
	    for (Subsample s : subsamples) {
		mzTab.append(s.toMzTab());
	    }
	}

	return mzTab.toString();
    }

    private String createField(String fieldName, Object value) {
	if (value == null) {
	    return "";
	}

	return LineType.METADATA.getPrefix() + MzTabFile.SEPARATOR + unitId + "-" + fieldName + MzTabFile.SEPARATOR + value.toString() + MzTabFile.EOL;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 13 * hash + (this.logger != null ? this.logger.hashCode() : 0);
	hash = 13 * hash + (this.unitId != null ? this.unitId.hashCode() : 0);
	hash = 13 * hash + (this.title != null ? this.title.hashCode() : 0);
	hash = 13 * hash + (this.description != null ? this.description.hashCode() : 0);
	hash = 13 * hash + (this.sampleProcessing != null ? this.sampleProcessing.hashCode() : 0);
	hash = 13 * hash + (this.instrument != null ? this.instrument.hashCode() : 0);
	hash = 13 * hash + (this.software != null ? this.software.hashCode() : 0);
	hash = 13 * hash + (this.softwareSetting != null ? this.softwareSetting.hashCode() : 0);
	hash = 13 * hash + (this.falseDiscoveryRate != null ? this.falseDiscoveryRate.hashCode() : 0);
	hash = 13 * hash + (this.publication != null ? this.publication.hashCode() : 0);
	hash = 13 * hash + (this.contact != null ? this.contact.hashCode() : 0);
	hash = 13 * hash + (this.uri != null ? this.uri.hashCode() : 0);
	hash = 13 * hash + (this.mod != null ? this.mod.hashCode() : 0);
	hash = 13 * hash + (this.quantificationMethod != null ? this.quantificationMethod.hashCode() : 0);
	hash = 13 * hash + (this.proteinQuantificationUnit != null ? this.proteinQuantificationUnit.hashCode() : 0);
	hash = 13 * hash + (this.peptideQuantificationUnit != null ? this.peptideQuantificationUnit.hashCode() : 0);
	hash = 13 * hash + (this.customParams != null ? this.customParams.hashCode() : 0);
	hash = 13 * hash + (this.species != null ? this.species.hashCode() : 0);
	hash = 13 * hash + (this.tissue != null ? this.tissue.hashCode() : 0);
	hash = 13 * hash + (this.cellType != null ? this.cellType.hashCode() : 0);
	hash = 13 * hash + (this.disease != null ? this.disease.hashCode() : 0);
	hash = 13 * hash + (this.subsamples != null ? this.subsamples.hashCode() : 0);
	hash = 13 * hash + (this.msFiles != null ? this.msFiles.hashCode() : 0);
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
	final Unit other = (Unit) obj;
	if (this.logger != other.logger && (this.logger == null || !this.logger.equals(other.logger))) {
	    return false;
	}
	if ((this.unitId == null) ? (other.unitId != null) : !this.unitId.equals(other.unitId)) {
	    return false;
	}
	if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
	    return false;
	}
	if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
	    return false;
	}
	if (this.sampleProcessing != other.sampleProcessing && (this.sampleProcessing == null || !this.sampleProcessing.equals(other.sampleProcessing))) {
	    return false;
	}
	if (this.instrument != other.instrument && (this.instrument == null || !this.instrument.equals(other.instrument))) {
	    return false;
	}
	if (this.software != other.software && (this.software == null || !this.software.equals(other.software))) {
	    return false;
	}
	if (this.softwareSetting != other.softwareSetting && (this.softwareSetting == null || !this.softwareSetting.equals(other.softwareSetting))) {
	    return false;
	}
	if (this.falseDiscoveryRate != other.falseDiscoveryRate && (this.falseDiscoveryRate == null || !this.falseDiscoveryRate.equals(other.falseDiscoveryRate))) {
	    return false;
	}
	if (this.publication != other.publication && (this.publication == null || !this.publication.equals(other.publication))) {
	    return false;
	}
	if (this.contact != other.contact && (this.contact == null || !this.contact.equals(other.contact))) {
	    return false;
	}
	if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri))) {
	    return false;
	}
	if (this.mod != other.mod && (this.mod == null || !this.mod.equals(other.mod))) {
	    return false;
	}
	if (this.quantificationMethod != other.quantificationMethod && (this.quantificationMethod == null || !this.quantificationMethod.equals(other.quantificationMethod))) {
	    return false;
	}
	if (this.proteinQuantificationUnit != other.proteinQuantificationUnit && (this.proteinQuantificationUnit == null || !this.proteinQuantificationUnit.equals(other.proteinQuantificationUnit))) {
	    return false;
	}
	if (this.peptideQuantificationUnit != other.peptideQuantificationUnit && (this.peptideQuantificationUnit == null || !this.peptideQuantificationUnit.equals(other.peptideQuantificationUnit))) {
	    return false;
	}
	if (this.customParams != other.customParams && (this.customParams == null || !this.customParams.equals(other.customParams))) {
	    return false;
	}
	if (this.species != other.species && (this.species == null || !this.species.equals(other.species))) {
	    return false;
	}
	if (this.tissue != other.tissue && (this.tissue == null || !this.tissue.equals(other.tissue))) {
	    return false;
	}
	if (this.cellType != other.cellType && (this.cellType == null || !this.cellType.equals(other.cellType))) {
	    return false;
	}
	if (this.disease != other.disease && (this.disease == null || !this.disease.equals(other.disease))) {
	    return false;
	}
	if (this.subsamples != other.subsamples && (this.subsamples == null || !this.subsamples.equals(other.subsamples))) {
	    return false;
	}
	if (this.msFiles != other.msFiles && (this.msFiles == null || !this.msFiles.equals(other.msFiles))) {
	    return false;
	}
	return true;
    }
}
