package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

/**
 * Search engine score cv param used to identify the type of scoring system used in the values of columns like
 * best_search_engine_score and/or search_engine_score in the different sections.
 *
 * @author qingwei
 * @since 20/3/14
 */
public abstract class SearchEngineScore extends IndexedElement {
    private Param param;

    /**
     * Create a index-organized {@link MetadataElement}, index value is non-negative integer.
     *
     * @param id      SHOULD be non-negative integer.
     */
    public SearchEngineScore(MetadataElement element, int id) {
        super(element, id);
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (param != null) {
            sb.append(printElement(param)).append(NEW_LINE);
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchEngineScore)) return false;

        SearchEngineScore that = (SearchEngineScore) o;

        if (param != null ? !param.equals(that.param) : that.param != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return param != null ? param.hashCode() : 0;
    }
}
