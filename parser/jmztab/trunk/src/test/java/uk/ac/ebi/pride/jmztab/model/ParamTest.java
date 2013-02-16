package uk.ac.ebi.pride.jmztab.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.Param;
import uk.ac.ebi.pride.jmztab.model.Param.ParamType;

public class ParamTest extends TestCase {

    private Param param;

    protected void setUp() throws Exception {
	super.setUp();
    }

    public void testParamStringStringStringString() {
	try {
	    param = new Param("PRIDE", "PRIDE:12345", "my param", "some value");

	    assertEquals(ParamType.CV_PARAM, param.getType());
	    assertEquals("PRIDE", param.getCvLabel());
	    assertEquals("PRIDE:12345", param.getAccession());
	    assertEquals("my param", param.getName());
	    assertEquals("some value", param.getValue());
	} catch (MzTabParsingException e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

    public void testParamStringString() {
	try {
	    param = new Param("the user", "param");

	    assertEquals(ParamType.USER_PARAM, param.getType());
	    assertEquals("the user", param.getName());
	    assertEquals("param", param.getValue());
	    assertNull(param.getCvLabel());
	    assertNull(param.getAccession());
	} catch (MzTabParsingException e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

    public void testParamString() {
	param = new Param("[PRIDE, PRIDE:12345, my param, some value]");

	assertEquals(ParamType.CV_PARAM, param.getType());
	assertEquals("PRIDE", param.getCvLabel());
	assertEquals("PRIDE:12345", param.getAccession());
	assertEquals("my param", param.getName());
	assertEquals("some value", param.getValue());
	assertEquals("[PRIDE,PRIDE:12345,my param,some value]", param.toString());

	param = new Param("[,,the user, param]");

	assertEquals(ParamType.USER_PARAM, param.getType());
	assertEquals("the user", param.getName());
	assertEquals("param", param.getValue());
	assertNull(param.getCvLabel());
	assertNull(param.getAccession());
	assertEquals("[,,the user,param]", param.toString());
    }

    public void testEscapedParam() {
	try {
	    String mzTabString = "[MOD,MOD:01506,iTRAQ4plex-117&#44; mTRAQ heavy&#44; reporter+balance reagent acylated residue,my value]";

	    Param param = new Param(mzTabString);
	    
	    assertEquals("MOD", param.getCvLabel());
	    assertEquals("MOD:01506", param.getAccession());
	    assertEquals("iTRAQ4plex-117, mTRAQ heavy, reporter+balance reagent acylated residue", param.getName());
	    assertEquals("my value", param.getValue());
	    
	    assertEquals(mzTabString, param.toString());
	    
	    Param newParam = new Param("MOD", "MOD:01506", "iTRAQ4plex-117, mTRAQ heavy, reporter+balance reagent acylated residue", "my value");
	    assertEquals(mzTabString, newParam.toString());
	} catch (MzTabParsingException ex) {
	    Logger.getLogger(ParamTest.class.getName()).log(Level.SEVERE, null, ex);
	    fail(ex.getMessage());
	}
    }
}
