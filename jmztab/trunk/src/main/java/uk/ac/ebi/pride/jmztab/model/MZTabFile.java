package uk.ac.ebi.pride.jmztab.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * In the jmzTab core model, the MZTabFile class is the central entry point to manage the internal relationships
 * among the different sections in the file. It contains three key components: i) Metadata, which is a mandatory
 * meta-model that provides the definitions contained in the dataset included in the file; ii) {@link MZTabColumnFactory},
 * a factory class that can be used to generate stable {@link MZTabColumn} elements, and to add dynamically different
 * optional columns (like e.g. protein and peptide abundance related columns). The {@link Metadata} and {@link MZTabColumnFactory}
 * constitute the framework for the MZTabFile class; and iii) Consistency constraints among the different sections
 * of the model. For example, the MZTabFile class supports the iterative modification of the elements '{@link MsRun}',
 * '{@link Sample}', '{@link StudyVariable}', and '{@link Assay}' assigned numbers (1-n) and its location in the file,
 * maintaining the  internal consistency between the Metadata section and the optional elements in the table-based sections.
 * These methods are particularly useful when information coming from different experiments (e.g. ms runs) is
 * condensed in a single mzTab file.
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class MZTabFile {
    // The metadata section.
    private Metadata metadata;

    // header line section.
    private MZTabColumnFactory proteinColumnFactory;
    private MZTabColumnFactory peptideColumnFactory;
    private MZTabColumnFactory psmColumnFactory;
    private MZTabColumnFactory smallMoleculeColumnFactory;

    // The line number indexed sorted map.
    private SortedMap<Integer, Comment> comments = new TreeMap<Integer, Comment>();
    private SortedMap<Integer, Protein> proteins = new TreeMap<Integer, Protein>();
    private SortedMap<Integer, Peptide> peptides = new TreeMap<Integer, Peptide>();
    private SortedMap<Integer, PSM> psms = new TreeMap<Integer, PSM>();
    private SortedMap<Integer, SmallMolecule> smallMolecules = new TreeMap<Integer, SmallMolecule>();

    /**
     * Create a MZTabFile with defined metadata.
     *
     * @param metadata SHOULD NOT set null.
     */
    public MZTabFile(Metadata metadata) {
        if (metadata == null) {
            throw new NullPointerException("Metadata should be created first.");
        }

        this.metadata = metadata;
    }

    /**
     * Get all comment line in mzTab. Comment lines can be placed anywhere in an mzTab file. These lines must
     * start with the three-letter code COM and are ignored by most parsers. Empty lines can also occur anywhere
     * in an mzTab file and are ignored.
     *
     * @return a unmodifiable collection.
     */
    public Collection<Comment> getComments() {
        return Collections.unmodifiableCollection(comments.values());
    }

    /**
     * Get the metadata section can provide additional information about the dataset(s) reported in the mzTab file.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Get the Protein header line column factory.
     */
    public MZTabColumnFactory getProteinColumnFactory() {
        return proteinColumnFactory;
    }

    /**
     * Get the Peptide header line column factory.
     */
    public MZTabColumnFactory getPeptideColumnFactory() {
        return peptideColumnFactory;
    }

    /**
     * Get the PSM header line column factory.
     */
    public MZTabColumnFactory getPsmColumnFactory() {
        return psmColumnFactory;
    }

    /**
     * Get the Small Molecule header line column factory.
     */
    public MZTabColumnFactory getSmallMoleculeColumnFactory() {
        return smallMoleculeColumnFactory;
    }

    /**
     * Set the Protein header line column factory.
     *
     * @param proteinColumnFactory if null, system will ignore Protein table output.
     */
    public void setProteinColumnFactory(MZTabColumnFactory proteinColumnFactory) {
        this.proteinColumnFactory = proteinColumnFactory;
    }

    /**
     * Set the Peptide header line column factory.
     *
     * @param peptideColumnFactory if null, system will ignore Peptide table output.
     */
    public void setPeptideColumnFactory(MZTabColumnFactory peptideColumnFactory) {
        this.peptideColumnFactory = peptideColumnFactory;
    }

    /**
     * Set the PSM header line column factory.
     *
     * @param psmColumnFactory if null, system will ignore PSM table output.
     */
    public void setPSMColumnFactory(MZTabColumnFactory psmColumnFactory) {
        this.psmColumnFactory = psmColumnFactory;
    }

    /**
     * Set the Small Molecule header line column factory.
     *
     * @param smallMoleculeColumnFactory if null, system will ignore Small Molecule table output.
     */
    public void setSmallMoleculeColumnFactory(MZTabColumnFactory smallMoleculeColumnFactory) {
        this.smallMoleculeColumnFactory = smallMoleculeColumnFactory;
    }

    /**
     * Add a Protein record.
     *
     * @param protein SHOULD NOT set null.
     */
    public void addProtein(Protein protein) {
        if (protein == null) {
            throw new NullPointerException("Protein record is null!");
        }

        Integer lineNumber = this.proteins.isEmpty() ? 1 : this.proteins.lastKey() + 1;
        this.proteins.put(lineNumber, protein);
    }

    /**
     * Add a Protein record.
     *
     * @param lineNumber SHOULD be positive integer
     * @param protein SHOULD NOT set null.
     *
     * @throws IllegalArgumentException if there exists Protein object for assigned lineNumber
     */
    public void addProtein(Integer lineNumber, Protein protein) {
        if (protein == null) {
            throw new NullPointerException("Protein record is null!");
        }
        if (lineNumber <= 0) {
            throw new IllegalArgumentException("Line number should be positive integer");
        }
        if (proteins.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist protein record in line number " + lineNumber);
        }

        this.proteins.put(lineNumber, protein);
    }

    /**
     * Add a Peptide record.
     *
     * @param peptide SHOULD NOT set null.
     */
    public void addPeptide(Peptide peptide) {
        if (peptide == null) {
            throw new NullPointerException("Peptide record is null!");
        }

        Integer position = this.peptides.isEmpty() ? 1 : this.peptides.lastKey() + 1;
        this.peptides.put(position, peptide);
    }

    /**
     * Add a Peptide record.
     *
     * @param lineNumber SHOULD be positive integer
     * @param peptide SHOULD NOT set null.
     *
     * @throws IllegalArgumentException if there exists Peptide object for assigned lineNumber
     */
    public void addPeptide(Integer lineNumber, Peptide peptide) {
        if (peptide == null) {
            throw new NullPointerException("Peptide record is null!");
        }
        if (lineNumber <= 0) {
            throw new IllegalArgumentException("Line number should be positive integer");
        }
        if (peptides.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist peptide record in line number " + lineNumber);
        }

        this.peptides.put(lineNumber, peptide);
    }

    /**
     * Add a PSM record.
     *
     * @param psm SHOULD NOT set null.
     */
    public void addPSM(PSM psm) {
        if (psm == null) {
            throw new NullPointerException("PSM record is null!");
        }

        Integer position = this.psms.isEmpty() ? 1 : this.psms.lastKey() + 1;
        this.psms.put(position, psm);
    }

    /**
     * Add a PSM record.
     *
     * @param lineNumber SHOULD be positive integer
     * @param psm SHOULD NOT set null.
     *
     * @throws IllegalArgumentException if there exists PSM object for assigned lineNumber
     */
    public void addPSM(Integer lineNumber, PSM psm) {
        if (psm == null) {
            throw new NullPointerException("PSM record is null!");
        }
        if (lineNumber <= 0) {
            throw new IllegalArgumentException("Line number should be positive integer");
        }
        if (psms.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist PSM record in line number " + lineNumber);
        }

        this.psms.put(lineNumber, psm);
    }

    /**
     * Add a Small Molecule record.
     *
     * @param smallMolecule SHOULD NOT set null.
     */
    public void addSmallMolecule(SmallMolecule smallMolecule) {
        if (smallMolecule == null) {
            throw new NullPointerException("Small Molecule record is null!");
        }

        Integer position = this.smallMolecules.isEmpty() ? 1 : this.smallMolecules.lastKey() + 1;
        this.smallMolecules.put(position, smallMolecule);
    }

    /**
     * Add a SmallMolecule record.
     *
     * @param lineNumber SHOULD be positive integer
     * @param smallMolecule SHOULD NOT set null.
     *
     * @throws IllegalArgumentException if there exists SmallMolecule object for assigned lineNumber
     */
    public void addSmallMolecule(Integer lineNumber, SmallMolecule smallMolecule) {
        if (smallMolecule == null) {
            throw new NullPointerException("Small Molecule record is null!");
        }
        if (lineNumber <= 0) {
            throw new IllegalArgumentException("Line number should be positive integer");
        }
        if (smallMolecules.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist small molecule record in line number " + lineNumber);
        }

        this.smallMolecules.put(lineNumber, smallMolecule);
    }

    /**
     * Add a Comment record.
     *
     * @param lineNumber SHOULD be positive integer
     * @param comment SHOULD NOT set null.
     *
     * @throws IllegalArgumentException if there exists Protein object for assigned lineNumber
     */
    public void addComment(Integer lineNumber, Comment comment) {
        if (comment == null) {
            throw new NullPointerException("Comment record is null!");
        }
        if (lineNumber <= 0) {
            throw new IllegalArgumentException("Line number should be positive integer");
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
     * @return A unmodifiable collection of proteins identified by the given accession.
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
     * @return A unmodifiable collection of proteins
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
     * @return A unmodifiable collection of peptides.
     */
    public Collection<Peptide> getPeptides() {
        return Collections.unmodifiableCollection(peptides.values());
    }

    /**
     * Returns a Collection holding all PSMs found in the mzTab file.
     *
     * @return A unmodifiable collection of PSMs.
     */
    public Collection<PSM> getPSMs() {
        return Collections.unmodifiableCollection(psms.values());
    }

    /**
     * Returns all SmallMoleculeS identified in the mzTab file.
     *
     * @return A unmodifiable collection of SmallMolecules
     */
    public Collection<SmallMolecule> getSmallMolecules() {
        return Collections.unmodifiableCollection(smallMolecules.values());
    }

    /**
     * Judge there exists records in MZTabFile or not.
     */
    public boolean isEmpty() {
        return proteins.isEmpty() && peptides.isEmpty() && psms.isEmpty() && smallMolecules.isEmpty();
    }

    /**
     * Print MZTabFile into a output stream.
     *
     * @param out SHOULD NOT be null
     */
    public void printMZTab(OutputStream out) throws IOException {
        if (out == null) {
            throw new NullPointerException("Output stream should be defined first.");
        }

        if (isEmpty()) {
            return;
        }

        out.write(metadata.toString().getBytes());
        out.write(NEW_LINE.getBytes());

        // print comment
        for (Comment comment : comments.values()) {
            out.write(comment.toString().getBytes());
            out.write(NEW_LINE.getBytes());
        }
        if (! comments.isEmpty()) {
            out.write(NEW_LINE.getBytes());
        }

        // print protein
        if (proteinColumnFactory != null && ! proteins.isEmpty()) {
            out.write(proteinColumnFactory.toString().getBytes());
            out.write(NEW_LINE.getBytes());
            for (Protein protein : proteins.values()) {
                out.write(protein.toString().getBytes());
                out.write(NEW_LINE.getBytes());
            }
            out.write(NEW_LINE.getBytes());
        }

        // print peptide
        if (peptideColumnFactory != null && ! peptides.isEmpty()) {
            out.write(peptideColumnFactory.toString().getBytes());
            out.write(NEW_LINE.getBytes());

            for (Peptide peptide : peptides.values()) {
                out.write(peptide.toString().getBytes());
                out.write(NEW_LINE.getBytes());
            }
            out.write(NEW_LINE.getBytes());
        }

        // print PSM
        if (psmColumnFactory != null && ! psms.isEmpty()) {
            out.write(psmColumnFactory.toString().getBytes());
            out.write(NEW_LINE.getBytes());

            for (PSM psm : psms.values()) {
                out.write(psm.toString().getBytes());
                out.write(NEW_LINE.getBytes());
            }
            out.write(NEW_LINE.getBytes());
        }

        // print small molecule
        if (smallMoleculeColumnFactory != null && ! smallMolecules.isEmpty()) {
            out.write(smallMoleculeColumnFactory.toString().getBytes());
            out.write(NEW_LINE.getBytes());

            for (SmallMolecule smallMolecule : smallMolecules.values()) {
                out.write(smallMolecule.toString().getBytes());
                out.write(NEW_LINE.getBytes());
            }
            out.write(NEW_LINE.getBytes());
        }

    }

    /**
     * Translate a MZTabFile into a string.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // print comment
        for (Comment comment : comments.values()) {
            sb.append(comment).append(NEW_LINE);
        }
        if (! comments.isEmpty()) {
            sb.append(NEW_LINE);
        }

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
