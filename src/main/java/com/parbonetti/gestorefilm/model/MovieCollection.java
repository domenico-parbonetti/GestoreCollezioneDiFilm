package com.parbonetti.gestorefilm.model;

import com.parbonetti.gestorefilm.persistence.PersistenceStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovieCollection {
    private static MovieCollection instance;

    private List<Movie> movies;
    private PersistenceStrategy persistenceStrategy;
    private MovieCollection() {
        this.movies = new ArrayList<>();
    }

    public static MovieCollection getInstance() {
        if (instance == null) {
            instance = new MovieCollection();
        }
        return instance;
    }
    public boolean addMovie(Movie movie) {
        if(movie==null) return false;
        return movies.add(movie);
    }

    public boolean removeMovie(String id) {
        return movies.removeIf(m->m.getId().equals(id));
    }

    public Movie getMovie(String id) {
        return movies.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies);
    }

    public boolean updateMovie(Movie uMovie) {
        if (uMovie==null) return false;
        for(int i = 0; i < movies.size(); i++) {
            if(movies.get(i).getId().equals(uMovie.getId())) {
                movies.set(i, uMovie);
                return true;
            }
        }
        return false;
    }

    public List<Movie> searchByTitle(String query){
        if (query==null || query.trim().isEmpty()) return getAllMovies();
        String lowerQuery = query.toLowerCase();
        return movies.stream().filter(m->m.getTitolo().toLowerCase().contains(lowerQuery)).collect(Collectors.toList());
    }

    public List<Movie> searchByRegista(String query){
        if (query==null || query.trim().isEmpty()) return getAllMovies();
        String lowerQuery = query.toLowerCase();
        return movies.stream().filter(m->m.getRegista().toLowerCase().contains(lowerQuery)).collect(Collectors.toList());
    }

    public List<Movie> filterByGenere(String genere){
        if (genere==null || genere.trim().isEmpty() || genere.equals("Tutti")) return getAllMovies();
        return movies.stream().filter(m->m.getGenere().equalsIgnoreCase(genere)).collect(Collectors.toList());
    }

    public List<Movie> filterByStatus(ViewingStatus status){
        if (status==null) return getAllMovies();
        return movies.stream().filter(m->m.getStatoVisione() == status).collect(Collectors.toList());
    }

    public List<Movie> filterByRating(int minRating) {
        return movies.stream()
                .filter(m -> m.getValutazione() >= minRating)
                .collect(Collectors.toList());
    }

    public List<Movie> sortBy(String criteria) {
        List<Movie> sorted = new ArrayList<>(movies);

        switch (criteria.toLowerCase()) {
            case "titolo":
                sorted.sort((m1, m2) -> m1.getTitolo().compareToIgnoreCase(m2.getTitolo()));
                break;
            case "regista":
                sorted.sort((m1, m2) -> m1.getRegista().compareToIgnoreCase(m2.getRegista()));
                break;
            case "anno":
                sorted.sort((m1, m2) -> Integer.compare(m2.getAnnoUscita(), m1.getAnnoUscita()));
                break;
            case "valutazione":
                sorted.sort((m1, m2) -> Integer.compare(m2.getValutazione(), m1.getValutazione()));
                break;
            default:
        }

        return sorted;
    }

    public int getMovieCount() {
        return movies.size();
    }

    public boolean isEmpty() {
        return movies.isEmpty();
    }

    public void clear() {
        movies.clear();
    }

    public List<String> getAllGenres() {
        return movies.stream()
                .map(Movie::getGenere)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public void setPersistenceStrategy(PersistenceStrategy strategy) {
        this.persistenceStrategy = strategy;
    }

    public void save(String filepath) {
        if (persistenceStrategy == null) {
            throw new IllegalStateException("Persistence strategy not set");
        }
        persistenceStrategy.save(movies, filepath);
    }

    public void load(String filepath) {
        if (persistenceStrategy == null) {
            throw new IllegalStateException("Persistence strategy not set");
        }
        List<Movie> loadedMovies = persistenceStrategy.load(filepath);
        if (loadedMovies != null) {
            this.movies = loadedMovies;
        }
    }
}
