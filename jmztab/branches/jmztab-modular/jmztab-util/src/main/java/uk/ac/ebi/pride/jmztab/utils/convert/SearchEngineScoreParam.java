package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.CVParam;

/**
 * User: qingwei
 * Date: 25/11/13
 */
public enum SearchEngineScoreParam {

    // coming from children terms of MS:1001153 (search engine specific score)
    MS_SEARCH_ENGINE_SPECIFIC_SCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001153", "search engine specific score", null),

    MS_ASCORE_ASCORE(SearchEngineParam.MS_ASCORE, "MS", "MS:1001985", "Ascore:Ascore", null),
    MS_BYONIC__PEPTIDE_ABSLOGPROB(SearchEngineParam.MS_BYONIC, "MS", "MS:1002309", "Byonic: Peptide AbsLogProb", null),
    MS_BYONIC__PEPTIDE_ABSLOGPROB2D(SearchEngineParam.MS_BYONIC, "MS", "MS:1002311", "Byonic: Peptide AbsLogProb2D", null),
    MS_BYONIC__PROTEIN_ABSLOGPROB(SearchEngineParam.MS_BYONIC, "MS", "MS:1002310", "Byonic: Protein AbsLogProb", null),
    MS_BYONIC_BEST_LOGPROB(SearchEngineParam.MS_BYONIC, "MS", "MS:1002268", "Byonic:Best LogProb", null),
    MS_BYONIC_BEST_SCORE(SearchEngineParam.MS_BYONIC, "MS", "MS:1002269", "Byonic:Best Score", null),
    MS_BYONIC_DELTA_SCORE(SearchEngineParam.MS_BYONIC, "MS", "MS:1002263", "Byonic:Delta Score", null),
    MS_BYONIC_DELTAMOD_SCORE(SearchEngineParam.MS_BYONIC, "MS", "MS:1002264", "Byonic:DeltaMod Score", null),
    MS_BYONIC_PEP(SearchEngineParam.MS_BYONIC, "MS", "MS:1002265", "Byonic:PEP", null),
    MS_BYONIC_PEPTIDE_LOGPROB(SearchEngineParam.MS_BYONIC, "MS", "MS:1002266", "Byonic:Peptide LogProb", null),
    MS_BYONIC_PROTEIN_LOGPROB(SearchEngineParam.MS_BYONIC, "MS", "MS:1002267", "Byonic:Protein LogProb", null),
    MS_BYONIC_SCORE(SearchEngineParam.MS_BYONIC, "MS", "MS:1002262", "Byonic:Score", null),
    MS_COMBINED_FDRSCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1002125", "combined FDRScore", null),
    MS_COMET_DELTACN(SearchEngineParam.MS_COMET, "MS", "MS:1002253", "Comet:deltacn", null),
    MS_COMET_DELTACNSTAR(SearchEngineParam.MS_COMET, "MS", "MS:1002254", "Comet:deltacnstar", null),
    MS_COMET_EXPECTATION_VALUE(SearchEngineParam.MS_COMET, "MS", "MS:1002257", "Comet:expectation value", null),
    MS_COMET_MATCHED_IONS(SearchEngineParam.MS_COMET, "MS", "MS:1002258", "Comet:matched ions", null),
    MS_COMET_SPRANK(SearchEngineParam.MS_COMET, "MS", "MS:1002256", "Comet:sprank", null),
    MS_COMET_SPSCORE(SearchEngineParam.MS_COMET, "MS", "MS:1002255", "Comet:spscore", null),
    MS_COMET_TOTAL_IONS(SearchEngineParam.MS_COMET, "MS", "MS:1002259", "Comet:total ions", null),
    MS_COMET_XCORR(SearchEngineParam.MS_COMET, "MS", "MS:1002252", "Comet:xcorr", null),
    MS_DEBUNKER_SCORE(SearchEngineParam.MS_DEBUNKER, "MS", "MS:1001974", "DeBunker:score", null),
    MS_FDRSCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001874", "FDRScore", null),
    MS_HIGHER_SCORE_BETTER(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1002108", "higher score better", null),
    MS_IDENTITYE_SCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001569", "IdentityE Score", null),
    MS_LOWER_SCORE_BETTER(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1002109", "lower score better", null),
    MS_MASCOT_EXPECTATION_VALUE(SearchEngineParam.MS_MASCOT, "MS", "MS:1001172", "Mascot:expectation value", null),
    MS_MASCOT_HOMOLOGY_THRESHOLD(SearchEngineParam.MS_MASCOT, "MS", "MS:1001370", "Mascot:homology threshold", null),
    MS_MASCOT_IDENTITY_THRESHOLD(SearchEngineParam.MS_MASCOT, "MS", "MS:1001371", "Mascot:identity threshold", null),
    MS_MASCOT_MATCHED_IONS(SearchEngineParam.MS_MASCOT, "MS", "MS:1001173", "Mascot:matched ions", null),
    MS_MASCOT_PTM_SITE_ASSIGNMENT_CONFIDENCE(SearchEngineParam.MS_MASCOT, "MS", "MS:1002012", "Mascot:PTM site assignment confidence", null),
    MS_MASCOT_SCORE(SearchEngineParam.MS_MASCOT, "MS", "MS:1001171", "Mascot:score", null),
    MS_MASCOT_TOTAL_IONS(SearchEngineParam.MS_MASCOT, "MS", "MS:1001174", "Mascot:total ions", null),
    MS_MAXQUANT_P_SITE_LOCALIZATION_PROBABILITY(SearchEngineParam.MS_MAXQUANT, "MS", "MS:1001982", "MaxQuant:P-site localization probability", null),
    MS_MAXQUANT_PHOSPHO__STY__PROBABILITIES(SearchEngineParam.MS_MAXQUANT, "MS", "MS:1001980", "MaxQuant:Phospho (STY) Probabilities", null),
    MS_MAXQUANT_PHOSPHO__STY__SCORE_DIFFS(SearchEngineParam.MS_MAXQUANT, "MS", "MS:1001981", "MaxQuant:Phospho (STY) Score Diffs", null),
    MS_MAXQUANT_PTM_DELTA_SCORE(SearchEngineParam.MS_MAXQUANT, "MS", "MS:1001983", "MaxQuant:PTM Delta Score", null),
    MS_MAXQUANT_PTM_SCORE(SearchEngineParam.MS_MAXQUANT, "MS", "MS:1001979", "MaxQuant:PTM Score", null),
    MS_MRMAID_PEPTIDE_SCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1002221", "MRMaid:peptide score", null),
    MS_MS_GF_DENOVOSCORE(SearchEngineParam.MS_MS_GF, "MS", "MS:1002050", "MS-GF:DeNovoScore", null),
    MS_MS_GF_ENERGY(SearchEngineParam.MS_MS_GF, "MS", "MS:1002051", "MS-GF:Energy", null),
    MS_MS_GF_EVALUE(SearchEngineParam.MS_MS_GF, "MS", "MS:1002053", "MS-GF:EValue", null),
    MS_MS_GF_PEP(SearchEngineParam.MS_MS_GF, "MS", "MS:1002056", "MS-GF:PEP", null),
    MS_MS_GF_PEPQVALUE(SearchEngineParam.MS_MS_GF, "MS", "MS:1002055", "MS-GF:PepQValue", null),
    MS_MS_GF_QVALUE(SearchEngineParam.MS_MS_GF, "MS", "MS:1002054", "MS-GF:QValue", null),
    MS_MS_GF_RAWSCORE(SearchEngineParam.MS_MS_GF, "MS", "MS:1002049", "MS-GF:RawScore", null),
    MS_MS_GF_SPECEVALUE(SearchEngineParam.MS_MS_GF, "MS", "MS:1002052", "MS-GF:SpecEValue", null),
    MS_MSFIT_MOWSE_SCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001501", "MSFit:Mowse score", null),
    MS_MSQUANT_PTM_SCORE(SearchEngineParam.MS_MSQUANT, "MS", "MS:1001978", "MSQuant:PTM-score", null),
    MS_MYRIMATCH_MVH(SearchEngineParam.MS_MYRIMATCH, "MS", "MS:1001589", "MyriMatch:MVH", null),
    MS_MYRIMATCH_MZFIDELITY(SearchEngineParam.MS_MYRIMATCH, "MS", "MS:1001590", "MyriMatch:mzFidelity", null),
    MS_MYRIMATCH_NMATCHS(SearchEngineParam.MS_MYRIMATCH, "MS", "MS:1001121", "number of matched peaks", null),
    MS_MYRIMATCH_NOMATCHS(SearchEngineParam.MS_MYRIMATCH, "MS", "MS:1001362", "number of unmatched peaks", null),
    MS_OMSSA_EVALUE(SearchEngineParam.MS_OMSSA, "MS", "MS:1001328", "OMSSA:evalue", null),
    MS_OMSSA_PVALUE(SearchEngineParam.MS_OMSSA, "MS", "MS:1001329", "OMSSA:pvalue", null),
    MS_PARAGON_CONFIDENCE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001167", "Paragon:confidence", null),
    MS_PARAGON_CONTRIB(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001170", "Paragon:contrib", null),
    MS_PARAGON_EXPRESSION_CHANGE_P_VALUE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001169", "Paragon:expression change p-value", null),
    MS_PARAGON_EXPRESSION_ERROR_FACTOR(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001168", "Paragon:expression error factor", null),
    MS_PARAGON_SCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001166", "Paragon:score", null),
    MS_PARAGON_TOTAL_PROTSCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001165", "Paragon:total protscore", null),
    MS_PARAGON_UNUSED_PROTSCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001164", "Paragon:unused protscore", null),
    MS_PEAKS_PEPTIDESCORE(SearchEngineParam.MS_PEAKS_STUDIO, "MS", "MS:1001950", "PEAKS:peptideScore", null),
    MS_PEAKS_PROTEINSCORE(SearchEngineParam.MS_PEAKS_STUDIO, "MS", "MS:1001951", "PEAKS:proteinScore", null),
    MS_PEPTIDESHAKER_PSM_SCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE,"MS", "MS:1002466", "PeptideShaker: PSM Score", null),
    MS_PEPTIDESHAKER_PSM_CONFIDENCE(SearchEngineParam.MS_ANALYSIS_SOFTWARE,"MS", "MS:1002467", "PeptideShaker: PSM Confidence", null),
    MS_PEPTIDESHAKER_PROTEIN_SCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE,"MS", "MS:1002470", "PeptideShaker: Protein Score", null),
    MS_PEPTIDESHAKER_PROTEIN_CONFIDENCE(SearchEngineParam.MS_ANALYSIS_SOFTWARE,"MS", "MS:1002471", "PeptideShake: Protein Confidence", null),
    MS_PERCOLATOR_PEP(SearchEngineParam.MS_PERCOLATOR, "MS", "MS:1001493", "percolator:PEP", null),
    MS_PERCOLATOR_Q_VALUE(SearchEngineParam.MS_PERCOLATOR, "MS", "MS:1001491", "percolator:Q value", null),
    MS_PERCOLATOR_SCORE(SearchEngineParam.MS_PERCOLATOR, "MS", "MS:1001492", "percolator:score", null),
    MS_PHENYX_AC(SearchEngineParam.MS_PHENYX, "MS", "MS:1001388", "Phenyx:AC", null),
    MS_PHENYX_AUTO(SearchEngineParam.MS_PHENYX, "MS", "MS:1001393", "Phenyx:Auto", null),
    MS_PHENYX_ID(SearchEngineParam.MS_PHENYX, "MS", "MS:1001389", "Phenyx:ID", null),
    MS_PHENYX_MODIF(SearchEngineParam.MS_PHENYX, "MS", "MS:1001398", "Phenyx:Modif", null),
    MS_PHENYX_NUMBEROFMC(SearchEngineParam.MS_PHENYX, "MS", "MS:1001397", "Phenyx:NumberOfMC", null),
    MS_PHENYX_PEPPVALUE(SearchEngineParam.MS_PHENYX, "MS", "MS:1001396", "Phenyx:PepPvalue", null),
    MS_PHENYX_PEPTIDES1(SearchEngineParam.MS_PHENYX, "MS", "MS:1001391", "Phenyx:Peptides1", null),
    MS_PHENYX_PEPTIDES2(SearchEngineParam.MS_PHENYX, "MS", "MS:1001392", "Phenyx:Peptides2", null),
    MS_PHENYX_PEPZSCORE(SearchEngineParam.MS_PHENYX, "MS", "MS:1001395", "Phenyx:Pepzscore", null),
    MS_PHENYX_SCORE(SearchEngineParam.MS_PHENYX, "MS", "MS:1001390", "Phenyx:Score", null),
    MS_PHENYX_USER(SearchEngineParam.MS_PHENYX, "MS", "MS:1001394", "Phenyx:User", null),
    MS_PROFOUND_CLUSTER(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001499", "Profound:Cluster", null),
    MS_PROFOUND_CLUSTERRANK(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001500", "Profound:ClusterRank", null),
    MS_PROFOUND_Z_VALUE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001498", "Profound:z value", null),
    MS_PROTEINEXTRACTOR_SCORE(SearchEngineParam.MS_PROTEINEXTRACTOR, "MS", "MS:1001507", "ProteinExtractor:Score", null),
    MS_PROTEINLYNX_LADDER_SCORE(SearchEngineParam.MS_PROTEINLYNX_GLOBAL_SERVER, "MS", "MS:1001571", "ProteinLynx:Ladder Score", null),
    MS_PROTEINLYNX_LOG_LIKELIHOOD(SearchEngineParam.MS_PROTEINLYNX_GLOBAL_SERVER, "MS", "MS:1001570", "ProteinLynx:Log Likelihood", null),
    MS_PROTEINPROSPECTOR_EXPECTATION_VALUE(SearchEngineParam.MS_PROTEINPROSPECTOR, "MS", "MS:1002045", "ProteinProspector:expectation value", null),
    MS_PROTEINPROSPECTOR_SCORE(SearchEngineParam.MS_PROTEINPROSPECTOR, "MS", "MS:1002044", "ProteinProspector:score", null),
    MS_PROTEINSCAPE_INTENSITYCOVERAGE(SearchEngineParam.MS_PROTEINSCAPE, "MS", "MS:1001505", "ProteinScape:IntensityCoverage", null),
    MS_PROTEINSCAPE_PFFSOLVEREXP(SearchEngineParam.MS_PROTEINSCAPE, "MS", "MS:1001503", "ProteinScape:PFFSolverExp", null),
    MS_PROTEINSCAPE_PFFSOLVERSCORE(SearchEngineParam.MS_PROTEINSCAPE, "MS", "MS:1001504", "ProteinScape:PFFSolverScore", null),
    MS_PROTEINSCAPE_PROFOUNDPROBABILITY(SearchEngineParam.MS_PROTEINSCAPE, "MS", "MS:1001497", "ProteinScape:ProfoundProbability", null),
    MS_PROTEINSCAPE_SEARCHEVENTID(SearchEngineParam.MS_PROTEINSCAPE, "MS", "MS:1001496", "ProteinScape:SearchEventId", null),
    MS_PROTEINSCAPE_SEARCHRESULTID(SearchEngineParam.MS_PROTEINSCAPE, "MS", "MS:1001495", "ProteinScape:SearchResultId", null),
    MS_PROTEINSCAPE_SEQUESTMETASCORE(SearchEngineParam.MS_PROTEINSCAPE, "MS", "MS:1001506", "ProteinScape:SequestMetaScore", null),
    MS_SCAFFOLD_PEPTIDE_PROBABILITY(SearchEngineParam.MS_SCAFFOLD, "MS", "MS:1001568", "Scaffold:Peptide Probability", null),
    MS_SCAFFOLD_PROTEIN_PROBABILITY(SearchEngineParam.MS_SCAFFOLD, "MS", "MS:1001579", "Scaffold:Protein Probability", null),
    MS_SEQUEST_CONSENSUS_SCORE(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001163", "SEQUEST:consensus score", null),
    MS_SEQUEST_DELTACN(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001156", "SEQUEST:deltacn", null),
    MS_SEQUEST_DELTACNSTAR(SearchEngineParam.MS_SEQUEST, "MS", "MS:1002250", "SEQUEST:deltacnstar", null),
    MS_SEQUEST_EXPECTATION_VALUE(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001159", "SEQUEST:expectation value", null),
    MS_SEQUEST_MATCHED_IONS(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001161", "SEQUEST:matched ions", null),
    MS_SEQUEST_PEPTIDEIDNUMBER(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001219", "SEQUEST:PeptideIdnumber", null),
    MS_SEQUEST_PEPTIDENUMBER(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001218", "SEQUEST:PeptideNumber", null),
    MS_SEQUEST_PEPTIDERANKSP(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001217", "SEQUEST:PeptideRankSp", null),
    MS_SEQUEST_PEPTIDESP(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001215", "SEQUEST:PeptideSp", null),
    MS_SEQUEST_PROBABILITY(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001154", "SEQUEST:probability", null),
    MS_SEQUEST_SEQUENCES(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001372", "SEQUEST:Sequences", null),
    MS_SEQUEST_SF(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001160", "SEQUEST:sf", null),
    MS_SEQUEST_SP(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001157", "SEQUEST:sp", null),
    MS_SEQUEST_SPRANK(SearchEngineParam.MS_SEQUEST, "MS", "MS:1002249", "SEQUEST:sprank", null),
    MS_SEQUEST_SPSCORE(SearchEngineParam.MS_SEQUEST, "MS", "MS:1002248", "SEQUEST:spscore", null),
    MS_SEQUEST_SUM(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001374", "SEQUEST:Sum", null),
    MS_SEQUEST_TIC(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001373", "SEQUEST:TIC", null),
    MS_SEQUEST_TOTAL_IONS(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001162", "SEQUEST:total ions", null),
    MS_SEQUEST_UNIQ(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001158", "SEQUEST:Uniq", null),
    MS_SEQUEST_XCORR(SearchEngineParam.MS_SEQUEST, "MS", "MS:1001155", "SEQUEST:xcorr", null),
    MS_SONAR_SCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001502", "Sonar:Score", null),
    MS_SPECTRAST_DELTA(SearchEngineParam.MS_SPECTRAST, "MS", "MS:1001420", "SpectraST:delta", null),
    MS_SPECTRAST_DISCRIMINANT_SCORE_F(SearchEngineParam.MS_SPECTRAST, "MS", "MS:1001419", "SpectraST:discriminant score F", null),
    MS_SPECTRAST_DOT_BIAS(SearchEngineParam.MS_SPECTRAST, "MS", "MS:1001418", "SpectraST:dot_bias", null),
    MS_SPECTRAST_DOT(SearchEngineParam.MS_SPECTRAST, "MS", "MS:1001417", "SpectraST:dot", null),
    MS_SPECTRUMMILL_DISCRIMINANT_SCORE(SearchEngineParam.MS_SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION, "MS", "MS:1001580", "SpectrumMill:Discriminant Score", null),
    MS_SPECTRUMMILL_SCORE(SearchEngineParam.MS_SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION, "MS", "MS:1001572", "SpectrumMill:Score", null),
    MS_SPECTRUMMILL_SPI(SearchEngineParam.MS_SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION, "MS", "MS:1001573", "SpectrumMill:SPI", null),
    MS_SQID_DELTASCORE(SearchEngineParam.MS_SQID, "MS", "MS:1001888", "SQID:deltaScore", null),
    MS_SQID_PROTEIN_SCORE(SearchEngineParam.MS_SQID, "MS", "MS:1001889", "SQID:protein score", null),
    MS_SQID_SCORE(SearchEngineParam.MS_SQID, "MS", "MS:1001887", "SQID:score", null),
    MS_X_TANDEM_EXPECT(SearchEngineParam.MS_X_TANDEM, "MS", "MS:1001330", "X!Tandem:expect", null),
    MS_X_TANDEM_HYPERSCORE(SearchEngineParam.MS_X_TANDEM, "MS", "MS:1001331", "X!Tandem:hyperscore", null),
    MS_ZCORE_PROBSCORE(SearchEngineParam.MS_ANALYSIS_SOFTWARE, "MS", "MS:1001952", "ZCore:probScore", null),

    // PRIDE Ontology: children of PRIDE:0000049 (Peptide search engine output parameter)
    PRIDE_MASCOT_EXPECT_VALUE(SearchEngineParam.MS_MASCOT, "PRIDE", "PRIDE:0000212", "Mascot expect value", null),
    PRIDE_MASCOT_SCORE(SearchEngineParam.MS_MASCOT, "PRIDE", "PRIDE:0000069", "Mascot score", null),
    PRIDE_OMSSA_E_VALUE(SearchEngineParam.MS_OMSSA, "PRIDE", "PRIDE:0000185", "OMSSA E-value", null),
    PRIDE_OMSSA_P_VALUE(SearchEngineParam.MS_OMSSA, "PRIDE", "PRIDE:0000186", "OMSSA P-value", null),
    PRIDE_PEPSPLICE_DELSTASCORE(SearchEngineParam.PRIDE_PEPSPLICE, "PRIDE", "PRIDE:0000149", "PepSplice Deltascore", null),
    PRIDE_PEPSPLICE_FALSE_DISCOVERY_RATE(SearchEngineParam.PRIDE_PEPSPLICE, "PRIDE", "PRIDE:0000147", "PepSplice False Discovery Rate", null),
    PRIDE_PEPSPLICE_P_VALUE(SearchEngineParam.PRIDE_PEPSPLICE, "PRIDE", "PRIDE:0000148", "PepSplice P-value", null),
    PRIDE_PEPSPLICE_PENALTY(SearchEngineParam.PRIDE_PEPSPLICE, "PRIDE", "PRIDE:0000151", "PepSplice Penalty", null),
    PRIDE_PEPSPLICE_SCORE_COUNT(SearchEngineParam.PRIDE_PEPSPLICE, "PRIDE", "PRIDE:0000150", "PepSplice Score Count", null),
    PRIDE_PEPTIDE_PROPHET_DISCRIMINANT_SCORE(SearchEngineParam.PRIDE_PEPTIDE_PROPHET, "PRIDE", "PRIDE:0000138", "Discriminant score", null),
    PRIDE_PEPTIDE_PROPHET_PROBABILITY(SearchEngineParam.PRIDE_PEPTIDE_PROPHET, "PRIDE", "PRIDE:0000099", "PeptideProphet probability score", null),
    PRIDE_SEQUEST_CN(SearchEngineParam.MS_SEQUEST, "PRIDE", "PRIDE:0000052", "Cn", null),
    PRIDE_SEQUEST_DELTA_CN(SearchEngineParam.MS_SEQUEST, "PRIDE", "PRIDE:0000012", "Delta Cn", null),
    PRIDE_SEQUEST_SCORE(SearchEngineParam.MS_SEQUEST, "PRIDE", "PRIDE:0000053", "Sequest score", null),
    PRIDE_SPECTRUM_MILL_PEPTIDE_SCORE(SearchEngineParam.MS_SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION, "PRIDE", "PRIDE:0000177", "Spectrum Mill peptide score", null),
    PRIDE_X_CORRELATION(SearchEngineParam.MS_SEQUEST, "PRIDE", "PRIDE:0000013", "X correlation", null),
    PRIDE_X_TANDEM_HYPERSCORE(SearchEngineParam.MS_X_TANDEM, "PRIDE", "PRIDE:0000176", "X!Tandem Hyperscore", null),
    PRIDE_XTANDEM_EXPECTANCY_SCORE(SearchEngineParam.MS_X_TANDEM, "PRIDE", "PRIDE:0000183", "X|Tandem expectancy score", null),
    PRIDE_XTANDEM_ZSCORE(SearchEngineParam.MS_X_TANDEM, "PRIDE", "PRIDE:0000182", "X|Tandem Z score", null);

    private SearchEngineParam searchEngineParam;
    private String cvLabel;
    private String accession;
    private String name;
    private String score;

    private SearchEngineScoreParam(SearchEngineParam searchEngineParam, String cvLabel, String accession, String name, String score) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.score = score;
        this.searchEngineParam = searchEngineParam;
    }

    public CVParam getParam(String score) {
        return new CVParam(cvLabel, accession, name, score);
    }

    public CVParam getParam() {
        return new CVParam(cvLabel, accession, name, score);
    }

    public static SearchEngineScoreParam getSearchEngineScoreParamByName(String searchEngineName) {
        SearchEngineParam searchEngineParam = SearchEngineParam.findParamByName(searchEngineName);

        if (SearchEngineParam.MS_MASCOT.equals(searchEngineParam)) {
            return SearchEngineScoreParam.MS_MASCOT_SCORE;
        } else if (SearchEngineParam.MS_OMSSA.equals(searchEngineParam)) {
            return SearchEngineScoreParam.MS_OMSSA_EVALUE;
        } else if (SearchEngineParam.MS_SEQUEST.equals(searchEngineParam)) {
            return SearchEngineScoreParam.MS_SEQUEST_CONSENSUS_SCORE;
        } else if (SearchEngineParam.MS_SPECTRUM_MILL_FOR_MASSHUNTER_WORKSTATION.equals(searchEngineParam)) {
            return SearchEngineScoreParam.MS_SPECTRUMMILL_SCORE;
        } else if (SearchEngineParam.MS_X_TANDEM.equals(searchEngineParam)) {
            return SearchEngineScoreParam.MS_X_TANDEM_HYPERSCORE;
        } else if (SearchEngineParam.MS_ASCORE.equals(searchEngineParam)) {
            return SearchEngineScoreParam.MS_ASCORE_ASCORE;
        } else if (SearchEngineParam.MS_BYONIC.equals(searchEngineParam)) {
            return SearchEngineScoreParam.MS_BYONIC_BEST_SCORE;
        } else if (SearchEngineParam.MS_PERCOLATOR.equals(searchEngineParam)) {
            return SearchEngineScoreParam.MS_PERCOLATOR_SCORE;
        }

        if (searchEngineParam != null) {
            return SearchEngineScoreParam.MS_SEARCH_ENGINE_SPECIFIC_SCORE;
        }

        return null;
    }

    public static SearchEngineScoreParam getSearchEngineScoreParamByAccession(String accession) {
        for (SearchEngineScoreParam scoreParam : SearchEngineScoreParam.values()) {
            if (scoreParam.accession.equalsIgnoreCase(accession)) {
                return scoreParam;
            }
        }

        return null;
    }

    public SearchEngineParam getSearchEngineParam() {
        return searchEngineParam;
    }
}
