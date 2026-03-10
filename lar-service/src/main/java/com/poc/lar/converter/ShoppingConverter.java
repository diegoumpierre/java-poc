package com.poc.lar.converter;

import com.poc.lar.domain.PantryItem;
import com.poc.lar.domain.ShoppingItem;
import com.poc.lar.domain.ShoppingList;
import com.poc.lar.dto.PantryItemDTO;
import com.poc.lar.dto.ShoppingItemDTO;
import com.poc.lar.dto.ShoppingListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShoppingConverter {

    // --- ShoppingList ---

    public ShoppingListDTO toDTO(ShoppingList list, Integer itemCount, Integer checkedCount) {
        return new ShoppingListDTO(
            list.getId(),
            list.getName(),
            list.getStatus(),
            list.getCreatedBy(),
            list.getAssignedTo(),
            list.getBudgetCents(),
            list.getActualTotalCents(),
            itemCount,
            checkedCount,
            list.getCompletedAt(),
            list.getCreatedAt(),
            list.getUpdatedAt()
        );
    }

    public ShoppingListDTO toDTO(ShoppingList list) {
        return toDTO(list, null, null);
    }

    public List<ShoppingListDTO> toDTOList(List<ShoppingList> lists) {
        return lists.stream().map(this::toDTO).toList();
    }

    // --- ShoppingItem ---

    public ShoppingItemDTO toDTO(ShoppingItem item) {
        return new ShoppingItemDTO(
            item.getId(),
            item.getListId(),
            item.getName(),
            item.getQuantity(),
            item.getUnit(),
            item.getCategory(),
            item.getEstimatedPriceCents(),
            item.getActualPriceCents(),
            item.getChecked(),
            item.getAddedBy(),
            item.getCheckedBy(),
            item.getRecurring(),
            item.getNotes(),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }

    public List<ShoppingItemDTO> toItemDTOList(List<ShoppingItem> items) {
        return items.stream().map(this::toDTO).toList();
    }

    // --- PantryItem ---

    public PantryItemDTO toDTO(PantryItem item) {
        return new PantryItemDTO(
            item.getId(),
            item.getName(),
            item.getQuantity(),
            item.getUnit(),
            item.getCategory(),
            item.getExpiryDate(),
            item.getStatus(),
            item.getAutoAddToList(),
            item.getPreferredBrand(),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }

    public List<PantryItemDTO> toPantryDTOList(List<PantryItem> items) {
        return items.stream().map(this::toDTO).toList();
    }
}
