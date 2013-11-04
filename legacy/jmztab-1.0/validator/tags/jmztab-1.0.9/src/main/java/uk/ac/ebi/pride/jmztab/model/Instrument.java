package uk.ac.ebi.pride.jmztab.model;

/**
 * An instrument in the meta-data section of a mzTab file.
 *
 * @author jg
 *
 */
public class Instrument {

    private Param source;
    private Param analyzer;
    private Param detector;
    private Param name;

    public Instrument(Param source, Param analyzer, Param detector, Param name) {
	super();
	this.source = source;
	this.analyzer = analyzer;
	this.detector = detector;
	this.name = name;
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

    public Param getName() {
	return name;
    }

    public void setName(Param name) {
	this.name = name;
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 37 * hash + (this.source != null ? this.source.hashCode() : 0);
	hash = 37 * hash + (this.analyzer != null ? this.analyzer.hashCode() : 0);
	hash = 37 * hash + (this.detector != null ? this.detector.hashCode() : 0);
	hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Instrument other = (Instrument) obj;
	if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
	    return false;
	}
	if (this.analyzer != other.analyzer && (this.analyzer == null || !this.analyzer.equals(other.analyzer))) {
	    return false;
	}
	if (this.detector != other.detector && (this.detector == null || !this.detector.equals(other.detector))) {
	    return false;
	}
	if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
	    return false;
	}
	return true;
    }
}
