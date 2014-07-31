package uk.ac.ebi.pride.jmztab.model;

/**
 * PSM search engine score cv param used to identify the type of scoring system used in search_engine_score in the
 * PSM section.
 *
 * @author ntoro
 * @since 16/06/2014 18:01
 */
public class PSMSearchEngineScore extends SearchEngineScore {
    /**
     * Create a index-organized {@link MetadataElement}, index value is non-negative integer.
     *
     * @param id      SHOULD be non-negative integer.
     */
    public PSMSearchEngineScore(int id) {
        super(MetadataElement.PSM_SEARCH_ENGINE_SCORE, id);
    }
}
