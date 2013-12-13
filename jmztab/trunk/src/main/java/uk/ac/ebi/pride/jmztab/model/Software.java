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

    /**
     * Create a software in metadata.
     * @param id SHOULD be positive integer.
     */
    public Software(int id) {
        super(SOFTWARE, id);
    }

    /**
     * A software setting used. This field MAY occur multiple times for a single software.
     * The value of this field is deliberately set as a String, since there currently
     * do not exist cvParams for every possible setting
     */
    private List<String> settingList = new ArrayList<String>();

    /**
     * Software used to analyze the data and obtain the reported results. The parameter’s value
     * SHOULD contain the software’s version. The order (numbering) should reflect the order in which the tools were used.
     */
    public Param getParam() {
        return param;
    }

    /**
     * Software used to analyze the data and obtain the reported results. The parameter’s value
     * SHOULD contain the software’s version. The order (numbering) should reflect the order in which the tools were used.
     */
    public void setParam(Param param) {
        this.param = param;
    }

    /**
     * A software setting used. This field MAY occur multiple times for a single software. The value of this field is
     * deliberately set as a String, since there currently do not exist cvParams for every possible setting.
     */
    public List<String> getSettingList() {
        return Collections.unmodifiableList(settingList);
    }

    /**
     * A software setting used. This field MAY occur multiple times for a single software. The value of this field is
     * deliberately set as a String, since there currently do not exist cvParams for every possible setting.
     */
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

        printList(settingList, MetadataProperty.SOFTWARE_SETTING, sb);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Software software = (Software) o;

        if (!param.equals(software.param)) return false;
        if (settingList != null ? !settingList.equals(software.settingList) : software.settingList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = param.hashCode();
        result = 31 * result + (settingList != null ? settingList.hashCode() : 0);
        return result;
    }
}
