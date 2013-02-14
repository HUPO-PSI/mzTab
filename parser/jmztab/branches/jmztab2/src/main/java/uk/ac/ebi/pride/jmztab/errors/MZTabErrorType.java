package uk.ac.ebi.pride.jmztab.errors;

import uk.ac.ebi.pride.jmztab.parser.MZTabParserUtils;
import uk.ac.ebi.pride.jmztab.utils.MZTabProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Qingwei
 * Date: 28/01/13
 */
public class MZTabErrorType {
    protected enum Category {
        Format,                // single field format error
        Logical,               // exists logical error among fields value.
        CrossCheck             // multiple documents cross check error.
    }

    public enum Level {
        Warn,
        Error
    }

    public static Map<Integer, MZTabErrorType> typeMap = new HashMap<Integer, MZTabErrorType>();

    private Integer code;
    private Category category;
    private Level level;
    private String original;
    private String cause;

    protected MZTabErrorType() {}

    private MZTabErrorType(Integer code, Category category, Level level, String original, String cause) {
        this.code = code;

        if (category == null) {
            throw new NullPointerException("MZTabErrorType category can not set null!");
        }
        this.category = category;

        if (level == null) {
            this.level = Level.Error;
        }

        if (original == null || original.trim().length() == 0) {
            throw new IllegalArgumentException("Original " + original + " is empty!");
        }
        this.original = original.trim();
        this.cause = cause;
    }

    protected static MZTabErrorType createError(MZTabErrorType.Category category, String keyword) {
        return MZTabErrorType.createMZTabError(category, MZTabErrorType.Level.Error, keyword);
    }

    protected static MZTabErrorType createWarn(MZTabErrorType.Category category, String keyword) {
        return MZTabErrorType.createMZTabError(category, MZTabErrorType.Level.Warn, keyword);
    }

    /**
     *  In *_error.properties file, code_{keyword}, original_{keyword}, cause+{keyword} have
     *  stable format. Thus, this method used to load these properties and create a error.
     */
    private static MZTabErrorType createMZTabError(Category category, Level level, String keyword) {
        if (MZTabParserUtils.isEmpty(keyword)) {
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

        MZTabErrorType type = new MZTabErrorType(code, category, level, original, cause);
        typeMap.put(code, type);

        return type;
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

    public void printMZTabErrorType(int code, OutputStream out) throws IOException {
        MZTabErrorType type = typeMap.get(code);
        if (type != null) {
            out.write(type.toString().getBytes());
        }
    }

    public String toString() {
        return  "    Code:\t" + code + "\r\n" +
                "    Field:\t" + category + "\r\n" +
                "Original:\t" + original + "\r\n" +
                "   Cause:\t" + (cause == null ? "" : cause) + "\r\n";
    }
}
