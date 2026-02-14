package com.poc.kanban.converter;

import com.poc.kanban.domain.KanbanBoard;
import com.poc.kanban.domain.KanbanLabel;
import com.poc.kanban.domain.KanbanList;
import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.model.KanbanLabelModel;
import com.poc.kanban.model.KanbanListModel;
import org.springframework.lang.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KanbanBoardToKanbanBoardModelConverter implements Converter<KanbanBoard, KanbanBoardModel> {

    private final KanbanListToKanbanListModelConverter listConverter;

    public KanbanBoardToKanbanBoardModelConverter(KanbanListToKanbanListModelConverter listConverter) {
        this.listConverter = listConverter;
    }

    @Override
    public @Nullable KanbanBoardModel convert(KanbanBoard source) {
        // Convert board labels to model
        List<KanbanLabelModel> boardLabels = new ArrayList<>();
        Map<UUID, KanbanLabel> labelMap = new java.util.HashMap<>();
        if (source.getLabels() != null) {
            for (KanbanLabel label : source.getLabels()) {
                labelMap.put(label.getId(), label);
                boardLabels.add(KanbanLabelModel.builder()
                        .id(label.getId())
                        .name(label.getName())
                        .color(label.getColor())
                        .createdAt(label.getCreatedAt())
                        .updatedAt(label.getUpdatedAt())
                        .build());
            }
        }

        KanbanBoardModel boardModel = KanbanBoardModel.builder()
                .id(source.getId())
                .title(source.getTitle())
                .boardCode(source.getBoardCode())
                .userId(source.getUserId())
                .tenantId(source.getTenantId())
                .lists(convertLists(source.getLists()))
                .labels(boardLabels)
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();

        // Set card codes and populate label details for all cards
        if (boardModel.getLists() != null) {
            boardModel.getLists().forEach(list -> {
                if (list.getCards() != null) {
                    list.getCards().forEach(card -> {
                        if (card.getId() != null) {
                            // Find the corresponding domain card to get cardNumber
                            source.getLists().stream()
                                    .flatMap(l -> l.getCards().stream())
                                    .filter(c -> c.getId().equals(card.getId()))
                                    .findFirst()
                                    .ifPresent(domainCard -> {
                                        if (domainCard.getCardNumber() != null && source.getBoardCode() != null) {
                                            String cardCode = String.format("%s-%04d",
                                                    source.getBoardCode(),
                                                    domainCard.getCardNumber());
                                            card.setCardCode(cardCode);
                                        }
                                    });

                            // Populate full label details from board labels
                            if (card.getLabels() != null && !card.getLabels().isEmpty()) {
                                List<KanbanLabelModel> fullLabels = card.getLabels().stream()
                                        .filter(labelModel -> labelModel.getId() != null && labelMap.containsKey(labelModel.getId()))
                                        .map(labelModel -> {
                                            KanbanLabel boardLabel = labelMap.get(labelModel.getId());
                                            return KanbanLabelModel.builder()
                                                    .id(boardLabel.getId())
                                                    .name(boardLabel.getName())
                                                    .color(boardLabel.getColor())
                                                    .createdAt(boardLabel.getCreatedAt())
                                                    .updatedAt(boardLabel.getUpdatedAt())
                                                    .build();
                                        })
                                        .collect(Collectors.toList());
                                card.setLabels(fullLabels);
                            }
                        }
                    });
                }
            });
        }

        return boardModel;
    }

    private List<KanbanListModel> convertLists(List<KanbanList> lists) {
        if (lists == null || lists.isEmpty()) {
            return new ArrayList<>();
        }
        return lists.stream()
                .map(listConverter::convert)
                .collect(Collectors.toList());
    }
}
