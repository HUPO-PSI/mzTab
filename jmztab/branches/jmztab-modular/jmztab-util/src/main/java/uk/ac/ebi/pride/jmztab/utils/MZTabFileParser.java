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

    private MZTabErrorList errorList;

    private void init(File tabFile) {
        if (tabFile == null || ! tabFile.exists()) {
            throw new IllegalArgumentException("MZTab File not exists!");
        }

        this.tabFile = tabFile;
    }

    /**
     * Create a new {@code MZTabFileParser} for the given file. Parsing output and errors
     * are written to the provided {@link java.io.OutputStream}.
     * @param tabFile the MZTab file
     * @param out the output stream for parsing messages
     * @throws java.io.IOException
     */
    public MZTabFileParser(File tabFile, OutputStream out) throws IOException {
        this(tabFile, out, LEVEL);
    }

    /**
     * Create a new {@code MZTabFileParser} for the given file. Parsing output and errors
     * are written to the provided {@link java.io.OutputStream}.
     * @param tabFile the MZTab file
     * @param out the output stream for parsing messages
     * @param level the minimum error level to report errors for
     * @throws java.io.IOException
     */
    public MZTabFileParser(File tabFile, OutputStream out, MZTabErrorType.Level level) throws IOException {
        this(tabFile, out, level, MAX_ERROR_COUNT);
    }

    /**
     * Create a new {@code MZTabFileParser} for the given file. Parsing output and errors
     * are written to the provided {@link java.io.OutputStream}.
     * @param tabFile the MZTab file
     * @param out the output stream for parsing messages
     * @param level the minimum error level to report errors for
     * @param maxErrorCount the maximum number of errors to report in {@see MZTabFileParser#getErrorList()}
     * @throws java.io.IOException
     */
    public MZTabFileParser(File tabFile, OutputStream out, MZTabErrorType.Level level, int maxErrorCount) throws IOException {
        init(tabFile);

        try {
            errorList = new MZTabErrorList(level, maxErrorCount);
            check();
            refine();
        } catch (MZTabException e) {
            out.write(MZTabExceptionMessage.getBytes());
            errorList.add(e.getError());
        } catch (MZTabErrorOverflowException e) {
            out.write(MZTabErrorOverflowExceptionMessage.getBytes());
        }

        errorList.print(out);
        if (errorList.isEmpty()) {
            out.write(("No errors in " + tabFile + " file!" + NEW_LINE).getBytes());
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
     * refine all MZTabFile consistency correct.
     */
    private void refine() throws MZTabException, MZTabErrorOverflowException {
        if (mzTabFile == null) {
            return;
        }

        Metadata metadata = mzTabFile.getMetadata();
        MZTabColumnFactory proteinFactory = mzTabFile.getProteinColumnFactory();
        MZTabColumnFactory peptideFactory = mzTabFile.getPeptideColumnFactory();
        MZTabColumnFactory psmFactory = mzTabFile.getPsmColumnFactory();
        MZTabColumnFactory smlFactory = mzTabFile.getSmallMoleculeColumnFactory();

        //If ms_run[1-n]-hash is present,  ms_run[1-n]-hash_method SHOULD also be present
        for (MsRun msRun : metadata.getMsRunMap().values()) {
            if (msRun.getHash() != null && msRun.getHashMethod() == null)  {
                throw new MZTabException(new MZTabError(LogicalErrorType.MsRunHashMethodNotDefined, -1, msRun.getId().toString()));
            }
        }

        // If mzTab-type is "Quantification", then at least one section with {protein|peptide|small_molecule}_abundance* columns MUST be present
        boolean hasAbundance = false;
        if (metadata.getMZTabType() == MZTabDescription.Type.Quantification) {
            if (proteinFactory != null && ! proteinFactory.getAbundanceColumnMapping().isEmpty()) {
                hasAbundance = true;
            }
            if (peptideFactory != null && ! peptideFactory.getAbundanceColumnMapping().isEmpty()) {
                hasAbundance = true;
            }
            if (smlFactory != null && ! smlFactory.getAbundanceColumnMapping().isEmpty()) {
                hasAbundance = true;
            }
            if (! hasAbundance) {
                throw new MZTabException(new MZTabError(LogicalErrorType.QuantificationAbundance, -1));
            }
        }
    }

    /**
     * Query {@link uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList} to check exist errors or not.
     * @throws java.io.IOException
     * @throws uk.ac.ebi.pride.jmztab.utils.errors.MZTabException during parse metadata, protein/peptide/small_molecule header line, exists error.
     * @throws uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorOverflowException reference mztab.properties file mztab.max_error_count parameter.
     */
    private void check() throws IOException, MZTabException, MZTabErrorOverflowException {
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

        PositionMapping prtPositionMapping = null;
        PositionMapping pepPositionMapping = null;
        PositionMapping psmPositionMapping = null;
        PositionMapping smlPositionMapping = null;

        MZTabError error;
        String line;
        int highWaterMark = 1;
        int lineNumber = 0;
        Section section;
        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if (MZTabUtils.isEmpty(line)) {
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
                Section currentSection = Section.findSection(highWaterMark);
                error = new MZTabError(LogicalErrorType.LineOrder, lineNumber, currentSection.getName(), section.getName());
                throw new MZTabException(error);
            }

            highWaterMark = section.getLevel();
            // There exists errors during checking metadata section.
            if (highWaterMark == 1 && ! errorList.isEmpty()) {
                break;
            }

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
                    prtPositionMapping = new PositionMapping(prhParser.getFactory(), line);

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
                        prtParser = new PRTLineParser(prhParser.getFactory(), prtPositionMapping, mtdParser.getMetadata(), errorList);
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

                    if (mtdParser.getMetadata().getMZTabType() == MZTabDescription.Type.Identification) {
                        errorList.add(new MZTabError(LogicalErrorType.PeptideSection, lineNumber, subString(line)));
                    }

                    // peptide header section
                    pehParser = new PEHLineParser(mtdParser.getMetadata());
                    pehParser.parse(lineNumber, line, errorList);
                    pepPositionMapping = new PositionMapping(pehParser.getFactory(), line);

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
                        pepParser = new PEPLineParser(pehParser.getFactory(), pepPositionMapping, mtdParser.getMetadata(), errorList);
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
                    psmPositionMapping = new PositionMapping(pshParser.getFactory(), line);

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
                        psmParser = new PSMLineParser(pshParser.getFactory(), psmPositionMapping, mtdParser.getMetadata(), errorList);
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
                    smlPositionMapping = new PositionMapping(smhParser.getFactory(), line);

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
                        smlParser = new SMLLineParser(smhParser.getFactory(), smlPositionMapping, mtdParser.getMetadata(), errorList);
                    }
                    smlParser.parse(lineNumber, line, errorList);
                    smallMoleculeMap.put(lineNumber, smlParser.getRecord(line));

                    break;
            }
        }

        if (reader != null) {
            reader.close();
        }

        if (errorList.isEmpty()) {
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
        }

    }

    public MZTabFile getMZTabFile() {
        return mzTabFile;
    }
}
