package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanSubTask;
import com.poc.kanban.model.KanbanSubTaskModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KanbanSubTaskModelToKanbanSubTaskConverter implements Converter<KanbanSubTaskModel, KanbanSubTask> {

    @Override
    public @Nullable KanbanSubTask convert(KanbanSubTaskModel source) {
        UUID assigneeUserId = null;
        if (source.getAssignee() != null && source.getAssignee().getUserId() != null) {
            assigneeUserId = source.getAssignee().getUserId();
        }

        return KanbanSubTask.builder()
                .id(source.getId() != null ? source.getId() : UUID.randomUUID())
                .text(source.getText())
                .completed(source.getCompleted() != null ? source.getCompleted() : false)
                .position(source.getPosition())
                .assigneeUserId(assigneeUserId)
                .dueDate(source.getDueDate())
                .build();
    }
}
