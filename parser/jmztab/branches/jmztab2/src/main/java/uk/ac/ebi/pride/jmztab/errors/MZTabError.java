package uk.ac.ebi.pride.jmztab.errors;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Qingwei
 * Date: 06/02/13
 */
public class MZTabError {
    private int lineNumber;
    private MZTabErrorType type;
    private String message;

    public MZTabError(MZTabErrorType type, int lineNumber, String... values) {
        this(type, lineNumber, true, values);
    }

    public MZTabError(MZTabErrorType type, int lineNumber, boolean record, String... values) {
        this.type = type;
        this.lineNumber = lineNumber;

        List<String> valueList = new ArrayList<String>();
        Collections.addAll(valueList, values);

        this.message = fill(0, valueList, type.getOriginal());

        if (record) {
            MZTabErrorList.add(this);
        }
    }

    private String fill(int count, List<String> values, String message) {
        String regexp = "\\{\\w\\}";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(message);

        String value;
        if (matcher.find()) {
            value = values.get(count);
            message = matcher.replaceFirst(value);
            return fill(count + 1, values, message);
        } else {
            return message;
        }
    }

    public MZTabErrorType getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[").append(type.getLevel()).append("-").append(type.getCode()).append("] ");
        sb.append("line ").append(lineNumber).append(": ");
        sb.append(message).append(MZTabConstants.NEW_LINE);

        return sb.toString();
    }
}
