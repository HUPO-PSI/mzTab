package uk.ac.ebi.pride.jmztab.model;

import java.util.Collection;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.BAR;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * A publication associated with this file. Several publications can be given by indicating the number
 * in the square brackets after “publication”. PubMed ids must be prefixed by “pubmed:”, DOIs by “doi:”.
 * Multiple identifiers MUST be separated by “|”.
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class Publication extends IndexedElement {
    private SplitList<PublicationItem> itemList = new SplitList<PublicationItem>(BAR);

    /**
     * Create a publication object.
     * @param id SHOULD be positive integer.
     */
    public Publication(int id) {
        super(MetadataElement.PUBLICATION, id);
    }

    /**
     * Add a {@link PublicationItem} into publication.
     *
     * @param item SHOULD NOT set null.
     */
    public void addPublicationItem(PublicationItem item) {
        if (item == null) {
            throw new NullPointerException("Can not add a null into publication.");
        }

        itemList.add(item);
    }

    /**
     * Add a couple of {@link PublicationItem} into publication.
     *
     * @param items SHOULD NOT set null.
     */
    public void addPublicationItems(Collection<PublicationItem> items) {
        itemList.addAll(items);
    }

    /**
     * @return publication item count.
     */
    public int size() {
        return itemList.size();
    }

    /**
     * Print publication into string.
     */
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
