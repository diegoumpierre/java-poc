package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanCard;
import com.poc.kanban.domain.KanbanList;
import com.poc.kanban.model.KanbanCardModel;
import com.poc.kanban.model.KanbanListModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KanbanListModelToKanbanListConverter implements Converter<KanbanListModel, KanbanList> {

    private final KanbanCardModelToKanbanCardConverter cardConverter;

    public KanbanListModelToKanbanListConverter(KanbanCardModelToKanbanCardConverter cardConverter) {
        this.cardConverter = cardConverter;
    }

    @Override
    public @Nullable KanbanList convert(KanbanListModel source) {
        return KanbanList.builder()
                .id(source.getId() != null ? source.getId() : UUID.randomUUID())
                .title(source.getTitle())
                .position(source.getPosition())
                .cards(convertCards(source.getCards()))
                .build();
    }

    private List<KanbanCard> convertCards(List<KanbanCardModel> cards) {
        if (cards == null || cards.isEmpty()) {
            return new ArrayList<>();
        }
        return cards.stream()
                .map(cardConverter::convert)
                .collect(Collectors.toList());
    }
}
