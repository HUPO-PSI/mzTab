package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import static uk.ac.ebi.pride.jmztab.model.PeptideColumn.*;

/**
* @author qingwei
* @since 10/02/13
*/
public class PEPLineParser extends MZTabDataLineParser {

    private Peptide peptide;

    public PEPLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
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
        peptide = new Peptide(factory, metadata);

        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {
            logicalPosition = positionMapping.get(physicalPosition);
            column = factory.getColumnMapping().get(logicalPosition);

            if (column != null) {
                columnName = column.getName();
                target = items[physicalPosition];

                if (column instanceof PeptideColumn) {

                    if (columnName.equals(SEQUENCE.getName())) {
                        peptide.setSequence(checkSequence(column, target));
                    } else if (columnName.equals(ACCESSION.getName())) {
                        peptide.setAccession(checkAccession(column, target));
                    } else if (columnName.equals(UNIQUE.getName())) {
                        peptide.setUnique(checkUnique(column, target));
                    } else if (columnName.equals(DATABASE.getName())) {
                        peptide.setDatabase(checkDatabase(column, target));
                    } else if (columnName.equals(DATABASE_VERSION.getName())) {
                        peptide.setDatabaseVersion(checkDatabaseVersion(column, target));
                    } else if (columnName.equals(SEARCH_ENGINE.getName())) {
                        peptide.setSearchEngine(checkSearchEngine(column, target));
                    } else if (columnName.startsWith(BEST_SEARCH_ENGINE_SCORE.getName())) {
                        int id = loadBestSearchEngineScoreId(column.getHeader());
                        peptide.setBestSearchEngineScore(id, checkBestSearchEngineScore(column, target));
                    } else if (columnName.startsWith(SEARCH_ENGINE_SCORE.getName())) {
                        int id = loadSearchEngineScoreId(column.getHeader());
                        MsRun msRun = (MsRun) column.getElement();
                        peptide.setSearchEngineScore(id, msRun, checkSearchEngineScore(column, target));
                    } else if (columnName.equals(RELIABILITY.getName())) {
                        peptide.setReliability(checkReliability(column, target));
                    } else if (columnName.equals(MODIFICATIONS.getName())) {
                        String sequence = items[exchangeMapping.get(SEQUENCE.getLogicPosition())];
                        peptide.setModifications(checkModifications(column, sequence, target));
                    } else if (columnName.equals(RETENTION_TIME.getName())) {
                        peptide.setRetentionTime(checkRetentionTime(column, target));
                    } else if (columnName.equals(RETENTION_TIME_WINDOW.getName())) {
                        peptide.setRetentionTimeWindow(checkRetentionTimeWindow(column, target));
                    } else if (columnName.equals(CHARGE.getName())) {
                        peptide.setCharge(checkCharge(column, target));
                    } else if (columnName.equals(MASS_TO_CHARGE.getName())) {
                        peptide.setMassToCharge(checkMassToCharge(column, target));
                    } else if (columnName.equals(URI.getName())) {
                        peptide.setURI(checkURI(column, target));
                    } else if (columnName.equals(SPECTRA_REF.getName())) {
                        peptide.setSpectraRef(checkSpectraRef(column, target));
                    }
                } else if (column instanceof AbundanceColumn) {
                    //Double check, the column name should contain
                    if (columnName.contains("abundance")) {
                        peptide.setValue(logicalPosition, checkDouble(column, target));
                    }
                } else if (column instanceof OptionColumn) {
                    //Double check, the column name should opt
                    if (columnName.startsWith("opt_")) {
                        Class dataType = column.getDataType();
                        if (dataType.equals(String.class)) {
                            peptide.setValue(column.getLogicPosition(), checkString(column, target));
                        } else if (dataType.equals(Double.class)) {
                            peptide.setValue(column.getLogicPosition(), checkDouble(column, target));
                        } else if (dataType.equals(MZBoolean.class)) {
                            peptide.setValue(column.getLogicPosition(), checkMZBoolean(column, target));
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

    public Peptide getRecord() {
        if(peptide == null){
            peptide = new Peptide(factory, metadata);
        }
        return peptide;
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
