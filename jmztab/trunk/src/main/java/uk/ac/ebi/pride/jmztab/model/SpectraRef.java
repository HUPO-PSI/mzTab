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
     * The msRun identifier
     */
    private MsRun msRun;
    /**
     * Reference to the spectrum in the
     * msRun.
     */
    private String reference;

    /**
     * Creates a new SpectraRef object.
     *
     * @param reference The reference to the spectrum in the MS fiile.
     */
    public SpectraRef(MsRun msRun, String reference) {
        if (msRun == null) {
            throw new NullPointerException("msRun can not null!");
        }
        if (reference == null) {
            throw new NullPointerException("msRun reference can not empty!");
        }

        this.msRun = msRun;
        this.reference = reference;
    }

    public MsRun getMsRun() {
        return msRun;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(msRun.getReference()).append(COLON).append(reference);

        return sb.toString();
    }
}
