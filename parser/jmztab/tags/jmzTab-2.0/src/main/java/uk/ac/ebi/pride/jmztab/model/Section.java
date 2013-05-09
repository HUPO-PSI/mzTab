package uk.ac.ebi.pride.jmztab.model;

/**
 * Every line in an mzTab file MUST start with a three letter code identifying the type of line delimited by
 * a Tab character. The three letter codes are as follows :
 * - MTD for metadata
 * - PRH for the protein table header line (the column labels)
 * - PRT for rows of the protein table
 * - PEH for the peptide table header line (the column labels)
 * - PEP for rows of the peptide table
 * - SMH for small molecule table header line (the column labels)
 * - SML for rows of the small molecule table
 * - COM for comment lines
 *
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public enum Section {
    Comment              ("COM", "comment",                  0),
    Metadata             ("MTD", "metadata",                 1),
    Protein_Header       ("PRH", "protein_header",           2),
    Protein              ("PRT", "protein",                  3),
    Peptide_Header       ("PEH", "peptide_header",           4),
    Peptide              ("PEP", "peptide",                  5),
    Small_Molecule_Header("SMH", "small_molecule_header",    6),
    Small_Molecule       ("SML", "small_molecule",           7);

    private String prefix;
    private String name;
    private int level;

    private Section(String prefix, String name, int level) {
        this.prefix = prefix;
        this.name = name;
        this.level = level;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public boolean isComment() {
        return this == Comment;
    }

    public boolean isMetadata() {
        return this == Metadata;
    }

    public boolean isHeader() {
        return this == Protein_Header || this == Peptide_Header || this == Small_Molecule_Header;
    }

    public boolean isData() {
        return this == Protein || this == Peptide || this == Small_Molecule;
    }

    public static Section findSection(String name) {
        if (name == null) {
            return null;
        }

        if (name.equals(Comment.getName()) || name.equals(Comment.getPrefix())) {
            return Comment;
        } else if (name.equals(Metadata.getName()) || name.equals(Metadata.getPrefix())) {
            return Metadata;
        } else if (name.equals(Peptide_Header.getName()) || name.equals(Peptide_Header.getPrefix())) {
            return Peptide_Header;
        } else if (name.equals(Peptide.getName()) || name.equals(Peptide.getPrefix())) {
            return Peptide;
        } else if (name.equals(Protein_Header.getName()) || name.equals(Protein_Header.getPrefix())) {
            return Protein_Header;
        } else if (name.equals(Protein.getName()) || name.equals(Protein.getPrefix())) {
            return Protein;
        } else if (name.equals(Small_Molecule_Header.getName()) || name.equals(Small_Molecule_Header.getPrefix())) {
            return Small_Molecule_Header;
        } else if (name.equals(Small_Molecule.getName()) || name.equals(Small_Molecule.getPrefix())) {
            return Small_Molecule;
        } else {
            return null;
        }
    }
}
