package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;
import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.NEW_LINE;

/**
 * The name of the instrument used in the experiment. Multiple instruments are numbered 1..n
 * The instrument's source used in the experiment. Multiple instruments are numbered 1..n
 * The instrument’s analyzer type used in the experiment. Multiple instruments are enumerated 1..n
 * The instrument's detector type used in the experiment. Multiple instruments are numbered 1..n
 *
 * User: Qingwei
 * Date: 30/01/13
 */
public class Instrument extends UnitElement {
    private Param name;
    private Param source;
    private Param analyzer;
    private Param detector;

    public Instrument(int id, Unit unit) {
        super(id, unit);
    }

    public Param getName() {
        return name;
    }

    public void setName(Param name) {
        this.name = name;
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

    /**
     * MTD  {UNIT_ID}-instrument[{id}]-{name|source|analyzer|detector}  {param}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (name != null) {
            sb.append(printProperty(INSTRUMENT_NAME, name)).append(NEW_LINE);
        }
        if (source != null) {
            sb.append(printProperty(INSTRUMENT_SOURCE, source)).append(NEW_LINE);
        }
        if (analyzer != null) {
            sb.append(printProperty(INSTRUMENT_ANALYZER, analyzer)).append(NEW_LINE);
        }
        if (detector != null) {
            sb.append(printProperty(INSTRUMENT_DETECTOR, detector)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
