package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.model.*;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PEPLineParser extends MZTabDataLineParser {
    public PEPLineParser(MZTabColumnFactory factory, Metadata metadata, MZTabErrorList errorList) {
        super(factory, metadata, errorList);
    }

    @Override
    protected int checkStableData() {
        String sequence = checkSequence(mapping.get(1), items[1]);
        Unit unit = checkUnitId(mapping.get(3), items[3]);
        checkUnique(mapping.get(4), items[4]);
        checkDatabase(mapping.get(5), items[5]);
        checkDatabaseVersion(mapping.get(6), items[6]);
        checkSearchEngine(mapping.get(7), items[7]);
        checkSearchEngineScore(mapping.get(8), items[8]);
        checkReliability(mapping.get(9), items[9]);
        checkModifications(mapping.get(10), sequence, items[10]);
        checkRetentionTime(mapping.get(11), items[11]);
        checkCharge(mapping.get(12), items[12]);
        checkMassToCharge(mapping.get(13), items[13]);
        checkURI(mapping.get(14), items[14]);
        checkSpectraRef(mapping.get(15), unit, items[15]);

        return 15;
    }

    @Override
    protected int loadStableData(AbstractMZTabRecord record, String line) {
        if (items == null) {
            items = line.split("\\s*" + TAB + "\\s*");
            items[items.length - 1] = items[items.length - 1].trim();
        }

        Peptide peptide = (Peptide) record;

        peptide.setSequence(items[1]);
        peptide.setAccession(items[2]);
        peptide.setUnitId(items[3]);
        peptide.setUnique(items[4]);
        peptide.setDatabase(items[5]);
        peptide.setDatabaseVersion(items[6]);
        peptide.setSearchEngine(items[7]);
        peptide.setSearchEngineScore(items[8]);
        peptide.setReliability(items[9]);
        peptide.setModifications(items[10]);
        peptide.setRetentionTime(items[11]);
        peptide.setCharge(items[12]);
        peptide.setMassToCharge(items[13]);
        peptide.setURI(items[14]);
        Unit unit = metadata.getUnit(peptide.getUnitId());
        peptide.setSpectraRef(unit, items[15]);

        return 15;
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
                    errorList.add(new MZTabError(LogicalErrorType.ModificationPosition, lineNumber, column.getHeader(), mod.toString()));
                    return null;
                }
            }

            if (mod.getType() == Modification.Type.CHEMMOD) {
                errorList.add(new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString()));
                return null;
            }
        }

        return modificationList;
    }
}
