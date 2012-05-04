package uk.ac.ebi.pride.mztab_java.model;

import junit.framework.TestCase;
import uk.ac.ebi.pride.mztab_java.MzTabParsingException;
import uk.ac.ebi.pride.mztab_java.model.Modification;

public class ModificationTest extends TestCase {

	public void testModificationStringInteger() {
		Modification mod = new Modification("MOD:12345", 1);
		
		assertEquals(mod.getPosition().size(), 1);
		assertEquals(new Integer(1), mod.getPosition().get(0));
		assertNull(mod.getPositionReliability().get(0));
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
		Modification mod = new Modification("MOD:12345", 1);
		mod.addPosition(5, 0.2);
		
		assertEquals(mod.getPosition().size(), 2);
		assertEquals(new Integer(5), mod.getPosition().get(1));
		assertEquals(new Double(0.2), mod.getPositionReliability().get(1));
	}

	public void testToString() {
		Modification mod = new Modification("MOD:12345", 1);
		mod.addPosition(5, 0.2);
		
		assertEquals("1|5[0.2]-MOD:12345", mod.toString());
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
}
