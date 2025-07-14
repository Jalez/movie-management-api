package com.movieapi.repository;

import com.movieapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    // Custom query methods can be added here if needed
    org.springframework.data.domain.Page<Review> findAll(org.springframework.data.domain.Pageable pageable);
    
    /**
     * Find all reviews for a specific movie by movie ID.
     * @param movieId the ID of the movie
     * @return list of reviews for the movie
     */
    List<Review> findByMovieId(Long movieId);
}
