package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanComment;
import com.poc.kanban.model.KanbanCommentModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KanbanCommentModelToKanbanCommentConverter implements Converter<KanbanCommentModel, KanbanComment> {

    @Override
    public @Nullable KanbanComment convert(KanbanCommentModel source) {
        return KanbanComment.builder()
                .id(source.getId() != null ? source.getId() : UUID.randomUUID())
                .userId(source.getUserId())
                .text(source.getText())
                .build();
    }
}
