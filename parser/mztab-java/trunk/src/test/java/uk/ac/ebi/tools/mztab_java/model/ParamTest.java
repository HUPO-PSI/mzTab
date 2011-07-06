package uk.ac.ebi.tools.mztab_java.model;

import uk.ac.ebi.tools.mztab_java.model.Param.ParamType;
import junit.framework.TestCase;

public class ParamTest extends TestCase {
	private Param param;

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testParamStringStringStringString() {
		param = new Param("PRIDE", "PRIDE:12345", "my param", "some value");
		
		assertEquals(ParamType.CV_PARAM, param.getType());
		assertEquals("PRIDE", param.getCvLabel());
		assertEquals("PRIDE:12345", param.getAccession());
		assertEquals("my param", param.getName());
		assertEquals("some value", param.getValue());
	}

	public void testParamStringString() {
		param = new Param("the user", "param");
		
		assertEquals(ParamType.USER_PARAM, param.getType());
		assertEquals("the user", param.getName());
		assertEquals("param", param.getValue());
		assertNull(param.getCvLabel());
		assertNull(param.getAccession());
	}

	public void testParamString() {
		param = new Param("[PRIDE, PRIDE:12345, my param, some value]");
		
		assertEquals(ParamType.CV_PARAM, param.getType());
		assertEquals("PRIDE", param.getCvLabel());
		assertEquals("PRIDE:12345", param.getAccession());
		assertEquals("my param", param.getName());
		assertEquals("some value", param.getValue());
		assertEquals("[PRIDE,PRIDE:12345,my param,some value]", param.toString());
		
		param = new Param("[the user, param]");
		
		assertEquals(ParamType.USER_PARAM, param.getType());
		assertEquals("the user", param.getName());
		assertEquals("param", param.getValue());
		assertNull(param.getCvLabel());
		assertNull(param.getAccession());
		assertEquals("[the user,param]", param.toString());
	}

}
