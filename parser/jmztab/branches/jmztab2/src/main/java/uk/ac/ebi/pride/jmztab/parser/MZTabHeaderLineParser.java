package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.*;

import java.util.SortedMap;
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
            throw new NullPointerException("Header line should be check first!");
        }
        this.factory = factory;

        if (metadata == null) {
            throw new NullPointerException("Metadata should be create first!");
        }
        this.metadata = metadata;
    }

    /**
     * We assume that user before call this method, have check the raw line
     * is not empty line and start with section prefix.
     */
    protected void check(int lineNumber, String line) throws MZTabException {
        super.check(lineNumber, line);

        matchStableColumns();

        SortedMap<Integer, MZTabColumn> mapping = this.factory.getColumnMapping();
        int offset = mapping.lastKey();
        if (offset == items.length - 1) {
            // no optional columns
            return;
        }

        offset++;
        matchOptionalColumns(offset);
    }

    /**
     * Check the stable columns matching, including position and header name.
     * If position or header name not matched, throw MZTabException, and stop
     * execution.
     */
    private void matchStableColumns() throws MZTabException {
        String header;
        for (int i = 1; i <= factory.getHeaderList().size(); i++) {
            header = factory.getColumn(i).getHeader();
            if (! header.equals(items[i])) {
                MZTabError error = new MZTabError(
                        FormatErrorType.StableColumn, lineNumber,
                        header, "" + factory.getColumn(header).getPosition(),
                       "" + i,  items[i]
                );
                throw new MZTabException(error);
            }
        }
    }

    private void matchOptionalColumns(int offset) throws MZTabException {
        String columnName = items[offset].trim();

        if (columnName.startsWith("opt_cv")) {
            if (parseCVParamOptColumnName(columnName) == null) {
                MZTabError error = new MZTabError(
                        FormatErrorType.OptionalCVParamColumn, lineNumber,
                        columnName
                );
                throw new MZTabException(error);
            }
        } else if (columnName.startsWith("opt_")) {
            if (! checkOptColumnName(columnName)) {
                MZTabError error = new MZTabError(
                        FormatErrorType.OptionalColumn, lineNumber,
                        columnName
                );
                throw new MZTabException(error);
            }
        } else if (columnName.contains("abundance")) {
            offset = checkAbundanceColumns(offset);
        } else {
            MZTabError error = new MZTabError(
                    FormatErrorType.OptionalColumn, lineNumber,
                    columnName
            );
            throw new MZTabException(error);
        }

        if (offset < items.length - 1) {
            offset++;
            matchOptionalColumns(offset);
        }
    }

    /**
     * opt_nameLabel
     */
    private boolean checkOptColumnName(String nameLabel) {
        String regexp = "opt_([A-Za-z0-9_\\-\\[\\]:\\.]+)";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(nameLabel);

        if (matcher.find() && matcher.end() == nameLabel.length()) {
            factory.addOptionColumn(matcher.group(1), String.class);
            return true;
        } else {
            return false;
        }
    }

    /**
     * opt_cv_{accession}_{parameter name}
     */
    private CVParam parseCVParamOptColumnName(String nameLabel) {
        String regexp = "opt_cv(_([A-Za-z0-9\\-\\[\\]:\\.]+))?(_([A-Za-z0-9_\\-\\[\\]:\\.]+)*)";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(nameLabel);

        CVParam param;
        if (! matcher.find() || matcher.end() != nameLabel.length()) {
            param = null;
        } else {
            param = matcher.group(4) == null ? null : new CVParam(null, matcher.group(2), matcher.group(4), null);
            factory.addCVParamOptionColumn(param);
        }

        return param;
    }

    /**
     * abundance_sub, abundance_stdev_sub, abundance_std_error_sub should be display together.
     * Thus, this method will check three abundance columns as a group, and return the offset of next
     */
    private int checkAbundanceColumns(int offset) throws MZTabException {
        String abundanceHeader = null;
        String abundanceStdevHeader = null;
        String abundanceStdErrorHeader;

        MZTabError error;

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

            error = new MZTabError(
                    FormatErrorType.AbundanceColumn, lineNumber, header);
            throw new MZTabException(error);
        }

        validAbundanceColumns(abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader);

        return offset;
    }

    private boolean validAbundanceSection(String sectionName) {
        return sectionName != null &&
               !(sectionName.equals(Section.Protein.getName()) && section != Section.Protein_Header) &&
               !(sectionName.equals(Section.Peptide.getName()) && section != Section.Peptide_Header) &&
               !(sectionName.equals(Section.Small_Molecule.getName()) && section != Section.Small_Molecule_Header);
    }

    /**
     * subId should be defined in the metadata.
     */
    private SubUnit validAbundanceSubId(String subId) {
        for (String identifier : metadata.keySet()) {
            if (identifier.contains(subId)) {
                return (SubUnit) metadata.getUnit(identifier);
            }
        }
        return null;
    }

    private boolean validAbundanceColumns(String abundanceHeader,
                                          String abundanceStdevHeader,
                                          String abundanceStdErrorHeader) throws MZTabException {
        Pattern pattern;
        Matcher matcher;
        MZTabError error;

        String sectionName;
        SubUnit subUnit;
        Integer group_id;
//        pattern = Pattern.compile("(protein|peptide|small_molecule)_abundance_(sub\\[(\\d+)\\])");
        pattern = Pattern.compile("(protein|peptide|smallmolecule)_abundance_(sub\\[(\\d+)\\])");
        matcher = pattern.matcher(abundanceHeader);
        if (matcher.find()) {
            sectionName = matcher.group(1);
            if (! validAbundanceSection(sectionName)) {
                error = new MZTabError(
                        FormatErrorType.AbundanceColumn, lineNumber,
                        AbundanceColumn.Field.ABUNDANCE.toString(), abundanceHeader);
                throw new MZTabException(error);
            }

            if (validAbundanceSubId(matcher.group(2)) == null) {
                error = new MZTabError(
                        LogicalErrorType.AbundanceColumnId, lineNumber,
                        AbundanceColumn.Field.ABUNDANCE.toString(), abundanceHeader);
                throw new MZTabException(error);
            }

            group_id = new Integer(matcher.group(3));
        } else {
            error = new MZTabError(
                    FormatErrorType.AbundanceColumn, lineNumber,
                    AbundanceColumn.Field.ABUNDANCE.toString(), abundanceHeader);
            throw new MZTabException(error);
        }

        pattern = Pattern.compile("(protein|peptide|smallmolecule)_abundance_stdev_(sub\\[(\\d+)\\])");
//        pattern = Pattern.compile("(protein|peptide|small_molecule)_abundance_stdev_(sub\\[(\\d+)\\])");
        matcher = pattern.matcher(abundanceStdevHeader);
        if (matcher.find()) {
            sectionName = matcher.group(1);
            if (! validAbundanceSection(sectionName)) {
                error = new MZTabError(
                        FormatErrorType.AbundanceColumn, lineNumber,
                        AbundanceColumn.Field.ABUNDANCE_STDEV.toString(), abundanceStdevHeader);
                throw new MZTabException(error);
            }

            if (validAbundanceSubId(matcher.group(2)) == null) {
                error = new MZTabError(
                        LogicalErrorType.AbundanceColumnId, lineNumber,
                        AbundanceColumn.Field.ABUNDANCE_STDEV.toString(), abundanceStdevHeader);
                throw new MZTabException(error);
            }

            // abundance_stdev should have same subid with abundance.
            if (! group_id.toString().equals(matcher.group(3))) {
                error = new MZTabError(
                        LogicalErrorType.AbundanceColumnSameId, lineNumber,
                        abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader);
                throw new MZTabException(error);
            }
        } else {
            error = new MZTabError(
                    FormatErrorType.AbundanceColumn, lineNumber,
                    AbundanceColumn.Field.ABUNDANCE_STDEV.toString(), abundanceStdevHeader);
            throw new MZTabException(error);
        }

        pattern = Pattern.compile("(protein|peptide|smallmolecule)_abundance_std_error_(sub\\[(\\d+)\\])");
//        pattern = Pattern.compile("(protein|peptide|small_molecule)_abundance_std_error_(sub\\[(\\d+)\\])");
        matcher = pattern.matcher(abundanceStdErrorHeader);
        if (matcher.find()) {
            sectionName = matcher.group(1);
            if (! validAbundanceSection(sectionName)) {
                error = new MZTabError(
                        FormatErrorType.AbundanceColumn, lineNumber,
                        AbundanceColumn.Field.ABUNDANCE_STD_ERROR.toString(), abundanceStdErrorHeader);
                throw new MZTabException(error);
            }

            if ((subUnit = validAbundanceSubId(matcher.group(2))) == null) {
                error = new MZTabError(
                        LogicalErrorType.AbundanceColumnId, lineNumber,
                        AbundanceColumn.Field.ABUNDANCE_STD_ERROR.toString(), abundanceStdErrorHeader);
                throw new MZTabException(error);
            }

            // abundance_std_error should have same subid with abundance.
            if (! group_id.toString().equals(matcher.group(3))) {
                error = new MZTabError(
                        LogicalErrorType.AbundanceColumnSameId, lineNumber,
                        abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader);
                throw new MZTabException(error);
            }
        } else {
            error = new MZTabError(
                    FormatErrorType.AbundanceColumn, lineNumber,
                    AbundanceColumn.Field.ABUNDANCE_STD_ERROR.toString(), abundanceStdErrorHeader);
            throw new MZTabException(error);
        }

        factory.addAbundanceColumns(subUnit);

        return true;
    }

    public MZTabColumnFactory getFactory() {
        return factory;
    }
}
