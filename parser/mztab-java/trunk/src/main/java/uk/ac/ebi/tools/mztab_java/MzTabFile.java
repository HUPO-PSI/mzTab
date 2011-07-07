package uk.ac.ebi.tools.mztab_java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ebi.tools.mztab_java.model.LineType;
import uk.ac.ebi.tools.mztab_java.model.Peptide;
import uk.ac.ebi.tools.mztab_java.model.PeptideTableField;
import uk.ac.ebi.tools.mztab_java.model.Protein;
import uk.ac.ebi.tools.mztab_java.model.ProteinTableField;
import uk.ac.ebi.tools.mztab_java.model.SmallMolecule;
import uk.ac.ebi.tools.mztab_java.model.SmallMoleculeTableField;
import uk.ac.ebi.tools.mztab_java.model.Unit;
import uk.ac.ebi.tools.mztab_java.util.TsvTableParser;

/**
 * Represents a mzTab file.
 * @author jg
 *
 */
public class MzTabFile {
	/**
	 * The separator used in the file.
	 */
	public static final String SEPARATOR = "\t";
	/**
	 * The line separator used.
	 */
	public static final String EOL = System.getProperty( "line.separator" );
	/**
	 * The currently used maximum peptide
	 * index. This variable is required to add
	 * new petpides.
	 */
	private int maxPeptideIndex = -1;
	/**
	 * The currently used maximum small molecule
	 * index. This variable is required to add
	 * new small molecules.
	 */
	private int maxSmallMoleculeIndex = -1;
	/**
	 * The maximum number of subsamples reported
	 * for protein.
	 */
	private int maxProteinSubsamples = 0;
	/**
	 * The maximum number of subsmaples reported
	 * for peptides.
	 */
	private int maxPeptideSubsamples = 0;
	/**
	 * The maximum number of subsamples reported
	 * for small molecules
	 */
	private int maxSmallMoleculeSubsamples = 0;
	/**
	 * The distinct custom columns observed
	 * in protein objects.
	 */
	private HashSet<String> proteinCustomColumns = new HashSet<String>();
	/**
	 * The distinct custom columns observed
	 * in peptide objects.
	 */
	private HashSet<String> peptideCustomColumns = new HashSet<String>();
	/**
	 * The distinct custom columns observed
	 * in small molecules.
	 */
	private HashSet<String> smallMoleculeCustomColumns = new HashSet<String>();
	/**
	 * The pattern to extract the UNIT_ID from
	 * a meta-data line.
	 */
	private static final Pattern metadataUnitIdPattern = Pattern.compile("^\\w{3}\t([^-]+)-.*");
	/**
	 * The "central" storage of proteins. The key
	 * is the PROTEIN_ID created by joining the UNIT_ID 
	 * and the protein's accession. Various other HashMaps
	 * then refer to this central storage.
	 */
	private HashMap<String, Protein> proteins = new HashMap<String, Protein>();;
	/**
	 * A HashMap used to retrieve all proteins
	 * identified through a given accession.
	 */
	private HashMap<String, HashSet<String>> accessionToProtein = new HashMap<String, HashSet<String>>();
	/**
	 * A HashMap to retrieve all proteins associated
	 * with a given unit. The UNIT_ID is used as key.
	 */
	private HashMap<String, HashSet<String>> unitProteins = new HashMap<String, HashSet<String>>();
	/**
	 * The central storage for units. The UNIT_ID is used
	 * as key.
	 */
	private HashMap<String, Unit> units = new HashMap<String, Unit>();
	/**
	 * The distinct unit ids found throughout all sections.
	 */
	private HashSet<String> distinctUnitIds = new HashSet<String>();
	/**
	 * The central storage of all peptides into the mzTab file.
	 * As peptides cannot be unambiguously identified there 0-based
	 * index in the file is used as key.
	 */
	private HashMap<Integer, Peptide> peptides = new HashMap<Integer, Peptide>();
	/**
	 * The peptides identifying a given protein in a given unit. The PROTEIN_ID
	 * is used as key.
	 */
	private HashMap<String, HashSet<Integer>> proteinPeptides = new HashMap<String, HashSet<Integer>>();
	/**
	 * The peptides identifying a given protein accession irrespective
	 * of the unit.
	 */
	private HashMap<String, HashSet<Integer>> proteinAccessionPeptides = new HashMap<String, HashSet<Integer>>();
	/**
	 * A HashMap to retrieve all peptides of a specified unit.
	 */
	private HashMap<String, HashSet<Integer>> unitPeptides = new HashMap<String, HashSet<Integer>>();
	/**
	 * A HashMap to retrieve all peptides containing a certain
	 * sequence.
	 */
	private HashMap<String, HashSet<Integer>> sequenceToPeptides = new HashMap<String, HashSet<Integer>>();
	/**
	 * The central storage for small molecules. As small
	 * molecules cannot be unambiguously identified their
	 * 0-based index in the file is used as key.
	 */
	private HashMap<Integer, SmallMolecule> smallMolecules = new HashMap<Integer, SmallMolecule>();
	/**
	 * A HashMap to retrieve all small molecules for a given
	 * identifier.
	 */
	private HashMap<String, HashSet<Integer>> identifierToSmallMolecule = new HashMap<String, HashSet<Integer>>();
	/**
	 * A HashMap top retrieve a unit's small molecules.
	 */
	private HashMap<String, HashSet<Integer>> unitSmallMolecules = new HashMap<String, HashSet<Integer>>();
	/**
	 * The parser to parse protein table lines.
	 */
	private TsvTableParser proteinTableParser;
	/**
	 * The parser to parse peptide table lines.
	 */
	private TsvTableParser peptideTableParser;
	/**
	 * The parser to parser small molecule lines.
	 */
	private TsvTableParser smallMoleculeTableParser;
	/**
	 * Creates a new empty MzTabFile object.
	 */
	public MzTabFile() {
		
	}
	
