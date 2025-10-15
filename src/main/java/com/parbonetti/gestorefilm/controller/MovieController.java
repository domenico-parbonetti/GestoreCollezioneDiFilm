package com.parbonetti.gestorefilm.controller;

import com.parbonetti.gestorefilm.model.CollectionObserver;
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

public class MovieController implements CollectionObserver {
    private final MainView view;
    private final MovieCollection collection;
    private String currentFilepath = "movies"; // default filename (senza estensione)
    private static final String AUTO_SAVE_FILE = "movies_autosave";

    public MovieController(MainView view) {
        this.view = view;
        this.collection = MovieCollection.getInstance();

        // Imposta strategia di default (JSON)
        collection.setPersistenceStrategy(new JSONPersistence());

        collection.addObserver(this);

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

        // Bottoni salvataggio
        view.getSaveButton().addActionListener(e -> handleSave());
        view.getLoadButton().addActionListener(e -> handleLoad());

        // Cambio formato persistenza
        view.getFormatComboBox().addActionListener(e -> handleFormatChange());

        // Filtri
        view.getFilterPanel().getApplyFilterButton().addActionListener(e -> handleApplyFilters());
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

        // Mostra dialog e attendi input utente
        Movie newMovie = dialog.showDialog();

        // Se confermato, aggiungi alla collezione
        if (newMovie != null) {
            boolean success = collection.addMovie(newMovie);
            if (success) {
                autoSave();
                refreshView();
                view.showMessage("Film aggiunto con successo!");
            } else {
                view.showError("Errore nell'aggiunta del film.");
            }
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

        // Crea dialog in modalità EDIT
        MovieFormDialog dialog = new MovieFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(view),
                movieToEdit
        );

        // Mostra dialog e attendi input utente
        Movie editedMovie = dialog.showDialog();

        // Se confermato, aggiorna nella collezione
        if (editedMovie != null) {
            boolean success = collection.updateMovie(editedMovie);
            if (success) {
                autoSave();
                refreshView();
                view.showMessage("Film modificato con successo!");
            } else {
                view.showError("Errore nella modifica del film.");
            }
        }
    }

    private void handleDeleteMovie() {
        // Verifica che ci sia una selezione
        String selectedId = view.getSelectedMovieId();
        if (selectedId == null) {
            view.showError("Seleziona un film da eliminare!");
            return;
        }

        // Ottieni il film per mostrare il titolo nella conferma
        Movie movieToDelete = collection.getMovie(selectedId);
        if (movieToDelete == null) {
            view.showError("Film non trovato!");
            return;
        }

        // Chiedi conferma
        boolean confirmed = view.showConfirmation(
                "Sei sicuro di voler eliminare il film:\n\"" +
                        movieToDelete.getTitolo() + "\"?"
        );

        if (confirmed) {
            boolean success = collection.removeMovie(selectedId);
            if (success) {
                autoSave();
                refreshView();
                view.showMessage("Film eliminato con successo!");
            } else {
                view.showError("Errore nell'eliminazione del film.");
            }
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

        // Parti da tutti i film
        List<Movie> filteredMovies = collection.getAllMovies();

        // Applica ricerca per titolo (se presente)
        if (!searchText.isEmpty()) {
            filteredMovies = collection.searchByTitle(searchText);
        }

        // Applica filtro genere (se non "Tutti")
        if (!selectedGenre.equals("Tutti")) {
            List<Movie> genreFiltered = collection.filterByGenere(selectedGenre);
            filteredMovies.retainAll(genreFiltered); // intersezione
        }

        // Applica filtro stato (se non "Tutti")
        if (selectedStatus != null) {
            List<Movie> statusFiltered = collection.filterByStatus(selectedStatus);
            filteredMovies.retainAll(statusFiltered); // intersezione
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

    @Override
    public void onMovieAdded(Movie movie) {
        System.out.println("[Observer] Film aggiunto: " + movie.getTitolo());
        refreshView();
    }

    @Override
    public void onMovieRemoved(Movie movie) {
        System.out.println("[Observer] Film rimosso: " + movie.getTitolo());
        refreshView();
    }

    @Override
    public void onMovieUpdated(Movie movie) {
        System.out.println("[Observer] Film aggiornato: " + movie.getTitolo());
        refreshView();
    }

    @Override
    public void onCollectionLoaded() {
        System.out.println("[Observer] Collezione caricata: " +
                collection.getMovieCount() + " movies");
        refreshView();
    }

}
