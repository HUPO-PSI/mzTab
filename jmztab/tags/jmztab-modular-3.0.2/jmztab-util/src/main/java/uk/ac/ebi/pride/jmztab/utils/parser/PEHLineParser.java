package uk.ac.ebi.pride.jmztab.utils.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.PeptideColumn.*;

/**
 * Parse and validate Peptide header line into a {@link MZTabColumnFactory}.
 *
 * @author qingwei
 * @author ntoro
 * @since 10/02/13
 */
public class PEHLineParser extends MZTabHeaderLineParser {

    private static Logger logger = LoggerFactory.getLogger(PEHLineParser.class);
    private Map<Integer, String> physPositionToOrder;


    public PEHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Peptide_Header), metadata);
    }

    @Override
    protected int parseColumns() throws MZTabException {

        String header;
        Integer physicalPosition;

        PeptideColumn column;

        SortedMap<String, MZTabColumn> columnMapping = factory.getColumnMapping();
        SortedMap<String, MZTabColumn> optionalMapping = factory.getOptionalColumnMapping();
        SortedMap<String, MZTabColumn> stableMapping = factory.getStableColumnMapping();

        physPositionToOrder = generateHeaderPhysPositionToOrderMap(items);

        //Iterates through the tokens in the protein header
        //It will identify the type of column and the position accordingly
        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {

            column = null;
            header = items[physicalPosition];

            if (header.equals(SEQUENCE.getName())) {
                column = SEQUENCE;
            } else if (header.equals(ACCESSION.getName())) {
                column = ACCESSION;
            } else if (header.equals(UNIQUE.getName())) {
                column = UNIQUE;
            } else if (header.equals(DATABASE.getName())) {
                column = DATABASE;
            } else if (header.equals(DATABASE_VERSION.getName())) {
                column = DATABASE_VERSION;
            } else if (header.equals(SEARCH_ENGINE.getName())) {
                column = SEARCH_ENGINE;
            } else if (header.startsWith(BEST_SEARCH_ENGINE_SCORE.getName())) {
                addBestSearchEngineScoreColumn(header, physicalPosition);
            } else if (header.startsWith(SEARCH_ENGINE_SCORE.getName())) {
                addSearchEngineScoreColumn(header, physicalPosition);
            } else if (header.equals(RELIABILITY.getName())) {
                column = RELIABILITY;
            } else if (header.equals(MODIFICATIONS.getName())) {
                column = MODIFICATIONS;
            } else if (header.equals(RETENTION_TIME.getName())) {
                column = RETENTION_TIME;
            } else if (header.equals(RETENTION_TIME_WINDOW.getName())) {
                column = RETENTION_TIME_WINDOW;
            } else if (header.equals(CHARGE.getName())) {
                column = CHARGE;
            } else if (header.equals(MASS_TO_CHARGE.getName())) {
                column = MASS_TO_CHARGE;
            } else if (header.equals(URI.getName())) {
                column = URI;
            } else if (header.equals(SPECTRA_REF.getName())) {
                column = SPECTRA_REF;
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
        PeptideColumn column;
        int id;

        if (matcher.find()) {
            id = parseIndex(header, matcher.group(1));
            if (!metadata.getPeptideSearchEngineScoreMap().containsKey(id)) {
                throw new MZTabException(new MZTabError(LogicalErrorType.PeptideSearchEngineScoreNotDefined, lineNumber, header));
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
        PeptideColumn column;

        if (matcher.find()) {
            Integer score_id = parseIndex(header, matcher.group(1));
            Integer ms_run_id = parseIndex(header, matcher.group(2));
            MsRun msRun = metadata.getMsRunMap().get(ms_run_id);

            if (msRun == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.MsRunNotDefined, lineNumber, header));
            } else if (!metadata.getPeptideSearchEngineScoreMap().containsKey(score_id)) {
                throw new MZTabException(new MZTabError(LogicalErrorType.PeptideSearchEngineScoreNotDefined, lineNumber, header + "_ms_run[" + msRun.getId() + "]"));
            } else {
                column = SEARCH_ENGINE_SCORE;
                column.setOrder(physPositionToOrder.get(physicalPosition));
                factory.addSearchEngineScoreOptionalColumn(column, score_id, msRun);
            }
        }
    }

    /**
     * In "Quantification" file, following optional columns are mandatory:
     * 1. peptide_abundance_study_variable[1-n]
     * 2. peptide_abundance_stdev_study_variable[1-n]
     * 3. peptide_abundance_std_error_study_variable[1-n]
     * 4. best_search_engine_score[1-n]
     * <p/>
     * Beside above, in "Complete" and "Quantification" file, following optional columns are also mandatory:
     * 1. search_engine_score[1-n]_ms_run[1-n]
     * 2. peptide_abundance_assay[1-n]
     * 3. spectra_ref             // This is special, currently all "Quantification" file's peptide line header
     * // should provide, because it is difficult to judge MS2 based quantification employed.
     * <p/>
     * NOTICE: this method will be called at end of parse() function.
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
        mandatoryColumnHeaders.add(SEQUENCE.getName());
        mandatoryColumnHeaders.add(ACCESSION.getName());
        mandatoryColumnHeaders.add(UNIQUE.getName());
        mandatoryColumnHeaders.add(DATABASE.getName());
        mandatoryColumnHeaders.add(DATABASE_VERSION.getName());
        mandatoryColumnHeaders.add(SEARCH_ENGINE.getName());
        mandatoryColumnHeaders.add(MODIFICATIONS.getName());
        mandatoryColumnHeaders.add(RETENTION_TIME.getName());
        mandatoryColumnHeaders.add(RETENTION_TIME_WINDOW.getName());
        mandatoryColumnHeaders.add(CHARGE.getName());
        mandatoryColumnHeaders.add(MASS_TO_CHARGE.getName());

        for (String columnHeader : mandatoryColumnHeaders) {
            if (factory.findColumnByHeader(columnHeader) == null) {
                throw new MZTabException(new MZTabError(FormatErrorType.StableColumn, lineNumber, columnHeader));
            }
        }

        //peptide_search_engine_score
        if (metadata.getPeptideSearchEngineScoreMap().size() == 0) {
            throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "peptide_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getPeptideQuantificationUnit() == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "peptide-quantification_unit", mode.toString(), type.toString()));
            }
            for (SearchEngineScore searchEngineScore : metadata.getPeptideSearchEngineScoreMap().values()) {
                String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                refineOptionalColumn(mode, type, "best_search_engine_score" + searchEngineScoreLabel);
            }

            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, "peptide_abundance" + svLabel);
                refineOptionalColumn(mode, type, "peptide_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, "peptide_abundance_std_error" + svLabel);
            }
            if (mode == MZTabDescription.Mode.Complete) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    for (SearchEngineScore searchEngineScore : metadata.getPeptideSearchEngineScoreMap().values()) {
                        String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                        refineOptionalColumn(mode, type, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                    }
                }
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, "peptide_abundance" + assayLabel);
                }
            }
        }
    }
}
