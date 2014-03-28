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
 * A limit max capacity list, if contains a couple of {@link MZTabError} objects.
 * If overflow, system will raise {@link MZTabErrorOverflowException}. Besides this, during
 * add a new {@link MZTabError} object, it's {@link MZTabErrorType#level} SHOULD equal or
 * great than its level setting.
 *
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabErrorList {
    private List<MZTabError> errorList = new ArrayList<MZTabError>(MAX_ERROR_COUNT);
    private MZTabErrorType.Level level;

    /**
     * Generate a error list, which max size is {@link MZTabProperties#MAX_ERROR_COUNT},
     * and only allow {@link MZTabErrorType.Level#Error} or greater level errors to be added
     * into list.
     */
    public MZTabErrorList() {
        this(MZTabErrorType.Level.Error);
    }

    /**
     * Generate a error list, which max size is {@link MZTabProperties#MAX_ERROR_COUNT}
     *
     * @param level if null, default level is {@link MZTabErrorType.Level#Error}
     */
    public MZTabErrorList(MZTabErrorType.Level level) {
        this.level = level == null ? MZTabErrorType.Level.Error : level;
    }

    /**
     * A limit max capacity list, if contains a couple of {@link MZTabError} objects.
     * If overflow, system will raise {@link MZTabErrorOverflowException}. Besides this, during
     * add a new {@link MZTabError} object, it's {@link MZTabErrorType#level} SHOULD equal or
     * great than its level setting.
     *
     * @param error SHOULD NOT set null
     */
    public boolean add(MZTabError error) throws MZTabErrorOverflowException {
        if (error == null) {
            throw new NullPointerException("Can not add a null error into list.");
        }

        if (error.getType().getLevel().compareTo(level) < 0) {
            return false;
        }

        if (errorList.size() >= MAX_ERROR_COUNT) {
            throw new MZTabErrorOverflowException();
        }

        return errorList.add(error);
    }

    /**
     * Clear all errors stored in the error list.
     */
    public void clear() {
        errorList.clear();
    }

    /**
     * Returns the number of elements in this list.
     */
    public int size() {
        return errorList.size();
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     */
    public MZTabError getError(int index) {
        return errorList.get(index);
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     */
    public boolean isEmpty() {
        return errorList.isEmpty();
    }

    /**
     * Print error list to output stream.
     * @param out SHOULD NOT set null.
     */
    public void print(OutputStream out) throws IOException {
        if (out == null) {
            throw new NullPointerException("Output stream should be set first.");
        }

        for (MZTabError e : errorList) {
            out.write(e.toString().getBytes());
        }
    }

    /**
     * Print error list to string.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (MZTabError error : errorList) {
            sb.append(error).append(MZTabConstants.NEW_LINE);
        }

        return sb.toString();
    }
}
