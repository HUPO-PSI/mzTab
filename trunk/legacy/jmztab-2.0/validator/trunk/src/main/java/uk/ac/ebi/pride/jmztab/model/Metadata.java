package uk.ac.ebi.pride.jmztab.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class Metadata extends OperationCenter implements PropertyChangeListener {
    private Comparator<String> idComparator = new Comparator<String>() {
        /**
         * if exists subId or repId which more than 9, the identifier comparator
         * compared based on character order, not based on integer order. It is
         * not good.
         *
         * This method overwrite the String comparator, add subId comparator.
         */
        @Override
        public int compare(String o1, String o2) {
            Pattern pattern = Pattern.compile("(.*)(sub|rep)\\[(\\d+)\\]");
            Matcher matcher1 = pattern.matcher(o1);
            Matcher matcher2 = pattern.matcher(o2);

            if (matcher1.matches() && matcher2.matches() &&
                    matcher1.group(1).equals(matcher2.group(1)) &&
                    matcher1.group(2).equals(matcher2.group(2))) {
                Integer id1 = new Integer(matcher1.group(3));
                Integer id2 = new Integer(matcher2.group(3));
                return id1.compareTo(id2);
            } else {
                return o1.compareTo(o2);
            }
        }
    };

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
    private SortedMap<String, Unit> unitMap;
    private MetadataDescription description;

    public Metadata() {
        unitMap = new TreeMap<String, Unit>(idComparator);
    }

    public MetadataDescription getDescription() {
        return description;
    }

    public void setDescription(MetadataDescription description) {
        if (description == null) {
            description = new MetadataDescription("1.0 rc3");
        }

        this.description = description;
    }

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

        if (unit instanceof SubUnit && unit.getUnitId() != null) {
            SubUnit subUnit = (SubUnit) unit;
            subUnit.addPropertyChangeListener(OperationCenter.SUB_UNIT_ID, this);
        }
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
        SortedMap<String, Unit> newUnitMap = new TreeMap<String, Unit>(idComparator);

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

        if (description == null) {
            description = new MetadataDescription("1.0 rc3");
        }
        sb.append(description);

        for (Unit unit : unitMap.values()) {
            sb.append(unit);
        }

        return sb.toString();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(OperationCenter.SUB_UNIT_ID)) {
            SortedMap<String, Unit> newUnitMap = new TreeMap<String, Unit>();

            for (Unit unit : unitMap.values()) {
                newUnitMap.put(unit.getIdentifier(), unit);
            }

            this.unitMap = newUnitMap;
        }
    }
}
