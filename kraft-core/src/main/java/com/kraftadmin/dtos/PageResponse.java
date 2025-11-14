package com.kraftadmin.dtos;

import com.kraftadmin.database.DbObjectSchema;

import java.util.List;

public class PageResponse {
    private final List<DbObjectSchema> content;
    private final int number;
    private final long totalElements;
    private final int totalPages;

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
