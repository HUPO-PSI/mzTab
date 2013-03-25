package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.*;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.MZTabProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * For data line validation, not raise MZTabException, just record error/warn message
 * into errorLines.
 *
 * User: Qingwei
 * Date: 14/02/13
 */
public abstract class MZTabDataLineParser extends MZTabLineParser {
    private MZTabColumnFactory factory;
    protected MZTabErrorList errorList;

    protected SortedMap<Integer, MZTabColumn> mapping;
    protected Metadata metadata;

    protected MZTabDataLineParser(MZTabColumnFactory factory, Metadata metadata, MZTabErrorList errorList) {
        this.factory = factory;
        this.mapping = factory.getColumnMapping();

        if (metadata == null) {
            throw new NullPointerException("Metadata should be parser first.");
        }
        this.metadata = metadata;
        this.errorList = errorList;
    }

    public void check(int lineNumber, String line) throws MZTabException {
        super.check(lineNumber, line);
        checkCount();

        int offset = checkStableData();
        if (offset == items.length - 1) {
            // no optional data.
            return;
        }

        offset++;
        checkOptionalData(offset);
    }

    protected AbstractMZTabRecord getRecord(Section section, String line) {
        AbstractMZTabRecord record = null;

        switch (section) {
            case Protein:
                record = new Protein(factory);
                break;
            case Peptide:
                record = new Peptide(factory);
                break;
            case Small_Molecule:
                record = new SmallMolecule(factory);
                break;
        }

        int offset = loadStableData(record, line);
        if (offset == items.length - 1) {
            return record;
        }

        offset++;
        loadOptionalData(record, offset);

        return record;
    }

    /**
     * Based on mapping order to check stable column data.
     * @return the last stable column position.
     */
    abstract int checkStableData();

    abstract int loadStableData(AbstractMZTabRecord record, String line);

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

    private void loadOptionalData(AbstractMZTabRecord record, int offset) {
        MZTabColumn column;

        column = mapping.get(offset);
        if (column.getHeader().contains("abundance")) {
            offset = loadAbundanceData(record, offset);
        } else if (column.getHeader().startsWith("opt_cv")) {
            loadCVParamOptData(record, offset);
        } else if (column.getHeader().startsWith("opt_")) {
            loadOptData(record, offset);
        }

        if (offset < items.length - 1) {
            offset++;
            loadOptionalData(record, offset);
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

    private int loadAbundanceData(AbstractMZTabRecord record, int offset) {
        record.addValue(offset, parseDouble(items[offset++]));
        record.addValue(offset, parseDouble(items[offset++]));
        record.addValue(offset, parseDouble(items[offset]));

        return offset;
    }

    private void checkCVParamOptData(int offset) {
        MZTabColumn column = mapping.get(offset);
        String data = checkData(column, items[offset], true);
        String header = column.getHeader();

        if (header.contains("MS:1002217") && checkMZBoolean(column, data) == null) {
            this.errorList.add(new MZTabError(LogicalErrorType.CVParamOptionalColumn, lineNumber, column.getHeader(), "Boolean(0/1)", data));
        } else if (header.contains("MS:1001905") && checkDouble(column, data) == null) {
            this.errorList.add(new MZTabError(LogicalErrorType.CVParamOptionalColumn, lineNumber, column.getHeader(), "value-type:xsd:double", data));
        }

        // using web service to cross check cv param definition matches data type.
        if (MZTabProperties.CVPARAM_CHECK) {

        }
    }

    private void loadCVParamOptData(AbstractMZTabRecord record, int offset) {
        MZTabColumn column = mapping.get(offset);
        String data = checkData(column, items[offset], true);
        String header = column.getHeader();

        if (header.contains("MS:1002217")) {
            record.addValue(offset, MZBoolean.findBoolean(data));
        } else if (header.contains("MS:1001905")) {
            record.addValue(offset, parseDouble(data));
        } else {
            record.addValue(offset, data);
        }
    }

    private void checkOptData(int offset) {
        checkData(mapping.get(offset), items[offset], true);
    }

    private void loadOptData(AbstractMZTabRecord record, int offset) {
        record.addValue(offset, items[offset]);
    }

    private void checkCount() {
        int headerCount = mapping.size();
        int dataCount = items.length - 1;

        if (headerCount != dataCount) {
            this.errorList.add(new MZTabError(FormatErrorType.CountMatch, lineNumber, "" + dataCount, "" + headerCount));
        }
    }

    /**
     * In the table-based sections (protein, peptide, and small molecule) there MUST NOT be any empty cells.
     * Some field not allow "null" value, for example unit_id, accession and so on.
     */
    protected String checkData(MZTabColumn column, String target, boolean allowNull) {
        if (target == null) {
            this.errorList.add(new MZTabError(LogicalErrorType.NULL, lineNumber, column.getHeader()));
            return null;
        }

        target = target.trim();
        if (target.isEmpty()) {
            this.errorList.add(new MZTabError(LogicalErrorType.NULL, lineNumber, column.getHeader()));
            return null;
        }

        if (target.equals(NULL)) {
            if (allowNull) {
                return NULL;
            } else {
                this.errorList.add(new MZTabError(LogicalErrorType.NotNULL, lineNumber, column.getHeader(), target));
            }
        }

        return target;
    }

    protected Integer checkInteger(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equals(NULL)) {
            return null;
        }

        Integer value = parseInteger(result);
        if (value == null) {
            this.errorList.add(new MZTabError(FormatErrorType.Integer, lineNumber, column.getHeader(), target));
        }

        return value;
    }

    protected Double checkDouble(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equals(NULL)) {
            return null;
        }

        Double value = parseDouble(result);
        if (value == null) {
            this.errorList.add(new MZTabError(FormatErrorType.Double, lineNumber, column.getHeader(), target));
            return null;
        }

        return value;
    }

