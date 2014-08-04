package uk.ac.ebi.pride.jmztab.converter.utils.cv;

import uk.ac.ebi.pride.jmztab.model.CVParam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qingwei
 * @since 25/11/13
 */
public enum SearchEngineParam {
    // all of them are direct children of MS:1001456 (analysis software)
    MS__4000_SERIES_EXPLORER_SOFTWARE("MS", "MS:1000659", "4000 Series Explorer Software",  null),
    MS__4700_EXPLORER("MS", "MS:1000537", "4700 Explorer",  null),
    MS__6300_SERIES_ION_TRAP_DATA_ANALYSIS_SOFTWARE("MS", "MS:1000688", "6300 Series Ion Trap Data Analysis Software",  null),
    MS_AB_SCIEX_TOF_TOF_SERIES_EXPLORER_SOFTWARE("MS", "MS:1001483", "AB SCIEX TOF/TOF Series Explorer Software",  null),
    MS_ANALYSIS_SOFTWARE("MS", "MS:1001456", "analysis software",  null),
    MS_ANALYST("MS", "MS:1000551", "Analyst",  null),
    MS_ASCORE("MS", "MS:1001984", "Ascore",  null),
    MS_BIOANALYST("MS", "MS:1000667", "BioAnalyst",  null),
    MS_BIOTOOLS("MS", "MS:1000707", "BioTools",  null),
    MS_BIOWORKS("MS", "MS:1000533", "Bioworks",  null),
    MS_BYONIC("MS", "MS:1002261", "Byonic",  null),
    MS_CHROMATOF_HRT_SOFTWARE("MS", "MS:1001877", "ChromaTOF HRT software",  null),
    MS_CHROMATOF_SOFTWARE("MS", "MS:1001799", "ChromaTOF software",  null),
    MS_CLINPROT_MICRO("MS", "MS:1000709", "CLINPROT micro",  null),
    MS_CLINPROT("MS", "MS:1000708", "CLINPROT",  null),
    MS_COMET("MS", "MS:1002251", "Comet",  null),
    MS_COMPASS_FOR_HCT_ESQUIRE("MS", "MS:1000713", "Compass for HCT/esquire",  null),
    MS_COMPASS_FOR_MICROTOF("MS", "MS:1000714", "Compass for micrOTOF",  null),
    MS_COMPASS_OPENACCESS("MS", "MS:1000715", "Compass OpenAccess",  null),
    MS_COMPASS("MS", "MS:1000712", "Compass",  null),
    MS_DATA_EXPLORER("MS", "MS:1000536", "Data Explorer",  null),
    MS_DATAANALYSIS("MS", "MS:1000719", "DataAnalysis",  null),
    MS_DEBUNKER("MS", "MS:1001973", "DeBunker",  null),
    MS_DIRECTAG("MS", "MS:1001586", "DirecTag",  null),
    MS_EMPOWER("MS", "MS:1001795", "Empower",  null),
    MS_FLEXANALYSIS("MS", "MS:1000535", "FlexAnalysis",  null),
    MS_FLEXIMAGING("MS", "MS:1000722", "flexImaging",  null),
    MS_GENESPRING_MS("MS", "MS:1000684", "Genespring MS",  null),
    MS_GPS_EXPLORER("MS", "MS:1000661", "GPS Explorer",  null),
    MS_GREYLAG("MS", "MS:1001461", "greylag",  null),
    MS_ISOBARIQ("MS", "MS:1002210", "IsobariQ",  null),
    MS_LIGHTSIGHT_SOFTWARE("MS", "MS:1000662", "LightSight Software",  null),
    MS_MALDI_SOLUTIONS_MICROBIAL_IDENTIFICATION("MS", "MS:1001878", "MALDI Solutions Microbial Identification",  null),
    MS_MALDI_SOLUTIONS("MS", "MS:1001558", "MALDI Solutions",  null),
    MS_MARKERVIEW_SOFTWARE("MS", "MS:1000665", "MarkerView Software",  null),
    MS_MASCOT_DISTILLER("MS", "MS:1001488", "Mascot Distiller",  null),
    MS_MASCOT_INTEGRA("MS", "MS:1001489", "Mascot Integra",  null),
    MS_MASCOT_PARSER("MS", "MS:1001478", "Mascot Parser",  null),
    MS_MASCOT("MS", "MS:1001207", "Mascot",  null),
    MS_MASSHUNTER_BIOCONFIRM("MS", "MS:1000683", "MassHunter BioConfirm",  null),
    MS_MASSHUNTER_MASS_PROFILER("MS", "MS:1000685", "MassHunter Mass Profiler",  null),
    MS_MASSHUNTER_METABOLITE_ID("MS", "MS:1000682", "MassHunter Metabolite ID",  null),
    MS_MASSHUNTER_QUALITATIVE_ANALYSIS("MS", "MS:1000680", "MassHunter Qualitative Analysis",  null),
    MS_MASSHUNTER_QUANTITATIVE_ANALYSIS("MS", "MS:1000681", "MassHunter Quantitative Analysis",  null),
    MS_MASSLYNX("MS", "MS:1000534", "MassLynx",  null),
    MS_MAXQUANT("MS", "MS:1001583", "MaxQuant",  null),
    MS_METLIN("MS", "MS:1000686", "METLIN",  null),
    MS_MRMPILOT_SOFTWARE("MS", "MS:1000666", "MRMPilot Software",  null),
    MS_MS_GF_("MS", "MS:1002048", "MS-GF+",  null),
    MS_MS_GF("MS", "MS:1002047", "MS-GF",  null),
    MS_MSQUANT("MS", "MS:1001977", "MSQuant",  null),
    MS_MULTIQUANT("MS", "MS:1000674", "MultiQuant",  null),
    MS_MYRIMATCH("MS", "MS:1001585", "MyriMatch",  null),
    MS_MZIDLIB("MS", "MS:1002237", "mzidLib",  null),
    MS_OMSSA("MS", "MS:1001475", "OMSSA",  null),
    MS_PANALYZER("MS", "MS:1002076", "PAnalyzer",  null),
    MS_PEAKS_NODE("MS", "MS:1001948", "PEAKS Node",  null),
    MS_PEAKS_ONLINE("MS", "MS:1001947", "PEAKS Online",  null),
    MS_PEAKS_STUDIO("MS", "MS:1001946", "PEAKS Studio",  null),
    MS_PEPITOME("MS", "MS:1001588", "Pepitome",  null),
    MS_PERCOLATOR("MS", "MS:1001490", "Percolator",  null),
    MS_PHENYX("MS", "MS:1001209", "Phenyx",  null),
    MS_PINPOINT("MS", "MS:1001912", "PinPoint",  null),
    MS_PRO_BLAST("MS", "MS:1000671", "Pro BLAST",  null),
    MS_PRO_ICAT("MS", "MS:1000669", "Pro ICAT",  null),
    MS_PRO_ID("MS", "MS:1000668", "Pro ID",  null),
    MS_PRO_QUANT("MS", "MS:1000670", "Pro Quant",  null),
    MS_PROFILEANALYSIS("MS", "MS:1000728", "ProfileAnalysis",  null),
    MS_PROTEINEXTRACTOR("MS", "MS:1001487", "ProteinExtractor",  null),
    MS_PROTEINLYNX_GLOBAL_SERVER("MS", "MS:1000601", "ProteinLynx Global Server",  null),
    MS_PROTEINPILOT_SOFTWARE("MS", "MS:1000663", "ProteinPilot Software",  null),
    MS_PROTEINPROSPECTOR("MS", "MS:1002043", "ProteinProspector",  null),
    MS_PROTEINSCAPE("MS", "MS:1000734", "ProteinScape",  null),
    MS_PROTEIOS("MS", "MS:1000600", "Proteios",  null),
    MS_PROTEOME_DISCOVERER("MS", "MS:1000650", "Proteome Discoverer",  null),
    MS_PROTEOWIZARD_SOFTWARE("MS", "MS:1000615", "ProteoWizard software",  null),
    MS_QUANTANALYSIS("MS", "MS:1000736", "QuantAnalysis",  null),
    MS_SCAFFOLD("MS", "MS:1001561", "Scaffold",  null),
    MS_SEQUEST("MS", "MS:1001208", "SEQUEST",  null),
    MS_SPECTRAST("MS", "MS:1001477", "SpectraST",  null),
    MS_SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION("MS", "MS:1000687", "Spectrum Mill for MassHunter Workstation",  null),
    MS_SQID("MS", "MS:1001886", "SQID",  null),
    MS_TAGRECON("MS", "MS:1001587", "TagRecon",  null),
    MS_TISSUEVIEW_SOFTWARE("MS", "MS:1000664", "TissueView Software",  null),
    MS_TOPP_SOFTWARE("MS", "MS:1000752", "TOPP software",  null),
    MS_TRANS_PROTEOMIC_PIPELINE_SOFTWARE("MS", "MS:1002286", "Trans-Proteomic Pipeline software",  null),
    MS_TRANS_PROTEOMIC_PIPELINE("MS", "MS:1002285", "Trans-Proteomic Pipeline",  null),
    MS_UNIFY("MS", "MS:1001796", "Unify",  null),
    MS_VOYAGER_BIOSPECTROMETRY_WORKSTATION_SYSTEM("MS", "MS:1000539", "Voyager Biospectrometry Workstation System",  null),
    MS_X_TANDEM("MS", "MS:1001476", "X!Tandem",  null),
    MS_XCALIBUR("MS", "MS:1000532", "Xcalibur",  null),
    MS_XCMS("MS", "MS:1001582", "XCMS",  null),

