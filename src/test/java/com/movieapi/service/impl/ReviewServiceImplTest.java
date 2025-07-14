package com.movieapi.service.impl;

import com.movieapi.entity.Review;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private com.movieapi.repository.MovieRepository movieRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review1;
    private Review review2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        review1 = new Review();
        review1.setId(1L);
        review1.setUserName("John");
        review1.setRating(8.5);
        review1.setReviewText("Great movie!");
        review1.setCreatedAt(LocalDateTime.now());
        review1.setUpdatedAt(LocalDateTime.now());

        review2 = new Review();
        review2.setId(2L);
        review2.setUserName("Jane");
        review2.setRating(9.0);
        review2.setReviewText("Loved it!");
        review2.setCreatedAt(LocalDateTime.now());
        review2.setUpdatedAt(LocalDateTime.now());
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
    void getReview_ShouldReturnReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));
        Optional<Review> result = reviewService.getReview(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getUserName()).isEqualTo("John");
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void addReview_ShouldSaveReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(new com.movieapi.entity.Movie()));
        Review result = reviewService.addReview(1L, review1);
        assertThat(result.getUserName()).isEqualTo("John");
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void updateReview_ShouldUpdateReview() {
        com.movieapi.entity.Movie movie = new com.movieapi.entity.Movie();
        movie.setId(1L);
        review1.setMovie(movie);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);
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
    }

    @Test
    void deleteReview_ShouldDeleteReview() {
        com.movieapi.entity.Movie movie = new com.movieapi.entity.Movie();
        movie.setId(1L);
        review1.setMovie(movie);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));
        doNothing().when(reviewRepository).delete(review1);
        reviewService.deleteReview(1L);
        verify(reviewRepository, times(1)).delete(review1);
    }

    @Test
    void searchReviews_ShouldReturnPagedReviews() {
        List<Review> reviews = Arrays.asList(review1, review2);
        Page<Review> page = new PageImpl<>(reviews, PageRequest.of(0, 2), 2);
        when(reviewRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class))).thenReturn(page); // Suppress unchecked warning
        Page<Review> result = reviewService.searchReviews(null, null, null, null, null, PageRequest.of(0, 2));
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getUserName()).isEqualTo("John");
        assertThat(result.getContent().get(1).getUserName()).isEqualTo("Jane");
        verify(reviewRepository, times(1)).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class)); // Suppress unchecked warning
    }
}
