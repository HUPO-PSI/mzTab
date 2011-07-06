package uk.ac.ebi.tools.mztab_java.model;

import java.util.ArrayList;

import uk.ac.ebi.tools.mztab_java.MzTabFile;

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
	
	private ArrayList<Param>		species;
	private ArrayList<Param>		tissue;
	private ArrayList<Param>		cellType;
	private ArrayList<Param>		disease;
	
	private String 					description;
	private Param					quantitationReagent;
	private ArrayList<Param>		customParams;
	
	/**
	 * Creates a new subsample.
	 * @param unitId The parent unit's id.
	 * @param subsampleIndex The subsample's 1-based index.
	 */
	public Subsample(String unitId, int subsampleIndex) {
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
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public ArrayList<Param> getSpecies() {
		return species;
	}
	public void setSpecies(ArrayList<Param> species) {
		this.species = species;
	}
	public ArrayList<Param> getTissue() {
		return tissue;
	}
	public void setTissue(ArrayList<Param> tissue) {
		this.tissue = tissue;
	}
	public ArrayList<Param> getDisease() {
		return disease;
	}
	public void setDisease(ArrayList<Param> disease) {
		this.disease = disease;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Param getQuantitationReagent() {
		return quantitationReagent;
	}
	public void setQuantitationReagent(Param quantitationReagent) {
		this.quantitationReagent = quantitationReagent;
	}
	public ArrayList<Param> getCustomParams() {
		return customParams;
	}
	public void setCustomParams(ArrayList<Param> customParams) {
		this.customParams = customParams;
	}
	public ArrayList<Param> getCellType() {
		return cellType;
	}
	public void setCellType(ArrayList<Param> cellType) {
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
		if (quantitationReagent != null)
			mzTab += createField("quantitation_reagent", quantitationReagent);
		// custom
		if (customParams != null) {
			for (Param p : customParams)
				mzTab += createField("custom", p);
		}
		
		return mzTab;
	}
	
	private String createField(String fieldName, Object value) {
		return unitId + "-sub[" + subsampleIndex + "]" + MzTabFile.SEPARATOR + value.toString() + MzTabFile.EOL;
	}
}
