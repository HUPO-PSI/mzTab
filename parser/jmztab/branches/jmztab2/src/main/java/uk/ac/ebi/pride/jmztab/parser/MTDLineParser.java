package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;
import uk.ac.ebi.pride.jmztab.utils.StringUtils;

import java.util.Arrays;

/**
 * Metadata Element start with MTD, and structure like:
 * MTD  {unit}-{element}([{id}])-{property} value
 *
 * In metadata section, every line is a Metadata Element Object.
 *
 * User: Qingwei
 * Date: 08/02/13
 */
public class MTDLineParser extends MZTabLineParser {
    private String defineLabel;
    private String valueLabel;

    private Unit unit;
    private MetadataElement element;
    private Integer id;
    private MetadataProperty property;
    private Object value;

    protected MTDLineParser(String mtdLine) {
        super(mtdLine);

        if (items.length != 3 || StringUtils.isEmpty(items[2])) {
            MZTabError error = new MZTabError(FormatErrorType.MTDLine, mtdLine);
            throw new MZTabException(error);
        }

        this.defineLabel = items[1];
        this.valueLabel = items[2];

        parseDefineLabel();
        parseValueLabel();
    }

    /**
     * DefineLabel structure: {unitID}(-SUB_ID|-REP_ID)-{element}([{id}])-{property}
     *
     * The (-SUB_ID|-REP_ID), ([{id}]) and {property} are optional.
     * parse label and generate Unit, MetadataElement, id, MetadataProperty objects.
     * If optional item not exists, return null.
     *
     * Notice:
     * In replicate unit, MetadataElement maybe null. For example:
     * Exp_1-rep[1].
     */
    private void parseDefineLabel() {
        String[] items = defineLabel.split(MZTabConstants.MINUS);
        String unitId;
        String subIdLabel;
        Integer subId;
        String repIdLabel;
        Integer repId;
        String elementLabel;
        String propertyLabel;

        MZTabError error;

        if (items.length < 2 || items.length > 4) {
            error = new MZTabError(FormatErrorType.MTDDefineLabel, defineLabel);
            throw new MZTabException(error);
        }

        // Stage 1: create Unit.
        int offset = 0;
        unitId = items[0];
        if (! StringUtils.parseUnitId(unitId)) {
            error = new MZTabError(FormatErrorType.UnitID, unitId);
            throw new MZTabException(error);
        }
        offset++;

        if (items[offset].startsWith(SubUnit.SUB)) {
            subIdLabel = items[1];
            subId = StringUtils.parseId(subIdLabel);
            if (subId == null) {
                error = new MZTabError(LogicalErrorType.MTDDefineIDLabel, defineLabel, subIdLabel);
                throw new MZTabException(error);
            }
            this.unit = new SubUnit(unitId, subId);
            offset++;
        } else if (items[offset].startsWith(ReplicateUnit.REP)) {
            repIdLabel = items[1];
            repId = StringUtils.parseId(repIdLabel);
            if (repId == null) {
                error = new MZTabError(LogicalErrorType.MTDDefineIDLabel, defineLabel, repIdLabel);
                throw new MZTabException(error);
            }
            this.unit = new ReplicateUnit(unitId,  repId, valueLabel);
            offset++;
            if (offset == items.length) {
                // In replicate unit, MetadataElement maybe null.
                return;
            }
        } else {
            // no sub[id] or rep[id]
            this.unit = new Unit(unitId);
        }

        // Stage 2: create MetadataElement.
        elementLabel = items[offset];
        id = StringUtils.parseId(elementLabel);
        if (id != null) {
            int endIndex = elementLabel.indexOf("[");
            elementLabel = elementLabel.substring(0, endIndex);
        }
        try {
            element = MetadataElement.valueOf(elementLabel.toUpperCase());
        } catch (IllegalArgumentException e) {
            // can not get MetadataElement.
            error = new MZTabError(
                    LogicalErrorType.MTDElementLabel,
                    defineLabel,
                    elementLabel,
                    Arrays.toString(MetadataElement.values())
            );
            throw new MZTabException(error);
        }
        offset++;
        if (offset == items.length) {
            return;
        }

        // Stage 3: create MetadataProperty.
        propertyLabel = items[offset];
        try {
            property = MetadataProperty.valueOf((elementLabel + "_" + propertyLabel).toUpperCase());
        } catch (IllegalArgumentException e) {
            // can not get MetadataProperty
            error = new MZTabError(
                    LogicalErrorType.MTDPropertyLabel,
                    defineLabel,
                    propertyLabel,
                    Arrays.toString(MetadataProperty.values())
            );
            throw new MZTabException(error);
        }
    }

    private void parseValueLabel() {

    }

    /**
     * based on identifier to generate Unit, SubUnit or RepUnit.
     */
    public Unit getUnit() {
        return unit;
    }

    public MetadataElement getElement() {
        return element;
    }

    /**
     * null means no unit's Id.
     */
    public Integer getId() {
        return id;
    }

    public MetadataProperty getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }
}
