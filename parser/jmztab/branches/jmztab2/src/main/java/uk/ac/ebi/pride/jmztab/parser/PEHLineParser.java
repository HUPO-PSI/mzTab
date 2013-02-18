package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.Section;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PEHLineParser extends MZTabHeaderLineParser {
    public PEHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Peptide_Header), metadata);
    }

    public void parse(int lineNumber, String line) {
        super.parse(lineNumber, line);
    }
}