	/**
	 * Creates a new MzTabFile object based on the given
	 * mzTabFile.
	 * @param mzTabFile The mzTabFile to parse.
	 * @throws MzTabParsingException
	 */
	public MzTabFile(File mzTabFile) throws MzTabParsingException {
		try {
			BufferedReader in = new BufferedReader(new FileReader(mzTabFile));
			String line;
			// read and parse the file line by line
			while ((line = in.readLine()) != null) {
				parseMzTabLine(line);
			}
		} catch (FileNotFoundException e) {
			throw new MzTabParsingException("Could not find specified mzTab file.", e);
		} catch (IOException e) {
			throw new MzTabParsingException("Failed to read mzTab file.", e);
		}
	}
	
	/**
	 * Creates a new MzTabFile based on the given mzTab
	 * formatted string.
	 * @param mzTabString An mzTab formatted string.
	 * @throws MzTabParsingException
	 */
	public MzTabFile(String mzTabString) throws MzTabParsingException {
		parseMzTab(mzTabString);
	}
	
	/**
	 * Parses a given mzTab string and merges it with the
	 * existing data (if any).
	 * @param mzTabString An mzTab formatted string to parse.
	 * @throws MzTabParsingException
	 */
	public void parseMzTab(String mzTabString) throws MzTabParsingException {
		// split the string into lines
		String[] lines = mzTabString.split(EOL);
		
		// parse the lines
		for (String line : lines)
			parseMzTabLine(line);
	}
	
	/**
	 * Parses the given mztab line.
	 * @param line The mzTab line to parse.
	 * @throws MzTabParsingException
	 */
	private void parseMzTabLine(String line) throws MzTabParsingException {
		line = line.trim();
		
		// get the line type
		LineType type = LineType.getLineType(line);
		
		// ignore unidentified lines
		if (type == null)
			return;
		
		switch (type) {
			case METADATA:
				parseMetadataLine(line);
				break;
			case PROTEIN_TABLE_HEADER:
				// create a new protein table parser
				proteinTableParser = new TsvTableParser(line);
				break;
			case PROTEIN:
				parseProteinLine(line);
				break;
			case PEPTIDE_TABLE_HEADER:
				peptideTableParser = new TsvTableParser(line);
				break;
			case PEPTIDE:
				parsePeptideLine(line, ++maxPeptideIndex);
				break;
			case SMALL_MOLECULE_TABLE_HEADER:
				smallMoleculeTableParser = new TsvTableParser(line);
				break;
			case SMALL_MOLECULE:
				parseSmallMoleculeLine(line, ++maxSmallMoleculeIndex);
				break;
			case COMMENT:
				// TODO: handle comment lines
				break;
		}
	}
	
	/**
	 * Parses a metadata line.
	 * @param line
	 * @throws MzTabParsingException
	 */
	private void parseMetadataLine(String line) throws MzTabParsingException {
		// extract the unit id from the line
		Matcher matcher = metadataUnitIdPattern.matcher(line);
		
		if (!matcher.find())
			throw new MzTabParsingException("Invalid meta-data line encountered. Failed to extract unit id: <" + line + ">");
		
		// get the unit id
		String unitId = matcher.group(1).trim();
		
		// check if the Unit object was already created
		if (!units.containsKey(unitId))
			units.put(unitId, new Unit());
		
		// parse the line
		units.get(unitId).unmarshall(line);
		
		// save the unit id
		distinctUnitIds.add(unitId);
	}
	
	/**
	 * Parses a protein line and creates and stores the associated protein.
	 * @param line
	 * @throws MzTabParsingException
	 */
	private void parseProteinLine(String line) throws MzTabParsingException {
		// make sure the protein parser was created
		if (proteinTableParser == null)
			throw new MzTabParsingException("Encountered a protein line before the protein table header line.");
		
		// parse the line
		try {
			Map<String, String> parsedLine = proteinTableParser.parseTableLine(line);
			
			// create the protein object
			Protein protein = new Protein(parsedLine);
			
			// save the protein
			saveProtein(protein);
		}
		catch (IllegalStateException e) {
			throw new MzTabParsingException("Failed to parse protein.", e);
		}
	}
	
	/**
	 * Parses a peptide line and creates and stores the associated peptide
	 * object.
	 * @param line The mzTab line to parse.
	 * @param index The 0-based index of the line in the peptide table.
	 * @throws MzTabParsingException
	 */
	private void parsePeptideLine(String line, int index) throws MzTabParsingException {
		// make sure the parser was created
		if (peptideTableParser == null)
			throw new MzTabParsingException("Encountered a peptide line before the peptide table header line.");
		
		try {
			// parse the line
			Map<String, String> parsedLine = peptideTableParser.parseTableLine(line);
			
			// create the peptide
			Peptide peptide = new Peptide(parsedLine, index);
			
			// save the peptide
			savePeptide(peptide);
		}
		catch (IllegalStateException e) {
			throw new MzTabParsingException("Failed to parse peptide line.", e);
		}
	}
	
