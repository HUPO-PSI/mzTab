package uk.ac.ebi.pride.jmztab.converter;

import uk.ac.ebi.pride.jmztab.model.*;

/**
 * User: qingwei
 * Date: 14/11/13
 */
public class ConvertSmallMolecule extends ConvertProvider<Void, Void> {

    private Metadata mtd;
    private MZTabColumnFactory smh;   // Protein Header Column Factory

    private MsRun msRun1;
    private Assay assay1;

    private CVParam param = new CVParam("MS", "MS:1002217", "decoy peptide", null);

    public ConvertSmallMolecule() {
        super(null, null);
    }

    @Override
    protected Metadata convertMetadata() {
        MZTabDescription tabDescription = new MZTabDescription(MZTabDescription.Mode.Summary, MZTabDescription.Type.Identification);
        tabDescription.setId("PRIDE_1234");
        mtd = new Metadata(tabDescription);

        mtd.setTitle("My first test experiment");

        // create ms_run[1]
        msRun1 = new MsRun(1);
        msRun1.setFormat(new CVParam("MS", "MS:1000584", "mzML file", null));
        msRun1.setLocation(MZTabUtils.parseURL("file://C:\\path\\to\\my\\file"));
        msRun1.setIdFormat(new CVParam("MS", "MS:1001530", "mzML unique identifier", null));
        msRun1.setFragmentationMethod(new CVParam("MS", "MS:1000133", "CID", null));

        // SHOULD add ms_run[1] into metadata.
        mtd.addMsRun(msRun1);

        // Automatically generate ms_run[2] and add it into metadata.
        mtd.addMsRunFormat(2, MZTabUtils.parseParam("[MS, MS:1001062, Mascot MGF file, ]"));
        mtd.addMsRunLocation(2, MZTabUtils.parseURL("ftp://ftp.ebi.ac.uk/path/to/file"));
        mtd.addMsRunFragmentationMethod(2, MZTabUtils.parseParam("[MS, MS:1000422, HCD, ]"));

        // @NOTICE: system generate assay[1] and assay[2], and add them into metadata automatically.
        // set value for assay[2]-quantification_mod[1]
        mtd.addAssayQuantificationModParam(2, 1, MZTabUtils.parseParam("[UNIMOD, UNIMOD:188, Label:13C(6), ]"));
        // set value for assay[1]-quantification_mod[2]
        mtd.addAssayQuantificationModParam(1, 2, MZTabUtils.parseParam("[UNIMOD, UNIMOD:188, Label:13C(6), ]"));
        // set value for assay[2]-quantification_mod[1]-site
        mtd.addAssayQuantificationModSite(2, 1, "R");
        // set value for assay[1]-quantification_mod[2]-site
        mtd.addAssayQuantificationModSite(1, 2, "K");
        // set value for assay[2]-quantification_mod[1]-position
        mtd.addAssayQuantificationModPosition(2, 1, "Anywhere");
        // set value for assay[1]-quantification_mod[2]-position
        mtd.addAssayQuantificationModPosition(1, 2, "Anywhere");
        // set value for assay[1]-ms_run_ref
        mtd.addAssayMsRun(1, msRun1);

        // get assay[1] and assay[2] reference from metadata
        assay1 = mtd.getAssayMap().get(1);
        Assay assay2 = mtd.getAssayMap().get(2);
        // set value for study_variable[1]-assay_refs    assay[1], assay[2]
        mtd.addStudyVariableAssay(1, assay1);
        mtd.addStudyVariableAssay(1, assay2);

        // set value for study_variable[1]-description
        mtd.addStudyVariableDescription(1, "description Group B (spike-in 0.74 fmol/uL)");

        // generate peptide header line
        MZTabColumnFactory peptideFactory = MZTabColumnFactory.getInstance(Section.Peptide);
        // find retention_time column from factory.
        PeptideColumn peptideColumn = (PeptideColumn) peptideFactory.findColumnByHeader("retention_time");

        // NOTICE: some item in metadata SHOULD reference columns in header line factory.
        // add colunit_peptide into metadata, we use findColumnByHeader function get a column.
        mtd.addPeptideColUnit(peptideColumn, new CVParam("UO", "UO:0000031", "minute", null));
        // add colunit_psm into metadata, we reference a stable PSMColumn.
        mtd.addPSMColUnit(PSMColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));
        // add colunit_small_molecule into metadata.
        mtd.addSmallMoleculeColUnit(SmallMoleculeColumn.RETENTION_TIME, new CVParam("UO", "UO:0000031", "minute", null));

