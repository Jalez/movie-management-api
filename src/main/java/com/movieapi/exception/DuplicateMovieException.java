package com.movieapi.exception;

/**
 * Exception thrown when attempting to create a movie that already exists.
 * This is typically thrown when a movie with the same title and director already exists.
 */
public class DuplicateMovieException extends RuntimeException {

    private final String title;
    private final String director;

    public DuplicateMovieException(String title, String director) {
        super(String.format("Movie '%s' by director '%s' already exists", title, director));
        this.title = title;
        this.director = director;
    }

    public DuplicateMovieException(String message) {
        super(message);
        this.title = null;
        this.director = null;
    }

    public DuplicateMovieException(String message, Throwable cause) {
        super(message, cause);
        this.title = null;
        this.director = null;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }
}
