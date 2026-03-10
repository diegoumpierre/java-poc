package com.poc.lar.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.lar.domain.Document;
import com.poc.lar.domain.EmergencyCard;
import com.poc.lar.dto.DocumentDTO;
import com.poc.lar.dto.EmergencyCardDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentConverter {

    private final ObjectMapper objectMapper;

    // --- Document ---

    public DocumentDTO toDTO(Document document, String memberNickname) {
        return new DocumentDTO(
            document.getId(),
            document.getMemberId(),
            memberNickname,
            document.getTitle(),
            document.getDescription(),
            document.getCategory(),
            document.getFileUrl(),
            document.getExpiryDate(),
            document.getReminderDaysBefore(),
            document.getCreatedAt(),
            document.getUpdatedAt()
        );
    }

    public DocumentDTO toDTO(Document document) {
        return toDTO(document, null);
    }

    public List<DocumentDTO> toDocumentDTOList(List<Document> documents) {
        return documents.stream().map(this::toDTO).toList();
    }

    // --- EmergencyCard ---

    public EmergencyCardDTO toDTO(EmergencyCard card, String memberNickname) {
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

    public EmergencyCardDTO toDTO(EmergencyCard card) {
        return toDTO(card, null);
    }

    public List<EmergencyCardDTO> toEmergencyCardDTOList(List<EmergencyCard> cards) {
        return cards.stream().map(this::toDTO).toList();
    }

    public String serializeList(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Failed to serialize list: {}", list);
            return null;
        }
    }

    // --- JSON helpers ---

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
