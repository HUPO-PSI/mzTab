package uk.ac.ebi.pride.jmztab.utils;


import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertMZidentMLFile;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertProvider;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertPrideXMLFile;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.io.File;
import java.util.SortedMap;

/*
 * User: qingwei
 * Date: 17/09/13
 */

/**
 * Convert third-party data source to mzTab file, and do whole {@link MZTabFile} consistency check.
 * Currently, only PRIDE XML v2.1 has been integrated into this framework.
 *
 * @see uk.ac.ebi.pride.jmztab.MZTabInspector
 * @see uk.ac.ebi.pride.jmztab.MZTabCommandLine
 *
 */
@Deprecated
public class MZTabFileConverter {
    private MZTabErrorList errorList = new MZTabErrorList();
    private ConvertProvider convertProvider;

    public MZTabFileConverter(File inFile, MassSpecFileFormat format) {
        this(inFile, format, true);
    }

    public MZTabFileConverter(File inFile, MassSpecFileFormat format, boolean validate) {
        if (format == null) {
            throw new NullPointerException("Source file format is null");
        }

        switch (format) {
            case PRIDE:
                convertProvider = new ConvertPrideXMLFile(inFile);
                break;
            case MZIDENTML:
                convertProvider = new ConvertMZidentMLFile(inFile);
                break;
            default:
                throw new IllegalArgumentException("Can not convert " + format + " to mztab.");
        }

        if(validate)
            check(convertProvider.getMZTabFile());
        else
            convertProvider.getMZTabFile();
    }

    /**
     * Use this constructor only to check/validate the files without convert them first. The file was generated previously.
     * It will change in future versions.
     */
    public MZTabFileConverter() {
    }

    /**
     * Do whole {@link MZTabFile} consistency check.
     *
     * @see #checkMetadata(uk.ac.ebi.pride.jmztab.model.Metadata)
     * @see #checkProtein(uk.ac.ebi.pride.jmztab.model.Metadata, uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory)
     * @see #checkPeptide(uk.ac.ebi.pride.jmztab.model.Metadata, uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory)
     * @see #checkPSM(uk.ac.ebi.pride.jmztab.model.Metadata, uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory)
     * @see #checkSmallMolecule(uk.ac.ebi.pride.jmztab.model.Metadata, uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory)
     */
    public void check(MZTabFile mzTabFile) {
        Metadata metadata = mzTabFile.getMetadata();
        MZTabColumnFactory proteinFactory = mzTabFile.getProteinColumnFactory();
        MZTabColumnFactory peptideFactory = mzTabFile.getPeptideColumnFactory();
        MZTabColumnFactory psmFactory = mzTabFile.getPsmColumnFactory();
        MZTabColumnFactory smlFactory = mzTabFile.getSmallMoleculeColumnFactory();

        checkMetadata(metadata);
        checkProtein(metadata, proteinFactory);
        checkPeptide(metadata, peptideFactory);
        checkPSM(metadata, psmFactory);
        checkSmallMolecule(metadata, smlFactory);
    }

