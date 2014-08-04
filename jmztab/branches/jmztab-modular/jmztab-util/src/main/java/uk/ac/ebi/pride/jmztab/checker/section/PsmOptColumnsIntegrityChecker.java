package uk.ac.ebi.pride.jmztab.checker.section;

import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabDescription;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.SearchEngineScore;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;

/**
 * Checks if all mandatory columns in the PSM section are properly defined depending on the mode and type of the mzTab file.

 * @author ntoro
 * @since 25/07/2014 16:07
 */
public class PsmOptColumnsIntegrityChecker extends MZTabOptColumnsIntegrityChecker {

    public PsmOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory columFactory) {
        super(metadata, columFactory);
    }

    public PsmOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory columFactory, MZTabErrorList errorList) {
        super(metadata, columFactory, errorList);
    }

    /**
     * In all the modes following optional columns are mandatory:
     * 1. search_engine_score[1-n]
     */
    public void check() {

        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //psm_search_engine_score
        if (metadata.getPsmSearchEngineScoreMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.PSMSearchEngineScoreNotDefined, -1, "psm_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getPsmSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, columnFactory, "search_engine_score" + searchEngineScoreLabel);
        }
    }
}
