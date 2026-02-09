package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("KANB_BOARD_TYPES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardType {

    @Id
    @Column("CODE")
    private String code;

    @Column("NAME")
    private String name;

    @Column("PRODUCT_CODE")
    private String productCode;

    @Column("VISIBLE_IN_KANBAN")
    @Builder.Default
    private Boolean visibleInKanban = true;

    @Column("SINGLETON")
    @Builder.Default
    private Boolean singleton = false;

    @Column("SCOPE")
    @Builder.Default
    private String scope = "TENANT";

    @Column("AUTO_CREATE")
    @Builder.Default
    private Boolean autoCreate = false;

    @Column("NUMBER_PREFIX")
    private String numberPrefix;

    @Column("ALLOW_CUSTOM_LISTS")
    @Builder.Default
    private Boolean allowCustomLists = true;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
