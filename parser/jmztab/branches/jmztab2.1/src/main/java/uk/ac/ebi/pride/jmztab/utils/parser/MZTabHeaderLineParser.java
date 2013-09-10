package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* The class used to do common operations in protein/peptide/small_molecular
* header line. There are two main categories in columns: stable columns and
* optional columns.
*
* User: Qingwei
* Date: 11/02/13
*/
public class MZTabHeaderLineParser extends MZTabLineParser {
    private MZTabColumnFactory factory;
    private Metadata metadata;

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
     * We assume that user before call this method, have parse the raw line
     * is not empty line and start with section prefix.
     */
    protected void parse(int lineNumber, String line) throws MZTabException {
        super.parse(lineNumber, line);

        int offset = parseStableOrderColumns();
        offset++;
        if (offset < items.length) {
            matchOptionalColumns(offset);
        }
    }

    /**
     * Checking these columns' which order stable. Just not care about abundance columns and opt_ columns.
     */
    private int parseStableOrderColumns() throws MZTabException {
        List<String> headerList = Arrays.asList(items);

        MZTabError error;

        // step 1: confirm stable columns have been included in the header line.
        for (MZTabColumn column : factory.getStableColumnMapping().values()) {
            if (! headerList.contains(column.getHeader())) {
                error = new MZTabError(LogicalErrorType.StableColumnNotFound, lineNumber, column.getHeader());
                throw new MZTabException(error);
            }
        }

        // step 2: checking some optional columns which have stable order.
        String header;
        Pattern pattern = Pattern.compile("(\\w+)_ms_run\\[(\\d)\\]");
        Matcher matcher;
        MZTabColumn column = null;
        MsRun msRun;
        int id;
        for (int i = 1; i < items.length; i++) {
            header = items[i];
            matcher = pattern.matcher(header);
            if (matcher.find()) {
                id = new Integer(matcher.group(2));
                msRun = metadata.getMsRunMap().get(id);
                if (msRun == null) {
                    error = new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, header);
                    throw new MZTabException(error);
                }

                switch (section) {
                    case Protein_Header:
                        if (header.startsWith(ProteinColumn.SEARCH_ENGINE_SCORE.getName())) {
                            column = ProteinColumn.SEARCH_ENGINE_SCORE;
                        } else if (header.startsWith(ProteinColumn.NUM_PSMS.getName())) {
                            column = ProteinColumn.NUM_PSMS;
                        } else if (header.startsWith(ProteinColumn.NUM_PEPTIDES_DISTINCT.getName())) {
                            column = ProteinColumn.NUM_PEPTIDES_DISTINCT;
                        } else if (header.startsWith(ProteinColumn.NUM_PEPTIDES_UNIQUE.getName())) {
                            column = ProteinColumn.NUM_PEPTIDES_UNIQUE;
                        }
                        break;
                    case Peptide_Header:
                        if (header.startsWith(PeptideColumn.SEARCH_ENGINE_SCORE.getName())) {
                            column = PeptideColumn.SEARCH_ENGINE_SCORE;
                        }
                        break;
                    case Small_Molecule_Header:
                        if (header.startsWith(SmallMoleculeColumn.SEARCH_ENGINE_SCORE.getName())) {
                            column = SmallMoleculeColumn.SEARCH_ENGINE_SCORE;
                        }
                        break;
                }

                if (column != null) {
                    factory.addOptionalColumn(column, msRun);
                }
            }
        }

