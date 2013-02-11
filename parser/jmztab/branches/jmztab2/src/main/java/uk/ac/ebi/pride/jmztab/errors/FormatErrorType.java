package uk.ac.ebi.pride.jmztab.errors;

import static uk.ac.ebi.pride.jmztab.errors.MZTabErrorType.Category.Format;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class FormatErrorType extends MZTabErrorType {
    public static MZTabErrorType MTDLine = createError(Format, "MTDLine");
    public static MZTabErrorType MTDDefineLabel = createError(Format, "MTDDefineLabel");
    public static MZTabErrorType UnitID = createError(Format, "UnitID");


    public static MZTabErrorType DataType = createError(Format, "DataType");
    public static MZTabErrorType StableColumn = createError(Format, "StableColumn");
}
