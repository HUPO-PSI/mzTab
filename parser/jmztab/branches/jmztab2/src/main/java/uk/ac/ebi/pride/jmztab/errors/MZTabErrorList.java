package uk.ac.ebi.pride.jmztab.errors;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.MAX_ERROR_COUNT;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabErrorList {
    private static List<MZTabError> errorList;

    static {
        errorList = new ArrayList<MZTabError>(MAX_ERROR_COUNT);
    }

    public static boolean add(MZTabError o) {
        return errorList.size() < MAX_ERROR_COUNT && errorList.add(o);
    }

    public static void clear() {
        errorList.clear();
    }

    public static void print(OutputStream out, MZTabErrorType.Level level) {
        try {
            for (MZTabError e : errorList) {
                if (e.getType().getLevel().compareTo(level) >= 0) {
                    out.write(e.toString().getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(Writer writer, MZTabErrorType.Level level) {
        try {
            for (MZTabError e : errorList) {
                if (e.getType().getLevel().compareTo(level) >= 0) {
                    writer.write(e.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
