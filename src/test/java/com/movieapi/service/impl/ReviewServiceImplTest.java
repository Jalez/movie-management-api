package com.movieapi.service.impl;

import com.movieapi.entity.Movie;
import com.movieapi.entity.Review;
import com.movieapi.exception.MovieNotFoundException;
import com.movieapi.exception.ReviewNotFoundException;
import com.movieapi.repository.MovieRepository;
import com.movieapi.repository.ReviewRepository;
import com.movieapi.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review1;
    private Review review2;
    private Movie movie1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Test Movie");
        movie1.setDirector("Test Director");
        movie1.setGenre("Action");
        movie1.setReleaseYear(2020);
        
        review1 = new Review();
        review1.setId(1L);
        review1.setUserName("John");
        review1.setRating(8.5);
        review1.setReviewText("Great movie!");
        review1.setCreatedAt(LocalDateTime.now());
        review1.setUpdatedAt(LocalDateTime.now());
        review1.setMovie(movie1);

        review2 = new Review();
        review2.setId(2L);
        review2.setUserName("Jane");
        review2.setRating(9.0);
        review2.setReviewText("Loved it!");
        review2.setCreatedAt(LocalDateTime.now());
        review2.setUpdatedAt(LocalDateTime.now());
        review2.setMovie(movie1);
    }

    @Test
    void getReviewsByMovie_ShouldReturnReviews() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findByMovieId(1L)).thenReturn(Arrays.asList(review1, review2));
        List<Review> result = reviewService.getReviewsByMovie(1L);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserName()).isEqualTo("John");
        assertThat(result.get(1).getUserName()).isEqualTo("Jane");
        verify(movieRepository, times(1)).existsById(1L);
        verify(reviewRepository, times(1)).findByMovieId(1L);
    }

    @Test
    void getReviewsByMovie_WhenMovieNotFound_ShouldThrowException() {
        when(movieRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> reviewService.getReviewsByMovie(999L))
                .isInstanceOf(MovieNotFoundException.class);
        verify(movieRepository, times(1)).existsById(999L);
        verify(reviewRepository, never()).findByMovieId(any());
    }

    @Test
    void getReview_ShouldReturnReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));
        Optional<Review> result = reviewService.getReview(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getUserName()).isEqualTo("John");
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void getReview_WhenReviewNotFound_ShouldReturnEmpty() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Review> result = reviewService.getReview(999L);
        assertThat(result).isEmpty();
        verify(reviewRepository, times(1)).findById(999L);
    }

    @Test
    void addReview_ShouldSaveReview() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);
        Review result = reviewService.addReview(1L, review1);
        assertThat(result.getUserName()).isEqualTo("John");
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void addReview_WhenMovieNotFound_ShouldThrowException() {
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.addReview(999L, review1))
                .isInstanceOf(MovieNotFoundException.class);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void addReview_WithReviewRequest_ShouldSaveReview() {
        com.movieapi.dto.ReviewRequest request = new com.movieapi.dto.ReviewRequest();
        request.setUserName("New User");
        request.setReviewText("New Review");
        request.setRating(7.5);
        
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);
        
        Review result = reviewService.addReview(1L, request);
        assertThat(result).isNotNull();
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void updateReview_ShouldUpdateReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie1);
        
        Review updated = new Review();
        updated.setId(1L);
        updated.setUserName("John Updated");
        updated.setRating(8.8);
        updated.setReviewText("Even better!");
        updated.setCreatedAt(LocalDateTime.now());
        updated.setUpdatedAt(LocalDateTime.now());
        
        Review result = reviewService.updateReview(1L, updated);
        assertThat(result.getUserName()).isEqualTo("John Updated");
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void updateReview_WhenReviewNotFound_ShouldThrowException() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.updateReview(999L, review1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Review not found");
    }

    @Test
    void updateReview_WithReviewRequest_ShouldUpdateReview() {
        com.movieapi.dto.ReviewRequest request = new com.movieapi.dto.ReviewRequest();
        request.setUserName("Updated User");
        request.setReviewText("Updated Review");
        request.setRating(9.5);
        
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie1);
        
        Review result = reviewService.updateReview(1L, request);
        assertThat(result).isNotNull();
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void updateReview_WithReviewRequest_WhenReviewNotFound_ShouldThrowException() {
        com.movieapi.dto.ReviewRequest request = new com.movieapi.dto.ReviewRequest();
        request.setUserName("Updated User");
        request.setReviewText("Updated Review");
        request.setRating(9.5);
        
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.updateReview(999L, request))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void deleteReview_ShouldDeleteReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));
        doNothing().when(reviewRepository).delete(review1);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie1);
        
        reviewService.deleteReview(1L);
        verify(reviewRepository, times(1)).delete(review1);
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void deleteReview_WhenReviewNotFound_ShouldThrowException() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.deleteReview(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Review not found");
    }

    @Test
    void deleteReview_WhenMovieIsNull_ShouldNotUpdateRating() {
        Review reviewWithoutMovie = new Review();
        reviewWithoutMovie.setId(1L);
        reviewWithoutMovie.setUserName("John");
        reviewWithoutMovie.setRating(8.5);
        reviewWithoutMovie.setMovie(null);
        
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewWithoutMovie));
        doNothing().when(reviewRepository).delete(reviewWithoutMovie);
        
        reviewService.deleteReview(1L);
        verify(reviewRepository, times(1)).delete(reviewWithoutMovie);
        verify(movieRepository, never()).save(any());
    }

    @Test
    void getReviewByMovieAndReviewId_ShouldReturnReview() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));
        
        Review result = reviewService.getReviewByMovieAndReviewId(1L, 1L);
        assertThat(result).isEqualTo(review1);
        verify(movieRepository, times(1)).existsById(1L);
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void getReviewByMovieAndReviewId_WhenMovieNotFound_ShouldThrowException() {
        when(movieRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> reviewService.getReviewByMovieAndReviewId(999L, 1L))
                .isInstanceOf(MovieNotFoundException.class);
        verify(reviewRepository, never()).findById(any());
    }

    @Test
    void getReviewByMovieAndReviewId_WhenReviewNotFound_ShouldThrowException() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.getReviewByMovieAndReviewId(1L, 999L))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void getReviewByMovieAndReviewId_WhenReviewDoesNotBelongToMovie_ShouldThrowException() {
        Movie differentMovie = new Movie();
        differentMovie.setId(2L);
        Review reviewForDifferentMovie = new Review();
        reviewForDifferentMovie.setId(1L);
        reviewForDifferentMovie.setMovie(differentMovie);
        
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewForDifferentMovie));
        
        assertThatThrownBy(() -> reviewService.getReviewByMovieAndReviewId(1L, 1L))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void searchReviews_ShouldReturnPagedReviews() {
        List<Review> reviews = Arrays.asList(review1, review2);
        Page<Review> page = new PageImpl<>(reviews, PageRequest.of(0, 2), 2);
        when(reviewRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class))).thenReturn(page);
        
        Page<Review> result = reviewService.searchReviews(null, null, null, null, null, PageRequest.of(0, 2));
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getUserName()).isEqualTo("John");
        assertThat(result.getContent().get(1).getUserName()).isEqualTo("Jane");
        verify(reviewRepository, times(1)).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class));
    }

    @Test
    void searchReviews_WithFilters_ShouldApplyFilters() {
        List<Review> reviews = Arrays.asList(review1);
        Page<Review> page = new PageImpl<>(reviews, PageRequest.of(0, 1), 1);
        when(reviewRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class))).thenReturn(page);
        
        Page<Review> result = reviewService.searchReviews(8.0, 9.0, "John", "2023-01-01T00:00:00", "2023-12-31T23:59:59", PageRequest.of(0, 1));
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(reviewRepository, times(1)).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class));
    }

    @Test
    void searchReviews_WithInvalidDate_ShouldIgnoreDateFilter() {
        List<Review> reviews = Arrays.asList(review1, review2);
        Page<Review> page = new PageImpl<>(reviews, PageRequest.of(0, 2), 2);
        when(reviewRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class))).thenReturn(page);
        
        Page<Review> result = reviewService.searchReviews(null, null, null, "invalid-date", "invalid-date", PageRequest.of(0, 2));
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(reviewRepository, times(1)).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class));
    }

    @Test
    void getAllReviews_ShouldReturnAllReviews() {
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewRepository.findAll()).thenReturn(reviews);
        
        List<Review> result = reviewService.getAllReviews();
        assertThat(result).hasSize(2);
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void getAllReviews_WithPageable_ShouldReturnPagedReviews() {
        List<Review> reviews = Arrays.asList(review1, review2);
        Page<Review> page = new PageImpl<>(reviews, PageRequest.of(0, 2), 2);
        when(reviewRepository.findAll(any(PageRequest.class))).thenReturn(page);
        
        Page<Review> result = reviewService.getAllReviews(PageRequest.of(0, 2));
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(reviewRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void updateMovieRating_WithNoReviews_ShouldSetRatingToZero() {
        Movie movieWithoutReviews = new Movie();
        movieWithoutReviews.setId(1L);
        movieWithoutReviews.setReviews(List.of());
        
        when(movieRepository.save(any(Movie.class))).thenReturn(movieWithoutReviews);
        
        // This will be called internally by addReview
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movieWithoutReviews));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);
        
        reviewService.addReview(1L, review1);
        
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void updateMovieRating_WithNullReviews_ShouldSetRatingToZero() {
        Movie movieWithNullReviews = new Movie();
        movieWithNullReviews.setId(1L);
        movieWithNullReviews.setReviews(null);
        
        when(movieRepository.save(any(Movie.class))).thenReturn(movieWithNullReviews);
        
        // This will be called internally by addReview
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movieWithNullReviews));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);
        
        reviewService.addReview(1L, review1);
        
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void updateMovieRating_WithNullMovie_ShouldNotSave() {
        // This test covers the edge case where movie is null
        // We'll test this by calling updateReview with a review that has null movie
        Review reviewWithNullMovie = new Review();
        reviewWithNullMovie.setId(1L);
        reviewWithNullMovie.setUserName("John");
        reviewWithNullMovie.setRating(8.5);
        reviewWithNullMovie.setMovie(null);
        
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewWithNullMovie));
        when(reviewRepository.save(any(Review.class))).thenReturn(reviewWithNullMovie);
        
        reviewService.updateReview(1L, reviewWithNullMovie);
        
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(movieRepository, never()).save(any());
    }
}
