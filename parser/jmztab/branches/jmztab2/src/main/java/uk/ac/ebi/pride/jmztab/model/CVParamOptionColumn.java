package uk.ac.ebi.pride.jmztab.model;

/**
 * CV parameter accessions MAY be used for optional columns following the format:
 * opt_cv_{accession}_{parameter name}. Spaces within the parameter’s name MUST be replaced by ‘_’.
 *
 * User: Qingwei
 * Date: 31/01/13
 */
public class CVParamOptionColumn extends OptionalColumn {
    private CVParam param;

    private static String getName(CVParam param) {
        if (param == null) {
            throw new NullPointerException("CVParam can not set null!");
        }

        StringBuilder sb = new StringBuilder();

        sb.append("cv");
        if (param.getAccession() != null) {
            sb.append("_").append(param.getAccession());
        }
        sb.append("_").append(param.getName().replaceAll(" ", "_"));

        return sb.toString();
    }

    /**
     * create optional opt_cv_{accession}_{parameter name} column at the end of table.
     * When user add value for this column, system will execute value type match operation first.
     *
     * @param param can not set null.
     * @param columnType is the value type which allow user to set for this optional column. Can not set null.
     * @param offset is the position of the rightest column of table. Can not set null.
     *
     * @see AbstractMZTabRecord#isMatch(int, Class)
     */
    protected CVParamOptionColumn(CVParam param, Class columnType, int offset) {
        super(getName(param), columnType, offset);
        this.param = param;
    }

    /**
     * create optional opt_cv_{accession}_{parameter name} column at the end of table.
     *
     * To report the results of a target-decoy search, decoy identifications MAY be labeled
     * using the optional column “opt_cv_MS: 1002217 _decoy_peptide”. The value of this column
     * MUST be a Boolean (1/0).
     *
     * @param param can not set null.
     * @param offset is the position of the rightest column of table. Can not set null.
     *
     * @see AbstractMZTabRecord#isMatch(int, Class)
     */
    public static CVParamOptionColumn getInstance(CVParam param, int offset) {
        return new CVParamOptionColumn(param, MZBoolean.class, offset);
    }

    public Param getParam() {
        return param;
    }
}
