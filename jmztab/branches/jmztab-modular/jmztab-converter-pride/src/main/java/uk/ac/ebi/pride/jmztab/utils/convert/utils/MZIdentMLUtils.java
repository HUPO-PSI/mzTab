package uk.ac.ebi.pride.jmztab.utils.convert.utils;

import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.pride.jmztab.utils.convert.SearchEngineParam;
import uk.ac.ebi.pride.jmztab.utils.convert.SearchEngineScoreParam;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by yperez on 13/06/2014.
 */
public final class MZIdentMLUtils {

    public static String CVTERM_MAIL = "MS:1000589";
    public static String CVTERM_PROTEIN_DESCRIPTION = "MS:1001088";
    public static String CVTERM_NEUTRAL_LOST = "MS:1001524";
    public static String OPTIONAL_ID_COLUMN  = "mzidentml_original_ID";
    public static String OPTIONAL_SEQUENCE_COLUMN = "protein_sequence";

    /** algebraic sign */
    private static final String SIGN = "[+-]";

    /** integer expression */
    public static final String INTEGER = SIGN + "?\\d+";


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

    public static SpecFileFormat getSpecFileFormat(String fileFormat) {
        if (fileFormat != null && fileFormat.length() > 0) {
            if (SpecFileFormat.MZXML.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.MZXML;
            if (SpecFileFormat.DTA.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.DTA;
            if (SpecFileFormat.MGF.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.MGF;
            if (SpecFileFormat.MZDATA.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.MZDATA;
            if (SpecFileFormat.MZML.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.MZML;
            if (SpecFileFormat.PKL.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.PKL;
        }
        return SpecFileFormat.NONE;
    }

    /**
     * Search and find a list of search engine types from input parameter group.
     *
     * @return List<SearchEngineType>  a list of search engine
     */
    public static List<SearchEngineScoreParam> getSearchEngineScoreTypes(List<CvParam> cvParams) {
        if (cvParams == null) {
            throw new IllegalArgumentException("Input argument for getSearchEngineScoreTypes can not be null");
        }
        List<SearchEngineScoreParam> scores = new ArrayList<SearchEngineScoreParam>();
        for(CvParam param: cvParams)
          if(SearchEngineScoreParam.getSearchEngineScoreParamByAccession(param.getAccession()) != null)
              scores.add(SearchEngineScoreParam.getSearchEngineScoreParamByAccession(param.getAccession()));

        return scores;
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


    /**
     * Get cv param by accession number and cv label.
     * This method tries to find the CvParam for the given accession and cvLabel.
     * IMPORTANT NOTE: As the cvLabel may not always be present, the method will
     * assume a valid match if the accession alone matches.
     * <p/>
     * ToDo: perhaps separate method without cvLabel would be better (then this one could fail if no cvLabel was found)
     *
     * @param cvLabel    cv label.
     * @param accession  cv accession.
     * @return CvParam  cv param.
     */
    public static List<CvParam> getCvParam(List<CvParam> cvParams, String cvLabel, String accession) {
        if (cvParams == null || cvLabel == null || accession == null) {
            throw new IllegalArgumentException("Input arguments for getCvParam can not be null");
        }

        List<CvParam> cps = new ArrayList<CvParam>();
        for (CvParam param : cvParams) {
            if (param.getAccession().equalsIgnoreCase(accession)) {
//                if (param.getCvLookupID() != null && !param.getCvLookupID().equalsIgnoreCase(cvLabel)) {
//                    // this could be the wrong CV param!!
//                    logger.warn("We may have got the wrong CV param: " + param.toString() + " compare to cvLabel: [" + cvLabel + "] accession: [" + accession + "]");
//                    // ToDo: proper logging (should perhaps fail, see comment above)
//                }
                cps.add(param);
            }
        }
        return cps;
    }

    public static List<SearchEngineScoreParam> getSearchEngineScoreTerm(List<CvParam> params) {
        List<SearchEngineScoreParam> scores = new ArrayList<SearchEngineScoreParam>();
        if (params != null)
            for (CvParam term : params)
              if(SearchEngineScoreParam.getSearchEngineScoreParamByAccession(term.getAccession()) != null)
                  scores.add(SearchEngineScoreParam.getSearchEngineScoreParamByAccession(term.getAccession()));
        return scores;
    }

    public static SpecIdFormat getSpectraDataIdFormat(uk.ac.ebi.jmzidml.model.mzidml.SpectraData spectraData) {
        CvParam specIdFormat = spectraData.getSpectrumIDFormat().getCvParam();
        return getSpectraDataIdFormat(specIdFormat.getAccession());
    }

    private static SpecIdFormat getSpectraDataIdFormat(String accession) {
        if (accession.equals("MS:1001528"))
            return SpecIdFormat.MASCOT_QUERY_NUM;
        if (accession.equals("MS:1000774"))
            return SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID;
        if (accession.equals("MS:1000775"))
            return SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID;
        if (accession.equals("MS:1001530"))
            return SpecIdFormat.MZML_ID;
        if (accession.equals("MS:1000776"))
            return SpecIdFormat.SCAN_NUMBER_NATIVE_ID;
        if (accession.equals("MS:1000770"))
            return SpecIdFormat.WIFF_NATIVE_ID;
        if (accession.equals("MS:1000777"))
            return SpecIdFormat.MZDATA_ID;
        if(accession.equals(("MS:1000768")))
            return SpecIdFormat.SPECTRUM_NATIVE_ID;
        return SpecIdFormat.NONE;
    }

    public static String getSpectrumId(uk.ac.ebi.jmzidml.model.mzidml.SpectraData spectraData, String spectrumID) {
        SpecIdFormat fileIdFormat = getSpectraDataIdFormat(spectraData);

        if (fileIdFormat == SpecIdFormat.MASCOT_QUERY_NUM) {
            String rValueStr = spectrumID.replaceAll("query=", "");
            String id = null;
            if(rValueStr.matches(INTEGER)){
                id = Integer.toString(Integer.parseInt(rValueStr) + 1);
            }
            return id;
        } else if (fileIdFormat == SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID) {
            String rValueStr = spectrumID.replaceAll("index=", "");
            String id = null;
            if(rValueStr.matches(INTEGER)){
                id = Integer.toString(Integer.parseInt(rValueStr) + 1);
            }
            return id;
        } else if (fileIdFormat == SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID) {
            return spectrumID.replaceAll("file=", "");
        } else if (fileIdFormat == SpecIdFormat.MZML_ID) {
            return spectrumID.replaceAll("mzMLid=", "");
        } else if (fileIdFormat == SpecIdFormat.SCAN_NUMBER_NATIVE_ID) {
            return spectrumID.replaceAll("scan=", "");
        } else {
            return spectrumID;
        }
    }
}
