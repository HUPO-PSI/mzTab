package uk.ac.ebi.pride.jmztab.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class StringUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    /**
     * idLabel pattern including "[id]", extract id from this pattern. If failure, return null!
     */
    public static Integer parseId(String idLabel) {
        Pattern pattern = Pattern.compile("\\w*\\[\\d+\\]\\w*");
        Matcher matcher = pattern.matcher(idLabel);
        if (! matcher.find()) {
            return null;
        }

        pattern = Pattern.compile("\\d+");
        matcher = pattern.matcher(idLabel);
        if (matcher.find()) {
            return new Integer(matcher.group());
        }

        return null;
    }

    /**
     * UnitId only contain the following characters: 'A'-'Z', 'a'-'z', '0'-'9', and '_'.
     */
    public static boolean parseUnitId(String unitId) {
        Pattern pattern = Pattern.compile("[A-Za-z]{1}[\\w_]*");
        Matcher matcher = pattern.matcher(unitId);
        return matcher.find();
    }
}
