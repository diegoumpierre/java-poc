package com.poc.tenant.model.response;

import com.poc.tenant.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private UUID id;
    private UUID userId;
    private String name;
    private String email;
    private String company;
    private String countryName;
    private String countryCode;
    private String city;
    private String state;
    private String website;
    private String bio;
    private String avatar;
    private String nickname;
    private LocalDate joinDate;
    private String status;
    private Integer activity;
    private BigDecimal balance;
    private Boolean verified;
    private BigDecimal amount;
    private BigDecimal price;
    private Integer rating;
    private String image;
    private String representativeName;
    private String representativeImage;
    private UUID tenantId;
    private String externalId;
    private String kind;
    private String metadata;
    private String phone;
    private Instant createdAt;
    private Instant updatedAt;

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .userId(customer.getUserId())
                .name(customer.getName())
                .email(customer.getEmail())
                .company(customer.getCompany())
                .countryName(customer.getCountryName())
                .countryCode(customer.getCountryCode())
                .city(customer.getCity())
                .state(customer.getState())
                .website(customer.getWebsite())
                .bio(customer.getBio())
                .avatar(customer.getAvatar())
                .nickname(customer.getNickname())
                .joinDate(customer.getJoinDate())
                .status(customer.getStatus())
                .activity(customer.getActivity())
                .balance(customer.getBalance())
                .verified(customer.getVerified())
                .amount(customer.getAmount())
                .price(customer.getPrice())
                .rating(customer.getRating())
                .image(customer.getImage())
                .representativeName(customer.getRepresentativeName())
                .representativeImage(customer.getRepresentativeImage())
                .tenantId(customer.getTenantId())
                .externalId(customer.getExternalId())
                .kind(customer.getKind())
                .metadata(customer.getMetadata())
                .phone(customer.getPhone())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
