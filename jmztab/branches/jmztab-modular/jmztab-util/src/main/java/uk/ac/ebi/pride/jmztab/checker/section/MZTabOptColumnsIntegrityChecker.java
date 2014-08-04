package uk.ac.ebi.pride.jmztab.checker.section;

import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabDescription;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.checker.MZTabIntegrityChecker;

/**
 * Abstract class for optional columns integrity checker.
 *
 * @author ntoro
 * @since 25/07/2014 17:12
 */
public abstract class MZTabOptColumnsIntegrityChecker implements MZTabIntegrityChecker {

    protected Metadata metadata;
    protected MZTabColumnFactory columnFactory;
    protected MZTabErrorList errorList;

    /**
     *
     * @param metadata defined in the mzTab file
     * @param columnFactory of the section that needs to be checked
     */
    public MZTabOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory columnFactory) {
        this(metadata, columnFactory, null);
    }

    /**
     *
     * @param metadata defined in the mzTab file
     * @param columnFactory of the section that needs to be checked
     * @param errorList MZTabErrorList to append the results of the check process
     */
    public MZTabOptColumnsIntegrityChecker(Metadata metadata, MZTabColumnFactory columnFactory, MZTabErrorList errorList) {
        if (metadata == null) {
            throw new IllegalArgumentException("Metadata metadata can not be null");
        }

        if (columnFactory == null) {
            throw new IllegalArgumentException("MZTabColumnFactory columnFactory can not be null");
        }

        if (errorList == null) {
            this.errorList = new MZTabErrorList();
        }

        this.errorList = errorList;
        this.metadata = metadata;
        this.columnFactory = columnFactory;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public MZTabColumnFactory getColumnFactory() {
        return columnFactory;
    }

    public void setColumnFactory(MZTabColumnFactory columnFactory) {
        this.columnFactory = columnFactory;
    }

    @Override
    public MZTabErrorList getErrorList() {
        return errorList;
    }

    public void setErrorList(MZTabErrorList errorList) {
        this.errorList = errorList;
    }

    public abstract void check();

    /**
     * Refine optional columns based one {@link MZTabDescription#mode} and {@link MZTabDescription#type}
     * These re-check operation will called in {@link #check()} method.
     */
    protected void refineOptionalColumn(MZTabDescription.Mode mode, MZTabDescription.Type type,
                                        MZTabColumnFactory factory, String columnHeader) {
        if (factory.findColumnByHeader(columnHeader) == null) {
            errorList.add(new MZTabError(LogicalErrorType.NotDefineInHeader, -1, columnHeader, factory.getSection().getName(), mode.toString(), type.toString()));
        }
    }
}
