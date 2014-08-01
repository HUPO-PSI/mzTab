package uk.ac.ebi.pride.jmztab.model;

import java.util.HashSet;
import java.util.Set;

/**
 * The reagent used to label the sample in the assay. For label-free analyses the "unlabeled sample" CV term SHOULD be used.
 * For the "light" channel in label-based experiments the appropriate CV term specifying the labelling channel should be used.
 *
 * Notice: This class will be used in the next version of jmzTab.
 *
 * @author qingwei
 * @since 17/09/13
 */
public class QuantificationReagent extends CVParam {
    private QuantificationReagent(String accession, String name, String value) {
        super("PRIDE", accession, name, value);
    }

    public static QuantificationReagent Unlabeled_sample     = new QuantificationReagent("PRIDE:0000434", "Unlabeled sample", null);

    public static QuantificationReagent ICAT_reagent         = new QuantificationReagent("PRIDE:0000344", "ICAT reagent", null);
    public static QuantificationReagent ICAT_heavy_reagent   = new QuantificationReagent("PRIDE:0000346", "ICAT heavy reagent", null);
    public static QuantificationReagent ICAT_light_reagent   = new QuantificationReagent("PRIDE:0000345", "ICAT light reagent", null);

    public static QuantificationReagent ICPL_reagent         = new QuantificationReagent("PRIDE:0000347", "ICPL reagent", null);
    public static QuantificationReagent ICPL_0_reagent       = new QuantificationReagent("PRIDE:0000348", "ICPL 0 reagent", null);
    public static QuantificationReagent ICPL_10_reagent      = new QuantificationReagent("PRIDE:0000351", "ICPL 10 reagent", null);
    public static QuantificationReagent ICPL_4_reagent       = new QuantificationReagent("PRIDE:0000349", "ICPL 4 reagent", null);
    public static QuantificationReagent ICPL_6_reagent       = new QuantificationReagent("PRIDE:0000350", "ICPL 6 reagent", null);

    public static QuantificationReagent SILAC_reagent        = new QuantificationReagent("PRIDE:0000328", "SILAC reagent", null);
    public static QuantificationReagent SILAC_heavy          = new QuantificationReagent("PRIDE:0000328", "SILAC heavy", null);
    public static QuantificationReagent SILAC_light          = new QuantificationReagent("PRIDE:0000328", "SILAC light", null);
    public static QuantificationReagent SILAC_medium         = new QuantificationReagent("PRIDE:0000328", "SILAC medium", null);


    public static QuantificationReagent TMT_reagent          = new QuantificationReagent("PRIDE:0000337", "TMT reagent", null);
    public static QuantificationReagent TMT_reagent_126      = new QuantificationReagent("PRIDE:0000285", "TMT reagent 126", null);
    public static QuantificationReagent TMT_reagent_127      = new QuantificationReagent("PRIDE:0000286", "TMT reagent 127", null);
    public static QuantificationReagent TMT_reagent_128      = new QuantificationReagent("PRIDE:0000287", "TMT reagent 128", null);
    public static QuantificationReagent TMT_reagent_129      = new QuantificationReagent("PRIDE:0000288", "TMT reagent 129", null);
    public static QuantificationReagent TMT_reagent_130      = new QuantificationReagent("PRIDE:0000289", "TMT reagent 130", null);
    public static QuantificationReagent TMT_reagent_131      = new QuantificationReagent("PRIDE:0000290", "TMT reagent 131", null);


    public static QuantificationReagent iTRAQ_reagent        = new QuantificationReagent("PRIDE:0000329", "iTRAQ reagent",     null);
    public static QuantificationReagent iTRAQ_reagent_113    = new QuantificationReagent("PRIDE:0000264", "iTRAQ reagent 113", null);
    public static QuantificationReagent iTRAQ_reagent_114    = new QuantificationReagent("PRIDE:0000114", "iTRAQ reagent 114", null);
    public static QuantificationReagent iTRAQ_reagent_115    = new QuantificationReagent("PRIDE:0000115", "iTRAQ reagent 115", null);
    public static QuantificationReagent iTRAQ_reagent_116    = new QuantificationReagent("PRIDE:0000116", "iTRAQ reagent 116", null);
    public static QuantificationReagent iTRAQ_reagent_117    = new QuantificationReagent("PRIDE:0000117", "iTRAQ reagent 117", null);
    public static QuantificationReagent iTRAQ_reagent_118    = new QuantificationReagent("PRIDE:0000256", "iTRAQ reagent 118", null);
    public static QuantificationReagent iTRAQ_reagent_119    = new QuantificationReagent("PRIDE:0000266", "iTRAQ reagent 119", null);
    public static QuantificationReagent iTRAQ_reagent_121    = new QuantificationReagent("PRIDE:0000267", "iTRAQ reagent 121", null);

    public static Set<QuantificationReagent> reagentSet = new HashSet<QuantificationReagent>();
    static {
        reagentSet.add(Unlabeled_sample);

        reagentSet.add(ICAT_reagent);
        reagentSet.add(ICAT_light_reagent);
        reagentSet.add(ICAT_heavy_reagent);

        reagentSet.add(ICPL_reagent);
        reagentSet.add(ICPL_0_reagent);
        reagentSet.add(ICPL_10_reagent);
        reagentSet.add(ICPL_4_reagent);
        reagentSet.add(ICPL_6_reagent);

        reagentSet.add(SILAC_reagent);
        reagentSet.add(SILAC_heavy);
        reagentSet.add(SILAC_light);
        reagentSet.add(SILAC_medium);

        reagentSet.add(TMT_reagent);
        reagentSet.add(TMT_reagent_126);
        reagentSet.add(TMT_reagent_127);
        reagentSet.add(TMT_reagent_128);
        reagentSet.add(TMT_reagent_129);
        reagentSet.add(TMT_reagent_130);
        reagentSet.add(TMT_reagent_131);

        reagentSet.add(iTRAQ_reagent);
        reagentSet.add(iTRAQ_reagent_113);
        reagentSet.add(iTRAQ_reagent_114);
        reagentSet.add(iTRAQ_reagent_115);
        reagentSet.add(iTRAQ_reagent_116);
        reagentSet.add(iTRAQ_reagent_117);
        reagentSet.add(iTRAQ_reagent_118);
        reagentSet.add(iTRAQ_reagent_119);
        reagentSet.add(iTRAQ_reagent_121);
    }

    public static boolean isReagent(String accession) {
        for (QuantificationReagent reagent : reagentSet) {
            if (reagent.getAccession().equals(accession)) {
                return true;
            }
        }

        return false;
    }


}
