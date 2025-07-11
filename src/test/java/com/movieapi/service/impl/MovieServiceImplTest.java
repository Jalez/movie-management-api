package com.movieapi.service.impl;

import com.movieapi.entity.Movie;
import com.movieapi.exception.DuplicateMovieException;
import com.movieapi.exception.InvalidMovieDataException;
import com.movieapi.exception.MovieNotFoundException;
import com.movieapi.repository.MovieRepository;
import com.movieapi.testutil.MovieTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.movieapi.testutil.MovieTestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MovieServiceImpl.
 * Uses Mockito to mock repository dependencies and test business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie validMovie;
    private Movie anotherMovie;

    @BeforeEach
    void setUp() {
        validMovie = inception()
                .withId(1L)
                .build();

        anotherMovie = theDarkKnight()
                .withId(2L)
                .build();
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() {
        // Given
        List<Movie> expectedMovies = Arrays.asList(validMovie, anotherMovie);
        when(movieRepository.findAll()).thenReturn(expectedMovies);

        // When
        List<Movie> actualMovies = movieService.getAllMovies();

        // Then
        assertThat(actualMovies).hasSize(2);
        assertThat(actualMovies).containsExactlyElementsOf(expectedMovies);
        verify(movieRepository).findAll();
    }

    @Test
    void getMovieById_WhenMovieExists_ShouldReturnMovie() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(validMovie));

        // When
        Optional<Movie> result = movieService.getMovieById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(validMovie);
        verify(movieRepository).findById(1L);
    }

    @Test
    void getMovieById_WhenMovieDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Movie> result = movieService.getMovieById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(movieRepository).findById(999L);
    }

    @Test
    void getMovieById_WhenIdIsNull_ShouldReturnEmpty() {
        // When
        Optional<Movie> result = movieService.getMovieById(null);

        // Then
        assertThat(result).isEmpty();
        verifyNoInteractions(movieRepository);
    }

    @Test
    void createMovie_WhenValidMovie_ShouldCreateSuccessfully() {
        // Given
        Movie movieToCreate = aMovie()
                .withTitle("New Movie")
                .withDirector("New Director")
                .withGenre("Drama")
                .withReleaseYear(2023)
                .withRating(7.5)
                .build();
        
        Movie savedMovie = aMovie()
                .withId(3L)
                .withTitle("New Movie")
                .withDirector("New Director")
                .withGenre("Drama")
                .withReleaseYear(2023)
                .withRating(7.5)
                .build();

        when(movieRepository.existsByTitleAndDirector("New Movie", "New Director")).thenReturn(false);
        when(movieRepository.save(movieToCreate)).thenReturn(savedMovie);

        // When
        Movie result = movieService.createMovie(movieToCreate);

        // Then
        assertThat(result).isEqualTo(savedMovie);
        assertThat(result.getId()).isEqualTo(3L);
        verify(movieRepository).existsByTitleAndDirector("New Movie", "New Director");
        verify(movieRepository).save(movieToCreate);
    }

    @Test
    void createMovie_WhenDuplicateExists_ShouldThrowDuplicateMovieException() {
        // Given
        when(movieRepository.existsByTitleAndDirector("Inception", "Christopher Nolan")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> movieService.createMovie(validMovie))
                .isInstanceOf(DuplicateMovieException.class)
                .hasMessage("Movie 'Inception' by director 'Christopher Nolan' already exists");

        verify(movieRepository).existsByTitleAndDirector("Inception", "Christopher Nolan");
        verify(movieRepository, never()).save(any());
    }

    @Test
    void createMovie_WhenMovieIsNull_ShouldThrowInvalidMovieDataException() {
        // When & Then
        assertThatThrownBy(() -> movieService.createMovie(null))
                .isInstanceOf(InvalidMovieDataException.class)
                .hasMessage("Movie cannot be null");

        verifyNoInteractions(movieRepository);
    }

    @Test
    void createMovie_WhenTitleIsEmpty_ShouldThrowInvalidMovieDataException() {
        // Given
        Movie invalidMovie = movieWithBlankTitle().build();

        // When & Then
        assertThatThrownBy(() -> movieService.createMovie(invalidMovie))
                .isInstanceOf(InvalidMovieDataException.class)
                .hasMessageContaining("Title cannot be null or empty");

        verifyNoInteractions(movieRepository);
    }

    @Test
    void createMovie_WhenReleaseYearTooOld_ShouldThrowInvalidMovieDataException() {
        // Given
        Movie invalidMovie = movieWithInvalidReleaseYear().build();

        // When & Then
        assertThatThrownBy(() -> movieService.createMovie(invalidMovie))
                .isInstanceOf(InvalidMovieDataException.class)
                .hasMessageContaining("Release year cannot be before 1888");

        verifyNoInteractions(movieRepository);
    }

    @Test
    void createMovie_WhenReleaseYearTooFuture_ShouldThrowInvalidMovieDataException() {
        // Given
        Movie invalidMovie = movieWithFutureReleaseYear().build();

        // When & Then
        assertThatThrownBy(() -> movieService.createMovie(invalidMovie))
                .isInstanceOf(InvalidMovieDataException.class)
                .hasMessageContaining("Release year cannot be more than 5 years in the future");

        verifyNoInteractions(movieRepository);
    }

    @Test
    void createMovie_WhenRatingTooHigh_ShouldThrowInvalidMovieDataException() {
        // Given
        Movie invalidMovie = movieWithInvalidRating().build();

        // When & Then
        assertThatThrownBy(() -> movieService.createMovie(invalidMovie))
                .isInstanceOf(InvalidMovieDataException.class)
                .hasMessageContaining("Rating cannot exceed 10.0");

        verifyNoInteractions(movieRepository);
    }

    @Test
    void updateMovie_WhenValidUpdate_ShouldUpdateSuccessfully() {
        // Given
        Movie updatedMovie = inception()
                .withId(1L)
                .withTitle("Updated Title")
                .withRating(9.0)
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(validMovie));
        when(movieRepository.existsByTitleAndDirector("Updated Title", "Christopher Nolan")).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        // When
        Movie result = movieService.updateMovie(1L, updatedMovie);

        // Then
        assertThat(result).isEqualTo(updatedMovie);
        verify(movieRepository).findById(1L);
        verify(movieRepository).existsByTitleAndDirector("Updated Title", "Christopher Nolan");
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void updateMovie_WhenMovieNotFound_ShouldThrowMovieNotFoundException() {
        // Given
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.updateMovie(999L, validMovie))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessage("Movie with ID 999 not found");

        verify(movieRepository).findById(999L);
        verify(movieRepository, never()).save(any());
    }

    @Test
    void deleteMovie_WhenMovieExists_ShouldDeleteSuccessfully() {
        // Given
        when(movieRepository.existsById(1L)).thenReturn(true);

        // When
        movieService.deleteMovie(1L);

        // Then
        verify(movieRepository).existsById(1L);
        verify(movieRepository).deleteById(1L);
    }

    @Test
    void deleteMovie_WhenMovieNotFound_ShouldThrowMovieNotFoundException() {
        // Given
        when(movieRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> movieService.deleteMovie(999L))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessage("Movie with ID 999 not found");

        verify(movieRepository).existsById(999L);
        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    void searchMovies_WithAllCriteria_ShouldReturnFilteredMovies() {
        // Given
        List<Movie> repositoryResult = Arrays.asList(validMovie, anotherMovie);
        when(movieRepository.findMoviesByCriteria("Action", new BigDecimal("8.0"), 2008))
                .thenReturn(repositoryResult);

        // When
        List<Movie> result = movieService.searchMovies("Action", 2008, new BigDecimal("8.0"), "Christopher");

        // Then
        assertThat(result).hasSize(2); // Both movies match director filter
        verify(movieRepository).findMoviesByCriteria("Action", new BigDecimal("8.0"), 2008);
    }

    @Test
    void searchMovies_WithDirectorFilter_ShouldFilterByDirector() {
        // Given
        List<Movie> repositoryResult = Arrays.asList(validMovie, anotherMovie);
        when(movieRepository.findMoviesByCriteria(null, null, null))
                .thenReturn(repositoryResult);

        // When
        List<Movie> result = movieService.searchMovies(null, null, null, "Christopher");

        // Then
        assertThat(result).hasSize(2); // Both movies are by Christopher Nolan
        verify(movieRepository).findMoviesByCriteria(null, null, null);
    }

    @Test
    void getMoviesByGenre_WhenGenreExists_ShouldReturnMovies() {
        // Given
        List<Movie> expectedMovies = Arrays.asList(validMovie);
        when(movieRepository.findByGenreIgnoreCase("Sci-Fi")).thenReturn(expectedMovies);

        // When
        List<Movie> result = movieService.getMoviesByGenre("Sci-Fi");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expectedMovies);
        verify(movieRepository).findByGenreIgnoreCase("Sci-Fi");
    }

    @Test
    void getMoviesByGenre_WhenGenreIsNull_ShouldReturnEmptyList() {
        // When
        List<Movie> result = movieService.getMoviesByGenre(null);

        // Then
        assertThat(result).isEmpty();
        verifyNoInteractions(movieRepository);
    }

    @Test
    void getMoviesByYear_WhenYearExists_ShouldReturnMovies() {
        // Given
        List<Movie> expectedMovies = Arrays.asList(validMovie);
        when(movieRepository.findByReleaseYear(2010)).thenReturn(expectedMovies);

        // When
        List<Movie> result = movieService.getMoviesByYear(2010);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expectedMovies);
        verify(movieRepository).findByReleaseYear(2010);
    }

    @Test
    void getMoviesByMinRating_WhenRatingProvided_ShouldReturnMovies() {
        // Given
        List<Movie> expectedMovies = Arrays.asList(anotherMovie);
        when(movieRepository.findByRatingGreaterThanEqual(new BigDecimal("9.0"))).thenReturn(expectedMovies);

        // When
        List<Movie> result = movieService.getMoviesByMinRating(new BigDecimal("9.0"));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expectedMovies);
        verify(movieRepository).findByRatingGreaterThanEqual(new BigDecimal("9.0"));
    }

    @Test
    void getTopRatedMovies_WhenValidLimit_ShouldReturnTopMovies() {
        // Given
        List<Movie> expectedMovies = Arrays.asList(anotherMovie, validMovie);
        when(movieRepository.findTopRatedMovies(any(Pageable.class))).thenReturn(expectedMovies);

        // When
        List<Movie> result = movieService.getTopRatedMovies(2);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedMovies);
        verify(movieRepository).findTopRatedMovies(any(Pageable.class));
    }

    @Test
    void getTopRatedMovies_WhenInvalidLimit_ShouldReturnEmptyList() {
        // When
        List<Movie> result = movieService.getTopRatedMovies(0);

        // Then
        assertThat(result).isEmpty();
        verifyNoInteractions(movieRepository);
    }

    @Test
    void getAverageRating_WhenMoviesExist_ShouldReturnAverage() {
        // Given
        BigDecimal expectedAverage = new BigDecimal("8.9");
        when(movieRepository.getAverageRating()).thenReturn(Optional.of(expectedAverage));

        // When
        Optional<BigDecimal> result = movieService.getAverageRating();

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualByComparingTo(expectedAverage);
        verify(movieRepository).getAverageRating();
    }

    @Test
    void getAverageRating_WhenNoMoviesExist_ShouldReturnEmpty() {
        // Given
        when(movieRepository.getAverageRating()).thenReturn(Optional.empty());

        // When
        Optional<BigDecimal> result = movieService.getAverageRating();

        // Then
        assertThat(result).isEmpty();
        verify(movieRepository).getAverageRating();
    }

    @Test
    void movieExists_WhenMovieExists_ShouldReturnTrue() {
        // Given
        when(movieRepository.existsByTitleAndDirector("Inception", "Christopher Nolan")).thenReturn(true);

        // When
        boolean result = movieService.movieExists("Inception", "Christopher Nolan");

        // Then
        assertThat(result).isTrue();
        verify(movieRepository).existsByTitleAndDirector("Inception", "Christopher Nolan");
    }

    @Test
    void movieExists_WhenMovieDoesNotExist_ShouldReturnFalse() {
        // Given
        when(movieRepository.existsByTitleAndDirector("Nonexistent", "Unknown")).thenReturn(false);

        // When
        boolean result = movieService.movieExists("Nonexistent", "Unknown");

        // Then
        assertThat(result).isFalse();
        verify(movieRepository).existsByTitleAndDirector("Nonexistent", "Unknown");
    }

    @Test
    void movieExists_WhenParametersAreNull_ShouldReturnFalse() {
        // When
        boolean result = movieService.movieExists(null, null);

        // Then
        assertThat(result).isFalse();
        verifyNoInteractions(movieRepository);
    }

    @Test
    void countMoviesByGenre_WhenGenreExists_ShouldReturnCount() {
        // Given
        when(movieRepository.countByGenreIgnoreCase("Action")).thenReturn(5L);

        // When
        long result = movieService.countMoviesByGenre("Action");

        // Then
        assertThat(result).isEqualTo(5L);
        verify(movieRepository).countByGenreIgnoreCase("Action");
    }

    @Test
    void countMoviesByGenre_WhenGenreIsNull_ShouldReturnZero() {
        // When
        long result = movieService.countMoviesByGenre(null);

        // Then
        assertThat(result).isEqualTo(0L);
        verifyNoInteractions(movieRepository);
    }
}
