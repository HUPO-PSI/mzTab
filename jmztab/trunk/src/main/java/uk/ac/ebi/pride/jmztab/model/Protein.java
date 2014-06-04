package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * The protein section is table-based. The protein section MUST always come after the metadata section.
 * All table columns MUST be tab-separated. There MUST NOT be any empty cells. Missing values MUST be
 * reported using "null". Most columns are mandatory. The order of columns is not specified although
 * for ease of human interpretation, it is RECOMMENDED to follow the order specified below.
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class Protein extends MZTabRecord {
    /**
     * Create a protein record which only include stable columns which defined in the {@link ProteinColumn}
     */
    public Protein() {
        super(MZTabColumnFactory.getInstance(Section.Protein));
    }

    /**
     * Create a protein record based on structure defined by {@link MZTabColumnFactory}
     *
     * @param factory SHOULD NOT set null.
     */
    public Protein(MZTabColumnFactory factory) {
        super(factory);
    }

    /**
     * The accession of the protein in the source database. A protein accession MUST be unique within one mzTab file.
     * If different quantification values are required for the same underlying accession, for example if differentially
     * modified forms of a protein have been quantified, a the suffix [1-n] SHOULD be appended to the accession e.g.
     * P12345[1], P12345[2].
     */
    public String getAccession() {
        return getString(ProteinColumn.ACCESSION.getLogicPosition());
    }

    /**
     * The accession of the protein in the source database. A protein accession MUST be unique within one mzTab file.
     * If different quantification values are required for the same underlying accession, for example if differentially
     * modified forms of a protein have been quantified, a the suffix [1-n] SHOULD be appended to the accession e.g.
     * P12345[1], P12345[2].
     */
    public void setAccession(String accession) {
        setValue(ProteinColumn.ACCESSION.getLogicPosition(), parseString(accession));
    }

    /**
     * The protein's name and or description line.
     */
    public String getDescription() {
        return getString(ProteinColumn.DESCRIPTION.getLogicPosition());
    }

    /**
     * The protein's name and or description line.
     */
    public void setDescription(String description) {
        setValue(ProteinColumn.DESCRIPTION.getLogicPosition(), parseString(description));
    }

    /**
     * The NCBI/NEWT taxonomy id for the species the protein was identified in.
     */
    public Integer getTaxid() {
        return getInteger(ProteinColumn.TAXID.getLogicPosition());
    }

    /**
     * The NCBI/NEWT taxonomy id for the species the protein was identified in.
     */
    public void setTaxid(Integer taxid) {
        setValue(ProteinColumn.TAXID.getLogicPosition(), taxid);
    }

    /**
     * The NCBI/NEWT taxonomy id for the species the protein was identified in.
     *
     * @param taxid parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setTaxid(String taxid) {
        setTaxid(parseInteger(taxid));
    }

    /**
     * The human readable species the protein was identified in - this SHOULD be the NCBI entry's name.
     */
    public String getSpecies() {
        return getString(ProteinColumn.SPECIES.getLogicPosition());
    }

    /**
     * The human readable species the protein was identified in - this SHOULD be the NCBI entry's name.
     */
    public void setSpecies(String species) {
        setValue(ProteinColumn.SPECIES.getLogicPosition(), parseString(species));
    }

    /**
     * The protein database used for the search (could theoretically come from a different species).
     * Wherever possible the Miriam (http://www.ebi.ac.uk/miriam) assigned name SHOULD be used.
     */
    public String getDatabase() {
        return getString(ProteinColumn.DATABASE.getLogicPosition());
    }

    /**
     * The protein database used for the search (could theoretically come from a different species).
     * Wherever possible the Miriam (http://www.ebi.ac.uk/miriam) assigned name SHOULD be used.
     */
    public void setDatabase(String database) {
        setValue(ProteinColumn.DATABASE.getLogicPosition(), parseString(database));
    }

    /**
     * The protein database's version - in case there is no version available (custom build) the creation
     * download (e.g., for NCBI nr) date SHOULD be given. Additionally, the number of entries in the database
     * MAY be reported in round brackets after the version in the format: {version} ({#entries} entries),
     * for example "2011-11 (1234 entries)".
     */
    public String getDatabaseVersion() {
        return getString(ProteinColumn.DATABASE_VERSION.getLogicPosition());
    }

    /**
     * The protein database's version - in case there is no version available (custom build) the creation
     * download (e.g., for NCBI nr) date SHOULD be given. Additionally, the number of entries in the database
     * MAY be reported in round brackets after the version in the format: {version} ({#entries} entries),
     * for example "2011-11 (1234 entries)".
     */
    public void setDatabaseVersion(String databaseVersion) {
        setValue(ProteinColumn.DATABASE_VERSION.getLogicPosition(), parseString(databaseVersion));
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this protein. Search engines MUST be supplied as parameters.
     */
    public SplitList<Param> getSearchEngine() {
        return getSplitList(ProteinColumn.SEARCH_ENGINE.getLogicPosition());
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this protein. Search engines MUST be supplied as parameters.
     */
    public void setSearchEngine(SplitList<Param> searchEngine) {
        setValue(ProteinColumn.SEARCH_ENGINE.getLogicPosition(), searchEngine);
    }

    /**
     * Add a search engine(s) used to identify this protein. Search engines MUST be supplied as parameters.
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
     * Add a search engine(s) used to identify this protein. Search engines MUST be supplied as parameters.
     *
     * @param paramLabel parsed by {@link MZTabUtils#parseParam(String)}
     */
    public boolean addSearchEngineParam(String paramLabel) {
        return !isEmpty(paramLabel) && addSearchEngineParam(parseParam(paramLabel));
    }

    /**
     * A "|" delimited list of search engine(s) used to identify this protein. Search engines MUST be supplied as parameters.
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
        return getDouble(getLogicalPosition(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, id, null));
    }


    /**
     * The best search engine score (for this type of score) for the given protein across
     * all replicates reported. The type of score MUST be defined in the metadata section.
     * If the protein was not identified by the specified search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     */
    public void setBestSearchEngineScore(Integer id, Double bestSearchEngineScore) {
        setValue(getLogicalPosition(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, id, null), bestSearchEngineScore);
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
     * @return
     */
    public Double getSearchEngineScore(Integer id, MsRun msRun) {
        return getDouble(getLogicalPosition(ProteinColumn.SEARCH_ENGINE_SCORE, id, msRun));
    }

    /**
     * The search engine score for the given protein in the defined ms run. The type of score
     * MUST be defined in the metadata section. If the protein was not identified by the specified
     * search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     * @param msRun SHOULD NOT set null
     * @return
     */
    public void setSearchEngineScore(Integer id, MsRun msRun, Double searchEngineScore) {
        setValue(getLogicalPosition(ProteinColumn.SEARCH_ENGINE_SCORE, id, msRun), searchEngineScore);
    }

    /**
     * The search engine score for the given protein in the defined ms run. The type of score
     * MUST be defined in the metadata section. If the protein was not identified by the specified
     * search engine “null” must be reported
     *
     * @param id search_engine_score[id] which MUST be defined in the metadata section.
     * @param msRun SHOULD NOT set null
     * @return
     */
    public void setSearchEngineScore(Integer id, MsRun msRun, String paramsLabel) {
        setSearchEngineScore(id, msRun, parseDouble(paramsLabel));
    }

    /**
     * The reliability of the given protein identification. This must be supplied by the resource and has to be one of the following values:
     * <ol>
     *     <li>high reliability</li>
     *     <li>medium reliability</li>
     *     <li>poor reliability</li>
     * </ol>
     */
    public Reliability getReliability() {
        return getReliability(ProteinColumn.RELIABILITY.getLogicPosition());
    }

    /**
     * The reliability of the given protein identification. This must be supplied by the resource and has to be one of the following values:
     * <ol>
     *     <li>high reliability</li>
     *     <li>medium reliability</li>
     *     <li>poor reliability</li>
     * </ol>
     */
    public void setReliability(Reliability reliability) {
        setValue(ProteinColumn.RELIABILITY.getLogicPosition(), reliability);
    }

    /**
     * The reliability of the given protein identification. This must be supplied by the resource and has to be one of the following values:
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
     * The count of the total significant PSMs that can be mapped to the reported protein.
     *
     * @param msRun SHOULD NOT set null.
     */
    public Integer getNumPSMs(MsRun msRun) {
        return getInteger(getLogicalPosition(ProteinColumn.NUM_PSMS, null, msRun));
    }

    /**
     * The count of the total significant PSMs that can be mapped to the reported protein.
     */
    public void setNumPSMs(String logicalPosition, Integer numPSMs) {
        setValue(logicalPosition, numPSMs);
    }

    /**
     * The count of the total significant PSMs that can be mapped to the reported protein.
     *
     * @param msRun SHOULD NOT set null.
     */
    public void setNumPSMs(MsRun msRun, Integer numPSMs) {
        setNumPSMs(getLogicalPosition(ProteinColumn.NUM_PSMS, null, msRun), numPSMs);
    }

    /**
     * The count of the total significant PSMs that can be mapped to the reported protein.
     *
     * @param numPSMsLabel parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setNumPSMs(String logicalPosition, String numPSMsLabel) {
        setNumPSMs(logicalPosition, parseInteger(numPSMsLabel));
    }

    /**
     * The count of the total significant PSMs that can be mapped to the reported protein.
     *
     * @param msRun SHOULD NOT set null.
     * @param numPSMsLabel parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setNumPSMs(MsRun msRun, String numPSMsLabel) {
        setNumPSMs(msRun, parseInteger(numPSMsLabel));
    }

    /**
     * The count of the number of different peptide sequences that have been identified above the significance threshold.
     * Different modifications or charge states of the same peptide are not counted.
     *
     * @param msRun SHOULD NOT set null.
     */
    public Integer getNumPeptidesDistinct(MsRun msRun) {
        return getInteger(getLogicalPosition(ProteinColumn.NUM_PEPTIDES_DISTINCT, null, msRun));
    }

    /**
     * The count of the number of different peptide sequences that have been identified above the significance threshold.
     * Different modifications or charge states of the same peptide are not counted.
     *
     * @param msRun SHOULD NOT set null.
     */
    public void setNumPeptidesDistinct(MsRun msRun, Integer numPeptidesDistinct) {
        setNumPeptidesDistinct(getLogicalPosition(ProteinColumn.NUM_PEPTIDES_DISTINCT, null, msRun), numPeptidesDistinct);
    }

    /**
     * The count of the number of different peptide sequences that have been identified above the significance threshold.
     * Different modifications or charge states of the same peptide are not counted.
     */
    public void setNumPeptidesDistinct(String logicalPosition, Integer numPeptidesDistinct) {
        setValue(logicalPosition, numPeptidesDistinct);
    }

    /**
     * The count of the number of different peptide sequences that have been identified above the significance threshold.
     * Different modifications or charge states of the same peptide are not counted.
     *
     * @param numPeptidesDistinct parsed by {@link MZTabUtils#parseInteger(String)}
     * @param msRun SHOULD NOT set null.
     */
    public void setNumPeptidesDistinct(MsRun msRun, String numPeptidesDistinct) {
        setNumPeptidesDistinct(msRun, parseInteger(numPeptidesDistinct));
    }

    /**
     * The count of the number of different peptide sequences that have been identified above the significance threshold.
     * Different modifications or charge states of the same peptide are not counted.
     *
     * @param numPeptidesDistinct parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setNumPeptidesDistinct(String logicalPosition, String numPeptidesDistinct) {
        setNumPeptidesDistinct(logicalPosition, parseInteger(numPeptidesDistinct));
    }

    /**
     * The number of peptides that can be mapped uniquely to the protein reported. If ambiguity members have been reported,
     * the count MUST be derived from the number of peptides that can be uniquely mapped to the group of accessions, since
     * the assumption is that these accessions are supported by the same evidence.
     *
     * @param msRun SHOULD NOT set null.
     */
    public Integer getNumPeptidesUnique(MsRun msRun) {
        return getInteger(getLogicalPosition(ProteinColumn.NUM_PEPTIDES_UNIQUE, null, msRun));
    }

    /**
     * The number of peptides that can be mapped uniquely to the protein reported. If ambiguity members have been reported,
     * the count MUST be derived from the number of peptides that can be uniquely mapped to the group of accessions, since
     * the assumption is that these accessions are supported by the same evidence.
     */
    public void setNumPeptidesUnique(String logicalPosition, Integer numPeptidesUnique) {
        setValue(logicalPosition, numPeptidesUnique);
    }

    /**
     * The number of peptides that can be mapped uniquely to the protein reported. If ambiguity members have been reported,
     * the count MUST be derived from the number of peptides that can be uniquely mapped to the group of accessions, since
     * the assumption is that these accessions are supported by the same evidence.
     *
     * @param msRun SHOULD NOT set null.
     */
    public void setNumPeptidesUnique(MsRun msRun, Integer numPeptidesUnique) {
        setNumPeptidesUnique(getLogicalPosition(ProteinColumn.NUM_PEPTIDES_UNIQUE, null, msRun), numPeptidesUnique);
    }

    /**
     * The number of peptides that can be mapped uniquely to the protein reported. If ambiguity members have been reported,
     * the count MUST be derived from the number of peptides that can be uniquely mapped to the group of accessions, since
     * the assumption is that these accessions are supported by the same evidence.
     *
     * @param numPeptidesUnique parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setNumPeptidesUnique(String logicalPosition, String numPeptidesUnique) {
        setNumPeptidesUnique(logicalPosition, parseInteger(numPeptidesUnique));
    }

    /**
     * The number of peptides that can be mapped uniquely to the protein reported. If ambiguity members have been reported,
     * the count MUST be derived from the number of peptides that can be uniquely mapped to the group of accessions, since
     * the assumption is that these accessions are supported by the same evidence.
     *
     * @param msRun SHOULD NOT set null.
     * @param numPeptidesUnique parsed by {@link MZTabUtils#parseInteger(String)}
     */
    public void setNumPeptidesUnique(MsRun msRun, String numPeptidesUnique) {
        setNumPeptidesUnique(msRun, parseInteger(numPeptidesUnique));
    }

    /**
     * A comma-delimited list of protein accessions. This field should be set in the representative protein of the ambiguity
     * group (the protein identified through the accession in the first column). The accessions listed in this field should
     * identify proteins that could also be identified through these peptides (e.g. "same-set proteins") but were not chosen
     * by the researcher or resource, often for arbitrary reasons. It is NOT RECOMMENDED to report subset proteins as
     * ambiguity_members, since the proteins reported here, together with the representative protein are taken to be a group
     * that cannot be separated based on the peptide evidence.
     */
    public SplitList<String> getAmbiguityMembers() {
        return getSplitList(ProteinColumn.AMBIGUITY_MEMBERS.getLogicPosition());
    }

    /**
     * Add a protein accessions. This field should be set in the representative protein of the ambiguity
     * group (the protein identified through the accession in the first column). The accessions listed in this field should
     * identify proteins that could also be identified through these peptides (e.g. "same-set proteins") but were not chosen
     * by the researcher or resource, often for arbitrary reasons. It is NOT RECOMMENDED to report subset proteins as
     * ambiguity_members, since the proteins reported here, together with the representative protein are taken to be a group
     * that cannot be separated based on the peptide evidence.
     */
    public boolean addAmbiguityMembers(String member) {
        if (isEmpty(member)) {
            return false;
        }

        SplitList<String> ambiguityMembers = getAmbiguityMembers();
        if (ambiguityMembers == null) {
            ambiguityMembers = new SplitList<String>(COMMA);
            setAmbiguityMembers(ambiguityMembers);
        }

        return ambiguityMembers.add(member);
    }

    /**
     * A comma-delimited list of protein accessions. This field should be set in the representative protein of the ambiguity
     * group (the protein identified through the accession in the first column). The accessions listed in this field should
     * identify proteins that could also be identified through these peptides (e.g. "same-set proteins") but were not chosen
     * by the researcher or resource, often for arbitrary reasons. It is NOT RECOMMENDED to report subset proteins as
     * ambiguity_members, since the proteins reported here, together with the representative protein are taken to be a group
     * that cannot be separated based on the peptide evidence.
     */
    public void setAmbiguityMembers(SplitList<String> ambiguityMembers) {
        setValue(ProteinColumn.AMBIGUITY_MEMBERS.getLogicPosition(), ambiguityMembers);
    }

    /**
     * A comma-delimited list of protein accessions. This field should be set in the representative protein of the ambiguity
     * group (the protein identified through the accession in the first column). The accessions listed in this field should
     * identify proteins that could also be identified through these peptides (e.g. "same-set proteins") but were not chosen
     * by the researcher or resource, often for arbitrary reasons. It is NOT RECOMMENDED to report subset proteins as
     * ambiguity_members, since the proteins reported here, together with the representative protein are taken to be a group
     * that cannot be separated based on the peptide evidence.
     *
     * @param ambiguityMembersLabel parsed by {@link MZTabUtils#parseStringList(char, String)}
     */
    public void setAmbiguityMembers(String ambiguityMembersLabel) {
        setAmbiguityMembers(parseStringList(COMMA, ambiguityMembersLabel));
    }

    /**
     * In contrast to the PSM section, fixed modifications or modifications caused by the quantification reagent
     * (i.e. the SILAC/iTRAQ label) SHOULD NOT be reported in this column.
     *
     * If different modifications are identified from different ms_runs, a superset of the identified modifications
     * SHOULD be reported here. Detailed modification mapping to individual ms_runs is provided through the PSM table.
     *
     * If protein level modifications are not reported, a "null" MUST be used. If protein level modifications are
     * reported but not present on a given protein, a "0" MUST be reported.
     */
    public SplitList<Modification> getModifications() {
        return getSplitList(ProteinColumn.MODIFICATIONS.getLogicPosition());
    }

    /**
     * In contrast to the PSM section, fixed modifications or modifications caused by the quantification reagent
     * (i.e. the SILAC/iTRAQ label) SHOULD NOT be reported in this column.
     *
     * If different modifications are identified from different ms_runs, a superset of the identified modifications
     * SHOULD be reported here. Detailed modification mapping to individual ms_runs is provided through the PSM table.
     *
     * If protein level modifications are not reported, a "null" MUST be used. If protein level modifications are
     * reported but not present on a given protein, a "0" MUST be reported.
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
     * In contrast to the PSM section, fixed modifications or modifications caused by the quantification reagent
     * (i.e. the SILAC/iTRAQ label) SHOULD NOT be reported in this column.
     *
     * If different modifications are identified from different ms_runs, a superset of the identified modifications
     * SHOULD be reported here. Detailed modification mapping to individual ms_runs is provided through the PSM table.
     *
     * If protein level modifications are not reported, a "null" MUST be used. If protein level modifications are
     * reported but not present on a given protein, a "0" MUST be reported.
     */
    public void setModifications(SplitList<Modification> modifications) {
        setValue(ProteinColumn.MODIFICATIONS.getLogicPosition(), modifications);
    }

    /**
     * In contrast to the PSM section, fixed modifications or modifications caused by the quantification reagent
     * (i.e. the SILAC/iTRAQ label) SHOULD NOT be reported in this column.
     *
     * If different modifications are identified from different ms_runs, a superset of the identified modifications
     * SHOULD be reported here. Detailed modification mapping to individual ms_runs is provided through the PSM table.
     *
     * If protein level modifications are not reported, a "null" MUST be used. If protein level modifications are
     * reported but not present on a given protein, a "0" MUST be reported.
     *
     * @param modificationsLabel parsed by {@link MZTabUtils#parseModificationList(Section, String)}
     */
    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Protein, modificationsLabel));
    }

    /**
     * A URI pointing to the protein's source entry in the unit it was identified in (e.g., the PRIDE database
     * or a local database / file identifier).
     */
    public URI getURI() {
        return getURI(ProteinColumn.URI.getLogicPosition());
    }

    /**
     * A URI pointing to the protein's source entry in the unit it was identified in (e.g., the PRIDE database
     * or a local database / file identifier).
     */
    public void setURI(URI uri) {
        setValue(ProteinColumn.URI.getLogicPosition(), uri);
    }

    /**
     * A URI pointing to the protein's source entry in the unit it was identified in (e.g., the PRIDE database
     * or a local database / file identifier).
     *
     * @param uriLabel parsed by {@link MZTabUtils#parseURI(String)}
     */
    public void setURI(String uriLabel) {
        setURI(parseURI(uriLabel));
    }

    /**
     * A '|'-delimited list of GO accessions for this protein.
     */
    public SplitList<String> getGOTerms() {
        return getSplitList(ProteinColumn.GO_TERMS.getLogicPosition());
    }

    /**
     * Add a GO accession for this protein.
     */
    public boolean addGOTerm(String term) {
        if (isEmpty(term)) {
            return false;
        }

        SplitList<String> goTerms = getGOTerms();
        if (goTerms == null) {
            goTerms = new SplitList<String>(BAR);
            setGOTerms(goTerms);
        }

        return goTerms.add(term);
    }

    /**
     * A '|'-delimited list of GO accessions for this protein.
     */
    public void setGOTerms(SplitList<String> goTerms) {
        setValue(ProteinColumn.GO_TERMS.getLogicPosition(), goTerms);
    }

    /**
     * A '|'-delimited list of GO accessions for this protein.
     *
     * @param goTermsLabel parsed by {@link MZTabUtils#parseStringList(char, String)}
     */
    public void setGOTerms(String goTermsLabel) {
        setGOTerms(parseStringList(BAR, goTermsLabel));
    }

    /**
     * A value between 0 and 1 defining the protein coverage.
     */
    public Double getProteinCoverage() {
        return getDouble(ProteinColumn.PROTEIN_COVERAGE.getLogicPosition());
    }

    /**
     * A value between 0 and 1 defining the protein coverage.
     */
    public void setProteinConverage(Double proteinConverage) {
        setValue(ProteinColumn.PROTEIN_COVERAGE.getLogicPosition(), proteinConverage);
    }

    /**
     * A value between 0 and 1 defining the protein coverage.
     *
     * @param proteinConverageLabel parsed by {@link MZTabUtils#parseDouble(String)}
     */
    public void setProteinConverage(String proteinConverageLabel) {
        setProteinConverage(parseDouble(proteinConverageLabel));
    }

    /**
     * Print protein record into a tab-split string.
     *
     * PRT  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Protein.getPrefix() + TAB + super.toString();
    }
}
