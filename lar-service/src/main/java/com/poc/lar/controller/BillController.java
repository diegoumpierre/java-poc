package com.poc.lar.controller;

import com.poc.lar.dto.BillPaymentDTO;
import com.poc.lar.dto.BillPaymentRequest;
import com.poc.lar.dto.HouseholdBillDTO;
import com.poc.lar.dto.HouseholdBillRequest;
import com.poc.lar.service.HouseService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/bills")
@RequiredArgsConstructor
public class BillController {

    private final HouseService houseService;

    @GetMapping
    public ResponseEntity<List<HouseholdBillDTO>> findAll() {
        return ResponseEntity.ok(houseService.findAllBills());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<HouseholdBillDTO> create(@Valid @RequestBody HouseholdBillRequest request) {
        HouseholdBillDTO created = houseService.createBill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<HouseholdBillDTO> update(@PathVariable UUID id, @Valid @RequestBody HouseholdBillRequest request) {
        return ResponseEntity.ok(houseService.updateBill(id, request));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<BillPaymentDTO>> findPayments(@PathVariable UUID id) {
        return ResponseEntity.ok(houseService.findPaymentsByBill(id));
    }

    @PostMapping("/{id}/pay")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<BillPaymentDTO> pay(@PathVariable UUID id, @Valid @RequestBody BillPaymentRequest request) {
        BillPaymentDTO payment = houseService.pay(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BillPaymentDTO>> findOverdue() {
        return ResponseEntity.ok(houseService.findOverdue());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Integer>> getSummary() {
        return ResponseEntity.ok(houseService.getSummary());
    }
}
