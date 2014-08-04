package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;

import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseString;

/**
* @author qingwei
* @since 10/02/13
*/
public class SMLLineParser extends MZTabDataLineParser {
    public SMLLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
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
                if (columnName.equals(SmallMoleculeColumn.IDENTIFIER.getName())) {
                    checkIdentifier(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.CHEMICAL_FORMULA.getName())) {
                    checkChemicalFormula(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.SMILES.getName())) {
                    checkSmiles(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.INCHI_KEY.getName())) {
                    checkInchiKey(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.DESCRIPTION.getName())) {
                    checkDescription(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.EXP_MASS_TO_CHARGE.getName())) {
                    checkExpMassToCharge(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.CALC_MASS_TO_CHARGE.getName())) {
                    checkCalcMassToCharge(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.CHARGE.getName())) {
                    checkCharge(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.RETENTION_TIME.getName())) {
                    checkRetentionTime(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.TAXID.getName())) {
                    checkTaxid(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.SPECIES.getName())) {
                    checkSpecies(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.DATABASE.getName())) {
                    checkDatabase(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.DATABASE_VERSION.getName())) {
                    checkDatabaseVersion(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.RELIABILITY.getName())) {
                    checkReliability(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.URI.getName())) {
                    checkURI(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.SPECTRA_REF.getName())) {
                    checkSpectraRef(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.SEARCH_ENGINE.getName())) {
                    checkSearchEngine(column, target);
                } else if (columnName.startsWith(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE.getName())) {
                    checkBestSearchEngineScore(column, target);
                } else if (columnName.startsWith(SmallMoleculeColumn.SEARCH_ENGINE_SCORE.getName())) {
                    checkSearchEngineScore(column, target);
                } else if (columnName.equals(SmallMoleculeColumn.MODIFICATIONS.getName())) {
                    checkModifications(column, target);
                }
            }
        }
    }

    protected int loadStableData(MZTabRecord record, String line) {
        items = line.split("\\s*" + TAB + "\\s*");
        items[items.length - 1] = items[items.length - 1].trim();

        SmallMolecule smallMolecule = (SmallMolecule) record;
        MZTabColumn column;
        String columnName;
        String target;
        SortedMap<String, MZTabColumn> columnMapping = factory.getColumnMapping();
        int physicalPosition = 1;
        String logicalPosition;

        logicalPosition = positionMapping.get(physicalPosition);
        column = columnMapping.get(logicalPosition);
        while (column != null && column instanceof SmallMoleculeColumn) {
            target = items[physicalPosition];
            columnName = column.getName();
            if (columnName.equals(SmallMoleculeColumn.IDENTIFIER.getName())) {
                smallMolecule.setIdentifier(target);
            } else if (columnName.equals(SmallMoleculeColumn.CHEMICAL_FORMULA.getName())) {
                smallMolecule.setChemicalFormula(target);
            } else if (columnName.equals(SmallMoleculeColumn.SMILES.getName())) {
                smallMolecule.setSmiles(target);
            } else if (columnName.equals(SmallMoleculeColumn.INCHI_KEY.getName())) {
                smallMolecule.setInchiKey(target);
            } else if (columnName.equals(SmallMoleculeColumn.DESCRIPTION.getName())) {
                smallMolecule.setDescription(target);
            } else if (columnName.equals(SmallMoleculeColumn.EXP_MASS_TO_CHARGE.getName())) {
                smallMolecule.setExpMassToCharge(target);
            } else if (columnName.equals(SmallMoleculeColumn.CALC_MASS_TO_CHARGE.getName())) {
                smallMolecule.setCalcMassToCharge(target);
            } else if (columnName.equals(SmallMoleculeColumn.CHARGE.getName())) {
                smallMolecule.setCharge(target);
            } else if (columnName.equals(SmallMoleculeColumn.RETENTION_TIME.getName())) {
                smallMolecule.setRetentionTime(target);
            } else if (columnName.equals(SmallMoleculeColumn.TAXID.getName())) {
                smallMolecule.setTaxid(target);
            } else if (columnName.equals(SmallMoleculeColumn.SPECIES.getName())) {
                smallMolecule.setSpecies(target);
            } else if (columnName.equals(SmallMoleculeColumn.DATABASE.getName())) {
                smallMolecule.setDatabase(target);
            } else if (columnName.equals(SmallMoleculeColumn.DATABASE_VERSION.getName())) {
                smallMolecule.setDatabaseVersion(target);
            } else if (columnName.equals(SmallMoleculeColumn.RELIABILITY.getName())) {
                smallMolecule.setReliability(target);
            } else if (columnName.equals(SmallMoleculeColumn.URI.getName())) {
                smallMolecule.setURI(target);
            } else if (columnName.equals(SmallMoleculeColumn.SPECTRA_REF.getName())) {
                smallMolecule.setSpectraRef(target);
            } else if (columnName.equals(SmallMoleculeColumn.SEARCH_ENGINE.getName())) {
                smallMolecule.setSearchEngine(target);
            } else if (columnName.startsWith(SmallMoleculeColumn.BEST_SEARCH_ENGINE_SCORE.getName())) {
                int id = loadBestSearchEngineScoreId(column.getHeader());
                smallMolecule.setBestSearchEngineScore(id, target);
            } else if (columnName.startsWith(SmallMoleculeColumn.SEARCH_ENGINE_SCORE.getName())) {
                int id = loadSearchEngineScoreId(column.getHeader());
                MsRun msRun = (MsRun) column.getElement();
                smallMolecule.setSearchEngineScore(id, msRun, target);
            } else if (columnName.equals(SmallMoleculeColumn.MODIFICATIONS.getName())) {
                smallMolecule.setModifications(target);
            }

            physicalPosition++;
            logicalPosition = positionMapping.get(physicalPosition);
            column = logicalPosition == null ? null : columnMapping.get(logicalPosition);
        }

        return physicalPosition;
    }

    public SmallMolecule getRecord(String line) {
        return (SmallMolecule) super.getRecord(Section.Small_Molecule, line);
    }

    /**
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     * CHEMMODs MUST NOT be used if the modification can be reported using a PSI-MOD or UNIMOD accession.
     * Mass deltas MUST NOT be used for CHEMMODs if the delta can be expressed through a known chemical formula .
     */
    protected SplitList<Modification> checkModifications(MZTabColumn column, String target) {
        SplitList<Modification> modificationList = super.checkModifications(section, column, target);

        for (Modification mod: modificationList) {
            if (mod.getType() == Modification.Type.CHEMMOD) {
                if (target.contains("-MOD:") || target.contains("-UNIMOD:")) {
                    errorList.add(new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString()));
                }

                if (parseChemmodAccession(mod.getAccession()) == null) {
                    errorList.add(new MZTabError(FormatErrorType.CHEMMODSAccession, lineNumber, column.getHeader(), mod.toString()));
                    return null;
                }
            }
        }

        return modificationList;
    }

   private String parseChemmodAccession(String accession) {
        accession = parseString(accession);

        Pattern pattern = Pattern.compile("[+-](\\d+(.\\d+)?)?|(([A-Z][a-z]*)(\\d*))?");
        Matcher matcher = pattern.matcher(accession);

        if (matcher.find()) {
            return accession;
        } else {
            return null;
        }
    }
}
