package com.parbonetti.gestorefilm.commands;

import com.parbonetti.gestorefilm.model.Movie;
import com.parbonetti.gestorefilm.model.MovieCollection;

public class EditMovieCommand implements Command {

    private final MovieCollection collection;
    private final Movie movie;
    private final Movie.Memento beforeState;

    public EditMovieCommand(MovieCollection collection, Movie movie,  Movie.Memento beforeState) {
        this.collection = collection;
        this.movie = movie;
        this.beforeState = beforeState;
    }
    @Override
    public void execute() {
        collection.updateMovie(movie);
        System.out.println("[Command] Executed: Modificato il film '" + movie.getTitolo() + "'");
    }

    @Override
    public void undo() {
        movie.restoreFromMemento(beforeState);
        collection.updateMovie(movie);
        System.out.println("[Command] Undo: Ripristinato il film '" + movie.getTitolo() + "' allo stato precedente");
    }

    @Override
    public String getDescription() {
        return "Modifica: " + movie.getTitolo();
    }
}
