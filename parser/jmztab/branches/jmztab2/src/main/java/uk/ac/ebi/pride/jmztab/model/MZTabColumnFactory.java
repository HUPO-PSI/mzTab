package uk.ac.ebi.pride.jmztab.model;

import java.util.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;

/**
 * This is a static factory class which used to generate a couple of MZTabColumn, and organized them
 * <position, MZTabColumn> pair.
 *
 * Currently, mzTab table including two parts:
 * <ol>
 *     <li>
 *         stable columns: based on protein, peptide, and small molecular, there are a couple of mandatory column
 *         and stable ordered columns.
 *     </li>
 *     <li>
 *         optional columns: these columns MAY be present in the table. These columns will add to the last column of
 *         table, which position based on the stable columns scale.
 *         @see AbundanceColumn
 *         @see OptionalColumn
 *         @see CVParamOptionColumn
 *     </li>
 * </ol>
 *
 * User: Qingwei
 * Date: 04/02/13
 */
public class MZTabColumnFactory extends OperationCenter {
    /**
     * maintain the position and MZTabColumn ordered pairs. Notice: the position start with 1.
     */
    private TreeMap<Integer, MZTabColumn> stableColumnMapping = new TreeMap<Integer, MZTabColumn>();

    private TreeMap<Integer, OptionalColumn> optionalColumnMapping = new TreeMap<Integer, OptionalColumn>();

    private TreeMap<Integer, AbundanceColumn> abundanceColumnMapping = new TreeMap<Integer, AbundanceColumn>();

    private TreeMap<Integer, MZTabColumn> columnMapping = new TreeMap<Integer, MZTabColumn>();

    /**
     * There are three type of table: protein, peptide or small molecular.
     */
    private Section section;

    private MZTabColumnFactory() {}

    /**
     * Based on section to generate stable columns. Notice that, section should be Protein_Header,
     * Peptide_Header or Small_Molecule_Header. Comment/Metadata Must Not setting here.
     *
     */
    public static MZTabColumnFactory getInstance(Section section) {
        if (section.isData()) {
            switch (section) {
                case Peptide:
                    section = Section.Peptide_Header;
                    break;
                case Protein:
                    section = Section.Protein_Header;
                    break;
                case Small_Molecule:
                    section = Section.Small_Molecule_Header;
                    break;
            }
        }

        if (! section.isHeader()) {
            throw new IllegalArgumentException("Section should use Protein_Header, Peptide_Header or Small_Molecule_Header.");
        }

        MZTabColumnFactory factory = new MZTabColumnFactory();

        switch (section) {
            case Peptide_Header:
                for (PeptideColumn column : PeptideColumn.values()) {
                    factory.stableColumnMapping.put(column.getPosition(), column);
                }
                break;
            case Protein_Header:
                for (ProteinColumn column : ProteinColumn.values()) {
                    factory.stableColumnMapping.put(column.getPosition(), column);
                }
                break;
            case Small_Molecule_Header:
                for (SmallMoleculeColumn column : SmallMoleculeColumn.values()) {
                    factory.stableColumnMapping.put(column.getPosition(), column);
                }
                break;
            default:
                throw new IllegalArgumentException("Section should be Protein, Peptide or " +
                        "Small_Molecule. Others can not setting. ");

        }
        factory.columnMapping.putAll(factory.stableColumnMapping);

        factory.section = section;

        return factory;
    }

    /**
     * Add three optional abundance columns (_abundance_, _abundance_stdev_, _abundance_std_error_) to the
     * rightest of the table.
     * @see AbundanceColumn
     */
    public void addAbundanceColumns(SubUnit subUnit) {
        int offset = columnMapping.lastKey();

        Section dataSection;
        switch (section) {
            case Protein_Header:
                dataSection = Section.Protein;
                break;
            case Peptide_Header:
                dataSection = Section.Peptide;
                break;
            case Small_Molecule_Header:
                dataSection = Section.Small_Molecule;
                break;
            default:
                dataSection = null;
        }

        TreeMap<Integer, AbundanceColumn> abundanceColumnMap = AbundanceColumn.getInstance(dataSection, offset, subUnit);
        for (AbundanceColumn column : abundanceColumnMap.values()) {
            addAbundanceColumn(column);
        }
    }

    public void addAbundanceColumn(AbundanceColumn column) {
        abundanceColumnMapping.put(column.getPosition(), column);
        columnMapping.put(column.getPosition(), column);
    }

    public void addAllAbundanceColumn(Collection<AbundanceColumn> columns) {
        for (AbundanceColumn column : columns) {
            addAbundanceColumn(column);
        }
    }

    /**
     * Add a Optional Column {opt_} to the rightest of the table.
     * @see OptionalColumn
     */
    public Integer addOptionalColumn(String name, Class dataType) {
        int offset = columnMapping.lastKey();
        OptionalColumn column = OptionalColumn.getInstance(name, dataType, offset);
        addOptionalColumn(column);
        return column.getPosition();
    }

