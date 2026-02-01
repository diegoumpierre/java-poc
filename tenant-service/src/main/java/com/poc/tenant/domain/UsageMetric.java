package com.poc.tenant.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table("TNT_MTR_USAGE_METRICS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageMetric implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("METRIC_TYPE")
    private String metricType;

    @Column("METRIC_VALUE")
    private BigDecimal metricValue;

    @Column("PERIOD_TYPE")
    private String periodType;

    @Column("PERIOD_START")
    private LocalDate periodStart;

    @Column("PERIOD_END")
    private LocalDate periodEnd;

    @Column("PRODUCT_ID")
    private UUID productId;

    @Column("METADATA")
    private String metadata;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
