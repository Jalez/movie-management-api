package com.movieapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO for review response data.
 * Contains only the fields that should be returned in API responses.
 */
@Schema(description = "Review response data")
public class ReviewResponse {
    
    @Schema(description = "Unique identifier of the review", example = "2")
    private Long id;
    
    @Schema(description = "Reviewer name", example = "John Doe")
    private String userName;
    
    @Schema(description = "Review content", example = "Great movie! The plot was engaging and the acting was superb.")
    private String reviewText;
    
    @Schema(description = "Individual review rating (1.0-10.0)", example = "9.0")
    private Double rating;
    
    @Schema(description = "Review creation timestamp", example = "2025-07-14T13:16:08.666263")
    private LocalDateTime createdAt;
    
    @Schema(description = "Review update timestamp", example = "2025-07-14T15:04:55.310303")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public ReviewResponse() {}
    
    // Constructor with all fields
    public ReviewResponse(Long id, String userName, String reviewText, Double rating, 
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userName = userName;
        this.reviewText = reviewText;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 