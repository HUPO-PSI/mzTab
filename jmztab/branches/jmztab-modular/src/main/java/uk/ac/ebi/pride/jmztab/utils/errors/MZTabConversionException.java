package uk.ac.ebi.pride.jmztab.utils.errors;

/**
 * Runtime exception for mzTab conversion
 *
 * @author Rui Wang
 * @version $Id$
 */
public class MZTabConversionException extends RuntimeException {

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

    public MZTabConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
