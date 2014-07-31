package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;

/**
 * Defining modifications in the meta-data section.
 *
 * The meta values "fixed_modification[1-n]" and "variable_modification[1-n]" describe all
 * search modifications used to identify peptides and proteins of the mzTab file (e.g.
 * carbamidomethylation, oxidation, labels/tags). This is the minimal information that MUST be
 * provided for Complete Identification or Quantification files.
 *
 * @see FixedMod
 * @see VariableMod
 *
 * @author qingwei
 * @since 14/10/13
 */
public abstract class Mod extends IndexedElement {
    private Param param;
    private String site;
    private String position;

    /**
     * Defining modifications in the meta-data section.
     */
    public Mod(MetadataElement element, int id) {
        super(element, id);
    }

    /**
     * Get a parameter describing a modifications searched for. Multiple fixed modifications are numbered 1..n.
     */
    public Param getParam() {
        return param;
    }

    /**
     * Set a parameter describing a modifications searched for. Multiple fixed modifications are numbered 1..n.
     */
    public void setParam(Param param) {
        this.param = param;
    }

    /**
     * A string describing a fixed modifications site. Following the unimod convention, modification site is a residue
     * (e.g. "M"), terminus ("N-term" or "C-term") or both (e.g. "N-term Q" or "C-term K").
     */
    public String getSite() {
        return site;
    }

    /**
     * A string describing a fixed modifications site. Following the unimod convention, modification site is a residue
     * (e.g. "M"), terminus ("N-term" or "C-term") or both (e.g. "N-term Q" or "C-term K").
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * A string describing the term specifity of a fixed modification. Following the unimod convention, term specifity
     * is denoted by the strings "Anywhere", "Any N-term", "Any C-term", "Protein N-term", "Protein C-term".
     */
    public String getPosition() {
        return position;
    }

    /**
     * A string describing the term specifity of a fixed modification. Following the unimod convention, term specifity
     * is denoted by the strings "Anywhere", "Any N-term", "Any C-term", "Protein N-term", "Protein C-term".
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Print modification in metadata to a String, the structure like:
     * <ul>
     *     <li>MTD	fixed_mod[1]	[UNIMOD, UNIMOD:4, Carbamidomethyl, ]</li>
     *     <li>MTD	fixed_mod[1]-site	M</li>
     *     <li>MTD	fixed_mod[3]-position	Protein C-term</li>
     * </ul>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (param != null) {
            sb.append(printElement(param)).append(NEW_LINE);
        }

        if (! isEmpty(site)) {
            sb.append(printProperty(MetadataProperty.findProperty(getElement(), "site"), site)).append(NEW_LINE);
        }

        if (! isEmpty(position)) {
            sb.append(printProperty(MetadataProperty.findProperty(getElement(), "position"), position)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
