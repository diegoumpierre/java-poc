package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("KANB_BOARD_TYPE_FEATURES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardTypeFeature implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("BOARD_TYPE_CODE")
    private String boardTypeCode;

    @Column("FEATURE_CODE")
    private String featureCode;

    @Column("ENABLED")
    @Builder.Default
    private Boolean enabled = true;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
