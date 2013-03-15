package uk.ac.ebi.pride.jmztab;

import org.apache.commons.cli.*;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorTypeMap;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileConverter;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileMerger;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: qingwei
 * Date: 27/02/13
 */
public class MZTabCommandLine {
    public static void main(String[] args) throws Exception {
        MZTabErrorTypeMap typeMap = new MZTabErrorTypeMap();

        // Definite command line
        CommandLineParser parser = new PosixParser();

        Options options = new Options();
        options.addOption("h", "help", false, "Print help message");

        String codeOpt = "code";
        options.addOption(codeOpt, true, "Get Error/Warn detail message based on code number");

        String inDirOpt = "inDir";
        String inDirLongOpt = "input_directory";
        options.addOption(inDirOpt, inDirLongOpt, true, "Setting input file directory.");

        String checkOpt = "check_file";
        options.addOption(checkOpt, true, "Tries to parse the given mzTab file.");

        String outDirOpt = "outDir";
        String outDirLongOpt = "output_directory";
        options.addOption(outDirOpt, outDirLongOpt, true, "Setting output file directory.");

        String mergeOpt = "merge";
        options.addOption(mergeOpt, true, "Merge multiple mztab files, a comma-delimited list of files");

        String combineOpt = "combine";
        String defaultCombineOpt = "false";
        Option combineOption = OptionBuilder.withArgName(combineOpt + "=true/false")
                .hasArgs(2)
                .withValueSeparator()
                .withDescription("Combine sub-sample abundance columns or not. Default value is " + defaultCombineOpt)
                .create(combineOpt);
        options.addOption(combineOption);

        String convertOpt = "convert";
        options.addOption(convertOpt, true, "Converts the given file to an mztab file");

        String formatOpt = "format";
        String defaultFormatOpt = MZTabFileConverter.PRIDE;
        Option formatOption = OptionBuilder.withArgName(formatOpt + "=value")
                                           .hasArgs(2)
                                           .withValueSeparator()
                                           .withDescription("Specifies the input file format. Default values are " + defaultFormatOpt)
                                           .create(convertOpt);
        options.addOption(formatOption);

        String outOpt = "out";
        String outLongOpt = "out_file";
        options.addOption(outOpt, outLongOpt, true, "Record error/warn messages into outfile. If not set, print message on the screen. ");

        // Parse command line
        CommandLine line = parser.parse(options, args);
        if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("jmztab", options);
            System.exit(0);
        }

        if (line.hasOption(codeOpt)) {
            Integer code = new Integer(line.getOptionValue(codeOpt));
            MZTabErrorType type = typeMap.getType(code);

            if (type == null) {
                System.out.println("not found MZTabErrorType, the code is :" + code);
            } else {
                System.out.println(type);
            }

            System.exit(0);
        }

        // if no
        File inDir = null;
        if (line.hasOption(inDirOpt)) {
            inDir = new File(line.getOptionValue(inDirOpt));
            if (! inDir.isDirectory()) {
                throw new IllegalArgumentException("input file directory not exists!");
            }
        }

        // if not provide output directory, default use input directory.
        File outDir = null;
        if (line.hasOption(outDirOpt)) {
            outDir = new File(line.getOptionValue(outDirOpt));
        }
        if (outDir == null || !outDir.isDirectory()) {
            outDir = inDir;
        }

        File outFile = null;
        OutputStream out;
        if (line.hasOption(outOpt)) {
            outFile = new File(outDir, line.getOptionValue(outOpt));
        }
        out = outFile == null ? System.out : new BufferedOutputStream(new FileOutputStream(outFile));

        if (line.hasOption(checkOpt)) {
            File tabFile = new File(inDir, line.getOptionValue(checkOpt));
            new MZTabFileParser(tabFile, out);
        } else if (line.hasOption(convertOpt)) {
            File inFile = new File(inDir, line.getOptionValue(convertOpt));

            ConvertFile.Format format = MZTabFileConverter.findFormat(line.getOptionValue(formatOpt, defaultFormatOpt));
            MZTabFileConverter converter = new MZTabFileConverter(inFile, format);
            converter.getMZTabFile().printMZTab(out);
        } else if (line.hasOption(mergeOpt)) {
            String combineLabel = line.getOptionValue(combineOpt, defaultCombineOpt);
            boolean combine = combineLabel.equals("true");

            String[] fileNameList = line.getOptionValue(mergeOpt).split(",");
            List<File> tabFileList = new ArrayList<File>();
            for (String fileName : fileNameList) {
                tabFileList.add(new File(inDir, fileName.trim()));
            }
            MZTabFileMerger merger = new MZTabFileMerger(tabFileList);
            merger.setCombine(combine);
            merger.printMZTab(out);
        }

        out.close();
    }
}
