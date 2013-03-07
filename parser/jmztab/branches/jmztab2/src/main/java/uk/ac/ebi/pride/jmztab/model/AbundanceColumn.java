package uk.ac.ebi.pride.jmztab.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TreeMap;

/**
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public class AbundanceColumn implements MZTabColumn, PropertyChangeListener {
    public enum Field {
        ABUNDANCE          ("abundance",              Double.class,    1),
        ABUNDANCE_STDEV    ("abundance_stdev",        Double.class,    2),
        ABUNDANCE_STD_ERROR("abundance_std_error",    Double.class,    3);

        private String name;
        private Class columnType;
        private int position;

        Field(String name, Class columnType, int position) {
            this.name = name;
            this.columnType = columnType;
            this.position = position;
        }

        public String toString() {
            return name;
        }
    }

    private Section section;
    private Field field;
    private SubUnit subUnit;

    private int position;

    private AbundanceColumn(Section section, Field field, int offset, SubUnit subUnit) {
        this.section = section;
        this.field = field;
        this.subUnit = subUnit;

        this.position = offset + field.position;

        subUnit.addPropertyChangeListener(OperationCenter.SUB_UNIT_ID, this);
    }

    /**
     * Create three {section}_[abundance|abundance_stdev|abundance_std_error]_{subUnit} columns at the
     * right of table. {@param offset} is the position of the last column of table.
     */
    public static TreeMap<Integer, AbundanceColumn> getInstance(Section section, int offset, SubUnit subUnit) {
        if (! section.isData()) {
            throw new IllegalArgumentException("Section should use Protein, Peptide or SmallMolecule.");
        }

        if (subUnit == null) {
            throw new NullPointerException("Sub Unit should be setting first");
        }

        TreeMap<Integer, AbundanceColumn> columnMap = new TreeMap<Integer, AbundanceColumn>();

        AbundanceColumn column;
        column = new AbundanceColumn(section, Field.ABUNDANCE, offset, subUnit);
        columnMap.put(column.getPosition(), column);
        column = new AbundanceColumn(section, Field.ABUNDANCE_STDEV, offset, subUnit);
        columnMap.put(column.getPosition(), column);
        column = new AbundanceColumn(section, Field.ABUNDANCE_STD_ERROR, offset, subUnit);
        columnMap.put(column.getPosition(), column);

        return columnMap;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(OperationCenter.SUB_UNIT_ID)) {
            if (subUnit.getSubId().equals(evt.getOldValue())) {
                subUnit.setSubId((Integer) evt.getNewValue());
            }
        }
    }

    public SubUnit getSubUnit() {
        return subUnit;
    }

    public Section getSection() {
        return section;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getHeader() {
        String name = section == Section.Small_Molecule ? "smallmolecule" : section.getName();

        return name + "_" + field.name + "_sub[" + subUnit.getSubId() + "]";
    }

    @Override
    public String toString() {
        return getHeader();
    }

    @Override
    public Class getColumnType() {
        return field.columnType;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static Field findField(String name) {
        if (name == null) {
            return null;
        }

        // temporary operator. Currently specification using smallmolecule, maybe change in the future.
        if (name.equalsIgnoreCase("smallmolecule")) {
            name = "small_molecule";
        }

        Field field;
        try {
            field = Field.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            field = null;
        }

        return field;
    }
}
