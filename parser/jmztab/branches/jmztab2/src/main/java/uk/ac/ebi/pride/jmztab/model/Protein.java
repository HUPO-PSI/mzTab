package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class Protein extends AbstractMZTabRecord {
    public Protein() {
        super(MZTabColumnFactory.getInstance(Section.Protein));
    }

    public Protein(MZTabColumnFactory factory) {
        super(factory);
    }

    public String getAccession() {
        return getString(1);
    }

    public void setAccession(String accession) {
        addValue(1, parseString(accession));
    }

    public String getUnitId() {
        return getString(2);
    }

    public void setUnitId(String unitId) {
        addValue(2, parseString(unitId));
    }

    public String getDescription() {
        return getString(3);
    }

    public void setDescription(String description) {
        addValue(3, parseString(description));
    }

    public Integer getTaxid() {
        return getInteger(4);
    }

    public void setTaxid(Integer taxid) {
        addValue(4, taxid);
    }

    public void setTaxid(String taxid) {
        setTaxid(parseInteger(taxid));
    }

    public String getSpecies() {
        return getString(5);
    }

    public void setSpecies(String species) {
        addValue(5, parseString(species));
    }

    public String getDatabase() {
        return getString(6);
    }

    public void setDatabase(String database) {
        addValue(6, parseString(database));
    }

    public String getDatabaseVersion() {
        return getString(7);
    }

    public void setDatabaseVersion(String databaseVersion) {
        addValue(7, parseString(databaseVersion));
    }

    public SplitList<Param> getSearchEngine() {
        return getSplitList(8);
    }

    public void setSearchEngine(SplitList<Param> searchEngine) {
        addValue(8, searchEngine);
    }

    public void setSearchEngine(String searchEngine) {
        setSearchEngine(parseParamList(searchEngine));
    }

    public SplitList<Param> getSearchEngineScore() {
        return getSplitList(9);
    }

    public void setSearchEngineScore(SplitList<Param> searchEngineScore) {
        addValue(9, searchEngineScore);
    }

    public void setSearchEngineScore(String searchEngineScore) {
        setSearchEngineScore(parseParamList(searchEngineScore));
    }

    public Reliability getReliability() {
        return getReliability(10);
    }

    public void setReliability(Reliability reliability) {
        addValue(10, reliability);
    }

    public void setReliability(String reliability) {
        setReliability(Reliability.findReliability(reliability));
    }

    public Integer getNumPeptides() {
        return getInteger(11);
    }

    public void setNumPeptides(Integer numPeptides) {
        addValue(11, numPeptides);
    }

    public void setNumPeptides(String numPeptides) {
        setNumPeptides(parseInteger(numPeptides));
    }

    public Integer getNumPeptidesDistinct() {
        return getInteger(12);
    }

    public void setNumPeptideDistinct(Integer numPeptideDistinct) {
        addValue(12, numPeptideDistinct);
    }

    public void setNumPeptideDistinct(String numPeptideDistinct) {
        setNumPeptideDistinct(parseInteger(numPeptideDistinct));
    }

    public Integer getNumPeptidesUnambiguous() {
        return getInteger(13);
    }

    public void setNumPeptidesUnambiguous(Integer numPeptidesUnambiguous) {
        addValue(13, numPeptidesUnambiguous);
    }

    public void setNumPeptidesUnambiguous(String numPeptidesUnambiguous) {
        setNumPeptidesUnambiguous(parseInteger(numPeptidesUnambiguous));
    }

    public SplitList<String> getAmbiguityMembers() {
        return getSplitList(14);
    }

    public void setAmbiguityMembers(SplitList<String> ambiguityMembers) {
        addValue(14, ambiguityMembers);
    }

    public void setAmbiguityMembers(String ambiguityMembers) {
        setAmbiguityMembers(parseStringList(MZTabConstants.COMMA, ambiguityMembers));
    }

    public SplitList<Modification> getModifications() {
        return getSplitList(15);
    }

    public void setModifications(SplitList<Modification> modifications) {
        addValue(15, modifications);
    }

    public void setModifications(String modifications) {
        setModifications(parseModificationList(Section.Protein, modifications));
    }

    public URI getURI() {
        return getURI(16);
    }

    public void setURI(URI uri) {
        addValue(16, uri);
    }

    public void setURI(String uri) {
        setURI(parseURI(uri));
    }

    public SplitList<String> getGOTerms() {
        return getSplitList(17);
    }

    public void setGOTerms(SplitList<String> goTerms) {
        addValue(17, goTerms);
    }

    public void setGOTerms(String goTerms) {
        setGOTerms(parseStringList(MZTabConstants.BAR, goTerms));
    }

    public Double getProteinCoverage() {
        return getDouble(18);
    }

    public void setProteinConverage(Double proteinConverage) {
        addValue(18, proteinConverage);
    }

    public void setProteinConverage(String proteinConverage) {
        setProteinConverage(parseDouble(proteinConverage));
    }

    /**
     * PRT  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Protein.getPrefix() + MZTabConstants.TAB + super.toString();
    }
}
