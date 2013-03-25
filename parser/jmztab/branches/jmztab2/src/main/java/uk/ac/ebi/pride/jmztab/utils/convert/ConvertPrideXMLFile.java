package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.Utils;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseDouble;

/**
 * User: Qingwei
 * Date: 12/03/13
 */
public class ConvertPrideXMLFile extends ConvertFile {
    private PrideXmlReader reader;
    private Unit unit;

    public ConvertPrideXMLFile(File inFile) {
        super(inFile, Format.PRIDE);
        this.reader = new PrideXmlReader(inFile);
        createArchitecture();
        fillData();
    }

    /**
     * Generates a unit id for the current PRIDE XML file. If the file contains
     * an accession the accession is used, otherwise a temporary unit id is generated.
     * @return The Unit id to use.
     */
    private String generateUnitId() {
        // check whether the pride xml file has an accession
        if (! MZTabUtils.isEmpty(reader.getExpAccession()))
            return "PRIDE_" + reader.getExpAccession();
        else
            return "PRIDEFILE_" + System.currentTimeMillis();
    }

    @Override
    protected Metadata convertMetadata() {
        this.metadata = new Metadata();

        this.unit = new Unit(generateUnitId());
        // process the references
        loadReferences(unit, reader.getReferences());
        // process the contacts
        loadContacts(unit, reader.getAdmin().getContact());
        // process the experiment params
        loadExperimentParams(unit, reader.getAdditionalParams());
        // process the instrument information
        loadInstrument(unit, reader.getInstrument());
        // set the accession as URI if there's one
        loadURI(unit, reader.getExpAccession());
        // set Ms File
        loadMsFile(unit, reader.getExpAccession());

        metadata.addUnit(unit);

        loadSubSamples(reader.getAdmin().getSampleDescription());

        return metadata;
    }

    @Override
    protected MZTabColumnFactory convertProteinColumnFactory() {
        MZTabColumnFactory proteinColumnFactory = MZTabColumnFactory.getInstance(Section.Protein);

        SortedMap<Integer, SubUnit> subUnits = metadata.getSubUnits();
        for (SubUnit subUnit : subUnits.values()) {
            proteinColumnFactory.addAbundanceColumns(subUnit);
        }

        return proteinColumnFactory;
    }

    @Override
    protected MZTabColumnFactory convertPeptideColumnFactory() {
        MZTabColumnFactory peptideColumnFactory = MZTabColumnFactory.getInstance(Section.Peptide);

        SortedMap<Integer, SubUnit> subUnits = metadata.getSubUnits();
        for (SubUnit subUnit : subUnits.values()) {
            peptideColumnFactory.addAbundanceColumns(subUnit);
        }

        return peptideColumnFactory;
    }

