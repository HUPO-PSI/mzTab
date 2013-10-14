package uk.ac.ebi.pride.jmztab.model;

import java.util.Map;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class Assay extends IndexedElement {
    private Param quantificationReagent;
    private Sample sample;
    private MsRun msRun;
    private Map<Integer, AssayQuantificationMod> quantificationModMap = new TreeMap<Integer, AssayQuantificationMod>();

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

    public Map<Integer, AssayQuantificationMod> getQuantificationModMap() {
        return quantificationModMap;
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

    public boolean addQuantificationModParam(Integer id, Param param) {
        AssayQuantificationMod mod = quantificationModMap.get(id);
        if (mod == null) {
            mod = new AssayQuantificationMod(this, id);
            mod.setParam(param);
            quantificationModMap.put(id, mod);
            return true;
        } else if (mod.getParam() != null) {
            return false;
        } else {
            mod.setParam(param);
            return true;
        }
    }

    public void addQuantificationModSite(Integer id, String site) {
        AssayQuantificationMod mod = quantificationModMap.get(id);
        if (mod == null) {
            mod = new AssayQuantificationMod(this, id);
            mod.setSite(site);
            quantificationModMap.put(id, mod);
        } else  {
            mod.setSite(site);
        }
    }

    public void addQuantificationModPosition(Integer id, String position) {
        AssayQuantificationMod mod = quantificationModMap.get(id);
        if (mod == null) {
            mod = new AssayQuantificationMod(this, id);
            mod.setPosition(position);
            quantificationModMap.put(id, mod);
        } else  {
            mod.setPosition(position);
        }
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
            printPrefix(sb).append(getReference()).append(MINUS).append(MetadataProperty.ASSAY_MS_RUN_REF);
            sb.append(TAB).append(msRun.getReference()).append(NEW_LINE);
        }

        for (AssayQuantificationMod mod : quantificationModMap.values()) {
            sb.append(mod);
        }

        return sb.toString();
    }
}
