package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class Protein extends MZTabRecord {
    public Protein() {
        super(MZTabColumnFactory.getInstance(Section.Protein));
    }

    public Protein(MZTabColumnFactory factory) {
        super(factory);
    }

    public String getAccession() {
        return getString(ProteinColumn.ACCESSION.getOrder());
    }

    public void setAccession(String accession) {
        setValue(ProteinColumn.ACCESSION.getOrder(), parseString(accession));
    }

    public String getDescription() {
        return getString(ProteinColumn.DESCRIPTION.getOrder());
    }

    public void setDescription(String description) {
        setValue(ProteinColumn.DESCRIPTION.getOrder(), parseString(description));
    }

    public Integer getTaxid() {
        return getInteger(ProteinColumn.TAXID.getOrder());
    }

    public void setTaxid(Integer taxid) {
        setValue(ProteinColumn.TAXID.getOrder(), taxid);
    }

    public void setTaxid(String taxid) {
        setTaxid(parseInteger(taxid));
    }

    public String getSpecies() {
        return getString(ProteinColumn.SPECIES.getOrder());
    }

    public void setSpecies(String species) {
        setValue(ProteinColumn.SPECIES.getOrder(), parseString(species));
    }

    public String getDatabase() {
        return getString(ProteinColumn.DATABASE.getOrder());
    }

    public void setDatabase(String database) {
        setValue(ProteinColumn.DATABASE.getOrder(), parseString(database));
    }

    public String getDatabaseVersion() {
        return getString(ProteinColumn.DATABASE_VERSION.getOrder());
    }

    public void setDatabaseVersion(String databaseVersion) {
        setValue(ProteinColumn.DATABASE_VERSION.getOrder(), parseString(databaseVersion));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getSearchEngine() {
        return getSplitList(ProteinColumn.SEARCH_ENGINE.getOrder());
    }

    public void setSearchEngine(SplitList<Param> searchEngine) {
        setValue(ProteinColumn.SEARCH_ENGINE.getOrder(), searchEngine);
    }

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

    public boolean addSearchEngineParam(String paramLabel) {
        return !isEmpty(paramLabel) && addSearchEngineParam(parseParam(paramLabel));
    }

    public void setSearchEngine(String searchEngineLabel) {
        setSearchEngine(parseParamList(searchEngineLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getBestSearchEngineScore() {
        return getSplitList(ProteinColumn.BEST_SEARCH_ENGINE_SCORE.getOrder());
    }

    public void setBestSearchEngineScore(SplitList<Param> bestSearchEngineScore) {
        setValue(ProteinColumn.BEST_SEARCH_ENGINE_SCORE.getOrder(), bestSearchEngineScore);
    }

    public boolean addBestSearchEngineScoreParam(Param param) {
        if (param == null) {
            return false;
        }

        SplitList<Param> params = getBestSearchEngineScore();
        if (params == null) {
            params = new SplitList<Param>(BAR);
            setBestSearchEngineScore(params);
        }

        return params.add(param);
    }

    public boolean addBestSearchEngineScoreParam(String paramLabel) {
        return !isEmpty(paramLabel) && addBestSearchEngineScoreParam(parseParam(paramLabel));
    }

    public void setBestSearchEngineScore(String searchEngineScoreLabel) {
        setBestSearchEngineScore(parseParamList(searchEngineScoreLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getSearchEngineScore(MsFile msFile) {
        return getSplitList(getPosition(ProteinColumn.SEARCH_ENGINE_SCORE, msFile));
    }

    public void setSearchEngineScore(MsFile msFile, SplitList<Param> searchEngineScore) {
        setValue(getPosition(ProteinColumn.SEARCH_ENGINE_SCORE, msFile), searchEngineScore);
    }

    public boolean addSearchEngineScoreParam(MsFile msFile, CVParam param) {
        if (param == null) {
            return false;
        }

        SplitList<Param> params = getSearchEngineScore(msFile);
        if (params == null) {
            params = new SplitList<Param>(BAR);
            setSearchEngineScore(msFile, params);
        }
        params.add(param);

        return true;
    }

    public void setSearchEngineScore(MsFile msFile, String paramsLabel) {
        setSearchEngineScore(msFile, parseParamList(paramsLabel));
    }

    public Reliability getReliability() {
        return getReliability(ProteinColumn.RELIABILITY.getOrder());
    }

    public void setReliability(Reliability reliability) {
        setValue(ProteinColumn.RELIABILITY.getOrder(), reliability);
    }

    public void setReliability(String reliabilityLabel) {
        setReliability(Reliability.findReliability(reliabilityLabel));
    }

    public Integer getNumPSMs(MsFile msFile) {
        return getInteger(getPosition(ProteinColumn.NUM_PSMS, msFile));
    }

    public void setNumPSMs(MsFile msFile, Integer numPSMs) {
        setValue(getPosition(ProteinColumn.NUM_PSMS, msFile), numPSMs);
    }

    public void setNumPSMs(MsFile msFile, String numPSMsLabel) {
        setNumPSMs(msFile, parseInteger(numPSMsLabel));
    }

    public Integer getNumPeptidesDistinct(MsFile msFile) {
        return getInteger(getPosition(ProteinColumn.NUM_PEPTIDES_DISTINCT, msFile));
    }

    public void setNumPeptidesDistinct(MsFile msFile, Integer numPeptidesDistinct) {
        setValue(getPosition(ProteinColumn.NUM_PEPTIDES_DISTINCT, msFile), numPeptidesDistinct);
    }

    public void setNumPeptidesDistinct(MsFile msFile, String numPeptidesDistinct) {
        setNumPeptidesDistinct(msFile, parseInteger(numPeptidesDistinct));
    }

    public Integer getNumPeptidesUnique(MsFile msFile) {
        return getInteger(getPosition(ProteinColumn.NUM_PEPTIDES_UNIQUE, msFile));
    }

    public void setNumPeptidesUnique(MsFile msFile, Integer numPeptidesUnique) {
        setValue(getPosition(ProteinColumn.NUM_PEPTIDES_UNIQUE, msFile), numPeptidesUnique);
    }

    public void setNumPeptidesUnique(MsFile msFile, String numPeptidesUnique) {
        setNumPeptidesUnique(msFile, parseInteger(numPeptidesUnique));
    }

    @SuppressWarnings("unchecked")
    public SplitList<String> getAmbiguityMembers() {
        return getSplitList(ProteinColumn.AMBIGUITY_MEMGERS.getOrder());
    }

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

    public void setAmbiguityMembers(SplitList<String> ambiguityMembers) {
        setValue(ProteinColumn.AMBIGUITY_MEMGERS.getOrder(), ambiguityMembers);
    }

    public void setAmbiguityMembers(String ambiguityMembersLabel) {
        setAmbiguityMembers(parseStringList(COMMA, ambiguityMembersLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Modification> getModifications() {
        return getSplitList(ProteinColumn.MODIFICATIONS.getOrder());
    }

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

    public void setModifications(SplitList<Modification> modifications) {
        setValue(ProteinColumn.MODIFICATIONS.getOrder(), modifications);
    }

    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Protein, modificationsLabel));
    }

    public URI getURI() {
        return getURI(ProteinColumn.URI.getOrder());
    }

    public void setURI(URI uri) {
        setValue(ProteinColumn.URI.getOrder(), uri);
    }

    public void setURI(String uriLabel) {
        setURI(parseURI(uriLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<String> getGOTerms() {
        return getSplitList(ProteinColumn.GO_TERMS.getOrder());
    }

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

    public void setGOTerms(SplitList<String> goTerms) {
        setValue(ProteinColumn.GO_TERMS.getOrder(), goTerms);
    }

    public void setGOTerms(String goTermsLabel) {
        setGOTerms(parseStringList(BAR, goTermsLabel));
    }

    public Double getProteinCoverage() {
        return getDouble(ProteinColumn.PROTEIN_COVERAGE.getOrder());
    }

    public void setProteinConverage(Double proteinConverage) {
        setValue(ProteinColumn.PROTEIN_COVERAGE.getOrder(), proteinConverage);
    }

    public void setProteinConverage(String proteinConverageLabel) {
        setProteinConverage(parseDouble(proteinConverageLabel));
    }

    /**
     * PRT  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Protein.getPrefix() + TAB + super.toString();
    }
}
