package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.MZTabColumn;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.Modification;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.util.List;

import static uk.ac.ebi.pride.jmztab.parser.MZTabParserUtils.parseModificationList;

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
        String unitId = checkUnitId(mapping.get(3), items[3]);
        checkAccession(mapping.get(2), items[2], unitId);
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
        checkSpectraRef(mapping.get(15), unitId, items[15]);

        return 15;
    }

    /**
     * accession should not null.
     * accession MUST be unique within one Unit.
     *
     * If check error return null, else return accession String.
     */
    protected String checkAccession(MZTabColumn column, String accession, String unitId) {
        if (unitId == null) {
            return null;
        }

        String result_accession = checkData(column, accession, false);

        if (result_accession == null) {
            return result_accession;
        }

        String unitId_accession = unitId + result_accession;
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
    protected String checkModifications(MZTabColumn column, String sequence, String modifications) {
        String result_modifications = super.checkModifications(column, modifications);

        if (result_modifications == null || result_modifications.equals(MZTabConstants.NULL)) {
            return result_modifications;
        }

        List<Modification> modificationList = parseModificationList(section, result_modifications);
        if (modificationList.size() == 0) {
            new MZTabError(FormatErrorType.ModificationList, lineNumber, column.getHeader(), result_modifications);
            return null;
        }

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

        return result_modifications;
    }
}
