package com.movieapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieapi.entity.Movie;
import com.movieapi.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MovieController search endpoint with real database.
 * Tests the complete flow from controller through service to repository for search functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class MovieSearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    @Test
    void searchMovies_IntegrationTest_ShouldReturnFilteredResults() throws Exception {
        // Create test movies
        Movie movie1 = createTestMovie("Inception", "Christopher Nolan", "Sci-Fi", 2010, "8.8");
        Movie movie2 = createTestMovie("The Matrix", "The Wachowskis", "Sci-Fi", 1999, "8.7");
        Movie movie3 = createTestMovie("Pulp Fiction", "Quentin Tarantino", "Crime", 1994, "8.9");
        
        Long movie1Id = createMovieAndGetId(movie1);
        Long movie2Id = createMovieAndGetId(movie2);
        Long movie3Id = createMovieAndGetId(movie3);

        // Test search by genre
        mockMvc.perform(get("/movies/search?genre=Sci-Fi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].genre").value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.is("Sci-Fi"))));

        // Test search by release year
        mockMvc.perform(get("/movies/search?releaseYear=2010"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Inception"));

        // Test search by minimum rating
        mockMvc.perform(get("/movies/search?minRating=8.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].rating").value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.greaterThanOrEqualTo(8.8))));

        // Test search by director (partial match)
        mockMvc.perform(get("/movies/search?director=nolan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].director").value("Christopher Nolan"));

        // Test multiple criteria
        mockMvc.perform(get("/movies/search?genre=Sci-Fi&minRating=8.7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Test no results
        mockMvc.perform(get("/movies/search?genre=NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchMovies_WithInvalidParameterTypes_ShouldReturn400() throws Exception {
        // Test invalid release year type
        mockMvc.perform(get("/movies/search?releaseYear=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"));

        // Test invalid minimum rating type
        mockMvc.perform(get("/movies/search?minRating=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"));
    }

    @Test
    void searchMovies_WithValidationErrors_ShouldReturn400() throws Exception {
        // Test empty genre parameter
        mockMvc.perform(get("/movies/search?genre="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"));

        // Test empty director parameter
        mockMvc.perform(get("/movies/search?director="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"));

        // Test invalid release year (too early)
        mockMvc.perform(get("/movies/search?releaseYear=1800"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"));

        // Test invalid release year (too far in future)
        mockMvc.perform(get("/movies/search?releaseYear=2050"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"));

        // Test negative minimum rating
        mockMvc.perform(get("/movies/search?minRating=-1.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"));

        // Test excessive minimum rating
        mockMvc.perform(get("/movies/search?minRating=15.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Movie Data"));
    }

    @Test
    void searchMovies_WithNoParameters_ShouldReturnAllMovies() throws Exception {
        // Create test movies
        Movie movie1 = createTestMovie("Movie 1", "Director 1", "Action", 2020, "7.5");
        Movie movie2 = createTestMovie("Movie 2", "Director 2", "Drama", 2021, "8.0");
        
        createMovieAndGetId(movie1);
        createMovieAndGetId(movie2);

        // Test search with no parameters should return all movies
        mockMvc.perform(get("/movies/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private Movie createTestMovie(String title, String director, String genre, Integer year, String rating) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDirector(director);
        movie.setGenre(genre);
        movie.setReleaseYear(year);
        movie.setRating(new BigDecimal(rating));
        return movie;
    }

    private Long createMovieAndGetId(Movie movie) throws Exception {
        String response = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Movie createdMovie = objectMapper.readValue(response, Movie.class);
        return createdMovie.getId();
    }
}