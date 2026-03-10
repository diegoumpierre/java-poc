package com.poc.lar.controller;

import com.poc.lar.dto.GamificationSummaryDTO;
import com.poc.lar.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final HouseService houseService;

    @GetMapping("/points/{memberId}")
    public ResponseEntity<GamificationSummaryDTO> getPoints(@PathVariable UUID memberId) {
        return ResponseEntity.ok(houseService.getPoints(memberId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<GamificationSummaryDTO>> getLeaderboard() {
        return ResponseEntity.ok(houseService.getLeaderboard());
    }
}
