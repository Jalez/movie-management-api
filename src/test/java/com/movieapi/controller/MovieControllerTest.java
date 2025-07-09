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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.movieapi.testutil.MovieTestDataBuilder.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}
