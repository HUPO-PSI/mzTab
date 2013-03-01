package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.errors.*;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.MZTabConstants;

import java.io.*;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

/**
 * User: Qingwei
 * Date: 21/02/13
 */
public class MZTabFileParser {
    private boolean buffered = true;

    private COMLineParser comParser = new COMLineParser();
    private MTDLineParser mtdParser = new MTDLineParser();
    private PRHLineParser prhParser = null;
    private PRTLineParser prtParser = null;
    private PEHLineParser pehParser = null;
    private PEPLineParser pepParser = null;
    private SMHLineParser smhParser = null;
    private SMLLineParser smlParser = null;

    private File tabFile;
    private SortedMap<Integer, Comment> commentMap = new TreeMap<Integer, Comment>();
    private SortedMap<Integer, Protein> proteinMap = new TreeMap<Integer, Protein>();
    private SortedMap<Integer, Peptide> peptideMap = new TreeMap<Integer, Peptide>();
    private SortedMap<Integer, SmallMolecule> smallMoleculeMap = new TreeMap<Integer, SmallMolecule>();

    public MZTabFileParser(File tabFile, OutputStream out) throws IOException {
        this(tabFile, out, MZTabConstants.BUFFERED);
    }

    public MZTabFileParser(File tabFile, OutputStream out, boolean buffered) throws IOException {
        if (tabFile == null || ! tabFile.exists()) {
            throw new IllegalArgumentException("MZTab File not exists!");
        }

        this.tabFile = tabFile;
        this.buffered = buffered;

        MZTabErrorList.clear();
        PRTLineParser.accessionSet.clear();
        check(out);
    }

    private Section getSection(String line) {
        String[] items = line.split("\\s*" + MZTabConstants.TAB + "\\s*");
        String section = items[0].trim();
        return Section.findSection(section);
    }

    private void check(OutputStream out) throws IOException {
        BufferedReader reader = null;

        try {
            if (tabFile.getName().endsWith(".gz")) {
                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(tabFile)), MZTabConstants.ENCODE));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(tabFile), MZTabConstants.ENCODE));
            }

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
                    if (buffered) {
                        comParser.check(lineNumber, line);
                        commentMap.put(lineNumber, comParser.getComment());
                        continue;
                    } else {
                        continue;
                    }
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
                        if (buffered) {
                            proteinMap.put(lineNumber, prtParser.getRecord(line));
                        }

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
                        if (buffered) {
                            peptideMap.put(lineNumber, pepParser.getRecord(line));
                        }

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
                        if (buffered) {
                            smallMoleculeMap.put(lineNumber, smlParser.getRecord(line));
                        }

                        break;
                }
            }

        } catch (MZTabException e) {
            out.write("There exists errors in the metadata section or protein/peptide/small_molecule header section!".getBytes());
            out.write("Validation will stop, and ignore data table check!".getBytes());
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

    private void checkErrors() {
        if (! MZTabErrorList.isEmpty()) {
            throw new IllegalArgumentException("There exits some errors in the " + tabFile);
        }
    }

    public Metadata getMetadata() {
        checkErrors();
        return mtdParser.getMetadata();
    }

    public MZTabColumnFactory getProteinColumnFactory() {
        checkErrors();

        if (prhParser == null) {
            return null;
        }

        return prhParser.getFactory();
    }

    public SortedMap<Integer, MZTabColumn> getProteinColumns() {
        checkErrors();

        if (prhParser == null) {
            return new TreeMap<Integer, MZTabColumn>();
        }

        return prhParser.getFactory().getColumnMapping();
    }

    public MZTabColumnFactory getPeptideColumnFactory() {
        checkErrors();

        if (pehParser == null) {
            return null;
        }

        return pehParser.getFactory();
    }

    public SortedMap<Integer, MZTabColumn> getPeptideColumns() {
        checkErrors();

        if (pehParser == null) {
            return new TreeMap<Integer, MZTabColumn>();
        }

        return pehParser.getFactory().getColumnMapping();
    }

    public MZTabColumnFactory getSmallMoleculeColumnFactory() {
        checkErrors();

        if (smhParser == null) {
            return null;
        }

        return smhParser.getFactory();
    }

    public SortedMap<Integer, MZTabColumn> getSmallMoleculeColumns() {
        checkErrors();

        if (smhParser == null) {
            return new TreeMap<Integer, MZTabColumn>();
        }

        return smhParser.getFactory().getColumnMapping();
    }

    private void checkBuffered() {
        if (! buffered) {
            throw new UnsupportedOperationException("not buffered comment/peptide/protein/small_molecule data into memory!");
        }
    }

    public SortedMap<Integer, Comment> getComments() {
        checkErrors();
        checkBuffered();

        return commentMap;
    }

    public SortedMap<Integer, Protein> getProteins() {
        checkErrors();
        checkBuffered();

        return proteinMap;
    }

    public SortedMap<Integer, Peptide> getPeptides() {
        checkErrors();
        checkBuffered();

        return peptideMap;
    }

    public SortedMap<Integer, SmallMolecule> getSmallMolecules() {
        checkErrors();
        checkBuffered();

        return smallMoleculeMap;
    }
}
