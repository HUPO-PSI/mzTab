package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 05/02/13
 */
public class MZTabRecordRun {
    public static void main(String[] args) {
        Peptide record = new Peptide();

        String sequence = "EIEILACEIR";
        record.addValue(1, sequence);
        record.addValue(4, MZBoolean.True);
        SplitList<Param> searchEngineScore = new SplitList<Param>(MZTabConstants.BAR);
        searchEngineScore.add(new CVParam("MS", "MS:1001155", "Sequest:xorr", "2"));
        searchEngineScore.add(new CVParam("MS", "MS:1001171", "Mascot score", "47.2"));
        record.addValue(8, searchEngineScore);

        assertEquals(record.getSequence(), sequence);
        assertEquals(record.getUnique(), MZBoolean.True);
        assertTrue(record.getSearchEngineScore().size() == 2);
    }
}
