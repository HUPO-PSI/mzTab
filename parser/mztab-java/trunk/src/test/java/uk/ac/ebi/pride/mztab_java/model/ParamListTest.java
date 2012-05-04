package uk.ac.ebi.pride.mztab_java.model;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jmzTab.model.ParamList;

public class ParamListTest extends TestCase {
	ParamList list;
	
	public void testParamList() {
		list = new ParamList("[PRIDE,PRIDE:12345,param name, param value]|[MS, MS:12345, another param, new value]");
		
		assertEquals(2, list.size());
		assertEquals("[PRIDE,PRIDE:12345,param name,param value]|[MS,MS:12345,another param,new value]", list.toString());
	}
}
