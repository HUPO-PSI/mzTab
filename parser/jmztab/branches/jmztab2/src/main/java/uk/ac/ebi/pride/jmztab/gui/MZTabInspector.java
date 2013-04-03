package uk.ac.ebi.pride.jmztab.gui;

import uk.ac.ebi.pride.jmztab.errors.MZTabErrorList;
import uk.ac.ebi.pride.jmztab.errors.MZTabErrorType;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileChecker;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileConverter;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileMerger;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.jmztab.utils.convert.ConvertFile;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
        mainPane.addTab("Convert", getConvertPane());
        mainPane.addTab("Merge", getMergePane());
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
        levelGroup.add(new JRadioButton("Warn", true));
        levelGroup.add(new JRadioButton("Error"));
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

    private File getConvertFile(String outDir, String fileName) {
        fileName = fileName.replaceAll("\\.", "_") + ".mztab";
        return new File(outDir, fileName);
    }

    private JPanel getConvertPane() {
        JPanel srcFilePane = new JPanel(new FlowLayout());
        final JTextField srcFileNameField = new JTextField();
        JPanel srcFileChoosePane = getFileChoosePane(
                "Choose Converted File: ", srcFileNameField,
                new FileNameExtensionFilter("PRIDE XML File(*.xml), mzIdentML File (*.mzid)", "xml", "mzid"), false);
        srcFilePane.add(srcFileChoosePane);

        final ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(new JRadioButton("PRIDE", true));
        formatGroup.add(new JRadioButton("mzIdentML"));
        JPanel formatPane = getParamsPane("Format", formatGroup);
        srcFilePane.add(formatPane);

        JPanel tarFilePane = new JPanel(new FlowLayout());

        final JTextField tarFileDirField = new JTextField();
        JPanel tarFileChoosePane = getFileChoosePane(
                "Choose Target Directory: ", tarFileDirField,
                null, true);
        tarFilePane.add(tarFileChoosePane);

        final ButtonGroup levelGroup = new ButtonGroup();
        levelGroup.add(new JRadioButton("Warn", true));
        levelGroup.add(new JRadioButton("Error"));
        JPanel paramPane = getParamsPane("Message Level", levelGroup);
        tarFilePane.add(paramPane);

        JPanel actionPane = getTitledPane("Action");
        final JButton btnConvert = new JButton("Convert");
        actionPane.add(btnConvert);
        tarFilePane.add(actionPane);

        btnConvert.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingWorker worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        btnConvert.setEnabled(false);
                        consolePane.clearContent();

                        String fileName = srcFileNameField.getText();
                        if (fileName == null || fileName.trim().length() == 0) {
                            JOptionPane.showMessageDialog(MZTabInspector.this, "Please choose a file!");
                            return null;
                        }

                        String outDirName = tarFileDirField.getText();
                        if (outDirName == null || outDirName.trim().length() == 0) {
                            JOptionPane.showMessageDialog(MZTabInspector.this, "Please choose a target directory!");
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
                        MZTabErrorType.Level level = MZTabErrorType.findLevel(levelLabel);

                        elements = formatGroup.getElements();
                        String formatLabel = "PRIDE";
                        while (elements.hasMoreElements()) {
                            element = elements.nextElement();
                            if (element.isSelected()) {
                                formatLabel = element.getText();
                                break;
                            }
                        }
                        ConvertFile.Format format = MZTabFileConverter.findFormat(formatLabel);
                        File inFile = new File(fileName);

                        System.out.println("Begin reading " + inFile.getName());
                        MZTabFileConverter converter;
                        try {
                            converter = new MZTabFileConverter(inFile, format);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(MZTabInspector.this, "There exists errors during read file process.");
                            return null;
                        }

                        System.out.println("Begin convert to mztab file.");
                        MZTabFile tabFile = converter.getMZTabFile();
                        MZTabErrorList errorList = new MZTabErrorList();
                        System.out.println("Being check new mztab file.");
                        MZTabFileChecker checker = new MZTabFileChecker(errorList);

                        checker.check(tabFile, level);
                        try {
                            if (errorList.isEmpty(level)) {
                                System.out.println("No errors in mztab.");
                                File outFile = getConvertFile(outDirName, inFile.getName());
                                System.out.println("Begin print mzTab file, which name is : " + outFile.getAbsolutePath());
                                tabFile.printMZTab(new FileOutputStream(outFile));
                                System.out.println("Finish!");
                                System.out.println();
                            } else {
                                errorList.print(System.out, level);
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }

                        btnConvert.setEnabled(true);

                        return null;
                    }
                };

                worker.execute();
            }
        });

        JPanel convertPane = new JPanel(new BorderLayout());
        convertPane.add(srcFilePane, BorderLayout.NORTH);
        convertPane.add(tarFilePane, BorderLayout.CENTER);
        return convertPane;
    }

    private class MZTabFileListPane extends JPanel {
        Map<String, MZTabFile> tabFileMap = new HashMap<String, MZTabFile>();
        java.util.List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();

        MZTabErrorType.Level level = null;

        private MZTabFileListPane() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        public void addFile(File file) {
            if (! tabFileMap.containsKey(file.getAbsolutePath())) {
                tabFileMap.put(file.getAbsolutePath(), null);
                JCheckBox checkBox = new JCheckBox(file.getAbsolutePath(), true);
                add(checkBox);
                checkBoxList.add(checkBox);
                revalidate();
                repaint();
                this.level = null;
            }
        }

        public boolean fillMZTabFiles(MZTabErrorType.Level level) {
            boolean success = true;
            Map<String, MZTabFile> newTabFileMap = new HashMap<String, MZTabFile>();

            for (String fileName : tabFileMap.keySet()) {
                File file = new File(fileName);

                try {
                    System.out.println("Begin check " + fileName);
                    MZTabFileParser parser = new MZTabFileParser(file, System.out, level);

                    if (parser.getErrorList().isEmpty(level)) {
                        newTabFileMap.put(fileName, parser.getMZTabFile());
                    } else {
                        success = false;
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                System.out.println("Finish!");
                System.out.println();
            }

            if (success) {
                tabFileMap = newTabFileMap;
            }

            return success;
        }

        public List<MZTabFile> getSelectedMZTabFileList(MZTabErrorType.Level level) {
            if (level != this.level) {
                boolean success = fillMZTabFiles(level);
                if (! success) {
                    return new ArrayList<MZTabFile>();
                }
                this.level = level;
            }

            List<MZTabFile> tabFiles = new ArrayList<MZTabFile>();

            for (JCheckBox checkBox : checkBoxList) {
                if (checkBox.isSelected()) {
                    tabFiles.add(tabFileMap.get(checkBox.getText()));
                }
            }
            return tabFiles;
        }
    }

    private String getMergeFileName() {
        return "merge_" + System.currentTimeMillis() + ".mztab";
    }

    private JPanel getMergePane() {
        JPanel mergePane = new JPanel(new BorderLayout());

        JPanel srcPane = new JPanel(new FlowLayout());

        final JTextField fileNameField = new JTextField();
        JPanel fileChoosePane = getFileChoosePane(
                "Choose MZTabFile: ", fileNameField,
                new FileNameExtensionFilter("mzTab File (*.mztab, *.txt)", "mztab", "txt"), false);
        srcPane.add(fileChoosePane);
        JButton btnAdd = new JButton("Add");
        srcPane.add(btnAdd);

        final MZTabFileListPane mzTabFileListPane = new MZTabFileListPane();
        JScrollPane filePane = new JScrollPane(mzTabFileListPane);
        mzTabFileListPane.setBackground(Color.WHITE);
        filePane.setPreferredSize(new Dimension(400, 100));
        srcPane.add(getTitledPane("Choose need merge files: ").add(filePane));

        JPanel tarPane = new JPanel(new FlowLayout());
        final JTextField tarFileDirField = new JTextField();
        JPanel tarFileChoosePane = getFileChoosePane(
                "Choose Target Directory: ", tarFileDirField,
                null, true);
        tarPane.add(tarFileChoosePane);

        final ButtonGroup levelGroup = new ButtonGroup();
        levelGroup.add(new JRadioButton("Warn", true));
        levelGroup.add(new JRadioButton("Error"));
        JPanel paramPane = getParamsPane("Message Level", levelGroup);
        tarPane.add(paramPane);

        JPanel combinePane = getTitledPane("Params");
        final JCheckBox cbxCombine = new JCheckBox("Combine SubSample Columns");
        combinePane.add(cbxCombine);
        tarPane.add(combinePane);

        JPanel actionPane = getTitledPane("Action");
        final JButton btnMerge = new JButton("Merge");
        actionPane.add(btnMerge);
        tarPane.add(actionPane);

        mergePane.add(srcPane, BorderLayout.NORTH);
        mergePane.add(tarPane, BorderLayout.CENTER);

        btnAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String fileName = fileNameField.getText();
                if (fileName == null || fileName.trim().length() == 0) {
                    JOptionPane.showMessageDialog(MZTabInspector.this, "Please choose a file!");
                    return;
                }

                mzTabFileListPane.addFile(new File(fileName));
            }
        });

        btnMerge.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingWorker worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        btnMerge.setEnabled(false);

                        String outDirName = tarFileDirField.getText();
                        if (outDirName == null || outDirName.trim().length() == 0) {
                            JOptionPane.showMessageDialog(MZTabInspector.this, "Please choose a target directory!");
                            return null;
                        }

                        consolePane.clearContent();

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
                        MZTabErrorType.Level level = MZTabErrorType.findLevel(levelLabel);

                        List<MZTabFile> tabFileList = mzTabFileListPane.getSelectedMZTabFileList(level);
                        if (tabFileList.isEmpty()) {
                            return null;
                        }

                        String mergeFileName = getMergeFileName();
                        File mergeFile = new File(outDirName, mergeFileName);
                        boolean combine = cbxCombine.isSelected();

                        System.out.println("Begin merge mztab files.....");
                        MZTabFileMerger merger = new MZTabFileMerger();
                        merger.addAllTabFiles(tabFileList);
                        merger.setCombine(combine);
                        MZTabFile mergedMZTabFile = merger.merge();
                        System.out.println("Begin check new mztab file");
                        MZTabErrorList errorList = new MZTabErrorList();
                        MZTabFileChecker checker = new MZTabFileChecker(errorList);
                        checker.check(mergedMZTabFile, level);
                        try {
                            if (errorList.isEmpty(level)) {
                                System.out.println("Write new mztab file, file name is: " + mergeFile.getAbsolutePath());
                                FileOutputStream out = new FileOutputStream(mergeFile);
                                mergedMZTabFile.printMZTab(out);
                                out.close();
                            } else {
                                errorList.print(System.out, level);
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }

                        System.out.printf("Finish!");
                        System.out.println();

                        btnMerge.setEnabled(true);
                        return null;
                    }
                };

                worker.execute();
            }
        });


        return mergePane;
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
