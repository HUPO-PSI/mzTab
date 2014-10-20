package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseParam;

/**
 * A couple of common method used to parse a header line into {@link MZTabColumnFactory} structure.
 * <p/>
 * NOTICE: {@link MZTabColumnFactory} maintain a couple of {@link MZTabColumn} which have internal logical
 * position and order. In physical mzTab file, we allow user not obey this logical position organized way,
 * and provide their date with own order. In order to distinguish them, we use physical position (a positive
 * integer) to record the column location in mzTab file. And use {@link PositionMapping} structure the maintain
 * the mapping between them.
 *
 * @author qingwei
 * @see PRHLineParser
 * @see PEHLineParser
 * @see PSHLineParser
 * @see SMHLineParser
 * @since 11/02/13
 */
public abstract class MZTabHeaderLineParser extends MZTabLineParser {

    protected MZTabColumnFactory factory;
    protected Metadata metadata;

    /**
     * Parse a header line into {@link MZTabColumnFactory} structure.
     *
     * @param factory  SHOULD NOT set null
     * @param metadata SHOULD NOT set null
     */
    protected MZTabHeaderLineParser(MZTabColumnFactory factory, Metadata metadata) {
        if (factory == null) {
            throw new NullPointerException("Header line should be parse first!");
        }
        this.factory = factory;

        if (metadata == null) {
            throw new NullPointerException("Metadata should be create first!");
        }
        this.metadata = metadata;
    }

    /**
     * Parse a header line into {@link MZTabColumnFactory} structure. There are several steps in this method:
     * Step 1: {@link #parseColumns()} focus on validate and parse all columns. Step 2: {@link #checkColUnit()} and
     * Step 3: {@link #refine()}
     */
    public void parse(int lineNumber, String line, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, line, errorList);

        int offset = parseColumns();
        if (offset != items.length) {
            this.errorList.add(new MZTabError(LogicalErrorType.HeaderLine, lineNumber, section.getName(), "" + offset, "" + items.length));
        }

