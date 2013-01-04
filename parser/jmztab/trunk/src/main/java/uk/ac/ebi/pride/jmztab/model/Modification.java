package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;

public class Modification {
	private final static Pattern mzTabModificationPattern = Pattern.compile("([^-]+)-(\\w+:[+-]*[A-Z0-9]+)");
	private final static Pattern mzTabGroupModificationPattern = Pattern.compile("\\[(\\d+)-(\\d+),(\\d+)\\](\\[([\\d.]+)\\])?-(\\w+:[+-]*[A-Z0-9]+)");
	private final static Pattern mzTabPositionPattern = Pattern.compile("(\\d+)(\\[([\\d.]+)\\])?\\|?");
	private final static Pattern mzTabModificationNeutralLossPattern = Pattern.compile(".*\\|?(\\[[^\\]]+\\])$");
	
	private List<Integer> position = new ArrayList<Integer>();
	private List<Double> positionReliability = new ArrayList<Double>();
	private Boolean hasRegion = false;
	private Integer regionStart = null;
	private Integer regionEnd = null;
	private Integer numberOfModificationsInRegion = 0;
	private Double regionReliability = null;
	private String modAccession;
	private Param neutralLoss = null;
	
	/**
	 * Creates a modification object that only reports
	 * a neutral loss not assigned to any modification.
	 * @param neutralLoss
	 * @throws MzTabParsingException 
	 */
	public Modification(Param neutralLoss) throws MzTabParsingException {
	    this.neutralLoss = neutralLoss;
	}
	
	/**
	 * Creates a new modification object. In case
	 * the modification's position is unknown the
	 * position should be set to null.
	 * @param modAccession The modification's PSI-MOD/UNIMOD accession, CHEMMOD string or substitutions (SUBST)
	 * @param position The modification's position.
	 * @throws MzTabParsingException 
	 */
	public Modification(String modAccession, Integer position) throws MzTabParsingException {
	    TableObject.checkStringValue(modAccession);
	    if (!modAccession.startsWith("MOD:") && !modAccession.startsWith("UNIMOD:") && !modAccession.startsWith("CHEMMOD:") && !modAccession.startsWith("SUBST:")) {
		throw new MzTabParsingException("Invalid modification accession used. Modification accessions must start with 'MOD:', 'UNIMOD:', 'CHEMMOD:', or 'SUBST:'.");
	    }
	    this.modAccession = modAccession;
		
	    if (position != null) {
		    this.position.add(position);
		    this.positionReliability.add(null);
	    }
	}
	
	/**
	 * Creates a new modification object.
	 * @param modAccession The modification's PSI-MOD/UNIMOD accession, CHEMMOD string or substitutions (SUBST)
	 * @param position The modification's position.
	 * @param reliability The position's reliability
	 * @throws MzTabParsingException 
	 */
	public Modification(String modAccession, Integer position, Double reliability) throws MzTabParsingException {
	    if (position == null) {
		throw new MzTabParsingException("Modification position must not be 0 when a modification reliability is being set.");
	    }
		
	    TableObject.checkStringValue(modAccession);
	    if (!modAccession.startsWith("MOD:") && !modAccession.startsWith("UNIMOD:") && !modAccession.startsWith("CHEMMOD:") && !modAccession.startsWith("SUBST:")) {
		throw new MzTabParsingException("Invalid modification accession used. Modification accessions must start with 'MOD:', 'UNIMOD:', 'CHEMMOD:', or 'SUBST:'.");
	    }
	    this.modAccession = modAccession;		
	    this.position.add(position);
	    this.positionReliability.add(reliability);
	}
	
