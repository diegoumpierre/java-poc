package com.poc.tenant.controller;

import com.poc.shared.security.SecurityContext;
import com.poc.tenant.model.response.CustomerResponse;
import com.poc.tenant.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CustomerPortalController {

    private final CustomerService customerService;

    // =========================================================================
    // ADMIN endpoints - manage customer accounts
    // =========================================================================

    @PostMapping("/api/tenants/customers/{id}/link-user")
    public ResponseEntity<CustomerResponse> linkCustomerToUser(
            @PathVariable UUID id,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(customerService.linkToUser(id, userId));
    }

    @GetMapping("/api/tenants/customers/with-accounts")
    public ResponseEntity<List<CustomerResponse>> findCustomersWithAccounts() {
        return ResponseEntity.ok(customerService.findWithAccounts());
    }

    // =========================================================================
    // PORTAL endpoints - customer self-service
    // =========================================================================

    @GetMapping("/api/tenants/portal/profile")
    public ResponseEntity<CustomerResponse> getMyProfile() {
        if (!SecurityContext.isCustomer()) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(customerService.getByCurrentUser());
    }
}
