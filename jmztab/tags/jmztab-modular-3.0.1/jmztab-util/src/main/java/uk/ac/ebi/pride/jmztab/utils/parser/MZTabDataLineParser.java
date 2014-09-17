package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * A couple of common method used to parse a data line into {@link MZTabRecord} structure. There are two step
 * in data line parser process: step 1: data validate, these methods name start with "checkXXX()" focus on this.
 * step 2: after validate, we fill the cell data into {@link MZTabRecord}, and use "loadXXX()" to generate the
 * concrete record.
 *
 * NOTICE: {@link MZTabColumnFactory} maintain a couple of {@link MZTabColumn} which have internal logical
 * position and order. In physical mzTab file, we allow user not obey this logical position organized way,
 * and provide their date with own order. In order to distinguish them, we use physical position (a positive
 * integer) to record the column location in mzTab file. And use {@link PositionMapping} structure the maintain
 * the mapping between them.
 *
 * @see PRTLineParser
 * @see PEPLineParser
 * @see PSMLineParser
 * @see SMLLineParser
 *
 * @author qingwei
 * @since 14/02/13
 */
public abstract class MZTabDataLineParser extends MZTabLineParser {
    protected MZTabColumnFactory factory;
    protected PositionMapping positionMapping;
    protected SortedMap<String, Integer> exchangeMapping; // reverse the key and value of positionMapping.

    protected SortedMap<Integer, MZTabColumn> mapping;   // logical position --> offset
    protected Metadata metadata;

