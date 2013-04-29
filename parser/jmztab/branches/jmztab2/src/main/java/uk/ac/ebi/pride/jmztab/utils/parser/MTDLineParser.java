package uk.ac.ebi.pride.jmztab.utils.parser;

import uk.ac.ebi.pride.jmztab.utils.errors.FormatErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;
import uk.ac.ebi.pride.jmztab.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.ReplicateUnit.REP;
import static uk.ac.ebi.pride.jmztab.model.SubUnit.SUB;
import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.*;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
* Metadata Element start with MTD, and structure like:
* MTD  {unit}-{element}([{id}])-{property} value
*
* In metadata section, every line is a Metadata Element Object.
*
* User: Qingwei
* Date: 08/02/13
*/
public class MTDLineParser extends MZTabLineParser {
    private Metadata metadata;

    private enum Result {
        ok,
        unitId_format_error,
        id_number_error,
        format_error,
        param_format_error,
        paramList_format_error,
        publication_format_error,
        uri_format_error,
        url_format_error,
        duplicate_error,
    }

    public MTDLineParser() {
        if (this.metadata == null) {
            this.metadata = new Metadata();
        }
    }

    public void check(int lineNumber, String mtdLine) throws MZTabException {
        super.check(lineNumber, mtdLine);

        if (items.length != 3) {
            MZTabError error = new MZTabError(FormatErrorType.MTDLine, lineNumber, mtdLine);
            throw new MZTabException(error);
        }

        if (items[1].contains("colunit")) {
            parseColUnit(items[1], items[2]);
        } else {
            parseNormalMetadata(items[1], items[2]);
        }
    }

