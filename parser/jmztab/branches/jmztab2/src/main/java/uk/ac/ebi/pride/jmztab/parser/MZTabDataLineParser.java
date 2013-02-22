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
        String abundance = items[offset++];
        String abundance_stdev = items[offset++];
        String abundance_std_error = items[offset];

        checkDouble(abundance);
        checkDouble(abundance_stdev);
        checkDouble(abundance_std_error);

        return offset;
    }

    private void checkCVParamOptData(int offset) {
        String data = checkData(items[offset], true);

        MZTabColumn column = mapping.get(offset);
        String header = column.getHeader();

        if (header.contains("MS:1002217") && checkMZBoolean(data) == null) {
            new MZTabError(LogicalErrorType.CVParamOptionalColumn, lineNumber, header, "Boolean(0/1)", data);
        } else if (header.contains("MS:1001905") && checkDouble(data) == null) {
            new MZTabError(LogicalErrorType.CVParamOptionalColumn, lineNumber, header, "value-type:xsd:double", data);
        }

        // using web service to cross check cv param definition matches data type.
        if (MZTabConstants.CVPARAM_CHECK) {


        }
    }

    private void checkOptData(int offset) {
        checkData(items[offset], true);
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
    protected String checkData(String target, boolean allowNull) {
        if (target == null) {
            new MZTabError(LogicalErrorType.NULL, lineNumber, "");
            return null;
        }

        target = target.trim();
        if (target.isEmpty()) {
            new MZTabError(LogicalErrorType.NULL, lineNumber, target);
            return null;
        }

        if (target.equals(MZTabConstants.NULL)) {
            if (allowNull) {
                return MZTabConstants.NULL;
            } else {
                new MZTabError(LogicalErrorType.NotNULL, lineNumber, target);
            }
        }

        return target;
    }

    protected String checkInteger(String target) {
        String result = checkData(target, true);

        if (result == null ||
                result.equals(MZTabConstants.NULL)) {
            return result;
        }

        Integer value = parseInteger(result);
        if (value == null) {
            new MZTabError(FormatErrorType.Integer, lineNumber, target);
            return null;
        }

        return result;
    }

    protected String checkDouble(String target) {
        String result = checkData(target, true);

        if (result == null ||
                result.endsWith(MZTabConstants.NULL)||
                result.equals(MZTabConstants.CALCULATE_ERROR) ||
                result.equals(MZTabConstants.INFINITY)) {
            return result;
        }

        BigDecimal value = parseBigDecimal(result);
        if (value == null) {
            new MZTabError(FormatErrorType.Double, lineNumber, target);
            return null;
        }

        return result;
    }

    protected String checkParamList(String target) {
        String result = checkData(target, true);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        SplitList<Param> paramList = parseParamList(result);
        if (paramList.size() == 0) {
            new MZTabError(FormatErrorType.ParamList, lineNumber, target);
            return null;
        }

        return result;
    }

    protected String checkStringList(String target, char splitChar) {
        String result = checkData(target, true);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        SplitList<String> stringList = parseStringList(splitChar, result);
        if (stringList.size() == 0) {
            new MZTabError(FormatErrorType.StringList, lineNumber, result, "" + splitChar);
            return null;
        }

        return result;
    }

    protected String checkMZBoolean(String target) {
        String result = checkData(target, true);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        MZBoolean mzBoolean = MZBoolean.findBoolean(result);
        if (mzBoolean == null) {
            new MZTabError(FormatErrorType.MZBoolean, lineNumber, result);
            return null;
        }

        return result;
    }

    /**
     * unitId should not "null", and should be defined in the metadata.
     *
     * If check error return null, else return unitId String.
     */
    protected String checkUnitId(String unitId) {
        String result_unitId = checkData(unitId, false);

        if (result_unitId == null) {
            return result_unitId;
        }

        Unit unit = metadata.getUnit(result_unitId);
        if (unit == null) {
            new MZTabError(LogicalErrorType.UnitID, lineNumber, result_unitId);
            return null;
        }

        return result_unitId;
    }

    protected String checkDescription(String description) {
        return checkData(description, true);
    }

    protected String checkTaxid(String taxid) {
        return checkInteger(taxid);
    }

    protected String checkSpecies(String species) {
        return checkData(species, true);
    }

    protected String checkDatabase(String database) {
        return checkData(database, true);
    }

    protected String checkDatabaseVersion(String databaseVersion) {
        return checkData(databaseVersion, true);
    }

    protected String checkSearchEngine(String searchEngine) {
        return checkParamList(searchEngine);
    }

    protected String checkSearchEngineScore(String searchEngineScore) {
        return checkParamList(searchEngineScore);
    }

    protected String checkReliability(String reliability) {
        String result_reliaility = checkData(reliability, true);

        if (result_reliaility == null || reliability.equals(MZTabConstants.NULL)) {
            return result_reliaility;
        }

        Reliability result = Reliability.findReliability(result_reliaility);
        if (result == null) {
            new MZTabError(FormatErrorType.Reliability, lineNumber, result_reliaility);
            return null;
        }

        return result_reliaility;
    }

    protected String checkNumPeptides(String numPeptides) {
        return checkInteger(numPeptides);
    }

    protected String checkNumPeptidesDistinct(String numPeptidesDistinct) {
        return checkInteger(numPeptidesDistinct);
    }

    protected String checkNumPeptidesUnambiguous(String numPeptidesUnambiguous) {
        return checkInteger(numPeptidesUnambiguous);
    }

    protected String checkAmbiguityMembers(String ambiguityMembers) {
        return checkStringList(ambiguityMembers, MZTabConstants.COMMA);
    }

    /**
     * protein, peptide, small_molecule have different check strategy.
     * need overwrite!
     */
    protected String checkModifications(String modifications) {
        String result_modifications = checkData(modifications, true);

        if (result_modifications == null || result_modifications.equals(MZTabConstants.NULL)) {
            return result_modifications;
        }

        return result_modifications;
    }

    protected String checkURI(String uri) {
        String result_uri = checkData(uri, true);

        if (result_uri == null || result_uri.equals(MZTabConstants.NULL)) {
            return result_uri;
        }

        java.net.URI result = parseURI(result_uri);
        if (result == null) {
            new MZTabError(FormatErrorType.URI, lineNumber, result_uri);
            return null;
        }

        return result_uri;
    }

    protected String checkSpectraRef(String unitId, String spectraRef) {
        if (unitId == null) {
            return null;
        }

        String result_spectraRef = checkData(spectraRef, true);

        if (result_spectraRef == null || result_spectraRef.equals(MZTabConstants.NULL)) {
            return result_spectraRef;
        }

        Unit unit = metadata.getUnit(unitId);

        List<SpecRef> refList = parseSepcRefList(unit, result_spectraRef);
        if (refList.size() == 0) {
            new MZTabError(FormatErrorType.SpectraRef, lineNumber, result_spectraRef);
            return null;
        }

        return result_spectraRef;
    }

    protected String checkGOTerms(String go_terms) {
        String result_go_terms = checkData(go_terms, true);

        if (result_go_terms == null || result_go_terms.equals(MZTabConstants.NULL)) {
            return result_go_terms;
        }


        SplitList<String> stringList = parseGOTermList(result_go_terms);
        if (stringList.size() == 0) {
            new MZTabError(FormatErrorType.GOTermList, lineNumber, result_go_terms);
            return null;
        }

        return result_go_terms;
    }

    protected String checkProteinCoverage(String protein_coverage) {
        String result = checkDouble(protein_coverage);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        BigDecimal value = new BigDecimal(result);
        if (value.compareTo(new BigDecimal(0)) < 0 || value.compareTo(new BigDecimal(1)) > 0) {
            new MZTabError(LogicalErrorType.ProteinCoverage, lineNumber, result);
            return null;
        }

        return result;
    }

    protected String checkSequence(String sequence) {
        return checkData(sequence, true);
    }

    protected String checkUnique(String unique) {
        return checkMZBoolean(unique);
    }

    protected String checkCharge(String charge) {
        return checkInteger(charge);
    }

    protected String checkMassToCharge(String mass_to_charge) {
        return checkDouble(mass_to_charge);
    }

    protected String checkIdentifier(String identifier) {
        return checkStringList(identifier, MZTabConstants.BAR);
    }

    protected String checkChemicalFormula(String chemical_formula) {
        return checkData(chemical_formula, true);
    }

    protected String checkSmiles(String smiles) {
        return checkStringList(smiles, MZTabConstants.BAR);
    }

    protected String checkInchiKey(String inchi_key) {
        return checkStringList(inchi_key, MZTabConstants.BAR);
    }

    protected String checkRetentionTime(String retention_time) {
        String result = checkData(retention_time, true);

        if (result == null || result.equals(MZTabConstants.NULL)) {
            return result;
        }

        SplitList<BigDecimal> valueList = parseBigDecimalList(result);
        if (valueList.size() == 0) {
            new MZTabError(FormatErrorType.DoubleList, lineNumber, result, "" + MZTabConstants.BAR);
            return null;
        }

        return result;
    }
}
