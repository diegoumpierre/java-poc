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
import java.util.stream.Collectors;

@Component
public class KanbanListToKanbanListModelConverter implements Converter<KanbanList, KanbanListModel> {

    private final KanbanCardToKanbanCardModelConverter cardConverter;

    public KanbanListToKanbanListModelConverter(KanbanCardToKanbanCardModelConverter cardConverter) {
        this.cardConverter = cardConverter;
    }

    @Override
    public @Nullable KanbanListModel convert(KanbanList source) {
        return KanbanListModel.builder()
                .id(source.getId())
                .listId(source.getId() != null ? String.valueOf(source.getId()) : null)
                .title(source.getTitle())
                .position(source.getPosition())
                .cards(convertCards(source.getCards()))
                .build();
    }

    private List<KanbanCardModel> convertCards(List<KanbanCard> cards) {
        if (cards == null || cards.isEmpty()) {
            return new ArrayList<>();
        }
        return cards.stream()
                .map(cardConverter::convert)
                .collect(Collectors.toList());
    }
}
