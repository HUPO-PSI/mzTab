package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PEPLineParser extends MZTabDataLineParser {
    public PEPLineParser(MZTabColumnFactory factory, Metadata metadata) {
        super(factory, metadata);
    }

    @Override
    protected int checkStableData() {
        String sequence = checkSequence(mapping.get(1), items[1]);
        Unit unit = checkUnitId(mapping.get(3), items[3]);
        checkAccession(mapping.get(2), items[2], unit);
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
            items = line.split("\\s*" + MZTabConstants.TAB + "\\s*");
            items[items.length - 1] = items[items.length - 1].trim();
        }

        String sequence = checkSequence(mapping.get(1), items[1]);
        record.addValue(1, sequence);
        Unit unit = checkUnitId(mapping.get(3), items[3]);
        record.addValue(3, unit.getUnitId());

        record.addValue(2, checkAccession(mapping.get(2), items[2], unit));
        record.addValue(4, checkUnique(mapping.get(4), items[4]));
        record.addValue(5, checkDatabase(mapping.get(5), items[5]));
        record.addValue(6, checkDatabaseVersion(mapping.get(6), items[6]));
        record.addValue(7, checkSearchEngine(mapping.get(7), items[7]));
        record.addValue(8, checkSearchEngineScore(mapping.get(8), items[8]));
        record.addValue(9, checkReliability(mapping.get(9), items[9]));
        record.addValue(10, checkModifications(mapping.get(10), sequence, items[10]));
        record.addValue(11, checkRetentionTime(mapping.get(11), items[11]));
        record.addValue(12, checkCharge(mapping.get(12), items[12]));
        record.addValue(13, checkMassToCharge(mapping.get(13), items[13]));
        record.addValue(14, checkURI(mapping.get(14), items[14]));
        record.addValue(15, checkSpectraRef(mapping.get(15), unit, items[15]));

        return 15;
    }

    public PeptideRecord getRecord(String line) {
        return (PeptideRecord) super.getRecord(Section.Peptide, line);
    }

    /**
     * accession should not null.
     * accession should be found in the protein accession set.
     *
     * If check error return null, else return accession String.
     */
    protected String checkAccession(MZTabColumn column, String accession, Unit unit) {
        String result_accession = checkData(column, accession, false);

        if (result_accession == null) {
            return result_accession;
        }

        String unitId_accession = unit.getUnitId() + result_accession;
        if (! PRTLineParser.accessionSet.contains(unitId_accession)) {
            new MZTabError(LogicalErrorType.PeptideAccession, lineNumber, column.getHeader(), accession);
            return null;
        }

        return result_accession;
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
                    new MZTabError(LogicalErrorType.ModificationPosition, lineNumber, column.getHeader(), mod.toString());
                    return null;
                }
            }

            if (mod.getType() == Modification.Type.CHEMMOD) {
                new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString());
                return null;
            }
        }

        return modificationList;
    }
}
