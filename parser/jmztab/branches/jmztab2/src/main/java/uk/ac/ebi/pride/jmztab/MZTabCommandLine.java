package uk.ac.ebi.pride.jmztab;

import org.apache.commons.cli.*;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorType;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorTypeMap;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileConverter;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileMerger;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;

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

        String dirOpt = "d";
        String dirLongOpt = "directory";
        options.addOption(dirOpt, dirLongOpt, true, "Setting file directory.");

        String checkOpt = "check_file";
        options.addOption(checkOpt, true, "Tries to parse the given mzTab file.");

        String mergeOpt = "merge";
        options.addOption(mergeOpt, true, "Merge multiple mztab files, a comma-delimited list of files");

        String convertOpt = "convert";
        options.addOption(convertOpt, true, "Converts the given file to an mztab file");

        String formatOpt = "format";
        String defaultType = "PRIDE";
        Option formatOption = OptionBuilder.withArgName(formatOpt + "=value")
                                           .hasArgs(2)
                                           .withValueSeparator()
                                           .withDescription("Specifies the input file format. Default values are " + defaultType)
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

        File dir = null;
        if (line.hasOption(dirOpt)) {
            dir = new File(line.getOptionValue(dirOpt));
            if (! dir.isDirectory()) {
                dir = null;
            }
        }

        File outFile = null;
        OutputStream out;
        if (line.hasOption(outOpt)) {
            outFile = new File(dir, line.getOptionValue(outOpt));
        }
        out = outFile == null ? System.out : new BufferedOutputStream(new FileOutputStream(outFile));

        if (line.hasOption(checkOpt)) {
            File tabFile = new File(dir, line.getOptionValue(checkOpt));
            new MZTabFileParser(tabFile, out);
        } else if (line.hasOption(convertOpt)) {
            File convertFile = new File(dir, line.getOptionValue(convertOpt));
            MZTabFileConverter.Format format = MZTabFileConverter.findFormat(line.getOptionValue(formatOpt));
            MZTabFileConverter.convert(convertFile, format, out);
        } else if (line.hasOption(mergeOpt)) {
            String[] fileNameList = line.getOptionValue(mergeOpt).split(",");
            List<File> tabFileList = new ArrayList<File>();
            for (String fileName : fileNameList) {
                tabFileList.add(new File(dir, fileName.trim()));
            }
            MZTabFileMerger merger = new MZTabFileMerger(tabFileList);
            merger.printMZTab(out);
        }

        out.close();
    }
}
