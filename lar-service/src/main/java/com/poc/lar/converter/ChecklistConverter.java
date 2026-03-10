package com.poc.lar.converter;

import com.poc.lar.domain.ChecklistItem;
import com.poc.lar.domain.ChecklistResponse;
import com.poc.lar.domain.ChecklistResponseItem;
import com.poc.lar.domain.ChecklistTemplate;
import com.poc.lar.dto.ChecklistItemDTO;
import com.poc.lar.dto.ChecklistResponseDTO;
import com.poc.lar.dto.ChecklistResponseItemDTO;
import com.poc.lar.dto.ChecklistTemplateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistConverter {

    public ChecklistTemplateDTO toTemplateDTO(ChecklistTemplate template, List<ChecklistItem> items) {
        List<ChecklistItemDTO> itemDTOs = items != null
            ? items.stream().map(this::toItemDTO).toList()
            : Collections.emptyList();

        return new ChecklistTemplateDTO(
            template.getId(),
            template.getName(),
            template.getType(),
            template.getActive(),
            itemDTOs,
            template.getCreatedAt(),
            template.getUpdatedAt()
        );
    }

    public ChecklistResponseDTO toResponseDTO(ChecklistResponse response, List<ChecklistResponseItem> responseItems, List<ChecklistItem> checklistItems) {
        Map<java.util.UUID, String> itemDescriptions = checklistItems != null
            ? checklistItems.stream().collect(Collectors.toMap(ChecklistItem::getId, ChecklistItem::getDescription))
            : Collections.emptyMap();

        List<ChecklistResponseItemDTO> itemDTOs = responseItems != null
            ? responseItems.stream().map(ri -> toResponseItemDTO(ri, itemDescriptions.get(ri.getItemId()))).toList()
            : Collections.emptyList();

        return new ChecklistResponseDTO(
            response.getId(),
            response.getTemplateId(),
            response.getOutingId(),
            response.getMemberId(),
            response.getAllPassed(),
            response.getCompletedAt(),
            itemDTOs
        );
    }

    private ChecklistItemDTO toItemDTO(ChecklistItem item) {
        return new ChecklistItemDTO(
            item.getId(),
            item.getDescription(),
            item.getOrderIndex(),
            item.getRequired(),
            item.getRequiresPhoto()
        );
    }

    private ChecklistResponseItemDTO toResponseItemDTO(ChecklistResponseItem item, String description) {
        return new ChecklistResponseItemDTO(
            item.getId(),
            item.getItemId(),
            description,
            item.getChecked(),
            item.getPhotoUrl(),
            item.getNote()
        );
    }
}
