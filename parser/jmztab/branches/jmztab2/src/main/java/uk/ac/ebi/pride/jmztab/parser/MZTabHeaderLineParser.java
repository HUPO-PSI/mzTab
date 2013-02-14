package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.MZTabColumn;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;

import java.util.SortedMap;

/**
 * The class used to do common operations in protein/peptide/small_molecular
 * header line. There are two main categories in columns: stable columns and
 * optional columns.
 *
 * User: Qingwei
 * Date: 11/02/13
 */
public class MZTabHeaderLineParser extends MZTabLineParser {
    private MZTabColumnFactory factory;

    /**
     * We assume that user before call this method, have check the raw line
     * is not empty line and start with section prefix.
     */
    protected MZTabHeaderLineParser(String line, MZTabColumnFactory factory) {
        super(line);
        this.factory = factory;

        SortedMap<Integer, MZTabColumn> mapping = this.factory.getColumnMapping();
        int offset = mapping.lastKey();
        if (offset == items.length - 1) {
            // no optional columns
            return;
        }

        matchStableColumns();
        matchOptionalColumns(offset);
    }

    /**
     * Check the stable columns matching, including position and header name.
     * If position or header name not matched, throw MZTabException, and stop
     * execution.
     */
    public boolean matchStableColumns() throws MZTabException {
        boolean match = true;

        String header;
        for (int i = 1; i < items.length; i++) {
            header = factory.getColumn(i).getHeader();
            match = header.equals(items[i]);
            if (! match) {
                MZTabError error = new MZTabError(
                        FormatErrorType.StableColumn, false,
                        header, "" + factory.getColumn(header).getPosition(),
                       "" + i,  items[i]
                );
                throw new MZTabException(error);
            }
        }

        return match;
    }

    public boolean matchOptionalColumns(int offset) throws MZTabException {
        return true;
    }

}
