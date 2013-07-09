package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class SmallMolecule extends MZTabRecord {
    private Metadata metadata;

    public SmallMolecule(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Small_Molecule));
        this.metadata = metadata;
    }

    public SmallMolecule(MZTabColumnFactory factory, Metadata metadata) {
        super(factory);
        this.metadata = metadata;
    }

    @SuppressWarnings("unchecked")
    public SplitList<String> getIdentifier() {
        return getSplitList(SmallMoleculeColumn.IDENTIFIER.getOrder());
    }

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

    public void setIdentifier(SplitList<String> identifier) {
        setValue(SmallMoleculeColumn.IDENTIFIER.getOrder(), identifier);
    }

    public void setIdentifier(String identifierLabel) {
        setIdentifier(parseStringList(BAR, identifierLabel));
    }

    public String getChemicalFormula() {
        return getString(SmallMoleculeColumn.CHEMICAL_FORMULA.getOrder());
    }

    public void setChemicalFormula(String chemicalFormula) {
        setValue(SmallMoleculeColumn.CHEMICAL_FORMULA.getOrder(), parseString(chemicalFormula));
    }

    public String getSmiles() {
        return getString(SmallMoleculeColumn.SMILES.getOrder());
    }

    public void setSmiles(String smiles) {
        setValue(SmallMoleculeColumn.SMILES.getOrder(), parseStringList(BAR, smiles));
    }

    public String getInchiKey() {
        return getString(SmallMoleculeColumn.INCHI_KEY.getOrder());
    }

    public void setInchiKey(String inchiKey) {
        setValue(SmallMoleculeColumn.INCHI_KEY.getOrder(), parseStringList(BAR, inchiKey));
    }

    public String getDescription() {
        return getString(SmallMoleculeColumn.DESCRIPTION.getOrder());
    }

    public void setDescription(String description) {
        setValue(SmallMoleculeColumn.DESCRIPTION.getOrder(), parseString(description));
    }

    public Double getExpMassToCharge() {
        return getDouble(SmallMoleculeColumn.EXP_MASS_TO_CHARGE.getOrder());
    }

    public void setExpMassToCharge(Double expMassToCharge) {
        setValue(SmallMoleculeColumn.EXP_MASS_TO_CHARGE.getOrder(), expMassToCharge);
    }

    public void setExpMassToCharge(String expMassToChargeLabel) {
        setExpMassToCharge(parseDouble(expMassToChargeLabel));
    }

    public Double getCalcMassToCharge() {
        return getDouble(SmallMoleculeColumn.CALC_MASS_TO_CHARGE.getOrder());
    }

    public void setCalcMassToCharge(Double calcMassToCharge) {
        setValue(SmallMoleculeColumn.CALC_MASS_TO_CHARGE.getOrder(), calcMassToCharge);
    }

    public void setCalcMassToCharge(String calcMassToChargeLabel) {
        setCalcMassToCharge(parseDouble(calcMassToChargeLabel));
    }

    public Integer getCharge() {
        return getInteger(SmallMoleculeColumn.CHARGE.getOrder());
    }

    public void setCharge(Integer charge) {
        setValue(SmallMoleculeColumn.CHARGE.getOrder(), charge);
    }

    public void setCharge(String chargeLabel) {
        setCharge(parseInteger(chargeLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Double> getRetentionTime() {
        return getSplitList(SmallMoleculeColumn.RETENTION_TIME.getOrder());
    }

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

    public boolean addRetentionTime(String rtLabel) {
        return !isEmpty(rtLabel) && addRetentionTime(parseDouble(rtLabel));
    }

    public void setRetentionTime(SplitList<Double> retentionTime) {
        setValue(SmallMoleculeColumn.RETENTION_TIME.getOrder(), retentionTime);
    }

    public void setRetentionTime(String retentionTimeLabel) {
        setRetentionTime(parseDoubleList(retentionTimeLabel));
    }

    public Integer getTaxid() {
        return getInteger(SmallMoleculeColumn.TAXID.getOrder());
    }

    public void setTaxid(Integer taxid) {
        setValue(SmallMoleculeColumn.TAXID.getOrder(), taxid);
    }

    public void setTaxid(String taxidLabel) {
        setTaxid(parseInteger(taxidLabel));
    }

    public String getSpecies() {
        return getString(SmallMoleculeColumn.SPECIES.getOrder());
    }

    public void setSpecies(String species) {
        setValue(SmallMoleculeColumn.SPECIES.getOrder(), parseString(species));
    }

    public String getDatabase() {
        return getString(SmallMoleculeColumn.DATABASE.getOrder());
    }

    public void setDatabase(String database) {
        setValue(SmallMoleculeColumn.DATABASE.getOrder(), parseString(database));
    }

    public String getDatabaseVersion() {
        return getString(SmallMoleculeColumn.DATABASE_VERSION.getOrder());
    }

    public void setDatabaseVersion(String databaseVersion) {
        setValue(SmallMoleculeColumn.DATABASE_VERSION.getOrder(), parseString(databaseVersion));
    }

    public Reliability getReliability() {
        return getReliability(SmallMoleculeColumn.RELIABILITY.getOrder());
    }

    public void setReliability(Reliability reliability) {
        setValue(SmallMoleculeColumn.RELIABILITY.getOrder(), reliability);
    }

    public void setReliability(String reliabilityLabel) {
        setReliability(Reliability.findReliability(reliabilityLabel));
    }

    public URI getURI() {
        return getURI(SmallMoleculeColumn.URI.getOrder());
    }

    public void setURI(URI uri) {
        setValue(SmallMoleculeColumn.URI.getOrder(), uri);
    }

    public void setURI(String uriLabel) {
        setURI(parseURI(uriLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<SpectraRef> getSpectraRef() {
        return getSplitList(SmallMoleculeColumn.SPECTRA_REF.getOrder());
    }

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

    public void setSpectraRef(SplitList<SpectraRef> spectraRef) {
        setValue(SmallMoleculeColumn.SPECTRA_REF.getOrder(), spectraRef);
    }

    public void setSpectraRef(String spectraRefLabel) {
        setSpectraRef(parseSpectraRefList(metadata, spectraRefLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getSearchEngine() {
        return getSplitList(SmallMoleculeColumn.SEARCH_ENGINE.getOrder());
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

    public void setSearchEngine(SplitList<Param> searchEngine) {
        setValue(SmallMoleculeColumn.SEARCH_ENGINE.getOrder(), searchEngine);
    }

    public void setSearchEngine(String searchEngineLabel) {
        setSearchEngine(parseParamList(searchEngineLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getBestSearchEngineScore() {
        return getSplitList(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE.getOrder());
    }

    public boolean addBestSearchEngineScoreParam(Param param) {
        if (param == null) {
            return false;
        }

        SplitList<Param> params = getBestSearchEngineScore();
        if (params == null) {
            params = new SplitList<Param>(BAR);
            this.setBestSearchEngineScore(params);
        }

        return params.add(param);
    }

    public boolean addBestSearchEngineScoreParam(String paramLabel) {
        return !isEmpty(paramLabel) && addBestSearchEngineScoreParam(parseParam(paramLabel));
    }

    public void setBestSearchEngineScore(SplitList<Param> bestSearchEngineScore) {
        setValue(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE.getOrder(), bestSearchEngineScore);
    }

    public void setBestSearchEngineScore(String bestSearchEngineScoreLabel) {
        this.setBestSearchEngineScore(parseParamList(bestSearchEngineScoreLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getSearchEngineScore(MsRun msRun) {
        return getSplitList(getPosition(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, msRun));
    }

    public void setSearchEngineScore(MsRun msRun, SplitList<Param> searchEngineScore) {
        setValue(getPosition(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, msRun), searchEngineScore);
    }

    public boolean addSearchEngineScore(MsRun msRun, CVParam param) {
        if (param == null) {
            return false;
        }

        SplitList<Param> params = getSearchEngineScore(msRun);
        if (params == null) {
            params = new SplitList<Param>(BAR);
            setSearchEngineScore(msRun, params);
        }
        params.add(param);

        return true;
    }

    public void setSearchEngineScore(MsRun msRun, String paramsLabel) {
        setSearchEngineScore(msRun, parseParamList(paramsLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Modification> getModifications() {
        return getSplitList(SmallMoleculeColumn.MODIFICATIONS.getOrder());
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
        setValue(SmallMoleculeColumn.MODIFICATIONS.getOrder(), modifications);
    }

    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Small_Molecule, modificationsLabel));
    }

    /**
     * SML  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Small_Molecule.getPrefix() + TAB + super.toString();
    }
}
