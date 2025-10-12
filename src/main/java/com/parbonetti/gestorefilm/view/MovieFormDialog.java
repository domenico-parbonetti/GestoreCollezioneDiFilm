package com.parbonetti.gestorefilm.view;

import com.parbonetti.gestorefilm.model.Movie;
import com.parbonetti.gestorefilm.model.ViewingStatus;

import javax.swing.*;
import java.awt.*;

public class MovieFormDialog extends JDialog {
    private JTextField titoloField;
    private JTextField registaField;
    private JSpinner annoSpinner;
    private JComboBox<String> genreComboBox;
    private JSlider valutazioneSlider;
    private JLabel valutazioneLabel;
    private JComboBox<String> statusComboBox;

    // Bottoni
    private JButton saveButton;
    private JButton cancelButton;

    // Stato
    private Movie movie; // Risultato del dialog
    private boolean confirmed = false;

    public MovieFormDialog(Frame parent) {
        super(parent, "Aggiungi Film", true); // true = modal
        initializeComponents();
        layoutComponents();
        setupListeners();
        configureDialog();
    }

    public MovieFormDialog(Frame parent, Movie movie) {
        super(parent, "Modifica Film", true);
        initializeComponents();
        layoutComponents();
        setupListeners();
        loadMovieData(movie);
        configureDialog();
    }

    private void initializeComponents() {
        // Campo titolo
        titoloField = new JTextField(30);

        // Campo regista
        registaField = new JTextField(30);

        // Spinner anno (da 1888 - primo film - a anno corrente + 5)
        int currentYear = java.time.Year.now().getValue();
        SpinnerNumberModel yearModel = new SpinnerNumberModel(
                currentYear,  // valore iniziale
                1888,         // minimo
                currentYear + 5,  // massimo
                1             // step
        );
        annoSpinner = new JSpinner(yearModel);

        // ComboBox genere (stessi di FilterPanel)
        genreComboBox = new JComboBox<>(new String[]{
                "Azione",
                "Avventura",
                "Animazione",
                "Commedia",
                "Documentario",
                "Drammatico",
                "Fantasy",
                "Horror",
                "Musical",
                "Romantico",
                "Fantascienza",
                "Thriller",
                "Western"
        });

        // Slider valutazione (1-5 stelle)
        valutazioneSlider = new JSlider(1, 5, 3);
        valutazioneSlider.setMajorTickSpacing(1);
        valutazioneSlider.setPaintTicks(true);
        valutazioneSlider.setPaintLabels(true);
        valutazioneSlider.setSnapToTicks(true);

        // Label per mostrare valutazione corrente
        valutazioneLabel = new JLabel("3/5 ★★★");
        valutazioneLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        // ComboBox stato visione
        statusComboBox = new JComboBox<>(new String[]{
                ViewingStatus.DA_VEDERE.getDisplayName(),
                ViewingStatus.IN_VISIONE.getDisplayName(),
                ViewingStatus.VISTO.getDisplayName()
        });

        // Bottoni
        saveButton = new JButton("Salva");
        cancelButton = new JButton("Annulla");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel principale con form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Riga 0 - Titolo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Titolo:*"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(titoloField, gbc);

        // Riga 1 - Regista
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Regista:*"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(registaField, gbc);

        // Riga 2 - Anno
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Anno di uscita:*"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(annoSpinner, gbc);

        // Riga 3 - Genere
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Genere:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(genreComboBox, gbc);

        // Riga 4 - Valutazione (label)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Valutazione:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(valutazioneLabel, gbc);

        // Riga 5 - Valutazione (slider)
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(valutazioneSlider, gbc);

        // Riga 6 - Stato
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Stato visione:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(statusComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Panel bottoni
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Label nota campi obbligatori
        JLabel noteLabel = new JLabel("* Campi obbligatori");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC, 11f));
        noteLabel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        add(noteLabel, BorderLayout.NORTH);
    }

    private void setupListeners() {
        // Listener per slider valutazione - aggiorna label
        valutazioneSlider.addChangeListener(e -> {
            int value = valutazioneSlider.getValue();
            String stars = "★".repeat(value);
            valutazioneLabel.setText(value + "/5 " + stars);
        });

        // Listener bottone Salva
        saveButton.addActionListener(e -> handleSave());

        // Listener bottone Annulla
        cancelButton.addActionListener(e -> handleCancel());

        // Enter su text field = salva
        titoloField.addActionListener(e -> handleSave());
        registaField.addActionListener(e -> handleSave());
    }

    private void configureDialog() {
        pack(); // Dimensiona automaticamente
        setMinimumSize(new Dimension(500, 400));
        setLocationRelativeTo(getParent()); // Centra rispetto al parent
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void loadMovieData(Movie movie) {
        if (movie != null) {
            titoloField.setText(movie.getTitolo());
            registaField.setText(movie.getRegista());
            annoSpinner.setValue(movie.getAnnoUscita());
            genreComboBox.setSelectedItem(movie.getGenere());
            valutazioneSlider.setValue(movie.getValutazione());
            statusComboBox.setSelectedItem(movie.getStatoVisione().getDisplayName());

            // Salva il movie originale (con ID) per mantenerlo in edit
            this.movie = movie;
        }
    }

    private boolean validateInput() {
        // Valida titolo
        if (titoloField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Il titolo è obbligatorio!",
                    "Errore di validazione",
                    JOptionPane.ERROR_MESSAGE);
            titoloField.requestFocus();
            return false;
        }

        // Valida regista
        if (registaField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Il regista è obbligatorio!",
                    "Errore di validazione",
                    JOptionPane.ERROR_MESSAGE);
            registaField.requestFocus();
            return false;
        }

        return true;
    }

    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            // Raccogli dati dal form
            String titolo = titoloField.getText().trim();
            String regista = registaField.getText().trim();
            int anno = (Integer) annoSpinner.getValue();
            String genere = (String) genreComboBox.getSelectedItem();
            int valutazione = valutazioneSlider.getValue();

            // Converti display name in enum
            String statusDisplay = (String) statusComboBox.getSelectedItem();
            ViewingStatus status = null;
            for (ViewingStatus vs : ViewingStatus.values()) {
                if (vs.getDisplayName().equals(statusDisplay)) {
                    status = vs;
                    break;
                }
            }

            // Crea o aggiorna movie
            if (movie == null) {
                // INSERT mode - crea nuovo movie
                movie = new Movie(regista, status, valutazione, genere, anno, titolo);
            } else {
                // EDIT mode - aggiorna movie esistente
                movie.setTitolo(titolo);
                movie.setRegista(regista);
                movie.setAnnoUscita(anno);
                movie.setGenere(genere);
                movie.setValutazione(valutazione);
                movie.setStatoVisione(status);
            }

            confirmed = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Errore nel salvataggio: " + e.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancel() {
        confirmed = false;
        movie = null;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Movie getMovie() {
        return movie;
    }

    public Movie showDialog() {
        setVisible(true); // Blocca finché non chiuso (modal)
        return isConfirmed() ? movie : null;
    }
}
