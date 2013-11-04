package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.BAR;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.COMMA;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseURI;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class Peptide extends MZTabRecord {
    private Metadata metadata;

    public Peptide(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Peptide));
        this.metadata = metadata;
    }

    public Peptide(MZTabColumnFactory factory, Metadata metadata) {
        super(factory);
        this.metadata = metadata;
    }

    public String getSequence() {
        return getString(PeptideColumn.SEQUENCE.getOrder());
    }

    public void setSequence(String sequence) {
        setValue(PeptideColumn.SEQUENCE.getOrder(), parseString(sequence));
    }

    public String getAccession() {
        return getString(PeptideColumn.ACCESSION.getOrder());
    }

    public void setAccession(String accession) {
        setValue(PeptideColumn.ACCESSION.getOrder(), parseString(accession));
    }

    public MZBoolean getUnique() {
        return getMZBoolean(PeptideColumn.UNIQUE.getOrder());
    }

    public void setUnique(MZBoolean unique) {
        setValue(PeptideColumn.UNIQUE.getOrder(), unique);
    }

    public void setUnique(String uniqueLabel) {
        setUnique(MZBoolean.findBoolean(uniqueLabel));
    }

    public String getDatabase() {
        return getString(PeptideColumn.DATABASE.getOrder());
    }

    public void setDatabase(String database) {
        setValue(PeptideColumn.DATABASE.getOrder(), parseString(database));
    }

    public String getDatabaseVersion() {
        return getString(PeptideColumn.DATABASE_VERSION.getOrder());
    }

    public void setDatabaseVersion(String databaseVersion) {
        setValue(PeptideColumn.DATABASE_VERSION.getOrder(), parseString(databaseVersion));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getSearchEngine() {
        return getSplitList(PeptideColumn.SEARCH_ENGINE.getOrder());
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
        setValue(PeptideColumn.SEARCH_ENGINE.getOrder(), searchEngine);
    }

    public void setSearchEngine(String searchEngineLabel) {
        setSearchEngine(parseParamList(searchEngineLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getBestSearchEngineScore() {
        return getSplitList(PeptideColumn.BEST_SEARCH_ENGINE_SCORE.getOrder());
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
        setValue(PeptideColumn.BEST_SEARCH_ENGINE_SCORE.getOrder(), bestSearchEngineScore);
    }

    public void setBestSearchEngineScore(String bestSearchEngineScoreLabel) {
        this.setBestSearchEngineScore(parseParamList(bestSearchEngineScoreLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Param> getSearchEngineScore(MsRun msRun) {
        return getSplitList(getPosition(PeptideColumn.SEARCH_ENGINE_SCORE, msRun));
    }

    public void setSearchEngineScore(String logicalPosition, SplitList<Param> searchEngineScore) {
        setValue(logicalPosition, searchEngineScore);
    }

    public void setSearchEngineScore(MsRun msRun, SplitList<Param> searchEngineScore) {
        setSearchEngineScore(getPosition(PeptideColumn.SEARCH_ENGINE_SCORE, msRun), searchEngineScore);
    }

    public boolean addSearchEngineScoreParam(MsRun msRun, CVParam param) {
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

    public void setSearchEngineScore(String logicalPosition, String paramsLabel) {
        setSearchEngineScore(logicalPosition, parseParamList(paramsLabel));
    }

    public void setSearchEngineScore(MsRun msRun, String paramsLabel) {
        setSearchEngineScore(msRun, parseParamList(paramsLabel));
    }

    public Reliability getReliability() {
        return getReliability(PeptideColumn.RELIABILITY.getOrder());
    }

    public void setReliability(Reliability reliability) {
        setValue(PeptideColumn.RELIABILITY.getOrder(), reliability);
    }

    public void setReliability(String reliabilityLabel) {
        setReliability(Reliability.findReliability(reliabilityLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Modification> getModifications() {
        return getSplitList(PeptideColumn.MODIFICATIONS.getOrder());
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
        setValue(PeptideColumn.MODIFICATIONS.getOrder(), modifications);
    }

    public void setModifications(String modificationsLabel) {
        setModifications(parseModificationList(Section.Peptide, modificationsLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Double> getRetentionTime() {
        return getSplitList(PeptideColumn.RETENTION_TIME.getOrder());
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
        setValue(PeptideColumn.RETENTION_TIME.getOrder(), retentionTime);
    }

    public void setRetentionTime(String retentionTimeLabel) {
        setRetentionTime(parseDoubleList(retentionTimeLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<Double> getRetentionTimeWindow() {
        return getSplitList(PeptideColumn.RETENTION_TIME_WINDOW.getOrder());
    }

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

    public boolean addRetentionTimeWindow(String rtwLabel) {
        return !isEmpty(rtwLabel) && addRetentionTimeWindow(parseDouble(rtwLabel));
    }

    public void setRetentionTimeWindow(SplitList<Double> retentionTimeWindow) {
        setValue(PeptideColumn.RETENTION_TIME_WINDOW.getOrder(), retentionTimeWindow);
    }

    public void setRetentionTimeWindow(String retentionTimeWindowLabel) {
        setRetentionTimeWindow(parseDoubleList(retentionTimeWindowLabel));
    }

    public Integer getCharge() {
        return getInteger(PeptideColumn.CHARGE.getOrder());
    }

    public void setCharge(Integer charge) {
        setValue(PeptideColumn.CHARGE.getOrder(), charge);
    }

    public void setCharge(String chargeLabel) {
        setCharge(parseInteger(chargeLabel));
    }

    public Double getMassToCharge() {
        return getDouble(PeptideColumn.MASS_TO_CHARGE.getOrder());
    }

    public void setMassToCharge(Double massToCharge) {
        setValue(PeptideColumn.MASS_TO_CHARGE.getOrder(), massToCharge);
    }

    public void setMassToCharge(String massToChargeLabel) {
        setMassToCharge(parseDouble(massToChargeLabel));
    }

    public URI getURI() {
        return getURI(PeptideColumn.URI.getOrder());
    }

    public void setURI(URI uri) {
        setValue(PeptideColumn.URI.getOrder(), uri);
    }

    public void setURI(String uriLabel) {
        setURI(parseURI(uriLabel));
    }

    @SuppressWarnings("unchecked")
    public SplitList<SpectraRef> getSpectraRef() {
        return getSplitList(PeptideColumn.SPECTRA_REF.getOrder());
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
        setValue(PeptideColumn.SPECTRA_REF.getOrder(), spectraRef);
    }

    public void setSpectraRef(String spectraRefLabel) {
        setSpectraRef(parseSpectraRefList(metadata, spectraRefLabel));
    }

    /**
     * PEP  value1  value2  value3  ...
     */
    @Override
    public String toString() {
        return Section.Peptide.getPrefix() + TAB + super.toString();
    }
}
