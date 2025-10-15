package com.parbonetti.gestorefilm.controller;

import com.parbonetti.gestorefilm.commands.AddMovieCommand;
import com.parbonetti.gestorefilm.commands.Command;
import com.parbonetti.gestorefilm.commands.DeleteMovieCommand;
import com.parbonetti.gestorefilm.commands.EditMovieCommand;
import com.parbonetti.gestorefilm.model.Movie;
import com.parbonetti.gestorefilm.model.MovieCollection;
import com.parbonetti.gestorefilm.model.ViewingStatus;
import com.parbonetti.gestorefilm.persistence.CSVPersistence;
import com.parbonetti.gestorefilm.persistence.JSONPersistence;
import com.parbonetti.gestorefilm.persistence.PersistenceStrategy;
import com.parbonetti.gestorefilm.view.MainView;
import com.parbonetti.gestorefilm.view.MovieFormDialog;

import javax.swing.*;
import java.util.List;
import java.util.Stack;

public class MovieController{
    private final MainView view;
    private final MovieCollection collection;
    private Stack<Command> commandHistory;
    private static final int MAX_HISTORY_SIZE = 50;
    private String currentFilepath = "movies"; // default filename (senza estensione)
    private static final String AUTO_SAVE_FILE = "movies_autosave";

    public MovieController(MainView view) {
        this.view = view;
        this.collection = MovieCollection.getInstance();

        // Imposta strategia di default (JSON)
        collection.setPersistenceStrategy(new JSONPersistence());

        this.commandHistory = new Stack<>();

        loadAutoSave();

        // Registra listener per gli eventi della view
        registerListeners();

        // Aggiorna la view iniziale
        refreshView();
    }

    private void loadAutoSave() {
        try {
            java.io.File autoSaveFile = new java.io.File(AUTO_SAVE_FILE + ".json");
            if (autoSaveFile.exists()) {
                collection.load(AUTO_SAVE_FILE);
                System.out.println("Auto-save caricato: " + collection.getMovieCount() + " film");
            } else {
                System.out.println("Nessun auto-save trovato (prima esecuzione)");
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento auto-save: " + e.getMessage());
        }
    }

    private void autoSave() {
        try {
            collection.save(AUTO_SAVE_FILE);
            System.out.println("Auto-save eseguito: " + collection.getMovieCount() + " film salvati");
        } catch (Exception e) {
            System.err.println("Errore auto-save: " + e.getMessage());
        }
    }

    private void registerListeners() {
        // Bottoni azioni principali
        view.getAddButton().addActionListener(e -> handleAddMovie());
        view.getEditButton().addActionListener(e -> handleEditMovie());
        view.getDeleteButton().addActionListener(e -> handleDeleteMovie());
        view.getUndoButton().addActionListener(e -> handleUndo());

        // Bottoni salvataggio
        view.getSaveButton().addActionListener(e -> handleSave());
        view.getLoadButton().addActionListener(e -> handleLoad());

        // Cambio formato persistenza
        view.getFormatComboBox().addActionListener(e -> handleFormatChange());

        // Filtri
        view.getFilterPanel().getClearFilterButton().addActionListener(e -> handleClearFilters());

        // Ricerca real-time (opzionale - ogni tasto premuto)
        view.getFilterPanel().getSearchField().getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    @Override
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        handleApplyFilters();
                    }

                    @Override
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        handleApplyFilters();
                    }

