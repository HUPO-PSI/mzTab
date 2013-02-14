package uk.ac.ebi.pride.jmztab.model;

/**
 * Additional columns can be added to the end of the protein table.
 * These column headers MUST start with the prefix “opt_”.
 * Column names MUST only contain the following characters:
 * ‘A’-‘Z’, ‘a’-‘z’, ‘0’-‘9’, ‘_’, ‘-’, ‘[’, ‘]’, and ‘:’.
 *
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public class OptionalColumn implements MZTabColumn {
    public final static String OPT = "opt_";

    private String name;
    private Class columnType;
    private int offset;

    /**
     * create optional opt_{name} column at the end of table. {@link #offset} is the position
     * of the rightest column of table. {@link #columnType} is the value type which allow user
     * to set for this optional column. When user add value for this column, system will execute
     * value type match operation first.
     * @see AbstractMZTabRecord#isMatch(int, Class)
     */
    protected OptionalColumn(String name, Class columnType, int offset) {
        if (name == null) {
            throw new NullPointerException("Optional Column's name can not set null!");
        }

        if (columnType == null) {
            throw new NullPointerException("Optional Column Type can not set null!");
        }

        this.name = name;
        this.columnType = columnType;
        this.offset = offset;
    }

    /**
     * create optional opt_{name} column at the end of table. {@link #offset} is the position
     * of the rightest column of table. {@link #columnType} is the value type which allow user
     * to set for this optional column. When user add value for this column, system will execute
     * value type match operation first.
     * @see AbstractMZTabRecord#isMatch(int, Class)
     */
    public static OptionalColumn getInstance(String name, Class dataType, int offset) {
        return new OptionalColumn(name, dataType, offset);
    }

    @Override
    public String getHeader() {
        return OPT + name;
    }

    @Override
    public Class getColumnType() {
        return columnType;
    }

    @Override
    public int getPosition() {
        return offset + 1;
    }
}
