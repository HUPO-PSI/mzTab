package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseInteger;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseParamList;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.parseString;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PRTLineParser extends MZTabDataLineParser {
    private Set<String> accessionSet = new HashSet<String>();

    public PRTLineParser(MZTabColumnFactory factory, PositionMapping positionMapping,
                         Metadata metadata, MZTabErrorList errorList) {
        super(factory, positionMapping, metadata, errorList);
    }

    @Override
    protected int checkStableData() {
        int offset = 1;

        checkAccession(mapping.get(offset), items[offset++]);
        checkDescription(mapping.get(offset), items[offset++]);
        checkTaxid(mapping.get(offset), items[offset++]);
        checkSpecies(mapping.get(offset), items[offset++]);
        checkDatabase(mapping.get(offset), items[offset++]);
        checkDatabaseVersion(mapping.get(offset), items[offset++]);
        checkSearchEngine(mapping.get(offset), items[offset++]);
        checkBestSearchEngineScore(mapping.get(offset), items[offset++]);
        while (mapping.get(offset).getName().equals(ProteinColumn.SEARCH_ENGINE_SCORE.getName())) {
            checkSearchEngineScore(mapping.get(offset), items[offset++]);
        }
        if (factory.findColumn(ProteinColumn.RELIABILITY.getHeader()) != null) {
            checkReliability(mapping.get(offset), items[offset++]);
        }
        while (mapping.get(offset).getName().equals(ProteinColumn.NUM_PSMS.getName())) {
            checkNumPSMs(mapping.get(offset), items[offset++]);
        }
        while (mapping.get(offset).getName().equals(ProteinColumn.NUM_PEPTIDES_DISTINCT.getName())) {
            checkNumPeptidesDistinct(mapping.get(offset), items[offset++]);
        }
        while (mapping.get(offset).getName().equals(ProteinColumn.NUM_PEPTIDES_UNIQUE.getName())) {
            checkNumPeptidesUnique(mapping.get(offset), items[offset++]);
        }
        checkAmbiguityMembers(mapping.get(offset), items[offset++]);
        checkModifications(mapping.get(offset), items[offset++]);
        if (factory.findColumn(ProteinColumn.URI.getHeader()) != null) {
            checkURI(mapping.get(offset), items[offset++]);
        }
        if (factory.findColumn(ProteinColumn.GO_TERMS.getHeader()) != null) {
            checkGOTerms(mapping.get(offset), items[offset++]);
        }
        checkProteinCoverage(mapping.get(offset), items[offset]);

        return offset;
    }

    @Override
    protected int loadStableData(MZTabRecord record, String line) {
        items = line.split("\\s*" + TAB + "\\s*");
        items[items.length - 1] = items[items.length - 1].trim();

        Protein protein = (Protein) record;
        int offset = 1;
        protein.setAccession(items[offset++]);
        protein.setDescription(items[offset++]);
        protein.setTaxid(items[offset++]);
        protein.setSpecies(items[offset++]);
        protein.setDatabase(items[offset++]);
        protein.setDatabaseVersion(items[offset++]);
        protein.setSearchEngine(items[offset++]);
        protein.setBestSearchEngineScore(items[offset++]);
        while (mapping.get(offset).getName().equals(ProteinColumn.SEARCH_ENGINE_SCORE.getName())) {
            protein.setValue(mapping.get(offset).getLogicPosition(), parseParamList(items[offset++]));
        }
        if (factory.findColumn(ProteinColumn.RELIABILITY.getHeader()) != null) {
            protein.setReliability(items[offset++]);
        }
        while (mapping.get(offset).getName().equals(ProteinColumn.NUM_PSMS.getName())) {
            protein.setValue(mapping.get(offset).getLogicPosition(), parseInteger(items[offset++]));
        }
        while (mapping.get(offset).getName().equals(ProteinColumn.NUM_PEPTIDES_DISTINCT.getName())) {
            protein.setValue(mapping.get(offset).getLogicPosition(), parseInteger(items[offset++]));
        }
        while (mapping.get(offset).getName().equals(ProteinColumn.NUM_PEPTIDES_UNIQUE.getName())) {
            protein.setValue(mapping.get(offset).getLogicPosition(), parseInteger(items[offset++]));
        }
        protein.setAmbiguityMembers(items[offset++]);
        protein.setModifications(items[offset++]);
        if (factory.findColumn(ProteinColumn.URI.getHeader()) != null) {
            protein.setURI(items[offset++]);
        }
        if (factory.findColumn(ProteinColumn.GO_TERMS.getHeader()) != null) {
            protein.setGOTerms(items[offset++]);
        }
        protein.setProteinConverage(items[offset]);

        return offset;
    }

    public Protein getRecord(String line) {
        return (Protein) super.getRecord(Section.Protein, line);
    }

    /**
     * accession should not null.
     * accession MUST be unique
     *
     * If check error return null, else return accession String.
     */
    protected String checkAccession(MZTabColumn column, String accession) {
        String result_accession = checkData(column, accession, false);

        if (result_accession == null) {
            return result_accession;
        }

        if (! accessionSet.add(result_accession)) {
            errorList.add(new MZTabError(LogicalErrorType.DuplicationAccession, lineNumber, column.getHeader(), result_accession));
            return null;
        }

        return result_accession;
    }

    /**
     * For proteins and peptides modifications SHOULD be reported using either UNIMOD or PSI-MOD accessions.
     * As these two ontologies are not applicable to small molecules, so-called CHEMMODs can also be defined.
     *
     * Ambiguity of modification position MUST NOT be reported at the Protein level.
     */
    protected SplitList<Modification> checkModifications(MZTabColumn column, String target) {
        SplitList<Modification> modificationList = super.checkModifications(section, column, target);

        for (Modification mod: modificationList) {
            if (mod.getPositionMap().size() > 1) {
                // this is warn
                errorList.add(new MZTabError(LogicalErrorType.AmbiguityMod, lineNumber, column.getHeader(), mod.toString()));
            }

            if (mod.getType() == Modification.Type.CHEMMOD) {
                // this is warn
                errorList.add(new MZTabError(LogicalErrorType.CHEMMODS, lineNumber, column.getHeader(), mod.toString()));
            }

            if (mod.getType() == Modification.Type.SUBST && parseSubstitutionIdentifier(mod.getAccession()) != null) {
                errorList.add(new MZTabError(LogicalErrorType.SubstituteIdentifier, lineNumber, column.getHeader(), mod.toString()));
                return null;
            }
        }

        return modificationList;
    }

    /**
     * In SUBST cases, the "sequence" column MUST contain the original, unaltered sequence.
     */
    private String parseSubstitutionIdentifier(String identifier) {
        identifier = parseString(identifier);
        if (identifier == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\"[^BJOUXZ]+\"");
        Matcher matcher = pattern.matcher(identifier);

        if (matcher.find() && matcher.start() == 0 && matcher.end() == identifier.length()) {
            return identifier;
        } else {
            return null;
        }
    }
}
