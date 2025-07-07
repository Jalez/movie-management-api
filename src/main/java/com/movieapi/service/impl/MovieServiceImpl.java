package com.movieapi.service.impl;

import com.movieapi.entity.Movie;
import com.movieapi.exception.DuplicateMovieException;
import com.movieapi.exception.InvalidMovieDataException;
import com.movieapi.exception.MovieNotFoundException;
import com.movieapi.repository.MovieRepository;
import com.movieapi.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of MovieService providing business logic for movie operations.
 * Handles validation, error checking, and coordinates with the repository layer.
 */
@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);

    private final MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getAllMovies() {
        logger.debug("Retrieving all movies");
        List<Movie> movies = movieRepository.findAll();
        logger.info("Retrieved {} movies", movies.size());
        return movies;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Movie> getMovieById(Long id) {
        logger.debug("Retrieving movie with ID: {}", id);
        if (id == null) {
            logger.warn("Attempted to retrieve movie with null ID");
            return Optional.empty();
        }
        
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()) {
            logger.debug("Found movie with ID: {}", id);
        } else {
            logger.debug("No movie found with ID: {}", id);
        }
        return movie;
    }

    @Override
    public Movie createMovie(Movie movie) {
        logger.debug("Creating new movie: {}", movie != null ? movie.getTitle() : "null");
        
        // Validate input
        validateMovieData(movie);
        
        // Additional business rule validation
        validateBusinessRules(movie);
        
        // Check for duplicates
        if (movieRepository.existsByTitleAndDirector(movie.getTitle(), movie.getDirector())) {
            logger.warn("Attempted to create duplicate movie: {} by {}", movie.getTitle(), movie.getDirector());
            throw new DuplicateMovieException(movie.getTitle(), movie.getDirector());
        }
        
        // Save the movie
        Movie savedMovie = movieRepository.save(movie);
        logger.info("Successfully created movie with ID: {} - {}", savedMovie.getId(), savedMovie.getTitle());
        
        return savedMovie;
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        logger.debug("Updating movie with ID: {}", id);
        
        // Check if movie exists
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Attempted to update non-existent movie with ID: {}", id);
                    return new MovieNotFoundException(id);
                });
        
        // Validate input
        validateMovieData(movie);
        
        // Additional business rule validation
        validateBusinessRules(movie);
        
        // Check for duplicates (excluding current movie)
        if (!existingMovie.getTitle().equals(movie.getTitle()) || 
            !existingMovie.getDirector().equals(movie.getDirector())) {
            if (movieRepository.existsByTitleAndDirector(movie.getTitle(), movie.getDirector())) {
                logger.warn("Attempted to update movie to duplicate: {} by {}", movie.getTitle(), movie.getDirector());
                throw new DuplicateMovieException(movie.getTitle(), movie.getDirector());
            }
        }
        
        // Update the movie
        movie.setId(id); // Ensure the ID is preserved
        Movie updatedMovie = movieRepository.save(movie);
        logger.info("Successfully updated movie with ID: {} - {}", updatedMovie.getId(), updatedMovie.getTitle());
        
        return updatedMovie;
    }

    @Override
    public void deleteMovie(Long id) {
        logger.debug("Deleting movie with ID: {}", id);
        
        // Check if movie exists
        if (!movieRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent movie with ID: {}", id);
            throw new MovieNotFoundException(id);
        }
        
        movieRepository.deleteById(id);
        logger.info("Successfully deleted movie with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> searchMovies(String genre, Integer year, BigDecimal minRating, String director) {
        logger.debug("Searching movies with criteria - genre: {}, year: {}, minRating: {}, director: {}", 
                    genre, year, minRating, director);
        
        List<Movie> movies = movieRepository.findMoviesByCriteria(genre, minRating, year);
        
        // Additional filtering for director if provided
        if (director != null && !director.trim().isEmpty()) {
            movies = movies.stream()
                    .filter(movie -> movie.getDirector().toLowerCase().contains(director.toLowerCase()))
                    .toList();
        }
        
        logger.info("Search returned {} movies", movies.size());
        return movies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByGenre(String genre) {
        logger.debug("Retrieving movies by genre: {}", genre);
        if (genre == null || genre.trim().isEmpty()) {
            return List.of();
        }
        
        List<Movie> movies = movieRepository.findByGenreIgnoreCase(genre);
        logger.debug("Found {} movies in genre: {}", movies.size(), genre);
        return movies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByYear(Integer year) {
        logger.debug("Retrieving movies by year: {}", year);
        if (year == null) {
            return List.of();
        }
        
        List<Movie> movies = movieRepository.findByReleaseYear(year);
        logger.debug("Found {} movies from year: {}", movies.size(), year);
        return movies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByMinRating(BigDecimal minRating) {
        logger.debug("Retrieving movies with minimum rating: {}", minRating);
        if (minRating == null) {
            return getAllMovies();
        }
        
        List<Movie> movies = movieRepository.findByRatingGreaterThanEqual(minRating);
        logger.debug("Found {} movies with rating >= {}", movies.size(), minRating);
        return movies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByDirector(String director) {
        logger.debug("Retrieving movies by director: {}", director);
        if (director == null || director.trim().isEmpty()) {
            return List.of();
        }
        
        List<Movie> movies = movieRepository.findByDirectorContainingIgnoreCase(director);
        logger.debug("Found {} movies by director: {}", movies.size(), director);
        return movies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getTopRatedMovies(int limit) {
        logger.debug("Retrieving top {} rated movies", limit);
        if (limit <= 0) {
            logger.warn("Invalid limit for top rated movies: {}", limit);
            return List.of();
        }
        
        List<Movie> movies = movieRepository.findTopRatedMovies(limit);
        logger.debug("Retrieved {} top rated movies", movies.size());
        return movies;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> getAverageRating() {
        logger.debug("Calculating average rating of all movies");
        Optional<BigDecimal> avgRating = movieRepository.getAverageRating();
        
        if (avgRating.isPresent()) {
            logger.debug("Average rating: {}", avgRating.get());
        } else {
            logger.debug("No movies found for average rating calculation");
        }
        
        return avgRating;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean movieExists(String title, String director) {
        logger.debug("Checking if movie exists: {} by {}", title, director);
        if (title == null || director == null) {
            return false;
        }
        
        boolean exists = movieRepository.existsByTitleAndDirector(title, director);
        logger.debug("Movie exists: {}", exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public long countMoviesByGenre(String genre) {
        logger.debug("Counting movies in genre: {}", genre);
        if (genre == null || genre.trim().isEmpty()) {
            return 0;
        }
        
        long count = movieRepository.countByGenreIgnoreCase(genre);
        logger.debug("Found {} movies in genre: {}", count, genre);
        return count;
    }

    /**
     * Validates basic movie data requirements.
     *
     * @param movie the movie to validate
     * @throws InvalidMovieDataException if validation fails
     */
    private void validateMovieData(Movie movie) {
        if (movie == null) {
            throw new InvalidMovieDataException("Movie cannot be null");
        }
        
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new InvalidMovieDataException("title", movie.getTitle(), "Title cannot be null or empty");
        }
        
        if (movie.getDirector() == null || movie.getDirector().trim().isEmpty()) {
            throw new InvalidMovieDataException("director", movie.getDirector(), "Director cannot be null or empty");
        }
        
        if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
            throw new InvalidMovieDataException("genre", movie.getGenre(), "Genre cannot be null or empty");
        }
        
        if (movie.getReleaseYear() == null) {
            throw new InvalidMovieDataException("releaseYear", null, "Release year cannot be null");
        }
        
        if (movie.getRating() == null) {
            throw new InvalidMovieDataException("rating", null, "Rating cannot be null");
        }
    }

    /**
     * Validates business rules beyond basic data validation.
     *
     * @param movie the movie to validate
     * @throws InvalidMovieDataException if business rules are violated
     */
    private void validateBusinessRules(Movie movie) {
        // Validate release year range
        int currentYear = Year.now().getValue();
        if (movie.getReleaseYear() < 1888) { // First motion picture
            throw new InvalidMovieDataException("releaseYear", movie.getReleaseYear(), 
                    "Release year cannot be before 1888 (first motion picture)");
        }
        
        if (movie.getReleaseYear() > currentYear + 5) {
            throw new InvalidMovieDataException("releaseYear", movie.getReleaseYear(), 
                    String.format("Release year cannot be more than 5 years in the future (current year: %d)", currentYear));
        }
        
        // Validate rating range (additional check beyond Bean Validation)
        if (movie.getRating().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMovieDataException("rating", movie.getRating(), "Rating cannot be negative");
        }
        
        if (movie.getRating().compareTo(new BigDecimal("10.0")) > 0) {
            throw new InvalidMovieDataException("rating", movie.getRating(), "Rating cannot exceed 10.0");
        }
        
        // Validate title length
        if (movie.getTitle().length() > 255) {
            throw new InvalidMovieDataException("title", movie.getTitle(), "Title cannot exceed 255 characters");
        }
        
        // Validate director length
        if (movie.getDirector().length() > 255) {
            throw new InvalidMovieDataException("director", movie.getDirector(), "Director name cannot exceed 255 characters");
        }
        
        // Validate genre length
        if (movie.getGenre().length() > 100) {
            throw new InvalidMovieDataException("genre", movie.getGenre(), "Genre cannot exceed 100 characters");
        }
    }
}
