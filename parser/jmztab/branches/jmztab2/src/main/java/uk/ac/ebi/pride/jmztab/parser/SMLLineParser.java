package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.model.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseString;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class SMLLineParser extends MZTabDataLineParser {
    public SMLLineParser(MZTabColumnFactory factory, Metadata metadata, MZTabErrorList errorList) {
        super(factory, metadata, errorList);
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
    protected int loadStableData(MZTabRecord record, String line) {
        if (items == null) {
            items = line.split("\\s*" + TAB + "\\s*");
            items[items.length - 1] = items[items.length - 1].trim();
        }

        SmallMolecule smallMolecule = (SmallMolecule) record;
        smallMolecule.setIdentifier(items[1]);
        smallMolecule.setUnitId(items[2]);
        smallMolecule.setChemicalFormula(items[3]);
        smallMolecule.setSmiles(items[4]);
        smallMolecule.setInchiKey(items[5]);
        smallMolecule.setDescription(items[6]);
        smallMolecule.setMassToCharge(items[7]);
        smallMolecule.setCharge(items[8]);
        smallMolecule.setRetentionTime(items[9]);
        smallMolecule.setTaxid(items[10]);
        smallMolecule.setSpecies(items[11]);
        smallMolecule.setDatabase(items[12]);
        smallMolecule.setDatabaseVersion(items[13]);
        smallMolecule.setReliability(items[14]);
        smallMolecule.setURI(items[15]);
        Unit unit = metadata.getUnit(smallMolecule.getUnitId());
        smallMolecule.setSpectraRef(unit, items[16]);
        smallMolecule.setSearchEngine(items[17]);
        smallMolecule.setSearchEngineScore(items[18]);
        smallMolecule.setModifications(items[19]);

        return 19;
    }

    public SmallMolecule getRecord(String line) {
        return (SmallMolecule) super.getRecord(Section.Small_Molecule, line);
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
                    errorList.add(new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString()));
                    return null;
                }

                if (parseChemmodAccession(mod.getAccession()) == null) {
                    errorList.add(new MZTabError(FormatErrorType.CHEMMODSAccession, lineNumber, column.getHeader(), mod.toString()));
                    return null;
                }
            }
        }

        return modificationList;
    }

   private String parseChemmodAccession(String accession) {
        accession = parseString(accession);

        Pattern pattern = Pattern.compile("[+-](\\d+(.\\d+)?)?|(([A-Z][a-z]*)(\\d*))?");
        Matcher matcher = pattern.matcher(accession);

        if (matcher.find()) {
            return accession;
        } else {
            return null;
        }
    }


}
