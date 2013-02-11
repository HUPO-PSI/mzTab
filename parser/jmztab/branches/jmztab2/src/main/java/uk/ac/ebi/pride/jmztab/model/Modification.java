package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.util.*;

/**
 *
 *
 * User: Qingwei
 * Date: 30/01/13
 */
public class Modification {
    public final static String PSI_MOD_PREFIX = "MOD";
    public final static String UNIMOD_PREFIX = "UNIMOD";
    public final static String CHEMMODS_PREFIX = "CHEMMODS";
    public final static String SUBST_PREFIX = "SUBST";

    public enum Type {PSI_MOD, UNIMOD, CHEMMOD, SUBST}

    private class Mod {
        Type type;
        String access;

        public Mod(Type type, String access) {
            this.type = type;
            this.access = access;
        }

        @Override
        public String toString() {
            String prefix;
            switch (type) {
                case PSI_MOD:
                    prefix = PSI_MOD_PREFIX;
                    break;
                case UNIMOD:
                    prefix = UNIMOD_PREFIX;
                    break;
                case CHEMMOD:
                    prefix = CHEMMODS_PREFIX;
                    break;
                case SUBST:
                    prefix = SUBST_PREFIX;
                    break;
                default:
                    prefix = "";
            }

            return prefix + ":" + access;
        }
    }

    private Map<Integer, Param> position = new TreeMap<Integer, Param>();
    private Mod mod = null;
    private Param neutralLoss = null;

    public Map<Integer, Param> getPosition() {
        return position;
    }

    public void addPosition(Integer id, Param param) {
        this.position.put(id, param);
    }

    public void setMod(Type type, String access) {
        this.mod = new Mod(type, access);
    }

    public Param getNeutralLoss() {
        return neutralLoss;
    }

    public void setNeutralLoss(Param neutralLoss) {
        this.neutralLoss = neutralLoss;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Integer id;
        Param param;
        Iterator<Integer> it;
        int count = 0;
        if (! position.isEmpty()) {
            it = position.keySet().iterator();
            while (it.hasNext()) {
                id = it.next();
                param = position.get(id);
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

        if (mod != null) {
            if (! position.isEmpty()) {
                sb.append(MZTabConstants.MINUS);
            }
            sb.append(mod);
        }

        if (neutralLoss != null) {
            sb.append(MZTabConstants.BAR).append(neutralLoss);
        }

        return sb.toString();
    }
}
