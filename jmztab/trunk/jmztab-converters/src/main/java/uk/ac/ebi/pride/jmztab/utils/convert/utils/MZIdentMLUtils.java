package uk.ac.ebi.pride.jmztab.utils.convert.utils;

import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.pride.jmztab.utils.convert.SearchEngineParam;
import uk.ac.ebi.pride.jmztab.utils.convert.SearchEngineScoreParam;

import java.util.ArrayList;
import java.util.List;

/**
 * MzIdentML utilities to convert mzIdentML files to mzTab.
 *  <p/>
 * @author yperez
 * @since 13/06/2014
 */
public final class MZIdentMLUtils {

    public static final String CVTERM_MAIL = "MS:1000589";
    public static final String CVTERM_PROTEIN_DESCRIPTION = "MS:1001088";
    public static final String CVTERM_NEUTRAL_LOST = "MS:1001524";
    public static final String OPTIONAL_ID_COLUMN  = "mzidentml_original_ID";
    public static final String OPTIONAL_SEQUENCE_COLUMN = "protein_sequence";
    public static final String[] CVTERMS_FDR_PSM = {"MS:1002260", "MS:1002350", "MS:1002351"};
    public static final String[] CVTERMS_FDR_PROTEIN = {"MS:1001447", "MS:1002364", "MS:1001214"};
    public static final String OPTIONAL_DECOY_COLUMN = "cv_MS:1002217_decoy_peptide";
    public static final String PEPTIDE_N_TERM = "MS:1001189";
    public static final String PROTEIN_N_TERM = "MS:1002057";
    public static final String PEPTIDE_C_TERM = "MS:1001190";
    public static final String PROTEIN_C_TERM = "MS:1002058";
    public static final String UNKNOWN_MOD = "MS:1001460";
    public static final String CHEMMOD = "CHEMMOD";
    public static final String UNKNOWN_MODIFICATION = "unknown modification";



    /** algebraic sign */
    private static final String SIGN = "[+-]";

    /** integer expression */
    private static final String INTEGER = SIGN + "?\\d+";


    /**
     * Supported id format used in the spectrum file.
     */
    public static enum SpecIdFormat {
        MASCOT_QUERY_NUM,
        MULTI_PEAK_LIST_NATIVE_ID,
        SINGLE_PEAK_LIST_NATIVE_ID,
        SCAN_NUMBER_NATIVE_ID,
        MZML_ID,
        MZDATA_ID,
        WIFF_NATIVE_ID,
        SPECTRUM_NATIVE_ID,
        NONE
    }

    /**
     * An enum of the supported spectra file types
     */
    public static enum SpecFileFormat {
        MZML,
        PKL,
        DTA,
        MGF,
        MZXML,
        MZDATA,
        NONE
    }

    /**
     * Search and find a list of search engine types from input parameter group.
     *
     * @return List<SearchEngineType>  a list of search engine
     */
    public static List<SearchEngineParam> getSearchEngineTypes(List<CvParam> cvParams) {
        if (cvParams == null) {
            throw new IllegalArgumentException("Input argument for getSearchEngineScoreTypes can not be null");
        }
        List<SearchEngineParam> searchEngines = new ArrayList<SearchEngineParam>();
        for(CvParam param: cvParams)
            if(SearchEngineScoreParam.getSearchEngineScoreParamByAccession(param.getAccession()) != null){
                SearchEngineScoreParam searchEngineScoreParam = SearchEngineScoreParam.getSearchEngineScoreParamByAccession(param.getAccession());
                SearchEngineParam seachEngine = searchEngineScoreParam.getSearchEngineParam();
                searchEngines.add(seachEngine);
            }

        return searchEngines;
    }


    public static List<SearchEngineScoreParam> getSearchEngineScoreTerm(List<CvParam> params) {
        List<SearchEngineScoreParam> scores = new ArrayList<SearchEngineScoreParam>();
        if (params != null)
            for (CvParam term : params)
                if (SearchEngineScoreParam.getSearchEngineScoreParamByAccession(term.getAccession()) != null)
                    scores.add(SearchEngineScoreParam.getSearchEngineScoreParamByAccession(term.getAccession()));
        return scores;
    }

//    private static SpecIdFormat getSpectraDataIdFormat(uk.ac.ebi.jmzidml.model.mzidml.SpectraData spectraData) {
//        CvParam specIdFormat = spectraData.getSpectrumIDFormat().getCvParam();
//        return getSpectraDataIdFormat(specIdFormat.getAccession());
//    }

//    private static SpecIdFormat getSpectraDataIdFormat(String accession) {
//        if (accession.equals("MS:1001528"))
//            return SpecIdFormat.MASCOT_QUERY_NUM;
//        if (accession.equals("MS:1000774"))
//            return SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID;
//        if (accession.equals("MS:1000775"))
//            return SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID;
//        if (accession.equals("MS:1001530"))
//            return SpecIdFormat.MZML_ID;
//        if (accession.equals("MS:1000776"))
//            return SpecIdFormat.SCAN_NUMBER_NATIVE_ID;
//        if (accession.equals("MS:1000770"))
//            return SpecIdFormat.WIFF_NATIVE_ID;
//        if (accession.equals("MS:1000777"))
//            return SpecIdFormat.MZDATA_ID;
//        if(accession.equals(("MS:1000768")))
//            return SpecIdFormat.SPECTRUM_NATIVE_ID;
//        return SpecIdFormat.NONE;
//    }

//    public static String getSpectrumId(uk.ac.ebi.jmzidml.model.mzidml.SpectraData spectraData, String spectrumID) {
//        SpecIdFormat fileIdFormat = getSpectraDataIdFormat(spectraData);
//
//        if (fileIdFormat == SpecIdFormat.MASCOT_QUERY_NUM) {
//            String rValueStr = spectrumID.replaceAll("query=", "");
//            String id = null;
//            if(rValueStr.matches(INTEGER)){
//                id = Integer.toString(Integer.parseInt(rValueStr) + 1);
//            }
//            return id;
//        } else if (fileIdFormat == SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID) {
//            String rValueStr = spectrumID.replaceAll("index=", "");
//            String id = null;
//            if(rValueStr.matches(INTEGER)){
//                id = Integer.toString(Integer.parseInt(rValueStr) + 1);
//            }
//            return id;
//        } else if (fileIdFormat == SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID) {
//            return spectrumID.replaceAll("file=", "");
//        } else if (fileIdFormat == SpecIdFormat.MZML_ID) {
//            return spectrumID.replaceAll("mzMLid=", "");
//        } else if (fileIdFormat == SpecIdFormat.SCAN_NUMBER_NATIVE_ID) {
//            return spectrumID.replaceAll("scan=", "");
//        } else {
//            return spectrumID;
//        }
//    }
}
