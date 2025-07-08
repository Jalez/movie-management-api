package com.movieapi.controller;

import com.movieapi.entity.Movie;
import com.movieapi.service.MovieService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
