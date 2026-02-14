package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanSubTask;
import com.poc.kanban.model.KanbanAssigneeModel;
import com.poc.kanban.model.KanbanSubTaskModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class KanbanSubTaskToKanbanSubTaskModelConverter implements Converter<KanbanSubTask, KanbanSubTaskModel> {

    @Override
    public @Nullable KanbanSubTaskModel convert(KanbanSubTask source) {
        KanbanAssigneeModel assignee = null;
        if (source.getAssigneeUserId() != null) {
            assignee = KanbanAssigneeModel.builder()
                    .userId(source.getAssigneeUserId())
                    .build();
        }

        return KanbanSubTaskModel.builder()
                .id(source.getId())
                .text(source.getText())
                .completed(source.getCompleted())
                .position(source.getPosition())
                .assignee(assignee)
                .dueDate(source.getDueDate())
                .build();
    }
}
