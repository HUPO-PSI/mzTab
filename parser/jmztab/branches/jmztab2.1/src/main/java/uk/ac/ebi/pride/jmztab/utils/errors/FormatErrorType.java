package uk.ac.ebi.pride.jmztab.utils.errors;

import static uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorType.Category.Format;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class FormatErrorType extends MZTabErrorType {
    public static MZTabErrorType LinePrefix = createWarn(Format, "LinePrefix");
    public static MZTabErrorType CountMatch = createError(Format, "CountMatch");

    public static MZTabErrorType IndexedElement = createError(Format, "IndexedElement");
    public static MZTabErrorType AbundanceColumn = createError(Format, "AbundanceColumn");
    public static MZTabErrorType MsRunOptionalColumn = createError(Format, "MsRunOptionalColumn");
    public static MZTabErrorType OptionalColumn = createError(Format, "OptionalColumn");
    public static MZTabErrorType OptionalCVParamColumn = createError(Format, "OptionalCVParamColumn");
    public static MZTabErrorType StableColumn = createError(Format, "StableColumn");

    public static MZTabErrorType MTDLine = createError(Format, "MTDLine");
    public static MZTabErrorType MTDDefineLabel = createError(Format, "MTDDefineLabel");
    public static MZTabErrorType MZTabMode = createError(Format, "MZTabMode");
    public static MZTabErrorType MZTabType = createError(Format, "MZTabType");
    public static MZTabErrorType Param = createError(Format, "Param");
    public static MZTabErrorType ParamList = createError(Format, "ParamList");
    public static MZTabErrorType Publication = createError(Format, "Publication");
    public static MZTabErrorType URI = createWarn(Format, "URI");
    public static MZTabErrorType URL = createWarn(Format, "URL");
    public static MZTabErrorType Email = createWarn(Format, "Email");

    public static MZTabErrorType Integer = createError(Format, "Integer");
    public static MZTabErrorType Double = createError(Format, "Double");
    public static MZTabErrorType Reliability = createError(Format, "Reliability");
    public static MZTabErrorType StringList = createError(Format, "StringList");
    public static MZTabErrorType DoubleList = createError(Format, "DoubleList");
    public static MZTabErrorType ModificationList = createError(Format, "ModificationList");
    public static MZTabErrorType GOTermList = createError(Format, "GOTermList");
    public static MZTabErrorType MZBoolean = createError(Format, "MZBoolean");
    public static MZTabErrorType SpectraRef = createError(Format, "SpectraRef");
    public static MZTabErrorType CHEMMODSAccession = createError(Format, "CHEMMODSAccession");
    public static MZTabErrorType SearchEngineScore = createWarn(Format, "SearchEngineScore");

    public static MZTabErrorType ColUnit = createError(Format, "ColUnit");
}
