package uk.ac.ebi.pride.jmztab.errors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.pride.jmztab.utils.MZTabProperties.MAX_ERROR_COUNT;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabErrorList {
    private static List<MZTabError> errorList;

    static {
        errorList = new ArrayList<MZTabError>(MAX_ERROR_COUNT);
    }

    public static boolean add(MZTabError o) throws MZTabErrorOverflowException {
        if (errorList.size() >= MAX_ERROR_COUNT) {
            throw new MZTabErrorOverflowException();
        }

        return errorList.add(o);
    }

    public static void clear() {
        errorList.clear();
    }

    public static boolean isEmpty() {
        return errorList.isEmpty();
    }

    public static void print(OutputStream out, MZTabErrorType.Level level) throws IOException {
        for (MZTabError e : errorList) {
            if (e.getType().getLevel().compareTo(level) >= 0) {
                out.write(e.toString().getBytes());
            }
        }
    }
}
