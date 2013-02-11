package uk.ac.ebi.pride.jmztab.utils;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * User: Qingwei
 * Date: 11/02/13
 */
public class StringUtilsTest {
    @Test
    public void testGetId() throws Exception {
        assertTrue(StringUtils.parseId("[4]").equals(4));
        assertTrue(StringUtils.parseId("[4") == null);
        assertTrue(StringUtils.parseId("4]") == null);
        assertTrue(StringUtils.parseId("[14]").equals(14));
    }

    @Test
    public void testParseUnitId() throws Exception {
        assertTrue(StringUtils.parseUnitId("PRIDE_1234"));
        assertFalse(StringUtils.parseUnitId("1234"));
        assertFalse(StringUtils.parseUnitId("_1234"));
        assertFalse(StringUtils.parseUnitId(""));
        assertFalse(StringUtils.parseUnitId("*"));
        assertTrue(StringUtils.parseUnitId("P_"));
    }
}
