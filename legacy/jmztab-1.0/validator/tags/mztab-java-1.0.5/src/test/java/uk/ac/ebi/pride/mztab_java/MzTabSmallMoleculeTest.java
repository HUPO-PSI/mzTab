package uk.ac.ebi.pride.mztab_java;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import uk.ac.ebi.pride.mztab_java.model.Modification;
import uk.ac.ebi.pride.mztab_java.model.SmallMolecule;

public class MzTabSmallMoleculeTest extends TestCase {
	MzTabFile mzTabFile;
	File sourceFile;
	
	public void setUp() throws Exception {
	    try {
	        URL testFile = getClass().getClassLoader().getResource("mztab_small_molecules.txt");
	        assertNotNull("Error loading mzTab test file", testFile);
	        sourceFile = new File(testFile.toURI());
	        mzTabFile = new MzTabFile(sourceFile);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	        fail(ex.getMessage());
	    }
	}

	public void testGetSmallMolecules() {
		Collection<SmallMolecule> sms = mzTabFile.getSmallMolecules();
		assertEquals(4, sms.size());
		
		Iterator<SmallMolecule> it = sms.iterator();
		
		assertEquals("TG46:0", it.next().getIdentifier().get(0));
		assertEquals("TG46:0", it.next().getIdentifier().get(0));
		assertEquals("TG48:0", it.next().getIdentifier().get(0));
		assertEquals("TG48:1", it.next().getIdentifier().get(0));
	}

	public void testGetSmallMoleculesString() {
		Collection<SmallMolecule> sms = mzTabFile.getSmallMolecules("LDA_4");
		assertEquals(4, sms.size());
		
		Iterator<SmallMolecule> it = sms.iterator();
		
		assertEquals("TG46:0", it.next().getIdentifier().get(0));
		assertEquals("TG46:0", it.next().getIdentifier().get(0));
		assertEquals("TG48:0", it.next().getIdentifier().get(0));
		assertEquals("TG48:1", it.next().getIdentifier().get(0));
	}

	public void testGetSmallMoleculesForIdentifier() {
		Collection<SmallMolecule> sms = mzTabFile.getSmallMoleculesForIdentifier("TG46:0");
		
		assertEquals(2, sms.size());
		Iterator<SmallMolecule> it = sms.iterator();
		SmallMolecule sm = it.next();
		
		assertNotNull(sm);
		
		assertEquals(1, sm.getCharge().intValue());
		assertEquals(1, sm.getModifications().size());
		Modification mod = sm.getModifications().get(0);
		assertEquals(14, mod.getPosition().get(0).intValue());
		assertEquals("CHEMMOD:+NH4", mod.getAccession());
		
		assertTrue(it.hasNext());
		sm = it.next();
		assertNotNull(sm);
		
		assertEquals(2, sm.getCharge().intValue());
		assertEquals(1, sm.getModifications().size());
		mod = sm.getModifications().get(0);
		assertEquals(0, mod.getPosition().size());
		assertEquals("CHEMMOD:-NH4", mod.getAccession());
	}

}
