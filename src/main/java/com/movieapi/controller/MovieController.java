package com.movieapi.controller;

import com.movieapi.entity.Movie;
import com.movieapi.service.MovieService;
import com.movieapi.validation.MovieSearchValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
@Tag(name = "Movie Management", description = "API for managing movies in the system")
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;
    private final MovieSearchValidator movieSearchValidator;

    @Autowired
    public MovieController(MovieService movieService, MovieSearchValidator movieSearchValidator) {
        this.movieService = movieService;
        this.movieSearchValidator = movieSearchValidator;
    }

    /**
     * Get all movies.
     *
     * @return ResponseEntity containing list of all movies
     */
    @GetMapping
    @Operation(
        summary = "Get all movies",
        description = "Retrieves a list of all movies in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all movies",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Movie.class))
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        )
    })
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
    @Operation(
        summary = "Get movie by ID",
        description = "Retrieves a specific movie by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Movie found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Movie.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid movie ID format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movie not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Movie> getMovieById(
            @Parameter(description = "Unique identifier of the movie", example = "1", required = true)
            @PathVariable Long id) {
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
    @Operation(
        summary = "Create a new movie",
        description = "Creates a new movie in the system with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Movie created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Movie.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid movie data or validation errors",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Movie already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "415",
            description = "Unsupported media type",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Movie> createMovie(
            @Parameter(description = "Movie data to create", required = true)
            @Valid @RequestBody Movie movie) {
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
    @Operation(
        summary = "Update an existing movie",
        description = "Updates an existing movie with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Movie updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Movie.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid movie data or validation errors",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movie not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "415",
            description = "Unsupported media type",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Movie> updateMovie(
            @Parameter(description = "Unique identifier of the movie to update", example = "1", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Updated movie data", required = true)
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
    @Operation(
        summary = "Delete a movie",
        description = "Deletes a movie from the system by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Movie deleted successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid movie ID format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movie not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "Unique identifier of the movie to delete", example = "1", required = true)
            @PathVariable Long id) {
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
    @Operation(
        summary = "Search movies",
        description = "Search for movies using various criteria. All parameters are optional and can be combined for advanced filtering."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully (may return empty list if no matches)",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Movie.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<List<Movie>> searchMovies(
            @Parameter(
                description = "Filter by movie genre (case-sensitive, exact match)",
                example = "Sci-Fi",
                required = false
            )
            @RequestParam(required = false) String genre,
            @Parameter(
                description = "Filter by release year (exact match)",
                example = "2010",
                required = false
            )
            @RequestParam(required = false) Integer releaseYear,
            @Parameter(
                description = "Filter by minimum rating (inclusive, 0.0-10.0)",
                example = "8.5",
                required = false
            )
            @RequestParam(required = false) BigDecimal minRating,
            @Parameter(
                description = "Search by director name (case-insensitive, partial match)",
                example = "Christopher Nolan",
                required = false
            )
            @RequestParam(required = false) String director) {
        
        logger.debug("GET /movies/search - Searching with criteria: genre={}, releaseYear={}, minRating={}, director={}", 
                    genre, releaseYear, minRating, director);
        
        // Validate query parameters
        movieSearchValidator.validateSearchParameters(genre, releaseYear, minRating, director);
        
        List<Movie> movies = movieService.searchMovies(genre, releaseYear, minRating, director);
        
        logger.info("GET /movies/search - Found {} movies matching criteria", movies.size());
        return ResponseEntity.ok(movies);
    }
}
