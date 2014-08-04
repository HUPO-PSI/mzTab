package uk.ac.ebi.pride.jmztab.checker.section;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.errors.LogicalErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabError;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.checker.MZTabIntegrityChecker;

import java.util.SortedMap;

/**
 * Check if mandatory information is missing in the metadata section depending on the type and mode mzTab file.
 * Detail information see specification 5.5

 * @author ntoro
 * @since 25/07/2014 16:07
 */
public class MetadataIntegrityChecker implements MZTabIntegrityChecker {

    private Metadata metadata;
    private MZTabErrorList errorList;

    public MetadataIntegrityChecker(Metadata metadata) {
        this(metadata, null);
    }

    public MetadataIntegrityChecker(Metadata metadata, MZTabErrorList errorList) {
        if(metadata == null){
            throw new IllegalArgumentException("Metadata metadata can not be null");
        }

        if(errorList == null){
            this.errorList = new MZTabErrorList();
        }

        this.errorList = errorList;
        this.metadata = metadata;
    }

    @Override
    public MZTabErrorList getErrorList() {
        return errorList;
    }

    public void setErrorList(MZTabErrorList errorList) {
        this.errorList = errorList;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public void check() {

        MZTabDescription.Mode mode = metadata.getMZTabMode();
        MZTabDescription.Type type = metadata.getMZTabType();

        SortedMap<Integer, StudyVariable> svMap = metadata.getStudyVariableMap();
        SortedMap<Integer, Assay> assayMap = metadata.getAssayMap();
        SortedMap<Integer, MsRun> runMap = metadata.getMsRunMap();

        // TODO: Check if we have more than one metadata entry with the same index.
        // For now is complicated because is the parser which take care of checking the position.

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

        if (type == MZTabDescription.Type.Quantification) {   //Complete and summary
            for (Integer id : svMap.keySet()) {
                if (svMap.get(id).getDescription() == null) {
                    errorList.add(new MZTabError(LogicalErrorType.NotDefineInMetadata, -1, "study_variable[" + id + "]-description", mode.toString(), type.toString()));
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

        //If ms_run[1-n]-hash is present,  ms_run[1-n]-hash_method SHOULD also be present
        for (MsRun msRun : metadata.getMsRunMap().values()) {
            if (msRun.getHash() != null && msRun.getHashMethod() == null)  {
                errorList.add(new MZTabError(LogicalErrorType.MsRunHashMethodNotDefined, -1, msRun.getId().toString()));
            }
        }
    }
}
