package uk.ac.ebi.pride.jmztab.checker;

import uk.ac.ebi.pride.jmztab.errors.*;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabDescription;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.checker.section.*;

import java.io.IOException;
import java.io.OutputStream;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.utils.MZTabProperties.*;

/**
 * Provide integrity checks for mzTab file. It checks the relationship between the information that needs to be define in
 * the metadata and it is used by the optional columns (e.g. search_engine_score)
 *
 * @author ntoro
 * @since 25/07/2014 16:55
 */
public class MZTabFileIntegrityChecker implements MZTabIntegrityChecker {

    private MZTabErrorList errorList;
    private MZTabFile mzTabFile;

    /**
     * Create a new {@code MZTabFileIntegrityChecker} for the given file. Validation output and errors
     * are written to the default output.
     *
     * @param mzTabFile the MZTab file
     * @param errorList list of generated errors during the checking
     * @throws IOException
     */
    public MZTabFileIntegrityChecker(MZTabFile mzTabFile, MZTabErrorList errorList) throws IOException {
        this(mzTabFile, System.out, errorList);
    }

    /**
     * Create a new {@code MZTabFileIntegrityChecker} for the given file. Validation output and errors
     * are written to the provided {@link java.io.OutputStream}.
     *
     * @param mzTabFile the MZTab file
     * @param out       the output stream for checking messages
     * @param errorList list of generated errors during the checking
     * @throws IOException
     */
    public MZTabFileIntegrityChecker(MZTabFile mzTabFile, OutputStream out, MZTabErrorList errorList) throws IOException  {

        if (mzTabFile == null || mzTabFile.isEmpty()) {
            throw new IllegalArgumentException("MZTab File not exists!");
        }

        this.mzTabFile = mzTabFile;
        this.errorList = errorList == null ? new MZTabErrorList() : errorList;

        try {
            check();
        } catch (MZTabErrorOverflowException e) {
            out.write(MZTabErrorOverflowExceptionMessage.getBytes());
        }

        this.errorList.print(out);
        if (this.errorList.isEmpty()) {
            out.write(("MZTabFileIntegrityChecker: no errors found in file!" + NEW_LINE).getBytes());
        }
    }

    /**
     * Create a new {@code MZTabFileIntegrityChecker} for the given file. Validation output and errors
     * are written to the default output.
     *
     * @param mzTabFile the MZTab file
     * @throws java.io.IOException
     */
    public MZTabFileIntegrityChecker(MZTabFile mzTabFile) throws IOException {
        this(mzTabFile, System.out, LEVEL, MAX_ERROR_COUNT);
    }

    /**
     * Create a new {@code MZTabFileIntegrityChecker} for the given file. Validation output and errors
     * are written to the provided {@link java.io.OutputStream}.
     *
     * @param mzTabFile the MZTab file
     * @param out       the output stream for checking messages
     * @throws java.io.IOException
     */
    public MZTabFileIntegrityChecker(MZTabFile mzTabFile, OutputStream out) throws IOException {
        this(mzTabFile, out, LEVEL, MAX_ERROR_COUNT);
    }

    /**
     * Create a new {@code MZTabFileIntegrityChecker} for the given file. Validation output and errors
     * are written to the provided {@link java.io.OutputStream}.
     *
     * @param mzTabFile the MZTab file
     * @param out       the output stream for checking messages
     * @param level     the minimum error level to report errors for
     * @throws java.io.IOException
     */
    public MZTabFileIntegrityChecker(MZTabFile mzTabFile, OutputStream out, MZTabErrorType.Level level) throws IOException {
        this(mzTabFile, out, level, MAX_ERROR_COUNT);
    }

    /**
     * Create a new {@code MZTabFileIntegrityChecker} for the given file. Validation output and errors
     * are written to the provided {@link java.io.OutputStream}.
     *
     * @param mzTabFile     the MZTab file
     * @param out           the output stream for checking messages
     * @param level         the minimum error level to report errors for
     * @param maxErrorCount the maximum number of errors to report in {@see MZTabFileIntegrityChecker#getErrorList()}
     * @throws java.io.IOException
     */
    public MZTabFileIntegrityChecker(MZTabFile mzTabFile, OutputStream out, MZTabErrorType.Level level, int maxErrorCount) throws IOException {
        if (mzTabFile == null || mzTabFile.isEmpty()) {
            throw new IllegalArgumentException("MZTab File not exists!");
        }

        this.mzTabFile = mzTabFile;

        try {
            errorList = new MZTabErrorList(level, maxErrorCount);
            check();
        } catch (MZTabErrorOverflowException e) {
            out.write(MZTabErrorOverflowExceptionMessage.getBytes());
        }

        errorList.print(out);
        if (errorList.isEmpty()) {
            out.write(("MZTabFileIntegrityChecker: no errors found in file!" + NEW_LINE).getBytes());
        }
    }

    /**
     * Do whole {@link MZTabFile} consistency check.
     *
     * @see MetadataIntegrityChecker#check()
     * @see ProteinOptColumnsIntegrityChecker#check()
     * @see PeptideOptColumnsIntegrityChecker#check()
     * @see PsmOptColumnsIntegrityChecker#check()
     * @see SmallMoleculeOptColumnsIntegrityChecker#check()
     */
    public void check() {

        Metadata metadata = mzTabFile.getMetadata();

        MZTabColumnFactory proteinFactory = mzTabFile.getProteinColumnFactory();
        MZTabColumnFactory peptideFactory = mzTabFile.getPeptideColumnFactory();
        MZTabColumnFactory psmFactory = mzTabFile.getPsmColumnFactory();
        MZTabColumnFactory smlFactory = mzTabFile.getSmallMoleculeColumnFactory();


        // If mzTab-type is "Quantification", then at least one section with {protein|peptide|small_molecule}_abundance* columns MUST be present
        boolean hasAbundance = false;
        if (metadata.getMZTabType() == MZTabDescription.Type.Quantification) {
            if (proteinFactory != null && !proteinFactory.getAbundanceColumnMapping().isEmpty()) {
                hasAbundance = true;
            }
            if (peptideFactory != null && !peptideFactory.getAbundanceColumnMapping().isEmpty()) {
                hasAbundance = true;
            }
            if (smlFactory != null && !smlFactory.getAbundanceColumnMapping().isEmpty()) {
                hasAbundance = true;
            }
            if (!hasAbundance) {
                errorList.add(new MZTabError(LogicalErrorType.QuantificationAbundance, -1));
            }
        }

        MZTabIntegrityChecker metadataValidator = new MetadataIntegrityChecker(metadata, errorList);
        metadataValidator.check();

        if (proteinFactory != null) {
            MZTabIntegrityChecker proteinValidator = new ProteinOptColumnsIntegrityChecker(metadata, proteinFactory, errorList);
            proteinValidator.check();
        }
        if (peptideFactory != null) {
            MZTabIntegrityChecker peptideValidator = new PeptideOptColumnsIntegrityChecker(metadata, peptideFactory, errorList);
            peptideValidator.check();
        }
        if (psmFactory != null) {
            MZTabIntegrityChecker psmValidator = new PsmOptColumnsIntegrityChecker(metadata, psmFactory, errorList);
            psmValidator.check();
        }
        if (smlFactory != null) {
            MZTabIntegrityChecker smlValidator = new SmallMoleculeOptColumnsIntegrityChecker(metadata, smlFactory, errorList);
            smlValidator.check();
        }

    }

    @Override
    public MZTabErrorList getErrorList() {
        return errorList;
    }

    public void setErrorList(MZTabErrorList errorList) {
        this.errorList = errorList;
    }

}
