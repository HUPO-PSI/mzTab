package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.TAB;

/**
 * Reporting replicates within experimental designs.
 *
 * Modeling the correct reporting of technical/biological replicates within experimental designs
 * is inherently complex. mzQuantML supports the detailed reporting of such results in respect to
 * quantitative data. mzTab is designed to be a simple data format. Therefore, the reporting of
 * results from such experimental designs is poorly supported in mzTab.
 *
 * A UNIT in an mzTab file can be any entity in which a protein is unambiguously identified by
 * its accession (see below). For instance, a UNIT can be the overall result of an experiment
 * after the data from the corresponding technical and/or biological replicates were processed.
 *
 * However, technical replicates MAY also be reported in a single mzTab file as separate UNITs.
 * When reporting technical replicates, for example for an experiment “EXP_1”, the replicates
 * MUST have the UNIT_IDs “EXP_1-rep[1-n]”.
 *
 * User: Qingwei
 * Date: 07/02/13
 */
public class ReplicateUnit extends Unit {
    public final static String REP = "rep";

    private int repId;
    private String comment;

    public ReplicateUnit(String unitId, int repId) {
        super(unitId);

        if (repId < 1) {
            throw new IllegalArgumentException("Replicate ID should great than 0!");
        }
        this.repId = repId;
    }

    /**
     * @return {Unit_id}-rep[repId]
     */
    @Override
    public String getIdentifier() {
        StringBuilder sb = new StringBuilder();

        sb.append(getUnitId()).append(MZTabConstants.MINUS).append(REP);
        sb.append("[").append(repId).append("]");

        return sb.toString();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(Section.Metadata.getPrefix()).append(MZTabConstants.TAB).append(getIdentifier());
        if (comment != null) {
            sb.append(MZTabConstants.TAB).append(comment);
        }
        sb.append(MZTabConstants.NEW_LINE);

        /**
         * replicateUnit can set the same values with Unit.
         */
        sb.append(super.toString());


        return sb.toString();
    }
}
