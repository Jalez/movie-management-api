package com.movieapi.controller;

import com.movieapi.entity.Movie;
import com.movieapi.exception.InvalidMovieDataException;
import com.movieapi.service.MovieService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Movie operations.
 * Provides CRUD endpoints for managing movies with proper HTTP status codes
 * and error handling.
 */
@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Get all movies.
     *
     * @return ResponseEntity containing list of all movies
     */
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        logger.debug("GET /movies - Retrieving all movies");
        
        List<Movie> movies = movieService.getAllMovies();
        
        logger.info("GET /movies - Retrieved {} movies", movies.size());
        return ResponseEntity.ok(movies);
    }

    /**
     * Get a specific movie by ID.
     *
     * @param id the movie ID
     * @return ResponseEntity containing the movie if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        logger.debug("GET /movies/{} - Retrieving movie by ID", id);
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isPresent()) {
            logger.info("GET /movies/{} - Movie found: {}", id, movie.get().getTitle());
            return ResponseEntity.ok(movie.get());
        } else {
            logger.info("GET /movies/{} - Movie not found", id);
            throw new com.movieapi.exception.MovieNotFoundException("Movie with ID " + id + " not found");
        }
    }

    /**
     * Create a new movie.
     *
     * @param movie the movie to create
     * @return ResponseEntity containing the created movie with 201 status
     */
    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        logger.debug("POST /movies - Creating new movie: {}", movie != null ? movie.getTitle() : "null");
        
        Movie createdMovie = movieService.createMovie(movie);
        
        logger.info("POST /movies - Successfully created movie with ID: {} - {}", 
                   createdMovie.getId(), createdMovie.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    /**
     * Update an existing movie.
     *
     * @param id    the ID of the movie to update
     * @param movie the updated movie data
     * @return ResponseEntity containing the updated movie
     */
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, 
                                           @Valid @RequestBody Movie movie) {
        logger.debug("PUT /movies/{} - Updating movie", id);
        
        Movie updatedMovie = movieService.updateMovie(id, movie);
        
        logger.info("PUT /movies/{} - Successfully updated movie: {}", 
                   id, updatedMovie.getTitle());
        return ResponseEntity.ok(updatedMovie);
    }

    /**
     * Delete a movie by ID.
     *
     * @param id the ID of the movie to delete
     * @return ResponseEntity with 204 status if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        logger.debug("DELETE /movies/{} - Deleting movie", id);
        
        movieService.deleteMovie(id);
        
        logger.info("DELETE /movies/{} - Successfully deleted movie", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search movies based on multiple criteria.
     * All query parameters are optional and can be combined.
     *
     * @param genre       the genre to filter by (case-sensitive, exact match)
     * @param releaseYear the release year to filter by (exact match)
     * @param minRating   the minimum rating to filter by (inclusive)
     * @param director    the director name to search for (case-insensitive partial match)
     * @return ResponseEntity containing list of movies matching the criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<Movie>> searchMovies(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) String director) {
        
        logger.debug("GET /movies/search - Searching with criteria: genre={}, releaseYear={}, minRating={}, director={}", 
                    genre, releaseYear, minRating, director);
        
        // Validate query parameters
        validateSearchParameters(genre, releaseYear, minRating, director);
        
        List<Movie> movies = movieService.searchMovies(genre, releaseYear, minRating, director);
        
        logger.info("GET /movies/search - Found {} movies matching criteria", movies.size());
        return ResponseEntity.ok(movies);
    }

    /**
     * Validates search parameters according to business rules.
     *
     * @param genre       the genre parameter
     * @param releaseYear the release year parameter
     * @param minRating   the minimum rating parameter
     * @param director    the director parameter
     * @throws InvalidMovieDataException if any parameter is invalid
     */
    private void validateSearchParameters(String genre, Integer releaseYear, BigDecimal minRating, String director) {
        int currentYear = Year.now().getValue();
        
        // Validate genre
        if (genre != null && genre.trim().isEmpty()) {
            throw new InvalidMovieDataException("genre", genre, "Genre cannot be empty if provided");
        }
        
        // Validate release year
        if (releaseYear != null) {
            if (releaseYear < 1900) {
                throw new InvalidMovieDataException("releaseYear", releaseYear, "Release year must be 1900 or later");
            }
            if (releaseYear > currentYear + 5) {
                throw new InvalidMovieDataException("releaseYear", releaseYear, 
                        String.format("Release year cannot be more than 5 years in the future (current year: %d)", currentYear));
            }
        }
        
        // Validate minimum rating
        if (minRating != null) {
            if (minRating.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidMovieDataException("minRating", minRating, "Minimum rating cannot be negative");
            }
            if (minRating.compareTo(new BigDecimal("10.0")) > 0) {
                throw new InvalidMovieDataException("minRating", minRating, "Minimum rating cannot exceed 10.0");
            }
        }
        
        // Validate director
        if (director != null && director.trim().isEmpty()) {
            throw new InvalidMovieDataException("director", director, "Director cannot be empty if provided");
        }
    }
}
