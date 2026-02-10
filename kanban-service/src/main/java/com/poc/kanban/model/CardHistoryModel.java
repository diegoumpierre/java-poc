package com.poc.kanban.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardHistoryModel {

    private UUID id;
    private String entityType;
    private UUID entityId;
    private UUID changedBy;
    private String changedByName;
    private Instant changedAt;
    private String changeType;
    private List<FieldChange> changes;
    private String comment;
    private Object snapshot;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldChange {
        private String field;
        private String fieldLabel;
        private Object oldValue;
        private Object newValue;
    }
}
