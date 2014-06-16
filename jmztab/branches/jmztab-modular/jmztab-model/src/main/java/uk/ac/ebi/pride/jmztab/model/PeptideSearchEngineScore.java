package uk.ac.ebi.pride.jmztab.model;

/**
 * User: ntoro
 * Date: 16/06/2014
 * Time: 18:04
 */
public class PeptideSearchEngineScore extends SearchEngineScore {
    /**
     * Create a index-organized {@link uk.ac.ebi.pride.jmztab.model.MetadataElement}, index value is non-negative integer.
     *
     * @param id      SHOULD be non-negative integer.
     */
    public PeptideSearchEngineScore(int id) {
        super(MetadataElement.PEPTIDE_SEARCH_ENGINE_SCORE, id);
    }
}
