package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.*;
import uk.ac.ebi.pride.jmztab.model.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;

/**
 * Parse a metadata line  into a element.
 * Metadata Element start with MTD, its structure like:
 * MTD  {@link MetadataElement}([id])(-{@link MetadataSubElement}[pid])(-{@link MetadataProperty})    {Element Value}
 *
 * @see MetadataElement
 * @see MetadataSubElement
 * @see MetadataProperty
 *
 * @author qingwei
 * @since 08/02/13
 */
public class MTDLineParser extends MZTabLineParser {
    private static final String Error_Header = "MTD\t";

    private Metadata metadata = new Metadata();

    //TODO Review col Unit
    /**
     * Most of time, we use {@link #parseNormalMetadata(String, String)} to parse defineLabel into
     * Metadata Element.
     */
    public void parse(int lineNumber, String mtdLine, MZTabErrorList errorList) throws MZTabException {
        super.parse(lineNumber, mtdLine, errorList);

        if (items.length != 3) {
            MZTabError error = new MZTabError(FormatErrorType.MTDLine, lineNumber, mtdLine);
            throw new MZTabException(error);
        }

        String defineLabel = items[1].trim().toLowerCase();
        String valueLabel = items[2].trim();

        parseNormalMetadata(defineLabel, valueLabel);

    }

    /**
     * Parse valueLabel based on email format. If exists parse error, add it into {@link MZTabErrorList}.
     */
    private String checkEmail(String defineLabel, String valueLabel) {
        String email = parseEmail(valueLabel);

        if (email == null) {
            errorList.add(new MZTabError(FormatErrorType.Email, lineNumber, Error_Header + defineLabel, valueLabel));
        }

        return email;
    }

    /**
     * Parse {@link MetadataProperty} which depend on the {@link MetadataElement}.
     * If exists parse error, stop validate and throw {@link MZTabException} directly.
     */
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

    /**
     * Parse {@link MetadataProperty} which depend on the {@link MetadataSubElement}
     * If exists parse error, stop validate and throw {@link MZTabException} directly.
     */
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

