package uk.ac.ebi.pride.jmztab.gui;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
* User: Qingwei
* Date: 25/03/13
*/
public class MZTabConsolePaneRun {
    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            public void run() {
                try {
                    createGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        EventQueue.invokeLater(runner);
    }

    public static void createGUI() throws Exception {
        JFrame frame = new JFrame("MZTabConsolePane Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MZTabConsolePane msgPane = new MZTabConsolePane();

        File tabFile = new File("testset/CPTAC_Progenesis_label_free_mzq.txt");
        MZTabFileParser parser = new MZTabFileParser(tabFile, System.out);
        MZTabFile mzTabFile = parser.getMZTabFile();
        mzTabFile.printMZTab(System.out);

        frame.setContentPane(msgPane);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }
}
