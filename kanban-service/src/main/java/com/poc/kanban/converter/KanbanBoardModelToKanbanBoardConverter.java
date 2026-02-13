package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanBoard;
import com.poc.kanban.domain.KanbanList;
import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.model.KanbanListModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KanbanBoardModelToKanbanBoardConverter implements Converter<KanbanBoardModel, KanbanBoard> {

    private final KanbanListModelToKanbanListConverter listConverter;

    public KanbanBoardModelToKanbanBoardConverter(KanbanListModelToKanbanListConverter listConverter) {
        this.listConverter = listConverter;
    }

    @Override
    public @Nullable KanbanBoard convert(KanbanBoardModel source) {
        boolean isNewEntity = source.getId() == null;
        return KanbanBoard.builder()
                .id(isNewEntity ? UUID.randomUUID() : source.getId())
                .title(source.getTitle())
                .boardCode(source.getBoardCode())
                .userId(source.getUserId())
                .tenantId(source.getTenantId())
                .lists(convertLists(source.getLists()))
                .isNew(isNewEntity)
                .build();
    }

    private List<KanbanList> convertLists(List<KanbanListModel> lists) {
        if (lists == null || lists.isEmpty()) {
            return new ArrayList<>();
        }
        return lists.stream()
                .map(listConverter::convert)
                .collect(Collectors.toList());
    }
}
