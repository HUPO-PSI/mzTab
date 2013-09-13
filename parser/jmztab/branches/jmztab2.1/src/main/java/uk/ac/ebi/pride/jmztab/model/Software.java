package uk.ac.ebi.pride.jmztab.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataElement.SOFTWARE;

/**
 * SoftwareVersion used to analyze the data and obtain the results reported.
 * The parameter’s value SHOULD contain the software’s version.
 * The order (numbering) should reflect the order in which the tools were used.
 *
 * A software setting used. This field MAY occur multiple times for a single software.
 * The value of this field is deliberately set as a String, since there currently
 * do not exist cvParams for every possible setting
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class Software extends IndexedElement {
    private Param param;

    public Software(int id) {
        super(SOFTWARE, id);
    }

    /**
     * A software setting used. This field MAY occur multiple times for a single software.
     * The value of this field is deliberately set as a String, since there currently
     * do not exist cvParams for every possible setting
     */
    private List<String> settingList = new ArrayList<String>();

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public List<String> getSettingList() {
        return Collections.unmodifiableList(settingList);
    }

    public void addSetting(String setting) {
        settingList.add(setting);
    }

    /**
     * MTD  software[1]  [MS, MS:1001207, Mascot, 2.3]
     * MTD  software[1]-setting  Fragment tolerance = 0.1 Da
     * MTD  software[1]-setting  Parent tolerance = 0.5 Da
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (param != null) {
            sb.append(printElement(param)).append(NEW_LINE);
        }

        for (String setting : settingList) {
            sb.append(printProperty(MetadataProperty.SOFTWARE_SETTING, setting)).append(NEW_LINE);
        }

        return sb.toString();
    }
}
