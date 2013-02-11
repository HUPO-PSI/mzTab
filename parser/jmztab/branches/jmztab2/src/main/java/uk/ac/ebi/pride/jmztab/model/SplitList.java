package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is list which each item split by a split char.
 *
 * User: Qingwei
 * Date: 31/01/13
 */
public class SplitList<E> {
    private String splitChar;
    private List<E> strList = new ArrayList<E>();

    public SplitList(String splitChar) {
        if (splitChar == null) {
            splitChar = " ";
        }

        this.splitChar = splitChar;
    }

    public String getSplitChar() {
        return splitChar;
    }

    public void setSplitChar(String splitChar) {
        this.splitChar = splitChar;
    }

    public boolean add(E e) {
        return strList.add(e);
    }

    public boolean addAll(Collection<E> c) {
        return strList.addAll(c);
    }

    public boolean isEmpty() {
        return strList.isEmpty();
    }

    @Override
    public String toString() {
        if (strList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(strList.get(0));

        for (int i = 1; i < strList.size(); i++) {
            sb.append(splitChar).append(strList.get(i));
        }

        return sb.toString();
    }
}
