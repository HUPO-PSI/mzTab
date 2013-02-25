package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.math.BigDecimal;
import java.util.*;

import static uk.ac.ebi.pride.jmztab.parser.MZTabParserUtils.*;

/**
 * For data line validation, not raise MZTabException, just record error/warn message
 * into errorLines.
 *
 * User: Qingwei
 * Date: 14/02/13
 */
public abstract class MZTabDataLineParser extends MZTabLineParser {
    protected SortedMap<Integer, MZTabColumn> mapping;
    protected Metadata metadata;

    protected MZTabDataLineParser(MZTabColumnFactory factory, Metadata metadata) {
        this.mapping = factory.getColumnMapping();

        if (metadata == null) {
            throw new NullPointerException("Metadata should be parser first.");
        }
        this.metadata = metadata;
    }

    protected void parse(int lineNumber, String line) throws MZTabException {
        super.parse(lineNumber, line);
        checkCount();

        int offset = checkStableData();
        if (offset == items.length - 1) {
            // no optional data.
            return;
        }

        offset++;
        checkOptionalData(offset);
    }

    /**
     * Based on mapping order to check stable column data.
     * @return the last stable column position.
     */
    abstract int checkStableData();

    private void checkOptionalData(int offset) {
        MZTabColumn column;

        column = mapping.get(offset);
        if (column.getHeader().contains("abundance")) {
            offset = checkAbundanceColumns(offset);
        } else if (column.getHeader().startsWith("opt_cv")) {
            checkCVParamOptData(offset);
        } else if (column.getHeader().startsWith("opt_")) {
            checkOptData(offset);
        }

        if (offset < items.length - 1) {
            offset++;
            checkOptionalData(offset);
        }
    }

    private int checkAbundanceColumns(int offset) {
        String abundance = items[offset];
        MZTabColumn abundance_column = mapping.get(offset++);
        String abundance_stdev = items[offset];
        MZTabColumn abundance_stdev_column = mapping.get(offset++);
        String abundance_std_error = items[offset];
        MZTabColumn abundance_std_error_column = mapping.get(offset);

        checkDouble(abundance_column, abundance);
        checkDouble(abundance_stdev_column, abundance_stdev);
        checkDouble(abundance_std_error_column, abundance_std_error);

        return offset;
    }

    private void checkCVParamOptData(int offset) {
        MZTabColumn column = mapping.get(offset);
        String data = checkData(column, items[offset], true);
        String header = column.getHeader();

        if (header.contains("MS:1002217") && checkMZBoolean(column, data) == null) {
            new MZTabError(LogicalErrorType.CVParamOptionalColumn, lineNumber, column.getHeader(), "Boolean(0/1)", data);
        } else if (header.contains("MS:1001905") && checkDouble(column, data) == null) {
            new MZTabError(LogicalErrorType.CVParamOptionalColumn, lineNumber, column.getHeader(), "value-type:xsd:double", data);
        }

        // using web service to cross check cv param definition matches data type.
        if (MZTabConstants.CVPARAM_CHECK) {


        }
    }

    private void checkOptData(int offset) {
        checkData(mapping.get(offset), items[offset], true);
    }

    private void checkCount() {
        int headerCount = mapping.size();
        int dataCount = items.length - 1;

        if (headerCount != dataCount) {
            new MZTabError(FormatErrorType.CountMatch, lineNumber, "" + dataCount, "" + headerCount);
        }
    }

    /**
     * In the table-based sections (protein, peptide, and small molecule) there MUST NOT be any empty cells.
     * Some field not allow "null" value, for example unit_id, accession and so on.
     */
    protected String checkData(MZTabColumn column, String target, boolean allowNull) {
        if (target == null) {
            new MZTabError(LogicalErrorType.NULL, lineNumber, column.getHeader());
            return null;
        }

        target = target.trim();
        if (target.isEmpty()) {
            new MZTabError(LogicalErrorType.NULL, lineNumber, column.getHeader());
            return null;
        }

        if (target.equals(MZTabConstants.NULL)) {
            if (allowNull) {
                return MZTabConstants.NULL;
            } else {
                new MZTabError(LogicalErrorType.NotNULL, lineNumber, column.getHeader(), target);
            }
        }

        return target;
    }

