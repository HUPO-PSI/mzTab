package uk.ac.ebi.pride.jmztab.utils;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class StringUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
