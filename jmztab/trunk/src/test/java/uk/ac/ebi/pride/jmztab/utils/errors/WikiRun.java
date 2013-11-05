package uk.ac.ebi.pride.jmztab.utils.errors;

import uk.ac.ebi.pride.jmztab.model.MZTabConstants;

import java.io.IOException;
import java.io.OutputStream;

/**
 * User: qingwei
 * Date: 05/11/13
 */
public class WikiRun {
    private void writeLine(OutputStream out, String content) throws IOException {
        out.write(content.getBytes());
        out.write(MZTabConstants.NEW_LINE.getBytes());
    }

    private void toWiki(OutputStream out) throws Exception {
        MZTabErrorTypeMap errorMap = new MZTabErrorTypeMap();
        for (MZTabErrorType errorType : errorMap.getTypeMap().values()) {
            writeLine(out, "|| *    Code*: || " + errorType.getCode() + " ||");
            writeLine(out, "|| *   Level*: || " + errorType.getLevel() + " ||");
            writeLine(out, "|| *Original*: || " + errorType.getOriginal() + " ||");
            writeLine(out, "|| *   Cause*: || " + errorType.getCause() + " ||");
            writeLine(out, "");
            writeLine(out, "----");
            writeLine(out, "");
        }
    }

    public static void main(String[] args) throws Exception {
        WikiRun run = new WikiRun();
        run.toWiki(System.out);
    }
}
