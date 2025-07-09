package com.movieapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieapi.entity.Movie;
import com.movieapi.exception.InvalidMovieDataException;
import com.movieapi.service.MovieService;
import com.movieapi.validation.MovieSearchValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for MovieController search endpoint.
 * Tests search functionality with various parameters and validation.
 */
@WebMvcTest(MovieController.class)
class MovieSearchControllerTest {

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
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Inception");
        testMovie.setDirector("Christopher Nolan");
        testMovie.setGenre("Sci-Fi");
        testMovie.setReleaseYear(2010);
        testMovie.setRating(new BigDecimal("8.8"));

        testMovie2 = new Movie();
        testMovie2.setId(2L);
        testMovie2.setTitle("Interstellar");
        testMovie2.setDirector("Christopher Nolan");
        testMovie2.setGenre("Sci-Fi");
        testMovie2.setReleaseYear(2014);
        testMovie2.setRating(new BigDecimal("8.6"));
    }

    // Search endpoint tests
    @Test
    void searchMovies_WithNoParameters_ShouldReturnAllMovies() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        when(movieService.searchMovies(null, null, null, null)).thenReturn(movies);

        // Act & Assert
        mockMvc.perform(get("/movies/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(movieService).searchMovies(null, null, null, null);
    }

    @Test
    void searchMovies_WithGenreParameter_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> sciFiMovies = Arrays.asList(testMovie, testMovie2);
        when(movieService.searchMovies("Sci-Fi", null, null, null)).thenReturn(sciFiMovies);

        // Act & Assert
        mockMvc.perform(get("/movies/search?genre=Sci-Fi"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].genre").value("Sci-Fi"))
                .andExpect(jsonPath("$[1].genre").value("Sci-Fi"));

        verify(movieService).searchMovies("Sci-Fi", null, null, null);
    }

    @Test
    void searchMovies_WithReleaseYearParameter_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> moviesFrom2010 = Arrays.asList(testMovie);
        when(movieService.searchMovies(null, 2010, null, null)).thenReturn(moviesFrom2010);

        // Act & Assert
        mockMvc.perform(get("/movies/search?releaseYear=2010"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].releaseYear").value(2010));

        verify(movieService).searchMovies(null, 2010, null, null);
    }

    @Test
    void searchMovies_WithMinRatingParameter_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> highRatedMovies = Arrays.asList(testMovie);
        when(movieService.searchMovies(null, null, new BigDecimal("8.8"), null)).thenReturn(highRatedMovies);

        // Act & Assert
        mockMvc.perform(get("/movies/search?minRating=8.8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rating").value(8.8));

        verify(movieService).searchMovies(null, null, new BigDecimal("8.8"), null);
    }

    @Test
    void searchMovies_WithDirectorParameter_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> nolanMovies = Arrays.asList(testMovie);
        when(movieService.searchMovies(null, null, null, "nolan")).thenReturn(nolanMovies);

        // Act & Assert
        mockMvc.perform(get("/movies/search?director=nolan"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].director").value("Christopher Nolan"));

        verify(movieService).searchMovies(null, null, null, "nolan");
    }

    @Test
    void searchMovies_WithMultipleParameters_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> filteredMovies = Arrays.asList(testMovie);
        when(movieService.searchMovies("Sci-Fi", 2010, new BigDecimal("8.0"), "christopher")).thenReturn(filteredMovies);

        // Act & Assert
        mockMvc.perform(get("/movies/search?genre=Sci-Fi&releaseYear=2010&minRating=8.0&director=christopher"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(movieService).searchMovies("Sci-Fi", 2010, new BigDecimal("8.0"), "christopher");
    }

    @Test
    void searchMovies_WithEmptyGenre_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("genre", "", "Genre cannot be empty if provided"))
                .when(movieSearchValidator).validateSearchParameters("", null, null, null);

        // Act & Assert
        mockMvc.perform(get("/movies/search?genre="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Genre cannot be empty")));

        verify(movieService, never()).searchMovies(any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithEmptyDirector_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("director", "", "Director cannot be empty if provided"))
                .when(movieSearchValidator).validateSearchParameters(null, null, null, "");

        // Act & Assert
        mockMvc.perform(get("/movies/search?director="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Director cannot be empty")));

        verify(movieService, never()).searchMovies(any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithInvalidReleaseYear_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("releaseYear", 1800, "Release year must be 1900 or later"))
                .when(movieSearchValidator).validateSearchParameters(null, 1800, null, null);

        // Act & Assert
        mockMvc.perform(get("/movies/search?releaseYear=1800"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Release year must be 1900 or later")));

        verify(movieService, never()).searchMovies(any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithFutureReleaseYear_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("releaseYear", 2050, 
                "Release year cannot be more than 5 years in the future (current year: " + Year.now().getValue() + ")"))
                .when(movieSearchValidator).validateSearchParameters(null, 2050, null, null);

        // Act & Assert
        mockMvc.perform(get("/movies/search?releaseYear=2050"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Release year cannot be more than 5 years in the future")));

        verify(movieService, never()).searchMovies(any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithNegativeMinRating_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("minRating", new BigDecimal("-1.0"), "Minimum rating cannot be negative"))
                .when(movieSearchValidator).validateSearchParameters(null, null, new BigDecimal("-1.0"), null);

        // Act & Assert
        mockMvc.perform(get("/movies/search?minRating=-1.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Minimum rating cannot be negative")));

        verify(movieService, never()).searchMovies(any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithExcessiveMinRating_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("minRating", new BigDecimal("15.0"), "Minimum rating cannot exceed 10.0"))
                .when(movieSearchValidator).validateSearchParameters(null, null, new BigDecimal("15.0"), null);

        // Act & Assert
        mockMvc.perform(get("/movies/search?minRating=15.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Minimum rating cannot exceed 10.0")));

        verify(movieService, never()).searchMovies(any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithInvalidParameters_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/movies/search?releaseYear=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"));

        verify(movieService, never()).searchMovies(any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithNoResults_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(movieService.searchMovies("NonExistentGenre", null, null, null)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/movies/search?genre=NonExistentGenre"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(movieService).searchMovies("NonExistentGenre", null, null, null);
    }
}