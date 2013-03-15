package uk.ac.ebi.pride.jmztab.model;

import java.util.*;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class Metadata extends OperationCenter {
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

    public boolean addUnit(Unit unit) {
        return addUnit(unit.getIdentifier(), unit);
    }

    public boolean addUnit(String identifier, Unit unit) {
        if (unit == null) {
            return false;
        }

        if (unitMap.containsKey(identifier)) {
            return false;
        }

        unitMap.put(identifier, unit);
        return true;
    }

    /**
     * Modify UnitId in Unit, cascade modify the Protein, Peptide, SmallMolecule data table.
     * Metadata fire <-----listen-----Peptide, Protein, SmallMolecule
     *
     * @see Peptide#propertyChange(java.beans.PropertyChangeEvent)
     * @see Protein#propertyChange(java.beans.PropertyChangeEvent)
     * @see  SmallMolecule#propertyChange(java.beans.PropertyChangeEvent)
     *
     * These methods used to register listener.
     * @see MZTabFile#addPeptide(Peptide)
     * @see MZTabFile#addProtein(Protein)
     * @see MZTabFile#addSmallMolecule(SmallMolecule)
     */
    public void modifyUnitId(String oldUnitId, String newUnitId) {
        Map<String, Unit> newUnitMap = new TreeMap<String, Unit>();

        for (Unit unit : values()) {
            if (unit.getUnitId().equals(oldUnitId)) {
                unit.setUnitId(newUnitId);
            }
            newUnitMap.put(unit.getIdentifier(), unit);
        }
        this.unitMap = newUnitMap;

        firePropertyChange(OperationCenter.UNIT_ID, oldUnitId, newUnitId);
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

    public Set<String> getUnitIds() {
        Set<String> unitIds = new TreeSet<String>();

        for (Unit unit : values()) {
            unitIds.add(unit.getUnitId());
        }

        return unitIds;
    }

    public SortedMap<Integer, SubUnit> getSubUnits() {
        SortedMap<Integer, SubUnit> units = new TreeMap<Integer, SubUnit>();

        for (Unit unit : values()) {
            if (unit instanceof SubUnit) {
                SubUnit subUnit = (SubUnit) unit;
                if (subUnit.getSubId() != null) {
                    units.put(subUnit.getSubId(), subUnit);
                }
            }
        }

        return Collections.unmodifiableSortedMap(units);
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
