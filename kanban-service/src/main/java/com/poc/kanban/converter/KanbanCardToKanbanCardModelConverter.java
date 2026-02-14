package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanCard;
import com.poc.kanban.model.KanbanAcceptanceCriteriaModel;
import com.poc.kanban.model.*;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KanbanCardToKanbanCardModelConverter implements Converter<KanbanCard, KanbanCardModel> {

    private final KanbanSubTaskToKanbanSubTaskModelConverter subTaskConverter;
    private final KanbanCommentToKanbanCommentModelConverter commentConverter;
    private final KanbanAcceptanceCriteriaToModelConverter acceptanceCriteriaConverter;

    public KanbanCardToKanbanCardModelConverter(
            KanbanSubTaskToKanbanSubTaskModelConverter subTaskConverter,
            KanbanCommentToKanbanCommentModelConverter commentConverter,
            KanbanAcceptanceCriteriaToModelConverter acceptanceCriteriaConverter) {
        this.subTaskConverter = subTaskConverter;
        this.commentConverter = commentConverter;
        this.acceptanceCriteriaConverter = acceptanceCriteriaConverter;
    }

    @Override
    public @Nullable KanbanCardModel convert(KanbanCard source) {
        KanbanCardModel.KanbanCardModelBuilder builder = KanbanCardModel.builder()
                .id(source.getId())
                .cardId(source.getId() != null ? String.valueOf(source.getId()) : null)
                .title(source.getTitle())
                .description(source.getDescription())
                .startDate(source.getStartDate())
                .dueDate(source.getDueDate())
                .completed(source.getCompleted())
                .progress(source.getProgress())
                .position(source.getPosition())
                .attachments(source.getAttachments());

        // Convert priority
        if (source.getPriorityColor() != null || source.getPriorityTitle() != null) {
            builder.priority(KanbanPriorityModel.builder()
                    .color(source.getPriorityColor())
                    .title(source.getPriorityTitle())
                    .build());
        }

        // Convert assignee
        if (source.getAssigneeUserId() != null) {
            builder.assignee(KanbanAssigneeModel.builder()
                    .userId(source.getAssigneeUserId())
                    .build());
        }

        // Convert comments - always return a list (empty if no comments)
        List<KanbanCommentModel> comments = new ArrayList<>();
        if (source.getComments() != null && !source.getComments().isEmpty()) {
            comments = source.getComments().stream()
                    .map(commentConverter::convert)
                    .collect(Collectors.toList());
        }
        builder.comments(comments);

        // Convert subtasks - always return a list (empty if no subtasks)
        List<KanbanSubTaskModel> subTasks = new ArrayList<>();
        if (source.getSubTasks() != null && !source.getSubTasks().isEmpty()) {
            subTasks = source.getSubTasks().stream()
                    .map(subTaskConverter::convert)
                    .collect(Collectors.toList());
        }
        builder.subTasks(subTasks);

        // Convert labels - always return a list (empty if no labels)
        List<KanbanLabelModel> labels = new ArrayList<>();
        if (source.getLabels() != null && !source.getLabels().isEmpty()) {
            labels = source.getLabels().stream()
                    .map(cardLabel -> KanbanLabelModel.builder()
                            .id(cardLabel.getLabelId())
                            .build())
                    .collect(Collectors.toList());
        }
        builder.labels(labels);

        // Convert acceptance criteria - always return a list (empty if no criteria)
        List<KanbanAcceptanceCriteriaModel> acceptanceCriteria = new ArrayList<>();
        if (source.getAcceptanceCriteria() != null && !source.getAcceptanceCriteria().isEmpty()) {
            acceptanceCriteria = source.getAcceptanceCriteria().stream()
                    .map(acceptanceCriteriaConverter::convert)
                    .collect(Collectors.toList());
        }
        builder.acceptanceCriteria(acceptanceCriteria);

        return builder.build();
    }
}
