package uk.ac.ebi.pride.jmztab.gui;

import javax.swing.*;
import java.io.*;

/**
 * Create a message output console for GUI application -- MZTabGraphicalInterface.
 *
 * @see uk.ac.ebi.pride.jmztab.MZTabGraphicalInterface
 *
 * @author qingwei
 * @since 23/03/13
 */
public class MZTabConsolePane extends JScrollPane {
    private JTextArea text;

    private class Console implements Runnable {
        JTextArea displayPane;
        BufferedReader reader;

        private Console(JTextArea displayPane, PipedOutputStream pos) {
            this.displayPane = displayPane;
            try {
                PipedInputStream pis = new PipedInputStream(pos);
                reader = new BufferedReader(new InputStreamReader(pis));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    displayPane.append(line + "\n");
                    displayPane.setCaretPosition(displayPane.getDocument().getLength());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MZTabConsolePane() {
        this.text = new JTextArea();
        text.setEditable(false);

        setViewportView(text);
        redirectOut(text);
        redirectErr(text);
    }

    public void clearContent() {
        this.text.setText("");
    }

    private void redirectOut(JTextArea text) {
        PipedOutputStream pos = new PipedOutputStream();
        System.setOut(new PrintStream(pos, true));

        Console console = new Console(text, pos);
        new Thread(console).start();
    }

    private void redirectErr(JTextArea text) {
        PipedOutputStream pos = new PipedOutputStream();
        System.setErr(new PrintStream(pos, true));

        Console console = new Console(text, pos);
        new Thread(console).start();
    }

}
