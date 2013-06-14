package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseParamList;

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
        int offset = 1;

        checkSequence(mapping.get(offset), items[offset++]);
        checkAccession(mapping.get(offset), items[offset++]);
        checkUnique(mapping.get(offset), items[offset++]);
        checkDatabase(mapping.get(offset), items[offset++]);
        checkDatabaseVersion(mapping.get(offset), items[offset++]);
        checkSearchEngine(mapping.get(offset), items[offset++]);
        checkBestSearchEngineScore(mapping.get(offset), items[offset++]);
        while (mapping.get(offset).getName().equals(PeptideColumn.SEARCH_ENGINE_SCORE.getName())) {
            checkSearchEngineScore(mapping.get(offset), items[offset++]);
        }
        checkReliability(mapping.get(offset), items[offset++]);
        checkModifications(mapping.get(offset), items[1], items[offset++]);
        checkRetentionTime(mapping.get(offset), items[offset++]);
        checkCharge(mapping.get(offset), items[offset++]);
        checkMassToCharge(mapping.get(offset), items[offset++]);
        checkURI(mapping.get(offset), items[offset++]);
        checkSpectraRef(mapping.get(offset), items[offset]);

        return offset;
    }

    @Override
    protected int loadStableData(MZTabRecord record, String line) {
        items = line.split("\\s*" + TAB + "\\s*");
        items[items.length - 1] = items[items.length - 1].trim();

        Peptide peptide = (Peptide) record;
        int offset = 1;
        peptide.setSequence(items[offset++]);
        peptide.setAccession(items[offset++]);
        peptide.setUnique(items[offset++]);
        peptide.setDatabase(items[offset++]);
        peptide.setDatabaseVersion(items[offset++]);
        peptide.setSearchEngine(items[offset++]);
        peptide.setBestSearchEngineScore(items[offset++]);
        while (mapping.get(offset).getName().equals(PeptideColumn.SEARCH_ENGINE_SCORE.getName())) {
            peptide.setValue(mapping.get(offset).getPosition(), parseParamList(items[offset++]));
        }
        peptide.setReliability(items[offset++]);
        peptide.setModifications(items[offset++]);
        peptide.setRetentionTime(items[offset++]);
        peptide.setCharge(items[offset++]);
        peptide.setMassToCharge(items[offset++]);
        peptide.setURI(items[offset++]);
        peptide.setSpectraRef(items[offset]);

        return offset;
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
