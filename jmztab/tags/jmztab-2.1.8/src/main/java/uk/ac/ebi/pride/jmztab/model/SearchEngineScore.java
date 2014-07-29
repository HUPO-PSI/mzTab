package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;

public class SearchEngineScore extends IndexedElement {
    private Param param;

    /**
     * Create a index-organized {@link uk.ac.ebi.pride.jmztab.model.MetadataElement}, index value is non-negative integer.
     *
     * @param id      SHOULD be non-negative integer.
     */
    public SearchEngineScore(int id) {
        super(MetadataElement.SEARCH_ENGINE_SCORE, id);
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
