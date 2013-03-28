package uk.ac.ebi.pride.jmztab.parser;

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
public class PRTLineParser extends MZTabDataLineParser {
    public PRTLineParser(MZTabColumnFactory factory, Metadata metadata, MZTabErrorList errorList) {
        super(factory, metadata, errorList);
    }

    @Override
    protected int checkStableData() {
        checkUnitId(mapping.get(2), items[2]).getUnitId();
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
    protected int loadStableData(MZTabRecord record, String line) {
        if (items == null) {
            items = line.split("\\s*" + TAB + "\\s*");
            items[items.length - 1] = items[items.length - 1].trim();
        }

        Protein protein = (Protein) record;
        protein.setAccession(items[1]);
        protein.setUnitId(items[2]);
        protein.setDescription(items[3]);
        protein.setTaxid(items[4]);
        protein.setSpecies(items[5]);
        protein.setDatabase(items[6]);
        protein.setDatabaseVersion(items[7]);
        protein.setSearchEngine(items[8]);
        protein.setSearchEngineScore(items[9]);
        protein.setReliability(items[10]);
        protein.setNumPeptides(items[11]);
        protein.setNumPeptideDistinct(items[12]);
        protein.setNumPeptidesUnambiguous(items[13]);
        protein.setAmbiguityMembers(items[14]);
        protein.setModifications(items[15]);
        protein.setURI(items[16]);
        protein.setGOTerms(items[17]);
        protein.setProteinConverage(items[18]);

        return 18;
    }

    public Protein getRecord(String line) {
        return (Protein) super.getRecord(Section.Protein, line);
    }

//    /**
//     * accession should not null.
//     * accession MUST be unique within one Unit.
//     *
//     * If check error return null, else return accession String.
//     */
//    protected String checkAccession(MZTabColumn column, String accession, String unitId) {
//        if (unitId == null) {
//            return null;
//        }
//
//        String result_accession = checkData(column, accession, false);
//
//        if (result_accession == null) {
//            return result_accession;
//        }
//
//        String unitId_accession = unitId + result_accession;
//        if (! accessionSet.add(unitId_accession)) {
//            errorList.add(new MZTabError(LogicalErrorType.DuplicationAccession, lineNumber, column.getHeader(), result_accession, unitId));
//            return null;
//        }
//
//        return result_accession;
//    }

    /**
     * For proteins and peptides modifications SHOULD be reported using either UNIMOD or PSI-MOD accessions.
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     */
    protected SplitList<Modification> checkModifications(MZTabColumn column, String target) {
        SplitList<Modification> modificationList = super.checkModifications(section, column, target);

        for (Modification mod: modificationList) {
            if (mod.getType() == Modification.Type.CHEMMOD) {
                errorList.add(new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString()));
                return null;
            }

            if (mod.getType() == Modification.Type.SUBST && parseSubstitutionIdentifier(mod.getAccession()) != null) {
                errorList.add(new MZTabError(LogicalErrorType.SubstituteIdentifier, lineNumber, column.getHeader(), mod.toString()));
                return null;
            }
        }

        return modificationList;
    }

    private String parseSubstitutionIdentifier(String identifier) {
        identifier = parseString(identifier);
        if (identifier == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\"[A-Z]+\"");
        Matcher matcher = pattern.matcher(identifier);

        if (matcher.find() && matcher.start() == 0 && matcher.end() == identifier.length()) {
            return identifier;
        } else {
            return null;
        }
    }
}
