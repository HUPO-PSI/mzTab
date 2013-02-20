package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

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
public class MZTabColumnFactory {
    /**
     * maintain the position and MZTabColumn ordered pairs. Notice: the position start with 1.
     */
    private TreeMap<Integer, MZTabColumn> columnMapping  = new TreeMap<Integer, MZTabColumn>();

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
                    factory.columnMapping.put(column.getPosition(), column);
                }
                break;
            case Protein_Header:
                for (ProteinColumn column : ProteinColumn.values()) {
                    factory.columnMapping.put(column.getPosition(), column);
                }
                break;
            case Small_Molecule_Header:
                for (SmallMoleculeColumn column : SmallMoleculeColumn.values()) {
                    factory.columnMapping.put(column.getPosition(), column);
                }
                break;
            default:
                throw new IllegalArgumentException("Section should be Protein, Peptide or " +
                        "Small_Molecule. Others can not setting. ");

        }

        factory.section = section;

        return factory;
    }

    /**
     * Add three optional abundance columns (_abundance_, _abundance_stdev_, _abundance_std_error_) to the
     * rightest of the table.
     * @see AbundanceColumn
     */
    public void addAbundanceColumn(SubUnit subUnit) {
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

        TreeMap<Integer, AbundanceColumn> abundanceColumnList = AbundanceColumn.getInstance(dataSection, offset, subUnit);
        columnMapping.putAll(abundanceColumnList);
    }

    /**
     * Add a Optional Column {opt_} to the rightest of the table.
     * @see OptionalColumn
     */
    public void addOptionColumn(String name, Class dataType) {
        int offset = columnMapping.lastKey();
        OptionalColumn column = OptionalColumn.getInstance(name, dataType, offset);
        columnMapping.put(column.getPosition(), column);
    }

    /**
     * Add a CVParam Optional Column {opt_cv_{accession}_{parameter name}} to the rightest of the table.
     * @see CVParamOptionColumn
     */
    public void addCVParamOptionColumn(CVParam param) {
        int offset = columnMapping.lastKey();
        CVParamOptionColumn column = CVParamOptionColumn.getInstance(param, offset);
        columnMapping.put(column.getPosition(), column);
    }


    /**
     * @return tab split column header string list.
     */
    public SplitList<String> getHeaderList() {
        SplitList<String> headerList = new SplitList<String>(MZTabConstants.TAB);

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
     * [PRH|PEH/SMH]    header1 header2 ...
     */
    @Override
    public String toString() {
        return section.getPrefix() + MZTabConstants.TAB + getHeaderList().toString();
    }
}
