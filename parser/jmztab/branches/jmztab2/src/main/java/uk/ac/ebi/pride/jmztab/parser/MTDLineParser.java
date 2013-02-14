package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     *
     * @param mtdLine
     */
    protected MTDLineParser(String mtdLine) throws MZTabException {
        super(mtdLine);

        if (items.length != 3 || MZTabParserUtils.isEmpty(items[2])) {
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
     * Notice 1:
     * In replicate unit, MetadataElement maybe null. For example: Exp_1-rep[1].
     *
     * Notice 2:
     * ColUnit structure is: {Unit_ID}-colunit-{XXXX}, need operate separately.
     */
    private void parseDefineLabel() {
        String regexp = "([A-Za-z]{1}[\\w_]*)(-(rep|sub)\\[(\\d+)\\])?(-((\\w+)(\\[(\\d+)\\])?)?(-(\\w+))?)?";

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(defineLabel);

        if (matcher.find()) {
            // Stage 1: create Unit.
            String unitId = matcher.group(1);
            // group[3] value is sub or rep.
            String type = matcher.group(3);

            if (type == null) {
                unit = new Unit(unitId);
            } else if (type.equals(SubUnit.SUB)) {
                unit = new SubUnit(unitId, new Integer(matcher.group(4)));
            } else if (type.equals(ReplicateUnit.REP)) {
                unit = new ReplicateUnit(unitId, new Integer(matcher.group(4)));
            }

            // Stage 2: create MetadataElement and MetaProperty
            String elementLabel = matcher.group(7);
            element = MetadataElement.findElement(elementLabel);

            // Stage 3: create id
            String idLabel = matcher.group(9);
            if (idLabel != null) {
                this.id = new Integer(idLabel);
            }

            // Stage 4: create MetadataProperty
            String propertyLabel = matcher.group(11);
            property = MetadataProperty.findProperty(elementLabel, propertyLabel);
        }
    }

    private void parseValueLabel() {
        String regexp = "";

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(defineLabel);

        if (matcher.find()) {

        }
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
