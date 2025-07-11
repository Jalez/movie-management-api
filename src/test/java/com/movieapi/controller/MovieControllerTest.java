package com.movieapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieapi.entity.Movie;
import com.movieapi.exception.DuplicateMovieException;
import com.movieapi.exception.InvalidMovieDataException;
import com.movieapi.exception.MovieNotFoundException;
import com.movieapi.service.MovieService;
import com.movieapi.testutil.MovieTestDataBuilder;
import com.movieapi.validation.MovieSearchValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.movieapi.testutil.MovieTestDataBuilder.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for MovieController.
 * Tests all CRUD endpoints, HTTP status codes, and error handling.
 */
@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private MovieSearchValidator movieSearchValidator;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie testMovie;
    private Movie testMovie2;

    @BeforeEach
    void setUp() {
        testMovie = inception()
                .withId(1L)
                .build();

        testMovie2 = theMatrix()
                .withId(2L)
                .build();
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovies() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        when(movieService.getAllMovies()).thenReturn(movies);

        // Act & Assert
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[0].director").value("Christopher Nolan"))
                .andExpect(jsonPath("$[0].genre").value("Sci-Fi"))
                .andExpect(jsonPath("$[0].releaseYear").value(2010))
                .andExpect(jsonPath("$[0].rating").value(8.8))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("The Matrix"));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    void getAllMovies_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(movieService.getAllMovies()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    void getMovieById_WhenExists_ShouldReturnMovie() throws Exception {
        // Arrange
        when(movieService.getMovieById(1L)).thenReturn(Optional.of(testMovie));

        // Act & Assert
        mockMvc.perform(get("/movies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.director").value("Christopher Nolan"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.releaseYear").value(2010))
                .andExpect(jsonPath("$.rating").value(8.8));

        verify(movieService, times(1)).getMovieById(1L);
    }

    @Test
    void getMovieById_WhenNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(movieService.getMovieById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/movies/999"))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).getMovieById(999L);
    }

    @Test
    void getMovieById_WithInvalidId_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/movies/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"))
                .andExpect(jsonPath("$.message").value(containsString("Invalid value 'invalid' for parameter 'id'")));
    }

    @Test
    void createMovie_WithValidData_ShouldReturn201() throws Exception {
        // Arrange
        Movie newMovie = aMovie()
                .withTitle("Interstellar")
                .withDirector("Christopher Nolan")
                .withGenre("Sci-Fi")
                .withReleaseYear(2014)
                .withRating(8.6)
                .build();

        Movie savedMovie = aMovie()
                .withId(3L)
                .withTitle("Interstellar")
                .withDirector("Christopher Nolan")
                .withGenre("Sci-Fi")
                .withReleaseYear(2014)
                .withRating(8.6)
                .build();

        when(movieService.createMovie(any(Movie.class))).thenReturn(savedMovie);

        // Act & Assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("Interstellar"))
                .andExpect(jsonPath("$.director").value("Christopher Nolan"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.releaseYear").value(2014))
                .andExpect(jsonPath("$.rating").value(8.6));

        verify(movieService, times(1)).createMovie(any(Movie.class));
    }

    @Test
    void createMovie_WithDuplicateData_ShouldReturn409() throws Exception {
        // Arrange
        when(movieService.createMovie(any(Movie.class)))
                .thenThrow(new DuplicateMovieException("Inception", "Christopher Nolan"));

        // Act & Assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate Movie"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")));

        verify(movieService, times(1)).createMovie(any(Movie.class));
    }

    @Test
    void createMovie_WithBusinessRuleViolation_ShouldReturn400() throws Exception {
        // Arrange
        when(movieService.createMovie(any(Movie.class)))
                .thenThrow(new InvalidMovieDataException("rating", new BigDecimal("15.0"), "Rating cannot exceed 10.0"));

        // Act & Assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Rating cannot exceed 10.0")));

        verify(movieService, times(1)).createMovie(any(Movie.class));
    }

    @Test
    void updateMovie_WithValidData_ShouldReturn200() throws Exception {
        // Arrange
        Movie updatedMovie = inception()
                .withId(1L)
                .withTitle("Inception Updated")
                .withRating(9.0)
                .build();

        when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(updatedMovie);

        // Act & Assert
        mockMvc.perform(put("/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception Updated"))
                .andExpect(jsonPath("$.rating").value(9.0));

        verify(movieService, times(1)).updateMovie(eq(1L), any(Movie.class));
    }

    @Test
    void updateMovie_WhenNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(movieService.updateMovie(eq(999L), any(Movie.class)))
                .thenThrow(new MovieNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(put("/movies/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Movie Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("not found")));

        verify(movieService, times(1)).updateMovie(eq(999L), any(Movie.class));
    }

    @Test
    void deleteMovie_WhenExists_ShouldReturn204() throws Exception {
        // Arrange
        doNothing().when(movieService).deleteMovie(1L);

        // Act & Assert
        mockMvc.perform(delete("/movies/1"))
                .andExpect(status().isNoContent());

        verify(movieService, times(1)).deleteMovie(1L);
    }

    @Test
    void deleteMovie_WhenNotExists_ShouldReturn404() throws Exception {
        // Arrange
        doThrow(new MovieNotFoundException(999L)).when(movieService).deleteMovie(999L);

        // Act & Assert
        mockMvc.perform(delete("/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Movie Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("not found")));

        verify(movieService, times(1)).deleteMovie(999L);
    }

    @Test
    void deleteMovie_WithInvalidId_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/movies/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"));
    }

    // --- Additional tests for GlobalExceptionHandler inner classes coverage ---

    @Test
    void validationErrorResponse_DefaultConstructor_ShouldCreateEmptyResponse() {
        // When
        GlobalExceptionHandler.ValidationErrorResponse response = new GlobalExceptionHandler.ValidationErrorResponse();

        // Then
        assertThat(response.getFieldErrors()).isEmpty();
    }

    @Test
    void validationErrorResponse_ParameterizedConstructor_ShouldCreateResponseWithFieldErrors() {
        // Given
        List<GlobalExceptionHandler.FieldValidationError> fieldErrors = Arrays.asList(
            new GlobalExceptionHandler.FieldValidationError("title", "", "Title cannot be blank"),
            new GlobalExceptionHandler.FieldValidationError("rating", 15.0, "Rating cannot exceed 10.0")
        );

        // When
        GlobalExceptionHandler.ValidationErrorResponse response = new GlobalExceptionHandler.ValidationErrorResponse(
            400, "Validation Failed", "Request validation failed", "/movies", LocalDateTime.now(), fieldErrors
        );

        // Then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Validation Failed");
        assertThat(response.getMessage()).isEqualTo("Request validation failed");
        assertThat(response.getPath()).isEqualTo("/movies");
        assertThat(response.getFieldErrors()).hasSize(2);
        assertThat(response.getFieldErrors().get(0).getField()).isEqualTo("title");
        assertThat(response.getFieldErrors().get(1).getField()).isEqualTo("rating");
    }

    @Test
    void validationErrorResponse_SetFieldErrors_ShouldUpdateFieldErrors() {
        // Given
        GlobalExceptionHandler.ValidationErrorResponse response = new GlobalExceptionHandler.ValidationErrorResponse();
        List<GlobalExceptionHandler.FieldValidationError> fieldErrors = Arrays.asList(
            new GlobalExceptionHandler.FieldValidationError("title", "", "Title cannot be blank")
        );

        // When
        response.setFieldErrors(fieldErrors);

        // Then
        assertThat(response.getFieldErrors()).hasSize(1);
        assertThat(response.getFieldErrors().get(0).getField()).isEqualTo("title");
    }

    @Test
    void fieldValidationError_DefaultConstructor_ShouldCreateEmptyError() {
        // When
        GlobalExceptionHandler.FieldValidationError error = new GlobalExceptionHandler.FieldValidationError();

        // Then
        assertThat(error.getField()).isNull();
        assertThat(error.getRejectedValue()).isNull();
        assertThat(error.getMessage()).isNull();
    }

    @Test
    void fieldValidationError_ParameterizedConstructor_ShouldCreateErrorWithDetails() {
        // When
        GlobalExceptionHandler.FieldValidationError error = new GlobalExceptionHandler.FieldValidationError(
            "title", "", "Title cannot be blank"
        );

        // Then
        assertThat(error.getField()).isEqualTo("title");
        assertThat(error.getRejectedValue()).isEqualTo("");
        assertThat(error.getMessage()).isEqualTo("Title cannot be blank");
    }

    @Test
    void fieldValidationError_Setters_ShouldUpdateFields() {
        // Given
        GlobalExceptionHandler.FieldValidationError error = new GlobalExceptionHandler.FieldValidationError();

        // When
        error.setField("rating");
        error.setRejectedValue(15.0);
        error.setMessage("Rating cannot exceed 10.0");

        // Then
        assertThat(error.getField()).isEqualTo("rating");
        assertThat(error.getRejectedValue()).isEqualTo(15.0);
        assertThat(error.getMessage()).isEqualTo("Rating cannot exceed 10.0");
    }

    // --- Additional tests for exception classes coverage ---

    @Test
    void duplicateMovieException_WithMessageConstructor_ShouldCreateException() {
        // When
        DuplicateMovieException exception = new DuplicateMovieException("Custom error message");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Custom error message");
        assertThat(exception.getTitle()).isNull();
        assertThat(exception.getDirector()).isNull();
    }

    @Test
    void duplicateMovieException_WithMessageAndCauseConstructor_ShouldCreateException() {
        // Given
        Throwable cause = new RuntimeException("Root cause");

        // When
        DuplicateMovieException exception = new DuplicateMovieException("Custom error message", cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Custom error message");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getTitle()).isNull();
        assertThat(exception.getDirector()).isNull();
    }

    @Test
    void duplicateMovieException_WithTitleAndDirectorConstructor_ShouldCreateException() {
        // When
        DuplicateMovieException exception = new DuplicateMovieException("Inception", "Christopher Nolan");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Movie 'Inception' by director 'Christopher Nolan' already exists");
        assertThat(exception.getTitle()).isEqualTo("Inception");
        assertThat(exception.getDirector()).isEqualTo("Christopher Nolan");
    }

    @Test
    void duplicateMovieException_GetterMethods_ShouldReturnValues() {
        // Given
        DuplicateMovieException exception = new DuplicateMovieException("Inception", "Christopher Nolan");

        // When & Then
        assertThat(exception.getTitle()).isEqualTo("Inception");
        assertThat(exception.getDirector()).isEqualTo("Christopher Nolan");
    }

    @Test
    void invalidMovieDataException_WithMessageConstructor_ShouldCreateException() {
        // When
        InvalidMovieDataException exception = new InvalidMovieDataException("Custom error message");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Custom error message");
        assertThat(exception.getField()).isNull();
        assertThat(exception.getValue()).isNull();
    }

    @Test
    void invalidMovieDataException_WithMessageAndCauseConstructor_ShouldCreateException() {
        // Given
        Throwable cause = new RuntimeException("Root cause");

        // When
        InvalidMovieDataException exception = new InvalidMovieDataException("Custom error message", cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Custom error message");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getField()).isNull();
        assertThat(exception.getValue()).isNull();
    }

    @Test
    void invalidMovieDataException_WithFieldValueMessageConstructor_ShouldCreateException() {
        // When
        InvalidMovieDataException exception = new InvalidMovieDataException("rating", 15.0, "Rating cannot exceed 10.0");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Invalid value for field 'rating': 15.0. Rating cannot exceed 10.0");
        assertThat(exception.getField()).isEqualTo("rating");
        assertThat(exception.getValue()).isEqualTo(15.0);
    }

    @Test
    void invalidMovieDataException_GetterMethods_ShouldReturnValues() {
        // Given
        InvalidMovieDataException exception = new InvalidMovieDataException("rating", 15.0, "Rating cannot exceed 10.0");

        // When & Then
        assertThat(exception.getField()).isEqualTo("rating");
        assertThat(exception.getValue()).isEqualTo(15.0);
    }

    @Test
    void movieNotFoundException_WithMessageConstructor_ShouldCreateException() {
        // When
        MovieNotFoundException exception = new MovieNotFoundException("Custom error message");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Custom error message");
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void movieNotFoundException_WithMessageAndCauseConstructor_ShouldCreateException() {
        // Given
        Throwable cause = new RuntimeException("Root cause");

        // When
        MovieNotFoundException exception = new MovieNotFoundException("Custom error message", cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Custom error message");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getMovieId()).isNull();
    }

    @Test
    void movieNotFoundException_WithMovieIdConstructor_ShouldCreateException() {
        // When
        MovieNotFoundException exception = new MovieNotFoundException(123L);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Movie with ID 123 not found");
        assertThat(exception.getMovieId()).isEqualTo(123L);
    }

    @Test
    void movieNotFoundException_GetterMethod_ShouldReturnValue() {
        // Given
        MovieNotFoundException exception = new MovieNotFoundException(456L);

        // When & Then
        assertThat(exception.getMovieId()).isEqualTo(456L);
    }

    // --- Additional tests for GlobalExceptionHandler coverage ---

    @Test
    void errorResponse_Setters_ShouldUpdateFields() {
        // Given
        GlobalExceptionHandler.ErrorResponse response = new GlobalExceptionHandler.ErrorResponse();

        // When
        response.setStatus(500);
        response.setError("Internal Server Error");
        response.setMessage("An unexpected error occurred");
        response.setPath("/test/path");
        LocalDateTime timestamp = LocalDateTime.now();
        response.setTimestamp(timestamp);

        // Then
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getError()).isEqualTo("Internal Server Error");
        assertThat(response.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getPath()).isEqualTo("/test/path");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void handleGenericException_ShouldReturn500Response() throws Exception {
        // This test verifies that the generic exception handler is properly configured
        // The actual generic exception handling is tested in integration tests
        // where real exceptions can be thrown and caught by the handler
        
        // Given - we'll test the handler directly by creating an exception scenario
        when(movieService.getMovieById(any())).thenThrow(new RuntimeException("Unexpected error"));

        // When & Assert
        mockMvc.perform(get("/movies/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."));
    }

    @Test
    void errorResponse_DefaultConstructor_ShouldCreateEmptyResponse() {
        // When
        GlobalExceptionHandler.ErrorResponse response = new GlobalExceptionHandler.ErrorResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(0);
        assertThat(response.getError()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getPath()).isNull();
        assertThat(response.getTimestamp()).isNull();
    }

    @Test
    void errorResponse_ParameterizedConstructor_ShouldCreateResponseWithValues() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        GlobalExceptionHandler.ErrorResponse response = new GlobalExceptionHandler.ErrorResponse(
                404, "Not Found", "Resource not found", "/test/path", timestamp);

        // Then
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getError()).isEqualTo("Not Found");
        assertThat(response.getMessage()).isEqualTo("Resource not found");
        assertThat(response.getPath()).isEqualTo("/test/path");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
    }
}
