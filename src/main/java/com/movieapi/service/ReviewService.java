package com.movieapi.service;

import com.movieapi.entity.Review;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Review business logic operations.
 * Defines the contract for all review-related business operations including
 * CRUD operations and rating calculation.
 */
public interface ReviewService {
    /**
     * Add a new review for a movie and update the movie's rating.
     * @param movieId the ID of the movie to review
     * @param review the review to add
     * @return the saved review
     * @throws com.movieapi.exception.MovieNotFoundException if the movie does not exist
     */
    Review addReview(Long movieId, Review review);

    /**
     * Add a new review for a movie using request DTO and update the movie's rating.
     * @param movieId the ID of the movie to review
     * @param reviewRequest the review data from request
     * @return the saved review
     * @throws com.movieapi.exception.MovieNotFoundException if the movie does not exist
     */
    Review addReview(Long movieId, com.movieapi.dto.ReviewRequest reviewRequest);

    /**
     * Update an existing review and update the movie's rating.
     * @param reviewId the ID of the review to update
     * @param updatedReview the updated review data
     * @return the updated review
     * @throws IllegalArgumentException if the review does not exist
     */
    Review updateReview(Long reviewId, Review updatedReview);

    /**
     * Update an existing review using request DTO and update the movie's rating.
     * @param reviewId the ID of the review to update
     * @param reviewRequest the updated review data from request
     * @return the updated review
     * @throws com.movieapi.exception.ReviewNotFoundException if the review does not exist
     */
    Review updateReview(Long reviewId, com.movieapi.dto.ReviewRequest reviewRequest);

    /**
     * Delete a review and update the movie's rating.
     * @param reviewId the ID of the review to delete
     * @throws IllegalArgumentException if the review does not exist
     */
    void deleteReview(Long reviewId);

    /**
     * Get all reviews for a specific movie.
     * @param movieId the ID of the movie
     * @return list of reviews for the movie
     * @throws com.movieapi.exception.MovieNotFoundException if the movie does not exist
     */
    List<Review> getReviewsByMovie(Long movieId);

    /**
     * Get a review by its ID.
     * @param reviewId the ID of the review
     * @return optional containing the review if found, empty otherwise
     */
    Optional<Review> getReview(Long reviewId);

    /**
     * Get a specific review for a movie by both movie ID and review ID.
     * @param movieId the ID of the movie
     * @param reviewId the ID of the review
     * @return the review if found and belongs to the movie
     * @throws com.movieapi.exception.MovieNotFoundException if the movie does not exist
     * @throws com.movieapi.exception.ReviewNotFoundException if the review does not exist or doesn't belong to the movie
     */
    Review getReviewByMovieAndReviewId(Long movieId, Long reviewId);
    /**
     * Get all reviews.
     * @return list of all reviews
     */
    List<Review> getAllReviews();

    /**
     * Get all reviews with pagination.
     * @param pageable pagination information
     * @return page of reviews
     */
    org.springframework.data.domain.Page<Review> getAllReviews(org.springframework.data.domain.Pageable pageable);
    /**
     * Search reviews with filtering and sorting.
     * @param minRating Minimum rating (inclusive)
     * @param maxRating Maximum rating (inclusive)
     * @param userName Reviewer name (partial match)
     * @param startDate Start date (inclusive, ISO format)
     * @param endDate End date (inclusive, ISO format)
     * @param pageable Pagination and sorting info
     * @return Page of filtered reviews
     */
    org.springframework.data.domain.Page<Review> searchReviews(Double minRating, Double maxRating, String userName, String startDate, String endDate, org.springframework.data.domain.Pageable pageable);

    /**
     * Update the rating of a movie based on its reviews.
     * @param movie the movie whose rating should be updated
     */
    void updateMovieRating(com.movieapi.entity.Movie movie);
}
