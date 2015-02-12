package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * The small molecule section is table-based. The small molecule section MUST always come after the metadata section,
 * peptide section and or protein section if they are present in the file. All table columns MUST be Tab separated.
 * There MUST NOT be any empty cells. Missing values MUST be reported using "null". Most columns are mandatory.
 * The order of columns is not specified although for ease of human interpretation, it is RECOMMENDED to follow the
 * order specified below.
 *
 * @author qingwei
 * @since 23/05/13
 */
public class SmallMolecule extends MZTabRecord {
    private Metadata metadata;

    /**
     * Create a small molecule record based on structure defined by {@link MZTabColumnFactory}
     *
     * @param factory SHOULD NOT set null.
     * @param metadata SHOULD NOT set null.
     */
    public SmallMolecule(MZTabColumnFactory factory, Metadata metadata) {
        super(factory);
        this.metadata = metadata;
    }

    /**
     * A list of "|" separated possible identifiers for these small molecules. The database identifier
     * must be preceded by the resource description followed by a colon (in case this is not already part
     * of the identifier format).
     */
    public SplitList<String> getIdentifier() {
        return getSplitList(SmallMoleculeColumn.IDENTIFIER.getLogicPosition());
    }

    /**
     * A list of "|" separated possible identifiers for these small molecules. The database identifier
     * must be preceded by the resource description followed by a colon (in case this is not already part
     * of the identifier format).
     */
    public boolean addIdentifier(String identifier) {
        if (isEmpty(identifier)) {
            return false;
        }

        SplitList<String> identifierList = getIdentifier();
        if (identifierList == null) {
            identifierList = new SplitList<String>(BAR);
            setIdentifier(identifierList);
        }
        return identifierList.add(identifier);
    }

    /**
     * A list of "|" separated possible identifiers for these small molecules. The database identifier
     * must be preceded by the resource description followed by a colon (in case this is not already part
     * of the identifier format).
     */
    public void setIdentifier(SplitList<String> identifier) {
        setValue(SmallMoleculeColumn.IDENTIFIER.getLogicPosition(), identifier);
    }

    /**
     * A list of "|" separated possible identifiers for these small molecules. The database identifier
     * must be preceded by the resource description followed by a colon (in case this is not already part
     * of the identifier format).
     *
     * @param identifierLabel parsed by {@link MZTabUtils#parseStringList(char, String)}
     */
    public void setIdentifier(String identifierLabel) {
        setIdentifier(parseStringList(BAR, identifierLabel));
    }

    /**
     * The chemical formula of the identified compound. This should be specified in Hill notation (EA Hill 1900),
     * i.e. elements in the order C, H and then alphabetically all other elements. Counts of one may be omitted.
     * Elements should be capitalized properly to avoid confusion (e.g., "CO" vs. "Co"). The chemical formula
     * reported should refer to the neutral form. Charge state is reported by the charge field. This permits the
     * comparison of positive and negative mode results.
     */
    public String getChemicalFormula() {
        return getString(SmallMoleculeColumn.CHEMICAL_FORMULA.getLogicPosition());
    }

    /**
     * The chemical formula of the identified compound. This should be specified in Hill notation (EA Hill 1900),
     * i.e. elements in the order C, H and then alphabetically all other elements. Counts of one may be omitted.
     * Elements should be capitalized properly to avoid confusion (e.g., "CO" vs. "Co"). The chemical formula
     * reported should refer to the neutral form. Charge state is reported by the charge field. This permits the
     * comparison of positive and negative mode results.
     */
    public void setChemicalFormula(String chemicalFormula) {
        setValue(SmallMoleculeColumn.CHEMICAL_FORMULA.getLogicPosition(), parseString(chemicalFormula));
    }

    /**
     * The molecules structure in the simplified molecular-input line-entry system (SMILES). If there are more than
     * one SMILES for a given small molecule, use the  "|" separator.
     */
    public SplitList<String> getSmiles() {
        return getSplitList(SmallMoleculeColumn.SMILES.getLogicPosition());
    }

