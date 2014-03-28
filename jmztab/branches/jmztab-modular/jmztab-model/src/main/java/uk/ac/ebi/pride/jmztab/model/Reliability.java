package uk.ac.ebi.pride.jmztab.model;

/**
 * All protein, peptide, psm and small molecule identifications reported in an mzTab file MAY be assigned a
 * reliability score (column "reliability" in all tables). This reliability only applies to the identification
 * reliability but not to modification position and or quantification reliabilities. The idea is to provide a way
 * for researchers and/or MS proteomics or metabolomics repositories to score the reported identifications based
 * on their own criteria. This score is completely resource-dependent and MUST NOT be interpreted as a comparable
 * score between mzTab files generated from different resources. The criteria used to generate this score SHOULD be
 * documented by the data providers. If this information is not provided by the producers of mzTab files, "null"
 * MUST be provided as the value for each of the protein, peptide or small molecule identification.
 *
 * This must be supplied by the resource and has to be one of the following:
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
     * Mapping label to Reliability object.
     *
     * @param reliabilityLabel 1: high reliability; 2: medium reliability; 3: poor reliability
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
