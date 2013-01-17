package uk.ac.ebi.pride.tools.mztab_exporter.exporter.impl;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.model.Param;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.jmztab.MzTabFile;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.Contact;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.Utils;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.mztab_exporter.exporter.MzTabExporter;
import uk.ac.ebi.pride.tools.mztab_exporter.exporter.util.SearchEngineParameter;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrideMzTabExporter implements MzTabExporter {

    private static final Logger logger = Logger.getLogger(PrideMzTabExporter.class);

	/**
	 * The PrideXmlReader instance to use
	 * to read the PRIDE XML file.
	 */
	private PrideXmlReader reader;
	/**
	 * The MzTabFile instance used to
	 * write the mzTab file.
	 */
	private MzTabFile writer;
	/**
	 * The unit id to use for the file.
	 */
	private String unitId;
	/**
	 * The sourcefile that's being exported.
	 */
	private File sourcefile;
	/**
	 * Pattern to extract the subsample number from
	 * a param's value.
	 */
	private static final Pattern subsampleNumberPattern = Pattern.compile("subsample(\\d+)");

	public void exportToMzTab(File inputFile, File outputFile) throws Exception {
		sourcefile = inputFile;
		
		// open the PRIDE XML file
		reader = new PrideXmlReader(inputFile);

        if (!inputOK(reader)) {
            System.out.println("Provided PRIDE XML does not seem to fulfil requirements! " + inputFile.getAbsolutePath());
            System.out.println("Not converting input file!");
            return;
        }

		// create the mzTab file
		writer = new MzTabFile();
		
		// generate the unit id
		unitId = generateUnitId();
		
		try {
			// process the meta-data
			processMetadata();
			
			// iterate over the identifications and process them together with the peptides
			processIdentifications();
			
			// write the file
			FileWriter fileWriter = new FileWriter(outputFile);
			
			// write the file
			fileWriter.write(writer.toMzTab());
			
			fileWriter.close();
		}
		catch (MzTabParsingException e) {
			throw new Exception("Failed to generate mzTabFile: " + e.getMessage(), e);
		}

        System.out.println("MzTab file written to " + outputFile.getAbsolutePath());
	}

    private boolean inputOK(PrideXmlReader reader) {

        // check if there are any identifications (GelFree or TwoDimensional)
        if (reader.getIdentIds() == null || reader.getIdentIds().size() < 1) {
            return false;
        }
        // check if there are any peptides
        if (reader.getNumberOfPeptides() < 1) {
            return false;
        }
        // check if there are any spectra (we don't fail, but log a warning)
        if (reader.getSpectrumIds() == null || reader.getSpectrumIds().size() < 1) {
            logger.warn("PRIDE file does not seem to contain spectra!");
        }
        return true;
    }

    /**
	 * Generates a unit id for the current
	 * PRIDE XML file. If the file contains
	 * an accession the accession is used, 
	 * otherwise a temporary unit id is
	 * generated.
	 * @return The Unit id to use.
	 */
	private String generateUnitId() {
		// check whether the pride xml file has an accession
		if (reader.getExpAccession() != null && !"".equals(reader.getExpAccession()))
			return "PRIDE_" + reader.getExpAccession();
		else
			return "PRIDEFILE_" + System.currentTimeMillis();
	}
	
	private void processMetadata() throws Exception {
		// create a new unit
		Unit unit = new Unit();
		try {
			unit.setUnitId(unitId);
			
			// set the description (in case there isn't any NULL is returned anyway)
            String desc = getFirstCvParamValue(reader.getAdditionalParams(), DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession());
            if (stringValueOK(desc)) {
                unit.setDescription(desc);
            }
			// process the references

			unit.setPublication(convertReferences(reader.getReferences()));
			// process the contacts
			unit.setContact(convertContacts(reader.getAdmin().getContact()));
			// process the species info
			unit = addSampleInfo(unit);
			// process the experiment params
			unit = processExperimentParams(unit);
            // process the instrument information
            unit.setInstrument(convertInstrument(reader.getInstrument()));
			// add the PRIDE XML file as an MS data file
			try {
				unit.setMsFile(1, new MsFile(
						new uk.ac.ebi.pride.jmztab.model.Param("MS", "MS:1000564", "PSI mzData file", null),
						new uk.ac.ebi.pride.jmztab.model.Param("MS", "MS:1000777", "spectrum identifier nativeID format", null), 
						sourcefile.getAbsolutePath()));
			} catch (MzTabParsingException e) {
				throw new IllegalStateException("Failed to generate MsFile object", e);
			}
			
			writer.setUnit(unit);
		} catch (MzTabParsingException e) {
			throw new Exception("Failed to create mzTab file. ", e);
		}
	}

    /**
     * Attention: We get only one instrument from PRIDE, which has multiple CvParams for source and detector,
     * and which has multipe analyzers with multiple CvParams.
     * We have to map this to a list of Instruments in mzTab, each of which can only carry one Param per source/detector/analyzer.
     *
     * @param instrument the PRIDE instrument record.
     * @return a List of mzTab instruments.
     * @throws MzTabParsingException
     */
    private List<uk.ac.ebi.pride.jmztab.model.Instrument> convertInstrument(uk.ac.ebi.pride.jaxb.model.Instrument instrument) throws MzTabParsingException {
        List<uk.ac.ebi.pride.jmztab.model.Instrument> instruments = new ArrayList<uk.ac.ebi.pride.jmztab.model.Instrument>();

        if (instrument != null) {
            uk.ac.ebi.pride.jmztab.model.Instrument newInstrument = new uk.ac.ebi.pride.jmztab.model.Instrument();

            // ToDo: the PRIDE instrument contains a free-text instrument name, that the mzTab version currently does not support!

            // handle the source information
            Param sourceParam = instrument.getSource();
            // ToDo: only take first CvParam, since we can only store one param in mzTab
            CvParam tmp = getFirstCvParam(sourceParam);
            if (tmp != null) {
                newInstrument.setSource(convertParam(tmp));
            }

            // handle the detector information
            Param detectorParam = instrument.getDetector();
            // ToDo: only take first CvParam, since we can only store one param in mzTab
            tmp = getFirstCvParam(detectorParam);
            if (tmp != null) {
                newInstrument.setDetector(convertParam( tmp ));
            }

            // handle the analyzer information
            // ToDo: the PRIDE instrument can contain multiple analyzers, the mzTab version can only have one!
            if (instrument.getAnalyzerList().getCount() > 0) {
                Param analyzerParam = instrument.getAnalyzerList().getAnalyzer().iterator().next();
            // ToDo: only take first CvParam, since we can only store one param in mzTab
                tmp = getFirstCvParam(analyzerParam);
                if (tmp != null) {
                    newInstrument.setAnalyzer(convertParam(tmp));
                }
            }

            instruments.add(newInstrument);
        }

        return instruments;
    }

    private  CvParam getFirstCvParam(Param param) throws MzTabParsingException {
        if (param == null || param.getCvParam() == null) {
            throw new MzTabParsingException("Cannot retrieve CvParams from null!");
        }

        if (param.getCvParam().iterator().hasNext()) {
            return param.getCvParam().iterator().next();
        }

        return null;
    }

    /**
	 * Processes the experiment additional params
	 * (f.e. quant method, description...).
	 * @param unit
	 * @return
	 * @throws MzTabParsingException 
	 */
	private Unit processExperimentParams(Unit unit) throws MzTabParsingException {
		// process the experiment additional params
        if (reader.getAdditionalParams() != null) {
            for (CvParam p : reader.getAdditionalParams().getCvParam()) {
                if (DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession().equals(p.getAccession()))
                    if (stringValueOK(p.getValue())) {
                        unit.setDescription(p.getValue());
                    }
                    // check if it's a quantification method
                    else if (QuantitationCvParams.isQuantificationMethod(p.getAccession()))
                        unit.setQuantificationMethod(convertParam(p));
                    else if (DAOCvParams.GEL_BASED_EXPERIMENT.getAccession().equals(p.getAccession())) {
                        if (unit.getCustomParams() == null)
                            unit.setCustomParams(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>(1));
                        unit.getCustomParams().add(convertParam(p));
                    }
            }
        }
		return unit;
	}

	/**
	 * Adds the sample parameters (species, tissue, cell type,
	 * disease) to the unit and the various subsamples.
	 * @param unit
	 * @return
	 * @throws MzTabParsingException 
	 */
    private Unit addSampleInfo(Unit unit) throws MzTabParsingException {
        SampleDescription sampleDescription = reader.getAdmin().getSampleDescription();
        // create a hashmap to store potential subsamples
        HashMap<Integer, Subsample> subsamples = new HashMap<Integer, Subsample>();

        if (sampleDescription != null) {
            // loop through all params
            for (CvParam p : sampleDescription.getCvParam()) {
                // check for subsample descriptions
                if (QuantitationCvParams.SUBSAMPLE1_DESCRIPTION.getAccession().equals(p)) {
                    if (!subsamples.containsKey(1))
                        subsamples.put(1, new Subsample(unitId, 1));
                    subsamples.get(1).setDescription(p.getValue());
                    continue;
                }
                if (QuantitationCvParams.SUBSAMPLE2_DESCRIPTION.getAccession().equals(p)) {
                    if (!subsamples.containsKey(2))
                        subsamples.put(2, new Subsample(unitId, 2));
                    subsamples.get(2).setDescription(p.getValue());
                    continue;
                }
                if (QuantitationCvParams.SUBSAMPLE3_DESCRIPTION.getAccession().equals(p)) {
                    if (!subsamples.containsKey(3))
                        subsamples.put(3, new Subsample(unitId, 3));
                    subsamples.get(3).setDescription(p.getValue());
                    continue;
                }
                if (QuantitationCvParams.SUBSAMPLE4_DESCRIPTION.getAccession().equals(p)) {
                    if (!subsamples.containsKey(4))
                        subsamples.put(4, new Subsample(unitId, 4));
                    subsamples.get(4).setDescription(p.getValue());
                    continue;
                }
                if (QuantitationCvParams.SUBSAMPLE5_DESCRIPTION.getAccession().equals(p)) {
                    if (!subsamples.containsKey(5))
                        subsamples.put(5, new Subsample(unitId, 5));
                    subsamples.get(5).setDescription(p.getValue());
                    continue;
                }
                if (QuantitationCvParams.SUBSAMPLE6_DESCRIPTION.getAccession().equals(p)) {
                    if (!subsamples.containsKey(6))
                        subsamples.put(6, new Subsample(unitId, 6));
                    subsamples.get(6).setDescription(p.getValue());
                    continue;
                }
                if (QuantitationCvParams.SUBSAMPLE7_DESCRIPTION.getAccession().equals(p)) {
                    if (!subsamples.containsKey(7))
                        subsamples.put(7, new Subsample(unitId, 7));
                    subsamples.get(7).setDescription(p.getValue());
                    continue;
                }
                if (QuantitationCvParams.SUBSAMPLE8_DESCRIPTION.getAccession().equals(p)) {
                    if (!subsamples.containsKey(8))
                        subsamples.put(8, new Subsample(unitId, 8));
                    subsamples.get(8).setDescription(p.getValue());
                    continue;
                }

                // check if it belongs to a subsample
                if (p.getValue() != null && p.getValue().startsWith("subsample")) {
                    // get the subsample number
                    Matcher matcher = subsampleNumberPattern.matcher(p.getValue());

                    if (matcher.find()) {
                        Integer subsampleIndex = Integer.parseInt(matcher.group(1));

                        // remove the value
                        p.setValue(null);

                        // check if the subsample already exists
                        if (!subsamples.containsKey(subsampleIndex))
                            subsamples.put(subsampleIndex, new Subsample(unitId, subsampleIndex));

                        // add the param depending on the type
                        if ("NEWT".equals(p.getCvLabel())) {
                            if (subsamples.get(subsampleIndex).getSpecies() == null)
                                subsamples.get(subsampleIndex).setSpecies(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>());

                            subsamples.get(subsampleIndex).getSpecies().add(convertParam(p));
                        }
                        else if ("BRENDA".equals(p.getCvLabel())) {
                            if (subsamples.get(subsampleIndex).getTissue() == null)
                                subsamples.get(subsampleIndex).setTissue(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>());

                            subsamples.get(subsampleIndex).getTissue().add(convertParam(p));
                        }
                        else if ("CL".equals(p.getCvLabel())) {
                            if (subsamples.get(subsampleIndex).getCellType() == null)
                                subsamples.get(subsampleIndex).setCellType(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>());

                            subsamples.get(subsampleIndex).getCellType().add(convertParam(p));
                        }
                        else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                            if (subsamples.get(subsampleIndex).getDisease() == null)
                                subsamples.get(subsampleIndex).setDisease(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>());

                            subsamples.get(subsampleIndex).getDisease().add(convertParam(p));
                        }
                        // check if it's a quantification reagent
                        else if (QuantitationCvParams.isQuantificationReagent(p.getAccession())) {
                            subsamples.get(subsampleIndex).setQuantificationReagent(convertParam(p));
                        }
                    }
                }
                // add the param to the "global" group
                else {
                    if ("NEWT".equals(p.getCvLabel())) {
                        if (unit.getSpecies() == null)
                            unit.setSpecies(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>(1));
                        unit.getSpecies().add(convertParam(p));
                    }
                    else if ("BRENDA".equals(p.getCvLabel())) {
                        if (unit.getTissue() == null)
                            unit.setTissue(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>(1));
                        unit.getTissue().add(convertParam(p));
                    }
                    else if ("CL".equals(p.getCvLabel())) {
                        if (unit.getCellType() == null)
                            unit.setCellType(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>(1));
                        unit.getCellType().add(convertParam(p));
                    }
                    else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                        if (unit.getDisease() == null)
                            unit.setDisease(new ArrayList<uk.ac.ebi.pride.jmztab.model.Param>(1));
                        unit.getDisease().add(convertParam(p));
                    }
                }
            }

        }

        // set the accession as URI if there's one
        if (reader.getExpAccession() != null) {
            try {
                unit.setUri(new URI("http://www.ebi.ac.uk/pride/directLink.do?experimentAccessionNumber=" + reader.getExpAccession()));
            } catch (URISyntaxException e) {
                // ignore any problem
            }
        }

        // add the subsamples
        for (Subsample s : subsamples.values())
            unit.setSubsample(s);

        return unit;
    }

	private uk.ac.ebi.pride.jmztab.model.Param convertParam(CvParam p) throws MzTabParsingException {
		return new uk.ac.ebi.pride.jmztab.model.Param(p.getCvLabel(), p.getAccession(), p.getName(), p.getValue());
	}

	/**
	 * Converts the experiment's references into
	 * the reference string (DOIs and PubMed ids)
	 * expected in mzTab.
	 * @param references
	 * @return
	 */
	private ArrayList<String> convertReferences(List<Reference> references) {
		if (references == null || references.size() == 0)
			return null;
		
		// initialize the ArrayList of references
		ArrayList<String> referenceStrings = new ArrayList<String>(references.size());
		
		for (Reference ref : references) {
			// check if there's a DOI
			String doi = getFirstCvParamValue(ref.getAdditional(), DAOCvParams.REFERENCE_DOI.getAccession());
			
			if (doi != null && !"".equals(doi))
				referenceStrings.add("doi:" + doi);
			
			// check if there's a pubmed id
			String pubmed = getFirstCvParamValue(ref.getAdditional(), DAOCvParams.REFERENCE_PUBMED.getAccession());
			
			if (pubmed != null && !"".equals(pubmed))
				referenceStrings.add("pubmed:" + pubmed);
		}
		
		if ("".equals(referenceStrings))
			return null;
		
		return referenceStrings;
	}
	
	/**
	 * Converts a list of PRIDE JAXB Contacts
	 * into an ArrayList of mzTab Contacts.
	 * @param contact
	 * @return
	 * @throws MzTabParsingException 
	 */
	private ArrayList<Contact> convertContacts(
			List<uk.ac.ebi.pride.jaxb.model.Contact> contact) throws MzTabParsingException {
		// make sure there are contacts to be processed
		if (contact == null || contact.size() == 0)
			return null;
		
		// initialize the return variable
		ArrayList<Contact> contacts = new ArrayList<Contact>(contact.size());
		
		for (uk.ac.ebi.pride.jaxb.model.Contact c : contact) {
			Contact mztabContact = new Contact();
			
			mztabContact.setName(c.getName());
			mztabContact.setAffiliation(c.getInstitution());
			if (c.getContactInfo() != null && c.getContactInfo().contains("@"))
				mztabContact.setEmail(c.getContactInfo());
			
			// add the contact
			contacts.add(mztabContact);
		}
		
		return contacts;
	}

	/**
	 * Process the PRIDE XML file's identifications (and peptides).
	 * @throws Exception 
	 */
	private void processIdentifications() throws Exception {
		// Get a list of Identification ids
		List<String> ids = reader.getIdentIds();

		// Iterate over each identification
		for(String id : ids) {
			Identification ident = reader.getIdentById(id);
			
			// ignore any decoy hits
			if (isDecoyHit(ident))
				continue;
			
			Protein protein = convertIdentification(ident);
			
			// convert the identification's peptides
			List<Peptide> peptides = convertIdentificationPeptides(ident);
			
			// add the peptides
			for (Peptide p : peptides)
				writer.addPeptide(p);
			
			// check if the protein already exists
			Collection<Protein> existingProteins = writer.getProtein(protein.getAccession());
			
			if (existingProteins.size() == 1) {
				// check if the protein's are identical
				Protein existingProtein = existingProteins.iterator().next();
				
				if (!existingProtein.equals(protein)) {
					mergeProteins(existingProtein, protein);
				}
			}
			else {
				writer.addProtein(convertIdentification(ident));
			}
		} 
	}

	/**
	 * Checks whether the passed identification object
	 * is a decoy hit. This function only checks for
	 * the presence of specific cv / user Params.
	 * 
	 * @param ident A PRIDE JAXB Identification object.
	 * @return Boolean indicating whether the passed identification is a decoy hit.
	 */
	private boolean isDecoyHit(Identification ident) {
		for (CvParam param : ident.getAdditional().getCvParam()) { 
			if (param.getAccession().equals(DAOCvParams.DECOY_HIT.getAccession()))
				return true;
		}
		
		for (UserParam param : ident.getAdditional().getUserParam()) {
			if ("Decoy Hit".equals(param.getName()))
				return true;
		}
		
		return false;
	}

	/**
	 * Merges the information from two proteins. This function assumes
	 * that all peptides found in the two proteins were already added
	 * to the (mzTab) writer. During the merging the peptide numbers
	 * and modifications of the existingProtein are adapted based on the
	 * information retrieved from protein.
	 * 
	 * @param existingProtein Protein already added to the mzTab file. This object will be updated.
	 * @param protein The protein's which information to add to the existing protein.
	 * @throws Exception
	 */
	private void mergeProteins(Protein existingProtein, Protein protein) throws Exception {
		if (!existingProtein.getAccession().equals(protein.getAccession()))
			throw new Exception("Cannot merge proteins with different accessions.");
		if (!existingProtein.getUnitId().equals(protein.getUnitId()))
			throw new Exception("Cannot merge proteins from different unit ids");
		
		// make sure the quant values are the same
		for (Integer subsambleIndex : existingProtein.getSubsampleIndexes()) {
			if (!existingProtein.getAbundance(subsambleIndex).equals(protein.getAbundance(subsambleIndex))) {
				throw new Exception("Cannot merge proteins with different abundance values (subsample " + subsambleIndex + ")");
			}
		}
		
		System.out.println("Merging entries for " + protein.getAccession());
		
		// get all peptides -- retrieves the peptides for both proteins
		Collection<Peptide> peptides = writer.getProteinPeptides(existingProtein);
		
		Set<String> distinctPeptides = new HashSet<String>();
		
		for (Peptide p : peptides) {
			distinctPeptides.add(p.getSequence() + p.getModification().toString());
		}		
		
		existingProtein.setNumPeptidesDistinct(distinctPeptides.size());
		existingProtein.setNumPeptides(peptides.size());
		
		// add the modifications
		Set<Modification> distinctMods = new HashSet<Modification>(existingProtein.getModifications());
		distinctMods.addAll(protein.getModifications());
		
		existingProtein.setModifications(new ArrayList<Modification>(distinctMods));
		
		
	}

	/**
	 * Converts the passed Identification object into an
	 * MzTab protein.
	 * @param ident
	 * @return
	 * @throws MzTabParsingException 
	 */
	private Protein convertIdentification(Identification ident) throws MzTabParsingException {
		// create the protein object
		Protein protein = new Protein();
		
		protein.setAccession(ident.getAccession());
		protein.setUnitId(unitId);
		protein.setDatabase(ident.getDatabase());
		protein.setDatabaseVersion(ident.getDatabaseVersion());
		
		// set the search engine
		uk.ac.ebi.pride.jmztab.model.Param searchEngineParam = convertSearchEngine(ident.getSearchEngine());
		if (searchEngineParam != null) {
			protein.setSearchEngine(new ParamList(1));
			protein.getSearchEngine().add(searchEngineParam);
		}
		// setting the score is not sensible
		
		// set the description if available
		String descripion = getFirstCvParamValue(ident.getAdditional(), DAOCvParams.PROTEIN_NAME.getAccession());
		protein.setDescription(descripion);
		
		// set the species if possible
		Unit unit = writer.getUnitMetadata(unitId);
		
		if (unit != null && unit.getSpecies() != null && unit.getSpecies().size() == 1) {
			protein.setSpecies(unit.getSpecies().get(0).getName());
			protein.setTaxid(unit.getSpecies().get(0).getAccession());
		}
			
		// get the number of peptides
		protein.setNumPeptides(ident.getPeptideItem().size());
		HashSet<String> peptideSequences = new HashSet<String>();
		for (PeptideItem p : ident.getPeptideItem()) {
			peptideSequences.add(p.getSequence() + p.getModificationItem().toString());
		}
		protein.setNumPeptidesDistinct(peptideSequences.size());
		
		// add the indistinguishable accessions to the ambiguity members
		List<String> indistinguishableAccessions = getCvParamValues(ident.getAdditional(), DAOCvParams.INDISTINGUISHABLE_ACCESSION.getAccession());
		protein.setAmbiguityMembers(indistinguishableAccessions);
		
		// set the modifications
		protein.setModifications(getIdentificationModifications(ident));
		
		// add potential quantitative values
		addProteinQuantitativeValues(protein, ident);
		
		// process the additional params
		for (CvParam p : ident.getAdditional().getCvParam()) {
			try {
				// check if there's a quant unit set
				if (QuantitationCvParams.UNIT_RATIO.getAccession().equals(p.getAccession()) ||
					QuantitationCvParams.UNIT_COPIES_PER_CELL.getAccession().equals(p.getAccession())) {
					
					if (writer.getUnitMetadata(unitId) != null) {
						if (writer.getUnitMetadata(unitId).getProteinQuantificationUnit() == null)
							writer.getUnitMetadata(unitId).setProteinQuantificationUnit(convertParam(p));
						// make sure there aren't different protein units reported
						else {
							if (!writer.getUnitMetadata(unitId).getProteinQuantificationUnit().getAccession().equals(p.getAccession()))
								// TODO: add proper error handling
								throw new RuntimeException("Different protein quantification units reported");
						}
							
					}
				}
				
				else if (QuantitationCvParams.EMPAI_VALUE.getAccession().equals(p.getAccession())) {
					protein.setCustomColumn("opt_empai", p.getValue());
				}
				
				// check if there's gel spot identifier
				else if (DAOCvParams.GEL_SPOT_IDENTIFIER.getAccession().equals(p.getAccession())) {
					protein.setCustomColumn("opt_gel_spotidentifier", p.getValue());
				}
				
				// check if there's gel identifier
				else if (DAOCvParams.GEL_IDENTIFIER.getAccession().equals(p.getAccession())) {
					protein.setCustomColumn("opt_gel_identifier", p.getValue());
				}
			}
			catch (MzTabParsingException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		
		return protein;
	}
	
	/**
	 * Adds the quantitative values for the given protein.
	 * @param protein
	 * @param ident
	 */
	private void addProteinQuantitativeValues(Protein protein, Identification ident) {
		// check subsample 1
		String abundance1 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE1.getAccession());
		String error1 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE1.getAccession());
		String std1 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE1.getAccession());
		
		if (abundance1 != null)
			protein.setAbundance(1, Double.parseDouble(abundance1), std1 != null ? Double.parseDouble(std1) : null, error1 != null ? Double.parseDouble(error1) : null);
		
		// check subsample 2
		String abundance2 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE2.getAccession());
		String error2 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE2.getAccession());
		String std2 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE2.getAccession());
		
		if (abundance2 != null)
			protein.setAbundance(2, Double.parseDouble(abundance2), std2 != null ? Double.parseDouble(std2) : null, error2 != null ? Double.parseDouble(error2) : null);
		
		// check subsample 3
		String abundance3 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE3.getAccession());
		String error3 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE3.getAccession());
		String std3 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE3.getAccession());
		
		if (abundance3 != null)
			protein.setAbundance(3, Double.parseDouble(abundance3), std3 != null ? Double.parseDouble(std3) : null, error3 != null ? Double.parseDouble(error3) : null);
		
		// check subsample 4
		String abundance4 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE4.getAccession());
		String error4 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE4.getAccession());
		String std4 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE4.getAccession());
		
		if (abundance4 != null)
			protein.setAbundance(4, Double.parseDouble(abundance4), std4 != null ? Double.parseDouble(std4) : null, error4 != null ? Double.parseDouble(error4) : null);
		
		// check subsample 5
		String abundance5 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE5.getAccession());
		String error5 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE5.getAccession());
		String std5 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE5.getAccession());
		
		if (abundance5 != null)
			protein.setAbundance(5, Double.parseDouble(abundance5), std5 != null ? Double.parseDouble(std5) : null, error5 != null ? Double.parseDouble(error5) : null);
		
		// check subsample 6
		String abundance6 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE6.getAccession());
		String error6 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE6.getAccession());
		String std6 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE6.getAccession());
		
		if (abundance6 != null)
			protein.setAbundance(6, Double.parseDouble(abundance6), std6 != null ? Double.parseDouble(std6) : null, error6 != null ? Double.parseDouble(error6) : null);
		
		// check subsample 7
		String abundance7 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE7.getAccession());
		String error7 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE7.getAccession());
		String std7 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE7.getAccession());
		
		if (abundance7 != null)
			protein.setAbundance(7, Double.parseDouble(abundance7), std7 != null ? Double.parseDouble(std7) : null, error7 != null ? Double.parseDouble(error7) : null);
		
		// check subsample 8
		String abundance8 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE8.getAccession());
		String error8 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE8.getAccession());
		String std8 = getFirstCvParamValue(ident.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE8.getAccession());
		
		if (abundance8 != null)
			protein.setAbundance(8, Double.parseDouble(abundance8), std8 != null ? Double.parseDouble(std8) : null, error8 != null ? Double.parseDouble(error8) : null);
	}
	
	/**
	 * Adds the quantitative values for the given peptide.
	 * @param peptide
	 * @param peptideItem
	 */
	private void addPeptideQuantitativeValues(Peptide peptide, PeptideItem peptideItem) {
		// check subsample 1
		String abundance1 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE1.getAccession());
		String error1 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE1.getAccession());
		String std1 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE1.getAccession());
		
		if (abundance1 != null)
			peptide.setAbundance(1, Double.parseDouble(abundance1), std1 != null ? Double.parseDouble(std1) : null, error1 != null ? Double.parseDouble(error1) : null);
		
		// check subsample 2
		String abundance2 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE2.getAccession());
		String error2 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE2.getAccession());
		String std2 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE2.getAccession());
		
		if (abundance2 != null)
			peptide.setAbundance(2, Double.parseDouble(abundance2), std2 != null ? Double.parseDouble(std2) : null, error2 != null ? Double.parseDouble(error2) : null);
		
		// check subsample 3
		String abundance3 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE3.getAccession());
		String error3 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE3.getAccession());
		String std3 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE3.getAccession());
		
		if (abundance3 != null)
			peptide.setAbundance(3, Double.parseDouble(abundance3), std3 != null ? Double.parseDouble(std3) : null, error3 != null ? Double.parseDouble(error3) : null);
		
		// check subsample 4
		String abundance4 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE4.getAccession());
		String error4 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE4.getAccession());
		String std4 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE4.getAccession());
		
		if (abundance4 != null)
			peptide.setAbundance(4, Double.parseDouble(abundance4), std4 != null ? Double.parseDouble(std4) : null, error4 != null ? Double.parseDouble(error4) : null);
		
		// check subsample 5
		String abundance5 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE5.getAccession());
		String error5 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE5.getAccession());
		String std5 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE5.getAccession());
		
		if (abundance5 != null)
			peptide.setAbundance(5, Double.parseDouble(abundance5), std5 != null ? Double.parseDouble(std5) : null, error5 != null ? Double.parseDouble(error5) : null);
		
		// check subsample 6
		String abundance6 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE6.getAccession());
		String error6 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE6.getAccession());
		String std6 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE6.getAccession());
		
		if (abundance6 != null)
			peptide.setAbundance(6, Double.parseDouble(abundance6), std6 != null ? Double.parseDouble(std6) : null, error6 != null ? Double.parseDouble(error6) : null);
		
		// check subsample 7
		String abundance7 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE7.getAccession());
		String error7 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE7.getAccession());
		String std7 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE7.getAccession());
		
		if (abundance7 != null)
			peptide.setAbundance(7, Double.parseDouble(abundance7), std7 != null ? Double.parseDouble(std7) : null, error7 != null ? Double.parseDouble(error7) : null);
		
		// check subsample 8
		String abundance8 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE8.getAccession());
		String error8 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE8.getAccession());
		String std8 = getFirstCvParamValue(peptideItem.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE8.getAccession());
		
		if (abundance8 != null)
			peptide.setAbundance(8, Double.parseDouble(abundance8), std8 != null ? Double.parseDouble(std8) : null, error8 != null ? Double.parseDouble(error8) : null);
	}

	/**
	 * Processes an identification's peptides'
	 * modifications, corrects the position and
	 * returns them as a list.
	 * @param identification
	 * @return
	 * @throws MzTabParsingException 
	 */
	private List<Modification> getIdentificationModifications(
			Identification identification) throws MzTabParsingException {
		HashSet<Modification> modifications = new HashSet<Modification>();

        if (identification == null) {
            throw new MzTabParsingException("Can not process modifications for non-existent identification: Identification can not be null!");
        }

        boolean allOK = true;
		for (PeptideItem p : identification.getPeptideItem()) {
			for (ModificationItem ptm : p.getModificationItem()) {
				// ignore modifications that can't be processed correctly
				if (p.getStart() == null || ptm.getModAccession() == null || ptm.getModLocation() == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Invalid PTM: no start position, no MOD or no MOD location!" + ptm.toString());
                    }
                    allOK = false;
                    continue;
                }
				Integer position = p.getStart().intValue() + ptm.getModLocation().intValue() - 1;
				
				Modification mod = new Modification(ptm.getModAccession(), position);
				
				modifications.add(mod);
			}
		}
        if (!allOK) {
            logger.warn("At least one invalid PTM: no start position, no MOD or no MOD location!");
        }
		
		return new ArrayList<Modification>(modifications);
	}

	/**
	 * Tries to convert the passed search engine to a cvParam.
	 * @param searchEngine
	 * @return
	 * @throws MzTabParsingException 
	 */
	private uk.ac.ebi.pride.jmztab.model.Param convertSearchEngine(
			String searchEngine) throws MzTabParsingException {
		SearchEngineParameter searchEngineParam = SearchEngineParameter.getParam(searchEngine);
		
		if (searchEngineParam != null)
			return searchEngineParam.toMzTabParam();
		else
			return null;
	}
	
	/**
	 * Converts a peptide's PeptidePTMs into a List
	 * of mzTab Modifications.
	 * @param p
	 * @return
	 * @throws MzTabParsingException 
	 */
	private List<Modification> getPeptideModifications(PeptideItem p) throws MzTabParsingException {
		ArrayList<Modification> modifications = new ArrayList<Modification>();

        boolean allOK = true;
		for (ModificationItem ptm : p.getModificationItem()) {
            if (p.getStart() == null || ptm.getModAccession() == null || ptm.getModLocation() == null) {
                if (logger.isTraceEnabled()) {
                    logger.debug("Invalid PTM: no start position, no MOD or no MOD location!");
                }
                allOK = false;
                continue;
            }

			Modification mod = new Modification(ptm.getModAccession(), ptm.getModLocation().intValue());
			
			modifications.add(mod);
		}
        if (!allOK) {
            logger.warn("At least one invalid PTM: no start position, no MOD or no MOD location!");
        }

		return modifications;
	}
	
	/**
	 * Extracts all search engine score related parameters
	 * from a peptide object and returns them in a list of
	 * mzTab ParamS.
	 * @param peptide
	 * @return
	 * @throws MzTabParsingException 
	 */
	private ParamList getPeptideSearchEngineScores(PeptideItem peptide) throws MzTabParsingException {
		ParamList scoreParams = new ParamList();

        if (peptide.getAdditional() != null) {
            for (CvParam param : peptide.getAdditional().getCvParam()) {
                if (Utils.PEPTIDE_SCORE_PARAM.isScoreAccession(param.getAccession())) {
                    scoreParams.add(convertParam(param));
                }
            }
        }

		return scoreParams;
	}

	/**
	 * Converts and Identification's peptides into a List
	 * of mzTab Peptides.
	 * @param ident
	 * @return
	 * @throws MzTabParsingException 
	 */
	private List<Peptide> convertIdentificationPeptides(Identification ident) throws MzTabParsingException {
		List<Peptide> convertedPeptides = new ArrayList<Peptide>(ident.getPeptideItem().size());
		
		for (PeptideItem peptideItem : ident.getPeptideItem()) {
			// create the peptide object
			Peptide peptide = new Peptide();
			
			peptide.setSequence(peptideItem.getSequence());
			peptide.setAccession(ident.getAccession());
			peptide.setUnitId(unitId);
			peptide.setDatabase(ident.getDatabase());
			peptide.setDatabaseVersion(ident.getDatabaseVersion());
			
			// set the peptide spectrum reference
            if (peptideItem.getSpectrum() != null) {

            }
			try {
                String spectrumReference = ( peptideItem.getSpectrum() == null ? "null" : Integer.toString(peptideItem.getSpectrum().getId()) );
				peptide.addSpecRef( new SpecRef("ms_file[1]", "spectrum=" + spectrumReference) );
			} catch (MzTabParsingException e) {
				throw new IllegalStateException("Failed to generate peptide spectrum reference.", e);
			}
			
			// set the search engine - is possible
			uk.ac.ebi.pride.jmztab.model.Param searchEngineParam = convertSearchEngine(ident.getSearchEngine());
			if (searchEngineParam != null)
				peptide.setSearchEngine(searchEngineParam);
			
			// set the search engine scores
			peptide.setSearchEngineScore(getPeptideSearchEngineScores(peptideItem));
			
			// set the modifications
			peptide.setModification(getPeptideModifications(peptideItem));
			
			// process the quant values
			addPeptideQuantitativeValues(peptide, peptideItem);
			
			// process the additional params -- mainly check for quant units
            if (peptideItem.getAdditional() != null) {
                for (CvParam p : peptideItem.getAdditional().getCvParam()) {
                    // check if there's a quant unit set
                    if (QuantitationCvParams.UNIT_RATIO.getAccession().equals(p.getAccession()) ||
                            QuantitationCvParams.UNIT_COPIES_PER_CELL.getAccession().equals(p.getAccession())) {

                        if (writer.getUnitMetadata(unitId) != null) {
                            if (writer.getUnitMetadata(unitId).getPeptideQuantificationUnit() == null)
                                writer.getUnitMetadata(unitId).setPeptideQuantificationUnit(convertParam(p));
                                // make sure there aren't different protein units reported
                            else {
                                if (!writer.getUnitMetadata(unitId).getPeptideQuantificationUnit().getAccession().equals(p.getAccession()))
                                    // TODO: add proper error handling
                                    throw new RuntimeException("Different peptide quantification units reported");
                            }

                        }
                    }
                }
            }

			convertedPeptides.add(peptide);
		}
		
		return convertedPeptides;
	}
	
	/**
	 * Returns the value of the first cvParam found
	 * with the given accession or NULL in case no
	 * cvParam with that value was found.
	 * @param param
	 * @param accession
	 * @return
	 */
	private String getFirstCvParamValue(Param param, String accession) {
        // this only makes sense if we have a list of params and an accession!
        if (param != null && accession != null) {
            for (CvParam p : param.getCvParam()) {
                if (accession.equals(p.getAccession())) {
                    return p.getValue();
                }
            }
        }
		return null;
	}

    private boolean stringValueOK(String value) {
        return value != null && !(value.contains("\n") || value.contains("\r"));
    }
	
	/**
	 * Returns the values of all cvParams with the
	 * given accession as a list or an empty list
	 * in case no cvParams with the given accessions
	 * were found.
	 * @param param
	 * @param accession
	 * @return
	 */
	private List<String> getCvParamValues(Param param, String accession) {
		ArrayList<String> values = new ArrayList<String>();
		
		for (CvParam p : param.getCvParam()) {
			if (accession.equals(p.getAccession()))
				values.add(p.getValue());
		}
		
		return values;
	}
}
