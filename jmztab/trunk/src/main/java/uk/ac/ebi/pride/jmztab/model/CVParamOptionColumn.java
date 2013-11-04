package uk.ac.ebi.pride.jmztab.model;

/**
 * CV parameter accessions MAY be used for optional columns following the format:
 * opt_cv_{param accession}_{parameter name}. Spaces within the parameter’s name MUST be replaced by ‘_’.
 *
 * User: Qingwei
 * Date: 30/05/13
 */
public class CVParamOptionColumn extends OptionColumn {
    public static final String CV = "cv_";

    private CVParam param;

    public CVParamOptionColumn(IndexedElement element, CVParam param, Class columnType, int offset) {
        super(element, CV + param.getAccession() + "_" + param.getName().replaceAll(" ", "_"), columnType, offset);
        this.param = param;
    }

    public static String getHeader(IndexedElement element, CVParam param) {
        StringBuilder sb = new StringBuilder();

        sb.append(OPT).append("_").append(element == null ? GLOBAL : element.getReference());
        sb.append("_").append(CV).append(param.getAccession()).append("_").append(param.getName().replaceAll(" ", "_"));

        return sb.toString();
    }

    public CVParam getParam() {
        return param;
    }

}
