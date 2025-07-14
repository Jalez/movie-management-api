package com.movieapi.controller;

import com.movieapi.entity.Movie;
import com.movieapi.service.MovieService;
import com.movieapi.validation.MovieSearchValidator;
import com.movieapi.dto.ErrorResponses;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        summary = "Get all movies (not paginated)",
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
                schema = @Schema(implementation = ErrorResponses.InternalServerError.class)
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
                schema = @Schema(implementation = ErrorResponses.BadRequestError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movie not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.MovieNotFoundError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.InternalServerError.class)
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
                schema = @Schema(implementation = ErrorResponses.ValidationError.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Movie already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.ConflictError.class)
            )
        ),
        @ApiResponse(
            responseCode = "415",
            description = "Unsupported media type",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.UnsupportedMediaTypeError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.InternalServerError.class)
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
                schema = @Schema(implementation = ErrorResponses.ValidationError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movie not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.MovieNotFoundError.class)
            )
        ),
        @ApiResponse(
            responseCode = "415",
            description = "Unsupported media type",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.UnsupportedMediaTypeError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.InternalServerError.class)
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
                schema = @Schema(implementation = ErrorResponses.BadRequestError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movie not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.MovieNotFoundError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.InternalServerError.class)
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
     * Search movies based on multiple criteria with pagination and sorting support.
     * All query parameters are optional and can be combined.
     *
     * @param genre       the genre to filter by (case-sensitive, exact match)
     * @param releaseYear the release year to filter by (exact match)
     * @param minRating   the minimum rating to filter by (inclusive)
     * @param maxRating   the maximum rating to filter by (inclusive)
     * @param yearMin     the minimum release year to filter by (inclusive)
     * @param yearMax     the maximum release year to filter by (inclusive)
     * @param title       the title to search for (case-insensitive, partial match)
     * @param director    the director name to search for (case-insensitive partial match)
     * @param page        the page number (0-based, default: 0)
     * @param size        the page size (default: 20, max: 100)
     * @param sort        the sorting criteria (field,direction, e.g., "rating,desc")
     * @return ResponseEntity containing Page of movies matching the criteria
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search movies with pagination and sorting",
        description = "Search for movies using various criteria with pagination and sorting support. All parameters are optional and can be combined for advanced filtering."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully (may return empty page if no matches)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.movieapi.dto.PagedMoviesResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search parameters or pagination parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.BadRequestError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.InternalServerError.class)
            )
        )
    })
    public ResponseEntity<Page<Movie>> searchMovies(
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
                description = "Filter by maximum rating (inclusive, 0.0-10.0)",
                example = "9.5",
                required = false
            )
            @RequestParam(required = false) BigDecimal maxRating,
            @Parameter(
                description = "Filter by minimum release year (inclusive)",
                example = "2000",
                required = false
            )
            @RequestParam(required = false) Integer yearMin,
            @Parameter(
                description = "Filter by maximum release year (inclusive)",
                example = "2020",
                required = false
            )
            @RequestParam(required = false) Integer yearMax,
            @Parameter(
                description = "Search by movie title (case-insensitive, partial match)",
                example = "Inception",
                required = false
            )
            @RequestParam(required = false) String title,
            @Parameter(
                description = "Search by director name (case-insensitive, partial match)",
                example = "Christopher Nolan",
                required = false
            )
            @RequestParam(required = false) String director,
            @Parameter(
                description = "Page number (0-based)",
                example = "0",
                required = false
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                description = "Page size (max 100)",
                example = "20",
                required = false
            )
            @RequestParam(defaultValue = "20") int size,
            @Parameter(
                description = "Sort criteria (field,direction). Available fields: title, director, genre, releaseYear, rating. Direction: asc, desc",
                example = "rating,desc",
                required = false
            )
            @RequestParam(defaultValue = "title,asc") String sort) {
        
        logger.debug("GET /movies/search - Advanced search with criteria: genre={}, releaseYear={}, minRating={}, maxRating={}, yearMin={}, yearMax={}, title={}, director={}, page={}, size={}, sort={}", 
                    genre, releaseYear, minRating, maxRating, yearMin, yearMax, title, director, page, size, sort);
        
        // Validate query parameters
        movieSearchValidator.validateAdvancedSearchParameters(genre, releaseYear, minRating, maxRating, 
                                                            yearMin, yearMax, title, director, page, size, sort);
        
        // Create pageable with sorting
        Pageable pageable = createPageable(page, size, sort);
        
        Page<Movie> movies = movieService.searchMoviesAdvanced(genre, releaseYear, minRating, maxRating,
                                                              yearMin, yearMax, title, director, pageable);
        
        logger.info("GET /movies/search - Found {} movies on page {} of {} total pages", 
                   movies.getNumberOfElements(), movies.getNumber(), movies.getTotalPages());
        return ResponseEntity.ok(movies);
    }

    /**
     * Create Pageable object with sorting from request parameters.
     */
    private Pageable createPageable(int page, int size, String sort) {
        // Validate and limit page size
        size = Math.min(size, 100); // Max 100 items per page
        size = Math.max(size, 1);   // Min 1 item per page
        
        if (sort == null || sort.trim().isEmpty()) {
            return PageRequest.of(page, size);
        }
        
        String[] sortParts = sort.split(",");
        String field = sortParts[0].trim();
        String direction = sortParts.length > 1 ? sortParts[1].trim() : "asc";
        
        // Validate sort field
        if (!isValidSortField(field)) {
            field = "title"; // Default to title if invalid
        }
        
        // Map camelCase to snake_case for native queries
        field = mapSortFieldToColumn(field);
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                         Sort.Direction.DESC : Sort.Direction.ASC;
        
        return PageRequest.of(page, size, Sort.by(sortDirection, field));
    }
    
    /**
     * Validate if the sort field is allowed.
     */
    private boolean isValidSortField(String field) {
        return List.of("title", "director", "genre", "releaseYear", "rating", "id").contains(field);
        }

                /**
         * Map camelCase sort fields to snake_case column names for native queries.
         */
        private String mapSortFieldToColumn(String field) {
            switch (field) {
                case "releaseYear": return "release_year";
                default: return field;
            }
        }
}
