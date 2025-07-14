package com.movieapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Specific error response classes for different error scenarios.
 * These provide better Swagger documentation with appropriate examples.
 */
public class ErrorResponses {

    @Schema(description = "Movie not found error response")
    public static class MovieNotFoundError {
        @Schema(description = "HTTP status code", example = "404")
        public int status = 404;
        
        @Schema(description = "Error category", example = "Movie Not Found")
        public String error = "Movie Not Found";
        
        @Schema(description = "Detailed error message", example = "Movie with ID 1 not found")
        public String message = "Movie with ID 1 not found";
        
        @Schema(description = "Request path that caused the error", example = "/movies/1")
        public String path = "/movies/1";
        
        @Schema(description = "Timestamp when the error occurred", example = "2025-07-14T11:01:27.436Z")
        public String timestamp = "2025-07-14T11:01:27.436Z";
    }

    @Schema(description = "Review not found error response")
    public static class ReviewNotFoundError {
        @Schema(description = "HTTP status code", example = "404")
        public int status = 404;
        
        @Schema(description = "Error category", example = "Review Not Found")
        public String error = "Review Not Found";
        
        @Schema(description = "Detailed error message", example = "Review with ID 5 not found for movie with ID 2")
        public String message = "Review with ID 5 not found for movie with ID 2";
        
        @Schema(description = "Request path that caused the error", example = "/movies/2/reviews/5")
        public String path = "/movies/2/reviews/5";
        
        @Schema(description = "Timestamp when the error occurred", example = "2025-07-14T11:01:27.436Z")
        public String timestamp = "2025-07-14T11:01:27.436Z";
    }

    @Schema(description = "Bad request error response")
    public static class BadRequestError {
        @Schema(description = "HTTP status code", example = "400")
        public int status = 400;
        
        @Schema(description = "Error category", example = "Bad Request")
        public String error = "Bad Request";
        
        @Schema(description = "Detailed error message", example = "Invalid value 'abc' for parameter 'movieId'. Expected type: Long")
        public String message = "Invalid value 'abc' for parameter 'movieId'. Expected type: Long";
        
        @Schema(description = "Request path that caused the error", example = "/movies/abc/reviews")
        public String path = "/movies/abc/reviews";
        
        @Schema(description = "Timestamp when the error occurred", example = "2025-07-14T11:01:27.436Z")
        public String timestamp = "2025-07-14T11:01:27.436Z";
    }

    @Schema(description = "Validation error response")
    public static class ValidationError {
        @Schema(description = "HTTP status code", example = "400")
        public int status = 400;
        
        @Schema(description = "Error category", example = "Validation Failed")
        public String error = "Validation Failed";
        
        @Schema(description = "Detailed error message", example = "Request validation failed")
        public String message = "Request validation failed";
        
        @Schema(description = "Request path that caused the error", example = "/movies/2/reviews")
        public String path = "/movies/2/reviews";
        
        @Schema(description = "Timestamp when the error occurred", example = "2025-07-14T11:01:27.436Z")
        public String timestamp = "2025-07-14T11:01:27.436Z";
        
        @Schema(description = "List of field validation errors")
        public FieldError[] fieldErrors = {
            new FieldError("rating", 15.0, "Rating cannot exceed 10.0"),
            new FieldError("userName", "", "User name cannot be blank")
        };
    }

    @Schema(description = "Individual field validation error")
    public static class FieldError {
        @Schema(description = "Name of the field that failed validation", example = "rating")
        public String field;
        
        @Schema(description = "The rejected value", example = "15.0")
        public Object rejectedValue;
        
        @Schema(description = "Validation error message", example = "Rating cannot exceed 10.0")
        public String message;

        public FieldError() {}

        public FieldError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
    }

    @Schema(description = "Conflict error response (e.g., duplicate movie)")
    public static class ConflictError {
        @Schema(description = "HTTP status code", example = "409")
        public int status = 409;
        
        @Schema(description = "Error category", example = "Conflict")
        public String error = "Conflict";
        
        @Schema(description = "Detailed error message", example = "Movie with title 'Inception' already exists")
        public String message = "Movie with title 'Inception' already exists";
        
        @Schema(description = "Request path that caused the error", example = "/movies")
        public String path = "/movies";
        
        @Schema(description = "Timestamp when the error occurred", example = "2025-07-14T11:01:27.436Z")
        public String timestamp = "2025-07-14T11:01:27.436Z";
    }

    @Schema(description = "Unsupported media type error response")
    public static class UnsupportedMediaTypeError {
        @Schema(description = "HTTP status code", example = "415")
        public int status = 415;
        
        @Schema(description = "Error category", example = "Unsupported Media Type")
        public String error = "Unsupported Media Type";
        
        @Schema(description = "Detailed error message", example = "Request content type 'application/xml' is not supported")
        public String message = "Request content type 'application/xml' is not supported";
        
        @Schema(description = "Request path that caused the error", example = "/movies")
        public String path = "/movies";
        
        @Schema(description = "Timestamp when the error occurred", example = "2025-07-14T11:01:27.436Z")
        public String timestamp = "2025-07-14T11:01:27.436Z";
    }

    @Schema(description = "Internal server error response")
    public static class InternalServerError {
        @Schema(description = "HTTP status code", example = "500")
        public int status = 500;
        
        @Schema(description = "Error category", example = "Internal Server Error")
        public String error = "Internal Server Error";
        
        @Schema(description = "Detailed error message", example = "An unexpected error occurred. Please try again later.")
        public String message = "An unexpected error occurred. Please try again later.";
        
        @Schema(description = "Request path that caused the error", example = "/movies/1/reviews")
        public String path = "/movies/1/reviews";
        
        @Schema(description = "Timestamp when the error occurred", example = "2025-07-14T11:01:27.436Z")
        public String timestamp = "2025-07-14T11:01:27.436Z";
    }
} 