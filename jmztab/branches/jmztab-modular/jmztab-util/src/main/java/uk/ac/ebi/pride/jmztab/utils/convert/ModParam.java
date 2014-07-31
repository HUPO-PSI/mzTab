package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.Mod;

/**
 * @author ntoro
 * @since 05/06/2014 11:48
 */
public enum ModParam {

    //LABELS
    MOD_15_N_LABELED_RESIDUE("MOD", "MOD:00843", "(15)N labeled residue", null, false),
    MOD_18_O_LABEL_AT_BOTH_C_TERMINAL_OXYGENS("MOD", "MOD:00546", "(18)O label at both C-terminal oxygens", null, false),
    MOD_18_O_LABELED_DEGLYCOSYLATED_ASPARAGINE("MOD", "MOD:00531", "(18)O labeled deglycosylated asparagine", null, false),
    MOD_1X_18_O_LABELED_DEAMIDATED_L_ASPARAGINE("MOD", "MOD:01293", "1x(18)O labeled deamidated L-asparagine", null, false),
    MOD_2X_13_C_6X_2_H_LABELED_DIMETHYLATED_L_ARGININE("MOD", "MOD:00638", "2x(13)C,6x(2)H labeled dimethylated L-arginine", null, false),
    MOD_2X_15_N_LABELED_L_LYSINE("MOD", "MOD:01603", "2x(15)N labeled L-lysine", null, false),
    MOD_2XC_13_3X_2_H_LABELED_N6_ACETYL_L_LYSINE("MOD", "MOD:01965", "2xC(13, null, false),3x(2)H labeled N6-acetyl-L-lysine", null, false),
    MOD_3X_12_C_LABELED_N6_PROPANOYL_L_LYSINE("MOD", "MOD:01232", "3x(12)C labeled N6-propanoyl-L-lysine", null, false),
    MOD_3X_13_C_LABELED_N6_PROPANOYL_L_LYSINE("MOD", "MOD:01231", "3x(13)C labeled N6-propanoyl-L-lysine", null, false),
    MOD_3X_2_H_LABELED_L_ASPARTIC_ACID_4_METHYL_ESTER("MOD", "MOD:01241", "3x(2)H labeled L-aspartic acid 4-methyl ester", null, false),
    MOD_3X_2_H_LABELED_N6_ACETYL_L_LYSINE("MOD", "MOD:01233", "3x(2)H labeled N6-acetyl-L-lysine", null, false),
    MOD_3X_2_H_RESIDUE_METHYL_ESTER("MOD", "MOD:00617", "3x(2)H residue methyl ester", null, false),
    MOD_4X_15_N_LABELED_L_ARGININE("MOD", "MOD:01604", "4x(15)N labeled L-arginine", null, false),
    MOD_4X_2_H_LABELED_ALPHA_DIMETHYLAMINO_N_TERMINAL_RESIDUE("MOD", "MOD:01459", "4x(2)H labeled alpha-dimethylamino N-terminal residue", null, false),
    MOD_4X_2_H_LABELED_DIMETHYLATED_L_LYSINE("MOD", "MOD:01254", "4x(2)H labeled dimethylated L-lysine", null, false),
    MOD_4X_2_H_LABELED_DIMETHYLATED_RESIDUE("MOD", "MOD:00552", "4x(2)H labeled dimethylated residue", null, false),
    MOD_5X_13_C_1X_15_N_LABELED_L_METHIONINE_SULFOXIDE("MOD", "MOD:01812", "5x(13)C,1x(15)N labeled L-methionine sulfoxide", null, false),
    MOD_5X_13_C_1X_15_N_LABELED_L_VALINE("MOD", "MOD:00588", "5x(13)C,1x(15)N labeled L-valine", null, false),
    MOD_5X_13_C_LABELED_L_METHIONINE_SULFONE("MOD", "MOD:01835", "5x(13)C-labeled L-methionine sulfone", null, false),
    MOD_6X_13_C_1X_15_N_LABELED_L_ISOLEUCINE("MOD", "MOD:01286", "6x(13)C,1x(15)N labeled L-isoleucine", null, false),
    MOD_6X_13_C_2X_15_N_LABELED_L_LYSINE("MOD", "MOD:00582", "6x(13)C,2x(15)N labeled L-lysine", null, false),
    MOD_6X_13_C_4X_15_N_LABELED_L_ARGININE("MOD", "MOD:00587", "6x(13)C,4x(15)N labeled L-arginine", null, false),
    MOD_6X_13_C_LABELED_L_ARGININE("MOD", "MOD:01331", "6x(13)C labeled L-arginine", null, false),
    MOD_6X_13_C_LABELED_L_ISOLEUCINE("MS", "MOD:01333", "6x(13)C labeled L-isoleucine", null, false),
    MOD_6X_13_C_LABELED_L_LEUCINE("MS", "MOD:01332", "6x(13)C labeled L-leucine", null, false),
    MOD_6X_13_C_LABELED_L_LYSINE("MOD", "MOD:01334", "6x(13)C labeled L-lysine", null, false),
    MOD_6X_13_C_LABELED_RESIDUE("MOD", "MOD:00544", "6x(13)C labeled residue", null, false),
    MOD_9X_13_C_LABELED_RESIDUE("MOD", "MOD:00540", "9x(13)C labeled residue", null, false),

