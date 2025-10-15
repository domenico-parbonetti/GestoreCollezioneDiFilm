package com.parbonetti.gestorefilm.model;

import com.parbonetti.gestorefilm.persistence.PersistenceStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovieCollection {
    private static MovieCollection instance;

    private List<Movie> movies;
    private PersistenceStrategy persistenceStrategy;
    private List<CollectionObserver> observers;
    private MovieCollection() {
        this.movies = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public static MovieCollection getInstance() {
        if (instance == null) {
            instance = new MovieCollection();
        }
        return instance;
    }

    public void addObserver(CollectionObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Observer registrato: " + observer.getClass().getSimpleName());
        }
    }

    public void removeObserver(CollectionObserver observer) {
        observers.remove(observer);
        System.out.println("Observer rimosso: " + observer.getClass().getSimpleName());
    }

    private void notifyMovieAdded(Movie movie) {
        for (CollectionObserver observer : observers) {
            observer.onMovieAdded(movie);
        }
    }

    private void notifyMovieRemoved(Movie movie) {
        for (CollectionObserver observer : observers) {
            observer.onMovieRemoved(movie);
        }
    }

    private void notifyMovieUpdated(Movie movie) {
        for (CollectionObserver observer : observers) {
            observer.onMovieUpdated(movie);
        }
    }

    private void notifyCollectionLoaded() {
        for (CollectionObserver observer : observers) {
            observer.onCollectionLoaded();
        }
    }

    public boolean addMovie(Movie movie) {
        if (movie == null) {
            return false;
        }
        boolean result = movies.add(movie);
        if (result) {
            notifyMovieAdded(movie);
        }
        return result;
    }

    public boolean removeMovie(String id) {
        Movie movieToRemove = getMovie(id);
        boolean result = movies.removeIf(m -> m.getId().equals(id));
        if (result && movieToRemove != null) {
            notifyMovieRemoved(movieToRemove);
        }
        return result;
    }

    public Movie getMovie(String id) {
        return movies.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies);
    }

    public boolean updateMovie(Movie uMovie) {
        if (uMovie == null) {
            return false;
        }

        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getId().equals(uMovie.getId())) {
                movies.set(i, uMovie);
                notifyMovieUpdated(uMovie);
                return true;
            }
        }
        return false;
    }

    public List<Movie> searchByTitleOrAuthor(String query){
        if (query == null || query.trim().isEmpty()) {
            return getAllMovies();
        }

        String lowerQuery = query.toLowerCase();
        return movies.stream()
                .filter(m -> m.getTitolo().toLowerCase().contains(lowerQuery) ||
                        m.getRegista().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
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

    public int getMovieCount() {
        return movies.size();
    }

    public boolean isEmpty() {
        return movies.isEmpty();
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
            notifyCollectionLoaded();
        }
    }
}