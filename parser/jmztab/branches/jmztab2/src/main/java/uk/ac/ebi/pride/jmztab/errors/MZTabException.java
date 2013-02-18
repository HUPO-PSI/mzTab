package uk.ac.ebi.pride.jmztab.errors;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabException extends RuntimeException {
    private MZTabError error;

    public MZTabException(MZTabError error) {
        super(error.getMessage());
        this.error = error;
    }

    public MZTabException(MZTabError error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }

    public MZTabError getError() {
        return error;
    }
}
