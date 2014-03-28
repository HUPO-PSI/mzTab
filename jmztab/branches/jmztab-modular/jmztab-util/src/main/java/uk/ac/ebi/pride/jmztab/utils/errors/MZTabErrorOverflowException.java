package uk.ac.ebi.pride.jmztab.utils.errors;

/**
 * If error count great than {@link uk.ac.ebi.pride.jmztab.utils.MZTabProperties#MAX_ERROR_COUNT}
 * System will stop validate and throw overflow exception.
 *
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabErrorOverflowException extends RuntimeException {
    /**
     * If error count great than {@link uk.ac.ebi.pride.jmztab.utils.MZTabProperties#MAX_ERROR_COUNT}
     * System will stop validate and throw overflow exception.
     */
    public MZTabErrorOverflowException() {
    }
}
