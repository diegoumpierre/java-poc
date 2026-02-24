package com.poc.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailPageResponse {

    private List<MailModel> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int size;

    public static MailPageResponse of(List<MailModel> content, long totalElements, int page, int size) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return MailPageResponse.builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(page)
                .size(size)
                .build();
    }
}
