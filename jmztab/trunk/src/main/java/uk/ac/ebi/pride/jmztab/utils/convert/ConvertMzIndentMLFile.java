package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;

import java.io.File;

/**
 * User: Qingwei
 * Date: 12/03/13
 */
public class ConvertMzIndentMLFile extends ConvertFile {
    private MzIdentMLUnmarshaller reader;

    public ConvertMzIndentMLFile(File inFile) {
        super(inFile, mzIdentML);
        reader = new MzIdentMLUnmarshaller(inFile);
        createArchitecture();
        fillData();
    }

    @Override
    protected Metadata convertMetadata() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected MZTabColumnFactory convertProteinColumnFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected MZTabColumnFactory convertPeptideColumnFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected MZTabColumnFactory convertPSMColumnFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        return null;
    }

    @Override
    protected void fillData() {

    }
}
