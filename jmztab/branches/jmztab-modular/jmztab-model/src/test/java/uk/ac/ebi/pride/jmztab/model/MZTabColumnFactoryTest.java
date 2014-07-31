package uk.ac.ebi.pride.jmztab.model;

import org.junit.Test;

import java.util.SortedMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author qingwei
 * @since 29/05/13
 */
public class MZTabColumnFactoryTest {
    @Test
    public void testProteinColumns() throws Exception {
        MZTabColumnFactory factory = MZTabColumnFactory.getInstance(Section.Protein_Header);
        SortedMap<String, MZTabColumn> stableColumns = factory.getStableColumnMapping();
        SortedMap<String, MZTabColumn> optionalColumns = factory.getOptionalColumnMapping();

        assertTrue(stableColumns.size() == 10);
        assertTrue(optionalColumns.size() == 0);
        int optionSize = 0;

        factory.addGoTermsOptionalColumn();
        factory.addReliabilityOptionalColumn();
        factory.addURIOptionalColumn();
        optionSize += 3;
        assertTrue(optionalColumns.size() == 3);

        MsRun msRun1 = new MsRun(1);
        MsRun msRun2 = new MsRun(2);
        Assay assay1 = new Assay(1);
        StudyVariable studyVariable1 = new StudyVariable(1);
        StudyVariable studyVariable2 = new StudyVariable(2);
        MZTabColumn column;

        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun1);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_UNIQUE, msRun1);
        optionSize += 3;
        assertTrue(optionalColumns.size() == optionSize);

        factory.addOptionalColumn(ProteinColumn.NUM_PSMS, msRun2);
        factory.addOptionalColumn(ProteinColumn.NUM_PEPTIDES_DISTINCT, msRun2);
        optionSize += 2;
        assertTrue(optionalColumns.size() == optionSize);

        factory.addBestSearchEngineScoreOptionalColumn(ProteinColumn.BEST_SEARCH_ENGINE_SCORE, 1);
        factory.addSearchEngineScoreOptionalColumn(ProteinColumn.SEARCH_ENGINE_SCORE, 1, msRun1);
        optionSize += 2;
        assertTrue(optionalColumns.size() == optionSize);
        System.out.println(factory);

        factory.addAbundanceOptionalColumn(assay1);
        optionSize += 1;
        assertTrue(optionalColumns.size() == optionSize);

        factory.addAbundanceOptionalColumn(studyVariable1);
        optionSize += 3;
        assertTrue(optionalColumns.size() == optionSize);

        factory.addOptionalColumn(msRun1, "my_value", String.class);
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
