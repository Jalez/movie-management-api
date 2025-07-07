package com.movieapi.entity;

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
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Director cannot be blank")
    @Size(max = 255, message = "Director cannot exceed 255 characters")
    @Column(name = "director", nullable = false)
    private String director;

    @NotBlank(message = "Genre cannot be blank")
    @Size(max = 100, message = "Genre cannot exceed 100 characters")
    @Column(name = "genre", nullable = false)
    private String genre;

    @NotNull(message = "Release year cannot be null")
    @Min(value = 1888, message = "Release year must be 1888 or later") // First motion picture
    @Max(value = 2100, message = "Release year cannot be in the far future")
    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Rating cannot exceed 10.0")
    @Digits(integer = 2, fraction = 1, message = "Rating must have at most 2 integer digits and 1 decimal place")
    @Column(name = "rating", nullable = false, precision = 3, scale = 1)
    private BigDecimal rating;

    // Default constructor (required by JPA)
    public Movie() {
    }

    // Constructor with all fields except id
    public Movie(String title, String director, String genre, Integer releaseYear, BigDecimal rating) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.rating = rating;
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
