package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseDouble;

/**
 * MZTabRecord used to store a row record of the table.
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class MZTabRecord {
    protected MZTabColumnFactory factory;

    private TreeMap<String, Object> record = new TreeMap<String, Object>();

    public MZTabRecord(MZTabColumnFactory factory) {
        if (factory == null) {
            throw new NullPointerException("Not create MZTabColumn by using MZTabColumnFactory yet.");
        }

        this.factory = factory;
        for (String position : factory.getColumnMapping().keySet()) {
            setValue(position, null);
        }
    }

    /**
     * validate the mzTabColumn's dataType match with the data's valueType.
     *
     * @see uk.ac.ebi.pride.jmztab.model.MZTabColumn#getDataType()
     */
    private boolean isMatch(String position, Class valueType) {
        MZTabColumn column = factory.getColumnMapping().get(position);
        if (column == null) {
            return false;
        }

        Class columnType = column.getDataType();
        return valueType == columnType;
    }

    public boolean setValue(String position, Object value) {
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

    public Object getValue(String position) {
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

    protected String getString(String position) {
        if (! isMatch(position, String.class)) {
            return null;
        }

        return (String) record.get(position);
    }

    protected Integer getInteger(String position) {
        if (! isMatch(position, Integer.class)) {
            return null;
        }

        return (Integer) record.get(position);
    }

    protected Double getDouble(String position) {
        if (! isMatch(position, Double.class)) {
            return null;
        }

        return (Double) record.get(position);
    }

    protected SplitList getSplitList(String position) {
        if (! isMatch(position, SplitList.class)) {
            return null;
        }

        return (SplitList) record.get(position);
    }

    protected URI getURI(String position) {
        if (! isMatch(position, URI.class)) {
            return null;
        }

        return (URI) record.get(position);
    }

    protected Reliability getReliability(String position) {
        if (! isMatch(position, Reliability.class)) {
            return null;
        }

        return (Reliability) record.get(position);
    }

    protected MZBoolean getMZBoolean(String position) {
        if (! isMatch(position, MZBoolean.class)) {
            return null;
        }

        return (MZBoolean) record.get(position);
    }

    protected String getPosition(MZTabColumn column, IndexedElement element) {
        return column.getOrder() + element.getId();
    }

    private MZTabColumn getColumn(String tag, IndexedElement element) {
        Section dataSection = Section.toDataSection(factory.getSection());
        String header = dataSection.getName() + tag + element.getReference();

        return factory.findColumn(header);
    }

    public Double getAbundanceColumn(Assay assay) {
        MZTabColumn column = getColumn("_abundance_", assay);
        if (column == null) {
            return null;
        } else {
            return getDouble(column.getPosition());
        }
    }

    public void setAbundanceColumn(Assay assay, double value) {
        MZTabColumn column = getColumn("_abundance_", assay);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    public void setAbundanceColumn(Assay assay, String valueLabel) {
        setAbundanceColumn(assay, parseDouble(valueLabel));
    }

    public Double getAbundanceColumn(StudyVariable studyVariable) {
        MZTabColumn column = getColumn("_abundance_", studyVariable);
        if (column == null) {
            return null;
        } else {
            return getDouble(column.getPosition());
        }
    }

    public void setAbundanceColumn(StudyVariable studyVariable, double value) {
        MZTabColumn column = getColumn("_abundance_", studyVariable);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    public void setAbundanceColumn(StudyVariable studyVariable, String valueLabel) {
        setAbundanceColumn(studyVariable, parseDouble(valueLabel));
    }

    public Double getAbundanceStdevColumn(StudyVariable studyVariable) {
        MZTabColumn column = getColumn("_abundance_stdev_", studyVariable);
        if (column == null) {
            return null;
        } else {
            return getDouble(column.getPosition());
        }
    }

    public void setAbundanceStdevColumn(StudyVariable studyVariable, double value) {
        MZTabColumn column = getColumn("_abundance_stdev_", studyVariable);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    public void setAbundanceStdevColumn(StudyVariable studyVariable, String valueLabel) {
        setAbundanceStdevColumn(studyVariable, parseDouble(valueLabel));
    }

    public Double getAbundanceStdErrorColumn(StudyVariable studyVariable) {
        MZTabColumn column = getColumn("_abundance_std_error_", studyVariable);
        if (column == null) {
            return null;
        } else {
            return getDouble(column.getPosition());
        }
    }

    public void setAbundanceStdErrorColumn(StudyVariable studyVariable, double value) {
        MZTabColumn column = getColumn("_abundance_std_error_", studyVariable);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    public void setAbundanceStdErrorColumn(StudyVariable studyVariable, String valueLabel) {
        setAbundanceStdErrorColumn(studyVariable, parseDouble(valueLabel));
    }

    /**
     * return user defined optional column,
     * opt_{ASSAY_ID}_name column's record value.
     *
     * If not find the optional column, return null;
     */
    public String getOptionColumn(Assay assay, String name) {
        String header = OptionColumn.getHeader(assay, name);
        MZTabColumn column = factory.findColumn(header);
        return column == null ? null : getString(column.getPosition());
    }

    public void setOptionColumn(Assay assay, String name, String value) {
        String header = OptionColumn.getHeader(assay, name);
        MZTabColumn column = factory.findColumn(header);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    public String getOptionColumn(Assay assay, CVParam param) {
        String header = CVParamOptionColumn.getHeader(assay, param);
        MZTabColumn column = factory.findColumn(header);
        return column == null ? null : getString(column.getPosition());
    }

    public void setOptionColumn(Assay assay, CVParam param, String value) {
        String header = CVParamOptionColumn.getHeader(assay, param);
        MZTabColumn column = factory.findColumn(header);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    /**
     * return user defined optional column,
     * opt_{STUDY_VARIABLE_ID}_name column's record value.
     *
     * If not find the optional column, return null;
     */
    public String getOptionColumn(StudyVariable studyVariable, String name) {
        String header = OptionColumn.getHeader(studyVariable, name);
        MZTabColumn column = factory.findColumn(header);
        return column == null ? null : getString(column.getPosition());
    }

    public void setOptionColumn(StudyVariable studyVariable, String name, String value) {
        String header = OptionColumn.getHeader(studyVariable, name);
        MZTabColumn column = factory.findColumn(header);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    public String getOptionColumn(StudyVariable studyVariable, CVParam param) {
        String header = CVParamOptionColumn.getHeader(studyVariable, param);
        MZTabColumn column = factory.findColumn(header);
        return column == null ? null : getString(column.getPosition());
    }

    public void setOptionColumn(StudyVariable studyVariable, CVParam param, String value) {
        String header = CVParamOptionColumn.getHeader(studyVariable, param);
        MZTabColumn column = factory.findColumn(header);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    /**
     * return user defined optional column,
     * opt_{MS_FILE_ID}_name column's record value.
     *
     * If not find the optional column, return null;
     */
    public String getOptionColumn(MsFile msFile, String name) {
        String header = OptionColumn.getHeader(msFile, name);
        MZTabColumn column = factory.findColumn(header);
        return column == null ? null : getString(column.getPosition());
    }

    public void setOptionColumn(MsFile msFile, String name, String value) {
        String header = OptionColumn.getHeader(msFile, name);
        MZTabColumn column = factory.findColumn(header);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    public String getOptionColumn(MsFile msFile, CVParam param) {
        String header = CVParamOptionColumn.getHeader(msFile, param);
        MZTabColumn column = factory.findColumn(header);
        return column == null ? null : getString(column.getPosition());
    }

    public void setOptionColumn(MsFile msFile, CVParam param, String value) {
        String header = CVParamOptionColumn.getHeader(msFile, param);
        MZTabColumn column = factory.findColumn(header);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    /**
     * return user defined optional column,
     * opt_global_name column's record value.
     *
     * If not find the optional column, return null;
     */
    public String getOptionColumn(String name) {
        String header = OptionColumn.getHeader(null, name);
        MZTabColumn column = factory.findColumn(header);
        return column == null ? null : getString(column.getPosition());
    }

    public void setOptionColumn(String name, Object value) {
        String header = OptionColumn.getHeader(null, name);
        MZTabColumn column = factory.findColumn(header);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }

    public String getOptionColumn(CVParam param) {
        String header = CVParamOptionColumn.getHeader(null, param);
        MZTabColumn column = factory.findColumn(header);
        return column == null ? null : getString(column.getPosition());
    }

    public void setOptionColumn(CVParam param, Object value) {
        String header = CVParamOptionColumn.getHeader(null, param);
        MZTabColumn column = factory.findColumn(header);
        if (column != null) {
            setValue(column.getPosition(), value);
        }
    }
}
