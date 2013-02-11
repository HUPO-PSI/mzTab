package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;
import uk.ac.ebi.pride.jmztab.utils.StringUtils;

/**
 * Peptides and small molecules MAY be linked to the source spectrum (in an external file)
 * from which the identifications are made by way of a reference in the spectra_ref attribute
 * and via the ms_file element which stores the URL of the file in the location attribute.
 * It is advantageous if there is a consistent system for identifying spectra in different file formats.
 * The following table is implemented in the PSI-MS CV for providing consistent identifiers for
 * different spectrum file formats. This is the exact same approach followed in mzIdentML and mzQuantML.
 *
 * User: Qingwei, Johannes Griss
 * Date: 30/01/13
 */
public class SpecRef {
    /**
     * The msFile identifier
     */
    private MsFile msFile;
    /**
     * Reference to the spectrum in the
     * msFile.
     */
    private String reference;

    /**
     * Creates a new SpecRef object.
     *
     * @param reference The reference to the spectrum in the MS fiile.
     */
    public SpecRef(MsFile msFile, String reference) {
        if (msFile == null) {
            throw new NullPointerException("msFile can not null!");
        }
        if (StringUtils.isEmpty(reference)) {
            throw new IllegalArgumentException("msFile reference can not empty!");
        }

        this.msFile = msFile;
        this.reference = reference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(msFile.getReference()).append(MZTabConstants.COLON).append(reference);

        return sb.toString();
    }
}
