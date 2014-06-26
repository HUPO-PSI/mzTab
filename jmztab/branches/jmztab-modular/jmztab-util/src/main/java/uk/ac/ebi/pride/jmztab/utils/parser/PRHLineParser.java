package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

/**
 * Parse and validate Protein header line into a {@link MZTabColumnFactory}.
 *
 * User: Qingwei
 * Date: 10/02/13
 */
public class PRHLineParser extends MZTabHeaderLineParser {
    public PRHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Protein_Header), metadata);
    }

    public void parse(int lineNumber, String line, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, line, errorList);
    }

    //TODO review doc

    /**
     * Principle 1: in "Quantification" file, following optional columns are mandatory provide:
     * 1. protein_abundance_study_variable[1-n]
     * 2. protein_abundance_stdev_study_variable[1-n]
     * 3. protein_abundance_std_error_study_variable[1-n]
     *
     * In "Complete" and "Identification" file, following optional columns also mandatory provide:
     * 1. search_engine_score_ms_run[1-n]
     * 2. num_psms_ms_run[1-n]
     * 3. num_peptides_distinct_ms_run[1-n]
     * 4. num_peptides_unique_ms_run[1-n]
     *
     * Beside principle 1, in "Complete" and "Quantification" file, following optional columns also mandatory provide:
     * 1. search_engine_score_ms_run[1-n]
     * 2. protein_abundance_assay[1-n]
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

        //protein_search_engine_score
        if (metadata.getProteinSearchEngineScoreMap().size() == 0) {
            throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "protein_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, "best_search_engine_score" + searchEngineScoreLabel);
        }

        if (mode == MZTabDescription.Mode.Complete) {

            //Mandatory for all complete (Quantification and Identification)
            for (MsRun msRun : metadata.getMsRunMap().values()) {
                String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                for (SearchEngineScore searchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
                    String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                    refineOptionalColumn(mode, type, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                }
            }

            if (type == MZTabDescription.Type.Identification) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    refineOptionalColumn(mode, type, "num_psms" + msRunLabel);
                    refineOptionalColumn(mode, type, "num_peptides_distinct" + msRunLabel);
                    refineOptionalColumn(mode, type, "num_peptides_unique" + msRunLabel);
                }
            } else { // Quantification and Complete
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, "protein_abundance" + assayLabel);
                }
            }
        }

        if (type == MZTabDescription.Type.Quantification) { //Summary and Complete
            if (metadata.getProteinQuantificationUnit() == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "protein-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, "protein_abundance" + svLabel);
                refineOptionalColumn(mode, type, "protein_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, "protein_abundance_std_error" + svLabel);
            }
        }
    }
}
