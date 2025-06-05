package com.bowerzlabs.dtos;

import com.bowerzlabs.database.DbObjectSchema;

import java.util.List;

public class PageResponse {
    private List<DbObjectSchema> content;
    private int number;
    private long totalElements;
    private int totalPages;

    public PageResponse(List<DbObjectSchema> content, int number, long totalElements, int totalPages) {
        this.content = content;
        this.number = number;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "content=" + content +
                ", number=" + number +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                '}';
    }
}
