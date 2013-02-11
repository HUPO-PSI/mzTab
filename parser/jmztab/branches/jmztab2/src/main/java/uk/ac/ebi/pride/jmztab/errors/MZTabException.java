package uk.ac.ebi.pride.jmztab.errors;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabException extends RuntimeException {
    public MZTabException(MZTabError error) {
        super(error.getMessage());
    }

    public MZTabException(MZTabError error, Throwable cause) {
        super(error.getMessage(), cause);
    }

}
