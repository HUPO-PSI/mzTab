package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.parser.MZTabFileParser;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.NEW_LINE;

/**
 * User: qingwei
 * Date: 28/02/13
 */
public class MZTabFile {
    private Metadata metadata;

    private MZTabColumnFactory proteinColumnFactory;
    private MZTabColumnFactory peptideColumnFactory;
    private MZTabColumnFactory smallMoleculeColumnFactory;

    /**
     * Integer: line number
     */
    private SortedMap<Integer, Comment> comments;
    private SortedMap<Integer, Protein> proteins;
    private SortedMap<Integer, Peptide> peptides;
    private SortedMap<Integer, SmallMolecule> smallMolecules;

    public MZTabFile(File tabFile) throws IOException {
        this(tabFile, MZTabConstants.BUFFERED);
    }

    public MZTabFile(File tabFile, boolean buffered) throws IOException {
        MZTabFileParser parser = new MZTabFileParser(tabFile, System.out, buffered);

        this.proteinColumnFactory = parser.getProteinColumnFactory();
        this.peptideColumnFactory = parser.getPeptideColumnFactory();
        this.smallMoleculeColumnFactory = parser.getSmallMoleculeColumnFactory();

        this.metadata = parser.getMetadata();

        this.comments = parser.getComments();
        this.proteins = parser.getProteins();
        this.peptides = parser.getPeptides();
        this.smallMolecules = parser.getSmallMolecules();
    }