    MOD_ACETATE_LABELING_REAGENT_N_TERM_HEAVY_FORM__3AMU_("MOD", "MOD:00449", "acetate labeling reagent (N-term) (heavy form, _3amu)", null, false),

    //ICAT
    MOD_APPLIED_BIOSYSTEMS_CLEAVABLE_ICAT_TM_HEAVY("MOD", "MOD:00481", "Applied Biosystems cleavable ICAT(TM) heavy", null, false),
    MOD_APPLIED_BIOSYSTEMS_CLEAVABLE_ICAT_TM_LIGHT("MOD", "MOD:00480", "Applied Biosystems cleavable ICAT(TM) light", null, false),
    MOD_APPLIED_BIOSYSTEMS_ORIGINAL_ICAT_TM_D8_MODIFIED_CYSTEINE("MOD", "MOD:00405", "Applied Biosystems original ICAT(TM) d8 modified cysteine", null, false),

    //ICPL
    MOD_BRUKER_DALTONICS_SERVA_ICPL_TM_QUANTIFICATION_CHEMISTRY_HEAVY_FORM_SITE_K("MOD", "MOD:01287", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry, heavy form - site K", null, false),
    MOD_BRUKER_DALTONICS_SERVA_ICPL_TM_QUANTIFICATION_CHEMISTRY_LIGHT_FORM_SITE_K("MOD", "MOD:01230", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry, light form - site K", null, false),
    MOD_BRUKER_DALTONICS_SERVA_ICPL_TM_QUANTIFICATION_CHEMISTRY_MEDIUM_FORM_SITE_K("MOD", "MOD:01359", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry, medium form - site K", null, false),
    MOD_BRUKER_DALTONICS_SERVA_ICPL_TM_QUANTIFICATION_CHEMISTRY_MEDIUM_FORM_SITE_N_TERM("MOD", "MOD:01358", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry, medium form - site N-term", null, false),
    MOD_BRUKER_DALTONICS_SERVA_ICPL_TM_QUANTIFICATION_CHEMISTRY("MOD", "MOD:00789", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry", null, false),

    MOD_ISOTOPE_TAGGED_REAGENT_DERIVATIZED_RESIDUE("MOD", "MOD:01426", "isotope tagged reagent derivatized residue", null, false),

    //ITRAQ
    MOD_APPLIED_BIOSYSTEMS_ITRAQ_TM_MULTIPLEXED_QUANTITATION_CHEMISTRY("MOD", "MOD:00564", "Applied Biosystems iTRAQ(TM) multiplexed quantitation chemistry", null, false),
    MOD_ITRAQ4PLEX_114_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MOD", "MOD:01486", "iTRAQ4plex-114 reporter+balance reagent acylated N-terminal", null, false),
    MOD_ITRAQ4PLEX_114_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01485", "iTRAQ4plex-114 reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ4PLEX_114_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MOD", "MOD:01487", "iTRAQ4plex-114 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_ITRAQ4PLEX_114_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MOD", "MOD:01488", "iTRAQ4plex-114 reporter+balance reagent O4';-acylated tyrosine", null, false),
    MOD_ITRAQ4PLEX_115_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MOD", "MOD:01493", "iTRAQ4plex-115 reporter+balance reagent acylated N-terminal", null, false),
    MOD_ITRAQ4PLEX_116_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MOD", "MOD:01500", "iTRAQ4plex-116 reporter+balance reagent acylated N-terminal", null, false),
    MOD_ITRAQ4PLEX_116_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01499", "iTRAQ4plex-116 reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ4PLEX_116_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01503", "iTRAQ4plex-116 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_ITRAQ4PLEX_116_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MS", "MOD:01501", "iTRAQ4plex-116 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_ITRAQ4PLEX_116_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01504", "iTRAQ4plex-116 reporter+balance reagent O3-acylated serine", null, false),
    MOD_ITRAQ4PLEX_116_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01505", "iTRAQ4plex-116 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_ITRAQ4PLEX_116_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01502", "iTRAQ4plex-116 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_ITRAQ4PLEX_117_MTRAQ_HEAVY_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01506", "iTRAQ4plex-117, mTRAQ heavy, reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ4PLEX_117_MTRAQ_HEAVY_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01509", "iTRAQ4plex-117, mTRAQ heavy, reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_ITRAQ4PLEX_117_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01510", "iTRAQ4plex-117 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_ITRAQ4PLEX_117_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MOD", "MOD:01508", "iTRAQ4plex-117 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_ITRAQ4PLEX_117_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01511", "iTRAQ4plex-117 reporter+balance reagent O3-acylated serine", null, false),
    MOD_ITRAQ4PLEX_117_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01512", "iTRAQ4plex-117 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_ITRAQ4PLEX_117("MOD", "MOD:01507", "iTRAQ4plex-117", null, false),
    MOD_ITRAQ4PLEX_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01518", "iTRAQ4plex reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ4PLEX_REPORTER_BALANCE_REAGENT_N_ACYLATED_RESIDUE("MOD", "MOD:01709", "iTRAQ4plex reporter+balance reagent N-acylated residue", null, false),
    MOD_ITRAQ4PLEX_REPORTER_FRAGMENT("MOD", "MOD:01521", "iTRAQ4plex reporter fragment", null, false),
    MOD_ITRAQ8PLEX_113_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01529", "iTRAQ8plex-113 reporter+balance reagent acylated N-terminal", null, false),
    MOD_ITRAQ8PLEX_113_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01528", "iTRAQ8plex-113 reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ8PLEX_113_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01532", "iTRAQ8plex-113 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_ITRAQ8PLEX_113_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MOD", "MOD:01530", "iTRAQ8plex-113 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_ITRAQ8PLEX_113_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01533", "iTRAQ8plex-113 reporter+balance reagent O3-acylated serine", null, false),
    MOD_ITRAQ8PLEX_113_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01534", "iTRAQ8plex-113 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_ITRAQ8PLEX_113_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MOD", "MOD:01531", "iTRAQ8plex-113 reporter+balance reagent O4';-acylated tyrosine", null, false),
    MOD_ITRAQ8PLEX_114_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MOD", "MOD:01536", "iTRAQ8plex-114 reporter+balance reagent acylated N-terminal", null, false),
    MOD_ITRAQ8PLEX_114_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01535", "iTRAQ8plex-114 reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ8PLEX_114_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01539", "iTRAQ8plex-114 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_ITRAQ8PLEX_114_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MOD", "MOD:01537", "iTRAQ8plex-114 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_ITRAQ8PLEX_114_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01540", "iTRAQ8plex-114 reporter+balance reagent O3-acylated serine", null, false),
    MOD_ITRAQ8PLEX_114_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01541", "iTRAQ8plex-114 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_ITRAQ8PLEX_114_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MOD", "MOD:01538", "iTRAQ8plex-114 reporter+balance reagent O4';-acylated tyrosine", null, false),
    MOD_ITRAQ8PLEX_115_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01542", "iTRAQ8plex-115 reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ8PLEX_116_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01550", "iTRAQ8plex-116 reporter+balance reagent acylated N-terminal", null, false),
    MOD_ITRAQ8PLEX_116_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01549", "iTRAQ8plex-116 reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ8PLEX_116_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01553", "iTRAQ8plex-116 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_ITRAQ8PLEX_116_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MOD", "MOD:01551", "iTRAQ8plex-116 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_ITRAQ8PLEX_116_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01554", "iTRAQ8plex-116 reporter+balance reagent O3-acylated serine", null, false),
    MOD_ITRAQ8PLEX_116_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01555", "iTRAQ8plex-116 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_ITRAQ8PLEX_116_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01552", "iTRAQ8plex-116 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_ITRAQ8PLEX_117_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01557", "iTRAQ8plex-117 reporter+balance reagent acylated N-terminal", null, false),
    MOD_ITRAQ8PLEX_117_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01556", "iTRAQ8plex-117 reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ8PLEX_117_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01560", "iTRAQ8plex-117 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_ITRAQ8PLEX_117_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MS", "MOD:01558", "iTRAQ8plex-117 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_ITRAQ8PLEX_117_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01561", "iTRAQ8plex-117 reporter+balance reagent O3-acylated serine", null, false),
    MOD_ITRAQ8PLEX_117_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01562", "iTRAQ8plex-117 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_ITRAQ8PLEX_117_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01559", "iTRAQ8plex-117 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_ITRAQ8PLEX_118_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01563", "iTRAQ8plex-118 reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ8PLEX_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MOD", "MOD:01712", "iTRAQ8plex reporter+balance reagent acylated N-terminal", null, false),
    MOD_ITRAQ8PLEX_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01526", "iTRAQ8plex reporter+balance reagent acylated residue", null, false),
    MOD_ITRAQ8PLEX_REPORTER_BALANCE_REAGENT_DERIVATIZED_RESIDUE("MOD", "MOD:1526", "iTRAQ8plex reporter+balance reagent derivatized residue", null, false),

    MOD_MTRAQ_LIGHT_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MOD", "MOD:01865", "mTRAQ light reporter+balance reagent acylated N-terminal", null, false),
    MOD_MTRAQ_LIGHT_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MOD", "MOD:01866", "mTRAQ light reporter+balance reagent N6-acylated lysine", null, false),

    MOD_PROPANOYL_LABELING_REAGENT_HEAVY_FORM__3AMU_K_("MOD", "MOD:00452", "propanoyl labeling reagent heavy form (_3amu) (K)", null, false),
    MOD_PROPANOYL_LABELING_REAGENT_LIGHT_FORM_N_TERM_("MOD", "MOD:00451", "propanoyl labeling reagent light form (N-term)", null, false),

    //TMT6PLEX
    MOD_TMT6PLEX_126_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01721", "TMT6plex-126 reporter+balance reagent acylated N-terminal", null, false),
    MOD_TMT6PLEX_126_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MOD", "MOD:01720", "TMT6plex-126 reporter+balance reagent acylated residue", null, false),
    MOD_TMT6PLEX_126_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01724", "TMT6plex-126 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_TMT6PLEX_126_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MS", "MOD:01722", "TMT6plex-126 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_TMT6PLEX_126_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01725", "TMT6plex-126 reporter+balance reagent O3-acylated serine", null, false),
    MOD_TMT6PLEX_126_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01726", "TMT6plex-126 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_TMT6PLEX_126_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01723", "TMT6plex-126 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_TMT6PLEX_127_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01729", "TMT6plex-127 reporter+balance reagent acylated N-terminal", null, false),
    MOD_TMT6PLEX_127_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01728", "TMT6plex-127 reporter+balance reagent acylated residue", null, false),
    MOD_TMT6PLEX_127_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01732", "TMT6plex-127 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_TMT6PLEX_127_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MS", "MOD:01730", "TMT6plex-127 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_TMT6PLEX_127_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01733", "TMT6plex-127 reporter+balance reagent O3-acylated serine", null, false),
    MOD_TMT6PLEX_127_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01734", "TMT6plex-127 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_TMT6PLEX_127_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01731", "TMT6plex-127 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_TMT6PLEX_128_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01737", "TMT6plex-128 reporter+balance reagent acylated N-terminal", null, false),
    MOD_TMT6PLEX_128_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01736", "TMT6plex-128 reporter+balance reagent acylated residue", null, false),
    MOD_TMT6PLEX_128_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01740", "TMT6plex-128 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_TMT6PLEX_128_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MS", "MOD:01738", "TMT6plex-128 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_TMT6PLEX_128_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01741", "TMT6plex-128 reporter+balance reagent O3-acylated serine", null, false),
    MOD_TMT6PLEX_128_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01742", "TMT6plex-128 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_TMT6PLEX_128_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01739", "TMT6plex-128 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_TMT6PLEX_129_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01745", "TMT6plex-129 reporter+balance reagent acylated N-terminal", null, false),
    MOD_TMT6PLEX_129_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01744", "TMT6plex-129 reporter+balance reagent acylated residue", null, false),
    MOD_TMT6PLEX_129_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01748", "TMT6plex-129 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_TMT6PLEX_129_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MS", "MOD:01746", "TMT6plex-129 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_TMT6PLEX_129_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01749", "TMT6plex-129 reporter+balance reagent O3-acylated serine", null, false),
    MOD_TMT6PLEX_129_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01750", "TMT6plex-129 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_TMT6PLEX_129_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01747", "TMT6plex-129 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_TMT6PLEX_130_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01753", "TMT6plex-130 reporter+balance reagent acylated N-terminal", null, false),
    MOD_TMT6PLEX_130_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01752", "TMT6plex-130 reporter+balance reagent acylated residue", null, false),
    MOD_TMT6PLEX_130_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01756", "TMT6plex-130 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_TMT6PLEX_130_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MS", "MOD:01754", "TMT6plex-130 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_TMT6PLEX_130_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01757", "TMT6plex-130 reporter+balance reagent O3-acylated serine", null, false),
    MOD_TMT6PLEX_130_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01758", "TMT6plex-130 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_TMT6PLEX_130_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE("MS", "MOD:01755", "TMT6plex-130 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_TMT6PLEX_131_REPORTER_BALANCE_REAGENT_ACYLATED_N_TERMINAL("MS", "MOD:01761", "TMT6plex-131 reporter+balance reagent acylated N-terminal", null, false),
    MOD_TMT6PLEX_131_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01760", "TMT6plex-131 reporter+balance reagent acylated residue", null, false),
    MOD_TMT6PLEX_131_REPORTER_BALANCE_REAGENT_N_ACYLATED_HISTIDINE("MS", "MOD:01764", "TMT6plex-131 reporter+balance reagent N'-acylated histidine", null, false),
    MOD_TMT6PLEX_131_REPORTER_BALANCE_REAGENT_N6_ACYLATED_LYSINE("MOD", "MOD:01762", "TMT6plex-131 reporter+balance reagent N6-acylated lysine", null, false),
    MOD_TMT6PLEX_131_REPORTER_BALANCE_REAGENT_O3_ACYLATED_SERINE("MS", "MOD:01765", "TMT6plex-131 reporter+balance reagent O3-acylated serine", null, false),
    MOD_TMT6PLEX_131_REPORTER_BALANCE_REAGENT_O3_ACYLATED_THREONINE("MS", "MOD:01766", "TMT6plex-131 reporter+balance reagent O3-acylated threonine", null, false),
    MOD_TMT6PLEX_131_REPORTER_BALANCE_REAGENT_O4_ACYLATED_TYROSINE_("MS", "MOD:01763", "TMT6plex-131 reporter+balance reagent O4'-acylated tyrosine", null, false),
    MOD_TMT6PLEX_REPORTER_BALANCE_REAGENT_ACYLATED_RESIDUE("MS", "MOD:01715", "TMT6plex reporter+balance reagent acylated residue", null, false),
    MOD_TMT6PLEX_REPORTER_FRAGMENT("MOD", "MOD:01716", "TMT6plex reporter fragment", null, false),
    MOD_CYSTMT6PLEX_126_REPORTER_BALANCE_REAGENT_CYSTEINE_DISULFIDE("MOD", "MOD:01823", "cysTMT6plex-126 reporter+balance reagent cysteine disulfide", null, false),
    MOD_CYSTMT6PLEX_REPORTER_BALANCE_REAGENT_CYSTEINE_DISULFIDE("MOD", "MOD:01821", "cysTMT6plex reporter+balance reagent cysteine disulfide", null, false),
    MOD_CYSTMT6PLEX_ZERO_REPORTER_BALANCE_REAGENT_CYSTEINE_DISULFIDE("MOD", "MOD:01822", "cysTMT6plex-zero reporter+balance reagent cysteine disulfide", null, false),

    UNIMOD_APPLIED_BIOSYSTEMS_CLEAVABLE_ICAT_TM_HEAVY("UNIMOD", "UNIMOD:106", "Applied Biosystems cleavable ICAT(TM) heavy", null, false),
    UNIMOD_APPLIED_BIOSYSTEMS_CLEAVABLE_ICAT_TM_LIGHT("UNIMOD", "UNIMOD:105", "Applied Biosystems cleavable ICAT(TM) light", null, false),
    UNIMOD_ITRAQ4PLEX("UNIMOD", "UNIMOD:214", "iTRAQ4plex", null, false),
    UNIMOD_ITRAQ8PLEX_13C_6_15N_2_("UNIMOD", "UNIMOD:731", "iTRAQ8plex:13C(6)15N(2)", null, false),
    UNIMOD_ITRAQ8PLEX("UNIMOD", "UNIMOD:730", "iTRAQ8plex", null, false),
    UNIMOD_LABEL_18O_1_("UNIMOD", "UNIMOD:258", "Label:18O(1)", null, false),
    UNIMOD_LABEL_18O_2_("UNIMOD", "UNIMOD:193", "Label:18O(2)", null, false),
    UNIMOD_TMT6PLEX("UNIMOD", "UNIMOD:737", "TMT6plex", null, false),

    //Carbamidomethylation
    MOD_S_N_ISOPROPYLCARBOXAMIDOMETHYL_L_CYSTEINE("MOD", "MOD:00410", "S-(N-isopropylcarboxamidomethyl)-L-cysteine", null, false),
    MOD_S_CARBOXAMIDOETHYL_L_CYSTEINE("MOD", "MOD:00417", "S-carboxamidoethyl-L-cysteine", null, false),
    MOD_S_CARBOXAMIDOMETHYL_L_CYSTEINE("MOD", "MOD:01060", "S-carboxamidomethyl-L-cysteine", null, false),
    MOD_S_CARBOXAMIDOMETHYL_L_CYSTEINE_SULFONE("MOD", "MOD:01831", "S-carboxamidomethyl-L-cysteine sulfone", null, false),
    MOD_S_CARBOXAMIDOMETHYL_L_CYSTEINE_SULFOXIDE("MOD", "MOD:01793", "S-carboxamidomethyl-L-cysteine sulfoxide", null, false),
    MOD_S_CARBOXYMETHYL_L_CYSTEINE("MOD", "MOD:01061", "S-carboxymethyl-L-cysteine", null, false),

    //Methylthio
    UNIMOD_METHYLTHIO("UNIMOD", "UNIMOD:39", "Methylthio", null, false),
    MOD_METHYLTHIOLATED_RESIDUE("MOD", "MOD:01153", "methylthiolated residue", null, false);

    private String cvLabel;
    private String accession;
    private String name;
    private String value;
    private Boolean biological;

    private ModParam(String cvLabel, String accession, String name, String value, Boolean biological) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.value = value;
        this.biological = biological;
    }

    public static boolean isBiological(String accession) {
        for (ModParam modParam : ModParam.values()) {
            if (modParam.accession.equalsIgnoreCase(accession)) {
                return modParam.getBiological();
            }
        }

        return true;
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

    public Boolean getBiological() {
        return biological;
    }
}
