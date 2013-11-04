package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.util.SortedMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;

/**
 * User: Qingwei
 * Date: 07/06/13
 */
public class PSMLineParser extends MZTabDataLineParser {
    public PSMLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
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
                if (columnName.equals(PSMColumn.SEQUENCE.getName())) {
                    checkSequence(column, target);
                } else if (columnName.equals(PSMColumn.PSM_ID.getName())) {
                    checkPSMID(column, target);
                } else if (columnName.equals(PSMColumn.ACCESSION.getName())) {
                    checkAccession(column, target);
                } else if (columnName.equals(PSMColumn.UNIQUE.getName())) {
                    checkUnique(column, target);
                } else if (columnName.equals(PSMColumn.DATABASE.getName())) {
                    checkDatabase(column, target);
                } else if (columnName.equals(PSMColumn.DATABASE_VERSION.getName())) {
                    checkDatabaseVersion(column, target);
                } else if (columnName.equals(PSMColumn.SEARCH_ENGINE.getName())) {
                    checkSearchEngine(column, target);
                } else if (columnName.equals(PSMColumn.SEARCH_ENGINE_SCORE.getName())) {
                    checkSearchEngineScore(column, target);
                } else if (columnName.equals(PSMColumn.RELIABILITY.getName())) {
                    checkReliability(column, target);
                } else if (columnName.equals(PSMColumn.MODIFICATIONS.getName())) {
                    String sequence = items[exchangeMapping.get(PSMColumn.SEQUENCE.getLogicPosition())];
                    checkModifications(column, sequence, target);
                } else if (columnName.equals(PSMColumn.RETENTION_TIME.getName())) {
                    checkRetentionTime(column, target);
                } else if (columnName.equals(PSMColumn.CHARGE.getName())) {
                    checkCharge(column, target);
                } else if (columnName.equals(PSMColumn.EXP_MASS_TO_CHARGE.getName())) {
                    checkExpMassToCharge(column, target);
                } else if (columnName.equals(PSMColumn.CALC_MASS_TO_CHARGE.getName())) {
                    checkCalcMassToCharge(column, target);
                } else if (columnName.equals(PSMColumn.URI.getName())) {
                    checkURI(column, target);
                } else if (columnName.equals(PSMColumn.SPECTRA_REF.getName())) {
                    checkSpectraRef(column, target);
                } else if (columnName.equals(PSMColumn.PRE.getName())) {
                    checkPre(column, target);
                } else if (columnName.equals(PSMColumn.POST.getName())) {
                    checkPost(column, target);
                } else if (columnName.equals(PSMColumn.START.getName())) {
                    checkStart(column, target);
                } else if (columnName.equals(PSMColumn.END.getName())) {
                    checkEnd(column, target);
                }
            }
        }
    }

    protected int loadStableData(MZTabRecord record, String line) {
        items = line.split("\\s*" + TAB + "\\s*");
        items[items.length - 1] = items[items.length - 1].trim();

        PSM psm = (PSM) record;
        MZTabColumn column;
        String columnName;
        String target;
        SortedMap<String, MZTabColumn> columnMapping = factory.getColumnMapping();
        int physicalPosition = 1;
        String logicalPosition;

        logicalPosition = positionMapping.get(physicalPosition);
        column = columnMapping.get(logicalPosition);
        while (column != null && column instanceof PSMColumn) {
            target = items[physicalPosition];
            columnName = column.getName();
            if (columnName.equals(PSMColumn.SEQUENCE.getName())) {
                psm.setSequence(target);
            } else if (columnName.equals(PSMColumn.PSM_ID.getName())) {
                psm.setPSM_ID(target);
            } else if (columnName.equals(PSMColumn.ACCESSION.getName())) {
                psm.setAccession(target);
            } else if (columnName.equals(PSMColumn.UNIQUE.getName())) {
                psm.setUnique(target);
            } else if (columnName.equals(PSMColumn.DATABASE.getName())) {
                psm.setDatabase(target);
            } else if (columnName.equals(PSMColumn.DATABASE_VERSION.getName())) {
                psm.setDatabaseVersion(target);
            } else if (columnName.equals(PSMColumn.SEARCH_ENGINE.getName())) {
                psm.setSearchEngine(target);
            } else if (columnName.equals(PSMColumn.SEARCH_ENGINE_SCORE.getName())) {
                psm.setSearchEngineScore(target);
            } else if (columnName.equals(PSMColumn.RELIABILITY.getName())) {
                psm.setReliability(target);
            } else if (columnName.equals(PSMColumn.MODIFICATIONS.getName())) {
                psm.setModifications(target);
            } else if (columnName.equals(PSMColumn.RETENTION_TIME.getName())) {
                psm.setRetentionTime(target);
            } else if (columnName.equals(PSMColumn.CHARGE.getName())) {
                psm.setCharge(target);
            } else if (columnName.equals(PSMColumn.EXP_MASS_TO_CHARGE.getName())) {
                psm.setExpMassToCharge(target);
            } else if (columnName.equals(PSMColumn.CALC_MASS_TO_CHARGE.getName())) {
                psm.setCalcMassToCharge(target);
            } else if (columnName.equals(PSMColumn.URI.getName())) {
                psm.setURI(target);
            } else if (columnName.equals(PSMColumn.SPECTRA_REF.getName())) {
                psm.setSpectraRef(target);
            } else if (columnName.equals(PSMColumn.PRE.getName())) {
                psm.setPre(target);
            } else if (columnName.equals(PSMColumn.POST.getName())) {
                psm.setPost(target);
            } else if (columnName.equals(PSMColumn.START.getName())) {
                psm.setStart(target);
            } else if (columnName.equals(PSMColumn.END.getName())) {
                psm.setEnd(target);
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

    public PSM getRecord(String line) {
        return (PSM) super.getRecord(Section.PSM, line);
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
                errorList.add(new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString()));
            }
        }

        return modificationList;
    }
}