    /**
     *
     */
    private void checkMetadata(Metadata metadata) {
        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        SortedMap<Integer, StudyVariable> svMap = metadata.getStudyVariableMap();
        SortedMap<Integer, Assay> assayMap = metadata.getAssayMap();
        SortedMap<Integer, MsRun> runMap = metadata.getMsRunMap();

        if (mode == MZTabDescription.Mode.Complete) {
            if (metadata.getSoftwareMap().size() == 0) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "software[1-n]", mode.toString(), type.toString()));
            }

            if (type == MZTabDescription.Type.Quantification) {
                if (metadata.getQuantificationMethod() == null) {
                    errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "quantification_method", mode.toString(), type.toString()));
                }
                for (Integer id : assayMap.keySet()) {
                    if (assayMap.get(id).getMsRun() == null) {
                        errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "assay[" + id + "]-ms_run_ref", mode.toString(), type.toString()));
                    }
                    if (assayMap.get(id).getQuantificationReagent() == null) {
                        errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "assay[" + id + "]-quantification_reagent", mode.toString(), type.toString()));
                    }
                }
                if (svMap.size() > 0 && assayMap.size() > 0) {
                    for (Integer id : svMap.keySet()) {
                        if (svMap.get(id).getAssayMap().size() == 0) {
                            errorList.add(new MZTabError(LogicalErrorType.AssayRefs, -1, "study_variable[" + id + "]-assay_refs"));
                        }
                    }
                }
            }
        }

        // Complete and Summary should provide following information.
        // mzTab-version, mzTab-mode and mzTab-type have default values in create metadata. Not check here.
        if (metadata.getDescription() == null) {
            errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "description", mode.toString(), type.toString()));
        }
        for (Integer id : runMap.keySet()) {
            if (runMap.get(id).getLocation() == null) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "ms_run[" + id + "]-location", mode.toString(), type.toString()));
            }
        }

        //mods
        //fixed
        if (metadata.getFixedModMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "fixed_mod[1-n]", mode.toString(), type.toString()));
        }
        //variable
        if (metadata.getVariableModMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "variable_mod[1-n]", mode.toString(), type.toString()));
        }

        if (type == MZTabDescription.Type.Quantification) {
            for (Integer id : svMap.keySet()) {
                if (svMap.get(id).getDescription() == null) {
                    errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "study_variable[" + id + "]-description", mode.toString(), type.toString()));
                }
            }
        }
    }

    protected void refineOptionalColumn(MZTabDescription.Mode mode, MZTabDescription.Type type,
                                        MZTabColumnFactory factory, String columnHeader) {
        if (factory.findColumnByHeader(columnHeader) == null) {
            errorList.add(new MZTabError(LogicalErrorType.NotDefineInHeader, -1, columnHeader, mode.toString(), type.toString()));
        }
    }

    private void checkProtein(Metadata metadata, MZTabColumnFactory proteinFactory) {
        if (proteinFactory == null) {
            return;
        }

        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //We check that protein_search_engine_score is defined
        if (metadata.getProteinSearchEngineScoreMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.ProteinSearchEngineScoreNotDefined, -1, "protein_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, proteinFactory, "best_search_engine_score" + searchEngineScoreLabel);
        }

        if (mode == MZTabDescription.Mode.Complete) {

            //Mandatory for all complete (Quantification and Identification)
            for (MsRun msRun : metadata.getMsRunMap().values()) {
                String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                for (SearchEngineScore searchEngineScore : metadata.getProteinSearchEngineScoreMap().values()) {
                    String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                    refineOptionalColumn(mode, type, proteinFactory, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                }
            }

            if (type == MZTabDescription.Type.Identification) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    refineOptionalColumn(mode, type, proteinFactory, "num_psms" + msRunLabel);
                    refineOptionalColumn(mode, type, proteinFactory, "num_peptides_distinct" + msRunLabel);
                    refineOptionalColumn(mode, type, proteinFactory, "num_peptides_unique" + msRunLabel);
                }
            } else { // Quantification and Complete
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, proteinFactory, "protein_abundance" + assayLabel);
                }
            }
        }

        if (type == MZTabDescription.Type.Quantification) { //Summary and Complete
            if (metadata.getProteinQuantificationUnit() == null) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "protein-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, proteinFactory, "protein_abundance" + svLabel);
                refineOptionalColumn(mode, type, proteinFactory, "protein_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, proteinFactory, "protein_abundance_std_error" + svLabel);
            }
        }
    }

    private void checkPeptide(Metadata metadata, MZTabColumnFactory peptideFactory) {
        if (peptideFactory == null) {
            return;
        }

        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();


        //peptide_search_engine_score
        if (metadata.getPeptideSearchEngineScoreMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.PeptideSearchEngineScoreNotDefined, -1, "peptide_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getPeptideQuantificationUnit() == null) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "peptide-quantification_unit", mode.toString(), type.toString()));
            }
            for (SearchEngineScore searchEngineScore : metadata.getPeptideSearchEngineScoreMap().values()) {
                String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                refineOptionalColumn(mode, type, peptideFactory, "best_search_engine_score" + searchEngineScoreLabel);
            }

            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, peptideFactory, "peptide_abundance" + svLabel);
                refineOptionalColumn(mode, type, peptideFactory, "peptide_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, peptideFactory, "peptide_abundance_std_error" + svLabel);
            }
            if (mode == MZTabDescription.Mode.Complete) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    for (SearchEngineScore searchEngineScore : metadata.getPeptideSearchEngineScoreMap().values()) {
                        String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                        refineOptionalColumn(mode, type, peptideFactory, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                    }
                }
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, peptideFactory, "peptide_abundance" + assayLabel);
                }
            }
        }
    }

    private void checkPSM(Metadata metadata, MZTabColumnFactory psmFactory) {
        if (psmFactory == null) {
            return;
        }

        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //psm_search_engine_score
        if (metadata.getPsmSearchEngineScoreMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.PSMSearchEngineScoreNotDefined, -1, "psm_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getPsmSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, psmFactory, "search_engine_score" + searchEngineScoreLabel);
        }


    }

    private void checkSmallMolecule(Metadata metadata, MZTabColumnFactory smlFactory) {
        if (smlFactory == null) {
            return;
        }

        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        //smallmolecule_search_engine_score
        if (metadata.getSmallMoleculeSearchEngineScoreMap().size() == 0) {
            errorList.add(new MZTabError(LogicalErrorType.SmallMoleculeSearchEngineScoreNotDefined, -1, "smallmolecule_search_engine_score[1-n]", mode.toString(), type.toString()));
        }

        //Mandatory in all modes
        for (SearchEngineScore searchEngineScore : metadata.getSmallMoleculeSearchEngineScoreMap().values()) {
            String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
            refineOptionalColumn(mode, type, smlFactory, "best_search_engine_score" + searchEngineScoreLabel);
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getSmallMoleculeQuantificationUnit() == null) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "smallmolecule-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, smlFactory, "smallmolecule_abundance" + svLabel);
                refineOptionalColumn(mode, type, smlFactory, "smallmolecule_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, smlFactory, "smallmolecule_abundance_std_error" + svLabel);
            }
            for (Assay assay : metadata.getAssayMap().values()) {
                String assayLabel = "_assay[" + assay.getId() + "]";
                refineOptionalColumn(mode, type, smlFactory, "smallmolecule_abundance" + assayLabel);
            }

            if (mode == MZTabDescription.Mode.Complete) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    for (SearchEngineScore searchEngineScore : metadata.getSmallMoleculeSearchEngineScoreMap().values()) {
                        String searchEngineScoreLabel = "[" + searchEngineScore.getId() + "]";
                        refineOptionalColumn(mode, type, smlFactory, "search_engine_score" + searchEngineScoreLabel + msRunLabel);
                    }
                }
            }
        }
    }

    public MZTabFile getMZTabFile() {
        return convertProvider.getMZTabFile();
    }

    public MZTabErrorList getErrorList() {
        return errorList;
    }
}
