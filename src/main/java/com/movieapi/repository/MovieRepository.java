package com.movieapi.repository;

import com.movieapi.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Movie entity operations.
 * Extends JpaRepository to provide basic CRUD operations and custom query methods.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Find movies by title (case-insensitive).
     *
     * @param title the title to search for
     * @return list of movies matching the title
     */
    List<Movie> findByTitleContainingIgnoreCase(String title);

    /**
     * Find movies by director (case-insensitive).
     *
     * @param director the director to search for
     * @return list of movies by the specified director
     */
    List<Movie> findByDirectorContainingIgnoreCase(String director);

    /**
     * Find movies by genre (case-insensitive).
     *
     * @param genre the genre to search for
     * @return list of movies in the specified genre
     */
    List<Movie> findByGenreIgnoreCase(String genre);

    /**
     * Find movies by release year.
     *
     * @param releaseYear the release year to search for
     * @return list of movies released in the specified year
     */
    List<Movie> findByReleaseYear(Integer releaseYear);

    /**
     * Find movies with rating greater than or equal to the specified value.
     *
     * @param rating the minimum rating
     * @return list of movies with rating >= specified value
     */
    List<Movie> findByRatingGreaterThanEqual(BigDecimal rating);

    /**
     * Find movies by release year range.
     *
     * @param startYear the start year (inclusive)
     * @param endYear   the end year (inclusive)
     * @return list of movies released between the specified years
     */
    List<Movie> findByReleaseYearBetween(Integer startYear, Integer endYear);

    /**
     * Find movies by rating range.
     *
     * @param minRating the minimum rating (inclusive)
     * @param maxRating the maximum rating (inclusive)
     * @return list of movies with rating in the specified range
     */
    List<Movie> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);

    /**
     * Check if a movie with the exact title and director exists.
     *
     * @param title    the movie title
     * @param director the movie director
     * @return true if movie exists, false otherwise
     */
    boolean existsByTitleAndDirector(String title, String director);

    /**
     * Find the top-rated movies limited by count.
     *
     * @param limit the maximum number of movies to return
     * @return list of top-rated movies
     */
    @Query("SELECT m FROM Movie m ORDER BY m.rating DESC LIMIT :limit")
    List<Movie> findTopRatedMovies(@Param("limit") int limit);

    /**
     * Find movies by multiple criteria using custom query.
     *
     * @param genre       the genre (optional)
     * @param minRating   the minimum rating (optional)
     * @param releaseYear the release year (optional)
     * @return list of movies matching the criteria
     */
    @Query("SELECT m FROM Movie m WHERE " +
            "(:genre IS NULL OR LOWER(m.genre) = LOWER(:genre)) AND " +
            "(:minRating IS NULL OR m.rating >= :minRating) AND " +
            "(:releaseYear IS NULL OR m.releaseYear = :releaseYear)")
    List<Movie> findMoviesByCriteria(
            @Param("genre") String genre,
            @Param("minRating") BigDecimal minRating,
            @Param("releaseYear") Integer releaseYear
    );

    /**
     * Get the average rating of all movies.
     *
     * @return the average rating
     */
    @Query("SELECT AVG(m.rating) FROM Movie m")
    Optional<BigDecimal> getAverageRating();

    /**
     * Count movies by genre.
     *
     * @param genre the genre to count
     * @return number of movies in the specified genre
     */
    long countByGenreIgnoreCase(String genre);
}
