package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.CVParam;

/**
 * QuantitationCvParam used in PRIDE Converter 2. Original from jgriss
 *
 * @author ntoro
 * @since 29/07/2014 13:43
 */
public enum QuantitationCVParam {

    /**
     * QUANTIFICATION VALUES
     */
    INTENSITY_SUBSAMPLE1("PRIDE", "PRIDE:0000354", "Intensity subsample 1", ""),
    INTENSITY_SUBSAMPLE2("PRIDE", "PRIDE:0000355", "Intensity subsample 2", ""),
    INTENSITY_SUBSAMPLE3("PRIDE", "PRIDE:0000356", "Intensity subsample 3", ""),
    INTENSITY_SUBSAMPLE4("PRIDE", "PRIDE:0000357", "Intensity subsample 4", ""),
    INTENSITY_SUBSAMPLE5("PRIDE", "PRIDE:0000358", "Intensity subsample 5", ""),
    INTENSITY_SUBSAMPLE6("PRIDE", "PRIDE:0000359", "Intensity subsample 6", ""),
    INTENSITY_SUBSAMPLE7("PRIDE", "PRIDE:0000360", "Intensity subsample 7", ""),
    INTENSITY_SUBSAMPLE8("PRIDE", "PRIDE:0000361", "Intensity subsample 8", ""),

    INTENSITY_STD_SUBSAMPLE1("PRIDE", "PRIDE:0000375", "Standard deviation subsample 1", ""),
    INTENSITY_STD_SUBSAMPLE2("PRIDE", "PRIDE:0000376", "Standard deviation subsample 2", ""),
    INTENSITY_STD_SUBSAMPLE3("PRIDE", "PRIDE:0000377", "Standard deviation subsample 3", ""),
    INTENSITY_STD_SUBSAMPLE4("PRIDE", "PRIDE:0000378", "Standard deviation subsample 4", ""),
    INTENSITY_STD_SUBSAMPLE5("PRIDE", "PRIDE:0000379", "Standard deviation subsample 5", ""),
    INTENSITY_STD_SUBSAMPLE6("PRIDE", "PRIDE:0000380", "Standard deviation subsample 6", ""),
    INTENSITY_STD_SUBSAMPLE7("PRIDE", "PRIDE:0000381", "Standard deviation subsample 7", ""),
    INTENSITY_STD_SUBSAMPLE8("PRIDE", "PRIDE:0000382", "Standard deviation subsample 8", ""),

    INTENSITY_STD_ERR_SUBSAMPLE1("PRIDE", "PRIDE:0000383", "Standard error subsample 1", ""),
    INTENSITY_STD_ERR_SUBSAMPLE2("PRIDE", "PRIDE:0000384", "Standard error subsample 2", ""),
    INTENSITY_STD_ERR_SUBSAMPLE3("PRIDE", "PRIDE:0000385", "Standard error subsample 3", ""),
    INTENSITY_STD_ERR_SUBSAMPLE4("PRIDE", "PRIDE:0000386", "Standard error subsample 4", ""),
    INTENSITY_STD_ERR_SUBSAMPLE5("PRIDE", "PRIDE:0000387", "Standard error subsample 5", ""),
    INTENSITY_STD_ERR_SUBSAMPLE6("PRIDE", "PRIDE:0000388", "Standard error subsample 6", ""),
    INTENSITY_STD_ERR_SUBSAMPLE7("PRIDE", "PRIDE:0000389", "Standard error subsample 7", ""),
    INTENSITY_STD_ERR_SUBSAMPLE8("PRIDE", "PRIDE:0000390", "Standard error subsample 8", ""),

    TIC_VALUE("PRIDE", "PRIDE:0000364", "TIC value", ""),
    EMPAI_VALUE("PRIDE", "PRIDE:0000363", "emPAI value", ""),