                    @Override
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        handleApplyFilters();
                    }
                }
        );
        view.getFilterPanel().getGenreComboBox().addActionListener(e -> handleApplyFilters());
        view.getFilterPanel().getStatusComboBox().addActionListener(e -> handleApplyFilters());
        view.getFilterPanel().getRatingComboBox().addActionListener(e -> handleApplyFilters());
        view.getRootPane().registerKeyboardAction(
                e -> handleUndo(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z,
                        java.awt.event.InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
        if (commandHistory.size() > MAX_HISTORY_SIZE) {
            commandHistory.remove(0);
        }
        view.getUndoButton().setEnabled(true);
        autoSave();
        refreshView();
    }

    private void handleUndo() {
        if (commandHistory.isEmpty()) {
            return;
        }
        Command command = commandHistory.pop();
        command.undo();
        view.showMessage("Annullato: " + command.getDescription());
        if (commandHistory.isEmpty()) {
            view.getUndoButton().setEnabled(false);
        }
        autoSave();
        refreshView();
    }

    private void refreshView() {
        List<Movie> movies = collection.getAllMovies();
        view.showMovies(movies);

        // Aggiorna lista generi nel filtro
        List<String> genres = collection.getAllGenres();
        view.getFilterPanel().updateGenres(genres);
    }

    private void handleAddMovie() {
        // Crea dialog in modalità INSERT
        MovieFormDialog dialog = new MovieFormDialog((JFrame) SwingUtilities.getWindowAncestor(view));

        Movie newMovie = dialog.showDialog();

        // Se confermato, aggiungi alla collezione
        if (newMovie != null) {
            Command command = new AddMovieCommand(collection, newMovie);
            executeCommand(command);
            view.showMessage("Film aggiunto con successo! (Ctrl+Z per annullare)");
        }
    }

    private void handleEditMovie() {
        // Verifica che ci sia una selezione
        String selectedId = view.getSelectedMovieId();
        if (selectedId == null) {
            view.showError("Seleziona un film da modificare!");
            return;
        }

        // Ottieni il film dalla collezione
        Movie movieToEdit = collection.getMovie(selectedId);
        if (movieToEdit == null) {
            view.showError("Film non trovato!");
            return;
        }
        Movie movieCopy = new Movie(movieToEdit);
        Movie originalMovie = new Movie(movieToEdit);

        // Crea dialog in modalità EDIT
        MovieFormDialog dialog = new MovieFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(view),
                movieCopy
        );

        // Mostra dialog e attendi input utente
        Movie editedMovie = dialog.showDialog();

        // Se confermato, aggiorna nella collezione
        if (editedMovie != null) {
            Command command = new EditMovieCommand(collection, originalMovie, editedMovie);
            executeCommand(command);
            view.showMessage("Film modificato con successo! (Ctrl+Z per annullare)");
        }
    }

    private void handleDeleteMovie() {
        String selectedId = view.getSelectedMovieId();
        if (selectedId == null) {
            view.showError("Seleziona un film da eliminare!");
            return;
        }

        Movie movie = collection.getMovie(selectedId);
        if (movie == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Eliminare '" + movie.getTitolo() + "'?\n(Puoi ripristinare con Annulla/Ctrl+Z)",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Command command = new DeleteMovieCommand(collection, movie);
            executeCommand(command);
            view.showMessage("Film eliminato con successo! (Ctrl+Z per annullare)");
        }
    }

    private void handleSave() {
        try {
            // Chiedi il nome del file all'utente
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salva Collezione");

            // Imposta filtro estensione in base al formato
            String format = (String) view.getFormatComboBox().getSelectedItem();
            if (format.equals("JSON")) {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "File JSON (*.json)", "json"));
            } else {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "File CSV (*.csv)", "csv"));
            }

            // Proponi nome file di default
            fileChooser.setSelectedFile(new java.io.File(currentFilepath));

            int result = fileChooser.showSaveDialog((JFrame) SwingUtilities.getWindowAncestor(view));

            if (result == JFileChooser.APPROVE_OPTION) {
                String filepath = fileChooser.getSelectedFile().getAbsolutePath();

                // Rimuovi estensione se presente (verrà aggiunta dalla strategia)
                if (filepath.endsWith(".json") || filepath.endsWith(".csv")) {
                    filepath = filepath.substring(0, filepath.lastIndexOf('.'));
                }

                currentFilepath = filepath;

                // Salva usando la strategia corrente
                collection.save(filepath);
                view.showMessage("Collezione salvata con successo in:\n" + filepath);
            }

        } catch (Exception e) {
            view.showError("Errore nel salvataggio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLoad() {
        try {
            // Chiedi il file all'utente
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Carica Collezione");

            // Imposta filtro estensione in base al formato
            String format = (String) view.getFormatComboBox().getSelectedItem();
            if (format.equals("JSON")) {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "File JSON (*.json)", "json"));
            } else {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "File CSV (*.csv)", "csv"));
            }

            int result = fileChooser.showOpenDialog((JFrame) SwingUtilities.getWindowAncestor(view));

            if (result == JFileChooser.APPROVE_OPTION) {
                String filepath = fileChooser.getSelectedFile().getAbsolutePath();

                // Rimuovi estensione se presente
                if (filepath.endsWith(".json") || filepath.endsWith(".csv")) {
                    filepath = filepath.substring(0, filepath.lastIndexOf('.'));
                }

                currentFilepath = filepath;

                // Carica usando la strategia corrente
                collection.load(filepath);

                // Pulisci filtri e aggiorna view
                handleClearFilters();
                refreshView();

                view.showMessage("Collezione caricata con successo!\n" +
                        "Film caricati: " + collection.getMovieCount());
            }

        } catch (Exception e) {
            view.showError("Errore nel caricamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleApplyFilters() {
        // Ottieni criteri di ricerca/filtro
        String searchText = view.getFilterPanel().getSearchText();
        String selectedGenre = view.getFilterPanel().getSelectedGenre();
        ViewingStatus selectedStatus = view.getFilterPanel().getSelectedStatusEnum();
        int minRating = view.getFilterPanel().getSelectedMinRating();

        // Parti da tutti i film
        List<Movie> filteredMovies = collection.getAllMovies();

        // Applica ricerca per titolo (se presente)
        if (searchText != null && !searchText.isEmpty()) {
            filteredMovies = collection.searchByTitleOrAuthor(searchText);
        }

        // Applica filtro genere (se non "Tutti")
        if (selectedGenre != null && !selectedGenre.equals("Tutti")) {
            List<Movie> genreFiltered = collection.filterByGenere(selectedGenre);
            filteredMovies.retainAll(genreFiltered); // intersezione
        }

        // Applica filtro stato (se non "Tutti")
        if (selectedStatus != null) {
            List<Movie> statusFiltered = collection.filterByStatus(selectedStatus);
            filteredMovies.retainAll(statusFiltered); // intersezione
        }

        if (minRating > 0) {
            List<Movie> ratingFiltered = collection.filterByRating(minRating);
            filteredMovies.retainAll(ratingFiltered);
        }

        // Aggiorna view con risultati filtrati
        view.showMovies(filteredMovies);
    }

    private void handleClearFilters() {
        view.getFilterPanel().clearFilters();
        refreshView();
    }

    private void handleFormatChange() {
        String selectedFormat = (String) view.getFormatComboBox().getSelectedItem();

        PersistenceStrategy strategy;
        if (selectedFormat.equals("JSON")) {
            strategy = new JSONPersistence();
        } else if (selectedFormat.equals("CSV")) {
            strategy = new CSVPersistence();
        } else {
            view.showError("Formato non supportato: " + selectedFormat);
            return;
        }

        // Cambia strategia nella collezione (Strategy pattern in azione!)
        collection.setPersistenceStrategy(strategy);

        System.out.println("Strategia di persistenza cambiata in: " + selectedFormat);
    }
}