    /**
     * The molecules structure in the simplified molecular-input line-entry system (SMILES). If there are more than
     * one SMILES for a given small molecule, use the  "|" separator.
     */
    public boolean addSmile(String smile) {
        if (smile == null) {
            return false;
        }

        SplitList<String> smiles = getSmiles();
        if (smiles == null) {
            smiles = new SplitList<String>(BAR);
            setSmiles(smiles);
        }

        return smiles.add(smile);
    }

    /**
     * The molecules structure in the simplified molecular-input line-entry system (SMILES). If there are more than
     * one SMILES for a given small molecule, use the  "|" separator.
     */
    public void setSmiles(String smilesLabel) {
        setSmiles(parseStringList(BAR, smilesLabel));
    }

    /**
     * The molecules structure in the simplified molecular-input line-entry system (SMILES). If there are more than
     * one SMILES for a given small molecule, use the  "|" separator.
     */
    public void setSmiles(SplitList<String> smiles) {
        setValue(SmallMoleculeColumn.SMILES.getLogicPosition(), smiles);
    }
    /**
     * The standard IUPAC International Chemical Identifier (InChI) Key of the given substance. If there are more than
     * one InChI identifier for a given small molecule, use the  "|" separator.
     */
    public SplitList<String> getInchiKey() {
        return getSplitList(SmallMoleculeColumn.INCHI_KEY.getLogicPosition());
    }

    /**
     * The standard IUPAC International Chemical Identifier (InChI) Key of the given substance. If there are more than
     * one InChI identifier for a given small molecule, use the  "|" separator.
     */
    public boolean addInchiKey(String inchiKey) {
        if (inchiKey == null) {
            return false;
        }

        SplitList<String> inchiKeys = getInchiKey();
        if (inchiKeys == null) {
            inchiKeys = new SplitList<String>(BAR);
            setInchiKey(inchiKeys);
        }

        return inchiKeys.add(inchiKey);
    }
    /**
     * The standard IUPAC International Chemical Identifier (InChI) Key of the given substance. If there are more than
     * one InChI identifier for a given small molecule, use the  "|" separator.
     */
    public void setInchiKey(String inchiKeyLabel) {
        setValue(SmallMoleculeColumn.INCHI_KEY.getLogicPosition(), parseStringList(BAR, inchiKeyLabel));
    }

    /**
     * The standard IUPAC International Chemical Identifier (InChI) Key of the given substance. If there are more than
     * one InChI identifier for a given small molecule, use the  "|" separator.
     */
    public void setInchiKey(SplitList<String> inchiKey) {
        setValue(SmallMoleculeColumn.INCHI_KEY.getLogicPosition(), inchiKey);
    }

    /**
     * The small molecule's description / name.
     */
    public String getDescription() {
        return getString(SmallMoleculeColumn.DESCRIPTION.getLogicPosition());
    }

    /**
     * The small molecule's description / name.
     */
    public void setDescription(String description) {
        setValue(SmallMoleculeColumn.DESCRIPTION.getLogicPosition(), parseString(description));
    }

    /**
     * The small molecule's experimental mass to charge (m/z).
     */
    public Double getExpMassToCharge() {
        return getDouble(SmallMoleculeColumn.EXP_MASS_TO_CHARGE.getLogicPosition());
    }

    /**
     * The small molecule's experimental mass to charge (m/z).
     */
    public void setExpMassToCharge(Double expMassToCharge) {
        setValue(SmallMoleculeColumn.EXP_MASS_TO_CHARGE.getLogicPosition(), expMassToCharge);
    }

    /**
     * The small molecule's experimental mass to charge (m/z).
     *
     * @param expMassToChargeLabel parsed by {@link MZTabUtils#parseDouble(String)}
     */
    public void setExpMassToCharge(String expMassToChargeLabel) {
        setExpMassToCharge(parseDouble(expMassToChargeLabel));
    }

    /**
     * The small molecule's precursor's calculated (theoretical) mass to charge ratio.
     */
    public Double getCalcMassToCharge() {
        return getDouble(SmallMoleculeColumn.CALC_MASS_TO_CHARGE.getLogicPosition());
    }

    /**
     * The small molecule's precursor's calculated (theoretical) mass to charge ratio.
     */
    public void setCalcMassToCharge(Double calcMassToCharge) {
        setValue(SmallMoleculeColumn.CALC_MASS_TO_CHARGE.getLogicPosition(), calcMassToCharge);
    }

