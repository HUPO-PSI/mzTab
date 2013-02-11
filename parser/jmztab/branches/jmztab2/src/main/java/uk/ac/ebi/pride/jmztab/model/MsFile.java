package uk.ac.ebi.pride.jmztab.model;

import java.net.URL;

import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;
import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.*;

/**
 * The external MS data file.
 *
 * User: Qingwei
 * Date: 30/01/13
 */
public class MsFile extends UnitElement {
    /**
     * A parameter specifying the data format of the external MS data file.
     */
    private CVParam format;

    /**
     * Parameter specifying the id format used in the external data file.
     */
    private Param idFormat;

    /**
     * Location of the external data file.
     */
    private URL location;

    public MsFile(int id, Unit unit) {
        super(id, unit);
    }

    public CVParam getFormat() {
        return format;
    }

    public void setFormat(CVParam format) {
        this.format = format;
    }

    public Param getIdFormat() {
        return idFormat;
    }

    public void setIdFormat(Param idFormat) {
        this.idFormat = idFormat;
    }

    public URL getLocation() {
        return location;
    }

    public void setLocation(URL location) {
        this.location = location;
    }

    /**
     * ms_file[id]
     */
    public String getReference() {
        StringBuilder sb = new StringBuilder();

        sb.append(MS_FILE).append("[").append(getId()).append("]");

        return sb.toString();
    }

    /**
     * {UNIT_ID}-ms_file[1-n]-[format|location|id_format]   {CVParam|URL}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (format != null) {
            sb.append(printProperty(MS_FILE_FORMAT, format)).append(NEW_LINE);
        }
        if (location != null) {
            sb.append(printProperty(MS_FILE_LOCATION, location)).append(NEW_LINE);
        }
        if (idFormat != null) {
            sb.append(printProperty(MS_FILE_ID_FORMAT, idFormat)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
