package com.parbonetti.gestorefilm.persistence;

import com.parbonetti.gestorefilm.model.Movie;

import java.util.List;

public interface PersistenceStrategy {
    void save(List<Movie> movies, String filepath);
    List<Movie> load(String filepath);
}
