package com.movieapi.exception;

/**
 * Exception thrown when a requested review is not found.
 * This is typically thrown when trying to access a review by ID that doesn't exist
 * or doesn't belong to the specified movie.
 */
public class ReviewNotFoundException extends RuntimeException {

    private final Long reviewId;
    private final Long movieId;

    public ReviewNotFoundException(Long reviewId, Long movieId) {
        super(String.format("Review with ID %d not found for movie with ID %d", reviewId, movieId));
        this.reviewId = reviewId;
        this.movieId = movieId;
    }

    public ReviewNotFoundException(String message) {
        super(message);
        this.reviewId = null;
        this.movieId = null;
    }

    public ReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.reviewId = null;
        this.movieId = null;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public Long getMovieId() {
        return movieId;
    }
} 