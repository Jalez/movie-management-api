package com.movieapi.validation;

import com.movieapi.exception.InvalidMovieDataException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Year;

/**
 * Validator for movie search parameters.
 * Provides validation logic for search endpoint parameters according to business rules.
 */
@Component
public class MovieSearchValidator {

    /**
     * Validates search parameters according to business rules.
     *
     * @param genre       the genre parameter
     * @param releaseYear the release year parameter
     * @param minRating   the minimum rating parameter
     * @param director    the director parameter
     * @throws InvalidMovieDataException if any parameter is invalid
     */
    public void validateSearchParameters(String genre, Integer releaseYear, BigDecimal minRating, String director) {
        int currentYear = Year.now().getValue();
        
        // Validate genre
        if (genre != null && genre.trim().isEmpty()) {
            throw new InvalidMovieDataException("genre", genre, "Genre cannot be empty if provided");
        }
        
        // Validate release year
        if (releaseYear != null) {
            if (releaseYear < 1900) {
                throw new InvalidMovieDataException("releaseYear", releaseYear, "Release year must be 1900 or later");
            }
            if (releaseYear > currentYear + 5) {
                throw new InvalidMovieDataException("releaseYear", releaseYear, 
                        String.format("Release year cannot be more than 5 years in the future (current year: %d)", currentYear));
            }
        }
        
        // Validate minimum rating
        if (minRating != null) {
            if (minRating.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidMovieDataException("minRating", minRating, "Minimum rating cannot be negative");
            }
            if (minRating.compareTo(new BigDecimal("10.0")) > 0) {
                throw new InvalidMovieDataException("minRating", minRating, "Minimum rating cannot exceed 10.0");
            }
        }
        
        // Validate director
        if (director != null && director.trim().isEmpty()) {
            throw new InvalidMovieDataException("director", director, "Director cannot be empty if provided");
        }
    }
}