package uk.ac.ebi.pride.jmztab.prideq;

import uk.ac.ebi.pride.jmztab.model.*;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

/**
 * User: qingwei
 * Date: 21/12/12
 */
public class PrideqExportRun {
    private enum ScoreType {
        mascot, xtandem_hyper, xtandem_expectancy, sequest_score, x_correslation, delta_cn,
        spectrum_mill_peptide, omssa_e, omssa_p
    }

    private void addSearchEngineScore(Peptide peptide, ScoreType type, String score) {
        switch (type) {
            case mascot:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1001207", "Mascot", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS", "MS:1001171", "Mascot:score", score));
                break;
            case xtandem_hyper:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1001476", "X!Tandem", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS", "MS:1001331", "X!Tandem:hyperscore", score));
                break;
            case xtandem_expectancy:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1001476", "X!Tandem", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS", "MS:1001330", "X!Tandem:expect", score));
                break;
            case sequest_score:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1001208", "SEQUEST", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS", "MS:1001163", "Sequest:consensus score", score));
                break;
            case x_correslation:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1001208", "SEQUEST", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS","MS:1001155","Sequest:xcorr", score));
                break;
            case delta_cn:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1001208", "SEQUEST", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS","MS:1001156","Sequest:deltacn", score));
                break;
            case spectrum_mill_peptide:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1000687", "Spectrum Mill for MassHunter Workstation", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS", "MS:1001572", "SpectrumMill:Score", score));
                break;
            case omssa_e:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1001475", "OMSSA", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS","MS:1001328", "OMSSA:evalue", score));
                break;
            case omssa_p:
                peptide.addSearchEngineParam(new CVParam("MS", "MS:1001475", "OMSSA", null));
                peptide.addSearchEngineSocreParam(new CVParam("MS","MS:1001329", "OMSSA:pvalue", score));
                break;
        }
    }

    public MZTabFile createMZTabFile(String species, String accession) throws Exception {
        Metadata metadata = new Metadata();
        Unit unit = new Unit(species);
        unit.setDescription("export from pride-q database");
        unit.addCustom(new UserParam("date of export", new Date().toString()));
        metadata.addUnit(unit);

        MZTabFile tabFile = new MZTabFile(metadata);
        MZTabColumnFactory peptideColumnFactory = MZTabColumnFactory.getInstance(Section.Peptide);
        peptideColumnFactory.addOptionalColumn("pride_peptide_score", Double.class);
        peptideColumnFactory.addOptionalColumn("pride_cluster_value", Integer.class);
        peptideColumnFactory.addOptionalColumn("pride_experiment_accession", String.class);
        tabFile.setPeptideColumnFactory(peptideColumnFactory);

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
                "       p.psms_score_OMSSA_P_VALUE\n" +
                "  FROM prideq_07_human.prideq_psms p, prideq_07_human.prideq_mods m\n" +
                "  WHERE p.psms_id = m.mods_psms_id\n" +
                "    AND p.psms_accession = ?" +
                "  GROUP BY psms_id";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, accession);
        ResultSet result = statement.executeQuery();

        Peptide peptide;
        int lastStablePosition;
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
            lastStablePosition = peptideColumnFactory.getStableColumnMapping().lastKey();
            peptide = new Peptide(peptideColumnFactory);
            peptide.setUnitId(unit.getUnitId());
            peptide.setSequence(result.getString(1));
            peptide.setModifications(result.getString(2));
            peptide.setMassToCharge(result.getDouble(3));
            peptide.setCharge(result.getInt(4));
            peptide.addValue(++lastStablePosition, result.getDouble(5));
            peptide.addValue(++lastStablePosition, result.getInt(6));
            peptide.addValue(++lastStablePosition, result.getString(7));
            peptide.setReliability(result.getString(8));

            mascotScore = result.getString(9);
            if (mascotScore != null) {
                addSearchEngineScore(peptide, ScoreType.mascot, mascotScore);
            }

            xtandemHyperScore = result.getString(10);
            if (xtandemHyperScore != null) {
                addSearchEngineScore(peptide, ScoreType.xtandem_hyper, xtandemHyperScore);
            }

            xtandemExpScore = result.getString(11);
            if (xtandemExpScore != null) {
                addSearchEngineScore(peptide, ScoreType.xtandem_expectancy, xtandemExpScore);
            }

            sequestScore = result.getString(12);
            if (sequestScore != null) {
                addSearchEngineScore(peptide, ScoreType.sequest_score, sequestScore);
            }

            xCorrScore = result.getString(13);
            if (xCorrScore != null) {
                addSearchEngineScore(peptide, ScoreType.x_correslation, xCorrScore);
            }

            deltaCNScore = result.getString(14);
            if (deltaCNScore != null) {
                addSearchEngineScore(peptide, ScoreType.delta_cn, deltaCNScore);
            }

            specMillScore = result.getString(15);
            if (specMillScore != null) {
                addSearchEngineScore(peptide, ScoreType.spectrum_mill_peptide, specMillScore);
            }

            omssaEScore = result.getString(16);
            if (omssaEScore != null) {
                addSearchEngineScore(peptide, ScoreType.omssa_e, omssaEScore);
            }

            omssaPScore = result.getString(17);
            if (omssaPScore != null) {
                addSearchEngineScore(peptide, ScoreType.omssa_p, omssaPScore);
            }

            tabFile.addPeptide(peptide);
        }

        statement.close();
        conn.close();

        if (tabFile.getPeptides().size() == 0) {
            return null;
        }

        return tabFile;
    }

    private void createMZTabFile(String species, File outDir) throws Exception {
        if (! outDir.isDirectory()) {
            throw new IllegalArgumentException(outDir + " is not a directory or not exists!");
        }

        Connection conn = ConnectionFactory.getPRIDEQConnection();
        String sql = "select distinct(psms_accession) from prideq_07_human.prideq_psms";

        PreparedStatement statement = conn.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        String accession;
        while (result.next()) {
            accession = result.getString(1);
            MZTabFile tabFile = createMZTabFile(species, accession);
            if (tabFile == null) {
                continue;
            }
            File outFile = new File(outDir, "prideq_" + species + "_" + accession + ".mztab");
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
            tabFile.printMZTab(out);
            out.close();
        }

        statement.close();
        conn.close();
    }

    private void combineFiles(String species, File inDir, File outDir) throws Exception {
        File outFile = new File(outDir, "prideq_" + species + ".mztab");
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        writer.write("MTD\tmzTab-version\t1.0 rc3" + MZTabConstants.NEW_LINE);
        writer.write("MTD\thuman-description\texport from pride-q database" + MZTabConstants.NEW_LINE);
        writer.write("MTD\thuman-custom\t[, , date of export, " + new Date() + "]" + MZTabConstants.NEW_LINE);
        writer.write(MZTabConstants.NEW_LINE);
        writer.write("PEH\tsequence\taccession\tunit_id\tunique\tdatabase\tdatabase_version\tsearch_engine\tsearch_engine_score\treliability\tmodifications\tretention_time\tcharge\tmass_to_charge\turi\tspectra_ref\topt_pride_peptide_score\topt_pride_cluster_value\topt_pride_experiment_accession" + MZTabConstants.NEW_LINE);

        for (String file : inDir.list()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("PEP")) {
                    writer.write(line + MZTabConstants.NEW_LINE);
                }
            }
            reader.close();
        }

        writer.close();
    }

    public static void main(String[] args) throws Exception {
        PrideqExportRun run = new PrideqExportRun();

        File tempDir = new File("temp");
        boolean success = tempDir.list().length == 0;

        for (File subFile : tempDir.listFiles()) {
            success = subFile.delete();
        }

        if (success) {
            run.createMZTabFile("human", tempDir);
            run.combineFiles("human", tempDir, new File("testset"));
        }
    }

}
