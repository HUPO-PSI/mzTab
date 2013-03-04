package uk.ac.ebi.pride.jmztab.model;

import java.util.*;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class Metadata {
    /**
     * the <identifier, unit> pair, identifier is unique of Unit object.
     * @see uk.ac.ebi.pride.jmztab.model.Unit#getIdentifier()
     * @see uk.ac.ebi.pride.jmztab.model.SubUnit#getIdentifier()
     * @see uk.ac.ebi.pride.jmztab.model.ReplicateUnit#getIdentifier()
     *
     * For SubUnit, there exists one special situation:
     * PRIDE_1234-species[1]
     * That is no "sub[id]" information.
     * In our system, we this kind of SubUnit's identifier is PRIDE_1234-sub
     */
    private Map<String, Unit> unitMap = new TreeMap<String, Unit>();

    public void addUnit(Unit unit) {
        if (unit != null) {
            unitMap.put(unit.getIdentifier(), unit);
        }
    }

    public void addUnit(String identifier, Unit unit) {
        if (unit != null) {
            unitMap.put(identifier, unit);
        }
    }

    public Unit removeUnit(String identifier) {
        return unitMap.remove(identifier);
    }

    public Unit getUnit(String identifier) {
        return unitMap.get(identifier);
    }

    public boolean contains(String identifier) {
        return unitMap.containsKey(identifier);
    }

    public Collection<Unit> values() {
        return unitMap.values();
    }

    public Set<String> keySet() {
        return unitMap.keySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Unit unit : unitMap.values()) {
            sb.append(unit);
        }

        return sb.toString();
    }
}
