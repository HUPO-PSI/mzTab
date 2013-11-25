package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.CVParam;
import uk.ac.ebi.pride.jmztab.model.MZTabUtils;

/**
 * User: qingwei
 * Date: 25/11/13
 */
public class SearchEngineScoreParam extends CVParam {
    private SearchEngineParam searchEngineParam;

    private SearchEngineScoreParam(SearchEngineParam searchEngineParam, String cvLabel, String accession, String name, String score) {
        super(cvLabel, accession, name, score);
        this.searchEngineParam = searchEngineParam;
    }

    public static SearchEngineScoreParam MASCOT_SCORE = new SearchEngineScoreParam(SearchEngineParam.MASCOT, "MS", "MS:1001171", "Mascot:score", null);
    public static SearchEngineScoreParam XTANDEM_HYPERSCORE = new SearchEngineScoreParam(SearchEngineParam.XTANDEM, "MS", "MS:1001331", "X!Tandem:hyperscore", null);
    public static SearchEngineScoreParam XTANDEM_EXPECT = new SearchEngineScoreParam(SearchEngineParam.XTANDEM, "MS", "MS:1001330", "X!Tandem:expect", null);
    public static SearchEngineScoreParam SEQUEST_SCORE = new SearchEngineScoreParam(SearchEngineParam.SEQUEST, "MS", "MS:1001163", "Sequest:consensus score", null);
    public static SearchEngineScoreParam SEQUEST_XCORR = new SearchEngineScoreParam(SearchEngineParam.SEQUEST, "MS", "MS:1001155", "Sequest:xcorr", null);
    public static SearchEngineScoreParam SEQUEST_DELTACN = new SearchEngineScoreParam(SearchEngineParam.SEQUEST, "MS", "MS:1001156", "Sequest:deltacn", null);
    public static SearchEngineScoreParam SPECTRUMMILL_SCORE = new SearchEngineScoreParam(SearchEngineParam.SPECTRUM_MILL_PEPTIDE, "MS", "MS:1001572", "SpectrumMill:Score", null);
    public static SearchEngineScoreParam OMSSA_EVALUE = new SearchEngineScoreParam(SearchEngineParam.OMSSA, "MS", "MS:1001328", "OMSSA:evalue", null);
    public static SearchEngineScoreParam OMSSA_PVALUE = new SearchEngineScoreParam(SearchEngineParam.OMSSA, "MS", "MS:1001329", "OMSSA:pvalue", null);

    public SearchEngineScoreParam setScore(String score) {
        if (MZTabUtils.isEmpty(score)) {
            throw new IllegalArgumentException("Score can not empty!");
        }

        return new SearchEngineScoreParam(searchEngineParam, getCvLabel(), getAccession(), getName(), score);
    }

    public SearchEngineParam getSearchEngineParam() {
        return searchEngineParam;
    }


}
