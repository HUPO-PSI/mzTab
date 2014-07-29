package uk.ac.ebi.pride.jmztab.model;

/**
 * In mzTab, using 0-false, 1-true to express the boolean value.
 *
 * User: Qingwei
 * Date: 06/02/13
 */
public enum MZBoolean {
    True("1"), False("0");

    private String value;

    /**
     * "0" for false, "1" for true.
     */
    private MZBoolean(String value) {
        this.value = value;
    }

    /**
     * @return "0" for false, "1" for true.
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * @param booleanLabel "0" or "1" which used to define a boolean used in mzTab.
     *
     * @return null if can not recognize the boolean label.
     */
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
