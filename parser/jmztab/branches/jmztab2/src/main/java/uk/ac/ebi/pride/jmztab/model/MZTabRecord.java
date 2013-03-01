package uk.ac.ebi.pride.jmztab.model;

/**
 * MZTabRecord used to store a row record of the table. Most of method have been implemented in AbstractMZTabRecord.
 * @see AbstractMZTabRecord
 *
 * User: Qingwei
 * Date: 05/02/13
 */
public interface MZTabRecord {
    public String getString(int position);
    public Integer getInteger(int position);
    public Double getDouble(int position);
    public SplitList getSplitList(int position);
    public java.net.URI getURI(int position);
    public Reliability getReliability(int position);
    public MZBoolean getMZBoolean(int position);
    public Object getOptional(int position);
}
