package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.jmztab.model.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Qingwei
 * Date: 12/03/13
 */
public abstract class ConvertProvider<T, V> {
    protected T source;
    protected V params;
    protected Metadata metadata;
    protected MZTabColumnFactory proteinColumnFactory;
    protected MZTabColumnFactory peptideColumnFactory;
    protected MZTabColumnFactory psmColumnFactory;
    protected MZTabColumnFactory smallMoleculeColumnFactory;
    protected Collection<Protein> proteins;
    protected Collection<Peptide> peptides;
    protected Collection<PSM> psms;
    protected Collection<SmallMolecule> smallMolecules;

    private MZTabFile mzTabFile;

    public ConvertProvider(T source, V params) {
        if (source == null) {
            throw new NullPointerException("Convert source can not set null!");
        }
        this.source = source;

        init(params);
        createArchitecture();
        fillData();
    }

    public static MassSpecFileFormat getFormat(String format) {
        if (MZTabUtils.isEmpty(format)) {
            return null;
        }

        if (format.equalsIgnoreCase(MassSpecFileFormat.PRIDE.name())) {
            return MassSpecFileFormat.PRIDE;
        } else if (format.equalsIgnoreCase(MassSpecFileFormat.MZIDENTML.name())) {
            return MassSpecFileFormat.MZIDENTML;
        } else {
            return MassSpecFileFormat.PRIDE;
        }
    }

    protected void init(V params) {
        this.params = params;
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

        this.psmColumnFactory = convertPSMColumnFactory();
        if (this.psmColumnFactory != null) {
            psms = new ArrayList<PSM>();
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

    protected abstract Metadata convertMetadata();

    protected MZTabColumnFactory convertProteinColumnFactory() {
        return null;
    }

    protected MZTabColumnFactory convertPeptideColumnFactory() {
        return null;
    }

    protected MZTabColumnFactory convertPSMColumnFactory() {
        return null;
    }

    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        return null;
    }

    protected abstract void fillData();
}
