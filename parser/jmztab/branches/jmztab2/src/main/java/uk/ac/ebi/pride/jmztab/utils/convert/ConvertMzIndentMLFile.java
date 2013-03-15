package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.*;

import java.io.File;
import java.util.Collection;

/**
 * User: Qingwei
 * Date: 12/03/13
 */
public class ConvertMzIndentMLFile extends ConvertFile {
    public ConvertMzIndentMLFile(File inFile) {
        super(inFile, Format.mzIdentML);
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
    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void fillData() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
