package com.poc.lar.service;

import com.poc.lar.converter.ShoppingConverter;
import com.poc.lar.domain.PantryItem;
import com.poc.lar.domain.ShoppingItem;
import com.poc.lar.domain.ShoppingList;
import com.poc.lar.dto.PantryItemDTO;
import com.poc.lar.dto.PantryItemRequest;
import com.poc.lar.dto.ShoppingItemDTO;
import com.poc.lar.dto.ShoppingItemRequest;
import com.poc.lar.dto.ShoppingListDTO;
import com.poc.lar.dto.ShoppingListRequest;
import com.poc.lar.repository.PantryItemRepository;
import com.poc.lar.repository.ShoppingItemRepository;
import com.poc.lar.repository.ShoppingListRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingService {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final PantryItemRepository pantryItemRepository;
    private final ShoppingConverter shoppingConverter;

    // ========== Shopping Lists ==========

    public List<ShoppingListDTO> findAllLists() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all shopping lists for tenant: {}", tenantId);
        return shoppingConverter.toDTOList(shoppingListRepository.findByTenantId(tenantId));
    }

    public Optional<ShoppingListDTO> findListById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding shopping list by id: {} for tenant: {}", id, tenantId);
        return shoppingListRepository.findByIdAndTenantId(id, tenantId)
                .map(list -> {
                    List<ShoppingItem> items = shoppingItemRepository.findByListId(list.getId());
                    int itemCount = items.size();
                    int checkedCount = (int) items.stream().filter(i -> Boolean.TRUE.equals(i.getChecked())).count();
                    return shoppingConverter.toDTO(list, itemCount, checkedCount);
                });
    }

    public ShoppingListDTO createList(ShoppingListRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Creating shopping list for tenant: {}", tenantId);

        ShoppingList list = ShoppingList.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(req.name())
                .status("ACTIVE")
                .createdBy(userId)
                .assignedTo(req.assignedTo())
                .budgetCents(req.budgetCents())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return shoppingConverter.toDTO(shoppingListRepository.save(list));
    }

    public ShoppingListDTO updateList(UUID id, ShoppingListRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating shopping list: {} for tenant: {}", id, tenantId);

        ShoppingList list = shoppingListRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Shopping list not found: " + id));

        list.setName(req.name());
        list.setAssignedTo(req.assignedTo());
        list.setBudgetCents(req.budgetCents());
        list.setUpdatedAt(LocalDateTime.now());
        list.markAsExisting();

        return shoppingConverter.toDTO(shoppingListRepository.save(list));
    }

    public ShoppingListDTO completeList(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Completing shopping list: {} for tenant: {}", id, tenantId);

        ShoppingList list = shoppingListRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Shopping list not found: " + id));

        // Calculate actual total from checked items
        List<ShoppingItem> items = shoppingItemRepository.findByListId(id);
        int actualTotal = items.stream()
                .filter(i -> Boolean.TRUE.equals(i.getChecked()))
                .mapToInt(i -> i.getActualPriceCents() != null ? i.getActualPriceCents() : i.getEstimatedPriceCents() != null ? i.getEstimatedPriceCents() : 0)
                .sum();

        list.setStatus("COMPLETED");
        list.setActualTotalCents(actualTotal);
        list.setCompletedAt(LocalDateTime.now());
        list.setUpdatedAt(LocalDateTime.now());
        list.markAsExisting();

        return shoppingConverter.toDTO(shoppingListRepository.save(list));
    }

    // ========== Shopping Items ==========

    public List<ShoppingItemDTO> findItemsByList(UUID listId) {
        log.debug("Finding items for shopping list: {}", listId);
        return shoppingConverter.toItemDTOList(shoppingItemRepository.findByListId(listId));
    }

    public ShoppingItemDTO addItem(UUID listId, ShoppingItemRequest req) {
        UUID userId = TenantContext.getCurrentUser();
        log.info("Adding item to shopping list: {}", listId);

        ShoppingItem item = ShoppingItem.builder()
                .id(UUID.randomUUID())
                .listId(listId)
                .name(req.name())
                .quantity(req.quantity())
                .unit(req.unit())
                .category(req.category())
                .estimatedPriceCents(req.estimatedPriceCents())
                .checked(false)
                .addedBy(userId)
                .recurring(req.recurring() != null ? req.recurring() : false)
                .notes(req.notes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return shoppingConverter.toDTO(shoppingItemRepository.save(item));
    }

    public ShoppingItemDTO updateItem(UUID id, ShoppingItemRequest req) {
        log.info("Updating shopping item: {}", id);

        ShoppingItem item = shoppingItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shopping item not found: " + id));

        item.setName(req.name());
        item.setQuantity(req.quantity());
        item.setUnit(req.unit());
        item.setCategory(req.category());
        item.setEstimatedPriceCents(req.estimatedPriceCents());
        item.setRecurring(req.recurring());
        item.setNotes(req.notes());
        item.setUpdatedAt(LocalDateTime.now());
        item.markAsExisting();

        return shoppingConverter.toDTO(shoppingItemRepository.save(item));
    }

    public ShoppingItemDTO checkItem(UUID id) {
        UUID userId = TenantContext.getCurrentUser();
        log.info("Checking shopping item: {}", id);

        ShoppingItem item = shoppingItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shopping item not found: " + id));

        item.setChecked(true);
        item.setCheckedBy(userId);
        item.setUpdatedAt(LocalDateTime.now());
        item.markAsExisting();

        return shoppingConverter.toDTO(shoppingItemRepository.save(item));
    }

    // ========== Pantry ==========

    public List<PantryItemDTO> findAllPantry() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all pantry items for tenant: {}", tenantId);
        return shoppingConverter.toPantryDTOList(pantryItemRepository.findByTenantId(tenantId));
    }

    public List<PantryItemDTO> findLowPantry() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding low pantry items for tenant: {}", tenantId);
        return shoppingConverter.toPantryDTOList(pantryItemRepository.findLow(tenantId));
    }

    public PantryItemDTO createPantryItem(PantryItemRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating pantry item for tenant: {}", tenantId);

        PantryItem item = PantryItem.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(req.name())
                .quantity(req.quantity())
                .unit(req.unit())
                .category(req.category())
                .expiryDate(req.expiryDate())
                .status(req.status() != null ? req.status() : "STOCKED")
                .autoAddToList(req.autoAddToList() != null ? req.autoAddToList() : false)
                .preferredBrand(req.preferredBrand())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return shoppingConverter.toDTO(pantryItemRepository.save(item));
    }

    public PantryItemDTO updatePantryItem(UUID id, PantryItemRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating pantry item: {} for tenant: {}", id, tenantId);

        PantryItem item = pantryItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Pantry item not found: " + id));

        item.setName(req.name());
        item.setQuantity(req.quantity());
        item.setUnit(req.unit());
        item.setCategory(req.category());
        item.setExpiryDate(req.expiryDate());
        item.setStatus(req.status());
        item.setAutoAddToList(req.autoAddToList());
        item.setPreferredBrand(req.preferredBrand());
        item.setUpdatedAt(LocalDateTime.now());
        item.markAsExisting();

        return shoppingConverter.toDTO(pantryItemRepository.save(item));
    }

    public ShoppingListDTO generateListFromPantry() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Generating shopping list from low pantry items for tenant: {}", tenantId);

        List<PantryItem> lowItems = pantryItemRepository.findLow(tenantId);

        if (lowItems.isEmpty()) {
            throw new RuntimeException("No low or empty pantry items to generate a list");
        }

        // Create a new shopping list
        ShoppingList list = ShoppingList.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name("Reposicao de Despensa")
                .status("ACTIVE")
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ShoppingList savedList = shoppingListRepository.save(list);

        // Add items from low pantry
        for (PantryItem pantryItem : lowItems) {
            ShoppingItem shopItem = ShoppingItem.builder()
                    .id(UUID.randomUUID())
                    .listId(savedList.getId())
                    .name(pantryItem.getPreferredBrand() != null
                            ? pantryItem.getName() + " (" + pantryItem.getPreferredBrand() + ")"
                            : pantryItem.getName())
                    .quantity(pantryItem.getQuantity() != null ? pantryItem.getQuantity() : BigDecimal.ONE)
                    .unit(pantryItem.getUnit())
                    .category(pantryItem.getCategory())
                    .checked(false)
                    .addedBy(userId)
                    .recurring(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            shoppingItemRepository.save(shopItem);
        }

        return shoppingConverter.toDTO(savedList, lowItems.size(), 0);
    }
}
