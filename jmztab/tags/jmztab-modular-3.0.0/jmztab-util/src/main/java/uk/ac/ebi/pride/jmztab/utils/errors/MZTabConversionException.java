package uk.ac.ebi.pride.jmztab.utils.errors;

/**
 * Runtime exception for mzTab conversion
 *
 * @author Rui Wang
 * @version $Id$
 */
public class MZTabConversionException extends RuntimeException {

    public static String ERROR_AMBIGUITY = "mzTab do not support one protein in more than one ambiguity groups.";

    public MZTabConversionException() {
    }

    public MZTabConversionException(String message) {
        super(message);
    }

    public MZTabConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MZTabConversionException(Throwable cause) {
        super(cause);
    }
}
