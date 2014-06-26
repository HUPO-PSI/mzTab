package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

/**
 * Parse and validate PSM header line into a {@link MZTabColumnFactory}.
 *
 * User: Qingwei
 * Date: 05/06/13
 */
public class PSHLineParser extends MZTabHeaderLineParser {
    public PSHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.PSM_Header), metadata);
    }

    public void parse(int lineNumber, String line, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, line, errorList);
    }

    /**
     * No optional columns defined in the PSM header line, so no refine check for it.
     */
    @Override
    protected void refine() throws MZTabException {

        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //psm_search_engine_score
        if (metadata.getPsmSearchEngineScoreMap().size() == 0) {
            throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "psm_search_engine_score[1-n]", mode.toString(), type.toString()));
        }


        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getPsmSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, "search_engine_score" + searchEngineScoreLabel);
        }

        // if PSM section is present, fixed_mod[1-n] and variable_mod[1-n] should be defined.
        if (metadata.getFixedModMap().size() == 0) {
            throw new MZTabException(new MZTabError(LogicalErrorType.FixedMod, lineNumber));
        }

        if (metadata.getVariableModMap().size() == 0) {
            throw new MZTabException(new MZTabError(LogicalErrorType.VariableMod, lineNumber));
        }
    }
}
