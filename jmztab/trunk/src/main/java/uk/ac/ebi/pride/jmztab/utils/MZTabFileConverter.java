package uk.ac.ebi.pride.jmztab.utils;


import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertFile;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertMzIndentMLFile;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertPrideXMLFile;
import uk.ac.ebi.pride.jmztab.utils.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;

import java.io.File;
import java.util.SortedMap;

/**
 * User: qingwei
 * Date: 17/09/13
 */
public class MZTabFileConverter {
    private MZTabErrorList errorList = new MZTabErrorList();
    private ConvertFile convertFile;

    public MZTabFileConverter(File inFile, String format) {
        if (format == null) {
            throw new NullPointerException("Source file format is null");
        }

        if (format.equalsIgnoreCase(ConvertFile.PRIDE)) {
            convertFile = new ConvertPrideXMLFile(inFile);
        } else if (format.equalsIgnoreCase(ConvertFile.mzIdentML)) {
            convertFile = new ConvertMzIndentMLFile(inFile);
        }

        check(convertFile.getMZTabFile());
    }

    private void check(MZTabFile mzTabFile) {
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

            if (type == MZTabDescription.Type.Quantification) {
                for (Integer id : svMap.keySet()) {
                    if (svMap.get(id).getDescription() == null) {
                        errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "study_variable[" + id + "]-description", mode.toString(), type.toString()));
                    }
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

        if (mode == MZTabDescription.Mode.Complete) {
            if (type == MZTabDescription.Type.Identification) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    refineOptionalColumn(mode, type, proteinFactory, "search_engine_score" + msRunLabel);
                    refineOptionalColumn(mode, type, proteinFactory, "num_psms" + msRunLabel);
                    refineOptionalColumn(mode, type, proteinFactory, "num_peptides_distinct" + msRunLabel);
                    refineOptionalColumn(mode, type, proteinFactory, "num_peptides_unique" + msRunLabel);
                }
            } else {
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, proteinFactory, "protein_abundance" + assayLabel);
                }
            }
        }

        if (type == MZTabDescription.Type.Quantification) {
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

        if (mode == MZTabDescription.Mode.Complete) {
            if (type == MZTabDescription.Type.Identification) {
                for (MsRun msRun : metadata.getMsRunMap().values()) {
                    String msRunLabel = "_ms_run[" + msRun.getId() + "]";
                    refineOptionalColumn(mode, type, peptideFactory, "search_engine_score" + msRunLabel);
                    refineOptionalColumn(mode, type, peptideFactory, "num_psms" + msRunLabel);
                    refineOptionalColumn(mode, type, peptideFactory, "num_peptides_distinct" + msRunLabel);
                    refineOptionalColumn(mode, type, peptideFactory, "num_peptides_unique" + msRunLabel);
                }
            } else {
                for (Assay assay : metadata.getAssayMap().values()) {
                    String assayLabel = "_assay[" + assay.getId() + "]";
                    refineOptionalColumn(mode, type, peptideFactory, "protein_abundance" + assayLabel);
                }
            }
        }

        if (type == MZTabDescription.Type.Quantification) {
            if (metadata.getProteinQuantificationUnit() == null) {
                errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "protein-quantification_unit", mode.toString(), type.toString()));
            }
            for (StudyVariable studyVariable : metadata.getStudyVariableMap().values()) {
                String svLabel = "_study_variable[" + studyVariable.getId() + "]";
                refineOptionalColumn(mode, type, peptideFactory, "protein_abundance" + svLabel);
                refineOptionalColumn(mode, type, peptideFactory, "protein_abundance_stdev" + svLabel);
                refineOptionalColumn(mode, type, peptideFactory, "protein_abundance_std_error" + svLabel);
            }
        }
    }

    private void checkPSM(Metadata metadata, MZTabColumnFactory psmFactory) {
        if (psmFactory == null) {
            return;
        }
    }

    private void checkSmallMolecule(Metadata metadata, MZTabColumnFactory smlFactory) {
        if (smlFactory == null) {
            return;
        }

        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

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
        }
    }

    public MZTabFile getMZTabFile() {
        return convertFile.getMZTabFile();
    }

    public MZTabErrorList getErrorList() {
        return errorList;
    }
}
