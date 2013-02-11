package uk.ac.ebi.pride.jmztab.parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Qingwei
 * Date: 11/02/13
 */
public class ParserUtils {
    public static List<String> getMTDLineList(File testFile) throws IOException{
        List<String> list = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new FileReader(testFile));
        String line;
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }

        return list;
    }
}
