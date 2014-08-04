package uk.ac.ebi.pride.jmztab.checker.section;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;

/**
 * Checks if all mandatory columns in the peptide section are properly defined depending on the mode and type of the mzTab file.
 *
 * @author ntoro
 * @since 25/07/2014 16:08
 */
public class PeptideOptColumnsIntegrityChecker extends MZTabOptColumnsIntegrityChecker {

    public PeptideOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory peptideFactory) {
        this(metadata, peptideFactory, null);
    }

    public PeptideOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory peptideFactory, MZTabErrorList errorList) {
        super(metadata, peptideFactory, errorList);
    }

    /**
     * In "Quantification" file, following optional columns are mandatory:
     * 1. peptide_abundance_study_variable[1-n]
     * 2. peptide_abundance_stdev_study_variable[1-n]
     * 3. peptide_abundance_std_error_study_variable[1-n]
     * 4. best_search_engine_score[1-n]
     *
     * Beside above, in "Complete" and "Quantification" file, following optional columns are also mandatory:
     * 1. search_engine_score[1-n]_ms_run[1-n]
     * 2. peptide_abundance_assay[1-n]
     * 3. spectra_ref             // This is special, currently all "Quantification" file's peptide line header
     *                            // should provide, because it is difficult to judge MS2 based quantification employed.
     *
     * NOTICE: this method will be called at end of parse() function.
     */
    @Override
    public void check() {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();


        //peptide_search_engine_score
        if (metadata.getPeptideSearchEngineScoreMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.PeptideSearchEngineScoreNotDefined, -1, "peptide_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getPeptideQuantificationUnit() == null) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "peptide-quantification_unit", mode.toString(), type.toString()));
            }
            for (SearchEngineScore searchEngineScore : metadata.getPeptideSearchEngineScoreMap().values()) {
                String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                refineOptionalColumn(mode, type, columnFactory, "best_search_engine_score" + searchEngineScoreLabel);
            }

            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, columnFactory, "peptide_abundance" + svLabel);
                refineOptionalColumn(mode, type, columnFactory, "peptide_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, columnFactory, "peptide_abundance_std_error" + svLabel);
            }
            if (mode == MZTabDescription.Mode.Complete) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    for (SearchEngineScore searchEngineScore : metadata.getPeptideSearchEngineScoreMap().values()) {
                        String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                        refineOptionalColumn(mode, type, columnFactory, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                    }
                }
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, columnFactory, "peptide_abundance" + assayLabel);
                }
            }
        }
    }

}
