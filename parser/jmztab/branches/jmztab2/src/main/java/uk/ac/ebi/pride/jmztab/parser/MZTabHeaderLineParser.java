package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
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
            throw new NullPointerException("Header line should be parse first!");
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
    public void parse(int lineNumber, String line) {
        super.parse(lineNumber, line);

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
        for (int i = 1; i < items.length; i++) {
            header = factory.getColumn(i).getHeader();
            if (! header.equals(items[i])) {
                MZTabError error = new MZTabError(
                        FormatErrorType.StableColumn, lineNumber, false,
                        header, "" + factory.getColumn(header).getPosition(),
                       "" + i,  items[i]
                );
                throw new MZTabException(error);
            }
        }
    }

    private void matchOptionalColumns(int offset) throws MZTabException {
        String columnName = items[offset].trim();

        if (columnName.startsWith("opt_cv") && parseCVParamOptColumnName(columnName) == null) {
            MZTabError error = new MZTabError(
                    FormatErrorType.Optional, lineNumber, false,
                    columnName
            );
            throw new MZTabException(error);
        } else if (columnName.startsWith("opt_") && ! checkOptColumnName(columnName)) {
            MZTabError error = new MZTabError(
                    FormatErrorType.Optional, lineNumber, false,
                    columnName
            );
            throw new MZTabException(error);
        } else if (columnName.contains("abundance")) {
            offset = checkAbundanceColumns(offset);
        } else {
            MZTabError error = new MZTabError(
                    FormatErrorType.Optional, lineNumber, false,
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
        String abundanceStdErrorHeader = null;

        MZTabError error;

        try {
            abundanceHeader = items[offset++];
            abundanceStdevHeader = items[offset++];
            abundanceStdErrorHeader = items[offset];
        } catch (ArrayIndexOutOfBoundsException e) {
            error = new MZTabError(
                    FormatErrorType.Abundance, lineNumber, false,
                    abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader
            );
            throw new MZTabException(error);
        }

        if (! validAbundanceColumns(abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader)) {
            error = new MZTabError(
                    FormatErrorType.Abundance, lineNumber, false,
                    abundanceHeader, abundanceStdevHeader, abundanceStdErrorHeader
            );
            throw new MZTabException(error);
        }

        return offset;
    }

    private boolean validAbundanceSection(String sectionName) {
        Section currentSection = Section.findSection(sectionName);

        return currentSection != null &&
                !(currentSection == Section.Protein && section != Section.Protein_Header) &&
                !(currentSection == Section.Peptide && section != Section.Peptide_Header) &&
                !(currentSection == Section.Small_Molecule && section != Section.Small_Molecule_Header);
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

    private boolean validAbundanceColumns(String abundanceHeader, String abundanceStdevHeader, String abundanceStdErrorHeader) {
        Pattern pattern;
        Matcher matcher;

        String sectionName;
        SubUnit subUnit;
        Integer group_id;
        pattern = Pattern.compile("(protein|peptide|smallmolecule)_abundance_(sub\\[(\\d+)\\])");
        matcher = pattern.matcher(abundanceHeader);
        if (matcher.find()) {
            sectionName = matcher.group(1);
            if (! validAbundanceSection(sectionName)) {
                return false;
            }

            if (validAbundanceSubId(matcher.group(2)) == null) {
                return false;
            }

            group_id = new Integer(matcher.group(3));
        } else {
            return false;
        }

        pattern = Pattern.compile("(protein|peptide|smallmolecule)_abundance_stdev_(sub\\[(\\d+)\\])");
        matcher = pattern.matcher(abundanceStdevHeader);
        if (matcher.find()) {
            sectionName = matcher.group(1);
            if (! validAbundanceSection(sectionName)) {
                return false;
            }

            if (validAbundanceSubId(matcher.group(2)) == null) {
                return false;
            }

            // abundance_stdev should have same subid with abundance.
            if (! group_id.toString().equals(matcher.group(3))) {
                return false;
            }
        } else {
            return false;
        }

        pattern = Pattern.compile("(protein|peptide|smallmolecule)_abundance_std_error_(sub\\[(\\d+)\\])");
        matcher = pattern.matcher(abundanceStdErrorHeader);
        if (matcher.find()) {
            sectionName = matcher.group(1);
            if (! validAbundanceSection(sectionName)) {
                return false;
            }

            if ((subUnit = validAbundanceSubId(matcher.group(2))) == null) {
                return false;
            }

            // abundance_std_error should have same subid with abundance.
            if (! group_id.toString().equals(matcher.group(3))) {
                return false;
            }
        } else {
            return false;
        }

        factory.addAbundanceColumn(subUnit);

        return true;
    }

    public MZTabColumnFactory getFactory() {
        return factory;
    }
}
