package com.movieapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Movie entity representing a movie in the database.
 * Contains all necessary fields with proper validation constraints.
 */
@Entity
@Table(name = "movies")
@Schema(description = "Movie entity representing a movie in the system")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the movie", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(name = "title", nullable = false)
    @Schema(description = "Title of the movie", example = "The Shawshank Redemption", required = true, maxLength = 255)
    private String title;

    @NotBlank(message = "Director cannot be blank")
    @Size(max = 255, message = "Director cannot exceed 255 characters")
    @Column(name = "director", nullable = false)
    @Schema(description = "Director of the movie", example = "Frank Darabont", required = true, maxLength = 255)
    private String director;

    @NotBlank(message = "Genre cannot be blank")
    @Size(max = 100, message = "Genre cannot exceed 100 characters")
    @Column(name = "genre", nullable = false)
    @Schema(description = "Genre of the movie", example = "Drama", required = true, maxLength = 100)
    private String genre;

    @NotNull(message = "Release year cannot be null")
    @Min(value = 1888, message = "Release year must be 1888 or later") // First motion picture
    @Max(value = 2100, message = "Release year cannot be in the far future")
    @Column(name = "release_year", nullable = false)
    @Schema(description = "Year the movie was released", example = "1994", required = true, minimum = "1888", maximum = "2100")
    private Integer releaseYear;

    @Column(name = "rating", nullable = true, precision = 3, scale = 1)
    @Schema(description = "Average rating of the movie based on reviews (0.0 to 10.0, null if no reviews)", example = "9.3", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal rating;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "List of reviews for the movie", accessMode = Schema.AccessMode.READ_ONLY)
    private java.util.List<Review> reviews = new java.util.ArrayList<>();

    // Default constructor (required by JPA)
    public Movie() {
    }

    // Constructor with all fields except id and rating (rating is calculated from reviews)
    public Movie(String title, String director, String genre, Integer releaseYear) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.rating = null; // Rating starts as null and is calculated from reviews
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    /**
     * Calculate and update the movie rating based on its reviews.
     * If there are no reviews, the rating is set to null.
     */
    public void calculateRatingFromReviews() {
        if (reviews == null || reviews.isEmpty()) {
            this.rating = null;
        } else {
            double averageRating = reviews.stream()
                    .mapToDouble(review -> review.getRating().doubleValue())
                    .average()
                    .orElse(0.0);
            
            // Round to 1 decimal place
            this.rating = BigDecimal.valueOf(Math.round(averageRating * 10.0) / 10.0);
        }
    }

    public java.util.List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(java.util.List<Review> reviews) {
        this.reviews = reviews;
    }

    // equals and hashCode based on id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear=" + releaseYear +
                ", rating=" + rating +
                '}';
    }
}
