package uk.ac.ebi.pride.tools.mztab_exporter.exporter;

import java.io.File;

public interface MzTabExporter {
	/**
	 * Converts the given input file to an
	 * mzTab file at the location specified
	 * by the outputFile. 
	 * @param inputFile The input file to convert to mzTab format.
	 * @param outputFile The location to write the mzTab String to.
	 * @throws Exception
	 */
	public void exportToMzTab(File inputFile, File outputFile) throws Exception;
}
