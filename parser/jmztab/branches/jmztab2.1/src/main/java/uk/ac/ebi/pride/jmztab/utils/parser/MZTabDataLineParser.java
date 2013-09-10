package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

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
        this.mapping = factory.getOffsetColumnsMap();

        if (metadata == null) {
            throw new NullPointerException("Metadata should be parser first.");
        }
        this.metadata = metadata;
        this.errorList = errorList;
    }

    public void parse(int lineNumber, String line) throws MZTabException {
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

    private void checkCount() {
        int headerCount = mapping.size();
        int dataCount = items.length - 1;

        if (headerCount != dataCount) {
            this.errorList.add(new MZTabError(FormatErrorType.CountMatch, lineNumber, "" + dataCount, "" + headerCount));
        }
    }

    protected MZTabRecord getRecord(Section section, String line) {
        MZTabRecord record = null;

        switch (section) {
            case Protein:
                record = new Protein(factory);
                break;
            case Peptide:
                record = new Peptide(factory, metadata);
                break;
            case PSM:
                record = new PSM(factory, metadata);
                break;
            case Small_Molecule:
                record = new SmallMolecule(factory, metadata);
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
     * Based on mapping order to parse stable column data.
     * @return the last stable column position.
     */
    abstract int checkStableData();

    abstract int loadStableData(MZTabRecord record, String line);

    private void checkOptionalData(int offset) {
        String target;
        MZTabColumn column;
        Class dataType;
        for (int i = offset; i < items.length; i++) {
            target = items[i];
            column = mapping.get(i);
            dataType = column.getDataType();

            if (dataType.equals(String.class)) {
                checkString(column, target);
            } else if (dataType.equals(Double.class)) {
                checkDouble(column, target);
            } else if (dataType.equals(MZBoolean.class)) {
                checkMZBoolean(column, target);
            }
        }
    }

    private void loadOptionalData(MZTabRecord record, int offset) {
        String target;
        MZTabColumn column;
        Class dataType;
        for (int i = offset; i < items.length; i++) {
            target = items[i];
            column = mapping.get(i);
            dataType = column.getDataType();

            if (dataType.equals(String.class)) {
                record.setValue(column.getPosition(), checkString(column, target));
            } else if (dataType.equals(Double.class)) {
                record.setValue(column.getPosition(), checkDouble(column, target));
            } else if (dataType.equals(MZBoolean.class)) {
                record.setValue(column.getPosition(), checkMZBoolean(column, target));
            }
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

    protected String checkString(MZTabColumn column, String target) {
        return checkData(column, target, true);
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

    protected SplitList<Param> checkBestSearchEngineScore(MZTabColumn column, String bestSearchEngineScore) {
        SplitList<Param> paramList = checkParamList(column, bestSearchEngineScore);

        for (Param param : paramList) {
            if (! (param instanceof CVParam) || (isEmpty(param.getValue()))) {
                this.errorList.add(new MZTabError(FormatErrorType.SearchEngineScore, lineNumber, column.getHeader(), bestSearchEngineScore, section.getName()));
            }
        }

        return paramList;
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

    protected Integer checkNumPSMs(MZTabColumn column, String numPSMs) {
        return checkInteger(column, numPSMs);
    }

    protected Integer checkNumPeptidesDistinct(MZTabColumn column, String numPeptidesDistinct) {
        return checkInteger(column, numPeptidesDistinct);
    }

    protected Integer checkNumPeptidesUnique(MZTabColumn column, String numPeptidesUnique) {
        return checkInteger(column, numPeptidesUnique);
    }

    protected SplitList<String> checkAmbiguityMembers(MZTabColumn column, String ambiguityMembers) {
        return checkStringList(column, ambiguityMembers, COMMA);
    }

    /**
     * protein, peptide, small_molecule have different parse strategy.
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

    protected List<SpectraRef> checkSpectraRef(MZTabColumn column, String spectraRef) {
        String result_spectraRef = checkData(column, spectraRef, true);

        if (result_spectraRef == null || result_spectraRef.equals(NULL)) {
            return new ArrayList<SpectraRef>();
        }

        List<SpectraRef> refList = parseSpectraRefList(metadata, result_spectraRef);
        if (refList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.SpectraRef, lineNumber, column.getHeader(), result_spectraRef));
        } else {
            for (SpectraRef ref : refList) {
                if (ref.getMsRun() == null || ref.getMsRun().getLocation() == null) {
                    this.errorList.add(new MZTabError(LogicalErrorType.SpectraRef, lineNumber, column.getHeader(), result_spectraRef));
                }
            }
        }

        return refList;
    }

    protected String checkPre(MZTabColumn column, String pre) {
        return checkData(column, pre, true);
    }

    protected String checkPost(MZTabColumn column, String post) {
        return checkData(column, post, true);
    }

    protected String checkStart(MZTabColumn column, String start) {
        return checkData(column, start, true);
    }

    protected String checkEnd(MZTabColumn column, String end) {
        return checkData(column, end, true);
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

    protected Integer checkPSMID(MZTabColumn column, String psm_id) {
        return checkInteger(column, psm_id);
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

    protected Double checkExpMassToCharge(MZTabColumn column, String exp_mass_to_charge) {
        return checkDouble(column, exp_mass_to_charge);
    }

    protected Double checkCalcMassToCharge(MZTabColumn column, String calc_mass_to_charge) {
        return checkDouble(column, calc_mass_to_charge);
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

    protected SplitList<Double> checkRetentionTimeWindow(MZTabColumn column, String retention_time_window) {
        String result = checkData(column, retention_time_window, true);

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