	/**
	 * Creates a modification object based on a region.
	 * @param modAccession The modification's accession.
	 * @param regionStart The modification region's start.
	 * @param regionEnd The modification region's end.
	 * @param occurrence The number of modifications expected to be within this region.
	 * @throws MzTabParsingException 
	 */
	public Modification(String modAccession, Integer regionStart, Integer regionEnd, Integer occurrence) throws MzTabParsingException {
	    // make sure that regionStart and regionEnd are set
	    if (regionStart == null || regionEnd == null) {
		throw new MzTabParsingException("Modification region's start and end positions must be set");
	    }
	    if (regionEnd < regionStart) {
		throw new MzTabParsingException("A modification region's end must not be before its start.");
	    }
	    
	    if (occurrence == null) {
		throw new MzTabParsingException("A modification's occurrence must be set");
	    }
	    if (occurrence < 1) {
		throw new MzTabParsingException("There must at least exist one modification within a specified region");
	    }
	    
	    TableObject.checkStringValue(modAccession);
	    if (!modAccession.startsWith("MOD:") && !modAccession.startsWith("UNIMOD:") && !modAccession.startsWith("CHEMMOD:") && !modAccession.startsWith("SUBST:")) {
		throw new MzTabParsingException("Invalid modification accession used. Modification accessions must start with 'MOD:', 'UNIMOD:', 'CHEMMOD:', or 'SUBST:'.");
	    }
	    
	    this.modAccession = modAccession;
	    this.regionStart = regionStart;
	    this.regionEnd = regionEnd;
	    this.numberOfModificationsInRegion = occurrence;
	    this.regionReliability = null;
	}
	
	/**
	 * Creates a modification object based on a region.
	 * @param modAccession The modification's accession.
	 * @param regionStart The modification region's start.
	 * @param regionEnd The modification region's end.
	 * @param occurrence The number of modifications expected to be within this region.
	 * @param reliability The reliability assigned to this modification identification.
	 * @throws MzTabParsingException 
	 */
	public Modification(String modAccession, Integer regionStart, Integer regionEnd, Integer occurrence, Double reliability) throws MzTabParsingException {
	    // make sure that regionStart and regionEnd are set
	    if (regionStart == null || regionEnd == null) {
		throw new MzTabParsingException("Modification region's start and end positions must be set");
	    }
	    if (regionEnd < regionStart) {
		throw new MzTabParsingException("A modification region's end must not be before its start.");
	    }
	    
	    if (occurrence == null) {
		throw new MzTabParsingException("A modification's occurrence must be set");
	    }
	    if (occurrence < 1) {
		throw new MzTabParsingException("There must at least exist one modification within a specified region");
	    }
	    
	    TableObject.checkStringValue(modAccession);
	    if (!modAccession.startsWith("MOD:") && !modAccession.startsWith("UNIMOD:") && !modAccession.startsWith("CHEMMOD:") && !modAccession.startsWith("SUBST:")) {
		throw new MzTabParsingException("Invalid modification accession used. Modification accessions must start with 'MOD:', 'UNIMOD:', 'CHEMMOD:', or 'SUBST:'.");
	    }
	    
	    this.modAccession = modAccession;
	    this.regionStart = regionStart;
	    this.regionEnd = regionEnd;
	    this.numberOfModificationsInRegion = occurrence;
	    this.regionReliability = reliability;
	}
	
