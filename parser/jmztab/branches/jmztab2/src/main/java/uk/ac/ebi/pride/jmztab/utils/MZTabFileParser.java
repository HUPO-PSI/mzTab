package uk.ac.ebi.pride.jmztab.utils;

import uk.ac.ebi.pride.jmztab.utils.errors.*;
import uk.ac.ebi.pride.jmztab.model.*;
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

        errorList.print(out, level);
        if (errorList.isEmpty()) {
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

    /**
     * Query {@link MZTabErrorList} to check exist errors or not.
     * @throws IOException
     * @throws MZTabException during parse metadata, protein/peptide/small_molecule header line, exists error.
     * @throws MZTabErrorOverflowException reference mztab.properties file mztab.max_error_count parameter.
     */
    private void check(MZTabErrorType.Level level) throws IOException, MZTabException, MZTabErrorOverflowException {
        BufferedReader reader = readFile(tabFile);

        COMLineParser comParser = new COMLineParser();
        MTDLineParser mtdParser = new MTDLineParser();
        PRHLineParser prhParser = null;
        PRTLineParser prtParser = null;
        PEHLineParser pehParser = null;
        PEPLineParser pepParser = null;
        SMHLineParser smhParser = null;
        SMLLineParser smlParser = null;


        SortedMap<Integer, Comment> commentMap = new TreeMap<Integer, Comment>();
        SortedMap<Integer, Protein> proteinMap = new TreeMap<Integer, Protein>();
        SortedMap<Integer, Peptide> peptideMap = new TreeMap<Integer, Peptide>();
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
                comParser.check(lineNumber, line);
                commentMap.put(lineNumber, comParser.getComment());
                continue;
            }

            section = getSection(line);
            if (section == null) {
                error = new MZTabError(FormatErrorType.LinePrefix, lineNumber, line);
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
                        prtParser = new PRTLineParser(prhParser.getFactory(), mtdParser.getMetadata(), errorList);
                    }
                    prtParser.check(lineNumber, line);
                    proteinMap.put(lineNumber, prtParser.getRecord(line));

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
                        pepParser = new PEPLineParser(pehParser.getFactory(), mtdParser.getMetadata(), errorList);
                    }
                    pepParser.check(lineNumber, line);
                    peptideMap.put(lineNumber, pepParser.getRecord(line));

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
                        smlParser = new SMLLineParser(smhParser.getFactory(), mtdParser.getMetadata(), errorList);
                    }
                    smlParser.check(lineNumber, line);
                    smallMoleculeMap.put(lineNumber, smlParser.getRecord(line));

                    break;
            }
        }

        if (reader != null) {
            reader.close();
        }

        if (errorList.isEmpty(level)) {
            mzTabFile = new MZTabFile(mtdParser.getMetadata());
            for (Integer id : commentMap.keySet()) {
                mzTabFile.addComment(id, commentMap.get(id));
            }

            if (prhParser != null) {
                MZTabColumnFactory proteinColumnFactory = prhParser.getFactory();
                mzTabFile.setProteinColumnFactory(proteinColumnFactory);
                for (AbundanceColumn column : proteinColumnFactory.getAbundanceColumnMapping().values()) {
                    column.getSubUnit().addPropertyChangeListener(OperationCenter.SUB_UNIT_ID, column);
                }
                for (Integer id : proteinMap.keySet()) {
                    mzTabFile.addProtein(id, proteinMap.get(id));
                }
            }

            if (pehParser != null) {
                MZTabColumnFactory peptideColumnFactory = pehParser.getFactory();
                mzTabFile.setPeptideColumnFactory(peptideColumnFactory);
                for (AbundanceColumn column : peptideColumnFactory.getAbundanceColumnMapping().values()) {
                    column.getSubUnit().addPropertyChangeListener(OperationCenter.SUB_UNIT_ID, column);
                }
                for (Integer id : peptideMap.keySet()) {
                    mzTabFile.addPeptide(id, peptideMap.get(id));
                }
            }

            if (smhParser != null) {
                MZTabColumnFactory smallMoleculeColumnFactory = smhParser.getFactory();
                mzTabFile.setSmallMoleculeColumnFactory(smallMoleculeColumnFactory);
                for (AbundanceColumn column : smallMoleculeColumnFactory.getAbundanceColumnMapping().values()) {
                    column.getSubUnit().addPropertyChangeListener(OperationCenter.SUB_UNIT_ID, column);
                }
                for (Integer id : smallMoleculeMap.keySet()) {
                    mzTabFile.addSmallMolecule(id, smallMoleculeMap.get(id));
                }
            }
        }

        MZTabFileChecker checker = new MZTabFileChecker(errorList);
        checker.check(mzTabFile, level);
    }

    public MZTabFile getMZTabFile() {
        return mzTabFile;
    }
}
