package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanComment;
import com.poc.kanban.model.KanbanCommentModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class KanbanCommentToKanbanCommentModelConverter implements Converter<KanbanComment, KanbanCommentModel> {

    @Override
    public @Nullable KanbanCommentModel convert(KanbanComment source) {
        return KanbanCommentModel.builder()
                .id(source.getId())
                .userId(source.getUserId())
                .text(source.getText())
                .createdAt(source.getCreatedAt())
                .build();
    }
}
