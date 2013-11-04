package uk.ac.ebi.pride.jmztab.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.STUDY_VARIABLE_DESCRIPTION;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class StudyVariable extends IndexedElement {
    private String description;
    private SortedMap<Integer, Assay> assayMap = new TreeMap<Integer, Assay>();
    private SortedMap<Integer, Sample> sampleMap = new TreeMap<Integer, Sample>();

    public StudyVariable(int id) {
        super(MetadataElement.STUDY_VARIABLE, id);
    }

    public String getDescription() {
        return description;
    }

    public SortedMap<Integer, Assay> getAssayMap() {
        return assayMap;
    }

    public SortedMap<Integer, Sample> getSampleMap() {
        return sampleMap;
    }

    public void addAssay(Integer pid, Assay assay) {
        assayMap.put(pid, assay);
    }

    public void addAssay(Assay assay) {
        assayMap.put(assay.getId(), assay);
    }

    public void addAllAssays(Collection<Assay> assays) {
        for (Assay assay : assays) {
            addAssay(assay);
        }
    }

    public void addSample(Integer pid, Sample sample) {
        sampleMap.put(pid, sample);
    }

    public void addSample(Sample sample) {
        sampleMap.put(sample.getId(), sample);
    }

    public void addAllSamples(Collection<Sample> samples) {
        for (Sample sample : samples) {
            addSample(sample);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSampleRef() {
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

    public String getAssayRef() {
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

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (description != null) {
            sb.append(printProperty(STUDY_VARIABLE_DESCRIPTION, description)).append(NEW_LINE);
        }

        String assayRef = getAssayRef();
        if (assayRef.length() != 0) {
            printPrefix(sb).append(getReference()).append(MINUS).append(MetadataProperty.STUDY_VARIABLE_ASSAY_REFS);
            sb.append(TAB).append(assayRef).append(NEW_LINE);
        }

        String sampleRef = getSampleRef();
        if (sampleRef.length() != 0) {
            printPrefix(sb).append(getReference()).append(MINUS).append(MetadataProperty.STUDY_VARIABLE_SAMPLE_REFS);
            sb.append(TAB).append(sampleRef).append(NEW_LINE);
        }

        return sb.toString();
    }
}
