package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Section;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class PEHLineParser extends MZTabHeaderLineParser {


    public PEHLineParser(String line) {
        super(line, MZTabColumnFactory.getInstance(Section.Peptide_Header));
    }
}
