package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Used to store a row record of table.
 *
 * User: Qingwei, Johannes Griss
 * Date: 05/02/13
 */
public class AbstractMZTabRecord implements MZTabRecord {
    /**
     * Inherit AbstractMZTabRecord class can use this MZTabColumnFactory to add optional columns.
     * @see MZTabColumnFactory#addAbundanceColumn(SubUnit)
     * @see MZTabColumnFactory#addOptionColumn(String, Class)
     * @see MZTabColumnFactory#addCVParamOptionColumn(CVParam)
     */
    protected MZTabColumnFactory factory;

    private TreeMap<Integer, Object> record = new TreeMap<Integer, Object>();

    protected AbstractMZTabRecord(MZTabColumnFactory factory) {
        if (factory == null) {
            throw new NullPointerException("Not create MZTabColumn by using MZTabColumnFactory yet.");
        }

        this.factory = factory;
    }

    public void addAbundanceColumn(SubUnit subUnit) {
        factory.addAbundanceColumn(subUnit);
    }

    public void addOptionColumn(String name, Class dataType) {
        factory.addOptionColumn(name, dataType);
    }

    public void addCVParamOptionColumn(CVParam param) {
        factory.addCVParamOptionColumn(param);
    }

    public SplitList<String> getHeaderList() {
        return factory.getHeaderList();
    }

    public MZTabColumn getColumn(Integer position) {
        return factory.getColumn(position);
    }

    public SortedMap<Integer, MZTabColumn> getColumnMapping() {
        return factory.getColumnMapping();
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
        if (isMatch(position, value.getClass())) {
            record.put(position, value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * add a couple of values into record. Position start with 1.
     */
    public boolean addAllValues(List<Object> values) {
        boolean success = true;
        for (int i = 0; i < values.size(); i++) {
            success = addValue(i + 1, values.get(i));
            if (! success) {
                break;
            }
        }
        return success;
    }

    /**
     * Tab split string.
     * value1   value2  value3  ...
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Iterator it = record.values().iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(MZTabConstants.TAB).append(it.next());
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
    public BigDecimal getBigDecimal(int position) {
        if (! isMatch(position, BigDecimal.class)) {
            return null;
        }

        return (BigDecimal) record.get(position);
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
    public Object getOptional(int position) {
        return record.get(position);
    }
}
