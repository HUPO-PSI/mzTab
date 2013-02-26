package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.*;
import uk.ac.ebi.pride.jmztab.model.Section;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabFileParser {
    private static BufferedReader readFile(File tabFile) throws IOException {
        if (tabFile.getName().endsWith(".gz")) {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(tabFile)), MZTabConstants.ENCODE));
        } else {
            return new BufferedReader(new InputStreamReader(new FileInputStream(tabFile), MZTabConstants.ENCODE));
        }
    }

    public static void check(File tabFile, OutputStream out) throws IOException {
        if (tabFile == null || ! tabFile.exists()) {
            throw new IllegalArgumentException("MZTab File not exists!");
        }

        MZTabErrorList.clear();

        BufferedReader reader = null;
        MTDLineParser mtdParser = new MTDLineParser();
        PRHLineParser prhParser = null;
        PRTLineParser prtParser = null;
        PEHLineParser pehParser = null;
        PEPLineParser pepParser = null;
        SMHLineParser smhParser = null;
        SMLLineParser smlParser = null;
        try {
            reader = readFile(tabFile);
            MZTabError error;
            String line;
            int highWaterMark = 1;
            int lineNumber = 0;
            Section section;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                if (line.startsWith(Section.Comment.getPrefix())) {
                    continue;
                }

                section = getSection(line);
                if (section.getLevel() < highWaterMark) {
                    error = new MZTabError(LogicalErrorType.LineOrder, lineNumber, section.getName());
                    throw new MZTabException(error);
                }

                highWaterMark = section.getLevel();

                switch (highWaterMark) {
                    case 1:
                        // metadata section.
                        mtdParser.check(lineNumber, line);
                        break;
                    case 2:
                        if (prhParser != null) {
                            // header line only display once!
                            error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, section.getName());
                            throw new MZTabException(error);
                        }

                        // protein header section
                        prhParser = new PRHLineParser(mtdParser.getMetadata());
                        prhParser.check(lineNumber, line);

                        // tell system to continue check protein data line.
                        highWaterMark = 3;
                        break;
                    case 3:
                        if (prhParser == null) {
                            // header line should be check first.
                            error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, section.getName());
                            throw new MZTabException(error);
                        }

                        if (prtParser == null) {
                            prtParser = new PRTLineParser(prhParser.getFactory(), mtdParser.getMetadata());
                        }
                        prtParser.check(lineNumber, line);
                        break;
                    case 4:
                        if (pehParser != null) {
                            // header line only display once!
                            error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, section.getName());
                            throw new MZTabException(error);
                        }

                        // peptide header section
                        pehParser = new PEHLineParser(mtdParser.getMetadata());
                        pehParser.check(lineNumber, line);

                        // tell system to continue check peptide data line.
                        highWaterMark = 5;
                        break;
                    case 5:
                        if (pehParser == null) {
                            // header line should be check first.
                            error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, section.getName());
                            throw new MZTabException(error);
                        }

                        if (pepParser == null) {
                            pepParser = new PEPLineParser(pehParser.getFactory(), mtdParser.getMetadata());
                        }
                        pepParser.check(lineNumber, line);
                        break;
                    case 6:
                        if (smhParser != null) {
                            // header line only display once!
                            error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, section.getName());
                            throw new MZTabException(error);
                        }

                        // small molecule header section
                        smhParser = new SMHLineParser(mtdParser.getMetadata());
                        smhParser.check(lineNumber, line);

                        // tell system to continue check small molecule data line.
                        highWaterMark = 7;
                        break;
                    case 7:
                        if (smhParser == null) {
                            // header line should be check first.
                            error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, section.getName());
                            throw new MZTabException(error);
                        }

                        if (smlParser == null) {
                            smlParser = new SMLLineParser(smhParser.getFactory(), mtdParser.getMetadata());
                        }
                        smlParser.check(lineNumber, line);
                        break;
                }
            }

        } catch (MZTabException e) {
            out.write("There exists errors in the metadata section or protein/peptide/small_molecule header section!".getBytes());
            out.write(MZTabConstants.NEW_LINE.getBytes());
        } catch (MZTabErrorOverflowException e) {
            out.write("System error queue overflow!".getBytes());
            out.write(MZTabConstants.NEW_LINE.getBytes());
        }  finally {
            if (reader != null) {
                reader.close();
            }

        }

        MZTabErrorList.print(out, MZTabConstants.LEVEL);
        if (MZTabErrorList.isEmpty()) {
            out.write(("not errors in " + tabFile + " file!" + MZTabConstants.NEW_LINE).getBytes());
        }
    }

    private static Section getSection(String line) {
        String[] items = line.split("\\s*" + MZTabConstants.TAB + "\\s*");
        String section = items[0].trim();
        return Section.findSection(section);
    }
}
