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

    // --- Advanced Search Parameter Tests ---

    @Test
    void validateAdvancedSearchParameters_WithValidParameters_ShouldNotThrowException() {
        assertDoesNotThrow(() -> validator.validateAdvancedSearchParameters(
                "Action", 2020, new BigDecimal("8.5"), new BigDecimal("9.5"), 2000, 2025, "Inception", "Nolan", 0, 20, "title,asc"));
    }

    @Test
    void validateAdvancedSearchParameters_WithEmptyTitle_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, "   ", null, 0, 20, null));
        assertEquals("title", exception.getField());
        assertTrue(exception.getMessage().contains("Title cannot be empty"));
    }

    @Test
    void validateAdvancedSearchParameters_WithNegativeMaxRating_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, new BigDecimal("-1.0"), null, null, null, null, 0, 20, null));
        assertEquals("maxRating", exception.getField());
        assertTrue(exception.getMessage().contains("Maximum rating cannot be negative"));
    }

    @Test
    void validateAdvancedSearchParameters_WithExcessiveMaxRating_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, new BigDecimal("15.0"), null, null, null, null, 0, 20, null));
        assertEquals("maxRating", exception.getField());
        assertTrue(exception.getMessage().contains("Maximum rating cannot exceed 10.0"));
    }

    @Test
    void validateAdvancedSearchParameters_WithMinRatingGreaterThanMaxRating_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, new BigDecimal("9.0"), new BigDecimal("5.0"), null, null, null, null, 0, 20, null));
        assertEquals("minRating", exception.getField());
        assertTrue(exception.getMessage().contains("Minimum rating cannot be greater than maximum rating"));
    }

    @Test
    void validateAdvancedSearchParameters_WithYearMinTooLow_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, 1800, null, null, null, 0, 20, null));
        assertEquals("yearMin", exception.getField());
        assertTrue(exception.getMessage().contains("Minimum year must be 1900 or later"));
    }

    @Test
    void validateAdvancedSearchParameters_WithYearMinTooHigh_ShouldThrowException() {
        int future = Year.now().getValue() + 10;
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, future, null, null, null, 0, 20, null));
        assertEquals("yearMin", exception.getField());
        assertTrue(exception.getMessage().contains("Minimum year cannot be more than 5 years in the future"));
    }

    @Test
    void validateAdvancedSearchParameters_WithYearMaxTooLow_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, 1800, null, null, 0, 20, null));
        assertEquals("yearMax", exception.getField());
        assertTrue(exception.getMessage().contains("Maximum year must be 1900 or later"));
    }

    @Test
    void validateAdvancedSearchParameters_WithYearMaxTooHigh_ShouldThrowException() {
        int future = Year.now().getValue() + 10;
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, future, null, null, 0, 20, null));
        assertEquals("yearMax", exception.getField());
        assertTrue(exception.getMessage().contains("Maximum year cannot be more than 5 years in the future"));
    }

    @Test
    void validateAdvancedSearchParameters_WithYearMinGreaterThanYearMax_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, 2025, 2020, null, null, 0, 20, null));
        assertEquals("yearMin", exception.getField());
        assertTrue(exception.getMessage().contains("Minimum year cannot be greater than maximum year"));
    }

    @Test
    void validateAdvancedSearchParameters_WithNegativePage_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, -1, 20, null));
        assertEquals("page", exception.getField());
        assertTrue(exception.getMessage().contains("Page number cannot be negative"));
    }

    @Test
    void validateAdvancedSearchParameters_WithSizeLessThanOne_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, 0, 0, null));
        assertEquals("size", exception.getField());
        assertTrue(exception.getMessage().contains("Page size must be at least 1"));
    }

    @Test
    void validateAdvancedSearchParameters_WithSizeGreaterThan100_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, 0, 101, null));
        assertEquals("size", exception.getField());
        assertTrue(exception.getMessage().contains("Page size cannot exceed 100"));
    }

    @Test
    void validateAdvancedSearchParameters_WithSortTooManyParts_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, 0, 20, "title,asc,extra"));
        assertEquals("sort", exception.getField());
        assertTrue(exception.getMessage().contains("Sort parameter must be in format"));
    }

    @Test
    void validateAdvancedSearchParameters_WithInvalidSortField_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, 0, 20, "invalidField,asc"));
        assertEquals("sort", exception.getField());
        assertTrue(exception.getMessage().contains("Invalid sort field"));
    }

    @Test
    void validateAdvancedSearchParameters_WithInvalidSortDirection_ShouldThrowException() {
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
                () -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, 0, 20, "title,upwards"));
        assertEquals("sort", exception.getField());
        assertTrue(exception.getMessage().contains("Invalid sort direction"));
    }

    @Test
    void validateAdvancedSearchParameters_WithValidSortAsc_ShouldNotThrowException() {
        assertDoesNotThrow(() -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, 0, 20, "title,asc"));
    }

    @Test
    void validateAdvancedSearchParameters_WithValidSortDesc_ShouldNotThrowException() {
        assertDoesNotThrow(() -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, 0, 20, "title,desc"));
    }

    @Test
    void validateAdvancedSearchParameters_WithValidSortFieldOnly_ShouldNotThrowException() {
        assertDoesNotThrow(() -> validator.validateAdvancedSearchParameters(null, null, null, null, null, null, null, null, 0, 20, "title"));
    }
}