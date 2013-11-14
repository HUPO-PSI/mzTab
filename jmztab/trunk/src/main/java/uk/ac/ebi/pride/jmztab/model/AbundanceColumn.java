package uk.ac.ebi.pride.jmztab.model;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Quantitative technologies generally result in some kind of abundance measurement of the identified analyze.
 * Several of the available techniques, furthermore, allow/require multiple similar samples to be multiplexed
 * and analyzed in a single MS run – for example in label-based techniques, such as SILAC/N15 where
 * quantification occurs on MS1 data or in tag-based techniques, such as iTRAQ/TMT where quantification occurs
 * in MS2 data.
 *
 * NOTICE: colunit columns MUST NOT be used to define a unit for quantification columns.
 * In mzTab package, these quantification columns are AbundanceColumn.
 *
 * @see ColUnit
 *
 * User: Qingwei
 * Date: 23/05/13
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

    private AbundanceColumn(Section section, Field field, IndexedElement element, int offset) {
        super(translate(section.getName()) + "_" + field.name, field.columnType, true, offset + field.position + "");
        setElement(element);
    }

    public static MZTabColumn createOptionalColumn(Section section, Assay assay, int offset) {
        return new AbundanceColumn(Section.toDataSection(section), Field.ABUNDANCE, assay, offset);
    }

    public static SortedMap<String, MZTabColumn> createOptionalColumns(Section section, StudyVariable studyVariable, int offset) {
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
