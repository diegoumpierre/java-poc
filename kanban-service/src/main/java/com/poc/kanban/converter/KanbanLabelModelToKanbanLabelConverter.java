package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanLabel;
import com.poc.kanban.model.KanbanLabelModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class KanbanLabelModelToKanbanLabelConverter implements Converter<KanbanLabelModel, KanbanLabel> {

    @Override
    public KanbanLabel convert(KanbanLabelModel source) {
        if (source == null) {
            return null;
        }

        return KanbanLabel.builder()
                .id(source.getId() != null ? source.getId() : UUID.randomUUID())
                .name(source.getName())
                .color(source.getColor())
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt() : Instant.now())
                .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt() : Instant.now())
                .build();
    }
}
