package uk.ac.ebi.pride.jmztab.utils.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.ProteinColumn.*;

/**
 * Parse and validate Protein header line into a {@link MZTabColumnFactory}.
 *
 * @author qingwei
 * @author ntoro
 * @since 10/02/13
 */
public class PRHLineParser extends MZTabHeaderLineParser {

    private static Logger logger = LoggerFactory.getLogger(PRHLineParser.class);
    private Map<Integer, String> physPositionToOrder;

    public PRHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.Protein_Header), metadata);
    }


    @Override
    protected int parseColumns() throws MZTabException {

        String header;
        Integer physicalPosition;

        ProteinColumn column;

        SortedMap<String, MZTabColumn> columnMapping = factory.getColumnMapping();
        SortedMap<String, MZTabColumn> optionalMapping = factory.getOptionalColumnMapping();
        SortedMap<String, MZTabColumn> stableMapping = factory.getStableColumnMapping();

        physPositionToOrder = generateHeaderPhysPositionToOrderMap(items);

        //Iterates through the tokens in the protein header
        //It will identify the type of column and the position accordingly
        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {

            column = null;
            header = items[physicalPosition];

            if (header.equals(ACCESSION.getName())) {
                column = ACCESSION;
            } else if (header.equals(DESCRIPTION.getName())) {
                column = DESCRIPTION;
            } else if (header.equals(TAXID.getName())) {
                column = TAXID;
            } else if (header.equals(SPECIES.getName())) {
                column = SPECIES;
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
            } else if (header.startsWith(NUM_PSMS.getName())) {
                addMsRunRelatedColumn(NUM_PSMS, header, physicalPosition);
            } else if (header.startsWith(NUM_PEPTIDES_DISTINCT.getName())) {
                addMsRunRelatedColumn(NUM_PEPTIDES_DISTINCT, header, physicalPosition);
            } else if (header.startsWith(NUM_PEPTIDES_UNIQUE.getName())) {
                addMsRunRelatedColumn(NUM_PEPTIDES_UNIQUE, header, physicalPosition);
            } else if (header.startsWith(NUM_PEPTIDES_UNIQUE.getName())) {
                addMsRunRelatedColumn(NUM_PEPTIDES_UNIQUE, header, physicalPosition);
            } else if (header.startsWith(AMBIGUITY_MEMBERS.getName())) {
                column = AMBIGUITY_MEMBERS;
            } else if (header.equals(MODIFICATIONS.getName())) {
                column = MODIFICATIONS;
            } else if (header.equals(URI.getName())) {
                column = URI;
            } else if (header.equals(GO_TERMS.getName())) {
                column = GO_TERMS;
            } else if (header.equals(PROTEIN_COVERAGE.getName())) {
                column = PROTEIN_COVERAGE;
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


    private void addBestSearchEngineScoreColumn(String header, Integer physicalPosition) throws MZTabException {
        Pattern pattern = Pattern.compile("best_search_engine_score\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(header);
        ProteinColumn column;
        int id;

        if (matcher.find()) {
            id = parseIndex(header, matcher.group(1));
            if (!metadata.getProteinSearchEngineScoreMap().containsKey(id)) {
                throw new MZTabException(new MZTabError(LogicalErrorType.ProteinSearchEngineScoreNotDefined, lineNumber, header));
            } else {
                column = BEST_SEARCH_ENGINE_SCORE;
                column.setOrder(physPositionToOrder.get(physicalPosition));
                factory.addBestSearchEngineScoreOptionalColumn(column, id);
            }
        }
    }

    private void addMsRunRelatedColumn(ProteinColumn column, String header, Integer physicalPosition) throws MZTabException {
        column.setOrder(physPositionToOrder.get(physicalPosition));
        factory.addOptionalColumn(column, splitMsRun(header));
    }

    private MsRun splitMsRun(String header) throws MZTabException {

        MsRun msRun = null;

        Pattern pattern = Pattern.compile("(.+)_ms_run\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(header);

        if (matcher.find()) {
            int id = parseIndex(header, matcher.group(2));
            msRun = metadata.getMsRunMap().get(id);
            if (msRun == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.MsRunNotDefined, lineNumber, header));
            }
        }

        return msRun;
    }

    private void addSearchEngineScoreColumn(String header, Integer physicalPosition) throws MZTabException {
        Pattern pattern = Pattern.compile("search_engine_score\\[(\\d+)\\]_ms_run\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(header);
        ProteinColumn column;

        if (matcher.find()) {
            Integer score_id = parseIndex(header, matcher.group(1));
            Integer ms_run_id = parseIndex(header, matcher.group(2));
            MsRun msRun = metadata.getMsRunMap().get(ms_run_id);

            if (msRun == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.MsRunNotDefined, lineNumber, header));
            } else if (!metadata.getProteinSearchEngineScoreMap().containsKey(score_id)) {
                throw new MZTabException(new MZTabError(LogicalErrorType.ProteinSearchEngineScoreNotDefined, lineNumber, header + "_ms_run[" + msRun.getId() + "]"));
            } else {
                column = SEARCH_ENGINE_SCORE;
                column.setOrder(physPositionToOrder.get(physicalPosition));
                factory.addSearchEngineScoreOptionalColumn(column, score_id, msRun);
            }
        }
    }

    private Map<Integer, String> generateHeaderPhysPositionToOrderMap(String[] items) {

        Integer physicalPosition;
        Map<Integer, String> physicalPositionToOrder = new LinkedHashMap<Integer, String>();
        int order = 0;
        boolean firstBSES = true; //BEST_SEARCH_ENGINE_SCORE
        boolean firstSES = true;  //SEARCH_ENGINE_SCORE
        boolean firstNPSM = true; //NUM_PSMS
        boolean firstNPDM = true; //NUM_PEPTIDES_DISTINCT
        boolean firstNPUM = true; //NUM_PEPTIDES_UNIQUE

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
            }
            else if (columnHeader.startsWith(SEARCH_ENGINE_SCORE.getName())){
                if(firstSES){
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(++order));
                    firstSES = false;
                }
                else {
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(order));
                }
            }
            else if (columnHeader.startsWith(NUM_PSMS.getName())){
                if(firstNPSM){
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(++order));
                    firstNPSM = false;
                }
                else {
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(order));
                }
            }
            else if (columnHeader.startsWith(NUM_PEPTIDES_DISTINCT.getName())){
                if(firstNPDM){
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(++order));
                    firstNPDM = false;
                }
                else {
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(order));
                }
            }
            else if (columnHeader.startsWith(NUM_PEPTIDES_UNIQUE.getName())){
                if(firstNPUM){
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(++order));
                    firstNPUM = false;
                }
                else {
                    physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(order));
                }
            }
            else {
                physicalPositionToOrder.put(physicalPosition, fromIndexToOrder(++order));
            }
        }
        return physicalPositionToOrder;
    }

    /**
     * In all the modes following optional columns are mandatory:
     * 1. best_search_engine_score[1-n]
     * <p/>
     * In "Quantification" file, following optional columns are mandatory:
     * 1. protein_abundance_study_variable[1-n]
     * 2. protein_abundance_stdev_study_variable[1-n]
     * 3. protein_abundance_std_error_study_variable[1-n]
     * <p/>
     * In "Complete" and "Identification" file, following optional columns are also mandatory:
     * 1. search_engine_score[1-n]_ms_run[1-n]
     * 2. num_psms_ms_run[1-n]
     * 3. num_peptides_distinct_ms_run[1-n]
     * 4. num_peptides_unique_ms_run[1-n]
     * <p/>
     * In "Complete" and "Quantification" file, following optional columns are also mandatory:
     * 1. search_engine_score[1-n]_ms_run[1-n]
     * 2. protein_abundance_assay[1-n]
     * <p/>
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
        mandatoryColumnHeaders.add(ACCESSION.getName());
        mandatoryColumnHeaders.add(DESCRIPTION.getName());
        mandatoryColumnHeaders.add(TAXID.getName());
        mandatoryColumnHeaders.add(SPECIES.getName());
        mandatoryColumnHeaders.add(DATABASE.getName());
        mandatoryColumnHeaders.add(DATABASE_VERSION.getName());
        mandatoryColumnHeaders.add(SEARCH_ENGINE.getName());
        mandatoryColumnHeaders.add(AMBIGUITY_MEMBERS.getName());
        mandatoryColumnHeaders.add(MODIFICATIONS.getName());

        for (String columnHeader : mandatoryColumnHeaders) {
            if (factory.findColumnByHeader(columnHeader) == null) {
                throw new MZTabException(new MZTabError(FormatErrorType.StableColumn, lineNumber, columnHeader));
            }
        }

        //protein_search_engine_score
        if (metadata.getProteinSearchEngineScoreMap().size() == 0) {
            throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "protein_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, "best_search_engine_score" + searchEngineScoreLabel);
        }

        if (mode == MZTabDescription.Mode.Complete) {

            //Protein Coverage is mandatory in complete
            refineOptionalColumn(mode, type, ProteinColumn.PROTEIN_COVERAGE.getHeader());

            //Mandatory for all complete (Quantification and Identification)
            for (MsRun msRun : metadata.getMsRunMap().values()) {
                String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                for (SearchEngineScore searchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
                    String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                    refineOptionalColumn(mode, type, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                }
            }

            if (type == MZTabDescription.Type.Identification) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    refineOptionalColumn(mode, type, "num_psms" + msRunLabel);
                    refineOptionalColumn(mode, type, "num_peptides_distinct" + msRunLabel);
                    refineOptionalColumn(mode, type, "num_peptides_unique" + msRunLabel);
                }
            } else { // Quantification and Complete
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, "protein_abundance" + assayLabel);
                }
            }
        }

        if (type == MZTabDescription.Type.Quantification) { //Summary and Complete
            if (metadata.getProteinQuantificationUnit() == null) {
                throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "protein-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, "protein_abundance" + svLabel);
                refineOptionalColumn(mode, type, "protein_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, "protein_abundance_std_error" + svLabel);
            }
        }
    }
}
