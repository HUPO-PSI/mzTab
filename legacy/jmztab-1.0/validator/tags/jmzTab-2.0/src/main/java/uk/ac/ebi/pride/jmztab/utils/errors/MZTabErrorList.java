package uk.ac.ebi.pride.jmztab.utils.errors;

import uk.ac.ebi.pride.jmztab.utils.MZTabProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.pride.jmztab.utils.MZTabProperties.LEVEL;
import static uk.ac.ebi.pride.jmztab.utils.MZTabProperties.MAX_ERROR_COUNT;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabErrorList {
    private List<MZTabError> errorList = new ArrayList<MZTabError>(MAX_ERROR_COUNT);

    public boolean add(MZTabError o) throws MZTabErrorOverflowException {
        if (errorList.size() >= MAX_ERROR_COUNT) {
            throw new MZTabErrorOverflowException();
        }

        return errorList.add(o);
    }

    public void clear() {
        errorList.clear();
    }

    public boolean isEmpty() {
        return isEmpty(LEVEL);
    }

    public boolean isEmpty(MZTabErrorType.Level level) {
        if (level.equals(MZTabErrorType.Level.Warn)) {
            return errorList.isEmpty();
        } else {
            for (MZTabError error : errorList) {
                if (error.getType().getLevel().equals(MZTabErrorType.Level.Error)) {
                    return false;
                }
            }
            // all errors' level are Warn.
            return true;
        }
    }

    public void print(OutputStream out, MZTabErrorType.Level level) throws IOException {
        for (MZTabError e : errorList) {
            if (e.getType().getLevel().compareTo(level) >= 0) {
                out.write(e.toString().getBytes());
            }
        }
    }

    public void print(OutputStream out) throws IOException {
        print(out, MZTabProperties.LEVEL);
    }
}
