package uk.ac.ebi.pride.jmztab.model;

/**
 * Small molecule search engine score cv param used to identify the type of scoring system used in the values of columns like
 * best_search_engine_score and/or search_engine_score in the small molecule section.
 *
 * @author ntoro
 * @since 16/06/2014 18:04
 */
public class SmallMoleculeSearchEngineScore extends SearchEngineScore {
    /**
     * Create a index-organized {@link uk.ac.ebi.pride.jmztab.model.MetadataElement}, index value is non-negative integer.
     *
     * @param id      SHOULD be non-negative integer.
     */
    public SmallMoleculeSearchEngineScore(int id) {
        super(MetadataElement.SMALLMOLECULE_SEARCH_ENGINE_SCORE, id);
    }
}
