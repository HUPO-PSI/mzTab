package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import static uk.ac.ebi.pride.jmztab.parser.MZTabParserUtils.parseChemmodAccession;

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
        Unit unit = checkUnitId(mapping.get(2), items[2]);
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
        checkSpectraRef(mapping.get(16), unit, items[16]);
        checkSearchEngine(mapping.get(17), items[17]);
        checkSearchEngineScore(mapping.get(18), items[18]);
        checkModifications(mapping.get(19), items[19]);

        return 19;
    }

    @Override
    protected int loadStableData(AbstractMZTabRecord record, String line) {
        if (items == null) {
            items = line.split("\\s*" + MZTabConstants.TAB + "\\s*");
            items[items.length - 1] = items[items.length - 1].trim();
        }

        record.addValue(1, checkIdentifier(mapping.get(1), items[1]));
        Unit unit = checkUnitId(mapping.get(2), items[2]);
        record.addValue(2, unit.getUnitId());

        record.addValue(3, checkChemicalFormula(mapping.get(3), items[3]));
        record.addValue(4, checkSmiles(mapping.get(4), items[4]));
        record.addValue(5, checkInchiKey(mapping.get(5), items[5]));
        record.addValue(6, checkDescription(mapping.get(6), items[6]));
        record.addValue(7, checkMassToCharge(mapping.get(7), items[7]));
        record.addValue(8, checkCharge(mapping.get(8), items[8]));
        record.addValue(9, checkRetentionTime(mapping.get(9), items[9]));
        record.addValue(10, checkTaxid(mapping.get(10), items[10]));
        record.addValue(11, checkSpecies(mapping.get(11), items[11]));
        record.addValue(12, checkDatabase(mapping.get(12), items[12]));
        record.addValue(13, checkDatabaseVersion(mapping.get(13), items[13]));
        record.addValue(14, checkReliability(mapping.get(14), items[14]));
        record.addValue(15, checkURI(mapping.get(15), items[15]));
        record.addValue(16, checkSpectraRef(mapping.get(16), unit, items[16]));
        record.addValue(17, checkSearchEngine(mapping.get(17), items[17]));
        record.addValue(18, checkSearchEngineScore(mapping.get(18), items[18]));
        record.addValue(19, checkModifications(mapping.get(19), items[19]));

        return 19;
    }

    public SmallMoleculeRecord getRecord(String line) {
        return (SmallMoleculeRecord) super.getRecord(Section.Small_Molecule, line);
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
    protected SplitList<Modification> checkModifications(MZTabColumn column, String target) {
        SplitList<Modification> modificationList = super.checkModifications(section, column, target);

        for (Modification mod: modificationList) {
            if (mod.getType() == Modification.Type.CHEMMOD) {
                if (target.contains("-MOD:") || target.contains("-UNIMOD:")) {
                    new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString());
                    return null;
                }

                if (parseChemmodAccession(mod.getAccession()) == null) {
                    new MZTabError(FormatErrorType.CHEMMODSAccession, lineNumber, column.getHeader(), mod.toString());
                    return null;
                }
            }
        }

        return modificationList;
    }
}
