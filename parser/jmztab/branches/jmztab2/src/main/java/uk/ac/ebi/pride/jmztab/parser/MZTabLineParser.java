package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.Section;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * User: Qingwei
 * Date: 10/02/13
 */
public abstract class MZTabLineParser {
    protected Section section;

    /**
     * based on TAB char to split raw line into String array.
     */
    protected String[] items;

    /**
     * We assume that user before call this method, have check the raw line
     * is not empty line and start with section prefix.
     */
    protected MZTabLineParser(String line) {
        this.items = line.split("\\s*" + MZTabConstants.TAB + "\\s*");
        items[0] = items[0].trim();
        items[items.length - 1] = items[items.length - 1].trim();

        section = Section.findSection(items[0]);

        if (section == null) {
            new MZTabError(FormatErrorType.LinePrefix, line);
        }
    }

    protected Section getSection() {
        return section;
    }
}
