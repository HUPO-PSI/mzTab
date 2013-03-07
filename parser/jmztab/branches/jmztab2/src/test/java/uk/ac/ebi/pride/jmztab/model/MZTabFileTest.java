package uk.ac.ebi.pride.jmztab.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: qingwei
 * Date: 01/03/13
 */
public class MZTabFileTest {
    private MZTabFile tabFile;

    @Before
    public void setUp() throws Exception {
        tabFile = new MZTabFile(new MZTabFileParser(new File("example/mztab_itraq_example.txt"), true));
    }

    @Test
    public void testUnitId() throws Exception {
        String oldUnitId = "PRIDE_1234";
        String newUnitId = "Test_1234";

        Collection<Unit> oldUnit = tabFile.getUnits(oldUnitId);
        tabFile.modifyUnitId(oldUnitId, newUnitId);

        Collection<Unit> newUnit = tabFile.getUnits(newUnitId);
        assertEquals(oldUnit, newUnit);

        Collection<Protein> proteins = tabFile.getProteins();
        if (proteins != null) {
            for (Protein protein : proteins) {
                assertTrue(protein.getUnitId().equals(newUnitId));
            }
        }

        Collection<Peptide> peptides = tabFile.getPeptides();
        if (peptides != null) {
            for (Peptide peptide : peptides) {
                assertTrue(peptide.getUnitId().equals(newUnitId));
            }
        }
    }

    @Test
    public void testAbundanceColumn() throws Exception {
        SortedMap<Integer, SubUnit> subUnitMap = tabFile.getMetadata().getSubUnits();
        Collection<SubUnit> subUnits = subUnitMap.values();

        Collection<AbundanceColumn> proteinAbundanceColumns = tabFile.getProteinColumnFactory().getAbundanceColumnMapping().values();
        Collection<AbundanceColumn> peptideAbundanceColumns = tabFile.getProteinColumnFactory().getAbundanceColumnMapping().values();
        for (AbundanceColumn column : proteinAbundanceColumns) {
            assertTrue(subUnits.contains(column.getSubUnit()));
        }
        for (AbundanceColumn column : peptideAbundanceColumns) {
            assertTrue(subUnits.contains(column.getSubUnit()));
        }

        int oldSubId = subUnitMap.firstKey();
        SubUnit subUnit = subUnitMap.get(oldSubId);
        int newSubId = subUnitMap.lastKey() + 1;
        tabFile.modifySubUnitId(subUnit, newSubId);

        Iterator<AbundanceColumn> it = proteinAbundanceColumns.iterator();
        while (it.hasNext()) {
            if (it.next().getSubUnit().equals(subUnit)) {
                assertTrue(it.next().getSubUnit().equals(subUnit));
                assertTrue(it.next().getSubUnit().equals(subUnit));
            }
        }

        it = peptideAbundanceColumns.iterator();
        while (it.hasNext()) {
            if (it.next().getSubUnit().equals(subUnit)) {
                assertTrue(it.next().getSubUnit().equals(subUnit));
                assertTrue(it.next().getSubUnit().equals(subUnit));
            }
        }
    }

    @Test
    public void testSetPosition() throws Exception {
        int oldPosition = 22;
        Collection<Object> proteinValues = new ArrayList<Object>();
        for (Protein protein : tabFile.getProteins()) {
            proteinValues.add(protein.getValue(oldPosition));
        }

        int newPosition = 31;
        tabFile.modifyProteinColumnPosition(oldPosition, newPosition);
        for (Protein protein : tabFile.getProteins()) {
            assertTrue(protein.getValue(oldPosition) == null);
            assertTrue(proteinValues.contains(protein.getValue(newPosition)));
        }


        oldPosition = 19;
        Collection<Object> peptideValues = new ArrayList<Object>();
        for (Peptide peptide : tabFile.getPeptides()) {
            peptideValues.add(peptide.getValue(oldPosition));
        }
        newPosition = 28;

        tabFile.modifyPeptideColumnPosition(oldPosition, newPosition);
        for (Peptide peptide : tabFile.getPeptides()) {
            assertTrue(peptide.getValue(oldPosition) == null);
            assertTrue(peptideValues.contains(peptide.getValue(newPosition)));
        }
    }

    @After
    public void tearDown() throws Exception {
        tabFile = null;
    }
}
