package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * Define the controlled vocabularies/ontologies used in the mzTab file.
 *
 * @author qingwei
 * @since 14/10/13
 */
public class CV extends IndexedElement {
    private String label;
    private String fullName;
    private String version;
    private String url;

    /**
     * Define the controlled vocabularies/ontologies used in the mzTab file.
     * @param id non-positive integer number.
     */
    public CV(int id) {
        super(MetadataElement.CV, id);
    }

    /**
     * A string describing the labels of the controlled vocabularies/ontologies used in the mzTab file
     */
    public String getLabel() {
        return label;
    }

    /**
     * A string describing the labels of the controlled vocabularies/ontologies used in the mzTab file
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * A string describing the full names of the controlled vocabularies/ontologies used in the mzTab file
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * A string describing the full names of the controlled vocabularies/ontologies used in the mzTab file
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * A string describing the version of the controlled vocabularies/ontologies used in the mzTab file
     */
    public String getVersion() {
        return version;
    }

    /**
     * A string describing the version of the controlled vocabularies/ontologies used in the mzTab file
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * A string containing the URLs of the controlled vocabularies/ontologies used in the mzTab file
     */
    public String getUrl() {
        return url;
    }

    /**
     * A string containing the URLs of the controlled vocabularies/ontologies used in the mzTab file
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Print CV into a String:
     * <ul>
     *     <li>MTD	cv[1]-label	MS</li>
     *     <li>MTD	cv[1]-full_name	MS</li>
     *     <li>MTD	cv[1]-version	3.54.0</li>
     *     <li>MTD	cv[1]-url	http://psidev.cvs.sourceforge.net/viewvc/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo</li>
     * </ul>
     */
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
