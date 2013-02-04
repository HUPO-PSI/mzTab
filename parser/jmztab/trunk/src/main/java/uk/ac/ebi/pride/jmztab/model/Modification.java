package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.MzTabParsingException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Modification {

    private final static Pattern mzTabModificationPattern = Pattern.compile("(.+)-(\\w+:[+-]*[A-Z0-9]+)");
    private final static Pattern mzTabPositionPattern = Pattern.compile("(\\d+)(\\[[^\\]]+\\])?\\|?");
    private final static Pattern mzTabModificationNeutralLossPattern = Pattern.compile(".*\\|?(\\[[^\\]]+\\])$");
    private List<Integer> position = new ArrayList<Integer>();
    private List<Param> positionReliability = new ArrayList<Param>();
    private String modAccession;
    private Param neutralLoss = null;

    /**
     * Creates a modification object that only reports a neutral loss not
     * assigned to any modification.
     *
     * @param neutralLoss
     * @throws MzTabParsingException
     */
    public Modification(Param neutralLoss) throws MzTabParsingException {
	this.neutralLoss = neutralLoss;
    }

    /**
     * Creates a new modification object. In case the modification's position is
     * unknown the position should be set to null.
     *
     * @param modAccession The modification's PSI-MOD/UNIMOD accession, CHEMMOD
     * string or substitutions (SUBST)
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
     *
     * @param modAccession The modification's PSI-MOD/UNIMOD accession, CHEMMOD
     * string or substitutions (SUBST)
     * @param position The modification's position.
     * @param reliability The position's reliability
     * @throws MzTabParsingException
     */
    public Modification(String modAccession, Integer position, Param reliability) throws MzTabParsingException {
	if (position == null) {
	    throw new MzTabParsingException("Modification position must not be 0 when a modification reliability is being set.");
	}

	TableObject.checkStringValue(modAccession);
	if (!modAccession.startsWith("MOD:") && !modAccession.startsWith("UNIMOD:") && !modAccession.startsWith("CHEMMOD:") && !modAccession.startsWith("SUBST:")) {
	    throw new MzTabParsingException("Invalid modification accession used. Modification accessions must start with 'MOD:', 'UNIMOD:', 'CHEMMOD:', or 'SUBST:'. " + modAccession);
	}
	this.modAccession = modAccession;
	this.position.add(position);
	this.positionReliability.add(reliability);
    }

    /**
     * Creates a modification based on a mzTab formatted modification definition
     *
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

	// take care of cases where no position is supplied
	if (!mzTabString.contains("-") || mzTabString.startsWith("CHEMMOD:-")) {
	    this.modAccession = mzTabString;
	    return;
	}

	// parse the modification
	Matcher matcher = mzTabModificationPattern.matcher(mzTabString);

	if (!matcher.find()) {
	    throw new MzTabParsingException("Failed to parse modification. Malformatted modification definition passed: <" + mzTabString + ">");
	}

	String positions = matcher.group(1);
	modAccession = matcher.group(2);

	matcher = mzTabPositionPattern.matcher(positions);

	while (matcher.find()) {
	    Integer thePosition = Integer.parseInt(matcher.group(1));
	    Param reliabilityParam = null;
	    if (matcher.group(2) != null) {
		reliabilityParam = new Param(matcher.group(2));
	    }

	    this.position.add(thePosition);
	    this.positionReliability.add(reliabilityParam);
	}

	// make sure at least one position was found
	if (this.position.size() < 1) {
	    throw new MzTabParsingException("Failed to parse modification position. Malformatted modification position passed: <" + positions + ">");
	}

	// check the mod accession at the end
	if (!modAccession.startsWith("MOD:") && !modAccession.startsWith("UNIMOD:") && !modAccession.startsWith("CHEMMOD:") && !modAccession.startsWith("SUBST:")) {
	    throw new MzTabParsingException("Invalid modification accession used. Modification accessions must start with 'MOD:', 'UNIMOD:', 'CHEMMOD:', or 'SUBST:'. " + modAccession);
	}
    }

    /**
     * Adds an additional position to the modification.
     *
     * @param position
     * @param reliability
     * @throws MzTabParsingException
     */
    public void addPosition(Integer position, Param reliability) throws MzTabParsingException {
	this.position.add(position);
	this.positionReliability.add(reliability);
    }

    /**
     * Sets the neutral loss for the modification. To remove a neutral loss from
     * a modification, set the parameter to null.
     *
     * @param modificationNeutralLoss
     */
    public void setNeutralLoss(Param modificationNeutralLoss) {
	this.neutralLoss = modificationNeutralLoss;
    }

    public List<Integer> getPosition() {
	return position;
    }

    public List<Param> getPositionReliability() {
	return new ArrayList<Param>(positionReliability);
    }

    /**
     * Returns the modification's accession. This may be null in case a neutral
     * loss without an associated modification is being reported.
     *
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

	for (int i = 0; i < position.size(); i++) {
	    mzTabString.append(mzTabString.length() > 0 ? "|" : "").append(position.get(i));

	    if (positionReliability.get(i) != null) {
		mzTabString.append(positionReliability.get(i).toString());
	    }
	}

	if (position.size() > 0) {
	    mzTabString.append("-");
	}

	mzTabString.append(modAccession);

	// add the neutral loss if there is one
	if (neutralLoss != null) {
	    mzTabString.append("|").append(neutralLoss);
	}

	return mzTabString.toString();
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 89 * hash + (this.position != null ? this.position.hashCode() : 0);
	hash = 89 * hash + (this.positionReliability != null ? this.positionReliability.hashCode() : 0);
	hash = 89 * hash + (this.modAccession != null ? this.modAccession.hashCode() : 0);
	hash = 89 * hash + (this.neutralLoss != null ? this.neutralLoss.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Modification other = (Modification) obj;
	if (this.position != other.position && (this.position == null || !this.position.equals(other.position))) {
	    return false;
	}
	if (this.positionReliability != other.positionReliability && (this.positionReliability == null || !this.positionReliability.equals(other.positionReliability))) {
	    return false;
	}
	if ((this.modAccession == null) ? (other.modAccession != null) : !this.modAccession.equals(other.modAccession)) {
	    return false;
	}
	if (this.neutralLoss != other.neutralLoss && (this.neutralLoss == null || !this.neutralLoss.equals(other.neutralLoss))) {
	    return false;
	}
	return true;
    }

    /**
     * Indicates whether the modification is defined using the CHEMMOD syntax.
     *
     * @return
     */
    public boolean isChemMod() {
	return modAccession.startsWith("CHEMMOD:");
    }

    /**
     * Returns the chemmod definition. In case the CHEMMOD syntax was not used,
     * NULL is returned.
     *
     * @return Returns the definition (either the chemical formula or the m/z
     * delta) specified through the CHEMMOD syntax.
     */
    public String getChemModDefinition() {
	if (!isChemMod()) {
	    return null;
	}

	return modAccession.substring(8);
    }

    /**
     * Returns the delta mass defined through the CHEMMOD. In case the
     * modification is not defined using the CHEMMOD syntax or is defined using
     * a chemical formula, NULL is returned.
     *
     * @return Returns the defined CHEMMOD delta or NULL in case a different
     * syntax was used.
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
    
    /**
     * Indicates whether the modification defines an amino acid substitution.
     * @return 
     */
    public boolean isSubstitution() {
	return this.modAccession.startsWith("SUBST:");
    }
    
    /**
     * If the modification is defining an amino acid substitution, this function
     * returns the amino acid substituting the original one. If the modification
     * is not defining a substitution, null is returned.
     * @return 
     */
    public String getSubstitutingAminoAcid() {
	if (!isSubstitution()) {
	    return null;
	}
	
	return this.modAccession.substring(6);
    }
}