    /**
     * The small molecule's precursor's calculated (theoretical) mass to charge ratio.
     *
     * @param calcMassToChargeLabel parsed by {@link MZTabUtils#parseDouble(String)}
     */
    public void setCalcMassToCharge(String calcMassToChargeLabel) {
        setCalcMassToCharge(parseDouble(calcMassToChargeLabel));
    }

    /**
     * The charge assigned by the search engine/software.
     */
    public Integer getCharge() {
        return getInteger(SmallMoleculeColumn.CHARGE.getLogicPosition());
    }

    /**
     * The charge assigned by the search engine/software.
     */
    public void setCharge(Integer charge) {
        setValue(SmallMoleculeColumn.CHARGE.getLogicPosition(), charge);
    }

    /**
     * The charge assigned by the search engine/software.
     *
     * @param chargeLabel parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setCharge(String chargeLabel) {
        setCharge(parseInteger(chargeLabel));
    }

    /**
     * A '|'-separated list of time points. Semantics may vary. This time should refer to the small molecule's
     * retention time if determined or the mid point between the first and last spectrum identifying the small molecule.
     * It MUST be reported in seconds. Otherwise, the corresponding unit MUST be specified in the Metadata Section
     * ('columnit_smallmolecule').
     */
    public SplitList<Double> getRetentionTime() {
        return getSplitList(SmallMoleculeColumn.RETENTION_TIME.getLogicPosition());
    }

    /**
     * A '|'-separated list of time points. Semantics may vary. This time should refer to the small molecule's
     * retention time if determined or the mid point between the first and last spectrum identifying the small molecule.
     * It MUST be reported in seconds. Otherwise, the corresponding unit MUST be specified in the Metadata Section
     * ('columnit_smallmolecule').
     */
    public boolean addRetentionTime(Double rt) {
        if (rt == null) {
            return false;
        }

        SplitList<Double> rtList = getRetentionTime();
        if (rtList == null) {
            rtList = new SplitList<Double>(BAR);
            setRetentionTime(rtList);
        }

        return rtList.add(rt);
    }

    /**
     * A '|'-separated list of time points. Semantics may vary. This time should refer to the small molecule's
     * retention time if determined or the mid point between the first and last spectrum identifying the small molecule.
     * It MUST be reported in seconds. Otherwise, the corresponding unit MUST be specified in the Metadata Section
     * ('columnit_smallmolecule').
     *
     * @param rtLabel parsed by {@link MZTabUtils#parseDouble(String)}
     */
    public boolean addRetentionTime(String rtLabel) {
        return !isEmpty(rtLabel) && addRetentionTime(parseDouble(rtLabel));
    }

    /**
     * A '|'-separated list of time points. Semantics may vary. This time should refer to the small molecule's
     * retention time if determined or the mid point between the first and last spectrum identifying the small molecule.
     * It MUST be reported in seconds. Otherwise, the corresponding unit MUST be specified in the Metadata Section
     * ('columnit_smallmolecule').
     */
    public void setRetentionTime(SplitList<Double> retentionTime) {
        setValue(SmallMoleculeColumn.RETENTION_TIME.getLogicPosition(), retentionTime);
    }

    /**
     * A '|'-separated list of time points. Semantics may vary. This time should refer to the small molecule's
     * retention time if determined or the mid point between the first and last spectrum identifying the small molecule.
     * It MUST be reported in seconds. Otherwise, the corresponding unit MUST be specified in the Metadata Section
     * ('columnit_smallmolecule').
     *
     * @param retentionTimeLabel parsed by {@link MZTabUtils#parseDoubleList(String)}
     */
    public void setRetentionTime(String retentionTimeLabel) {
        setRetentionTime(parseDoubleList(retentionTimeLabel));
    }

    /**
     * The taxonomy id coming from the NEWT taxonomy for the species (if applicable).
     */
    public Integer getTaxid() {
        return getInteger(SmallMoleculeColumn.TAXID.getLogicPosition());
    }

    /**
     * The taxonomy id coming from the NEWT taxonomy for the species (if applicable).
     */
    public void setTaxid(Integer taxid) {
        setValue(SmallMoleculeColumn.TAXID.getLogicPosition(), taxid);
    }

