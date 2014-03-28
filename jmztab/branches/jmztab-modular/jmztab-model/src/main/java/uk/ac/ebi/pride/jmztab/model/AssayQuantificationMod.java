package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * A parameter describing a modification associated with a quantification_reagent for a assay.
 * Multiple modifications are numbered 1..n.
 *
 * @see Assay
 *
 * User: qingwei
 * Date: 14/10/13
 */
public class AssayQuantificationMod extends Mod {
    private Assay assay;

    public AssayQuantificationMod(Assay assay, int id) {
        super(MetadataSubElement.ASSAY_QUANTIFICATION_MOD.getElement(), id);

        if (assay == null) {
            throw new NullPointerException("Assay should not be null!");
        }
        this.assay = assay;
    }

    /**
     * Output a string like: assay[id]-quantification_mod[id]
     */
    @Override
    public String getReference() {
        StringBuilder sb = new StringBuilder();

        sb.append(MetadataElement.ASSAY).append("[").append(assay.getId()).append("]").append(MINUS);
        sb.append(MetadataSubElement.ASSAY_QUANTIFICATION_MOD).append("[").append(getId()).append("]");

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (getParam() != null) {
            printPrefix(sb).append(getReference()).append(TAB).append(getParam()).append(NEW_LINE);
        }

        if (! MZTabUtils.isEmpty(getSite())) {
            printPrefix(sb).append(getReference()).append(MINUS).append("site").append(TAB).append(getSite()).append(NEW_LINE);
        }

        if (! MZTabUtils.isEmpty(getPosition())) {
            printPrefix(sb).append(getReference()).append(MINUS).append("position").append(TAB).append(getPosition()).append(NEW_LINE);
        }

        return sb.toString();
    }
}
