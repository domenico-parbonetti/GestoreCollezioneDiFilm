package com.parbonetti.gestorefilm.persistence;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.parbonetti.gestorefilm.model.Movie;
import com.parbonetti.gestorefilm.model.ViewingStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVPersistence implements PersistenceStrategy {
    private static final String[] CSV_HEADER = {
            "ID", "Titolo", "Regista", "Anno", "Genere", "Valutazione", "Stato"
    };

    @Override
    public void save(List<Movie> movies, String filepath) {
        if (movies == null) {
            throw new IllegalArgumentException("La lista non puo' essere null");
        }
        if (filepath == null || filepath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso non può essere vuoto o null");
        }

        // Aggiungi estensione .csv se manca
        if (!filepath.endsWith(".csv")) {
            filepath += ".csv";
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(filepath))) {

            writer.writeNext(CSV_HEADER);

            // Scrivo ogni film come riga CSV
            for (Movie movie : movies) {
                String[] row = movieToStringArray(movie);
                writer.writeNext(row);
            }

            System.out.println("Collezione salvata correttamente in: " + filepath);

        } catch (IOException e) {
            System.err.println("Error saving to CSV: " + e.getMessage());
            throw new RuntimeException("Non sono riuscito a salvare in CSV", e);
        }
    }

    private String[] movieToStringArray(Movie movie) {
        return new String[] {
                movie.getId(),
                movie.getTitolo(),
                movie.getRegista(),
                String.valueOf(movie.getAnnoUscita()),
                movie.getGenere(),
                String.valueOf(movie.getValutazione()),
                movie.getStatoVisione().name()
        };
    }

    @Override
    public List<Movie> load(String filepath) {
        if (filepath == null || filepath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso non può essere vuoto o null");
        }

        if (!filepath.endsWith(".csv")) {
            filepath += ".csv";
        }

        File file = new File(filepath);

        if (!file.exists()) {
            System.out.println("File non trovato: " + filepath + ". Inizializzo una collezione vuota.");
            return new ArrayList<>();
        }

        List<Movie> movies = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(file))) {

            List<String[]> rows = reader.readAll();

            if (rows.isEmpty()) {
                return movies;
            }

            // Salta la prima riga (header)
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                // Salta righe vuote o incomplete
                if (row.length < 7) {
                    System.err.println("Skipping invalid row: " + String.join(",", row));
                    continue;
                }

                try {
                    Movie movie = stringArrayToMovie(row);
                    movies.add(movie);
                } catch (Exception e) {
                    System.err.println("Error parsing row: " + String.join(",", row));
                    System.err.println("Error: " + e.getMessage());
                    // Continua con la prossima riga
                }
            }

            System.out.println("Loaded " + movies.size() + " movies from: " + filepath);
            return movies;

        } catch (IOException e) {
            System.err.println("Error loading from CSV: " + e.getMessage());
            throw new RuntimeException("Non sono riuscito a caricare la collezione CSV", e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private Movie stringArrayToMovie(String[] row) {
        // Parsing dei campi
        String id = row[0];
        String titolo = row[1];
        String regista = row[2];
        int annoUscita = Integer.parseInt(row[3]);
        String genere = row[4];
        int valutazione = Integer.parseInt(row[5]);
        ViewingStatus statoVisione = ViewingStatus.valueOf(row[6]);

        Movie movie = new Movie(regista, statoVisione, valutazione, genere, annoUscita, titolo);

        // Usa reflection per impostare l'ID originale (mantiene lo stesso ID dopo save/load)
        try {
            java.lang.reflect.Field idField = Movie.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(movie, id);
        } catch (Exception e) {
            // Se fallisce, usa l'ID generato automaticamente (non è un problema critico)
            System.err.println("Warning: Could not restore original ID for movie: " + titolo);
        }

        return movie;
    }
}
