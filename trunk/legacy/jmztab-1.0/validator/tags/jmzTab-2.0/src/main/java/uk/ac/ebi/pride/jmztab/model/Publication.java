package uk.ac.ebi.pride.jmztab.model;

import java.util.Collection;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.BAR;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * A publication on this unit. PubMed ids must be prefixed by “pubmed:”, DOIs by “doi:”.
 *
 * User: Qingwei
 * Date: 01/02/13
 */
public class Publication extends UnitElement {
    public Publication(int id, Unit unit) {
        super(id, unit);
    }

    private SplitList<PublicationItem> itemList = new SplitList<PublicationItem>(BAR);

    public void addPublicationItem(PublicationItem item) {
        itemList.add(item);
    }

    public void addPublicationItems(Collection<PublicationItem> items) {
        itemList.addAll(items);
    }

    public int size() {
        return itemList.size();
    }

    public void clear() {
        itemList.clear();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(printElement(MetadataElement.PUBLICATION, itemList)).append(NEW_LINE);

        return sb.toString();
    }
}