        checkColUnit();
        refine();

    }

    /**
     * This methods delegates to the subclasses the parsing of the columns. All of the columns are defined in the {@link uk.ac.ebi.pride.jmztab.model.ProteinColumn}, {@link uk.ac.ebi.pride.jmztab.model.PeptideColumn}, {@link uk.ac.ebi.pride.jmztab.model.PSMColumn}
     * or {@link uk.ac.ebi.pride.jmztab.model.SmallMoleculeColumn}.
     * @return the next physical index of column available after the parsing.
     * @throws MZTabException
     */
    protected abstract int parseColumns() throws MZTabException;


    /**
     * Some validate operation need to be done after the whole {@link MZTabColumnFactory} created.
     * Thus, user can add them, and called at the end of the
     * {@link #parse(int, String, MZTabErrorList)} method.
     */
    protected abstract void refine() throws MZTabException;


    /**
     * Refine optional columns based one {@link MZTabDescription#mode} and {@link MZTabDescription#type}
     * These re-validate operation will called in {@link #refine()} method.
     */
    protected void refineOptionalColumn(MZTabDescription.Mode mode, MZTabDescription.Type type,
                                        String columnHeader) throws MZTabException {
        if (factory.findColumnByHeader(columnHeader) == null) {
            throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInHeader, lineNumber, columnHeader, mode.toString(), type.toString()));
        }
    }

    protected String fromIndexToOrder(Integer index) {
        return String.format("%02d", index);
    }

    /**
     * Additional columns can be added to the end of the protein table. These column headers MUST start with the prefix "opt_".
     * Column names MUST only contain the following characters: 'A'-'Z', 'a'-'z', '0'-'9', '_', '-', '[', ']', and ':'.
     * <p/>
     * the format: opt_{IndexedElement[id]}_{value}. Spaces within the parameter's name MUST be replaced by '_'.
     */
    protected boolean checkOptColumnName(String nameLabel) throws MZTabException {
        nameLabel = nameLabel.trim();

        String regexp = "opt_((assay|study_variable|ms_run)\\[(\\w+)\\]|global)_([A-Za-z0-9_\\-\\[\\]:\\.]+)";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(nameLabel);

        Integer id;
        String object_id;
        String value;
        MZTabError error;
        if (matcher.find()) {
            object_id = matcher.group(1);
            value = matcher.group(4);

            CVParam param = null;
            if (value.startsWith("cv_")) {
                param = checkCVParamOptColumnName(nameLabel, value);
            }

            Class dataType = getDataType(param);

            if (object_id.contains("global")) {
                if (param == null) {
                    factory.addOptionalColumn(value, dataType);
                } else {
                    factory.addOptionalColumn(param, dataType);
                }
            } else {
                id = parseIndex(nameLabel, matcher.group(3));

                if (object_id.contains("assay")) {
                    Assay element = metadata.getAssayMap().get(id);
                    // not found assay_id in metadata.
                    if (element == null) {
                        error = new MZTabError(LogicalErrorType.AssayNotDefined, lineNumber, nameLabel);
                        throw new MZTabException(error);
                    } else if (param == null) {
                        factory.addOptionalColumn(element, value, dataType);
                    } else {
                        factory.addOptionalColumn(element, param, dataType);
                    }
                } else if (object_id.contains("study_variable")) {
                    StudyVariable element = metadata.getStudyVariableMap().get(id);
                    // not found study_variable_id in metadata.
                    if (element == null) {
                        error = new MZTabError(LogicalErrorType.StudyVariableNotDefined, lineNumber, nameLabel);
                        throw new MZTabException(error);
                    } else if (param == null) {
                        factory.addOptionalColumn(element, value, dataType);
                    } else {
                        factory.addOptionalColumn(element, param, dataType);
                    }
                } else if (object_id.contains("ms_run")) {
                    // not found ms_run_id in metadata.
                    MsRun element = metadata.getMsRunMap().get(id);
                    if (element == null) {
                        error = new MZTabError(LogicalErrorType.MsRunNotDefined, lineNumber, nameLabel);
                        throw new MZTabException(error);
                    } else if (param == null) {
                        factory.addOptionalColumn(element, value, dataType);
                    } else {
                        factory.addOptionalColumn(element, param, dataType);
                    }
                }
            }

            return true;
        } else {
            throw new MZTabException(new MZTabError(FormatErrorType.OptionalCVParamColumn, lineNumber, nameLabel));
        }
    }

    /**
     * An kind of {@link CVParamOptionColumn} which use CV parameter accessions in following the format:
     * opt_{OBJECT_ID}_cv_{accession}_{parameter name}. Spaces within the parameter' s name MUST be replaced by '_'.
     */
    private CVParam checkCVParamOptColumnName(String nameLabel, String valueLabel) throws MZTabException {
        nameLabel = nameLabel.trim();
        valueLabel = valueLabel.trim();

        String regexp = "cv(_([A-Za-z0-9\\-\\[\\]:\\.]+))?(_([A-Za-z0-9_\\-\\[\\]:\\.]+)*)";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(valueLabel);

        CVParam param;
        if (!matcher.find() || matcher.end() != valueLabel.length()) {
            throw new MZTabException(new MZTabError(FormatErrorType.OptionalCVParamColumn, lineNumber, nameLabel));
        } else {
            String accession = matcher.group(2);
            String name = matcher.group(4);
            if (name == null || name.trim().length() == 0) {
                throw new MZTabException(new MZTabError(FormatErrorType.OptionalCVParamColumn, lineNumber, nameLabel));
            }

            param = matcher.group(4) == null ? null : new CVParam(null, accession, name, null);
        }

        return param;
    }

    /**
     * Some {@link CVParamOptionColumn}, their data type have defined. Currently, we provide two {@link CVParam}
     * which defined in the mzTab specification. One is "emPAI value" (MS:1001905), data type is Double;
     * another is "decoy peptide" (MS:1002217), the data type is Boolean (0/1). Besides them, "opt_" start optional
     * column data type is String.
     *
     * @see #checkOptColumnName(String)
     */
    private Class getDataType(CVParam param) {
        Class dataType;

        if (param == null) {
            dataType = String.class;
        } else if (param.getAccession().equals("MS:1001905")) {
            dataType = Double.class;
        } else if (param.getAccession().equals("MS:1002217")) {
            dataType = MZBoolean.class;
        } else if (param.getAccession().equals("PRIDE:0000303")) {
            dataType = MZBoolean.class;
        } else {
            dataType = String.class;
        }

        return dataType;
    }

    protected int checkAbundanceColumns(int offset, String order) throws MZTabException {
        if (items[offset].contains("abundance_assay")) {
            checkAbundanceAssayColumn(items[offset], order);

            return offset;
        } else {
            String abundanceHeader = "";
            String abundanceStdevHeader = "";
            String abundanceStdErrorHeader = "";

            try {
                abundanceHeader = items[offset++];
                abundanceStdevHeader = items[offset++];
                abundanceStdErrorHeader = items[offset];
            } catch (ArrayIndexOutOfBoundsException e) {
                // do nothing.
            }

            checkAbundanceStudyVariableColumns(abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader, order);

            return offset;
        }
    }

    /**
     * Check (protein|peptide|smallmolecule)_abundance is correct, and return object value label.
     * For example, protein_abundance_std_error_study_variable[id], return study_variable[id].
     */
    private String checkAbundanceSection(String abundanceHeader) throws MZTabException {
        abundanceHeader = abundanceHeader.trim().toLowerCase();

        Pattern pattern = Pattern.compile("(protein|peptide|smallmolecule)_abundance_(.+)");
        Matcher matcher = pattern.matcher(abundanceHeader);

        if (matcher.find()) {
            String sectionName = translate(matcher.group(1));
            if (sectionName != null &&
                    !(sectionName.equals(Section.Protein.getName()) && section != Section.Protein_Header) &&
                    !(sectionName.equals(Section.Peptide.getName()) && section != Section.Peptide_Header) &&
                    !(sectionName.equals(Section.Small_Molecule.getName()) && section != Section.Small_Molecule_Header)) {
                return matcher.group(2);
            }

            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        } else {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }
    }

    private void checkAbundanceAssayColumn(String abundanceHeader, String order) throws MZTabException {
        String valueLabel = checkAbundanceSection(abundanceHeader);

        Pattern pattern = Pattern.compile("assay\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(valueLabel);
        if (!matcher.find()) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        int id = parseIndex(abundanceHeader, matcher.group(1));
        Assay assay = metadata.getAssayMap().get(id);
        if (assay == null) {
            MZTabError error = new MZTabError(LogicalErrorType.AssayNotDefined, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        factory.addAbundanceOptionalColumn(assay, order);
    }


    private void checkAbundanceStudyVariableColumns(String abundanceHeader,
                                                    String abundanceStdevHeader,
                                                    String abundanceStdErrorHeader,
                                                    String order) throws MZTabException {
        abundanceHeader = abundanceHeader.trim().toLowerCase();
        abundanceStdevHeader = abundanceStdevHeader.trim().toLowerCase();
        abundanceStdErrorHeader = abundanceStdErrorHeader.trim().toLowerCase();

        if (!abundanceHeader.contains("_abundance_study_variable")) {
            String missHeader = Section.toDataSection(section).getName() + "_abundance_study_variable";

            MZTabError error = new MZTabError(LogicalErrorType.AbundanceColumnTogether, lineNumber, missHeader);
            throw new MZTabException(error);
        }

        if (!abundanceStdevHeader.contains("_abundance_stdev_study_variable")) {
            String missHeader = Section.toDataSection(section).getName() + "_abundance_stdev_study_variable";

            MZTabError error = new MZTabError(LogicalErrorType.AbundanceColumnTogether, lineNumber, missHeader);
            throw new MZTabException(error);
        }

        if (!abundanceStdErrorHeader.contains("_abundance_std_error_study_variable")) {
            String missHeader = Section.toDataSection(section).getName() + "_abundance_std_error_study_variable";

            MZTabError error = new MZTabError(LogicalErrorType.AbundanceColumnTogether, lineNumber, missHeader);
            throw new MZTabException(error);
        }

        StudyVariable abundanceStudyVariable = checkAbundanceStudyVariableColumn(abundanceHeader);
        StudyVariable abundanceStdevStudyVariable = checkAbundanceStudyVariableColumn(abundanceStdevHeader);
        StudyVariable abundanceStdErrorStudyVariable = checkAbundanceStudyVariableColumn(abundanceStdErrorHeader);

        //It need to be the same studyVariable
        if (abundanceStudyVariable != abundanceStdevStudyVariable || abundanceStudyVariable != abundanceStdErrorStudyVariable) {
            MZTabError error = new MZTabError(LogicalErrorType.AbundanceColumnSameId, lineNumber, abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader);
            throw new MZTabException(error);
        }

        factory.addAbundanceOptionalColumn(abundanceStudyVariable, order);
    }

    /**
     * Check XXXX_abundance_study_variable[id], XXXX_abundance_stdev_study_variable[id], XXXX_abundance_std_error_study_variable[id]
     * column header. If parse error, stop validate and raise {@link MZTabException}.
     */
    private StudyVariable checkAbundanceStudyVariableColumn(String abundanceHeader) throws MZTabException {
        String valueLabel = checkAbundanceSection(abundanceHeader);

        Pattern pattern = Pattern.compile("study_variable\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(valueLabel);
        if (!matcher.find()) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        int id = parseIndex(abundanceHeader, matcher.group(1));
        StudyVariable studyVariable = metadata.getStudyVariableMap().get(id);
        if (studyVariable == null) {
            MZTabError error = new MZTabError(LogicalErrorType.StudyVariableNotDefined, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        return studyVariable;
    }

    /**
     * Facing colunit definition line, for example:
     * MTD  colunit-protein retention_time=[UO, UO:000031, minute, ]
     * which depends on the header line definitions.
     */
    //TODO Migrate to specific class
    public void checkColUnit() throws MZTabException {
        String valueLabel;
        for (String defineLabel : metadata.getColUnitMap().keySet()) {
            if (defineLabel.equalsIgnoreCase("colunit-" + Section.toDataSection(factory.getSection()).getName())) {
                valueLabel = metadata.getColUnitMap().get(defineLabel);

                String[] items = valueLabel.split("=");
                String columnName = items[0].trim();
                String value = items[1].trim();

                MZTabColumn column = factory.findColumnByHeader(columnName);
                if (column == null) {
                    // column_name not exists in the factory.
                    errorList.add(new MZTabError(FormatErrorType.ColUnit, lineNumber, valueLabel, columnName));
                } else {
                    Param param = parseParam(value);
                    if (param == null) {
                        errorList.add(new MZTabError(FormatErrorType.Param, lineNumber, valueLabel, value));
                    } else {
                        switch (factory.getSection()) {
                            case Protein_Header:
                                metadata.addProteinColUnit(column, param);
                                break;
                            case Peptide_Header:
                                metadata.addPeptideColUnit(column, param);
                                break;
                            case PSM_Header:
                                metadata.addPSMColUnit(column, param);
                                break;
                            case Small_Molecule_Header:
                                metadata.addSmallMoleculeColUnit(column, param);
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * This is a temporary method which face smallmolecule in SmallMolecule header line.
     * translate smallmolecule --> small_molecule.
     *
     * @see AbundanceColumn#translate(String)
     */
    private String translate(String oldName) {
        if (oldName.equals("smallmolecule")) {
            return "small_molecule";
        }
        return oldName;
    }

    /**
     * Parse header to a index id number.
     * If exists parse error, stop validate and throw {@link MZTabException} directly.
     */
    protected int parseIndex(String header, String id) throws MZTabException {
        try {
            Integer index = Integer.parseInt(id);
            if (index < 1) {
                throw new NumberFormatException();
            }

            return index;
        } catch (NumberFormatException e) {
            MZTabError error = new MZTabError(LogicalErrorType.IdNumber, lineNumber, header, id);
            throw new MZTabException(error);
        }
    }

    public MZTabColumnFactory getFactory() {
        return factory;
    }
}
