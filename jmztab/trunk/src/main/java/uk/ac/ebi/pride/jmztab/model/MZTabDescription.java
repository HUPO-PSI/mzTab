package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.MZTAB;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * Describe all the fields which start with "mzTab-" in metadata section.
 *
 * User: Qingwei
 * Date: 31/05/13
 */
public class MZTabDescription {
    public enum Mode {Complete, Summary}
    public enum Type {Quantification, Identification}

    public static final String default_version = "1.0";

    private String version;     // mzTab-version
    private Mode mode;          // mzTab-mode
    private Type type;          // mzTab-type
    private String id;          // mzTab-ID

    /**
     * Create a mzTab metadata description, which including mode and type definitions.
     * The version of the mzTab file use default value {@link #default_version}
     *
     * @param mode The results included in an mzTab file can be reported in 2 ways: 'Complete'
     *             (when results for each assay/replicate are included) and 'Summary', when only
     *             the most representative results are reported. The value SHOULD NOT null.
     * @param type The results included in an mzTab file MUST be flagged as 'Identification' or
     *             'Quantification'  - the latter encompassing approaches that are quantification
     *             only or quantification and identification. The value SHOULD NOT null.
     */
    public MZTabDescription(Mode mode, Type type) {
        this(null, mode, type);
    }

    /**
     * Create a mzTab metadata description, which including version, mode and type definitions.
     *
     * @param version The version of the mzTab file. The default value is {@link #default_version}
     * @param mode The results included in an mzTab file can be reported in 2 ways: 'Complete'
     *             (when results for each assay/replicate are included) and 'Summary', when only
     *             the most representative results are reported. The value SHOULD NOT null.
     * @param type The results included in an mzTab file MUST be flagged as 'Identification' or
     *             'Quantification'  - the latter encompassing approaches that are quantification
     *             only or quantification and identification. The value SHOULD NOT null.
     */
    public MZTabDescription(String version, Mode mode, Type type) {
        this.version = version == null ? default_version : version;

        setMode(mode);
        setType(type);
    }

    /**
     * Get the version of the mzTab file.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version of the mzTab file.
     * @param version SHOULD NOT be empty.
     */
    public void setVersion(String version) {
        if (MZTabUtils.isEmpty(version)) {
            throw new IllegalArgumentException("mzTab-version should not be empty!");
        }

        this.version = version;
    }

    /**
     * Get the mzTab-mode. The results included in an mzTab file can be reported in 2 ways:
     * 'Complete' (when results for each assay/replicate are included) and 'Summary',
     * when only the most representative results are reported.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Set the mzTab-mode. The results included in an mzTab file can be reported in 2 ways:
     * 'Complete' (when results for each assay/replicate are included) and 'Summary',
     * when only the most representative results are reported.
     *
     * @param mode SHOULD NOT be null.
     */
    public void setMode(Mode mode) {
        if (mode == null) {
            throw new NullPointerException("mzTab-mode should be defined!");
        }

        this.mode = mode;
    }

    /**
     * Get mzTab-type value. The results included in an mzTab file MUST be flagged as
     * 'Identification' or 'Quantification'  - the latter encompassing approaches
     * that are quantification only or quantification and identification.
     */
    public Type getType() {
        return type;
    }

    /**
     * Get mzTab-type value. The results included in an mzTab file MUST be flagged as
     * 'Identification' or 'Quantification'  - the latter encompassing approaches
     * that are quantification only or quantification and identification.
     *
     * @param type SHOULD NOT be null.
     */
    public void setType(Type type) {
        if (type == null) {
            throw new NullPointerException("mzTab-type should be defined!");
        }

        this.type = type;
    }

    /**
     * Get the mzTab-ID of the mzTab file
     */
    public String getId() {
        return id;
    }

    /**
     * Set the mzTab-ID of the mzTab file. If mzTab-ID is empty, not output this item.
     */
    public void setId(String id) {
        this.id = id;
    }

    private StringBuilder printPrefix(StringBuilder sb) {
        sb.append(Section.Metadata.getPrefix()).append(TAB);

        return sb;
    }

    /**
     * Print mzTab- elements in metadata to string. The structure like:
     * <ul>
     *     <li>MTD	mzTab-version	1.0</li>
     *     <li>MTD	mzTab-mode	Summary</li>
     *     <li>MTD	mzTab-type	Identification</li>
     *     <li>MTD	mzTab-ID	PRIDE_1234</li>
     * </ul>
     */
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
