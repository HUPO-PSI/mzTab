package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

/**
 * Provide semantic validation for mzTab file.
 */
public class MZTabFileValidator {
    private MZTabErrorList errorList;

    public MZTabFileValidator(MZTabErrorList errorList) {
        this.errorList = errorList == null ? new MZTabErrorList() : errorList;
    }


}