    /**
     * QUANTIFICATION METHODS
     */
    ITRAQ_QUANTIFIED("PRIDE", "PRIDE:0000313", "iTRAQ", ""),
    TMT_QUANTIFIED("PRIDE", "PRIDE:0000314", "TMT", ""),
    O18_QUANTIFIED("PRIDE", "PRIDE:0000318", "18O", ""),
    AQUA_QUANTIFIED("PRIDE", "PRIDE:0000320", "AQUA", ""),
    ICAT_QUANTIFIED("PRIDE", "PRIDE:0000319", "ICAT", ""),
    ICPL_QUANTIFIED("PRIDE", "PRIDE:0000321", "ICPL", ""),
    SILAC_QUANTIFIED("PRIDE", "PRIDE:0000315", "SILAC", ""),
    TIC_QUANTIFIED("PRIDE", "PRIDE:0000323", "TIC", ""),
    EMPAI_QUANTIFIED("PRIDE", "PRIDE:0000322", "emPAI", ""),

    /**
     * mzData SampleDescription specific params
     */
    CONTAINS_MULTIPLE_SUBSAMPLES("PRIDE", "PRIDE:0000366", "Contains multiple subsamples", ""),

    SUBSAMPLE1_DESCRIPTION("PRIDE", "PRIDE:0000367", "Subample 1 description", ""),
    SUBSAMPLE2_DESCRIPTION("PRIDE", "PRIDE:0000368", "Subample 2 description", ""),
    SUBSAMPLE3_DESCRIPTION("PRIDE", "PRIDE:0000369", "Subample 3 description", ""),
    SUBSAMPLE4_DESCRIPTION("PRIDE", "PRIDE:0000370", "Subample 4 description", ""),
    SUBSAMPLE5_DESCRIPTION("PRIDE", "PRIDE:0000371", "Subample 5 description", ""),
    SUBSAMPLE6_DESCRIPTION("PRIDE", "PRIDE:0000372", "Subample 6 description", ""),
    SUBSAMPLE7_DESCRIPTION("PRIDE", "PRIDE:0000373", "Subample 7 description", ""),
    SUBSAMPLE8_DESCRIPTION("PRIDE", "PRIDE:0000374", "Subample 8 description", ""),

    /**
     * QUANTIFICATION REAGENTS
     */
    SILAC_HEAVY_REAGENT("PRIDE", "PRIDE:0000325", "SILAC heavy", ""),
    SILAC_MEDIUM_REAGENT("PRIDE", "PRIDE:0000327", "SILAC medium", ""),
    SILAC_LIGHT_REAGENT("PRIDE", "PRIDE:0000326", "SILAC light", ""),

    ICAT_HEAVY_REAGENT("PRIDE", "PRIDE:0000346", "ICAT heavy reagent", ""),
    ICAT_LIGHT_REAGENT("PRIDE", "PRIDE:0000345", "ICAT light reagent", ""),

    ICPL_0_REAGENT("PRIDE", "PRIDE:0000348", "ICPL 0 reagent", ""),
    ICPL_4_REAGENT("PRIDE", "PRIDE:0000349", "ICPL 4 reagent", ""),
    ICPL_6_REAGENT("PRIDE", "PRIDE:0000350", "ICPL 6 reagent", ""),
    ICPL_10_REAGENT("PRIDE", "PRIDE:0000351", "ICPL 10 reagent", ""),

    ITRAQ_113_REAGENT("PRIDE", "PRIDE:0000264", "iTRAQ reagent 113", ""),
    ITRAQ_114_REAGENT("PRIDE", "PRIDE:0000114", "iTRAQ reagent 114", ""),
    ITRAQ_115_REAGENT("PRIDE", "PRIDE:0000115", "iTRAQ reagent 115", ""),
    ITRAQ_116_REAGENT("PRIDE", "PRIDE:0000116", "iTRAQ reagent 116", ""),
    ITRAQ_117_REAGENT("PRIDE", "PRIDE:0000117", "iTRAQ reagent 117", ""),
    ITRAQ_118_REAGENT("PRIDE", "PRIDE:0000265", "iTRAQ reagent 118", ""),
    ITRAQ_119_REAGENT("PRIDE", "PRIDE:0000266", "iTRAQ reagent 119", ""),
    ITRAQ_121_REAGENT("PRIDE", "PRIDE:0000267", "iTRAQ reagent 121", ""),

