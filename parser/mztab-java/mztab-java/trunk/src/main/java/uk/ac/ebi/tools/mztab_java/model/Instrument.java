package uk.ac.ebi.tools.mztab_java.model;

/**
 * An instrument in the meta-data
 * section of a mzTab file.
 * @author jg
 *
 */
public class Instrument {
	private Param source;
	private Param analyzer;
	private Param detector;
	
	public Instrument(Param source, Param analyzer, Param detector) {
		super();
		this.source = source;
		this.analyzer = analyzer;
		this.detector = detector;
	}
	
	public Instrument() {
		
	}

	public Param getSource() {
		return source;
	}

	public void setSource(Param source) {
		this.source = source;
	}

	public Param getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Param analyzer) {
		this.analyzer = analyzer;
	}

	public Param getDetector() {
		return detector;
	}

	public void setDetector(Param detector) {
		this.detector = detector;
	}
}
