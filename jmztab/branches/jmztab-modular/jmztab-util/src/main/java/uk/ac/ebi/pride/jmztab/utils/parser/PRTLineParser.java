package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseString;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PRTLineParser extends MZTabDataLineParser {
    private Set<String> accessionSet = new HashSet<String>();

    public PRTLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
                         Metadata metadata, MZTabErrorList errorList) {
        super(factory, positionMapping, metadata, errorList);
    }

    @Override
    protected void checkStableData() {
        MZTabColumn column;
        String columnName;
        String target;
        for (int physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {
            column = factory.getColumnMapping().get(positionMapping.get(physicalPosition));
            if (column != null) {
                columnName = column.getName();
                target = items[physicalPosition];
                if (columnName.equals(ProteinColumn.ACCESSION.getName())) {
                    checkAccession(column, target);
                } else if (columnName.equals(ProteinColumn.DESCRIPTION.getName())) {
                    checkDescription(column, target);
                } else if (columnName.equals(ProteinColumn.TAXID.getName())) {
                    checkTaxid(column, target);
                } else if (columnName.equals(ProteinColumn.SPECIES.getName())) {
                    checkSpecies(column, target);
                } else if (columnName.equals(ProteinColumn.DATABASE.getName())) {
                    checkDatabase(column, target);
                } else if (columnName.equals(ProteinColumn.DATABASE_VERSION.getName())) {
                    checkDatabaseVersion(column, target);
                } else if (columnName.equals(ProteinColumn.SEARCH_ENGINE.getName())) {
                    checkSearchEngine(column, target);
                } else if (columnName.startsWith(ProteinColumn.BEST_SEARCH_ENGINE_SCORE.getName())) {
                    checkBestSearchEngineScore(column, target);
                } else if (columnName.startsWith(ProteinColumn.SEARCH_ENGINE_SCORE.getName())) {
                    checkSearchEngineScore(column, target);
                } else if (columnName.equals(ProteinColumn.RELIABILITY.getName())) {
                    checkReliability(column, target);
                } else if (columnName.equals(ProteinColumn.NUM_PSMS.getName())) {
                    checkNumPSMs(column, target);
                } else if (columnName.equals(ProteinColumn.NUM_PEPTIDES_DISTINCT.getName())) {
                    checkNumPeptidesDistinct(column, target);
                } else if (columnName.equals(ProteinColumn.NUM_PEPTIDES_UNIQUE.getName())) {
                    checkNumPeptidesUnique(column, target);
                } else if (columnName.equals(ProteinColumn.AMBIGUITY_MEMBERS.getName())) {
                    checkAmbiguityMembers(column, target);
                } else if (columnName.equals(ProteinColumn.MODIFICATIONS.getName())) {
                    checkModifications(column, target);
                } else if (columnName.equals(ProteinColumn.URI.getName())) {
                    checkURI(column, target);
                } else if (columnName.equals(ProteinColumn.GO_TERMS.getName())) {
                    checkGOTerms(column, target);
                } else if (columnName.equals(ProteinColumn.PROTEIN_COVERAGE.getName())) {
                    checkProteinCoverage(column, target);
                }
            }
        }
    }

    protected int loadStableData(MZTabRecord record, String line) {
        items = line.split("\\s*" + TAB + "\\s*");
        items[items.length - 1] = items[items.length - 1].trim();
        Protein protein = (Protein) record;
        MZTabColumn column;
        String columnName;
        String target;
        SortedMap<String, MZTabColumn> columnMapping = factory.getColumnMapping();
        int physicalPosition = 1;
        String logicalPosition;

        logicalPosition = positionMapping.get(physicalPosition);
        column = columnMapping.get(logicalPosition);
        while (column != null && column instanceof ProteinColumn) {
            target = items[physicalPosition];
            columnName = column.getName();
            if (columnName.equals(ProteinColumn.ACCESSION.getName())) {
                protein.setAccession(target);
            } else if (columnName.equals(ProteinColumn.DESCRIPTION.getName())) {
                protein.setDescription(target);
            } else if (columnName.equals(ProteinColumn.TAXID.getName())) {
                protein.setTaxid(target);
            } else if (columnName.equals(ProteinColumn.SPECIES.getName())) {
                protein.setSpecies(target);
            } else if (columnName.equals(ProteinColumn.DATABASE.getName())) {
                protein.setDatabase(target);
            } else if (columnName.equals(ProteinColumn.DATABASE_VERSION.getName())) {
                protein.setDatabaseVersion(target);
            } else if (columnName.equals(ProteinColumn.SEARCH_ENGINE.getName())) {
                protein.setSearchEngine(target);
            } else if (columnName.startsWith(ProteinColumn.BEST_SEARCH_ENGINE_SCORE.getName())) {
                int id = loadBestSearchEngineScoreId(column.getHeader());
                protein.setBestSearchEngineScore(id, target);
            } else if (columnName.startsWith(ProteinColumn.SEARCH_ENGINE_SCORE.getName())) {
                int id = loadSearchEngineScoreId(column.getHeader());
                MsRun msRun = (MsRun) column.getElement();
                protein.setSearchEngineScore(id, msRun, target);
            } else if (columnName.equals(ProteinColumn.RELIABILITY.getName())) {
                protein.setReliability(target);
            } else if (columnName.equals(ProteinColumn.NUM_PSMS.getName())) {
                protein.setNumPSMs(logicalPosition, target);
            } else if (columnName.equals(ProteinColumn.NUM_PEPTIDES_DISTINCT.getName())) {
                protein.setNumPeptidesDistinct(logicalPosition, target);
            } else if (columnName.equals(ProteinColumn.NUM_PEPTIDES_UNIQUE.getName())) {
                protein.setNumPeptidesUnique(logicalPosition, target);
            } else if (columnName.equals(ProteinColumn.AMBIGUITY_MEMBERS.getName())) {
                protein.setAmbiguityMembers(target);
            } else if (columnName.equals(ProteinColumn.MODIFICATIONS.getName())) {
                protein.setModifications(target);
            } else if (columnName.equals(ProteinColumn.URI.getName())) {
                protein.setURI(target);
            } else if (columnName.equals(ProteinColumn.GO_TERMS.getName())) {
                protein.setGOTerms(target);
            } else if (columnName.equals(ProteinColumn.PROTEIN_COVERAGE.getName())) {
                protein.setProteinConverage(target);
            }

            physicalPosition++;
            logicalPosition = positionMapping.get(physicalPosition);
            column = logicalPosition == null ? null : columnMapping.get(logicalPosition);
        }

        return physicalPosition;
    }

    public Protein getRecord(String line) {
        return (Protein) super.getRecord(Section.Protein, line);
    }

    /**
     * accession should not null.
     * accession MUST be unique, otherwise raise {@link LogicalErrorType#DuplicationAccession} error.
     *
     * If check error return null, else return accession String.
     */
    protected String checkAccession(MZTabColumn column, String accession) {
        String result_accession = checkData(column, accession, false);

        if (result_accession == null) {
            return result_accession;
        }

        if (! accessionSet.add(result_accession)) {
            errorList.add(new MZTabError(LogicalErrorType.DuplicationAccession, lineNumber, column.getHeader(), result_accession));
            return null;
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
