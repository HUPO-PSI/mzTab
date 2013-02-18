package uk.ac.ebi.pride.jmztab.model;

import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public class AbundanceColumn implements MZTabColumn {
    public enum Field {
        ABUNDANCE          ("abundance",              BigDecimal.class,    1),
        ABUNDANCE_STDEV    ("abundance_stdev",        BigDecimal.class,    2),
        ABUNDANCE_STD_ERROR("abundance_std_error",    BigDecimal.class,    3);

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
    private int offset;
    private SubUnit subUnit;

    private AbundanceColumn(Section section, Field field, int offset, SubUnit subUnit) {
        this.section = section;
        this.field = field;
        this.offset = offset;
        this.subUnit = subUnit;
    }

    /**
     * Create three {section}_[abundance|abundance_stdev|abundance_std_error]_{subUnit} columns at the
     * right of table. {@link #offset} is the position of the last column of table.
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
        return section.getName() + "_" + field.name + "_sub[" + subUnit.getSubId() + "]";
    }

    @Override
    public Class getColumnType() {
        return field.columnType;
    }

    @Override
    public int getPosition() {
        return offset + field.position;
    }

    public static Field findField(String name) {
        if (name == null) {
            return null;
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
