package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

/**
 * Parse and validate Small Molecule header line into a {@link MZTabColumnFactory}.
 *
 * User: Qingwei
 * Date: 10/02/13
 */
public class SMHLineParser extends MZTabHeaderLineParser {
    public SMHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Small_Molecule_Header), metadata);
    }

    public void parse(int lineNumber, String line, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, line, errorList);
    }

    /**
     * In "Quantification" file, following optional columns are mandatory provide:
     * 1. smallmolecule_abundance_study_variable[1-n]
     * 2. smallmolecule_abundance_stdev_study_variable[1-n]
     * 3. smallmolecule_abundance_std_error_study_variable[1-n]
     *
     * Beside above, in "Complete" and "Quantification" file, following optional columns also mandatory provide:
     * 1. search_engine_score_ms_run[1-n]
     *
     * NOTICE: this hock method will be called at end of parse() function.
     *
     * @see MZTabHeaderLineParser#parse(int, String, uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList)
     * @see #refineOptionalColumn(uk.ac.ebi.pride.jmztab.model.MZTabDescription.Mode, uk.ac.ebi.pride.jmztab.model.MZTabDescription.Type, String)
     */
    @Override
    protected void refine() throws MZTabException {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, "best_search_engine_score" + searchEngineScoreLabel);
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getSmallMoleculeQuantificationUnit() == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "smallmolecule-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, "smallmolecule_abundance" + svLabel);
                refineOptionalColumn(mode, type, "smallmolecule_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, "smallmolecule_abundance_std_error" + svLabel);
            }
            for (Assay assay : metadata.getAssayMap().values()) {
                String assayLabel = "_assay[" + assay.getId() + "]";
                refineOptionalColumn(mode, type, "smallmolecule_abundance" + assayLabel);
            }

            if (mode == MZTabDescription.Mode.Complete) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    for (SearchEngineScore searchEngineScore : metadata.getSearchEngineScoreMap().values()) {
                        String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                        refineOptionalColumn(mode, type, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                    }
                }
            }
        }
    }
}
