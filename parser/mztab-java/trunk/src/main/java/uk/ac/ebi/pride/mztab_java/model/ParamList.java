package uk.ac.ebi.pride.mztab_java.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A wrapper class for Param Arrays.
 * @author jg
 *
 */
public class ParamList implements List<Param> {
	/**
	 * The separator used between the different
	 * Params.
	 */
	private final String SEPARATOR = "|";
	/**
	 * The private list representing the
	 * actual data. Most functions are just
	 * piped through to this list.
	 */
	private ArrayList<Param> internalList;
	
	public ParamList() {
		internalList = new ArrayList<Param>();
	}
	
	public ParamList(int startingSize) {
		internalList = new ArrayList<Param>(startingSize);
	}
	
	/**
	 * Creates a ParamList object based on a
	 * mzTab String.
	 * @param mzTabString
	 */
	public ParamList(String mzTabString) {
		// extract the param string
		String[] paramStrings = mzTabString.split(SEPARATOR.equals("|") ? "\\" + SEPARATOR : SEPARATOR);
		
		// create the internal list with the correct size
		internalList = new ArrayList<Param>(paramStrings.length);
		
		// add all the params
		for (String paramString : paramStrings)
			internalList.add(new Param(paramString));
	}
	
	@Override
	public String toString() {
		String mzTabString = "";
		
		// create the string
		for (Param param : internalList)
			mzTabString += (mzTabString.length() > 1 ? SEPARATOR : "") + param.toString();
		
		return mzTabString;
	}
	
	/**
	 * Returns the parameter identified by the given
	 * accession. In case a Param with the given
	 * accession was not defined, null is returned.
	 * @param accession The parameter's accession.
	 * @return A Param object or null in case no Param with the given accession exists.
	 */
	public Param getParamByAccession(String accession) {
		for (Param param : internalList)
			if (accession.equals(param.getAccession()))
				return param;
		
		return null;
	}

	public void add(int index, Param param) {
		internalList.add(index, param);
	}

	public boolean add(Param param) {
		return internalList.add(param);
	}

	public boolean addAll(Collection<? extends Param> params) {
		return internalList.addAll(params);
	}

	public boolean addAll(int index, Collection<? extends Param> params) {
		return internalList.addAll(index, params);
	}

	public void clear() {
		internalList.clear();
	}

	public boolean contains(Object param) {
		return internalList.contains(param);
	}

	public boolean containsAll(Collection<?> params) {
		return internalList.containsAll(params);
	}

	public Param get(int index) {
		return internalList.get(index);
	}

	public int indexOf(Object param) {
		return internalList.indexOf(param);
	}

	public boolean isEmpty() {
		return internalList.isEmpty();
	}

	public Iterator<Param> iterator() {
		return internalList.iterator();
	}

	public int lastIndexOf(Object arg0) {
		return internalList.lastIndexOf(arg0);
	}

	public ListIterator<Param> listIterator() {
		return internalList.listIterator();
	}

	public ListIterator<Param> listIterator(int arg0) {
		return internalList.listIterator(arg0);
	}

	public Param remove(int arg0) {
		return internalList.remove(arg0);
	}

	public boolean remove(Object arg0) {
		return internalList.remove(arg0);
	}

	public boolean removeAll(Collection<?> arg0) {
		return internalList.removeAll(arg0);
	}

	public boolean retainAll(Collection<?> arg0) {
		return internalList.retainAll(arg0);
	}

	public Param set(int arg0, Param arg1) {
		return internalList.set(arg0, arg1);
	}

	public int size() {
		return internalList.size();
	}

	public List<Param> subList(int arg0, int arg1) {
		return internalList.subList(arg0, arg1);
	}

	public Object[] toArray() {
		return internalList.toArray();
	}

	public <T> T[] toArray(T[] arg0) {
		return internalList.toArray(arg0);
	}
}
