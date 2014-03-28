package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jmztab.model.MZTabColumn;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.model.MetadataElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: qingwei
 * Date: 29/11/13
 */
public class DynamicMZTabFile implements PropertyChangeListener {
    private DynamicMetadata dynamicMetadata;
    private MZTabFile tabFile;

    public DynamicMZTabFile(MZTabFile tabFile) {
        dynamicMetadata = new DynamicMetadata(tabFile.getMetadata());
        dynamicMetadata.addPropertyChangeListener(DynamicMetadata.CHANGE_MS_RUN_INDEX, this);

        this.tabFile = tabFile;
    }

    public DynamicMetadata getDynamicMetadata() {
        return dynamicMetadata;
    }

    public MZTabFile getTabFile() {
        return tabFile;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DynamicMetadata.CHANGE_MS_RUN_INDEX)) {
            Integer oldId = (Integer) evt.getOldValue();
            Integer newId = (Integer) evt.getNewValue();

            modifyMZTabColumnFactory(MetadataElement.MS_RUN, oldId, newId);
        }
    }

    /**
     * Modify columns header, set ms_run[oldId] to ms_run[newId].
     *
     * Notice: In DynamicMZTabFile, modify ms_run id, not mention data move. That means, the logical position
     * can not calculate by using order + id.
     */
    private void modifyMZTabColumnFactory(MetadataElement element, Integer oldId, Integer newId) {
        modifyMZTabColumnFactory(tabFile.getProteinColumnFactory(), element, oldId, newId);
        modifyMZTabColumnFactory(tabFile.getPeptideColumnFactory(), element, oldId, newId);
        modifyMZTabColumnFactory(tabFile.getPsmColumnFactory(), element, oldId, newId);
        modifyMZTabColumnFactory(tabFile.getSmallMoleculeColumnFactory(), element, oldId, newId);
    }

    private void modifyMZTabColumnFactory(MZTabColumnFactory columnFactory, MetadataElement element, Integer oldId, Integer newId) {
        if (columnFactory != null) {
            for (MZTabColumn column : columnFactory.getOptionalColumnMapping().values()) {
                column.setLogicPosition(column.getOrder() + newId);
                column.setHeader(modifyId(column.getHeader(), element.getName(), oldId, newId));
            }
        }
    }

    private String modifyId(String context, String keyword, Integer oldId, Integer newId) {
        Pattern pattern = Pattern.compile("(.+" + keyword + "\\[)" + oldId + "(\\].*)");
        Matcher matcher = pattern.matcher(context);

        if (matcher.find()) {
            return matcher.group(1) + newId + matcher.group(2);
        } else {
            return context;
        }
    }
}
