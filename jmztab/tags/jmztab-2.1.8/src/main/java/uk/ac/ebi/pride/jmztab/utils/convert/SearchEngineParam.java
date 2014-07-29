package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.CVParam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: qingwei
 * Date: 25/11/13
 */
public enum SearchEngineParam {
    // all of them are direct children of MS:1001456 (analysis software)
    PEAKS_NODE("MS", "MS:1001948", "PEAKS Node",  null),
    AB_SCIEX_TOF_TOF_SERIES_EXPLORER_SOFTWARE("MS", "MS:1001483", "AB SCIEX TOF/TOF Series Explorer Software",  null),
    ASCORE("MS", "MS:1001984", "Ascore",  null),
    PEAKS_ONLINE("MS", "MS:1001947", "PEAKS Online",  null),
    COMET("MS", "MS:1002251", "Comet",  null),
    PROTEINEXTRACTOR("MS", "MS:1001487", "ProteinExtractor",  null),
    PROTEOWIZARD_SOFTWARE("MS", "MS:1000615", "ProteoWizard software",  null),
    MYRIMATCH("MS", "MS:1001585", "MyriMatch",  null),
    DIRECTAG("MS", "MS:1001586", "DirecTag",  null),
    MASCOT_DISTILLER("MS", "MS:1001488", "Mascot Distiller",  null),
    MAXQUANT("MS", "MS:1001583", "MaxQuant",  null),
    MASCOT_INTEGRA("MS", "MS:1001489", "Mascot Integra",  null),
    XCMS("MS", "MS:1001582", "XCMS",  null),
    PEAKS_STUDIO("MS", "MS:1001946", "PEAKS Studio",  null),
    _4000_SERIES_EXPLORER_SOFTWARE("MS", "MS:1000659", "4000 Series Explorer Software",  null),
    PINPOINT("MS", "MS:1001912", "PinPoint",  null),
    PROFILEANALYSIS("MS", "MS:1000728", "ProfileAnalysis",  null),
    SQID("MS", "MS:1001886", "SQID",  null),
    EMPOWER("MS", "MS:1001795", "Empower",  null),
    LIGHTSIGHT_SOFTWARE("MS", "MS:1000662", "LightSight Software",  null),
    GPS_EXPLORER("MS", "MS:1000661", "GPS Explorer",  null),
    FLEXIMAGING("MS", "MS:1000722", "flexImaging",  null),
    MASCOT_PARSER("MS", "MS:1001478", "Mascot Parser",  null),
    SPECTRAST("MS", "MS:1001477", "SpectraST",  null),
    PERCOLATOR("MS", "MS:1001490", "Percolator",  null),
    OMSSA("MS", "MS:1001475", "OMSSA",  null),
    X_TANDEM("MS", "MS:1001476", "X!Tandem",  null),
    MARKERVIEW_SOFTWARE("MS", "MS:1000665", "MarkerView Software",  null),
    MRMPILOT_SOFTWARE("MS", "MS:1000666", "MRMPilot Software",  null),
    UNIFY("MS", "MS:1001796", "Unify",  null),
    PROTEINPILOT_SOFTWARE("MS", "MS:1000663", "ProteinPilot Software",  null),
    CHROMATOF_SOFTWARE("MS", "MS:1001799", "ChromaTOF software",  null),
    TISSUEVIEW_SOFTWARE("MS", "MS:1000664", "TissueView Software",  null),
    PRO_ICAT("MS", "MS:1000669", "Pro ICAT",  null),
    TRANS_PROTEOMIC_PIPELINE("MS", "MS:1002285", "Trans-Proteomic Pipeline",  null),
    TRANS_PROTEOMIC_PIPELINE_SOFTWARE("MS", "MS:1002286", "Trans-Proteomic Pipeline software",  null),
    BIOANALYST("MS", "MS:1000667", "BioAnalyst",  null),
    PRO_ID("MS", "MS:1000668", "Pro ID",  null),
    DATAANALYSIS("MS", "MS:1000719", "DataAnalysis",  null),
    COMPASS_OPENACCESS("MS", "MS:1000715", "Compass OpenAccess",  null),
    TOPP_SOFTWARE("MS", "MS:1000752", "TOPP software",  null),
    PRO_BLAST("MS", "MS:1000671", "Pro BLAST",  null),
    PRO_QUANT("MS", "MS:1000670", "Pro Quant",  null),
    MS_GF("MS", "MS:1002047", "MS-GF",  null),
    MS_GF_("MS", "MS:1002048", "MS-GF+",  null),
    COMPASS("MS", "MS:1000712", "Compass",  null),
    COMPASS_FOR_HCT_ESQUIRE("MS", "MS:1000713", "Compass for HCT/esquire",  null),
    COMPASS_FOR_MICROTOF("MS", "MS:1000714", "Compass for micrOTOF",  null),
    PROTEINPROSPECTOR("MS", "MS:1002043", "ProteinProspector",  null),
    MZIDLIB("MS", "MS:1002237", "mzidLib",  null),
    PANALYZER("MS", "MS:1002076", "PAnalyzer",  null),
    XCALIBUR("MS", "MS:1000532", "Xcalibur",  null),
    BIOWORKS("MS", "MS:1000533", "Bioworks",  null),
    MASSLYNX("MS", "MS:1000534", "MassLynx",  null),
    FLEXANALYSIS("MS", "MS:1000535", "FlexAnalysis",  null),
    GREYLAG("MS", "MS:1001461", "greylag",  null),
    DATA_EXPLORER("MS", "MS:1000536", "Data Explorer",  null),
    _4700_EXPLORER("MS", "MS:1000537", "4700 Explorer",  null),
    SCAFFOLD("MS", "MS:1001561", "Scaffold",  null),
    VOYAGER_BIOSPECTROMETRY_WORKSTATION_SYSTEM("MS", "MS:1000539", "Voyager Biospectrometry Workstation System",  null),
    MASSHUNTER_BIOCONFIRM("MS", "MS:1000683", "MassHunter BioConfirm",  null),
    GENESPRING_MS("MS", "MS:1000684", "Genespring MS",  null),
    MASSHUNTER_QUANTITATIVE_ANALYSIS("MS", "MS:1000681", "MassHunter Quantitative Analysis",  null),
    MASSHUNTER_METABOLITE_ID("MS", "MS:1000682", "MassHunter Metabolite ID",  null),
    MASSHUNTER_QUALITATIVE_ANALYSIS("MS", "MS:1000680", "MassHunter Qualitative Analysis",  null),
    MULTIQUANT("MS", "MS:1000674", "MultiQuant",  null),
    MALDI_SOLUTIONS("MS", "MS:1001558", "MALDI Solutions",  null),
    MSQUANT("MS", "MS:1001977", "MSQuant",  null),
    PROTEINLYNX_GLOBAL_SERVER("MS", "MS:1000601", "ProteinLynx Global Server",  null),
    PROTEIOS("MS", "MS:1000600", "Proteios",  null),
    ISOBARIQ("MS", "MS:1002210", "IsobariQ",  null),
    DEBUNKER("MS", "MS:1001973", "DeBunker",  null),
    MASCOT("MS", "MS:1001207", "Mascot",  null),
    CLINPROT_MICRO("MS", "MS:1000709", "CLINPROT micro",  null),
    CLINPROT("MS", "MS:1000708", "CLINPROT",  null),
    BYONIC("MS", "MS:1002261", "Byonic",  null),
    PHENYX("MS", "MS:1001209", "Phenyx",  null),
    SEQUEST("MS", "MS:1001208", "SEQUEST",  null),
    BIOTOOLS("MS", "MS:1000707", "BioTools",  null),
    CHROMATOF_HRT_SOFTWARE("MS", "MS:1001877", "ChromaTOF HRT software",  null),
    MALDI_SOLUTIONS_MICROBIAL_IDENTIFICATION("MS", "MS:1001878", "MALDI Solutions Microbial Identification",  null),
    PEPITOME("MS", "MS:1001588", "Pepitome",  null),
    TAGRECON("MS", "MS:1001587", "TagRecon",  null),
    PROTEOME_DISCOVERER("MS", "MS:1000650", "Proteome Discoverer",  null),
    METLIN("MS", "MS:1000686", "METLIN",  null),
    MASSHUNTER_MASS_PROFILER("MS", "MS:1000685", "MassHunter Mass Profiler",  null),
    _6300_SERIES_ION_TRAP_DATA_ANALYSIS_SOFTWARE("MS", "MS:1000688", "6300 Series Ion Trap Data Analysis Software",  null),
    SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION("MS", "MS:1000687", "Spectrum Mill for MassHunter Workstation",  null),
    PROTEINSCAPE("MS", "MS:1000734", "ProteinScape",  null),
    ANALYST("MS", "MS:1000551", "Analyst",  null),
    QUANTANALYSIS("MS", "MS:1000736", "QuantAnalysis",  null),
    ANALYSIS_SOFTWARE("MS", "MS:1001456", "analysis software",  null);