    /**
     * Parse valueLabel to {@link MZTabDescription.Mode}
     * If exists parse error, stop validate and throw {@link MZTabException} directly.
     */
    private MZTabDescription.Mode checkMZTabMode(String defineLabel, String valueLabel) throws MZTabException {
        try {
            return MZTabDescription.Mode.valueOf(valueLabel);
        } catch (IllegalArgumentException e) {
            MZTabError error = new MZTabError(FormatErrorType.MZTabMode, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }
    }

    /**
     * Parse valueLabel to {@link MZTabDescription.Mode}
     * If exists parse error, stop validate and throw {@link MZTabException} directly.
     */
    private MZTabDescription.Type checkMZTabType(String defineLabel, String valueLabel) throws MZTabException {
        try {
            return MZTabDescription.Type.valueOf(valueLabel);
        } catch (IllegalArgumentException e) {
            MZTabError error = new MZTabError(FormatErrorType.MZTabType, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }
    }

    /**
     * Parse valueLabel to {@link Param}
     * If exists parse error, add it into {@link MZTabErrorList}
     */
    private Param checkParam(String defineLabel, String valueLabel) {
        Param param = parseParam(valueLabel);
        if (param == null) {
            errorList.add(new MZTabError(FormatErrorType.Param, lineNumber, Error_Header + defineLabel, valueLabel));
        }
        return param;
    }

    /**
     * Parse valueLabel to a list of '|' separated parameters.
     * If exists parse error, add it into {@link MZTabErrorList}
     */
    private SplitList<Param> checkParamList(String defineLabel, String valueLabel) {
        SplitList<Param> paramList = parseParamList(valueLabel);

        if (paramList.size() == 0) {
            errorList.add(new MZTabError(FormatErrorType.ParamList, lineNumber, Error_Header + defineLabel, valueLabel));
        }

        return paramList;
    }

    /**
     * Parse valueLabel to a list of '|' separated parameters.
     * If exists parse error, add it into {@link MZTabErrorList}
     */
    private SplitList<PublicationItem> checkPublication(String defineLabel, String valueLabel) {
        SplitList<PublicationItem> publications = parsePublicationItems(valueLabel);
        if (publications.size() == 0) {
            errorList.add(new MZTabError(FormatErrorType.Publication, lineNumber, Error_Header + defineLabel, valueLabel));
        }

        return publications;
    }

    /**
     * Parse valueLabel to a {@link java.net.URI}
     * If exists parse error, add it into {@link MZTabErrorList}
     */
    private java.net.URI checkURI(String defineLabel, String valueLabel) {
        java.net.URI uri = parseURI(valueLabel);
        if (uri == null) {
            errorList.add(new MZTabError(FormatErrorType.URI, lineNumber, Error_Header + defineLabel, valueLabel));
        }

        return uri;
    }

    /**
     * Parse valueLabel to {@link java.net.URL}
     * If exists parse error, add it into {@link MZTabErrorList}
     */
    private java.net.URL checkURL(String defineLabel, String valueLabel) {
        java.net.URL url = parseURL(valueLabel);
        if (url == null) {
            errorList.add(new MZTabError(FormatErrorType.URL, lineNumber, Error_Header + defineLabel, valueLabel));
        }

        return url;
    }

    /**
     * Parse defineLabel to a index id number.
     * If exists parse error, stop validate and throw {@link MZTabException} directly.
     */
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

    /**
     * Parse valueLabel to a {@link IndexedElement}
     * If exists parse error, stop validate and throw {@link MZTabException} directly.
     */
    private IndexedElement checkIndexedElement(String defineLabel, String valueLabel, MetadataElement element) throws MZTabException {
        IndexedElement indexedElement = parseIndexedElement(valueLabel, element);
        if (indexedElement == null) {
            MZTabError error = new MZTabError(FormatErrorType.IndexedElement, lineNumber, Error_Header + defineLabel, valueLabel);
            throw new MZTabException(error);
        }

        return indexedElement;
    }

    /**
     * Parse valueLabel to a {@link IndexedElement} list.
     * If exists parse error, stop validate and throw {@link MZTabException} directly.
     */
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
     * MTD  {defineLabel}    {valueLabel}
     *
     * In normal, define label structure like:
     * {@link MetadataElement}([id])(-{@link MetadataSubElement}[pid])(-{@link MetadataProperty})
     *
     * @see MetadataElement     : Mandatory
     * @see MetadataSubElement  : Optional
     * @see MetadataProperty    : Optional.
     *
     * If exists parse error, add it into {@link MZTabErrorList}
     */
    private void parseNormalMetadata(String defineLabel, String valueLabel) throws MZTabException {
        String regexp = "(\\w+)(\\[(\\w+)\\])?(-(\\w+)(\\[(\\w+)\\])?)?(-(\\w+))?";

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(defineLabel);

        if (matcher.find()) {
            // Stage 1: create Unit.
            MetadataElement element = MetadataElement.findElement(matcher.group(1));
            if (element == null) {
                throw new MZTabException(new MZTabError(FormatErrorType.MTDDefineLabel, lineNumber, defineLabel));
            }

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
                        if (param != null && (param.getValue() == null || param.getValue().trim().length() == 0)) {
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
                case PROTEIN_SEARCH_ENGINE_SCORE:
                    id = checkIndex(defineLabel, matcher.group(3));
                    metadata.addProteinSearchEngineScoreParam(id, checkParam(defineLabel, valueLabel));
                    break;
                case PEPTIDE_SEARCH_ENGINE_SCORE:
                    id = checkIndex(defineLabel, matcher.group(3));
                    metadata.addPeptideSearchEngineScoreParam(id, checkParam(defineLabel, valueLabel));
                    break;
                case PSM_SEARCH_ENGINE_SCORE:
                    id = checkIndex(defineLabel, matcher.group(3));
                    metadata.addPsmSearchEngineScoreParam(id, checkParam(defineLabel, valueLabel));
                    break;
                case SMALLMOLECULE_SEARCH_ENGINE_SCORE:
                    id = checkIndex(defineLabel, matcher.group(3));
                    metadata.addSmallMoleculeSearchEngineScoreParam(id, checkParam(defineLabel, valueLabel));
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
                        if (param != null) {
                            // fixed modification parameter should be setting.
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
                        if (param != null) {
                            // variable modification parameter should be setting.
                            metadata.addVariableModParam(id, param);
                        }
                    } else {
                        switch (property) {
                            case VARIABLE_MOD_POSITION:
                                metadata.addVariableModPosition(id, valueLabel);
                                break;
                            case VARIABLE_MOD_SITE:
                                metadata.addVariableModSite(id, valueLabel);
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
                        case MS_RUN_HASH:
                            metadata.addMsRunHash(id, valueLabel);
                            break;
                        case MS_RUN_HASH_METHOD:
                            metadata.addMsRunHashMethod(id, checkParam(defineLabel, valueLabel));
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
                                if (indexedElement != null) {
                                    Sample sample = metadata.getSampleMap().get(indexedElement.getId());
                                    if (sample == null) {
                                        throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, valueLabel,
                                            valueLabel, metadata.getMZTabMode().toString(), metadata.getMZTabType().toString()));
                                    }
                                    metadata.addAssaySample(id, sample);
                                }
                                break;
                            case ASSAY_MS_RUN_REF:
                                indexedElement = checkIndexedElement(defineLabel, valueLabel, MetadataElement.MS_RUN);
                                if (indexedElement != null) {
                                    MsRun msRun = metadata.getMsRunMap().get(indexedElement.getId());
                                    if (msRun == null) {
                                        throw new MZTabException(new MZTabError(
                                            LogicalErrorType.NotDefineInMetadata, lineNumber,
                                            valueLabel, metadata.getMZTabMode().toString(), metadata.getMZTabType().toString()));
                                    }
                                    metadata.addAssayMsRun(id, msRun);
                                }
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
                                    throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, valueLabel,
                                        valueLabel, metadata.getMZTabMode().toString(), metadata.getMZTabType().toString()));
                                }
                                if (metadata.getStudyVariableMap().containsKey(id) && metadata.getStudyVariableMap().get(id).getAssayMap().containsKey(e.getId())) {
                                    errorList.add(new MZTabError(LogicalErrorType.DuplicationID, lineNumber, valueLabel));
                                }
                                metadata.addStudyVariableAssay(id, metadata.getAssayMap().get(e.getId()));
                            }
                            break;
                        case STUDY_VARIABLE_SAMPLE_REFS:
                            indexedElementList = checkIndexedElementList(defineLabel, valueLabel, MetadataElement.SAMPLE);
                            for (IndexedElement e : indexedElementList) {
                                if (! metadata.getSampleMap().containsKey(e.getId())) {
                                    // can not find assay[id] in metadata.
                                    throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, valueLabel,
                                        valueLabel, metadata.getMZTabMode().toString(), metadata.getMZTabType().toString()));
                                }
                                if (metadata.getStudyVariableMap().containsKey(id) && metadata.getStudyVariableMap().get(id).getSampleMap().containsKey(e.getId())) {
                                    errorList.add(new MZTabError(LogicalErrorType.DuplicationID, lineNumber, valueLabel));
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
                case COLUNIT:
                        // In the stage, just store them into colUnitMap<defineLabel, valueLabel>.
                        // after table section columns created, add the col unit.
                        if (! defineLabel.equals("colunit-protein") &&
                                ! defineLabel.equals("colunit-peptide") &&
                                ! defineLabel.equals("colunit-psm") &&
                                ! defineLabel.equals("colunit-small_molecule")) {
                            errorList.add(new MZTabError(FormatErrorType.MTDDefineLabel, lineNumber, defineLabel));
                        } else {
                            metadata.getColUnitMap().put(defineLabel, valueLabel);
                        }
                    break;
            }

        } else {
            throw new MZTabException(new MZTabError(FormatErrorType.MTDLine, lineNumber, line));
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
