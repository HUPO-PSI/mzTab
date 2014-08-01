package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.util.SortedMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;

/**
* @author qingwei
* @since 10/02/13
*/
public class PEPLineParser extends MZTabDataLineParser {
    public PEPLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
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
                if (columnName.equals(PeptideColumn.SEQUENCE.getName())) {
                    checkSequence(column, target);
                } else if (columnName.equals(PeptideColumn.ACCESSION.getName())) {
                    checkAccession(column, target);
                } else if (columnName.equals(PeptideColumn.UNIQUE.getName())) {
                    checkUnique(column, target);
                } else if (columnName.equals(PeptideColumn.DATABASE.getName())) {
                    checkDatabase(column, target);
                } else if (columnName.equals(PeptideColumn.DATABASE_VERSION.getName())) {
                    checkDatabaseVersion(column, target);
                } else if (columnName.equals(PeptideColumn.SEARCH_ENGINE.getName())) {
                    checkSearchEngine(column, target);
                } else if (columnName.startsWith(PeptideColumn.BEST_SEARCH_ENGINE_SCORE.getName())) {
                    checkBestSearchEngineScore(column, target);
                } else if (columnName.startsWith(PeptideColumn.SEARCH_ENGINE_SCORE.getName())) {
                    checkSearchEngineScore(column, target);
                } else if (columnName.equals(PeptideColumn.RELIABILITY.getName())) {
                    checkReliability(column, target);
                } else if (columnName.equals(PeptideColumn.MODIFICATIONS.getName())) {
                    String sequence = items[exchangeMapping.get(PeptideColumn.SEQUENCE.getLogicPosition())];
                    checkModifications(column, sequence, target);
                } else if (columnName.equals(PeptideColumn.RETENTION_TIME.getName())) {
                    checkRetentionTime(column, target);
                } else if (columnName.equals(PeptideColumn.RETENTION_TIME_WINDOW.getName())) {
                    checkRetentionTimeWindow(column, target);
                } else if (columnName.equals(PeptideColumn.CHARGE.getName())) {
                    checkCharge(column, target);
                } else if (columnName.equals(PeptideColumn.MASS_TO_CHARGE.getName())) {
                    checkMassToCharge(column, target);
                } else if (columnName.equals(PeptideColumn.URI.getName())) {
                    checkURI(column, target);
                } else if (columnName.equals(PeptideColumn.SPECTRA_REF.getName())) {
                    checkSpectraRef(column, target);
                }
            }
        }
    }

    protected int loadStableData(MZTabRecord record, String line) {
        items = line.split("\\s*" + TAB + "\\s*");
        items[items.length - 1] = items[items.length - 1].trim();

        Peptide peptide = (Peptide) record;
        MZTabColumn column;
        String columnName;
        String target;
        SortedMap<String, MZTabColumn> columnMapping = factory.getColumnMapping();
        int physicalPosition = 1;
        String logicalPosition;

        logicalPosition = positionMapping.get(physicalPosition);
        column = columnMapping.get(logicalPosition);
        while (column != null && column instanceof PeptideColumn) {
            target = items[physicalPosition];
            columnName = column.getName();
            if (columnName.equals(PeptideColumn.SEQUENCE.getName())) {
                peptide.setSequence(target);
            } else if (columnName.equals(PeptideColumn.ACCESSION.getName())) {
                peptide.setAccession(target);
            } else if (columnName.equals(PeptideColumn.UNIQUE.getName())) {
                peptide.setUnique(target);
            } else if (columnName.equals(PeptideColumn.DATABASE.getName())) {
                peptide.setDatabase(target);
            } else if (columnName.equals(PeptideColumn.DATABASE_VERSION.getName())) {
                peptide.setDatabaseVersion(target);
            } else if (columnName.equals(PeptideColumn.SEARCH_ENGINE.getName())) {
                peptide.setSearchEngine(target);
            } else if (columnName.startsWith(PeptideColumn.BEST_SEARCH_ENGINE_SCORE.getName())) {
                int id = loadBestSearchEngineScoreId(column.getHeader());
                peptide.setBestSearchEngineScore(id, target);
            } else if (columnName.startsWith(PeptideColumn.SEARCH_ENGINE_SCORE.getName())) {
                int id = loadSearchEngineScoreId(column.getHeader());
                MsRun msRun = (MsRun) column.getElement();
                peptide.setSearchEngineScore(id, msRun, target);
            } else if (columnName.equals(PeptideColumn.RELIABILITY.getName())) {
                peptide.setReliability(target);
            } else if (columnName.equals(PeptideColumn.MODIFICATIONS.getName())) {
                peptide.setModifications(target);
            } else if (columnName.equals(PeptideColumn.RETENTION_TIME.getName())) {
                peptide.setRetentionTime(target);
            } else if (columnName.equals(PeptideColumn.RETENTION_TIME_WINDOW.getName())) {
                peptide.setRetentionTimeWindow(target);
            } else if (columnName.equals(PeptideColumn.CHARGE.getName())) {
                peptide.setCharge(target);
            } else if (columnName.equals(PeptideColumn.MASS_TO_CHARGE.getName())) {
                peptide.setMassToCharge(target);
            } else if (columnName.equals(PeptideColumn.URI.getName())) {
                peptide.setURI(target);
            } else if (columnName.equals(PeptideColumn.SPECTRA_REF.getName())) {
                peptide.setSpectraRef(target);
            }
            physicalPosition++;
            logicalPosition = positionMapping.get(physicalPosition);
            column = logicalPosition == null ? null : columnMapping.get(logicalPosition);
        }

        return physicalPosition;
    }

    private String checkAccession(MZTabColumn column, String target) {
        return checkData(column, target, true);
    }

    public Peptide getRecord(String line) {
        return (Peptide) super.getRecord(Section.Peptide, line);
    }

    /**
     * For proteins and peptides modifications SHOULD be reported using either UNIMOD or PSI-MOD accessions.
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     */
    protected SplitList<Modification> checkModifications(MZTabColumn column, String sequence, String target) {
        SplitList<Modification> modificationList = super.checkModifications(section, column, target);

        int terminal_position = sequence.length() + 1;
        for (Modification mod: modificationList) {
            for (Integer position : mod.getPositionMap().keySet()) {
                if (position > terminal_position || position < 0) {
                    errorList.add(new MZTabError(LogicalErrorType.ModificationPosition, lineNumber, column.getHeader(), mod.toString(), sequence));
                    return null;
                }
            }

            if (mod.getType() == Modification.Type.CHEMMOD) {
                // this is warn
                errorList.add(new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString()));
            }
        }

        return modificationList;
    }
}
