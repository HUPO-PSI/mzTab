package uk.ac.ebi.pride.jmztab.model;

/**
 * User: ntoro
 * Date: 16/06/2014
 * Time: 17:50
 */
public class ProteinSearchEngineScore extends SearchEngineScore {
    /**
     * Create a index-organized {@link uk.ac.ebi.pride.jmztab.model.MetadataElement}, index value is non-negative integer.
     *
     * @param id      SHOULD be non-negative integer.
     */
    public ProteinSearchEngineScore(int id) {
        super(MetadataElement.PROTEIN_SEARCH_ENGINE_SCORE, id);
    }
}
