package uk.ac.ebi.pride.jmztab.model;

import java.util.HashMap;
import java.util.Map;

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
     */
    private Map<String, Unit> unitMap = new HashMap<String, Unit>();

    public void addUnit(Unit unit) {
        if (unit != null) {
            unitMap.put(unit.getIdentifier(), unit);
        }
    }

    public Unit getUnit(String identifier) {
        return unitMap.get(identifier);
    }

    public boolean contains(String identifier) {
        return unitMap.containsKey(identifier);
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
