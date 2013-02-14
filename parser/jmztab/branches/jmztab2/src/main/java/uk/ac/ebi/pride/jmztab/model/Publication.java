package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * A publication on this unit. PubMed ids must be prefixed by “pubmed:”, DOIs by “doi:”.
 *
 * User: Qingwei
 * Date: 01/02/13
 */
public class Publication {
    public enum Type {
        PUBMED         ("pubmed"),
        DOI            ("doi");

        private String name;
        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private SplitList<String> itemList = new SplitList<String>(MZTabConstants.BAR);

    public void addPublication(Type type, String accession) {
        if (type == null) {
            throw new NullPointerException("Publication type can not set null!");
        }
        if (accession == null) {
            throw new IllegalArgumentException("Publication accession can not empty!");
        }

        itemList.add(type.getName() + MZTabConstants.COLON + accession);
    }

    public int size() {
        return itemList.size();
    }

    public String get(int index) {
        return itemList.get(index);
    }

    public void clear() {
        itemList.clear();
    }

    public String toString() {
        return itemList.toString();
    }

    public static Type findType(String name) {
        if (name == null) {
            return null;
        }

        Type type;
        try {
            type = Type.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            type = null;
        }

        return type;
    }
}
