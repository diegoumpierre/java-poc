package com.poc.tenant.model.response;

import com.poc.tenant.domain.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponse {

    private UUID id;
    private UUID tenantId;
    private String name;
    private String email;
    private String phone;
    private String document;
    private String category;
    private String address;
    private String city;
    private String state;
    private String notes;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public static SupplierResponse from(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .tenantId(supplier.getTenantId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .document(supplier.getDocument())
                .category(supplier.getCategory())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .state(supplier.getState())
                .notes(supplier.getNotes())
                .active(supplier.getActive())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}