    /**
     * Generate a mzTab data line parser. A couple of common method used to parse a data line into
     * {@link MZTabRecord} structure.
     *
     * NOTICE: {@link MZTabColumnFactory} maintain a couple of {@link MZTabColumn} which have internal logical
     * position and order. In physical mzTab file, we allow user not obey this logical position organized way,
     * and provide their date with own order. In order to distinguish them, we use physical position (a positive
     * integer) to record the column location in mzTab file. And use {@link PositionMapping} structure the maintain
     * the mapping between them.
     *
     * @param factory SHOULD NOT set null
     * @param positionMapping SHOULD NOT set null
     * @param metadata SHOULD NOT set null
     */
    protected MZTabDataLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
                                  Metadata metadata, MZTabErrorList errorList) {
        if (factory == null) {
            throw new NullPointerException("Column header factory should be create first.");
        }
        this.factory = factory;

        this.positionMapping = positionMapping;
        exchangeMapping = positionMapping.exchange();
        this.mapping = factory.getOffsetColumnsMap();

        if (metadata == null) {
            throw new NullPointerException("Metadata should be parser first.");
        }
        this.metadata = metadata;
        this.errorList = errorList == null ? new MZTabErrorList() : errorList;
    }

    /**
     * Validate the data line, if there exist errors, add them into {@link MZTabErrorList}.
     *
     * NOTICE: this step just do validate, not do convert operation. Convert the data line into
     * {@link MZTabRecord} implemented by {@link #getRecord(Section, String)}
     * method.
     */
    public void parse(int lineNumber, String line, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, line, errorList);
        checkCount();
        checkStableData();
        checkOptionalData();
    }

    /**
     * Check header line items size equals data line items size.
     * The number of Data line items does not match with the number of Header line items. Normally,
     * the user has not used the Unicode Horizontal Tab character (Unicode codepoint 0009) as the
     * column delimiter, there is a file encoding error, or the user has not provided the definition
     * of optional columns in the header line.
     */
    private void checkCount() {
        int headerCount = mapping.size();
        int dataCount = items.length - 1;

        if (headerCount != dataCount) {
            this.errorList.add(new MZTabError(FormatErrorType.CountMatch, lineNumber, "" + dataCount, "" + headerCount));
        }
    }

    /**
     * Translate the data line to a {@link MZTabRecord}.
     *
     * NOTICE: Normally, we suggest user do convert operation after validate successfully.
     *
     * @see #parse(int, String, MZTabErrorList)
     */
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
        if (offset == items.length) {
            return record;
        }

        loadOptionalData(record, offset);

        return record;
    }

    /**
     * Check and translate the stable columns and optional columns with stable order into mzTab elements.
     */
    abstract void checkStableData();

    /**
     * Load mzTab element and generate a {@link MZTabRecord}.
     */
    abstract int loadStableData(MZTabRecord record, String line);

    private void checkOptionalData() {
        MZTabColumn column;
        Class dataType;
        String target;
        for (int physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {
            column = factory.getColumnMapping().get(positionMapping.get(physicalPosition));
            if (column != null) {
                target = items[physicalPosition];
                dataType = column.getDataType();
                if (column instanceof AbundanceColumn) {
                    checkDouble(column, target);
                } else if (column instanceof OptionColumn) {
                    if (dataType.equals(String.class)) {
                        checkString(column, target);
                    } else if (dataType.equals(Double.class)) {
                        checkDouble(column, target);
                    } else if (dataType.equals(MZBoolean.class)) {
                        checkMZBoolean(column, target);
                    }
                }
            }
        }
    }

    private void loadOptionalData(MZTabRecord record, int physicalOffset) {
        String target;
        MZTabColumn column;
        Class dataType;
        for (int physicalPosition = physicalOffset; physicalPosition < items.length; physicalPosition++) {
            target = items[physicalPosition].trim();
            column = factory.getColumnMapping().get(positionMapping.get(physicalPosition));
            dataType = column.getDataType();

            if (dataType.equals(String.class)) {
                record.setValue(column.getLogicPosition(), checkString(column, target));
            } else if (dataType.equals(Double.class)) {
                record.setValue(column.getLogicPosition(), checkDouble(column, target));
            } else if (dataType.equals(MZBoolean.class)) {
                record.setValue(column.getLogicPosition(), checkMZBoolean(column, target));
            }
        }

    }

    /**
     * load best_search_engine_score[id], read id value.
     */
    protected Integer loadBestSearchEngineScoreId(String bestSearchEngineScoreLabel) {
        Pattern pattern = Pattern.compile("search_engine_score\\[(\\d+)\\](\\w+)?");
        Matcher matcher = pattern.matcher(bestSearchEngineScoreLabel);

        if (matcher.find()) {
            return new Integer(matcher.group(1));
        }

        return null;
    }

    /**
     * load search_engine_score[id]_ms_run[..], read id value.
     */
    protected Integer loadSearchEngineScoreId(String searchEngineLabel) {
        Pattern pattern = Pattern.compile("search_engine_score\\[(\\d+)\\]\\w*");
        Matcher matcher = pattern.matcher(searchEngineLabel);

        if (matcher.find()) {
            return new Integer(matcher.group(1));
        }

        return null;
    }

    /**
     * In the table-based sections (protein, peptide, and small molecule) there MUST NOT be any empty cells.
     * Some field not allow "null" value, for example unit_id, accession and so on. In "Complete" file, in
     * general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param target SHOULD NOT be empty.
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

        if (target.equalsIgnoreCase(NULL)) {
            if (! allowNull || metadata.getMZTabMode() == MZTabDescription.Mode.Complete) {
                this.errorList.add(new MZTabError(LogicalErrorType.NotNULL, lineNumber, column.getHeader()));
            }
        }

        return target;
    }

    /**
     * In the table-based sections (protein, peptide, and small molecule) there MUST NOT be any empty cells.
     * Some field not allow "null" value, for example unit_id, accession and so on. In "Complete" file, in
     * general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param target SHOULD NOT be empty.
     */
    protected String checkString(MZTabColumn column, String target) {
        return checkData(column, target, true);
    }

    /**
     * Check and translate target string into Integer. If parse incorrect, raise {@link FormatErrorType#Integer} error.
     *
     * @param column SHOULD NOT set null
     * @param target SHOULD NOT be empty.
     */
    protected Integer checkInteger(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equalsIgnoreCase(NULL)) {
            return null;
        }

        Integer value = parseInteger(result);
        if (value == null) {
            this.errorList.add(new MZTabError(FormatErrorType.Integer, lineNumber, column.getHeader(), target));
        }

        return value;
    }

    /**
     * Check and translate target string into Double. If parse incorrect, raise {@link FormatErrorType#Double} error.
     *
     * NOTICE: If ratios are included and the denominator is zero, the "INF" value MUST be used. If the result leads
     * to calculation errors (for example 0/0), this MUST be reported as "not a number" ("NaN").
     *
     * @param column SHOULD NOT set null
     * @param target SHOULD NOT be empty.
     */
    protected Double checkDouble(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equalsIgnoreCase(NULL)) {
            return null;
        }

        Double value = parseDouble(result);
        if (value == null) {
            this.errorList.add(new MZTabError(FormatErrorType.Double, lineNumber, column.getHeader(), target));
            return null;
        }
        if (value.equals(Double.NaN) || value.equals(Double.POSITIVE_INFINITY)) {
            return value;
        }

        return value;
    }

    /**
     * Check and translate target string into parameter list which split by '|' character..
     * If parse incorrect, raise {@link FormatErrorType#ParamList} error.
     *
     * @param column SHOULD NOT set null
     * @param target SHOULD NOT be empty.
     */
    protected SplitList<Param> checkParamList(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equalsIgnoreCase(NULL)) {
            return new SplitList<Param>(BAR);
        }

        SplitList<Param> paramList = parseParamList(result);
        if (paramList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.ParamList, lineNumber, "Column " + column.getHeader(), target));
        }

        return paramList;
    }

    /**
     * Check and translate target string into parameter list which split by splitChar character..
     * If parse incorrect, raise {@link FormatErrorType#StringList} error.
     *
     * @param column SHOULD NOT set null
     * @param target SHOULD NOT be empty.
     */
    protected SplitList<String> checkStringList(MZTabColumn column, String target, char splitChar) {
        String result = checkData(column, target, true);

        if (result == null || result.equalsIgnoreCase(NULL)) {
            return new SplitList<String>(splitChar);
        }

        SplitList<String> stringList = parseStringList(splitChar, result);
        if (stringList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.StringList, lineNumber, column.getHeader(), result, "" + splitChar));
        }

        return stringList;
    }

    /**
     * Check and translate target to {@link MZBoolean}. Only "0" and "1" allow used in express Boolean (0/1).
     * If parse incorrect, raise {@link FormatErrorType#MZBoolean} error.
     *
     * @param column SHOULD NOT set null
     * @param target SHOULD NOT be empty.
     */
    protected MZBoolean checkMZBoolean(MZTabColumn column, String target) {
        String result = checkData(column, target, true);

        if (result == null || result.equalsIgnoreCase(NULL)) {
            return null;
        }

        MZBoolean value = MZBoolean.findBoolean(result);
        if (value == null) {
            this.errorList.add(new MZTabError(FormatErrorType.MZBoolean, lineNumber, column.getHeader(), result));
        }

        return value;
    }

    /**
     * Check target string. Normally, description can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param description SHOULD NOT be empty.
     */
    protected String checkDescription(MZTabColumn column, String description) {
        return checkData(column, description, true);
    }

    /**
     * Check and translate taxid string into Integer. If exists error during parse, raise {@link FormatErrorType#Integer} error.
     * Normally, taxid can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param taxid SHOULD NOT be empty.
     */
    protected Integer checkTaxid(MZTabColumn column, String taxid) {
        return checkInteger(column, taxid);
    }

    /**
     * Check target string. Normally, species can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param species SHOULD NOT be empty.
     */
    protected String checkSpecies(MZTabColumn column, String species) {
        return checkData(column, species, true);
    }

    /**
     * Check target string. Normally, database can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param database SHOULD NOT be empty.
     */
    protected String checkDatabase(MZTabColumn column, String database) {
        return checkData(column, database, true);
    }

    /**
     * Check target string. Normally, databaseVersion can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param databaseVersion SHOULD NOT be empty.
     */
    protected String checkDatabaseVersion(MZTabColumn column, String databaseVersion) {
        return checkData(column, databaseVersion, true);
    }

    /**
     * Check and translate searchEngine string into parameter list which split by '|' character..
     * If parse incorrect, raise {@link FormatErrorType#ParamList} error.
     * Normally, searchEngine can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param searchEngine SHOULD NOT be empty.
     */
    protected SplitList<Param> checkSearchEngine(MZTabColumn column, String searchEngine) {
        return checkParamList(column, searchEngine);
    }

    /**
     * The best search engine score (for this type of score) for the given peptide across all replicates
     * reported. The type of score MUST be defined in the metadata section. If the peptide was not identified
     * by the specified search engine, “null” MUST be reported.
     *
     * @param column SHOULD NOT set null
     * @param bestSearchEngineScore SHOULD NOT be empty.
     */
    protected Double checkBestSearchEngineScore(MZTabColumn column, String bestSearchEngineScore) {
        return checkDouble(column, bestSearchEngineScore);
    }

    /**
     * The search engine score for the given peptide in the defined ms run. The type of score MUST be
     * defined in the metadata section. If the peptide was not identified by the specified search engine
     * “null” must be reported.
     *
     * @param column SHOULD NOT set null
     * @param searchEngineScore SHOULD NOT be empty.
     */
    protected Double checkSearchEngineScore(MZTabColumn column, String searchEngineScore) {
        return checkDouble(column, searchEngineScore);
    }

    /**
     * Check and translate reliability string into {@link Reliability}. Currently, only "1", "2", "3" and "null" are
     * correct value, and others will raise {@link FormatErrorType#Reliability} error.
     * But in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param reliability SHOULD NOT be empty.
     */
    protected Reliability checkReliability(MZTabColumn column, String reliability) {
        String result_reliaility = checkData(column, reliability, true);

        if (result_reliaility == null || result_reliaility.equalsIgnoreCase(NULL)) {
            return null;
        }

        Reliability result = Reliability.findReliability(result_reliaility);
        if (result == null) {
            this.errorList.add(new MZTabError(FormatErrorType.Reliability, lineNumber, column.getHeader(), result_reliaility));
        }

        return result;
    }

    /**
     * Check and translate numPSMs string into Integer. If exists error during parse, raise {@link FormatErrorType#Integer} error.
     * Normally, numPSMs can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param numPSMs SHOULD NOT be empty.
     */
    protected Integer checkNumPSMs(MZTabColumn column, String numPSMs) {
        return checkInteger(column, numPSMs);
    }

    /**
     * Check and translate numPeptidesDistinct string into Integer. If exists error during parse, raise {@link FormatErrorType#Integer} error.
     * Normally, numPeptidesDistinct can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param numPeptidesDistinct SHOULD NOT be empty.
     */
    protected Integer checkNumPeptidesDistinct(MZTabColumn column, String numPeptidesDistinct) {
        return checkInteger(column, numPeptidesDistinct);
    }

    /**
     * Check and translate numPeptidesUnique string into Integer. If exists error during parse, raise {@link FormatErrorType#Integer} error.
     * Normally, numPeptidesUnique can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param numPeptidesUnique SHOULD NOT be empty.
     */
    protected Integer checkNumPeptidesUnique(MZTabColumn column, String numPeptidesUnique) {
        return checkInteger(column, numPeptidesUnique);
    }

    /**
     * Check and translate target string into parameter list which split by ',' character..
     * If parse incorrect, raise {@link FormatErrorType#StringList} error.
     * Normally, ambiguityMembers can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param ambiguityMembers SHOULD NOT be empty.
     */
    protected SplitList<String> checkAmbiguityMembers(MZTabColumn column, String ambiguityMembers) {
        return checkStringList(column, ambiguityMembers, COMMA);
    }

    /**
     * Check and translate target string into {@link Modification} list which split by ',' character..
     * If parse incorrect, raise {@link FormatErrorType#ModificationList} error.
     * Normally, ambiguityMembers can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * If software cannot determine protein-level modifications, "null" MUST be used.
     * If the software has determined that there are no modifications to a given protein "0" MUST be used.
     *
     * @param section SHOULD NOT set null
     * @param column SHOULD NOT set null
     * @param modificationsLabel SHOULD NOT be empty.
     */
    protected SplitList<Modification> checkModifications(Section section, MZTabColumn column, String modificationsLabel) {
        String result_modifications = checkData(column, modificationsLabel, true);

        if (result_modifications == null || result_modifications.equalsIgnoreCase(NULL) || result_modifications.equals("0")) {
            return new SplitList<Modification>(COMMA);
        }

        SplitList<Modification> modificationList = parseModificationList(section, modificationsLabel);
        if (modificationList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.ModificationList, lineNumber, column.getHeader(), result_modifications));
        }

        return modificationList;
    }

    protected java.net.URI checkURI(MZTabColumn column, String uri) {
        String result_uri = checkData(column, uri, true);

        if (result_uri == null || result_uri.equalsIgnoreCase(NULL)) {
            return null;
        }

        java.net.URI result = parseURI(result_uri);
        if (result == null) {
            this.errorList.add(new MZTabError(FormatErrorType.URI, lineNumber, "Column " + column.getHeader(), result_uri));
        }

        return result;
    }

    /**
     * Check and translate spectraRef string into {@link SpectraRef} list.
     * If parse incorrect, or ms_run not defined in metadata raise {@link FormatErrorType#SpectraRef} error.
     * Normally, spectraRef can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param spectraRef SHOULD NOT be empty.
     */
    protected List<SpectraRef> checkSpectraRef(MZTabColumn column, String spectraRef) {
        String result_spectraRef = checkData(column, spectraRef, true);

        if (result_spectraRef == null || result_spectraRef.equalsIgnoreCase(NULL)) {
            return new ArrayList<SpectraRef>();
        }

        List<SpectraRef> refList = parseSpectraRefList(metadata, result_spectraRef);
        if (refList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.SpectraRef, lineNumber, column.getHeader(), result_spectraRef));
        } else {
            for (SpectraRef ref : refList) {
                MsRun run = ref.getMsRun();
                if (run.getLocation() == null) {
                    this.errorList.add(new MZTabError(LogicalErrorType.SpectraRef, lineNumber, column.getHeader(), result_spectraRef, "ms_run[" + run.getId() + "]-location"));
                    refList.clear();
                    break;
                }
            }
        }

        return refList;
    }

    /**
     * Check target string. Normally, pre can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param pre SHOULD NOT be empty.
     */
    protected String checkPre(MZTabColumn column, String pre) {
        return checkData(column, pre, true);
    }

    /**
     * Check target string. Normally, post can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param post SHOULD NOT be empty.
     */
    protected String checkPost(MZTabColumn column, String post) {
        return checkData(column, post, true);
    }

    /**
     * Check target string. Normally, start can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param start SHOULD NOT be empty.
     */
    protected String checkStart(MZTabColumn column, String start) {
        return checkData(column, start, true);
    }

    /**
     * Check target string. Normally, end can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param end SHOULD NOT be empty.
     */
    protected String checkEnd(MZTabColumn column, String end) {
        return checkData(column, end, true);
    }

    /**
     * Check and translate target string into string list which split by ',' character..
     * If parse incorrect, raise {@link FormatErrorType#StringList} error. Besides, each item in list should be
     * start with "GO:", otherwise system raise {@link FormatErrorType#GOTermList} error.
     * Normally, go_terms can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param go_terms SHOULD NOT be empty.
     */
    protected SplitList<String> checkGOTerms(MZTabColumn column, String go_terms) {
        String result_go_terms = checkData(column, go_terms, true);

        if (result_go_terms == null || result_go_terms.equalsIgnoreCase(NULL)) {
            return new SplitList<String>(COMMA);
        }


        SplitList<String> stringList = parseGOTermList(result_go_terms);
        if (stringList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.GOTermList, lineNumber, column.getHeader(), result_go_terms));
        }

        return stringList;
    }

    /**
     * Check and translate protein_coverage string into Double. If parse incorrect, raise {@link FormatErrorType#Double} error.
     * protein_coverage range should be in the [0, 1), otherwise raise {@link LogicalErrorType#ProteinCoverage} error.
     *
     * NOTICE: If ratios are included and the denominator is zero, the "INF" value MUST be used. If the result leads
     * to calculation errors (for example 0/0), this MUST be reported as "not a number" ("NaN").
     *
     * @param column SHOULD NOT set null
     * @param protein_coverage SHOULD NOT be empty.
     */
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

    /**
     * Check and translate peptide sequence. 'O' and 'U' are encoded by codons that are usually interpreted as stop codons,
     * which can not displayed in the sequence. So, if find it, system raise {@link FormatErrorType#Sequence} error.
     *
     * @param column SHOULD NOT set null
     * @param sequence SHOULD NOT be empty.
     */
    protected String checkSequence(MZTabColumn column, String sequence) {
        String result = checkData(column, sequence, true);

        if (result == null) {
            return null;
        }

        result = result.toUpperCase();

        Pattern pattern = Pattern.compile("[OU]");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            this.errorList.add(new MZTabError(FormatErrorType.Sequence, lineNumber, column.getHeader(), sequence));
        }

        return result;
    }

    /**
     * Check and translate psm_id string into Integer. If exists error during parse, raise {@link FormatErrorType#Integer} error.
     * Normally, psm_id can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param psm_id SHOULD NOT be empty.
     */
    protected Integer checkPSMID(MZTabColumn column, String psm_id) {
        return checkInteger(column, psm_id);
    }

    /**
     * Check and translate unique to {@link MZBoolean}. Only "0" and "1" allow used in express Boolean (0/1).
     * If parse incorrect, raise {@link FormatErrorType#MZBoolean} error.
     *
     * @param column SHOULD NOT set null
     * @param unique SHOULD NOT be empty.
     */
    protected MZBoolean checkUnique(MZTabColumn column, String unique) {
        return checkMZBoolean(column, unique);
    }

    /**
     * Check and translate charge string into Integer. If exists error during parse, raise {@link FormatErrorType#Integer} error.
     * Normally, charge can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param charge SHOULD NOT be empty.
     */
    protected Integer checkCharge(MZTabColumn column, String charge) {
        return checkInteger(column, charge);
    }

    /**
     * Check and translate mass_to_charge string into Double. If parse incorrect, raise {@link FormatErrorType#Double} error.
     *
     * NOTICE: If ratios are included and the denominator is zero, the "INF" value MUST be used. If the result leads
     * to calculation errors (for example 0/0), this MUST be reported as "not a number" ("NaN").
     *
     * @param column SHOULD NOT set null
     * @param mass_to_charge SHOULD NOT be empty.
     */
    protected Double checkMassToCharge(MZTabColumn column, String mass_to_charge) {
        return checkDouble(column, mass_to_charge);
    }

    /**
     * Check and translate exp_mass_to_charge string into Double. If parse incorrect, raise {@link FormatErrorType#Double} error.
     *
     * NOTICE: If ratios are included and the denominator is zero, the "INF" value MUST be used. If the result leads
     * to calculation errors (for example 0/0), this MUST be reported as "not a number" ("NaN").
     *
     * @param column SHOULD NOT set null
     * @param exp_mass_to_charge SHOULD NOT be empty.
     */
    protected Double checkExpMassToCharge(MZTabColumn column, String exp_mass_to_charge) {
        return checkDouble(column, exp_mass_to_charge);
    }

    /**
     * Check and translate calc_mass_to_charge string into Double. If parse incorrect, raise {@link FormatErrorType#Double} error.
     *
     * NOTICE: If ratios are included and the denominator is zero, the "INF" value MUST be used. If the result leads
     * to calculation errors (for example 0/0), this MUST be reported as "not a number" ("NaN").
     *
     * @param column SHOULD NOT set null
     * @param calc_mass_to_charge SHOULD NOT be empty.
     */
    protected Double checkCalcMassToCharge(MZTabColumn column, String calc_mass_to_charge) {
        return checkDouble(column, calc_mass_to_charge);
    }

    /**
     * Check and translate identifier string into string list which split by '|' character..
     * If parse incorrect, raise {@link FormatErrorType#StringList} error.
     * Normally, identifier can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param identifier SHOULD NOT be empty.
     */
    protected SplitList<String> checkIdentifier(MZTabColumn column, String identifier) {
        return checkStringList(column, identifier, BAR);
    }

    /**
     * Check chemical_formula string. Normally, chemical_formula can set "null". But
     * in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @see #checkData(MZTabColumn, String, boolean)
     *
     * @param column SHOULD NOT set null
     * @param chemical_formula SHOULD NOT be empty.
     */
    protected String checkChemicalFormula(MZTabColumn column, String chemical_formula) {
        return checkData(column, chemical_formula, true);
    }

    /**
     * Check and translate smiles string into parameter list which split by '|' character..
     * If parse incorrect, raise {@link FormatErrorType#StringList} error.
     * Normally, smiles can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param smiles SHOULD NOT be empty.
     */
    protected SplitList<String> checkSmiles(MZTabColumn column, String smiles) {
        return checkStringList(column, smiles, BAR);
    }

    /**
     * Check and translate inchi_key string into parameter list which split by '|' character..
     * If parse incorrect, raise {@link FormatErrorType#StringList} error.
     * Normally, inchi_key can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param inchi_key SHOULD NOT be empty.
     */
    protected SplitList<String> checkInchiKey(MZTabColumn column, String inchi_key) {
        return checkStringList(column, inchi_key, BAR);
    }

    /**
     * Check and translate retention_time string into Double list which split by '|' character..
     * If parse incorrect, raise {@link FormatErrorType#DoubleList} error.
     * Normally, retention_time can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param retention_time SHOULD NOT be empty.
     */
    protected SplitList<Double> checkRetentionTime(MZTabColumn column, String retention_time) {
        String result = checkData(column, retention_time, true);

        if (result == null || result.equalsIgnoreCase(NULL)) {
            return new SplitList<Double>(BAR);
        }

        SplitList<Double> valueList = parseDoubleList(result);
        if (valueList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.DoubleList, lineNumber, column.getHeader(), result, "" + BAR));
        }

        return valueList;
    }

    /**
     * Check and translate retention_time_window string into Double list which split by '|' character..
     * If parse incorrect, raise {@link FormatErrorType#DoubleList} error.
     * Normally, retention_time_window can set "null", but in "Complete" file, in general "null" values SHOULD not be given.
     *
     * @param column SHOULD NOT set null
     * @param retention_time_window SHOULD NOT be empty.
     */
    protected SplitList<Double> checkRetentionTimeWindow(MZTabColumn column, String retention_time_window) {
        String result = checkData(column, retention_time_window, true);

        if (result == null || result.equalsIgnoreCase(NULL)) {
            return new SplitList<Double>(BAR);
        }

        SplitList<Double> valueList = parseDoubleList(result);
        if (valueList.size() == 0) {
            this.errorList.add(new MZTabError(FormatErrorType.DoubleList, lineNumber, column.getHeader(), result, "" + BAR));
        }

        return valueList;
    }
}