    /**
     * The taxonomy id coming from the NEWT taxonomy for the species (if applicable).
     *
     * @param taxidLabel parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setTaxid(String taxidLabel) {
        setTaxid(parseInteger(taxidLabel));
    }

    /**
     * The species as a human readable string (if applicable).
     */
    public String getSpecies() {
        return getString(SmallMoleculeColumn.SPECIES.getLogicPosition());
    }

    /**
     * The species as a human readable string (if applicable).
     */
    public void setSpecies(String species) {
        setValue(SmallMoleculeColumn.SPECIES.getLogicPosition(), parseString(species));
    }

    /**
     * Generally references the used spectral library (if applicable).
     */
    public String getDatabase() {
        return getString(SmallMoleculeColumn.DATABASE.getLogicPosition());
    }

    /**
     * Generally references the used spectral library (if applicable).
     */
    public void setDatabase(String database) {
        setValue(SmallMoleculeColumn.DATABASE.getLogicPosition(), parseString(database));
    }

    /**
     * Either the version of the used database if available or otherwise the date of creation.
     * Additionally, the number of entries in the database MAY be reported in round brackets after
     * the version in the format: {version} ({#entries} entries), for example "2011-11 (1234 entries)".
     */
    public String getDatabaseVersion() {
        return getString(SmallMoleculeColumn.DATABASE_VERSION.getLogicPosition());
    }

    /**
     * Either the version of the used database if available or otherwise the date of creation.
     * Additionally, the number of entries in the database MAY be reported in round brackets after
     * the version in the format: {version} ({#entries} entries), for example "2011-11 (1234 entries)".
     */
    public void setDatabaseVersion(String databaseVersion) {
        setValue(SmallMoleculeColumn.DATABASE_VERSION.getLogicPosition(), parseString(databaseVersion));
    }

    /**
     * The reliability of the given small molecule identification. This must be supplied by the resource
     * and has to be one of the following values:
     * <ol>
     *     <li>high reliability</li>
     *     <li>medium reliability</li>
     *     <li>poor reliability</li>
     * </ol>
     */
    public Reliability getReliability() {
        return getReliability(SmallMoleculeColumn.RELIABILITY.getLogicPosition());
    }

    /**
     * The reliability of the given small molecule identification. This must be supplied by the resource
     * and has to be one of the following values:
     * <ol>
     *     <li>high reliability</li>
     *     <li>medium reliability</li>
     *     <li>poor reliability</li>
     * </ol>
     *
     * @see Reliability
     */
    public void setReliability(Reliability reliability) {
        setValue(SmallMoleculeColumn.RELIABILITY.getLogicPosition(), reliability);
    }

    /**
     * The reliability of the given small molecule identification. This must be supplied by the resource
     * and has to be one of the following values:
     * <ol>
     *     <li>high reliability</li>
     *     <li>medium reliability</li>
     *     <li>poor reliability</li>
     * </ol>
     *
     * @param reliabilityLabel parsed by {@link Reliability#findReliability(String)}
     *
     * @see Reliability
     */
    public void setReliability(String reliabilityLabel) {
        setReliability(Reliability.findReliability(reliabilityLabel));
    }

    /**
     * A URI pointing to the small molecule's entry in the experiment it was identified in (e.g.,
     * the small molecule's PRIDE entry).
     */
    public URI getURI() {
        return getURI(SmallMoleculeColumn.URI.getLogicPosition());
    }

    /**
     * A URI pointing to the small molecule's entry in the experiment it was identified in (e.g.,
     * the small molecule's PRIDE entry).
     */
    public void setURI(URI uri) {
        setValue(SmallMoleculeColumn.URI.getLogicPosition(), uri);
    }

    /**
     * A URI pointing to the small molecule's entry in the experiment it was identified in (e.g.,
     * the small molecule's PRIDE entry).
     *
     * @param uriLabel parsed by {@link MZTabUtils#parseURI(String)}
     */
    public void setURI(String uriLabel) {
        setURI(parseURI(uriLabel));
    }

