package uk.ac.ebi.pride.jmztab.model;

/**
 * In mztab, using 0-false, 1-true to express the boolean value.
 *
 * User: Qingwei
 * Date: 06/02/13
 */
public enum MZBoolean {
    True("1"), False("0");

    private String value;

    /**
     * Boolean(0/1)
     */
    private MZBoolean(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
