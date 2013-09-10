package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.MZTAB;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * User: Qingwei
 * Date: 31/05/13
 */
public class MZTabDescription {
    public enum Mode {Complete, Summary}
    public enum Type {Quantification, Identification}

    public static final String default_version = "1.0 rc5";

    private String version;
    private Mode mode;
    private Type type;
    private String id;

    public MZTabDescription(Mode mode, Type type) {
        this(null, mode, type);
    }

    public MZTabDescription(String version, Mode mode, Type type) {
        this.version = version == null ? default_version : version;

        if (mode == null) {
            throw new NullPointerException("mz-tab mode should be defined!");
        }

        if (type == null) {
            throw new NullPointerException("mz-tab type should be defined!");
        }

        this.mode = mode;
        this.type = type;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
        printPrefix(sb).append(MZTAB).append(MINUS).append(MZTAB_TYPE).append(TAB).append(type).append(NEW_LINE);

        if (id != null) {
            printPrefix(sb).append(MZTAB).append(MINUS).append(MZTAB_ID).append(TAB).append(id).append(NEW_LINE);
        }

        return sb.toString();
    }
}
