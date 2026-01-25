package com.poc.auth.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewAccessRequestRequest {

    @NotNull(message = "Approved status is required")
    private Boolean approved;

    @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
    private String rejectionReason;

    private List<UUID> roleIds;
}