    /**
     * Reference to spectra in a spectrum file. It is expected that spectra_ref SHOULD only be used for MS2-based
     * quantification approaches, in which retention time values cannot identify the spectra used for quantification.
     * The reference must be in the format ms_run[1-n]:{SPECTRA_REF} where SPECTRA_REF MUST follow the format defined in 5.2.
     * Multiple spectra MUST be referenced using a "|" delimited list.
     */
    public SplitList<SpectraRef> getSpectraRef() {
        return getSplitList(SmallMoleculeColumn.SPECTRA_REF.getLogicPosition());
    }

    /**
     * Reference to spectra in a spectrum file. It is expected that spectra_ref SHOULD only be used for MS2-based
     * quantification approaches, in which retention time values cannot identify the spectra used for quantification.
     * The reference must be in the format ms_run[1-n]:{SPECTRA_REF} where SPECTRA_REF MUST follow the format defined in 5.2.
     * Multiple spectra MUST be referenced using a "|" delimited list.
     *
     * @see SpectraRef
     */
    public boolean addSpectraRef(SpectraRef specRef) {
        if (specRef == null) {
            return false;
        }

        SplitList<SpectraRef> specRefs = getSpectraRef();
        if (specRefs == null) {
            specRefs = new SplitList<SpectraRef>(BAR);
            setSpectraRef(specRefs);
        }

        return specRefs.add(specRef);
    }

    /**
     * Reference to spectra in a spectrum file. It is expected that spectra_ref SHOULD only be used for MS2-based
     * quantification approaches, in which retention time values cannot identify the spectra used for quantification.
     * The reference must be in the format ms_run[1-n]:{SPECTRA_REF} where SPECTRA_REF MUST follow the format defined in 5.2.
     * Multiple spectra MUST be referenced using a "|" delimited list.
     *
     * @see SpectraRef
     */
    public void setSpectraRef(SplitList<SpectraRef> spectraRef) {
        setValue(SmallMoleculeColumn.SPECTRA_REF.getLogicPosition(), spectraRef);
    }

