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
        checkIdentifier(mapping.get(1), items[1]);
        String unitId = checkUnitId(mapping.get(2), items[2]);
        checkChemicalFormula(mapping.get(3), items[3]);
        checkSmiles(mapping.get(4), items[4]);
        checkInchiKey(mapping.get(5), items[5]);
        checkDescription(mapping.get(6), items[6]);
        checkMassToCharge(mapping.get(7), items[7]);
        checkCharge(mapping.get(8), items[8]);
        checkRetentionTime(mapping.get(9), items[9]);
        checkTaxid(mapping.get(10), items[10]);
        checkSpecies(mapping.get(11), items[11]);
        checkDatabase(mapping.get(12), items[12]);
        checkDatabaseVersion(mapping.get(13), items[13]);
        checkReliability(mapping.get(14), items[14]);
        checkURI(mapping.get(15), items[15]);
        checkSpectraRef(mapping.get(16), unitId, items[16]);
        checkSearchEngine(mapping.get(17), items[17]);
        checkSearchEngineScore(mapping.get(18), items[18]);
        checkModifications(mapping.get(19), items[19]);

        return 19;
    }

    @Override
    protected String checkDescription(MZTabColumn column, String description) {
        return super.checkDescription(column, description);
    }

    /**
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     * CHEMMODs MUST NOT be used if the modification can be reported using a PSI-MOD or UNIMOD accession.
     * Mass deltas MUST NOT be used for CHEMMODs if the delta can be expressed through a known chemical formula .
     */
    protected String checkModifications(MZTabColumn column, String modifications) {
        String result_modifications = super.checkModifications(column, modifications);

        if (result_modifications == null || result_modifications.equals(MZTabConstants.NULL)) {
            return result_modifications;
        }

        List<Modification> modificationList = parseModificationList(section, result_modifications);
        if (modificationList.size() == 0) {
            new MZTabError(FormatErrorType.ModificationList, lineNumber, column.getHeader(), result_modifications);
            return null;
        }

        for (Modification mod: modificationList) {
            if (mod.getType() == Modification.Type.CHEMMOD) {
                if (modifications.contains("-MOD:") || modifications.contains("-UNIMOD:")) {
                    new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString());
                    return null;
                }

                if (parseChemmodAccession(mod.getAccession()) == null) {
                    new MZTabError(FormatErrorType.CHEMMODSAccession, lineNumber, column.getHeader(), mod.toString());
                    return null;
                }
            }
        }

        return result_modifications;
    }
}
