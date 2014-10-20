package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import static uk.ac.ebi.pride.jmztab.model.PSMColumn.*;

/**
 * @author qingwei
 * @since 07/06/13
 */
public class PSMLineParser extends MZTabDataLineParser {

    private PSM psm;

    public PSMLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
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
        psm = new PSM(factory, metadata);

        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {
            logicalPosition = positionMapping.get(physicalPosition);
            column = factory.getColumnMapping().get(logicalPosition);

            if (column != null) {
                columnName = column.getName();
                target = items[physicalPosition];
                if (column instanceof PSMColumn) {
                    if (columnName.equals(SEQUENCE.getName())) {
                        psm.setSequence(checkSequence(column, target));
                    } else if (columnName.equals(PSM_ID.getName())) {
                        psm.setPSM_ID(checkPSMID(column, target));
                    } else if (columnName.equals(ACCESSION.getName())) {
                        psm.setAccession(checkAccession(column, target));
                    } else if (columnName.equals(UNIQUE.getName())) {
                        psm.setUnique(checkUnique(column, target));
                    } else if (columnName.equals(DATABASE.getName())) {
                        psm.setDatabase(checkDatabase(column, target));
                    } else if (columnName.equals(DATABASE_VERSION.getName())) {
                        psm.setDatabaseVersion(checkDatabaseVersion(column, target));
                    } else if (columnName.equals(SEARCH_ENGINE.getName())) {
                        psm.setSearchEngine(checkSearchEngine(column, target));
                    } else if (columnName.startsWith(SEARCH_ENGINE_SCORE.getName())) {
                        int id = loadSearchEngineScoreId(column.getHeader());
                        psm.setSearchEngineScore(id, checkSearchEngineScore(column, target));
                    } else if (columnName.equals(RELIABILITY.getName())) {
                        psm.setReliability(checkReliability(column, target));
                    } else if (columnName.equals(MODIFICATIONS.getName())) {
                        String sequence = items[exchangeMapping.get(SEQUENCE.getLogicPosition())];
                        psm.setModifications(checkModifications(column, sequence, target));
                    } else if (columnName.equals(RETENTION_TIME.getName())) {
                        psm.setRetentionTime(checkRetentionTime(column, target));
                    } else if (columnName.equals(CHARGE.getName())) {
                        psm.setCharge(checkCharge(column, target));
                    } else if (columnName.equals(EXP_MASS_TO_CHARGE.getName())) {
                        psm.setExpMassToCharge(checkExpMassToCharge(column, target));
                    } else if (columnName.equals(CALC_MASS_TO_CHARGE.getName())) {
                        psm.setCalcMassToCharge(checkCalcMassToCharge(column, target));
                    } else if (columnName.equals(URI.getName())) {
                        psm.setURI(checkURI(column, target));
                    } else if (columnName.equals(SPECTRA_REF.getName())) {
                        psm.setSpectraRef(checkSpectraRef(column, target));
                    } else if (columnName.equals(PRE.getName())) {
                        psm.setPre(checkPre(column, target));
                    } else if (columnName.equals(POST.getName())) {
                        psm.setPost(checkPost(column, target));
                    } else if (columnName.equals(START.getName())) {
                        psm.setStart(checkStart(column, target));
                    } else if (columnName.equals(END.getName())) {
                        psm.setEnd(checkEnd(column, target));
                    }
                    //Abundance can't be reported at psm level
                }  else if (column instanceof OptionColumn) {
                    //Double check, the column name should opt
                    if (columnName.startsWith("opt_")) {
                        Class dataType = column.getDataType();
                        if (dataType.equals(String.class)) {
                            psm.setValue(column.getLogicPosition(), checkString(column, target));
                        } else if (dataType.equals(Double.class)) {
                            psm.setValue(column.getLogicPosition(), checkDouble(column, target));
                        } else if (dataType.equals(MZBoolean.class)) {
                            psm.setValue(column.getLogicPosition(), checkMZBoolean(column, target));
                        }
                    }
                }
            }
        }

        return physicalPosition;
    }

    private String checkAccession(MZTabColumn column, String target) {
        return checkData(column, target, true);
    }

    public PSM getRecord() {

        if(psm == null){
           psm = new PSM(factory, metadata);
        }

        return psm;
    }

    /**
     * For proteins and peptides modifications SHOULD be reported using either UNIMOD or PSI-MOD accessions.
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     */
    protected SplitList<Modification> checkModifications(MZTabColumn column, String sequence, String target) {
        SplitList<Modification> modificationList = super.checkModifications(section, column, target);

        int terminal_position = sequence.length() + 1;
        for (Modification mod : modificationList) {
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
