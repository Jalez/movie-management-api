package com.movieapi.validation;

import com.movieapi.exception.InvalidMovieDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MovieSearchValidator.
 * Tests validation logic for search parameters in isolation.
 */
class MovieSearchValidatorTest {

    private MovieSearchValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MovieSearchValidator();
    }

    @Test
    void validateSearchParameters_WithValidParameters_ShouldNotThrowException() {
        // Test valid parameters - should not throw any exception
        assertDoesNotThrow(() -> validator.validateSearchParameters(
                "Action", 2020, new BigDecimal("8.5"), "John Doe"));
    }

    @Test
    void validateSearchParameters_WithNullParameters_ShouldNotThrowException() {
        // Test null parameters - should not throw any exception
        assertDoesNotThrow(() -> validator.validateSearchParameters(
                null, null, null, null));
    }

    @Test
    void validateSearchParameters_WithEmptyGenre_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateSearchParameters("", null, null, null));
        
        assertEquals("genre", exception.getField());
        assertTrue(exception.getMessage().contains("Genre cannot be empty"));
    }

    @Test
    void validateSearchParameters_WithWhitespaceGenre_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateSearchParameters("   ", null, null, null));
        
        assertEquals("genre", exception.getField());
        assertTrue(exception.getMessage().contains("Genre cannot be empty"));
    }

    @Test
    void validateSearchParameters_WithEmptyDirector_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateSearchParameters(null, null, null, ""));
        
        assertEquals("director", exception.getField());
        assertTrue(exception.getMessage().contains("Director cannot be empty"));
    }

    @Test
    void validateSearchParameters_WithWhitespaceDirector_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateSearchParameters(null, null, null, "   "));
        
        assertEquals("director", exception.getField());
        assertTrue(exception.getMessage().contains("Director cannot be empty"));
    }

    @Test
    void validateSearchParameters_WithTooEarlyReleaseYear_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateSearchParameters(null, 1800, null, null));
        
        assertEquals("releaseYear", exception.getField());
        assertTrue(exception.getMessage().contains("Release year must be 1900 or later"));
    }

    @Test
    void validateSearchParameters_WithBoundaryReleaseYear1900_ShouldNotThrowException() {
        // 1900 is the minimum valid year
        assertDoesNotThrow(() -> validator.validateSearchParameters(null, 1900, null, null));
    }

    @Test
    void validateSearchParameters_WithCurrentYear_ShouldNotThrowException() {
        int currentYear = Year.now().getValue();
        assertDoesNotThrow(() -> validator.validateSearchParameters(null, currentYear, null, null));
    }

    @Test
    void validateSearchParameters_WithValidFutureYear_ShouldNotThrowException() {
        int futureYear = Year.now().getValue() + 3; // Within 5 years
        assertDoesNotThrow(() -> validator.validateSearchParameters(null, futureYear, null, null));
    }

    @Test
    void validateSearchParameters_WithTooFarFutureYear_ShouldThrowException() {
        int tooFarFuture = Year.now().getValue() + 10;
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateSearchParameters(null, tooFarFuture, null, null));
        
        assertEquals("releaseYear", exception.getField());
        assertTrue(exception.getMessage().contains("Release year cannot be more than 5 years in the future"));
        assertTrue(exception.getMessage().contains("current year: " + Year.now().getValue()));
    }

    @Test
    void validateSearchParameters_WithNegativeMinRating_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateSearchParameters(null, null, new BigDecimal("-1.0"), null));
        
        assertEquals("minRating", exception.getField());
        assertTrue(exception.getMessage().contains("Minimum rating cannot be negative"));
    }

    @Test
    void validateSearchParameters_WithZeroMinRating_ShouldNotThrowException() {
        // 0.0 is the minimum valid rating
        assertDoesNotThrow(() -> validator.validateSearchParameters(null, null, BigDecimal.ZERO, null));
    }

    @Test
    void validateSearchParameters_WithMaxValidMinRating_ShouldNotThrowException() {
        // 10.0 is the maximum valid rating
        assertDoesNotThrow(() -> validator.validateSearchParameters(null, null, new BigDecimal("10.0"), null));
    }

    @Test
    void validateSearchParameters_WithExcessiveMinRating_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateSearchParameters(null, null, new BigDecimal("15.0"), null));
        
        assertEquals("minRating", exception.getField());
        assertTrue(exception.getMessage().contains("Minimum rating cannot exceed 10.0"));
    }

    @Test
    void validateSearchParameters_WithBoundaryMinRating_ShouldNotThrowException() {
        // Test boundary values that should be valid
        assertDoesNotThrow(() -> validator.validateSearchParameters(null, null, new BigDecimal("0.1"), null));
        assertDoesNotThrow(() -> validator.validateSearchParameters(null, null, new BigDecimal("9.9"), null));
        assertDoesNotThrow(() -> validator.validateSearchParameters(null, null, new BigDecimal("5.0"), null));
    }
}