	/**
	 * Parses a small molecule line and creates and stores the associated 
	 * small molecule object.
	 * @param line The mzTab line to parse.
	 * @param index The 0-based index of the line in the small molecule table.
	 * @throws MzTabParsingException
	 */
	private void parseSmallMoleculeLine(String line, int index) throws MzTabParsingException {
		// make sure the parser was created
		if (smallMoleculeTableParser == null)
			throw new MzTabParsingException("Encountered a small molecule line before the small molecule table header line.");
		
		// parse the line
		try {
			Map<String, String> parsedLine = smallMoleculeTableParser.parseTableLine(line);
			
			// create the small molecule
			SmallMolecule smallMolecule = new SmallMolecule(parsedLine, index);
			
			// save it
			saveSmallMolecule(smallMolecule);
		}
		catch (IllegalStateException e) {
			throw new MzTabParsingException("Failed to parse small molecule line.", e);
		}
	}
	
	/**
	 * Saves the given protein and updates all HashMaps
	 * accordingly.
	 * @param protein The protein to save.
	 * @throws MzTabParsingException Thrown in case the protein already exists.
	 */
	private void saveProtein(Protein protein) throws MzTabParsingException {
		// create the unambiguous protein id
		String proteinId = protein.getUnitId() + protein.getAccession();
		// check if the protein already exists
		if (proteins.containsKey(proteinId)) {
			throw new MzTabParsingException("Duplicate protein entry " + protein.getAccession() + " in unit " + protein.getUnitId() + ". Proteins must be unambiguously identified by their accession in a given unit.");
		}
		
		// set the maximum number of observed protein subsamples
		Integer maxSubsetId = Collections.max(protein.getSubsampleIndexes());
		
		if (maxSubsetId != null && maxProteinSubsamples < maxSubsetId)
			maxProteinSubsamples = maxSubsetId;
		
		// save the optional columns
		proteinCustomColumns.addAll(protein.getCustom().keySet());
		
		// store the protein
		proteins.put(proteinId, protein);
		
		// save the unit id just in case
		distinctUnitIds.add(protein.getUnitId());
		
		// update the protein accessions HashMap
		if (!accessionToProtein.containsKey(protein.getAccession()))
			accessionToProtein.put(protein.getAccession(), new HashSet<String>(1));
		// add the protein's id
		accessionToProtein.get(protein.getAccession()).add(proteinId);
		
		// update the unit proteins HashMap
		if (!unitProteins.containsKey(protein.getUnitId()))
			unitProteins.put(protein.getUnitId(), new HashSet<String>(1));
		// add the protein's id
		unitProteins.get(protein.getUnitId()).add(proteinId);
	}
	
	/**
	 * Saves the given peptide and updates the HashMaps accordingly. 
	 * To add a new peptide the peptide's index should be
	 * set to -1. In case a parsed peptide is added the index is used.
	 * @param peptide The peptide to add.
	 * @throws MzTabParsingException Thrown in case a peptide with the given index already exists.
	 */
	private void savePeptide(Peptide peptide) throws MzTabParsingException {
		// if the peptide doesn't contain an index, add the next "free" index
		if (peptide.getIndex() == -1)
			peptide.setIndex(++maxPeptideIndex);
		
		// check that the peptide hasn't been set yet
		if (peptides.containsKey(peptide.getIndex()))
			throw new MzTabParsingException("Duplicate peptide encountered for row " + peptide.getIndex() + 1 + ".");
		
		// set the maximum number of observed protein subsamples
		Integer maxSubsetId = Collections.max(peptide.getSubsampleIds());
		
		if (maxSubsetId != null && maxPeptideSubsamples < maxSubsetId)
			maxPeptideSubsamples = maxSubsetId;
		
		// save the optional columns
		peptideCustomColumns.addAll(peptide.getCustom().keySet());
		
		// save the peptide
		peptides.put(peptide.getIndex(), peptide);
		
		// save the unit id
		distinctUnitIds.add(peptide.getUnitId());
		
		// add the peptide to the proteinPeptides HashMap
		String proteinId = peptide.getUnitId() + peptide.getAccession();
		if (!proteinPeptides.containsKey(proteinId))
			proteinPeptides.put(proteinId, new HashSet<Integer>(1));
		// save the peptide in the proteinPeptides HashMap
		proteinPeptides.get(proteinId).add(peptide.getIndex());
		
		// add the peptide to the proteinAccessionPeptides HashMap
		if (!proteinAccessionPeptides.containsKey(peptide.getAccession()))
			proteinAccessionPeptides.put(peptide.getAccession(), new HashSet<Integer>(1));
		proteinAccessionPeptides.get(peptide.getAccession()).add(peptide.getIndex());
		
		// add the peptide to the unitPeptides HashMap
		if (!unitPeptides.containsKey(peptide.getUnitId()))
			unitPeptides.put(peptide.getUnitId(), new HashSet<Integer>(1));
		unitPeptides.get(peptide.getUnitId()).add(peptide.getIndex());
		
		// add the peptide to the "sequenceToPeptides" HashMap
		if (!sequenceToPeptides.containsKey(peptide.getSequence()))
			sequenceToPeptides.put(peptide.getSequence(), new HashSet<Integer>(1));
		sequenceToPeptides.get(peptide.getSequence()).add(peptide.getIndex());
	}
	
