package uk.ac.ebi.pride.mztab_java.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.mztab_java.MzTabFile;
import uk.ac.ebi.pride.mztab_java.MzTabParsingException;

public class Unit {
	private Logger logger = Logger.getLogger(Unit.class);
	/**
	 * Pattern to parse meta-data lines. It extracts
	 * the line type, UNIT_ID, SUB_ID (null if not
	 * present), and the field name and value.
	 */
	private static final Pattern MZTAB_LINE_PATTERN = Pattern.compile("^(\\w{3})\t([^-]+)-(sub\\[\\d+\\])?-?([^\t]+)\t(.+)");
	/**
	 * Unit attributes
	 */
	private String				unitId;
	private String 				title;
	private String 				description;
	private List<ParamList> 	sampleProcessing;
	private List<Instrument> 	instrument;
	private List<Param>			software;
	private ParamList			falseDiscoveryRate;
	/**
	 * DOIs must be prefixed by "doi:", PubMed ids by "pubmed:"
	 */
	private List<String>		publication;
	private List<Contact>		contact;
	private URI					uri;
	private ParamList			mod;
	private Param				modProbabilityMethod;
	private Param				quantificationMethod;
	private Param				proteinQuantificationUnit;
	private Param				peptideQuantificationUnit;
	private List<Param>			customParams;
	
	private List<Param>			species;
	private List<Param>			tissue;
	private List<Param>			cellType;
	private List<Param>			disease;
	
	private List<Subsample>		subsamples;
	
	/**
	 * Constructur constructing an empty Uni.
	 */
	public Unit() {
		
	}
	
	/**
	 * Parses the given mztab string and sets the various
	 * properties accordingly. Properties that are already
	 * set and not defined in the passed string are not
	 * altered. The only exception to this rule is the unit
	 * id: once the unit id was set unmarshalling properties
	 * assigned to a different unit causes a parsing exception
	 * to be thrown. 
	 * @param mzTabString
	 * @throws MzTabParsingException Thrown on any parsing error.
	 */
	public void unmarshall(String mzTabString) throws MzTabParsingException {
		// parse the string line by line
		String[] lines = mzTabString.split("\r?\n");
		
		for (String line : lines) {
			// ignore empty and non-metadata lines
			if (line.trim().length() < 1 || !"MTD".equals(line.substring(0, 3)))
				continue;
			
			// parse the line
			Matcher matcher = MZTAB_LINE_PATTERN.matcher(line);
			
			// throw a parsing exception if the line couldn't be parsed
			if (!matcher.find())
				throw new MzTabParsingException("Invalid meta-data line encountered: <" + line + ">");
			
			// get the various fields
			String unitId 	= matcher.group(2).trim();
			String subId 	= matcher.group(3);
			String field	= matcher.group(4);
			String value	= matcher.group(5);
			
			if (subId != null)
				subId = subId.trim();
			if (field != null)
				field = field.trim();
			if (value != null)
				value = value.trim();
			
			// check that the unitId didn't change - if it wasn't set yet, set it
			if (this.unitId == null)
				this.unitId = unitId;
			else if (!this.unitId.equals(unitId))
				throw new MzTabParsingException("Metadata line passed to Unit object (id = " + this.unitId + ") with a different UNIT_ID (" + unitId + ")");
			
			// parse the field
			parseField(subId, field, value);
		}
	}

