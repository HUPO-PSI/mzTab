package uk.ac.ebi.pride.mztab_java.model;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((analyzer == null) ? 0 : analyzer.hashCode());
		result = prime * result
				+ ((detector == null) ? 0 : detector.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instrument other = (Instrument) obj;
		if (analyzer == null) {
			if (other.analyzer != null)
				return false;
		} else if (!analyzer.equals(other.analyzer))
			return false;
		if (detector == null) {
			if (other.detector != null)
				return false;
		} else if (!detector.equals(other.detector))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
}