	/**
	 * Saves the given small molecule and updates the HashMaps accordingly. 
	 * To add a new small molecule the small molecule's index should be
	 * set to -1. In case a parsed small molecule is added the index is used.
	 * @param smallMolecule The small molecule to add.
	 * @throws MzTabParsingException Thrown in case a small molecule with the given index already exists.
	 */
	private void saveSmallMolecule(SmallMolecule smallMolecule) throws MzTabParsingException {
		// if the small molecule doesn't contain an index, add the next "free" index
		if (smallMolecule.getIndex() == -1)
			smallMolecule.setIndex(++maxSmallMoleculeIndex);
		
		// make sure the small molecule doesn't yet exist
		if (smallMolecules.containsKey(smallMolecule.getIndex()))
			throw new MzTabParsingException("Duplicate small molecule encountered for row " + smallMolecule.getIndex() + 1 + ".");
		
		// set the maximum number of observed protein subsamples
		Integer maxSubsetId = Collections.max(smallMolecule.getSubsampleIds());
		
		if (maxSubsetId != null && maxSmallMoleculeSubsamples < maxSubsetId)
			maxSmallMoleculeSubsamples = maxSubsetId;
		
		// save the optional columns
		smallMoleculeCustomColumns.addAll(smallMolecule.getCustom().keySet());
		
		// save the small molecule
		smallMolecules.put(smallMolecule.getIndex(), smallMolecule);
		
		// save the unit id
		distinctUnitIds.add(smallMolecule.getUnitId());
		
		// save the small molecule in the identifier to smallMolecule HashMap
		if (smallMolecule.getIdentifier() != null) {
			for (String identifier : smallMolecule.getIdentifier()) {
				if (!identifierToSmallMolecule.containsKey(identifier))
					identifierToSmallMolecule.put(identifier, new HashSet<Integer>(1));
				
				identifierToSmallMolecule.get(identifier).add(smallMolecule.getIndex());
			}
		}
		
		// save the small molecule in the unit small molecules HashMap
		if (!unitSmallMolecules.containsKey(smallMolecule.getUnitId()))
			unitSmallMolecules.put(smallMolecule.getUnitId(), new HashSet<Integer>(1));
		unitSmallMolecules.get(smallMolecule.getUnitId()).add(smallMolecule.getIndex());
	}
	
	/**
	 * Returns a list of proteins identified through the
	 * passed proteinIds. Throws an IllegalStateException
	 * in case on of the referenced proteins can not be
	 * found.
	 * @param proteinIds A Collection of protein ids.
	 * @return A List of ProteinS.
	 */
	private Collection<Protein> getProteinsForIds(Collection<String> proteinIds) {
		// get all proteins identified through this accession and add them to a list
		List<Protein> proteins = new ArrayList<Protein>(proteinIds.size());
		
		for (String proteinId : proteinIds) {
			// make sure the protein exists
			if (!this.proteins.containsKey(proteinId))
				throw new IllegalStateException("Referenced protein <" + proteinId + "> does not exist.");
			
			proteins.add(this.proteins.get(proteinId));
		}
		
		return proteins;
	}
	
	/**
	 * Returns a Collection of PeptideS identified through
	 * the passed index. Throws an IllegalStateException
	 * in case on of the peptides does not exist.
	 * @param peptideIndexes A Collection of Peptide indexes.
	 * @return A Collection of PeptideS.
	 */
	private Collection<Peptide> getPeptidesForIds(Collection<Integer> peptideIndexes) {
		// create a list to hold the peptides
		List<Peptide> peptides = new ArrayList<Peptide>(peptideIndexes.size());
		
		for (Integer index : peptideIndexes) {
			// make sure the peptide exists
			if (!this.peptides.containsKey(index))
				throw new IllegalStateException("Referenced peptide <" + index + "> does not exist.");
			
			peptides.add(this.peptides.get(index));
		}
		
		return peptides;
	}
	
	/**
	 * Returns a Collection of SmallMoleculeS identified through
	 * the passed index. Throws an IllegalStateException
	 * in case on of the small molecules does not exist.
	 * @param indexes A Collection of small molecule indexes.
	 * @return A Collection of SmallMoleculeS.
	 */
	private Collection<SmallMolecule> getSmallMoleculesForIds(Collection<Integer> indexes) {
		// create a list to hold the small molecules
		List<SmallMolecule> smallMoleculeList = new ArrayList<SmallMolecule>(indexes.size());
		
		for (Integer index : indexes) {
			// make sure the peptide exists
			if (!smallMolecules.containsKey(index))
				throw new IllegalStateException("Referenced small molecule <" + index + "> does not exist.");
			
			smallMoleculeList.add(smallMolecules.get(index));
		}
		
		return smallMoleculeList;
	}
	