    /**
     * For sub-sample definition, species, tissue, cell type and disease may be display "sub", may be not.
     * For example:
     * PRIDE_1234-cell_type[1]
     * PRIDE_1234-sub[1]-cell_type[1]
     */
    public boolean isSubSampleElement(MetadataElement element) {
        if (element == null) {
            return false;
        }

        switch (element) {
            case SPECIES:
            case TISSUE:
            case CELL_TYPE:
            case DISEASE:
                return true;
            default:
                return false;
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

    private Result checkNormalMetadata(String defineLabel, String valueLabel) {
        String regexp = "([\\w_]+)(-(rep|sub)\\[(\\d+)\\])?(-((\\w+)(\\[(\\d+)\\])?)?(-(\\w+))?)?";

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(defineLabel);

        if (matcher.find()) {
            // Stage 1: create Unit.
            String unitId = matcher.group(1);
            if (parseUnitId(unitId) == null) {
                return Result.unitId_format_error;
            }

            // group[3] value is sub or rep.
            String type = matcher.group(3);

            // Stage 2: create MetadataElement and MetaProperty
            String elementLabel = matcher.group(7);
            MetadataElement element = MetadataElement.findElement(elementLabel);

            // Stage 3: create id
            String idLabel = matcher.group(9);
            Integer id = null;
            if (idLabel != null) {
                id = new Integer(idLabel);
                if (id < 1) {
                    return Result.id_number_error;
                }
            }

            // Stage 4: create MetadataProperty
            String propertyLabel = matcher.group(11);
            MetadataProperty property = MetadataProperty.findProperty(elementLabel, propertyLabel);

            SplitList<Param> paramList;
            Param param;
            if (type == null) {
                if (isSubSampleElement(element)) {
                    /**
                     * type is null, element belong to sub-sample. For example:
                     * "MTD  PRIDE_1234-species[1]  [NEWT, 9606, Homo sapiens (Human), ]"
                     * Before we add this element into metadata, we should make sure that not exists Sub-Sample
                     * definition in the metadata currently. For example:
                     * "MTD  PRIDE_1234-sub[1]-species[1]  [NEWT, 9606, Homo sapiens (Human), ]"
                     * They are not allowed to display the same metadata.
                     *
                     * In metadata, "MTD  PRIDE_1234-species[1]  [NEWT, 9606, Homo sapiens (Human), ]"
                     * using "PRIDE_1234-sub" as unit identifier
                     */
                    String identifier = unitId + MINUS + SUB;
                    Unit unit = metadata.getUnit(identifier);
                    SubUnit subUnit = unit == null ? new SubUnit(unitId, null) : (SubUnit) unit;

                    // find all using "PRIDE_1234-sub[id]" as identifier subUnit.
                    List<SubUnit> subUnitList = new ArrayList<SubUnit>();
                    for (Unit u: metadata.values()) {
                        if (u.getIdentifier().startsWith(identifier + "[")) {
                            subUnitList.add((SubUnit) u);
                        }
                    }

                    if (element == null) {
                        return Result.format_error;
                    }

                    switch (element) {
                        case SPECIES:
                            for (SubUnit sub : subUnitList) {
                                if (! sub.getSpeciesMap().isEmpty()) {
                                    // already exists PRIDE_1234-sub[1]-species[1] definition in metadata.
                                    return Result.duplicate_error;
                                }
                            }
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return Result.param_format_error;
                            }
                            subUnit.addSpecies(id, param);
                            break;
                        case TISSUE:
                            for (SubUnit sub : subUnitList) {
                                if (! sub.getTissueMap().isEmpty()) {
                                    return Result.duplicate_error;
                                }
                            }
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return Result.param_format_error;
                            }
                            subUnit.addTissue(id, param);
                            break;
                        case CELL_TYPE:
                            for (SubUnit sub : subUnitList) {
                                if (! sub.getCellTypeMap().isEmpty()) {
                                    return Result.duplicate_error;
                                }
                            }
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return Result.param_format_error;
                            }
                            subUnit.addCellType(id, param);
                            break;
                        case DISEASE:
                            for (SubUnit sub : subUnitList) {
                                if (! sub.getDiseaseMap().isEmpty()) {
                                    return Result.duplicate_error;
                                }
                            }
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return Result.param_format_error;
                            }
                            subUnit.addDisease(id, param);
                            break;
                    }

                    metadata.addUnit(identifier, subUnit);
                } else {
                    Unit unit;
                    if (metadata.contains(unitId)) {
                        unit = metadata.getUnit(unitId);
                    } else {
                        unit = new Unit(unitId);
                        metadata.addUnit(unit);
                    }

                    if (element == null) {
                        return Result.format_error;
                    }

                    switch (element) {
                        case TITLE:
                            if (! unit.setTitle(valueLabel)) {
                                return Result.duplicate_error;
                            }
                            break;
                        case DESCRIPTION:
                            if (! unit.setDescription(valueLabel)) {
                                return Result.duplicate_error;
                            }
                            break;
                        case SAMPLE_PROCESSING:
                            paramList = parseParamList(valueLabel);
                            if (paramList.size() == 0) {
                                return Result.paramList_format_error;
                            } else if (! unit.addSampleProcessing(id, paramList)) {
                                return Result.duplicate_error;
                            }
                            break;
                        case INSTRUMENT:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return Result.param_format_error;
                            }
                            switch (property) {
                                case INSTRUMENT_NAME:
                                    if (! unit.addInstrumentName(id, param)) {
                                        return Result.duplicate_error;
                                    }
                                    break;
                                case INSTRUMENT_SOURCE:
                                    if (! unit.addInstrumentSource(id, param)) {
                                        return Result.duplicate_error;
                                    }
                                    break;
                                case INSTRUMENT_ANALYZER:
                                    if (! unit.addInstrumentAnalyzer(id, param)) {
                                        return Result.duplicate_error;
                                    }
                                    break;
                                case INSTRUMENT_DETECTOR:
                                    if (! unit.addInstrumentDetector(id, param)) {
                                        return Result.duplicate_error;
                                    }
                                    break;
                                default:
                                    return Result.format_error;
                            }
                            break;
                        case SOFTWARE:
                            if (isEmpty(propertyLabel)) {
                                param = parseParam(valueLabel);
                                if (param == null) {
                                    return Result.param_format_error;
                                }
                                if (isEmpty(param.getValue())) {
                                    new MZTabError(LogicalErrorType.Software, lineNumber, valueLabel);
                                }
                                if (! unit.addSoftwareParam(id, parseParam(valueLabel))) {
                                    return Result.duplicate_error;
                                }
                            } else {
                                switch (property) {
                                    case SOFTWARE_SETTING:
                                        unit.addSoftwareSetting(id, valueLabel);
                                        break;
                                    default:
                                        return Result.format_error;
                                }
                            }
                            break;
                        case FALSE_DISCOVERY_RATE:
                            paramList = parseParamList(valueLabel);
                            if (paramList.size() == 0) {
                                return Result.paramList_format_error;
                            }
                            unit.setFalseDiscoveryRate(paramList);
                            break;
                        case PUBLICATION:
                            SplitList<PublicationItem> items = parsePublicationItems(valueLabel);
                            if (items.size() == 0) {
                                return Result.publication_format_error;
                            }
                            unit.addPublicationItems(id, items);
                            break;
                        case CONTACT:
                            switch (property) {
                                case CONTACT_NAME:
                                    unit.addContactName(id, valueLabel);
                                    break;
                                case CONTACT_AFFILIATION:
                                    unit.addContactAffiliation(id, valueLabel);
                                    break;
                                case CONTACT_EMAIL:
                                    String email = checkEmail(defineLabel, valueLabel);
                                    unit.addContactEmail(id, email);
                                    break;
                                default:
                                    return Result.format_error;
                            }
                            break;
                        case URI:
                            java.net.URI uri = parseURI(valueLabel);
                            if (uri == null) {
                                return Result.uri_format_error;
                            }
                            unit.addUri(uri);
                            break;
                        case MOD:
                            paramList = parseParamList(valueLabel);
                            if (paramList.size() == 0) {
                                return Result.paramList_format_error;
                            }
                            unit.setMod(paramList);
                            break;
                        case QUANTIFICATION_METHOD:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return Result.param_format_error;
                            }
                            unit.setQuantificationMethod(param);
                            break;
                        case PROTEIN:
                            switch (property) {
                                case PROTEIN_QUANTIFICATION_UNIT:
                                    param = parseParam(valueLabel);
                                    if (param == null) {
                                        return Result.param_format_error;
                                    }
                                    unit.setProteinQuantificationUnit(param);
                                    break;
                                default:
                                    return Result.format_error;
                            }
                            break;
                        case PEPTIDE:
                            switch (property) {
                                case PEPTIDE_QUANTIFICATION_UNIT:
                                    param = parseParam(valueLabel);
                                    if (param == null) {
                                        return Result.param_format_error;
                                    }
                                    unit.setPeptideQuantificationUnit(param);
                                    break;
                                default:
                                    return Result.format_error;
                            }
                            break;
                        case MS_FILE:
                            switch (property) {
                                case MS_FILE_FORMAT:
                                    param = parseParam(valueLabel);
                                    if (param == null || ! (param instanceof CVParam)) {
                                        return Result.param_format_error;
                                    }
                                    unit.addMsFileFormat(id, (CVParam) param);
                                    break;
                                case MS_FILE_LOCATION:
                                    java.net.URL url = parseURL(valueLabel);
                                    if (url == null) {
                                        return Result.url_format_error;
                                    }
                                    unit.addMsFileLocation(id, url);
                                    break;
                                case MS_FILE_ID_FORMAT:
                                    param = parseParam(valueLabel);
                                    if (param == null || ! (param instanceof CVParam)) {
                                        return Result.param_format_error;
                                    }
                                    unit.addMsFileIdFormat(id, (CVParam) param);
                                    break;
                                default:
                                    return Result.format_error;
                            }
                            break;
                        case CUSTOM:
                            param = parseParam(valueLabel);
                            if (param == null) {
                                return Result.param_format_error;
                            }
                            unit.addCustom(param);
                            break;
                        default:
                            return Result.format_error;
                    }

