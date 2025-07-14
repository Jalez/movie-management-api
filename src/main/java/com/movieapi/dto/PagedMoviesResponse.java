package com.movieapi.dto;

import com.movieapi.entity.Movie;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response DTO for paginated movie search results.
 * Provides pagination metadata along with the actual movie data.
 */
@Schema(description = "Paginated response for movie search results")
public class PagedMoviesResponse {
    
    @Schema(description = "List of movies on current page")
    private List<Movie> content;

    @Schema(description = "Pageable information")
    private PageableInfo pageable;

    @Schema(description = "Whether this is the last page")
    private boolean last;

    @Schema(description = "Total number of pages")
    private int totalPages;

    @Schema(description = "Total number of items across all pages")
    private long totalElements;

    @Schema(description = "Page size")
    private int size;

    @Schema(description = "Current page number (0-based)")
    private int number;

    @Schema(description = "Sort information")
    private SortInfo sort;

    @Schema(description = "Whether this is the first page")
    private boolean first;

    @Schema(description = "Whether there is a previous page")
    private boolean hasPrevious;

    @Schema(description = "Whether there is a next page")
    private boolean hasNext;

    @Schema(description = "Number of items on current page")
    private int numberOfElements;

    @Schema(description = "Whether the page is empty")
    private boolean empty;

    // Inner classes for pageable and sort
    public static class PageableInfo {
        private int pageNumber;
        private int pageSize;
        private SortInfo sort;
        private long offset;
        private boolean paged;
        private boolean unpaged;

        // Getters and setters
        public int getPageNumber() { return pageNumber; }
        public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        public SortInfo getSort() { return sort; }
        public void setSort(SortInfo sort) { this.sort = sort; }
        public long getOffset() { return offset; }
        public void setOffset(long offset) { this.offset = offset; }
        public boolean isPaged() { return paged; }
        public void setPaged(boolean paged) { this.paged = paged; }
        public boolean isUnpaged() { return unpaged; }
        public void setUnpaged(boolean unpaged) { this.unpaged = unpaged; }
    }

    public static class SortInfo {
        private boolean empty;
        private boolean sorted;
        private boolean unsorted;

        // Getters and setters
        public boolean isEmpty() { return empty; }
        public void setEmpty(boolean empty) { this.empty = empty; }
        public boolean isSorted() { return sorted; }
        public void setSorted(boolean sorted) { this.sorted = sorted; }
        public boolean isUnsorted() { return unsorted; }
        public void setUnsorted(boolean unsorted) { this.unsorted = unsorted; }
    }
    
    // Constructors
    public PagedMoviesResponse() {}

    // Getters and setters
    public List<Movie> getContent() { return content; }
    public void setContent(List<Movie> content) { this.content = content; }

    public PageableInfo getPageable() { return pageable; }
    public void setPageable(PageableInfo pageable) { this.pageable = pageable; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public SortInfo getSort() { return sort; }
    public void setSort(SortInfo sort) { this.sort = sort; }

    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }

    public int getNumberOfElements() { return numberOfElements; }
    public void setNumberOfElements(int numberOfElements) { this.numberOfElements = numberOfElements; }

    public boolean isEmpty() { return empty; }
    public void setEmpty(boolean empty) { this.empty = empty; }

    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
}
