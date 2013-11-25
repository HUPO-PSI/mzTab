package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.CVParam;

/**
 * User: qingwei
 * Date: 25/11/13
 */
public class SearchEngineParam extends CVParam {
    private SearchEngineParam(String cvLabel, String accession, String name, String value) {
        super(cvLabel, accession, name, value);
    }

    public static SearchEngineParam MASCOT =  new SearchEngineParam("MS", "MS:1001207", "Mascot", null);
    public static SearchEngineParam XTANDEM =  new SearchEngineParam("MS", "MS:1001476", "X!Tandem", null);
    public static SearchEngineParam SEQUEST =  new SearchEngineParam("MS", "MS:1001208", "SEQUEST", null);
    public static SearchEngineParam SPECTRUM_MILL_PEPTIDE =  new SearchEngineParam("MS", "MS:1000687", "Spectrum Mill for MassHunter Workstation", null);
    public static SearchEngineParam OMSSA =  new SearchEngineParam("MS", "MS:1001475", "OMSSA", null);
    public static SearchEngineParam SPECTRA_ST =  new SearchEngineParam("MS", "MS:1001477", "SpectaST", null);

    /**
     * Tries to guess which search engine is described by the passed name. In case no matching parameter
     * is found, null is returned.
     */
    public static SearchEngineParam findParam(String searchEngineName) {
        if (searchEngineName == null) {
            return null;
        }

        searchEngineName = searchEngineName.toLowerCase();

        SearchEngineParam param = null;
        if (searchEngineName.contains("mascot")) {
            param = MASCOT;
        } else if (searchEngineName.contains("omssa")) {
            param = OMSSA;
        } else if (searchEngineName.contains("sequest")) {
            param = SEQUEST;
        } else if (searchEngineName.contains("spectrummill") || searchEngineName.contains("spectrum_mill")) {
            param = SPECTRUM_MILL_PEPTIDE;
        } else if (searchEngineName.contains("spectrast")) {
            param = SPECTRA_ST;
        } else if (searchEngineName.contains("xtandem") || searchEngineName.contains("x!tandem")) {
            param = XTANDEM;
        }

        return param;
    }
}