	private void parseField(String subId, String field, String value) throws MzTabParsingException {
		logger.debug("parsing field: subId = " + subId + ", field = " + field + ", value = " + value);
		
		try {
			// simple fields with only one value
			if ("title".equals(field))
				title = value.trim();
			else if ("description".equals(field) && subId == null)
				description = value.trim();
			else if ("false_discovery_rate".equals(field))
				falseDiscoveryRate = new ParamList(value);
			else if ("uri".equals(field))
				uri = new URI(value);
			else if ("mod".equals(field))
				mod = new ParamList(value);
			else if ("mod-probability_method".equals(field))
				modProbabilityMethod = new Param(value);
			else if ("quantification_method".equals(field))
				quantificationMethod = new Param(value);
			else if ("protein-quantification_unit".equals(field))
				proteinQuantificationUnit = new Param(value);
			else if ("peptide-quantification_unit".equals(field))
				peptideQuantificationUnit = new Param(value);
			
			/**
			 * Complex fields with multiple values
			 */
			
			// sample processing
			else if (field.startsWith("sample_processing")) {
				int sampleProcessingIndex = Integer.parseInt( field.substring(18, field.length() - 1) ); // extract the processing step number
				// create the array if necessary
				if (sampleProcessing == null)
					sampleProcessing = new ArrayList<ParamList>();
				// set the param
				sampleProcessing.add(sampleProcessingIndex - 1, new ParamList(value));
			}
			
			// instruments
			else if (field.startsWith("instrument")) {
				// get the instrument's index
				int instrumentIndex = Integer.parseInt( field.substring(11, field.indexOf(']', 11)) );
				// create the instrument array if necessary
				if (instrument == null)
					instrument = new ArrayList<Instrument>();
				// create the instrument if necessary
				if (instrument.get(instrumentIndex - 1) == null)
					instrument.add(instrumentIndex - 1, new Instrument());
				// check which value is set
				if (field.endsWith("source"))
					instrument.get(instrumentIndex - 1).setSource(new Param(value));
				else if (field.endsWith("analyzer"))
					instrument.get(instrumentIndex - 1).setAnalyzer(new Param(value));
				else if (field.endsWith("detector"))
					instrument.get(instrumentIndex - 1).setDetector(new Param(value));
			}
			
			// software
			else if (field.startsWith("software")) {
				// get the software's 1-based index
				int softwareIndex = Integer.parseInt( field.substring(9, field.length() - 1) );
				// create the software array if necessary
				if (software == null)
					software = new ArrayList<Param>();
				// add the software
				software.add(softwareIndex - 1, new Param(value));
			}
			
			// publication
			else if (field.equals("publication")) {
				// split the string
				String[] publications = value.trim().split("\\|");
				// create the publications array if necessary
				if (publication == null)
					publication = new ArrayList<String>(publications.length);
				// add the publications
				for (String pub : publications)
					publication.add(pub);
			}
			
			// contact
			else if (field.startsWith("contact")) {
				// get the instrument's index
				int contactIndex = Integer.parseInt( field.substring(8, field.indexOf(']', 8)) );
				// create the instrument array if necessary
				if (contact == null)
					contact = new ArrayList<Contact>();
				// create the instrument if necessary
				if (contact.size() < contactIndex)
					contact.add(contactIndex - 1, new Contact());
				// check which value is set
				if (field.endsWith("name"))
					contact.get(contactIndex - 1).setName(value.trim());
				else if (field.endsWith("email"))
					contact.get(contactIndex - 1).setEmail(value.trim());
				else if (field.endsWith("affiliation"))
					contact.get(contactIndex - 1).setAffiliation(value.trim());
			}
			
			// TODO: define how -custom params are handled and react on that
			else if (field.equals("custom")) {
				if (customParams == null)
					customParams = new ArrayList<Param>();
				
				customParams.add(new Param(value));
			}
			
			// species, tissue, cell type, disease - on the unit level
			else if (subId == null && field.startsWith("species")) {
				// get the instrument's index
				int speciesIndex = Integer.parseInt( field.substring(8, field.length() - 1) );
				// create the instrument array if necessary
				if (species == null)
					species = new ArrayList<Param>();
				
				species.add(speciesIndex - 1, new Param(value));
			}
			else if (subId == null && field.startsWith("tissue")) {
				// get the instrument's index
				int tissueIndex = Integer.parseInt( field.substring(7, field.length() - 1) );
				// create the instrument array if necessary
				if (tissue == null)
					tissue = new ArrayList<Param>();
				
				tissue.add(tissueIndex - 1, new Param(value));
			}
			else if (subId == null && field.startsWith("cell_type")) {
				// get the instrument's index
				int cellTypeIndex = Integer.parseInt( field.substring(10, field.length() - 1) );
				// create the instrument array if necessary
				if (cellType == null)
					cellType = new ArrayList<Param>();
				
				cellType.add(cellTypeIndex - 1, new Param(value));
			}
			else if (subId == null && field.startsWith("disease")) {
				// get the instrument's index
				int diseaseIndex = Integer.parseInt( field.substring(8, field.length() - 1) );
				// create the instrument array if necessary
				if (disease == null)
					disease = new ArrayList<Param>();
				
				disease.add(diseaseIndex - 1, new Param(value));
			}
			
			/**
			 * Parse subsample specific data
			 */
			else if (subId != null) {
				// extract the index
				int subIndex = Integer.parseInt( subId.substring(4, subId.length() - 1));
				// make sure the subsample array exists
				if (subsamples == null)
					subsamples = new ArrayList<Subsample>();
				// make sure this subsample already exists
				if (subsamples.size() < subIndex)
					subsamples.add(subIndex - 1, new Subsample(this.unitId, subIndex));
				
				Subsample subsample = subsamples.get(subIndex - 1);
				
				// parse the field
				if ("description".equals(field))
					subsample.setDescription(value.trim());
				else if ("quantitation_reagent".equals(field))
					subsample.setQuantitationReagent(new Param(value));
				else if ("custom".equals(field)) {
					if (subsample.getCustomParams() == null)
						subsample.setCustomParams(new ArrayList<Param>(1));
					subsample.getCustomParams().add(new Param(value));
				}
				else if (field.startsWith("species")) {
					// get the instrument's index
					int speciesIndex = Integer.parseInt( field.substring(8, field.length() - 1) );
					// create the instrument array if necessary
					if (subsample.getSpecies() == null)
						subsample.setSpecies(new ArrayList<Param>());
					
					subsample.getSpecies().add(speciesIndex - 1, new Param(value));
				}
				else if (field.startsWith("tissue")) {
					// get the instrument's index
					int tissueIndex = Integer.parseInt( field.substring(7, field.length() - 1) );
					// create the instrument array if necessary
					if (subsample.getTissue() == null)
						subsample.setTissue(new ArrayList<Param>());
					
					subsample.getTissue().add(tissueIndex - 1, new Param(value));
				}
				else if (field.startsWith("cell_type")) {
					// get the instrument's index
					int cellTypeIndex = Integer.parseInt( field.substring(10, field.length() - 1) );
					// create the instrument array if necessary
					if (subsample.getCellType() == null)
						subsample.setCellType(new ArrayList<Param>());
					
					subsample.getCellType().add(cellTypeIndex - 1, new Param(value));
				}
				else if (field.startsWith("disease")) {
					// get the instrument's index
					int diseaseIndex = Integer.parseInt( field.substring(8, field.length() - 1) );
					// create the instrument array if necessary
					if (subsample.getDisease() == null)
						subsample.setDisease( new ArrayList<Param>() );
					
					subsample.getDisease().add(diseaseIndex - 1, new Param(value));
				}
					
			}
		}
		catch (Exception e) {
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

	public Param getModProbabilityMethod() {
		return modProbabilityMethod;
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
	 * Returns the subsample with the given index. In
	 * case the subsample doesn't exist, null is returned.
	 * @param subsampleId The subsamples index.
	 * @return
	 */
	public Subsample getSubsample(Integer subsampleId) {
		if (subsamples == null)
			return null;
		
		for (Subsample s : subsamples) {
			if (s.getSubsampleIndex() == subsampleId)
				return s;
		}
		
		return null;
	}
	
	/**
	 * Adds the given subsample. In case a subsample
	 * with the same index already exists, this subample
	 * is replaced.
	 * @param s
	 */
	public void setSubsample(Subsample s) {
		if (subsamples == null)
			subsamples = new ArrayList<Subsample>(1);
		
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
	 * Adds the given subsamples to the unit. In case
	 * there are multiple subsamples with the same index
	 * only one of them is being added.
	 * @param subsamples
	 */
	public void setSubsamples(Collection<Subsample> subsamples) {
		for (Subsample s : subsamples)
			setSubsample(s);
	}
	
	public List<Subsample> getSubsamples() {
		return subsamples;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
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

	public void setFalseDiscoveryRate(ParamList falseDiscoveryRate) {
		this.falseDiscoveryRate = falseDiscoveryRate;
	}

	public void setPublication(List<String> publication) {
		this.publication = publication;
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

	public void setModProbabilityMethod(Param modProbabilityMethod) {
		this.modProbabilityMethod = modProbabilityMethod;
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
	 * Converts the given meta-data to an mzTab formatted string.
	 * @return
	 */
	public String toMzTab() {
		StringBuffer mzTab = new StringBuffer();
		
		if (title != null)
			mzTab.append(createField("title", title));
		if (description != null)
			mzTab.append(createField("description", description));
		// sample processing
		if (sampleProcessing != null) {
			for (Integer i = 1; i <= sampleProcessing.size(); i++)
				mzTab.append(createField(String.format("sample_processing[%d]", i), sampleProcessing.get(i - 1)));
		}
		// instrument
		if (instrument != null) {
			for (Integer i = 1; i <= instrument.size(); i++) {
				mzTab.append(createField(String.format("instrument[%d]-source", i), instrument.get(i - 1).getSource()));
				mzTab.append(createField(String.format("instrument[%d]-analyzer", i), instrument.get(i - 1).getAnalyzer()));
				mzTab.append(createField(String.format("instrument[%d]-detector", i), instrument.get(i - 1).getDetector()));
			}
		}
		// software
		if (software != null) {
			for (Integer i = 1; i <= software.size(); i++)
				mzTab.append(createField(String.format("software[%d]", i), software.get(i - 1)));
		}
		// false discovery rate
		if (falseDiscoveryRate != null)
			mzTab.append(createField("false_discovery_rate", falseDiscoveryRate));
		// publication
		if (publication != null && publication.size() > 0) {
			String string = "";
			
			for (String p : publication)
				string += (string.length() > 1 ? "," : "") + p;
			
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
		if (uri != null)
			mzTab.append(createField("uri", uri));
		// mods
		if (mod != null)
			mzTab.append(createField("mod", mod));
		// mod probability method
		if (modProbabilityMethod != null)
			mzTab.append(createField("mod-probability_method", modProbabilityMethod));
		// quantification method
		if (quantificationMethod != null)
			mzTab.append(createField("quantification_method", quantificationMethod));
		// protein quant unit
		if (proteinQuantificationUnit != null)
			mzTab.append(createField("protein-quantification_unit", proteinQuantificationUnit));
		// peptide quant unit
		if (peptideQuantificationUnit != null)
			mzTab.append(createField("peptide-quantification_unit", peptideQuantificationUnit));
		// custom
		if (customParams != null) {
			for (Param p : customParams)
				mzTab.append(createField("custom", p));
		}
		// species
		if (species != null) {
			for (int i = 1; i <= species.size(); i++)
				mzTab.append(createField(String.format("species[%d]", i), species.get(i - 1)));
		}
		// tissue
		if (tissue != null) {
			for (int i = 1; i <= tissue.size(); i++)
				mzTab.append(createField(String.format("tissue[%d]", i), tissue.get(i - 1)));
		}
		// cell_type
		if (cellType != null) {
			for (int i = 1; i <= cellType.size(); i++)
				mzTab.append(createField(String.format("cell_type[%d]", i), cellType.get(i - 1)));
		}
		// disease
		if (disease != null) {
			for (int i = 1; i <= disease.size(); i++)
				mzTab.append(createField(String.format("disease[%d]", i), disease.get(i - 1)));
		}
		// subsamples
		if (subsamples != null) {
			for (Subsample s : subsamples)
				mzTab.append(s.toMzTab());
		}
		
		return mzTab.toString();
	}
	
	private String createField(String fieldName, Object value) {
		if (value == null)
			return "";
		
		return LineType.METADATA.getPrefix() + MzTabFile.SEPARATOR + unitId + "-" + fieldName + MzTabFile.SEPARATOR + value.toString() + MzTabFile.EOL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cellType == null) ? 0 : cellType.hashCode());
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result
				+ ((customParams == null) ? 0 : customParams.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime
				* result
				+ ((falseDiscoveryRate == null) ? 0 : falseDiscoveryRate
						.hashCode());
		result = prime * result
				+ ((instrument == null) ? 0 : instrument.hashCode());
		result = prime * result + ((mod == null) ? 0 : mod.hashCode());
		result = prime
				* result
				+ ((modProbabilityMethod == null) ? 0 : modProbabilityMethod
						.hashCode());
		result = prime
				* result
				+ ((peptideQuantificationUnit == null) ? 0
						: peptideQuantificationUnit.hashCode());
		result = prime
				* result
				+ ((proteinQuantificationUnit == null) ? 0
						: proteinQuantificationUnit.hashCode());
		result = prime * result
				+ ((publication == null) ? 0 : publication.hashCode());
		result = prime
				* result
				+ ((quantificationMethod == null) ? 0 : quantificationMethod
						.hashCode());
		result = prime
				* result
				+ ((sampleProcessing == null) ? 0 : sampleProcessing.hashCode());
		result = prime * result
				+ ((software == null) ? 0 : software.hashCode());
		result = prime * result + ((species == null) ? 0 : species.hashCode());
		result = prime * result
				+ ((subsamples == null) ? 0 : subsamples.hashCode());
		result = prime * result + ((tissue == null) ? 0 : tissue.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Unit other = (Unit) obj;
		if (cellType == null) {
			if (other.cellType != null)
				return false;
		} else if (!cellType.equals(other.cellType))
			return false;
		if (contact == null) {
			if (other.contact != null)
				return false;
		} else if (!contact.equals(other.contact))
			return false;
		if (customParams == null) {
			if (other.customParams != null)
				return false;
		} else if (!customParams.equals(other.customParams))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (disease == null) {
			if (other.disease != null)
				return false;
		} else if (!disease.equals(other.disease))
			return false;
		if (falseDiscoveryRate == null) {
			if (other.falseDiscoveryRate != null)
				return false;
		} else if (!falseDiscoveryRate.equals(other.falseDiscoveryRate))
			return false;
		if (instrument == null) {
			if (other.instrument != null)
				return false;
		} else if (!instrument.equals(other.instrument))
			return false;
		if (mod == null) {
			if (other.mod != null)
				return false;
		} else if (!mod.equals(other.mod))
			return false;
		if (modProbabilityMethod == null) {
			if (other.modProbabilityMethod != null)
				return false;
		} else if (!modProbabilityMethod.equals(other.modProbabilityMethod))
			return false;
		if (peptideQuantificationUnit == null) {
			if (other.peptideQuantificationUnit != null)
				return false;
		} else if (!peptideQuantificationUnit
				.equals(other.peptideQuantificationUnit))
			return false;
		if (proteinQuantificationUnit == null) {
			if (other.proteinQuantificationUnit != null)
				return false;
		} else if (!proteinQuantificationUnit
				.equals(other.proteinQuantificationUnit))
			return false;
		if (publication == null) {
			if (other.publication != null)
				return false;
		} else if (!publication.equals(other.publication))
			return false;
		if (quantificationMethod == null) {
			if (other.quantificationMethod != null)
				return false;
		} else if (!quantificationMethod.equals(other.quantificationMethod))
			return false;
		if (sampleProcessing == null) {
			if (other.sampleProcessing != null)
				return false;
		} else if (!sampleProcessing.equals(other.sampleProcessing))
			return false;
		if (software == null) {
			if (other.software != null)
				return false;
		} else if (!software.equals(other.software))
			return false;
		if (species == null) {
			if (other.species != null)
				return false;
		} else if (!species.equals(other.species))
			return false;
		if (subsamples == null) {
			if (other.subsamples != null)
				return false;
		} else if (!subsamples.equals(other.subsamples))
			return false;
		if (tissue == null) {
			if (other.tissue != null)
				return false;
		} else if (!tissue.equals(other.tissue))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
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
		return true;
	}
}
