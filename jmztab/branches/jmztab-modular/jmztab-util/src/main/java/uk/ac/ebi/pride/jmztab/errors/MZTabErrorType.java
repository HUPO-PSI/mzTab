package uk.ac.ebi.pride.jmztab.errors;

import uk.ac.ebi.pride.jmztab.model.MZTabUtils;
import uk.ac.ebi.pride.jmztab.utils.MZTabProperties;

/**
 * @author qingwei
 * @since 28/01/13
 */
public class MZTabErrorType {
    protected enum Category {
        Format,                // single field format error
        Logical,               // exists logical error among fields value.
        CrossCheck             // cross check validate by calling outside files or web services. Not implement yet.
    }

    /**
     * We classify diagnosis message types into three levels: Info, Warn and Error, all of them coming from
     * the notational conventions of the specification. All diagnosis rules, which use "MUST", "MUST NOT",
     * "REQUIRED" keywords, are defined for Error level; use "SHALL", "SHALL NOT", "SHOULD",
     * "SHOULD NOT" keywords are defined for Warn Level; and others keywords, such as "RECOMMENDED",
     * "MAY" and "OPTIONAL", are defined for Info Level.
     */
    public enum Level {
        Warn,
        Error,
        Info
    }

    private Integer code;
    private Category category;
    private Level level;
    private String original;
    private String cause;

    protected MZTabErrorType() {}

    /**
     * Code: Unique number for error/warn
     * Category: Currently, there are three types of messages: Format, Logical
     * Original: Message expression pattern. "{?}" is a couple of parameters which can be filled during validate processing.
     * Cause: A readable text to describe the reason why raise this error/warn. Currently, these cause message coming from mztab specification mainly.
     */
    private MZTabErrorType(Integer code, Category category, Level level, String original, String cause) {
        this.code = code;

        if (category == null) {
            throw new NullPointerException("MZTabErrorType category can not set null!");
        }
        this.category = category;

        this.level = level == null ? Level.Info : level;

        if (original == null || original.trim().length() == 0) {
            throw new IllegalArgumentException("Original " + original + " is empty!");
        }
        this.original = original.trim();
        this.cause = cause;
    }

    /**
     * Generate a {@link MZTabErrorType.Level#Error} by parse keyword.
     */
    protected static MZTabErrorType createError(MZTabErrorType.Category category, String keyword) {
        return MZTabErrorType.createMZTabError(category, Level.Error, keyword);
    }

    /**
     * Generate a {@link MZTabErrorType.Level#Warn} by parse keyword.
     */
    protected static MZTabErrorType createWarn(MZTabErrorType.Category category, String keyword) {
        return MZTabErrorType.createMZTabError(category, Level.Warn, keyword);
    }

    /**
     * Generate a {@link MZTabErrorType.Level#Info} by parse keyword.
     */
    protected static MZTabErrorType createInfo(MZTabErrorType.Category category, String keyword) {
        return MZTabErrorType.createMZTabError(category, Level.Info, keyword);
    }

    /**
     *  In *_error.properties file, code_{keyword}, original_{keyword}, cause+{keyword} have
     *  stable format. Thus, this method used to load these properties and create a error.
     */
    private static MZTabErrorType createMZTabError(Category category, Level level, String keyword) {
        if (MZTabUtils.isEmpty(keyword)) {
            throw new NullPointerException(keyword + " can not empty!");
        }

        String prefix = null;
        switch (category) {
            case Format:
                prefix = "f_";
                break;
            case Logical:
                prefix = "l_";
                break;
            case CrossCheck:
                prefix = "c_";
                break;
        }

        Integer code = new Integer(MZTabProperties.getProperty(prefix + "code_" + keyword));
        String original = MZTabProperties.getProperty(prefix + "original_" + keyword);
        String cause = MZTabProperties.getProperty(prefix + "cause_" + keyword);

        return new MZTabErrorType(code, category, level, original, cause);
    }

    public Integer getCode() {
        return code;
    }

    public Category getCategory() {
        return category;
    }

    public Level getLevel() {
        return level;
    }

    public String getOriginal() {
        return original;
    }

    public String getCause() {
        return cause;
    }

    /**
     * Code: Unique number for error/warn
     * Category: Currently, there are three types of messages: Format, Logical
     * Original: Message expression pattern. "{?}" is a couple of parameters which can be filled during validate processing.
     * Cause: A readable text to describe the reason why raise this error/warn. Currently, these cause message coming from mztab specification mainly.
     */
    public String toString() {
        return  "    Code:\t" + code + "\r\n" +
                "Category:\t" + category + "\r\n" +
                "Original:\t" + original + "\r\n" +
                "   Cause:\t" + (cause == null ? "" : cause) + "\r\n";
    }

    /**
     * @return the {@link MZTabErrorType.Level}
     */
    public static Level findLevel(String target) {
        if (MZTabUtils.isEmpty(target)) {
            return null;
        }

        target = MZTabUtils.toCapital(target.trim());

        Level level;
        try {
            level = Level.valueOf(target);
        } catch (IllegalArgumentException e) {
            level = null;
        }

        return level;
    }
}
