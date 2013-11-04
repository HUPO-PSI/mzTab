package uk.ac.ebi.pride.jmztab.model;

/**
 * This must be supplied by the resource and has to be one of the following values:
 * 1: high reliability
 * 2: medium reliability
 * 3: poor reliability
 *
 * User: Qingwei
 * Date: 31/01/13
 */
public enum Reliability {
    High(        "high reliability",           1),
    Medium(      "medium reliability",         2),
    Poor(        "poor reliability",           3);

    private String name;
    private int level;

    Reliability(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "" + getLevel();
    }

    /**
     * @param reliabilityLabel 1, 2, 3
     */
    public static Reliability findReliability(String reliabilityLabel) {
        reliabilityLabel = reliabilityLabel.trim();
        try {
            Integer id = new Integer(reliabilityLabel);
            Reliability reliability = null;
            switch (id) {
                case 1:
                    reliability = Reliability.High;
                    break;
                case 2:
                    reliability = Reliability.Medium;
                    break;
                case 3:
                    reliability = Reliability.Poor;
                    break;
            }
            return reliability;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
