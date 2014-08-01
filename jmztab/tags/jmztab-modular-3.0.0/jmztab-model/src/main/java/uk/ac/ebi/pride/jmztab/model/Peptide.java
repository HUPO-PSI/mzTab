package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.BAR;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.COMMA;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseURI;

/**
 * The peptide section is table based. The peptide section must always come after the metadata section
 * and or protein section if these are present in the file. All table columns MUST be tab separated.
 * There MUST NOT be any empty cells. Missing values MUST be reported using "null". Most columns are mandatory.
 *
 * @author qingwei
 * @since 23/05/13
 */
public class Peptide extends MZTabRecord {
    private Metadata metadata;

    /**
     * Create a peptide record which only include stable columns which defined in the {@link PeptideColumn}
     *
     * @param metadata SHOULD NOT set null.
     */
    public Peptide(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Peptide));

        if (metadata == null) {
            throw new NullPointerException("Should define metadata first.");
        }
        this.metadata = metadata;
    }

    /**
     * Create a peptide record based on structure defined by {@link MZTabColumnFactory}
     *
     * @param factory SHOULD NOT set null.
     * @param metadata SHOULD NOT set null.
     */
    public Peptide(MZTabColumnFactory factory, Metadata metadata) {
        super(factory);

        if (metadata == null) {
            throw new NullPointerException("Should define metadata first.");
        }
        this.metadata = metadata;
    }

    /**
     * The peptide's sequence
     */
    public String getSequence() {
        return getString(PeptideColumn.SEQUENCE.getLogicPosition());
    }

    /**
     * The peptide's sequence
     */
    public void setSequence(String sequence) {
        setValue(PeptideColumn.SEQUENCE.getLogicPosition(), parseString(sequence));
    }

    /**
     * The protein's accession the peptide is associated with. In case no protein section is present in the file
     * or the peptide was not assigned to a protein the field should be filled with "null". If the peptide can
     * be assigned to more than one protein, multiple rows SHOULD be provided for each peptide to protein mapping.
     */
    public String getAccession() {
        return getString(PeptideColumn.ACCESSION.getLogicPosition());
    }

    /**
     * The protein's accession the peptide is associated with. In case no protein section is present in the file
     * or the peptide was not assigned to a protein the field should be filled with "null". If the peptide can
     * be assigned to more than one protein, multiple rows SHOULD be provided for each peptide to protein mapping.
     */
    public void setAccession(String accession) {
        setValue(PeptideColumn.ACCESSION.getLogicPosition(), parseString(accession));
    }

    /**
     * Indicates whether the peptide is unique for this protein in respect to the searched database.
     */
    public MZBoolean getUnique() {
        return getMZBoolean(PeptideColumn.UNIQUE.getLogicPosition());
    }

    /**
     * Indicates whether the peptide is unique for this protein in respect to the searched database.
     */
    public void setUnique(MZBoolean unique) {
        setValue(PeptideColumn.UNIQUE.getLogicPosition(), unique);
    }

    /**
     * Indicates whether the peptide is unique for this protein in respect to the searched database.
     *
     * @param uniqueLabel parsed by {@link MZBoolean#findBoolean(String)}
     */
    public void setUnique(String uniqueLabel) {
        setUnique(MZBoolean.findBoolean(uniqueLabel));
    }

    /**
     * The protein database used for the search (could theoretically come from a different species) and
     * the peptide sequence comes from.
     */
    public String getDatabase() {
        return getString(PeptideColumn.DATABASE.getLogicPosition());
    }

    /**
     * The protein database used for the search (could theoretically come from a different species) and
     * the peptide sequence comes from.
     */
    public void setDatabase(String database) {
        setValue(PeptideColumn.DATABASE.getLogicPosition(), parseString(database));
    }

    /**
     * The protein database's version - in case there is no version available (custom build) the creation download
     * (e.g., for NCBI nr) date should be given. Additionally, the number of entries in the database MAY be reported
     * in round brackets after the version in the format: {version} ({#entries} entries), for example "2011-11 (1234 entries)".
     */
    public String getDatabaseVersion() {
        return getString(PeptideColumn.DATABASE_VERSION.getLogicPosition());
    }

    /**
     * The protein database's version - in case there is no version available (custom build) the creation download
     * (e.g., for NCBI nr) date should be given. Additionally, the number of entries in the database MAY be reported
     * in round brackets after the version in the format: {version} ({#entries} entries), for example "2011-11 (1234 entries)".
     */
    public void setDatabaseVersion(String databaseVersion) {
        setValue(PeptideColumn.DATABASE_VERSION.getLogicPosition(), parseString(databaseVersion));
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this peptide. Search engines must be supplied as parameters.
     */
    public SplitList<Param> getSearchEngine() {
        return getSplitList(PeptideColumn.SEARCH_ENGINE.getLogicPosition());
    }

    /**
     * Add a search engine used to identify this peptide. Search engines must be supplied as parameters.
     */
    public boolean addSearchEngineParam(Param param) {
        if (param == null) {
            return false;
        }

        SplitList<Param> params = getSearchEngine();
        if (params == null) {
            params = new SplitList<Param>(BAR);
            setSearchEngine(params);
            params.add(param);
        } else if (! params.contains(param)) {
            params.add(param);
        }

        return true;
    }

    /**
     * Add a search engine used to identify this peptide. Search engines must be supplied as parameters.
     *
     * @param paramLabel parsed by {@link MZTabUtils#parseParam(String)}
     */
    public boolean addSearchEngineParam(String paramLabel) {
        return !isEmpty(paramLabel) && addSearchEngineParam(parseParam(paramLabel));
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this peptide. Search engines must be supplied as parameters.
     */
    public void setSearchEngine(SplitList<Param> searchEngine) {
        setValue(PeptideColumn.SEARCH_ENGINE.getLogicPosition(), searchEngine);
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this peptide. Search engines must be supplied as parameters.
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
        return getDouble(getLogicalPosition(PeptideColumn.BEST_SEARCH_ENGINE_SCORE, id, null));
    }


    /**
     * The best search engine score (for this type of score) for the given protein across
     * all replicates reported. The type of score MUST be defined in the metadata section.
     * If the protein was not identified by the specified search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     */
    public void setBestSearchEngineScore(Integer id, Double bestSearchEngineScore) {
        setValue(getLogicalPosition(PeptideColumn.BEST_SEARCH_ENGINE_SCORE, id, null), bestSearchEngineScore);
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
        return getDouble(getLogicalPosition(PeptideColumn.SEARCH_ENGINE_SCORE, id, msRun));
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
        setValue(getLogicalPosition(PeptideColumn.SEARCH_ENGINE_SCORE, id, msRun), searchEngineScore);
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
     * The reliability of the given peptide identification. This must be supplied by the resource and has to be one of the following values:
     * <ol>
     *     <li>high reliability</li>
     *     <li>medium reliability</li>
     *     <li>poor reliability</li>
     * </ol>
     */
    public Reliability getReliability() {
        return getReliability(PeptideColumn.RELIABILITY.getLogicPosition());
    }

    /**
     * The reliability of the given peptide identification. This must be supplied by the resource and has to be one of the following values:
     * <ol>
     *     <li>high reliability</li>
     *     <li>medium reliability</li>
     *     <li>poor reliability</li>
     * </ol>
     */
    public void setReliability(Reliability reliability) {
        setValue(PeptideColumn.RELIABILITY.getLogicPosition(), reliability);
    }

    /**
     * The reliability of the given peptide identification. This must be supplied by the resource and has to be one of the following values:
     * <ol>
     *     <li>high reliability</li>
     *     <li>medium reliability</li>
     *     <li>poor reliability</li>
     * </ol>
     *
     * @param reliabilityLabel parsed by {@link Reliability#findReliability(String)}
     */
    public void setReliability(String reliabilityLabel) {
        setReliability(Reliability.findReliability(reliabilityLabel));
    }

    /**
     * The peptide's modifications or substitutions. To further distinguish peptide terminal modifications,
     * these SHOULD be reported at position 0 or peptide size + 1 respectively. For detailed information see
     * the modifications section in the protein table. If substitutions are reported, the "sequence" column
     * MUST contain the original, unaltered sequence. Note that in contrast to the PSM  section, fixed modifications
     * or modifications caused by the quantification reagent  i.e. the SILAC labels/tags SHOULD NOT be reported.
     * It is thus also expected that modification reliability scores will typically be reported at the PSM-level only.
     */
    public SplitList<Modification> getModifications() {
        return getSplitList(PeptideColumn.MODIFICATIONS.getLogicPosition());
    }

    /**
     * The peptide's modifications or substitutions. To further distinguish peptide terminal modifications,
     * these SHOULD be reported at position 0 or peptide size + 1 respectively. For detailed information see
     * the modifications section in the protein table. If substitutions are reported, the "sequence" column
     * MUST contain the original, unaltered sequence. Note that in contrast to the PSM  section, fixed modifications
     * or modifications caused by the quantification reagent  i.e. the SILAC labels/tags SHOULD NOT be reported.
     * It is thus also expected that modification reliability scores will typically be reported at the PSM-level only.
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
     * The peptide's modifications or substitutions. To further distinguish peptide terminal modifications,
     * these SHOULD be reported at position 0 or peptide size + 1 respectively. For detailed information see
     * the modifications section in the protein table. If substitutions are reported, the "sequence" column
     * MUST contain the original, unaltered sequence. Note that in contrast to the PSM  section, fixed modifications
     * or modifications caused by the quantification reagent  i.e. the SILAC labels/tags SHOULD NOT be reported.
     * It is thus also expected that modification reliability scores will typically be reported at the PSM-level only.
     */
    public void setModifications(SplitList<Modification> modifications) {
        setValue(PeptideColumn.MODIFICATIONS.getLogicPosition(), modifications);
    }

    /**
     * The peptide's modifications or substitutions. To further distinguish peptide terminal modifications,
     * these SHOULD be reported at position 0 or peptide size + 1 respectively. For detailed information see
     * the modifications section in the protein table. If substitutions are reported, the "sequence" column
     * MUST contain the original, unaltered sequence. Note that in contrast to the PSM  section, fixed modifications
     * or modifications caused by the quantification reagent  i.e. the SILAC labels/tags SHOULD NOT be reported.
     * It is thus also expected that modification reliability scores will typically be reported at the PSM-level only.
     *
     * @param modificationsLabel parsed by {@link MZTabUtils#parseModificationList(Section, String)}
     */
    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Peptide, modificationsLabel));
    }

    /**
     * A '|'-separated list of time points. Semantics may vary on how retention times are reported. For quantification
     * approaches, different exporters MAY wish to export the retention times of all spectra used for quantification
     * (e.g. in MS2 approaches) or the centre point of the feature quantified for MS1 approaches. It is assumed that the
     * reported value(s) are for a given "master" peptide from one assay only (and the unlabeled peptide in label-based
     * approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section ("colunit-peptide").
     */
    public SplitList<Double> getRetentionTime() {
        return getSplitList(PeptideColumn.RETENTION_TIME.getLogicPosition());
    }

    /**
     * A '|'-separated list of time points. Semantics may vary on how retention times are reported. For quantification
     * approaches, different exporters MAY wish to export the retention times of all spectra used for quantification
     * (e.g. in MS2 approaches) or the centre point of the feature quantified for MS1 approaches. It is assumed that the
     * reported value(s) are for a given "master" peptide from one assay only (and the unlabeled peptide in label-based
     * approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section ("colunit-peptide").
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
     * A '|'-separated list of time points. Semantics may vary on how retention times are reported. For quantification
     * approaches, different exporters MAY wish to export the retention times of all spectra used for quantification
     * (e.g. in MS2 approaches) or the centre point of the feature quantified for MS1 approaches. It is assumed that the
     * reported value(s) are for a given "master" peptide from one assay only (and the unlabeled peptide in label-based
     * approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section ("colunit-peptide").
     *
     * @param rtLabel parsed by {@link MZTabUtils#parseDouble(String)}
     */
    public boolean addRetentionTime(String rtLabel) {
        return !isEmpty(rtLabel) && addRetentionTime(parseDouble(rtLabel));
    }

    /**
     * A '|'-separated list of time points. Semantics may vary on how retention times are reported. For quantification
     * approaches, different exporters MAY wish to export the retention times of all spectra used for quantification
     * (e.g. in MS2 approaches) or the centre point of the feature quantified for MS1 approaches. It is assumed that the
     * reported value(s) are for a given "master" peptide from one assay only (and the unlabeled peptide in label-based
     * approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section ("colunit-peptide").
     */
    public void setRetentionTime(SplitList<Double> retentionTime) {
        setValue(PeptideColumn.RETENTION_TIME.getLogicPosition(), retentionTime);
    }

    /**
     * A '|'-separated list of time points. Semantics may vary on how retention times are reported. For quantification
     * approaches, different exporters MAY wish to export the retention times of all spectra used for quantification
     * (e.g. in MS2 approaches) or the centre point of the feature quantified for MS1 approaches. It is assumed that the
     * reported value(s) are for a given "master" peptide from one assay only (and the unlabeled peptide in label-based
     * approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section ("colunit-peptide").
     *
     * @param retentionTimeLabel parsed by {@link MZTabUtils#parseDoubleList(String)}
     */
    public void setRetentionTime(String retentionTimeLabel) {
        setRetentionTime(parseDoubleList(retentionTimeLabel));
    }

    /**
     * Start and end of the retention time window separated by a single '|'. Semantics may vary but its primary intention is
     * to report feature boundaries of eluting peptides (along with feature centroids in the retention_time column). It is
     * assumed that the reported interval is for a given "master" peptide from one assay only (and the unlabeled peptide in
     * label-based approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time windows MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section
     * ("colunit-peptide").
     */
    public SplitList<Double> getRetentionTimeWindow() {
        return getSplitList(PeptideColumn.RETENTION_TIME_WINDOW.getLogicPosition());
    }

    /**
     * Start and end of the retention time window separated by a single '|'. Semantics may vary but its primary intention is
     * to report feature boundaries of eluting peptides (along with feature centroids in the retention_time column). It is
     * assumed that the reported interval is for a given "master" peptide from one assay only (and the unlabeled peptide in
     * label-based approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time windows MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section
     * ("colunit-peptide").
     */
    public boolean addRetentionTimeWindow(Double rtw) {
        if (rtw == null) {
            return false;
        }

        SplitList<Double> rtwList = getRetentionTimeWindow();
        if (rtwList == null) {
            rtwList = new SplitList<Double>(BAR);
            setRetentionTimeWindow(rtwList);
        }

        return rtwList.add(rtw);
    }

    /**
     * Start and end of the retention time window separated by a single '|'. Semantics may vary but its primary intention is
     * to report feature boundaries of eluting peptides (along with feature centroids in the retention_time column). It is
     * assumed that the reported interval is for a given "master" peptide from one assay only (and the unlabeled peptide in
     * label-based approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time windows MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section
     * ("colunit-peptide").
     *
     * @param retentionTimeWindowLabel parsed by {@link MZTabUtils#parseDouble(String)}
     */
    public boolean addRetentionTimeWindow(String retentionTimeWindowLabel) {
        return !isEmpty(retentionTimeWindowLabel) && addRetentionTimeWindow(parseDouble(retentionTimeWindowLabel));
    }

    /**
     * Start and end of the retention time window separated by a single '|'. Semantics may vary but its primary intention is
     * to report feature boundaries of eluting peptides (along with feature centroids in the retention_time column). It is
     * assumed that the reported interval is for a given "master" peptide from one assay only (and the unlabeled peptide in
     * label-based approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time windows MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section
     * ("colunit-peptide").
     */
    public void setRetentionTimeWindow(SplitList<Double> retentionTimeWindow) {
        setValue(PeptideColumn.RETENTION_TIME_WINDOW.getLogicPosition(), retentionTimeWindow);
    }

    /**
     * Start and end of the retention time window separated by a single '|'. Semantics may vary but its primary intention is
     * to report feature boundaries of eluting peptides (along with feature centroids in the retention_time column). It is
     * assumed that the reported interval is for a given "master" peptide from one assay only (and the unlabeled peptide in
     * label-based approaches). If the exporter wishes to export values for all assays, this can be done using optional columns.
     * Retention time windows MUST be reported in seconds. Otherwise, units MUST be reported in the Metadata Section
     * ("colunit-peptide").
     *
     * @param retentionTimeWindowLabel parsed by {@link MZTabUtils#parseDoubleList(String)}
     */
    public void setRetentionTimeWindow(String retentionTimeWindowLabel) {
        setRetentionTimeWindow(parseDoubleList(retentionTimeWindowLabel));
    }

    /**
     * The charge assigned by the search engine/software. In case multiple charge states for the same peptide are observed
     * these should be reported as distinct entries in the peptide table. In case the charge is unknown "null" MUST be used.
     */
    public Integer getCharge() {
        return getInteger(PeptideColumn.CHARGE.getLogicPosition());
    }

    /**
     * The charge assigned by the search engine/software. In case multiple charge states for the same peptide are observed
     * these should be reported as distinct entries in the peptide table. In case the charge is unknown "null" MUST be used.
     */
    public void setCharge(Integer charge) {
        setValue(PeptideColumn.CHARGE.getLogicPosition(), charge);
    }

    /**
     * The charge assigned by the search engine/software. In case multiple charge states for the same peptide are observed
     * these should be reported as distinct entries in the peptide table. In case the charge is unknown "null" MUST be used.
     *
     * @param chargeLabel parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setCharge(String chargeLabel) {
        setCharge(parseInteger(chargeLabel));
    }

    /**
     * The precursor's experimental mass to charge (m/z). It is assumed that the reported value is for a given "master"
     * peptide from one assay only (and the unlabeled peptide in label-based approaches). If the exporter wishes to export
     * values for all assays, this can be done using optional columns.
     */
    public Double getMassToCharge() {
        return getDouble(PeptideColumn.MASS_TO_CHARGE.getLogicPosition());
    }

    /**
     * The precursor's experimental mass to charge (m/z). It is assumed that the reported value is for a given "master"
     * peptide from one assay only (and the unlabeled peptide in label-based approaches). If the exporter wishes to export
     * values for all assays, this can be done using optional columns.
     */
    public void setMassToCharge(Double massToCharge) {
        setValue(PeptideColumn.MASS_TO_CHARGE.getLogicPosition(), massToCharge);
    }

    /**
     * The precursor's experimental mass to charge (m/z). It is assumed that the reported value is for a given "master"
     * peptide from one assay only (and the unlabeled peptide in label-based approaches). If the exporter wishes to export
     * values for all assays, this can be done using optional columns.
     *
     * @param massToChargeLabel parsed by {@link MZTabUtils#parseDouble(String)}
     */
    public void setMassToCharge(String massToChargeLabel) {
        setMassToCharge(parseDouble(massToChargeLabel));
    }

    /**
     * A URI pointing to the peptide's entry in the experiment it was identified in (e.g., the peptide's PRIDE entry).
     */
    public URI getURI() {
        return getURI(PeptideColumn.URI.getLogicPosition());
    }

    /**
     * A URI pointing to the peptide's entry in the experiment it was identified in (e.g., the peptide's PRIDE entry).
     */
    public void setURI(URI uri) {
        setValue(PeptideColumn.URI.getLogicPosition(), uri);
    }

    /**
     * A URI pointing to the peptide's entry in the experiment it was identified in (e.g., the peptide's PRIDE entry).
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
        return getSplitList(PeptideColumn.SPECTRA_REF.getLogicPosition());
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
     */
    public void setSpectraRef(SplitList<SpectraRef> spectraRef) {
        setValue(PeptideColumn.SPECTRA_REF.getLogicPosition(), spectraRef);
    }

    /**
     * Reference to spectra in a spectrum file. It is expected that spectra_ref SHOULD only be used for MS2-based
     * quantification approaches, in which retention time values cannot identify the spectra used for quantification.
     * The reference must be in the format ms_run[1-n]:{SPECTRA_REF} where SPECTRA_REF MUST follow the format defined in 5.2.
     * Multiple spectra MUST be referenced using a "|" delimited list.
     *
     * @param spectraRefLabel parsed by {@link MZTabUtils#parseSpectraRefList(Metadata, String)}
     */
    public void setSpectraRef(String spectraRefLabel) {
        setSpectraRef(parseSpectraRefList(metadata, spectraRefLabel));
    }

    /**
     * Print a peptide record to a tab-split string.
     *
     * PEP  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Peptide.getPrefix() + TAB + super.toString();
    }
}
