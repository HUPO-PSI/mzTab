package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.model.MZTabColumn;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * User: Qingwei
 * Date: 14/02/13
 */
public abstract class MZTabDataLineParser extends MZTabLineParser {
    private String number;
    private SortedMap<Integer, MZTabColumn> mapping;
    private Metadata metadata;

    private static SortedMap<Integer, String> errorLines = new TreeMap<Integer, String>();

    /**
     * @param number line number.
     */
    protected MZTabDataLineParser(String number, String line, MZTabColumnFactory factory, Metadata metadata) {
        super(line);

        this.number = number;
        this.mapping = factory.getColumnMapping();

        if (metadata == null) {
            throw new NullPointerException("Metadata should be parser first.");
        }
        this.metadata = metadata;

        checkCount();
    }

    protected void checkCount() {
        int headerCount = mapping.size();
        int dataCount = items.length - 1;

        if (headerCount != dataCount) {
            new MZTabError(FormatErrorType.CountMatch, number, "" + dataCount, "" + headerCount);
        }
    }

    /**
     * accession MUST be unique within one Unit.
     *
     * @param accession can not "null"
     * @param unitId should be found in metadata.
     */
    protected void checkAccessionAndUnitId(String accession, String unitId) {

    }
}
