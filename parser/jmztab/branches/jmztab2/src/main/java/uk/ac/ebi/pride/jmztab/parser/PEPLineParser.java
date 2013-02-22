package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.Modification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String sequence = checkSequence(items[1]);
        String unitId = checkUnitId(items[3]);
        checkAccession(items[2], unitId);
        checkUnique(items[4]);
        checkDatabase(items[5]);
        checkDatabaseVersion(items[6]);
        checkSearchEngine(items[7]);
        checkSearchEngineScore(items[8]);
        checkReliability(items[9]);
        checkModifications(sequence, items[10]);
        checkRetentionTime(items[11]);
        checkCharge(items[12]);
        checkMassToCharge(items[13]);
        checkURI(items[14]);
        checkSpectraRef(unitId, items[15]);

        return 15;
    }

    /**
     * accession should not null.
     * accession MUST be unique within one Unit.
     *
     * If check error return null, else return accession String.
     */
    protected String checkAccession(String accession, String unitId) {
        if (unitId == null) {
            return null;
        }

        String result_accession = checkData(accession, false);

        if (result_accession == null) {
            return result_accession;
        }

        String unitId_accession = unitId + result_accession;
        if (! PRTLineParser.accessionSet.contains(unitId_accession)) {
            new MZTabError(LogicalErrorType.PeptideAccession, lineNumber, accession);
            return null;
        }

        return result_accession;
    }

    /**
     * For proteins and peptides modifications SHOULD be reported using either UNIMOD or PSI-MOD accessions.
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     */
    protected String checkModifications(String sequence, String modifications) {
        String result_modifications = super.checkModifications(modifications);

        List<Modification> modificationList = parseModificationList(section, result_modifications);
        if (modificationList.size() == 0) {
            new MZTabError(FormatErrorType.ModificationList, lineNumber, result_modifications);
            return null;
        }

        int terminal_position = sequence.length() + 1;
        for (Modification mod: modificationList) {
            for (Integer position : mod.getPositionMap().keySet()) {
                if (position > terminal_position || position < 0) {
                    new MZTabError(LogicalErrorType.ModificationPosition, lineNumber, mod.toString());
                    return null;
                }
            }

            if (mod.getType() == Modification.Type.CHEMMOD) {
                new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, mod.toString());
                return null;
            }
        }

        return result_modifications;
    }
}
