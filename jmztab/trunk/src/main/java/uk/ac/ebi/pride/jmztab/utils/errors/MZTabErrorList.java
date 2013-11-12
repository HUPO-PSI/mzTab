package uk.ac.ebi.pride.jmztab.utils.errors;

import uk.ac.ebi.pride.jmztab.model.MZTabConstants;
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
    private MZTabErrorType.Level level;

    public MZTabErrorList() {
        level = MZTabErrorType.Level.Error;
    }

    public MZTabErrorList(MZTabErrorType.Level level) {
        this.level = level;
    }

    public boolean add(MZTabError o) throws MZTabErrorOverflowException {
        if (o.getType().getLevel().compareTo(level) < 0) {
            return false;
        }

        if (errorList.size() >= MAX_ERROR_COUNT) {
            throw new MZTabErrorOverflowException();
        }

        return errorList.add(o);
    }

    public void clear() {
        errorList.clear();
    }

    public int size() {
        return errorList.size();
    }

    public MZTabError getError(int index) {
        return errorList.get(index);
    }

    public boolean isEmpty() {
        return errorList.isEmpty();
    }

    public MZTabErrorList filterList(MZTabErrorType.Level level) {
        MZTabErrorList newList = new MZTabErrorList();
        if (level.equals(MZTabErrorType.Level.Info)) {
            newList.errorList.addAll(this.errorList);
        } else {
            for (MZTabError error : errorList) {
                if (error.getType().getLevel().compareTo(level) >= 0) {
                    newList.errorList.add(error);
                }
            }
        }

        return newList;
    }

    public void print(OutputStream out) throws IOException {
        for (MZTabError e : errorList) {
            out.write(e.toString().getBytes());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (MZTabError error : errorList) {
            sb.append(error).append(MZTabConstants.NEW_LINE);
        }

        return sb.toString();
    }
}
