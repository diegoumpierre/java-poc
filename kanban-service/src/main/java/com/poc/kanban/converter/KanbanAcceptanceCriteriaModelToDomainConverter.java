package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanAcceptanceCriteria;
import com.poc.kanban.model.KanbanAcceptanceCriteriaModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KanbanAcceptanceCriteriaModelToDomainConverter implements Converter<KanbanAcceptanceCriteriaModel, KanbanAcceptanceCriteria> {

    @Override
    public @Nullable KanbanAcceptanceCriteria convert(KanbanAcceptanceCriteriaModel source) {
        return KanbanAcceptanceCriteria.builder()
                .id(source.getId() != null ? source.getId() : UUID.randomUUID())
                .text(source.getText())
                .completed(source.getCompleted() != null ? source.getCompleted() : false)
                .position(source.getPosition())
                .build();
    }
}
