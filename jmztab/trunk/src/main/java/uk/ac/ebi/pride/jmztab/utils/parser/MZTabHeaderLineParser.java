package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* A couple of common method used to parse a header line into {@link MZTabColumnFactory} structure.
 *
 * NOTICE: {@link MZTabColumnFactory} maintain a couple of {@link MZTabColumn} which have internal logical
 * position and order. In physical mzTab file, we allow user not obey this logical position organized way,
 * and provide their date with own order. In order to distinguish them, we use physical position (a positive
 * integer) to record the column location in mzTab file. And use {@link PositionMapping} structure the maintain
 * the mapping between them.
 *
 * @see PRHLineParser
 * @see PEHLineParser
 * @see PSHLineParser
 * @see SMHLineParser
*
* User: Qingwei
* Date: 11/02/13
*/
public abstract class MZTabHeaderLineParser extends MZTabLineParser {
    protected MZTabColumnFactory factory;
    protected Metadata metadata;

    /**
     * Parse a header line into {@link MZTabColumnFactory} structure.
     *
     * @param factory SHOULD NOT set null
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
     * Some validate operation need to be done after the whole {@link MZTabColumnFactory} created.
     * Thus, user can add them, and called at the end of the
     * {@link #parse(int, String, uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList)} method.
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

    /**
     * Parse a header line into {@link MZTabColumnFactory} structure. There are two steps in this method:
     * Step 1: {@link #parseStableOrderColumns()} focus on validate and parse for stable columns and optional
     * columns which have stable order; and Step 2: {@link #parseOptionalColumns(int)} focus on {@link AbundanceColumn},
     * {@link OptionColumn} and {@link CVParamOptionColumn} parse and validation.
     */
    protected void parse(int lineNumber, String line, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, line, errorList);

        int offset = parseStableOrderColumns();
        if (offset < items.length) {
            parseOptionalColumns(offset);
        }