	/**
	 * Returns the metadata for the given unit as a 
	 * Unit object. Returns null in case the unit
	 * does not exist.
	 * @param unitId The unit'd id.
	 * @return A Unit object or null in case the unit doesn't exist.
	 */
	public Unit getUnitMetadata(String unitId) {
		return units.get(unitId);
	}
	
	/**
	 * Returns the metadata as a Unit object
	 * for the protein's unit. Returns null in
	 * case no metadata is availbale.
	 * @param protein The protein object to get the metadata for.
	 * @return A Unit object or null in case no metadata was specified.
	 */
	public Unit getUnitMetadata(Protein protein) {
		return getUnitMetadata(protein.getUnitId());
	}
	
	/**
	 * Returns the metadata as a Unit object
	 * for the peptide's unit. Returns null in
	 * case no metadata is availbale.
	 * @param peptide The peptide object to get the metadata for.
	 * @return A Unit object or null in case no metadata was specified.
	 */
	public Unit getUnitMetadata(Peptide peptide) {
		return getUnitMetadata(peptide.getUnitId());
	}
	
	/**
	 * Returns the metadata as a Unit object
	 * for the small molecule's unit. Returns null in
	 * case no metadata is availbale.
	 * @param smallMolecule The small molecule object to get the metadata for.
	 * @return A Unit object or null in case no metadata was specified.
	 */
	public Unit getUnitMetadata(SmallMolecule smallMolecule) {
		return getUnitMetadata(smallMolecule.getUnitId());
	}
	
	/**
	 * Returns the complete metadata as a list of
	 * Unit objects.
	 * @return A list of Unit objects.
	 */
	public Collection<Unit> getUnitMetadata() {
		return units.values();
	}
	
	/**
	 * A returns a collection holding all unit
	 * ids in the file.
	 * @return A collection of unit ids.
	 */
	public Set<String> getUnitIds() {
		return distinctUnitIds;
	}
	
	/**
	 * Returns all proteins identified by the
	 * given accession.
	 * @param accession The accession identifying the proteins.
	 * @return A collection of proteins identified by the given accession.
	 */
	public Collection<Protein> getProtein(String accession) {
		// if the accession is unknown, return an empty list
		if (!accessionToProtein.containsKey(accession))
			return Collections.emptyList();
		
		// return the proteins
		return getProteinsForIds(accessionToProtein.get(accession));
	}
	
	/**
	 * Returns the protein identified by the given accesion
	 * in the given unit. Returns null in case the protein
	 * does not exist.
	 * @param accession The protein's accession.
	 * @param unitId The protein's unit's id.
	 * @return A Protein object or null in case the protein does not exist.
	 */
	public Protein getProtein(String accession, String unitId) {
		String proteinId = unitId + accession;
		
		return proteins.get(proteinId);
	}
	
	/**
	 * Returns all proteins of a given unit or null
	 * in case the unit does not exist.
	 * @param unitId The unit's id.
	 * @return A Collection of ProteinS identified in this unit or null in case the unit does not exist.
	 */
	public Collection<Protein> getUnitProteins(String unitId) {
		// make sure the unit exists
		if (!unitProteins.containsKey(unitId))
			return null;
		
		return getProteinsForIds(unitProteins.get(unitId));
	}
	
	/**
	 * Returns a Collection holding all proteins identified
	 * in this mzTabFile.
	 * @return A Collection of ProteinS
	 */
	public Collection<Protein> getProteins() {
		return proteins.values();
	}
	
	/**
	 * Returns all peptides identifying the given
	 * protein.
	 * @param protein The Protein to get the peptides for.
	 * @return A Collection of PeptideS or null in case the protein is not referenced in the peptide table.
	 */
	public Collection<Peptide> getProteinPeptides(Protein protein) {
		return getProteinPeptides(protein.getAccession(), protein.getUnitId());
	}
	
	/**
	 * Returns all peptides identifying the given
	 * protein in the given unit.
	 * @param accession The protein accession.
	 * @param unitId The unit id.
	 * @return A list of PeptideS or null in case this protein is not referenced in the peptide table.
	 */
	public Collection<Peptide> getProteinPeptides(String accession, String unitId) {
		// create the protein id
		String proteinId = unitId + accession;
		
		// make sure the protein exists
		if (!proteinPeptides.containsKey(proteinId))
			return null;
		
		return getPeptidesForIds(proteinPeptides.get(proteinId));
	}
	
	/**
	 * Returns all peptides identifying a given protein
	 * irrespective of the unit.
	 * @param accession The protein's accession.
	 * @return A Collection of PeptideS or null in case the accession is not referenced in the peptide table.
	 */
	public Collection<Peptide> getProteinPeptides(String accession) {
		// make sure the accession was referenced
		if (!proteinAccessionPeptides.containsKey(accession))
			return null;
		
		return getPeptidesForIds(proteinAccessionPeptides.get(accession));
	}
	
	/**
	 * Returns all peptides identified in a given unit.
	 * @param unitId The unit's id.
	 * @return A Collection of PeptideS or null in case the unitId does not contain any peptides.
	 */
	public Collection<Peptide> getPeptides(String unitId) {
		// check if the unit contains peptides
		if (!unitPeptides.containsKey(unitId))
			return null;
		
		return getPeptidesForIds(unitPeptides.get(unitId));
	}
	
	/**
	 * Returns a Collection holding all peptides found in the
	 * mzTab file.
	 * @return A Collection of peptides.
	 */
	public Collection<Peptide> getPeptides() {
		return peptides.values();
	}
	
