package com.parbonetti.gestorefilm.model;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MovieCollectionTest {
    private MovieCollection collection;

    @BeforeEach
    void setUp() {
        // Reset singleton prima di ogni test
        MovieCollection.resetForTesting();
        collection = MovieCollection.getInstance();
    }

    @AfterEach
    void tearDown() {
        MovieCollection.resetForTesting();
    }

    // ========== SINGLETON TESTS ==========

    @Test
    @DisplayName("getInstance dovrebbe ritornare la stessa istanza")
    void testGetInstanceReturnsSameInstance() {
        MovieCollection instance1 = MovieCollection.getInstance();
        MovieCollection instance2 = MovieCollection.getInstance();

        assertSame(instance1, instance2, "getInstance dovrebbe ritornare la stessa istanza");
    }

    @Test
    @DisplayName("resetForTesting dovrebbe creare una nuova istanza")
    void testResetForTesting() {
        MovieCollection instance1 = MovieCollection.getInstance();
        MovieCollection.resetForTesting();
        MovieCollection instance2 = MovieCollection.getInstance();

        assertNotSame(instance1, instance2, "Reset dovrebbe creare una nuova istanza");
    }

    // ========== ADD MOVIE TESTS ==========

    @Test
    @DisplayName("addMovie dovrebbe aggiungere un film alla collezione")
    void testAddMovie() {
        Movie movie = new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception");

        boolean result = collection.addMovie(movie);

        assertTrue(result, "addMovie dovrebbe ritornare true");
        assertEquals(1, collection.getMovieCount(), "Collection dovrebbe avere 1 film");
        assertTrue(collection.getAllMovies().contains(movie), "Collection dovrebbe contenere il film aggiunto");
    }

    @Test
    @DisplayName("Passando null ad addMovie dovrebbe ritornare false")
    void testAddMovieWithNull() {
        boolean result = collection.addMovie(null);

        assertFalse(result, "addMovie(null) -> false");
        assertEquals(0, collection.getMovieCount(), "Collection dovrebbe essere vuota");
    }

    @Test
    @DisplayName("addMovie ammette film duplicati")
    void testAddMovieDuplicates() {
        Movie movie1 = new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception");
        Movie movie2 = new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception");

        collection.addMovie(movie1);
        collection.addMovie(movie2);

        assertEquals(2, collection.getMovieCount(), "Dovrebbe ammettere film duplicati con ID differenti");
    }

    // ========== REMOVE MOVIE TESTS ==========

    @Test
    @DisplayName("removeMovie dovrebbe rimuovere un film esistente")
    void testRemoveMovie() {
        Movie movie = new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception");
        collection.addMovie(movie);

        boolean result = collection.removeMovie(movie.getId());

        assertTrue(result, "removeMovie -> true");
        assertEquals(0, collection.getMovieCount(), "Collection dovrebbe essere vuota");
        assertFalse(collection.getAllMovies().contains(movie), "Collection non dovrebbe contenere il film rimosso");
    }

    @Test
    @DisplayName("removeMovie con un ID non esistente -> false")
    void testRemoveMovieNonExistent() {
        boolean result = collection.removeMovie("non-existent-id");

        assertFalse(result, "removeMovie con un ID non esistente -> false");
    }

    @Test
    @DisplayName("removeMovie con un ID null -> false")
    void testRemoveMovieWithNull() {
        boolean result = collection.removeMovie(null);

        assertFalse(result, "removeMovie(null) -> false");
    }

    // ========== UPDATE MOVIE TESTS ==========

    @Test
    @DisplayName("updateMovie dovrebbe aggiornare un film esistente")
    void testUpdateMovie() {
        Movie original = new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception");
        collection.addMovie(original);

        Movie updated = new Movie(original.getId(),"C Nolan", ViewingStatus.VISTO, 4, "Sci-Fi", 2010, "Inception AGG");

        boolean result = collection.updateMovie(updated);

        assertTrue(result, "updateMovie -> true");
        Movie retrieved = collection.getMovie(original.getId());
        assertEquals("Inception AGG", retrieved.getTitolo(), "Titolo dovrebbe essere aggiornato");
        assertEquals("C Nolan", retrieved.getRegista(), "Il regista dovrebbe essere aggiornato");
        assertEquals(4, retrieved.getValutazione(), "La val. dovrebbe essere aggiornata");
        assertEquals(ViewingStatus.VISTO, retrieved.getStatoVisione(), "Lo stato dovrebbe essere aggiornato");
    }

    @Test
    @DisplayName("updateMovie con null -> false")
    void testUpdateMovieWithNull() {
        boolean result = collection.updateMovie(null);

        assertFalse(result, "updateMovie(null) -> false");
    }

    @Test
    @DisplayName("updateMovie con ID non esistente -> false")
    void testUpdateMovieNonExistent() {
        Movie movie = new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception");

        boolean result = collection.updateMovie(movie);

        assertFalse(result, "updateMovie con ID non esistente -> false");
    }

    // ========== GET MOVIE TESTS ==========

    @Test
    @DisplayName("getMovie dovrebbe ritornare un film dall'ID")
    void testGetMovie() {
        Movie movie = new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception");
        collection.addMovie(movie);

        Movie retrieved = collection.getMovie(movie.getId());

        assertNotNull(retrieved, "getMovie dovrebbe ritornare un film");
        assertEquals(movie.getId(), retrieved.getId(), "ID dovrebbero essere uguali");
        assertEquals("Inception", retrieved.getTitolo(), "I titoli dovrebbero essere uguali");
    }

    @Test
    @DisplayName("getMovie con ID non esistente -> null")
    void testGetMovieNonExistent() {
        Movie result = collection.getMovie("non-existent-id");

        assertNull(result, "getMovie con ID non esistente -> null");
    }

    @Test
    @DisplayName("getMovie con ID null -> null")
    void testGetMovieWithNull() {
        Movie result = collection.getMovie(null);

        assertNull(result, "getMovie(null) -> null");
    }

    // ========== SEARCH TESTS ==========

    @Test
    @DisplayName("searchByTitleOrAuthor dovrebbe trovare il film dal titolo")
    void testSearchByTitle() {
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception"));
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2014, "Interstellar"));
        collection.addMovie(new Movie("Gerwig", ViewingStatus.DA_VEDERE, 5, "Commedia", 2023, "Barbie"));

        List<Movie> results = collection.searchByTitleOrAuthor("Inception");

        assertEquals(1, results.size(), "Risultato 1 film?");
        assertEquals("Inception", results.get(0).getTitolo(), "Dovrebbe essere il film corretto");
    }

    @Test
    @DisplayName("searchByTitleOrAuthor dovrebbe trovare il film dal regista")
    void testSearchByDirector() {
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception"));
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2014, "Interstellar"));
        collection.addMovie(new Movie("Gerwig", ViewingStatus.DA_VEDERE, 5, "Commedia", 2023, "Barbie"));

        List<Movie> results = collection.searchByTitleOrAuthor("Nolan");

        assertEquals(2, results.size(), "Dovrebbe trovare 2 film di Nolan");
    }

    @Test
    @DisplayName("searchByTitleOrAuthor dovrebbe essere case-insensitive")
    void testSearchCaseInsensitive() {
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception"));

        List<Movie> results = collection.searchByTitleOrAuthor("inception");

        assertEquals(1, results.size(), "La ricerca dovrebbe essere case-insensitive");
    }

    @Test
    @DisplayName("searchByTitleOrAuthor con null -> lista vuota")
    void testSearchWithNull() {
        List<Movie> results = collection.searchByTitleOrAuthor(null);

        assertTrue(results.isEmpty(), "Ricerca con null -> lista vuota");
    }

    // ========== FILTER TESTS ==========

    @Test
    @DisplayName("filterByGenere dovrebbe filtrare i film per genere")
    void testFilterByGenere() {
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2010, "Inception"));
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2014, "Interstellar"));
        collection.addMovie(new Movie("Gerwig", ViewingStatus.DA_VEDERE, 5, "Commedia", 2023, "Barbie"));

        List<Movie> results = collection.filterByGenere("Sci-Fi");

        assertEquals(2, results.size(), "Dovrebbe trovare 2 film");
        assertTrue(results.stream().allMatch(m -> m.getGenere().equals("Sci-Fi")),
                "Entrambi i risultati dovrebbero essere Sci-Fi");
    }

    @Test
    @DisplayName("filterByStatus dovrebbe filtrare i film per stato")
    void testFilterByStatus() {
        collection.addMovie(new Movie("Nolan", ViewingStatus.VISTO, 5, "Sci-Fi", 2010, "Inception"));
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2014, "Interstellar"));
        collection.addMovie(new Movie("Gerwig", ViewingStatus.VISTO, 5, "Commedia", 2023, "Barbie"));

        List<Movie> results = collection.filterByStatus(ViewingStatus.VISTO);

        assertEquals(2, results.size(), "Dovrebbe trovare 2 film");
        assertTrue(results.stream().allMatch(m -> m.getStatoVisione() == ViewingStatus.VISTO),
                "tutti i risultati dovrebbero essere VISTO");
    }

    @Test
    @DisplayName("filterByRating dovrebbe filtrare i film per valutazione")
    void testFilterByRating() {
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 4, "Sci-Fi", 2010, "Inception"));
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 4, "Sci-Fi", 2014, "Interstellar"));
        collection.addMovie(new Movie("Gerwig", ViewingStatus.DA_VEDERE, 5, "Commedia", 2023, "Barbie"));

        List<Movie> results = collection.filterByRating(4);

        assertEquals(2, results.size(), "Dovrebbe trovare 2 film con valutazione = 4");
        assertTrue(results.stream().allMatch(m -> m.getValutazione() == 4),
                "Tutti i risultati dovrebbero avere valutazione = 4");
    }

    // ========== GET ALL GENRES TEST ==========

    @Test
    @DisplayName("getAllGenres dovrebbe tornare i generi")
    void testGetAllGenres() {
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Drammatico", 2010, "Inception"));
        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2014, "Interstellar"));
        collection.addMovie(new Movie("Gerwig", ViewingStatus.DA_VEDERE, 5, "Commedia", 2023, "Barbie"));

        List<String> genres = collection.getAllGenres();

        assertEquals(3, genres.size(), "dovrebbe avere 3 generi");
        assertTrue(genres.contains("Sci-Fi"), "Dovrebbe contenere Sci-Fi");
        assertTrue(genres.contains("Drammatico"), "Dovrebbe contenere Drammatico");
        assertTrue(genres.contains("Commedia"), "Dovrebbe contenere Commedia");
    }

    @Test
    @DisplayName("getAllGenres su una collezione vuota dovrebbe ritornare una lista vuota")
    void testGetAllGenresEmpty() {
        List<String> genres = collection.getAllGenres();

        assertTrue(genres.isEmpty(), "Collezione vuota dovrebbe tornare una lista vuota");
    }

    // ========== COUNT TEST ==========

    @Test
    @DisplayName("getMovieCount dovrebbe tornare il conteggio corretto")
    void testGetMovieCount() {
        assertEquals(0, collection.getMovieCount(), "Inizialmente 0");

        collection.addMovie(new Movie("Gerwig", ViewingStatus.DA_VEDERE, 5, "Commedia", 2023, "Barbie"));
        assertEquals(1, collection.getMovieCount(), "Dopo aver aggiunto un film dovrebbe essere 1");

        collection.addMovie(new Movie("Nolan", ViewingStatus.DA_VEDERE, 5, "Sci-Fi", 2014, "Interstellar"));
        assertEquals(2, collection.getMovieCount(), "Adesso dovrebbe essere 2");

        collection.removeMovie(collection.getAllMovies().get(0).getId());
        assertEquals(1, collection.getMovieCount(), "Dopo la rimozione dovrebbe tornare a 1");
    }
}
