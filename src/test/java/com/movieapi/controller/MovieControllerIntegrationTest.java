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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MovieController with real database.
 * Tests the complete flow from controller through service to repository.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie testMovie1;
    private Movie testMovie2;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        movieRepository.deleteAll();

        testMovie1 = new Movie();
        testMovie1.setTitle("Inception");
        testMovie1.setDirector("Christopher Nolan");
        testMovie1.setGenre("Sci-Fi");
        testMovie1.setReleaseYear(2010);
        testMovie1.setRating(new BigDecimal("8.8"));

        testMovie2 = new Movie();
        testMovie2.setTitle("The Matrix");
        testMovie2.setDirector("The Wachowskis");
        testMovie2.setGenre("Sci-Fi");
        testMovie2.setReleaseYear(1999);
        testMovie2.setRating(new BigDecimal("8.7"));
    }

    @Test
    void fullCrudWorkflow_ShouldWorkEndToEnd() throws Exception {
        // Verify database is empty
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Create first movie
        String response1 = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.director").value("Christopher Nolan"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.releaseYear").value(2010))
                .andExpect(jsonPath("$.rating").value(8.8))
                .andReturn().getResponse().getContentAsString();

        Movie createdMovie1 = objectMapper.readValue(response1, Movie.class);
        Long movie1Id = createdMovie1.getId();

        // Create second movie
        String response2 = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("The Matrix"))
                .andReturn().getResponse().getContentAsString();

        Movie createdMovie2 = objectMapper.readValue(response2, Movie.class);
        Long movie2Id = createdMovie2.getId();

        // Verify database contains both movies
        assertEquals(2, movieRepository.count());

        // Get all movies
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Get first movie by ID
        mockMvc.perform(get("/movies/" + movie1Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.director").value("Christopher Nolan"))
                .andExpect(jsonPath("$.id").value(movie1Id));

        // Update the first movie
        Movie updateMovie = new Movie();
        updateMovie.setTitle("Inception - Director's Cut");
        updateMovie.setDirector("Christopher Nolan");
        updateMovie.setGenre("Sci-Fi");
        updateMovie.setReleaseYear(2010);
        updateMovie.setRating(new BigDecimal("9.0"));

        mockMvc.perform(put("/movies/" + movie1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception - Director's Cut"))
                .andExpect(jsonPath("$.rating").value(9.0))
                .andExpect(jsonPath("$.id").value(movie1Id));

        // Verify update persisted in database
        Optional<Movie> updatedInDb = movieRepository.findById(movie1Id);
        assertTrue(updatedInDb.isPresent());
        assertEquals("Inception - Director's Cut", updatedInDb.get().getTitle());
        assertEquals(new BigDecimal("9.0"), updatedInDb.get().getRating());

        // Delete the first movie
        mockMvc.perform(delete("/movies/" + movie1Id))
                .andExpect(status().isNoContent());

        // Verify movie is deleted from database
        assertFalse(movieRepository.existsById(movie1Id));
        assertEquals(1, movieRepository.count());

        // Verify deleted movie returns 404
        mockMvc.perform(get("/movies/" + movie1Id))
                .andExpect(status().isNotFound());

        // Verify second movie still exists
        mockMvc.perform(get("/movies/" + movie2Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Matrix"));

        // Verify only one movie remains in list
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("The Matrix"));
    }

    @Test
    void createMovie_WithDuplicateData_ShouldReturn409() throws Exception {
        // Create first movie
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isCreated());

        // Verify movie exists in database
        assertTrue(movieRepository.existsByTitleAndDirector("Inception", "Christopher Nolan"));

        // Try to create duplicate movie
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate Movie"))
                .andExpect(jsonPath("$.message").value("Movie 'Inception' by director 'Christopher Nolan' already exists"));

        // Verify only one movie exists in database
        assertEquals(1, movieRepository.count());
    }

    @Test
    void updateMovie_ToExistingTitleAndDirector_ShouldReturn409() throws Exception {
        // Create both movies
        String response1 = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String response2 = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Movie movie1 = objectMapper.readValue(response1, Movie.class);
        Movie movie2 = objectMapper.readValue(response2, Movie.class);

        // Verify both movies exist
        assertEquals(2, movieRepository.count());

        // Try to update second movie to have same title/director as first
        Movie conflictingUpdate = new Movie();
        conflictingUpdate.setTitle("Inception");
        conflictingUpdate.setDirector("Christopher Nolan");
        conflictingUpdate.setGenre("Action");
        conflictingUpdate.setReleaseYear(2020);
        conflictingUpdate.setRating(new BigDecimal("8.0"));

        mockMvc.perform(put("/movies/" + movie2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictingUpdate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate Movie"));

        // Verify original movie data is unchanged
        Optional<Movie> unchanged = movieRepository.findById(movie2.getId());
        assertTrue(unchanged.isPresent());
        assertEquals("The Matrix", unchanged.get().getTitle());
        assertEquals("The Wachowskis", unchanged.get().getDirector());
    }

    @Test
    void createMovie_WithInvalidData_ShouldReturn400AndNotPersist() throws Exception {
        // Test various validation scenarios

        // Empty title
        Movie invalidMovie1 = new Movie();
        invalidMovie1.setTitle("");
        invalidMovie1.setDirector("Test Director");
        invalidMovie1.setGenre("Action");
        invalidMovie1.setReleaseYear(2020);
        invalidMovie1.setRating(new BigDecimal("8.0"));

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        // Invalid rating > 10.0
        Movie invalidMovie2 = new Movie();
        invalidMovie2.setTitle("Test Movie");
        invalidMovie2.setDirector("Test Director");
        invalidMovie2.setGenre("Action");
        invalidMovie2.setReleaseYear(2020);
        invalidMovie2.setRating(new BigDecimal("15.0"));

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // Invalid year < 1888
        Movie invalidMovie3 = new Movie();
        invalidMovie3.setTitle("Test Movie");
        invalidMovie3.setDirector("Test Director");
        invalidMovie3.setGenre("Action");
        invalidMovie3.setReleaseYear(1800);
        invalidMovie3.setRating(new BigDecimal("8.0"));

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // Verify no movies were persisted due to validation failures
        assertEquals(0, movieRepository.count());
    }

    @Test
    void getMovie_WithNonExistentId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/movies/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie_WithNonExistentId_ShouldReturn404() throws Exception {
        mockMvc.perform(put("/movies/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Movie Not Found"));
    }

    @Test
    void deleteMovie_WithNonExistentId_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/movies/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Movie Not Found"));
    }

    @Test
    void createMovie_WithMalformedJson_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        assertEquals(0, movieRepository.count());
    }

    @Test
    void createMovie_WithMissingContentType_ShouldReturn415() throws Exception {
        mockMvc.perform(post("/movies")
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isUnsupportedMediaType());

        assertEquals(0, movieRepository.count());
    }

    @Test
    void parameterValidation_WithInvalidPathVariables_ShouldReturn400() throws Exception {
        // Invalid ID format
        mockMvc.perform(get("/movies/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"));

        mockMvc.perform(put("/movies/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"));

        mockMvc.perform(delete("/movies/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"));
    }

    @Test
    void transactionRollback_OnServiceException_ShouldNotPersistData() throws Exception {
        // Create a movie first
        String response = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Movie createdMovie = objectMapper.readValue(response, Movie.class);
        assertEquals(1, movieRepository.count());

        // Try to update with invalid business rule (should trigger service exception)
        Movie invalidUpdate = new Movie();
        invalidUpdate.setTitle("Test");
        invalidUpdate.setDirector("Test");
        invalidUpdate.setGenre("Test");
        invalidUpdate.setReleaseYear(2050); // Future year that should be rejected
        invalidUpdate.setRating(new BigDecimal("8.0"));

        mockMvc.perform(put("/movies/" + createdMovie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());

        // Verify original movie data is unchanged
        Optional<Movie> unchanged = movieRepository.findById(createdMovie.getId());
        assertTrue(unchanged.isPresent());
        assertEquals("Inception", unchanged.get().getTitle());
        assertEquals("Christopher Nolan", unchanged.get().getDirector());
        assertEquals(Integer.valueOf(2010), unchanged.get().getReleaseYear());
    }

    @Test
    void concurrentOperations_ShouldMaintainDataConsistency() throws Exception {
        // Create initial movie
        String response = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Movie movie = objectMapper.readValue(response, Movie.class);
        Long movieId = movie.getId();

        // Simulate concurrent read operations
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/movies/" + movieId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Inception"));
        }

        // Perform update
        Movie updateMovie = new Movie();
        updateMovie.setTitle("Inception Updated");
        updateMovie.setDirector("Christopher Nolan");
        updateMovie.setGenre("Sci-Fi");
        updateMovie.setReleaseYear(2010);
        updateMovie.setRating(new BigDecimal("9.0"));

        mockMvc.perform(put("/movies/" + movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception Updated"));

        // Verify all subsequent reads return updated data
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/movies/" + movieId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Inception Updated"))
                    .andExpect(jsonPath("$.rating").value(9.0));
        }

        // Verify database consistency
        Optional<Movie> finalMovie = movieRepository.findById(movieId);
        assertTrue(finalMovie.isPresent());
        assertEquals("Inception Updated", finalMovie.get().getTitle());
        assertEquals(new BigDecimal("9.0"), finalMovie.get().getRating());
    }

    @Test
    void errorResponse_ShouldHaveConsistentFormat() throws Exception {
        // Test various error scenarios to ensure consistent error response format

        // 404 Not Found
        mockMvc.perform(get("/movies/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // 400 Bad Request (validation)
        Movie invalidMovie = new Movie();
        invalidMovie.setTitle("");
        
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.fieldErrors").exists());

        // 400 Bad Request (parameter type mismatch)
        mockMvc.perform(get("/movies/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Parameter"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.timestamp").exists());
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