    public SortedMap<Integer, Comment> getComments() {
        return comments;
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

    public MZTabColumnFactory getSmallMoleculeColumnFactory() {
        return smallMoleculeColumnFactory;
    }

    /**
     * Returns the metadata for the given unit as a Unit object. Returns null in
     * case the unit does not exist.
     *
     * @param unitId The unit'd id.
     * @return A Unit object or null in case the unit doesn't exist.
     */
    public Unit getUnitMetadata(String unitId) {
        return metadata.getUnit(unitId);
    }

    /**
     * Returns the metadata as a Unit object for the protein's unit. Returns
     * null in case no metadata is availbale.
     *
     * @param protein The protein object to get the metadata for.
     * @return A Unit object or null in case no metadata was specified.
     */
    public Unit getUnitMetadata(Protein protein) {
        return getUnitMetadata(protein.getUnitId());
    }

    /**
     * Returns the metadata as a Unit object for the peptide's unit. Returns
     * null in case no metadata is availbale.
     *
     * @param peptide The peptide object to get the metadata for.
     * @return A Unit object or null in case no metadata was specified.
     */
    public Unit getUnitMetadata(Peptide peptide) {
        return getUnitMetadata(peptide.getUnitId());
    }

    /**
     * Returns the metadata as a Unit object for the small molecule's unit.
     * Returns null in case no metadata is availbale.
     *
     * @param smallMolecule The small molecule object to get the metadata for.
     * @return A Unit object or null in case no metadata was specified.
     */
    public Unit getUnitMetadata(SmallMolecule smallMolecule) {
        return getUnitMetadata(smallMolecule.getUnitId());
    }

    /**
     * Returns the complete metadata as a list of Unit objects.
     *
     * @return A list of Unit objects.
     */
    public Collection<Unit> getUnitMetadata() {
        return metadata.values();
    }

    /**
     * A returns a collection holding all unit ids in the file.
     *
     * @return A collection of unit ids.
     */
    public Set<String> getUnitIds() {
        Collection<Unit> units = getUnitMetadata();

        HashSet<String> unitIds = new HashSet<String>();
        String identifier;
        for (Unit unit : units) {
            identifier = unit.getIdentifier();
            if (! identifier.contains("sub")) {
                unitIds.add(identifier);
            }
        }

        return unitIds;
    }

    /**
     * Returns all proteins identified by the given accession.
     *
     * @param accession The accession identifying the proteins.
     * @return A collection of proteins identified by the given accession.
     */
    public Collection<Protein> getProtein(String accession) {
        Collection<Protein> result = new ArrayList<Protein>();

        for (Protein record : proteins.values()) {
            if (record.getAccession().equals(accession)) {
                result.add(record);
            }
        }

        return result;
    }

    /**
     * Returns the protein identified by the given accesion in the given unit.
     * Returns null in case the protein does not exist.
     *
     * @param accession The protein's accession.
     * @param unitId The protein's unit's id.
     * @return A Protein object or null in case the protein does not exist.
     */
    public Protein getProtein(String accession, String unitId) {
        for (Protein record : proteins.values()) {
            if (record.getAccession().equals(accession) && record.getUnitId().equals(unitId)) {
                return record;
            }
        }

        return null;
    }

    /**
     * Returns all proteins of a given unit or null in case the unit does not
     * exist.
     *
     * @param unitId The unit's id.
     * @return A Collection of ProteinS identified in this unit or null in case
     * the unit does not exist.
     */
    public Collection<Protein> getUnitProteins(String unitId) {
        Collection<Protein> result = new ArrayList<Protein>();

        for (Protein record : proteins.values()) {
            if (record.getUnitId().equals(unitId)) {
                result.add(record);
            }
        }

        return result;
    }

    /**
     * Returns a Collection holding all proteins identified in this mzTabFile.
     *
     * @return A Collection of ProteinS
     */
    public Collection<Protein> getProteins() {
        return proteins.values();
    }

    /**
     * Returns all peptides identifying the given protein.
     *
     * @param protein The Protein to get the peptides for.
     * @return A Collection of PeptideS or null in case the protein is not
     * referenced in the peptide table.
     */
    public Collection<Peptide> getProteinPeptides(Protein protein) {
        return getProteinPeptides(protein.getAccession());
    }

    /**
     * Returns all peptides identifying the given protein in the given unit.
     *
     * @param accession The protein accession.
     * @param unitId The unit id.
     * @return A list of PeptideS or null in case this protein is not referenced
     * in the peptide table.
     */
    public Collection<Peptide> getProteinPeptides(String accession, String unitId) {
        Collection<Peptide> result = new ArrayList<Peptide>();

        for (Peptide record : peptides.values()) {
            if (record.getAccession().equals(accession) && record.getUnitId().equals(unitId)) {
                result.add(record);
            }
        }

        return result;
    }

    /**
     * Returns all peptides identifying a given protein irrespective of the
     * unit.
     *
     * @param accession The protein's accession.
     * @return A Collection of PeptideS or null in case the accession is not
     * referenced in the peptide table.
     */
    public Collection<Peptide> getProteinPeptides(String accession) {
        Collection<Peptide> result = new ArrayList<Peptide>();

        for (Peptide record : peptides.values()) {
            if (record.getAccession().equals(accession)) {
                result.add(record);
            }
        }

        return result;
    }

    /**
     * Returns all peptides identified in a given unit.
     *
     * @param unitId The unit's id.
     * @return A Collection of PeptideS or null in case the unitId does not
     * contain any peptides.
     */
    public Collection<Peptide> getPeptides(String unitId) {
        Collection<Peptide> result = new ArrayList<Peptide>();

        for (Peptide record : peptides.values()) {
            if (record.getUnitId().equals(unitId)) {
                result.add(record);
            }
        }

        return result;
    }

    /**
     * Returns a Collection holding all peptides found in the mzTab file.
     *
     * @return A Collection of peptides.
     */
    public Collection<Peptide> getPeptides() {
        return peptides.values();
    }

    /**
     * Returns all peptides with the given sequence.
     *
     * @param sequence The amino acid sequence to get the peptides for.
     * @return A Collection of PeptideS or null in case the sequence is not
     * present in the peptide table.
     */
    public Collection<Peptide> getPeptidesForSequence(String sequence) {
        Collection<Peptide> result = new ArrayList<Peptide>();

        for (Peptide record : peptides.values()) {
            if (record.getSequence().equals(sequence)) {
                result.add(record);
            }
        }

        return result;
    }

    /**
     * Returns all SmallMoleculeS identified in the mzTab file.
     *
     * @return A Collection of SmallMoleculeS
     */
    public Collection<SmallMolecule> getSmallMolecules() {
        return smallMolecules.values();
    }

    /**
     * Returns all small molecules for the given unit.
     *
     * @param unitId The unit'd id.
     * @return A Collection of SmallMolecules or null in case there are not
     * small molcules identified for the passed unit.
     */
    public Collection<SmallMolecule> getSmallMolecules(String unitId) {
        Collection<SmallMolecule> result = new ArrayList<SmallMolecule>();

        for (SmallMolecule record : smallMolecules.values()) {
            if (record.getUnitId().equals(unitId)) {
                result.add(record);
            }
        }

        return result;
    }

    /**
     * Returns all small molecules having the given identifier.
     *
     * @param identifier The identifier to get the small molecules for.
     * @return A Collection of SmallMoleculeS or null in case the identifier is
     * not used in the SmallMolecule section.
     */
    public Collection<SmallMolecule> getSmallMoleculesForIdentifier(String identifier) {
        Collection<SmallMolecule> result = new ArrayList<SmallMolecule>();

        for (SmallMolecule record : smallMolecules.values()) {
            if (record.getIdentifier().toString().equals(identifier)) {
                result.add(record);
            }
        }

        return result;
    }

    public void print(OutputStream out) throws IOException {
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

}
