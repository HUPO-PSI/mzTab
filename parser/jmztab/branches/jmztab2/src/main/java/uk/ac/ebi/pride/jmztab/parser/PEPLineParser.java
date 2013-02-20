package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;

import java.util.HashSet;
import java.util.Set;

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
        checkSequence(items[1]);
        checkAccession(items[2], items[3]);
        checkUnitId(items[3]);
        checkUnique(items[4]);
        checkDatabase(items[5]);
        checkDatabaseVersion(items[6]);
        checkSearchEngine(items[7]);
        checkSearchEngineScore(items[8]);
        checkReliability(items[9]);
        checkModifications(items[10]);
        checkRetentionTime(items[11]);
        checkCharge(items[12]);
        checkMassToCharge(items[13]);
        checkURI(items[14]);
        checkSpectraRef(items[3], items[15]);

        return 15;
    }

    // accession + unitId should be unique.
    private static Set<String> accessionSet = new HashSet<String>();

    /**
     * accession should not null.
     * accession MUST be unique within one Unit.
     *
     * If check error return null, else return accession String.
     */
    protected String checkAccession(String accession, String unitId) {
        String result_accession = checkData(accession, false);

        if (result_accession == null) {
            return result_accession;
        }

        String unitId_accession = unitId + result_accession;
        if (! accessionSet.add(unitId_accession)) {
            new MZTabError(LogicalErrorType.DuplicationAccession, lineNumber, result_accession, unitId);
            return null;
        }

        return result_accession;
    }


}
