package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.COLON;

/**
 * Peptides and small molecules MAY be linked to the source spectrum (in an external file)
 * from which the identifications are made by way of a reference in the spectra_ref attribute
 * and via the ms_file element which stores the URL of the file in the location attribute.
 * It is advantageous if there is a consistent system for identifying spectra in different file formats.
 * The following table is implemented in the PSI-MS CV for providing consistent identifiers for
 * different spectrum file formats. This is the exact same approach followed in mzIdentML and mzQuantML.
 *
 * User: Qingwei
 * Date: 29/05/13
 */
public class SpectraRef {
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
     * Creates a new SpectraRef object.
     *
     * @param reference The reference to the spectrum in the MS fiile.
     */
    public SpectraRef(MsFile msFile, String reference) {
        if (msFile == null) {
            throw new NullPointerException("msFile can not null!");
        }
        if (reference == null) {
            throw new NullPointerException("msFile reference can not empty!");
        }

        this.msFile = msFile;
        this.reference = reference;
    }

    public MsFile getMsFile() {
        return msFile;
    }

    public String getReference() {
        return reference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(msFile.getReference()).append(COLON).append(reference);

        return sb.toString();
    }
}
