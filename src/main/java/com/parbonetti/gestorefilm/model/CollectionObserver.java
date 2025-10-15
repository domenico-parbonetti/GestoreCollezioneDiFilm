package com.parbonetti.gestorefilm.model;

public interface CollectionObserver {
    void onMovieAdded(Movie movie);
    void onMovieRemoved(Movie movie);
    void onMovieUpdated(Movie movie);
    void onCollectionLoaded();
}
