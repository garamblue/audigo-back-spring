package com.audigo.audigo_back.dto.object;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pagination(
    @JsonProperty("current_page")
    int currentPage,
    
    @JsonProperty("count_per_page")
    int countPerPage,
    
    @JsonProperty("has_previous_page")
    boolean hasPreviousPage,
    
    @JsonProperty("has_next_page")
    boolean hasNextPage,
    
    @JsonProperty("total_list")
    long totalList,
    
    @JsonProperty("total_page")
    int totalPage
) {
    public static Pagination of(int currentPage, int countPerPage, long totalElements) {
        int totalPage = (int) Math.ceil((double) totalElements / countPerPage);
        boolean hasPreviousPage = currentPage > 1;
        boolean hasNextPage = currentPage < totalPage;
        
        return new Pagination(
            currentPage,
            countPerPage,
            hasPreviousPage,
            hasNextPage,
            totalElements,
            totalPage
        );
    }
}