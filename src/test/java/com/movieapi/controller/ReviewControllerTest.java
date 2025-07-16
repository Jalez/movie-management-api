package com.movieapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieapi.entity.Review;
import com.movieapi.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ReviewController.
 * Tests CRUD, search, and error handling endpoints.
 */
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review testReview;
    private Review testReview2;

    @BeforeEach
    void setUp() {
        testReview = new Review();
        testReview.setId(1L);
        testReview.setUserName("John");
        testReview.setRating(8.5);
        testReview.setReviewText("Great movie!");
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setUpdatedAt(LocalDateTime.now());

        testReview2 = new Review();
        testReview2.setId(2L);
        testReview2.setUserName("Jane");
        testReview2.setRating(9.0);
        testReview2.setReviewText("Loved it!");
        testReview2.setCreatedAt(LocalDateTime.now());
        testReview2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getReviewsByMovie_ShouldReturnListOfReviews() throws Exception {
        List<Review> reviews = Arrays.asList(testReview, testReview2);
        when(reviewService.getReviewsByMovie(1L)).thenReturn(reviews);

        mockMvc.perform(get("/movies/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userName").value("John"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userName").value("Jane"));

        verify(reviewService, times(1)).getReviewsByMovie(1L);
    }

    @Test
    void getReview_ShouldReturnReview() throws Exception {
        when(reviewService.getReviewByMovieAndReviewId(1L, 1L)).thenReturn(testReview);

        mockMvc.perform(get("/movies/1/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("John"));

        verify(reviewService, times(1)).getReviewByMovieAndReviewId(1L, 1L);
    }

    @Test
    void getReview_WhenNotExists_ShouldReturn404() throws Exception {
        when(reviewService.getReviewByMovieAndReviewId(1L, 999L)).thenThrow(new com.movieapi.exception.ReviewNotFoundException("Review not found"));

        mockMvc.perform(get("/movies/1/reviews/999"))
                .andExpect(status().isNotFound());

        verify(reviewService, times(1)).getReviewByMovieAndReviewId(1L, 999L);
    }

    @Test
    void addReview_ShouldReturnCreatedReview() throws Exception {
        Review newReview = new Review();
        newReview.setUserName("Alice");
        newReview.setRating(7.5);
        newReview.setReviewText("Good!");
        newReview.setCreatedAt(LocalDateTime.now());
        newReview.setUpdatedAt(LocalDateTime.now());

        Review savedReview = new Review();
        savedReview.setId(3L);
        savedReview.setUserName("Alice");
        savedReview.setRating(7.5);
        savedReview.setReviewText("Good!");
        savedReview.setCreatedAt(LocalDateTime.now());
        savedReview.setUpdatedAt(LocalDateTime.now());

        when(reviewService.addReview(eq(1L), any(com.movieapi.dto.ReviewRequest.class))).thenReturn(savedReview);

        mockMvc.perform(post("/movies/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReview)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.userName").value("Alice"));

        verify(reviewService, times(1)).addReview(eq(1L), any(com.movieapi.dto.ReviewRequest.class));
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() throws Exception {
        Review updatedReview = new Review();
        updatedReview.setId(1L);
        updatedReview.setUserName("John Updated");
        updatedReview.setRating(8.8);
        updatedReview.setReviewText("Even better!");
        updatedReview.setCreatedAt(LocalDateTime.now());
        updatedReview.setUpdatedAt(LocalDateTime.now());

        when(reviewService.updateReview(eq(1L), any(com.movieapi.dto.ReviewRequest.class))).thenReturn(updatedReview);

        mockMvc.perform(put("/movies/1/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedReview)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("John Updated"));

        verify(reviewService, times(1)).updateReview(eq(1L), any(com.movieapi.dto.ReviewRequest.class));
    }

    @Test
    void deleteReview_ShouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/movies/1/reviews/1"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(1L);
    }

    @Test
    void searchReviews_ShouldReturnPagedReviews() throws Exception {
        List<Review> reviews = Arrays.asList(testReview, testReview2);
        Page<Review> page = new PageImpl<>(reviews, PageRequest.of(0, 2), 2);
        when(reviewService.searchReviews(eq(null), eq(null), eq(null), eq(null), eq(null), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/reviews/search?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));

        verify(reviewService, times(1)).searchReviews(eq(null), eq(null), eq(null), eq(null), eq(null), any(PageRequest.class));
    }

    @Test
    void searchReviews_WithFilters_ShouldReturnFilteredReviews() throws Exception {
        List<Review> reviews = Arrays.asList(testReview2);
        Page<Review> page = new PageImpl<>(reviews, PageRequest.of(0, 1), 1);
        when(reviewService.searchReviews(eq(9.0), eq(null), eq("Jane"), eq(null), eq(null), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/reviews/search?minRating=9.0&userName=Jane&page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].userName").value("Jane"));

        verify(reviewService, times(1)).searchReviews(eq(9.0), eq(null), eq("Jane"), eq(null), eq(null), any(PageRequest.class));
    }
}
