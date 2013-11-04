package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;

/**
 * User: qingwei
 * Date: 14/10/13
 */
public abstract class Mod extends IndexedElement {
    private Param param;
    private String site;
    private String position;

    public Mod(MetadataElement element, int id) {
        super(element, id);
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (param != null) {
            sb.append(printElement(param)).append(NEW_LINE);
        }

        if (! isEmpty(site)) {
            sb.append(printProperty(MetadataProperty.findProperty(getElement(), "site"), site)).append(NEW_LINE);
        }

        if (! isEmpty(position)) {
            sb.append(printProperty(MetadataProperty.findProperty(getElement(), "position"), position)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