	/**
	 * Creates a modification based on a 
	 * mzTab formatted modification definition
	 * @param mzTabString
	 */
	public Modification(String mzTabString) throws MzTabParsingException {
	    // check if there's a neutral loss associated with this modification
	    Matcher neutralLossMatcher = mzTabModificationNeutralLossPattern.matcher(mzTabString);
	    if (neutralLossMatcher.find()) {
		neutralLoss = new Param(neutralLossMatcher.group(1));
		
		// check if it's only a neutal loss that's being reported
		if (neutralLossMatcher.group(1).length() == mzTabString.length()) {
		    return;
		}
		
		// remove the neutral loss Param from the end of the string
		mzTabString = mzTabString.substring(0, mzTabString.length() - neutralLossMatcher.group(1).length() - 1);
	    }
	    
	    // check if the modification is defined for a group
	    if (mzTabString.startsWith("[")) {
		parseGroupModification(mzTabString);
		return;
	    }
	    
	    // check if there is a position set
	    if (!mzTabString.contains("-") || mzTabString.startsWith("CHEMMOD:-")) {
		    this.modAccession = mzTabString;
		    return;
	    }
	    
	    // parse the modification
	    Matcher matcher = mzTabModificationPattern.matcher(mzTabString);

	    if (!matcher.find()) {
		throw new MzTabParsingException("Failed to parse modification. Malformatted modification definition passed: <" + mzTabString + ">");
	    }

	    String positions 	= matcher.group(1);
	    modAccession 	= matcher.group(2);
	    
	    matcher = mzTabPositionPattern.matcher(positions);

	    while (matcher.find()) {
		Integer thePosition = Integer.parseInt(matcher.group(1));
		Double reliability = null;
		if (matcher.group(3) != null) {
		    reliability = Double.parseDouble(matcher.group(3));

		    // make sure the reliability is valid
		    if (reliability < 0 || reliability > 1) {
			throw new MzTabParsingException("Malformatted modification. The reliability must only be between 0-1: '" + mzTabString + "'");
		    }
		}

		this.position.add(thePosition);
		this.positionReliability.add(reliability);
	    }

	    // make sure at least one position was found
	    if (this.position.size() < 1) {
		throw new MzTabParsingException("Failed to parse modification position. Malformatted modification position passed: <" + positions + ">");
	    }
	    
	    // check the mod accession at the end
	    if (!modAccession.startsWith("MOD:") && !modAccession.startsWith("UNIMOD:") && !modAccession.startsWith("CHEMMOD:") && !modAccession.startsWith("SUBST:")) {
		throw new MzTabParsingException("Invalid modification accession used. Modification accessions must start with 'MOD:', 'UNIMOD:', 'CHEMMOD:', or 'SUBST:'.");
	    }
	}
	
	/**
	 * Parses the mzTab String representing a modification
	 * defined for a group of amino acids.
	 * @param mzTabString 
	 */
	private void parseGroupModification(String mzTabString) throws MzTabParsingException {
	    hasRegion = true;
	    
	    // parse the region
	    Matcher matcher = mzTabGroupModificationPattern.matcher(mzTabString);

	    if (!matcher.find()) {
		throw new MzTabParsingException("Failed to parse modification: '" + mzTabString + "'");
	    }

	    this.regionStart = Integer.parseInt(matcher.group(1));
	    this.regionEnd = Integer.parseInt(matcher.group(2));
	    this.numberOfModificationsInRegion = Integer.parseInt(matcher.group(3));
	    this.modAccession = matcher.group(6);

	    if (matcher.group(5) != null) {
		this.regionReliability = Double.parseDouble(matcher.group(5));
	    }

	    // make sure the end is not before the start
	    if (this.regionEnd < this.regionStart) {
		throw new MzTabParsingException("Malformatted modification region. The region's end is before the region's start: '" + mzTabString + "'");
	    }
	    // make sure the occurrence is larger than 0
	    if (this.numberOfModificationsInRegion < 1) {
		throw new MzTabParsingException("Malformatted modification region. The occurrence of the modification must at least be 1: '" + mzTabString + "'");
	    }
	    
	    // check the mod accession at the end
	    if (!modAccession.startsWith("MOD:") && !modAccession.startsWith("UNIMOD:") && !modAccession.startsWith("CHEMMOD:") && !modAccession.startsWith("SUBST:")) {
		throw new MzTabParsingException("Invalid modification accession used. Modification accessions must start with 'MOD:', 'UNIMOD:', 'CHEMMOD:', or 'SUBST:'.");
	    }
	}
	
	/**
	 * Adds an additional position to the modification. This is only
	 * possible if the modification is not defined for a region.
	 * @param position
	 * @param reliability
	 * @throws MzTabParsingException
	 */
	public void addPosition(Integer position, Double reliability) throws MzTabParsingException {
	    if (this.hasRegion) {
		throw new MzTabParsingException("Modification positions can not be added to modifications defined for regions.");
	    }
	    
	    this.position.add(position);
	    this.positionReliability.add(reliability);
	}
	
	/**
	 * Sets the neutral loss for the modification. To remove
	 * a neutral loss from a modification, set the parameter
	 * to null.
	 * @param modificationNeutralLoss 
	 */
	public void setNeutralLoss(Param modificationNeutralLoss) {
	    this.neutralLoss = modificationNeutralLoss;
	}
	
	public Boolean hasRegionDefined() {
	    return hasRegion;
	}
	
	public List<Integer> getPosition() {
		return position;
	}
	