	/**
	 * Returns all peptides with the given sequence.
	 * @param sequence The amino acid sequence to get the peptides for.
	 * @return A Collection of PeptideS or null in case the sequence is not present in the peptide table.
	 */
	public Collection<Peptide> getPeptidesForSequence(String sequence) {
		// make sure the sequence is present
		if (!sequenceToPeptides.containsKey(sequence))
			return null;
		
		return getPeptidesForIds(sequenceToPeptides.get(sequence));
	}
	
	/**
	 * Returns all SmallMoleculeS identified in the
	 * mzTab file.
	 * @return A Collection of SmallMoleculeS
	 */
	public Collection<SmallMolecule> getSmallMolecules() {
		return smallMolecules.values();
	}
	
	/**
	 * Returns all small molecules for the given unit.
	 * @param unitId The unit'd id.
	 * @return A Collection of SmallMolecules or null in case there are not small molcules identified for the passed unit.
	 */
	public Collection<SmallMolecule> getSmallMolecules(String unitId) {
		// make sure the unit contains small molecules
		if (!unitSmallMolecules.containsKey(unitId))
			return null;
		
		return getSmallMoleculesForIds(unitSmallMolecules.get(unitId));
	}
	
	/**
	 * Returns all small molecules having the given identifier.
	 * @param identifier The identifier to get the small molecules for.
	 * @return A Collection of SmallMoleculeS or null in case the identifier is not used in the SmallMolecule section.
	 */
	public Collection<SmallMolecule> getSmallMoleculesForIdentifier(String identifier) {
		// make sure the identifier is used
		if (!identifierToSmallMolecule.containsKey(identifier))
			return null;
		
		return getSmallMoleculesForIds(identifierToSmallMolecule.get(identifier));
	}
	
	/**
	 * Removes the unit identified by this
	 * UNIT_ID from the mzTab file.
	 * @param unitId The unit's id.
	 * @param removeProteins Indicates whether associated proteins should be removed
	 * @param removePeptides Indicates whether associated petpides should be removed
	 * @param removeSmallMolecules Indicates whether associated small molecules should be removed
	 */
	public void removeUnit(String unitId, boolean removeProteins, boolean removePeptides, boolean removeSmallMolecules) {
		// remove the unit
		units.remove(unitId);
		
		// remove the associated proteins if set
		if (removeProteins && unitProteins.containsKey(unitId)) {
			for (String proteinId : unitProteins.get(unitId)) {
				Protein p = proteins.remove(proteinId);
				
				if (p != null && accessionToProtein.containsKey(p.getAccession()))
					accessionToProtein.get(p.getAccession()).remove(proteinId);
			}
			
			unitProteins.remove(unitId);
		}
			
		// remove the associated peptides if set
		if (removePeptides && unitPeptides.containsKey(unitId)) {
			for (Integer pepId :unitPeptides.get(unitId)) {
				Peptide p = peptides.remove(pepId);
				
				if (p == null)
					continue;
				
				if (proteinPeptides.containsKey(unitId + p.getAccession()))
					proteinPeptides.get(unitId + p.getAccession()).remove(pepId);
				
				if (proteinAccessionPeptides.containsKey(p.getAccession()))
					proteinAccessionPeptides.get(p.getAccession()).remove(pepId);
				
				if (sequenceToPeptides.containsKey(p.getSequence()))
					sequenceToPeptides.get(p.getSequence()).remove(pepId);
			}
			
			unitPeptides.remove(unitId);
		}
		
		// remove the associated small molecules if set
		if (removeSmallMolecules && unitSmallMolecules.containsKey(unitId)) {
			for (Integer smallMoleculeId : unitSmallMolecules.get(unitId)) {
				SmallMolecule m = smallMolecules.remove(smallMoleculeId);
				
				if (m == null)
					continue;
				
				for (String identifier : m.getIdentifier()) {
					if (identifierToSmallMolecule.containsKey(identifier))
						identifierToSmallMolecule.get(identifier).remove(smallMoleculeId);
				}
			}
			
			unitSmallMolecules.remove(unitId);
		}
	}
	
	/**
	 * Removes the unit from the mzTab file.
	 * @param unit The unit to remove.
	 * @param removeProteins Indicates whether associated proteins should be removed
	 * @param removePeptides Indicates whether associated petpides should be removed
	 * @param removeSmallMolecules Indicates whether associated small molecules should be removed
	 */
	public void removeUnit(Unit unit, boolean removeProteins, boolean removePeptides, boolean removeSmallMolecules) {
		removeUnit(unit.getUnitId(), removeProteins, removePeptides, removeSmallMolecules);
	}
	
	/**
	 * Removes a protein from the mzTab file.
	 * @param accession The protein's accession.
	 * @param unitId The protein's unit'd id.
	 * @param removePeptides Indicates whether potentially associated peptides should be removed as well.
	 */
	public void removeProtein(String accession, String unitId, boolean removePeptides) {
		String proteinId = unitId + accession;
		
		// remove the protein from the proteins hashmap
		proteins.remove(proteinId);
		
		// remove the protein from the various other HashMaps
		if (unitProteins.containsKey(unitId))
			unitProteins.get(unitId).remove(proteinId);
		if (accessionToProtein.containsKey(accession))
			accessionToProtein.get(accession).remove(proteinId);
		
		// remove the peptides if indicated
		if (removePeptides && proteinPeptides.containsKey(proteinId)) {
			for (Integer peptideId : proteinPeptides.get(proteinId))
				removePeptide(peptideId);
		}
	}
	
