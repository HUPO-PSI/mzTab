package uk.ac.ebi.pride.jmztab.model;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * If the data exporter wishes to report only final results for 'Summary' files (i.e. following averaging over replicates),
 * then these MUST be reported as quantitative values in the columns associated with the study_variable[1-n] (e.g.
 * protein_abundance_study_variable[1]). mzTab allows the reporting of abundance, standard deviation, and standard error
 * for any study_variable. The unit of values in the abundance column MUST be specified in the metadata section of the mzTab file.
 * The reported values SHOULD represent the final result of the performed data analysis. The exact meaning of the values will
 * thus depend on the used analysis pipeline and quantitation method and is not expected to be comparable across multiple mzTab files.
 *
 * @author qingwei
 * @since 23/05/13
 */
public class AbundanceColumn extends MZTabColumn {
    public enum Field {
        ABUNDANCE          ("abundance",              Double.class,    1),
        ABUNDANCE_STDEV    ("abundance_stdev",        Double.class,    2),
        ABUNDANCE_STD_ERROR("abundance_std_error",    Double.class,    3);

        private String name;
        private Class columnType;
        private int position;

        Field(String name, Class columnType, int position) {
            this.name = name;
            this.columnType = columnType;
            this.position = position;
        }

        public String toString() {
            return name;
        }
    }

    /**
     * This is a temporary method, which face small molecule abundance column:
     * translate small_molecule --> smallmolecule
     * @see uk.ac.ebi.pride.jmztab.utils.parser.MZTabHeaderLineParser#translate(String)
     */
    public static String translate(String oldName) {
        if (oldName.equals("small_molecule")) {
            return "smallmolecule";
        } else {
            return oldName;
        }
    }

    /**
     * Generate a abundance column:
     * The column header is: {Section}_{Field#name()}_{IndexedElement[id]}
     * The column data type: {Field#columnType()}
     * The column position: always most right side, calculated by offset.
     */
    private AbundanceColumn(Section section, Field field, IndexedElement element, int offset) {
        super(translate(section.getName()) + "_" + field.name, field.columnType, true, offset + field.position + "");
        setElement(element);
    }

    /**
     * Generate a abundance optional column as measured in the given assay.The column header like
     * protein_abundance_assay[1-n], the position always stay the most right of the tabled section,
     * and the data type is Double.
     *
     * @param section SHOULD be {@link Section#Protein}, {@link Section#Peptide}, {@link Section#PSM},
     *                or {@link Section#Small_Molecule}.
     * @param assay SHOULD not be null.
     * @param offset Normally the last column's position in header, {@link MZTabColumnFactory#getColumnMapping()},
     * @return an abundance optional column as measured in the given assay.
     */
    public static MZTabColumn createOptionalColumn(Section section, Assay assay, int offset) {
        if (section.isComment() || section.isMetadata()) {
            throw new IllegalArgumentException("Section should be Protein, Peptide, PSM or SmallMolecule.");
        }
        if (assay == null) {
            throw new NullPointerException("Assay should not be null!");
        }

        return new AbundanceColumn(Section.toDataSection(section), Field.ABUNDANCE, assay, offset);
    }

    /**
     * Generate three abundance optional columns as measured in the given study variable.
     * The columns header like protein_abundance_study_variable[1-n], protein_abundance_stdev_study_variable[1-n]
     * and protein_abundance_std_error_study_variable[1-n].
     * The position always stay the most right of the tabled section, and the data type is Double.
     *
     * @param section SHOULD be {@link Section#Protein}, {@link Section#Peptide}, {@link Section#PSM},
     *                or {@link Section#Small_Molecule}.
     * @param studyVariable SHOULD not be null.
     * @param order Normally the last column's position in header, {@link MZTabColumnFactory#getColumnMapping()},
     * @return an abundance optional column as measured in the given study variable.
     */
    public static SortedMap<String, MZTabColumn> createOptionalColumns(Section section, StudyVariable studyVariable, String order) {
        if (section.isComment() || section.isMetadata()) {
            throw new IllegalArgumentException("Section should be Protein, Peptide, PSM or SmallMolecule.");
        }
        if (studyVariable == null) {
            throw new NullPointerException("Study Variable should not be null!");
        }

        int offset = new Integer(order);

        SortedMap<String, MZTabColumn> columns = new TreeMap<String, MZTabColumn>();
        Section dataSection = Section.toDataSection(section);

        AbundanceColumn column;
        column = new AbundanceColumn(dataSection, Field.ABUNDANCE, studyVariable, offset);
        columns.put(column.getLogicPosition(), column);
        column = new AbundanceColumn(dataSection, Field.ABUNDANCE_STDEV, studyVariable, offset);
        columns.put(column.getLogicPosition(), column);
        column = new AbundanceColumn(dataSection, Field.ABUNDANCE_STD_ERROR, studyVariable, offset);
        columns.put(column.getLogicPosition(), column);

        return columns;
    }
}
