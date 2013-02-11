package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * Comment lines can be placed anywhere in an mzTab file. These lines must start with
 * the three-letter code COM and are ignored by most parsers.
 *
 * User: Qingwei, Johannes Griss
 * Date: 05/02/13
 */
public class Comment {
    private String msg;

    public Comment(String msg) {
        this.msg = msg == null ? "" : msg;
    }

    public String toString() {
        return Section.Comment.getPrefix() + MZTabConstants.TAB + msg;
    }
}