	/**
	 * Removes a protein from the mzTab file.
	 * @param protein The protein to remove.
	 * @param removePeptides Indicates whether potentially associated peptides should be removed as well.
	 */
	public void removeProtein(Protein protein, boolean removePeptides) {
		removeProtein(protein.getAccession(), protein.getUnitId(), removePeptides);
	}
	
	/**
	 * Removes the peptide identified
	 * by the index from the mzTab file.
	 * @param index 0-based index of the peptide in the peptide table. WARNING: This index does not shift but remains the same even if preceeding peptides are removed.
	 */
	public void removePeptide(Integer index) {
		// remove the peptide
		Peptide p = peptides.remove(index);
		
		// return if the peptide didn't exist
		if (p == null)
			return;
		
		// remove the peptide from the various hashmaps
		if (proteinPeptides.containsKey(p.getUnitId() + p.getAccession()))
			proteinPeptides.get(p.getUnitId() + p.getAccession()).remove(index);
		if (proteinAccessionPeptides.containsKey(p.getAccession()))
			proteinAccessionPeptides.get(p.getAccession()).remove(index);
		if (unitPeptides.containsKey(p.getUnitId()))
			unitPeptides.get(p.getUnitId()).remove(index);
		if (sequenceToPeptides.containsKey(p.getSequence()))
			sequenceToPeptides.get(p.getSequence()).remove(index);
	}
	
	/**
	 * Removes the peptide identified
	 * by the index from the mzTab file.
	 * @param peptide The peptide to remove.
	 */
	public void removePeptide(Peptide peptide) {
		removePeptide(peptide.getIndex());
	}
	
	/**
	 * Removes a small molecule from the mzTab file.
	 * @param index The 0-based index of the small molecule in the Small Molecule table. WARNING: This index does not shift but remains the same even if preceeding small molecules are removed.
	 */
	public void removeSmallMolecule(Integer index) {
		// delete the small molecule
		SmallMolecule s = smallMolecules.remove(index);
		
		// make sure it existed.
		if (s == null)
			return;
		
		// remove the small molecule from all HashMaps
		for (String identifier : s.getIdentifier()) {
			if (identifierToSmallMolecule.containsKey(identifier))
				identifierToSmallMolecule.get(identifier).remove(index);
		}
		if (unitSmallMolecules.containsKey(s.getUnitId()))
			unitSmallMolecules.get(s.getUnitId()).remove(index);
	}
	
	/**
	 * Removes a small molecule from the mzTab file.
	 * @param smallMolecule The small molcule to remove.
	 */
	public void removeSmallMolecule(SmallMolecule smallMolecule) {
		removeSmallMolecule(smallMolecule.getIndex());
	}
	
	/**
	 * Adds the protein to the mzTab file.
	 * @param protein The protein to add.
	 * @throws MzTabParsingException Thrown if the protein (based on its UNIT_ID and accession) already exists.
	 */
	public void addProtein(Protein protein) throws MzTabParsingException {
		saveProtein(protein);
	}
	
	/**
	 * Adds a new peptide to the mzTab file. The peptide's
	 * index property is overwritten and a new index created.
	 * @param peptide The peptide to add.
	 */
	public void addPeptide(Peptide peptide) {
		try {
			// set the peptide's index
			peptide.setIndex(++maxPeptideIndex);
			// save the peptide
			savePeptide(peptide);
		} catch (MzTabParsingException e) {
			// this should never happen
			throw new IllegalStateException("Failed to save peptide.", e);
		}
	}
	
	/**
	 * Adds a new small molecule to the mzTab file. The 
	 * small molecule's index property is overwritten 
	 * and a new index created.
	 * @param smallMolecule The small molecule to add.
	 */
	public void addSmallMolecule(SmallMolecule smallMolecule) {
		try {
			// set the small molecule's index
			smallMolecule.setIndex(++maxSmallMoleculeIndex);
			// save the small molecule
			saveSmallMolecule(smallMolecule);
		} catch(MzTabParsingException e) {
			// this should never happen
			throw new IllegalStateException("Failed to save small molecule.", e);
		}
	}
	
	/**
	 * Converts the mzTabFile Object to an mzTab formatted
	 * String.
	 * @return An mzTab formatted String representing the object.
	 */
	public String toMzTab() {
		String mzTab = "";
		
		// add the units
		for (Unit unit : units.values())
			mzTab += unit.toMzTab();
		
		// add an empty line
		mzTab += EOL;
		
		// create the protein table header
		List<String> proteinCustom = new ArrayList<String>(proteinCustomColumns);
		mzTab += createProteinTableHeader(proteinCustom);
		
		// add the proteins
		for (Protein p : proteins.values())
			mzTab += p.toMzTab(maxProteinSubsamples, proteinCustom);
		
		// empty line
		mzTab += EOL;
		
		// write the peptide table header
		List<String> peptideCustom = new ArrayList<String>(peptideCustomColumns);
		mzTab += createPeptideHeader(peptideCustom);
		
		// add the peptides
		for (Peptide p : peptides.values())
			mzTab += p.toMzTab(maxPeptideSubsamples, peptideCustom);
		
		// write the small molecule table header
		List<String> smallMoleculeCustom = new ArrayList<String>(smallMoleculeCustomColumns);
		mzTab += createSmallMoleculesHeader(smallMoleculeCustom);
		
		// add the small molecules
		for (SmallMolecule m : smallMolecules.values())
			mzTab += m.toMzTab(maxSmallMoleculeSubsamples, smallMoleculeCustom);
		
		return mzTab;
	}
	
