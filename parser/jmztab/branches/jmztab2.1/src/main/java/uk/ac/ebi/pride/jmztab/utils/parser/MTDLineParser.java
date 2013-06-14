package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
* Metadata Element start with MTD, and structure like:
* MTD  {MetadataElement}[id]-{MetadataProperty}[pid]
*
* @see MetadataElement
* @see MetadataProperty
*
* User: Qingwei
* Date: 08/02/13
*/
public class MTDLineParser extends MZTabLineParser {
    private Metadata metadata = new Metadata();

    private Map<String, String> colUnitMap = new HashMap<String, String>();

    /**
     * For facing colunit definition line, for example:
     * MTD  colunit-protein retention_time=[UO, UO:000031, minute, ]
     * after parse metadata and header lines, need calling
     * {@link #refineColUnit(uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory)} manually.
     */
    public void parse(int lineNumber, String mtdLine) throws MZTabException {
        super.parse(lineNumber, mtdLine);

        if (items.length != 3) {
            MZTabError error = new MZTabError(FormatErrorType.MTDLine, lineNumber, mtdLine);
            throw new MZTabException(error);
        }

        if (items[1].contains("colunit")) {
            // ignore colunit parse. In the stage, just store them into colUnitMap<defineLabel, valueLabel>.
            // after table section columns created, call checkColUnit manually.
            colUnitMap.put(items[1], items[2]);
        } else {
            parseNormalMetadata(items[1], items[2]);
        }
    }

    private String checkEmail(String defineLabel, String valueLabel) {
        String regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(valueLabel);

        if (! matcher.find()) {
            new MZTabError(FormatErrorType.Email, lineNumber, defineLabel, valueLabel);
        }

        return valueLabel;
    }

    /**
     * {MetadataElement}[id]-{MetadataProperty}[pid]
     */
    private CheckResult checkNormalMetadata(String defineLabel, String valueLabel) {
        String regexp = "(\\w+)(\\[(\\d)\\])?(-(\\w+)(\\[(\\d)\\])?)?";

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(defineLabel);

        if (matcher.find()) {
            // Stage 1: create Unit.
            MetadataElement element = MetadataElement.findElement(matcher.group(1));

            Integer id;
            MetadataProperty property;
            Integer pid;
            Param param;
            SplitList<Param> paramList;
            IndexedElement indexedElement;
            List<IndexedElement> indexedElementList;
            switch (element) {
                case MZTAB:
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }
                    switch (property) {
                        case MZTAB_VERSION:
                            metadata.setMZTabVersion(valueLabel);
                            break;
                        case MZTAB_ID:
                            metadata.setMZTabID(valueLabel);
                            break;
                        case MZTAB_MODE:
                            MZTabDescription.Mode mode = MZTabDescription.Mode.valueOf(valueLabel);
                            if (mode == null) {
                                return CheckResult.mztab_mode_error;
                            }
                            metadata.setMZTabMode(mode);
                            break;
                        default:
                            return CheckResult.property_error;
                    }

                    break;
                case TITLE:
                    metadata.setTitle(valueLabel);
                    break;
                case DESCRIPTION:
                    metadata.setDescription(valueLabel);
                    break;
                case SAMPLE_PROCESSING:
                    id = new Integer(matcher.group(3));
                    metadata.addSampleProcessing(id, parseParamList(valueLabel));
                    break;
                case INSTRUMENT:
                    id = new Integer(matcher.group(3));
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }
                    param = parseParam(valueLabel);
                    if (param == null) {
                        return CheckResult.param_format_error;
                    }

                    switch (property) {
                        case INSTRUMENT_NAME:
                            metadata.addInstrumentName(id, param);
                            break;
                        case INSTRUMENT_SOURCE:
                            metadata.addInstrumentSource(id, param);
                            break;
                        case INSTRUMENT_ANALYZER:
                            metadata.addInstrumentAnalyzer(id, param);
                            break;
                        case INSTRUMENT_DETECTOR:
                            metadata.addInstrumentDetector(id, param);
                            break;
                        default:
                            return CheckResult.property_error;
                    }

                    break;
                case SOFTWARE:
                    id = new Integer(matcher.group(3));
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        param = parseParam(valueLabel);
                        if (param == null) {
                            return CheckResult.param_format_error;
                        }
                        metadata.addSoftwareParam(id, param);
                    } else {
                        switch (property) {
                            case SOFTWARE_SETTING:
                                metadata.addSoftwareSetting(id, valueLabel);
                                break;
                            default:
                                return CheckResult.property_error;
                        }
                    }

