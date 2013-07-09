package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseParamList;
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
        int offset = 1;

        checkIdentifier(mapping.get(offset), items[offset++]);
        checkChemicalFormula(mapping.get(offset), items[offset++]);
        checkSmiles(mapping.get(offset), items[offset++]);
        checkInchiKey(mapping.get(offset), items[offset++]);
        checkDescription(mapping.get(offset), items[offset++]);
        checkMassToCharge(mapping.get(offset), items[offset++]);
        checkCharge(mapping.get(offset), items[offset++]);
        checkRetentionTime(mapping.get(offset), items[offset++]);
        checkTaxid(mapping.get(offset), items[offset++]);
        checkSpecies(mapping.get(offset), items[offset++]);
        checkDatabase(mapping.get(offset), items[offset++]);
        checkDatabaseVersion(mapping.get(offset), items[offset++]);
        checkReliability(mapping.get(offset), items[offset++]);
        checkURI(mapping.get(offset), items[offset++]);
        checkSpectraRef(mapping.get(offset), items[offset++]);
        checkSearchEngine(mapping.get(offset), items[offset++]);
        checkBestSearchEngineScore(mapping.get(offset), items[offset++]);
        while (mapping.get(offset).getName().equals(SmallMoleculeColumn.SEARCH_ENGINE_SCORE.getName())) {
            checkSearchEngineScore(mapping.get(offset), items[offset++]);
        }
        checkModifications(mapping.get(offset), items[offset]);

        return offset;
    }

    @Override
    protected int loadStableData(MZTabRecord record, String line) {
        items = line.split("\\s*" + TAB + "\\s*");
        items[items.length - 1] = items[items.length - 1].trim();

        int offset = 1;
        SmallMolecule smallMolecule = (SmallMolecule) record;
        smallMolecule.setIdentifier(items[offset++]);
        smallMolecule.setChemicalFormula(items[offset++]);
        smallMolecule.setSmiles(items[offset++]);
        smallMolecule.setInchiKey(items[offset++]);
        smallMolecule.setDescription(items[offset++]);
        smallMolecule.setExpMassToCharge(items[offset++]);
        smallMolecule.setCharge(items[offset++]);
        smallMolecule.setRetentionTime(items[offset++]);
        smallMolecule.setTaxid(items[offset++]);
        smallMolecule.setSpecies(items[offset++]);
        smallMolecule.setDatabase(items[offset++]);
        smallMolecule.setDatabaseVersion(items[offset++]);
        smallMolecule.setReliability(items[offset++]);
        smallMolecule.setURI(items[offset++]);
        smallMolecule.setSpectraRef(items[offset++]);
        smallMolecule.setSearchEngine(items[offset++]);
        smallMolecule.setBestSearchEngineScore(items[offset++]);
        while (mapping.get(offset).getName().equals(SmallMoleculeColumn.SEARCH_ENGINE_SCORE.getName())) {
            smallMolecule.setValue(mapping.get(offset).getPosition(), parseParamList(items[offset++]));
        }
        smallMolecule.setModifications(items[offset]);

        return 19;
    }

    public SmallMolecule getRecord(String line) {
        return (SmallMolecule) super.getRecord(Section.Small_Molecule, line);
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
