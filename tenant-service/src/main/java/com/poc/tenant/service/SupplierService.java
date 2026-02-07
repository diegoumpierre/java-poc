package com.poc.tenant.service;

import com.poc.shared.tenant.TenantAware;
import com.poc.shared.tenant.TenantContext;
import com.poc.tenant.domain.Supplier;
import com.poc.tenant.exception.ResourceNotFoundException;
import com.poc.tenant.model.request.SupplierRequest;
import com.poc.tenant.model.response.SupplierResponse;
import com.poc.tenant.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@TenantAware
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<SupplierResponse> findAll() {
        UUID tenantId = TenantContext.getCurrentTenant();
        return supplierRepository.findByTenantIdAndActiveTrue(tenantId).stream()
                .map(SupplierResponse::from)
                .collect(Collectors.toList());
    }

    public SupplierResponse findById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        Supplier supplier = supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        return SupplierResponse.from(supplier);
    }

    public List<SupplierResponse> findByCategory(String category) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return supplierRepository.findByCategoryAndTenantId(category, tenantId).stream()
                .map(SupplierResponse::from)
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> search(String query) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return supplierRepository.searchByTenantId(tenantId, query).stream()
                .map(SupplierResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        UUID tenantId = TenantContext.getCurrentTenant();

        Supplier supplier = Supplier.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .document(request.getDocument())
                .category(request.getCategory())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .notes(request.getNotes())
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        supplier = supplierRepository.save(supplier);
        log.info("Created supplier: {} for tenant {}", supplier.getName(), tenantId);
        return SupplierResponse.from(supplier);
    }

    @Transactional
    public SupplierResponse update(UUID id, SupplierRequest request) {
        UUID tenantId = TenantContext.getCurrentTenant();

        Supplier supplier = supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));

        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setDocument(request.getDocument());
        supplier.setCategory(request.getCategory());
        supplier.setAddress(request.getAddress());
        supplier.setCity(request.getCity());
        supplier.setState(request.getState());
        supplier.setNotes(request.getNotes());
        supplier.setUpdatedAt(Instant.now());
        supplier.markNotNew();

        supplier = supplierRepository.save(supplier);
        log.info("Updated supplier: {} for tenant {}", supplier.getName(), tenantId);
        return SupplierResponse.from(supplier);
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();

        Supplier supplier = supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));

        supplier.setActive(false);
        supplier.setUpdatedAt(Instant.now());
        supplier.markNotNew();

        supplierRepository.save(supplier);
        log.info("Soft-deleted supplier: {} for tenant {}", supplier.getName(), tenantId);
    }
}
