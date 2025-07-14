package com.movieapi.service.impl;

import com.movieapi.entity.Movie;
import com.movieapi.entity.Review;
import com.movieapi.exception.MovieNotFoundException;
import com.movieapi.exception.ReviewNotFoundException;
import com.movieapi.repository.MovieRepository;
import com.movieapi.repository.ReviewRepository;
import com.movieapi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, MovieRepository movieRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public Review addReview(Long movieId, Review review) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        review.setMovie(movie);
        review.setCreatedAt(java.time.LocalDateTime.now());
        review.setUpdatedAt(java.time.LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        updateMovieRating(movie);
        return savedReview;
    }

    @Override
    public Review addReview(Long movieId, com.movieapi.dto.ReviewRequest reviewRequest) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        
        Review review = new Review();
        review.setMovie(movie);
        review.setUserName(reviewRequest.getUserName());
        review.setReviewText(reviewRequest.getReviewText());
        review.setRating(reviewRequest.getRating());
        review.setCreatedAt(java.time.LocalDateTime.now());
        review.setUpdatedAt(java.time.LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        updateMovieRating(movie);
        return savedReview;
    }
    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public org.springframework.data.domain.Page<Review> getAllReviews(org.springframework.data.domain.Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    public Review updateReview(Long reviewId, Review updatedReview) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setUserName(updatedReview.getUserName());
        review.setReviewText(updatedReview.getReviewText());
        review.setRating(updatedReview.getRating());
        review.setUpdatedAt(java.time.LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        Movie movie = review.getMovie();
        if (movie != null) {
            updateMovieRating(movie);
        }
        return savedReview;
    }

    @Override
    public Review updateReview(Long reviewId, com.movieapi.dto.ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId, null));
        review.setUserName(reviewRequest.getUserName());
        review.setReviewText(reviewRequest.getReviewText());
        review.setRating(reviewRequest.getRating());
        review.setUpdatedAt(java.time.LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        Movie movie = review.getMovie();
        if (movie != null) {
            updateMovieRating(movie);
        }
        return savedReview;
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        Movie movie = review.getMovie();
        reviewRepository.delete(review);
        if (movie != null) {
            updateMovieRating(movie);
        }
    }

    @Override
    public List<Review> getReviewsByMovie(Long movieId) {
        // First check if the movie exists
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException(movieId);
        }
        // Then get reviews directly without loading the movie
        return reviewRepository.findByMovieId(movieId);
    }

    @Override
    public Optional<Review> getReview(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Override
    public Review getReviewByMovieAndReviewId(Long movieId, Long reviewId) {
        // First check if the movie exists
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException(movieId);
        }
        // Then get the review and check if it belongs to the movie
        return reviewRepository.findById(reviewId)
                .filter(review -> review.getMovie().getId().equals(movieId))
                .orElseThrow(() -> new ReviewNotFoundException(reviewId, movieId));
    }

    @Override
    public org.springframework.data.domain.Page<Review> searchReviews(Double minRating, Double maxRating, String userName, String startDate, String endDate, org.springframework.data.domain.Pageable pageable) {
        // Use Specification for filtering
        return reviewRepository.findAll((root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating));
            }
            if (maxRating != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating"), maxRating));
            }
            if (userName != null && !userName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("userName")), "%" + userName.toLowerCase() + "%"));
            }
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ISO_DATE_TIME;
            if (startDate != null && !startDate.trim().isEmpty()) {
                try {
                    java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate, formatter);
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
                } catch (Exception ignored) {}
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                try {
                    java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate, formatter);
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end));
                } catch (Exception ignored) {}
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
    }

    private void updateMovieRating(Movie movie) {
        if (movie == null) return;
        List<Review> reviews = movie.getReviews();
        if (reviews == null || reviews.isEmpty()) {
            movie.setRating(BigDecimal.ZERO);
        } else {
            double avg = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            BigDecimal rounded = BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP);
            movie.setRating(rounded);
        }
        movieRepository.save(movie);
    }
}
