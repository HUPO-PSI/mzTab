package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.Comment;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class COMLineParser extends MZTabLineParser {
    public void check(int lineNumber, String line) throws MZTabException {
        super.check(lineNumber, line);
    }

    public Comment getComment() {
        String msg = items.length == 1 ? "" : items[1];
        return new Comment(msg);
    }
}
