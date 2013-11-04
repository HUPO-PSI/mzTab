package uk.ac.ebi.pride.jmztab.model;

/**
 * Every line in an mzTab file MUST start with a three letter code identifying the type of line delimited by
 * a Tab character. The three letter codes are as follows :
 * - MTD for metadata
 * - PRH for the protein table header line (the column labels)
 * - PRT for rows of the protein table
 * - PEH for the peptide table header line (the column labels)
 * - PEP for rows of the peptide table
 * - PSH for the PSM table header (the column labels)
 * - PSM for rows of the PSM table
 * - SMH for small molecule table header line (the column labels)
 * - SML for rows of the small molecule table
 * - COM for comment lines
 *
 * User: Qingwei, Johannes Griss
 * Date: 31/01/13
 */
public enum Section {
    Comment                  ("COM", "comment",                  0),
    Metadata                 ("MTD", "metadata",                 1),
    Protein_Header           ("PRH", "protein_header",           2),
    Protein                  ("PRT", "protein",                  3),
    Peptide_Header           ("PEH", "peptide_header",           4),
    Peptide                  ("PEP", "peptide",                  5),
    PSM_Header               ("PSH", "psm_header",               6),
    PSM                      ("PSM", "psm",                      7),
    Small_Molecule_Header    ("SMH", "small_molecule_header",    8),
    Small_Molecule           ("SML", "small_molecule",           9);

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

    public static Section findSection(int level) {
        Section section;
        switch (level) {
            case 0:
                section = Comment;
                break;
            case 1:
                section = Metadata;
                break;
            case 2:
                section = Protein_Header;
                break;
            case 3:
                section = Protein;
                break;
            case 4:
                section = Peptide_Header;
                break;
            case 5:
                section = Peptide;
                break;
            case 6:
                section = PSM_Header;
                break;
            case 7:
                section = PSM;
                break;
            case 8:
                section = Small_Molecule_Header;
                break;
            case 9:
                section = Small_Molecule;
                break;
            default:
                section = null;
        }

        return section;
    }

    public boolean isComment() {
        return this == Comment;
    }

    public boolean isMetadata() {
        return this == Metadata;
    }

    public boolean isHeader() {
        return this == Protein_Header || this == Peptide_Header || this == PSM_Header || this == Small_Molecule_Header;
    }

    public boolean isData() {
        return this == Protein || this == Peptide || this == PSM || this == Small_Molecule;
    }

    public static Section toHeaderSection(Section section) {
        Section header = null;
        switch (section) {
            case Peptide:
            case Peptide_Header:
                header = Section.Peptide_Header;
                break;
            case Protein:
            case Protein_Header:
                header = Section.Protein_Header;
                break;
            case PSM:
            case PSM_Header:
                header = Section.PSM_Header;
                break;
            case Small_Molecule:
            case Small_Molecule_Header:
                header = Section.Small_Molecule_Header;
                break;
            default:
                header = null;
        }

        return header;
    }

    public static Section toDataSection(Section section) {
        Section data = null;
        switch (section) {
            case Peptide:
            case Peptide_Header:
                data = Section.Peptide;
                break;
            case Protein:
            case Protein_Header:
                data = Section.Protein;
                break;
            case PSM:
            case PSM_Header:
                data = Section.PSM;
                break;
            case Small_Molecule:
            case Small_Molecule_Header:
                data = Section.Small_Molecule;
                break;
            default:
                data = null;
        }

        return data;
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
        } else if (name.equals(PSM_Header.getName()) || name.equals(PSM_Header.getPrefix())) {
            return PSM_Header;
        } else if (name.equals(PSM.getName()) || name.equals(PSM.getPrefix())) {
            return PSM;
        } else if (name.equals(Small_Molecule_Header.getName()) || name.equals(Small_Molecule_Header.getPrefix())) {
            return Small_Molecule_Header;
        } else if (name.equals(Small_Molecule.getName()) || name.equals(Small_Molecule.getPrefix())) {
            return Small_Molecule;
        } else {
            return null;
        }
    }
}
