package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.*;
import uk.ac.ebi.pride.jmztab.model.Section;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.io.*;

/**
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabFileParser {
    public static void parse(File tabFile, OutputStream out) {
        if (tabFile == null || ! tabFile.exists()) {
            throw new IllegalArgumentException("MZTab File not exists!");
        }

        BufferedReader reader = null;
        MTDLineParser mtdParser = new MTDLineParser();
        PRHLineParser prhParser = null;
        PRTLineParser prtParser = null;
        PEHLineParser pehParser = null;
        PEPLineParser pepParser = null;
        SMHLineParser smhParser = null;
        SMLLineParser smlParser = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(tabFile), MZTabConstants.ENCODE));
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

                section = getSection(line);
                if (section.getLevel() < highWaterMark) {
                    error = new MZTabError(LogicalErrorType.LineOrder, lineNumber, false, section.getName());
                    throw new MZTabException(error);
                }

                highWaterMark = section.getLevel();

                switch (highWaterMark) {
                    case 1:
                        // metadata section.
                        mtdParser.parse(lineNumber, line);
                        break;
                    case 2:
                        if (prhParser != null) {
                            // header line only display once!
                            error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, false, section.getName());
                            throw new MZTabException(error);
                        }

                        // protein header section
                        prhParser = new PRHLineParser(mtdParser.getMetadata());
                        prhParser.parse(lineNumber, line);

                        // tell system to continue parse protein data line.
                        highWaterMark = 3;
                        break;
                    case 3:
                        if (prhParser == null) {
                            // header line should be parse first.
                            error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, false, section.getName());
                            throw new MZTabException(error);
                        }

                        if (prtParser == null) {
                            prtParser = new PRTLineParser(prhParser.getFactory(), mtdParser.getMetadata());
                        }
                        prtParser.parse(lineNumber, line);
                        break;
                    case 4:
                        if (pehParser != null) {
                            // header line only display once!
                            error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, false, section.getName());
                            throw new MZTabException(error);
                        }

                        // peptide header section
                        pehParser = new PEHLineParser(mtdParser.getMetadata());
                        pehParser.parse(lineNumber, line);

                        // tell system to continue parse peptide data line.
                        highWaterMark = 5;
                        break;
                    case 5:
                        if (pehParser == null) {
                            // header line should be parse first.
                            error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, false, section.getName());
                            throw new MZTabException(error);
                        }

                        if (pepParser == null) {
                            pepParser = new PEPLineParser(pehParser.getFactory(), mtdParser.getMetadata());
                        }
                        pepParser.parse(lineNumber, line);
                        break;
                    case 6:
                        if (smhParser != null) {
                            // header line only display once!
                            error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, false, section.getName());
                            throw new MZTabException(error);
                        }

                        // small molecule header section
                        smhParser = new SMHLineParser(mtdParser.getMetadata());
                        smhParser.parse(lineNumber, line);

                        // tell system to continue parse small molecule data line.
                        highWaterMark = 7;
                        break;
                    case 7:
                        if (smhParser == null) {
                            // header line should be parse first.
                            error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, false, section.getName());
                            throw new MZTabException(error);
                        }

                        if (smlParser == null) {
                            smlParser = new SMLLineParser(smhParser.getFactory(), mtdParser.getMetadata());
                        }
                        smlParser.parse(lineNumber, line);
                        break;
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MZTabException e) {
            System.err.println(e.getMessage());
        } catch (MZTabErrorOverflowException e) {
            System.err.println("System error queue overflow");
        }  finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            MZTabErrorList.print(out, MZTabConstants.LEVEL);
        }
    }

    private static Section getSection(String line) {
        String[] items = line.split("\\s*" + MZTabConstants.TAB + "\\s*");
        String section = items[0].trim();
        return Section.findSection(section);
    }
}
