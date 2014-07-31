package uk.ac.ebi.pride.jmztab.model;

/**
 * Protein search engine score cv param used to identify the type of scoring system used in the values of columns like
 * best_search_engine_score and/or search_engine_score in the protein section.
 *
 * @author ntoro
 * @since 16/06/2014 17:50
 */
public class ProteinSearchEngineScore extends SearchEngineScore {
    /**
     * Create a index-organized {@link MetadataElement}, index value is non-negative integer.
     *
     * @param id      SHOULD be non-negative integer.
     */
    public ProteinSearchEngineScore(int id) {
        super(MetadataElement.PROTEIN_SEARCH_ENGINE_SCORE, id);
    }
}
