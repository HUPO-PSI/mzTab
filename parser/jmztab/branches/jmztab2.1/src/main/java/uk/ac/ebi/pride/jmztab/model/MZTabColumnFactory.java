package uk.ac.ebi.pride.jmztab.model;

import java.util.SortedMap;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;

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
 *         @see OptionColumn
 *         @see CVParamOptionColumn
 *     </li>
 * </ol>
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class MZTabColumnFactory {
    /**
     * maintain the position and MZTabColumn ordered pairs. Notice: the position start with 1.
     */
    private SortedMap<String, MZTabColumn> stableColumnMapping = new TreeMap<String, MZTabColumn>();

    private SortedMap<String, MZTabColumn> optionalColumnMapping = new TreeMap<String, MZTabColumn>();

    private SortedMap<String, MZTabColumn> columnMapping = new TreeMap<String, MZTabColumn>();

    /**
     * There are three type of table: protein, peptide or small molecular.
     */
    private Section section;

    private MZTabColumnFactory() {}

    private static void addStableColumn(MZTabColumnFactory factory, MZTabColumn column) {
        factory.stableColumnMapping.put(column.getOrder(), column);
    }

    /**
     * Based on section to generate stable columns. Notice that, section should be Protein_Header,
     * Peptide_Header or Small_Molecule_Header. Comment/Metadata Must Not setting here.
     *
     */
    public static MZTabColumnFactory getInstance(Section section) {
        section = Section.toHeaderSection(section);

        if (section == null) {
            throw new IllegalArgumentException("Section should use Protein_Header, Peptide_Header, PSM_Header or Small_Molecule_Header.");
        }

        MZTabColumnFactory factory = new MZTabColumnFactory();

        switch (section) {
            case Protein_Header:
                addStableColumn(factory, ProteinColumn.ACCESSION);
                addStableColumn(factory, ProteinColumn.DESCRIPTION);
                addStableColumn(factory, ProteinColumn.TAXID);
                addStableColumn(factory, ProteinColumn.SPECIES);
                addStableColumn(factory, ProteinColumn.DATABASE);
                addStableColumn(factory, ProteinColumn.DATABASE_VERSION);
                addStableColumn(factory, ProteinColumn.SEARCH_ENGINE);
                addStableColumn(factory, ProteinColumn.BEST_SEARCH_ENGINE_SCORE);
                addStableColumn(factory, ProteinColumn.AMBIGUITY_MEMBERS);
                addStableColumn(factory, ProteinColumn.MODIFICATIONS);
                addStableColumn(factory, ProteinColumn.PROTEIN_COVERAGE);
                break;
            case Peptide_Header:
                addStableColumn(factory, PeptideColumn.SEQUENCE);
                addStableColumn(factory, PeptideColumn.ACCESSION);
                addStableColumn(factory, PeptideColumn.UNIQUE);
                addStableColumn(factory, PeptideColumn.DATABASE);
                addStableColumn(factory, PeptideColumn.DATABASE_VERSION);
                addStableColumn(factory, PeptideColumn.SEARCH_ENGINE);
                addStableColumn(factory, PeptideColumn.BEST_SEARCH_ENGINE_SCORE);
                addStableColumn(factory, PeptideColumn.MODIFICATIONS);
                addStableColumn(factory, PeptideColumn.RETENTION_TIME);
                addStableColumn(factory, PeptideColumn.RETENTION_TIME_WINDOW);
                addStableColumn(factory, PeptideColumn.CHARGE);
                addStableColumn(factory, PeptideColumn.MASS_TO_CHARGE);
                addStableColumn(factory, PeptideColumn.SPECTRA_REF);
                break;
            case PSM_Header:
                addStableColumn(factory, PSMColumn.SEQUENCE);
                addStableColumn(factory, PSMColumn.PSM_ID);
                addStableColumn(factory, PSMColumn.ACCESSION);
                addStableColumn(factory, PSMColumn.UNIQUE);
                addStableColumn(factory, PSMColumn.DATABASE);
                addStableColumn(factory, PSMColumn.DATABASE_VERSION);
                addStableColumn(factory, PSMColumn.SEARCH_ENGINE);
                addStableColumn(factory, PSMColumn.SEARCH_ENGINE_SCORE);
                addStableColumn(factory, PSMColumn.MODIFICATIONS);
                addStableColumn(factory, PSMColumn.RETENTION_TIME);
                addStableColumn(factory, PSMColumn.CHARGE);
                addStableColumn(factory, PSMColumn.EXP_MASS_TO_CHARGE);
                addStableColumn(factory, PSMColumn.CALC_MASS_TO_CHARGE);
                addStableColumn(factory, PSMColumn.SPECTRA_REF);
                addStableColumn(factory, PSMColumn.PRE);
                addStableColumn(factory, PSMColumn.POST);
                addStableColumn(factory, PSMColumn.START);
                addStableColumn(factory, PSMColumn.END);
                break;
            case Small_Molecule_Header:
                addStableColumn(factory, SmallMoleculeColumn.IDENTIFIER);
                addStableColumn(factory, SmallMoleculeColumn.CHEMICAL_FORMULA);
                addStableColumn(factory, SmallMoleculeColumn.SMILES);
                addStableColumn(factory, SmallMoleculeColumn.INCHI_KEY);
                addStableColumn(factory, SmallMoleculeColumn.DESCRIPTION);
                addStableColumn(factory, SmallMoleculeColumn.EXP_MASS_TO_CHARGE);
                addStableColumn(factory, SmallMoleculeColumn.CALC_MASS_TO_CHARGE);
                addStableColumn(factory, SmallMoleculeColumn.CHARGE);
                addStableColumn(factory, SmallMoleculeColumn.RETENTION_TIME);
                addStableColumn(factory, SmallMoleculeColumn.TAXID);
                addStableColumn(factory, SmallMoleculeColumn.SPECIES);
                addStableColumn(factory, SmallMoleculeColumn.DATABASE);
                addStableColumn(factory, SmallMoleculeColumn.DATABASE_VERSION);
                addStableColumn(factory, SmallMoleculeColumn.SPECTRA_REF);
                addStableColumn(factory, SmallMoleculeColumn.SEARCH_ENGINE);
                addStableColumn(factory, SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE);
                addStableColumn(factory, SmallMoleculeColumn.MODIFICATIONS);
                break;
            default:
                throw new IllegalArgumentException("Section should be Protein, Peptide or " +
                        "Small_Molecule. Others can not setting. ");

        }

        factory.columnMapping.putAll(factory.stableColumnMapping);
        factory.section = section;

        return factory;
    }

    public Section getSection() {
        return section;
    }

    public SortedMap<String, MZTabColumn> getStableColumnMapping() {
        return stableColumnMapping;
    }

    public SortedMap<String, MZTabColumn> getOptionalColumnMapping() {
        return optionalColumnMapping;
    }

    public SortedMap<String, MZTabColumn> getColumnMapping() {
        return columnMapping;
    }

    public void addOptionalColumn(MZTabColumn column, MsRun msRun) {
        String position = column.getLogicPosition();
        if (optionalColumnMapping.containsKey(position)) {
            throw new IllegalArgumentException("There exists column " + optionalColumnMapping.get(position) + " in position " + position);
        }

        MZTabColumn newColumn = null;
        switch (section) {
            case Protein_Header:
                if (position.equals("09") || position.equals("11") || position.equals("12") || position.equals("13")) {
                    newColumn = MZTabColumn.createOptionalColumn(section, column, msRun);
                }
                break;
            case Peptide_Header:
                if (position.equals("08")) {
                    newColumn = MZTabColumn.createOptionalColumn(section, column, msRun);
                }
                break;
            case Small_Molecule_Header:
                if (position.equals("19")) {
                    newColumn = MZTabColumn.createOptionalColumn(section, column, msRun);
                }
                break;
        }

        if (newColumn != null) {
            optionalColumnMapping.put(newColumn.getLogicPosition(), newColumn);
            columnMapping.put(newColumn.getLogicPosition(), newColumn);
        }
    }

    public static int getColumnOrder(String position) {
        return new Integer(position.substring(0, 2));
    }

    public static int getColumnId(String position) {
        return position.length() == 2 ? 0 : new Integer(position.substring(2));
    }

    public void addGoTermsOptionalColumn() {
        if (section != Section.Protein_Header) {
            throw new IllegalArgumentException("go_terms optional column only add into the protein section.");
        }

        MZTabColumn column = ProteinColumn.GO_TERMS;
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addReliabilityOptionalColumn() {
        MZTabColumn column = null;
        switch (section) {
            case Protein_Header:
                column = ProteinColumn.RELIABILITY;
                break;
            case Peptide_Header:
                column = PeptideColumn.RELIABILITY;
                break;
            case Small_Molecule_Header:
                column = SmallMoleculeColumn.RELIABILITY;
                break;
            case PSM_Header:
                column = PSMColumn.RELIABILITY;
                break;
        }

        if (column != null) {
            optionalColumnMapping.put(column.getLogicPosition(), column);
            columnMapping.put(column.getLogicPosition(), column);
        }
    }

    public void addURIOptionalColumn() {
        MZTabColumn column = null;
        switch (section) {
            case Protein_Header:
                column = ProteinColumn.URI;
                break;
            case Peptide_Header:
                column = PeptideColumn.URI;
                break;
            case Small_Molecule_Header:
                column = SmallMoleculeColumn.URI;
                break;
            case PSM_Header:
                column = PSMColumn.URI;
                break;
        }

        if (column != null) {
            optionalColumnMapping.put(column.getLogicPosition(), column);
            columnMapping.put(column.getLogicPosition(), column);
        }
    }

    public void addOptionalColumn(String name, Class columnType) {
        MZTabColumn column = new OptionColumn(null, name, columnType, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addOptionalColumn(Assay assay, String name, Class columnType) {
        MZTabColumn column = new OptionColumn(assay, name, columnType, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addOptionalColumn(StudyVariable studyVariable, String name, Class columnType) {
        MZTabColumn column = new OptionColumn(studyVariable, name, columnType, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addOptionalColumn(MsRun msRun, String name, Class columnType) {
        MZTabColumn column = new OptionColumn(msRun, name, columnType, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addOptionalColumn(CVParam param, Class columnType) {
        MZTabColumn column = new CVParamOptionColumn(null, param, columnType, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addOptionalColumn(Assay assay, CVParam param, Class columnType) {
        MZTabColumn column = new CVParamOptionColumn(assay, param, columnType, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addOptionalColumn(StudyVariable studyVariable, CVParam param, Class columnType) {
        MZTabColumn column = new CVParamOptionColumn(studyVariable, param, columnType, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addOptionalColumn(MsRun msRun, CVParam param, Class columnType) {
        MZTabColumn column = new CVParamOptionColumn(msRun, param, columnType, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addAbundanceOptionalColumn(Assay assay) {
        MZTabColumn column = AbundanceColumn.createOptionalColumn(section, assay, getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.put(column.getLogicPosition(), column);
        columnMapping.put(column.getLogicPosition(), column);
    }

    public void addAbundanceOptionalColumn(StudyVariable studyVariable) {
        SortedMap<String, MZTabColumn> columns = AbundanceColumn.createOptionalColumns(section, studyVariable,  getColumnOrder(columnMapping.lastKey()));
        optionalColumnMapping.putAll(columns);
        columnMapping.putAll(columns);
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

    /**
     * [PRH|PEH/SMH]    header1 header2 ...
     */
    @Override
    public String toString() {
        return section.getPrefix() + TAB + getHeaderList().toString();
    }

    /**
     * @return map(offset, MZTabColumn), the offset record the position of MZTabColumn in header line.
     */
    public SortedMap<Integer, MZTabColumn> getOffsetColumnsMap() {
        SortedMap<Integer, MZTabColumn> map = new TreeMap<Integer, MZTabColumn>();

        int offset = 1;
        for (MZTabColumn column : columnMapping.values()) {
            map.put(offset++, column);
        }

        return map;
    }

    public boolean isOptionalColumn(String header) {
        header = header.trim().toLowerCase();

        switch (section) {
            case Protein_Header:
                if (header.startsWith(ProteinColumn.SEARCH_ENGINE_SCORE.getName())   ||
                    header.startsWith(ProteinColumn.NUM_PSMS.getName())              ||
                    header.startsWith(ProteinColumn.NUM_PEPTIDES_DISTINCT.getName()) ||
                    header.startsWith(ProteinColumn.NUM_PEPTIDES_UNIQUE.getName())   ||
                    header.startsWith("protein_abundance_assay")                     ||
                    header.startsWith("protein_abundance_study_variable")            ||
                    header.startsWith("protein_abundance_stdev_study_variable")      ||
                    header.startsWith("protein_abundance_std_error_study_variable")) {
                    return true;
                }
                break;
            case Peptide_Header:
                if (header.startsWith(PeptideColumn.SEARCH_ENGINE_SCORE.getName())   ||
                    header.startsWith("peptide_abundance_assay")                     ||
                    header.startsWith("peptide_abundance_study_variable")            ||
                    header.startsWith("peptide_abundance_stdev_study_variable")      ||
                    header.startsWith("peptide_abundance_std_error_study_variable")) {
                    return true;
                }
                break;
            case Small_Molecule_Header:
                if (header.startsWith(SmallMoleculeColumn.SEARCH_ENGINE_SCORE.getName())   ||
                    header.startsWith("smallmolecule_abundance_assay")                     ||
                    header.startsWith("smallmolecule_abundance_study_variable")            ||
                    header.startsWith("smallmolecule_abundance_stdev_study_variable")      ||
                    header.startsWith("smallmolecule_abundance_std_error_study_variable")) {
                    return true;
                }
                break;
        }

        return header.startsWith("opt_");

    }

    /**
     * Based on header name to query the MZTabColumn.
     * Notice: for optional columns, header name maybe flexible. For example,
     * num_peptides_distinct_ms_file[1].
     * At this time, user SHOULD BE provide the concrete header name to query
     * MZTabColumn. If just provide num_peptides_distinct, return null.
     *
     * @see
     */
    public MZTabColumn findColumn(String header) {
        header = header.trim();

        for (MZTabColumn column : columnMapping.values()) {
            if (header.equals(column.getHeader())) {
                return column;
            }
        }

        return null;
    }
}
