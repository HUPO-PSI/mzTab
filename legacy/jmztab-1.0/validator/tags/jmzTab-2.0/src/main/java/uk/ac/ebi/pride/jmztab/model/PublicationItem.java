package uk.ac.ebi.pride.jmztab.model;

/**
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

    public PublicationItem(Type type, String accession) {
        if (type == null) {
            throw new NullPointerException("Publication type can not set null!");
        }
        if (accession == null) {
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
}
