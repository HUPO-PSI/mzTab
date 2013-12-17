package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A framework for convert some other source into mzTab. {@link T} used to set data source,
 * {@link V} is a object store parameter(s) which maybe used in processing data source.
 *
 * User: Qingwei
 * Date: 12/03/13
 */
public abstract class ConvertProvider<T, V> {
    protected T source;
    protected V params;
    private Metadata metadata;
    private MZTabColumnFactory proteinColumnFactory;
    private MZTabColumnFactory peptideColumnFactory;
    private MZTabColumnFactory psmColumnFactory;
    private MZTabColumnFactory smallMoleculeColumnFactory;
    protected Collection<Protein> proteins;
    protected Collection<Peptide> peptides;
    protected Collection<PSM> psms;
    protected Collection<SmallMolecule> smallMolecules;

    private MZTabFile mzTabFile;

    /**
     * A framework for convert some other source into mzTab. {@link T} used to set data source,
     * {@link V} is a object store parameter(s) which maybe used in processing data source.
     */
    public ConvertProvider(T source, V params) {
        this.source = source;
        this.params = params;

        init();
        createArchitecture();
    }

    /**
     * Do some initial setting operations before call {@link #convertMetadata()} method.
     */
    protected void init() {
    }

    /**
     * create basic mzTab file architecture, including:
     * metadata, protein/peptide/small_molecule header line and empty table section.
     */
    private void createArchitecture() {
        this.metadata = convertMetadata();

        this.proteinColumnFactory = convertProteinColumnFactory();
        if (this.proteinColumnFactory != null) {
            proteins = new ArrayList<Protein>();
        }

        this.peptideColumnFactory = convertPeptideColumnFactory();
        if (this.peptideColumnFactory != null) {
            peptides = new ArrayList<Peptide>();
        }

        this.psmColumnFactory = convertPSMColumnFactory();
        if (this.psmColumnFactory != null) {
            psms = new ArrayList<PSM>();
        }

        this.smallMoleculeColumnFactory = convertSmallMoleculeColumnFactory();
        if (this.smallMoleculeColumnFactory != null) {
            smallMolecules = new ArrayList<SmallMolecule>();
        }
    }

    /**
     * @return {@link MZTabFile} model.
     */
    public MZTabFile getMZTabFile() {
        if (this.mzTabFile == null) {
            mzTabFile = new MZTabFile(metadata);
            fillData();

            if (proteinColumnFactory != null) {
                mzTabFile.setProteinColumnFactory(proteinColumnFactory);
                for (Protein protein : proteins) {
                    mzTabFile.addProtein(protein);
                }
            }

            if (peptideColumnFactory != null) {
                mzTabFile.setPeptideColumnFactory(peptideColumnFactory);
                for (Peptide peptide : peptides) {
                    mzTabFile.addPeptide(peptide);
                }
            }

            if (psmColumnFactory != null) {
                mzTabFile.setPSMColumnFactory(psmColumnFactory);
                for (PSM psm : psms) {
                    mzTabFile.addPSM(psm);
                }
            }

            if (smallMoleculeColumnFactory != null) {
                mzTabFile.setSmallMoleculeColumnFactory(smallMoleculeColumnFactory);
                for (SmallMolecule smallMolecule : smallMolecules) {
                    mzTabFile.addSmallMolecule(smallMolecule);
                }
            }
        }

        return mzTabFile;
    }

    /**
     * Generate {@link Metadata}
     */
    protected abstract Metadata convertMetadata();

    /**
     * Generate {@link MZTabColumnFactory} which maintain a couple of {@link ProteinColumn}
     */
    protected MZTabColumnFactory convertProteinColumnFactory() {
        return null;
    }

    /**
     * Generate {@link MZTabColumnFactory} which maintain a couple of {@link PeptideColumn}
     */
    protected MZTabColumnFactory convertPeptideColumnFactory() {
        return null;
    }

    /**
     * Generate {@link MZTabColumnFactory} which maintain a couple of {@link PSMColumn}
     */
    protected MZTabColumnFactory convertPSMColumnFactory() {
        return null;
    }

    /**
     * Generate {@link MZTabColumnFactory} which maintain a couple of {@link SmallMoleculeColumn}
     */
    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        return null;
    }

    /**
     * Fill records into model. This method will be called in {@link #getMZTabFile()} method.
     */
    protected abstract void fillData();
}
