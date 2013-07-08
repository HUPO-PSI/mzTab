package uk.ac.ebi.pride.jmztab.model;

import java.net.URL;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.MS_RUN_FORMAT;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.MS_RUN_ID_FORMAT;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.MS_RUN_LOCATION;

/**
 * The external MS data file.
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class MsRun extends IndexedElement {
    /**
     * A parameter specifying the data format of the external MS data file.
     */
    private Param format;

    /**
     * Parameter specifying the id format used in the external data file.
     */
    private Param idFormat;

    /**
     * Location of the external data file.
     */
    private URL location;

    public MsRun(int id) {
        super(MetadataElement.MS_RUN, id);
    }

    public Param getFormat() {
        return format;
    }

    public void setFormat(Param format) {
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
     * ms_file[1-n]-[format|location|id_format]   {CVParam|URL}
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

        return sb.toString();
    }
}
