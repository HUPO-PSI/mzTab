package uk.ac.ebi.pride.jmztab;

import uk.ac.ebi.pride.jmztab.gui.MZTabConsolePane;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorType;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * User: Qingwei
 * Date: 26/03/13
 */
public class MZTabInspector extends JFrame {
    private MZTabConsolePane consolePane;

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            public void run() {
                try {
                    MZTabInspector inspector = new MZTabInspector();
                    inspector.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        EventQueue.invokeLater(runner);
    }

    public MZTabInspector() {
        setTitle("MZTabInspector v1.0");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JTabbedPane mainPane = new JTabbedPane();
        this.consolePane = new MZTabConsolePane();
        consolePane.setPreferredSize(new Dimension(800, 600));
        getContentPane().add(consolePane, BorderLayout.SOUTH);
        getContentPane().add(mainPane, BorderLayout.CENTER);

        mainPane.addTab("Validate", getValidatePane());
    }

    private JPanel getValidatePane() {
        final JPanel validatePane = new JPanel();
        validatePane.setLayout(new FlowLayout());

        final JTextField fileNameField = new JTextField();
        JPanel fileChoosePane = getFileChoosePane(
                "Choose MZTabFile: ", fileNameField,
                new FileNameExtensionFilter("mzTab File (*.mztab, *.txt)", "mztab", "txt"), false);
        validatePane.add(fileChoosePane);

        JPanel controlPane = new JPanel();
        controlPane.setLayout(new FlowLayout());

        final ButtonGroup levelGroup = new ButtonGroup();
        levelGroup.add(new JRadioButton("Warn"));
        levelGroup.add(new JRadioButton("Error", true));
        JPanel paramPane = getParamsPane("Message Level", levelGroup);
        controlPane.add(paramPane);

        JPanel actionPane = getTitledPane("Action");
        final JButton btnValidate = new JButton("Validate");
        actionPane.add(btnValidate);
        controlPane.add(actionPane);

        btnValidate.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingWorker worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        btnValidate.setEnabled(false);
                        consolePane.clearContent();

                        String fileName = fileNameField.getText();
                        if (fileName == null || fileName.trim().length() == 0) {
                            JOptionPane.showMessageDialog(MZTabInspector.this, "Please choose a MZTabFile!");
                            return null;
                        }

                        Enumeration<AbstractButton> elements = levelGroup.getElements();
                        AbstractButton element;
                        String levelLabel = "Warn";
                        while (elements.hasMoreElements()) {
                            element = elements.nextElement();
                            if (element.isSelected()) {
                                levelLabel = element.getText();
                                break;
                            }
                        }

                        MZTabErrorType.Level level = levelLabel.equals("Error") ? MZTabErrorType.Level.Error : MZTabErrorType.Level.Warn;

                        File file = new File(fileName);
                        try {
                            System.out.println("Begin check " + file.getAbsolutePath());
                            new MZTabFileParser(file, System.out, level);
                            System.out.println("Finish!");
                            System.out.println();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }

                        btnValidate.setEnabled(true);

                        return null;
                    }
                };

                worker.execute();
            }
        });

        validatePane.add(controlPane);
        return validatePane;
    }

    private JPanel getTitledPane(String title) {
        JPanel panel = new JPanel();

        panel.setLayout(new FlowLayout());
        panel.setBorder(new CompoundBorder(new TitledBorder(title), new EmptyBorder(5, 5, 5, 5)));

        return panel;
    }

    private JPanel getFileChoosePane(String title, final JTextField fileNameField,
                                     final FileNameExtensionFilter filter, final boolean directoryOnly) {
        final JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel(title));

        fileNameField.setEditable(false);
        fileNameField.setPreferredSize(new Dimension(400, 20));
        panel.add(fileNameField);
        JButton browseButton = new JButton("Browse...");
        panel.add(browseButton);

        browseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String lastedDirectory = ".";
                if (fileNameField.getText().length() != 0) {
                    lastedDirectory = directoryOnly ? fileNameField.getText() : new File(fileNameField.getText()).getParent();
                }

                JFileChooser chooser = new JFileChooser();
                if (filter != null) {
                    chooser.setFileFilter(filter);
                }
                chooser.setCurrentDirectory(new File(lastedDirectory));
                if (directoryOnly) {
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                }
                int result = chooser.showOpenDialog(panel);
                if(result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    fileNameField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        return panel;
    }

    private JPanel getParamsPane(String title, final ButtonGroup group) {
        final JPanel panel = getTitledPane(title);

        Enumeration<AbstractButton> elements = group.getElements();
        AbstractButton element;
        while (elements.hasMoreElements()) {
            element = elements.nextElement();
            panel.add(element);
        }

        return panel;
    }


}
