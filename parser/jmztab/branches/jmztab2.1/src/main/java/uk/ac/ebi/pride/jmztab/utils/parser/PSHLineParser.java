package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabDescription;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.Section;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

/**
 * User: Qingwei
 * Date: 05/06/13
 */
public class PSHLineParser extends MZTabHeaderLineParser {
    public PSHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.PSM_Header), metadata);
    }

    public void parse(int lineNumber, String line, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, line, errorList);
    }

    @Override
    protected void refine() throws MZTabException {
        // do nothing.
    }
}
