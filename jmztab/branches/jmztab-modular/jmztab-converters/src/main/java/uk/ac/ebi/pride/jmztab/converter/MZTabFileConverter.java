package uk.ac.ebi.pride.jmztab.converter;


import uk.ac.ebi.pride.jmztab.checker.MZTabFileIntegrityChecker;
import uk.ac.ebi.pride.jmztab.checker.MZTabIntegrityChecker;
import uk.ac.ebi.pride.jmztab.converter.mzidentml.ConvertAmbiguityModMZIdentMLFile;
import uk.ac.ebi.pride.jmztab.converter.mzidentml.ConvertMZidentMLFile;
import uk.ac.ebi.pride.jmztab.converter.pridexml.ConvertPrideXMLFile;
import uk.ac.ebi.pride.jmztab.converter.utils.FileFormat;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;

import java.io.File;
import java.io.IOException;

/**
 * Convert third-party data source to mzTab file, and do whole {@link MZTabFile} consistency check.
 * Currently, only PRIDE XML v2.1 and MZIdentML v1.0 has been integrated into this framework.
 *
 * @see uk.ac.ebi.pride.jmztab.MZTabGraphicalInterface
 * @see uk.ac.ebi.pride.jmztab.MZTabCommandLine
 *
 * @author qingwei
 * @author ntoro
 * @since 17/09/13
 */
public class MZTabFileConverter {
    private MZTabErrorList errorList = new MZTabErrorList();
    private MZTabFile mzTabFile;
    private ConvertProvider convertProvider;

    public MZTabFileConverter(File inFile, FileFormat format){
        this(inFile,format, false);
    }

    public MZTabFileConverter(File inFile, FileFormat format, boolean ambiguityMod) {
        this(inFile, format, ambiguityMod, true, true);
    }

    public MZTabFileConverter(File inFile, FileFormat format, boolean ambiguityMod, boolean integrityCheck,  boolean mzIdentMLInMemory) {
        if (format == null) {
            throw new NullPointerException("Source file format is null");
        }

        switch (format) {
            case PRIDE:
                convertProvider = new ConvertPrideXMLFile(inFile);
                break;
            case MZIDENTML:
                if(!ambiguityMod)
                   convertProvider = new ConvertMZidentMLFile(inFile, mzIdentMLInMemory);
                else
                   convertProvider = new ConvertAmbiguityModMZIdentMLFile(inFile);
                break;
            default:
                throw new IllegalArgumentException("Can not convert " + format + " to mztab.");
        }

        mzTabFile = convertProvider.getMZTabFile();

        if(integrityCheck) {
            MZTabIntegrityChecker integrityChecker;
            try {
                integrityChecker = new MZTabFileIntegrityChecker(mzTabFile, errorList);
                integrityChecker.check();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MZTabFileConverter(ConvertProvider convertProvider) {
        this(convertProvider, true);
    }

    public MZTabFileConverter(ConvertProvider convertProvider, boolean integrityCheck) {
        if (convertProvider == null) {
            throw new NullPointerException("ConvertProvider");
        }

        this.convertProvider = convertProvider;
        mzTabFile = this.convertProvider.getMZTabFile();

        if(integrityCheck) {
            MZTabIntegrityChecker integrityChecker;
            try {
                integrityChecker = new MZTabFileIntegrityChecker(mzTabFile, errorList);
                integrityChecker.check();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MZTabFile getMZTabFile() {
        return mzTabFile;
    }

    public MZTabErrorList getErrorList() {
        return errorList;
    }
}
