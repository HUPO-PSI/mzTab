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
import static uk.ac.ebi.pride.jmztab.parser.MZTabParserUtils.parseSubstitutionIdentifier;

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
        String unit_id = checkUnitId(items[2]);
        checkAccession(items[1], unit_id);
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
    public static Set<String> accessionSet = new HashSet<String>();

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
        if (! accessionSet.add(unitId_accession)) {
            new MZTabError(LogicalErrorType.DuplicationAccession, lineNumber, result_accession, unitId);
            return null;
        }

        return result_accession;
    }

    /**
     * For proteins and peptides modifications SHOULD be reported using either UNIMOD or PSI-MOD accessions.
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
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
                new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, mod.toString());
                return null;
            }

            if (mod.getType() == Modification.Type.SUBST && parseSubstitutionIdentifier(mod.getAccession()) != null) {
                new MZTabError(LogicalErrorType.SubstituteIdentifier, lineNumber, mod.toString());
                return null;
            }
        }

        return result_modifications;
    }
}
