package uk.ac.ebi.pride.jmztab.model;

import java.util.Collection;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.BAR;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * A publication on this unit. PubMed ids must be prefixed by "pubmed:", DOIs by "doi:".
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class Publication extends IndexedElement {
    private SplitList<PublicationItem> itemList = new SplitList<PublicationItem>(BAR);

    public Publication(int id) {
        super(MetadataElement.PUBLICATION, id);
    }

    public void addPublicationItem(PublicationItem item) {
        itemList.add(item);
    }

    public void addPublicationItems(Collection<PublicationItem> items) {
        itemList.addAll(items);
    }

    public int size() {
        return itemList.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(printElement(itemList)).append(NEW_LINE);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publication that = (Publication) o;

        if (itemList != null ? !itemList.equals(that.itemList) : that.itemList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return itemList != null ? itemList.hashCode() : 0;
    }
}
