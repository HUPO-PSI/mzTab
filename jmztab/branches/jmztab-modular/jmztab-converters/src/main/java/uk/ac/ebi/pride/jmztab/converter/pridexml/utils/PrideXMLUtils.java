package uk.ac.ebi.pride.jmztab.converter.pridexml.utils;

import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.jmztab.converter.utils.cv.ConverterCVParam;
import uk.ac.ebi.pride.jmztab.model.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;

/**
 * User: ntoro
 * Date: 24/07/2014
 * Time: 14:41
 */
public final class PrideXMLUtils {

    public static final String DUP_PROTEINS_SEARCH_ENGINES = "duplicated_proteins_search_engines";
    public static final String DUP_PROTEINS_SEARCH_ENGINES_SCORES = "duplicated_proteins_search_engines_scores";
    public static final String DUP_PROTEINS_BEST_SEARCH_ENGINES_SCORE = "duplicated_proteins_best_search_engines_score";
    public static final String DUP_PROTEINS_HAD_QUANT = "duplicated_proteins_had_quantification";
    public static final String NUM_MERGE_PROTEINS = "num_merge_proteins";


    public static CVParam convertParam(uk.ac.ebi.pride.jaxb.model.CvParam param) {
        return new CVParam(param.getCvLabel(), param.getAccession(), param.getName(), param.getValue());
    }

    public static CVParam convertUserParam(uk.ac.ebi.pride.jaxb.model.UserParam param) {
        return new CVParam(null, null, param.getName(), param.getValue());
    }

    public static uk.ac.ebi.pride.jaxb.model.CvParam getFirstCvParam(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return null;
        }

        if (param.getCvParam().iterator().hasNext()) {
            return param.getCvParam().iterator().next();
        }

        return null;
    }

    public static uk.ac.ebi.pride.jaxb.model.UserParam getFirstUserParam(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return null;
        }

        if (param.getUserParam().iterator().hasNext()) {
            return param.getUserParam().iterator().next();
        }

        return null;
    }

    public static String getCvParamValue(uk.ac.ebi.pride.jaxb.model.Param param, String accession) {
        if (param == null || isEmpty(accession)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (accession.equals(p.getAccession())) {
                return p.getValue();
            }
        }

        return null;
    }

    public static void addOptionalColumnValue(MZTabRecord record, MZTabColumnFactory columnFactory, String name, String value) {

        String header = OptionColumn.getHeader(null, name);
        MZTabColumn column = columnFactory.findColumnByHeader(header);

        String logicalPosition;

        if (column == null) {
            logicalPosition =  columnFactory.addOptionalColumn(name, String.class);
        }
        else {
            logicalPosition = column.getLogicPosition();
        }

        record.setValue(logicalPosition, value);
    }

    public static String getFileNameWithoutExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        return fileName.substring(0, lastIndexOfDot);
    }

    /**
     * Extract publication accession number from CVParam.
     */
    public static String getPublicationAccession(uk.ac.ebi.pride.jaxb.model.Param param, String name) {
        if (param == null || isEmpty(name)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (name.equalsIgnoreCase(p.getName())) {
                return p.getValue();
            }
        }

        return null;
    }

    /**
     * Checks whether the passed identification object is a decoy hit. This function only checks for
     * the presence of specific cv / user Params.
     *
     * @param identification A PRIDE JAXB Identification object.
     * @return Boolean indicating whether the passed identification is a decoy hit.
     */
    public static boolean isDecoyHit(Identification identification) {
        if (identification.getAdditional() != null) {
            for (uk.ac.ebi.pride.jaxb.model.CvParam param : identification.getAdditional().getCvParam()) {
                if (param.getAccession().equals(ConverterCVParam.PRIDE_DECOY_HIT.getAccession())) {
                    return true;
                }
            }

            for (uk.ac.ebi.pride.jaxb.model.UserParam param : identification.getAdditional().getUserParam()) {
                if ("Decoy Hit".equals(param.getName())) {
                    return true;
                }
            }
        }

        return false;
    }
}
