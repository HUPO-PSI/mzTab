package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

/**
 * User: Qingwei
 * Date: 05/02/13
 */
public class MZTabRecordRun {
    public static void main(String[] args) {
        PeptideRecord record = new PeptideRecord();
        record.addValue(1, "EIEILACEIR");
        record.addValue(4, 1);
        SplitList<Param> searchEngineScore = new SplitList<Param>(MZTabConstants.BAR);
        searchEngineScore.add(new CVParam("MS", "MS:1001155", "Sequest:xorr", "2"));
        searchEngineScore.add(new CVParam("MS", "MS:1001171", "Mascot score", "47.2"));
        record.addValue(7, searchEngineScore);

        System.out.println(record);
    }
}