        return mtd;
    }

    @Override
    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        smh = MZTabColumnFactory.getInstance(Section.Small_Molecule);

        // add optional columns which have stable order.
        smh.addReliabilityOptionalColumn();
        smh.addURIOptionalColumn();

        // add optional columns which have stable order.
        smh.addOptionalColumn(SmallMoleculeColumn.SEARCH_ENGINE_SCORE, mtd.getMsRunMap().get(1));

        // add abundance columns which locate the end of table.
        smh.addAbundanceOptionalColumn(assay1);
        smh.addAbundanceOptionalColumn(mtd.getStudyVariableMap().get(1));
        smh.addAbundanceOptionalColumn(mtd.getAssayMap().get(2));

        // add user defined optional columns opt_ms_run[1]_my_value, the value data type is String.class
        smh.addOptionalColumn(msRun1, "my_value", String.class);

        // add a user defined CVParam optional column: opt_global_cv_1002217_decoy_peptide, the data type is String.class
        smh.addOptionalColumn(param, String.class);

        return smh;
    }

    @Override
    protected void fillData() {
        getRecord();
    }

    private SmallMolecule getRecord() {
        SmallMolecule sm = new SmallMolecule(smh, mtd);
        sm.setIdentifier("CID:00027395");
        sm.setChemicalFormula("C17H20N4O2");
        sm.setSmiles("C1=CC=C(C=C1)CCNC(=O)CCNNC(=O)C2=CC=NC=C2");
        sm.setInchiKey("QXBMEGUKVLFJAM-UHFFFAOYSA-N");
        sm.setDescription("N-(2-phenylethyl)-3-[2-(pyridine-4-carbonyl)hydrazinyl]propanamide");
        sm.setExpMassToCharge("1234.4");
        sm.setCalcMassToCharge("1234.5");
        sm.setCharge("2");
        sm.setRetentionTime("10.2|11.5");
        sm.setTaxid("10116");
        sm.setSpecies("Rattus norvegicus (Rat)");
        sm.setDatabase("UniProtKB");
        sm.setDatabaseVersion("2011_11");
        sm.setReliability("2");
        sm.setURI("http://www.ebi.ac.uk/pride/link/to/identification");
        sm.setSpectraRef("ms_run[2]:index=7|ms_run[2]:index=9");
        sm.setSearchEngine("[MS, MS:1001477, SpectraST,]");
        sm.setBestSearchEngineScore(1, "0.7");
        sm.setModifications("CHEMMOD:+Na-H");

        // set search_engine_score[1]_ms_run[1]
        sm.setSearchEngineScore(1, msRun1, "50");

        // set smallmolecule_abundance_assay[1]
        sm.setAbundanceColumnValue(assay1, "12.3");

        // set value for opt_ms_run[1]_my_value
        sm.setOptionColumnValue(msRun1, "my_value", "Tom");

        // set value for opt_global_cv_1002217_decoy_peptide
        sm.setOptionColumnValue(param, "111");

        return sm;
    }

    public static void main(String[] args) throws Exception {
        ConvertSilacExperiment run = new ConvertSilacExperiment();
        MZTabFile tabFile = run.getMZTabFile();

        // print mzTab model into console.
        tabFile.printMZTab(System.out);
    }


}
