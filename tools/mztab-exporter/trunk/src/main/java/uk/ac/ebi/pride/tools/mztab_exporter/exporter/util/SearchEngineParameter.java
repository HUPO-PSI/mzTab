package uk.ac.ebi.pride.tools.mztab_exporter.exporter.util;

import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.Param;

public enum SearchEngineParameter {
	MASCOT("mascot", "MS", "MS:1001207", "Mascot"),
	OMSSA("omssa", "MS", "MS:1001475", "OMSSA"),
	SEQUEST("sequest", "MS", "MS:1001208", "Sequest"),
	SPECTRUMMILL("spectrummill", "MS", "MS:1000687", "Spectrum Mill for MassHunter Workstation"),
	SPECTRAST("spectrast", "MS", "MS:1001477", "SpectaST"),
	XTANDEM_1("xtandem", "MS", "MS:1001476", "X!Tandem"),
	XTANDEM_2("x!tandem", "MS", "MS:1001476", "X!Tandem");
	
	
	private SearchEngineParameter(String expectedName, String cvLabel,
			String accession, String name) {
		this.expectedName = expectedName;
		this.cvLabel = cvLabel;
		this.accession = accession;
		this.name = name;
	}
	/**
	 * The name that's probably encountered
	 * in the PRIDE XML file.
	 */
	private String expectedName;
	private String cvLabel;
	private String accession;
	private String name;
	
	/**
	 * Tries to guess which search engine is described
	 * by the passed name. In case no matching parameter
	 * is found, null is returned.
	 * @param searchEngineName
	 * @return
	 */
	public static SearchEngineParameter getParam(String searchEngineName) {
		searchEngineName = searchEngineName.toLowerCase();
		
		for (SearchEngineParameter p : values()) {
			if (searchEngineName.contains(p.getExpectedName()))
				return p;
		}
		
		return null;
	}

	public String getExpectedName() {
		return expectedName;
	}

	public String getCvLabel() {
		return cvLabel;
	}

	public String getAccession() {
		return accession;
	}

	public String getName() {
		return name;
	}
	
	public Param toMzTabParam() throws MzTabParsingException {
		return new Param(cvLabel, accession, name, null);
	}
}
