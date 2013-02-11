package uk.ac.ebi.pride.jmztab.errors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Qingwei
 * Date: 29/01/13
 */
public class MZTabErrorTest {
    @Test
    public void testError() throws Exception {
        MZTabError error = new MZTabError(LogicalErrorType.NULL, "1", "5");
        assertTrue(error.getType().getCode().equals(1000));
        System.out.println(error.getMessage());
    }
}
