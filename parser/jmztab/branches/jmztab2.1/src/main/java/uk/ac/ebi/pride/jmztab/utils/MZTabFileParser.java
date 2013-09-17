package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.*;
import uk.ac.ebi.pride.jmztab.utils.parser.*;

import java.io.*;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.TAB;
import static uk.ac.ebi.pride.jmztab.utils.MZTabProperties.*;

/**
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabFileParser {
    private MZTabFile mzTabFile;
    private File tabFile;

    private MZTabErrorList errorList = new MZTabErrorList();

    private void init(File tabFile) {
        if (tabFile == null || ! tabFile.exists()) {
            throw new IllegalArgumentException("MZTab File not exists!");
        }

        this.tabFile = tabFile;
    }

    public MZTabFileParser(File tabFile, OutputStream out) throws IOException {
        this(tabFile, out, LEVEL);
    }

    public MZTabFileParser(File tabFile, OutputStream out, MZTabErrorType.Level level) throws IOException {
        init(tabFile);

        try {
            check(level);
        } catch (MZTabException e) {
            out.write(MZTabExceptionMessage.getBytes());
            errorList.add(e.getError());
        } catch (MZTabErrorOverflowException e) {
            out.write(MZTabErrorOverflowExceptionMessage.getBytes());
        }

        MZTabErrorList filterList = errorList.filterList(level);
        filterList.print(out);
        if (filterList.isEmpty()) {
            out.write(("not errors in " + tabFile + " file!" + NEW_LINE).getBytes());
        }
    }

    public MZTabErrorList getErrorList() {
        return errorList;
    }

    private Section getSection(String line) {
        String[] items = line.split("\\s*" + TAB + "\\s*");
        String section = items[0].trim();
        return Section.findSection(section);
    }

    private BufferedReader readFile(File tabFile) throws IOException {
        BufferedReader reader;

        if (tabFile.getName().endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(tabFile)), ENCODE));
        } else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(tabFile), ENCODE));
        }

        return reader;
    }

    private String subString(String source) {
        int length = 20;

        if (length >= source.length()) {
            return source;
        } else {
            return source.substring(0, length - 1) + "...";
        }
    }

    /**
     * Query {@link uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList} to check exist errors or not.
     * @throws java.io.IOException
     * @throws uk.ac.ebi.pride.jmztab.utils.errors.MZTabException during parse metadata, protein/peptide/small_molecule header line, exists error.
     * @throws uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorOverflowException reference mztab.properties file mztab.max_error_count parameter.
     */
    private void check(MZTabErrorType.Level level) throws IOException, MZTabException, MZTabErrorOverflowException {
        BufferedReader reader = readFile(tabFile);

        COMLineParser comParser = new COMLineParser();
        MTDLineParser mtdParser = new MTDLineParser();
        PRHLineParser prhParser = null;
        PRTLineParser prtParser = null;
        PEHLineParser pehParser = null;
        PEPLineParser pepParser = null;
        PSHLineParser pshParser = null;
        PSMLineParser psmParser = null;
        SMHLineParser smhParser = null;
        SMLLineParser smlParser = null;


        SortedMap<Integer, Comment> commentMap = new TreeMap<Integer, Comment>();
        SortedMap<Integer, Protein> proteinMap = new TreeMap<Integer, Protein>();
        SortedMap<Integer, Peptide> peptideMap = new TreeMap<Integer, Peptide>();
        SortedMap<Integer, PSM> psmMap = new TreeMap<Integer, PSM>();
        SortedMap<Integer, SmallMolecule> smallMoleculeMap = new TreeMap<Integer, SmallMolecule>();

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
                comParser.parse(lineNumber, line, errorList);
                commentMap.put(lineNumber, comParser.getComment());
                continue;
            }

            section = getSection(line);
            if (section == null) {
                error = new MZTabError(FormatErrorType.LinePrefix, lineNumber, subString(line));
                throw new MZTabException(error);
            }
            if (section.getLevel() < highWaterMark) {
                error = new MZTabError(LogicalErrorType.LineOrder, lineNumber, section.getName());
                throw new MZTabException(error);
            }

            highWaterMark = section.getLevel();

            switch (highWaterMark) {
                case 1:
                    // metadata section.
                    mtdParser.parse(lineNumber, line, errorList);
                    break;
                case 2:
                    if (prhParser != null) {
                        // header line only display once!
                        error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, subString(line));
                        throw new MZTabException(error);
                    }

                    // protein header section
                    prhParser = new PRHLineParser(mtdParser.getMetadata());
                    prhParser.parse(lineNumber, line, errorList);

                    // tell system to continue check protein data line.
                    highWaterMark = 3;
                    break;
                case 3:
                    if (prhParser == null) {
                        // header line should be check first.
                        error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, subString(line));
                        throw new MZTabException(error);
                    }

                    if (prtParser == null) {
                        prtParser = new PRTLineParser(prhParser.getFactory(), mtdParser.getMetadata(), errorList);
                    }
                    prtParser.parse(lineNumber, line, errorList);
                    proteinMap.put(lineNumber, prtParser.getRecord(line));

                    break;
                case 4:
                    if (pehParser != null) {
                        // header line only display once!
                        error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, subString(line));
                        throw new MZTabException(error);
                    }

                    // peptide header section
                    pehParser = new PEHLineParser(mtdParser.getMetadata());
                    pehParser.parse(lineNumber, line, errorList);

                    // tell system to continue check peptide data line.
                    highWaterMark = 5;
                    break;
                case 5:
                    if (pehParser == null) {
                        // header line should be check first.
                        error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, subString(line));
                        throw new MZTabException(error);
                    }

                    if (pepParser == null) {
                        pepParser = new PEPLineParser(pehParser.getFactory(), mtdParser.getMetadata(), errorList);
                    }
                    pepParser.parse(lineNumber, line, errorList);
                    peptideMap.put(lineNumber, pepParser.getRecord(line));

                    break;
                case 6:
                    if (pshParser != null) {
                        // header line only display once!
                        error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, subString(line));
                        throw new MZTabException(error);
                    }

                    // psm header section
                    pshParser = new PSHLineParser(mtdParser.getMetadata());
                    pshParser.parse(lineNumber, line, errorList);

                    // tell system to continue check peptide data line.
                    highWaterMark = 7;
                    break;
                case 7:
                    if (pshParser == null) {
                        // header line should be check first.
                        error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, subString(line));
                        throw new MZTabException(error);
                    }

                    if (psmParser == null) {
                        psmParser = new PSMLineParser(pshParser.getFactory(), mtdParser.getMetadata(), errorList);
                    }
                    psmParser.parse(lineNumber, line, errorList);
                    psmMap.put(lineNumber, psmParser.getRecord(line));

                    break;
                case 8:
                    if (smhParser != null) {
                        // header line only display once!
                        error = new MZTabError(LogicalErrorType.HeaderLine, lineNumber, subString(line));
                        throw new MZTabException(error);
                    }

                    // small molecule header section
                    smhParser = new SMHLineParser(mtdParser.getMetadata());
                    smhParser.parse(lineNumber, line, errorList);

                    // tell system to continue check small molecule data line.
                    highWaterMark = 9;
                    break;
                case 9:
                    if (smhParser == null) {
                        // header line should be check first.
                        error = new MZTabError(LogicalErrorType.NoHeaderLine, lineNumber, subString(line));
                        throw new MZTabException(error);
                    }

                    if (smlParser == null) {
                        smlParser = new SMLLineParser(smhParser.getFactory(), mtdParser.getMetadata(), errorList);
                    }
                    smlParser.parse(lineNumber, line, errorList);
                    smallMoleculeMap.put(lineNumber, smlParser.getRecord(line));

                    break;
            }
        }

        if (reader != null) {
            reader.close();
        }

        if (errorList.filterList(level).isEmpty()) {
            mzTabFile = new MZTabFile(mtdParser.getMetadata());
            for (Integer id : commentMap.keySet()) {
                mzTabFile.addComment(id, commentMap.get(id));
            }

            if (prhParser != null) {
                MZTabColumnFactory proteinColumnFactory = prhParser.getFactory();
                mzTabFile.setProteinColumnFactory(proteinColumnFactory);
                for (Integer id : proteinMap.keySet()) {
                    mzTabFile.addProtein(id, proteinMap.get(id));
                }
            }

            if (pehParser != null) {
                MZTabColumnFactory peptideColumnFactory = pehParser.getFactory();
                mzTabFile.setPeptideColumnFactory(peptideColumnFactory);
                for (Integer id : peptideMap.keySet()) {
                    mzTabFile.addPeptide(id, peptideMap.get(id));
                }
            }

            if (pshParser != null) {
                MZTabColumnFactory psmColumnFactory = pshParser.getFactory();
                mzTabFile.setPSMColumnFactory(psmColumnFactory);
                for (Integer id : psmMap.keySet()) {
                    mzTabFile.addPSM(id, psmMap.get(id));
                }
            }

            if (smhParser != null) {
                MZTabColumnFactory smallMoleculeColumnFactory = smhParser.getFactory();
                mzTabFile.setSmallMoleculeColumnFactory(smallMoleculeColumnFactory);
                for (Integer id : smallMoleculeMap.keySet()) {
                    mzTabFile.addSmallMolecule(id, smallMoleculeMap.get(id));
                }
            }

            MZTabFileChecker checker = new MZTabFileChecker(errorList);
            checker.check(mzTabFile, level);
        }

    }

    public MZTabFile getMZTabFile() {
        return mzTabFile;
    }
}
