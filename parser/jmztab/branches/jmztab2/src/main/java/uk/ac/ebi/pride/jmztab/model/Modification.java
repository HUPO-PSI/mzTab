package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
        SUBST            //Substitution identifier
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
            throw new IllegalArgumentException("Section should use Protein, Peptide or SmallMolecule.");
        }
        this.section = section;
        this.type = type;

        if (accession == null) {
            throw new NullPointerException("Modification accession can not null!");
        }
        this.accession = accession;
    }

    public Section getSection() {
        return section;
    }

    public void addPosition(Integer id, CVParam param) {
        if (positionMap.containsKey(id)) {
            throw new IllegalArgumentException("one modification can not assigned to the same position.");
        }

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
                    sb.append(MZTabConstants.BAR).append(id);
                }
                if (param != null) {
                    sb.append(param);
                }
            }
        }

        if (positionMap.size() > 0) {
            sb.append(MZTabConstants.MINUS);
        }
        sb.append(type).append(MZTabConstants.COLON).append(accession);

        if (neutralLoss != null) {
            sb.append(MZTabConstants.BAR).append(neutralLoss);
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
