package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * User: qingwei
 * Date: 14/10/13
 */
public class CV extends IndexedElement {
    private String label;
    private String fullName;
    private String version;
    private String url;

    public CV(int id) {
        super(MetadataElement.CV, id);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (! isEmpty(label)) {
            sb.append(printProperty(MetadataProperty.CV_LABEL, label)).append(NEW_LINE);
        }
        if (! isEmpty(fullName)) {
            sb.append(printProperty(MetadataProperty.CV_FULL_NAME, fullName)).append(NEW_LINE);
        }
        if (! isEmpty(version)) {
            sb.append(printProperty(MetadataProperty.CV_VERSION, version)).append(NEW_LINE);
        }
        if (! isEmpty(url)) {
            sb.append(printProperty(MetadataProperty.CV_URL, url)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
