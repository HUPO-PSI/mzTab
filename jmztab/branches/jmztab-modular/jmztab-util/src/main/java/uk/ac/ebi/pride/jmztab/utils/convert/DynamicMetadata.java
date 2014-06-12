package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.SortedMap;

/**
 * User can modify some index for index elements.
 *
 * User: qingwei
 * Date: 29/11/13
 */
public class DynamicMetadata implements PropertyChangeListener {
    public final static String CHANGE_SAMPLE_INDEX = "change_sample_index";
    public final static String CHANGE_ASSAY_INDEX = "change_assay_index";
    public final static String CHANGE_MS_RUN_INDEX = "change_ms_run_index";

    //TODO extend for search engine score

    private Metadata metadata;
    private PropertyChangeSupport changeSupport;

    public DynamicMetadata(Metadata metadata) {
        if (metadata == null) {
            throw new NullPointerException("Metadata can not set null!");
        }
        this.metadata = metadata;

        addPropertyChangeListener(CHANGE_MS_RUN_INDEX, this);
        addPropertyChangeListener(CHANGE_ASSAY_INDEX, this);
        addPropertyChangeListener(CHANGE_SAMPLE_INDEX, this);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (changeSupport == null) {
            changeSupport = new PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeSupport changeSupport = this.changeSupport;
        if (changeSupport == null || oldValue == newValue) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void modifySampleId(Integer oldId, Integer newId) {
        SortedMap<Integer,Sample> sampleMap = metadata.getSampleMap();

        if (sampleMap.containsKey(newId)) {
            throw new IllegalArgumentException("Can not set new sample id: " + newId + ", because there have conflict in current metadata.");
        }

        Sample sample = sampleMap.remove(oldId);
        if (sample == null) {
            throw new IllegalArgumentException("Can not find sample " + newId + " in current metadata.");
        }

        sample.setId(newId);
        sampleMap.put(newId, sample);
        firePropertyChange(CHANGE_SAMPLE_INDEX, oldId, newId);
    }

    public void modifyAssayId(Integer oldId, Integer newId) {
        SortedMap<Integer, Assay> assayMap = metadata.getAssayMap();

        if (assayMap.containsKey(newId)) {
            throw new IllegalArgumentException("Can not set new assay id: " + newId + ", because there have conflict in current metadata.");
        }

        Assay assay = assayMap.remove(oldId);
        if (assay == null) {
            throw new IllegalArgumentException("Can not find assay " + newId + " in current metadata.");
        }

        assay.setId(newId);
        assayMap.put(newId, assay);
        firePropertyChange(CHANGE_ASSAY_INDEX, oldId, newId);
    }

    public void modifyMsRunId(Integer oldId, Integer newId) {
        SortedMap<Integer, MsRun> msRunMap = metadata.getMsRunMap();

        if (msRunMap.containsKey(newId)) {
            throw new IllegalArgumentException("Can not set new ms_run id: " + newId + ", because there have conflict in current metadata.");
        }

        MsRun msRun = msRunMap.remove(oldId);
        if (msRun == null) {
            throw new IllegalArgumentException("Can not find ms_run " + newId + " in current metadata.");
        }

        msRun.setId(newId);
        msRunMap.put(newId, msRun);
        firePropertyChange(CHANGE_MS_RUN_INDEX, oldId, newId);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SortedMap<Integer, Assay> assayMap = metadata.getAssayMap();
        SortedMap<Integer, StudyVariable> studyVariableMap = metadata.getStudyVariableMap();

        if (evt.getPropertyName().equals(CHANGE_SAMPLE_INDEX)) {
            Integer oldId = (Integer) evt.getOldValue();
            Integer newId = (Integer) evt.getNewValue();
            for (Assay assay : assayMap.values()) {
                if (assay.getSample() != null && assay.getSample().getId().equals(oldId)) {
                    assay.getSample().setId(newId);
                }
            }
            for (StudyVariable studyVariable : studyVariableMap.values()) {
                Sample sample = studyVariable.getSampleMap().remove(oldId);
                if (sample != null) {
                    sample.setId(newId);
                    studyVariable.getSampleMap().put(newId, sample);
                }
            }
        } else if (evt.getPropertyName().equals(CHANGE_ASSAY_INDEX)) {
            Integer oldId = (Integer) evt.getOldValue();
            Integer newId = (Integer) evt.getNewValue();
            for (StudyVariable studyVariable : studyVariableMap.values()) {
                Assay assay = studyVariable.getAssayMap().remove(oldId);
                if (assay != null) {
                    assay.setId(newId);
                    studyVariable.getAssayMap().put(newId, assay);
                }
            }
        } else if (evt.getPropertyName().equals(CHANGE_MS_RUN_INDEX)) {
            Integer oldId = (Integer) evt.getOldValue();
            Integer newId = (Integer) evt.getNewValue();
            for (Assay assay : assayMap.values()) {
                if (assay.getMsRun() != null && assay.getMsRun().getId().equals(oldId)) {
                    assay.getMsRun().setId(newId);
                }
            }
        }
    }
}
