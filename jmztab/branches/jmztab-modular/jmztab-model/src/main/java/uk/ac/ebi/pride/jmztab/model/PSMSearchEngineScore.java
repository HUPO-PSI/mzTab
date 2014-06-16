package uk.ac.ebi.pride.jmztab.model;

/**
 * User: ntoro
 * Date: 16/06/2014
 * Time: 18:01
 */
public class PSMSearchEngineScore extends SearchEngineScore {
    /**
     * Create a index-organized {@link uk.ac.ebi.pride.jmztab.model.MetadataElement}, index value is non-negative integer.
     *
     * @param id      SHOULD be non-negative integer.
     */
    public PSMSearchEngineScore(int id) {
        super(MetadataElement.PSM_SEARCH_ENGINE_SCORE, id);
    }
}