    //PRIDE SearchEngines
    PRIDE_PEPSPLICE("PRIDE", "PRIDE:0000145", "PepSplice",null),
    PRIDE_PEPTIDE_PROPHET("PRIDE", "PRIDE:0000101", "PeptideProphet", null);

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

    public CVParam getParam() {
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
            param = MS_MASCOT;
        } else if (matchRegexpName(".*omssa.*", searchEngineName)) {
            param = MS_OMSSA;
        } else if (matchRegexpName(".*sequest.*", searchEngineName)) {
            param = MS_SEQUEST;
        } else if (matchRegexpName(".*spectrum.*mill.*", searchEngineName)) {
            param = MS_SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION;
        } else if (matchRegexpName(".*spectra.*st.*", searchEngineName)) {
            param = MS_SPECTRAST;
        } else if (matchRegexpName(".*x.*tandem.*", searchEngineName)) {
            param = MS_X_TANDEM;
        }

        if (param == null) {
            param = MS_ANALYSIS_SOFTWARE;
        }

        return param;
    }

    public static CVParam findParamByAccession(String accession) {
        for (SearchEngineParam searchEngineParam : values()) {
            if (searchEngineParam.accession.equalsIgnoreCase(accession)) {
                return searchEngineParam.getParam();
            }
        }

        return null;
    }
}
