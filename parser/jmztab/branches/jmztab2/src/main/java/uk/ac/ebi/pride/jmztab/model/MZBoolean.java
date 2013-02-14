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

    public static MZBoolean findBoolean(String booleanLabel) {
        booleanLabel = booleanLabel.trim();
        try {
            Integer id = new Integer(booleanLabel);
            MZBoolean mzBoolean = null;
            switch (id) {
                case 0:
                    mzBoolean = MZBoolean.False;
                    break;
                case 1:
                    mzBoolean = MZBoolean.True;
                    break;
            }
            return mzBoolean;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
