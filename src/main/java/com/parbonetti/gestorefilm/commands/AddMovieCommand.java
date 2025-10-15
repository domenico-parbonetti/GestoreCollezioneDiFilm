package com.parbonetti.gestorefilm.commands;

import com.parbonetti.gestorefilm.model.Movie;
import com.parbonetti.gestorefilm.model.MovieCollection;

public class AddMovieCommand implements Command {
    private final MovieCollection collection;
    private final Movie movie;

    public AddMovieCommand(MovieCollection collection, Movie movie) {
        this.collection = collection;
        this.movie = movie;
    }

    @Override
    public void execute() {
        collection.addMovie(movie);
        System.out.println("[Command] Executed: Aggiunto il film '" + movie.getTitolo() + "'");
    }

    @Override
    public void undo() {
        collection.removeMovie(movie.getId());
        System.out.println("[Command] Undo: Rimosso il film '" + movie.getTitolo() + "'");
    }

    @Override
    public String getDescription() {
        return "Aggiungi: " + movie.getTitolo();
    }
}
