package com.movieapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO for review request data.
 * Contains only the fields that should be provided in the request body.
 */
@Schema(description = "Review request data")
public class ReviewRequest {
    
    @NotBlank(message = "User name cannot be blank")
    @Size(max = 100, message = "User name cannot exceed 100 characters")
    @Schema(description = "Reviewer name", example = "John Doe", required = true, maxLength = 100)
    private String userName;
    
    @Size(max = 2000, message = "Review text cannot exceed 2000 characters")
    @Schema(description = "Review content", example = "Great movie! The plot was engaging and the acting was superb.", maxLength = 2000)
    private String reviewText;
    
    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "10.0", message = "Rating cannot exceed 10.0")
    @Schema(description = "Individual review rating (1.0-10.0)", example = "8.5", required = true, minimum = "1.0", maximum = "10.0")
    private Double rating;
    
    // Default constructor
    public ReviewRequest() {}
    
    // Constructor with all fields
    public ReviewRequest(String userName, String reviewText, Double rating) {
        this.userName = userName;
        this.reviewText = reviewText;
        this.rating = rating;
    }
    
    // Getters and setters
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getReviewText() {
        return reviewText;
    }
    
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
} 