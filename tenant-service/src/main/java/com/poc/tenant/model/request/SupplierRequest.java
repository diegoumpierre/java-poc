package com.poc.tenant.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String email;
    private String phone;
    private String document;
    private String category;
    private String address;
    private String city;
    private String state;
    private String notes;
}