    private String cvLabel;
    private String accession;
    private String name;
    private String value;

    private SearchEngineParam(String cvLabel, String accession, String name, String value) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.value = value;
    }

    public CVParam toCVParam() {
        return new CVParam(cvLabel, accession, name, value);
    }

    private static String convertName(String name) {
        name = name.trim().toUpperCase().replaceAll("[^A-Z0-9]", "_");
        if (name.startsWith("[0-9]")) {
            return "_" + name;
        } else {
            return name;
        }
    }

    private static boolean matchRegexpName(String regexp, String name) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(name.trim().toLowerCase());
        return matcher.find();
    }

    /**
     * Tries to guess which search engine is described by the passed name. In case no matching parameter
     * is found, null is returned.
     */
    public static SearchEngineParam findParamByName(String searchEngineName) {
        if (searchEngineName == null) {
            return null;
        }

        SearchEngineParam param;

        try {
            param = SearchEngineParam.valueOf(convertName(searchEngineName));
            if (param != null) {
                return param;
            }
        } catch (IllegalArgumentException e) {
            param = null;
        }


        if (matchRegexpName(".*mascot.*", searchEngineName)) {
            param = MASCOT;
        } else if (matchRegexpName(".*omssa.*", searchEngineName)) {
            param = OMSSA;
        } else if (matchRegexpName(".*sequest.*", searchEngineName)) {
            param = SEQUEST;
        } else if (matchRegexpName(".*spectrum.*mill.*", searchEngineName)) {
            param = SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION;
        } else if (matchRegexpName(".*spectra.*st.*", searchEngineName)) {
            param = SPECTRAST;
        } else if (matchRegexpName(".*x.*tandem.*", searchEngineName)) {
            param = X_TANDEM;
        }

        if (param == null) {
            param = ANALYSIS_SOFTWARE;
        }

        return param;
    }

    public static CVParam findParamByAccession(String accession) {
        for (SearchEngineParam searchEngineParam : values()) {
            if (searchEngineParam.accession.equalsIgnoreCase(accession)) {
                return searchEngineParam.toCVParam();
            }
        }

        return null;
    }
}
