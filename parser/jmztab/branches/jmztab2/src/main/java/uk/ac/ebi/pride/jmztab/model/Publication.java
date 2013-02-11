package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;
import uk.ac.ebi.pride.jmztab.utils.StringUtils;

/**
 * A publication on this unit. PubMed ids must be prefixed by “pubmed:”, DOIs by “doi:”.
 *
 * User: Qingwei
 * Date: 01/02/13
 */
public class Publication {
    private final static String PUBMED = "pubmed:";
    private final static String DOI = "doi:";

    public enum Type {Pubmed, DOIs}

    private SplitList<String> itemList = new SplitList<String>(MZTabConstants.BAR);

    public void addPublication(Type type, String accession) {
        if (type == null) {
            throw new NullPointerException("Publication type can not set null!");
        }
        if (StringUtils.isEmpty(accession)) {
            throw new IllegalArgumentException("Publication accession can not empty!");
        }

        switch (type) {
            case Pubmed:
                itemList.add(PUBMED + accession);
                break;
            case DOIs:
                itemList.add(DOI + accession);
                break;
        }
    }

    public String toString() {
        return itemList.toString();
    }
}
