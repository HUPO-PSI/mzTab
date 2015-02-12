package uk.ac.ebi.pride.jmztab.utils.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.PSMColumn.*;

/**
 * Parse and validate PSM header line into a {@link MZTabColumnFactory}.
 *
 * @author qingwei
 * @author ntoro
 * @since 05/06/13
 */
public class PSHLineParser extends MZTabHeaderLineParser {

    private static Logger logger = LoggerFactory.getLogger(PSHLineParser.class);
    private Map<Integer, String> physPositionToOrder;

    public PSHLineParser(Metadata metadata) {
        super(MZTabColumnFactory.getInstance(Section.PSM_Header), metadata);
    }

    @Override
    protected int parseColumns() throws MZTabException {
        String header;
        Integer physicalPosition;
        PSMColumn column;

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
            } else if (header.equals(PSM_ID.getName())) {
                column = PSM_ID;
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
            } else if (header.startsWith(SEARCH_ENGINE_SCORE.getName())) {
                addSearchEngineScoreColumn(header, physicalPosition);
            } else if (header.equals(RELIABILITY.getName())) {
                column = RELIABILITY;
            } else if (header.equals(MODIFICATIONS.getName())) {
                column = MODIFICATIONS;
            } else if (header.equals(RETENTION_TIME.getName())) {
                column = RETENTION_TIME;
            } else if (header.equals(CHARGE.getName())) {
                column = CHARGE;
            } else if (header.equals(EXP_MASS_TO_CHARGE.getName())) {
                column = EXP_MASS_TO_CHARGE;
            } else if (header.equals(CALC_MASS_TO_CHARGE.getName())) {
                column = CALC_MASS_TO_CHARGE;
            } else if (header.equals(URI.getName())) {
                column = URI;
            } else if (header.equals(SPECTRA_REF.getName())) {
                column = SPECTRA_REF;
            } else if (header.equals(PRE.getName())) {
                column = PRE;
            } else if (header.equals(POST.getName())) {
                column = POST;
            } else if (header.equals(START.getName())) {
                column = START;
            } else if (header.equals(END.getName())) {
                column = END;
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


    private void addSearchEngineScoreColumn(String searchEngineHeader, Integer physicalPosition) throws MZTabException {
        Pattern pattern = Pattern.compile("search_engine_score\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(searchEngineHeader);
        PSMColumn column;

        if (matcher.find()) {
            Integer id = parseIndex(searchEngineHeader, matcher.group(1));

            if (!metadata.getPsmSearchEngineScoreMap().containsKey(id)) {
                throw new MZTabException(new MZTabError(LogicalErrorType.PSMSearchEngineScoreNotDefined, lineNumber, searchEngineHeader));
            } else {
                column = SEARCH_ENGINE_SCORE;
                column.setOrder(physPositionToOrder.get(physicalPosition));
                factory.addSearchEngineScoreOptionalColumn(column, id, null);
            }
        }
    }

    private Map<Integer, String> generateHeaderPhysPositionToOrderMap(String[] items) {
        Integer physicalPosition;
        Map<Integer, String> physicalPositionToOrder = new LinkedHashMap<Integer, String>();
        int order = 0;
        boolean firstSES = true;  //SEARCH_ENGINE_SCORE
        String columnHeader;

        for (physicalPosition = 1; physicalPosition < items.length; physicalPosition++) {

            columnHeader = items[physicalPosition];

            if (columnHeader.startsWith(SEARCH_ENGINE_SCORE.getName())) {
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

    /**
     * In all the modes following optional columns are mandatory:
     * 1. search_engine_score[1-n]
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
        mandatoryColumnHeaders.add(PSM_ID.getName());
        mandatoryColumnHeaders.add(ACCESSION.getName());
        mandatoryColumnHeaders.add(UNIQUE.getName());
        mandatoryColumnHeaders.add(DATABASE.getName());
        mandatoryColumnHeaders.add(DATABASE_VERSION.getName());
        mandatoryColumnHeaders.add(SEARCH_ENGINE.getName());
        mandatoryColumnHeaders.add(MODIFICATIONS.getName());
        mandatoryColumnHeaders.add(SPECTRA_REF.getName());
        mandatoryColumnHeaders.add(RETENTION_TIME.getName());
        mandatoryColumnHeaders.add(CHARGE.getName());
        mandatoryColumnHeaders.add(EXP_MASS_TO_CHARGE.getName());
        mandatoryColumnHeaders.add(CALC_MASS_TO_CHARGE.getName());
        mandatoryColumnHeaders.add(PRE.getName());
        mandatoryColumnHeaders.add(POST.getName());
        mandatoryColumnHeaders.add(START.getName());
        mandatoryColumnHeaders.add(END.getName());

        for (String columnHeader : mandatoryColumnHeaders) {
            if (factory.findColumnByHeader(columnHeader) == null) {
                throw new MZTabException(new MZTabError(FormatErrorType.StableColumn, lineNumber, columnHeader));
            }
        }

        //psm_search_engine_score
        if (metadata.getPsmSearchEngineScoreMap().size() == 0) {
            throw new MZTabException(new MZTabError(LogicalErrorType.NotDefineInMetadata, lineNumber, "psm_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getPsmSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, "search_engine_score" + searchEngineScoreLabel);
        }

    }
}
