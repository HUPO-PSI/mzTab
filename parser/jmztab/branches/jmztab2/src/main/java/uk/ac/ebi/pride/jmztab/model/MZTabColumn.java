package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 04/02/13
 */
public interface MZTabColumn {
    /**
     * @return the position of current column. Notice, column position start with 1.
     */
    public int getPosition();

    /**
     * @return the column header string.
     */
    public String getHeader();

    /**
     * @return the data type of value, which can set for this column.
     * @see AbstractMZTabRecord#isMatch(int, Class)
     */
    public Class getColumnType();
}
