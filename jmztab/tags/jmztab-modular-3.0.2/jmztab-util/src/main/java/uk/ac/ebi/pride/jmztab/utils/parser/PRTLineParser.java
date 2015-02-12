package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseString;
import static uk.ac.ebi.pride.jmztab.model.ProteinColumn.*;

/**
 * @author qingwei
 * @since 10/02/13
 */
public class PRTLineParser extends MZTabDataLineParser {

    private Set<String> accessionSet = new HashSet<String>();
    private Protein protein = null;

    public PRTLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
                         Metadata metadata, MZTabErrorList errorList) {
        super(factory, positionMapping, metadata, errorList);
    }

    @Override
    protected int checkData() {

        MZTabColumn column;
        String columnName;
        String target;
        int physicalPosition;
        String logicalPosition;
        protein = new Protein(factory);

        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {
            logicalPosition = positionMapping.get(physicalPosition);
            column = factory.getColumnMapping().get(logicalPosition);

            if (column != null) {
                columnName = column.getName();
                target = items[physicalPosition];
                if (column instanceof ProteinColumn) {
                    if (columnName.equals(ACCESSION.getName())) {
                        protein.setAccession(checkAccession(column, target));
                    } else if (columnName.equals(DESCRIPTION.getName())) {
                        protein.setDescription(checkDescription(column, target));
                    } else if (columnName.equals(TAXID.getName())) {
                        protein.setTaxid(checkTaxid(column, target));
                    } else if (columnName.equals(SPECIES.getName())) {
                        protein.setSpecies(checkSpecies(column, target));
                    } else if (columnName.equals(DATABASE.getName())) {
                        protein.setDatabase(checkDatabase(column, target));
                    } else if (columnName.equals(DATABASE_VERSION.getName())) {
                        protein.setDatabaseVersion(checkDatabaseVersion(column, target));
                    } else if (columnName.equals(SEARCH_ENGINE.getName())) {
                        protein.setSearchEngine(checkSearchEngine(column, target));
                    } else if (columnName.startsWith(BEST_SEARCH_ENGINE_SCORE.getName())) {
                        int id = loadBestSearchEngineScoreId(column.getHeader());
                        protein.setBestSearchEngineScore(id, checkBestSearchEngineScore(column, target));
                    } else if (columnName.startsWith(SEARCH_ENGINE_SCORE.getName())) {
                        int id = loadSearchEngineScoreId(column.getHeader());
                        MsRun msRun = (MsRun) column.getElement();
                        protein.setSearchEngineScore(id, msRun, checkSearchEngineScore(column, target));
                    } else if (columnName.equals(RELIABILITY.getName())) {
                        protein.setReliability(checkReliability(column, target));
                    } else if (columnName.equals(NUM_PSMS.getName())) {
                        protein.setNumPSMs(logicalPosition, checkNumPSMs(column, target));
                    } else if (columnName.equals(NUM_PEPTIDES_DISTINCT.getName())) {
                        protein.setNumPeptidesDistinct(logicalPosition, checkNumPeptidesDistinct(column, target));
                    } else if (columnName.equals(NUM_PEPTIDES_UNIQUE.getName())) {
                        protein.setNumPeptidesUnique(logicalPosition, checkNumPeptidesUnique(column, target));
                    } else if (columnName.equals(AMBIGUITY_MEMBERS.getName())) {
                        protein.setAmbiguityMembers(checkAmbiguityMembers(column, target));
                    } else if (columnName.equals(MODIFICATIONS.getName())) {
                        protein.setModifications(checkModifications(column, target));
                    } else if (columnName.equals(URI.getName())) {
                        protein.setURI(checkURI(column, target));
                    } else if (columnName.equals(GO_TERMS.getName())) {
                        protein.setGOTerms(checkGOTerms(column, target));
                    } else if (columnName.equals(PROTEIN_COVERAGE.getName())) {
                        protein.setProteinConverage(checkProteinCoverage(column, target));
                    }
                } else if (column instanceof AbundanceColumn) {
                    //Double check, the column name should contain abundance
                    if (columnName.contains("abundance")) {
                        protein.setValue(logicalPosition, checkDouble(column, target));
                    }
                } else if(column instanceof OptionColumn){
                    //Double check, the column name should opt
                    if (columnName.startsWith("opt_")) {
                        Class dataType = column.getDataType();
                        if (dataType.equals(String.class)) {
                            protein.setValue(column.getLogicPosition(), checkString(column, target));
                        } else if (dataType.equals(Double.class)) {
                            protein.setValue(column.getLogicPosition(), checkDouble(column, target));
                        } else if (dataType.equals(MZBoolean.class)) {
                            protein.setValue(column.getLogicPosition(), checkMZBoolean(column, target));
                        }
                    }
                }
            }
        }

        return physicalPosition;
    }

    public Protein getRecord() {
        if(protein == null){
            protein = new Protein(factory);
        }
        return protein;
    }

    /**
     * accession should not null.
     * accession MUST be unique, otherwise raise {@link LogicalErrorType#DuplicationAccession} error.
     *
     * If check error return null, else return accession String.
     */
    protected String checkAccession(MZTabColumn column, String accession) {
        String result_accession = checkData(column, accession, false);

        if (result_accession != null) {

            if (!accessionSet.add(result_accession)) {
                errorList.add(new MZTabError(LogicalErrorType.DuplicationAccession, lineNumber, column.getHeader(), result_accession));
                return null;
            }
        }

        return result_accession;
    }

    /**
     * For proteins and peptides modifications SHOULD be reported using either UNIMOD or PSI-MOD accessions.
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     *
     * Ambiguity of modification position MUST NOT be reported at the Protein level.
     */
    protected SplitList<Modification> checkModifications(MZTabColumn column, String target) {
        SplitList<Modification> modificationList = super.checkModifications(section, column, target);

        for (Modification mod: modificationList) {
            if (mod.getPositionMap().size() > 1) {
                // this is warn
                errorList.add(new MZTabError(LogicalErrorType.AmbiguityMod, lineNumber, column.getHeader(), mod.toString()));
            }

            if (mod.getType() == Modification.Type.CHEMMOD) {
                // this is warn
                errorList.add(new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString()));
            }

            if (mod.getType() == Modification.Type.SUBST && parseSubstitutionIdentifier(mod.getAccession()) != null) {
                errorList.add(new MZTabError(LogicalErrorType.SubstituteIdentifier, lineNumber, column.getHeader(), mod.toString()));
                return null;
            }
        }

        return modificationList;
    }

    /**
     * In SUBST cases, the "sequence" column MUST contain the original, unaltered sequence.
     */
    private String parseSubstitutionIdentifier(String identifier) {
        identifier = parseString(identifier);
        if (identifier == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\"[^BJOUXZ]+\"");
        Matcher matcher = pattern.matcher(identifier);

        if (matcher.find() && matcher.start() == 0 && matcher.end() == identifier.length()) {
            return identifier;
        } else {
            return null;
        }
    }
}
