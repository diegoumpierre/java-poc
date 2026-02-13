package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanAcceptanceCriteria;
import com.poc.kanban.model.KanbanAcceptanceCriteriaModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class KanbanAcceptanceCriteriaToModelConverter implements Converter<KanbanAcceptanceCriteria, KanbanAcceptanceCriteriaModel> {

    @Override
    public @Nullable KanbanAcceptanceCriteriaModel convert(KanbanAcceptanceCriteria source) {
        return KanbanAcceptanceCriteriaModel.builder()
                .id(source.getId())
                .text(source.getText())
                .completed(source.getCompleted())
                .position(source.getPosition())
                .build();
    }
}