    @Override
    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        // not exists small molecule table section.
        return null;
    }

    @Override
    protected void fillData() {
        // Get a list of Identification ids
        List<String> ids = reader.getIdentIds();

        // Iterate over each identification
        for(String id : ids) {
            Identification identification = reader.getIdentById(id);

            // ignore any decoy hits
            if (isDecoyHit(identification)) {
                continue;
            }

            Protein protein = loadProtein(identification);
            proteins.add(protein);

            // convert the identification's peptides
            List<Peptide> peptideList = loadPeptides(identification);
            peptides.addAll(peptideList);
        }
    }

    private enum SearchEngineParameter {
        MASCOT("MS", "MS:1001207", "Mascot"),
        OMSSA("MS", "MS:1001475", "OMSSA"),
        SEQUEST("MS", "MS:1001208", "Sequest"),
        SPECTRUMMILL( "MS", "MS:1000687", "Spectrum Mill for MassHunter Workstation"),
        SPECTRAST("MS", "MS:1001477", "SpectaST"),
        XTANDEM_1("MS", "MS:1001476", "X!Tandem"),
        XTANDEM_2("MS", "MS:1001476", "X!Tandem");

        private String cvLabel;
        private String accession;
        private String name;

        private SearchEngineParameter(String cvLabel, String accession, String name) {
            this.cvLabel = cvLabel;
            this.accession = accession;
            this.name = name;
        }
    }

    /**
     * Tries to guess which search engine is described by the passed name. In case no matching parameter
     * is found, null is returned.
     */
    private CVParam findSearchEngineParam(String searchEngineName) {
        if (searchEngineName == null) {
            return null;
        }

        searchEngineName = searchEngineName.toLowerCase();

        SearchEngineParameter param = null;
        if (searchEngineName.contains("mascot")) {
            param = SearchEngineParameter.MASCOT;
        } else if (searchEngineName.contains("omssa")) {
            param = SearchEngineParameter.OMSSA;
        } else if (searchEngineName.contains("sequest")) {
            param = SearchEngineParameter.SEQUEST;
        } else if (searchEngineName.contains("spectrummill")) {
            param = SearchEngineParameter.SPECTRUMMILL;
        } else if (searchEngineName.contains("spectrast")) {
            param = SearchEngineParameter.SPECTRAST;
        } else if (searchEngineName.contains("xtandem")) {
            param = SearchEngineParameter.XTANDEM_1;
        } else if (searchEngineName.contains("x!tandem")) {
            param = SearchEngineParameter.XTANDEM_2;
        }

        if (param == null) {
            return null;
        } else {
            return new CVParam(param.cvLabel, param.accession, param.name, null);
        }
    }

    private CVParam convertParam(uk.ac.ebi.pride.jaxb.model.CvParam param) {
        return new CVParam(param.getCvLabel(), param.getAccession(), param.getName(), param.getValue());
    }

    private uk.ac.ebi.pride.jaxb.model.CvParam getFirstCvParam(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return null;
        }

        if (param.getCvParam().iterator().hasNext()) {
            return param.getCvParam().iterator().next();
        }

        return null;
    }

    /**
     * Checks whether the passed identification object is a decoy hit. This function only checks for
     * the presence of specific cv / user Params.
     *
     * @param identification A PRIDE JAXB Identification object.
     * @return Boolean indicating whether the passed identification is a decoy hit.
     */
    private boolean isDecoyHit(Identification identification) {
        for (uk.ac.ebi.pride.jaxb.model.CvParam param : identification.getAdditional().getCvParam()) {
            if (param.getAccession().equals(DAOCvParams.DECOY_HIT.getAccession()))
                return true;
        }

        for (uk.ac.ebi.pride.jaxb.model.UserParam param : identification.getAdditional().getUserParam()) {
            if ("Decoy Hit".equals(param.getName()))
                return true;
        }

        return false;
    }

    private String getPublicationAccession(uk.ac.ebi.pride.jaxb.model.Param param, String name) {
        if (param == null || isEmpty(name)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (name.equals(p.getCvLabel())) {
                return p.getAccession();
            }
        }

        return null;
    }

    /**
     * Converts the experiment's references into the reference string (DOIs and PubMed ids)
     */
    private void loadReferences(Unit unit, List<Reference> references) {
        if (references == null || references.size() == 0) {
            return;
        }

        Publication publication;
        for (Reference ref : references) {


            uk.ac.ebi.pride.jaxb.model.Param param = ref.getAdditional();
            if (param == null) {
                continue;
            }

            publication = new Publication();

            // check if there's a DOI
            String doi = getPublicationAccession(param, DAOCvParams.REFERENCE_DOI.getName());
            if (! isEmpty(doi)) {
                publication.addPublication(Publication.Type.DOI, doi);
            }

            // check if there's a pubmed id
            String pubmed = getPublicationAccession(param, DAOCvParams.REFERENCE_PUBMED.getName());
            if (! isEmpty(pubmed)) {
                publication.addPublication(Publication.Type.PUBMED, pubmed);
            }

            unit.addPublication(publication);
        }
    }

    /**
     * Converts a list of PRIDE JAXB Contacts into an ArrayList of mzTab Contacts.
     */
    private void loadContacts(Unit unit, List<uk.ac.ebi.pride.jaxb.model.Contact> contactList)  {
        // make sure there are contacts to be processed
        if (contactList == null || contactList.size() == 0) {
            return;
        }

        // initialize the return variable
        int id = 1;
        for (uk.ac.ebi.pride.jaxb.model.Contact c : contactList) {
            unit.addContactName(id, c.getName());
            unit.addContactAffiliation(id, c.getInstitution());
            if (c.getContactInfo() != null && c.getContactInfo().contains("@")) {
                unit.addContactEmail(id, c.getContactInfo());
            }
            id++;
        }
    }

    /**
     * Processes the experiment additional params
     * (f.e. quant method, description...).
     */
    private void loadExperimentParams(Unit unit, uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return;
        }

        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession().equals(p.getAccession())) {
                unit.setDescription(p.getValue());
            } else if (QuantitationCvParams.isQuantificationMethod(p.getAccession())) {
                // check if it's a quantification method
                unit.setQuantificationMethod(convertParam(p));
            } else if (DAOCvParams.GEL_BASED_EXPERIMENT.getAccession().equals(p.getAccession())) {
                unit.addCustom(convertParam(p));
            }
        }
    }

    private void loadInstrument(Unit unit, uk.ac.ebi.pride.jaxb.model.Instrument instrument) {
        if (instrument == null) {
            return;
        }

        // handle the source information
        uk.ac.ebi.pride.jaxb.model.Param sourceParam = instrument.getSource();
        CvParam param = getFirstCvParam(sourceParam);
        if (param != null) {
            unit.addInstrumentSource(1, convertParam(param));
        }

        uk.ac.ebi.pride.jaxb.model.Param detectorParam = instrument.getDetector();
        param = getFirstCvParam(detectorParam);
        if (param != null) {
            unit.addInstrumentDetector(1, convertParam(param));
        }

        // handle the analyzer information
        if (instrument.getAnalyzerList().getCount() > 0) {
            uk.ac.ebi.pride.jaxb.model.Param analyzerParam = instrument.getAnalyzerList().getAnalyzer().iterator().next();
            param = getFirstCvParam(analyzerParam);
            if (param != null) {
                unit.addInstrumentAnalyzer(1, convertParam(param));
            }
        }

    }

    private void loadURI(Unit unit, String expAccession) {
        if (isEmpty(expAccession)) {
            return;
        }

        try {
            URI uri = new URI("http://www.ebi.ac.uk/pride/experiment.do?experimentAccessionNumber=" + expAccession);
            unit.addUri(uri);
        } catch (URISyntaxException e) {
            // do nothing
        }
    }

    private void loadMsFile(Unit unit, String expAccession) {
        if (!inFile.isFile()) {
            return;
        }

        unit.addMsFileFormat(1, new CVParam("MS", "MS:1000564", "PSI mzData file", null));
        unit.addMsFileIdFormat(1, new CVParam("MS", "MS:1000777", "spectrum identifier nativeID format", null));
        try {
            unit.addMsFileLocation(1, new URL("ftp://ftp.ebi.ac.uk/pub/databases/pride/PRIDE_Exp_Complete_Ac_" + expAccession + ".xml"));
        } catch (MalformedURLException e) {
            // do nothing
        }
    }

    /**
     * Adds the sample parameters (species, tissue, cell type, disease) to the unit and the various subsamples.
     */
    private void loadSubSamples(SampleDescription sampleDescription) {
        if (sampleDescription == null) {
            return;
        }

        SubUnit subUnit1 = null;
        SubUnit subUnit2 = null;
        SubUnit subUnit3 = null;
        SubUnit subUnit4 = null;
        SubUnit subUnit5 = null;
        SubUnit subUnit6 = null;
        SubUnit subUnit7 = null;
        SubUnit subUnit8 = null;

        SubUnit noIdSubUnit = null;
        String unitId = this.unit.getUnitId();
        for (CvParam p : sampleDescription.getCvParam()) {
            // check for subsample descriptions
            if (QuantitationCvParams.SUBSAMPLE1_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (subUnit1 == null) {
                    subUnit1 = new SubUnit(unitId, 1);
                    metadata.addUnit(subUnit1);
                }
                subUnit1.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE2_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (subUnit2 == null) {
                    subUnit2 = new SubUnit(unitId, 2);
                    metadata.addUnit(subUnit1);
                }
                subUnit2.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE3_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (subUnit3 == null) {
                    subUnit3 = new SubUnit(unitId, 3);
                    metadata.addUnit(subUnit3);
                }
                subUnit3.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE4_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (subUnit4 == null) {
                    subUnit4 = new SubUnit(unitId, 4);
                    metadata.addUnit(subUnit4);
                }
                subUnit4.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE5_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (subUnit5 == null) {
                    subUnit5 = new SubUnit(unitId, 5);
                    metadata.addUnit(subUnit5);
                }
                subUnit5.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE6_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (subUnit6 == null) {
                    subUnit6 = new SubUnit(unitId, 6);
                    metadata.addUnit(subUnit6);
                }
                subUnit6.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE7_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (subUnit7 == null) {
                    subUnit7 = new SubUnit(unitId, 7);
                    metadata.addUnit(subUnit7);
                }
                subUnit7.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE8_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (subUnit8 == null) {
                    subUnit8 = new SubUnit(unitId, 8);
                    metadata.addUnit(subUnit8);
                }
                subUnit8.setDescription(p.getValue());
                continue;
            }

            // check if it belongs to a subsample
            if (p.getValue() != null && p.getValue().startsWith("subsample")) {
                // get the subsample number
                Pattern subsampleNumberPattern = Pattern.compile("subsample(\\d+)");
                Matcher matcher = subsampleNumberPattern.matcher(p.getValue());

                if (matcher.find()) {
                    Integer subId = Integer.parseInt(matcher.group(1));

                    // remove the value
                    p.setValue(null);

                    SubUnit subUnit = metadata.getSubUnits().get(subId);
                    if (subUnit == null) {
                        subUnit = new SubUnit(unitId, subId);
                        metadata.addUnit(subUnit);
                    }

                    // add the param depending on the type
                    if ("NEWT".equals(p.getCvLabel())) {
                        subUnit.addSpecies(1, convertParam(p));
                    } else if ("BRENDA".equals(p.getCvLabel())) {
                        subUnit.addTissue(1, convertParam(p));
                    } else if ("CL".equals(p.getCvLabel())) {
                        subUnit.addCellType(1, convertParam(p));
                    } else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                        subUnit.addDisease(1, convertParam(p));
                    }
                    else if (QuantitationCvParams.isQuantificationReagent(p.getAccession())) {
                        // check if it's a quantification reagent
                        subUnit.setQuantificationReagent(convertParam(p));
                    }
                }
            } else {
                Unit u = metadata.getUnit(unitId + "-sub");
                if (u == null) {
                    noIdSubUnit = new SubUnit(unitId, null);
                } else {
                    noIdSubUnit = (SubUnit) u;
                }

                // add the param to the "global" group
                if ("NEWT".equals(p.getCvLabel())) {
                    noIdSubUnit.addSpecies(1, convertParam(p));
                }
                else if ("BTO".equals(p.getCvLabel())) {
                    noIdSubUnit.addTissue(1, convertParam(p));
                }
                else if ("CL".equals(p.getCvLabel())) {
                    noIdSubUnit.addCellType(1, convertParam(p));
                }
                else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                    //DOID: Human Disease Ontology
                    //IDO: Infectious Disease Ontology
                    noIdSubUnit.addDisease(1, convertParam(p));
                }
            }
        }

        // combine subUnits
        if (noIdSubUnit == null) {
            return;
        }

        SortedMap<Integer, SubUnit> subUnits = metadata.getSubUnits();
        if (subUnits.isEmpty()) {
            metadata.addUnit(noIdSubUnit);
        }
    }

    private String getCvParamValue(uk.ac.ebi.pride.jaxb.model.Param param, String accession) {
        if (param == null || isEmpty(accession)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (accession.equals(p.getAccession())) {
                return p.getValue();
            }
        }

        return null;
    }

    private List<String> getAmbiguityMembers(uk.ac.ebi.pride.jaxb.model.Param param, String accession) {
        if (param == null || isEmpty(accession)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        List<String> ambiguityMembers = new ArrayList<String>();
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (accession.equals(p.getAccession())) {
                ambiguityMembers.add(p.getValue());
            }
        }

        return ambiguityMembers;
    }

    private Species getSpecies(SubUnit subUnit) {
        SortedMap<Integer, Species> speciesMap = subUnit.getSpeciesMap();
        if (speciesMap.isEmpty()) {
            return null;
        }
        return speciesMap.get(speciesMap.firstKey());
    }

    private Collection<Modification> getProteinModifications(List<PeptideItem> items) {
        HashMap<String, Modification> modifications = new HashMap<String, Modification>();

        Modification mod;
        for (PeptideItem item : items) {
            for (ModificationItem ptm : item.getModificationItem()) {
                // ignore modifications that can't be processed correctly
                if (item.getStart() == null || ptm.getModAccession() == null || ptm.getModLocation() == null) {
                    continue;
                }
                mod = modifications.get(ptm.getModAccession());
                if (mod == null) {
                    mod = MZTabUtils.parseModification(Section.Protein, ptm.getModAccession());
                    modifications.put(ptm.getModAccession(), mod);
                }

                Integer position = item.getStart().intValue() + ptm.getModLocation().intValue() - 1;
                mod.addPosition(position, null);
            }
        }

        return modifications.values();
    }

    /**
     * Adds the quantitative values for the given protein.
     */
    private void addAbundanceValues(AbstractMZTabRecord record, MZTabColumnFactory columnFactory, Identification identification) {
        SortedMap<Integer, AbundanceColumn> abundanceColumns = columnFactory.getAbundanceColumnMapping();
        if (abundanceColumns.isEmpty()) {
            return;
        }

        // every three abundance columns is a group.
        int abundancePosition;
        String abundance = null;
        int stdevPosition;
        String stdev = null;
        int stdErrorPosition;
        String stdError = null;
        SubUnit subUnit;
        for (Iterator<Integer> it = abundanceColumns.keySet().iterator(); it.hasNext();) {
            abundancePosition = it.next();
            stdevPosition = it.next();
            stdErrorPosition = it.next();
            subUnit = abundanceColumns.get(abundancePosition).getSubUnit();

            switch (subUnit.getSubId()) {
                case 1:
                    abundance = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE1.getAccession());
                    stdev = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE1.getAccession());
                    stdError = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE1.getAccession());
                    break;
                case 2:
                    abundance = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE2.getAccession());
                    stdev = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE2.getAccession());
                    stdError = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE2.getAccession());
                    break;
                case 3:
                    abundance = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE3.getAccession());
                    stdev = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE3.getAccession());
                    stdError = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE3.getAccession());
                    break;
                case 4:
                    abundance = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE4.getAccession());
                    stdev = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE4.getAccession());
                    stdError = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE4.getAccession());
                    break;
                case 5:
                    abundance = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE5.getAccession());
                    stdev = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE5.getAccession());
                    stdError = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE5.getAccession());
                    break;
                case 6:
                    abundance = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE6.getAccession());
                    stdev = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE6.getAccession());
                    stdError = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE6.getAccession());
                    break;
                case 7:
                    abundance = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE7.getAccession());
                    stdev = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE7.getAccession());
                    stdError = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE7.getAccession());
                    break;
                case 8:
                    abundance = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_SUBSAMPLE8.getAccession());
                    stdev = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_SUBSAMPLE8.getAccession());
                    stdError = getCvParamValue(identification.getAdditional(), QuantitationCvParams.INTENSITY_STD_ERR_SUBSAMPLE8.getAccession());
                    break;
            }
            record.addValue(abundancePosition, parseDouble(abundance));
            record.addValue(stdevPosition, parseDouble(stdev));
            record.addValue(stdErrorPosition, parseDouble(stdError));
        }
    }

    private void addOptionalColumnValue(AbstractMZTabRecord record, MZTabColumnFactory columnFactory, String name, String value) {
        Integer position = columnFactory.containOptionalColumn(name);
        if (position == null) {
            position = columnFactory.addOptionalColumn(name, String.class);
        }
        record.addValue(position, value);
    }

    /**
     * Converts the passed Identification object into an MzTab protein.
     */
    private Protein loadProtein(Identification identification) {
        // create the protein object
        Protein protein = new Protein(proteinColumnFactory);

        protein.setAccession(identification.getAccession());
        protein.setUnitId(unit.getUnitId());
        protein.setDatabase(identification.getDatabase());
        protein.setDatabaseVersion(identification.getDatabaseVersion());

        // set the search engine
        uk.ac.ebi.pride.jmztab.model.Param searchEngineParam = findSearchEngineParam(identification.getSearchEngine());
        if (searchEngineParam != null) {
            protein.addSearchEngineParam(searchEngineParam);
        }
        // setting the score is not sensible

        // set the description if available
        String description = getCvParamValue(identification.getAdditional(), DAOCvParams.PROTEIN_NAME.getAccession());
        protein.setDescription(description);

        // set the species if possible
        Species species;
        Unit u = metadata.getUnit(this.unit.getUnitId() + "-sub");
        if (u != null) {
            // exists global sub-sample
            SubUnit subUnit = (SubUnit) u;
            species = getSpecies(subUnit);
            if (species != null) {
                protein.setSpecies(species.getParam().getName());
                protein.setTaxid(species.getParam().getAccession());
            }
        } else {
            // exists subId sub-samples
            SortedMap<Integer, SubUnit> subUnits = metadata.getSubUnits();
            for (SubUnit subUnit : subUnits.values()) {
                species = getSpecies(subUnit);
                if (species != null) {
                    protein.setSpecies(species.getParam().getName());
                    protein.setTaxid(species.getParam().getAccession());
                    break;
                }
            }
        }

        // get the number of peptides
        List<PeptideItem> items = identification.getPeptideItem();
        protein.setNumPeptides(items.size());
        HashSet<String> peptideSequences = new HashSet<String>();
        for (PeptideItem item : items) {
            List<ModificationItem> modList = item.getModificationItem();
            StringBuilder sb = new StringBuilder();
            for (ModificationItem mod : modList) {
                sb.append(mod.getModAccession()).append(mod.getModLocation());
            }
            sb.append(item.getSequence());
            peptideSequences.add(sb.toString());
        }
        // sequence + modifications
        protein.setNumPeptideDistinct(peptideSequences.size());

        // add the indistinguishable accessions to the ambiguity members
        List<String> ambiguityMembers = getAmbiguityMembers(identification.getAdditional(), DAOCvParams.INDISTINGUISHABLE_ACCESSION.getAccession());
        for (String member : ambiguityMembers) {
            protein.addAmbiguityMembers(member);
        }

        // set the modifications
        for (Modification modification : getProteinModifications(items)) {
            protein.addModification(modification);
        }

        // add potential quantitative values
        addAbundanceValues(protein, proteinColumnFactory, identification);

        // process the additional params
        for (CvParam p : identification.getAdditional().getCvParam()) {
            // check if there's a quant unit set
            if (QuantitationCvParams.UNIT_RATIO.getAccession().equals(p.getAccession()) || QuantitationCvParams.UNIT_COPIES_PER_CELL.getAccession().equals(p.getAccession())) {
                CVParam param = convertParam(p);
                if (this.unit != null && param != null) {
                    this.unit.setProteinQuantificationUnit(param);
                }
            } else {
                // check optional column.
                if (QuantitationCvParams.EMPAI_VALUE.getAccession().equals(p.getAccession())) {
                    addOptionalColumnValue(protein, proteinColumnFactory, "empai", p.getValue());
                } else if (DAOCvParams.GEL_SPOT_IDENTIFIER.getAccession().equals(p.getAccession())) {
                    // check if there's gel spot identifier
                    addOptionalColumnValue(protein, proteinColumnFactory, "gel_spotidentifier", p.getValue());
                } else if (DAOCvParams.GEL_IDENTIFIER.getAccession().equals(p.getAccession())) {
                    // check if there's gel identifier
                    addOptionalColumnValue(protein, proteinColumnFactory, "gel_identifier", p.getValue());
                }
            }
        }

        return protein;
    }

    /**
     * Extracts all search engine score related parameters from a peptide object and returns them in a list of
     * mzTab Params.
     */
    private void loadPeptideSearchEngineScores(Peptide peptide, PeptideItem item) {
        if (item.getAdditional() != null) {
            for (CvParam param : item.getAdditional().getCvParam()) {
                if (Utils.PEPTIDE_SCORE_PARAM.isScoreAccession(param.getAccession())) {
                    peptide.addSearchEngineSocreParam(convertParam(param));
                }
            }
        }
    }

    private Collection<Modification> getPeptideModifications(PeptideItem item) {
        HashMap<String, Modification> modifications = new HashMap<String, Modification>();

        Modification mod;
        for (ModificationItem ptm : item.getModificationItem()) {
            // ignore modifications that can't be processed correctly
            if (item.getStart() == null || ptm.getModAccession() == null || ptm.getModLocation() == null) {
                continue;
            }
            mod = modifications.get(ptm.getModAccession());
            if (mod == null) {
                mod = MZTabUtils.parseModification(Section.Protein, ptm.getModAccession());
                modifications.put(ptm.getModAccession(), mod);
            }

            Integer position = item.getStart().intValue() + ptm.getModLocation().intValue() - 1;
            mod.addPosition(position, null);
        }

        return modifications.values();
    }

    /**
     * Converts and Identification's peptides into a List of mzTab Peptides.
     */
    private List<Peptide> loadPeptides(Identification identification) {
        List<Peptide> peptideList = new ArrayList<Peptide>();

        for (PeptideItem peptideItem : identification.getPeptideItem()) {
            // create the peptide object
            Peptide peptide = new Peptide(peptideColumnFactory);

            peptide.setSequence(peptideItem.getSequence());
            peptide.setAccession(identification.getAccession());
            peptide.setUnitId(unit.getUnitId());
            peptide.setDatabase(identification.getDatabase());
            peptide.setDatabaseVersion(identification.getDatabaseVersion());

            // set the peptide spectrum reference
            String spectrumReference = peptideItem.getSpectrum() == null ? "null" : Integer.toString(peptideItem.getSpectrum().getId());
            MsFile msFile = unit.getMsFileMap().get(1);
            peptide.addSpectraRef(new SpecRef(msFile, "spectrum=" + spectrumReference));

            // set the search engine - is possible
            uk.ac.ebi.pride.jmztab.model.Param searchEngineParam = findSearchEngineParam(identification.getSearchEngine());
            if (searchEngineParam != null) {
                peptide.addSearchEngineParam(searchEngineParam);
            }

            // set the search engine scores
            loadPeptideSearchEngineScores(peptide, peptideItem);

            // set the modifications
            for (Modification modification : getPeptideModifications(peptideItem)) {
                peptide.addModification(modification);
            }

            // process the quant values
            addAbundanceValues(peptide, peptideColumnFactory, identification);

            // process the additional params -- mainly check for quantity units
            if (peptideItem.getAdditional() != null) {
                for (CvParam p : peptideItem.getAdditional().getCvParam()) {
                    // check if there's a quant unit set
                    if (QuantitationCvParams.UNIT_RATIO.getAccession().equals(p.getAccession()) || QuantitationCvParams.UNIT_COPIES_PER_CELL.getAccession().equals(p.getAccession())) {
                        CVParam param = convertParam(p);
                        if (param != null) {
                            unit.setPeptideQuantificationUnit(param);
                        }
                    }
                }
            }

            peptideList.add(peptide);
        }

        return peptideList;
    }
}