                    metadata.addUnit(unit);
                }
            } else if (type.equals(SUB)) {
                String identifier = unitId + MINUS + SUB;

                // locate subUnit which using "UnitID-sub[id]" as its identifier.
                Unit unit = metadata.getUnit(identifier + "[" + matcher.group(4) + "]");
                SubUnit subUnit;
                if (unit == null) {
                    subUnit = new SubUnit(unitId, new Integer(matcher.group(4)));
                } else {
                    subUnit = (SubUnit) unit;
                }

                // locate subUnit which using "UnitID-sub" as its identifier.
                SubUnit sampleUnit = metadata.getUnit(identifier) == null ? null : (SubUnit) metadata.getUnit(identifier);

                switch (element) {
                    case SPECIES:
                        if (sampleUnit != null && sampleUnit.getSpeciesMap().get(id) != null) {
                            // exists PRIDE_1234-species[id]
                            return Result.duplicate_error;
                        }

                        param = parseParam(valueLabel);
                        if (param == null) {
                            return Result.param_format_error;
                        }
                        if (! subUnit.addSpecies(id, param)) {
                            return Result.duplicate_error;
                        }
                        break;
                    case TISSUE:
                        if (sampleUnit != null && sampleUnit.getTissueMap().get(id) != null) {
                            return Result.duplicate_error;
                        }

                        param = parseParam(valueLabel);
                        if (param == null) {
                            return Result.param_format_error;
                        }
                        if (! subUnit.addTissue(id, param)) {
                            return Result.duplicate_error;
                        }
                        break;
                    case CELL_TYPE:
                        if (sampleUnit != null && sampleUnit.getCellTypeMap().get(id) != null) {
                            return Result.duplicate_error;
                        }

                        param = parseParam(valueLabel);
                        if (param == null) {
                            return Result.param_format_error;
                        }
                        if (! subUnit.addCellType(id, param)) {
                            return Result.duplicate_error;
                        }
                        break;
                    case DISEASE:
                        if (sampleUnit != null && sampleUnit.getDiseaseMap().get(id) != null) {
                            return Result.duplicate_error;
                        }

                        param = parseParam(valueLabel);
                        if (param == null) {
                            return Result.param_format_error;
                        }
                        if (! subUnit.addDisease(id, param)) {
                            return Result.duplicate_error;
                        }
                        break;
                    case DESCRIPTION:
                        if (! subUnit.setDescription(valueLabel)) {
                            return Result.duplicate_error;
                        }
                        break;
                    case QUANTIFICATION_REAGENT:
                        param = parseParam(valueLabel);
                        if (param == null) {
                            return Result.param_format_error;
                        }
                        subUnit.setQuantificationReagent(param);
                        break;
                    case CUSTOM:
                        param = parseParam(valueLabel);
                        if (param == null) {
                            return Result.param_format_error;
                        }
                        subUnit.addCustom(param);
                        break;
                    default:
                        return Result.format_error;
                }

                metadata.addUnit(subUnit);
            } else if (type.equals(REP)) {
                Unit unit = metadata.getUnit(unitId + MINUS + REP + "[" + matcher.group(4) + "]");
                ReplicateUnit repUnit = unit == null ? new ReplicateUnit(unitId, new Integer(matcher.group(4))) : (ReplicateUnit) unit;
                repUnit.setComment(valueLabel);
                metadata.addUnit(repUnit);
            }

            return Result.ok;
        } else {
            return Result.format_error;
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
     * check label and generate Unit, MetadataElement, id, MetadataProperty objects.
     * If optional item not exists, return null.
     *
     * Notice 1:
     * In replicate unit, MetadataElement maybe null. For example: Exp_1-rep[1].
     */
    private void parseNormalMetadata(String defineLabel, String valueLabel) throws MZTabException {
        Result result  = checkNormalMetadata(defineLabel, valueLabel);

        MZTabError error = null;
        switch (result) {
            case unitId_format_error:
                error = new MZTabError(FormatErrorType.UnitID, lineNumber, "", defineLabel);
                break;
            case id_number_error:
                error = new MZTabError(LogicalErrorType.IdNumber, lineNumber, defineLabel);
                break;
            case format_error:
                error = new MZTabError(FormatErrorType.MTDDefineLabel, lineNumber, defineLabel);
                break;
            case param_format_error:
                error = new MZTabError(FormatErrorType.Param, lineNumber, defineLabel, valueLabel);
                break;
            case paramList_format_error:
                error = new MZTabError(FormatErrorType.ParamList, lineNumber, defineLabel, valueLabel);
                break;
            case publication_format_error:
                error = new MZTabError(FormatErrorType.Publication, lineNumber, defineLabel, valueLabel);
                break;
            case uri_format_error:
                error = new MZTabError(FormatErrorType.URI, lineNumber, defineLabel, valueLabel);
                break;
            case url_format_error:
                error = new MZTabError(FormatErrorType.URL, lineNumber, defineLabel, valueLabel);
                break;
            case duplicate_error:
                error = new MZTabError(LogicalErrorType.Duplication, lineNumber, defineLabel + TAB + valueLabel);
                break;
        }

        if (error != null) {
            throw new MZTabException(error);
        }
    }

    /**
     *
     * @param defineLabel {UnitId}-colunit-{protein|peptide|small_molecule}
     * @param valueLabel {column name}={Parameter defining the unit}
     * @throws MZTabException
     */
    private void parseColUnit(String defineLabel, String valueLabel) throws MZTabException {
        Pattern pattern;
        Matcher matcher;

        MZTabError error;
        Section colSection;
        Unit unit;
        MZTabColumnFactory factory;

        // Stage 1: check define label
        pattern = Pattern.compile("(\\w+)-colunit-(protein|peptide|small_molecule)");
        matcher = pattern.matcher(defineLabel);
        if (matcher.find()) {
            String identifier = matcher.group(1);
            unit = metadata.getUnit(identifier);
            if (unit == null) {
                error = new MZTabError(LogicalErrorType.UnitID, lineNumber, identifier);
                throw new MZTabException(error);
            }

            colSection = Section.findSection(matcher.group(2));
            if (colSection == null) {
                error = new MZTabError(FormatErrorType.ColUnit, lineNumber, defineLabel);
                throw new MZTabException(error);
            }

            factory = MZTabColumnFactory.getInstance(colSection);
        } else {
            error = new MZTabError(FormatErrorType.ColUnit, lineNumber, defineLabel);
            throw new MZTabException(error);
        }

        // validate {column_name}={parameter}
        if (factory != null) {
            String[] items = valueLabel.split("=");
            MZTabColumn column = factory.getColumn(items[0].trim());
            if (column == null) {
                // column_name not exists in the factory.
                new MZTabError(LogicalErrorType.ColUnit, lineNumber, defineLabel + TAB + valueLabel, items[0]);
            } else {
                Param param = parseParam(items[1].trim());
                if (param == null) {
                    error = new MZTabError(FormatErrorType.Param, lineNumber, items[1]);
                    throw new MZTabException(error);
                }

                switch (colSection) {
                    case Protein:
                        unit.addProteinColUnit(column, param);
                        break;
                    case Peptide:
                        unit.addPeptideColUnit(column, param);
                        break;
                    case Small_Molecule:
                        unit.addSmallMoleculeColUnit(column, param);
                        break;

                }
            }
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
