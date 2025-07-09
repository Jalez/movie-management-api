package com.movieapi.testutil;

import com.movieapi.entity.Movie;
import java.math.BigDecimal;

/**
 * Test data builder for creating Movie instances in tests.
 * Provides fluent API for building test data with sensible defaults.
 */
public class MovieTestDataBuilder {
    
    private Long id;
    private String title = "Default Movie";
    private String director = "Default Director";
    private String genre = "Drama";
    private Integer releaseYear = 2020;
    private BigDecimal rating = new BigDecimal("8.0");
    
    public static MovieTestDataBuilder aMovie() {
        return new MovieTestDataBuilder();
    }
    
    public MovieTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public MovieTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public MovieTestDataBuilder withDirector(String director) {
        this.director = director;
        return this;
    }
    
    public MovieTestDataBuilder withGenre(String genre) {
        this.genre = genre;
        return this;
    }
    
    public MovieTestDataBuilder withReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }
    
    public MovieTestDataBuilder withRating(BigDecimal rating) {
        this.rating = rating;
        return this;
    }
    
    public MovieTestDataBuilder withRating(double rating) {
        this.rating = new BigDecimal(String.valueOf(rating));
        return this;
    }
    
    // Common preset builders
    public static MovieTestDataBuilder inception() {
        return aMovie()
                .withTitle("Inception")
                .withDirector("Christopher Nolan")
                .withGenre("Sci-Fi")
                .withReleaseYear(2010)
                .withRating(8.8);
    }
    
    public static MovieTestDataBuilder theMatrix() {
        return aMovie()
                .withTitle("The Matrix")
                .withDirector("The Wachowskis")
                .withGenre("Sci-Fi")
                .withReleaseYear(1999)
                .withRating(8.7);
    }
    
    public static MovieTestDataBuilder theDarkKnight() {
        return aMovie()
                .withTitle("The Dark Knight")
                .withDirector("Christopher Nolan")
                .withGenre("Action")
                .withReleaseYear(2008)
                .withRating(9.0);
    }
    
    // Invalid data builders for testing validation
    public static MovieTestDataBuilder movieWithBlankTitle() {
        return aMovie().withTitle("");
    }
    
    public static MovieTestDataBuilder movieWithBlankDirector() {
        return aMovie().withDirector("");
    }
    
    public static MovieTestDataBuilder movieWithInvalidRating() {
        return aMovie().withRating(15.0);
    }
    
    public static MovieTestDataBuilder movieWithInvalidReleaseYear() {
        return aMovie().withReleaseYear(1800);
    }
    
    public static MovieTestDataBuilder movieWithFutureReleaseYear() {
        return aMovie().withReleaseYear(2050);
    }
    
    public Movie build() {
        Movie movie = new Movie();
        if (id != null) {
            movie.setId(id);
        }
        movie.setTitle(title);
        movie.setDirector(director);
        movie.setGenre(genre);
        movie.setReleaseYear(releaseYear);
        movie.setRating(rating);
        return movie;
    }
}
