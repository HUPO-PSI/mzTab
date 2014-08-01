package uk.ac.ebi.pride.jmztab.utils.errors;

/**
 * Provide crosscheck service, that is parse the consistent between current mztab file and
 * some other resource (eg, database, xml file and so on).
 *
 * Not implement yet.
 *
 * @author qingwei
 * @since 29/01/13
 */
public class CrossCheckErrorType extends MZTabErrorType {
    public static MZTabErrorType Species = createWarn(Category.CrossCheck, "Species");


}
