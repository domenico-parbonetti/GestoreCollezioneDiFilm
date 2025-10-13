package com.parbonetti.gestorefilm.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.parbonetti.gestorefilm.model.Movie;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONPersistence implements PersistenceStrategy {

    private final Gson gson;

    public JSONPersistence() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void save(List<Movie> movies, String filepath) {
        if (movies == null) {
            throw new IllegalArgumentException("La lista dei film non può essere null");
        }
        if (filepath == null || filepath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso non può essere null o vuoto");
        }

        // Aggiungi estensione .json se manca
        if (!filepath.endsWith(".json")) {
            filepath += ".json";
        }

        try (Writer writer = new FileWriter(filepath)) {
            // Converti lista in JSON e scrivi su file
            gson.toJson(movies, writer);
            System.out.println("Salvataggio effettuato correttamente in: " + filepath);

        } catch (IOException e) {
            System.err.println("Error saving to JSON: " + e.getMessage());
            throw new RuntimeException("Non sono riuscito a salvare la collezione in JSON", e);
        }
    }

    @Override
    public List<Movie> load(String filepath) {
        if (filepath == null || filepath.trim().isEmpty()) {
            throw new IllegalArgumentException("il percorso non può essere null o vuoto");
        }

        // Aggiungi estensione .json se manca
        if (!filepath.endsWith(".json")) {
            filepath += ".json";
        }

        File file = new File(filepath);

        if (!file.exists()) {
            System.out.println("File non trovato: " + filepath + ". Inizializzo una collezione vuota.");
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            // Definisci il tipo per la deserializzazione
            Type movieListType = new TypeToken<ArrayList<Movie>>(){}.getType();

            // Leggi JSON e converti in lista di Movie
            List<Movie> movies = gson.fromJson(reader, movieListType);

            if (movies == null) {
                movies = new ArrayList<>();
            }

            System.out.println("Ho caricato " + movies.size() + " film da: " + filepath);
            return movies;

        } catch (IOException e) {
            System.err.println("Error loading from JSON: " + e.getMessage());
            throw new RuntimeException("Non sono riuscito a caricare la collezione da JSON", e);
        }
    }
}
