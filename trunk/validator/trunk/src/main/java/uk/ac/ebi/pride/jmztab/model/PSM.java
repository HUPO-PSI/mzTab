package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.BAR;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.COMMA;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class PSM extends MZTabRecord {
    private Metadata metadata;

    public PSM(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Peptide));
        this.metadata = metadata;
    }

    public PSM(MZTabColumnFactory factory, Metadata metadata) {
        super(factory);
        this.metadata = metadata;
    }

    public String getSequence() {
        return getString(PSMColumn.SEQUENCE.getOrder());
    }

    public void setSequence(String sequence) {
        setValue(PSMColumn.SEQUENCE.getOrder(), parseString(sequence));
    }

    public String getPSM_ID() {
        return getString(PSMColumn.PSM_ID.getOrder());
    }

    public void setPSM_ID(Integer psmId) {
        setValue(PSMColumn.PSM_ID.getOrder(), psmId);
    }

    public void setPSM_ID(String psmIdLabel) {
        setValue(PSMColumn.PSM_ID.getOrder(), parseInteger(psmIdLabel));
    }

    public String getAccession() {
        return getString(PSMColumn.ACCESSION.getOrder());
    }

    public void setAccession(String accession) {
        setValue(PSMColumn.ACCESSION.getOrder(), parseString(accession));
    }

    public MZBoolean getUnique() {
        return getMZBoolean(PSMColumn.UNIQUE.getOrder());
    }

    public void setUnique(MZBoolean unique) {
        setValue(PSMColumn.UNIQUE.getOrder(), unique);
    }

    public void setUnique(String uniqueLabel) {
        setUnique(MZBoolean.findBoolean(uniqueLabel));
    }

    public String getDatabase() {
        return getString(PSMColumn.DATABASE.getOrder());
    }

    public void setDatabase(String database) {
        setValue(PSMColumn.DATABASE.getOrder(), parseString(database));
    }

    public String getDatabaseVersion() {
        return getString(PSMColumn.DATABASE_VERSION.getOrder());
    }

    public void setDatabaseVersion(String databaseVersion) {
        setValue(PSMColumn.DATABASE_VERSION.getOrder(), parseString(databaseVersion));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getSearchEngine() {
        return getSplitList(PSMColumn.SEARCH_ENGINE.getOrder());
    }

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

    public boolean addSearchEngineParam(String paramLabel) {
        return !isEmpty(paramLabel) && addSearchEngineParam(parseParam(paramLabel));
    }

    public void setSearchEngine(SplitList<Param> searchEngine) {
        setValue(PSMColumn.SEARCH_ENGINE.getOrder(), searchEngine);
    }

    public void setSearchEngine(String searchEngineLabel) {
        setSearchEngine(parseParamList(searchEngineLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getSearchEngineScore() {
        return getSplitList(PSMColumn.SEARCH_ENGINE_SCORE.getOrder());
    }

    public boolean addSearchEngineScoreParam(Param param) {
        if (param == null) {
            return false;
        }

        SplitList<Param> params = getSearchEngineScore();
        if (params == null) {
            params = new SplitList<Param>(BAR);
            this.setSearchEngineScore(params);
        }

        return params.add(param);
    }

    public boolean addSearchEngineScoreParam(String paramLabel) {
        return !isEmpty(paramLabel) && addSearchEngineScoreParam(parseParam(paramLabel));
    }

    public void setSearchEngineScore(SplitList<Param> searchEngineScore) {
        setValue(PSMColumn.SEARCH_ENGINE_SCORE.getOrder(), searchEngineScore);
    }

    public void setSearchEngineScore(String searchEngineScoreLabel) {
        this.setSearchEngineScore(parseParamList(searchEngineScoreLabel));
    }

    public Reliability getReliability() {
        return getReliability(PSMColumn.RELIABILITY.getOrder());
    }

    public void setReliability(Reliability reliability) {
        setValue(PSMColumn.RELIABILITY.getOrder(), reliability);
    }

    public void setReliability(String reliabilityLabel) {
        setReliability(Reliability.findReliability(reliabilityLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Modification> getModifications() {
        return getSplitList(PSMColumn.MODIFICATIONS.getOrder());
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
        setValue(PSMColumn.MODIFICATIONS.getOrder(), modifications);
    }

    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Peptide, modificationsLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Double> getRetentionTime() {
        return getSplitList(PSMColumn.RETENTION_TIME.getOrder());
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
        setValue(PSMColumn.RETENTION_TIME.getOrder(), retentionTime);
    }

    public void setRetentionTime(String retentionTimeLabel) {
        setRetentionTime(parseDoubleList(retentionTimeLabel));
    }

    public Integer getCharge() {
        return getInteger(PSMColumn.CHARGE.getOrder());
    }

    public void setCharge(Integer charge) {
        setValue(PSMColumn.CHARGE.getOrder(), charge);
    }

    public void setCharge(String chargeLabel) {
        setCharge(parseInteger(chargeLabel));
    }

    public Double getExpMassToCharge() {
        return getDouble(PSMColumn.EXP_MASS_TO_CHARGE.getOrder());
    }

    public void setExpMassToCharge(Double expMassToCharge) {
        setValue(PSMColumn.EXP_MASS_TO_CHARGE.getOrder(), expMassToCharge);
    }

    public void setExpMassToCharge(String expMassToChargeLabel) {
        setExpMassToCharge(parseDouble(expMassToChargeLabel));
    }

    public Double getCalcMassToCharge() {
        return getDouble(PSMColumn.CALC_MASS_TO_CHARGE.getOrder());
    }

    public void setCalcMassToCharge(Double calcMassToCharge) {
        setValue(PSMColumn.CALC_MASS_TO_CHARGE.getOrder(), calcMassToCharge);
    }

    public void setCalcMassToCharge(String calcMassToChargeLabel) {
        setCalcMassToCharge(parseDouble(calcMassToChargeLabel));
    }

    public URI getURI() {
        return getURI(PSMColumn.URI.getOrder());
    }

    public void setURI(URI uri) {
        setValue(PSMColumn.URI.getOrder(), uri);
    }

    public void setURI(String uriLabel) {
        setURI(parseURI(uriLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<SpectraRef> getSpectraRef() {
        return getSplitList(PSMColumn.SPECTRA_REF.getOrder());
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
        setValue(PSMColumn.SPECTRA_REF.getOrder(), spectraRef);
    }

    public void setSpectraRef(String spectraRefLabel) {
        setSpectraRef(parseSpectraRefList(metadata, spectraRefLabel));
    }

    public String getPre() {
        return getString(PSMColumn.PRE.getOrder());
    }

    public void setPre(String pre) {
        setValue(PSMColumn.PRE.getOrder(), parseString(pre));
    }

    public String getPost() {
        return getString(PSMColumn.POST.getOrder());
    }

    public void setPost(String post) {
        setValue(PSMColumn.POST.getOrder(), parseString(post));
    }

    public String getStart() {
        return getString(PSMColumn.START.getOrder());
    }

    public void setStart(String start) {
        setValue(PSMColumn.START.getOrder(), parseString(start));
    }

    public String getEnd() {
        return getString(PSMColumn.END.getOrder());
    }

    public void setEnd(String end) {
        setValue(PSMColumn.END.getOrder(), parseString(end));
    }

    /**
     * PEP  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.PSM.getPrefix() + TAB + super.toString();
    }
}
