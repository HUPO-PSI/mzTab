package uk.ac.ebi.pride.jmztab.checker.section;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;

/**
 * Checks if all mandatory columns in the protein section are properly defined depending on the mode and type of the mzTab file.
 *
 * @author ntoro
 * @since 25/07/2014 16:07
 */
public class ProteinOptColumnsIntegrityChecker extends MZTabOptColumnsIntegrityChecker {

    public ProteinOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory columFactory) {
        super(metadata, columFactory);
    }

    public ProteinOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory columFactory, MZTabErrorList errorList) {
        super(metadata, columFactory, errorList);
    }

    /**
     * In all the modes following optional columns are mandatory:
     * 1. best_search_engine_score[1-n]
     *
     * In "Quantification" file, following optional columns are mandatory:
     * 1. protein_abundance_study_variable[1-n]
     * 2. protein_abundance_stdev_study_variable[1-n]
     * 3. protein_abundance_std_error_study_variable[1-n]
     *
     * In "Complete" and "Identification" file, following optional columns are also mandatory:
     * 1. search_engine_score[1-n]_ms_run[1-n]
     * 2. num_psms_ms_run[1-n]
     * 3. num_peptides_distinct_ms_run[1-n]
     * 4. num_peptides_unique_ms_run[1-n]
     *
     * In "Complete" and "Quantification" file, following optional columns are also mandatory:
     * 1. search_engine_score[1-n]_ms_run[1-n]
     * 2. protein_abundance_assay[1-n]
     *
     */
    public void check() {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //We check that protein_search_engine_score is defined
        if (metadata.getProteinSearchEngineScoreMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.ProteinSearchEngineScoreNotDefined, -1, "protein_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, columnFactory, "best_search_engine_score" + searchEngineScoreLabel);
        }

        if (mode == MZTabDescription.Mode.Complete) {

            //Mandatory for all complete (Quantification and Identification)
            for (MsRun msRun : metadata.getMsRunMap().values()) {
                String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                for (SearchEngineScore searchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
                    String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                    refineOptionalColumn(mode, type, columnFactory, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                }
            }

            if (type == MZTabDescription.Type.Identification) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    refineOptionalColumn(mode, type, columnFactory, "num_psms" + msRunLabel);
                    refineOptionalColumn(mode, type, columnFactory, "num_peptides_distinct" + msRunLabel);
                    refineOptionalColumn(mode, type, columnFactory, "num_peptides_unique" + msRunLabel);
                }
            } else { // Quantification and Complete
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, columnFactory, "protein_abundance" + assayLabel);
                }
            }
        }

        if (type == MZTabDescription.Type.Quantification) { //Summary and Complete
            if (metadata.getProteinQuantificationUnit() == null) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "protein-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, columnFactory, "protein_abundance" + svLabel);
                refineOptionalColumn(mode, type, columnFactory, "protein_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, columnFactory, "protein_abundance_std_error" + svLabel);
            }
        }
    }

}
