package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class Assay extends IndexedElement {
    private Param quantificationReagent;
    private Sample sample;
    private MsRun msRun;

    public Assay(int id) {
        super(MetadataElement.ASSAY, id);
    }

    public Param getQuantificationReagent() {
        return quantificationReagent;
    }

    public Sample getSample() {
        return sample;
    }

    public MsRun getMsRun() {
        return msRun;
    }

    public void setQuantificationReagent(Param quantificationReagent) {
        this.quantificationReagent = quantificationReagent;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public void setMsRun(MsRun msRun) {
        this.msRun = msRun;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (quantificationReagent != null) {
            printPrefix(sb).append(getReference()).append(MINUS).append(MetadataProperty.ASSAY_QUANTIFICATION_REAGENT);
            sb.append(TAB).append(quantificationReagent).append(NEW_LINE);
        }

        if (sample != null) {
            printPrefix(sb).append(getReference()).append(MINUS).append(MetadataProperty.ASSAY_SAMPLE_REF);
            sb.append(TAB).append(sample.getReference()).append(NEW_LINE);
        }

        if (msRun != null) {
            printPrefix(sb).append(getReference()).append(MINUS).append(MetadataProperty.ASSAY_MS_FILE_REF);
            sb.append(TAB).append(msRun.getReference()).append(NEW_LINE);
        }

        return sb.toString();
    }
}
