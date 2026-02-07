package com.poc.tenant.service;

import com.poc.shared.tenant.TenantAware;
import com.poc.shared.tenant.TenantContext;
import com.poc.tenant.domain.Customer;
import com.poc.tenant.exception.ResourceNotFoundException;
import com.poc.tenant.model.response.CustomerResponse;
import com.poc.tenant.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@TenantAware
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerResponse> findAll() {
        UUID tenantId = TenantContext.getCurrentTenant();
        return customerRepository.findByTenantId(tenantId).stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    public CustomerResponse findById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        Customer customer = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return CustomerResponse.from(customer);
    }

    public List<CustomerResponse> findByStatus(String status) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return customerRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> search(String query) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return customerRepository.searchByTenantId(tenantId, query).stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    public CustomerResponse linkToUser(UUID customerId, UUID userId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        Customer customer = customerRepository.findByIdAndTenantId(customerId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        customer.setUserId(userId);
        customer.markNotNew();
        customerRepository.save(customer);
        log.info("Customer {} linked to user {} in tenant {}", customerId, userId, tenantId);
        return CustomerResponse.from(customer);
    }

    public CustomerResponse getByCurrentUser() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        Customer customer = customerRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for current user"));
        return CustomerResponse.from(customer);
    }

    public List<CustomerResponse> findWithAccounts() {
        UUID tenantId = TenantContext.getCurrentTenant();
        return customerRepository.findWithAccountsByTenantId(tenantId).stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    public CustomerResponse findByEmail(String email) {
        UUID tenantId = TenantContext.getCurrentTenant();
        Customer customer = customerRepository.findByTenantIdAndEmail(tenantId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
        return CustomerResponse.from(customer);
    }
}
