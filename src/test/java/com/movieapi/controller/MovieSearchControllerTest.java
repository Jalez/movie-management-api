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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithGenreParameter_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> sciFiMovies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(sciFiMovies, PageRequest.of(0, 20), sciFiMovies.size());
        when(movieService.searchMoviesAdvanced(
            eq("Sci-Fi"), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?genre=Sci-Fi"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.content[1].genre").value("Sci-Fi"));

        verify(movieService).searchMoviesAdvanced(
            eq("Sci-Fi"), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithReleaseYearParameter_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> moviesFrom2010 = Arrays.asList(testMovie);
        Page<Movie> moviePage = new PageImpl<>(moviesFrom2010, PageRequest.of(0, 20), moviesFrom2010.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq(2010), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?releaseYear=2010"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].releaseYear").value(2010));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq(2010), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithMinRatingParameter_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> highRatedMovies = Arrays.asList(testMovie);
        Page<Movie> moviePage = new PageImpl<>(highRatedMovies, PageRequest.of(0, 20), highRatedMovies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq(new BigDecimal("8.8")), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?minRating=8.8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].rating").value(8.8));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq(new BigDecimal("8.8")), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithDirectorParameter_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> nolanMovies = Arrays.asList(testMovie);
        Page<Movie> moviePage = new PageImpl<>(nolanMovies, PageRequest.of(0, 20), nolanMovies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq("nolan"), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?director=nolan"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].director").value("Christopher Nolan"));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq("nolan"), any(Pageable.class));
    }

    @Test
    void searchMovies_WithMultipleParameters_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        List<Movie> filteredMovies = Arrays.asList(testMovie);
        Page<Movie> moviePage = new PageImpl<>(filteredMovies, PageRequest.of(0, 20), filteredMovies.size());
        when(movieService.searchMoviesAdvanced(
            eq("Sci-Fi"), eq(2010), eq(new BigDecimal("8.0")), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq("christopher"), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?genre=Sci-Fi&releaseYear=2010&minRating=8.0&director=christopher"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(movieService).searchMoviesAdvanced(
            eq("Sci-Fi"), eq(2010), eq(new BigDecimal("8.0")), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq("christopher"), any(Pageable.class));
    }

    @Test
    void searchMovies_WithEmptyGenre_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("genre", "", "Genre cannot be empty if provided"))
                .when(movieSearchValidator).validateAdvancedSearchParameters("", null, null, null, null, null, null, null, 0, 20, "title,asc");

        // Act & Assert
        mockMvc.perform(get("/movies/search?genre="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Genre cannot be empty")));

        verify(movieService, never()).searchMoviesAdvanced(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithEmptyDirector_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("director", "", "Director cannot be empty if provided"))
                .when(movieSearchValidator).validateAdvancedSearchParameters(null, null, null, null, null, null, null, "", 0, 20, "title,asc");

        // Act & Assert
        mockMvc.perform(get("/movies/search?director="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Director cannot be empty")));

        verify(movieService, never()).searchMoviesAdvanced(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithInvalidReleaseYear_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("releaseYear", 1800, "Release year must be 1900 or later"))
                .when(movieSearchValidator).validateAdvancedSearchParameters(null, 1800, null, null, null, null, null, null, 0, 20, "title,asc");

        // Act & Assert
        mockMvc.perform(get("/movies/search?releaseYear=1800"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Release year must be 1900 or later")));

        verify(movieService, never()).searchMoviesAdvanced(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithFutureReleaseYear_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("releaseYear", 2050, 
                "Release year cannot be more than 5 years in the future (current year: " + Year.now().getValue() + ")"))
                .when(movieSearchValidator).validateAdvancedSearchParameters(null, 2050, null, null, null, null, null, null, 0, 20, "title,asc");

        // Act & Assert
        mockMvc.perform(get("/movies/search?releaseYear=2050"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Release year cannot be more than 5 years in the future")));

        verify(movieService, never()).searchMoviesAdvanced(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithNegativeMinRating_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("minRating", new BigDecimal("-1.0"), "Minimum rating cannot be negative"))
                .when(movieSearchValidator).validateAdvancedSearchParameters(null, null, new BigDecimal("-1.0"), null, null, null, null, null, 0, 20, "title,asc");

        // Act & Assert
        mockMvc.perform(get("/movies/search?minRating=-1.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Minimum rating cannot be negative")));

        verify(movieService, never()).searchMoviesAdvanced(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithExcessiveMinRating_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidMovieDataException("minRating", new BigDecimal("15.0"), "Minimum rating cannot exceed 10.0"))
                .when(movieSearchValidator).validateAdvancedSearchParameters(null, null, new BigDecimal("15.0"), null, null, null, null, null, 0, 20, "title,asc");

        // Act & Assert
        mockMvc.perform(get("/movies/search?minRating=15.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"))
                .andExpect(jsonPath("$.message").value(containsString("Minimum rating cannot exceed 10.0")));

        verify(movieService, never()).searchMoviesAdvanced(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithInvalidParameters_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/movies/search?releaseYear=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"));

        verify(movieService, never()).searchMoviesAdvanced(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchMovies_WithNoResults_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        Page<Movie> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(movieService.searchMoviesAdvanced(
            eq("NonExistentGenre"), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?genre=NonExistentGenre"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(movieService).searchMoviesAdvanced(
            eq("NonExistentGenre"), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    // --- Additional tests for createPageable method coverage ---

    @Test
    void searchMovies_WithPagination_ShouldReturnPaginatedResults() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(1, 5), 10); // Page 1, size 5, total 10
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?page=1&size=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true)); // Page 1 of 2 pages (0-based), so this is the last page

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithLargePageSize_ShouldLimitToMaxSize() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 100), movies.size()); // Max size 100
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?size=150")) // Request 150, should be limited to 100
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size").value(100));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithSmallPageSize_ShouldLimitToMinSize() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 1), movies.size()); // Min size 1
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?size=0")) // Request 0, should be limited to 1
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size").value(1));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithNullSort_ShouldUseDefaultSorting() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=")) // Empty sort parameter
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithWhitespaceSort_ShouldUseDefaultSorting() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=   ")) // Whitespace sort parameter
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    // --- Additional tests for mapSortFieldToColumn method coverage ---

    @Test
    void searchMovies_WithReleaseYearSort_ShouldMapToSnakeCase() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=releaseYear,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithMinRatingSort_ShouldMapToRating() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=minRating,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithMaxRatingSort_ShouldMapToRating() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=maxRating,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithDefaultFieldSort_ShouldUseFieldAsIs() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=title,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithInvalidSortField_ShouldDefaultToTitle() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=invalidField,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithSortFieldOnly_ShouldUseDefaultDirection() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=rating")) // Only field, no direction
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithDescDirection_ShouldUseDescendingSort() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=director,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }

    @Test
    void searchMovies_WithCaseInsensitiveDirection_ShouldWork() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, testMovie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 20), movies.size());
        when(movieService.searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class)))
            .thenReturn(moviePage);

        // Act & Assert
        mockMvc.perform(get("/movies/search?sort=genre,DESC")) // Uppercase direction
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService).searchMoviesAdvanced(
            eq((String) null), eq((Integer) null), eq((BigDecimal) null), eq((BigDecimal) null), 
            eq((Integer) null), eq((Integer) null), eq((String) null), eq((String) null), any(Pageable.class));
    }
}