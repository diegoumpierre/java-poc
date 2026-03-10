package com.poc.lar.service;

import com.poc.lar.converter.ChecklistConverter;
import com.poc.lar.domain.ChecklistItem;
import com.poc.lar.domain.ChecklistResponse;
import com.poc.lar.domain.ChecklistResponseItem;
import com.poc.lar.domain.ChecklistTemplate;
import com.poc.lar.domain.FamilyMember;
import com.poc.lar.dto.ChecklistItemResponseRequest;
import com.poc.lar.dto.ChecklistResponseDTO;
import com.poc.lar.dto.ChecklistSubmitRequest;
import com.poc.lar.dto.ChecklistTemplateDTO;
import com.poc.lar.dto.ChecklistTemplateRequest;
import com.poc.lar.repository.ChecklistItemRepository;
import com.poc.lar.repository.ChecklistResponseItemRepository;
import com.poc.lar.repository.ChecklistResponseRepository;
import com.poc.lar.repository.ChecklistTemplateRepository;
import com.poc.lar.repository.FamilyMemberRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChecklistService {

    private final ChecklistTemplateRepository checklistTemplateRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final ChecklistResponseRepository checklistResponseRepository;
    private final ChecklistResponseItemRepository checklistResponseItemRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final ChecklistConverter checklistConverter;

    public List<ChecklistTemplateDTO> findAll() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all checklist templates for tenant: {}", tenantId);
        List<ChecklistTemplate> templates = checklistTemplateRepository.findByTenantId(tenantId);
        return templates.stream()
                .map(template -> {
                    List<ChecklistItem> items = checklistItemRepository.findByTemplateId(template.getId());
                    return checklistConverter.toTemplateDTO(template, items);
                })
                .toList();
    }

    public Optional<ChecklistTemplateDTO> findById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding checklist template by id: {} for tenant: {}", id, tenantId);
        return checklistTemplateRepository.findByIdAndTenantId(id, tenantId)
                .map(template -> {
                    List<ChecklistItem> items = checklistItemRepository.findByTemplateId(template.getId());
                    return checklistConverter.toTemplateDTO(template, items);
                });
    }

    public ChecklistTemplateDTO create(ChecklistTemplateRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating checklist template for tenant: {}", tenantId);

        ChecklistTemplate template = ChecklistTemplate.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(req.name())
                .type(req.type())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ChecklistTemplate savedTemplate = checklistTemplateRepository.save(template);

        // Create items
        AtomicInteger orderIndex = new AtomicInteger(0);
        List<ChecklistItem> savedItems = List.of();
        if (req.items() != null && !req.items().isEmpty()) {
            savedItems = req.items().stream()
                    .map(itemReq -> {
                        ChecklistItem item = ChecklistItem.builder()
                                .id(UUID.randomUUID())
                                .templateId(savedTemplate.getId())
                                .description(itemReq.description())
                                .orderIndex(itemReq.orderIndex() != null ? itemReq.orderIndex() : orderIndex.getAndIncrement())
                                .required(itemReq.required() != null ? itemReq.required() : true)
                                .requiresPhoto(itemReq.requiresPhoto() != null ? itemReq.requiresPhoto() : false)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        return checklistItemRepository.save(item);
                    })
                    .toList();
        }

        return checklistConverter.toTemplateDTO(savedTemplate, savedItems);
    }

    public ChecklistTemplateDTO update(UUID id, ChecklistTemplateRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating checklist template: {} for tenant: {}", id, tenantId);

        ChecklistTemplate template = checklistTemplateRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Checklist template not found: " + id));

        template.setName(req.name());
        template.setType(req.type());
        template.setUpdatedAt(LocalDateTime.now());
        template.markAsExisting();

        ChecklistTemplate savedTemplate = checklistTemplateRepository.save(template);

        // Replace existing items: delete old ones, create new ones
        List<ChecklistItem> existingItems = checklistItemRepository.findByTemplateId(id);
        checklistItemRepository.deleteAll(existingItems);

        AtomicInteger orderIndex = new AtomicInteger(0);
        List<ChecklistItem> savedItems = List.of();
        if (req.items() != null && !req.items().isEmpty()) {
            savedItems = req.items().stream()
                    .map(itemReq -> {
                        ChecklistItem item = ChecklistItem.builder()
                                .id(UUID.randomUUID())
                                .templateId(savedTemplate.getId())
                                .description(itemReq.description())
                                .orderIndex(itemReq.orderIndex() != null ? itemReq.orderIndex() : orderIndex.getAndIncrement())
                                .required(itemReq.required() != null ? itemReq.required() : true)
                                .requiresPhoto(itemReq.requiresPhoto() != null ? itemReq.requiresPhoto() : false)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        return checklistItemRepository.save(item);
                    })
                    .toList();
        }

        return checklistConverter.toTemplateDTO(savedTemplate, savedItems);
    }

    public void delete(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Soft deleting checklist template: {} for tenant: {}", id, tenantId);

        ChecklistTemplate template = checklistTemplateRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Checklist template not found: " + id));

        template.setActive(false);
        template.setUpdatedAt(LocalDateTime.now());
        template.markAsExisting();
        checklistTemplateRepository.save(template);
    }

    public ChecklistResponseDTO submitResponse(UUID templateId, UUID outingId, ChecklistSubmitRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Submitting checklist response for template: {}, outing: {}", templateId, outingId);

        // Find the member for the current user
        FamilyMember member = familyMemberRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new RuntimeException("Family member not found for user: " + userId));

        // Determine if all items passed
        boolean allPassed = req.items() != null && req.items().stream()
                .allMatch(item -> Boolean.TRUE.equals(item.checked()));

        ChecklistResponse response = ChecklistResponse.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .templateId(templateId)
                .outingId(outingId)
                .memberId(member.getId())
                .allPassed(allPassed)
                .completedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        ChecklistResponse savedResponse = checklistResponseRepository.save(response);

        // Save response items
        List<ChecklistResponseItem> savedItems = List.of();
        if (req.items() != null && !req.items().isEmpty()) {
            savedItems = req.items().stream()
                    .map(itemReq -> {
                        ChecklistResponseItem responseItem = ChecklistResponseItem.builder()
                                .id(UUID.randomUUID())
                                .responseId(savedResponse.getId())
                                .itemId(itemReq.itemId())
                                .checked(itemReq.checked())
                                .photoUrl(itemReq.photoUrl())
                                .note(itemReq.note())
                                .createdAt(LocalDateTime.now())
                                .build();
                        return checklistResponseItemRepository.save(responseItem);
                    })
                    .toList();
        }

        List<ChecklistItem> checklistItems = checklistItemRepository.findByTemplateId(templateId);
        return checklistConverter.toResponseDTO(savedResponse, savedItems, checklistItems);
    }

    public List<ChecklistResponseDTO> findResponseByOuting(UUID outingId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding checklist responses for outing: {} in tenant: {}", outingId, tenantId);

        List<ChecklistResponse> responses = checklistResponseRepository.findByOutingId(outingId, tenantId);
        return responses.stream()
                .map(response -> {
                    List<ChecklistResponseItem> responseItems = checklistResponseItemRepository.findByResponseId(response.getId());
                    List<ChecklistItem> checklistItems = checklistItemRepository.findByTemplateId(response.getTemplateId());
                    return checklistConverter.toResponseDTO(response, responseItems, checklistItems);
                })
                .toList();
    }
}
