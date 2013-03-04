package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * User: Qingwei, Johannes Griss
 * Date: 30/01/13
 */
public class SmallMolecule extends AbstractMZTabRecord {
    public SmallMolecule() {
        super(MZTabColumnFactory.getInstance(Section.Small_Molecule));
    }

    public SmallMolecule(MZTabColumnFactory factory) {
        super(factory);
    }

    public SplitList<String> getIdentifier() {
        return getSplitList(1);
    }

    public boolean addIdentifier(String identifier) {
        return getIdentifier().add(identifier);
    }

    public void setIdentifier(SplitList<String> identifier) {
        addValue(1, identifier);
    }

    public void setIdentifier(String identifierLabel) {
        setIdentifier(parseStringList(MZTabConstants.BAR, identifierLabel));
    }

    public String getUnitId() {
        return getString(2);
    }

    public void setUnitId(String unitId) {
        addValue(2, parseString(unitId));
    }

    public String getChemicalFormula() {
        return getString(3);
    }

    public void setChemicalFormula(String chemicalFormula) {
        addValue(3, parseString(chemicalFormula));
    }

    public String getSmiles() {
        return getString(4);
    }

    public void setSmiles(String smiles) {
        addValue(4, parseString(smiles));
    }

    public String getInchiKey() {
        return getString(5);
    }

    public void setInchiKey(String inchiKey) {
        addValue(5, parseString(inchiKey));
    }

    public String getDescription() {
        return getString(6);
    }

    public void setDescription(String description) {
        addValue(6, parseString(description));
    }

    public Double getMassToCharge() {
        return getDouble(7);
    }

    public void setMassToCharge(Double massToCharge) {
        addValue(7, massToCharge);
    }

    public void setMassToCharge(String massToChargeLabel) {
        setMassToCharge(parseDouble(massToChargeLabel));
    }

    public Integer getCharge() {
        return getInteger(8);
    }

    public void setCharge(Integer charge) {
        addValue(8, charge);
    }

    public void setCharge(String chargeLabel) {
        setCharge(parseInteger(chargeLabel));
    }

    public SplitList<Double> getRetentionTime() {
        return getSplitList(9);
    }

    public boolean addRetentionTime(Double rt) {
        return getRetentionTime().add(rt);
    }

    public boolean addRetentionTime(String rtLabel) {
        return addRetentionTime(parseDouble(rtLabel));
    }

    public void setRetentionTime(SplitList<Double> retentionTime) {
        addValue(9, retentionTime);
    }

    public void setRetentionTime(String retentionTimeLabel) {
        setRetentionTime(parseDoubleList(retentionTimeLabel));
    }

    public Integer getTaxid() {
        return getInteger(10);
    }

    public void setTaxid(Integer taxid) {
        addValue(10, taxid);
    }

    public void setTaxid(String taxidLabel) {
        setTaxid(parseInteger(taxidLabel));
    }

    public String getSpecies() {
        return getString(11);
    }

    public void setSpecies(String species) {
        addValue(11, parseString(species));
    }

    public String getDatabase() {
        return getString(12);
    }

    public void setDatabase(String database) {
        addValue(12, parseString(database));
    }

    public String getDatabaseVersion() {
        return getString(13);
    }

    public void setDatabaseVersion(String databaseVersion) {
        addValue(13, parseString(databaseVersion));
    }

    public Reliability getReliability() {
        return getReliability(14);
    }

    public void setReliability(Reliability reliability) {
        addValue(14, reliability);
    }

    public void setReliability(String reliabilityLabel) {
        setReliability(Reliability.findReliability(reliabilityLabel));
    }

    public URI getURI() {
        return getURI(15);
    }

    public void setURI(URI uri) {
        addValue(15, uri);
    }

    public void setURI(String uriLabel) {
        setURI(parseURI(uriLabel));
    }

    public SplitList<SpecRef> getSpectraRef() {
        return getSplitList(16);
    }

    public boolean addSpectraRef(SpecRef specRef) {
        return getSpectraRef().add(specRef);
    }

    public void setSpectraRef(SplitList<SpecRef> spectraRef) {
        addValue(16, spectraRef);
    }

    public void setSpectraRef(Unit unit, String spectraRef) {
        setSpectraRef(parseSpecRefList(unit, spectraRef));
    }

    public SplitList<Param> getSearchEngine() {
        return getSplitList(17);
    }

    public boolean addSearchEngineParam(Param param) {
        return getSearchEngine().add(param);
    }

    public boolean addSearchEngineParam(String paramLabel) {
        return addSearchEngineParam(parseParam(paramLabel));
    }

    public void setSearchEngine(SplitList<Param> searchEngine) {
        addValue(17, searchEngine);
    }

    public void setSearchEngine(String searchEngineLabel) {
        setSearchEngine(parseParamList(searchEngineLabel));
    }

    public SplitList<Param> getSearchEngineScore() {
        return getSplitList(18);
    }

    public boolean addSearchEngineSocreParam(Param param) {
        return getSearchEngineScore().add(param);
    }

    public boolean addSearchEngineSocreParam(String paramLabel) {
        return addSearchEngineSocreParam(parseParam(paramLabel));
    }

    public void setSearchEngineScore(SplitList<Param> searchEngineScore) {
        addValue(18, searchEngineScore);
    }

    public void setSearchEngineScore(String searchEngineScoreLabel) {
        setSearchEngineScore(parseParamList(searchEngineScoreLabel));
    }

    public SplitList<Modification> getModifications() {
        return getSplitList(19);
    }

    public boolean addModification(Modification modification) {
        return getModifications().add(modification);
    }

    public void setModifications(SplitList<Modification> modifications) {
        addValue(19, modifications);
    }

    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Small_Molecule, modificationsLabel));
    }

    /**
     * SML  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Small_Molecule.getPrefix() + MZTabConstants.TAB + super.toString();
    }
}
