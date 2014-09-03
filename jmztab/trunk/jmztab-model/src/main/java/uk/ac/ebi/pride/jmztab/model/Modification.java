package uk.ac.ebi.pride.jmztab.model;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * Reporting of modifications in columns of the protein, peptide, small molecule and PSM sections.
 * Modifications or substitutions are modelled using a specific modification object with the following format:
 *
 * {position}{Parameter}-{Modification or Substitution identifier}|{neutral loss}
 *
 * Detail information, please reference 5.8 in mzTab specification v1.0
 *
 * @author qingwei
 * @since 30/01/13
 */
public class Modification {
    public enum Type {
        MOD,             // PSI-MOD
        UNIMOD,
        CHEMMOD,
        SUBST,           // to report substitutions of amino acids
        UNKNOWN,          // Unrecognized modification
        NEUTRAL_LOSS
    }

    private Map<Integer, CVParam> positionMap = new TreeMap<Integer, CVParam>();
    private Section section;
    private Type type;
    private String accession;
    private CVParam neutralLoss;

    /**
     * Create a modification in columns of the protein, peptide, small molecule and PSM sections.
     * The structure like: {Type:accession}
     *
     * NOTICE: {position} is mandatory. However, if it is not known (e.g. MS1 Peptide Mass Fingerprinting),
     * "null" must be used. Thus, in construct method we not provide position parameter. User can define
     * this by using {@link #addPosition(Integer, CVParam)} method.
     *
     * @param section SHOULD be {@link Section#Protein}, {@link Section#Peptide}, {@link Section#PSM} or
     * {@link Section#Small_Molecule}
     * @param type SHOULD NOT be null.
     * @param accession SHOULD not be empty.
     */
    public Modification(Section section, Type type, String accession) {
        if (! section.isData()) {
            throw new IllegalArgumentException("Section should use Protein, Peptide, PSM or SmallMolecule.");
        }
        this.section = section;

        if (type == null) {
            throw new NullPointerException("Modification type should not be null!");
        }
        this.type = type;

        if (MZTabUtils.isEmpty(accession)) {
            throw new IllegalArgumentException("Modification accession can not empty!");
        }
        this.accession = accession;
    }

    /**
     * If the software has determined that there are no modifications to a given protein, "0" MUST be used.
     * In this situation, we define a {@link Type#UNKNOWN} modification, which accession is "0".
     */
    public static Modification createNoModification(Section section) {
        return new Modification(section, Type.UNKNOWN, "0");
    }

    /**
     * @return the {@link Section} which modification belong to.
     */
    public Section getSection() {
        return section;
    }

    /**
     * Check if the position of the modification is ambiguous or not (multiple positions associated to the same modification
     */
    public boolean isAmbiguous() {
        return positionMap.size() > 1;
    }

    /**
     * Add a optional position value for modification. If not set, "null" will report.
     * {position} is mandatory. However, if it is not known (e.g. MS1 Peptide Mass Fingerprinting), 'null'
     * must be used Terminal modifications in proteins and peptides MUST be reported with the position set to
     * 0 (N-terminal) or the amino acid length +1 (C-terminal) respectively. N-terminal modifications that are
     * specifically on one amino acid MUST still be reported at the position 0. This object allows modifications
     * to be assigned to ambiguous locations, but only at the PSM and Peptide level. Ambiguity of modification
     * position MUST NOT be reported at the Protein level. In that case, the modification element can be left empty.
     * Ambiguous positions can be reported by separating the {position} and (optional) {cvParam} by an '|' from
     * the next position. Thereby, it is possible to report reliabilities / scores / probabilities etc. for every
     * potential location.
     *
     * @param id SHOULD be non-negative integer.
     * @param param Ambiguous positions can be reported by separating the {position} and  (optional) {cvParam}
     *              by an '|' from the next position. This value can set null, it MAY be used to report a numerical
     *              value e.g. a probability score associated with the modification or location.
     */
    public void addPosition(Integer id, CVParam param) {
        this.positionMap.put(id, param);
    }

    /**
     * @return the modification position map, the key is position, and value is {@link CVParam}. This value can set null,
     * it MAY be used to report a numerical value e.g. a probability score associated with the modification or location.
     */
    public Map<Integer, CVParam> getPositionMap() {
        return positionMap;
    }

    /**
     * @return Modification enum {@link Type}.
     */
    public Type getType() {
        return type;
    }

    /**
     * @return Modification accession number.
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Neutral losses are reported as cvParams. They are reported in the same way that modification objects are
     * (as separate, comma-separated objects in the modification column). The position for a neutral loss MAY be reported.
     *
     * @return Neutral loss.
     */
    public CVParam getNeutralLoss() {
        return neutralLoss;
    }

    /**
     * Neutral loss is optional. Neutral losses are reported as cvParams. They are reported in the same way that
     * modification objects are (as separate, comma-separated objects in the modification column). The position
     * for a neutral loss MAY be reported.
     *
     * @param neutralLoss can set NULL.
     */
    public void setNeutralLoss(CVParam neutralLoss) {
        this.neutralLoss = neutralLoss;
    }

    /**
     * Print Modification in metadata to String. The following are examples:
     * <ul>
     *     <li>3-MOD:00412, 8-MOD:00412</li>
     *     <li>3|4-MOD:00412, 8-MOD:00412</li>
     *     <li>3|4|8-MOD:00412, 3|4|8-MOD:00412</li>
     *     <li>3[MS,MS:1001876, modification probability, 0.8]|4[MS,MS:1001876, modification probability, 0.2]-MOD:00412, 8-MOD:00412</li>
     *     <li>CHEMMOD:+NH4</li>
     *     <li>CHEMMOD:-18.0913</li>
     *     <li>UNIMOD:18</li>
     *     <li>SUBST:{amino acid}</li>
     *     <li>3-UNIMOD:21, 3-[MS, MS:1001524, fragment neutral loss, 63.998285]</li>
     *     <li>[MS, MS:1001524, fragment neutral loss, 63.998285], 7-UNIMOD:4</li>
     *     <li>5-[MS, MS:1001524, fragment neutral loss, 63.998285], 7-UNIMOD:4</li>
     *     <li>0</li>
     * </ul>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // no modification.
        if (type == Type.UNKNOWN) {
            return accession;
        }

        Integer id;
        Param param;
        Iterator<Integer> it;
        int count = 0;

        //position part example: 3[MS, MS:1001876, modification probability, 0.8]|4[MS, MS:1001876, modification probability, 0.2]
        if (! positionMap.isEmpty()) {
            it = positionMap.keySet().iterator();
            while (it.hasNext()) {
                id = it.next();
                param = positionMap.get(id);
                if (count++ == 0) {
                    sb.append(id);
                } else {
                    sb.append(BAR).append(id);
                }
                if (param != null) {
                    sb.append(param);
                }
            }
        }

        //example:  -
        if (positionMap.size() > 0) {
            sb.append(MINUS);
        }

        // example: MOD:00412
        if(type != Type.NEUTRAL_LOSS)
            sb.append(type).append(COLON).append(accession);

        // example: [MS, MS:1001524, fragment neutral loss, value]
        if (neutralLoss != null) {
            sb.append(neutralLoss);
        }

        return sb.toString();
    }

    /**
     * Find modification type by name with case-insensitive.
     *
     * @param name SHOULD not be empty.
     * @return If not find, return null value.
     */
    public static Type findType(String name) {
        if (MZTabUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Modification type name should not be empty!");
        }

        Type type;
        try {
            type = Type.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            type = null;
        }

        return type;
    }
}
