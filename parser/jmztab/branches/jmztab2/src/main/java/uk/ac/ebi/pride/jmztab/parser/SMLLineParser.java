package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.Modification;

import java.util.List;

import static uk.ac.ebi.pride.jmztab.parser.MZTabParserUtils.parseChemmodAccession;
import static uk.ac.ebi.pride.jmztab.parser.MZTabParserUtils.parseModificationList;

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
        String unitId = checkUnitId(items[2]);
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
        checkSpectraRef(unitId, items[16]);
        checkSearchEngine(items[17]);
        checkSearchEngineScore(items[18]);
        checkModifications(items[19]);

        return 19;
    }

    @Override
    protected String checkDescription(String description) {
        return super.checkDescription(description);
    }

    /**
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     * CHEMMODs MUST NOT be used if the modification can be reported using a PSI-MOD or UNIMOD accession.
     * Mass deltas MUST NOT be used for CHEMMODs if the delta can be expressed through a known chemical formula .
     */
    protected String checkModifications(String modifications) {
        String result_modifications = super.checkModifications(modifications);

        List<Modification> modificationList = parseModificationList(section, result_modifications);
        if (modificationList.size() == 0) {
            new MZTabError(FormatErrorType.ModificationList, lineNumber, result_modifications);
            return null;
        }

        for (Modification mod: modificationList) {
            if (mod.getType() == Modification.Type.CHEMMOD) {
                if (modifications.contains("-MOD:") || modifications.contains("-UNIMOD:")) {
                    new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, mod.toString());
                    return null;
                }

                if (parseChemmodAccession(mod.getAccession()) == null) {
                    new MZTabError(FormatErrorType.CHEMMODSAccession, lineNumber, mod.toString());
                    return null;
                }
            }
        }

        return result_modifications;
    }
}
