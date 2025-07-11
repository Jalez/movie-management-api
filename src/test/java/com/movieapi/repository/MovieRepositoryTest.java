package com.movieapi.repository;

import com.movieapi.entity.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for MovieRepository.
 * Uses @DataJpaTest for focused testing of repository layer.
 */
@DataJpaTest
@ActiveProfiles("test")
class MovieRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    private Movie movie1;
    private Movie movie2;
    private Movie movie3;

    @BeforeEach
    void setUp() {
        // Create test movies
        movie1 = new Movie("Inception", "Christopher Nolan", "Sci-Fi", 2010, new BigDecimal("8.8"));
        movie2 = new Movie("The Dark Knight", "Christopher Nolan", "Action", 2008, new BigDecimal("9.0"));
        movie3 = new Movie("Pulp Fiction", "Quentin Tarantino", "Crime", 1994, new BigDecimal("8.9"));

        // Persist test data
        entityManager.persistAndFlush(movie1);
        entityManager.persistAndFlush(movie2);
        entityManager.persistAndFlush(movie3);
    }

    @Test
    void testFindAll() {
        // When
        List<Movie> movies = movieRepository.findAll();

        // Then
        assertThat(movies).hasSize(3);
        assertThat(movies).extracting("title")
                .containsExactlyInAnyOrder("Inception", "The Dark Knight", "Pulp Fiction");
    }

    @Test
    void testFindById() {
        // When
        Optional<Movie> found = movieRepository.findById(movie1.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Inception");
        assertThat(found.get().getDirector()).isEqualTo("Christopher Nolan");
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        // When
        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCase("inception");

        // Then
        assertThat(movies).hasSize(1);
        assertThat(movies.get(0).getTitle()).isEqualTo("Inception");
    }

    @Test
    void testFindByTitleContainingIgnoreCase_PartialMatch() {
        // When
        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCase("dark");

        // Then
        assertThat(movies).hasSize(1);
        assertThat(movies.get(0).getTitle()).isEqualTo("The Dark Knight");
    }

    @Test
    void testFindByDirectorContainingIgnoreCase() {
        // When
        List<Movie> movies = movieRepository.findByDirectorContainingIgnoreCase("christopher nolan");

        // Then
        assertThat(movies).hasSize(2);
        assertThat(movies).extracting("title")
                .containsExactlyInAnyOrder("Inception", "The Dark Knight");
    }

    @Test
    void testFindByGenreIgnoreCase() {
        // When
        List<Movie> movies = movieRepository.findByGenreIgnoreCase("ACTION");

        // Then
        assertThat(movies).hasSize(1);
        assertThat(movies.get(0).getTitle()).isEqualTo("The Dark Knight");
    }

    @Test
    void testFindByReleaseYear() {
        // When
        List<Movie> movies = movieRepository.findByReleaseYear(2010);

        // Then
        assertThat(movies).hasSize(1);
        assertThat(movies.get(0).getTitle()).isEqualTo("Inception");
    }

    @Test
    void testFindByRatingGreaterThanEqual() {
        // When
        List<Movie> movies = movieRepository.findByRatingGreaterThanEqual(new BigDecimal("8.9"));

        // Then
        assertThat(movies).hasSize(2);
        assertThat(movies).extracting("title")
                .containsExactlyInAnyOrder("The Dark Knight", "Pulp Fiction");
    }

    @Test
    void testFindByReleaseYearBetween() {
        // When
        List<Movie> movies = movieRepository.findByReleaseYearBetween(2000, 2010);

        // Then
        assertThat(movies).hasSize(2);
        assertThat(movies).extracting("title")
                .containsExactlyInAnyOrder("Inception", "The Dark Knight");
    }

    @Test
    void testFindByRatingBetween() {
        // When
        List<Movie> movies = movieRepository.findByRatingBetween(
                new BigDecimal("8.8"), new BigDecimal("9.0"));

        // Then
        assertThat(movies).hasSize(3);
        assertThat(movies).extracting("title")
                .containsExactlyInAnyOrder("Inception", "The Dark Knight", "Pulp Fiction");
    }

    @Test
    void testExistsByTitleAndDirector() {
        // When & Then
        assertThat(movieRepository.existsByTitleAndDirector("Inception", "Christopher Nolan"))
                .isTrue();
        assertThat(movieRepository.existsByTitleAndDirector("Inception", "Quentin Tarantino"))
                .isFalse();
        assertThat(movieRepository.existsByTitleAndDirector("Nonexistent Movie", "Christopher Nolan"))
                .isFalse();
    }

    @Test
    void testFindTopRatedMovies() {
        // When
        Pageable pageable = PageRequest.of(0, 2);
        List<Movie> topMovies = movieRepository.findTopRatedMovies(pageable);

        // Then
        assertThat(topMovies).hasSize(2);
        assertThat(topMovies.get(0).getTitle()).isEqualTo("The Dark Knight"); // 9.0
        assertThat(topMovies.get(1).getTitle()).isEqualTo("Pulp Fiction"); // 8.9
    }

    @Test
    void testFindMoviesByCriteria_AllCriteria() {
        // When
        List<Movie> movies = movieRepository.findMoviesByCriteria(
                "Action", new BigDecimal("8.5"), 2008);

        // Then
        assertThat(movies).hasSize(1);
        assertThat(movies.get(0).getTitle()).isEqualTo("The Dark Knight");
    }

    @Test
    void testFindMoviesByCriteria_PartialCriteria() {
        // When
        List<Movie> movies = movieRepository.findMoviesByCriteria(
                null, new BigDecimal("8.9"), null);

        // Then
        assertThat(movies).hasSize(2);
        assertThat(movies).extracting("title")
                .containsExactlyInAnyOrder("The Dark Knight", "Pulp Fiction");
    }

    @Test
    void testFindMoviesByCriteria_NoCriteria() {
        // When
        List<Movie> movies = movieRepository.findMoviesByCriteria(null, null, null);

        // Then
        assertThat(movies).hasSize(3);
    }

    @Test
    void testGetAverageRating() {
        // When
        Optional<BigDecimal> averageRating = movieRepository.getAverageRating();

        // Then
        assertThat(averageRating).isPresent();
        // Average of 8.8, 9.0, 8.9 = 8.9
        assertThat(averageRating.get()).isEqualByComparingTo(new BigDecimal("8.9"));
    }

    @Test
    void testGetAverageRating_EmptyRepository() {
        // Given - clear all movies
        movieRepository.deleteAll();
        entityManager.flush();

        // When
        Optional<BigDecimal> averageRating = movieRepository.getAverageRating();

        // Then
        assertThat(averageRating).isEmpty();
    }

    @Test
    void testCountByGenreIgnoreCase() {
        // When
        long actionCount = movieRepository.countByGenreIgnoreCase("action");
        long sciFiCount = movieRepository.countByGenreIgnoreCase("SCI-FI");
        long nonexistentCount = movieRepository.countByGenreIgnoreCase("Horror");

        // Then
        assertThat(actionCount).isEqualTo(1);
        assertThat(sciFiCount).isEqualTo(1);
        assertThat(nonexistentCount).isEqualTo(0);
    }

    @Test
    void testSaveMovie() {
        // Given
        Movie newMovie = new Movie("The Matrix", "The Wachowskis", "Sci-Fi", 1999, new BigDecimal("8.7"));

        // When
        Movie savedMovie = movieRepository.save(newMovie);

        // Then
        assertThat(savedMovie.getId()).isNotNull();
        assertThat(savedMovie.getTitle()).isEqualTo("The Matrix");

        // Verify it's in database
        Optional<Movie> found = movieRepository.findById(savedMovie.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("The Matrix");
    }

    @Test
    void testUpdateMovie() {
        // Given
        Movie movieToUpdate = movieRepository.findById(movie1.getId()).orElseThrow();
        
        // When
        movieToUpdate.setRating(new BigDecimal("9.5"));
        Movie updatedMovie = movieRepository.save(movieToUpdate);

        // Then
        assertThat(updatedMovie.getRating()).isEqualByComparingTo(new BigDecimal("9.5"));

        // Verify in database
        Movie fromDb = movieRepository.findById(movie1.getId()).orElseThrow();
        assertThat(fromDb.getRating()).isEqualByComparingTo(new BigDecimal("9.5"));
    }

    @Test
    void testDeleteMovie() {
        // Given
        Long movieId = movie1.getId();
        assertThat(movieRepository.existsById(movieId)).isTrue();

        // When
        movieRepository.deleteById(movieId);

        // Then
        assertThat(movieRepository.existsById(movieId)).isFalse();
        assertThat(movieRepository.findAll()).hasSize(2);
    }
}
