package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
* Metadata Element start with MTD, and structure like:
* MTD  {MetadataElement}([id])(-{MetadataProperty})
*
* @see MetadataElement  : Mandatory
* @see MetadataProperty : Optional.
*
* User: Qingwei
* Date: 08/02/13
*/
public class MTDLineParser extends MZTabLineParser {
    private static final String Error_Header = "MTD\t";

    private Metadata metadata = new Metadata();

    private Map<String, String> colUnitMap = new HashMap<String, String>();

    /**
     * For facing colunit definition line, for example:
     * MTD  colunit-protein retention_time=[UO, UO:000031, minute, ]
     * after parse metadata and header lines, need calling
     * {@link #refineColUnit(uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory)} manually.
     */
    public void parse(int lineNumber, String mtdLine, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, mtdLine, errorList);

        if (items.length != 3) {
            MZTabError error = new MZTabError(FormatErrorType.MTDLine, lineNumber, mtdLine);
            throw new MZTabException(error);
        }

        String defineLabel = items[1].trim().toLowerCase();
        String valueLabel = items[2].trim();

        if (defineLabel.contains("colunit")) {
            // ignore colunit parse. In the stage, just store them into colUnitMap<defineLabel, valueLabel>.
            // after table section columns created, call checkColUnit manually.
            colUnitMap.put(defineLabel, valueLabel);

            if (! defineLabel.equals("colunit-protein") &&
                ! defineLabel.equals("colunit-peptide") &&
                ! defineLabel.equals("colunit-psm") &&
                ! defineLabel.equals("colunit-small_molecule")) {
                MZTabError error = new MZTabError(FormatErrorType.MTDDefineLabel, lineNumber, defineLabel);
                throw new MZTabException(error);
            }
        } else {
            parseNormalMetadata(defineLabel, valueLabel);
        }
    }

    private String checkEmail(String defineLabel, String valueLabel) throws MZTabException {
        String email = parseEmail(valueLabel);

        if (email == null) {
            MZTabError error = new MZTabError(FormatErrorType.Email, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return email;
    }

    private MetadataProperty checkProperty(MetadataElement element, String propertyName) throws MZTabException {
        if (isEmpty(propertyName)) {
            return null;
        }

        MetadataProperty property = MetadataProperty.findProperty(element, propertyName);
        if (property == null) {
            MZTabError error = new MZTabError(FormatErrorType.MTDDefineLabel, lineNumber, element.getName() + "-" + propertyName);
            throw new MZTabException(error);
        }

        return property;
    }

    private MetadataProperty checkProperty(MetadataSubElement subElement, String propertyName) throws MZTabException {
        if (isEmpty(propertyName)) {
            return null;
        }

        MetadataProperty property = MetadataProperty.findProperty(subElement, propertyName);
        if (property == null) {
            MZTabError error = new MZTabError(FormatErrorType.MTDDefineLabel, lineNumber, subElement.getName() + "-" + propertyName);
            throw new MZTabException(error);
        }

        return property;
    }

    private MZTabDescription.Mode checkMZTabMode(String defineLabel, String valueLabel) throws MZTabException {
        try {
            return MZTabDescription.Mode.valueOf(valueLabel);
        } catch (IllegalArgumentException e) {
            MZTabError error = new MZTabError(FormatErrorType.MZTabMode, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }
    }

    private MZTabDescription.Type checkMZTabType(String defineLabel, String valueLabel) throws MZTabException {
        try {
            return MZTabDescription.Type.valueOf(valueLabel);
        } catch (IllegalArgumentException e) {
            MZTabError error = new MZTabError(FormatErrorType.MZTabType, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }
    }

    private Param checkParam(String defineLabel, String valueLabel) throws MZTabException {
        Param param = parseParam(valueLabel);
        if (param == null) {
            MZTabError error = new MZTabError(FormatErrorType.Param, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return param;
    }

    private SplitList<Param> checkParamList(String defineLabel, String valueLabel) throws MZTabException {
        SplitList<Param> paramList = parseParamList(valueLabel);
        if (paramList == null || paramList.size() == 0) {
            MZTabError error = new MZTabError(FormatErrorType.ParamList, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return paramList;
    }

    private SplitList<PublicationItem> checkPublication(String defineLabel, String valueLabel) throws MZTabException {
        SplitList<PublicationItem> publications = parsePublicationItems(valueLabel);
        if (publications.size() == 0) {
            MZTabError error = new MZTabError(FormatErrorType.Publication, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return publications;
    }

    private java.net.URI checkURI(String defineLabel, String valueLabel) throws MZTabException {
        java.net.URI uri = parseURI(valueLabel);
        if (uri == null) {
            MZTabError error = new MZTabError(FormatErrorType.URI, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return uri;
    }

    private java.net.URL checkURL(String defineLabel, String valueLabel) throws MZTabException {
        java.net.URL url = parseURL(valueLabel);
        if (url == null) {
            MZTabError error = new MZTabError(FormatErrorType.URL, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return url;
    }

    // the id is not correct number in the define label.
    private int checkIndex(String defineLabel, String id) throws MZTabException {
        try {
            Integer index = Integer.parseInt(id);
            if (index < 1) {
                throw new NumberFormatException();
            }

            return index;
        } catch (NumberFormatException e) {
            MZTabError error = new MZTabError(LogicalErrorType.IdNumber, lineNumber, Error_Header + defineLabel, id);
            throw new MZTabException(error);
        }
    }

    private IndexedElement checkIndexedElement(String defineLabel, String valueLabel, MetadataElement element) throws MZTabException {
        IndexedElement indexedElement = parseIndexedElement(valueLabel, element);
        if (indexedElement == null) {
            MZTabError error = new MZTabError(FormatErrorType.IndexedElement, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return indexedElement;
    }

    private List<IndexedElement> checkIndexedElementList(String defineLabel, String valueLabel, MetadataElement element) throws MZTabException {
        List<IndexedElement> indexedElementList = parseIndexedElementList(valueLabel, element);
        if (indexedElementList == null || indexedElementList.size() == 0) {
            MZTabError error = new MZTabError(FormatErrorType.IndexedElement, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return indexedElementList;
    }

    /**
     * The metadata line including three parts:
     * MTD  {define}    {value}
     *
     * In normal, define label structure like:
     * {element}([{id}])(-property)
     *
     * ([{id}]) and {-property} are optional.
     *
     * parse label and generate Unit, MetadataElement, id, MetadataProperty objects.
     * If optional item not exists, return null.
     */
    private void parseNormalMetadata(String defineLabel, String valueLabel) throws MZTabException {
        String regexp = "(\\w+)(\\[(\\w+)\\])?(-(\\w+)(\\[(\\w+)\\])?)?(-(\\w+))?";

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(defineLabel);

        if (matcher.find()) {
            // Stage 1: create Unit.
            MetadataElement element = MetadataElement.findElement(matcher.group(1));

            Integer id;
            MetadataProperty property;
            Param param;
            SplitList<Param> paramList;
            IndexedElement indexedElement;
            List<IndexedElement> indexedElementList;
            switch (element) {
                case MZTAB:
                    property = checkProperty(element, matcher.group(5));
                    switch (property) {
                        case MZTAB_VERSION:
                            metadata.setMZTabVersion(valueLabel);
                            break;
                        case MZTAB_MODE:
                            metadata.setMZTabMode(checkMZTabMode(defineLabel, valueLabel));
                            break;
                        case MZTAB_TYPE:
                            metadata.setMZTabType(checkMZTabType(defineLabel, valueLabel));
                            break;
                        case MZTAB_ID:
                            if (metadata.getMZTabID() != null) {
                                throw new MZTabException(new MZTabError(LogicalErrorType.DuplicationDefine, lineNumber, defineLabel));
                            }
                            metadata.setMZTabID(valueLabel);
                            break;
                    }

                    break;
                case TITLE:
                    if (metadata.getTitle() != null) {
                        throw new MZTabException(new MZTabError(LogicalErrorType.DuplicationDefine, lineNumber, defineLabel));
                    }
                    metadata.setTitle(valueLabel);
                    break;
                case DESCRIPTION:
                    if (metadata.getDescription() != null) {
                        throw new MZTabException(new MZTabError(LogicalErrorType.DuplicationDefine, lineNumber, defineLabel));
                    }
                    metadata.setDescription(valueLabel);
                    break;
                case SAMPLE_PROCESSING:
                    id = checkIndex(defineLabel, matcher.group(3));
                    metadata.addSampleProcessing(id, checkParamList(defineLabel, valueLabel));
                    break;
                case INSTRUMENT:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));
                    param = checkParam(defineLabel, valueLabel);

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
                    }

                    break;
                case SOFTWARE:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));
                    if (property == null) {
                        param = checkParam(defineLabel, valueLabel);
                        if (param.getValue() == null || param.getValue().trim().length() == 0) {
                            // this is a warn.
                            errorList.add(new MZTabError(LogicalErrorType.SoftwareVersion, lineNumber, valueLabel));
                        }
                        metadata.addSoftwareParam(id, param);
                    } else {
                        switch (property) {
                            case SOFTWARE_SETTING:
                                metadata.addSoftwareSetting(id, valueLabel);
                                break;
                        }
                    }

                    break;
                case FALSE_DISCOVERY_RATE:
                    if (metadata.getFalseDiscoveryRate().size() > 0) {
                        throw new MZTabException(new MZTabError(LogicalErrorType.DuplicationDefine, lineNumber, defineLabel));
                    }
                    paramList = checkParamList(defineLabel, valueLabel);
                    metadata.setFalseDiscoveryRate(paramList);
                    break;
                case PUBLICATION:
                    id = checkIndex(defineLabel, matcher.group(3));
                    metadata.addPublicationItems(id, checkPublication(defineLabel, valueLabel));
                    break;
                case CONTACT:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));

                    switch (property) {
                        case CONTACT_NAME:
                            metadata.addContactName(id, valueLabel);
                            break;
                        case CONTACT_AFFILIATION:
                            metadata.addContactAffiliation(id, valueLabel);
                            break;
                        case CONTACT_EMAIL:
                            metadata.addContactEmail(id, checkEmail(defineLabel, valueLabel));
                            break;
                    }
                    break;
                case URI:
                    metadata.addUri(checkURI(defineLabel, valueLabel));
                    break;
                case FIXED_MOD:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));
                    if (property == null) {
                        param = checkParam(defineLabel, valueLabel);
                        if (param == null) {
                            // fixed modification parameter should be setting.
                            errorList.add(new MZTabError(FormatErrorType.Param, lineNumber, valueLabel));
                        } else {
                            metadata.addFixedModParam(id, param);
                        }
                    } else {
                        switch (property) {
                            case FIXED_MOD_POSITION:
                                metadata.addFixedModPosition(id, valueLabel);
                                break;
                            case FIXED_MOD_SITE:
                                metadata.addFixedModSite(id, valueLabel);
                                break;
                        }
                    }
                    break;
                case VARIABLE_MOD:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));
                    if (property == null) {
                        param = checkParam(defineLabel, valueLabel);
                        if (param == null) {
                            // variable modification parameter should be setting.
                            errorList.add(new MZTabError(FormatErrorType.Param, lineNumber, valueLabel));
                        } else {
                            metadata.addVariableModParam(id, param);
                        }
                    } else {
                        switch (property) {
                            case VARIABLE_MOD_POSITION:
                                metadata.addFixedModPosition(id, valueLabel);
                                break;
                            case VARIABLE_MOD_SITE:
                                metadata.addFixedModSite(id, valueLabel);
                                break;
                        }
                    }
                    break;
                case QUANTIFICATION_METHOD:
                    if (metadata.getQuantificationMethod() != null) {
                        throw new MZTabException(new MZTabError(LogicalErrorType.DuplicationDefine, lineNumber, defineLabel));
                    }
                    metadata.setQuantificationMethod(checkParam(defineLabel, valueLabel));
                    break;
                case PROTEIN:
                    property = checkProperty(element, matcher.group(5));
                    switch (property) {
                        case PROTEIN_QUANTIFICATION_UNIT:
                            if (metadata.getProteinQuantificationUnit() != null) {
                                throw new MZTabException(new MZTabError(LogicalErrorType.DuplicationDefine, lineNumber, defineLabel));
                            }
                            metadata.setProteinQuantificationUnit(checkParam(defineLabel, valueLabel));
                            break;
                    }
                    break;
                case PEPTIDE:
                    property = checkProperty(element, matcher.group(5));
                    switch (property) {
                        case PEPTIDE_QUANTIFICATION_UNIT:
                            if (metadata.getPeptideQuantificationUnit() != null) {
                                throw new MZTabException(new MZTabError(LogicalErrorType.DuplicationDefine, lineNumber, defineLabel));
                            }
                            metadata.setPeptideQuantificationUnit(checkParam(defineLabel, valueLabel));
                            break;
                    }
                    break;
                case SMALL_MOLECULE:
                    property = checkProperty(element, matcher.group(5));
                    switch (property) {
                        case SMALL_MOLECULE_QUANTIFICATION_UNIT:
                            if (metadata.getSmallMoleculeQuantificationUnit() != null) {
                                throw new MZTabException(new MZTabError(LogicalErrorType.DuplicationDefine, lineNumber, defineLabel));
                            }
                            metadata.setSmallMoleculeQuantificationUnit(checkParam(defineLabel, valueLabel));
                            break;
                    }
                    break;
                case MS_RUN:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));

                    switch (property) {
                        case MS_RUN_FORMAT:
                            metadata.addMsRunFormat(id, checkParam(defineLabel, valueLabel));
                            break;
                        case MS_RUN_LOCATION:
                            metadata.addMsRunLocation(id, checkURL(defineLabel, valueLabel));
                            break;
                        case MS_RUN_ID_FORMAT:
                            metadata.addMsRunIdFormat(id, checkParam(defineLabel, valueLabel));
                            break;
                        case MS_RUN_FRAGMENTATION_METHOD:
                            metadata.addMsRunFragmentationMethod(id, checkParam(defineLabel, valueLabel));
                            break;
                    }

                    break;
                case CUSTOM:
                    metadata.addCustom(checkParam(defineLabel, valueLabel));
                    break;
                case SAMPLE:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));

                    switch (property) {
                        case SAMPLE_SPECIES:
                            metadata.addSampleSpecies(id, checkParam(defineLabel, valueLabel));
                            break;
                        case SAMPLE_TISSUE:
                            metadata.addSampleTissue(id, checkParam(defineLabel, valueLabel));
                            break;
                        case SAMPLE_CELL_TYPE:
                            metadata.addSampleCellType(id, checkParam(defineLabel, valueLabel));
                            break;
                        case SAMPLE_DISEASE:
                            metadata.addSampleDisease(id, checkParam(defineLabel, valueLabel));
                            break;
                        case SAMPLE_DESCRIPTION:
                            metadata.addSampleDescription(id, valueLabel);
                            break;
                        case SAMPLE_CUSTOM:
                            metadata.addSampleCustom(id, checkParam(defineLabel, valueLabel));
                            break;
                    }
                    break;
                case ASSAY:
                    if (isEmpty(matcher.group(6))) {
                        // no quantification modification. For example: assay[1-n]-quantification_reagent
                        id = checkIndex(defineLabel, matcher.group(3));
                        property = checkProperty(element, matcher.group(5));
                        switch (property) {
                            case ASSAY_QUANTIFICATION_REAGENT:
                                metadata.addAssayQuantificationReagent(id, checkParam(defineLabel, valueLabel));
                                break;
                            case ASSAY_SAMPLE_REF:
                                indexedElement = checkIndexedElement(defineLabel, valueLabel, MetadataElement.SAMPLE);
                                Sample sample = metadata.getSampleMap().get(indexedElement.getId());
                                if (sample == null) {
                                    throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, valueLabel));
                                }
                                metadata.addAssaySample(id, sample);
                                break;
                            case ASSAY_MS_RUN_REF:
                                indexedElement = checkIndexedElement(defineLabel, valueLabel, MetadataElement.MS_RUN);
                                MsRun msRun = metadata.getMsRunMap().get(indexedElement.getId());
                                if (msRun == null) {
                                    throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, valueLabel));
                                }
                                metadata.addAssayMsRun(id, msRun);
                                break;
                        }
                    } else {
                        // quantification modification. For example: assay[1]-quantification_mod[1], assay[1]-quantification_mod[1]-site
                        id = checkIndex(defineLabel, matcher.group(3));
                        MetadataSubElement subElement = MetadataSubElement.findSubElement(element, matcher.group(5));
                        switch (subElement) {
                            case ASSAY_QUANTIFICATION_MOD:
                                int modId = checkIndex(defineLabel, matcher.group(7));
                                property = checkProperty(subElement, matcher.group(9));
                                if (property == null) {
                                    metadata.addAssayQuantificationModParam(id, modId, checkParam(defineLabel, valueLabel));
                                } else {
                                    switch (property) {
                                        case ASSAY_QUANTIFICATION_MOD_SITE:
                                            metadata.addAssayQuantificationModSite(id, modId, valueLabel);
                                            break;
                                        case ASSAY_QUANTIFICATION_MOD_POSITION:
                                            metadata.addAssayQuantificationModPosition(id, modId, valueLabel);
                                            break;
                                    }
                                }

                                break;
                        }
                    }

                    break;
                case STUDY_VARIABLE:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));
                    switch (property) {
                        case STUDY_VARIABLE_ASSAY_REFS:
                            indexedElementList = checkIndexedElementList(defineLabel, valueLabel, MetadataElement.ASSAY);
                            for (IndexedElement e : indexedElementList) {
                                if (! metadata.getAssayMap().containsKey(e.getId())) {
                                    // can not find assay[id] in metadata.
                                    throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, valueLabel));
                                }
                                metadata.addStudyVariableAssay(id, metadata.getAssayMap().get(e.getId()));
                            }
                            break;
                        case STUDY_VARIABLE_SAMPLE_REFS:
                            indexedElementList = checkIndexedElementList(defineLabel, valueLabel, MetadataElement.SAMPLE);
                            for (IndexedElement e : indexedElementList) {
                                if (! metadata.getSampleMap().containsKey(e.getId())) {
                                    // can not find assay[id] in metadata.
                                    throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, valueLabel));
                                }
                                metadata.addStudyVariableSample(id, metadata.getSampleMap().get(e.getId()));
                            }
                            break;
                        case STUDY_VARIABLE_DESCRIPTION:
                            metadata.addStudyVariableDescription(id, valueLabel);
                            break;
                    }
                    break;
                case CV:
                    id = checkIndex(defineLabel, matcher.group(3));
                    property = checkProperty(element, matcher.group(5));
                    switch (property) {
                        case CV_LABEL:
                            metadata.addCVLabel(id, valueLabel);
                            break;
                        case CV_FULL_NAME:
                            metadata.addCVFullName(id, valueLabel);
                            break;
                        case CV_VERSION:
                            metadata.addCVVersion(id, valueLabel);
                            break;
                        case CV_URL:
                            metadata.addCVURL(id, valueLabel);
                            break;
                    }
                    break;
            }

        } else {
            throw new MZTabException(new MZTabError(FormatErrorType.MTDLine, lineNumber, line));
        }
    }

    /**
     * Refine the metadata, and check whether missing some important information.
     * fixed_mode, variable_mode must provide in the Complete file.
     * Detail information see specification 5.5
     */
    public void refineNormalMetadata() throws MZTabException {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        SortedMap<Integer, StudyVariable> svMap = metadata.getStudyVariableMap();
        SortedMap<Integer, Assay> assayMap = metadata.getAssayMap();
        SortedMap<Integer, MsRun> runMap = metadata.getMsRunMap();

        if (mode == MZTabDescription.Mode.Complete) {
            if (metadata.getSoftwareMap().size() == 0) {
                throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "software[1-n]", mode.toString(), type.toString()));
            }

            if (type == MZTabDescription.Type.Quantification) {
                if (metadata.getQuantificationMethod() == null) {
                    throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "quantification_method", mode.toString(), type.toString()));
                }
                for (Integer id : assayMap.keySet()) {
                    if (assayMap.get(id).getMsRun() == null) {
                        throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "assay[" + id + "]-ms_run_ref", mode.toString(), type.toString()));
                    }
                    if (assayMap.get(id).getQuantificationReagent() == null) {
                        throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "assay[" + id + "]-quantification_reagent", mode.toString(), type.toString()));
                    }
                }
                if (svMap.size() > 0 && assayMap.size() > 0) {
                    for (Integer id : svMap.keySet()) {
                        if (svMap.get(id).getAssayMap().size() == 0) {
                            throw new MZTabException(new MZTabError(LogicalErrorType.AssayRefs, lineNumber, "study_variable[" + id + "]-assay_refs"));
                        }
                    }
                }
            }

        }

        // Complete and Summary should provide following information.
        // mzTab-version, mzTab-mode and mzTab-type have default values in create metadata. Not check here.
        if (metadata.getDescription() == null) {
            throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "description", mode.toString(), type.toString()));
        }
        for (Integer id : runMap.keySet()) {
            if (runMap.get(id).getLocation() == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "ms_run[" + id + "]-location", mode.toString(), type.toString()));
            }
        }

        if (type == MZTabDescription.Type.Quantification) {
            for (Integer id : svMap.keySet()) {
                if (svMap.get(id).getDescription() == null) {
                    throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "study_variable[" + id + "]-description", mode.toString(), type.toString()));
                }
            }
        }
    }

    /**
     * valueLabel pattern like: column_name=param_string
     */
    private MZTabError checkColUnit(String valueLabel, MZTabColumnFactory factory) {
        String[] items = valueLabel.split("=");
        String columnName = items[0].trim();
        String value = items[1].trim();

        MZTabColumn column = factory.findColumnByHeader(columnName);
        if (column == null) {
            // column_name not exists in the factory.
            return new MZTabError(FormatErrorType.ColUnit, lineNumber, valueLabel, columnName);
        } else {
            Param param = parseParam(value);
            if (param == null) {
                return new MZTabError(FormatErrorType.Param, lineNumber, valueLabel, value);
            }

            switch (factory.getSection()) {
                case Protein_Header:
                    metadata.addProteinColUnit(column, param);
                    break;
                case Peptide_Header:
                    metadata.addPeptideColUnit(column, param);
                    break;
                case PSM_Header:
                    metadata.addPSMColUnit(column, param);
                    break;
                case Small_Molecule_Header:
                    metadata.addSmallMoleculeColUnit(column, param);
                    break;
            }

            return null;
        }
    }

    /**
     * Based on factory, navigate colUnitMap<defineLabel, valueLabel>
     * and refine colunit columns are correct or not.
     *
     * Notice: after refined phase, colunit definition can be record in the metadata.
     */
    public void refineColUnit(MZTabColumnFactory factory) throws MZTabException {
        String valueLabel;
        for (String defineLabel : colUnitMap.keySet()) {
            if (defineLabel.equalsIgnoreCase("colunit-" + Section.toDataSection(factory.getSection()).getName())) {
                valueLabel = colUnitMap.get(defineLabel);
                MZTabError error = checkColUnit(valueLabel, factory);

                if (error != null) {
                    throw new MZTabException(error);
                }
            }
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
