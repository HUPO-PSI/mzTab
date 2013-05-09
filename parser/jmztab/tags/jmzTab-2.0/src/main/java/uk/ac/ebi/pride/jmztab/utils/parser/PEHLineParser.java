package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.Section;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PEHLineParser extends MZTabHeaderLineParser {
    public PEHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Peptide), metadata);
    }

    public void check(int lineNumber, String line) throws MZTabException {
        super.check(lineNumber, line);
    }
}
