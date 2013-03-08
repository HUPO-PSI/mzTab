package uk.ac.ebi.pride.jmztab.model;

import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * Used to store a row record of table.
 *
 * User: Qingwei, Johannes Griss
 * Date: 05/02/13
 */
public abstract class AbstractMZTabRecord implements MZTabRecord, PropertyChangeListener {
    /**
     * Inherit AbstractMZTabRecord class can use this MZTabColumnFactory to add optional columns.
     * @see MZTabColumnFactory#addAbundanceColumns(SubUnit)
     * @see MZTabColumnFactory#addOptionalColumn(String, Class)
     * @see MZTabColumnFactory#addCVParamOptionalColumn(CVParam)
     */
    protected MZTabColumnFactory factory;

    private TreeMap<Integer, Object> record = new TreeMap<Integer, Object>();

    public AbstractMZTabRecord(MZTabColumnFactory factory) {
        if (factory == null) {
            throw new NullPointerException("Not create MZTabColumn by using MZTabColumnFactory yet.");
        }

        this.factory = factory;
        for (Integer position : factory.getColumnMapping().keySet()) {
            addValue(position,  null);
        }
    }

    /**
     * validate the mzTabColumn's dataType match with the data's valueType.
     *
     * @see uk.ac.ebi.pride.jmztab.model.MZTabColumn#getColumnType()
     */
    private boolean isMatch(int position, Class valueType) {
        MZTabColumn column = factory.getColumnMapping().get(position);
        if (column == null) {
            return false;
        }

        Class columnType = column.getColumnType();
        return valueType == columnType;
    }

    public boolean addValue(int position, Object value) {
        if (value == null) {
            record.put(position, value);
            return true;
        }

        if (isMatch(position, value.getClass())) {
            record.put(position, value);
            return true;
        } else {
            return false;
        }
    }

    public Object getValue(Integer position) {
        return record.get(position);
    }

    private Object translateValue(Object value) {
        if (value == null) {
            return NULL;
        } else if (value instanceof List) {
            if (((List)value).isEmpty()) {
                return NULL;
            } else {
                return value;
            }
        } else if (value instanceof Double) {
            if (value.equals(Double.NaN)) {
                return CALCULATE_ERROR;
            } else if (value.equals(Double.POSITIVE_INFINITY)) {
                return INFINITY;
            } else {
                return value;
            }
        } else {
            return value;
        }
    }

    /**
     * Tab split string.
     * value1   value2  value3  ...
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Iterator it = record.values().iterator();
        Object value;
        if (it.hasNext()) {
            value = translateValue(it.next());
            sb.append(value);

            while (it.hasNext()) {
                sb.append(TAB).append(translateValue(it.next()));
            }
        }

        return sb.toString();
    }

    @Override
    public String getString(int position) {
        if (! isMatch(position, String.class)) {
            return null;
        }

        return (String) record.get(position);
    }

    @Override
    public Integer getInteger(int position) {
        if (! isMatch(position, Integer.class)) {
            return null;
        }

        return (Integer) record.get(position);
    }

    @Override
    public Double getDouble(int position) {
        if (! isMatch(position, Double.class)) {
            return null;
        }

        return (Double) record.get(position);
    }

    @Override
    public SplitList getSplitList(int position) {
        if (! isMatch(position, SplitList.class)) {
            return null;
        }

        return (SplitList) record.get(position);
    }

    @Override
    public URI getURI(int position) {
        if (! isMatch(position, URI.class)) {
            return null;
        }

        return (URI) record.get(position);
    }

    @Override
    public Reliability getReliability(int position) {
        if (! isMatch(position, Reliability.class)) {
            return null;
        }

        return (Reliability) record.get(position);
    }

    @Override
    public MZBoolean getMZBoolean(int position) {
        if (! isMatch(position, MZBoolean.class)) {
            return null;
        }

        return (MZBoolean) record.get(position);
    }
}
