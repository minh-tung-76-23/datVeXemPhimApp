package com.example.datvexemphim.ui.model;

import java.util.List;

public class FilmListResponse {
    private List<filmAPI> movies;

    public List<filmAPI> getMovies() {
        return movies;
    }

    public void setMovies(List<filmAPI> movies) {
        this.movies = movies;
    }
}
