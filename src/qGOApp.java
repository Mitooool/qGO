import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class qGOApp extends JFrame {

    // Components for file selection
    private JTextField filePathField;
    private JButton browseButton;

    // Components for GeneRegion input
    private JTextField regionNumberField;
    private JTextField startIndexField;
    private JTextField endIndexField;
    private JButton addRegionButton;
    private JButton removeRegionButton;

    // Table to display GeneRegions
    private JTable geneRegionsTable;
    private DefaultTableModel tableModel;

    // Run button
    private JButton runButton;

    public qGOApp() {
        setTitle("qGO - Quantifying the Diversity of Mitochondrial Genome Organization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize UI components
        initFileSelectionPanel();
        initGeneRegionPanel();
        initRunPanel();
    }

    private void initFileSelectionPanel() {
        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        filePanel.setBorder(BorderFactory.createTitledBorder("Select FASTA File"));

        filePathField = new JTextField();
        browseButton = new JButton("Browse");

        browseButton.addActionListener(e -> chooseFile());

        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);

        add(filePanel, BorderLayout.NORTH);
    }

    private void initGeneRegionPanel() {
        JPanel genePanel = new JPanel(new BorderLayout(5, 5));
        genePanel.setBorder(BorderFactory.createTitledBorder("Manage Gene Regions"));

        // Input fields with GridBagLayout for better control over spacing
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2); // Reduce space between components

        // Add Region Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Region Number:"), gbc);

        gbc.gridx = 1;
        regionNumberField = new JTextField();
        regionNumberField.setPreferredSize(new Dimension(200, 25)); // Set preferred width and height
        inputPanel.add(regionNumberField, gbc);

        // Add Starting Index
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Starting Index:"), gbc);

        gbc.gridx = 1;
        startIndexField = new JTextField();
        startIndexField.setPreferredSize(new Dimension(200, 25)); // Set preferred width and height
        inputPanel.add(startIndexField, gbc);

        // Add Ending Index
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Ending Index:"), gbc);

        gbc.gridx = 1;
        endIndexField = new JTextField();
        endIndexField.setPreferredSize(new Dimension(200, 25)); // Set preferred width and height
        inputPanel.add(endIndexField, gbc);

        genePanel.add(inputPanel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        addRegionButton = new JButton("Add GeneRegion");
        removeRegionButton = new JButton("Remove Selected");

        buttonPanel.add(addRegionButton);
        buttonPanel.add(removeRegionButton);

        genePanel.add(buttonPanel, BorderLayout.SOUTH); // Add buttons to the bottom

        // Table for displaying GeneRegions
        tableModel = new DefaultTableModel(new Object[]{"Region Number", "Start Index", "End Index"}, 0);
        geneRegionsTable = new JTable(tableModel);
        geneRegionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(geneRegionsTable);
        genePanel.add(tableScroll, BorderLayout.CENTER);

        // Action listeners
        addRegionButton.addActionListener(e -> addGeneRegion());
        removeRegionButton.addActionListener(e -> removeSelectedGeneRegion());

        add(genePanel, BorderLayout.CENTER);
    }





    private void initRunPanel() {
        JPanel runPanel = new JPanel();
        runButton = new JButton("Run");
        runPanel.add(runButton);

        runButton.addActionListener(e -> runProcessing());

        add(runPanel, BorderLayout.SOUTH);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(qGOApp.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            Path selectedPath = fileChooser.getSelectedFile().toPath();
            filePathField.setText(selectedPath.toString());
        }
    }

    private void addGeneRegion() {
        String regionNumStr = regionNumberField.getText().trim();
        String startIdxStr = startIndexField.getText().trim();
        String endIdxStr = endIndexField.getText().trim();

        if (regionNumStr.isEmpty() || startIdxStr.isEmpty() || endIdxStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all GeneRegion fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int regionNumber = Integer.parseInt(regionNumStr);
            int startIndex = Integer.parseInt(startIdxStr);
            int endIndex = Integer.parseInt(endIdxStr);

            // Validation
            if (startIndex < 0 || endIndex < startIndex) {
                JOptionPane.showMessageDialog(this, "Invalid indices. Ensure that Starting Index >= 0 and Ending Index >= Starting Index.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate Region Numbers
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == regionNumber) {
                    JOptionPane.showMessageDialog(this, "Region Number already exists. Please enter a unique Region Number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Add to table
            tableModel.addRow(new Object[]{regionNumber, startIndex, endIndex});

            // Clear input fields
            regionNumberField.setText("");
            startIndexField.setText("");
            endIndexField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedGeneRegion() {
        int selectedRow = geneRegionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a GeneRegion to remove.", "Selection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runProcessing() {
        String fileName = filePathField.getText().trim();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify the file path.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Collect GeneRegions
        List<Main.GeneRegion> geneRegions = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int regionNumber = (int) tableModel.getValueAt(i, 0);
            int startIndex = (int) tableModel.getValueAt(i, 1);
            int endIndex = (int) tableModel.getValueAt(i, 2);
            geneRegions.add(new Main.GeneRegion(regionNumber, startIndex, endIndex));
        }

        // Confirm execution
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to run the processing?", "Confirm Run", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Disable Run button to prevent multiple clicks
        runButton.setEnabled(false);

        // Run the processing in a separate thread to avoid freezing the GUI
        new Thread(() -> {
            try {
                // Read all lines from the file
                List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

                // Read even lines (assuming readEvenLines is defined in Main.java)
                List<String> geneLines = Main.readEvenLines(fileName);

                ArrayList<String> benchmark1 = new ArrayList<>();
                String benchmarkStr = geneLines.get(0);

                String[] string = benchmarkStr.split(",");
                for (String str : string)
                    benchmark1.add(str);

                // 待量化基因数据
                String species1 = "";
                for (String line : lines) {
                    if ('>' == (line.charAt(0)) && !"".equals(line)) {
                        species1 = species1 + line.substring(1) + "\n";
                    } else species1 = species1 + line + "\n";
                }

                System.out.println("----------------------RS matrix----------------------");
                for (String line : geneLines) {
                    // Process each gene line
                    List<String> benchmark = new ArrayList<>();
                    String[] strings = line.split(",");
                    Collections.addAll(benchmark, strings);

                    // Process species data
                    StringBuilder speciesBuilder = new StringBuilder();
                    for (String line1 : lines) {
                        if (line1.startsWith(">") && !line1.isEmpty()) {
                            speciesBuilder.append(line1.substring(1)).append("\n");
                        } else {
                            speciesBuilder.append(line1).append("\n");
                        }
                    }
                    String species = speciesBuilder.toString();

                    // Get result by invoking Main.getResult
                    Map<String, Object> result = Main.getResult((ArrayList<String>) benchmark, species, geneRegions);

                    // Output to console
//                    System.out.println(result);
                }
                System.out.println("----------------------RF----------------------");
                Map<String, Integer> frequency = Main.getResult1(benchmark1, species1, geneRegions);

                JOptionPane.showMessageDialog(this, "Processing completed. Check the console for results.", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading the file: " + ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Re-enable Run button
                runButton.setEnabled(true);
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            qGOApp app = new qGOApp();
            app.setVisible(true);
        });
    }
}