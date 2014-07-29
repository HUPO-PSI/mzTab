package uk.ac.ebi.pride.jmztab.model;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseDouble;

/**
 * MZTabRecord used to store a row record of the table. The record SHOULD keep the same structure with
 * the {@link MZTabColumnFactory}, which defined in the construct method.
 *
 * @see Protein
 * @see Peptide
 * @see PSM
 * @see SmallMolecule
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public abstract class MZTabRecord {
    protected MZTabColumnFactory factory;

    private TreeMap<String, Object> record = new TreeMap<String, Object>();

    /**
     * Create a record based on {@link MZTabColumnFactory} structure. The default cell value is null.
     *
     * @param factory SHOULD NOT be null.
     */
    protected MZTabRecord(MZTabColumnFactory factory) {
        if (factory == null) {
            throw new NullPointerException("MZTabColumnFactory SHOULD be defined first.");
        }

        this.factory = factory;
        for (String position : factory.getColumnMapping().keySet()) {
            setValue(position, null);
        }
    }

    /**
     * Validate the column's data type whether match with the data's type or not.
     *
     * @see uk.ac.ebi.pride.jmztab.model.MZTabColumn#getDataType()
     */
    private boolean isMatch(String logicalPosition, Class valueType) {
        MZTabColumn column = factory.getColumnMapping().get(logicalPosition);
        if (column == null) {
            return false;
        }

        Class columnType = column.getDataType();
        return valueType == columnType;
    }

    /**
     * Set a value to a special logical position cell. Before set value, system will do a match
     * validate by calling {@link #isMatch(String, Class)}. If not match, system not do set operation
     * and return false value.
     *
     * @param logicalPosition locate the column data type definition in {@link MZTabColumnFactory}
     * @param value SHOULD NOT set null.
     */
    public boolean setValue(String logicalPosition, Object value) {
        if (value == null) {
            record.put(logicalPosition, value);
            return true;
        }

        if (isMatch(logicalPosition, value.getClass())) {
            record.put(logicalPosition, value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the value of a special logical position cell.
     */
    public Object getValue(String logicalPosition) {
        return record.get(logicalPosition);
    }

    /**
     * Internal function, mainly used to process some special value, such as "null", "NaN" and "INF".
     *
     * In the table-based sections (protein, peptide, and small molecule) there MUST NOT be any empty cells.
     * In case a given property is not available "null" MUST be used. This is, for example, the case when
     * a URI is not available for a given protein (i.e. the table cell MUST NOT be empty but "null" has to
     * be reported). If ratios are included and the denominator is zero, the "INF" value MUST be used. If
     * the result leads to calculation errors (for example 0/0), this MUST be reported as "not a number"
     * ("NaN"). In some cases, there is ambiguity with respect to these cases: e.g. in spectral counting
     * if no peptide spectrum matches are observed for a given protein, it is open for debate as to whether
     * its abundance is zero or missing ("null").
     */
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
     * Print record to a tab split string.
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

    /**
     * Get cell value and convert it to String. If can not convert, return null.
     */
    protected String getString(String logicalPosition) {
        if (! isMatch(logicalPosition, String.class)) {
            return null;
        }

        //We need to check that the retrieved string is not the "NULL" string
        String val = (String) record.get(logicalPosition);
        if(val!= null && !val.isEmpty()){
            if(val.trim().equalsIgnoreCase("null")){
                val = null;
            }
        }

        return val;
    }

    /**
     * Get cell value and convert it to Integer. If can not convert, return null.
     */
    protected Integer getInteger(String logicalPosition) {
        if (! isMatch(logicalPosition, Integer.class)) {
            return null;
        }

        return (Integer) record.get(logicalPosition);
    }

    /**
     * Get cell value and convert it to Double. If can not convert, return null.
     */
    protected Double getDouble(String logicalPosition) {
        if (! isMatch(logicalPosition, Double.class)) {
            return null;
        }

        return (Double) record.get(logicalPosition);
    }

    /**
     * Get cell value and convert it to {@link SplitList} object. If can not convert, return null.
     */
    protected SplitList getSplitList(String logicalPosition) {
        if (! isMatch(logicalPosition, SplitList.class)) {
            return null;
        }

        return (SplitList) record.get(logicalPosition);
    }

    /**
     * Get cell value and convert it to {@link URI}. If can not convert, return null.
     */
    protected URI getURI(String logicalPosition) {
        if (! isMatch(logicalPosition, URI.class)) {
            return null;
        }

        return (URI) record.get(logicalPosition);
    }

    /**
     * Get cell value and convert it to {@link Reliability}. If can not convert, return null.
     */
    protected Reliability getReliability(String logicalPosition) {
        if (! isMatch(logicalPosition, Reliability.class)) {
            return null;
        }

        return (Reliability) record.get(logicalPosition);
    }

    /**
     * Get cell value and convert it to {@link MZBoolean}. If can not convert, return null.
     */
    protected MZBoolean getMZBoolean(String logicalPosition) {
        if (! isMatch(logicalPosition, MZBoolean.class)) {
            return null;
        }

        return (MZBoolean) record.get(logicalPosition);
    }

    /**
     * Get logical position based on column's order and element id.
     *
     * order + id + element.id
     */
    protected String getLogicalPosition(MZTabColumn column, Integer id, IndexedElement element) {
        StringBuilder sb = new StringBuilder();

        sb.append(column.getOrder());
        if (id != null) {
            // generate id string which length is 2. Eg. 12, return 12; 1, return 01
            sb.append(String.format("%02d", id));
        } else {
            sb.append("00");
        }
        if (element != null) {
            sb.append(String.format("%02d", element.getId()));
        } else {
            sb.append("00");
        }

        return sb.toString();
    }

    /**
     * Get a abundance column based on header. For example: protein_abundance_assay[1-n].
     *
     * NOTICE: abundance columns in {@link Section#Small_Molecule} is very special, which miss '_' character.
     * For example: smallmolecule_abundance_assay[1-n]. We use {@link AbundanceColumn#translate(String)}
     * function to overcome this problem.
     */
    private MZTabColumn getAbundanceColumn(String tag, IndexedElement element) {
        if (element == null) {
            throw new NullPointerException("Element should be provide!");
        }

        Section dataSection = Section.toDataSection(factory.getSection());
        String header = (dataSection == Section.Small_Molecule ? AbundanceColumn.translate(dataSection.getName()) : dataSection.getName()) + tag + element.getReference();

        return factory.findColumnByHeader(header);
    }

    /**
     * Get value from {section name}_abundance_assay[1-n] column cell.
     * @param assay SHOULD NOT be null.
     */
    public Double getAbundanceColumnValue(Assay assay) {
        MZTabColumn column = getAbundanceColumn("_abundance_", assay);
        if (column == null) {
            return null;
        } else {
            return getDouble(column.getLogicPosition());
        }
    }

    /**
     * Set value from {section name}_abundance_assay[1-n] column cell.
     * @param assay SHOULD NOT be null.
     */
    public void setAbundanceColumnValue(Assay assay, Double value) {
        if (assay == null) {
            return;
        }

        MZTabColumn column = getAbundanceColumn("_abundance_", assay);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Set value from {section name}_abundance_assay[1-n] column cell.
     * @param assay SHOULD NOT be null.
     */
    public void setAbundanceColumnValue(Assay assay, String valueLabel) {
        setAbundanceColumnValue(assay, parseDouble(valueLabel));
    }

    /**
     * Get value from {section name}_abundance_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public Double getAbundanceColumnValue(StudyVariable studyVariable) {
        MZTabColumn column = getAbundanceColumn("_abundance_", studyVariable);
        if (column == null) {
            return null;
        } else {
            return getDouble(column.getLogicPosition());
        }
    }

    /**
     * Set value from {section name}_abundance_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public void setAbundanceColumnValue(StudyVariable studyVariable, Double value) {
        MZTabColumn column = getAbundanceColumn("_abundance_", studyVariable);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Set value from {section name}_abundance_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public void setAbundanceColumnValue(StudyVariable studyVariable, String valueLabel) {
        setAbundanceColumnValue(studyVariable, parseDouble(valueLabel));
    }

    /**
     * Get value from {section name}_abundance_stdev_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public Double getAbundanceStdevColumnValue(StudyVariable studyVariable) {
        MZTabColumn column = getAbundanceColumn("_abundance_stdev_", studyVariable);
        if (column == null) {
            return null;
        } else {
            return getDouble(column.getLogicPosition());
        }
    }

    /**
     * Set value from {section name}_abundance_stdev_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public void setAbundanceStdevColumnValue(StudyVariable studyVariable, Double value) {
        MZTabColumn column = getAbundanceColumn("_abundance_stdev_", studyVariable);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Set value from {section name}_abundance_stdev_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public void setAbundanceStdevColumnValue(StudyVariable studyVariable, String valueLabel) {
        setAbundanceStdevColumnValue(studyVariable, parseDouble(valueLabel));
    }

    /**
     * Get value from {section name}_abundance_std_error_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public Double getAbundanceStdErrorColumnValue(StudyVariable studyVariable) {
        MZTabColumn column = getAbundanceColumn("_abundance_std_error_", studyVariable);
        if (column == null) {
            return null;
        } else {
            return getDouble(column.getLogicPosition());
        }
    }

    /**
     * Set value from {section name}_abundance_std_error_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public void setAbundanceStdErrorColumnValue(StudyVariable studyVariable, Double value) {
        MZTabColumn column = getAbundanceColumn("_abundance_std_error_", studyVariable);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Set value from {section name}_abundance_std_error_study_variable[1-n] column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public void setAbundanceStdErrorColumnValue(StudyVariable studyVariable, String valueLabel) {
        setAbundanceStdErrorColumnValue(studyVariable, parseDouble(valueLabel));
    }

    /**
     * Get value from opt_assay[1-n]_name column cell.
     * @param assay SHOULD NOT be null.
     */
    public String getOptionColumnValue(Assay assay, String name) {
        String header = OptionColumn.getHeader(assay, name);
        MZTabColumn column = factory.findColumnByHeader(header);
        return column == null ? null : getString(column.getLogicPosition());
    }

    /**
     * Set value for opt_assay[1-n]_name column cell.
     * @param assay SHOULD NOT be null.
     */
    public void setOptionColumnValue(Assay assay, String name, String value) {
        String header = OptionColumn.getHeader(assay, name);
        MZTabColumn column = factory.findColumnByHeader(header);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Get value from opt_assay[1-n]_cv_{accession} column cell.
     * @param assay SHOULD NOT be null.
     */
    public String getOptionColumnValue(Assay assay, CVParam param) {
        String header = CVParamOptionColumn.getHeader(assay, param);
        MZTabColumn column = factory.findColumnByHeader(header);
        return column == null ? null : getString(column.getLogicPosition());
    }

    /**
     * Set value for opt_assay[1-n]_cv_{accession} column cell.
     * @param assay SHOULD NOT be null.
     */
    public void setOptionColumnValue(Assay assay, CVParam param, String value) {
        String header = CVParamOptionColumn.getHeader(assay, param);
        MZTabColumn column = factory.findColumnByHeader(header);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Get value from opt_study_variable[1-n]_name column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public String getOptionColumnValue(StudyVariable studyVariable, String name) {
        String header = OptionColumn.getHeader(studyVariable, name);
        MZTabColumn column = factory.findColumnByHeader(header);
        return column == null ? null : getString(column.getLogicPosition());
    }

    /**
     * Set value for opt_study_variable[1-n]_name column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public void setOptionColumnValue(StudyVariable studyVariable, String name, String value) {
        String header = OptionColumn.getHeader(studyVariable, name);
        MZTabColumn column = factory.findColumnByHeader(header);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Get value from opt_study_variable[1-n]_cv_{accession} column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public String getOptionColumnValue(StudyVariable studyVariable, CVParam param) {
        String header = CVParamOptionColumn.getHeader(studyVariable, param);
        MZTabColumn column = factory.findColumnByHeader(header);
        return column == null ? null : getString(column.getLogicPosition());
    }

    /**
     * Set value from opt_study_variable[1-n]_cv_{accession} column cell.
     * @param studyVariable SHOULD NOT be null.
     */
    public void setOptionColumnValue(StudyVariable studyVariable, CVParam param, String value) {
        String header = CVParamOptionColumn.getHeader(studyVariable, param);
        MZTabColumn column = factory.findColumnByHeader(header);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Get value from opt_ms_run[1-n]_name column cell.
     * @param msRun SHOULD NOT be null.
     */
    public String getOptionColumnValue(MsRun msRun, String name) {
        String header = OptionColumn.getHeader(msRun, name);
        MZTabColumn column = factory.findColumnByHeader(header);
        return column == null ? null : getString(column.getLogicPosition());
    }

    /**
     * Set value for opt_ms_run[1-n]_name column cell.
     * @param msRun SHOULD NOT be null.
     */
    public void setOptionColumnValue(MsRun msRun, String name, String value) {
        String header = OptionColumn.getHeader(msRun, name);
        MZTabColumn column = factory.findColumnByHeader(header);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Get value from opt_ms_run[1-n]_name column cell.
     * @param msRun SHOULD NOT be null.
     */
    public String getOptionColumnValue(MsRun msRun, CVParam param) {
        String header = CVParamOptionColumn.getHeader(msRun, param);
        MZTabColumn column = factory.findColumnByHeader(header);
        return column == null ? null : getString(column.getLogicPosition());
    }

    /**
     * Set value for opt_ms_run[1-n]_cv_{accession} column cell.
     * @param msRun SHOULD NOT be null.
     */
    public void setOptionColumnValue(MsRun msRun, CVParam param, String value) {
        String header = CVParamOptionColumn.getHeader(msRun, param);
        MZTabColumn column = factory.findColumnByHeader(header);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Get value from opt_global_name column cell.
     */
    public String getOptionColumnValue(String name) {
        String header = OptionColumn.getHeader(null, name);
        MZTabColumn column = factory.findColumnByHeader(header);
        return column == null ? null : getString(column.getLogicPosition());
    }

    /**
     * Set value for opt_global_name column cell.
     */
    public void setOptionColumnValue(String name, Object value) {
        String header = OptionColumn.getHeader(null, name);
        MZTabColumn column = factory.findColumnByHeader(header);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }

    /**
     * Get value from opt_global_cv_{accession} column cell.
     */
    public String getOptionColumnValue(CVParam param) {
        String header = CVParamOptionColumn.getHeader(null, param);
        MZTabColumn column = factory.findColumnByHeader(header);
        return column == null ? null : getString(column.getLogicPosition());
    }

    /**
     * Set value for opt_global_cv_{accession} column cell.
     */
    public void setOptionColumnValue(CVParam param, Object value) {
        String header = CVParamOptionColumn.getHeader(null, param);
        MZTabColumn column = factory.findColumnByHeader(header);
        if (column != null) {
            setValue(column.getLogicPosition(), value);
        }
    }
}
