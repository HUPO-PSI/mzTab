package uk.ac.ebi.pride.jmztab.prideq;

import uk.ac.ebi.pride.jmztab.model.*;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * User: qingwei
 * Date: 21/12/12
 */
public class PrideqExportRun {
    private String schema = "prideq_09_human";

//    private String file_suffix = ".mztab";
    private String file_suffix = ".txt";

    private boolean filter = true;

    private enum ScoreType {
        mascot, xtandem_hyper, xtandem_expectancy, sequest_score, x_correslation, delta_cn,
        spectrum_mill_peptide, omssa_e, omssa_p
    }

    private enum Species {
        Human("human", "Homo sapiens", "9606"),
        Mouse("mouse", "Mus musculus", "10090");

        private String name;
        private String description;
        private String taxid;

        private Species(String name, String description, String taxid) {
            this.name = name;
            this.description = description;
            this.taxid = taxid;
        }

        public String getName() {
            return name;
        }

        public String getTaxid() {
            return taxid;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private void addSearchEngineScore(PSM psm, ScoreType type, String score) {
        switch (type) {
            case mascot:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1001207", "Mascot", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001171", "Mascot:score", score));
                break;
            case xtandem_hyper:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1001476", "X!Tandem", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001331", "X!Tandem:hyperscore", score));
                break;
            case xtandem_expectancy:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1001476", "X!Tandem", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001330", "X!Tandem:expect", score));
                break;
            case sequest_score:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1001208", "SEQUEST", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001163", "Sequest:consensus score", score));
                break;
            case x_correslation:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1001208", "SEQUEST", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001155", "Sequest:xcorr", score));
                break;
            case delta_cn:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1001208", "SEQUEST", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001156", "Sequest:deltacn", score));
                break;
            case spectrum_mill_peptide:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1000687", "Spectrum Mill for MassHunter Workstation", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001572", "SpectrumMill:Score", score));
                break;
            case omssa_e:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1001475", "OMSSA", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001328", "OMSSA:evalue", score));
                break;
            case omssa_p:
                psm.addSearchEngineParam(new CVParam("MS", "MS:1001475", "OMSSA", null));
                psm.addSearchEngineScoreParam(new CVParam("MS", "MS:1001329", "OMSSA:pvalue", score));
                break;
        }
    }

    public MZTabFile createMZTabFile(String accession, SplitList<String> pmidList) throws Exception {
        Metadata metadata = new Metadata();
        metadata.setDescription("export from pride-q database");
        metadata.addCustom(new UserParam("date of export", new Date().toString()));

        CVParam pride_peptide_score = new CVParam("PRIDE", "PRIDE:0000446", "PRIDE peptide score", null);
        CVParam pride_cluster_value = new CVParam("PRIDE", "PRIDE:0000445", "PRIDE-Cluster score", null);
        CVParam pride_experiment_accession = new CVParam("PRIDE", "PRIDE:0000444", "PRIDE experiment accession", null);
        CVParam pubmed_id = new CVParam("MS", "MS:1000879", "PubMed identifier", null);
        CVParam pxid = new CVParam("MS", "MS:1001919", "ProteomeXchange accession number", null);

        MZTabFile tabFile = new MZTabFile(metadata);
        MZTabColumnFactory psmColumnFactory = MZTabColumnFactory.getInstance(Section.PSM);
        psmColumnFactory.addReliabilityOptionalColumn();
        psmColumnFactory.addOptionalColumn(pride_peptide_score, Double.class);
        psmColumnFactory.addOptionalColumn(pride_cluster_value, Integer.class);
        psmColumnFactory.addOptionalColumn(pride_experiment_accession, String.class);
        psmColumnFactory.addOptionalColumn(pubmed_id, String.class);
        psmColumnFactory.addOptionalColumn(pxid, String.class);
        tabFile.setPSMColumnFactory(psmColumnFactory);

        Connection conn = ConnectionFactory.getPRIDEQConnection();

        String sql = "SELECT p.psms_sequence,\n" +
                "       group_concat(concat(m.mods_location, \"-\", m.mods_main_accession)),\n" +
                "       round(p.psms_prec_mz, 2),\n" +
                "       p.psms_prec_z,\n" +
                "       p.psms_score_peptide,\n" +
                "       p.psms_score_CLUSTER_VALUE,\n" +
                "       p.psms_accession,\n" +
                "       case p.psms_prideq_rating when 1 then '3' when 3 then '1' when 2 then '2' else 'null' end,\n" +
                "       p.psms_score_MASCOT_SCORE,\n" +
                "       p.psms_score_XTANDEM_HYPER_SCORE,\n" +
                "       p.psms_score_XTANDEM_EXPECTANCY_SCORE,\n" +
                "       p.psms_score_SEQUEST_SCORE,\n" +
                "       p.psms_score_X_CORRELATION,\n" +
                "       p.psms_score_DELTA_CN,\n" +
                "       p.psms_score_SPECTRUM_MILL_PEPTIDE_SCORE,\n" +
                "       p.psms_score_OMSSA_E_VALUE,\n" +
                "       p.psms_score_OMSSA_P_VALUE,\n" +
                "       p.psms_id\n" +
                "  FROM " + schema + ".prideq_psms p, " + schema + ".prideq_mods m\n" +
                "  WHERE p.psms_id = m.mods_psms_id\n" +
                "    AND p.psms_accession = ?" +
//                "  GROUP BY psms_id";
                "  GROUP BY psms_id limit 1, 10";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, accession);
        ResultSet result = statement.executeQuery();

        PSM psm;
        String mascotScore;
        String xtandemHyperScore;
        String xtandemExpScore;
        String sequestScore;
        String xCorrScore;
        String deltaCNScore;
        String specMillScore;
        String omssaEScore;
        String omssaPScore;
        while (result.next()) {
            psm = new PSM(psmColumnFactory, metadata);
            psm.setSequence(result.getString(1));
            psm.setPSM_ID(result.getString("psms_id"));
            psm.setAccession(accession);
            psm.setModifications(result.getString(2));
            psm.setExpMassToCharge(result.getDouble(3));
            psm.setCharge(result.getInt(4));
            psm.setOptionColumn(pride_peptide_score, result.getDouble(5));
            psm.setOptionColumn(pride_cluster_value, result.getInt(6));
            psm.setOptionColumn(pride_experiment_accession, result.getString(7));
            psm.setOptionColumn(pubmed_id, pmidList == null ? null : pmidList.toString());
            psm.setOptionColumn(pxid, null);

            psm.setReliability(result.getString(8));

            mascotScore = result.getString(9);
            if (mascotScore != null) {
                addSearchEngineScore(psm, ScoreType.mascot, mascotScore);
            }

            xtandemHyperScore = result.getString(10);
            if (xtandemHyperScore != null) {
                addSearchEngineScore(psm, ScoreType.xtandem_hyper, xtandemHyperScore);
            }

            xtandemExpScore = result.getString(11);
            if (xtandemExpScore != null) {
                addSearchEngineScore(psm, ScoreType.xtandem_expectancy, xtandemExpScore);
            }

            sequestScore = result.getString(12);
            if (sequestScore != null) {
                addSearchEngineScore(psm, ScoreType.sequest_score, sequestScore);
            }

            xCorrScore = result.getString(13);
            if (xCorrScore != null) {
                addSearchEngineScore(psm, ScoreType.x_correslation, xCorrScore);
            }

            deltaCNScore = result.getString(14);
            if (deltaCNScore != null) {
                addSearchEngineScore(psm, ScoreType.delta_cn, deltaCNScore);
            }

            specMillScore = result.getString(15);
            if (specMillScore != null) {
                addSearchEngineScore(psm, ScoreType.spectrum_mill_peptide, specMillScore);
            }

            omssaEScore = result.getString(16);
            if (omssaEScore != null) {
                addSearchEngineScore(psm, ScoreType.omssa_e, omssaEScore);
            }

            omssaPScore = result.getString(17);
            if (omssaPScore != null) {
                addSearchEngineScore(psm, ScoreType.omssa_p, omssaPScore);
            }

            tabFile.addPSM(psm);
        }

        statement.close();
        conn.close();

        if (tabFile.getPSMs().size() == 0) {
            return null;
        }

        return tabFile;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    /**
     * User need I would need an example file containing the PSMs containing
     * the experiments pointing out to references PMIDs 23043182 and 23429522.
     */
    private List<String> getLimitPMIDList() {
        List<String> pmidList = new ArrayList<String>();

        pmidList.add("23043182");
        pmidList.add("23429522");

        return pmidList;
    }

    /**
     * "Project_accession : List<PMID>
     */
    private HashMap<String, SplitList<String>> parseProjectPMIDs() throws Exception {
        Pride2Utils utils = new Pride2Utils();
//        utils.setFilterPMIDList(getLimitPMIDList());
        return utils.createProjectPMIDs();
    }

    private void createMZTabFile(Species species, File outDir) throws Exception {
        if (! outDir.isDirectory()) {
            throw new IllegalArgumentException(outDir + " is not a directory or not exists!");
        }

        Connection conn = ConnectionFactory.getPRIDEQConnection();
        String sql = "select distinct(psms_accession) from " + schema + ".prideq_psms";

        PreparedStatement statement = conn.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        String accession;
        HashMap<String, SplitList<String>> map = parseProjectPMIDs();

        while (result.next()) {
            accession = result.getString(1);
            SplitList<String> pmidList = map.get(accession);
            if (pmidList == null) {
                continue;
            }

            if (isFilter() && pmidList.isEmpty()) {
                continue;
            }
            MZTabFile tabFile = createMZTabFile(accession, pmidList);
            if (tabFile == null) {
                continue;
            }
            File outFile = new File(outDir, "prideq_" + species + "_" + accession + file_suffix);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
            tabFile.printMZTab(out);
            out.close();
        }

        statement.close();
        conn.close();
    }

    private void combineFiles(Species species, File inDir, File outDir) throws Exception {
        File outFile = new File(outDir, "prideq_" + species + file_suffix);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

        boolean firstFile = true;

        for (File file : inDir.listFiles()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (firstFile) {
                    writer.write(line + MZTabConstants.NEW_LINE);
                } else if (line.startsWith(Section.PSM.getPrefix())) {
                    writer.write(line + MZTabConstants.NEW_LINE);
                }
            }
            reader.close();
            firstFile = false;
        }

        writer.close();
    }

    public static void main(String[] args) throws Exception {
        PrideqExportRun run = new PrideqExportRun();

        long start = System.currentTimeMillis();

        File tempDir = new File("temp");
        boolean success = tempDir.list().length == 0;

        for (File subFile : tempDir.listFiles()) {
            success = subFile.delete();
        }

        if (success) {
            run.createMZTabFile(Species.Human, tempDir);
            run.combineFiles(Species.Human, tempDir, new File("testset"));
        }

        long end = System.currentTimeMillis();

        System.out.println((end - start) / 1000d);

    }

}
