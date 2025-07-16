package com.movieapi.controller;

import com.movieapi.entity.Review;
import com.movieapi.service.ReviewService;
import com.movieapi.dto.ErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.movieapi.dto.PagedReviewsResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Review operations.
 * Provides endpoints for managing reviews and movie ratings.
 */
@RestController
@RequestMapping
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Review Management", description = "API for managing reviews for movies")
public class ReviewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    /**
     * Search reviews with filtering and sorting.
     * All query parameters are optional and can be combined.
     */
    @GetMapping(value = "/reviews/search", produces = "application/json")
    @Operation(
        summary = "Search reviews with pagination and sorting",
        description = "Search for reviews using various criteria with pagination and sorting support. All parameters are optional and can be combined for advanced filtering."
    )
    public ResponseEntity<PagedReviewsResponse> searchReviews(
            @Parameter(description = "Filter by minimum rating (inclusive, 1.0-10.0)", example = "7.0")
            @RequestParam(required = false) Double minRating,
            @Parameter(description = "Filter by maximum rating (inclusive, 1.0-10.0)", example = "9.5")
            @RequestParam(required = false) Double maxRating,
            @Parameter(description = "Filter by reviewer name (partial match)", example = "John")
            @RequestParam(required = false) String userName,
            @Parameter(description = "Filter by start date (inclusive, ISO format)", example = "2025-07-01T00:00:00")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Filter by end date (inclusive, ISO format)", example = "2025-07-14T23:59:59")
            @RequestParam(required = false) String endDate,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction). Available fields: rating, createdAt, userName. Direction: asc, desc", example = "rating,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        logger.debug("GET /reviews/search - Searching reviews with criteria - minRating: {}, maxRating: {}, userName: {}, startDate: {}, endDate: {}, page: {}, size: {}, sort: {}", 
                    minRating, maxRating, userName, startDate, endDate, page, size, sort);

        Pageable pageable = createPageable(page, size, sort);
        Page<Review> reviews = reviewService.searchReviews(minRating, maxRating, userName, startDate, endDate, pageable);
        PagedReviewsResponse response;
        if (reviews == null) {
            response = new PagedReviewsResponse(
                List.of(),
                page,
                size,
                0,
                0,
                0,
                true,
                true,
                false,
                false
            );
        } else {
            response = new PagedReviewsResponse(
                reviews.getContent(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getNumberOfElements(),
                reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isFirst(),
                reviews.isLast(),
                reviews.hasPrevious(),
                reviews.hasNext()
            );
        }
        
        logger.info("GET /reviews/search - Found {} reviews on page {} of {} total pages", 
                   reviews != null ? reviews.getNumberOfElements() : 0, 
                   reviews != null ? reviews.getNumber() : 0, 
                   reviews != null ? reviews.getTotalPages() : 0);
        
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(response);
    }

    private Pageable createPageable(int page, int size, String sort) {
        size = Math.min(size, 100);
        size = Math.max(size, 1);
        if (sort == null || sort.trim().isEmpty()) {
            return PageRequest.of(page, size);
        }
        String[] sortParts = sort.split(",");
        String field = sortParts[0].trim();
        String direction = sortParts.length > 1 ? sortParts[1].trim() : "asc";
        if (!isValidSortField(field)) {
            field = "createdAt";
        }
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(sortDirection, field));
    }

    private boolean isValidSortField(String field) {
        return List.of("rating", "createdAt", "userName").contains(field);
    }

    /**
     * Add a review to a movie.
     */
    @PostMapping("/movies/{movieId}/reviews")
    @Operation(summary = "Add review to movie", description = "Adds a new review to the specified movie.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review created successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = com.movieapi.dto.ReviewResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = "{\"id\": 1, \"userName\": \"John Doe\", \"reviewText\": \"Great movie!\", \"rating\": 8.5, \"createdAt\": \"2025-07-16T01:00:00\", \"updatedAt\": \"2025-07-16T01:00:00\"}"
                ))),
        @ApiResponse(responseCode = "404", description = "Movie not found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.MovieNotFoundError.class))),
        @ApiResponse(responseCode = "400", description = "Invalid review data or movie ID format",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.ValidationError.class)))
    })
    public ResponseEntity<com.movieapi.dto.ReviewResponse> addReview(
            @Parameter(description = "Movie ID", required = true, example = "1") @PathVariable Long movieId,
            @Valid @RequestBody com.movieapi.dto.ReviewRequest reviewRequest) {
        logger.debug("POST /movies/{}/reviews - Adding review for movie", movieId);
        
        Review created = reviewService.addReview(movieId, reviewRequest);
        com.movieapi.dto.ReviewResponse response = convertToReviewResponse(created);
        
        logger.info("POST /movies/{}/reviews - Successfully created review with ID: {} - {} by {}", 
                   movieId, created.getId(), created.getReviewText(), created.getUserName());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * Get all reviews for a movie.
     */
    @GetMapping("/movies/{movieId}/reviews")
    @Operation(summary = "Get reviews for movie", description = "Retrieves all reviews for the specified movie.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = com.movieapi.dto.ReviewResponse.class, type = "array"))),
        @ApiResponse(responseCode = "404", description = "Movie not found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.MovieNotFoundError.class))),
        @ApiResponse(responseCode = "400", description = "Invalid movie ID format",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.BadRequestError.class)))
    })
    public ResponseEntity<List<com.movieapi.dto.ReviewResponse>> getReviewsByMovie(
            @Parameter(description = "Movie ID", required = true, example = "1") @PathVariable Long movieId) {
        logger.debug("GET /movies/{}/reviews - Retrieving reviews for movie", movieId);
        
        List<Review> reviews = reviewService.getReviewsByMovie(movieId);
        List<com.movieapi.dto.ReviewResponse> responses = reviews.stream()
                .map(this::convertToReviewResponse)
                .toList();
        
        logger.info("GET /movies/{}/reviews - Retrieved {} reviews", movieId, reviews.size());
        
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(responses);
    }

    /**
     * Get a specific review for a movie.
     */
    @GetMapping("/movies/{movieId}/reviews/{reviewId}")
    @Operation(summary = "Get specific review for movie", description = "Retrieves a specific review for the specified movie.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review retrieved successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = com.movieapi.dto.ReviewResponse.class))),
        @ApiResponse(responseCode = "404", description = "Movie or review not found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.ReviewNotFoundError.class))),
        @ApiResponse(responseCode = "400", description = "Invalid movie ID or review ID format",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.BadRequestError.class)))
    })
    public ResponseEntity<com.movieapi.dto.ReviewResponse> getReview(
            @Parameter(description = "Movie ID", required = true, example = "1") @PathVariable Long movieId,
            @Parameter(description = "Review ID", required = true, example = "1") @PathVariable Long reviewId) {
        logger.debug("GET /movies/{}/reviews/{} - Retrieving specific review", movieId, reviewId);
        
        Review review = reviewService.getReviewByMovieAndReviewId(movieId, reviewId);
        com.movieapi.dto.ReviewResponse response = convertToReviewResponse(review);
        
        logger.info("GET /movies/{}/reviews/{} - Review found: {} by {}", 
                   movieId, reviewId, review.getReviewText(), review.getUserName());
        
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * Update a review for a movie.
     */
    @PutMapping("/movies/{movieId}/reviews/{reviewId}")
    @Operation(summary = "Update review for movie", description = "Updates a specific review for the specified movie.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review updated successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = com.movieapi.dto.ReviewResponse.class))),
        @ApiResponse(responseCode = "404", description = "Movie or review not found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.ReviewNotFoundError.class))),
        @ApiResponse(responseCode = "400", description = "Invalid review data or ID format",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.ValidationError.class)))
    })
    public ResponseEntity<com.movieapi.dto.ReviewResponse> updateReview(
            @Parameter(description = "Movie ID", required = true, example = "1") @PathVariable Long movieId,
            @Parameter(description = "Review ID", required = true, example = "1") @PathVariable Long reviewId,
            @Valid @RequestBody com.movieapi.dto.ReviewRequest reviewRequest) {
        logger.debug("PUT /movies/{}/reviews/{} - Updating review", movieId, reviewId);
        
        Review updated = reviewService.updateReview(reviewId, reviewRequest);
        com.movieapi.dto.ReviewResponse response = convertToReviewResponse(updated);
        
        logger.info("PUT /movies/{}/reviews/{} - Successfully updated review: {} by {}", 
                   movieId, reviewId, updated.getReviewText(), updated.getUserName());
        
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * Delete a review for a movie.
     */
    @DeleteMapping("/movies/{movieId}/reviews/{reviewId}")
    @Operation(summary = "Delete review for movie", description = "Deletes a specific review for the specified movie.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Movie or review not found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.ReviewNotFoundError.class))),
        @ApiResponse(responseCode = "400", description = "Invalid movie ID or review ID format",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponses.BadRequestError.class)))
    })
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Movie ID", required = true, example = "1") @PathVariable Long movieId,
            @Parameter(description = "Review ID", required = true, example = "1") @PathVariable Long reviewId) {
        logger.debug("DELETE /movies/{}/reviews/{} - Deleting review", movieId, reviewId);
        
        reviewService.deleteReview(reviewId);
        
        logger.info("DELETE /movies/{}/reviews/{} - Successfully deleted review", movieId, reviewId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all reviews.
     */
    @GetMapping("/reviews")
    @Operation(summary = "Get all reviews (not paginated)", description = "Retrieves all reviews in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully",
            content = @Content(mediaType = "application/json", 
                            array = @ArraySchema(schema = @Schema(implementation = Review.class)))
        ),
    })
    public ResponseEntity<List<com.movieapi.dto.ReviewResponse>> getAllReviews() {
        logger.debug("GET /reviews - Retrieving all reviews");
        
        List<Review> reviews = reviewService.getAllReviews();
        List<com.movieapi.dto.ReviewResponse> responses = reviews.stream()
                .map(this::convertToReviewResponse)
                .toList();
        
        logger.info("GET /reviews - Retrieved {} reviews", reviews.size());
        
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(responses);
    }

    /**
     * Convert Review entity to ReviewResponse DTO.
     */
    private com.movieapi.dto.ReviewResponse convertToReviewResponse(Review review) {
        return new com.movieapi.dto.ReviewResponse(
                review.getId(),
                review.getUserName(),
                review.getReviewText(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
