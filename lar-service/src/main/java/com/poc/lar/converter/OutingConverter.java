package com.poc.lar.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.lar.domain.OutingRequest;
import com.poc.lar.dto.CompanionDTO;
import com.poc.lar.dto.OutingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutingConverter {

    private final ObjectMapper objectMapper;

    public OutingDTO toDTO(OutingRequest outing, String memberNickname) {
        return new OutingDTO(
            outing.getId(),
            outing.getMemberId(),
            memberNickname,
            outing.getEventName(),
            outing.getEventDate(),
            outing.getEventTime(),
            outing.getAddress(),
            outing.getAddressLat(),
            outing.getAddressLng(),
            outing.getLocationContactName(),
            outing.getLocationContactPhone(),
            outing.getDepartureTime(),
            outing.getReturnMethod(),
            outing.getReturnMethodDetail(),
            outing.getEstimatedReturnTime(),
            parseCompanions(outing.getCompanions()),
            outing.getStatus(),
            outing.getApprovedBy(),
            outing.getApprovedAt(),
            outing.getRejectionReason(),
            outing.getActualDeparture(),
            outing.getActualReturn(),
            outing.getParentNotes(),
            outing.getTeenNotes(),
            outing.getCreatedAt(),
            outing.getUpdatedAt()
        );
    }

    public OutingDTO toDTO(OutingRequest outing) {
        return toDTO(outing, null);
    }

    public List<OutingDTO> toDTOList(List<OutingRequest> outings) {
        return outings.stream().map(this::toDTO).toList();
    }

    public String serializeCompanions(List<CompanionDTO> companions) {
        if (companions == null || companions.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(companions);
        } catch (Exception e) {
            log.warn("Failed to serialize companions: {}", companions);
            return null;
        }
    }

    private List<CompanionDTO> parseCompanions(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<CompanionDTO>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse companions JSON: {}", json);
            return Collections.emptyList();
        }
    }
}
