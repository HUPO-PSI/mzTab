package uk.ac.ebi.pride.jmztab.model;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.Modification;

public class ModificationTest extends TestCase {

	public void testModificationStringInteger() {
		Modification mod;
		try {
			mod = new Modification("MOD:12345", 1);
			
			
			assertEquals(mod.getPosition().size(), 1);
			assertEquals(new Integer(1), mod.getPosition().get(0));
			assertNull(mod.getPositionReliability().get(0));
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testModificationStringIntegerDouble() {
		Modification mod;
		try {
			mod = new Modification("MOD:12345", 1, 0.4);
			
			assertEquals(mod.getPosition().size(), 1);
			assertEquals(mod.getPosition().get(0), new Integer(1));
			assertEquals(new Double(0.4), mod.getPositionReliability().get(0));
		
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testAddPosition() {
		Modification mod;
		try {
			mod = new Modification("MOD:12345", 1);
			mod.addPosition(5, 0.2);
			
			assertEquals(mod.getPosition().size(), 2);
			assertEquals(new Integer(5), mod.getPosition().get(1));
			assertEquals(new Double(0.2), mod.getPositionReliability().get(1));
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testToString() {
		Modification mod;
		try {
			mod = new Modification("MOD:12345", 1);
			mod.addPosition(5, 0.2);
		
			assertEquals("1|5[0.2]-MOD:12345", mod.toString());
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testFromMzTab() {
		try {
			Modification mod = new Modification("MOD:12345");
			
			assertEquals(mod.getAccession(), "MOD:12345");
			assertEquals(0, mod.getPosition().size());
			
			mod = new Modification("1|5[0.2]-MOD:12345");
			assertEquals(mod.getPosition().size(), 2);
			assertEquals(new Integer(5), mod.getPosition().get(1));
			assertEquals(new Double(0.2), mod.getPositionReliability().get(1));
		} catch (MzTabParsingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testChemModModification() {
		try {
			Modification mod = new Modification("1-CHEMMOD:+NH4");
			assertEquals(1, mod.getPosition().get(0).intValue());
			assertTrue(mod.isChemMod());
			assertEquals("+NH4", mod.getChemModDefinition());
			assertNull(mod.getChemModDelta());
			
			mod = new Modification("CHEMMOD:+36.098");
			assertEquals(0, mod.getPosition().size());
			assertTrue(mod.isChemMod());
			assertEquals("+36.098", mod.getChemModDefinition());
			assertEquals(36.098, mod.getChemModDelta());
			
			mod = new Modification("MOD:00389");
			assertFalse(mod.isChemMod());
			assertNull(mod.getChemModDefinition());
			assertNull(mod.getChemModDelta());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
