package uk.ac.ebi.pride.jmztab.model;

/**
 * There are two kinds of publication items, PubMed and doi. Both of them used in create a list of
 * publication, which split by "|" character.
 *
 * @see Publication
 *
 * User: qingwei
 * Date: 29/04/13
 */
public class PublicationItem {
    private Type type;
    private String accession;

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

    /**
     * Create a publication item.
     *
     * @param type SHOULD NOT set null
     * @param accession SHOULD NOT empty
     */
    public PublicationItem(Type type, String accession) {
        if (type == null) {
            throw new NullPointerException("Publication type can not set null!");
        }
        if (MZTabUtils.isEmpty(accession)) {
            throw new IllegalArgumentException("Publication accession can not empty!");
        }

        this.type = type;
        this.accession = accession;
    }

    public Type getType() {
        return type;
    }

    public String getAccession() {
        return accession;
    }

    /**
     * PubMed ids must be prefixed by "pubmed:", DOIs by "doi:"
     */
    public String toString() {
        return type.getName() + ":" + accession;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicationItem that = (PublicationItem) o;

        if (!accession.equals(that.accession)) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + accession.hashCode();
        return result;
    }
}
