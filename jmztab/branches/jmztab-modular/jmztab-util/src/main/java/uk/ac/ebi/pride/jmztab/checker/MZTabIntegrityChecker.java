package uk.ac.ebi.pride.jmztab.checker;

import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;

/**
 * Define the interface for checking the stability of the mzTab file.
 *
 * @author ntoro
 * @since 25/07/2014 16:45
 */
public interface MZTabIntegrityChecker {

    /**
     * Check the data consistency.
     */
    public void check();

    /**
     * List of errors generated during the process
     * @return MZTabErrorList
     */
    public MZTabErrorList getErrorList();

}