    /**
     * Reference to spectra in a spectrum file. It is expected that spectra_ref SHOULD only be used for MS2-based
     * quantification approaches, in which retention time values cannot identify the spectra used for quantification.
     * The reference must be in the format ms_run[1-n]:{SPECTRA_REF} where SPECTRA_REF MUST follow the format defined in 5.2.
     * Multiple spectra MUST be referenced using a "|" delimited list.
     *
     * @param spectraRefLabel parsed by {@link MZTabUtils#parseSpectraRefList(Metadata, String)}
     *
     * @see SpectraRef
     */
    public void setSpectraRef(String spectraRefLabel) {
        setSpectraRef(parseSpectraRefList(metadata, spectraRefLabel));
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this small molecule. Search engines must be supplied
     * as parameters.
     */
    public SplitList<Param> getSearchEngine() {
        return getSplitList(SmallMoleculeColumn.SEARCH_ENGINE.getLogicPosition());
    }

    /**
     * Add a search engine used to identify this small molecule. Search engines must be supplied
     * as parameters.
     */
    public boolean addSearchEngineParam(Param param) {
        if (param == null) {
            return false;
        }

        SplitList<Param> params = getSearchEngine();
        if (params == null) {
            params = new SplitList<Param>(BAR);
            setSearchEngine(params);
        }

        return params.add(param);
    }

    /**
     * Add a search engine used to identify this small molecule. Search engines must be supplied
     * as parameters.
     *
     * @param paramLabel parsed by {@link MZTabUtils#parseParam(String)}
     */
    public boolean addSearchEngineParam(String paramLabel) {
        return !isEmpty(paramLabel) && addSearchEngineParam(parseParam(paramLabel));
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this small molecule. Search engines must be supplied
     * as parameters.
     */
    public void setSearchEngine(SplitList<Param> searchEngine) {
        setValue(SmallMoleculeColumn.SEARCH_ENGINE.getLogicPosition(), searchEngine);
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this small molecule. Search engines must be supplied
     * as parameters.
     *
     * @param searchEngineLabel parsed by {@link MZTabUtils#parseParamList(String)}
     */
    public void setSearchEngine(String searchEngineLabel) {
        setSearchEngine(parseParamList(searchEngineLabel));
    }

    /**
     * The best search engine score (for this type of score) for the given protein across
     * all replicates reported. The type of score MUST be defined in the metadata section.
     * If the protein was not identified by the specified search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     */
    public Double getBestSearchEngineScore(Integer id) {
        return getDouble(getLogicalPosition(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE, id, null));
    }


    /**
     * The best search engine score (for this type of score) for the given protein across
     * all replicates reported. The type of score MUST be defined in the metadata section.
     * If the protein was not identified by the specified search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     */
    public void setBestSearchEngineScore(Integer id, Double bestSearchEngineScore) {
        setValue(getLogicalPosition(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE, id, null), bestSearchEngineScore);
    }

    /**
     * The best search engine score (for this type of score) for the given protein across
     * all replicates reported. The type of score MUST be defined in the metadata section.
     * If the protein was not identified by the specified search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     */
    public void setBestSearchEngineScore(Integer id, String searchEngineScoreLabel) {
        setBestSearchEngineScore(id, parseDouble(searchEngineScoreLabel));
    }


    /**
     * The search engine score for the given protein in the defined ms run. The type of score
     * MUST be defined in the metadata section. If the protein was not identified by the specified
     * search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     * @param msRun SHOULD NOT set null
     * @return search engine score
     */
    public Double getSearchEngineScore(Integer id, MsRun msRun) {
        return getDouble(getLogicalPosition(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, id, msRun));
    }

    /**
     * The search engine score for the given protein in the defined ms run. The type of score
     * MUST be defined in the metadata section. If the protein was not identified by the specified
     * search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     * @param msRun SHOULD NOT set null
     */
    public void setSearchEngineScore(Integer id, MsRun msRun, Double searchEngineScore) {
        setValue(getLogicalPosition(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, id, msRun), searchEngineScore);
    }

    /**
     * The search engine score for the given protein in the defined ms run. The type of score
     * MUST be defined in the metadata section. If the protein was not identified by the specified
     * search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     * @param msRun SHOULD NOT set null
     */
    public void setSearchEngineScore(Integer id, MsRun msRun, String paramsLabel) {
        setSearchEngineScore(id, msRun, parseDouble(paramsLabel));
    }

    /**
     * The small molecule's modifications or adducts. The position of the modification must be given relative to
     * the small molecule's beginning. The exact semantics of this position depends on the type of small molecule identified.
     * In case the position information is unknown or not applicable it should not be supplied. For detailed information see
     * protein table.
     */
    public SplitList<Modification> getModifications() {
        return getSplitList(SmallMoleculeColumn.MODIFICATIONS.getLogicPosition());
    }

    /**
     * The small molecule's modifications or adducts. The position of the modification must be given relative to
     * the small molecule's beginning. The exact semantics of this position depends on the type of small molecule identified.
     * In case the position information is unknown or not applicable it should not be supplied. For detailed information see
     * protein table.
     */
    public boolean addModification(Modification modification) {
        if (modification == null) {
            return false;
        }

        SplitList<Modification> modList = getModifications();
        if (modList == null) {
            modList = new SplitList<Modification>(COMMA);
            setModifications(modList);
        }

        return modList.add(modification);
    }

    /**
     * The small molecule's modifications or adducts. The position of the modification must be given relative to
     * the small molecule's beginning. The exact semantics of this position depends on the type of small molecule identified.
     * In case the position information is unknown or not applicable it should not be supplied. For detailed information see
     * protein table.
     */
    public void setModifications(SplitList<Modification> modifications) {
        setValue(SmallMoleculeColumn.MODIFICATIONS.getLogicPosition(), modifications);
    }

    /**
     * The small molecule's modifications or adducts. The position of the modification must be given relative to
     * the small molecule's beginning. The exact semantics of this position depends on the type of small molecule identified.
     * In case the position information is unknown or not applicable it should not be supplied. For detailed information see
     * protein table.
     *
     * @param modificationsLabel parsed by {@link MZTabUtils#parseModificationList(Section, String)}
     */
    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Small_Molecule, modificationsLabel));
    }

    /**
     * Print small molecule record to a tab-split string.
     *
     * SML  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Small_Molecule.getPrefix() + TAB + super.toString();
    }
}
