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
public class PRTLineParser extends MZTabDataLineParser {
    public PRTLineParser(MZTabColumnFactory factory, Metadata metadata) {
        super(factory, metadata);
    }

    @Override
    protected int checkStableData() {
        checkAccession(items[1], items[2]);
        checkUnitId(items[2]);
        checkDescription(items[3]);
        checkTaxid(items[4]);
        checkSpecies(items[5]);
        checkDatabase(items[6]);
        checkDatabaseVersion(items[7]);
        checkSearchEngine(items[8]);
        checkSearchEngineScore(items[9]);
        checkReliability(items[10]);
        checkNumPeptides(items[11]);
        checkNumPeptidesDistinct(items[12]);
        checkNumPeptidesUnambiguous(items[13]);
        checkAmbiguityMembers(items[14]);
        checkModifications(items[15]);
        checkURI(items[16]);
        checkGOTerms(items[17]);
        checkProteinCoverage(items[18]);

        return 18;
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
