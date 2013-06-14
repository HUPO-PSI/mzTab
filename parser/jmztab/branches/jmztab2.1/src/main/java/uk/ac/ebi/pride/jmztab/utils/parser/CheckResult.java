package uk.ac.ebi.pride.jmztab.utils.parser;

/**
 * User: Qingwei
 * Date: 05/06/13
 */
public enum CheckResult {
    ok,
    format_error,
    element_error,
    property_error,
    mztab_mode_error,

    indexed_element_format_error,
    indexed_element_list_format_error,


    not_found_in_metadata_error,
    colunit_column_error,                 // can not found this column in the factory.
    colunit_abundance_error,              // column is a quantification column.

    param_format_error,
    paramList_format_error,
    publication_format_error,
    uri_format_error,
    url_format_error,
}
