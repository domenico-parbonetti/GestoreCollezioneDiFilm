package com.parbonetti.gestorefilm.commands;

import com.parbonetti.gestorefilm.model.Movie;
import com.parbonetti.gestorefilm.model.MovieCollection;

public class EditMovieCommand implements Command {

    private final MovieCollection collection;
    private final Movie oldMovie;
    private final Movie newMovie;

    public EditMovieCommand(MovieCollection collection, Movie oldMovie) {
        this.collection = collection;
        this.oldMovie = oldMovie;
        this.newMovie = oldMovie;
    }
    @Override
    public void execute() {
        collection.updateMovie(newMovie);
        System.out.println("[Command] Executed: Modificato il film '" + newMovie.getTitolo() + "'");
    }

    @Override
    public void undo() {
        collection.updateMovie(oldMovie);
        System.out.println("[Command] Undo: Ripristinato il film '" + oldMovie.getTitolo() + "' allo stato precedente");
    }

    @Override
    public String getDescription() {
        return "Modifica: " + newMovie.getTitolo();
    }
}
