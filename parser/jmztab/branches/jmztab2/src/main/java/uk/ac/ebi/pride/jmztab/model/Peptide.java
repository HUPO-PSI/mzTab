package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class Peptide extends AbstractMZTabRecord {
    public Peptide() {
        super(MZTabColumnFactory.getInstance(Section.Peptide));
    }

    public Peptide(MZTabColumnFactory factory) {
        super(factory);
    }

    public String getSequence() {
        return getString(1);
    }

    public void setSequence(String sequence) {
        addValue(1, parseString(sequence));
    }

    public String getAccession() {
        return getString(2);
    }

    public void setAccession(String accession) {
        addValue(2, parseString(accession));
    }

    public String getUnitId() {
        return getString(3);
    }

    public void setUnitId(String unitId) {
        addValue(3, parseString(unitId));
    }

    public MZBoolean getUnique() {
        return getMZBoolean(4);
    }

    public void setUnique(MZBoolean unique) {
        addValue(4, unique);
    }

    public void setUnique(String uniqueLabel) {
        setUnique(MZBoolean.findBoolean(uniqueLabel));
    }

    public String getDatabase() {
        return getString(5);
    }

    public void setDatabase(String database) {
        addValue(5, parseString(database));
    }

    public String getDatabaseVersion() {
        return getString(6);
    }

    public void setDatabaseVersion(String databaseVersion) {
        addValue(6, parseString(databaseVersion));
    }

    public SplitList<Param> getSearchEngine() {
        return getSplitList(7);
    }

    public boolean addSearchEngineParam(Param param) {
        return getSearchEngine().add(param);
    }

    public boolean addSearchEngineParam(String paramLabel) {
        return addSearchEngineParam(parseParam(paramLabel));
    }

    public void setSearchEngine(SplitList<Param> searchEngine) {
        addValue(7, searchEngine);
    }

    public void setSearchEngine(String searchEngineLabel) {
        setSearchEngine(parseParamList(searchEngineLabel));
    }

    public SplitList<Param> getSearchEngineScore() {
        return getSplitList(8);
    }

    public boolean addSearchEngineSocreParam(Param param) {
        return getSearchEngineScore().add(param);
    }

    public boolean addSearchEngineSocreParam(String paramLabel) {
        return addSearchEngineSocreParam(parseParam(paramLabel));
    }

    public void setSearchEngineScore(SplitList<Param> searchEngineScore) {
        addValue(8, searchEngineScore);
    }

    public void setSearchEngineScore(String searchEngineScoreLabel) {
        setSearchEngineScore(parseParamList(searchEngineScoreLabel));
    }

    public Reliability getReliability() {
        return getReliability(9);
    }

    public void setReliability(Reliability reliability) {
        addValue(9, reliability);
    }

    public void setReliability(String reliabilityLabel) {
        setReliability(Reliability.findReliability(reliabilityLabel));
    }

    public SplitList<Modification> getModifications() {
        return getSplitList(10);
    }

    public boolean addModification(Modification modification) {
        return getModifications().add(modification);
    }

    public void setModifications(SplitList<Modification> modifications) {
        addValue(10, modifications);
    }

    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Peptide, modificationsLabel));
    }

    public SplitList<Double> getRetentionTime() {
        return getSplitList(11);
    }

    public boolean addRetentionTime(Double rt) {
        return getRetentionTime().add(rt);
    }

    public boolean addRetentionTime(String rtLabel) {
        return addRetentionTime(parseDouble(rtLabel));
    }

    public void setRetentionTime(SplitList<Double> retentionTime) {
        addValue(11, retentionTime);
    }

    public void setRetentionTime(String retentionTimeLabel) {
        setRetentionTime(parseDoubleList(retentionTimeLabel));
    }

    public Integer getCharge() {
        return getInteger(12);
    }

    public void setCharge(Integer charge) {
        addValue(12, charge);
    }

    public void setCharge(String chargeLabel) {
        setCharge(parseInteger(chargeLabel));
    }

    public Double getMassToCharge() {
        return getDouble(13);
    }

    public void setMassToCharge(Double massToCharge) {
        addValue(13, massToCharge);
    }

    public void setMassToCharge(String massToChargeLabel) {
        setMassToCharge(parseDouble(massToChargeLabel));
    }

    public URI getURI() {
        return getURI(14);
    }

    public void setURI(URI uri) {
        addValue(14, uri);
    }

    public void setURI(String uriLabel) {
        setURI(parseURI(uriLabel));
    }

    public SplitList<SpecRef> getSpectraRef() {
        return getSplitList(15);
    }

    public boolean addSpectraRef(SpecRef specRef) {
        return getSpectraRef().add(specRef);
    }

    public void setSpectraRef(SplitList<SpecRef> spectraRef) {
        addValue(15, spectraRef);
    }

    public void setSpectraRef(Unit unit, String spectraRefLabel) {
        setSpectraRef(parseSpecRefList(unit, spectraRefLabel));
    }

    /**
     * PEP  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Peptide.getPrefix() + MZTabConstants.TAB + super.toString();
    }
}
