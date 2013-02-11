package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public abstract class MZTabLineParser {
    /**
     * based on TAB char to split raw line into String array.
     */
    protected String[] items;

    /**
     * We assume that user before call this method, have check the raw line
     * is not empty line and start with section prefix.
     */
    protected MZTabLineParser(String line) {
        items = line.split(MZTabConstants.TAB);
    }
}
