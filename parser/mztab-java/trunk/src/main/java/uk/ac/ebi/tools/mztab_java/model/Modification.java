package uk.ac.ebi.tools.mztab_java.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ebi.tools.mztab_java.MzTabParsingException;

public class Modification {
	private final static Pattern mzTabModificationPattern = Pattern.compile("([^-]+)-(\\w+:\\d+)");
	private final static Pattern mzTabPositionPattern = Pattern.compile("(\\d+)(\\[([\\d.]+)\\])?\\|?");
	
	private List<Integer> position = new ArrayList<Integer>();
	private List<Double> positionReliability = new ArrayList<Double>();
	private String modAccession;
	
	/**
	 * Creates a new modification object.
	 * @param modAccession The modification's PSI-MOD accession
	 * @param position The modification's position.
	 */
	public Modification(String modAccession, Integer position) {
		this.modAccession = modAccession;
		this.position.add(position);
		this.positionReliability.add(null);
	}
	
	/**
	 * Creates a new modification object.
	 * @param modAccession The modification's PSI-MOD accession
	 * @param position The modification's position.
	 * @param reliability The position's reliability
	 */
	public Modification(String modAccession, Integer position, Double reliability) {
		this.modAccession = modAccession;
		this.position.add(position);
		this.positionReliability.add(reliability);
	}
	
	/**
	 * Creates a modification based on a 
	 * mzTab formatted modification definition
	 * @param mzTabString
	 */
	public Modification(String mzTabString) throws MzTabParsingException {
		// parse the modification
		Matcher matcher = mzTabModificationPattern.matcher(mzTabString);
		
		if (!matcher.find())
			throw new MzTabParsingException("Failed to parse modification. Malformatted modification definition passed: <" + mzTabString + ">");
		
		String positions 	= matcher.group(1);
		modAccession 		= matcher.group(2);
		
		// parse the positions
		matcher = mzTabPositionPattern.matcher(positions);
		
		while (matcher.find()) {
			Integer position = Integer.parseInt(matcher.group(1));
			Double reliability = null;
			if (matcher.group(3) != null)
				reliability = Double.parseDouble(matcher.group(3));
			
			this.position.add(position);
			this.positionReliability.add(reliability);
		}
		
		// make sure at least one position was found
		if (this.position.size() < 1)
			throw new MzTabParsingException("Failed to parse modification position. Malformatted modification position passed: <" + positions + ">");
	}
	
	/**
	 * Adds an additional position to the modification
	 * @param position
	 * @param reliability
	 */
	public void addPosition(Integer position, Double reliability) {
		this.position.add(position);
		this.positionReliability.add(reliability);
	}
	
	public List<Integer> getPosition() {
		return position;
	}
	
	public List<Double> getPositionReliability() {
		return positionReliability;
	}
	
	public String getAccession() {
		return modAccession;
	}

	@Override
	public String toString() {
		// make sure there are as many positions as reliabilities
		if (position.size() != positionReliability.size() || position.size() < 1)
			throw new IllegalStateException("Tried to convert modification object to mzTab not containing any position information.");
		
		String mzTabString = "";
		
		for (int i = 0; i < position.size(); i++) {
			mzTabString += (mzTabString.length() > 0 ? "|" : "") + position.get(i);
			
			if (positionReliability.get(i) != null)
				mzTabString += "[" + positionReliability.get(i) + "]";
		}
		
		mzTabString += "-" + modAccession;
		
		return mzTabString;
	}
	
	
}
