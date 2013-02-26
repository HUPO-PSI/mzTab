package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.util.HashSet;
import java.util.Set;

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
        String unit_id = checkUnitId(mapping.get(2), items[2]).getUnitId();
        checkAccession(mapping.get(1), items[1], unit_id);
        checkDescription(mapping.get(3), items[3]);
        checkTaxid(mapping.get(4), items[4]);
        checkSpecies(mapping.get(5), items[5]);
        checkDatabase(mapping.get(6), items[6]);
        checkDatabaseVersion(mapping.get(7), items[7]);
        checkSearchEngine(mapping.get(8), items[8]);
        checkSearchEngineScore(mapping.get(9), items[9]);
        checkReliability(mapping.get(10), items[10]);
        checkNumPeptides(mapping.get(11), items[11]);
        checkNumPeptidesDistinct(mapping.get(12), items[12]);
        checkNumPeptidesUnambiguous(mapping.get(13), items[13]);
        checkAmbiguityMembers(mapping.get(14), items[14]);
        checkModifications(mapping.get(15), items[15]);
        checkURI(mapping.get(16), items[16]);
        checkGOTerms(mapping.get(17), items[17]);
        checkProteinCoverage(mapping.get(18), items[18]);

        return 18;
    }

    @Override
    protected int loadStableData(AbstractMZTabRecord record, String line) {
        if (items == null) {
            items = line.split("\\s*" + MZTabConstants.TAB + "\\s*");
            items[items.length - 1] = items[items.length - 1].trim();
        }

        String unit_id = checkUnitId(mapping.get(2), items[2]).getUnitId();
        record.addValue(2, unit_id);

        record.addValue(1, checkAccession(mapping.get(1), items[1], unit_id));
        record.addValue(3, checkDescription(mapping.get(3), items[3]));
        record.addValue(4, checkTaxid(mapping.get(4), items[4]));
        record.addValue(5, checkSpecies(mapping.get(5), items[5]));
        record.addValue(6, checkDatabase(mapping.get(6), items[6]));
        record.addValue(7, checkDatabaseVersion(mapping.get(7), items[7]));
        record.addValue(8, checkSearchEngine(mapping.get(8), items[8]));
        record.addValue(9, checkSearchEngineScore(mapping.get(9), items[9]));
        record.addValue(10, checkReliability(mapping.get(10), items[10]));
        record.addValue(11, checkNumPeptides(mapping.get(11), items[11]));
        record.addValue(12, checkNumPeptidesDistinct(mapping.get(12), items[12]));
        record.addValue(13, checkNumPeptidesUnambiguous(mapping.get(13), items[13]));
        record.addValue(14, checkAmbiguityMembers(mapping.get(14), items[14]));
        record.addValue(15, checkModifications(mapping.get(15), items[15]));
        record.addValue(16, checkURI(mapping.get(16), items[16]));
        record.addValue(17, checkGOTerms(mapping.get(17), items[17]));
        record.addValue(18, checkProteinCoverage(mapping.get(18), items[18]));

        return 18;
    }

    public ProteinRecord getRecord(String line) {
        return (ProteinRecord) super.getRecord(Section.Protein, line);
    }

    // accession + unitId should be unique.
    public static Set<String> accessionSet = new HashSet<String>();

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
        if (! accessionSet.add(unitId_accession)) {
            new MZTabError(LogicalErrorType.DuplicationAccession, lineNumber, column.getHeader(), result_accession, unitId);
            return null;
        }

        return result_accession;
    }

    /**
     * For proteins and peptides modifications SHOULD be reported using either UNIMOD or PSI-MOD accessions.
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     */
    protected SplitList<Modification> checkModifications(MZTabColumn column, String target) {
        SplitList<Modification> modificationList = super.checkModifications(section, column, target);

        for (Modification mod: modificationList) {
            if (mod.getType() == Modification.Type.CHEMMOD) {
                new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString());
                return null;
            }

            if (mod.getType() == Modification.Type.SUBST && parseSubstitutionIdentifier(mod.getAccession()) != null) {
                new MZTabError(LogicalErrorType.SubstituteIdentifier, lineNumber, column.getHeader(), mod.toString());
                return null;
            }
        }

        return modificationList;
    }
}
