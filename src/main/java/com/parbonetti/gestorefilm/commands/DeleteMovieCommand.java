package com.parbonetti.gestorefilm.commands;

import com.parbonetti.gestorefilm.model.Movie;
import com.parbonetti.gestorefilm.model.MovieCollection;

public class DeleteMovieCommand implements Command {

    private final MovieCollection collection;
    private final Movie movie;

    public DeleteMovieCommand(MovieCollection collection, Movie movie) {
        this.collection = collection;
        this.movie = movie;
    }

    @Override
    public void execute() {
        collection.removeMovie(movie.getId());
        System.out.println("[Command] Executed: Eliminato il film '" + movie.getTitolo() + "'");
    }

    @Override
    public void undo() {
        collection.addMovie(movie);
        System.out.println("[Command] Undo: Ripristinato il film '" + movie.getTitolo() + "'");
    }

    @Override
    public String getDescription() {
        return "Elimina: " + movie.getTitolo();
    }
}