        refine();
    }

    private void addSearchEngineColumn(String searchEngineHeader, MsRun msRun) throws MZTabException {
        Pattern pattern = Pattern.compile("search_engine_score\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(searchEngineHeader);

        if (matcher.find()) {
            Integer id = checkIndex(searchEngineHeader, matcher.group(1));
            if (! metadata.getSearchEngineScoreMap().containsKey(id)) {
                throw new MZTabException(new MZTabError(LogicalErrorType.SearchEngineScoreNotDefined, lineNumber, searchEngineHeader));
            }

            switch (section) {
                case Protein_Header:
                    factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, id, msRun);
                    break;
                case Peptide_Header:
                    factory.addSearchEngineScoreOptionalColumn(PeptideColumn.SEARCH_ENGINE_SCORE, id, msRun);
                    break;
                case PSM_Header:
                    factory.addSearchEngineScoreOptionalColumn(PSMColumn.SEARCH_ENGINE_SCORE, id, null);
                    break;
                case Small_Molecule_Header:
                    factory.addSearchEngineScoreOptionalColumn(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, id, msRun);
                    break;
            }

        }
    }

    /**
     * Focus on validate and parse for stable columns and optional columns which have stable order;
     * All of them defined in the {@link ProteinColumn}, {@link PeptideColumn}, {@link PSMColumn}
     * or {@link SmallMoleculeColumn}.
     *
     * NOTICE: there not exist optional columns with stable order in {@link PSMColumn}
     */
    private int parseStableOrderColumns() throws MZTabException {
        List<String> headerList = Arrays.asList(items);

        MZTabError error;

        // step 1: confirm stable columns have been included in the header line.
        for (MZTabColumn column : factory.getStableColumnMapping().values()) {
            if (! headerList.contains(column.getHeader())) {
                error = new MZTabError(FormatErrorType.StableColumn, lineNumber, column.getHeader());
                throw new MZTabException(error);
            }
        }

        // step 2: checking some optional columns which have stable order.
        for (String header : headerList) {
            if (header.equals(ProteinColumn.GO_TERMS.getHeader())) {
                factory.addGoTermsOptionalColumn();
            } else if (header.equals(ProteinColumn.RELIABILITY.getHeader())) {
                factory.addReliabilityOptionalColumn();
            } else if (header.equals(ProteinColumn.URI.getHeader())) {
                factory.addURIOptionalColumn();
            }
        }

        // step 3: checking best_search_engine_score columns which have stable order.
        String header;
        Pattern pattern = Pattern.compile("best_search_engine_score\\[(\\d+)\\]");
        Matcher matcher;
        MZTabColumn column = null;
        int id;
        for (int i = 1; i < items.length; i++) {
            header = items[i];
            matcher = pattern.matcher(header);
            if (matcher.find()) {
                id = checkIndex(header, matcher.group(1));
                if (! metadata.getSearchEngineScoreMap().containsKey(id)) {
                     throw new MZTabException(new MZTabError(LogicalErrorType.SearchEngineScoreNotDefined, lineNumber, header));
                }

                switch (section) {
                    case Protein_Header:
                        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, id);
                        break;
                    case Peptide_Header:
                        factory.addBestSearchEngineScoreOptionalColumn(PeptideColumn.BEST_SEARCH_ENGINE_SCORE, id);
                        break;
                    case Small_Molecule_Header:
                        factory.addBestSearchEngineScoreOptionalColumn(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE, id);
                        break;
                }
            }
        }

        // step 4: checking ms_run flexible optional columns which have stable order.
        pattern = Pattern.compile("(.+)_ms_run\\[(\\d+)\\]");
        MsRun msRun;
        for (int i = 1; i < items.length; i++) {
            header = items[i];
            matcher = pattern.matcher(header);
            if (matcher.find()) {
                id = checkIndex(header, matcher.group(2));

                msRun = metadata.getMsRunMap().get(id);
                if (msRun == null) {
                    throw new MZTabException(new MZTabError(LogicalErrorType.MsRunNotDefined, lineNumber, header));
                }

                switch (section) {
                    case Protein_Header:
                        if (header.startsWith(ProteinColumn.SEARCH_ENGINE_SCORE.getName())) {
                            addSearchEngineColumn(matcher.group(1), msRun);
                        } else if (header.startsWith(ProteinColumn.NUM_PSMS.getName())) {
                            column = ProteinColumn.NUM_PSMS;
                        } else if (header.startsWith(ProteinColumn.NUM_PEPTIDES_DISTINCT.getName())) {
                            column = ProteinColumn.NUM_PEPTIDES_DISTINCT;
                        } else if (header.startsWith(ProteinColumn.NUM_PEPTIDES_UNIQUE.getName())) {
                            column = ProteinColumn.NUM_PEPTIDES_UNIQUE;
                        } else if (header.startsWith("opt_")) {
                            // ignore opt_ms_run....
                            // This kind of optional columns will be processed in the parseOptionalColumns() method.
                        } else {
                            throw new MZTabException(new MZTabError(FormatErrorType.MsRunOptionalColumn, lineNumber, header, section.getName()));
                        }
                        break;
                    case Peptide_Header:
                        if (header.startsWith(PeptideColumn.SEARCH_ENGINE_SCORE.getName())) {
                            addSearchEngineColumn(matcher.group(1), msRun);
                        } else if (header.startsWith("opt_")) {
                            // ignore opt_ms_run....
                            // This kind of optional columns will be processed in the parseOptionalColumns() method.
                        } else {
                            throw new MZTabException(new MZTabError(FormatErrorType.MsRunOptionalColumn, lineNumber, header, section.getName()));
                        }
                        break;
                    case Small_Molecule_Header:
                        if (header.startsWith(SmallMoleculeColumn.SEARCH_ENGINE_SCORE.getName())) {
                            addSearchEngineColumn(matcher.group(1), msRun);
                        } else if (header.startsWith("opt_")) {
                            // ignore opt_ms_run....
                            // This kind of optional columns will be processed in the parseOptionalColumns() method.
                        } else {
                            throw new MZTabException(new MZTabError(FormatErrorType.MsRunOptionalColumn, lineNumber, header, section.getName()));
                        }
                        break;
                }

                if (column != null) {
                    factory.addOptionalColumn(column, msRun);
                }
            }
        }

        // Step 6: PSM section, parse search_engine_score[id]
        if (section == Section.PSM_Header) {
            for (int i = 1; i < items.length; i++) {
                header = items[i];

                if (header.startsWith(PSMColumn.SEARCH_ENGINE_SCORE.getName())) {
                    addSearchEngineColumn(header, null);
                }
            }
        }


        return factory.getStableColumnMapping().values().size();
    }

    /**
     * Iterative parse optional columns with dynamic logical position and order. These optional column including:
     * {@link AbundanceColumn}, {@link OptionColumn} and {@link CVParamOptionColumn} parse and validation.
     * All of these optional columns always put the end of the table, thus, we use offset to locate the last
     * column position.
     *
     * @param offset the cursor to locate the optional columns locate the end of the table.
     */
    private void parseOptionalColumns(int offset) throws MZTabException {
        String columnName = items[offset].trim();

        if (columnName.startsWith("opt_")) {
            checkOptColumnName(columnName);
        } else if (columnName.contains("abundance")) {
            offset = checkAbundanceColumns(offset);
        }

        if (offset < items.length - 1) {
            offset++;
            parseOptionalColumns(offset);
        }
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
        } else {
            dataType = String.class;
        }

        return dataType;
    }

    /**
     * Additional columns can be added to the end of the protein table. These column headers MUST start with the prefix "opt_".
     * Column names MUST only contain the following characters: 'A'-'Z', 'a'-'z', '0'-'9', '_', '-', '[', ']', and ':'.
     *
     * the format: opt_{IndexedElement[id]}_{value}. Spaces within the parameter's name MUST be replaced by '_'.
     */
    private boolean checkOptColumnName(String nameLabel) throws MZTabException {
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
                id = checkIndex(nameLabel, matcher.group(3));

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
        if (! matcher.find() || matcher.end() != valueLabel.length()) {
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
     * Abundance_assay[id], abundance_study_variable[id] abundance_stdev_sub_study_variable[id],
     * abundance_std_error_study_variable[id]. The last three columns should be display together.
     * Thus, this method will parse three abundance columns as a group, and return the offset of next
     */
    private int checkAbundanceColumns(int offset) throws MZTabException {
        if (items[offset].contains("abundance_assay")) {
            checkAbundanceAssayColumn(items[offset]);

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

            checkAbundanceStudyVariableColumns(abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader);

            return offset;
        }
    }

    /**
     * This is a temporary method which face smallmolecule in SmallMolecule header line.
     * translate smallmolecule --> small_molecule.
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
    private int checkIndex(String header, String id) throws MZTabException {
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

    /**
     * Check XXXX_abundance_assay[id] column header. If parse error, stop validate and raise
     * {@link MZTabException}.
     */
    private void checkAbundanceAssayColumn(String abundanceHeader) throws MZTabException {
        String valueLabel = checkAbundanceSection(abundanceHeader);

        Pattern pattern = Pattern.compile("assay\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(valueLabel);
        if (! matcher.find()) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        int id = checkIndex(abundanceHeader, matcher.group(1));
        Assay assay = metadata.getAssayMap().get(id);
        if (assay == null) {
            MZTabError error = new MZTabError(LogicalErrorType.AssayNotDefined, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        factory.addAbundanceOptionalColumn(assay);
    }

    /**
     * Check XXXX_abundance_study_variable[id], XXXX_abundance_stdev_study_variable[id], XXXX_abundance_std_error_study_variable[id]
     * column header. If parse error, stop validate and raise {@link MZTabException}.
     */
    private StudyVariable checkAbundanceStudyVariableColumn(String abundanceHeader) throws MZTabException {
        String valueLabel = checkAbundanceSection(abundanceHeader);

        Pattern pattern = Pattern.compile("study_variable\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(valueLabel);
        if (! matcher.find()) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        int id = checkIndex(abundanceHeader, matcher.group(1));
        StudyVariable studyVariable = metadata.getStudyVariableMap().get(id);
        if (studyVariable == null) {
            MZTabError error = new MZTabError(LogicalErrorType.StudyVariableNotDefined, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        return studyVariable;
    }

    /**
     * Check XXXX_abundance_study_variable[id], XXXX_abundance_stdev_study_variable[id], XXXX_abundance_std_error_study_variable[id]
     * column header. If parse error, stop validate and raise {@link MZTabException}.
     */
    private void checkAbundanceStudyVariableColumns(String abundanceHeader,
                                                    String abundanceStdevHeader,
                                                    String abundanceStdErrorHeader) throws MZTabException {
        abundanceHeader = abundanceHeader.trim().toLowerCase();
        abundanceStdevHeader = abundanceStdevHeader.trim().toLowerCase();
        abundanceStdErrorHeader = abundanceStdErrorHeader.trim().toLowerCase();

        if (! abundanceHeader.contains("_abundance_study_variable")) {
            String missHeader = Section.toDataSection(section).getName() + "_abundance_study_variable";

            MZTabError error = new MZTabError(LogicalErrorType.AbundanceColumnTogether, lineNumber, missHeader);
            throw new MZTabException(error);
        }

        if (! abundanceStdevHeader.contains("_abundance_stdev_study_variable")) {
            String missHeader = Section.toDataSection(section).getName() + "_abundance_stdev_study_variable";

            MZTabError error = new MZTabError(LogicalErrorType.AbundanceColumnTogether, lineNumber, missHeader);
            throw new MZTabException(error);
        }

        if (! abundanceStdErrorHeader.contains("_abundance_std_error_study_variable")) {
            String missHeader = Section.toDataSection(section).getName() + "_abundance_std_error_study_variable";

            MZTabError error = new MZTabError(LogicalErrorType.AbundanceColumnTogether, lineNumber, missHeader);
            throw new MZTabException(error);
        }

        StudyVariable abundanceStudyVariable = checkAbundanceStudyVariableColumn(abundanceHeader);
        StudyVariable abundanceStdevStudyVariable = checkAbundanceStudyVariableColumn(abundanceStdevHeader);
        StudyVariable abundanceStdErrorStudyVariable = checkAbundanceStudyVariableColumn(abundanceStdErrorHeader);

        if (abundanceStudyVariable != abundanceStdevStudyVariable || abundanceStudyVariable != abundanceStdErrorStudyVariable) {
            MZTabError error = new MZTabError(LogicalErrorType.AbundanceColumnSameId, lineNumber, abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader);
            throw new MZTabException(error);
        }

        factory.addAbundanceOptionalColumn(abundanceStudyVariable);
    }

    public MZTabColumnFactory getFactory() {
        return factory;
    }
}
