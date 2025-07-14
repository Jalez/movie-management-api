package com.movieapi.controller;

import com.movieapi.exception.DuplicateMovieException;
import com.movieapi.exception.InvalidMovieDataException;
import com.movieapi.exception.MovieNotFoundException;
import com.movieapi.exception.ReviewNotFoundException;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Movie Management API.
 * Provides consistent error responses across all controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle Movie Not Found exceptions.
     */
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.MovieNotFoundError> handleMovieNotFoundException(
            MovieNotFoundException ex, WebRequest request) {
        
        logger.warn("Movie not found: {}", ex.getMessage());
        
        com.movieapi.dto.ErrorResponses.MovieNotFoundError errorResponse = new com.movieapi.dto.ErrorResponses.MovieNotFoundError();
        errorResponse.status = HttpStatus.NOT_FOUND.value();
        errorResponse.error = "Movie Not Found";
        errorResponse.message = ex.getMessage();
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle Review Not Found exceptions.
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.ReviewNotFoundError> handleReviewNotFoundException(
            ReviewNotFoundException ex, WebRequest request) {
        
        logger.warn("Review not found: {}", ex.getMessage());
        
        com.movieapi.dto.ErrorResponses.ReviewNotFoundError errorResponse = new com.movieapi.dto.ErrorResponses.ReviewNotFoundError();
        errorResponse.status = HttpStatus.NOT_FOUND.value();
        errorResponse.error = "Review Not Found";
        errorResponse.message = ex.getMessage();
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle Duplicate Movie exceptions.
     */
    @ExceptionHandler(DuplicateMovieException.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.ConflictError> handleDuplicateMovieException(
            DuplicateMovieException ex, WebRequest request) {
        
        logger.warn("Duplicate movie: {}", ex.getMessage());
        
        com.movieapi.dto.ErrorResponses.ConflictError errorResponse = new com.movieapi.dto.ErrorResponses.ConflictError();
        errorResponse.status = HttpStatus.CONFLICT.value();
        errorResponse.error = "Conflict";
        errorResponse.message = ex.getMessage();
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle Invalid Movie Data exceptions.
     */
    @ExceptionHandler(InvalidMovieDataException.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.BadRequestError> handleInvalidMovieDataException(
            InvalidMovieDataException ex, WebRequest request) {
        
        logger.warn("Invalid movie data: {}", ex.getMessage());
        
        com.movieapi.dto.ErrorResponses.BadRequestError errorResponse = new com.movieapi.dto.ErrorResponses.BadRequestError();
        errorResponse.status = HttpStatus.BAD_REQUEST.value();
        errorResponse.error = "Bad Request";
        errorResponse.message = ex.getMessage();
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle Bean Validation exceptions (from @Valid annotations).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.ValidationError> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Validation failed: {}", ex.getMessage());
        
        List<com.movieapi.dto.ErrorResponses.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new com.movieapi.dto.ErrorResponses.FieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());
        
        com.movieapi.dto.ErrorResponses.ValidationError errorResponse = new com.movieapi.dto.ErrorResponses.ValidationError();
        errorResponse.status = HttpStatus.BAD_REQUEST.value();
        errorResponse.error = "Validation Failed";
        errorResponse.message = "Request validation failed";
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        errorResponse.fieldErrors = fieldErrors.toArray(new com.movieapi.dto.ErrorResponses.FieldError[0]);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle method argument type mismatch (e.g., invalid path variables).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.BadRequestError> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        logger.warn("Method argument type mismatch: {}", ex.getMessage());
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        com.movieapi.dto.ErrorResponses.BadRequestError errorResponse = new com.movieapi.dto.ErrorResponses.BadRequestError();
        errorResponse.status = HttpStatus.BAD_REQUEST.value();
        errorResponse.error = "Bad Request";
        errorResponse.message = message;
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle JSON parsing errors (malformed JSON).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.BadRequestError> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        logger.warn("Malformed JSON request: {}", ex.getMessage());
        
        com.movieapi.dto.ErrorResponses.BadRequestError errorResponse = new com.movieapi.dto.ErrorResponses.BadRequestError();
        errorResponse.status = HttpStatus.BAD_REQUEST.value();
        errorResponse.error = "Bad Request";
        errorResponse.message = "Invalid JSON format in request body";
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle unsupported media type errors (e.g., missing Content-Type).
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.UnsupportedMediaTypeError> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, WebRequest request) {
        
        logger.warn("Unsupported media type: {}", ex.getMessage());
        
        String message = "Content type '" + ex.getContentType() + "' is not supported. Please use 'application/json'.";
        
        com.movieapi.dto.ErrorResponses.UnsupportedMediaTypeError errorResponse = new com.movieapi.dto.ErrorResponses.UnsupportedMediaTypeError();
        errorResponse.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value();
        errorResponse.error = "Unsupported Media Type";
        errorResponse.message = message;
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<com.movieapi.dto.ErrorResponses.InternalServerError> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        com.movieapi.dto.ErrorResponses.InternalServerError errorResponse = new com.movieapi.dto.ErrorResponses.InternalServerError();
        errorResponse.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        errorResponse.error = "Internal Server Error";
        errorResponse.message = "An unexpected error occurred. Please try again later.";
        errorResponse.path = request.getDescription(false).replace("uri=", "");
        errorResponse.timestamp = LocalDateTime.now().toString();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }


}
