package uk.ac.ebi.pride.jmztab.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * User: Qingwei
 * Date: 06/03/13
 */
public class OperationCenter {
    public static final String UNIT_ID = "unit_id";

    // {unit_id}-sub[id],   id change.
    public static final String SUB_UNIT_ID = "sub_unit_id";

    // {unit_id}-rep[id],   id change.
    public static final String REP_UNIT_ID = "rep_unit_id";

    // {identifier}-element[id]    id change
    public static final String ELEMENT_ID = "element_id";

    // column position change
    public static final String POSITION = "position";

    private PropertyChangeSupport changeSupport;

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (changeSupport == null) {
            changeSupport = new PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeSupport changeSupport = this.changeSupport;
        if (changeSupport == null || oldValue == newValue) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
