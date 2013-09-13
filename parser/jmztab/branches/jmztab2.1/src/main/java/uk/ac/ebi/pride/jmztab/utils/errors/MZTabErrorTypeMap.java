package uk.ac.ebi.pride.jmztab.utils.errors;

import java.util.HashMap;
import java.util.Map;

/**
 * This list class used to storage all types of MZTabError, and provide query service based on
 * the error/warn code.
 *
 * User: qingwei
 * Date: 27/02/13
 */
public class MZTabErrorTypeMap {
    private Map<Integer, MZTabErrorType> typeMap = new HashMap<Integer, MZTabErrorType>();

    private void add(MZTabErrorType type) {
        typeMap.put(type.getCode(), type);
    }

    public MZTabErrorTypeMap() {
//        add(FormatErrorType.LinePrefix);
//        add(FormatErrorType.CountMatch);
//        add(FormatErrorType.IndexedElement);
//        add(FormatErrorType.AbundanceColumn);
//        add(FormatErrorType.MsRunOptionalColumn);
//        add(FormatErrorType.OptionalColumn);
//        add(FormatErrorType.OptionalCVParamColumn);
//        add(FormatErrorType.StableColumn);
//        add(FormatErrorType.MTDLine);
//        add(FormatErrorType.MTDDefineLabel);
//        add(FormatErrorType.Param);
//        add(FormatErrorType.ParamList);
//        add(FormatErrorType.Publication);
//        add(FormatErrorType.URI);
//        add(FormatErrorType.URL);
//        add(FormatErrorType.Email);
//        add(FormatErrorType.Integer);
//        add(FormatErrorType.Double);
//        add(FormatErrorType.Reliability);
//        add(FormatErrorType.StringList);
//        add(FormatErrorType.DoubleList);
//        add(FormatErrorType.ModificationList);
//        add(FormatErrorType.GOTermList);
//        add(FormatErrorType.MZBoolean);
//        add(FormatErrorType.SpectraRef);
//        add(FormatErrorType.CHEMMODSAccession);
//        add(FormatErrorType.SearchEngineScore);
//        add(FormatErrorType.ColUnit);
//
//        add(LogicalErrorType.NULL);
//        add(LogicalErrorType.NotNULL);
//        add(LogicalErrorType.LineOrder);
//        add(LogicalErrorType.HeaderLine);
//        add(LogicalErrorType.NoHeaderLine);
//        add(LogicalErrorType.MsRunNotDefined);
//        add(LogicalErrorType.AssayNotDefined);
//        add(LogicalErrorType.StudyVariableNotDefined);
//        add(LogicalErrorType.DuplicationDefine);
//        add(LogicalErrorType.DuplicationAccession);
//        add(LogicalErrorType.PeptideAccession);
//        add(LogicalErrorType.ProteinCoverage);
//        add(LogicalErrorType.IdNumber);
//        add(LogicalErrorType.ModificationPosition);
//        add(LogicalErrorType.CHEMMODS);
//        add(LogicalErrorType.SubstituteIdentifier);
//        add(LogicalErrorType.CVParamOptionalColumn);
//        add(LogicalErrorType.SoftwareVersion);
//        add(LogicalErrorType.AbundanceColumnTogether);
//        add(LogicalErrorType.AbundanceColumnSameId);
    }

    public MZTabErrorType getType(int code) {
        return typeMap.get(code);
    }
}
