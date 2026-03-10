package com.poc.lar.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.lar.domain.FamilyMember;
import com.poc.lar.dto.FamilyMemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FamilyMemberConverter {

    private final ObjectMapper objectMapper;

    public FamilyMemberDTO toDTO(FamilyMember member) {
        return new FamilyMemberDTO(
            member.getId(),
            member.getUserId(),
            member.getNickname(),
            member.getBirthDate(),
            member.getBloodType(),
            member.getRoleType(),
            member.getSchoolName(),
            member.getSchoolPhone(),
            member.getSchoolGrade(),
            member.getHealthInsurance(),
            member.getHealthInsuranceNumber(),
            parseJsonList(member.getAllergies()),
            parseJsonList(member.getMedicalConditions()),
            member.getEmergencyNotes(),
            member.getAvatarUrl(),
            member.getActive(),
            member.getCreatedAt(),
            member.getUpdatedAt()
        );
    }

    public List<FamilyMemberDTO> toDTOList(List<FamilyMember> members) {
        return members.stream().map(this::toDTO).toList();
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
