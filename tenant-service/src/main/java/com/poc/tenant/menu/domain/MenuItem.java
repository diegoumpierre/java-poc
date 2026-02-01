package com.poc.tenant.menu.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Table("TNT_MENU_ITEMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("PARENT_ID")
    private UUID parentId;

    @Column("MENU_KEY")
    private String menuKey;

    @Column("LABEL")
    private String label;

    @Column("ICON")
    private String icon;

    @Column("ROUTE")
    private String route;

    @Column("URL")
    private String url;

    @Column("TARGET")
    private String target;

    @Column("CATEGORY")
    @Builder.Default
    private String category = "AUTHENTICATED";

    @Column("FEATURE_CODES")
    private String featureCodes;

    @Column("ROLES")
    private String roles;

    @Column("PERMISSIONS")
    private String permissions;

    @Column("ORDER_INDEX")
    @Builder.Default
    private Integer orderIndex = 0;

    @Column("VISIBLE")
    @Builder.Default
    private Boolean visible = true;

    @Column("BADGE")
    private String badge;

    @Column("BADGE_CLASS")
    private String badgeClass;

    @Column("SEPARATOR")
    @Builder.Default
    private Boolean separator = false;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Transient
    private List<MenuItem> children;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
