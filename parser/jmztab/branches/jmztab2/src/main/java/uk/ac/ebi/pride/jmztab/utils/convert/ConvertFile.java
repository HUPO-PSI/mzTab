package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Qingwei
 * Date: 12/03/13
 */
public abstract class ConvertFile {
    protected File inFile;
    protected String format;
    protected Metadata metadata;
    protected MZTabColumnFactory proteinColumnFactory;
    protected MZTabColumnFactory peptideColumnFactory;
    protected MZTabColumnFactory smallMoleculeColumnFactory;
    protected Collection<Protein> proteins;
    protected Collection<Peptide> peptides;
    protected Collection<SmallMolecule> smallMolecules;

    private MZTabFile mzTabFile;

    public final static String PRIDE = "PRIDE";
    public final static String mzIdentML = "mzIndenML";

    public ConvertFile(File inFile, String format) {
        if (inFile == null || inFile.isDirectory()) {
            throw new IllegalArgumentException("Invalid input file " + inFile);
        }

        this.format = format;
        this.inFile = inFile;
    }

    /**
     * create basic mzTab file architecture, including:
     * metadata, protein/peptide/small_molecule header line
     * empty table section.
     */
    protected void createArchitecture() {
        this.metadata = convertMetadata();

        this.proteinColumnFactory = convertProteinColumnFactory();
        if (this.proteinColumnFactory != null) {
            proteins = new ArrayList<Protein>();
        }

        this.peptideColumnFactory = convertPeptideColumnFactory();
        if (this.peptideColumnFactory != null) {
            peptides = new ArrayList<Peptide>();
        }

        this.smallMoleculeColumnFactory = convertSmallMoleculeColumnFactory();
        if (this.smallMoleculeColumnFactory != null) {
            smallMolecules = new ArrayList<SmallMolecule>();
        }
    }

    public MZTabFile getMZTabFile() {
        if (this.mzTabFile == null) {
            mzTabFile = new MZTabFile(metadata);

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

            if (smallMoleculeColumnFactory != null) {
                mzTabFile.setSmallMoleculeColumnFactory(smallMoleculeColumnFactory);
                for (SmallMolecule smallMolecule : smallMolecules) {
                    mzTabFile.addSmallMolecule(smallMolecule);
                }
            }
        }

        return mzTabFile;
    }

    protected abstract Metadata convertMetadata();
    protected abstract MZTabColumnFactory convertProteinColumnFactory();
    protected abstract MZTabColumnFactory convertPeptideColumnFactory();
    protected abstract MZTabColumnFactory convertSmallMoleculeColumnFactory();
    protected abstract void fillData();
}
