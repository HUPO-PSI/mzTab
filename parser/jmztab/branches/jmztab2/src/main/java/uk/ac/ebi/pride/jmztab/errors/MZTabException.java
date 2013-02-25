package uk.ac.ebi.pride.jmztab.errors;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabException extends Exception {
    private MZTabError error;

    public MZTabException() {
    }

    public MZTabException(MZTabError error) {
        super(error.toString());
        this.error = error;
    }

    public MZTabException(MZTabError error, Throwable cause) {
        super(error.toString(), cause);
        this.error = error;
    }

    public MZTabError getError() {
        return error;
    }
}
