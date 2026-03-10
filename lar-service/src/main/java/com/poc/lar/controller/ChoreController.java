package com.poc.lar.controller;

import com.poc.lar.dto.ChoreCompleteRequest;
import com.poc.lar.dto.ChoreDTO;
import com.poc.lar.dto.ChoreLogDTO;
import com.poc.lar.dto.ChoreRequest;
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
@RequestMapping("/api/lar/chores")
@RequiredArgsConstructor
public class ChoreController {

    private final HouseService houseService;

    @GetMapping
    public ResponseEntity<List<ChoreDTO>> findAll() {
        return ResponseEntity.ok(houseService.findAllChores());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ChoreDTO> create(@Valid @RequestBody ChoreRequest request) {
        ChoreDTO created = houseService.createChore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ChoreDTO> update(@PathVariable UUID id, @Valid @RequestBody ChoreRequest request) {
        return ResponseEntity.ok(houseService.updateChore(id, request));
    }

    @GetMapping("/today")
    public ResponseEntity<List<ChoreDTO>> findToday() {
        return ResponseEntity.ok(houseService.findTodayChores());
    }

    @PostMapping("/{id}/complete")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ChoreLogDTO> complete(@PathVariable UUID id, @RequestBody ChoreCompleteRequest request) {
        ChoreLogDTO log = houseService.completeChore(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(log);
    }

    @PostMapping("/{id}/verify")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ChoreLogDTO> verify(@PathVariable UUID id) {
        return ResponseEntity.ok(houseService.verifyChore(id));
    }

    @GetMapping("/log/{memberId}")
    public ResponseEntity<List<ChoreLogDTO>> findLog(@PathVariable UUID memberId) {
        return ResponseEntity.ok(houseService.findChoreLog(memberId));
    }
}
