package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;

/**
 * Comment lines can be placed anywhere in an mzTab file. These lines must start with the three-letter
 * code COM and are ignored by most parsers. Empty lines can also occur anywhere in an mzTab file and are ignored.
 *
 * User: Qingwei, Johannes Griss
 * Date: 05/02/13
 */
public class Comment {
    private String msg;

    /**
     * Set comment message.
     * @param msg if null system will set empty string for it.
     */
    public Comment(String msg) {
        setMsg(msg);
    }

    /**
     * @return the comment message.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Set comment message.
     * @param msg if null system will set empty string for it.
     */
    public void setMsg(String msg) {
        this.msg = msg == null ? "" : msg;
    }

    /**
     * Print Comment to a String, the structure like:
     * <br>COM  message</br>
     */
    public String toString() {
        return Section.Comment.getPrefix() + TAB + msg;
    }
}
