package com.parbonetti.gestorefilm.controller;

import com.parbonetti.gestorefilm.AppConfiguration;
import com.parbonetti.gestorefilm.commands.*;
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

public class MovieController{
    private final MainView view;
    private final MovieCollection collection;
    private final CommandManager commandManager;;
    private String currentFilepath = AppConfiguration.DEFAULT_FILENAME;

    public MovieController(MainView view) {
        this.view = view;
        this.collection = MovieCollection.getInstance();

        // Imposta strategia di default (JSON)
        collection.setPersistenceStrategy(new JSONPersistence());

        this.commandManager = new CommandManager(AppConfiguration.MAX_COMMAND_HISTORY_SIZE);

        loadAutoSave();
        registerListeners();
        refreshView();
    }

    private void loadAutoSave() {
        try {
            java.io.File autoSaveFile = new java.io.File(AppConfiguration.AUTO_SAVE_FILENAME + ".json");
            if (autoSaveFile.exists()) {
                collection.load(AppConfiguration.AUTO_SAVE_FILENAME);
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
            collection.save(AppConfiguration.AUTO_SAVE_FILENAME);
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

        view.getFilterPanel().getClearFilterButton().addActionListener(e -> handleClearFilters());

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
        commandManager.executeCommand(command);
        view.getUndoButton().setEnabled(commandManager.canUndo());
        autoSave();
        refreshView();
    }

    private void handleUndo() {
        Command undoneCommand = commandManager.undo();

        if (undoneCommand != null) {
            view.showMessage("Annullato: " + undoneCommand.getDescription());
            view.getUndoButton().setEnabled(commandManager.canUndo());
            autoSave();
            refreshView();
        }
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

        Movie.Memento beforeState = movieToEdit.createMemento();

        // Crea dialog in modalità EDIT
        MovieFormDialog dialog = new MovieFormDialog(view.getParentFrame(),movieToEdit);

        // Mostra dialog e attendi input utente
        Movie editedMovie = dialog.showDialog();

        // Se confermato, aggiorna nella collezione
        if (editedMovie != null) {
            Command command = new EditMovieCommand(collection, movieToEdit, beforeState);
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
            String format = view.getSelectedFormat();

            String filepath = view.showSaveDialog(format, currentFilepath);

            if (filepath != null) {
                currentFilepath = filepath;
                collection.save(filepath);
                view.showMessage("Collezione salvata con successo in:\n" + filepath);
            }

        } catch (Exception e) {
            view.showError("Errore nel salvataggio: " + e.getMessage());
        }
    }

    private void handleLoad() {
        try {
            String format = view.getSelectedFormat();

            String filepath = view.showLoadDialog(format);

            if (filepath != null) {
                currentFilepath = filepath;
                collection.load(filepath);

                handleClearFilters();
                refreshView();

                view.showMessage("Collezione caricata con successo!\n" +
                        "Film caricati: " + collection.getMovieCount());
            }

        } catch (Exception e) {
            view.showError("Errore nel caricamento: " + e.getMessage());
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

        collection.setPersistenceStrategy(strategy);

        System.out.println("Strategia di persistenza cambiata in: " + selectedFormat);
    }
}