    protected String checkInteger(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null ||
                result.equals(MZTabConstants.NULL)) {
            return result;
        }

        Integer value = parseInteger(result);
        if (value == null) {
            new MZTabError(FormatErrorType.Integer, lineNumber, column.getHeader(), target);
            return null;
        }

        return result;
    }

    protected String checkDouble(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null ||
                result.endsWith(MZTabConstants.NULL)||
                result.equals(MZTabConstants.CALCULATE_ERROR) ||
                result.equals(MZTabConstants.INFINITY)) {
            return result;
        }

        BigDecimal value = parseBigDecimal(result);
        if (value == null) {
            new MZTabError(FormatErrorType.Double, lineNumber, column.getHeader(), target);
            return null;
        }

        return result;
    }

    protected SplitList<Param> checkParamList(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return new SplitList<Param>(MZTabConstants.BAR);
        }

        SplitList<Param> paramList = parseParamList(result);
        if (paramList.size() == 0) {
            new MZTabError(FormatErrorType.ParamList, lineNumber, column.getHeader(), target);
            return null;
        }

        return paramList;
    }

    protected String checkStringList(MZTabColumn column, String target, char splitChar) {
        String result = checkData(column, target, true);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        SplitList<String> stringList = parseStringList(splitChar, result);
        if (stringList.size() == 0) {
            new MZTabError(FormatErrorType.StringList, lineNumber, column.getHeader(), result, "" + splitChar);
            return null;
        }

        return result;
    }

    protected String checkMZBoolean(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        MZBoolean mzBoolean = MZBoolean.findBoolean(result);
        if (mzBoolean == null) {
            new MZTabError(FormatErrorType.MZBoolean, lineNumber, column.getHeader(), result);
            return null;
        }

        return result;
    }

    /**
     * unitId should not "null", and should be defined in the metadata.
     *
     * If check error return null, else return unitId String.
     */
    protected String checkUnitId(MZTabColumn column, String unitId) {
        String result_unitId = checkData(column, unitId, false);

        if (result_unitId == null) {
            return result_unitId;
        }

        Unit unit = metadata.getUnit(result_unitId);
        if (unit == null) {
            new MZTabError(LogicalErrorType.UnitID, lineNumber, column.getHeader(), result_unitId);
            return null;
        }

        return result_unitId;
    }

    protected String checkDescription(MZTabColumn column, String description) {
        return checkData(column, description, true);
    }

    protected String checkTaxid(MZTabColumn column, String taxid) {
        return checkInteger(column, taxid);
    }

    protected String checkSpecies(MZTabColumn column, String species) {
        return checkData(column, species, true);
    }

    protected String checkDatabase(MZTabColumn column, String database) {
        return checkData(column, database, true);
    }

    protected String checkDatabaseVersion(MZTabColumn column, String databaseVersion) {
        return checkData(column, databaseVersion, true);
    }

    protected SplitList<Param> checkSearchEngine(MZTabColumn column, String searchEngine) {
        return checkParamList(column, searchEngine);
    }

    protected SplitList<Param> checkSearchEngineScore(MZTabColumn column, String searchEngineScore) {
        SplitList<Param> paramList = checkParamList(column, searchEngineScore);

        for (Param param : paramList) {
            if (! (param instanceof CVParam)) {
                new MZTabError(FormatErrorType.SearchEngineScore, lineNumber, column.getHeader(), searchEngineScore, section.getName());
                paramList.clear();
                return paramList;
            }
        }

        return paramList;
    }

    protected String checkReliability(MZTabColumn column, String reliability) {
        String result_reliaility = checkData(column, reliability, true);

        if (result_reliaility == null || reliability.equals(MZTabConstants.NULL)) {
            return result_reliaility;
        }

        Reliability result = Reliability.findReliability(result_reliaility);
        if (result == null) {
            new MZTabError(FormatErrorType.Reliability, lineNumber, column.getHeader(), result_reliaility);
            return null;
        }

        return result_reliaility;
    }

    protected String checkNumPeptides(MZTabColumn column, String numPeptides) {
        return checkInteger(column, numPeptides);
    }

    protected String checkNumPeptidesDistinct(MZTabColumn column, String numPeptidesDistinct) {
        return checkInteger(column, numPeptidesDistinct);
    }

    protected String checkNumPeptidesUnambiguous(MZTabColumn column, String numPeptidesUnambiguous) {
        return checkInteger(column, numPeptidesUnambiguous);
    }

    protected String checkAmbiguityMembers(MZTabColumn column, String ambiguityMembers) {
        return checkStringList(column, ambiguityMembers, MZTabConstants.COMMA);
    }

    /**
     * protein, peptide, small_molecule have different check strategy.
     * need overwrite!
     */
    protected String checkModifications(MZTabColumn column, String modifications) {
        String result_modifications = checkData(column, modifications, true);

        if (result_modifications == null || result_modifications.equals(MZTabConstants.NULL)) {
            return result_modifications;
        }

        return result_modifications;
    }

    protected String checkURI(MZTabColumn column, String uri) {
        String result_uri = checkData(column, uri, true);

        if (result_uri == null || result_uri.equals(MZTabConstants.NULL)) {
            return result_uri;
        }

        java.net.URI result = parseURI(result_uri);
        if (result == null) {
            new MZTabError(FormatErrorType.URI, lineNumber, column.getHeader(), result_uri);
            return null;
        }

        return result_uri;
    }

    protected String checkSpectraRef(MZTabColumn column, String unitId, String spectraRef) {
        if (unitId == null) {
            return null;
        }

        String result_spectraRef = checkData(column, spectraRef, true);

        if (result_spectraRef == null || result_spectraRef.equals(MZTabConstants.NULL)) {
            return result_spectraRef;
        }

        Unit unit = metadata.getUnit(unitId);

        List<SpecRef> refList = parseSepcRefList(unit, result_spectraRef);
        if (refList.size() == 0) {
            new MZTabError(FormatErrorType.SpectraRef, lineNumber, column.getHeader(), result_spectraRef);
            return null;
        }

        return result_spectraRef;
    }

    protected String checkGOTerms(MZTabColumn column, String go_terms) {
        String result_go_terms = checkData(column, go_terms, true);

        if (result_go_terms == null || result_go_terms.equals(MZTabConstants.NULL)) {
            return result_go_terms;
        }


        SplitList<String> stringList = parseGOTermList(result_go_terms);
        if (stringList.size() == 0) {
            new MZTabError(FormatErrorType.GOTermList, lineNumber, column.getHeader(), result_go_terms);
            return null;
        }

        return result_go_terms;
    }

    protected String checkProteinCoverage(MZTabColumn column, String protein_coverage) {
        String result = checkDouble(column, protein_coverage);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        BigDecimal value = new BigDecimal(result);
        if (value.compareTo(new BigDecimal(0)) < 0 || value.compareTo(new BigDecimal(1)) > 0) {
            new MZTabError(LogicalErrorType.ProteinCoverage, lineNumber, column.getHeader(), result);
            return null;
        }

        return result;
    }

    protected String checkSequence(MZTabColumn column, String sequence) {
        return checkData(column, sequence, true);
    }

    protected String checkUnique(MZTabColumn column, String unique) {
        return checkMZBoolean(column, unique);
    }

    protected String checkCharge(MZTabColumn column, String charge) {
        return checkInteger(column, charge);
    }

    protected String checkMassToCharge(MZTabColumn column, String mass_to_charge) {
        return checkDouble(column, mass_to_charge);
    }

    protected String checkIdentifier(MZTabColumn column, String identifier) {
        return checkStringList(column, identifier, MZTabConstants.BAR);
    }

    protected String checkChemicalFormula(MZTabColumn column, String chemical_formula) {
        return checkData(column, chemical_formula, true);
    }

    protected String checkSmiles(MZTabColumn column, String smiles) {
        return checkStringList(column, smiles, MZTabConstants.BAR);
    }

    protected String checkInchiKey(MZTabColumn column, String inchi_key) {
        return checkStringList(column, inchi_key, MZTabConstants.BAR);
    }

    protected String checkRetentionTime(MZTabColumn column, String retention_time) {
        String result = checkData(column, retention_time, true);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        SplitList<BigDecimal> valueList = parseBigDecimalList(result);
        if (valueList.size() == 0) {
            new MZTabError(FormatErrorType.DoubleList, lineNumber, column.getHeader(), result, "" + MZTabConstants.BAR);
            return null;
        }

        return result;
    }
}
