package uk.ac.ebi.pride.jmztab.model;

import java.net.URL;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * An MS run is effectively one run (or set of runs on pre-fractionated samples) on an MS instrument,
 * and is referenced from assay in different contexts.
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class MsRun extends IndexedElement {
    private Param format;
    private Param idFormat;
    private URL location;
    private Param fragmentationMethod;
    private String hash;
    private Param hashMethod;

    public MsRun(int id) {
        super(MetadataElement.MS_RUN, id);
    }

    /**
     * Get a parameter specifying the data format of the external MS data file.
     */
    public Param getFormat() {
        return format;
    }

    /**
     * Set a parameter specifying the data format of the external MS data file.
     */
    public void setFormat(Param format) {
        this.format = format;
    }

    /**
     * Get parameter specifying the id format used in the external data file.
     */
    public Param getIdFormat() {
        return idFormat;
    }

    /**
     * Set parameter specifying the id format used in the external data file.
     */
    public void setIdFormat(Param idFormat) {
        this.idFormat = idFormat;
    }

    /**
     * Get location of the external data file.
     */
    public URL getLocation() {
        return location;
    }

    /**
     * Set location of the external data file.
     */
    public void setLocation(URL location) {
        this.location = location;
    }

    /**
     * Get a parameter describing the types of fragmentation used in a given ms run.
     */
    public Param getFragmentationMethod() {
        return fragmentationMethod;
    }

    /**
     * Set a parameter describing the types of fragmentation used in a given ms run.
     */
    public void setFragmentationMethod(Param fragmentationMethod) {
        this.fragmentationMethod = fragmentationMethod;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Param getHashMethod() {
        return hashMethod;
    }

    public void setHashMethod(Param hashMethod) {
        this.hashMethod = hashMethod;
    }

    /**
     * Print a MsRun to a String. The format like:
     * <ul>
     *     <li>MTD	ms_run[1]-format	[MS, MS:1000584, mzML file, ]</li>
     *     <li>MTD	ms_run[1]-location	file://C:/path/to/my/file</li>
     *     <li>MTD	ms_run[1]-id_format	[MS, MS:1001530, mzML unique identifier, ]</li>
     *     <li>MTD	ms_run[1]-fragmentation_method	[MS, MS:1000133, CID, ]</li>
     * </ul>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (format != null) {
            sb.append(printProperty(MS_RUN_FORMAT, format)).append(NEW_LINE);
        }
        if (location != null) {
            sb.append(printProperty(MS_RUN_LOCATION, location)).append(NEW_LINE);
        }
        if (idFormat != null) {
            sb.append(printProperty(MS_RUN_ID_FORMAT, idFormat)).append(NEW_LINE);
        }
        if (fragmentationMethod != null) {
            sb.append(printProperty(MS_RUN_FRAGMENTATION_METHOD, fragmentationMethod)).append(NEW_LINE);
        }
        if (hash != null) {
            sb.append(printProperty(MS_RUN_HASH, hash)).append(NEW_LINE);
        }
        if (hashMethod != null) {
            sb.append(printProperty(MS_RUN_HASH_METHOD, hashMethod)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
