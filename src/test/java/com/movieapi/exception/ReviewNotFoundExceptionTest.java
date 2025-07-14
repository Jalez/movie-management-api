package com.movieapi.exception;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ReviewNotFoundExceptionTest {

    @Test
    void constructor_WithReviewIdAndMovieId_ShouldCreateExceptionWithFormattedMessage() {
        Long reviewId = 123L;
        Long movieId = 456L;
        
        ReviewNotFoundException exception = new ReviewNotFoundException(reviewId, movieId);
        
        assertThat(exception.getMessage()).isEqualTo("Review with ID 123 not found for movie with ID 456");
        assertThat(exception.getReviewId()).isEqualTo(reviewId);
        assertThat(exception.getMovieId()).isEqualTo(movieId);
    }

    @Test
    void constructor_WithNullReviewIdAndMovieId_ShouldCreateExceptionWithFormattedMessage() {
        ReviewNotFoundException exception = new ReviewNotFoundException((Long) null, (Long) null);
        
        assertThat(exception.getMessage()).isEqualTo("Review with ID null not found for movie with ID null");
        assertThat(exception.getReviewId()).isNull();
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void constructor_WithMessage_ShouldCreateExceptionWithMessage() {
        String message = "Custom error message";
        
        ReviewNotFoundException exception = new ReviewNotFoundException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getReviewId()).isNull();
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void constructor_WithNullMessage_ShouldCreateExceptionWithNullMessage() {
        ReviewNotFoundException exception = new ReviewNotFoundException((String) null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getReviewId()).isNull();
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void constructor_WithMessageAndCause_ShouldCreateExceptionWithMessageAndCause() {
        String message = "Custom error message";
        Throwable cause = new RuntimeException("Root cause");
        
        ReviewNotFoundException exception = new ReviewNotFoundException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getReviewId()).isNull();
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void constructor_WithNullMessageAndCause_ShouldCreateExceptionWithNullMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        
        ReviewNotFoundException exception = new ReviewNotFoundException((String) null, cause);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getReviewId()).isNull();
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void constructor_WithMessageAndNullCause_ShouldCreateExceptionWithMessageAndNullCause() {
        String message = "Custom error message";
        
        ReviewNotFoundException exception = new ReviewNotFoundException(message, null);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getReviewId()).isNull();
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void getReviewId_ShouldReturnReviewId() {
        Long reviewId = 123L;
        ReviewNotFoundException exception = new ReviewNotFoundException(reviewId, 456L);
        
        assertThat(exception.getReviewId()).isEqualTo(reviewId);
    }

    @Test
    void getMovieId_ShouldReturnMovieId() {
        Long movieId = 456L;
        ReviewNotFoundException exception = new ReviewNotFoundException(123L, movieId);
        
        assertThat(exception.getMovieId()).isEqualTo(movieId);
    }

    @Test
    void getReviewId_WhenCreatedWithMessageConstructor_ShouldReturnNull() {
        ReviewNotFoundException exception = new ReviewNotFoundException("Test message");
        
        assertThat(exception.getReviewId()).isNull();
    }

    @Test
    void getMovieId_WhenCreatedWithMessageConstructor_ShouldReturnNull() {
        ReviewNotFoundException exception = new ReviewNotFoundException("Test message");
        
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void getReviewId_WhenCreatedWithMessageAndCauseConstructor_ShouldReturnNull() {
        ReviewNotFoundException exception = new ReviewNotFoundException("Test message", new RuntimeException());
        
        assertThat(exception.getReviewId()).isNull();
    }

    @Test
    void getMovieId_WhenCreatedWithMessageAndCauseConstructor_ShouldReturnNull() {
        ReviewNotFoundException exception = new ReviewNotFoundException("Test message", new RuntimeException());
        
        assertThat(exception.getMovieId()).isNull();
    }
} 