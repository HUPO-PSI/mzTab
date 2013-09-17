package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

/**
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

    @Override
    protected void refine() throws MZTabException {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

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
        }
    }
}
