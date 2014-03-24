package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;

/**
 * This is list which each item split by a split char.
 *
 * User: Qingwei
 * Date: 31/01/13
 */
public class SplitList<E> extends ArrayList<E> {
    private char splitChar;

    public SplitList(char splitChar) {
        this.splitChar = splitChar;
    }

    public char getSplitChar() {
        return splitChar;
    }

    public void setSplitChar(char splitChar) {
        this.splitChar = splitChar;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(get(0));

        for (int i = 1; i < size(); i++) {
            sb.append(splitChar).append(get(i));
        }

        return sb.toString();
    }
}
