package uk.ac.ebi.pride.jmztab.utils.errors;

import uk.ac.ebi.pride.jmztab.model.MZTabConstants;

import java.io.*;

/**
 * User: qingwei
 * Date: 05/11/13
 */
public class WikiRun {
    private OutputStream out = System.out;

    private void writeLine(String content) throws IOException {
        out.write(content.getBytes());
        out.write(MZTabConstants.NEW_LINE.getBytes());
    }

    /**
     * print all error messages to wiki document.
     */
    private void printErrorMessage() throws Exception {
        MZTabErrorTypeMap errorMap = new MZTabErrorTypeMap();
        for (MZTabErrorType errorType : errorMap.getTypeMap().values()) {
            writeLine("|| *    Code*: || " + errorType.getCode() + " ||");
            writeLine("|| *   Level*: || " + errorType.getLevel() + " ||");
            writeLine("|| *Original*: || " + errorType.getOriginal() + " ||");
            writeLine("|| *   Cause*: || " + errorType.getCause() + " ||");
            writeLine("|| || ||");
            writeLine("|| || ||");
        }
    }

    private void printTabSplitFile(String header, String inFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inFile));

        String line;
        String[] items;
        writeLine("|| *" + header + "* || *Identification* || *Quantification* ||");
        while ((line = reader.readLine()) != null) {
            items = line.split("\t");
            if (items.length == 1) {
                writeLine("|| || || " + items[0] + " ||");
            } else if (items.length == 2){
                writeLine("|| || " + items[0] + " || " + items[1] + " ||");
            }
        }

        reader.close();
    }

    /**
     * print summary table file to wiki document.
     */
    private void printSummaryTable() throws Exception {
        printTabSplitFile("Protein Section", "testset/protein_columns_in_summary.txt");
        writeLine("|| || || || ");
        printTabSplitFile("Peptide Section", "testset/peptide_columns_in_summary.txt");
        writeLine("|| || || || ");
        printTabSplitFile("PSM Section", "testset/psm_columns_in_summary.txt");
        writeLine("|| || || || ");
        printTabSplitFile("Small Molecule Section", "testset/smallmolecule_columns_in_summary.txt");
        writeLine("");
        writeLine("*Table 2*. Mandatory columns in mzTab 'Summary' files. Where noted, these columns " +
            "are mandatory for every study_variable[1-n] or every ms_run[1-n] reported in the file. Note" +
            " - any Quantification file type MAY include any of the Columns or Sections required for an Identification file type.");
        writeLine("");
        writeLine("");
    }

    /**
     * print complete table file to wiki document.
     */
    private void printCompleteTable() throws Exception {
        printTabSplitFile("Protein Section", "testset/protein_columns_in_complete.txt");
        writeLine("|| || || || ");
        printTabSplitFile("Peptide Section", "testset/peptide_columns_in_complete.txt");
        writeLine("|| || || || ");
        printTabSplitFile("Small Molecule Section", "testset/smallmolecule_columns_in_complete.txt");
        writeLine("");
        writeLine("*Table 3*. Mandatory columns in mzTab 'Complete' files. In addition, 'Complete' files " +
            "MUST also have all the items that are MANDATORY in a 'Summary' file (Table 2 above). Where noted, " +
            "these columns are mandatory for every assay[1-n], ms_run[1-n] or study_variable[1-n] reported in the file.");
        writeLine("");
        writeLine("");
    }

    /**
     * print optional table file to wiki document.
     */
    private void printOptionalTable() throws Exception {
        printTabSplitFile("Protein Section", "testset/protein_columns_in_optional.txt");
        writeLine("|| || || || ");
        printTabSplitFile("Peptide Section", "testset/peptide_columns_in_optional.txt");
        writeLine("|| || || || ");
        printTabSplitFile("PSM Section", "testset/psm_columns_in_optional.txt");
        writeLine("|| || || || ");
        printTabSplitFile("Small Molecule Section", "testset/smallmolecule_columns_in_optional.txt");
        writeLine("");
        writeLine("*Table 4*. Optional fields in mzTab 'Complete' and 'Summary' files. ");
        writeLine("");
        writeLine("");
    }

    public static void main(String[] args) throws Exception {
        WikiRun run = new WikiRun();
        run.printErrorMessage();

//        run.printSummaryTable();
//        run.printCompleteTable();
//        run.printOptionalTable();
    }
}
