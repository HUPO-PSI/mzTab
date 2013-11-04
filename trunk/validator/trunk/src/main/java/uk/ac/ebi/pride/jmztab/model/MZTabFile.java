package uk.ac.ebi.pride.jmztab.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class MZTabFile {
    private Metadata metadata;

    private MZTabColumnFactory proteinColumnFactory;
    private MZTabColumnFactory peptideColumnFactory;
    private MZTabColumnFactory psmColumnFactory;
    private MZTabColumnFactory smallMoleculeColumnFactory;

    /**
     * Integer: line number
     */
    private SortedMap<Integer, Comment> comments = new TreeMap<Integer, Comment>();
    private SortedMap<Integer, Protein> proteins = new TreeMap<Integer, Protein>();
    private SortedMap<Integer, Peptide> peptides = new TreeMap<Integer, Peptide>();
    private SortedMap<Integer, PSM> psms = new TreeMap<Integer, PSM>();
    private SortedMap<Integer, SmallMolecule> smallMolecules = new TreeMap<Integer, SmallMolecule>();

    public MZTabFile(Metadata metadata) {
        this.metadata = metadata;
    }

    public Collection<Comment> getComments() {
        return Collections.unmodifiableCollection(comments.values());
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public MZTabColumnFactory getProteinColumnFactory() {
        return proteinColumnFactory;
    }

    public MZTabColumnFactory getPeptideColumnFactory() {
        return peptideColumnFactory;
    }

    public MZTabColumnFactory getPsmColumnFactory() {
        return psmColumnFactory;
    }

    public MZTabColumnFactory getSmallMoleculeColumnFactory() {
        return smallMoleculeColumnFactory;
    }

    public void setProteinColumnFactory(MZTabColumnFactory proteinColumnFactory) {
        if (proteinColumnFactory == null) {
            this.proteinColumnFactory = null;
            return;
        }

        this.proteinColumnFactory = proteinColumnFactory;
    }

    public void setPeptideColumnFactory(MZTabColumnFactory peptideColumnFactory) {
        if (peptideColumnFactory == null) {
            this.peptideColumnFactory = null;
            return;
        }
        this.peptideColumnFactory = peptideColumnFactory;
    }

    public void setPSMColumnFactory(MZTabColumnFactory psmColumnFactory) {
        if (psmColumnFactory == null) {
            this.psmColumnFactory = null;
            return;
        }

        this.psmColumnFactory = psmColumnFactory;
    }

    public void setSmallMoleculeColumnFactory(MZTabColumnFactory smallMoleculeColumnFactory) {
        if (smallMoleculeColumnFactory == null) {
            this.smallMoleculeColumnFactory = null;
            return;
        }

        this.smallMoleculeColumnFactory = smallMoleculeColumnFactory;
    }

    public void addProtein(Protein protein) {
        if (protein == null) {
            throw new NullPointerException("Protein record is null!");
        }

        Integer lineNumber = this.proteins.isEmpty() ? 1 : this.proteins.lastKey() + 1;
        this.proteins.put(lineNumber, protein);
    }

    public void addProtein(Integer lineNumber, Protein protein) {
        if (protein == null) {
            throw new NullPointerException("Protein record is null!");
        }

        if (proteins.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist protein record in line number " + lineNumber);
        }

        this.proteins.put(lineNumber, protein);
    }

    public void addPeptide(Peptide peptide) {
        if (peptide == null) {
            throw new NullPointerException("Peptide record is null!");
        }

        Integer position = this.peptides.isEmpty() ? 1 : this.peptides.lastKey() + 1;
        this.peptides.put(position, peptide);
    }

    public void addPeptide(Integer lineNumber, Peptide peptide) {
        if (peptide == null) {
            throw new NullPointerException("Peptide record is null!");
        }

        if (peptides.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist peptide record in line number " + lineNumber);
        }

        this.peptides.put(lineNumber, peptide);
    }

    public void addPSM(PSM psm) {
        if (psm == null) {
            throw new NullPointerException("PSM record is null!");
        }

        Integer position = this.psms.isEmpty() ? 1 : this.psms.lastKey() + 1;
        this.psms.put(position, psm);
    }

    public void addPSM(Integer lineNumber, PSM psm) {
        if (psm == null) {
            throw new NullPointerException("PSM record is null!");
        }

        if (psms.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist PSM record in line number " + lineNumber);
        }

        this.psms.put(lineNumber, psm);
    }

    public void addSmallMolecule(SmallMolecule smallMolecule) {
        if (smallMolecule == null) {
            throw new NullPointerException("Small Molecule record is null!");
        }

        Integer position = this.smallMolecules.isEmpty() ? 1 : this.smallMolecules.lastKey() + 1;
        this.smallMolecules.put(position, smallMolecule);
    }

    public void addSmallMolecule(Integer lineNumber, SmallMolecule smallMolecule) {
        if (smallMolecule == null) {
            throw new NullPointerException("Small Molecule record is null!");
        }

        if (smallMolecules.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist small molecule record in line number " + lineNumber);
        }

        this.smallMolecules.put(lineNumber, smallMolecule);
    }

    public void addComment(Integer lineNumber, Comment comment) {
        if (comment == null) {
            throw new NullPointerException("Comment record is null!");
        }

        if (comments.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist comment in line number " + lineNumber);
        }

        this.comments.put(lineNumber, comment);
    }

    /**
     * Returns all proteins identified by the given accession.
     *
     * @param accession The accession identifying the proteins.
     * @return A collection of proteins identified by the given accession.
     */
    public Collection<Protein> getProteins(String accession) {
        Collection<Protein> result = new ArrayList<Protein>();

        for (Protein record : proteins.values()) {
            if (record.getAccession().equals(accession)) {
                result.add(record);
            }
        }

        return Collections.unmodifiableCollection(result);
    }

    /**
     * Returns a Collection holding all proteins identified in this mzTabFile.
     *
     * @return A Collection of ProteinS
     */
    public Collection<Protein> getProteins() {
        return Collections.unmodifiableCollection(proteins.values());
    }

    public SortedMap<Integer, Protein> getProteinsWithLineNumber() {
        return Collections.unmodifiableSortedMap(proteins);
    }

    /**
     * Returns a Collection holding all peptides found in the mzTab file.
     *
     * @return A Collection of peptides.
     */
    public Collection<Peptide> getPeptides() {
        return Collections.unmodifiableCollection(peptides.values());
    }

    /**
     * Returns a Collection holding all PSMs found in the mzTab file.
     *
     * @return A Collection of PSMs.
     */
    public Collection<PSM> getPSMs() {
        return Collections.unmodifiableCollection(psms.values());
    }

    /**
     * Returns all SmallMoleculeS identified in the mzTab file.
     *
     * @return A Collection of SmallMoleculeS
     */
    public Collection<SmallMolecule> getSmallMolecules() {
        return Collections.unmodifiableCollection(smallMolecules.values());
    }

    public void printMZTab(OutputStream out) throws IOException {
        out.write(metadata.toString().getBytes());
        out.write(NEW_LINE.getBytes());

        // print protein
        if (proteinColumnFactory != null) {
            out.write(proteinColumnFactory.toString().getBytes());
            out.write(NEW_LINE.getBytes());
            for (Protein protein : proteins.values()) {
                out.write(protein.toString().getBytes());
                out.write(NEW_LINE.getBytes());
            }
            out.write(NEW_LINE.getBytes());
        }

        // print peptide
        if (peptideColumnFactory != null) {
            out.write(peptideColumnFactory.toString().getBytes());
            out.write(NEW_LINE.getBytes());

            for (Peptide peptide : peptides.values()) {
                out.write(peptide.toString().getBytes());
                out.write(NEW_LINE.getBytes());
            }
            out.write(NEW_LINE.getBytes());
        }

        // print PSM
        if (psmColumnFactory != null) {
            out.write(psmColumnFactory.toString().getBytes());
            out.write(NEW_LINE.getBytes());

            for (PSM psm : psms.values()) {
                out.write(psm.toString().getBytes());
                out.write(NEW_LINE.getBytes());
            }
            out.write(NEW_LINE.getBytes());
        }

        // print small molecule
        if (smallMoleculeColumnFactory != null) {
            out.write(smallMoleculeColumnFactory.toString().getBytes());
            out.write(NEW_LINE.getBytes());

            for (SmallMolecule smallMolecule : smallMolecules.values()) {
                out.write(smallMolecule.toString().getBytes());
                out.write(NEW_LINE.getBytes());
            }
            out.write(NEW_LINE.getBytes());
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(metadata).append(NEW_LINE);

        if (proteinColumnFactory != null) {
            sb.append(proteinColumnFactory).append(NEW_LINE);
            for (Protein protein : proteins.values()) {
                sb.append(protein).append(NEW_LINE);
            }
            sb.append(NEW_LINE);
        }

        if (peptideColumnFactory != null) {
            sb.append(peptideColumnFactory).append(NEW_LINE);
            for (Peptide peptide : peptides.values()) {
                sb.append(peptide).append(NEW_LINE);
            }
            sb.append(NEW_LINE);
        }

        if (psmColumnFactory != null) {
            sb.append(psmColumnFactory).append(NEW_LINE);
            for (PSM psm : psms.values()) {
                sb.append(psm).append(NEW_LINE);
            }
            sb.append(NEW_LINE);
        }

        if (smallMoleculeColumnFactory != null) {
            sb.append(smallMoleculeColumnFactory).append(NEW_LINE);
            for (SmallMolecule smallMolecule : smallMolecules.values()) {
                sb.append(smallMolecule).append(NEW_LINE);
            }
            sb.append(NEW_LINE);
        }

        return sb.toString();
    }
}
