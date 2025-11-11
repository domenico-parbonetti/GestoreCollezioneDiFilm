package com.parbonetti.gestorefilm.view;

import com.parbonetti.gestorefilm.model.Movie;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainView extends JFrame {
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton saveButton;
    private JButton undoButton;
    private JButton loadButton;
    private FilterPanel filterPanel;
    private JComboBox<String> formatComboBox;

    private static final String[] COLUMN_NAMES = {
            "ID", "Titolo", "Regista", "Anno", "Genere", "Valutazione", "Stato"
    };

    public MainView() {
        initializeComponents();
        layoutComponents();
        configureWindow();
    }

    private void initializeComponents() {
        // Inizializza tabella
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabella non editabile direttamente
            }
        };
        movieTable = new JTable(tableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setAutoCreateRowSorter(true);

        movieTable.getColumnModel().getColumn(0).setMinWidth(0);
        movieTable.getColumnModel().getColumn(0).setMaxWidth(0);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        movieTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Titolo
        movieTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Regista
        movieTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Anno
        movieTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Genere
        movieTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Valutazione
        movieTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Stato

        // Inizializza bottoni
        addButton = new JButton("Aggiungi Film");
        editButton = new JButton("Modifica");
        deleteButton = new JButton("Elimina");
        saveButton = new JButton("Salva Collezione");
        loadButton = new JButton("Carica Collezione");
        undoButton = new JButton("Annulla (Ctrl+Z)");
        undoButton.setEnabled(false);
        undoButton.setToolTipText("Annulla l'ultima operazione");

        filterPanel = new FilterPanel();
        formatComboBox = new JComboBox<>(new String[]{"JSON", "CSV"});
        formatComboBox.setSelectedItem("JSON");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel superiore - Filtri
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.add(filterPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Centro - Tabella con scroll
        JScrollPane scrollPane = new JScrollPane(movieTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Collezione Film"));
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferiore - Bottoni e formato
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // Bottoni azioni
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(new JSeparator(SwingConstants.VERTICAL));  // Separatore visivo
        actionPanel.add(undoButton);
        bottomPanel.add(actionPanel, BorderLayout.WEST);

        // Formato e salvataggio
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.add(new JLabel("Formato:"));
        savePanel.add(formatComboBox);
        savePanel.add(saveButton);
        savePanel.add(loadButton);
        bottomPanel.add(savePanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void configureWindow() {
        setTitle("Gestione Film - Collezione Personale");
        setSize(1000, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null); // Centra la finestra
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showMovies(List<Movie> movies) {
        // Pulisci tabella
        tableModel.setRowCount(0);

        // Aggiungi ogni film come riga
        if (movies != null) {
            for (Movie movie : movies) {
                Object[] row = {
                        movie.getId(),
                        movie.getTitolo(),
                        movie.getRegista(),
                        movie.getAnnoUscita(),
                        movie.getGenere(),
                        "★".repeat(movie.getValutazione()),
                        movie.getStatoVisione().getDisplayName()
                };
                tableModel.addRow(row);
            }
        }
    }
    public String getSelectedMovieId() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = movieTable.convertRowIndexToModel(selectedRow);
            return (String) tableModel.getValueAt(modelRow, 0);
        }
        return null;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Informazione",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Errore",
                JOptionPane.ERROR_MESSAGE);
    }

    public boolean showConfirmation(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Conferma",
                JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getUndoButton() {
        return undoButton;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getLoadButton() {
        return loadButton;
    }

    public FilterPanel getFilterPanel() {
        return filterPanel;
    }

    public JComboBox<String> getFormatComboBox() {
        return formatComboBox;
    }

    public void display() {
        setVisible(true);
    }

    public String showSaveDialog(String format, String defaultFilename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salva Collezione");

        // Set filter based on format
        if (format.equals("JSON")) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "File JSON (*.json)", "json"));
        } else if (format.equals("CSV")) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "File CSV (*.csv)", "csv"));
        }

        fileChooser.setSelectedFile(new java.io.File(defaultFilename));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filepath = fileChooser.getSelectedFile().getAbsolutePath();

            if (filepath.endsWith(".json") || filepath.endsWith(".csv")) {
                filepath = filepath.substring(0, filepath.lastIndexOf('.'));
            }

            return filepath;
        }

        return null;
    }

    public String showLoadDialog(String format) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Carica Collezione");

        // Set filter based on format
        if (format.equals("JSON")) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "File JSON (*.json)", "json"));
        } else if (format.equals("CSV")) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "File CSV (*.csv)", "csv"));
        }

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filepath = fileChooser.getSelectedFile().getAbsolutePath();

            if (filepath.endsWith(".json") || filepath.endsWith(".csv")) {
                filepath = filepath.substring(0, filepath.lastIndexOf('.'));
            }

            return filepath;
        }

        return null;
    }

    public String getSelectedFormat() {
        return (String) formatComboBox.getSelectedItem();
    }

    public JFrame getParentFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(this);
    }
}
