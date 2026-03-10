package com.poc.lar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.lar.domain.Document;
import com.poc.lar.domain.EmergencyCard;
import com.poc.lar.domain.FamilyMember;
import com.poc.lar.dto.DocumentDTO;
import com.poc.lar.dto.DocumentRequest;
import com.poc.lar.dto.EmergencyCardDTO;
import com.poc.lar.dto.EmergencyCardRequest;
import com.poc.lar.repository.DocumentRepository;
import com.poc.lar.repository.EmergencyCardRepository;
import com.poc.lar.repository.FamilyMemberRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EmergencyCardRepository emergencyCardRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final ObjectMapper objectMapper;

    // ========== Documents ==========

    public List<DocumentDTO> findAll() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all documents for tenant: {}", tenantId);
        return documentRepository.findByTenantId(tenantId).stream()
                .map(this::toDocumentDTO)
                .toList();
    }

    public List<DocumentDTO> findByMember(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding documents for member: {} in tenant: {}", memberId, tenantId);
        return documentRepository.findByMemberId(tenantId, memberId).stream()
                .map(this::toDocumentDTO)
                .toList();
    }

    public List<DocumentDTO> findExpiring() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding expiring documents for tenant: {}", tenantId);
        return documentRepository.findExpiring(tenantId).stream()
                .map(this::toDocumentDTO)
                .toList();
    }

    public DocumentDTO create(DocumentRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating document for tenant: {}", tenantId);

        Document document = Document.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .memberId(req.memberId())
                .title(req.title())
                .description(req.description())
                .category(req.category())
                .fileUrl(req.fileUrl())
                .expiryDate(req.expiryDate())
                .reminderDaysBefore(req.reminderDaysBefore())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toDocumentDTO(documentRepository.save(document));
    }

    public DocumentDTO update(UUID id, DocumentRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating document: {} for tenant: {}", id, tenantId);

        Document document = documentRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + id));

        document.setMemberId(req.memberId());
        document.setTitle(req.title());
        document.setDescription(req.description());
        document.setCategory(req.category());
        document.setFileUrl(req.fileUrl());
        document.setExpiryDate(req.expiryDate());
        document.setReminderDaysBefore(req.reminderDaysBefore());
        document.setUpdatedAt(LocalDateTime.now());
        document.markAsExisting();

        return toDocumentDTO(documentRepository.save(document));
    }

    public void delete(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Deleting document: {} for tenant: {}", id, tenantId);

        Document document = documentRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + id));

        documentRepository.delete(document);
    }

    // ========== Emergency Cards ==========

    public Optional<EmergencyCardDTO> findEmergencyByMember(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding emergency card for member: {} in tenant: {}", memberId, tenantId);
        return emergencyCardRepository.findByMemberIdAndTenantId(memberId, tenantId)
                .map(this::toEmergencyDTO);
    }

    public EmergencyCardDTO createOrUpdateEmergency(EmergencyCardRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating or updating emergency card for member: {} in tenant: {}", req.memberId(), tenantId);

        Optional<EmergencyCard> existing = emergencyCardRepository.findByMemberIdAndTenantId(req.memberId(), tenantId);

        EmergencyCard card;
        if (existing.isPresent()) {
            // Update existing
            card = existing.get();
            card.setBloodType(req.bloodType());
            card.setAllergies(serializeList(req.allergies()));
            card.setMedicalConditions(serializeList(req.medicalConditions()));
            card.setCurrentMedications(serializeList(req.currentMedications()));
            card.setEmergencyContact1(req.emergencyContact1());
            card.setEmergencyPhone1(req.emergencyPhone1());
            card.setEmergencyContact2(req.emergencyContact2());
            card.setEmergencyPhone2(req.emergencyPhone2());
            card.setDoctorName(req.doctorName());
            card.setDoctorPhone(req.doctorPhone());
            card.setHealthInsurance(req.healthInsurance());
            card.setHealthInsuranceNumber(req.healthInsuranceNumber());
            card.setSpecialInstructions(req.specialInstructions());
            card.setUpdatedAt(LocalDateTime.now());
            card.markAsExisting();
        } else {
            // Create new
            card = EmergencyCard.builder()
                    .id(UUID.randomUUID())
                    .tenantId(tenantId)
                    .memberId(req.memberId())
                    .bloodType(req.bloodType())
                    .allergies(serializeList(req.allergies()))
                    .medicalConditions(serializeList(req.medicalConditions()))
                    .currentMedications(serializeList(req.currentMedications()))
                    .emergencyContact1(req.emergencyContact1())
                    .emergencyPhone1(req.emergencyPhone1())
                    .emergencyContact2(req.emergencyContact2())
                    .emergencyPhone2(req.emergencyPhone2())
                    .doctorName(req.doctorName())
                    .doctorPhone(req.doctorPhone())
                    .healthInsurance(req.healthInsurance())
                    .healthInsuranceNumber(req.healthInsuranceNumber())
                    .specialInstructions(req.specialInstructions())
                    .publicToken(UUID.randomUUID())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        return toEmergencyDTO(emergencyCardRepository.save(card));
    }

    /**
     * Public access by token - NO tenant check required.
     * Used for QR code emergency access.
     */
    public Optional<EmergencyCardDTO> findByPublicToken(UUID token) {
        log.debug("Finding emergency card by public token");
        return emergencyCardRepository.findByPublicToken(token)
                .map(this::toEmergencyDTO);
    }

    // ========== Private Helpers ==========

    private DocumentDTO toDocumentDTO(Document doc) {
        UUID tenantId = TenantContext.getCurrentTenant();
        String memberNickname = null;
        if (doc.getMemberId() != null) {
            memberNickname = familyMemberRepository.findByIdAndTenantId(doc.getMemberId(), tenantId)
                    .map(FamilyMember::getNickname)
                    .orElse(null);
        }

        return new DocumentDTO(
                doc.getId(),
                doc.getMemberId(),
                memberNickname,
                doc.getTitle(),
                doc.getDescription(),
                doc.getCategory(),
                doc.getFileUrl(),
                doc.getExpiryDate(),
                doc.getReminderDaysBefore(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }

    private EmergencyCardDTO toEmergencyDTO(EmergencyCard card) {
        String memberNickname = null;
        if (card.getMemberId() != null && card.getTenantId() != null) {
            memberNickname = familyMemberRepository.findByIdAndTenantId(card.getMemberId(), card.getTenantId())
                    .map(FamilyMember::getNickname)
                    .orElse(null);
        }

        return new EmergencyCardDTO(
                card.getId(),
                card.getMemberId(),
                memberNickname,
                card.getBloodType(),
                parseJsonList(card.getAllergies()),
                parseJsonList(card.getMedicalConditions()),
                parseJsonList(card.getCurrentMedications()),
                card.getEmergencyContact1(),
                card.getEmergencyPhone1(),
                card.getEmergencyContact2(),
                card.getEmergencyPhone2(),
                card.getDoctorName(),
                card.getDoctorPhone(),
                card.getHealthInsurance(),
                card.getHealthInsuranceNumber(),
                card.getSpecialInstructions(),
                card.getPublicToken(),
                card.getActive(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }

    private String serializeList(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Failed to serialize list: {}", list);
            return null;
        }
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse JSON list: {}", json);
            return Collections.emptyList();
        }
    }
}
