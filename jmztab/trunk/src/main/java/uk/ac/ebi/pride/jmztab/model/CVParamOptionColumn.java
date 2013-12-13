package uk.ac.ebi.pride.jmztab.model;

/**
 * An kind of {@link OptionColumn} which use CV parameter accessions in following the format:
 * opt_{OBJECT_ID}_cv_{accession}_{parameter name}. Spaces within the parameter' s name MUST be replaced by '_'.
 *
 * User: Qingwei
 * Date: 30/05/13
 */
public class CVParamOptionColumn extends OptionColumn {
    public static final String CV = "cv_";

    private CVParam param;

    /**
     * Define a {@link OptionColumn} which use CV parameter accessions in following the format:
     * opt_{OBJECT_ID}_cv_{accession}_{parameter name}. Spaces within the parameter' s name MUST be replaced by '_'.
     *
     * @param element SHOULD not be null.
     * @param param SHOULD not be null.
     * @param columnType SHOULD not be null.
     * @param offset SHOULD be non-negative integer.
     */
    public CVParamOptionColumn(IndexedElement element, CVParam param, Class columnType, int offset) {
        super(element, CV + param.getAccession() + "_" + param.getName().replaceAll(" ", "_"), columnType, offset);
        this.param = param;
    }

    /**
     * get column header like: opt_{OBJECT_ID}_cv_{accession}_{parameter name}
     * Spaces within the parameter's name MUST be replaced by '_'.
     *
     * @param element {@link Assay}, {@link StudyVariable}, {@link MsRun} or "global" (if the value relates to all replicates).
     * @param param SHOULD NOT be null.
     */
    public static String getHeader(IndexedElement element, CVParam param) {
        StringBuilder sb = new StringBuilder();

        sb.append(OPT).append("_").append(element == null ? GLOBAL : element.getReference());
        sb.append("_").append(CV).append(param.getAccession()).append("_").append(param.getName().replaceAll(" ", "_"));

        return sb.toString();
    }

}
