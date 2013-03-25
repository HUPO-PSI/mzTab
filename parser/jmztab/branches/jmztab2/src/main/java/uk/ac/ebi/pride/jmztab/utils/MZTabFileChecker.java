package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorType;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.model.Peptide;
import uk.ac.ebi.pride.jmztab.model.Protein;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

/**
 * User: Qingwei
 * Date: 22/03/13
 */
public class MZTabFileChecker {
    private MZTabErrorList errorList;

    public MZTabFileChecker(MZTabErrorList errorList) {
        this.errorList = errorList == null ? new MZTabErrorList() : errorList;
    }

    public boolean check(MZTabFile mzTabFile) {
        Set<String> unitAccessionSet = new HashSet<String>();
        Set<String> accessionSet = new HashSet<String>();

        // Stage 1: check unitId + accession should be unique in Protein section.
        Protein protein;
        SortedMap<Integer, Protein> proteins = mzTabFile.getProteinsWithLineNumber();
        MZTabColumnFactory proteinColumnFactory = mzTabFile.getProteinColumnFactory();
        String accession;
        String unitId;
        for (Integer lineNumber : proteins.keySet()) {
            protein = proteins.get(lineNumber);
            accession = protein.getAccession();
            unitId = protein.getUnitId();
            if (! unitAccessionSet.add(unitId + accession)) {
                this.errorList.add(new MZTabError(LogicalErrorType.DuplicationAccession, lineNumber, proteinColumnFactory.getColumn(1).getHeader(), accession, unitId));
            }
            accessionSet.add(accession);
        }

        // If level is error, ignore stage 2 check.
        if (MZTabProperties.LEVEL == MZTabErrorType.Level.Error) {
            return errorList.isEmpty();
        }

        // Stage 2: check accession of Peptide section, which may be display in the protein section.
        // This is Warn level message.
        SortedMap<Integer, Peptide> peptides = mzTabFile.getPeptidesWithLineNumber();
        MZTabColumnFactory peptideColumnFactory = mzTabFile.getPeptideColumnFactory();
        Peptide peptide;
        for (Integer lineNumber : peptides.keySet()) {
            peptide = peptides.get(lineNumber);
            if (! accessionSet.contains(peptide.getAccession())) {
                errorList.add(new MZTabError(LogicalErrorType.PeptideAccession, lineNumber, peptideColumnFactory.getColumn(2).getHeader(), peptide.getAccession()));
            }
        }

        return errorList.isEmpty();
    }
}
