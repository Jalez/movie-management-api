package com.movieapi.validation;

import com.movieapi.exception.InvalidMovieDataException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

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

    /**
     * Validates advanced search parameters including range queries and pagination.
     *
     * @param genre       the genre parameter
     * @param releaseYear the release year parameter
     * @param minRating   the minimum rating parameter
     * @param maxRating   the maximum rating parameter
     * @param yearMin     the minimum year parameter
     * @param yearMax     the maximum year parameter
     * @param title       the title parameter
     * @param director    the director parameter
     * @param page        the page number
     * @param size        the page size
     * @param sort        the sort parameter
     * @throws InvalidMovieDataException if any parameter is invalid
     */
    public void validateAdvancedSearchParameters(String genre, Integer releaseYear, BigDecimal minRating, 
                                               BigDecimal maxRating, Integer yearMin, Integer yearMax,
                                               String title, String director, int page, int size, String sort) {
        int currentYear = Year.now().getValue();
        
        // Validate basic parameters
        validateSearchParameters(genre, releaseYear, minRating, director);
        
        // Validate title
        if (title != null && title.trim().isEmpty()) {
            throw new InvalidMovieDataException("title", title, "Title cannot be empty if provided");
        }
        
        // Validate maximum rating
        if (maxRating != null) {
            if (maxRating.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidMovieDataException("maxRating", maxRating, "Maximum rating cannot be negative");
            }
            if (maxRating.compareTo(new BigDecimal("10.0")) > 0) {
                throw new InvalidMovieDataException("maxRating", maxRating, "Maximum rating cannot exceed 10.0");
            }
        }
        
        // Validate rating range
        if (minRating != null && maxRating != null && minRating.compareTo(maxRating) > 0) {
            throw new InvalidMovieDataException("minRating", minRating, 
                    "Minimum rating cannot be greater than maximum rating");
        }
        
        // Validate year range parameters
        if (yearMin != null) {
            if (yearMin < 1900) {
                throw new InvalidMovieDataException("yearMin", yearMin, "Minimum year must be 1900 or later");
            }
            if (yearMin > currentYear + 5) {
                throw new InvalidMovieDataException("yearMin", yearMin, 
                        String.format("Minimum year cannot be more than 5 years in the future (current year: %d)", currentYear));
            }
        }
        
        if (yearMax != null) {
            if (yearMax < 1900) {
                throw new InvalidMovieDataException("yearMax", yearMax, "Maximum year must be 1900 or later");
            }
            if (yearMax > currentYear + 5) {
                throw new InvalidMovieDataException("yearMax", yearMax, 
                        String.format("Maximum year cannot be more than 5 years in the future (current year: %d)", currentYear));
            }
        }
        
        // Validate year range
        if (yearMin != null && yearMax != null && yearMin > yearMax) {
            throw new InvalidMovieDataException("yearMin", yearMin, 
                    "Minimum year cannot be greater than maximum year");
        }
        
        // Validate pagination parameters
        if (page < 0) {
            throw new InvalidMovieDataException("page", page, "Page number cannot be negative");
        }
        
        if (size < 1) {
            throw new InvalidMovieDataException("size", size, "Page size must be at least 1");
        }
        
        if (size > 100) {
            throw new InvalidMovieDataException("size", size, "Page size cannot exceed 100");
        }
        
        // Validate sort parameter
        if (sort != null && !sort.trim().isEmpty()) {
            String[] sortParts = sort.split(",");
            if (sortParts.length > 2) {
                throw new InvalidMovieDataException("sort", sort, 
                        "Sort parameter must be in format 'field' or 'field,direction'");
            }
            
            String field = sortParts[0].trim();
            if (!isValidSortField(field)) {
                throw new InvalidMovieDataException("sort", field, 
                        "Invalid sort field. Allowed fields: title, director, genre, releaseYear, rating, id");
            }
            
            if (sortParts.length == 2) {
                String direction = sortParts[1].trim();
                if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
                    throw new InvalidMovieDataException("sort", direction, 
                            "Invalid sort direction. Allowed values: asc, desc");
                }
            }
        }
    }
    
    /**
     * Check if the sort field is valid.
     */
    private boolean isValidSortField(String field) {
        return List.of("title", "director", "genre", "releaseYear", "rating", "id").contains(field);
    }
}