package com.movieapi.dto;

import com.movieapi.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response DTO for paginated review search results.
 * Provides pagination metadata along with the actual review data.
 */
@Schema(description = "Paginated response for review search results")
public class PagedReviewsResponse {
    @Schema(description = "List of reviews on current page")
    private List<Review> content;
    @Schema(description = "Current page number (0-based)", example = "0")
    private int number;
    @Schema(description = "Number of items per page", example = "20")
    private int size;
    @Schema(description = "Number of items on current page", example = "15")
    private int numberOfElements;
    @Schema(description = "Total number of items across all pages", example = "150")
    private long totalElements;
    @Schema(description = "Total number of pages", example = "8")
    private int totalPages;
    @Schema(description = "Whether this is the first page")
    private boolean first;
    @Schema(description = "Whether this is the last page")
    private boolean last;
    @Schema(description = "Whether there is a previous page")
    private boolean hasPrevious;
    @Schema(description = "Whether there is a next page")
    private boolean hasNext;

    public PagedReviewsResponse() {}

    public PagedReviewsResponse(List<Review> content, int number, int size, int numberOfElements, long totalElements, int totalPages, boolean first, boolean last, boolean hasPrevious, boolean hasNext) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.numberOfElements = numberOfElements;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }

    public List<Review> getContent() { return content; }
    public void setContent(List<Review> content) { this.content = content; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public int getNumberOfElements() { return numberOfElements; }
    public void setNumberOfElements(int numberOfElements) { this.numberOfElements = numberOfElements; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }
    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
}
