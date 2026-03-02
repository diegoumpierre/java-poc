package com.poc.chat.controller;

import com.poc.chat.dto.chat.SearchResultDTO;
import com.poc.chat.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<SearchResultDTO>> search(
            @RequestParam("q") String query,
            @RequestParam(value = "channelId", required = false) Long channelId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(searchService.search(query.trim(), channelId, offset, limit));
    }
}
