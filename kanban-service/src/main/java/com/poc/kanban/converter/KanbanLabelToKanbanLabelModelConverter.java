package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanLabel;
import com.poc.kanban.model.KanbanLabelModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class KanbanLabelToKanbanLabelModelConverter implements Converter<KanbanLabel, KanbanLabelModel> {

    @Override
    public KanbanLabelModel convert(KanbanLabel source) {
        if (source == null) {
            return null;
        }

        return KanbanLabelModel.builder()
                .id(source.getId())
                .name(source.getName())
                .color(source.getColor())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }
}
