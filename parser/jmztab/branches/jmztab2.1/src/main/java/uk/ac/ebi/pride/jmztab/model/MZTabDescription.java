package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.MINUS;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.MZTAB;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.MZTAB_ID;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.MZTAB_MODE;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.MZTAB_VERSION;

/**
 * User: Qingwei
 * Date: 31/05/13
 */
public class MZTabDescription {
    public enum Mode {Complete, Summary}
    public static final String default_version = "1.0 rc4";

    private String version;
    private Mode mode;
    private String id;

    public MZTabDescription() {
        this(null, null);
    }

    public MZTabDescription(Mode mode) {
        this(null, mode);
    }

    public MZTabDescription(String version, Mode mode) {
        this.version = version == null ? default_version : version;
        this.mode = mode == null ? Mode.Summary : mode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private StringBuilder printPrefix(StringBuilder sb) {
        sb.append(Section.Metadata.getPrefix()).append(TAB);

        return sb;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        printPrefix(sb).append(MZTAB).append(MINUS).append(MZTAB_VERSION).append(TAB).append(version).append(NEW_LINE);
        printPrefix(sb).append(MZTAB).append(MINUS).append(MZTAB_MODE).append(TAB).append(mode).append(NEW_LINE);

        if (id != null) {
            printPrefix(sb).append(MZTAB).append(MINUS).append(MZTAB_ID).append(TAB).append(id).append(NEW_LINE);
        }

        return sb.toString();
    }
}
