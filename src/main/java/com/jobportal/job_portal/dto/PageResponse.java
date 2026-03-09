package com.jobportal.job_portal.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {

    private List<T> content;

    private int page;
    private int size;

    private long totalElements;
    private int totalPages;

}
