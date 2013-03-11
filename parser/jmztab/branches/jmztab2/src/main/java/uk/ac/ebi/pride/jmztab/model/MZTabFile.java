package uk.ac.ebi.pride.jmztab.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

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
    private SortedMap<Integer, Comment> comments = new TreeMap<Integer, Comment>();
    private SortedMap<Integer, Protein> proteins;
    private SortedMap<Integer, Peptide> peptides;
    private SortedMap<Integer, SmallMolecule> smallMolecules;

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

    public MZTabColumnFactory getSmallMoleculeColumnFactory() {
        return smallMoleculeColumnFactory;
    }

    public void setProteinColumnFactory(MZTabColumnFactory proteinColumnFactory) {
        if (proteinColumnFactory == null) {
            this.proteinColumnFactory = null;
            this.proteins = null;
            return;
        }

        this.proteinColumnFactory = proteinColumnFactory;
        this.proteins = new TreeMap<Integer, Protein>();

        for (AbundanceColumn column : proteinColumnFactory.getAbundanceColumnMapping().values()) {
            column.getSubUnit().addPropertyChangeListener(OperationCenter.SUB_UNIT_ID, column);
        }
    }

    public void setPeptideColumnFactory(MZTabColumnFactory peptideColumnFactory) {
        if (peptideColumnFactory == null) {
            this.peptideColumnFactory = null;
            this.peptides = null;
            return;
        }

        this.peptideColumnFactory = peptideColumnFactory;
        this.peptides = new TreeMap<Integer, Peptide>();

        for (AbundanceColumn column : peptideColumnFactory.getAbundanceColumnMapping().values()) {
            column.getSubUnit().addPropertyChangeListener(OperationCenter.SUB_UNIT_ID, column);
        }
    }

    public void setSmallMoleculeColumnFactory(MZTabColumnFactory smallMoleculeColumnFactory) {
        if (smallMoleculeColumnFactory == null) {
            this.smallMoleculeColumnFactory = null;
            this.smallMolecules = null;
            return;
        }

        this.smallMoleculeColumnFactory = smallMoleculeColumnFactory;
        this.smallMolecules = new TreeMap<Integer, SmallMolecule>();

        for (AbundanceColumn column : smallMoleculeColumnFactory.getAbundanceColumnMapping().values()) {
            column.getSubUnit().addPropertyChangeListener(OperationCenter.SUB_UNIT_ID, column);
        }
    }

    public void addProtein(Protein protein) {
        if (protein == null) {
            throw new NullPointerException("Protein record is null!");
        }

        Integer lineNumber = this.proteins.isEmpty() ? 1 : this.proteins.lastKey() + 1;
        this.proteins.put(lineNumber, protein);
        this.metadata.addPropertyChangeListener(OperationCenter.UNIT_ID, protein);
        this.proteinColumnFactory.addPropertyChangeListener(OperationCenter.POSITION, protein);
    }

    public void addProtein(Integer lineNumber, Protein protein) {
        if (protein == null) {
            throw new NullPointerException("Protein record is null!");
        }

        if (proteins.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist protein record in line number " + lineNumber);
        }

        this.proteins.put(lineNumber, protein);
        this.metadata.addPropertyChangeListener(OperationCenter.UNIT_ID, protein);
        this.proteinColumnFactory.addPropertyChangeListener(OperationCenter.POSITION, protein);
    }

    public void addPeptide(Peptide peptide) {
        if (peptide == null) {
            throw new NullPointerException("Peptide record is null!");
        }

        Integer position = this.peptides.isEmpty() ? 1 : this.peptides.lastKey() + 1;
        this.peptides.put(position, peptide);
        this.metadata.addPropertyChangeListener(OperationCenter.UNIT_ID, peptide);
        this.peptideColumnFactory.addPropertyChangeListener(OperationCenter.POSITION, peptide);
    }

    public void addPeptide(Integer lineNumber, Peptide peptide) {
        if (peptide == null) {
            throw new NullPointerException("Peptide record is null!");
        }

        if (peptides.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist peptide record in line number " + lineNumber);
        }

        this.peptides.put(lineNumber, peptide);
        this.metadata.addPropertyChangeListener(OperationCenter.UNIT_ID, peptide);
        this.peptideColumnFactory.addPropertyChangeListener(OperationCenter.POSITION, peptide);
    }

    public void addSmallMolecule(SmallMolecule smallMolecule) {
        if (smallMolecule == null) {
            throw new NullPointerException("Small Molecule record is null!");
        }

        Integer position = this.smallMolecules.isEmpty() ? 1 : this.smallMolecules.lastKey() + 1;
        this.smallMolecules.put(position, smallMolecule);
        metadata.addPropertyChangeListener(OperationCenter.UNIT_ID, smallMolecule);
        this.smallMoleculeColumnFactory.addPropertyChangeListener(OperationCenter.POSITION, smallMolecule);
    }

    public void addSmallMolecule(Integer lineNumber, SmallMolecule smallMolecule) {
        if (smallMolecule == null) {
            throw new NullPointerException("Small Molecule record is null!");
        }

        if (smallMolecules.containsKey(lineNumber)) {
            throw new IllegalArgumentException("There already exist small molecule record in line number " + lineNumber);
        }

        this.smallMolecules.put(lineNumber, smallMolecule);
        this.metadata.addPropertyChangeListener(OperationCenter.UNIT_ID, smallMolecule);
        this.smallMoleculeColumnFactory.addPropertyChangeListener(OperationCenter.POSITION, smallMolecule);
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

    public Collection<Unit> getUnits(String unitId) {
        Collection<Unit> units = new ArrayList<Unit>();

        for (Unit unit : getUnits()) {
            if (unit.getUnitId().equals(unitId)) {
                units.add(unit);
            }
        }

        return units;
    }

    /**
     * Returns the metadata for the given unit as a Unit object. Returns null in
     * case the unit does not exist.
     *
     * @param identifier The unit'd id.
     * @return A Unit object or null in case the unit doesn't exist.
     */
    public Unit getUnit(String identifier) {
        return metadata.getUnit(identifier);
    }

    /**
     * Returns the metadata as a Unit object for the protein's unit. Returns
     * null in case no metadata is availbale.
     *
     * @param protein The protein object to get the metadata for.
     * @return A Unit object or null in case no metadata was specified.
     */
    public Unit getUnit(Protein protein) {
        return getUnit(protein.getUnitId());
    }

    /**
     * Returns the metadata as a Unit object for the peptide's unit. Returns
     * null in case no metadata is availbale.
     *
     * @param peptide The peptide object to get the metadata for.
     * @return A Unit object or null in case no metadata was specified.
     */
    public Unit getUnit(Peptide peptide) {
        return getUnit(peptide.getUnitId());
    }

    /**
     * Returns the metadata as a Unit object for the small molecule's unit.
     * Returns null in case no metadata is availbale.
     *
     * @param smallMolecule The small molecule object to get the metadata for.
     * @return A Unit object or null in case no metadata was specified.
     */
    public Unit getUnit(SmallMolecule smallMolecule) {
        return getUnit(smallMolecule.getUnitId());
    }

    /**
     * Returns the complete metadata as a list of Unit objects.
     *
     * @return A list of Unit objects.
     */
    public Collection<Unit> getUnits() {
        return Collections.unmodifiableCollection(metadata.values());
    }

    /**
     * A returns a collection holding all unit ids in the file.
     *
     * @return A collection of unit ids.
     */
    public Set<String> getUnitIds() {
        Collection<Unit> units = getUnits();

        HashSet<String> unitIds = new HashSet<String>();
        for (Unit unit : units) {
            unitIds.add(unit.getUnitId());
        }

        return Collections.unmodifiableSet(unitIds);
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

        return Collections.unmodifiableCollection(result);
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
    public Collection<Protein> getProteins(String unitId) {
        Collection<Protein> result = new ArrayList<Protein>();

        for (Protein record : proteins.values()) {
            if (record.getUnitId().equals(unitId)) {
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

        return Collections.unmodifiableCollection(result);
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

        return Collections.unmodifiableCollection(result);
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

        return Collections.unmodifiableCollection(result);
    }

    /**
     * Returns a Collection holding all peptides found in the mzTab file.
     *
     * @return A Collection of peptides.
     */
    public Collection<Peptide> getPeptides() {
        return Collections.unmodifiableCollection(peptides.values());
    }

    public SortedMap<Integer, Peptide> getPeptidesWithLineNumber() {
        return Collections.unmodifiableSortedMap(peptides);
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

        return Collections.unmodifiableCollection(result);
    }

    /**
     * Returns all SmallMoleculeS identified in the mzTab file.
     *
     * @return A Collection of SmallMoleculeS
     */
    public Collection<SmallMolecule> getSmallMolecules() {
        return Collections.unmodifiableCollection(smallMolecules.values());
    }

    public SortedMap<Integer, SmallMolecule> getSmallMoleculesWithLineNumber() {
        return Collections.unmodifiableSortedMap(smallMolecules);
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

        return Collections.unmodifiableCollection(result);
    }

    /**
     * Modify UnitId in Unit, cascade modify the Protein, Peptide, SmallMolecule data table.
     * Metadata fire <-----listen-----Peptide, Protein, SmallMolecule
     *
     * @see Peptide#propertyChange(java.beans.PropertyChangeEvent)
     * @see Protein#propertyChange(java.beans.PropertyChangeEvent)
     * @see  SmallMolecule#propertyChange(java.beans.PropertyChangeEvent)
     *
     * These methods used to register listener.
     * @see MZTabFile#addPeptide(Peptide)
     * @see MZTabFile#addProtein(Protein)
     * @see MZTabFile#addSmallMolecule(SmallMolecule)
     */
    public void modifyUnitId(String oldUnitId, String newUnitId) {
        metadata.modifyUnitId(oldUnitId, newUnitId);
    }

    /**
     * Move optional and abundance column and data to new position.
     * Notice: alter move, maybe exists some column, the data become empty. Need fill "null".
     *
     * @see uk.ac.ebi.pride.jmztab.model.MZTabFile#fillNull()
     *
     * ProteinColumnFactory fire <-----listen-----Protein
     * @see Protein#propertyChange(java.beans.PropertyChangeEvent) ;
     *
     * In MZTabFile register listener.
     * @see MZTabFile#addProtein(Protein)
     */
    public void modifyProteinColumnPosition(int oldPosition, int newPosition) {
        proteinColumnFactory.modifyColumnPosition(oldPosition, newPosition);
    }

    /**
     * Move optional and abundance column and data to new position.
     * Notice: alter move, maybe exists some column, the data become empty. Need fill "null".
     *
     * @see uk.ac.ebi.pride.jmztab.model.MZTabFile#fillNull()
     *
     * PeptideColumnFactory fire <-----listen-----Peptide
     * @see Peptide#propertyChange(java.beans.PropertyChangeEvent) ;
     *
     * In MZTabFile register listener.
     * @see MZTabFile#addPeptide(Peptide)
     */
    public void modifyPeptideColumnPosition(int oldPosition, int newPosition) {
        peptideColumnFactory.modifyColumnPosition(oldPosition, newPosition);
    }

    /**
     * Move optional and abundance column and data to new position.
     * Notice: alter move, maybe exists some column, the data become empty. Need fill "null".
     *
     * @see uk.ac.ebi.pride.jmztab.model.MZTabFile#fillNull()
     *
     * SmallMoleculeColumnFactory fire <-----listen-----SmallMolecule
     * @see SmallMolecule#propertyChange(java.beans.PropertyChangeEvent) ;
     *
     * In MZTabFile register listener.
     * @see MZTabFile#addSmallMolecule(SmallMolecule)
     */
    public void modifySmallMoleculeColumnPosition(int oldPosition, int newPosition) {
        smallMoleculeColumnFactory.modifyColumnPosition(oldPosition, newPosition);
    }

    public void modifySubUnitId(SubUnit subUnit, int newSubId) {
        subUnit.setSubId(newSubId);
    }

    /**
     * During move optional column and data to a new position, some other data table record not fill null
     * value as default. This method will used to maintain data cell integrity.
     */
    public void fillNull() {
        Object value;
        if (proteinColumnFactory != null) {
            for (Protein protein : proteins.values()) {
                for (Integer position : proteinColumnFactory.getAbundanceColumnMapping().keySet()) {
                    value = protein.getValue(position);
                    protein.addValue(position, value);
                }
                for (Integer position : proteinColumnFactory.getOptionalColumnMapping().keySet()) {
                    value = protein.getValue(position);
                    protein.addValue(position, value);
                }
            }
        }
        if (peptideColumnFactory != null) {
            for (Peptide peptide : peptides.values()) {
                for (Integer position : peptideColumnFactory.getAbundanceColumnMapping().keySet()) {
                    value = peptide.getValue(position);
                    peptide.addValue(position, value);
                }
                for (Integer position : peptideColumnFactory.getOptionalColumnMapping().keySet()) {
                    value = peptide.getValue(position);
                    peptide.addValue(position, value);
                }
            }
        }
        if (smallMoleculeColumnFactory != null) {
            for (SmallMolecule smallMolecule : smallMolecules.values()) {
                for (Integer position : smallMoleculeColumnFactory.getAbundanceColumnMapping().keySet()) {
                    value = smallMolecule.getValue(position);
                    smallMolecule.addValue(position, value);
                }
                for (Integer position : smallMoleculeColumnFactory.getOptionalColumnMapping().keySet()) {
                    value = smallMolecule.getValue(position);
                    smallMolecule.addValue(position, value);
                }
            }
        }
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

        return Collections.unmodifiableCollection(result);
    }

    public void printMZTab(OutputStream out) throws IOException {
        fillNull();

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