    /**
     * Add a CVParam Optional Column {opt_cv_{accession}_{parameter name}} to the rightest of the table.
     * @see CVParamOptionColumn
     */
    public Integer addCVParamOptionalColumn(CVParam param) {
        int offset = columnMapping.lastKey();
        CVParamOptionColumn column = CVParamOptionColumn.getInstance(param, offset);
        addOptionalColumn(column);
        return column.getPosition();
    }

    public Integer addOptionalColumn(OptionalColumn column) {
        stableColumnMapping.put(column.getPosition(), column);
        columnMapping.put(column.getPosition(), column);
        return column.getPosition();
    }

    public void addAllOptionalColumn(Collection<OptionalColumn> columns) {
        for (OptionalColumn column : columns) {
            addOptionalColumn(column);
        }
    }

    /**
     * Move optional and abundance column and data to new position.
     * Notice: alter move, maybe exists some column, the data become empty. Need fill "null".
     *
     * @see uk.ac.ebi.pride.jmztab.model.MZTabFile#fillNull()
     *
     * MZTabColumnFactory fire <-----listen-----Protein, Peptide, SmallMolecule
     * @see Protein#propertyChange(java.beans.PropertyChangeEvent) ;
     * @see Peptide#propertyChange(java.beans.PropertyChangeEvent) ;
     * @see SmallMolecule#propertyChange(java.beans.PropertyChangeEvent) ;
     *
     * In MZTabFile register listener.
     * @see MZTabFile#addPeptide(Peptide)
     * @see MZTabFile#addProtein(Protein)
     * @see MZTabFile#addSmallMolecule(SmallMolecule)
     */
    public void modifyColumnPosition(int oldPosition, int newPosition) {
        if (oldPosition <= stableColumnMapping.lastKey()) {
            throw new IllegalArgumentException("The column in position " + oldPosition + " is not optional column.");
        }

        if (columnMapping.containsKey(newPosition)) {
            throw new IllegalArgumentException("The new position " + newPosition + " has exists a column, can not overwrite.");
        }

        MZTabColumn column = columnMapping.get(oldPosition);
        if (column == null) {
            throw new IllegalArgumentException("The column in position " + oldPosition + " is not exists.");
        }

        if (column instanceof AbundanceColumn) {
            AbundanceColumn abundanceColumn = (AbundanceColumn) column;
            abundanceColumn.setPosition(newPosition);
            abundanceColumnMapping.remove(oldPosition);
            columnMapping.remove(oldPosition);
            abundanceColumnMapping.put(newPosition, abundanceColumn);
            columnMapping.put(newPosition, abundanceColumn);
        } else if (column instanceof OptionalColumn) {
            OptionalColumn optionalColumn = (OptionalColumn) column;
            optionalColumn.setPosition(newPosition);
            optionalColumnMapping.remove(oldPosition);
            columnMapping.remove(oldPosition);
            optionalColumnMapping.put(newPosition, optionalColumn);
            columnMapping.put(newPosition, optionalColumn);
        }

        firePropertyChange(OperationCenter.POSITION, oldPosition, newPosition);
    }

    /**
     * @return tab split column header string list.
     */
    public SplitList<String> getHeaderList() {
        SplitList<String> headerList = new SplitList<String>(TAB);

        for (MZTabColumn mzTabColumn : columnMapping.values()) {
            headerList.add(mzTabColumn.getHeader());
        }

        return headerList;
    }

    public MZTabColumn getColumn(Integer position) {
        return columnMapping.get(position);
    }

    public MZTabColumn getColumn(String header) {
        if (header == null) {
            return null;
        }

        for (MZTabColumn column : columnMapping.values()) {
            if (column.getHeader().equals(header)) {
                return column;
            }
        }
        return null;
    }

    /**
     * @return a readonly sorted <Position, MZTabColumn> map.
     */
    public SortedMap<Integer, MZTabColumn> getColumnMapping() {
        return Collections.unmodifiableSortedMap(columnMapping);
    }

    /**
     * @return a readonly sorted <Position, MZTabColumn> map.
     */
    public SortedMap<Integer, MZTabColumn> getStableColumnMapping() {
        return Collections.unmodifiableSortedMap(stableColumnMapping);
    }

    /**
     * @return a readonly sorted <Position, MZTabColumn> map.
     */
    public SortedMap<Integer, AbundanceColumn> getAbundanceColumnMapping() {
        return Collections.unmodifiableSortedMap(abundanceColumnMapping);
    }

    /**
     * @return a readonly sorted <Position, MZTabColumn> map.
     */
    public SortedMap<Integer, OptionalColumn> getOptionalColumnMapping() {
        return Collections.unmodifiableSortedMap(optionalColumnMapping);
    }

    public Integer containOptionalColumn(String name) {
        if (isEmpty(name)) {
            return null;
        }

        for (Integer position : optionalColumnMapping.keySet()) {
            if (optionalColumnMapping.get(position).getName().equals(name)) {
                return position;
            }
        }

        return null;
    }

    public Section getSection() {
        return section;
    }

    /**
     * [PRH|PEH/SMH]    header1 header2 ...
     */
    @Override
    public String toString() {
        return section.getPrefix() + TAB + getHeaderList().toString();
    }
}
