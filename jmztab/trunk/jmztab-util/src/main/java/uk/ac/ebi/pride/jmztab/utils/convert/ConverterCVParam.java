package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.CVParam;

/**
 * @author ntoro
 * @since 29/07/2014 13:31
 */
public enum ConverterCVParam {

    PRIDE_DECOY_HIT("PRIDE", "PRIDE:0000303", "Decoy hit", null),
    PRIDE_EXPERIMENT_DESCRIPTION("PRIDE", "PRIDE:0000040", "Experiment description", null),

    PRIDE_GEL_BASED_EXPERIMENT("PRIDE", "PRIDE:0000305", "Gel-based experiment", null),
    PRIDE_GEL_IDENTIFIER("PRIDE", "PRIDE:0000304", "Gel identifier", null),
    PRIDE_GEL_SPOT_IDENTIFIER("PRIDE", "PRIDE:0000300", "Gel spot identifier", null),

    PRIDE_INDISTINGUISHABLE_ACCESSION("PRIDE", "PRIDE:0000098", "Indistinguishable alternative protein accession",null),
    PRIDE_PROTEIN_NAME("PRIDE", "PRIDE:0000063", "Protein description line", null),

    PRIDE_REFERENCE_DOI("PRIDE", "PRIDE:0000042", "DOI",null),
    PRIDE_REFERENCE_PUBMED("PRIDE", "PRIDE:0000029", "PubMed", null),

    PRIDE_DOWNSTREAM_FLANKING_SEQUENCE("PRIDE", "PRIDE:0000066", "Downstream flanking sequence", null),
    PRIDE_UPSTREAM_FLANKING_SEQUENCE("PRIDE", "PRIDE:0000065", "Upstream flanking sequence", null),

    PRIDE_PROTEIN_SEQUENCE("PRIDE", "PRIDE:0000041","Search database protein sequence", null),
    PRIDE_PROTEIN_LENGTH("PRIDE", "PRIDE:0000172","Search database protein sequence length", null),

    MS_CHARGE_STATE("MS", "MS:1000041", "charge state", null),
    MS_PRECURSOR_MZ("MS", "MS:1000744", "selected ion m/z", null),

    PSI_CHARGE_STATE("PSI", "PSI:1000041", "ChargeState", null),
    PSI_MZ_RATIO("PSI", "PSI:1000040", "MassToChargeRatio", null),

    MS_DECOY_PEPTIDE("MS", "MS:1002217", "decoy peptide", null),

    MS_PSI_MZDATA_FILE("MS", "MS:1000564", "PSI mzData file", null),
    MS_SPEC_NATIVE_ID_FORMAT("MS", "MS:1000777", "spectrum identifier nativeID format", null),

    MS_FIXED_MOD("MS", "MS:1002453", "No fixed modifications searched", null),
    MS_VAR_MOD("MS", "MS:1002454", "No variable modifications searched", null);

    private String cvLabel;
    private String accession;
    private String name;
    private String value;

    private ConverterCVParam(String cvLabel, String accession, String name, String value) {
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
}
