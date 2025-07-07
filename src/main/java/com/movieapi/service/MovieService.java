package com.movieapi.service;

import com.movieapi.entity.Movie;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Movie business logic operations.
 * Defines the contract for all movie-related business operations including
 * CRUD operations, validation, and search functionality.
 */
public interface MovieService {

    /**
     * Retrieve all movies from the database.
     *
     * @return list of all movies
     */
    List<Movie> getAllMovies();

    /**
     * Retrieve a movie by its ID.
     *
     * @param id the movie ID
     * @return optional containing the movie if found, empty otherwise
     */
    Optional<Movie> getMovieById(Long id);

    /**
     * Create a new movie after validating business rules.
     * Performs duplicate detection and validation before saving.
     *
     * @param movie the movie to create
     * @return the created movie with generated ID
     * @throws com.movieapi.exception.DuplicateMovieException if movie already exists
     * @throws com.movieapi.exception.InvalidMovieDataException if validation fails
     */
    Movie createMovie(Movie movie);

    /**
     * Update an existing movie by ID.
     * Validates business rules and updates the movie if it exists.
     *
     * @param id    the ID of the movie to update
     * @param movie the updated movie data
     * @return the updated movie
     * @throws com.movieapi.exception.MovieNotFoundException if movie doesn't exist
     * @throws com.movieapi.exception.InvalidMovieDataException if validation fails
     */
    Movie updateMovie(Long id, Movie movie);

    /**
     * Delete a movie by its ID.
     *
     * @param id the ID of the movie to delete
     * @throws com.movieapi.exception.MovieNotFoundException if movie doesn't exist
     */
    void deleteMovie(Long id);

    /**
     * Search for movies based on multiple criteria.
     * All parameters are optional (can be null) and will be ignored if not provided.
     *
     * @param genre     the genre to filter by (case-insensitive, optional)
     * @param year      the release year to filter by (optional)
     * @param minRating the minimum rating to filter by (inclusive, optional)
     * @param director  the director name to search for (case-insensitive partial match, optional)
     * @return list of movies matching the criteria
     */
    List<Movie> searchMovies(String genre, Integer year, BigDecimal minRating, String director);

    /**
     * Get movies by genre (case-insensitive).
     *
     * @param genre the genre to search for
     * @return list of movies in the specified genre
     */
    List<Movie> getMoviesByGenre(String genre);

    /**
     * Get movies by release year.
     *
     * @param year the release year
     * @return list of movies released in the specified year
     */
    List<Movie> getMoviesByYear(Integer year);

    /**
     * Get movies with rating greater than or equal to the specified value.
     *
     * @param minRating the minimum rating (inclusive)
     * @return list of movies with rating >= minRating
     */
    List<Movie> getMoviesByMinRating(BigDecimal minRating);

    /**
     * Get movies by director name (case-insensitive partial match).
     *
     * @param director the director name to search for
     * @return list of movies by the specified director
     */
    List<Movie> getMoviesByDirector(String director);

    /**
     * Get the top-rated movies limited by count.
     *
     * @param limit the maximum number of movies to return
     * @return list of top-rated movies
     */
    List<Movie> getTopRatedMovies(int limit);

    /**
     * Get the average rating of all movies.
     *
     * @return the average rating, or empty if no movies exist
     */
    Optional<BigDecimal> getAverageRating();

    /**
     * Check if a movie exists with the given title and director.
     *
     * @param title    the movie title
     * @param director the director name
     * @return true if movie exists, false otherwise
     */
    boolean movieExists(String title, String director);

    /**
     * Count the number of movies in a specific genre.
     *
     * @param genre the genre to count (case-insensitive)
     * @return the number of movies in the genre
     */
    long countMoviesByGenre(String genre);
}