        return factory.getColumnMapping().values().size();
    }

    private void matchOptionalColumns(int offset) throws MZTabException {
        String columnName = items[offset].trim();

        if (columnName.startsWith("opt_")) {
            checkOptColumnName(columnName);
        } else if (columnName.contains("abundance")) {
            offset = checkAbundanceColumns(offset);
        }

        if (offset < items.length - 1) {
            offset++;
            matchOptionalColumns(offset);
        }
    }

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
     * opt_{assay_id|study_variable_id|ms_run_id|global}_value
     */
    private boolean checkOptColumnName(String nameLabel) throws MZTabException {
        String regexp = "opt_((assay|study_variable|ms_run)\\[(\\d)\\]|global)_([A-Za-z0-9_\\-\\[\\]:\\.]+)";
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
                param = parseCVParamOptColumnName(value);
            }

            Class dataType = getDataType(param);

            if (object_id.contains("global")) {
                if (param == null) {
                    factory.addOptionalColumn(value, dataType);
                } else {
                    factory.addOptionalColumn(param, dataType);
                }
            } else {
                id = new Integer(matcher.group(3));

                if (object_id.contains("assay")) {
                    Assay element = metadata.getAssayMap().get(id);
                    // not found assay_id in metadata.
                    if (element == null) {
                        error = new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, nameLabel);
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
                        error = new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, nameLabel);
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
                        error = new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, nameLabel);
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
            return false;
        }
    }

    /**
     * opt_cv_{accession}_{parameter name}
     */
    private CVParam parseCVParamOptColumnName(String valueLabel) {
        String regexp = "cv(_([A-Za-z0-9\\-\\[\\]:\\.]+))?(_([A-Za-z0-9_\\-\\[\\]:\\.]+)*)";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(valueLabel);

        CVParam param;
        if (! matcher.find() || matcher.end() != valueLabel.length()) {
            param = null;
        } else {
            param = matcher.group(4) == null ? null : new CVParam(null, matcher.group(2), matcher.group(4), null);
        }

        return param;
    }

    /**
     * abundance_assay[id], abundance_study_variable[id] abundance_stdev_sub_study_variable[id],
     * abundance_std_error_study_variable[id]. The last three columns should be display together.
     * Thus, this method will parse three abundance columns as a group, and return the offset of next
     */
    private int checkAbundanceColumns(int offset) throws MZTabException {
        if (items[offset].contains("abundance_assay")) {
            checkAbundanceAssayColumn(items[offset]);

            return offset;
        } else {
            String abundanceHeader = null;
            String abundanceStdevHeader = null;
            String abundanceStdErrorHeader = null;

            try {
                abundanceHeader = items[offset++];
                abundanceStdevHeader = items[offset++];
                abundanceStdErrorHeader = items[offset];
            } catch (ArrayIndexOutOfBoundsException e) {
                String header;
                if (abundanceHeader == null) {
                    header = AbundanceColumn.Field.ABUNDANCE.toString();
                } else if (abundanceStdevHeader == null) {
                    header = AbundanceColumn.Field.ABUNDANCE_STDEV.toString();
                } else {
                    header = AbundanceColumn.Field.ABUNDANCE_STD_ERROR.toString();
                }

                MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, header);
                throw new MZTabException(error);
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
     * Check (protein|peptide|smallmolecule)_abundance is correct, and return object value label.
     * For example, protein_abundance_std_error_study_variable[id], return study_variable[id].
     */
    private String checkAbundanceSection(String abundanceHeader) throws MZTabException {
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
     * check XXXX_abundance_assay[id]
     */
    private void checkAbundanceAssayColumn(String abundanceHeader) throws MZTabException {
        String valueLabel = checkAbundanceSection(abundanceHeader);

        Pattern pattern = Pattern.compile("assay\\[(\\d)\\]");
        Matcher matcher = pattern.matcher(valueLabel);
        if (! matcher.find()) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        int id = new Integer(matcher.group(1));
        Assay assay = metadata.getAssayMap().get(id);
        if (assay == null) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        factory.addAbundanceOptionalColumn(assay);
    }

    private StudyVariable checkAbundanceStudyVariableColumn(String abundanceHeader) throws MZTabException {
        String valueLabel = checkAbundanceSection(abundanceHeader);

        Pattern pattern = Pattern.compile("study_variable\\[(\\d)\\]");
        Matcher matcher = pattern.matcher(valueLabel);
        if (! matcher.find()) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        int id = new Integer(matcher.group(1));
        StudyVariable studyVariable = metadata.getStudyVariableMap().get(id);
        if (studyVariable == null) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader);
            throw new MZTabException(error);
        }

        return studyVariable;
    }

    private void checkAbundanceStudyVariableColumns(String abundanceHeader,
                                                    String abundanceStdevHeader,
                                                    String abundanceStdErrorHeader) throws MZTabException {

        StudyVariable abundanceStudyVariable = checkAbundanceStudyVariableColumn(abundanceHeader);
        StudyVariable abundanceStdevStudyVariable = checkAbundanceStudyVariableColumn(abundanceStdevHeader);
        StudyVariable abundanceStdErrorStudyVariable = checkAbundanceStudyVariableColumn(abundanceStdErrorHeader);

        if (abundanceStudyVariable != abundanceStdevStudyVariable || abundanceStudyVariable != abundanceStdErrorStudyVariable) {
            MZTabError error = new MZTabError(FormatErrorType.AbundanceColumn, lineNumber, abundanceHeader + "\t" + abundanceStdevHeader + "\t" + abundanceStdErrorHeader);
            throw new MZTabException(error);
        }

        factory.addAbundanceOptionalColumn(abundanceStudyVariable);
    }

    public MZTabColumnFactory getFactory() {
        return factory;
    }
}
