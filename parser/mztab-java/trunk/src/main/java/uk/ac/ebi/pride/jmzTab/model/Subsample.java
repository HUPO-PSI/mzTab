package uk.ac.ebi.pride.jmzTab.model;

import uk.ac.ebi.pride.jmzTab.MzTabFile;
import uk.ac.ebi.pride.jmzTab.MzTabParsingException;

import java.util.List;

public class Subsample {
    /**
     * The 1-based index of the subsample in
     * the file.
     */
    private int subsampleIndex;
    /**
     * The subsample's unit id (??? really needed)
     */
    private String unitId;

    private List<Param> species;
    private List<Param> tissue;
    private List<Param> cellType;
    private List<Param> disease;

    private String description;
    private Param quantificationReagent;
    private List<Param> customParams;

    /**
     * Creates a new subsample.
     *
     * @param unitId         The parent unit's id.
     * @param subsampleIndex The subsample's 1-based index.
     * @throws MzTabParsingException 
     */
    public Subsample(String unitId, int subsampleIndex) throws MzTabParsingException {
    	TableObject.checkUnitId(unitId);
    	
        this.unitId = unitId;
        this.subsampleIndex = subsampleIndex;
    }

    public int getSubsampleIndex() {
        return subsampleIndex;
    }

    public void setSubsampleIndex(int subsampleIndex) {
        this.subsampleIndex = subsampleIndex;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) throws MzTabParsingException {
    	TableObject.checkUnitId(unitId);
        this.unitId = unitId;
    }

    public List<Param> getSpecies() {
        return species;
    }

    public void setSpecies(List<Param> species) {
        this.species = species;
    }

    public List<Param> getTissue() {
        return tissue;
    }

    public void setTissue(List<Param> tissue) {
        this.tissue = tissue;
    }

    public List<Param> getDisease() {
        return disease;
    }

    public void setDisease(List<Param> disease) {
        this.disease = disease;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws MzTabParsingException {
    	TableObject.checkStringValue(description);
        this.description = description;
    }

    public Param getQuantificationReagent() {
        return quantificationReagent;
    }

    public void setQuantificationReagent(Param quantitationReagent) {
        this.quantificationReagent = quantitationReagent;
    }

    public List<Param> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(List<Param> customParams) {
        this.customParams = customParams;
    }

    public List<Param> getCellType() {
        return cellType;
    }

    public void setCellType(List<Param> cellType) {
        this.cellType = cellType;
    }

    public String toMzTab() {
        String mzTab = "";

        // species
        if (species != null) {
            for (int i = 1; i <= species.size(); i++)
                mzTab += createField(String.format("species[%d]", i), species.get(i - 1));
        }
        // tissue
        if (tissue != null) {
            for (int i = 1; i <= tissue.size(); i++)
                mzTab += createField(String.format("tissue[%d]", i), tissue.get(i - 1));
        }
        // cell_type
        if (cellType != null) {
            for (int i = 1; i <= cellType.size(); i++)
                mzTab += createField(String.format("cell_type[%d]", i), cellType.get(i - 1));
        }
        // disease
        if (disease != null) {
            for (int i = 1; i <= disease.size(); i++)
                mzTab += createField(String.format("disease[%d]", i), disease.get(i - 1));
        }
        // description
        if (description != null)
            mzTab += createField("description", description);
        // quant reagent
        if (quantificationReagent != null)
            mzTab += createField("quantification_reagent", quantificationReagent);
        // custom
        if (customParams != null) {
            for (Param p : customParams)
                mzTab += createField("custom", p);
        }

        return mzTab;
    }

    private String createField(String fieldName, Object value) {
        return LineType.METADATA.getPrefix() + MzTabFile.SEPARATOR + unitId + "-sub[" + subsampleIndex + "]-" + fieldName + MzTabFile.SEPARATOR + value.toString() + MzTabFile.EOL;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((cellType == null) ? 0 : cellType.hashCode());
        result = prime * result
                + ((customParams == null) ? 0 : customParams.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((disease == null) ? 0 : disease.hashCode());
        result = prime
                * result
                + ((quantificationReagent == null) ? 0 : quantificationReagent
                .hashCode());
        result = prime * result + ((species == null) ? 0 : species.hashCode());
        result = prime * result + subsampleIndex;
        result = prime * result + ((tissue == null) ? 0 : tissue.hashCode());
        result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Subsample other = (Subsample) obj;
        if (cellType == null) {
            if (other.cellType != null)
                return false;
        } else if (!cellType.equals(other.cellType))
            return false;
        if (customParams == null) {
            if (other.customParams != null)
                return false;
        } else if (!customParams.equals(other.customParams))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (disease == null) {
            if (other.disease != null)
                return false;
        } else if (!disease.equals(other.disease))
            return false;
        if (quantificationReagent == null) {
            if (other.quantificationReagent != null)
                return false;
        } else if (!quantificationReagent.equals(other.quantificationReagent))
            return false;
        if (species == null) {
            if (other.species != null)
                return false;
        } else if (!species.equals(other.species))
            return false;
        if (subsampleIndex != other.subsampleIndex)
            return false;
        if (tissue == null) {
            if (other.tissue != null)
                return false;
        } else if (!tissue.equals(other.tissue))
            return false;
        if (unitId == null) {
            if (other.unitId != null)
                return false;
        } else if (!unitId.equals(other.unitId))
            return false;
        return true;
    }
}
