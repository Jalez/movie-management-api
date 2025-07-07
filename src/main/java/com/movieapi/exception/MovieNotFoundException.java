package com.movieapi.exception;

/**
 * Exception thrown when a requested movie is not found.
 * This is typically thrown when trying to access a movie by ID that doesn't exist.
 */
public class MovieNotFoundException extends RuntimeException {

    private final Long movieId;

    public MovieNotFoundException(Long movieId) {
        super(String.format("Movie with ID %d not found", movieId));
        this.movieId = movieId;
    }

    public MovieNotFoundException(String message) {
        super(message);
        this.movieId = null;
    }

    public MovieNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.movieId = null;
    }

    public Long getMovieId() {
        return movieId;
    }
}
