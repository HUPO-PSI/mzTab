/*
 * Copyright 2013 European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.pride.jmztab.model;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;

/**
 *
 * @author jg
 */
public class TableObjectTest extends TestCase {

    private TableObject tableObject = new Peptide();

    public void testIsCvColumn() {
	assertFalse(tableObject.isCvColumn("opt_some_column"));
	assertTrue(tableObject.isCvColumn("opt_cv_MS:12345_my_column"));
    }

    public void testGetCvParamForCvColumn() {
	try {
	    String columnName = "opt_cv_MS:12345_emPAI_value";
	    Param param = tableObject.getCvParamForCvColumn(columnName);
	    assertEquals("MS", param.getCvLabel());
	    assertEquals("MS:12345", param.getAccession());
	    assertEquals("emPAI value", param.getName());
	    assertEquals("", param.getValue());
	} catch (MzTabParsingException e) {
	    fail(e.getMessage());
	}
    }
}
