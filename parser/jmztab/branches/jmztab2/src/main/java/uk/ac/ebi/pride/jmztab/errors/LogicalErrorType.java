package uk.ac.ebi.pride.jmztab.errors;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class LogicalErrorType extends MZTabErrorType {
    public static MZTabErrorType NULL = createError(Category.Logical, "NULL");
    public static MZTabErrorType NotNULL = createError(Category.Logical, "NotNULL");
    public static MZTabErrorType LineOrder = createError(Category.Logical, "LineOrder");
    public static MZTabErrorType HeaderLine = createError(Category.Logical, "HeaderLine");
    public static MZTabErrorType NoHeaderLine = createError(Category.Logical, "NoHeaderLine");

    public static MZTabErrorType Duplication = createError(Category.Logical, "Duplication");
    public static MZTabErrorType UnitID = createError(Category.Logical, "UnitID");
    public static MZTabErrorType ColUnit = createWarn(Category.Logical, "ColUnit");

    public static MZTabErrorType DuplicationAccession = createError(Category.Logical, "DuplicationAccession");
    public static MZTabErrorType PeptideAccession = createWarn(Category.Logical, "PeptideAccession");

    public static MZTabErrorType ProteinCoverage = createError(Category.Logical, "ProteinCoverage");
    public static MZTabErrorType IdNumber = createError(Category.Logical, "IdNumber");
    public static MZTabErrorType ModificationPosition = createError(Category.Logical, "ModificationPosition");
    public static MZTabErrorType CHEMMODS = createError(Category.Logical, "CHEMMODS");
    public static MZTabErrorType SubstituteIdentifier = createError(Category.Logical, "SubstituteIdentifier");
    public static MZTabErrorType CVParamOptionalColumn = createError(Category.Logical, "CVParamOptionalColumn");

    public static MZTabErrorType AbundanceColumnId = createError(Category.Logical, "AbundanceColumnId");
    public static MZTabErrorType AbundanceColumnSameId = createError(Category.Logical, "AbundanceColumnSameId");

}
