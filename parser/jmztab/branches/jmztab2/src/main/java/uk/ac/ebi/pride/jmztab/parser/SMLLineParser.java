package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class SMLLineParser extends MZTabDataLineParser {
    public SMLLineParser(MZTabColumnFactory factory, Metadata metadata) {
        super(factory, metadata);
    }

    @Override
    protected int checkStableData() {
        checkIdentifier(items[1]);
        checkUnitId(items[2]);
        checkChemicalFormula(items[3]);
        checkSmiles(items[4]);
        checkInchiKey(items[5]);
        checkDescription(items[6]);
        checkMassToCharge(items[7]);
        checkCharge(items[8]);
        checkRetentionTime(items[9]);
        checkTaxid(items[10]);
        checkSpecies(items[11]);
        checkDatabase(items[12]);
        checkDatabaseVersion(items[13]);
        checkReliability(items[14]);
        checkURI(items[15]);
        checkSpectraRef(items[2], items[16]);
        checkSearchEngine(items[17]);
        checkSearchEngineScore(items[18]);
        checkModifications(items[19]);

        return 19;
    }

    @Override
    protected String checkDescription(String description) {
        return super.checkDescription(description);
    }
}
