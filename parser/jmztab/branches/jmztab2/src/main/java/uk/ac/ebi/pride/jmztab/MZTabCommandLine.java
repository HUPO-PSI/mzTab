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

        String helpOpt = "help";
        options.addOption("h", helpOpt, false, "print help message");

        String msgOpt = "message";
        String codeOpt = "code";
        Option msgOption = OptionBuilder.withArgName(codeOpt + "=CodeNumber")
                .hasArgs(2)
                .withValueSeparator()
                .withDescription("print Error/Warn detail message based on code number.")
                .create(msgOpt);
        options.addOption(msgOption);

        String inDirOpt = "inDir";
        String inDirLongOpt = "input_directory";
        options.addOption(inDirOpt, inDirLongOpt, true, "Setting input file directory.");

        String outDirOpt = "outDir";
        String outDirLongOpt = "output_directory";
        options.addOption(outDirOpt, outDirLongOpt, true, "Setting output file directory.");

        String outOpt = "outFile";
        String outLongOpt = "out_file";
        options.addOption(outOpt, outLongOpt, true, "Record error/warn messages into outfile. If not set, print message on the screen. ");

        String checkOpt = "check";
        String inFileOpt = "inFile";
        Option checkOption = OptionBuilder.withArgName(inFileOpt + "=FileName")
                .hasArgs(2)
                .withValueSeparator()
                .withDescription("choose a file from input directory.")
                .create(checkOpt);
        options.addOption(checkOption);

        String convertOpt = "convert";
        String formatOpt = "format";
        Option convertOption = OptionBuilder.withArgName(inFileOpt + "=FileName " + formatOpt + "=PRIDE/mzIdentML")
                .hasArgs()
                .withValueSeparator()
                .withDescription("Converts the given file to an mztab file.")
                .create(convertOpt);
        options.addOption(convertOption);

        String mergeOpt = "merge";
        String inFileListOpt = "inFileList";
        String combineOpt = "combine";
        Option mergeOption = OptionBuilder.withArgName(inFileListOpt + "=File1,file2,file3 " + combineOpt + "=true/false")
                .hasArgs()
                .withValueSeparator()
                .withDescription("Merge more than one mztab files into another mztab File.")
                .create(mergeOpt);
        options.addOption(mergeOption);


        // Parse command line
        CommandLine line = parser.parse(options, args);
        if (line.hasOption(helpOpt)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("jmztab", options);
        } else if (line.hasOption(msgOpt)) {
            String[] values = line.getOptionValues(msgOpt);
            Integer code = new Integer(values[1]);
            MZTabErrorType type = typeMap.getType(code);

            if (type == null) {
                System.out.println("Not found MZTabErrorType, the code is :" + code);
            } else {
                System.out.println(type);
            }
        } else {
            File inDir = null;
            if (line.hasOption(inDirOpt)) {
                inDir = new File(line.getOptionValue(inDirOpt));
                if (! inDir.isDirectory()) {
                    throw new IllegalArgumentException("input file directory not setting!");
                }
            }
            if (inDir == null) {
                inDir = new File(".");
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
            if (line.hasOption(outOpt)) {
                outFile = new File(outDir, line.getOptionValue(outOpt));
            }
            OutputStream out = outFile == null ? System.out : new BufferedOutputStream(new FileOutputStream(outFile));

            if (line.hasOption(checkOpt)) {
                String[] values = line.getOptionValues(checkOpt);
                if (values.length != 2) {
                    throw new IllegalArgumentException("Not setting input file!");
                }
                File inFile = new File(inDir, values[1].trim());
                new MZTabFileParser(inFile, out);
            } else if (line.hasOption(convertOpt)) {
                String[] values = line.getOptionValues(convertOpt);
                File inFile = null;
                ConvertFile.Format format = null;
                for (int i = 0; i < values.length; i++) {
                    String type = values[i++].trim();
                    String value = values[i].trim();
                    if (type.equals(inFileOpt)) {
                        inFile = new File(inDir, value.trim());
                    } else if (type.equals(formatOpt)) {
                        format = MZTabFileConverter.findFormat(value);
                    }
                }
                if (inFile == null) {
                    throw new IllegalArgumentException("Not setting input file!");
                }
                if (format == null) {
                    format = ConvertFile.Format.PRIDE;
                }

                MZTabFileConverter converter = new MZTabFileConverter(inFile, format);
                converter.getMZTabFile().printMZTab(out);
            } else if (line.hasOption(mergeOpt)) {
                String[] values = line.getOptionValues(mergeOpt);
                List<File> inFileList = new ArrayList<File>();
                boolean combine = false;
                for (int i = 0; i < values.length; i++) {
                    String type = values[i++].trim();
                    String value = values[i].trim();
                    if (type.equals(inFileListOpt)) {
                        String[] fileNames = value.split(",");
                        for (String fileName : fileNames) {
                            inFileList.add(new File(inDir, fileName));
                        }
                    } else if (type.equals(combineOpt)) {
                        combine = value.equals("true");
                    }
                }
                MZTabFileMerger merger = new MZTabFileMerger(inFileList);
                merger.setCombine(combine);
                merger.printMZTab(out);
            }


            out.close();
        }
    }
}
