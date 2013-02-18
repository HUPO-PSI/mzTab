package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.model.Comment;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public class COMLineParser extends MZTabLineParser {
    public void parse(int lineNumber, String line) {
        super.parse(lineNumber, line);
    }

    public Comment getComment() {
        String msg = items.length == 1 ? "" : items[1];
        return new Comment(msg);
    }
}
