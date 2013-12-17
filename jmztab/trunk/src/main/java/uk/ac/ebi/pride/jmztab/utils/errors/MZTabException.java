package uk.ac.ebi.pride.jmztab.utils.errors;

/**
 * Wrap a {@link MZTabError}. This exception mainly used in parse the metadata section of mzTab file.
 * Once raise exception, system will stop validate and output the error messages.
 *
 * NOTICE: In some special situation, the consistency constraints SHOULD be maintain in metadata section,
 * for example: the assay[n]-sample_ref, study_variable[1-n]-assay_refs and so on. We suggest user raise
 * this exception very carefully, because of this break the continuous validate principle. During process
 * the value format, system will add the {@link MZTabError} into {@link MZTabErrorList}, instead of raise
 * the exception directly. And all errors will output after validate the whole mzTab file.
 *
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabException extends Exception {
    private MZTabError error;

    public MZTabException(String message) {
        super(message);
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
