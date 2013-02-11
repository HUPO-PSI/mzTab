package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.MetadataElement;
import uk.ac.ebi.pride.jmztab.model.MetadataProperty;
import uk.ac.ebi.pride.jmztab.model.Section;
import uk.ac.ebi.pride.jmztab.model.Unit;

/**
 * Metadata Element start with MTD, and structure like:
 * MTD  {unit}-{element}([{id}])-{property} value
 *
 * In metadata section, every line is a Metadata Element Object.
 *
 * User: Qingwei
 * Date: 08/02/13
 */
public abstract class MTDLineParser extends MZTabLineParser {
    private Unit unit;
    private MetadataElement element;
    private Integer id;
    private MetadataProperty property;
    private String valueLabel;

    protected MTDLineParser(String mtdLine) throws MZTabException {
        super(mtdLine);

        if (items.length > 3) {

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

    public abstract Object getValue();
}