    protected SplitList<Param> checkParamList(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equals(NULL)) {
            return new SplitList<Param>(BAR);
        }

        SplitList<Param> paramList = parseParamList(result);
        if (paramList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.ParamList, lineNumber, column.getHeader(), target));
            return null;
        }

        return paramList;
    }

    protected SplitList<String> checkStringList(MZTabColumn column, String target, char splitChar) {
        String result = checkData(column, target, true);

        if (result == null || result.equals(NULL)) {
            return new SplitList<String>(splitChar);
        }

        SplitList<String> stringList = parseStringList(splitChar, result);
        if (stringList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.StringList, lineNumber, column.getHeader(), result, "" + splitChar));
        }

        return stringList;
    }

    protected MZBoolean checkMZBoolean(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equals(NULL)) {
            return null;
        }

        MZBoolean value = MZBoolean.findBoolean(result);
        if (value == null) {
            this.errorList.add(new MZTabError(FormatErrorType.MZBoolean, lineNumber, column.getHeader(), result));
        }

        return value;
    }

    /**
     * unitId should not "null", and should be defined in the metadata.
     *
     * If check error return null, else return unitId String.
     */
    protected Unit checkUnitId(MZTabColumn column, String unitId) {
        String result_unitId = checkData(column, unitId, false);

        if (result_unitId == null) {
            return null;
        }

        Unit unit = metadata.getUnit(result_unitId);
        if (unit == null) {
            this.errorList.add(new MZTabError(LogicalErrorType.UnitID, lineNumber, column.getHeader(), result_unitId));
        }

        return unit;
    }

    protected String checkDescription(MZTabColumn column, String description) {
        return checkData(column, description, true);
    }

    protected Integer checkTaxid(MZTabColumn column, String taxid) {
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
            if (! (param instanceof CVParam) || (isEmpty(param.getValue()))) {
                this.errorList.add(new MZTabError(FormatErrorType.SearchEngineScore, lineNumber, column.getHeader(), searchEngineScore, section.getName()));
            }


        }

        return paramList;
    }

    protected Reliability checkReliability(MZTabColumn column, String reliability) {
        String result_reliaility = checkData(column, reliability, true);

        if (result_reliaility == null || result_reliaility.equals(NULL)) {
            return null;
        }

        Reliability result = Reliability.findReliability(result_reliaility);
        if (result == null) {
            this.errorList.add(new MZTabError(FormatErrorType.Reliability, lineNumber, column.getHeader(), result_reliaility));
        }

        return result;
    }

    protected Integer checkNumPeptides(MZTabColumn column, String numPeptides) {
        return checkInteger(column, numPeptides);
    }

    protected Integer checkNumPeptidesDistinct(MZTabColumn column, String numPeptidesDistinct) {
        return checkInteger(column, numPeptidesDistinct);
    }

    protected Integer checkNumPeptidesUnambiguous(MZTabColumn column, String numPeptidesUnambiguous) {
        return checkInteger(column, numPeptidesUnambiguous);
    }

    protected SplitList<String> checkAmbiguityMembers(MZTabColumn column, String ambiguityMembers) {
        return checkStringList(column, ambiguityMembers, COMMA);
    }

    /**
     * protein, peptide, small_molecule have different check strategy.
     * need overwrite!
     */
    protected SplitList<Modification> checkModifications(Section section, MZTabColumn column, String target) {
        String result_modifications = checkData(column, target, true);

        if (result_modifications == null || result_modifications.equals(NULL)) {
            return new SplitList<Modification>(COMMA);
        }

        SplitList<Modification> modificationList = parseModificationList(section, target);
        if (modificationList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.ModificationList, lineNumber, column.getHeader(), result_modifications));
        }

        return modificationList;
    }

    protected java.net.URI checkURI(MZTabColumn column, String uri) {
        String result_uri = checkData(column, uri, true);

        if (result_uri == null || result_uri.equals(NULL)) {
            return null;
        }

        java.net.URI result = parseURI(result_uri);
        if (result == null) {
            this.errorList.add(new MZTabError(FormatErrorType.URI, lineNumber, column.getHeader(), result_uri));
        }

        return result;
    }

    protected List<SpecRef> checkSpectraRef(MZTabColumn column, Unit unit, String spectraRef) {
        String result_spectraRef = checkData(column, spectraRef, true);

        if (result_spectraRef == null || result_spectraRef.equals(NULL)) {
            return new ArrayList<SpecRef>();
        }

        List<SpecRef> refList = parseSpecRefList(unit, result_spectraRef);
        if (refList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.SpectraRef, lineNumber, column.getHeader(), result_spectraRef));
        }

        return refList;
    }

    protected SplitList<String> checkGOTerms(MZTabColumn column, String go_terms) {
        String result_go_terms = checkData(column, go_terms, true);

        if (result_go_terms == null || result_go_terms.equals(NULL)) {
            return new SplitList<String>(COMMA);
        }


        SplitList<String> stringList = parseGOTermList(result_go_terms);
        if (stringList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.GOTermList, lineNumber, column.getHeader(), result_go_terms));
        }

        return stringList;
    }

    protected Double checkProteinCoverage(MZTabColumn column, String protein_coverage) {
        Double result = checkDouble(column, protein_coverage);

        if (result == null) {
            return result;
        }

        if (result < 0 || result > 1) {
            this.errorList.add(new MZTabError(LogicalErrorType.ProteinCoverage, lineNumber, column.getHeader(), printDouble(result)));
            return null;
        }

        return result;
    }

    protected String checkSequence(MZTabColumn column, String sequence) {
        return checkData(column, sequence, true);
    }

    protected MZBoolean checkUnique(MZTabColumn column, String unique) {
        return checkMZBoolean(column, unique);
    }

    protected Integer checkCharge(MZTabColumn column, String charge) {
        return checkInteger(column, charge);
    }

    protected Double checkMassToCharge(MZTabColumn column, String mass_to_charge) {
        return checkDouble(column, mass_to_charge);
    }

    protected SplitList<String> checkIdentifier(MZTabColumn column, String identifier) {
        return checkStringList(column, identifier, BAR);
    }

    protected String checkChemicalFormula(MZTabColumn column, String chemical_formula) {
        return checkData(column, chemical_formula, true);
    }

    protected SplitList<String> checkSmiles(MZTabColumn column, String smiles) {
        return checkStringList(column, smiles, BAR);
    }

    protected SplitList<String> checkInchiKey(MZTabColumn column, String inchi_key) {
        return checkStringList(column, inchi_key, BAR);
    }

    protected SplitList<Double> checkRetentionTime(MZTabColumn column, String retention_time) {
        String result = checkData(column, retention_time, true);

        if (result == null || result.equals(NULL)) {
            return new SplitList<Double>(BAR);
        }

        SplitList<Double> valueList = parseDoubleList(result);
        if (valueList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.DoubleList, lineNumber, column.getHeader(), result, "" + BAR));
        }

        return valueList;
    }
}
