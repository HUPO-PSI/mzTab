package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.MZTabColumn;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;

import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Create and maintain a couple of mappings between physical position and logical position.
 * Physical position: Integer, the position of mzTab file.
 * Logical position: String, the internal order of specification.
 *
 * User: qingwei
 * Date: 16/10/13
 */
public class PositionMapping {
    // physicalPosition <--> logicalPosition
    private SortedMap<Integer, String> mappings = new TreeMap<Integer, String>();

    public PositionMapping(MZTabColumnFactory factory, String headerLine) {
        this(factory, headerLine.split("\t"));
    }

    public PositionMapping(MZTabColumnFactory factory, String[] headerList) {
        String header;
        for (int physicalPosition = 0; physicalPosition < headerList.length; physicalPosition++) {
            header = headerList[physicalPosition];
            MZTabColumn column = factory.findColumnByHeader(header);
            if (column != null) {
                put(physicalPosition, column.getLogicPosition());
            }
        }
    }

    public void put(Integer physicalPosition, String logicalPosition) {
        this.mappings.put(physicalPosition, logicalPosition);
    }

    public boolean isEmpty() {
        return mappings.isEmpty();
    }

    public int size() {
        return mappings.size();
    }

    public boolean containsKey(Integer key) {
        return mappings.containsKey(key);
    }

    public Set<Integer> keySet() {
        return mappings.keySet();
    }

    public Collection<String> values() {
        return mappings.values();
    }

    public String get(Integer key) {
        return mappings.get(key);
    }

    /**
     * Exchange "LogicalPosition, PhysicalPosition"
     */
    public SortedMap<String, Integer> exchange() {
        SortedMap<String, Integer> exchangeMappings = new TreeMap<String, Integer>();

        String logicalPosition;
        for (Integer physicalPosition : mappings.keySet()) {
            logicalPosition = mappings.get(physicalPosition);
            exchangeMappings.put(logicalPosition, physicalPosition);
        }

        return exchangeMappings;
    }
}
