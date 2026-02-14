package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanCard;
import com.poc.kanban.domain.KanbanComment;
import com.poc.kanban.domain.KanbanSubTask;
import com.poc.kanban.model.*;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KanbanCardModelToKanbanCardConverter implements Converter<KanbanCardModel, KanbanCard> {

    private final KanbanSubTaskModelToKanbanSubTaskConverter subTaskConverter;
    private final KanbanCommentModelToKanbanCommentConverter commentConverter;

    public KanbanCardModelToKanbanCardConverter(
            KanbanSubTaskModelToKanbanSubTaskConverter subTaskConverter,
            KanbanCommentModelToKanbanCommentConverter commentConverter) {
        this.subTaskConverter = subTaskConverter;
        this.commentConverter = commentConverter;
    }

    @Override
    public @Nullable KanbanCard convert(KanbanCardModel source) {
        KanbanCard.KanbanCardBuilder builder = KanbanCard.builder()
                .id(source.getId() != null ? source.getId() : UUID.randomUUID())
                .title(source.getTitle())
                .description(source.getDescription())
                .startDate(source.getStartDate())
                .dueDate(source.getDueDate())
                .completed(source.getCompleted() != null ? source.getCompleted() : false)
                .progress(source.getProgress() != null ? source.getProgress() : 0)
                .position(source.getPosition())
                .attachments(source.getAttachments() != null ? source.getAttachments() : 0);

        // Convert priority
        if (source.getPriority() != null) {
            builder.priorityColor(source.getPriority().getColor());
            builder.priorityTitle(source.getPriority().getTitle());
        }

        // Convert assignee
        if (source.getAssignee() != null && source.getAssignee().getUserId() != null) {
            builder.assigneeUserId(source.getAssignee().getUserId());
        }

        // Convert comments
        if (source.getComments() != null && !source.getComments().isEmpty()) {
            List<KanbanComment> comments = source.getComments().stream()
                    .map(commentConverter::convert)
                    .collect(Collectors.toList());
            builder.comments(comments);
        } else {
            builder.comments(new ArrayList<>());
        }

        // Convert subtasks
        if (source.getSubTasks() != null && !source.getSubTasks().isEmpty()) {
            List<KanbanSubTask> subTasks = source.getSubTasks().stream()
                    .map(subTaskConverter::convert)
                    .collect(Collectors.toList());
            builder.subTasks(subTasks);
        } else {
            builder.subTasks(new ArrayList<>());
        }

        return builder.build();
    }
}
