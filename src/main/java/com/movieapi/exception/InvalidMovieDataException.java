package com.movieapi.exception;

/**
 * Exception thrown when movie data validation fails.
 * This is typically thrown when business rule validation fails beyond basic Bean Validation.
 */
public class InvalidMovieDataException extends RuntimeException {

    private final String field;
    private final Object value;

    public InvalidMovieDataException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }

    public InvalidMovieDataException(String field, Object value, String message) {
        super(String.format("Invalid value for field '%s': %s. %s", field, value, message));
        this.field = field;
        this.value = value;
    }

    public InvalidMovieDataException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
