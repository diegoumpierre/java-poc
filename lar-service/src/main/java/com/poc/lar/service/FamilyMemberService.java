package com.poc.lar.service;

import com.poc.lar.converter.FamilyMemberConverter;
import com.poc.lar.domain.FamilyMember;
import com.poc.lar.dto.DashboardDTO;
import com.poc.lar.dto.FamilyMemberDTO;
import com.poc.lar.dto.FamilyMemberRequest;
import com.poc.lar.dto.MemberSummaryDTO;
import com.poc.lar.repository.ChoreLogRepository;
import com.poc.lar.repository.FamilyMemberRepository;
import com.poc.lar.repository.RewardRedemptionRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyMemberConverter familyMemberConverter;
    private final ChoreLogRepository choreLogRepository;
    private final RewardRedemptionRepository rewardRedemptionRepository;

    public List<FamilyMemberDTO> findAll() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all family members for tenant: {}", tenantId);
        return familyMemberConverter.toDTOList(familyMemberRepository.findByTenantId(tenantId));
    }

    public Optional<FamilyMemberDTO> findById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding family member by id: {} for tenant: {}", id, tenantId);
        return familyMemberRepository.findByIdAndTenantId(id, tenantId)
                .map(familyMemberConverter::toDTO);
    }

    public FamilyMemberDTO create(FamilyMemberRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Creating family member for tenant: {}, user: {}", tenantId, userId);

        FamilyMember member = FamilyMember.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .nickname(req.nickname())
                .birthDate(req.birthDate())
                .bloodType(req.bloodType())
                .roleType(req.roleType())
                .schoolName(req.schoolName())
                .schoolPhone(req.schoolPhone())
                .schoolGrade(req.schoolGrade())
                .healthInsurance(req.healthInsurance())
                .healthInsuranceNumber(req.healthInsuranceNumber())
                .allergies(familyMemberConverter.serializeList(req.allergies()))
                .medicalConditions(familyMemberConverter.serializeList(req.medicalConditions()))
                .emergencyNotes(req.emergencyNotes())
                .avatarUrl(req.avatarUrl())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return familyMemberConverter.toDTO(familyMemberRepository.save(member));
    }

    public FamilyMemberDTO update(UUID id, FamilyMemberRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating family member: {} for tenant: {}", id, tenantId);

        FamilyMember member = familyMemberRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Family member not found: " + id));

        member.setNickname(req.nickname());
        member.setBirthDate(req.birthDate());
        member.setBloodType(req.bloodType());
        member.setRoleType(req.roleType());
        member.setSchoolName(req.schoolName());
        member.setSchoolPhone(req.schoolPhone());
        member.setSchoolGrade(req.schoolGrade());
        member.setHealthInsurance(req.healthInsurance());
        member.setHealthInsuranceNumber(req.healthInsuranceNumber());
        member.setAllergies(familyMemberConverter.serializeList(req.allergies()));
        member.setMedicalConditions(familyMemberConverter.serializeList(req.medicalConditions()));
        member.setEmergencyNotes(req.emergencyNotes());
        member.setAvatarUrl(req.avatarUrl());
        member.setUpdatedAt(LocalDateTime.now());
        member.markAsExisting();

        return familyMemberConverter.toDTO(familyMemberRepository.save(member));
    }

    public void delete(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Soft deleting family member: {} for tenant: {}", id, tenantId);

        FamilyMember member = familyMemberRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Family member not found: " + id));

        member.setActive(false);
        member.setUpdatedAt(LocalDateTime.now());
        member.markAsExisting();
        familyMemberRepository.save(member);
    }

    public DashboardDTO getDashboard(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Getting dashboard for member: {} in tenant: {}", memberId, tenantId);

        List<FamilyMember> members = familyMemberRepository.findByTenantId(tenantId);
        List<MemberSummaryDTO> memberSummaries = members.stream()
                .filter(m -> Boolean.TRUE.equals(m.getActive()))
                .map(m -> {
                    Integer earned = choreLogRepository.sumPointsByMemberId(m.getId());
                    Integer spent = rewardRedemptionRepository.sumPointsSpentByMemberId(m.getId());
                    int balance = earned - spent;
                    return new MemberSummaryDTO(
                            m.getId(),
                            m.getNickname(),
                            m.getRoleType(),
                            balance,
                            0
                    );
                })
                .toList();

        return new DashboardDTO(0, 0, 0, 0, 0, 0, 0, memberSummaries);
    }
}