                    break;
                case FALSE_DISCOVERY_RATE:
                    paramList = parseParamList(valueLabel);
                    if (paramList.size() == 0) {
                        return CheckResult.paramList_format_error;
                    }
                    metadata.setFalseDiscoveryRate(paramList);
                    break;
                case PUBLICATION:
                    id = new Integer(matcher.group(3));
                    SplitList<PublicationItem> publications = parsePublicationItems(valueLabel);
                    if (publications.size() == 0) {
                        return CheckResult.publication_format_error;
                    }
                    metadata.addPublicationItems(id, publications);
                    break;
                case CONTACT:
                    id = new Integer(matcher.group(3));
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }

                    switch (property) {
                        case CONTACT_NAME:
                            metadata.addContactName(id, valueLabel);
                            break;
                        case CONTACT_AFFILIATION:
                            metadata.addContactAffiliation(id, valueLabel);
                            break;
                        case CONTACT_EMAIL:
                            String email = checkEmail(defineLabel, valueLabel);
                            metadata.addContactEmail(id, email);
                            break;
                        default:
                            return CheckResult.property_error;
                    }
                    break;
                case URI:
                    java.net.URI uri = parseURI(valueLabel);
                    if (uri == null) {
                        return CheckResult.uri_format_error;
                    }
                    metadata.addUri(uri);
                    break;
                case MOD:
                    paramList = parseParamList(valueLabel);
                    if (paramList.size() == 0) {
                        return CheckResult.paramList_format_error;
                    }
                    metadata.setMod(paramList);
                    break;
                case QUANTIFICATION_METHOD:
                    param = parseParam(valueLabel);
                    if (param == null) {
                        return CheckResult.param_format_error;
                    }
                    metadata.setQuantificationMethod(param);
                    break;
                case PROTEIN:
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }
                    param = parseParam(valueLabel);
                    if (param == null) {
                        return CheckResult.param_format_error;
                    }
                    switch (property) {
                        case PROTEIN_QUANTIFICATION_UNIT:
                            metadata.setProteinQuantificationUnit(param);
                            break;
                        default:
                            return CheckResult.property_error;
                    }
                    break;
                case PEPTIDE:
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }
                    param = parseParam(valueLabel);
                    if (param == null) {
                        return CheckResult.param_format_error;
                    }
                    switch (property) {
                        case PEPTIDE_QUANTIFICATION_UNIT:
                            metadata.setPeptideQuantificationUnit(param);
                            break;
                        default:
                            return CheckResult.property_error;
                    }
                    break;
                case SMALL_MOLECULE:
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }
                    param = parseParam(valueLabel);
                    if (param == null) {
                        return CheckResult.param_format_error;
                    }
                    switch (property) {
                        case SMALL_MOLECULE_QUANTIFICATION_UNIT:
                            metadata.setSmallMoleculeQuantificationUnit(param);
                            break;
                        default:
                            return CheckResult.property_error;
                    }
                    break;
                case MS_FILE:
                    id = new Integer(matcher.group(3));
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }

                    switch (property) {
                        case MS_FILE_FORMAT:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return CheckResult.param_format_error;
                            }
                            metadata.addMsFileFormat(id, param);
                            break;
                        case MS_FILE_LOCATION:
                            java.net.URL url = parseURL(valueLabel);
                            if (url == null) {
                                return CheckResult.url_format_error;
                            }
                            metadata.addMsFileLocation(id, url);
                            break;
                        case MS_FILE_ID_FORMAT:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return CheckResult.param_format_error;
                            }
                            metadata.addMsFileIdFormat(id, param);
                            break;
                        default:
                            return CheckResult.property_error;
                    }

                    break;
                case CUSTOM:
                    param = parseParam(valueLabel);
                    if (param == null) {
                        return CheckResult.param_format_error;
                    }
                    metadata.addCustom(param);
                    break;
                case SAMPLE:
                    id = new Integer(matcher.group(3));
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }

                    switch (property) {
                        case SAMPLE_SPECIES:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return CheckResult.param_format_error;
                            }
                            pid = new Integer(matcher.group(7));
                            metadata.addSampleSpecies(id, pid, param);
                            break;
                        case SAMPLE_TISSUE:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return CheckResult.param_format_error;
                            }
                            pid = new Integer(matcher.group(7));
                            metadata.addSampleTissue(id, pid, param);
                            break;
                        case SAMPLE_CELL_TYPE:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return CheckResult.param_format_error;
                            }
                            pid = new Integer(matcher.group(7));
                            metadata.addSampleCellType(id, pid, param);
                            break;
                        case SAMPLE_DISEASE:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return CheckResult.param_format_error;
                            }
                            pid = new Integer(matcher.group(7));
                            metadata.addSampleDisease(id, pid, param);
                            break;
                        case SAMPLE_DESCRIPTION:
                            metadata.addSampleDescription(id, valueLabel);
                            break;
                        case SAMPLE_CUSTOM:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return CheckResult.param_format_error;
                            }
                            metadata.addSampleCustom(id, param);
                            break;
                        default:
                            return CheckResult.property_error;
                    }
                    break;
                case ASSAY:
                    id = new Integer(matcher.group(3));
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }
                    switch (property) {
                        case ASSAY_QUANTIFICATION_REAGENT:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return CheckResult.param_format_error;
                            }
                            metadata.addAssayQuantificationReagent(id, param);
                            break;
                        case ASSAY_SAMPLE_REF:
                            indexedElement = parseIndexedElement(valueLabel, MetadataElement.SAMPLE);
                            if (indexedElement == null) {
                                return CheckResult.indexed_element_format_error;
                            }
                            Sample sample = metadata.getSampleMap().get(indexedElement.getId());
                            if (sample == null) {
                                return CheckResult.not_found_in_metadata_error;
                            }
                            metadata.addAssaySample(id, sample);
                            break;
                        case ASSAY_MS_FILE_REF:
                            indexedElement = parseIndexedElement(valueLabel, MetadataElement.MS_FILE);
                            if (indexedElement == null) {
                                return CheckResult.indexed_element_format_error;
                            }
                            MsFile msFile = metadata.getMsFileMap().get(indexedElement.getId());
                            if (msFile == null) {
                                return CheckResult.not_found_in_metadata_error;
                            }
                            metadata.addAssayMsFile(id, msFile);
                            break;
                        default:
                            return CheckResult.property_error;
                    }

                    break;
                case STUDY_VARIABLE:
                    id = new Integer(matcher.group(3));
                    property = MetadataProperty.findProperty(element.getName(), matcher.group(5));
                    if (property == null) {
                        return CheckResult.property_error;
                    }
                    switch (property) {
                        case STUDY_VARIABLE_ASSAY_REFS:
                            indexedElementList = parseIndexedElementList(valueLabel, MetadataElement.ASSAY);
                            if (indexedElementList == null || indexedElementList.size() == 0) {
                                return CheckResult.indexed_element_list_format_error;
                            }
                            for (IndexedElement e : indexedElementList) {
                                if (! metadata.getAssayMap().containsKey(e.getId())) {
                                    // can not find assay[id] in metadata.
                                    return CheckResult.not_found_in_metadata_error;
                                }
                                metadata.addStudyVariableAssay(id, metadata.getAssayMap().get(e.getId()));
                            }
                            break;
                        case STUDY_VARIABLE_SAMPLE_REFS:
                            indexedElementList = parseIndexedElementList(valueLabel, MetadataElement.SAMPLE);
                            if (indexedElementList == null || indexedElementList.size() == 0) {
                                return CheckResult.indexed_element_list_format_error;
                            }
                            for (IndexedElement e : indexedElementList) {
                                if (! metadata.getSampleMap().containsKey(e.getId())) {
                                    // can not find assay[id] in metadata.
                                    return CheckResult.not_found_in_metadata_error;
                                }
                                metadata.addStudyVariableSample(id, metadata.getSampleMap().get(e.getId()));
                            }
                            break;
                        case STUDY_VARIABLE_DESCRIPTION:
                            metadata.addStudyVariableDescription(id, valueLabel);
                            break;
                        default:
                            return CheckResult.property_error;
                    }
                    break;
                default:
                    return CheckResult.element_error;
            }

            return CheckResult.ok;
        } else {
            return CheckResult.format_error;
        }
    }


    /**
     * The metadata line including three parts:
     * MTD  {define}    {value}
     *
     * In normal, define label structure like:
     * {unitID}(-SUB_ID|-REP_ID)-{element}([{id}])-{property}
     *
     * The (-SUB_ID|-REP_ID), ([{id}]) and {property} are optional.
     * parse label and generate Unit, MetadataElement, id, MetadataProperty objects.
     * If optional item not exists, return null.
     *
     * Notice 1:
     * In replicate unit, MetadataElement maybe null. For example: Exp_1-rep[1].
     */
    private void parseNormalMetadata(String defineLabel, String valueLabel) throws MZTabException {
        CheckResult result  = checkNormalMetadata(defineLabel, valueLabel);

//        MZTabError error = null;
//        switch (result) {
//            case unitId_format_error:
//                error = new MZTabError(FormatErrorType.UnitID, lineNumber, "", defineLabel);
//                break;
//            case id_number_error:
//                error = new MZTabError(LogicalErrorType.IdNumber, lineNumber, defineLabel);
//                break;
//            case format_error:
//                error = new MZTabError(FormatErrorType.MTDDefineLabel, lineNumber, defineLabel);
//                break;
//            case param_format_error:
//                error = new MZTabError(FormatErrorType.Param, lineNumber, defineLabel, valueLabel);
//                break;
//            case paramList_format_error:
//                error = new MZTabError(FormatErrorType.ParamList, lineNumber, defineLabel, valueLabel);
//                break;
//            case publication_format_error:
//                error = new MZTabError(FormatErrorType.Publication, lineNumber, defineLabel, valueLabel);
//                break;
//            case uri_format_error:
//                error = new MZTabError(FormatErrorType.URI, lineNumber, defineLabel, valueLabel);
//                break;
//            case url_format_error:
//                error = new MZTabError(FormatErrorType.URL, lineNumber, defineLabel, valueLabel);
//                break;
//            case duplicate_error:
//                error = new MZTabError(LogicalErrorType.Duplication, lineNumber, defineLabel + TAB + valueLabel);
//                break;
//        }
//
//        if (error != null) {
//            throw new MZTabException(error);
//        }
    }

    /**
     * Based on factory, navigate colUnitMap<defineLabel, valueLabel>
     * and refine colunit columns are correct or not.
     *
     * Notice: after refined phase, colunit definition can be record in the metadata.
     */
    public void refineColUnit(MZTabColumnFactory factory) throws MZTabException {
        String valueLabel;
        CheckResult result;
        for (String defineLabel : colUnitMap.keySet()) {
            if (defineLabel.equals("colunit-" + Section.toDataSection(factory.getSection()).getName())) {
                valueLabel = colUnitMap.get(defineLabel);
                result = checkColUnit(valueLabel, factory);

                switch (result) {
                    case colunit_column_error:
                        break;
                    case colunit_abundance_error:
                        break;
                    case param_format_error:
                        break;
                }
            }
        }
    }

    private CheckResult checkColUnit(String valueLabel, MZTabColumnFactory factory) {
        String[] items = valueLabel.split("=");
        String columnName = items[0].trim();
        String value = items[1].trim();

        MZTabColumn column = factory.findColumn(columnName);
        if (column == null) {
            // column_name not exists in the factory.
            return CheckResult.colunit_column_error;
        } else if (column instanceof AbundanceColumn) {
            return CheckResult.colunit_abundance_error;
        } else {
            Param param = parseParam(value);
            if (param == null) {
                return CheckResult.param_format_error;
            }

            switch (factory.getSection()) {
                case Protein_Header:
                    metadata.addProteinColUnit(column, param);
                    break;
                case Peptide_Header:
                    metadata.addPeptideColUnit(column, param);
                    break;
                case Small_Molecule_Header:
                    metadata.addSmallMoleculeColUnit(column, param);
                    break;

            }
            return CheckResult.ok;
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
