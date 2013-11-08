package uk.ac.ebi.pride.jmztab.model;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * {position}{Parameter}-{Type:accession}|{neutral loss}
 *
 * User: Qingwei
 * Date: 30/01/13
 */
public class Modification {
    public enum Type {
        MOD,             //PSI-MOD
        UNIMOD,
        CHEMMOD,
        SUBST,           //Substitution identifier
        UNKNOW
    }

    private Map<Integer, CVParam> positionMap = new TreeMap<Integer, CVParam>();
    private Section section;
    private Type type;
    private String accession;
    private CVParam neutralLoss;

    /**
     * {position}{Parameter}-{Type:accession}|{neutral loss}
     */
    public Modification(Section section, Type type, String accession) {
        if (! section.isData()) {
            throw new IllegalArgumentException("Section should use Protein, Peptide, PSM or SmallMolecule.");
        }
        this.section = section;
        this.type = type;

        if (accession == null) {
            throw new NullPointerException("Modification accession can not null!");
        }
        this.accession = accession;
    }

    /**
     *. If the software has determined that there are no modifications to a given protein "0" MUST be used.
     */
    public static Modification createNoModification(Section section) {
        return new Modification(section, Type.UNKNOW, "0");
    }

    public Section getSection() {
        return section;
    }

    public void addPosition(Integer id, CVParam param) {
        this.positionMap.put(id, param);
    }

    public Map<Integer, CVParam> getPositionMap() {
        return positionMap;
    }

    public Type getType() {
        return type;
    }

    public String getAccession() {
        return accession;
    }

    public CVParam getNeutralLoss() {
        return neutralLoss;
    }

    public void setNeutralLoss(CVParam neutralLoss) {
        this.neutralLoss = neutralLoss;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // no modification.
        if (type == Type.UNKNOW) {
            return accession;
        }

        Integer id;
        Param param;
        Iterator<Integer> it;
        int count = 0;
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

        if (positionMap.size() > 0) {
            sb.append(MINUS);
        }
        sb.append(type).append(COLON).append(accession);

        if (neutralLoss != null) {
            sb.append(BAR).append(neutralLoss);
        }

        return sb.toString();
    }

    public static Type findType(String name) {
        if (name == null) {
            return null;
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