	public List<Double> getPositionReliability() {
		return positionReliability;
	}
	
	public Integer getRegionStart() {
	    return regionStart;
	}
	
	public Integer getRegionEnd() {
	    return regionEnd;
	}
	
	public Integer getNumberOfModificationsInRegion() {
	    return numberOfModificationsInRegion;
	}
	
	public Double getRegionReliability() {
	    return regionReliability;
	}
	
	/**
	 * Returns the modification's accession. This may be null
	 * in case a neutral loss without an associated modification
	 * is being reported.
	 * @return 
	 */
	public String getAccession() {
		return modAccession;
	}
	
	public Boolean hasNeutralLoss() {
	    return neutralLoss != null;
	}
	
	public Param getNeutralLoss() {
	    return neutralLoss;
	}

	@Override
	public String toString() {
	    // check if it's only a neutral loss that's being reported
	    if (modAccession == null && neutralLoss != null) {
		return neutralLoss.toString();
	    }
	    
	    // make sure there are as many positions as reliabilities
	    if (position.size() != positionReliability.size()) {
		throw new IllegalStateException("Tried to convert modification object to mzTab not containing any position information but reliability information.");
	    }

	    StringBuilder mzTabString = new StringBuilder();

	    if (hasRegion) {
		mzTabString.append("[").append(regionStart).append("-").append(regionEnd).append(",").append(numberOfModificationsInRegion).append("]");
		
		if (regionReliability != null) {
		   mzTabString.append("[").append(regionReliability).append("]");
		}
	    }
	    else {
		for (int i = 0; i < position.size(); i++) {
		    mzTabString.append(mzTabString.length() > 0 ? "|" : "").append( position.get(i));

		    if (positionReliability.get(i) != null) {
			mzTabString.append("[").append(positionReliability.get(i)).append( "]");
		    }
		}
	    }
	    
	    if (position.size() > 0 || hasRegion) {
		mzTabString.append("-");
	    }

	    mzTabString.append( modAccession );
	    
	    // add the neutral loss if there is one
	    if (neutralLoss != null) {
		mzTabString.append("|").append(neutralLoss);
	    }

	    return mzTabString.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((modAccession == null) ? 0 : modAccession.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime
				* result
				+ ((positionReliability == null) ? 0 : positionReliability
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (obj == null) {
		return false;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    Modification other = (Modification) obj;
	    if (modAccession == null) {
		if (other.modAccession != null) {
		    return false;
		}
	    } else if (!modAccession.equals(other.modAccession)) {
		return false;
	    }
	    if (position == null) {
		if (other.position != null) {
		    return false;
		}
	    } else if (!position.equals(other.position)) {
		return false;
	    }
	    if (positionReliability == null) {
		if (other.positionReliability != null) {
		    return false;
		}
	    } else if (!positionReliability.equals(other.positionReliability)) {
		return false;
	    }
	    return true;
	}
	
	/**
	 * Indicates whether the modification is defined
	 * using the CHEMMOD syntax.
	 * @return
	 */
	public boolean isChemMod() {
		return modAccession.startsWith("CHEMMOD:");
	}
	
	/**
	 * Returns the chemmod definition. In case the
	 * CHEMMOD syntax was not used, NULL is returned.
	 * @return Returns the definition (either the chemical formula or the m/z delta) specified through the CHEMMOD syntax.
	 */
	public String getChemModDefinition() {
	    if (!isChemMod()) {
		return null;
	    }
		
	    return modAccession.substring(8);
	}
	
	/**
	 * Returns the delta mass defined through the
	 * CHEMMOD. In case the modification is not defined
	 * using the CHEMMOD syntax or is defined using a
	 * chemical formula, NULL is returned.
	 * @return Returns the defined CHEMMOD delta or NULL in case a different syntax was used.
	 */
	public Double getChemModDelta() {
	    if (!isChemMod()) {
		return null;
	    }
		
	    String definition = getChemModDefinition();
	    Double delta = null;

	    try {
		    delta = Double.parseDouble(definition);
	    } catch (NumberFormatException e) {
		    // ignore this issue, since the delta is set to NULL anyway
	    }

	    return delta;
	}
}
