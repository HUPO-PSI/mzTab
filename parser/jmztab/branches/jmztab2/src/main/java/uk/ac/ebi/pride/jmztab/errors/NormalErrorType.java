package uk.ac.ebi.pride.jmztab.errors;

/**
 * error properties stored in the mztab.properties file.
 *
 * User: Qingwei
 * Date: 06/02/13
 */
public class NormalErrorType extends MZTabErrorType {
    public static MZTabErrorType Data_Type = createError(Category.Normal, "DataType");
    public static MZTabErrorType StableColumn = createError(Category.Normal, "StableColumn");
}
