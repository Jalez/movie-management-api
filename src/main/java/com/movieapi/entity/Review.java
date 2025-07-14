package com.movieapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the review", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @Schema(description = "Movie associated with the review", implementation = Movie.class, required = true, accessMode = Schema.AccessMode.READ_ONLY)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Movie movie;

    @NotBlank
    @Size(max = 100)
    @Column(name = "user_name", nullable = false, length = 100)
    @Schema(description = "Reviewer name", example = "John Doe", required = true, maxLength = 100)
    private String userName;

    @Size(max = 2000)
    @Column(name = "review_text", length = 2000)
    @Schema(description = "Review content", example = "Great movie!", maxLength = 2000)
    private String reviewText;

    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "10.0")
    @Column(name = "rating", nullable = false)
    @Schema(description = "Individual review rating (1.0-10.0)", example = "8.5", required = true, minimum = "1.0", maximum = "10.0")
    private Double rating;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "Review creation timestamp", example = "2025-07-14T12:34:56", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Review update timestamp", example = "2025-07-14T12:34:56", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
