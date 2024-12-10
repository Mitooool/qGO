//// qGOApp.java
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.*;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//import java.util.*;
//import java.util.List;
//
//// Import Apache POI libraries for Excel export
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//public class qGOApp extends JFrame {
//
//    // Components for input selection
//    private JRadioButton fileRadioButton;
//    private JRadioButton textRadioButton;
//    private ButtonGroup inputGroup;
//
//    // Components for file selection
//    private JTextField filePathField;
//    private JButton browseButton;
//
//    // Components for text input
//    private JTextArea textInputArea;
//
//    // Components for central sequence
//    private JTextArea centralSequenceArea;
//
//    // Components for GeneRegion input
//    private JTextField regionNumberField;
//    private JTextField startIndexField;
//    private JTextField endIndexField;
//    private JButton addRegionButton;
//    private JButton removeRegionButton;
//
//    // Table to display GeneRegions
//    private JTable geneRegionsTable;
//    private DefaultTableModel geneRegionsTableModel;
//
//    // Run and Export buttons
//    private JButton runButton;
//    private JButton exportButton;
//
//    // Table to display results
//    private JTable resultsTable;
//    private DefaultTableModel resultsTableModel;
//
//    public qGOApp() {
//        setTitle("qGO - Quantifying the Diversity of Mitochondrial Genome Organization");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(1200, 700);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout());
//
//        // Initialize UI components
//        initMainPanel();
//    }
//
//    private void initMainPanel() {
//        // Split the main panel into left and right
//        JSplitPane splitPane = new JSplitPane();
//        splitPane.setDividerLocation(500);
//        splitPane.setResizeWeight(0.5);
//
//        // Left Panel for Inputs
//        JPanel leftPanel = new JPanel();
//        leftPanel.setLayout(new BorderLayout());
//        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        // Input Selection Panel (File or Text)
//        JPanel inputSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        fileRadioButton = new JRadioButton("Select File");
//        textRadioButton = new JRadioButton("Input Text");
//        inputGroup = new ButtonGroup();
//        inputGroup.add(fileRadioButton);
//        inputGroup.add(textRadioButton);
//        fileRadioButton.setSelected(true);
//
//        inputSelectionPanel.add(fileRadioButton);
//        inputSelectionPanel.add(textRadioButton);
//
//        leftPanel.add(inputSelectionPanel, BorderLayout.NORTH);
//
//        // File Selection Panel
//        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
//        filePanel.setBorder(BorderFactory.createTitledBorder("Select FASTA File"));
//
//        filePathField = new JTextField();
//        browseButton = new JButton("Browse");
//
//        browseButton.addActionListener(e -> chooseFile());
//
//        filePanel.add(filePathField, BorderLayout.CENTER);
//        filePanel.add(browseButton, BorderLayout.EAST);
//
//        // Text Input Panel
//        JPanel textInputPanel = new JPanel(new BorderLayout(5, 5));
//        textInputPanel.setBorder(BorderFactory.createTitledBorder("Input Data"));
//        textInputArea = new JTextArea(5, 30);
//        textInputArea.setEnabled(false); // Initially disabled
//        JScrollPane textScroll = new JScrollPane(textInputArea);
//        textInputPanel.add(textScroll, BorderLayout.CENTER);
//
//        // Toggle between File and Text Input
//        fileRadioButton.addActionListener(e -> {
//            filePanel.setVisible(true);
//            textInputArea.setEnabled(false);
//        });
//
//        textRadioButton.addActionListener(e -> {
//            filePanel.setVisible(false);
//            textInputArea.setEnabled(true);
//        });
//
//        // Central Sequence Panel
//        JPanel centralSequencePanel = new JPanel(new BorderLayout(5, 5));
//        centralSequencePanel.setBorder(BorderFactory.createTitledBorder("Central Sequence (Benchmark)"));
//        centralSequenceArea = new JTextArea(3, 30);
//        JScrollPane centralScroll = new JScrollPane(centralSequenceArea);
//        centralSequencePanel.add(centralScroll, BorderLayout.CENTER);
//
//        // GeneRegion Management Panel
//        JPanel geneRegionPanel = new JPanel(new BorderLayout(5,5));
//        geneRegionPanel.setBorder(BorderFactory.createTitledBorder("Manage Gene Regions"));
//
//        // Input fields with GridBagLayout for better control over spacing
//        JPanel inputPanel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.insets = new Insets(2, 2, 2, 2); // Reduce space between components
//
//        // Add Region Number
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        inputPanel.add(new JLabel("Region Number:"), gbc);
//
//        gbc.gridx = 1;
//        regionNumberField = new JTextField();
//        regionNumberField.setPreferredSize(new Dimension(150, 25)); // Set preferred width and height
//        inputPanel.add(regionNumberField, gbc);
//
//        // Add Starting Index
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        inputPanel.add(new JLabel("Starting Index:"), gbc);
//
//        gbc.gridx = 1;
//        startIndexField = new JTextField();
//        startIndexField.setPreferredSize(new Dimension(150, 25)); // Set preferred width and height
//        inputPanel.add(startIndexField, gbc);
//
//        // Add Ending Index
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        inputPanel.add(new JLabel("Ending Index:"), gbc);
//
//        gbc.gridx = 1;
//        endIndexField = new JTextField();
//        endIndexField.setPreferredSize(new Dimension(150, 25)); // Set preferred width and height
//        inputPanel.add(endIndexField, gbc);
//
//        geneRegionPanel.add(inputPanel, BorderLayout.NORTH);
//
//        // Buttons Panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        addRegionButton = new JButton("Add GeneRegion");
//        removeRegionButton = new JButton("Remove Selected");
//
//        buttonPanel.add(addRegionButton);
//        buttonPanel.add(removeRegionButton);
//
//        geneRegionPanel.add(buttonPanel, BorderLayout.SOUTH); // Add buttons to the bottom
//
//        // Table for displaying GeneRegions
//        geneRegionsTableModel = new DefaultTableModel(new Object[]{"Region Number", "Start Index", "End Index"}, 0);
//        geneRegionsTable = new JTable(geneRegionsTableModel);
//        geneRegionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane geneTableScroll = new JScrollPane(geneRegionsTable);
//        geneRegionPanel.add(geneTableScroll, BorderLayout.CENTER);
//
//        // Action listeners for GeneRegion buttons
//        addRegionButton.addActionListener(e -> addGeneRegion());
//        removeRegionButton.addActionListener(e -> removeSelectedGeneRegion());
//
//        // Combine all input panels in leftPanel
//        JPanel inputsContainer = new JPanel();
//        inputsContainer.setLayout(new BoxLayout(inputsContainer, BoxLayout.Y_AXIS));
//        inputsContainer.add(filePanel);
//        inputsContainer.add(textInputPanel);
//        inputsContainer.add(centralSequencePanel);
//        inputsContainer.add(geneRegionPanel);
//
//        leftPanel.add(inputsContainer, BorderLayout.CENTER);
//
//        // Run and Export Buttons Panel
//        JPanel runExportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
//        runButton = new JButton("Run");
//        exportButton = new JButton("Export Results");
//        exportButton.setEnabled(false); // Initially disabled
//
//        runExportPanel.add(runButton);
//        runExportPanel.add(exportButton);
//
//        leftPanel.add(runExportPanel, BorderLayout.SOUTH);
//
//        // Right Panel for Results
//        JPanel rightPanel = new JPanel(new BorderLayout());
//        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//        rightPanel.setBorder(BorderFactory.createTitledBorder("Results"));
//
//        // Table for displaying results (Symmetric Matrix)
//        resultsTableModel = new DefaultTableModel();
//        resultsTable = new JTable(resultsTableModel);
//        JScrollPane resultsScroll = new JScrollPane(resultsTable);
//        rightPanel.add(resultsScroll, BorderLayout.CENTER);
//
//        // Add left and right panels to splitPane
//        splitPane.setLeftComponent(leftPanel);
//        splitPane.setRightComponent(rightPanel);
//
//        add(splitPane, BorderLayout.CENTER);
//
//        // Action listeners for Run and Export buttons
//        runButton.addActionListener(e -> runProcessing());
//        exportButton.addActionListener(e -> exportResults());
//    }
//
//    private void chooseFile() {
//        JFileChooser fileChooser = new JFileChooser();
//        int option = fileChooser.showOpenDialog(qGOApp.this);
//        if (option == JFileChooser.APPROVE_OPTION) {
//            Path selectedPath = fileChooser.getSelectedFile().toPath();
//            filePathField.setText(selectedPath.toString());
//        }
//    }
//
//    private void addGeneRegion() {
//        String regionNumStr = regionNumberField.getText().trim();
//        String startIdxStr = startIndexField.getText().trim();
//        String endIdxStr = endIndexField.getText().trim();
//
//        if (regionNumStr.isEmpty() || startIdxStr.isEmpty() || endIdxStr.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please fill all GeneRegion fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        try {
//            int regionNumber = Integer.parseInt(regionNumStr);
//            int startIndex = Integer.parseInt(startIdxStr);
//            int endIndex = Integer.parseInt(endIdxStr);
//
//            // Validation
//            if (startIndex < 0 || endIndex < startIndex) {
//                JOptionPane.showMessageDialog(this, "Invalid indices. Ensure that Starting Index >= 0 and Ending Index >= Starting Index.", "Input Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Check for duplicate Region Numbers
//            for (int i = 0; i < geneRegionsTableModel.getRowCount(); i++) {
//                if ((int) geneRegionsTableModel.getValueAt(i, 0) == regionNumber) {
//                    JOptionPane.showMessageDialog(this, "Region Number already exists. Please enter a unique Region Number.", "Input Error", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }
//            }
//
//            // Add to table
//            geneRegionsTableModel.addRow(new Object[]{regionNumber, startIndex, endIndex});
//
//            // Clear input fields
//            regionNumberField.setText("");
//            startIndexField.setText("");
//            endIndexField.setText("");
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Please enter valid integers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private void removeSelectedGeneRegion() {
//        int selectedRow = geneRegionsTable.getSelectedRow();
//        if (selectedRow >= 0) {
//            geneRegionsTableModel.removeRow(selectedRow);
//        } else {
//            JOptionPane.showMessageDialog(this, "Please select a GeneRegion to remove.", "Selection Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private void runProcessing() {
//        // Validate input selection
//        boolean isFileSelected = fileRadioButton.isSelected();
//        boolean isTextInput = textRadioButton.isSelected();
//
//        String inputData = "";
//        if (isFileSelected) {
//            String fileName = filePathField.getText().trim();
//            if (fileName.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Please specify the file path.", "Input Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            inputData = fileName;
//        } else {
//            inputData = textInputArea.getText().trim();
//            if (inputData.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Please enter the data in the text box.", "Input Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//        }
//
//        // Get central sequence (benchmark)
//        String centralSequence = centralSequenceArea.getText().trim();
//        if (centralSequence.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please enter the central sequence.", "Input Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        // Collect GeneRegions
//        List<Main.GeneRegion> geneRegions = new ArrayList<>();
//        for (int i = 0; i < geneRegionsTableModel.getRowCount(); i++) {
//            int regionNumber = (int) geneRegionsTableModel.getValueAt(i, 0);
//            int startIndex = (int) geneRegionsTableModel.getValueAt(i, 1);
//            int endIndex = (int) geneRegionsTableModel.getValueAt(i, 2);
//            geneRegions.add(new Main.GeneRegion(regionNumber, startIndex, endIndex));
//        }
//
//        // Confirm execution
//        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to run the processing?", "Confirm Run", JOptionPane.YES_NO_OPTION);
//        if (confirm != JOptionPane.YES_OPTION) {
//            return;
//        }
//
//        // Disable Run button to prevent multiple clicks
//        runButton.setEnabled(false);
//        exportButton.setEnabled(false); // Disable export until processing is done
//
//        // Clear previous results
//        resultsTableModel.setRowCount(0);
//        resultsTableModel.setColumnCount(0);
//
//        // Run the processing in a separate thread to avoid freezing the GUI
//        String finalInputData = inputData;
//        new Thread(() -> {
//            try {
//                // Read input data
//                List<String> geneLines = new ArrayList<>();
//                if (isFileSelected) {
//                    geneLines = Main.readEvenLines(finalInputData); // Assuming readEvenLines is defined in Main.java
//                } else {
//                    // Assume each line in textInputArea represents a gene line
//                    String[] lines = finalInputData.split("\\R");
//                    for (String line : lines) {
//                        geneLines.add(line.trim());
//                    }
//                }
//
//                // Read species data
//                String speciesData = "";
//                if (isFileSelected) {
//                    List<String> lines = Files.readAllLines(Paths.get(finalInputData), StandardCharsets.UTF_8);
//                    StringBuilder speciesBuilder = new StringBuilder();
//                    for (String line : lines) {
//                        if (line.startsWith(">") && !line.isEmpty()) {
//                            speciesBuilder.append(line.substring(1)).append("\n");
//                        } else {
//                            speciesBuilder.append(line).append("\n");
//                        }
//                    }
//                    speciesData = speciesBuilder.toString();
//                } else {
//                    speciesData = finalInputData;
//                }
//
//                // Assume centralSequence is the benchmark
//                String[] benchmarkGenes = centralSequence.split(",");
//                ArrayList<String> benchmarkList = new ArrayList<>();
//                for (String gene : benchmarkGenes) {
//                    benchmarkList.add(gene.trim());
//                }
//
//                // Prepare a list of all gene sequences to use as benchmarks
//                List<String> allBenchmarks = benchmarkList;
//
//                // Initialize a map to hold all frequencies
//                Map<String, Map<String, Integer>> symmetricMatrix = new LinkedHashMap<>();
//
//                // Initialize the symmetric matrix keys
//                for (String geneA : allBenchmarks) {
//                    symmetricMatrix.put(geneA, new LinkedHashMap<>());
//                    for (String geneB : allBenchmarks) {
//                        symmetricMatrix.get(geneA).put(geneB, 0); // Initialize frequencies to 0
//                    }
//                }
//
//                // Iterate through each benchmark gene and compute frequencies against all benchmarks
//                for (String benchmarkGene : allBenchmarks) {
//                    ArrayList<String> currentBenchmark = new ArrayList<>();
//                    currentBenchmark.add(benchmarkGene);
//
//                    // Call Main.getResult for the current benchmark
//                    Map<String, Integer> frequency = Main.getResult1(currentBenchmark, speciesData, geneRegions);
//
//                    // For each target gene, update the symmetric matrix
//                    for (String targetGene : allBenchmarks) {
//                        int freq = frequency.getOrDefault(targetGene, 0);
//                        symmetricMatrix.get(benchmarkGene).put(targetGene, freq);
//                    }
//
//                    // Optional: Output to console
//                    System.out.println("Benchmark: " + benchmarkGene + " -> " + frequency);
//                }
//
//                // Update the results table in the Event Dispatch Thread
//                SwingUtilities.invokeLater(() -> {
//                    // Set up the table columns
//                    List<String> columns = new ArrayList<>();
//                    columns.add("Benchmark \\ Target");
//                    columns.addAll(allBenchmarks);
//
//                    // Set the table model
//                    resultsTableModel.setColumnIdentifiers(columns.toArray());
//
//                    // Populate the table rows
//                    for (String benchmarkGene : allBenchmarks) {
//                        List<Object> row = new ArrayList<>();
//                        row.add(benchmarkGene);
//                        for (String targetGene : allBenchmarks) {
//                            int freq = symmetricMatrix.get(benchmarkGene).get(targetGene);
//                            row.add(freq);
//                        }
//                        resultsTableModel.addRow(row.toArray());
//                    }
//
//                    JOptionPane.showMessageDialog(this, "Processing completed. Check the results table.", "Success", JOptionPane.INFORMATION_MESSAGE);
//                    exportButton.setEnabled(true); // Enable export button
//                });
//
//            } catch (IOException ex) {
//                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error reading the file: " + ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE));
//            } catch (Exception ex) {
//                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
//            } finally {
//                SwingUtilities.invokeLater(() -> runButton.setEnabled(true));
//            }
//        }).start();
//    }
//
//    private void exportResults() {
//        if (resultsTableModel.getRowCount() == 0 || resultsTableModel.getColumnCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No results to export.", "Export Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Specify a file to save");
//        // Set default file name
//        fileChooser.setSelectedFile(new java.io.File("results.xlsx"));
//
//        int userSelection = fileChooser.showSaveDialog(this);
//        if (userSelection != JFileChooser.APPROVE_OPTION) {
//            return;
//        }
//
//        java.io.File fileToSave = fileChooser.getSelectedFile();
//
//        // Ensure the file has .xlsx extension
//        String filePath = fileToSave.getAbsolutePath();
//        if (!filePath.toLowerCase().endsWith(".xlsx")) {
//            fileToSave = new java.io.File(filePath + ".xlsx");
//        }
//
//        // Create Excel workbook and sheet
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Results");
//
//        // Create header row
//        Row headerRow = sheet.createRow(0);
//        for (int i = 0; i < resultsTableModel.getColumnCount(); i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(resultsTableModel.getColumnName(i));
//        }
//
//        // Populate data rows
//        for (int i = 0; i < resultsTableModel.getRowCount(); i++) {
//            Row row = sheet.createRow(i + 1);
//            for (int j = 0; j < resultsTableModel.getColumnCount(); j++) {
//                Cell cell = row.createCell(j);
//                Object value = resultsTableModel.getValueAt(i, j);
//                if (value instanceof String) {
//                    cell.setCellValue((String) value);
//                } else if (value instanceof Integer) {
//                    cell.setCellValue((Integer) value);
//                }
//            }
//        }
//
//        // Autosize columns
//        for (int i = 0; i < resultsTableModel.getColumnCount(); i++) {
//            sheet.autoSizeColumn(i);
//        }
//
//        // Write the output to file
//        try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
//            workbook.write(fileOut);
//            workbook.close();
//            JOptionPane.showMessageDialog(this, "Results exported successfully to:\n" + fileToSave.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(this, "Error exporting the file: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    public static void main(String[] args) {
//        // Ensure Apache POI dependencies are available
//        //    try {
//        //        Class.forName("org.apache.poi.xssf.usermodel.XSSFWorkbook");
//        //    } catch (ClassNotFoundException e) {
//        //        JOptionPane.showMessageDialog(null, "Apache POI library not found. Please include Apache POI in the classpath.", "Dependency Error", JOptionPane.ERROR_MESSAGE);
//        //        System.exit(1);
//        //    }
//
//        SwingUtilities.invokeLater(() -> {
//            qGOApp app = new qGOApp();
//            app.setVisible(true);
//        });
//    }
//}
