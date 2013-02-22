package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.Section;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PRHLineParser extends MZTabHeaderLineParser {
    public PRHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Protein), metadata);
    }

    public void parse(int lineNumber, String line) throws MZTabException {
        super.parse(lineNumber, line);
    }
}
