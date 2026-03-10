package com.poc.lar.controller;

import com.poc.lar.dto.RewardDTO;
import com.poc.lar.dto.RewardRedemptionDTO;
import com.poc.lar.dto.RewardRequest;
import com.poc.lar.service.HouseService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final HouseService houseService;

    @GetMapping
    public ResponseEntity<List<RewardDTO>> findAll() {
        return ResponseEntity.ok(houseService.findAllRewards());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<RewardDTO> create(@Valid @RequestBody RewardRequest request) {
        RewardDTO created = houseService.createReward(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/redeem")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<RewardRedemptionDTO> redeem(@PathVariable UUID id) {
        RewardRedemptionDTO redemption = houseService.redeemReward(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(redemption);
    }

    @PostMapping("/redemptions/{id}/approve")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<RewardRedemptionDTO> approveRedemption(@PathVariable UUID id) {
        return ResponseEntity.ok(houseService.approveRedemption(id));
    }
}