	/**
	 * Creates the protein table header including the EOL.
	 * @param customHeader
	 * @return
	 */
	private String createProteinTableHeader(List<String> customHeader) {
		String header = "";
		
		for (ProteinTableField f : ProteinTableField.getOrderedFieldList()) {
			// ignore quant fields and abundance fields
			if (f == ProteinTableField.PROTEIN_ABUNDANCE || f == ProteinTableField.PROTEIN_ABUNDANCE_STD ||
				f == ProteinTableField.PROTEIN_ABUNDANCE_STD_ERROR || f == ProteinTableField.CUSTOM)
				continue;
			
			if (f == ProteinTableField.ROW_PREFIX)
				f = ProteinTableField.HEADER_PREFIX;
			
			header += (header.length() == 0 ? "" : SEPARATOR) + f;
		}
		
		// add the quant fields
		for (int i = 1; i <= maxProteinSubsamples; i++) {
			header += (header.length() == 0 ? "" : SEPARATOR) + ProteinTableField.PROTEIN_ABUNDANCE + "[" + i + "]";
			header += SEPARATOR + ProteinTableField.PROTEIN_ABUNDANCE_STD + "[" + i + "]";
			header += SEPARATOR + ProteinTableField.PROTEIN_ABUNDANCE_STD_ERROR + "[" + i + "]";
		}
		
		// add the custom fields
		for (String custom : customHeader)
			header += (header.length() == 0 ? "" : SEPARATOR) + custom;
		
		header += EOL;
		
		return header;
	}
	
	/**
	 * Creates the peptide table header including the EOL.
	 * @param customHeader
	 * @return
	 */
	private String createPeptideHeader(List<String> customHeader) {
		String header = "";
		
		for (PeptideTableField f : PeptideTableField.getOrderedFieldList()) {
			// ignore quant fields and abundance fields
			if (f == PeptideTableField.PEPTIDE_ABUNDANCE || f == PeptideTableField.PEPTIDE_ABUNDANCE_STD ||
				f == PeptideTableField.PEPTIDE_ABUNDANCE_STD_ERROR || f == PeptideTableField.CUSTOM)
				continue;
			
			if (f == PeptideTableField.ROW_PREFIX)
				f = PeptideTableField.HEADER_PREFIX;
			
			header += (header.length() == 0 ? "" : SEPARATOR) + f;
		}
		
		// add the quant fields
		for (int i = 1; i <= maxPeptideSubsamples; i++) {
			header += (header.length() == 0 ? "" : SEPARATOR) + PeptideTableField.PEPTIDE_ABUNDANCE + "[" + i + "]";
			header += SEPARATOR + PeptideTableField.PEPTIDE_ABUNDANCE_STD + "[" + i + "]";
			header += SEPARATOR + PeptideTableField.PEPTIDE_ABUNDANCE_STD_ERROR + "[" + i + "]";
		}
		
		// add the custom fields
		for (String custom : customHeader)
			header += (header.length() == 0 ? "" : SEPARATOR) + custom;
		
		header += EOL;
		
		return header;
	}
	
	/**
	 * Creates the small molecules table header including the EOL.
	 * @param customHeader
	 * @return
	 */
	private String createSmallMoleculesHeader(List<String> customHeader) {
		String header = "";
		
		for (SmallMoleculeTableField f : SmallMoleculeTableField.getOrderedFieldList()) {
			// ignore quant fields and abundance fields
			if (f == SmallMoleculeTableField.ABUNDANCE || f == SmallMoleculeTableField.ABUNDANCE_STD ||
				f == SmallMoleculeTableField.ABUNDANCE_STD_ERROR || f == SmallMoleculeTableField.CUSTOM)
				continue;
			
			if (f == SmallMoleculeTableField.ROW_PREFIX)
				f = SmallMoleculeTableField.HEADER_PREFIX;
			
			header += (header.length() == 0 ? "" : SEPARATOR) + f;
		}
		
		// add the quant fields
		for (int i = 1; i <= maxPeptideSubsamples; i++) {
			header += (header.length() == 0 ? "" : SEPARATOR) + SmallMoleculeTableField.ABUNDANCE + "[" + i + "]";
			header += SEPARATOR + SmallMoleculeTableField.ABUNDANCE_STD + "[" + i + "]";
			header += SEPARATOR + SmallMoleculeTableField.ABUNDANCE_STD_ERROR + "[" + i + "]";
		}
		
		// add the custom fields
		for (String custom : customHeader)
			header += (header.length() == 0 ? "" : SEPARATOR) + custom;
		
		header += EOL;
		
		return header;
	}
	
	// TODO: add a function where the user can specify the various subsample numbers + custom columns
}
