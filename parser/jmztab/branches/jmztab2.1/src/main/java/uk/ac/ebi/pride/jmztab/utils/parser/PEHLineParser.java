package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PEHLineParser extends MZTabHeaderLineParser {
    public PEHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Peptide_Header), metadata);
    }

    public void parse(int lineNumber, String line) throws MZTabException {
        super.parse(lineNumber, line);
    }

    @Override
    protected void refine() throws MZTabException {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        if (mode == MZTabDescription.Mode.Complete) {
            if (type == MZTabDescription.Type.Quantification) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    refineOptionalColumn(mode, type, "search_engine_score" + msRunLabel);
                }
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, "peptide_abundance" + assayLabel);
                }
            }
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getPeptideQuantificationUnit() == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "peptide-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, "peptide_abundance" + svLabel);
                refineOptionalColumn(mode, type, "peptide_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, "peptide_abundance_std_error" + svLabel);
            }
        }
    }
}