    TMT_126_REAGENT("PRIDE", "PRIDE:0000285", "TMT reagent 126", ""),
    TMT_127_REAGENT("PRIDE", "PRIDE:0000286", "TMT reagent 127", ""),
    TMT_128_REAGENT("PRIDE", "PRIDE:0000287", "TMT reagent 128", ""),
    TMT_129_REAGENT("PRIDE", "PRIDE:0000288", "TMT reagent 129", ""),
    TMT_130_REAGENT("PRIDE", "PRIDE:0000289", "TMT reagent 130", ""),
    TMT_131_REAGENT("PRIDE", "PRIDE:0000290", "TMT reagent 131", ""),

    /**
     * QUANTIFICATION UNITS
     */
    UNIT_RATIO("PRIDE", "PRIDE:0000395", "Ratio", ""),
    UNIT_COPIES_PER_CELL("PRIDE", "PRIDE:0000396", "Copies per cell", "");

    private String cvLabel;
    private String accession;
    private String name;
    private String value;

    private QuantitationCVParam(String cvLabel, String accession, String name,
                                String value) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.value = value;
    }

    public String getCvLabel() {
        return cvLabel;
    }

    public String getAccession() {
        return accession;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public CVParam getParam() {
        return new CVParam(cvLabel, accession, name, value);
    }

    public CVParam getParam(String value) {
        return new CVParam(cvLabel, accession, name, value);
    }

    public static CVParam getProteinIntensityParam(int subsample, String value) {
        if (subsample == 1)
            return INTENSITY_SUBSAMPLE1.getParam(value);
        if (subsample == 2)
            return INTENSITY_SUBSAMPLE2.getParam(value);
        if (subsample == 3)
            return INTENSITY_SUBSAMPLE3.getParam(value);
        if (subsample == 4)
            return INTENSITY_SUBSAMPLE4.getParam(value);
        if (subsample == 5)
            return INTENSITY_SUBSAMPLE5.getParam(value);
        if (subsample == 6)
            return INTENSITY_SUBSAMPLE6.getParam(value);
        if (subsample == 7)
            return INTENSITY_SUBSAMPLE7.getParam(value);
        if (subsample == 8)
            return INTENSITY_SUBSAMPLE8.getParam(value);

        return null;
    }

    public static CVParam getProteinIntensityStdParam(int subsample, String value) {
        if (subsample == 1)
            return INTENSITY_STD_SUBSAMPLE1.getParam(value);
        if (subsample == 2)
            return INTENSITY_STD_SUBSAMPLE2.getParam(value);
        if (subsample == 3)
            return INTENSITY_STD_SUBSAMPLE3.getParam(value);
        if (subsample == 4)
            return INTENSITY_STD_SUBSAMPLE4.getParam(value);
        if (subsample == 5)
            return INTENSITY_STD_SUBSAMPLE5.getParam(value);
        if (subsample == 6)
            return INTENSITY_STD_SUBSAMPLE6.getParam(value);
        if (subsample == 7)
            return INTENSITY_STD_SUBSAMPLE7.getParam(value);
        if (subsample == 8)
            return INTENSITY_STD_SUBSAMPLE8.getParam(value);

        return null;
    }

    public static CVParam getProteinIntensityStdErrParam(int subsample, String value) {
        if (subsample == 1)
            return INTENSITY_STD_ERR_SUBSAMPLE1.getParam(value);
        if (subsample == 2)
            return INTENSITY_STD_ERR_SUBSAMPLE2.getParam(value);
        if (subsample == 3)
            return INTENSITY_STD_ERR_SUBSAMPLE3.getParam(value);
        if (subsample == 4)
            return INTENSITY_STD_ERR_SUBSAMPLE4.getParam(value);
        if (subsample == 5)
            return INTENSITY_STD_ERR_SUBSAMPLE5.getParam(value);
        if (subsample == 6)
            return INTENSITY_STD_ERR_SUBSAMPLE6.getParam(value);
        if (subsample == 7)
            return INTENSITY_STD_ERR_SUBSAMPLE7.getParam(value);
        if (subsample == 8)
            return INTENSITY_STD_ERR_SUBSAMPLE8.getParam(value);

        return null;
    }

    public static CVParam getPeptideIntensityParam(int subsample, String value) {
        if (subsample == 1)
            return INTENSITY_SUBSAMPLE1.getParam(value);
        if (subsample == 2)
            return INTENSITY_SUBSAMPLE2.getParam(value);
        if (subsample == 3)
            return INTENSITY_SUBSAMPLE3.getParam(value);
        if (subsample == 4)
            return INTENSITY_SUBSAMPLE4.getParam(value);
        if (subsample == 5)
            return INTENSITY_SUBSAMPLE5.getParam(value);
        if (subsample == 6)
            return INTENSITY_SUBSAMPLE6.getParam(value);
        if (subsample == 7)
            return INTENSITY_SUBSAMPLE7.getParam(value);
        if (subsample == 8)
            return INTENSITY_SUBSAMPLE8.getParam(value);

        return null;
    }

    public static CVParam getPeptideIntensityStdParam(int subsample, String value) {
        if (subsample == 1)
            return INTENSITY_STD_SUBSAMPLE1.getParam(value);
        if (subsample == 2)
            return INTENSITY_STD_SUBSAMPLE2.getParam(value);
        if (subsample == 3)
            return INTENSITY_STD_SUBSAMPLE3.getParam(value);
        if (subsample == 4)
            return INTENSITY_STD_SUBSAMPLE4.getParam(value);
        if (subsample == 5)
            return INTENSITY_STD_SUBSAMPLE5.getParam(value);
        if (subsample == 6)
            return INTENSITY_STD_SUBSAMPLE6.getParam(value);
        if (subsample == 7)
            return INTENSITY_STD_SUBSAMPLE7.getParam(value);
        if (subsample == 8)
            return INTENSITY_STD_SUBSAMPLE8.getParam(value);

        return null;
    }

    public static CVParam getPeptideIntensityStdErrParam(int subsample, String value) {
        if (subsample == 1)
            return INTENSITY_STD_ERR_SUBSAMPLE1.getParam(value);
        if (subsample == 2)
            return INTENSITY_STD_ERR_SUBSAMPLE2.getParam(value);
        if (subsample == 3)
            return INTENSITY_STD_ERR_SUBSAMPLE3.getParam(value);
        if (subsample == 4)
            return INTENSITY_STD_ERR_SUBSAMPLE4.getParam(value);
        if (subsample == 5)
            return INTENSITY_STD_ERR_SUBSAMPLE5.getParam(value);
        if (subsample == 6)
            return INTENSITY_STD_ERR_SUBSAMPLE6.getParam(value);
        if (subsample == 7)
            return INTENSITY_STD_ERR_SUBSAMPLE7.getParam(value);
        if (subsample == 8)
            return INTENSITY_STD_ERR_SUBSAMPLE8.getParam(value);

        return null;
    }

    public static CVParam getSubsampleDescription(int subsample, String value) {
        if (subsample == 1)
            return SUBSAMPLE1_DESCRIPTION.getParam(value);
        if (subsample == 2)
            return SUBSAMPLE2_DESCRIPTION.getParam(value);
        if (subsample == 3)
            return SUBSAMPLE3_DESCRIPTION.getParam(value);
        if (subsample == 4)
            return SUBSAMPLE4_DESCRIPTION.getParam(value);
        if (subsample == 5)
            return SUBSAMPLE5_DESCRIPTION.getParam(value);
        if (subsample == 6)
            return SUBSAMPLE6_DESCRIPTION.getParam(value);
        if (subsample == 7)
            return SUBSAMPLE7_DESCRIPTION.getParam(value);
        if (subsample == 8)
            return SUBSAMPLE8_DESCRIPTION.getParam(value);

        return null;
    }

    /**
     * Checks whether the passed accession describes a
     * QuantificationCvParam.
     * @param accession A cvParam's accession.
     * @return Boolean indicating whether the accession belongs to a quantification parameter.
     */
    public static boolean isAQuantificationParam(String accession) {
        for (QuantitationCVParam p : values()) {
            if (p.getAccession().equals(accession))
                return true;
        }

        return false;
    }

    public static boolean isQuantificationReagent(String accession) {
        if (ICAT_HEAVY_REAGENT.getAccession().equals(accession))
            return true;
        if (ICAT_LIGHT_REAGENT.getAccession().equals(accession))
            return true;
        if (ICPL_0_REAGENT.getAccession().equals(accession))
            return true;
        if (ICPL_4_REAGENT.getAccession().equals(accession))
            return true;
        if (ICPL_6_REAGENT.getAccession().equals(accession))
            return true;
        if (ICPL_10_REAGENT.getAccession().equals(accession))
            return true;
        if (SILAC_HEAVY_REAGENT.getAccession().equals(accession))
            return true;
        if (SILAC_MEDIUM_REAGENT.getAccession().equals(accession))
            return true;
        if (SILAC_LIGHT_REAGENT.getAccession().equals(accession))
            return true;
        if (TMT_126_REAGENT.getAccession().equals(accession))
            return true;
        if (TMT_127_REAGENT.getAccession().equals(accession))
            return true;
        if (TMT_128_REAGENT.getAccession().equals(accession))
            return true;
        if (TMT_129_REAGENT.getAccession().equals(accession))
            return true;
        if (TMT_130_REAGENT.getAccession().equals(accession))
            return true;
        if (TMT_131_REAGENT.getAccession().equals(accession))
            return true;
        if (ITRAQ_113_REAGENT.getAccession().equals(accession))
            return true;
        if (ITRAQ_114_REAGENT.getAccession().equals(accession))
            return true;
        if (ITRAQ_115_REAGENT.getAccession().equals(accession))
            return true;
        if (ITRAQ_116_REAGENT.getAccession().equals(accession))
            return true;
        if (ITRAQ_117_REAGENT.getAccession().equals(accession))
            return true;
        if (ITRAQ_118_REAGENT.getAccession().equals(accession))
            return true;
        if (ITRAQ_119_REAGENT.getAccession().equals(accession))
            return true;
        if (ITRAQ_121_REAGENT.getAccession().equals(accession))
            return true;

        return false;
    }

    public static boolean isQuantificationMethod(String accession) {
        if (O18_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (AQUA_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (ICAT_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (ICPL_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (SILAC_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (TMT_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (ITRAQ_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (EMPAI_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (TIC_QUANTIFIED.getAccession().equals(accession))
            return true;

        return false;
    }

    /**
     * Checks whether the passed quantification method
     * contains peptide quantification values
     */
    public static boolean containsPeptideQuantification(String accession) {
        if (O18_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (AQUA_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (ICAT_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (ICPL_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (SILAC_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (TMT_QUANTIFIED.getAccession().equals(accession))
            return true;
        if (ITRAQ_QUANTIFIED.getAccession().equals(accession))
            return true;
        // only the emPAI can't be reported for peptides
        if (EMPAI_QUANTIFIED.getAccession().equals(accession))
            return false;
        if (TIC_QUANTIFIED.getAccession().equals(accession))
            return true;

        return false;
    }

    /**
     * Checks whether the passed quantification method
     * contains protein quantification values
     */
    public static boolean containsProteinQuantification(String accession) {
        // any quantification method can contain protein quant values
        return isQuantificationMethod(accession);
    }
}
