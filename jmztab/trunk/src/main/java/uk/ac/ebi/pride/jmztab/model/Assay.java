package uk.ac.ebi.pride.jmztab.model;

import java.util.SortedMap;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * The application of a measurement about the sample (in this case through MS) –
 * producing values about small molecules, peptides or proteins. One assay is
 * typically mapped to one MS run in the case of label-free MS analysis or
 * multiple assays are mapped to one MS run for multiplexed techniques,
 * along with a description of the label or tag applied.
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class Assay extends IndexedElement {
    private Param quantificationReagent;
    private Sample sample;
    private MsRun msRun;
    private SortedMap<Integer, AssayQuantificationMod> quantificationModMap = new TreeMap<Integer, AssayQuantificationMod>();

    public Assay(int id) {
        super(MetadataElement.ASSAY, id);
    }

    /**
     * The reagent used to label the sample in the assay. For label-free analyses the "unlabeled sample"
     * CV term SHOULD be used. For the "light" channel in label-based experiments the appropriate CV term
     * specifying the labelling channel should be used.
     *
     * @return assay[1-n]-quantification_reagent data type is a {@link Param}
     */
    public Param getQuantificationReagent() {
        return quantificationReagent;
    }

    /**
     * An association from a given assay to the sample analysed.
     *
     * @return {@link Sample}, which come from assay[1-n]-sample_ref.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * An association from a given assay to the source MS run.
     *
     * @return {@link MsRun}, which come from assay[1-n]-ms_run_ref
     */
    public MsRun getMsRun() {
        return msRun;
    }

    public SortedMap<Integer, AssayQuantificationMod> getQuantificationModMap() {
        return quantificationModMap;
    }

    /**
     * The reagent used to label the sample in the assay. For label-free analyses the "unlabeled sample"
     * CV term SHOULD be used. For the "light" channel in label-based experiments the appropriate CV term
     * specifying the labelling channel should be used.
     *
     * @param quantificationReagent Set a {@link Param} for assay[1-n]-quantification_reagent.
     */
    public void setQuantificationReagent(Param quantificationReagent) {
        this.quantificationReagent = quantificationReagent;
    }

    /**
     * An association from a given assay to the sample analysed.
     *
     * @param sample Set {@link Sample} for assay[1-n]-sample_ref.
     *               Notice: {@link Sample} SHOULD BE defined in the {@link Metadata} first.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * An association from a given assay to the source MS run.
     *
     * @param msRun set {@link MsRun} for assay[1-n]-ms_run_ref.
     *              Notice: {@link Sample} SHOULD BE defined in the {@link Metadata} first.
     *
     */
    public void setMsRun(MsRun msRun) {
        this.msRun = msRun;
    }

    /**
     * assay[1-n]-quantification_mod[1-n]: A parameter describing a modification associated with a quantification_reagent.
     * Multiple modifications are numbered 1..n.
     *
     * @param mod reference {@link AssayQuantificationMod}
     */
    public void addQuantificationMod(AssayQuantificationMod mod) {
        if (mod == null) {
            throw new IllegalArgumentException("AssayQuantificationMod should not be null");
        }

        quantificationModMap.put(mod.getId(), mod);
    }

    /**
     * assay[1-n]-quantification_mod[1-n]: A parameter describing a modification associated with a quantification_reagent.
     * Multiple modifications are numbered 1..n.
     *
     * @param id a non-negative integer number.
     * @param param a not null {@link Param}.
     */
    public void addQuantificationModParam(Integer id, Param param) {
        if (id < 0) {
            throw new IllegalArgumentException("Id should be a non-negative integer number.");
        }
        if (param == null) {
            throw new NullPointerException("Quantification modification Param should not be null!");
        }

        AssayQuantificationMod mod = quantificationModMap.get(id);
        if (mod == null) {
            mod = new AssayQuantificationMod(this, id);
            mod.setParam(param);
            quantificationModMap.put(id, mod);
        } else {
            mod.setParam(param);
        }
    }

    /**
     * assay[1-n]-quantification_mod[1-n]-site: A string describing the modifications site.
     * Following the unimod convention, modification site is a residue (e.g. "M"), terminus
     * ("N-term" or "C-term") or both (e.g. "N-term Q" or "C-term K").
     *
     * @param id a non-negative integer number.
     * @param site a non {@link MZTabUtils#isEmpty(String)} String.
     */
    public void addQuantificationModSite(Integer id, String site) {
        if (id < 0) {
            throw new IllegalArgumentException("Id should be a non-negative integer number.");
        }
        if (MZTabUtils.isEmpty(site)) {
            throw new IllegalArgumentException("Quantification site should not be empty!");
        }

        AssayQuantificationMod mod = quantificationModMap.get(id);
        if (mod == null) {
            mod = new AssayQuantificationMod(this, id);
            mod.setSite(site);
            quantificationModMap.put(id, mod);
        } else  {
            mod.setSite(site);
        }
    }

    /**
     * assay[1-n]-quantification_mod[1-n]-position: A string describing the term specifity of the modification.
     * Following the unimod convention, term specifity is denoted by the strings "Anywhere", "Any N-term",
     * "Any C-term", "Protein N-term", "Protein C-term".
     *
     * @param id a non-negative integer number.
     * @param position a non {@link MZTabUtils#isEmpty(String)} String.
     */
    public void addQuantificationModPosition(Integer id, String position) {
        if (id < 0) {
            throw new IllegalArgumentException("Id should be a non-negative integer number.");
        }
        if (MZTabUtils.isEmpty(position)) {
            throw new IllegalArgumentException("Quantification position should not be empty!");
        }

        AssayQuantificationMod mod = quantificationModMap.get(id);
        if (mod == null) {
            mod = new AssayQuantificationMod(this, id);
            mod.setPosition(position);
            quantificationModMap.put(id, mod);
        } else  {
            mod.setPosition(position);
        }
    }

    /**
     * Translate the assay into a String. The output like:
     * <ul>
     *     <li>MTD	assay[1]-quantification_reagent	[PRIDE, PRIDE:0000115, iTRAQ reagent, 115]</li>
     *     <li>MTD	assay[1]-sample_ref	sample[1]</li>
     *     <li>MTD	assay[1]-quantification_mod[1]	[UNIMOD, UNIMOD:188, Label:13C(6), ]</li>
     *     <li>MTD	assay[1]-quantification_mod[1]-site	R</li>
     *     <li>MTD	assay[1]-quantification_mod[1]-position	Anywhere</li>
     * </ul>
     *
     * @see {@link uk.ac.ebi.pride.jmztab.model.Metadata#toString()}
     * @see {@link MZTabFile#printMZTab(java.io.OutputStream)}
     */
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
