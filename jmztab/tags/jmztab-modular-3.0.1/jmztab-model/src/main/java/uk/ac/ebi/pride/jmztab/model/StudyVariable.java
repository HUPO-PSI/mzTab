package uk.ac.ebi.pride.jmztab.model;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.STUDY_VARIABLE_DESCRIPTION;

/**
 * The variables about which the final results of a study are reported, which may have been derived following
 * averaging across a group of replicate measurements (assays). In files where assays are reported, study variables
 * have references to assays. The same concept has been defined by others as "experimental factor".
 *
 * @author qingwei
 * @since 23/05/13
 */
public class StudyVariable extends IndexedElement {
    private String description;
    private SortedMap<Integer, Assay> assayMap = new TreeMap<Integer, Assay>();
    private SortedMap<Integer, Sample> sampleMap = new TreeMap<Integer, Sample>();

    /**
     * Create a study_variable[id] in metadata section.
     *
     * @param id SHOULD be positive integer.
     */
    public StudyVariable(int id) {
        super(MetadataElement.STUDY_VARIABLE, id);
    }

    /**
     * A textual description of the study variable.
     * study_variable[1-n]-description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Comma-separated references to the IDs of assays grouped in the study variable.
     * study_variable[1-n]-assay_refs	assay[1], assay[2], assay[3]
     */
    public SortedMap<Integer, Assay> getAssayMap() {
        return assayMap;
    }

    /**
     * Comma-separated references to the samples that were analysed in the study variable.
     * study_variable[1-n]-sample_refs	sample[1]
     */
    public SortedMap<Integer, Sample> getSampleMap() {
        return sampleMap;
    }

    /**
     * Add a assay to study_variable[1-n]-assay_refs.
     *
     * @param assay SHOULD NOT set null, and have added it into {@link Metadata}.
     */
    public void addAssay(Assay assay) {
        assayMap.put(assay.getId(), assay);
    }

    /**
     * Add a sample to study_variable[1-n]-sample_refs.
     *
     * @param sample SHOULD NOT set null, and have added it into {@link Metadata}.
     */
    public void addSample(Sample sample) {
        sampleMap.put(sample.getId(), sample);
    }

    /**
     * A textual description of the study variable.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    private String toSampleRef() {
        StringBuilder sb = new StringBuilder();

        Sample sample;
        if (! sampleMap.isEmpty()) {
            Iterator<Sample> it = sampleMap.values().iterator();
            sample = it.next();
            sb.append(sample.getReference());

            while (it.hasNext()) {
                sample = it.next();
                sb.append(COMMA).append(" ").append(sample.getReference());
            }
        }

        return sb.toString();
    }

    private String toAssayRef() {
        StringBuilder sb = new StringBuilder();

        Assay assay;
        if (! assayMap.isEmpty()) {
            Iterator<Assay> it = assayMap.values().iterator();
            assay = it.next();
            sb.append(assay.getReference());

            while (it.hasNext()) {
                assay = it.next();
                sb.append(COMMA).append(" ").append(assay.getReference());
            }
        }

        return sb.toString();
    }

    /**
     * Print study_variable[1-n] object to a string. The structure like:
     * <ul>
     *     <li>MTD	study_variable[1]-description	description Group B (spike-in 0.74 fmol/uL)</li>
     *     <li>MTD	study_variable[1]-assay_refs	assay[1], assay[2]</li>
     *     <li>MTD	study_variable[1]-sample_refs	sample[1]</li>
     * </ul>
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (description != null) {
            sb.append(printProperty(STUDY_VARIABLE_DESCRIPTION, description)).append(NEW_LINE);
        }

        String assayRef = toAssayRef();
        if (assayRef.length() != 0) {
            printPrefix(sb).append(getReference()).append(MINUS).append(MetadataProperty.STUDY_VARIABLE_ASSAY_REFS);
            sb.append(TAB).append(assayRef).append(NEW_LINE);
        }

        String sampleRef = toSampleRef();
        if (sampleRef.length() != 0) {
            printPrefix(sb).append(getReference()).append(MINUS).append(MetadataProperty.STUDY_VARIABLE_SAMPLE_REFS);
            sb.append(TAB).append(sampleRef).append(NEW_LINE);
        }

        return sb.toString();
    }
}
