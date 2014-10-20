package uk.ac.ebi.pride.jmztab.utils.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.SmallMoleculeColumn.*;

/**
 * Parse and validate Small Molecule header line into a {@link MZTabColumnFactory}.
 *
 * @author qingwei
 * @author ntoro
 * @since 10/02/13
 */
public class SMHLineParser extends MZTabHeaderLineParser {

    private static Logger logger = LoggerFactory.getLogger(SMHLineParser.class);
    private Map<Integer, String> physPositionToOrder;


    public SMHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Small_Molecule_Header), metadata);
    }

    @Override
    protected int parseColumns() throws MZTabException {
        String header;
        Integer physicalPosition;

        SmallMoleculeColumn column;
        SortedMap<String, MZTabColumn> columnMapping = factory.getColumnMapping();
        SortedMap<String, MZTabColumn> optionalMapping = factory.getOptionalColumnMapping();
        SortedMap<String, MZTabColumn> stableMapping = factory.getStableColumnMapping();

        physPositionToOrder = generateHeaderPhysPositionToOrderMap(items);

        //Iterates through the tokens in the protein header
        //It will identify the type of column and the position accordingly
        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {

            column = null;
            header = items[physicalPosition];

            if (header.equals(IDENTIFIER.getName())) {
                column = IDENTIFIER;
            } else if (header.equals(CHEMICAL_FORMULA.getName())) {
                column = CHEMICAL_FORMULA;
            } else if (header.equals(SMILES.getName())) {
                column = SMILES;
            } else if (header.equals(INCHI_KEY.getName())) {
                column = INCHI_KEY;
            } else if (header.equals(DESCRIPTION.getName())) {
                column = DESCRIPTION;
            } else if (header.equals(EXP_MASS_TO_CHARGE.getName())) {
                column = EXP_MASS_TO_CHARGE;
            } else if (header.equals(CALC_MASS_TO_CHARGE.getName())) {
                column = CALC_MASS_TO_CHARGE;
            } else if (header.equals(CHARGE.getName())) {
                column = CHARGE;
            } else if (header.equals(RETENTION_TIME.getName())) {
                column = RETENTION_TIME;
            } else if (header.equals(TAXID.getName())) {
                column = TAXID;
            } else if (header.equals(SPECIES.getName())) {
                column = SPECIES;
            } else if (header.equals(DATABASE.getName())) {
                column = DATABASE;
            } else if (header.equals(DATABASE_VERSION.getName())) {
                column = DATABASE_VERSION;
            } else if (header.equals(RELIABILITY.getName())) {
                column = RELIABILITY;
            } else if (header.equals(URI.getName())) {
                column = URI;
            } else if (header.equals(SPECTRA_REF.getName())) {
                column = SPECTRA_REF;
            } else if (header.equals(SEARCH_ENGINE.getName())) {
                column = SEARCH_ENGINE;
            } else if (header.startsWith(BEST_SEARCH_ENGINE_SCORE.getName())) {
                addBestSearchEngineScoreColumn(header, physicalPosition);
            } else if (header.startsWith(SEARCH_ENGINE_SCORE.getName())) {
                addSearchEngineScoreColumn(header, physicalPosition);
            } else if (header.equals(MODIFICATIONS.getName())) {
                column = MODIFICATIONS;
            } else if (header.contains("abundance_assay")) {
                checkAbundanceColumns(physicalPosition, physPositionToOrder.get(physicalPosition));
            } else if (header.contains("abundance_study_variable")) {
                checkAbundanceColumns(physicalPosition, physPositionToOrder.get(physicalPosition));
                // stdev_study_variable and std_error_will be process inside
            } else if (header.contains("abundance_stdev") || header.contains("abundance_std_error")) {
                // ignore then, they have been process....
            } else if (header.startsWith("opt_")) {
                checkOptColumnName(header);
            } else {
                throw new MZTabException(new MZTabError(LogicalErrorType.ColumnNotValid,lineNumber,header,section.getName()));
            }

            if (column != null) {
                if (!column.getOrder().equals(physPositionToOrder.get(physicalPosition))) {
                    column.setOrder(physPositionToOrder.get(physicalPosition));
                    logger.debug(column.toString());
                }
                if(column.isOptional()){
                    optionalMapping.put(column.getLogicPosition(), column);
                } else {
                    stableMapping.put(column.getLogicPosition(), column);
                }
                columnMapping.put(column.getLogicPosition(), column);
            }
        }
        return physicalPosition;
    }

    private Map<Integer, String> generateHeaderPhysPositionToOrderMap(String[] items) {
        Integer physicalPosition;
        Map<Integer, String> physicalPositionToOrder = new LinkedHashMap<Integer, String>();
        int order = 0;
        boolean firstBSES = true; //BEST_SEARCH_ENGINE_SCORE
        boolean firstSES = true;  //SEARCH_ENGINE_SCORE
        String columnHeader;

        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {

            columnHeader = items[physicalPosition];

            if (columnHeader.startsWith(BEST_SEARCH_ENGINE_SCORE.getName())) {
                //We assume that columns which start with the same name they are contiguous in the table
                if (firstBSES) {
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(++order));
                    firstBSES = false;
                } else {
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(order));
                }
            } else if (columnHeader.startsWith(SEARCH_ENGINE_SCORE.getName())) {
                if (firstSES) {
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(++order));
                    firstSES = false;
                } else {
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(order));
                }
            } else {
                physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(++order));
            }
        }
        return physicalPositionToOrder;
    }

    private void addBestSearchEngineScoreColumn(String header, Integer physicalPosition) throws MZTabException {
        Pattern pattern = Pattern.compile("best_search_engine_score\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(header);
        SmallMoleculeColumn column;
        int id;

        if (matcher.find()) {
            id = parseIndex(header, matcher.group(1));
            if (!metadata.getSmallMoleculeSearchEngineScoreMap().containsKey(id)) {
                throw new MZTabException(new MZTabError(LogicalErrorType.SmallMoleculeSearchEngineScoreNotDefined, lineNumber, header));
            } else {
                column = BEST_SEARCH_ENGINE_SCORE;
                column.setOrder(physPositionToOrder.get(physicalPosition));
                factory.addBestSearchEngineScoreOptionalColumn(column, id);
            }
        }
    }

    private void addSearchEngineScoreColumn(String header, Integer physicalPosition) throws MZTabException {
        Pattern pattern = Pattern.compile("search_engine_score\\[(\\d+)\\]_ms_run\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(header);
        SmallMoleculeColumn column;

        if (matcher.find()) {
            Integer score_id = parseIndex(header, matcher.group(1));
            Integer ms_run_id = parseIndex(header, matcher.group(2));
            MsRun msRun = metadata.getMsRunMap().get(ms_run_id);

            if (msRun == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.MsRunNotDefined, lineNumber, header));
            } else if (!metadata.getSmallMoleculeSearchEngineScoreMap().containsKey(score_id)) {
                throw new MZTabException(new MZTabError(LogicalErrorType.SmallMoleculeSearchEngineScoreNotDefined, lineNumber, header + "_ms_run[" + msRun.getId() + "]"));
            } else {
                column = SEARCH_ENGINE_SCORE;
                column.setOrder(physPositionToOrder.get(physicalPosition));
                factory.addSearchEngineScoreOptionalColumn(column, score_id, msRun);
            }
        }
    }

    /**
     * In "Quantification" file, following optional columns are mandatory:
     * 1. smallmolecule_abundance_study_variable[1-n]
     * 2. smallmolecule_abundance_stdev_study_variable[1-n]
     * 3. smallmolecule_abundance_std_error_study_variable[1-n]
     * <p/>
     * Beside above, in "Complete" and "Quantification" file, following optional columns also mandatory:
     * 1. search_engine_score_ms_run[1-n]
     * <p/>
     * NOTICE: this hock method will be called at end of parse() function.
     *
     * @see MZTabHeaderLineParser#parse(int, String, MZTabErrorList)
     * @see #refineOptionalColumn(MZTabDescription.Mode, MZTabDescription.Type, String)
     */
    @Override
    protected void refine() throws MZTabException {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //mandatory columns
        List<String> mandatoryColumnHeaders = new ArrayList<String>();
        mandatoryColumnHeaders.add(IDENTIFIER.getName());
        mandatoryColumnHeaders.add(CHEMICAL_FORMULA.getName());
        mandatoryColumnHeaders.add(SMILES.getName());
        mandatoryColumnHeaders.add(INCHI_KEY.getName());
        mandatoryColumnHeaders.add(DESCRIPTION.getName());
        mandatoryColumnHeaders.add(EXP_MASS_TO_CHARGE.getName());
        mandatoryColumnHeaders.add(CALC_MASS_TO_CHARGE.getName());
        mandatoryColumnHeaders.add(CHARGE.getName());
        mandatoryColumnHeaders.add(RETENTION_TIME.getName());
        mandatoryColumnHeaders.add(TAXID.getName());
        mandatoryColumnHeaders.add(SPECIES.getName());
        mandatoryColumnHeaders.add(DATABASE.getName());
        mandatoryColumnHeaders.add(DATABASE_VERSION.getName());
        mandatoryColumnHeaders.add(SPECTRA_REF.getName());
        mandatoryColumnHeaders.add(SEARCH_ENGINE.getName());
        mandatoryColumnHeaders.add(MODIFICATIONS.getName());

        for (String columnHeader : mandatoryColumnHeaders) {
            if (factory.findColumnByHeader(columnHeader) == null) {
                throw new MZTabException(new MZTabError(FormatErrorType.StableColumn, lineNumber, columnHeader));
            }
        }

        //smallmolecule_search_engine_score
        if (metadata.getSmallMoleculeSearchEngineScoreMap().size() == 0) {
            throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "smallmolecule_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getSmallMoleculeSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, "best_search_engine_score" + searchEngineScoreLabel);
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getSmallMoleculeQuantificationUnit() == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "smallmolecule-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, "smallmolecule_abundance" + svLabel);
                refineOptionalColumn(mode, type, "smallmolecule_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, "smallmolecule_abundance_std_error" + svLabel);
            }
            for (Assay assay : metadata.getAssayMap().values()) {
                String assayLabel = "_assay[" + assay.getId() + "]";
                refineOptionalColumn(mode, type, "smallmolecule_abundance" + assayLabel);
            }

            if (mode == MZTabDescription.Mode.Complete) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    for (SearchEngineScore searchEngineScore : metadata.getSmallMoleculeSearchEngineScoreMap().values()) {
                        String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                        refineOptionalColumn(mode, type, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                    }
                }
            }
        }
    }
}
