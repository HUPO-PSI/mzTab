package uk.ac.ebi.pride.jmztab.model;

import org.junit.Test;

import java.util.SortedMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 29/05/13
 */
public class MZTabColumnFactoryTest {
    @Test
    public void testProteinColumns() throws Exception {
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein_Header);
        SortedMap<String, MZTabColumn> stableColumns = factory.getStableColumnMapping();
        SortedMap<String, MZTabColumn> optionalColumns = factory.getOptionalColumnMapping();

        assertTrue(stableColumns.size() == 14);
        assertTrue(optionalColumns.size() == 0);
        int optionSize = 0;

        MsFile msFile1 = new MsFile(1);
        MsFile msFile2 = new MsFile(2);
        Assay assay1 = new Assay(1);
        StudyVariable studyVariable1 = new StudyVariable(1);
        StudyVariable studyVariable2 = new StudyVariable(2);
        MZTabColumn column;

        factory.addOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, msFile1);
        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msFile1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msFile1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msFile1);
        optionSize += 4;
        assertTrue(optionalColumns.size() == optionSize);
        column = optionalColumns.get(ProteinColumn.SEARCH_ENGINE_SCORE.getPosition() + msFile1.getId());
        assertTrue(column != null);

        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msFile2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msFile2);
        optionSize += 2;
        assertTrue(optionalColumns.size() == optionSize);
        column = optionalColumns.get(ProteinColumn.NUM_PEPTIDES_UNIQUE.getPosition() + msFile2.getId());
        assertTrue(column == null);

        factory.addAbundanceOptionalColumn(assay1);
        optionSize += 1;
        assertTrue(optionalColumns.size() == optionSize);

        factory.addAbundanceOptionalColumn(studyVariable1);
        optionSize += 3;
        assertTrue(optionalColumns.size() == optionSize);

        factory.addOptionalColumn(msFile1, "my_value", String.class);
        optionSize += 1;
        assertTrue(optionalColumns.size() == optionSize);

        CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);
        factory.addOptionalColumn(param, String.class);
        optionSize += 1;
        assertTrue(optionalColumns.size() == optionSize);

        factory.addAbundanceOptionalColumn(studyVariable2);
        optionSize += 3;
        assertTrue(optionalColumns.size() == optionSize);

        assertEquals(stableColumns.size() + optionalColumns.size(), factory.getColumnMapping().size());
    }
}
