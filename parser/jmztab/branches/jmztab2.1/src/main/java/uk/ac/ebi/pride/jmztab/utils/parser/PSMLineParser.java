package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;

/**
 * User: Qingwei
 * Date: 07/06/13
 */
public class PSMLineParser extends MZTabDataLineParser {
    public PSMLineParser(MZTabColumnFactory factory, Metadata metadata, MZTabErrorList errorList) {
        super(factory, metadata, errorList);
    }

    @Override
    protected int checkStableData() {
        int offset = 1;

        checkSequence(mapping.get(offset), items[offset++]);
        checkPSMID(mapping.get(offset), items[offset++]);
        checkAccession(mapping.get(offset), items[offset++]);
        checkUnique(mapping.get(offset), items[offset++]);
        checkDatabase(mapping.get(offset), items[offset++]);
        checkDatabaseVersion(mapping.get(offset), items[offset++]);
        checkSearchEngine(mapping.get(offset), items[offset++]);
        checkSearchEngineScore(mapping.get(offset), items[offset++]);
        if (factory.findColumn(ProteinColumn.RELIABILITY.getHeader()) != null) {
            checkReliability(mapping.get(offset), items[offset++]);
        }
        checkModifications(mapping.get(offset), items[1], items[offset++]);
        checkRetentionTime(mapping.get(offset), items[offset++]);
        checkCharge(mapping.get(offset), items[offset++]);
        checkExpMassToCharge(mapping.get(offset), items[offset++]);
        checkCalcMassToCharge(mapping.get(offset), items[offset++]);
        if (factory.findColumn(ProteinColumn.URI.getHeader()) != null) {
            checkURI(mapping.get(offset), items[offset++]);
        }
        checkSpectraRef(mapping.get(offset), items[offset++]);
        checkPre(mapping.get(offset), items[offset++]);
        checkPost(mapping.get(offset), items[offset++]);
        checkStart(mapping.get(offset), items[offset++]);
        checkEnd(mapping.get(offset), items[offset]);

        return offset;
    }

    @Override
    protected int loadStableData(MZTabRecord record, String line) {
        items = line.split("\\s*" + TAB + "\\s*");
        items[items.length - 1] = items[items.length - 1].trim();

        PSM psm = (PSM) record;
        int offset = 1;
        psm.setSequence(items[offset++]);
        psm.setPSM_ID(items[offset++]);
        psm.setAccession(items[offset++]);
        psm.setUnique(items[offset++]);
        psm.setDatabase(items[offset++]);
        psm.setDatabaseVersion(items[offset++]);
        psm.setSearchEngine(items[offset++]);
        psm.setSearchEngineScore(items[offset++]);
        if (factory.findColumn(PSMColumn.RELIABILITY.getHeader()) != null) {
            psm.setReliability(items[offset++]);
        }
        psm.setModifications(items[offset++]);
        psm.setRetentionTime(items[offset++]);
        psm.setCharge(items[offset++]);
        psm.setExpMassToCharge(items[offset++]);
        psm.setCalcMassToCharge(items[offset++]);
        if (factory.findColumn(PSMColumn.URI.getHeader()) != null) {
            psm.setURI(items[offset++]);
        }
        psm.setSpectraRef(items[offset++]);
        psm.setPre(items[offset++]);
        psm.setPost(items[offset++]);
        psm.setStart(items[offset++]);
        psm.setEnd(items[offset]);

        return offset;
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
