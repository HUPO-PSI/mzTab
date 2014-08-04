package uk.ac.ebi.pride.jmztab.checker.section;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;

/**
 * Checks if all mandatory columns in the small molecule section are properly defined depending on the mode and type of
 * the mzTab file.
 *
 * @author ntoro
 * @since 25/07/2014 16:08
 */
public class SmallMoleculeOptColumnsIntegrityChecker extends MZTabOptColumnsIntegrityChecker {

    public SmallMoleculeOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory columFactory) {
        super(metadata, columFactory);
    }

    public SmallMoleculeOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory columFactory, MZTabErrorList errorList) {
        super(metadata, columFactory, errorList);
    }


    /**
     * In "Quantification" file, following optional columns are mandatory:
     * 1. smallmolecule_abundance_study_variable[1-n]
     * 2. smallmolecule_abundance_stdev_study_variable[1-n]
     * 3. smallmolecule_abundance_std_error_study_variable[1-n]
     *
     * Beside above, in "Complete" and "Quantification" file, following optional columns also mandatory:
     * 1. search_engine_score_ms_run[1-n]
     *
     */
    @Override
    public void check() {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //smallmolecule_search_engine_score
        if (metadata.getSmallMoleculeSearchEngineScoreMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.SmallMoleculeSearchEngineScoreNotDefined, -1, "smallmolecule_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getSmallMoleculeSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, columnFactory, "best_search_engine_score" + searchEngineScoreLabel);
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getSmallMoleculeQuantificationUnit() == null) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "smallmolecule-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, columnFactory, "smallmolecule_abundance" + svLabel);
                refineOptionalColumn(mode, type, columnFactory, "smallmolecule_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, columnFactory, "smallmolecule_abundance_std_error" + svLabel);
            }
            for (Assay assay : metadata.getAssayMap().values()) {
                String assayLabel = "_assay[" + assay.getId() + "]";
                refineOptionalColumn(mode, type, columnFactory, "smallmolecule_abundance" + assayLabel);
            }

            if (mode == MZTabDescription.Mode.Complete) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    for (SearchEngineScore searchEngineScore : metadata.getSmallMoleculeSearchEngineScoreMap().values()) {
                        String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                        refineOptionalColumn(mode, type, columnFactory, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                    }
                }
            }
        }
    }
}
